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

/**
 * Implement a Gnutella web cache as a Java servlet. Support
 * both the V1 and V2 versions of the protocol.
 */
public class GWebCache extends HttpServlet {

    /**
     * The worker thread for doing hourly data maintenance.
     */
    private transient Thread hourlyWorker;
    /**
     * The worker thread for verifying cache URLs.
     */
    private transient Thread verifierWorker;
	
	/**
	 * The Stats instance to use
	 */
	private Stats stats;
    /**
     * Initialize the servlet by reading the cache data from disk
     * and create the worker threads.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final ServletContext context = getServletContext();
        //log("init");
        Data.readData(context);
        //Stats.readStats(context);
        stats = Stats.getInstance();
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

    /**
     * Shutdown the servlet, write the current cache data to disk
     * and kill the worker threads. In some situations the termination
     * of the URL verifier thread may take a long time if currently an
     * URL to a non-existant host is being opened.
     */
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

    /**
     * Do implement the HTTP GET method. Check the supplied parameters
     * whether the request uses the V1 or V2 cache protocol. If no
     * parameters are found, redirect to the index.jsp page.
     */
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

    /**
     * Perform a Gnutella V1 web cache protocol request.
     */
    public void doV1(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        stats.bumpHour(new Date());
        stats.bumpRequests();
        ClientVersion clientVersion = clientVersionFromParams(request);
		stats.bumpByClient(clientVersion);
		
        try {
            String netName = "gnutella";
            if (request.getParameter("ping") != null) {
                out.println("PONG jumswebcache/" + getVersion());
            } else if (request.getParameter("urlfile") != null) {
            	stats.bumpUrlRequests();
                Iterator it = Data.getInstance().getURLs(netName, RemoteURL.PROTO_V1).iterator();
                while (it.hasNext()) {
                    RemoteURL url = (RemoteURL)it.next();
                    out.println(url.getRemoteURL());
                }
            } else if (request.getParameter("hostfile") != null) {
            	stats.bumpHostRequests();
                Iterator it = Data.getInstance().getHosts(netName).iterator();
                while (it.hasNext()) {
                    RemoteClient host = (RemoteClient)it.next();
                    out.println(host.getRemoteIP() + ":" + host.getPort());
                }
            } else if (request.getParameter("url") != null || request.getParameter("ip") != null) {
                stats.bumpUpdates();
                String remoteIP = request.getRemoteAddr();
				RemoteClient remoteClient = remoteFromParams(request,clientVersion);
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
                    checkAddress(remoteClient.getRemoteIP());
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
            } else if (request.getParameter("statfile") != null) {
                out.println(stats.numRequests.getTotalCount());
                out.println(stats.numRequests.getLastHourCount());
                out.println(stats.numUpdates.getLastHourCount());
                out.println(stats.numUpdates);
            } else {
                out.println("ERROR: unknown command");
            }
        } catch (WebCacheException ex) {
            out.println("ERROR: " + ex.getMessage());
            //log("doV1", ex);
        }
    }

    /**
     * Perform a Gnutella V2 web cache protocol request.
     */
    public void doV2(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Date now = new Date();
        stats.bumpHour(now);
        stats.bumpRequests();
        try {
            RemoteClient remoteClient = remoteFromParams(request);
            stats.bumpByClient(remoteClient.getClientVersion());
            if (remoteClient.getClientVersion().getClient() == null ||
                remoteClient.getClientVersion().getVersion() == null)
                throw new WebCacheException("no anonymous clients allowed");
            boolean didOne = false;
            String net = request.getParameter("net");
            if (net == null)
                net = "gnutella";
            if (request.getParameter("ping") != null) {
                /*
                 * Hack for bazooka: it insists on the net appended to
                 * the pong response.
                 */
                if (remoteClient.getClientVersion().getClient().indexOf("Bazooka") != -1)
                    out.println("I|pong|jumswebcache/" + getVersion() + "|" + net);
                else
                    out.println("I|pong|jumswebcache/" + getVersion());
                didOne = true;
            }
            if (request.getParameter("update") != null) {
                stats.bumpUpdates();
                if (remoteClient.getRemoteIP() != null) {
                    String remoteIP = request.getRemoteAddr();
                    if (!remoteIP.equals(remoteClient.getRemoteIP()))
                        throw new WebCacheException("rejected IP");
                    if (Data.getInstance().isRateLimited(remoteIP)) {
                        out.println("I|update|WARNING|update denied due to rate limit");
                        return;
                    }
                    checkAddress(remoteClient.getRemoteIP());
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
            	stats.bumpGetRequests();
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
	/**
	 * Parse the remote client data from the request, including
	 * remote IP, port number. Client ID and version are supplied
	 * from previously parsed parameters. 
	 * @param request The HttpServletRequest to extract the
	 * parameters from.
	 * @param c A ClientVersion object describing the client
	 * 				It will be put in the RemoteClient object created
	 * @return A RemoteClient object describing the client.
	 * @throws WebCacheException
	 */
	public RemoteClient remoteFromParams(
		HttpServletRequest request,
		ClientVersion c)
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
			return new RemoteClient(ip, port, c);
	}
    /**
     * Parse the remote client data from the request, including
     * remote IP, port number and client ID and version. 
     * @param request The HttpServletRequest to extract the
     * parameters from.
     * @return A RemoteClient object describing the client.
     * @throws WebCacheException
     */
    public RemoteClient remoteFromParams(HttpServletRequest request)
                                            throws WebCacheException {
        return remoteFromParams(request, clientVersionFromParams(request));
    }

    /**
     * Parse the client ID and version strings from the request.
     * @param request The HttpServletRequest to extract the
     * parameters from.
     * @return A ClientVersion object.
     */
    public ClientVersion clientVersionFromParams(HttpServletRequest request) {
        String client = request.getParameter("client");
        String version = request.getParameter("version");
        /*
         * Hack for the strange way the bazooka php script communicates
         * its version.
         */
        if (client != null && version == null && client.startsWith("TESTBazooka_")) {
            version = client.substring(12);
            client = "Bazooka";
        }
        return new ClientVersion(client, version);
    }

    /**
     * Check that a particular IP address is reachable from the world.
     * In particular it may not be a local loopback or a private address.
     * @param remoteIP
     * @throws WebCacheException
     */
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

    public String getServletInfo() {
        return "GWebCache " + getVersion() + " by Jens-Uwe Mager <jum@anubis.han.de>";
    }

    public static String getVersion() {
        return "0.1.4";
    }
}
