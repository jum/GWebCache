package org.mager.gwebcache;

import java.io.Serializable;

/**
 * @author Frédéric Bastien
 * This is a counter that allow to record with time
 */
public class Counter implements Serializable {
    public static final int HOUR_UPDATE = 1;
    public static final int DAY_UPDATE = 2;
    public static final int WEEK_UPDATE = 3;

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
     * We bump the HOUR, DAY or WEEK time.
     * @param time the constant that repensent the time to bump
     */
    public void bumpTime(int time){
        if(time == HOUR_UPDATE)
            bumpHour();
        else if(time == DAY_UPDATE)
            bumpDay();
        else if(time == WEEK_UPDATE)
            bumpWeek();
        else 
            throw new IllegalArgumentException("the argument of " +
                "bumpTime must be the constant of HOUR,DAY or WEEK");
    }
    /**
     * We make the change needed when we change of hour
     */
    private void bumpHour() {
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
    private void bumpDay() {
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
    private void bumpWeek() {
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
