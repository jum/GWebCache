/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;
import java.io.*;

/**
 * Encapsulate the data about another web cache.
 */
public class RemoteURL implements Serializable {

    /**
     * The constant stored in cacheVersion while the URL
     * is queued for verification.
     */
    public static final String STATE_QUEUED = "QUEUED";
    /**
     * The constant stored in cacheVersion while the URL
     * is being verified.
     */
    public static final String STATE_CHECKING = "CHECKING";
    /**
     * The constant an error is prefixed with if stored in
     * the cacheVersion variable.
     */
    public static final String STATE_FAILED = "FAILED";
    /**
     * The constant stored in protoVersion to signify a
     * web cache talking the V1 protocol.
     */
    public static final int PROTO_V1 = 1;
    /**
     * The constant stored in protoVersion to signify a
     * web cache talking the V2 protocol.
     */
    public static final int PROTO_V2 = 2;

    /**
     * The URL where to find the web cache,
     */
    private String remoteURL;
    /**
     * The version as reported by the pong response. This
     * field is also used to state that this URL is in the
     * verification queue or if the verification failed.
     */
    private String cacheVersion;
    /**
     * The protocol version of the web cache.
     */
    private int protoVersion;
    /**
     * The client that submitted this URL.
     */
    private ClientVersion clientVersion;
    /**
     * The time stamp this record was last updated.
     */
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
