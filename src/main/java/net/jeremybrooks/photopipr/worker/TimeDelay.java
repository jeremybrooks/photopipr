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

package net.jeremybrooks.photopipr.worker;

import net.jeremybrooks.photopipr.model.Action;

import java.util.Date;

public class TimeDelay {
    private final WorkflowRunner workflowRunner;
    private final Action action;
    private final int index;
    private final String message;

    private final long sleepMillis;

    public TimeDelay(WorkflowRunner workflowRunner, Action action, int index, String message, long sleepMillis) {
        this.workflowRunner = workflowRunner;
        this.action = action;
        this.index = index;
        this.message = message;
        this.sleepMillis = sleepMillis;
    }

    void go() throws InterruptedException {
        long waitUntil = ((System.currentTimeMillis() + sleepMillis) / 1000)
                * 1000 + 1000;
        action.setStatus(Action.Status.RUNNING);
        do {
            long remainingSeconds = (waitUntil - System.currentTimeMillis()) / 1000;

            action.setStatusMessage(String.format("%s %02d:%02d:%02d",
                    message,
                    (remainingSeconds / (60 * 60)) % 24,
                    (remainingSeconds / 60) % 60,
                    remainingSeconds % 60));

            workflowRunner.publish(action, index);
            Thread.sleep(500);
        } while (System.currentTimeMillis() < waitUntil);
        action.setStatus(Action.Status.IDLE);
        action.setStatusMessage("Timer completed at " + new Date());
        workflowRunner.publish(action, index);
    }
}
