package com.motorola.gesturetouch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import com.android.systemui.Dependency;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.StatusBar;

class GestureActionController {
    private static GestureActionController sInstance;
    private final String EDGETOUCH_CLASS = "com.motorola.motoedgeassistant.GestureActionService";
    private final String LAUNCH_APPTRAY_TIMES = "launch_apptray_times";
    private final String LAUNCH_NOTIFICATION_PANEL_TIMES = "launch_notification_panel_times";
    private final String LAUNCH_QUICK_SETTINGS_TIMES = "launch_quick_settings_times";
    private final String LAUNCH_RECENT_TIMES = "launch_recent_times";
    private final String SWIPE_DOWN_ON_HOME_TIMES = "swipe_down_on_home_times";
    private final String SWIPE_DOWN_ON_OTHER_TIMES = "swipe_down_on_other_times";
    private final String SWIPE_UP_ON_HOME_TIMES = "swipe_up_on_home_times";
    private final String SWIPE_UP_ON_OTHER_TIMES = "swipe_up_on_other_times";
    private ArrowHintController mArrowHintController;
    private Context mContext;
    private GestureTouchSettingsManager mEdgeTouchSettingsManager;
    private GestureTouchController mGestureTouchController;
    private boolean mInTutorial;
    private NotificationPanelViewController mNotificationPanelViewController;
    private OverviewProxyService mOverviewProxyService;

    public static GestureActionController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GestureActionController(context);
        }
        return sInstance;
    }

    public GestureActionController(Context context) {
        this.mContext = context;
        this.mOverviewProxyService = (OverviewProxyService) Dependency.get(OverviewProxyService.class);
    }

    public void setSettingsManager(GestureTouchSettingsManager gestureTouchSettingsManager) {
        this.mEdgeTouchSettingsManager = gestureTouchSettingsManager;
    }

    public void setNotificaitonPanelView(NotificationPanelViewController notificationPanelViewController) {
        this.mNotificationPanelViewController = notificationPanelViewController;
    }

    public void setGestureTouchController(GestureTouchController gestureTouchController, boolean z) {
        this.mGestureTouchController = gestureTouchController;
        this.mInTutorial = gestureTouchController.isInTutorialMode();
        if (!z) {
            this.mArrowHintController = new ArrowHintController(this.mContext, this.mGestureTouchController, this);
        }
    }

    public void excuteAction(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        if (this.mInTutorial) {
                            handleTutorialAction(3);
                            return;
                        }
                        ArrowHintController arrowHintController = this.mArrowHintController;
                        if (arrowHintController != null) {
                            arrowHintController.showArrowHint(false);
                        } else {
                            handleSwipeDownAction();
                        }
                    }
                } else if (this.mInTutorial) {
                    handleTutorialAction(2);
                } else {
                    ArrowHintController arrowHintController2 = this.mArrowHintController;
                    if (arrowHintController2 != null) {
                        arrowHintController2.showArrowHint(true);
                    } else {
                        handleSwipeUpAction();
                    }
                }
            } else if (this.mInTutorial) {
                handleTutorialAction(1);
            } else {
                handleDoubleTapeAction();
            }
        } else if (this.mInTutorial) {
            handleTutorialAction(4);
        } else {
            handleShowWhatNewUI(4);
        }
    }

    public void arrowHintViewCallback(boolean z) {
        if (z) {
            if (isInHomeState()) {
                this.mEdgeTouchSettingsManager.writeCheckInData("swipe_up_on_home_times");
            } else {
                this.mEdgeTouchSettingsManager.writeCheckInData("swipe_up_on_other_times");
            }
            handleSwipeUpAction();
            return;
        }
        if (isInHomeState()) {
            this.mEdgeTouchSettingsManager.writeCheckInData("swipe_down_on_home_times");
        } else {
            this.mEdgeTouchSettingsManager.writeCheckInData("swipe_down_on_other_times");
        }
        handleSwipeDownAction();
    }

    public void handleTutorialAction(int i) {
        Log.i("GestureTouch", "handleTutorialAction actionType = " + i);
        String touchAppPackageName = this.mEdgeTouchSettingsManager.getTouchAppPackageName();
        if (touchAppPackageName == null) {
            Log.i("GestureTouch", "appPackageName is null");
            return;
        }
        Intent intent = new Intent("com.motorola.motoedgeassistant.tutorial");
        intent.putExtra("tutorial_action_key", i);
        intent.setComponent(new ComponentName(touchAppPackageName, "com.motorola.motoedgeassistant.GestureActionService"));
        this.mContext.startForegroundService(intent);
    }

    public void handleShowWhatNewUI(int i) {
        Log.i("GestureTouch", "handleShowWhatNewUI actionType = " + i);
        String touchAppPackageName = this.mEdgeTouchSettingsManager.getTouchAppPackageName();
        if (touchAppPackageName == null) {
            Log.i("GestureTouch", "appPackageName is null");
            return;
        }
        Intent intent = new Intent("com.motorola.motoedgeassistant.gesture");
        intent.putExtra("gesture_action_key", i);
        intent.setComponent(new ComponentName(touchAppPackageName, "com.motorola.motoedgeassistant.GestureActionService"));
        this.mContext.startForegroundService(intent);
    }

    private void handleDoubleTapeAction() {
        Log.i("GestureTouch", "handleDoubleTapeAction");
        String touchAppPackageName = this.mEdgeTouchSettingsManager.getTouchAppPackageName();
        if (touchAppPackageName == null) {
            Log.i("GestureTouch", "appPackageName is null");
            return;
        }
        Intent intent = new Intent("com.motorola.motoedgeassistant.gesture");
        intent.putExtra("gesture_action_key", 1);
        intent.setComponent(new ComponentName(touchAppPackageName, "com.motorola.motoedgeassistant.GestureActionService"));
        this.mContext.startForegroundService(intent);
    }

    private void handleSwipeDownAction() {
        if (this.mGestureTouchController.isNeedShowWhatNew()) {
            handleShowWhatNewUI(3);
            return;
        }
        Log.i("GestureTouch", "handleSwipeDownAction");
        if (this.mNotificationPanelViewController.isFullyCollapsed()) {
            this.mEdgeTouchSettingsManager.writeCheckInData("launch_notification_panel_times");
        } else {
            this.mEdgeTouchSettingsManager.writeCheckInData("launch_quick_settings_times");
        }
        ((StatusBar) Dependency.get(StatusBar.class)).handleSystemNavigationDown();
    }

    private void handleSwipeUpAction() {
        if (this.mGestureTouchController.isNeedShowWhatNew()) {
            handleShowWhatNewUI(2);
            return;
        }
        Log.i("GestureTouch", "handleSwipeUpAction");
        if (!this.mNotificationPanelViewController.isFullyCollapsed()) {
            this.mNotificationPanelViewController.collapse(false, 1.0f);
            return;
        }
        String launcherState = getLauncherState();
        if (launcherState == null) {
            Log.w("GestureTouch", "get luancher state failed.");
            return;
        }
        Log.d("GestureTouch", "handleSwipeUpAction currentState = " + launcherState);
        int swipeUpTargetAction = getSwipeUpTargetAction(launcherState);
        if (swipeUpTargetAction == 1) {
            this.mEdgeTouchSettingsManager.writeCheckInData("launch_recent_times");
            showOverviewScreen();
        } else if (swipeUpTargetAction == 2) {
            this.mEdgeTouchSettingsManager.writeCheckInData("launch_apptray_times");
            showAppTray();
        } else if (swipeUpTargetAction == 3) {
            goHome();
        }
    }

    private boolean isInHomeState() {
        String launcherState = getLauncherState();
        Log.i("GestureTouch", "isInHomeScreen currentState = " + launcherState);
        return "Normal".equals(launcherState);
    }

    public void showAppTray() {
        Log.i("GestureTouch", "LAUNCHER_ACTION_APPTRAY");
        this.mContext.getContentResolver().call(Uri.parse("content://com.motorola.launcher3.settings/"), "do_launcher_action", "toggle_app_tray", (Bundle) null);
    }

    public void goHome() {
        sendEvent(0, 3);
        sendEvent(1, 3);
    }

    private void sendEvent(int i, int i2) {
        long uptimeMillis = SystemClock.uptimeMillis();
        InputManager.getInstance().injectInputEvent(new KeyEvent(uptimeMillis, uptimeMillis, i, i2, 0, 4096, -1, 0, 72, 257), 0);
    }

    public void showOverviewScreen() {
        OverviewProxyService overviewProxyService = this.mOverviewProxyService;
        if (overviewProxyService == null) {
            Log.w("GestureTouch", "showOverviewScreen failed mOverviewProxyService is NULL.");
            return;
        }
        try {
            if (overviewProxyService.getProxy() != null) {
                this.mOverviewProxyService.getProxy().onOverviewToggle();
            }
        } catch (RemoteException e) {
            Log.e("GestureTouch", "Cannot send toggle recents through proxy service.", e);
        }
    }

    private String getLauncherState() {
        Bundle bundle;
        try {
            bundle = this.mContext.getContentResolver().call(Uri.parse("content://com.motorola.launcher3.settings/"), "get_launcher_state", (String) null, (Bundle) null);
        } catch (IllegalArgumentException e) {
            Log.w("GestureTouch", "queryLauncherState failed: " + e.getMessage());
            bundle = null;
        }
        if (bundle == null) {
            return null;
        }
        return bundle.getString("launcher_state", (String) null);
    }

    private int getSwipeUpTargetAction(String str) {
        if (str.equals("Normal") || str.equals("Paused")) {
            return 1;
        }
        if (str.equals("Overview")) {
            return 2;
        }
        return str.equals("AllApps") ? 3 : -1;
    }

    public void updateTutorialMode(boolean z) {
        this.mInTutorial = z;
    }

    public void onUserSwitch(Context context) {
        this.mContext = context;
    }
}
