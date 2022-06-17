package com.motorola.systemui.screenshot;

import android.app.ActivityManager;
import android.app.IApplicationThread;
import android.app.ILongScreenshotListener;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

public class LongScreenShotHelper {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static LongScreenShotHelper sLastStartedLongScreenShotHelper;
    private IApplicationThread mIApplicationThread;
    private ILongScreenshotListener mILongScreenshotListener = new ILongScreenshotListener.Stub() {
        public void onScreenshotUpdate(int[] iArr, Rect rect) {
            if (LongScreenShotHelper.this.mOnLongScreenshotListener != null) {
                LongScreenShotHelper.this.mOnLongScreenshotListener.onScreenShotUpdate(iArr, rect);
            }
        }

        public void onScreenshotFail() {
            if (LongScreenShotHelper.this.mOnLongScreenshotListener != null) {
                LongScreenShotHelper.this.mOnLongScreenshotListener.onScreenShotFail();
            }
        }

        public void onLongScreenshotReady(boolean z, Rect rect) {
            if (LongScreenShotHelper.this.mOnLongScreenshotListener != null) {
                LongScreenShotHelper.this.mOnLongScreenshotListener.onLongScreenshotReady(z, rect);
            }
        }
    };
    /* access modifiers changed from: private */
    public OnLongScreenshotListener mOnLongScreenshotListener;

    public interface OnLongScreenshotListener {
        void onLongScreenshotReady(boolean z, Rect rect);

        void onScreenShotFail() {
        }

        void onScreenShotUpdate(int[] iArr, Rect rect) {
        }
    }

    public LongScreenShotHelper(Context context) {
        init(context);
    }

    private void init(Context context) {
        this.mIApplicationThread = ((ActivityManager) context.getSystemService("activity")).getFocusedWindowApplicationThread();
    }

    public boolean startLongScreeenShot(OnLongScreenshotListener onLongScreenshotListener) {
        LongScreenShotHelper longScreenShotHelper = sLastStartedLongScreenShotHelper;
        if (!(longScreenShotHelper == null || longScreenShotHelper == this)) {
            longScreenShotHelper.stopLongScreenShot();
        }
        sLastStartedLongScreenShotHelper = this;
        IApplicationThread iApplicationThread = this.mIApplicationThread;
        if (iApplicationThread == null) {
            Log.w("LongScreenShotHelper", "startLongScreeenShot mIApplicationThread = null");
            return false;
        }
        this.mOnLongScreenshotListener = onLongScreenshotListener;
        try {
            iApplicationThread.startLongScreenshot(this.mILongScreenshotListener);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void continueLongScreenShot(int i) {
        IApplicationThread iApplicationThread = this.mIApplicationThread;
        if (iApplicationThread != null) {
            try {
                iApplicationThread.continueLongScreenshot(i);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopLongScreenShot() {
        IApplicationThread iApplicationThread = this.mIApplicationThread;
        if (iApplicationThread != null) {
            try {
                iApplicationThread.stopLongScreenshot();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.mIApplicationThread = null;
            this.mILongScreenshotListener = null;
            sLastStartedLongScreenShotHelper = null;
            this.mOnLongScreenshotListener = null;
        }
    }
}
