/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;
import java.io.*;

/**
 * Encapsulate all data about a Gnutella client, including
 * client id and version, IP and port number.
 */
public class RemoteClient implements Serializable {

    /**
     * The remote clients IP address as a string.
     */
    private String remoteIP;
    /**
     * The TCP/IP port number used by the client.
     */
    private int port;
    /**
     * The client ID and version.
     */
    private ClientVersion clientVersion;
    /**
     * The time stamp this record was last updated.
     */
    private Date lastUpdated;

    public RemoteClient(String remoteIP, int port,
                        ClientVersion clientVersion) {
        this.remoteIP = remoteIP;
        this.port = port;
        this.clientVersion = clientVersion;
        lastUpdated = new Date();
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public int getPort() {
        return port;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String toString() {
        return remoteIP + ":" + port + " " + clientVersion + " " + lastUpdated;
    }
}
