/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

/**
 * Encapsulate the data for an URL that is queued for verification.
 */
public class VerifyURL {

    /**
     * The URL describing the web cache,
     */
    private RemoteURL remoteURL;
    /**
     * The Gnutella network name to use if remoteURL is a V2 cache.
     */
    private String netName;

    public VerifyURL(RemoteURL remoteURL, String netName) {
        this.remoteURL = remoteURL;
        this.netName = netName;
    }

    public RemoteURL getRemoteURL() {
        return remoteURL;
    }

    public String getNetName() {
        return netName;
    }

    public String toString() {
        return remoteURL + "@" + netName;
    }
}
