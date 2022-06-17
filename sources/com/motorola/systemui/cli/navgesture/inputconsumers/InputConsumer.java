package com.motorola.systemui.cli.navgesture.inputconsumers;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface InputConsumer {
    public static final String[] NAMES = {"TYPE_NO_OP", "TYPE_OVERVIEW", "TYPE_OTHER_ACTIVITY", "TYPE_RESET_GESTURE", "TYPE_DEVICE_LOCKED"};
    public static final InputConsumer NO_OP = InputConsumer$$ExternalSyntheticLambda0.INSTANCE;

    /* access modifiers changed from: private */
    static /* synthetic */ int lambda$static$0() {
        return 1;
    }

    InputConsumer getActiveConsumerInHierarchy() {
        return this;
    }

    int getType();

    boolean isConsumerDetachedFromGesture() {
        return false;
    }

    void onConsumerAboutToBeSwitched() {
    }

    void onKeyEvent(KeyEvent keyEvent) {
    }

    void onMotionEvent(MotionEvent motionEvent) {
    }

    String getName() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            String[] strArr = NAMES;
            if (i >= strArr.length) {
                return sb.toString();
            }
            if ((getType() & (1 << i)) != 0) {
                if (sb.length() > 0) {
                    sb.append(":");
                }
                sb.append(strArr[i]);
            }
            i++;
        }
    }
}
