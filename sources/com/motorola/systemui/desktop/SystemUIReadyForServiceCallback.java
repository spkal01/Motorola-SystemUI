package com.motorola.systemui.desktop;

import android.content.ComponentName;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.motorola.taskbar.ISystemUIReadyForServiceCallback;

public class SystemUIReadyForServiceCallback {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static ISystemUIReadyForServiceCallback mISystemUIReadyForServiceCallback;

    public static void setISystemUIReadyForServiceCallback(ISystemUIReadyForServiceCallback iSystemUIReadyForServiceCallback) {
        mISystemUIReadyForServiceCallback = iSystemUIReadyForServiceCallback;
    }

    public static void onTileStartListening(ComponentName componentName) {
        if (mISystemUIReadyForServiceCallback == null) {
            Log.w("SystemUIR4SCallback", "onTileStartListening with null callback: " + componentName);
            return;
        }
        if (DEBUG) {
            Log.d("SystemUIR4SCallback", "onTileStartListening mComponentName: " + componentName);
        }
        try {
            mISystemUIReadyForServiceCallback.onTileStartListening(componentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onTileStopListening(ComponentName componentName) {
        if (mISystemUIReadyForServiceCallback == null) {
            Log.w("SystemUIR4SCallback", "onTileStopListening with null callback: " + componentName);
            return;
        }
        if (DEBUG) {
            Log.d("SystemUIR4SCallback", "onTileStopListening mComponentName: " + componentName);
        }
        try {
            mISystemUIReadyForServiceCallback.onTileStopListening(componentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onTileClick(ComponentName componentName, IBinder iBinder) {
        if (mISystemUIReadyForServiceCallback == null) {
            Log.w("SystemUIR4SCallback", "onTileClick with null callback: " + componentName);
            return;
        }
        if (DEBUG) {
            Log.d("SystemUIR4SCallback", "onTileClick mComponentName: " + componentName);
        }
        try {
            mISystemUIReadyForServiceCallback.onTileClick(componentName, iBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTileBindRequested(ComponentName componentName, boolean z) {
        if (mISystemUIReadyForServiceCallback == null) {
            Log.w("SystemUIR4SCallback", "setTileBindRequested with null callback: " + componentName + "; bindRequested: " + z);
            return;
        }
        if (DEBUG) {
            Log.d("SystemUIR4SCallback", "setTileBindRequested mComponentName: " + componentName + "; bindRequested" + z);
        }
        try {
            mISystemUIReadyForServiceCallback.setTileBindRequested(componentName, z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
