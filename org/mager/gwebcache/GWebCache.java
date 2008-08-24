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
        Stats.readStats(context);
        stats = Stats.getInstance();
        checkWorkers(context);
    }

    /**
     * Check if the worker threads are running and start them if needed.
     * @param context The ServletContext to use for logging.
     */
    public void checkWorkers(final ServletContext context) {
        if (hourlyWorker == null || !hourlyWorker.isAlive()) {
            if (hourlyWorker != null)
                log("restarting hourly worker thread");
            hourlyWorker = new Thread(new Runnable() {
                public void run() {
                    Data.getInstance().hourly(context);
                }
            }, "GWebCache.hourly");
            hourlyWorker.start();
        }
        if (verifierWorker == null || !verifierWorker.isAlive()) {
            if (verifierWorker != null)
                log("restarting verifier worker thread");
            verifierWorker = new Thread(new Runnable() {
                public void run() {
                    Data.getInstance().urlVerifier(context);
                }
            }, "GWebCache.urlVerifier");
            verifierWorker.start();
        }
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
        Stats.writeStats(getServletContext());
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
        checkWorkers(getServletContext());
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
        stats.numRequests.bumpCount();
        ClientVersion clientVersion = clientVersionFromParams(request);
		stats.bumpByClient(clientVersion);
		
        try {
            String netName = "gnutella";
            if (request.getParameter("ping") != null) {
                stats.pingRequestsGWC1.bumpCount();
                out.println("PONG jumswebcache/" + getVersion());
            } else if (request.getParameter("urlfile") != null) {
            	stats.urlfileRequests.bumpCount();
                Iterator<RemoteURL> it = Data.getInstance().getURLs(netName, RemoteURL.PROTO_V1).iterator();
                while (it.hasNext()) {
                    RemoteURL url = it.next();
                    out.println(url.getRemoteURL());
                }
            } else if (request.getParameter("hostfile") != null) {
            	stats.hostfileRequests.bumpCount();
                Iterator<RemoteClient> it = Data.getInstance().getHosts(netName).iterator();
                while (it.hasNext()) {
                    RemoteClient host = it.next();
                    out.println(host.getRemoteIP() + ":" + host.getPort());
                }
            } else if (request.getParameter("url") != null || request.getParameter("ip") != null) {
                stats.bumpGWC1Updates();
                String remoteIP = request.getRemoteAddr();
                RemoteClient remoteParam = remoteFromParams(request, clientVersion);
                RemoteClient remote = new RemoteClient(remoteIP, 0, clientVersion);

                String c = remoteParam.getClientVersion().getClient();
                if (c.equals("GNUT"))
                    throw new WebCacheException("phatbot not allowd here");
                out.println("OK");
                if (Data.getInstance().isRateLimited(remote)) {
                    stats.RateLimitedGWC1.bumpCount();
                    out.println("WARNING: update denied due to rate limit");
                    return;
                }
                if (remoteParam.getRemoteIP() != null) {
                    stats.IPUpdateRequestsGWC1.bumpCount();
                    checkAddress(remoteParam.getRemoteIP());
                    Data.getInstance().addHost(netName, remoteParam);
                }
                String url = request.getParameter("url");
                if (url != null) {
                    stats.URLUpdateRequestsGWC1.bumpCount();
                    try {
                        url = URLDecoder.decode(url, "ISO-8859-1");
                        url = canonURI(url);
                    } catch (UnsupportedEncodingException ex) {
                        throw new WebCacheException("bad encoding");
                    } catch (URISyntaxException ex) {
                        throw new WebCacheException("bad URI", ex);
                    }
                    RemoteURL remoteURL = new RemoteURL(url, RemoteURL.PROTO_V1, remoteParam.getClientVersion());
                    Data.getInstance().addURL(netName, remoteURL);
                }
            } else if (request.getParameter("statfile") != null) {
                stats.statfileRequests.bumpCount();
                out.println(stats.numRequests.getTotalCount());
                out.println(stats.numRequests.getLastHourCount());
                out.println(stats.numUpdates.getLastHourCount());
                out.println(stats.numUpdates.getTotalCount());
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
        stats.numRequests.bumpCount();
        try {
            RemoteClient remoteParam = remoteFromParams(request);
            stats.bumpByClient(remoteParam.getClientVersion());
            if (remoteParam.getClientVersion().getClient().equals(ClientVersion.UNKNOWN) ||
                remoteParam.getClientVersion().getVersion().equals(ClientVersion.UNKNOWN))
                throw new WebCacheException("no anonymous clients allowed");
            boolean didOne = false;
            String net = request.getParameter("net");
            if (net == null)
                net = "gnutella";
            if (request.getParameter("ping") != null) {
                stats.pingRequestsGWC2.bumpCount();
                /*
                 * Hack for bazooka: it insists on the net appended to
                 * the pong response.
                 */
                if (remoteParam.getClientVersion().getClient().indexOf("Bazooka") != -1)
                    out.println("I|pong|jumswebcache/" + getVersion() + "|" + net);
                else
                    out.println("I|pong|jumswebcache/" + getVersion());
                didOne = true;
            }
            if (request.getParameter("update") != null) {
                stats.bumpGWC2Updates();
                String remoteIP = request.getRemoteAddr();
                RemoteClient remote = new RemoteClient(remoteIP,0,
                    remoteParam.getClientVersion());
                if (remoteParam.getRemoteIP() != null) {
                    stats.IPUpdateRequestsGWC2.bumpCount();
                    if (!remoteIP.equals(remoteParam.getRemoteIP()))
                        throw new WebCacheException("rejected IP");
                    if (Data.getInstance().isRateLimited(remote)) {
                        stats.RateLimitedGWC2.bumpCount();
                        out.println("I|update|WARNING|update denied due to rate limit");
                        return;
                    }
                    checkAddress(remoteParam.getRemoteIP());
                    Data.getInstance().addHost(net, remoteParam);
                    out.println("I|update|OK");
                    didOne = true;
                }
                String url = request.getParameter("url");
                if (url != null) {
                    stats.URLUpdateRequestsGWC2.bumpCount();
                    try {
                        url = URLDecoder.decode(url, "ISO-8859-1");
                        url = canonURI(url);
                    } catch (UnsupportedEncodingException ex) {
                        throw new WebCacheException("bad encoding");
                    } catch (URISyntaxException ex) {
                        throw new WebCacheException("bad URI", ex);
                    }
                    RemoteURL remoteURL = new RemoteURL(url, RemoteURL.PROTO_V2, remoteParam.getClientVersion());
                    Data.getInstance().addURL(net, remoteURL);
                    out.println("I|update|OK");
                    didOne = true;
                }
            }
            if (request.getParameter("get") != null) {
            	stats.getRequests.bumpCount();
                Iterator<RemoteClient> cit = Data.getInstance().getHosts(net).iterator();
                while (cit.hasNext()) {
                    RemoteClient host = cit.next();
                    Date d = host.getLastUpdated();
                    long age = (now.getTime() - d.getTime())/1000;
                    out.println("H|" + host.getRemoteIP() + ":" + host.getPort() + "|" + age);
                    didOne = true;
                }
                Iterator<RemoteURL> uit = Data.getInstance().getURLs(net, RemoteURL.PROTO_V2).iterator();
                while (uit.hasNext()) {
                    RemoteURL url = uit.next();
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
     * 	It will be put in the RemoteClient object created
     * @return A RemoteClient object describing the client.
     * @throws WebCacheException
     */
    public RemoteClient remoteFromParams( HttpServletRequest request,
                            ClientVersion c) throws WebCacheException {
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
         * V2 requests may combine client and version into the client
         * parameter. The client ID is then the first four characters,
         * the rest (up to 16 chars) is the version.
         */
        if (client != null && version == null && client.length() > 4) {
            version = client.substring(4);
            client = client.substring(0, 4);
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

    /**
     * Do canonicalize the URI and ensure that it is suitable as a web
     * cache URI. Enforce that it is of scheme http, has no
     * authentication info and no query and fragment parts.
     * @param spec The incoming URI
     * @return The URI sanitized for web cache purposes
     * @throws URISyntaxException
     */
    public static String canonURI(String spec) throws URISyntaxException {
        URI uri = new URI(spec).normalize().parseServerAuthority();
        String scheme = uri.getScheme();
        if (!scheme.equals("http"))
            throw new URISyntaxException(spec, "scheme must be http");
        String host = uri.getHost();
        if (host == null)
            throw new URISyntaxException(spec, "host must be specified");
        if (host.endsWith("."))
            host = host.substring(0, host.length()-1);
        host = host.toLowerCase();
        int port = uri.getPort();
        if (port == 80)
            port = -1;
        String path = uri.getPath();
        if (path.length() == 0)
            path = "/";
        String query = uri.getQuery();
        if (query != null)
            throw new URISyntaxException(spec, "no query allowed");
        String frag = uri.getFragment();
        if (frag != null)
            throw new URISyntaxException(spec, "no fragment allowed");
        return new URI(scheme, null, host, port, path, null, null).toASCIIString();
    }

    public String getServletInfo() {
        return "GWebCache " + getVersion() + " by Jens-Uwe Mager <jum@anubis.han.de>";
    }

    public static String getVersion() {
        return "0.3.0";
    }
}
