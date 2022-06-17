package com.android.systemui.screenshot;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.ScreenshotHelper;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.shared.recents.utilities.BitmapUtil;
import com.motorola.systemui.screenshot.MotoTakeScreenshotService;

public class TakeScreenshotService extends Service {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static final String TAG = LogConfig.logTag(TakeScreenshotService.class);
    private final BroadcastReceiver mCloseSystemDialogs = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && TakeScreenshotService.this.mScreenshot != null && !TakeScreenshotService.this.mScreenshot.isPendingSharedTransition()) {
                TakeScreenshotService.this.mScreenshot.dismissScreenshot(false);
            }
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper(), new TakeScreenshotService$$ExternalSyntheticLambda0(this));
    private final ScreenshotNotificationsController mNotificationsController;
    /* access modifiers changed from: private */
    public ScreenshotController mScreenshot;
    private boolean mSupportCLI = false;
    private final UiEventLogger mUiEventLogger;
    private final UserManager mUserManager;

    interface RequestCallback {
        void onFinish();

        void reportError();
    }

    public TakeScreenshotService(ScreenshotController screenshotController, UserManager userManager, UiEventLogger uiEventLogger, ScreenshotNotificationsController screenshotNotificationsController) {
        this.mScreenshot = screenshotController;
        this.mUserManager = userManager;
        this.mUiEventLogger = uiEventLogger;
        this.mNotificationsController = screenshotNotificationsController;
    }

    public void onCreate() {
        this.mSupportCLI = MotoFeature.getInstance(this).isSupportCli();
    }

    public IBinder onBind(Intent intent) {
        registerReceiver(this.mCloseSystemDialogs, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        return new Messenger(this.mHandler).getBinder();
    }

    public boolean onUnbind(Intent intent) {
        ScreenshotController screenshotController = this.mScreenshot;
        if (screenshotController != null) {
            screenshotController.removeWindow();
            this.mScreenshot = null;
        }
        unregisterReceiver(this.mCloseSystemDialogs);
        return false;
    }

    public void onDestroy() {
        super.onDestroy();
        ScreenshotController screenshotController = this.mScreenshot;
        if (screenshotController != null) {
            screenshotController.removeWindow();
            this.mScreenshot.releaseContext();
            this.mScreenshot = null;
        }
    }

    static class RequestCallbackImpl implements RequestCallback {
        private final Messenger mReplyTo;

        RequestCallbackImpl(Messenger messenger) {
            this.mReplyTo = messenger;
        }

        public void reportError() {
            TakeScreenshotService.reportUri(this.mReplyTo, (Uri) null);
            TakeScreenshotService.sendComplete(this.mReplyTo);
        }

        public void onFinish() {
            TakeScreenshotService.sendComplete(this.mReplyTo);
        }
    }

    /* access modifiers changed from: private */
    public boolean handleMessage(Message message) {
        String str;
        Messenger messenger = message.replyTo;
        TakeScreenshotService$$ExternalSyntheticLambda1 takeScreenshotService$$ExternalSyntheticLambda1 = new TakeScreenshotService$$ExternalSyntheticLambda1(messenger);
        RequestCallbackImpl requestCallbackImpl = new RequestCallbackImpl(messenger);
        if (!this.mUserManager.isUserUnlocked()) {
            Log.w(TAG, "Skipping screenshot because storage is locked!");
            this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_save_user_locked_text);
            requestCallbackImpl.reportError();
            return true;
        }
        Object obj = message.obj;
        if (obj == null || !(obj instanceof ScreenshotHelper.ScreenshotRequest)) {
            Log.w(TAG, "Skipping screenshot because invalid request!");
            requestCallbackImpl.reportError();
            return true;
        }
        ScreenshotHelper.ScreenshotRequest screenshotRequest = (ScreenshotHelper.ScreenshotRequest) obj;
        this.mUiEventLogger.log(ScreenshotEvent.getScreenshotSource(screenshotRequest.getSource()));
        int extraType = screenshotRequest.getExtraType();
        if (message.what == 1 && !ScreenshotHelper.isSilentType(extraType)) {
            notifyScreenshotTaken(message.getData());
        }
        int extraDisplayId = screenshotRequest.getExtraDisplayId();
        if (this.mSupportCLI && MotoFeature.getInstance(this).isLidClosed()) {
            extraDisplayId = 1;
        }
        if (shouldStartMotoScreenshot(message)) {
            if (DEBUG) {
                Log.d(TAG, "start moto screen shot");
            }
            Intent intent = new Intent(this, MotoTakeScreenshotService.class);
            if (message.what == 3) {
                intent.putExtra("requestIndex", MotoTakeScreenshotService.pushScreenshotRequest(screenshotRequest));
                str = "take_screenshot_provided_image";
            } else {
                str = "take_screenshot_fullscreen";
            }
            intent.setAction(str);
            intent.putExtra("displayId", extraDisplayId);
            intent.putExtra("extraType", extraType);
            startService(intent);
            requestCallbackImpl.reportError();
            return true;
        }
        int i = message.what;
        if (i == 1) {
            this.mScreenshot.takeScreenshotFullscreen(takeScreenshotService$$ExternalSyntheticLambda1, requestCallbackImpl);
        } else if (i == 2) {
            this.mScreenshot.takeScreenshotPartial(takeScreenshotService$$ExternalSyntheticLambda1, requestCallbackImpl);
        } else if (i != 3) {
            Log.w(TAG, "Invalid screenshot option: " + message.what);
            return false;
        } else {
            Bitmap bundleToHardwareBitmap = BitmapUtil.bundleToHardwareBitmap(screenshotRequest.getBitmapBundle());
            Rect boundsInScreen = screenshotRequest.getBoundsInScreen();
            Insets insets = screenshotRequest.getInsets();
            int taskId = screenshotRequest.getTaskId();
            int userId = screenshotRequest.getUserId();
            ComponentName topComponent = screenshotRequest.getTopComponent();
            if (bundleToHardwareBitmap == null) {
                Log.e(TAG, "Got null bitmap from screenshot message");
                this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
                requestCallbackImpl.reportError();
            } else {
                this.mScreenshot.handleImageAsScreenshot(bundleToHardwareBitmap, boundsInScreen, insets, taskId, userId, topComponent, takeScreenshotService$$ExternalSyntheticLambda1, requestCallbackImpl);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static void sendComplete(Messenger messenger) {
        try {
            messenger.send(Message.obtain((Handler) null, 2));
        } catch (RemoteException e) {
            Log.d(TAG, "ignored remote exception", e);
        }
    }

    /* access modifiers changed from: private */
    public static void reportUri(Messenger messenger, Uri uri) {
        try {
            messenger.send(Message.obtain((Handler) null, 1, uri));
        } catch (RemoteException e) {
            Log.d(TAG, "ignored remote exception", e);
        }
    }

    private boolean shouldStartMotoScreenshot(Message message) {
        int i = message.what;
        if (i != 1 && i != 3) {
            return false;
        }
        if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) != 0) {
            return true;
        }
        return false;
    }

    private void notifyScreenshotTaken(Bundle bundle) {
        Intent intent = new Intent();
        try {
            intent.setPackage("com.motorola.actions");
            intent.setAction("com.motorola.actions.quickScreenshot.ACTION_SHOW_FDN");
            intent.addFlags(268435456);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            sendBroadcast(intent, "com.motorola.permission.TAKE_SCREENSHOT");
        } catch (Exception e) {
            String str = TAG;
            Log.e(str, "Error to notify Moto Actions, e= " + e);
        }
    }
}
