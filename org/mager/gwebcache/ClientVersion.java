/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.io.*;

/**
 * Encapsulate the Gnutella client ID and version strings.
 */
public class ClientVersion implements Serializable {

	/**
	 * The string identfying a particular client servent.
	 * Several cache implementations require this to be a
	 * four character all uppercase string.
	 */
    private String client;
    /**
     * The string identifying the version of the servent.
     */
    private String version;

    public ClientVersion(String client, String version) {
        this.client = client;
        this.version = version;
    }

    public String getClient() {
        return client;
    }

    public String getVersion() {
        return version;
    }

    public String toString() {
        return (client == null ? "NULL" : client) + "/" +
                (version == null ? "NULL" : version);
    }
}
