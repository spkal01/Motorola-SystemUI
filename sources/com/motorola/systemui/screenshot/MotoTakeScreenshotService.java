package com.motorola.systemui.screenshot;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.UserManager;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.ScreenshotHelper;
import com.android.systemui.screenshot.MotoGlobalScreenshot;

public class MotoTakeScreenshotService extends Service {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static SparseArray<ScreenshotHelper.ScreenshotRequest> sScreenshotRequest = new SparseArray<>();
    private static int sScreenshotRequestIndex = 0;
    private MotoGlobalScreenshot mScreenshot;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public MotoTakeScreenshotService(MotoGlobalScreenshot motoGlobalScreenshot, UserManager userManager) {
        this.mScreenshot = motoGlobalScreenshot;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        boolean z = DEBUG;
        if (z) {
            Log.d("MotoTakeScreenshotService", "onStartCommand intent: " + intent);
        }
        if (intent == null) {
            Log.w("MotoTakeScreenshotService", "onStartCommand intent is null");
            return 1;
        }
        ScreenshotHelper.ScreenshotRequest screenshotRequest = null;
        String action = intent.getAction();
        action.hashCode();
        if (action.equals("take_screenshot_provided_image")) {
            screenshotRequest = pullScreenshotRequest(intent.getIntExtra("requestIndex", 0));
        } else if (!action.equals("take_screenshot_fullscreen")) {
            Log.w("MotoTakeScreenshotService", "onStartCommand invalid action");
            return 1;
        }
        int intExtra = intent.getIntExtra("displayId", 0);
        int intExtra2 = intent.getIntExtra("extraType", 0);
        if (z) {
            Log.d("MotoTakeScreenshotService", "onStartCommand displayId: " + intExtra);
        }
        if (screenshotRequest != null) {
            this.mScreenshot.takeScreenshot(intExtra, screenshotRequest);
        } else {
            this.mScreenshot.takeScreenshot(intExtra, intExtra2);
        }
        return 1;
    }

    public static int pushScreenshotRequest(ScreenshotHelper.ScreenshotRequest screenshotRequest) {
        int i;
        synchronized (sScreenshotRequest) {
            int i2 = sScreenshotRequestIndex + 1;
            sScreenshotRequestIndex = i2;
            sScreenshotRequest.put(i2, screenshotRequest);
            i = sScreenshotRequestIndex;
        }
        return i;
    }

    private static ScreenshotHelper.ScreenshotRequest pullScreenshotRequest(int i) {
        ScreenshotHelper.ScreenshotRequest screenshotRequest;
        synchronized (sScreenshotRequest) {
            screenshotRequest = sScreenshotRequest.get(i);
            sScreenshotRequest.remove(i);
        }
        return screenshotRequest;
    }
}
