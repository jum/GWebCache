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

/**
 * Encapsulate all permantly stored data about all known
 * networks. Also contains the code to verify other web
 * caches and do periodical maintenance. This singleton
 * is read in upon cache start and written upon cache
 * shutdown and once per hour.
 */
public class Data implements Serializable {

    /**
     * The number of milliseconds per hour.
     */
    public final static long MILLIS_PER_HOUR = 3600*1000;
    /**
     * The number of milliseconds per day.
     */
    public final static long MILLIS_PER_DAY = 3600*1000*24;
    /**
     * The number of milliseconds per week.
     */
    public static long MILLIS_PER_WEEK = 3600*1000*24*7;
    /**
     * The time to keep a client IP number in the "hostfile".
     */
    public final static long MAX_HOST_AGE = 6 * MILLIS_PER_HOUR;
    /**
     * The time to keep a cache URL in the "urlfile".
     */
    public final static long MAX_URL_AGE = 24 * MILLIS_PER_HOUR;
    /**
     * The time an URL is considered valid after verification.
     */
    public final static long MAX_URL_VALID = 2 * MILLIS_PER_HOUR;

    /**
     * The maximum number of client IP numbers in the "hostfile".
     */
    public final static int MAX_HOSTS_STORED = 20;
    /**
     * The maximum number of cache URLs stored in the "urlfile".
     */
    public final static int MAX_URLS_STORED = 200;

    /**
     * The maximum number of IP numbers returned in a "hostfile"
     * request.
     */
    public final static int MAX_HOSTS_RETURNED = 20;
    /**
     * The maximum number of URLs returned in an "urlfile" request.
     */
    public final static int MAX_URLS_RETURNED = 10;

    /**
     * The only instance of the cache data.
     */
    private static Data instance;
    /**
     * Map from a String IP number to a RemoteClient object. Used to rate
     * limit updates from clients.
     */
    private HashMap<String, RemoteClient> rateLimited;
    /**
     * Map from a String network name to a GnutellaNet object.
     * @see GnutellaNet
     */
    private HashMap<String, GnutellaNet> nets;
    /**
     * The queue of VerifyURLs currently being verified. This is
     * not serialized as it can be reconstructed from the cacheVersion
     * string in each RemoteURL.
     * @see VerifyURL
     */
    private transient LinkedList<VerifyURL> verifyList;

    /**
     * Instanciate a new set of cache data. Used only if no
     * file to read the data from is found.
     */
    private Data() {
        rateLimited = new HashMap<String, RemoteClient>();
        nets = new HashMap<String, GnutellaNet>();
        verifyList = new LinkedList<VerifyURL>();
    }

    /**
     * Retrieve the current cache Data instance.
     * @return A Data obejct.
     */
    public static Data getInstance() {
        return instance;
    }

    /**
     * Perform hourly maintenance on the cache data by deleting
     * too old data. Also write the data and stats to disk. Update the 
     * hourly stats
     * @param context The ServletContext to use for logging.
     */
    public void hourly(ServletContext context) {
        for (;;) {
            try {
                Thread.sleep(MILLIS_PER_HOUR);
                synchronized (this) {
                    Date now = new Date();
                    Iterator<String> it = rateLimited.keySet().iterator();
                    while (it.hasNext()) {
                        String remoteIP = it.next();
                        RemoteClient r = rateLimited.get(remoteIP);
                        if (now.getTime() - r.getLastUpdated().getTime() > MILLIS_PER_HOUR)
                            it.remove();
                    }
                    it = nets.keySet().iterator();
                    while (it.hasNext()) {
                        String netName = it.next();
                        GnutellaNet net = nets.get(netName);
                        HashMap<String, RemoteClient> hmap = net.getHosts();
                        Iterator<String> it1 = hmap.keySet().iterator();
                        while (it1.hasNext()) {
                            String remoteIP = it1.next();
                            Date d = hmap.get(remoteIP).getLastUpdated();
                            if (now.getTime() - d.getTime() > MAX_HOST_AGE)
                                it1.remove();
                        }
                        HashMap<String, RemoteURL> umap = net.getURLs();
                        it1 = umap.keySet().iterator();
                        while (it1.hasNext()) {
                            String url = it1.next();
                            RemoteURL remoteURL = umap.get(url);
                            Date d = remoteURL.getLastUpdated();
                            if (now.getTime() - d.getTime() > MAX_URL_AGE)
                                it1.remove();
                            else {
                                d = remoteURL.getLastChecked();
                                /*
                                 * Re-verify good URLs occasionally.
                                 */
                                if (!(remoteURL.getCacheVersion().startsWith(RemoteURL.STATE_FAILED) ||
                                      remoteURL.getCacheVersion().startsWith(RemoteURL.STATE_CHECKING) ||
                                      remoteURL.getCacheVersion().startsWith(RemoteURL.STATE_QUEUED)) &&
                                    d != null &&
                                    now.getTime() - d.getTime() > MAX_URL_VALID)
                                    addUrlForVerification(netName, remoteURL);
                            }
                        }
                        if (hmap.size() == 0 && umap.size() == 0)
                            it.remove();
                    }
                }
                writeData(context);
                Stats.getInstance().bumpHour(System.currentTimeMillis());
                Stats.writeStats(context);
            } catch (InterruptedException ex) {
                //context.log("hourly exiting");
                return;
            }
        }
    }

    /**
     * Run the queue of RemoteURLs to verify. For each RemoteURL connect
     * to the other web cache and use a PING request to extract the caches
     * version.
     * @param context The ServletContext to use for logging.
     */
    public void urlVerifier(ServletContext context) {
        for (;;) {
            VerifyURL vu = null;
            /*
             * Wait until there is anything to verify.
             */
            synchronized (verifyList) {
                while (verifyList.isEmpty()) {
                    try {
                        verifyList.wait();
                    } catch (InterruptedException ex) {
                        //context.log("urlVerifier exiting");
                        return;
                    }
                }
                vu = verifyList.removeFirst();
            }
            /*
             * In case we did not wait check if the thread is supposed
             * to exit.
             */
            if (Thread.interrupted())
                return;
            RemoteURL target = vu.getRemoteURL();
            target.setCacheVersion(RemoteURL.STATE_CHECKING);
            try {
                String testURL = target.getRemoteURL();
                int protoVersion = target.getProtoVersion();
                if (protoVersion == RemoteURL.PROTO_V1)
                    testURL = testURL + "?ping=1" + "&client=JGWC&version=" +
                        GWebCache.getVersion();
                else {
                    /*
                     * Well, the get=1 parameter forces a V2 query. It
                     * would be nice if all scripts did understand the
                     * Jon Atkins extension ping=2.
                     */
                    testURL = testURL + "?ping=1&get=1&net=" + vu.getNetName() +
                        "&client=JGWC" + GWebCache.getVersion();
                }
                //context.log("verifying: " + testURL);
                URL url = new URL(testURL);
                HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
                /*
                 * Avoid redirects as these point to error pages in most
                 * of the cases anyways.
                 */
                urlConn.setInstanceFollowRedirects(false);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                                                urlConn.getInputStream()));
                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    throw new BadResponseException(urlConn.getResponseCode(),
                                                urlConn.getResponseMessage());
                String line;
                String firstLine = null;
                String cacheVersion = null;
                while ((line = in.readLine()) != null) {
                    if (Thread.interrupted())
                        return;
                    if (firstLine == null && line.trim().length() > 0)
                        firstLine = line;
                    /*
                     * Check if the text returned contains less than
                     * chars, surely an indicator that the response is
                     * HTML infested, for example on a free hosting
                     * service.
                     */
                    if (line.indexOf('<') != -1)
                        break;
                    if (protoVersion == RemoteURL.PROTO_V1) {
                        if (line.length() > 4 && 
                            line.substring(0, 4).equalsIgnoreCase("PONG")) {
                            cacheVersion = line.substring(4);
                            break;
                        }
                    } else {
                        if (line.length() > 7 && 
                            line.substring(0, 7).equalsIgnoreCase("I|pong|")) {
                            cacheVersion = line.substring(7);
                            int i = cacheVersion.indexOf('|');
                            if (i != -1)
                                cacheVersion = cacheVersion.substring(0, i);
                            break;
                        }
                        /*
                         * We expect a V2 pong here, but also accept the V1
                         * style that some caches use.
                         */
                        if (line.length() > 4 && 
                            line.substring(0, 4).equalsIgnoreCase("PONG")) {
                            cacheVersion = "V1:" + line.substring(4);
                            break;
                        }
                    }
                }
                in.close();
                if (cacheVersion == null) {
                    if (firstLine == null)
                        firstLine = "empty response";
                    cacheVersion = RemoteURL.STATE_FAILED + ": " + firstLine;
                }
                target.setCacheVersion(htmlEscape(cacheVersion.trim()));
            } catch (Exception ex) {
                //context.log("verify exception:", ex);
                String msg = ex.toString();
                String[] prefixes = {
                    "org.mager.gwebcache",
                    "java.io",
                    "java.net"
                };
                for (int i = 0; i < prefixes.length; i++)
                    if (msg.startsWith(prefixes[i])) {
                        msg = msg.substring(prefixes[i].length()+1);
                        break;
                    }
                target.setCacheVersion(RemoteURL.STATE_FAILED + ": " + msg);
            }
            target.setLastChecked(new Date());
        }
    }

    static String htmlEscape(String html) {
        /*
         * Do some quick and dirty HTML escaping on the text
         * in case it contains HTML.
         */
        html = html.replaceAll("&", "&amp;");
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");
        return html;
    }

    /**
     * Add a new URL for verification into the queue and awake the
     * URL verification thread.
     * @param url The RemoteURL to enqueue.
     */
    public void addUrlForVerification(String netName, RemoteURL url) {
        url.setCacheVersion(RemoteURL.STATE_QUEUED);
        VerifyURL vu = new VerifyURL(url, netName);
        synchronized(verifyList) {
            verifyList.addLast(vu);
            verifyList.notify();
        }
    }

    /**
     * Check if a particular IP number is rate limited (more than
     * one update attempt per hour) and limite its rate
     * @param remoteC The object representing the client to check.
     * @return True if the IP is rate limited.
     */
    public synchronized boolean isRateLimited(RemoteClient remoteC){
        String remoteIP = remoteC.getRemoteIP();
        RemoteClient r = rateLimited.get(remoteIP);
        rateLimited.put(remoteIP, remoteC);
        if (r == null)
            return false;
        return System.currentTimeMillis() - r.getLastUpdated().getTime() <= MILLIS_PER_HOUR;
    }

    /**
     * Add a client to the "hostfile". Make sure no more than the max
     * number of hosts are stored.
     * @param netName The Gnutella network name to add the client to.
     * @param remoteClient The RemoteClient describing the client.
     */
    public synchronized void addHost(String netName, RemoteClient remoteClient) {
        GnutellaNet net = lookupNet(netName);
        HashMap<String, RemoteClient> map = net.getHosts();
        map.put(remoteClient.getRemoteIP(), remoteClient);
        Date oldest = remoteClient.getLastUpdated();
        Date now = new Date();
        while (map.size() > MAX_HOSTS_STORED) {
            String oldestKey = null;
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Date d = map.get(key).getLastUpdated();
                if (now.getTime() - d.getTime() > MAX_HOST_AGE)
                    it.remove();
                if (oldest.getTime() >= d.getTime()) {
                    oldestKey = key;
                    oldest = d;
                }
            }
            if (oldestKey != null && map.size() > MAX_HOSTS_STORED) {
                map.remove(oldestKey);
            }
        }
    }

    /**
     * Add a cache URL to the "urlfile". Make sure no more than the max
     * number of URLs are stored.
     * @param netName The Gnutella network name to add the URL to.
     * @param remoteURL The RemoteURL describing the URL.
     */
    public synchronized void addURL(String netName, RemoteURL remoteURL) {
        GnutellaNet net = lookupNet(netName);
        HashMap<String, RemoteURL> map = net.getURLs();
        if (map.containsKey(remoteURL.getRemoteURL()))
            return;
        map.put(remoteURL.getRemoteURL(), remoteURL);
        addUrlForVerification(netName, remoteURL);
        Date oldest = remoteURL.getLastUpdated();
        Date now = new Date();
        while (map.size() > MAX_URLS_STORED) {
            String oldestKey = null;
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Date d = map.get(key).getLastUpdated();
                if (now.getTime() - d.getTime() > MAX_URL_AGE)
                    it.remove();
                if (oldest.getTime() >= d.getTime()) {
                    oldestKey = key;
                    oldest = d;
                }
            }
            if (oldestKey != null && map.size() > MAX_URLS_STORED) {
                map.remove(oldestKey);
            }
        }
    }

    /**
     * Lookup the GnutellaNet for a network name. Make sure it exists
     * if it is not found in the Map.
     * @param netName The Gnutella network name to find.
     * @return The GnutellaNet describing the network.
     */
    public GnutellaNet lookupNet(String netName) {
        GnutellaNet net = nets.get(netName);
        if (net == null) {
            net = new GnutellaNet(netName);
            nets.put(netName, net);
        }
        return net;
    }

    /**
     * Get the HashMap of GnutellaNets.
     * @return A HashMap of GnutellaNet objects.
     */
    public HashMap<String, GnutellaNet> getNets() {
        return nets;
    }

    public HashMap<String, RemoteClient> getRateLimited() {
        return rateLimited;
    }

    /**
     * Lookup hosts for a "hostfile" request.
     * @param netName The Gnutella network name to find hosts in.
     * @return A collection of RemoteIP objects.
     */    
    public synchronized Collection<RemoteClient> getHosts(String netName) {
        GnutellaNet net = lookupNet(netName);
        ArrayList<RemoteClient> res = new ArrayList<RemoteClient>(MAX_HOSTS_RETURNED);
        HashMap<String, RemoteClient> map = net.getHosts();
        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        Collections.shuffle(keys);
        Iterator<String> it = keys.iterator();
        int n = 0;
        while (it.hasNext() && n < MAX_HOSTS_RETURNED) {
            String key = it.next();
            res.add(map.get(key));
            n++;
        }
        return res;
    }

    /**
     * Lookup URLs for an "urlfile" request.
     * @param netName The Gnutella network name to find URLs in.
     * @param protoVersion The cache protocol version needed.
     * @return A Collection of RemoteURL objects.
     */
    public synchronized Collection<RemoteURL> getURLs(String netName, int protoVersion) {
        GnutellaNet net = lookupNet(netName);
        ArrayList<RemoteURL> res = new ArrayList<RemoteURL>(MAX_URLS_RETURNED);
        HashMap<String, RemoteURL> map = net.getURLs();
        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        Collections.shuffle(keys);
        Iterator<String> it = keys.iterator();
        int n = 0;
        while (it.hasNext() && n < MAX_URLS_RETURNED) {
            String key = it.next();
            RemoteURL url = map.get(key);
            if (url.getProtoVersion() != protoVersion)
                continue;
            String cacheVersion = url.getCacheVersion();
            if (cacheVersion.equals(RemoteURL.STATE_QUEUED))
                continue;
            if (cacheVersion.equals(RemoteURL.STATE_CHECKING))
                continue;
            if (cacheVersion.startsWith(RemoteURL.STATE_FAILED))
                continue;
            res.add(url);
            n++;
        }
        return res;
    }

    /**
     * Read the cache data from disk using serialization. If an
     * error occurs, instanciate an empty cache.
     * @param context The ServletContext to use for logging.
     */
    public static void readData(ServletContext context) {
        Data newData = null;
        try {
            ObjectInputStream i = new ObjectInputStream(
                                    new FileInputStream(dataFile(context)));
            newData = (Data)i.readObject();
            i.close();
            newData.verifyList = new LinkedList<VerifyURL>();
        } catch (FileNotFoundException e) {
            context.log("GWebCache: no data file");
        } catch (Exception e) {
            context.log("GWebCache: readData", e);
        }
        if (newData == null)
            newData = new Data();
        instance = newData;
        synchronized (instance) {
            Iterator<String> it = instance.nets.keySet().iterator();
            while (it.hasNext()) {
                String netName = it.next();
                GnutellaNet net = instance.nets.get(netName);
                HashMap<String, RemoteURL> map = net.getURLs();
                Iterator<String> it1 = map.keySet().iterator();
                while (it1.hasNext()) {
                    String url = it1.next();
                    RemoteURL u = map.get(url);
                    String cv = u.getCacheVersion();
                    if (cv.equals(RemoteURL.STATE_QUEUED))
                        instance.addUrlForVerification(netName, u);
                    if (cv.equals(RemoteURL.STATE_CHECKING))
                        instance.addUrlForVerification(netName, u);
                }
            }
        }
    }

    /**
     * Write the cache data to disk using serialization.
     * @param context The ServletContext to use for logging.
     */
    public static void writeData(ServletContext context) {
        try {
            ObjectOutputStream o = new ObjectOutputStream(
                                    new FileOutputStream(dataFile(context)));
            synchronized (instance) {
                synchronized (instance.verifyList) {
                    o.writeObject(instance);
                }
            }
            o.close();
        } catch (Exception e) {
            context.log("writeData", e);
        }
    }

    /**
     * Find the location for the cache data file. If the "filename"
     * init parameter is not given, use a file in the servlet container
     * provided temporary working directory.
     * @param context The ServletContext to use for logging and
     * for for extracting runtime arguments.
     * @return A File object describing the data file.
     */
    public static File dataFile(ServletContext context) {
        File f;
        String fname = context.getInitParameter("datafilename");
        if (fname != null)
            f = new File(fname);
        else {
            File tmpDir =
                (File)context.getAttribute("javax.servlet.context.tempdir");
            f = new File(tmpDir, "gwebcache.data");
        }
        //context.log("datafile=" + f);
        return f;
    }
}
