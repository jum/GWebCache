/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

import java.io.*;
import java.util.*;
import javax.servlet.*;

/**
 * Implement the simple statistics as defined per the V1 protocol,
 * stat by client/version, by this day/last day and this week/last week.
 * Also used by stats.jsp for display.
 * Follow the singleton pattern
 */
public class Stats  implements Serializable {

    /**
     * The date this cache was started.
     */
    public Date startTime;
    /**
     * The date this cache was stoped.
     * Will be used to determine the average uptime/downtime
     */
    public Date stopTime;
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
     * This is a GWC1 requests
     */
    public volatile Counter urlRequests;

    /**
     * The number of hostfile requests received.
     * This is a GWC1 requests
     */
    public volatile Counter hostRequests;

    /**
     * The number of get requests received.
     * This is a GWC1 update.
     */
    public volatile Counter updateRequestsGWC1;
    /**
     * The number of get requests received.
     * This is a GWC2 update.
     */
    public volatile Counter updateRequestsGWC2;
    /**
     * The number of get requests received.
     * This is a GWC2 requests for url and host
     */
    public volatile Counter getRequests;

    /**
     * The number of stats page requests received.
     */
    public volatile Counter numStatsRequests;
    /**
     * The number of data page requests received.
     */
    public volatile Counter numDataRequests;
    /**
     * The number of index page requests received.
     */
    public volatile Counter numIndexRequests;
    /**
     * The number of licence page requests received.
     */
    public volatile Counter numLicenseRequests;
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
        updateRequestsGWC1 = new Counter();
        updateRequestsGWC2 = new Counter();
        numStatsRequests = new Counter();;
        numDataRequests = new Counter();;
        numIndexRequests = new Counter();;
        numLicenseRequests = new Counter();;
        startTime = new Date();
        lastHour = startTime.getTime()/Data.MILLIS_PER_HOUR;
        lastDay = startTime.getTime()/Data.MILLIS_PER_DAY;
        lastWeek = startTime.getTime()/Data.MILLIS_PER_WEEK;
        stopTime = null;
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
     * stats from this hour to that of the previous hour. Do
     * it also for day and week.
     * @param now An instance of Date signifying the current
     * time.
    */
     public void bumpHour(Date now) {
        now.getTime();
     }
    /**
     * Check if we are past the current hour and move the
     * stats from this hour to that of the previous hour. Do
     * it also for day and week
     * @param now the time in millisecond since 1st january 1970
	*/
     public void bumpHour(long now) {
        long thisHour = now / Data.MILLIS_PER_HOUR;
        long thisDay = now / Data.MILLIS_PER_DAY;
        long thisWeek = now / Data.MILLIS_PER_WEEK;
        if (thisWeek != lastWeek) {
            lastWeek = thisWeek;

            numRequests.bumpWeek();
            numUpdates.bumpWeek();
            getRequests.bumpWeek();
            urlRequests.bumpWeek();
            hostRequests.bumpWeek();
            updateRequestsGWC1.bumpWeek();
            updateRequestsGWC2.bumpWeek();
            numStatsRequests.bumpWeek();
            numDataRequests.bumpWeek();
            numIndexRequests.bumpWeek();
            numLicenseRequests.bumpWeek();
            //Requests by client
            for (Iterator i = clientRequests.entrySet().iterator();
                                                            i.hasNext();) {
                ((Counter) ((Map.Entry) i.next()).getValue()).bumpWeek();
            }
            //Request by client/version
            for (Iterator i = clientVersionRequests.entrySet().iterator();
				                            i.hasNext();) {
                ((Counter) ((Map.Entry) i.next()).getValue()).bumpWeek();
            }
        } else if (thisDay != lastDay) {
            lastDay = thisDay;

            numRequests.bumpDay();
            numUpdates.bumpDay();
            getRequests.bumpDay();
            urlRequests.bumpDay();
            hostRequests.bumpDay();
            updateRequestsGWC1.bumpDay();
            updateRequestsGWC2.bumpDay();
            numStatsRequests.bumpDay();
            numDataRequests.bumpDay();
            numIndexRequests.bumpDay();
            numLicenseRequests.bumpDay();
            //Requests by client
            for(Iterator i = clientRequests.entrySet().iterator();
                                                            i.hasNext();){
                ((Counter)((Map.Entry)i.next()).getValue()).bumpDay();
            }
            //Request by client/version
            for(Iterator i = clientVersionRequests.entrySet().iterator();
                                                            i.hasNext();){
                ((Counter)((Map.Entry)i.next()).getValue()).bumpDay();
            }
        } else if (thisHour != lastHour) {
            lastHour = thisHour;
            numRequests.bumpHour();
            numUpdates.bumpHour();
            getRequests.bumpHour();
            urlRequests.bumpHour();
            hostRequests.bumpHour();
            updateRequestsGWC1.bumpHour();
            updateRequestsGWC2.bumpHour();
            numStatsRequests.bumpHour();
            numDataRequests.bumpHour();
            numIndexRequests.bumpHour();
            numLicenseRequests.bumpHour();
            //Requests by client
            for(Iterator i = clientRequests.entrySet().iterator();
                                                            i.hasNext();){
                ((Counter)((Map.Entry)i.next()).getValue()).bumpHour();
            }
            //Request by client/version
            for(Iterator i = clientVersionRequests.entrySet().iterator();
                                                            i.hasNext();){
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
     * Increment the total number of updates received.
     */
    private void bumpUpdates() {
        numUpdates.bumpCount();
    }
    /**
     * Increment the number of GWC1 updates received.
     * and total number of updates received.
     */
    public void bumpGWC1Updates() {
        bumpUpdates();
        updateRequestsGWC1.bumpCount();
    }
    /**
     * Increment the number GWC2 of updates received
     * and total number of updates received.
     */
    public void bumpGWC2Updates() {
        bumpUpdates();
        updateRequestsGWC2.bumpCount();
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
    /**
     * Find the location for the cache stats file. If the "filename"
     * init parameter is not given, use a file in the servlet container
     * provided temporary working directory.
     * @param context The ServletContext to use for logging and
     * for for extracting runtime arguments.
     * @return A File object describing the data file.
     */
    public static File statsFile(ServletContext context) {
        File f;
        String fname = context.getInitParameter("statsfilename");
        if (fname != null)
            f = new File(fname);
        else {
            File tmpDir =
                (File)context.getAttribute("javax.servlet.context.tempdir");
            f = new File(tmpDir, "gwebcache.stats");
        }
        //context.log("datafile=" + f);
        return f;
    }
    /**
     * Write the cache date to disk using serialization.
     * @param context The ServletContext to use for logging.
     */
    public static void writeStats(ServletContext context) {
        try {
            ObjectOutputStream o = new ObjectOutputStream(
                                    new FileOutputStream(statsFile(context)));
            synchronized (instance) {
                instance.stopTime = new Date();
                o.writeObject(instance);
            }
            o.close();
        } catch (Exception e) {
            context.log("GWebCache:writeStats", e);
        }
    }
    /**
     * Read the cache data from disk using serialization. If an
     * error occurs, instanciate an empty cache.
     * @param context The ServletContext to use for logging.
     */
    public static void readStats(ServletContext context) {
        Stats newStats = null;
        try {
            ObjectInputStream i = new ObjectInputStream(
                                    new FileInputStream(statsFile(context)));
            newStats = (Stats)i.readObject();
            i.close();
        } catch (FileNotFoundException e) {
            context.log("GWebCache:no stats file");
        } catch (Exception e) {
            context.log("GWebCache:readStats", e);
        }
        if (newStats == null)
            newStats = new Stats();
        instance = newStats;
        synchronized (instance) {
            Date now = new Date();
            instance.startTime = now;
            for (Date hour = new Date(instance.lastHour*Data.MILLIS_PER_HOUR
                                + Data.MILLIS_PER_HOUR);
                 hour.getTime()<now.getTime();
                 hour.setTime(hour.getTime() + Data.MILLIS_PER_HOUR)) {
                    instance.bumpHour(hour);
            }

        }
    }
}
