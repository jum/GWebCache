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
    private HashMap hosts;
    /**
     * Map from a String URL to a RemoteURL object.
     * @see RemoteURL
     */
    private HashMap urls;

    public GnutellaNet(String netName) {
        this.netName = netName;
        hosts = new HashMap();
        urls = new HashMap();
    }

    public String getNetName() {
        return netName;
    }

    public HashMap getHosts() {
        return hosts;
    }

    public HashMap getURLs() {
        return urls;
    }

}
