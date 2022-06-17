package com.android.systemui.navigationbar;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import android.view.Display;
import android.widget.Toast;
import com.android.systemui.R$string;
import com.android.systemui.SysUIToast;
import com.android.systemui.moto.MotoFeature;
import com.motorola.internal.app.MotoDesktopManager;

public class ScreenPinningNotify {
    private final Context mContext;
    private DisplayManager mDisplayManager;
    private long mLastShowToastTime;
    private Toast mLastToast;

    public ScreenPinningNotify(Context context) {
        this.mContext = context;
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
    }

    public void showPinningStartToast() {
        makeAllUserToastAndShow(R$string.screen_pinning_start);
    }

    public void showPinningExitToast() {
        makeAllUserToastAndShow(R$string.screen_pinning_exit);
    }

    public void showEscapeToast(boolean z, boolean z2) {
        int i;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - this.mLastShowToastTime < 1000) {
            Slog.i("ScreenPinningNotify", "Ignore toast since it is requested in very short interval.");
            return;
        }
        Toast toast = this.mLastToast;
        if (toast != null) {
            toast.cancel();
        }
        Toast showToastForDesktop = showToastForDesktop(R$string.desktop_screen_pinning_toast);
        this.mLastToast = showToastForDesktop;
        if (showToastForDesktop == null) {
            if (z) {
                i = R$string.screen_pinning_toast_gesture_nav;
            } else if (z2) {
                i = R$string.screen_pinning_toast;
            } else {
                i = R$string.screen_pinning_toast_recents_invisible;
            }
            this.mLastToast = makeAllUserToastAndShow(i);
        }
        this.mLastShowToastTime = elapsedRealtime;
    }

    private Toast makeAllUserToastAndShow(int i) {
        Context context = this.mContext;
        if (MotoFeature.getInstance(context).isSupportCli() && MotoFeature.isLidClosed(this.mContext)) {
            context = MotoFeature.getCliContext(this.mContext);
        }
        Toast makeText = SysUIToast.makeText(context, i, 1);
        makeText.show();
        return makeText;
    }

    private Toast showToastForDesktop(int i) {
        int i2;
        if (!MotoDesktopManager.isDesktopConnected(this.mContext)) {
            return null;
        }
        try {
            ActivityTaskManager.RootTaskInfo focusedRootTaskInfo = ActivityManager.getService().getFocusedRootTaskInfo();
            if (!(focusedRootTaskInfo == null || (i2 = focusedRootTaskInfo.displayId) == 0)) {
                return showDisplayToast(this.mDisplayManager.getDisplay(i2), i);
            }
        } catch (RemoteException unused) {
        }
        return null;
    }

    private Toast showDisplayToast(Display display, int i) {
        Toast makeText = SysUIToast.makeText(this.mContext.createDisplayContext(display), i, 0);
        makeText.show();
        return makeText;
    }
}
