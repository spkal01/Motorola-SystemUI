package com.android.systemui.demomode;

import android.os.Bundle;

public interface DemoModeCommandReceiver {
    void dispatchDemoCommand(String str, Bundle bundle);

    void onDemoModeFinished() {
    }

    void onDemoModeStarted() {
    }
}
