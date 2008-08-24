/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;
import java.io.*;

/**
 * Encapsulate the data stored per Gnutella network. Implicit in
 * the V1 protocol is "gnutella". Other arbitrary networks may
 * be created automatically upon client request.
 */
public class GnutellaNet implements Serializable {

    /**
     * The name of this network.
     */
    private String netName;
    /**
     * Map from a String IP address to a RemoteClient object.
     * @see RemoteClient
     */
    private HashMap<String, RemoteClient> hosts;
    /**
     * Map from a String URL to a RemoteURL object.
     * @see RemoteURL
     */
    private HashMap<String, RemoteURL> urls;

    public GnutellaNet(String netName) {
        this.netName = netName;
        hosts = new HashMap<String, RemoteClient>();
        urls = new HashMap<String, RemoteURL>();
    }

    public String getNetName() {
        return netName;
    }

    public HashMap<String, RemoteClient> getHosts() {
        return hosts;
    }

    public HashMap<String, RemoteURL> getURLs() {
        return urls;
    }

}
