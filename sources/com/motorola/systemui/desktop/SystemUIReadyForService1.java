package com.motorola.systemui.desktop;

import android.util.Log;

public class SystemUIReadyForService1 extends SystemUIReadyForService {
    public void onCreate() {
        Log.d("SystemUIReadyForService", "SystemUIReadyForService1 onCreate");
        super.onCreate();
    }

    public void onDestroy() {
        Log.d("SystemUIReadyForService", "SystemUIReadyForService1 onDestroy");
        super.onDestroy();
    }
}
