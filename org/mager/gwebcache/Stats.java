/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.io.*;
import java.util.*;

/**
 * Implement the simple statistics as defined per the V1 protocol,
 * stat by client/version, by this day/last day and this week/last week.
 * Also used by stats.jsp for display.
 * Follow the syngleton pattern
 */
public class Stats  implements Serializable {

    /**
     * The date this cache was started.
     */
    public Date startTime;

    /**
     * The hour we last updated the hourly statistics.
     */
    public volatile long lastHour;
    /**
     * The day we last updated the daily statistics.
     */
    public volatile long lastDay;
    /**
     * The week we last updated the weekly statistics.
     */
    public volatile long lastWeek;
    /**
     * The number of requests received.
     */
    public volatile Counter numRequests;

    /**
     * The number of updates received.
     */
    public volatile Counter numUpdates;

    /**
     * The number of urlfile requests received.
     * This is a GWB1 requests
     */
    public volatile Counter urlRequests;
    
    /**
     * The number of hostfile requests received.
     * This is a GWB1 requests
     */
    public volatile Counter hostRequests;
    
    /**
     * The number of get requests received.
     * This is a GWB2 requests for url and host
     */
    public volatile Counter getRequests;
	
    /**
     * The number of request by client 
     * We don't differenciate between version
     * String(client) -> Counter
     */
    public Map clientRequests;

    /**
     * The number of request by client/version 
     * We differenciate between version
     * ClientVersion -> Counter
     */
    public Map clientVersionRequests;
    
    /**
     * The only instance of the stats data.
     */
    private static Stats instance = new Stats();
    private Stats() {
        clientVersionRequests =
                        Collections.synchronizedMap( new HashMap());
        clientRequests =
                        Collections.synchronizedMap( new HashMap());
        getRequests = new Counter();
        hostRequests = new Counter();
        urlRequests = new Counter();
        numUpdates = new Counter();
        numRequests = new Counter();
        startTime = new Date();
    }
    /**
     * Retrieve the current cache Stats instance.
     * @return A Stats obejct.
     */
    public static Stats getInstance() {
        return instance;
    }
    /**
     * Check if we are past the current hour and move the
     * data from this hour to that of the previous hour.
     * @param now An instance of Date signifying the current
     * time.
     */
    public void bumpHour(Date now) {
        long thisHour = now.getTime() / Data.MILLIS_PER_HOUR;
        long thisDay = now.getTime() / Data.MILLIS_PER_DAY;
        long thisWeek = now.getTime()/ Data.MILLLIS_PER_WEEK;
        if (thisDay != lastDay) {
            lastDay = thisDay;

            numRequests.bumpDay();
            numUpdates.bumpDay();
            getRequests.bumpDay();
            urlRequests.bumpDay();
            hostRequests.bumpDay();
            //Requests by client
            for(Iterator i = clientRequests.entrySet().iterator();i.hasNext();){
                ((Counter)((Map.Entry)i.next()).getValue()).bumpDay();
            }
            //Request by client/version
            for(Iterator i = clientVersionRequests.entrySet().iterator();i.hasNext();){
                ((Counter)((Map.Entry)i.next()).getValue()).bumpDay();
            }
        } else if (thisHour != lastHour) {
            lastHour = thisHour;
            numRequests.bumpHour();
            numUpdates.bumpHour();
            getRequests.bumpHour();
            urlRequests.bumpHour();
            hostRequests.bumpHour();
            //Requests by client
            for(Iterator i = clientRequests.entrySet().iterator();i.hasNext();){
                ((Counter)((Map.Entry)i.next()).getValue()).bumpHour();
            }
            //Request by client/version
            for(Iterator i = clientVersionRequests.entrySet().iterator();i.hasNext();){
            	((Counter)((Map.Entry)i.next()).getValue()).bumpHour();
            }
        }

    }

    /**
     * Increment the number of requests received.
     */
    public void bumpRequests() {
        numRequests.bumpCount();
    }

    /**
     * Increment the number of updates received.
     */
    public void bumpUpdates() {
        numUpdates.bumpCount();
    }
    
    /**
     * Increment the number of urlfile request
     * This is a GWC1 request
     */
    public void bumpUrlRequests(){
        urlRequests.bumpCount();
    }
	
    /**
     * Increment the number of host request
     * This is a GWC1 request
     */
    public void bumpHostRequests(){
        hostRequests.bumpCount();
    }
	
    /**
     * Increment the number of get request
     * This is a GWC2 request
     */
    public void bumpGetRequests(){
        getRequests.bumpCount();
    }
	
    /**
     * This bump the client only and client/version count
     * @param c
     */
    public void bumpByClient(ClientVersion c) {
        String client = c.getClient();

        //This is not very well syncronized, but a mistake
        //make that we will not count 1 request
		
        //We bump the number of request for this client
        Counter count = (Counter)clientRequests.get(client);
        if(count == null){
            clientRequests.put(client,new Counter(1));
        } else
            count.bumpCount();

        //We bump the number of request for this client/version
        count = (Counter)clientVersionRequests.get(c);
        if(count == null){
            clientVersionRequests.put(c,new Counter(1));
        } else
            count.bumpCount();
    } 

}
