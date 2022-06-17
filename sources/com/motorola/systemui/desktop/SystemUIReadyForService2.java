package com.motorola.systemui.desktop;

import android.util.Log;

public class SystemUIReadyForService2 extends SystemUIReadyForService {
    public void onCreate() {
        Log.d("SystemUIReadyForService", "SystemUIReadyForService2 onCreate");
        super.onCreate();
    }

    public void onDestroy() {
        Log.d("SystemUIReadyForService", "SystemUIReadyForService2 onDestroy");
        super.onDestroy();
    }
}
