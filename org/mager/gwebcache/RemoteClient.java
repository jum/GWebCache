/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;
import java.io.*;

public class RemoteClient implements Serializable {

    private String remoteIP;
    private int port;
    private ClientVersion clientVersion;
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
