package org.mager.gwebcache;

import java.io.Serializable;

/**
 * @author Frédéric Bastien
 * This is a counter that allow to record with time
 */
public class Counter implements Serializable {

    //They are not always up to date.
    //Use the accessor method to get the real value
    private volatile int total = 0;
    private volatile int thisHour = 0;
    private volatile int lastHour = 0;
    private volatile int thisDay = 0;
    private volatile int lastDay = 0;
    private volatile int thisWeek = 0;
    private volatile int lastWeek = 0;
    /**
     * The counter is initialized to 0.
     */
    public Counter() {
    }
    /**
     * We initialize the counter to nb
     * @param nb the number used for initialized the counter
     */
    public Counter(int nb) {
        thisHour = nb;
    }
    /**
     * Add a count in this object
     */
    public void bumpCount() {
        thisHour++;
    }
    /**
     * We make the change needed when we change of hour
     */
    public void bumpHour() {
        int tmp = thisHour;
        thisHour -= tmp;
        lastHour = tmp;
        total += tmp;
        thisDay += tmp;
        thisWeek += tmp;
    }
    /**
     * We make the change for stat when the
     * day change. DON'T CALL bumpHour()
     */
    public void bumpDay() {
        bumpHour();
        int tmp = thisDay;
        thisDay -= tmp;
        lastDay = tmp;
    }
    /**
     * We make the change for stat when the
     * day change. DON'T CALL bumpHour()
     * or bumpDay()
     */
    public void bumpWeek() {
        bumpHour();
        bumpDay();
        int tmp = thisWeek;
        thisWeek -= tmp;
        lastWeek = tmp;
    }
    /**
     * @return The count in the current hour
     */
    public int getThisHourCount() {
        return thisHour;
    }
    /**
     * @return The count in the last hour.
     */
    public int getLastHourCount() {
        return lastHour;
    }
    /**
     * @return The count for this day.
     */
    public int getThisDayCount() {
        return thisDay + thisHour;
    }
    /**
     * @return The count for the last day.
     */
    public int getLastDayCount() {
        return lastDay;
    }
    /**
     * @return The count for this week.
     */
    public int getThisWeekCount() {
        return thisWeek + thisHour;
    }
    /**
     * @return The count for the last week.
     */
    public int getLastWeekCount() {
        return lastWeek;
    }
    /**
     * @return The total number of count
     */
    public int getTotalCount() {
        return total + thisHour;
    }

}
