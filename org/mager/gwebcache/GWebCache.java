/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class GWebCache extends HttpServlet {

    private transient Thread hourlyWorker;
    private transient Thread verifierWorker;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final ServletContext context = getServletContext();
        //log("init");
        Data.readData(context);
        hourlyWorker = new Thread(new Runnable() {
            public void run() {
                Data.getInstance().hourly(context);
            }
        }, "GWebCache.hourly");
        hourlyWorker.start();
        verifierWorker = new Thread(new Runnable() {
            public void run() {
                Data.getInstance().urlVerifier(context);
            }
        }, "GWebCache.urlVerifier");
        verifierWorker.start();
    }

    public void destroy() {
        //log("destroy");
        Data.writeData(getServletContext());
        hourlyWorker.interrupt();
        try {
            hourlyWorker.join();
        } catch (InterruptedException ex) {}
        hourlyWorker = null;
        verifierWorker.interrupt();
        try {
            verifierWorker.join();
        } catch (InterruptedException ex) {}
        verifierWorker = null;
    }

    public void doGet(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {
        String pingValue = request.getParameter("ping");
        if (request.getParameter("get") != null ||
            request.getParameter("update") != null ||
            request.getParameter("net") != null ||
            (pingValue != null && pingValue.equals("2")))
            doV2(request, response);
        else if (request.getParameterNames().hasMoreElements())
            doV1(request, response);
        else
            response.sendRedirect("index.jsp");
    }

    public void doV1(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            String netName = "gnutella";
            if (request.getParameter("ping") != null) {
                out.println("PONG jumswebcache/" + getVersion());
            } else if (request.getParameter("urlfile") != null) {
                Iterator it = Data.getInstance().getURLs(netName, RemoteURL.PROTO_V1).iterator();
                while (it.hasNext()) {
                    RemoteURL url = (RemoteURL)it.next();
                    out.println(url.getRemoteURL());
                }
            } else if (request.getParameter("hostfile") != null) {
                Iterator it = Data.getInstance().getHosts(netName).iterator();
                while (it.hasNext()) {
                    RemoteClient host = (RemoteClient)it.next();
                    out.println(host.getRemoteIP() + ":" + host.getPort());
                }
            } else if (request.getParameter("url") != null || request.getParameter("ip") != null) {
                String remoteIP = request.getRemoteAddr();
                RemoteClient remoteClient = remoteFromParams(request);
                String c = remoteClient.getClientVersion().getClient();
                if (c != null && c.equals("GNUT"))
                    throw new WebCacheException("phatbot not allowd here");
                String url = request.getParameter("url");
                out.println("OK");
                if (Data.getInstance().isRateLimited(remoteIP)) {
                    out.println("WARNING: update denied due to rate limit");
                    return;
                }
                if (remoteClient.getRemoteIP() != null) {
                    Data.getInstance().addHost(netName, remoteClient);
                }
                if (url != null) {
                    try {
                        url = URLDecoder.decode(url, "ISO-8859-1");
                    } catch (UnsupportedEncodingException ex) {
                        throw new WebCacheException("bad encoding");
                    }
                    RemoteURL remoteURL = new RemoteURL(url, RemoteURL.PROTO_V1, remoteClient.getClientVersion());
                    Data.getInstance().addURL(netName, remoteURL);
                }
            } else {
                out.println("ERROR: unknown command");
            }
        } catch (WebCacheException ex) {
            out.println("ERROR: " + ex.getMessage());
            //log("doV1", ex);
        }
    }

    public void doV2(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            RemoteClient remoteClient = remoteFromParams(request);
            if (remoteClient.getClientVersion().getClient() == null ||
                remoteClient.getClientVersion().getVersion() == null)
                throw new WebCacheException("no anonymous clients allowed");
            boolean didOne = false;
            String net = request.getParameter("net");
            if (net == null)
                net = "gnutella";
            if (request.getParameter("ping") != null) {
                out.println("I|pong|jumswebcache/" + getVersion());
                didOne = true;
            }
            if (request.getParameter("update") != null) {
                if (remoteClient.getRemoteIP() != null) {
                    String remoteIP = request.getRemoteAddr();
                    if (!remoteIP.equals(remoteClient.getRemoteIP()))
                        throw new WebCacheException("rejected IP");
                    if (Data.getInstance().isRateLimited(remoteIP)) {
                        out.println("I|update|WARNING|update denied due to rate limit");
                        return;
                    }
                    Data.getInstance().addHost(net, remoteClient);
                    out.println("I|update|OK");
                    didOne = true;
                }
                String url = request.getParameter("url");
                if (url != null) {
                    try {
                        url = URLDecoder.decode(url, "ISO-8859-1");
                    } catch (UnsupportedEncodingException ex) {
                        throw new WebCacheException("bad encoding");
                    }
                    RemoteURL remoteURL = new RemoteURL(url, RemoteURL.PROTO_V2, remoteClient.getClientVersion());
                    Data.getInstance().addURL(net, remoteURL);
                    out.println("I|update|OK");
                    didOne = true;
                }
            }
            if (request.getParameter("get") != null) {
                Date now = new Date();
                Iterator it = Data.getInstance().getHosts(net).iterator();
                while (it.hasNext()) {
                    RemoteClient host = (RemoteClient)it.next();
                    Date d = host.getLastUpdated();
                    long age = (now.getTime() - d.getTime())/1000;
                    out.println("H|" + host.getRemoteIP() + ":" + host.getPort() + "|" + age);
                    didOne = true;
                }
                it = Data.getInstance().getURLs(net, RemoteURL.PROTO_V2).iterator();
                while (it.hasNext()) {
                    RemoteURL url = (RemoteURL)it.next();
                    Date d = url.getLastUpdated();
                    long age = (now.getTime() - d.getTime())/1000;
                    out.println("U|" + url.getRemoteURL() + "|" + age);
                    didOne = true;
                }
            }
            if (!didOne)
                out.println("I");
        } catch (WebCacheException ex) {
            out.println("I|update|WARNING|" + ex.getMessage());
            //log("doV2", ex);
        }
    }

    public RemoteClient remoteFromParams(HttpServletRequest request)
                                            throws WebCacheException {
        String ipPort = request.getParameter("ip");
        int port = 0;
        String ip = null;
        if (ipPort != null) {
            try {
                ipPort = URLDecoder.decode(ipPort, "ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                throw new WebCacheException("bad encoding");
            }
            int i;
            i = ipPort.indexOf(':');
            if (i == -1)
                throw new WebCacheException("bad ipPort value: " + ipPort);
            ip = ipPort.substring(0, i);
            try {
                port = Integer.parseInt(ipPort.substring(i+1));
            } catch (NumberFormatException ex) {
                throw new WebCacheException("bad port: " +
                    ipPort.substring(i+1), ex);
            }
            if (port < 1 || port > 65535)
                throw new WebCacheException("port out of range: " + port);
        }
        return new RemoteClient(ip, port, clientVersionFromParams(request));
    }

    public ClientVersion clientVersionFromParams(HttpServletRequest request) {
        return new ClientVersion(request.getParameter("client"),
            request.getParameter("version"));
    }

    public static void checkAddress(String remoteIP) throws WebCacheException {
        try {
            InetAddress addr = InetAddress.getByName(remoteIP);
            if (addr.isAnyLocalAddress())
                throw new WebCacheException("bad IP: any local");
            if (addr.isLoopbackAddress())
                throw new WebCacheException("bad IP: loopback");
            if (addr.isLinkLocalAddress())
                throw new WebCacheException("bad IP: link local");
            if (addr.isSiteLocalAddress())
                throw new WebCacheException("bad IP: site local");
        } catch (UnknownHostException ex) {
            throw new WebCacheException("bad IP: unknown host", ex);
        }
    }

    public void debug(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String remoteIP = request.getRemoteAddr();
        String title = "Reading All Request Parameters";
        out.println("<HTML><HEAD><TITLE>" + title +
            "</TITLE></HEAD>\n" +
            "<BODY BGCOLOR=\"#FDF5E6\">\n" +
            "<H1 ALIGN=CENTER>" + title + "</H1>\n" +
            "Remote IP: " + remoteIP + "<br>" +
            "Info: " + getServletInfo() + "<br>" +
            "Name: " + getServletName() + "<br>" +
            "<TABLE BORDER=1>\n" +
            "<TR BGCOLOR=\"#FFAD00\">\n" +
            "<TH>Parameter Name<TH>Parameter Value(s)");
        Enumeration paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            out.println("<TR><TD>" + paramName + "\n<TD>");
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() == 0)
                    out.print("<I>No Value</I>");
                else
                    out.print(paramValue);
            } else {
                out.println("<UL>");
                for(int i=0; i<paramValues.length; i++) {
                    out.println("<LI>" + paramValues[i]);
                }
                out.println("</UL>");
            }
        }
        out.println("</TABLE>\n<P>\n");
        out.println("<TABLE BORDER=1>\n" +
            "<TR BGCOLOR=\"#FFAD00\">\n" +
            "<TH>Attribute Name<TH>Attribute Value");
        ServletContext context = getServletContext();
        Enumeration attribNames = context.getAttributeNames();
        while(attribNames.hasMoreElements()) {
            String attribName = (String)attribNames.nextElement();
            out.println("<TR><TD>" + attribName + "\n<TD>");
            Object value = context.getAttribute(attribName);
            if (value == null)
                out.print("<I>null</I>");
            else
                out.print(value);
        }
        out.println("</TABLE>\n</BODY></HTML>");
    }

    public String getServletInfo() {
        return "GWebCache " + getVersion() + " by Jens-Uwe Mager <jum@anubis.han.de>";
    }

    public static String getVersion() {
        return "0.0.3";
    }
}
