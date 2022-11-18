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

import net.jeremybrooks.photopipr.gui.WorkflowWindow;
import net.jeremybrooks.photopipr.model.Action;
import net.jeremybrooks.photopipr.model.FinishAction;
import net.jeremybrooks.photopipr.model.TimerAction;
import net.jeremybrooks.photopipr.model.UploadAction;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import java.util.Date;
import java.util.List;

import static net.jeremybrooks.photopipr.PPConstants.*;

public class WorkflowRunner extends SwingWorker<Void, WorkflowRunner.ActionUpdate> {

    private final DefaultListModel<Action> actionListModel;

    public WorkflowRunner(DefaultListModel<Action> actionListModel) {
        this.actionListModel = actionListModel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        WorkflowWindow.getInstance().setBusy(true);
        // set all the actions to PENDING
        for (int i = 0; i < actionListModel.size(); i++) {
            Action a = actionListModel.get(i);
            a.setStatus(Action.Status.PENDING);
            a.setStatusMessage("Execution pending...");
            publish(a, i);
        }
        boolean repeat = false;
        do {
            for (int i = 0; i < actionListModel.size(); i++) {
                Action a = actionListModel.get(i);
                if (a instanceof TimerAction ta) {
                    new TimeDelay(this, ta, i, "Sleeping", ta.getSleepMillis()).go();

                } else if (a instanceof UploadAction ua) {
                    new Upload(ua, i, this).go();

                } else if (a instanceof FinishAction fa) {
                    switch (fa.getFinishMode()) {
                        case FINISH_ACTION_EXIT -> {
                            new TimeDelay(this, fa, i, "Exiting in", 3000).go();
                            System.exit(0);
                        }
                        case FINISH_ACTION_REPEAT -> {
                            repeat = fa.getFinishMode().equals(FINISH_ACTION_REPEAT);
                            for (int j = 0; j < actionListModel.size(); j++) {
                                Action action = actionListModel.get(j);
                                action.setStatus(Action.Status.PENDING);
                                publish(new ActionUpdate(action, j));
                            }
                        }
                        case FINISH_ACTION_STOP -> {
                            fa.setStatus(Action.Status.IDLE);
                            fa.setStatusMessage("Workflow completed at " + new Date());
                            publish(fa, i);
                        }
                    }
                } else {
                    // todo unknown action
                }
            }
        } while (repeat);
        WorkflowWindow.getInstance().workflowRunnerFinished();
        return null;
    }

    public void cancelWorkflow() {
        cancel(true);
        for (int i = 0; i < actionListModel.size(); i++) {
            Action a = actionListModel.get(i);
            a.setStatus(Action.Status.IDLE);
            a.setStatusMessage("Execution canceled.");
            publish(a, i);
        }
        WorkflowWindow.getInstance().workflowRunnerFinished();
    }


    void publish(Action action, int index) {
        publish(new ActionUpdate(action, index));
    }

    @Override
    protected void process(List<ActionUpdate> chunks) {
        chunks.forEach(c -> actionListModel.set(c.index, c.action));
    }

    public static class ActionUpdate {
        private final Action action;
        private final int index;
        ActionUpdate(Action action, int index) {
            this.action = action;
            this.index = index;
        }
    }
}
