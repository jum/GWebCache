/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.util.*;

/**
 * Implement the simple statistics as defined per the V1 protocol.
 * Also used by stats.jsp for display.
 */
public class Stats {

    /**
     * The date this cache was started.
     */
    public static final Date startTime = new Date();

    /**
     * The hour we last updated the hourly statistics.
     */
    public static volatile long lastHour;
    /**
     * The number of requests received since cache start.
     */
    public static volatile long numRequests;
    /**
     * The number of updates received since cache start.
     */
    public static volatile long numUpdates;
    /**
     * The number of requests in the previous hour.
     */
    public static volatile long numRequestsLastHour;
    /**
     * The number of requests in this current hour.
     */
    public static volatile long numRequestsThisHour;
    /**
     * The number of updates in the previous hour.
     */
    public static volatile long numUpdatesLastHour;
    /**
     * The number of updates in this current hour.
     */
    public static volatile long numUpdatesThisHour;

    /**
     * Check if we are past the current hour and move the
     * data from this hour to that of the previous hour.
     * @param now An instance of Date signifying the current
     * time.
     */
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

	/**
	 * Increment the number of requests received.
	 */
    public static void bumpRequests() {
        numRequests++;
        numRequestsThisHour++;
    }

	/**
	 * Increment the number if updates received.
	 */
    public static void bumpUpdates() {
        numUpdates++;
        numUpdatesThisHour++;
    }

}
