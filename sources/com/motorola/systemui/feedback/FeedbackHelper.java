package com.motorola.systemui.feedback;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.ScreenshotHelper;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.motorola.systemui.desktop.DesktopStatusBar;
import com.motorola.systemui.desktop.DesktopSystemUIFactory;
import java.util.function.Consumer;

public class FeedbackHelper {
    private static final boolean DEBUG = (!Build.IS_USER);
    public static final ComponentName FEEDBACK_TILE_SERVICE_COMPONENT = new ComponentName("com.motorola.help", "com.motorola.feedback.quicksettings.FeedBackTileService");
    /* access modifiers changed from: private */
    public static Handler sHandler = new Handler(Looper.getMainLooper());

    public static boolean isFeedbackQsActionValid(Context context) {
        if (!context.getResources().getBoolean(R$bool.config_feedback_in_tile)) {
            return false;
        }
        return isFeedbackActionValid(context, "com.motorola.help.ACTION_LAUNCH_FEEDBACK_FROM_QUICK_SETTINGS");
    }

    private static boolean isFeedbackActionValid(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(str);
        intent.setPackage("com.motorola.help");
        intent.setType("image/png");
        if (packageManager.resolveActivityAsUser(intent, 65536, getCurrentUser().id) == null) {
            Log.w("FeedbackHelper", "Intent Filter or Action not available.");
            return false;
        } else if (packageManager.checkSignatures("com.motorola.motosignature.app", "com.motorola.help") == 0) {
            return true;
        } else {
            Log.w("FeedbackHelper", "Motorola Signature not verified.");
            return false;
        }
    }

    private static UserInfo getCurrentUser() {
        try {
            return ActivityManager.getService().getCurrentUser();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static void startFeedback(Context context, String str, Uri uri, String str2) {
        Intent intent = new Intent(str);
        intent.setPackage("com.motorola.help");
        intent.addFlags(268435456);
        intent.putExtra("app_package_name", str2);
        intent.setDataAndType(uri, "image/png");
        intent.addFlags(1);
        intent.addFlags(2);
        intent.putExtra("deffered_screenshot_uri", true);
        if (DEBUG) {
            Log.d("FeedbackHelper", "startFeedback uri = " + uri);
            Log.d("FeedbackHelper", "startFeedback targetPackageName = " + str2);
        }
        try {
            context.startActivityAsUser(intent, new UserHandle(-2));
        } catch (ActivityNotFoundException unused) {
            Log.w("FeedbackHelper", "Can NOT find feedback activity");
        }
    }

    public static void sendScreenshotUri(Context context, String str, Uri uri) {
        if (DEBUG) {
            Log.d("FeedbackHelper", "sendScreenshotUri uri = " + uri);
        }
        Intent intent = new Intent(str);
        intent.setPackage("com.motorola.help");
        intent.setDataAndType(uri, "image/png");
        context.grantUriPermission("com.motorola.help", uri, 3);
        context.sendBroadcastAsUser(intent, new UserHandle(-2));
    }

    public static void triggerQsFeedBack(final Context context) {
        sHandler.post(new Runnable() {
            public void run() {
                if (DesktopFeature.isDesktopDisplayContext(context)) {
                    StatusBar statusBar = DesktopSystemUIFactory.getDesktopFactory().getSysUIComponent().getStatusBar();
                    if (statusBar != null && (statusBar instanceof DesktopStatusBar)) {
                        ((DesktopStatusBar) statusBar).requestHidePanel();
                    }
                } else {
                    ShadeController shadeController = (ShadeController) Dependency.get(ShadeController.class);
                    if (shadeController != null) {
                        shadeController.collapsePanel(false);
                    }
                }
                new ScreenshotHelper(context).takeScreenshot(1, true, true, FeedbackHelper.sHandler, (Consumer) null, 1, context.getDisplayId());
            }
        });
    }
}
