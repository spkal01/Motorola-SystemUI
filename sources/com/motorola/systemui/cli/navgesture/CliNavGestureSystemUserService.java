package com.motorola.systemui.cli.navgesture;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.android.systemui.Dependency;

public class CliNavGestureSystemUserService extends Service {
    public void onCreate() {
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        MultiUserCliNavGestures multiUserCliNavGestures = (MultiUserCliNavGestures) Dependency.get(MultiUserCliNavGestures.class);
        if (multiUserCliNavGestures != null) {
            return multiUserCliNavGestures.getSystemUserCallbacks();
        }
        return null;
    }
}
