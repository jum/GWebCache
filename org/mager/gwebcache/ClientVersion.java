package org.mager.gwebcache;

import java.io.*;

public class ClientVersion implements Serializable {

    private String client;
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
