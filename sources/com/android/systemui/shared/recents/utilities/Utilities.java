package com.android.systemui.shared.recents.utilities;

import android.os.Handler;

public class Utilities {
    public static void postAtFrontOfQueueAsynchronously(Handler handler, Runnable runnable) {
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage().setCallback(runnable));
    }
}
