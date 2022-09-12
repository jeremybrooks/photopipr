/*
 *  PhotoPipr is Copyright 2017-2022 by Jeremy Brooks
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

package net.jeremybrooks.photopipr.action;

import static net.jeremybrooks.photopipr.PPConstants.*;

public class FinishAction implements Action {
    private String finishMode = FINISH_ACTION_STOP;
    private transient Status status = Status.IDLE;

    private transient String statusMessage = " ";

    public String getDescription() {
        String description;
        switch (finishMode) {
            case FINISH_ACTION_EXIT -> description = "Exit PhotoPipr";
            case FINISH_ACTION_REPEAT -> description = "Start over at the first Action";
            default -> description = "Stop";
        }
        return description;
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

    public String getFinishMode() {
        return finishMode;
    }

    public void setFinishMode(String finishMode) {
        this.finishMode = finishMode;
    }
}
