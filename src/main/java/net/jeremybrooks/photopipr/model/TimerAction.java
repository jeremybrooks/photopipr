/*
 *  PhotoPipr is Copyright 2017-2023 by Jeremy Brooks
 *
 *  This file is part of PhotoPipr.
 *
 *   PhotoPipr is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   PhotoPipr is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with PhotoPipr.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jeremybrooks.photopipr.model;

import net.jeremybrooks.photopipr.PPConstants;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimerAction implements Action {

    private int hours = 1;
    private int minutes;
    private int seconds;
    private String timerMode = PPConstants.TimerMode.TIMER.name();

    private transient Status status = Status.IDLE;
    private transient String statusMessage;


    public String getDescription() {
        if (timerMode.equals(PPConstants.TimerMode.TIMER.name())) {
            return String.format("Sleep for %02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("Sleep until %02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String toString() {
        return getDescription();
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public long getSleepMillis() {
        if (timerMode.equals(PPConstants.TimerMode.TIMER.name())) {
            return ((hours * 3600L) + (minutes * 60L) + seconds) * 1000;
        } else {
            // make a calendar with current time/date
            Calendar cal = new GregorianCalendar();
            // set the calendar time fields to the selected wake time
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, seconds);
            // if the time is earlier than the current time, make it tomorrow
            if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
            }
            return (cal.getTimeInMillis() - System.currentTimeMillis());
        }
    }

    public String getTimerMode() {
        return timerMode;
    }

    public void setTimerMode(String timerMode) {
        this.timerMode = timerMode;
    }
}
