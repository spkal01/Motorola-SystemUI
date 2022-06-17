package com.motorola.systemui.cli.navgesture.inputconsumers;

import android.view.MotionEvent;
import com.motorola.systemui.cli.navgesture.animation.remote.TaskAnimationManager;

public class ResetGestureInputConsumer implements InputConsumer {
    private final TaskAnimationManager mTaskAnimationManager;

    public int getType() {
        return 8;
    }

    public ResetGestureInputConsumer(TaskAnimationManager taskAnimationManager) {
        this.mTaskAnimationManager = taskAnimationManager;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            this.mTaskAnimationManager.finishRunningRecentsAnimation(false);
        }
    }
}
