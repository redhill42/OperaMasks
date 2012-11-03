/*
 * $Id: ProgressStatus.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.operamasks.faces.component.ajax;

public class ProgressStatus
{
    private ProgressAction action;
    private ProgressState state;
    private int phase;
    private int percentage;
    private String message;

    public ProgressStatus(ProgressAction action) {
        this.action = action;
    }

    public ProgressAction getAction() {
        return this.action;
    }

    public ProgressState getState() {
        return this.state;
    }

    public void setState(ProgressState state) {
        this.state = state;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        if (percentage < 0)
            percentage = 0;
        else if (percentage > 100)
            percentage = 100;
        this.percentage = percentage;
    }

    public void setPercentage(long numerator, long denominator) {
        if (numerator < 0)
            numerator = 0;
        else if (numerator > denominator)
            numerator = denominator;
        this.percentage = (int)(100*numerator/denominator);
    }

    public void setPercentage(long lowBound, long highBound, long currentValue) {
        if (currentValue < lowBound)
            currentValue = lowBound;
        else if (currentValue >= highBound)
            currentValue = highBound;
        this.percentage  = (int)((currentValue - lowBound) / (highBound - lowBound));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStopped() {
        return state == ProgressState.STOPPED;
    }

    public boolean isPaused() {
        return state == ProgressState.PAUSED;
    }

    public boolean isCompleted() {
        return state == ProgressState.COMPLETED;
    }

    public boolean isFailed() {
        return state == ProgressState.FAILED;
    }

    public boolean isRunning() {
        return state == ProgressState.RUNNING;
    }
}
