/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;
import java.io.*;

public class RemoteURL implements Serializable {

    public static final String STATE_QUEUED = "QUEUED";
    public static final String STATE_CHECKING = "CHECKING";
    public static final String STATE_FAILED = "FAILED";
    public static final int PROTO_V1 = 1;
    public static final int PROTO_V2 = 2;

    private String remoteURL;
    private String cacheVersion;
    private int protoVersion;
    private ClientVersion clientVersion;
    private Date lastUpdated;

    public RemoteURL(String remoteURL, int protoVersion,
                     ClientVersion clientVersion) {
        this.remoteURL = remoteURL;
        this.protoVersion = protoVersion;
        this.clientVersion = clientVersion;
        cacheVersion = STATE_QUEUED;
        lastUpdated = new Date();
    }

    public String getRemoteURL() {
        return remoteURL;
    }

    public String getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(String cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    public int getProtoVersion() {
        return protoVersion;
    }

    public ClientVersion getClientVersion() {
        return clientVersion;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String toString() {
        return remoteURL + " " + cacheVersion + " V" + protoVersion + " " +
            clientVersion + " " + lastUpdated;
    }
}
