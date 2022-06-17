package com.android.systemui.screenshot;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.RemoteAnimationAdapter;
import android.view.WindowManagerGlobal;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.Optional;

public class ActionProxyReceiver extends BroadcastReceiver {
    private final ActivityManagerWrapper mActivityManagerWrapper;
    private final ScreenshotSmartActions mScreenshotSmartActions;
    private final StatusBar mStatusBar;

    public ActionProxyReceiver(Optional<StatusBar> optional, ActivityManagerWrapper activityManagerWrapper, ScreenshotSmartActions screenshotSmartActions) {
        this.mStatusBar = optional.orElse((Object) null);
        this.mActivityManagerWrapper = activityManagerWrapper;
        this.mScreenshotSmartActions = screenshotSmartActions;
    }

    public void onReceive(Context context, Intent intent) {
        ActionProxyReceiver$$ExternalSyntheticLambda0 actionProxyReceiver$$ExternalSyntheticLambda0 = new ActionProxyReceiver$$ExternalSyntheticLambda0(this, intent, context);
        StatusBar statusBar = this.mStatusBar;
        if (statusBar != null) {
            statusBar.executeRunnableDismissingKeyguard(actionProxyReceiver$$ExternalSyntheticLambda0, (Runnable) null, true, true, true);
        } else {
            actionProxyReceiver$$ExternalSyntheticLambda0.run();
        }
        if (intent.getBooleanExtra("android:smart_actions_enabled", false)) {
            this.mScreenshotSmartActions.notifyScreenshotAction(context, intent.getStringExtra("android:screenshot_id"), "android.intent.action.EDIT".equals(intent.getAction()) ? "Edit" : "Share", false, (Intent) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$0(Intent intent, Context context) {
        this.mActivityManagerWrapper.closeSystemWindows("screenshot");
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("android:screenshot_action_intent");
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setDisallowEnterPictureInPictureWhileLaunching(intent.getBooleanExtra("android:screenshot_disallow_enter_pip", false));
        try {
            pendingIntent.send(context, 0, (Intent) null, (PendingIntent.OnFinished) null, (Handler) null, (String) null, makeBasic.toBundle());
            if (intent.getBooleanExtra("android:screenshot_override_transition", false)) {
                try {
                    WindowManagerGlobal.getWindowManagerService().overridePendingAppTransitionRemote(new RemoteAnimationAdapter(ScreenshotController.SCREENSHOT_REMOTE_RUNNER, 0, 0), 0);
                } catch (Exception e) {
                    Log.e("ActionProxyReceiver", "Error overriding screenshot app transition", e);
                }
            }
        } catch (PendingIntent.CanceledException e2) {
            Log.e("ActionProxyReceiver", "Pending intent canceled", e2);
        }
    }
}
