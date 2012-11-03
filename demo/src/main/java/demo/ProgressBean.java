/*
 * $Id: ProgressBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
 *
 * Copyright (c) 2000-2006 Apusic Software, Inc.
 * All rights reserved
 */

package demo;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.ajax.ProgressAction;
import org.operamasks.faces.component.ajax.ProgressState;
import org.operamasks.faces.component.ajax.ProgressStatus;

/**
 * This bean is for illustration only. An actual progress object should query
 * external resource to get progress status.
 */
@ManagedBean(scope=ManagedBeanScope.SESSION)
public class ProgressBean
{
    private int percentage = 0;

    public void percentageAction(ProgressStatus status) {
        switch (status.getAction().ordinal()) {
          case ProgressAction._START:
            this.percentage = 0;
            status.setPercentage(0);
            status.setState(ProgressState.RUNNING);
            break;

          case ProgressAction._STOP:
            this.percentage = 0;
            status.setPercentage(0);
            status.setMessage("Progress stopped");
            status.setState(ProgressState.STOPPED);
            break;

          case ProgressAction._POLL:
            this.percentage += 4;
            if (percentage >= 100) {
                status.setPercentage(100);
                status.setMessage("Progress completed");
                status.setState(ProgressState.COMPLETED);
            } else {
                status.setPercentage(percentage);
                status.setState(ProgressState.RUNNING);
            }
            break;

          case ProgressAction._PAUSE:
            status.setPercentage(percentage);
            status.setMessage("Progress paused");
            status.setState(ProgressState.PAUSED);
            break;

          case ProgressAction._RESUME:
            status.setPercentage(percentage);
            status.setState(ProgressState.RUNNING);
            break;
        }
    }

    private int phase = 0;
    private int maxPhase = 4;

    private String phaseMessages[] = {
        "Initializing...",
        "Loading...",
        "Configuring...",
        "Running...",
        "Finished"
    };

    public void phaseAction(ProgressStatus status) {
        switch (status.getAction().ordinal()) {
          case ProgressAction._START:
            this.phase = 0;
            status.setPercentage(0);
            status.setMessage(phaseMessages[0]);
            status.setState(ProgressState.RUNNING);
            break;

          case ProgressAction._POLL:
            if (phase < maxPhase) {
                this.phase++;
                status.setPercentage(phase, maxPhase);
                status.setMessage(phaseMessages[phase]);
                if (phase >= maxPhase) {
                    status.setState(ProgressState.COMPLETED);
                } else {
                    status.setState(ProgressState.RUNNING);
                }
            }
            break;
        }
    }

    public void complexAction(ProgressStatus status) {
        switch (status.getAction().ordinal()) {
        case ProgressAction._START:
            this.phase = 0;
            this.percentage = 0;
            status.setPhase(phase);
            status.setPercentage(percentage);
            status.setMessage(phaseMessages[0]);
            status.setState(ProgressState.RUNNING);
            break;

        case ProgressAction._POLL:
            boolean completed = false;
            percentage += 25;
            if (percentage == 100) {
                if (++phase >= maxPhase) {
                    phase = maxPhase;
                    completed = true;
                }
            } else if (percentage > 100) {
                percentage = 0;
            }

            status.setPhase(phase);
            status.setPercentage(percentage);
            status.setMessage(phaseMessages[phase]);
            status.setState(completed ? ProgressState.COMPLETED : ProgressState.RUNNING);
        }
    }
}
