package org.mager.gwebcache;

import java.util.*;
import java.io.*;

public class GnutellaNet implements Serializable {

    private String netName;
    private HashMap hosts; // ip -> RemoteClient
    private HashMap urls; // urlstring -> RemoteURL

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
