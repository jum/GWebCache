/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;

public class Stats {

    public static final Date startTime = new Date();

    public static volatile long lastHour;
    public static volatile long numRequests;
    public static volatile long numUpdates;
    public static volatile long numRequestsLastHour;
    public static volatile long numRequestsThisHour;
    public static volatile long numUpdatesLastHour;
    public static volatile long numUpdatesThisHour;

    public static void bumpHour(Date now) {
        long thisHour = now.getTime() / Data.MILLIS_PER_HOUR;
        if (thisHour != lastHour) {
            lastHour = thisHour;
            numRequestsLastHour = numRequestsThisHour;
            numRequestsThisHour = 0;
            numUpdatesLastHour = numUpdatesThisHour;
            numUpdatesThisHour = 0;
        }
    }

    public static void bumpRequests() {
        numRequests++;
        numRequestsThisHour++;
    }

    public static void bumpUpdates() {
        numUpdates++;
        numUpdatesThisHour++;
    }

}
