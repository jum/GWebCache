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

public class Data implements Serializable {

    public final static long MILLIS_PER_HOUR = 3600*1000;
    public final static long MAX_HOST_AGE = 6 * MILLIS_PER_HOUR;
    public final static long MAX_URL_AGE = 24 * MILLIS_PER_HOUR;

    public final static int MAX_HOSTS_STORED = 20;
    public final static int MAX_URLS_STORED = 200;

    public final static int MAX_HOSTS_RETURNED = 20;
    public final static int MAX_URLS_RETURNED = 10;

    private static Data instance;
    private HashMap rateLimited; // remoteIP -> Date
    private HashMap nets; // netName -> GnutellaNet
    private transient LinkedList verifyList; // of RemoteURLs

    private Data() {
        rateLimited = new HashMap();
        nets = new HashMap();
        verifyList = new LinkedList();
    }

    public static Data getInstance() {
        return instance;
    }

    public void hourly(ServletContext context) {
        for (;;) {
            try {
                Thread.sleep(MILLIS_PER_HOUR);
                synchronized (this) {
                    Date now = new Date();
                    Iterator it = rateLimited.keySet().iterator();
                    while (it.hasNext()) {
                        String remoteIP = (String)it.next();
                        Date d = (Date)rateLimited.get(remoteIP);
                        if (d != null && now.getTime() - d.getTime() > MILLIS_PER_HOUR)
                            it.remove();
                    }
                    it = nets.keySet().iterator();
                    while (it.hasNext()) {
                        String netName = (String)it.next();
                        GnutellaNet net = (GnutellaNet)nets.get(netName);
                        HashMap map = net.getHosts();
                        Iterator it1 = map.keySet().iterator();
                        while (it1.hasNext()) {
                            String remoteIP = (String)it1.next();
                            Date d = ((RemoteClient)map.get(remoteIP)).getLastUpdated();
                            if (now.getTime() - d.getTime() > MAX_HOST_AGE)
                                it1.remove();
                        }
                        int count = map.size();
                        map = net.getURLs();
                        it1 = map.keySet().iterator();
                        while (it1.hasNext()) {
                            String url = (String)it1.next();
                            Date d = ((RemoteURL)map.get(url)).getLastUpdated();
                            if (now.getTime() - d.getTime() > MAX_URL_AGE)
                                it1.remove();
                        }
                        count += map.size();
                        if (count == 0)
                            it.remove();
                    }
                }
                writeData(context);
            } catch (InterruptedException ex) {
                //context.log("hourly exiting");
                return;
            }
        }
    }

    public void urlVerifier(ServletContext context) {
        for (;;) {
            RemoteURL target = null;
            synchronized (verifyList) {
                while (verifyList.isEmpty()) {
                    try {
                        verifyList.wait();
                    } catch (InterruptedException ex) {
                        //context.log("urlVerifier exiting");
                        return;
                    }
                }
                target = (RemoteURL)verifyList.removeFirst();
            }
            if (Thread.interrupted())
                return;
            target.setCacheVersion(RemoteURL.STATE_CHECKING);
            try {
                String testURL = target.getRemoteURL();
                int protoVersion = target.getProtoVersion();
                if (protoVersion == RemoteURL.PROTO_V1)
                    testURL = testURL + "?ping=1";
                else
                    testURL = testURL + "?ping=1&get=1";
                testURL = testURL + "&client=jumswebcache&version=" + GWebCache.getVersion();
                //context.log("verifying: " + testURL);
                URL url = new URL(testURL);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                                                url.openStream()));
                String line;
                String cacheVersion = RemoteURL.STATE_FAILED + ": undecipherable response";
                while ((line = in.readLine()) != null) {
                    if (Thread.interrupted())
                        return;
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
                        if (line.length() > 4 && 
                            line.substring(0, 4).equalsIgnoreCase("PONG")) {
                            cacheVersion = "V1:" + line.substring(4);
                            break;
                        }
                    }
                }
                in.close();
                target.setCacheVersion(cacheVersion.trim());
            } catch (Exception ex) {
                //context.log("verify exception:", ex);
                target.setCacheVersion(RemoteURL.STATE_FAILED + ": " + ex);
            }
        }
    }

    public void addUrlForVerification(RemoteURL url) {
        url.setCacheVersion(RemoteURL.STATE_QUEUED);
        synchronized(verifyList) {
            verifyList.addLast(url);
            verifyList.notify();
        }
    }

    public synchronized boolean isRateLimited(String remoteIP) {
        Date now = new Date();
        Date d = (Date)rateLimited.get(remoteIP);
        rateLimited.put(remoteIP, now);
        if (d == null)
            return false;
        return now.getTime() - d.getTime() <= MILLIS_PER_HOUR;
    }

    public synchronized void addHost(String netName, RemoteClient remoteClient) {
        GnutellaNet net = lookupNet(netName);
        HashMap map = net.getHosts();
        map.put(remoteClient.getRemoteIP(), remoteClient);
        Date oldest = remoteClient.getLastUpdated();
        Date now = new Date();
        while (map.size() > MAX_HOSTS_STORED) {
            String oldestKey = null;
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                Date d = ((RemoteClient)map.get(key)).getLastUpdated();
                if (now.getTime() - d.getTime() > MAX_HOST_AGE)
                    it.remove();
                if (oldest.getTime() > d.getTime()) {
                    oldestKey = key;
                    oldest = d;
                }
            }
            if (oldestKey != null && map.size() > MAX_HOSTS_STORED) {
                map.remove(oldestKey);
            }
        }
    }

    public synchronized void addURL(String netName, RemoteURL remoteURL) {
        GnutellaNet net = lookupNet(netName);
        HashMap map = net.getURLs();
        if (map.containsKey(remoteURL.getRemoteURL()))
            return;
        map.put(remoteURL.getRemoteURL(), remoteURL);
        addUrlForVerification(remoteURL);
        Date oldest = remoteURL.getLastUpdated();
        Date now = new Date();
        while (map.size() > MAX_URLS_STORED) {
            String oldestKey = null;
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                Date d = ((RemoteURL)map.get(key)).getLastUpdated();
                if (now.getTime() - d.getTime() > MAX_URL_AGE)
                    it.remove();
                if (oldest.getTime() > d.getTime()) {
                    oldestKey = key;
                    oldest = d;
                }
            }
            if (oldestKey != null && map.size() > MAX_URLS_STORED) {
                map.remove(oldestKey);
            }
        }
    }

    public GnutellaNet lookupNet(String netName) {
        GnutellaNet net = (GnutellaNet)nets.get(netName);
        if (net == null) {
            net = new GnutellaNet(netName);
            nets.put(netName, net);
        }
        return net;
    }

    public Iterator allNets() {
        return nets.keySet().iterator();
    }

    public HashMap getRateLimited() {
        return rateLimited;
    }
    
    public synchronized Collection getHosts(String netName) {
        GnutellaNet net = lookupNet(netName);
        ArrayList res = new ArrayList(MAX_HOSTS_RETURNED);
        HashMap map = net.getHosts();
        ArrayList keys = new ArrayList(map.keySet());
        Collections.shuffle(keys);
        Iterator it = keys.iterator();
        int n = 0;
        while (it.hasNext() && n <= MAX_HOSTS_RETURNED) {
            String key = (String)it.next();
            res.add(map.get(key));
            n++;
        }
        return res;
    }

    public synchronized Collection getURLs(String netName, int protoVersion) {
        GnutellaNet net = lookupNet(netName);
        ArrayList res = new ArrayList(MAX_URLS_RETURNED);
        HashMap map = net.getURLs();
        ArrayList keys = new ArrayList(map.keySet());
        Collections.shuffle(keys);
        Iterator it = keys.iterator();
        int n = 0;
        while (it.hasNext() && n <= MAX_URLS_RETURNED) {
            String key = (String)it.next();
            RemoteURL url = (RemoteURL)map.get(key);
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

    public static void readData(ServletContext context) {
        Data newData = null;
        try {
            ObjectInputStream i = new ObjectInputStream(
                                    new FileInputStream(dataFile(context)));
            newData = (Data)i.readObject();
            i.close();
            newData.verifyList = new LinkedList();
        } catch (FileNotFoundException e) {
            context.log("no data file");
        } catch (Exception e) {
            context.log("readData", e);
        }
        if (newData == null)
            newData = new Data();
        instance = newData;
        synchronized (instance) {
            Iterator it = instance.nets.keySet().iterator();
            while (it.hasNext()) {
                String netName = (String)it.next();
                GnutellaNet net = (GnutellaNet)instance.nets.get(netName);
                HashMap map = net.getURLs();
                Iterator it1 = map.keySet().iterator();
                while (it1.hasNext()) {
                    String url = (String)it1.next();
                    RemoteURL u = (RemoteURL)map.get(url);
                    String cv = u.getCacheVersion();
                    if (cv.equals(RemoteURL.STATE_QUEUED))
                        instance.addUrlForVerification(u);
                    if (cv.equals(RemoteURL.STATE_CHECKING))
                        instance.addUrlForVerification(u);
                }
            }
        }
    }

    public static void writeData(ServletContext context) {
        try {
            ObjectOutputStream o = new ObjectOutputStream(
                                    new FileOutputStream(dataFile(context)));
            synchronized (instance) {
                o.writeObject(instance);
            }
            o.close();
        } catch (Exception e) {
            context.log("writeData", e);
        }
    }

    public static File dataFile(ServletContext context) {
        File f;
        String fname = context.getInitParameter("filename");
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
