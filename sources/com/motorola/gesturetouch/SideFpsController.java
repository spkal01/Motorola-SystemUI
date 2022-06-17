package com.motorola.gesturetouch;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.SystemVibrator;
import android.os.VibrationEffect;
import android.util.Log;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.motorola.gesturetouch.GestureTouchSettingsManager;

public class SideFpsController extends GestureTouchController implements GestureTouchSettingsManager.GestureSettingsListener {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private Context mContext;
    private boolean mInTutorialMode;
    private boolean mIsNeedShowWhatNew;
    NotificationPanelViewController mNotificationPanelViewController;
    private boolean mPowerTouchEnabled = false;
    private GestureActionController mSideFpsActionController;
    private GestureTouchSettingsManager mSideFpsSettingsManager;
    private boolean mSideGestureEnabled;
    private boolean mSideSwipeUpDownEnabled;
    private boolean mSupportSideFpsUpdown = false;
    private boolean mSystemNavigationKeysEnabled;
    private boolean mVibrationEnabled;
    private SystemVibrator mVibrator;

    public void dateChanged() {
    }

    public void updatePositionModeState(boolean z) {
    }

    public void updatePositionX(int i) {
    }

    public void updatePositionY(int i) {
    }

    public void updateShortcutState(boolean z) {
    }

    public SideFpsController(Context context, NotificationPanelViewController notificationPanelViewController) {
        this.mContext = context;
        this.mNotificationPanelViewController = notificationPanelViewController;
        this.mSupportSideFpsUpdown = context.getResources().getBoolean(17891839);
        GestureTouchSettingsManager instance = GestureTouchSettingsManager.getInstance(this.mContext);
        this.mSideFpsSettingsManager = instance;
        instance.addSettingsListener(this);
        initSettingState();
        GestureActionController instance2 = GestureActionController.getInstance(this.mContext);
        this.mSideFpsActionController = instance2;
        instance2.setNotificaitonPanelView(this.mNotificationPanelViewController);
        this.mSideFpsActionController.setGestureTouchController(this, true);
        this.mSideFpsActionController.setSettingsManager(this.mSideFpsSettingsManager);
    }

    public void initSettingState() {
        this.mVibrator = (SystemVibrator) this.mContext.getSystemService("vibrator");
        this.mSideGestureEnabled = this.mSideFpsSettingsManager.isGestureEnabled();
        this.mSideSwipeUpDownEnabled = this.mSupportSideFpsUpdown && this.mSideFpsSettingsManager.isSwipeUpDownEnabled();
        this.mInTutorialMode = this.mSideFpsSettingsManager.isInTutorialMode();
        this.mVibrationEnabled = this.mSideFpsSettingsManager.isVibrationEnabled();
        this.mSystemNavigationKeysEnabled = this.mSideFpsSettingsManager.isSystemNavigationKeysEnabled();
        this.mPowerTouchEnabled = this.mSideFpsSettingsManager.isAppAvaliable();
        if (DEBUG) {
            Log.i("GestureTouch", "mSideGestureEnabled = " + this.mSideGestureEnabled + " mSupportSideFpsUpdown = " + this.mSupportSideFpsUpdown + " mSideSwipeUpDownEnabled = " + this.mSideSwipeUpDownEnabled + " mInTutorialMode = " + this.mInTutorialMode + " mSystemNavigationKeysEnabled = " + this.mSystemNavigationKeysEnabled + " mPowerTouchEnabled = " + this.mPowerTouchEnabled);
        }
    }

    public void updateGestureTouchEnabled(boolean z) {
        this.mSideGestureEnabled = z;
    }

    public void updateSwipeEnabled(boolean z) {
        this.mSideSwipeUpDownEnabled = this.mSupportSideFpsUpdown && z;
    }

    public void updateVibrationEnabled(boolean z) {
        this.mVibrationEnabled = z;
    }

    public void updateTutorialMode(boolean z) {
        this.mInTutorialMode = z;
        this.mSideFpsActionController.updateTutorialMode(z);
    }

    public void updateWhatNewState(boolean z, boolean z2) {
        this.mIsNeedShowWhatNew = z;
    }

    public void updateAppState(boolean z) {
        this.mPowerTouchEnabled = z;
    }

    public void onUserSwitch(Context context) {
        if (DEBUG) {
            Log.i("GestureTouch", "onUserSwitch");
        }
        this.mContext = context;
        this.mSideFpsActionController.onUserSwitch(context);
        initSettingState();
    }

    public void updateSNGState(boolean z) {
        if (DEBUG) {
            Log.i("GestureTouch", "updateSNGState isSNGEnabled");
        }
        this.mSystemNavigationKeysEnabled = z;
    }

    public boolean isSystemGestureEnabled() {
        return this.mSystemNavigationKeysEnabled;
    }

    public boolean isSideGestureEnabled() {
        return this.mSideGestureEnabled && this.mPowerTouchEnabled;
    }

    public boolean isTutorialMode() {
        return this.mInTutorialMode;
    }

    public boolean isNeedShowWhatNew() {
        return this.mIsNeedShowWhatNew;
    }

    public boolean handleSideFpsGesture(int i) {
        if (DEBUG) {
            Log.d("GestureTouch", "handleSideFpsGesture keyCode = " + i);
        }
        switch (i) {
            case 280:
                if (!this.mSideSwipeUpDownEnabled) {
                    return false;
                }
                handleVibrationFeedback();
                this.mSideFpsActionController.excuteAction(3);
                return true;
            case 281:
                if (!this.mSideSwipeUpDownEnabled) {
                    return false;
                }
                handleVibrationFeedback();
                this.mSideFpsActionController.excuteAction(4);
                return true;
            case 282:
                handleVibrationFeedback();
                this.mSideFpsActionController.excuteAction(2);
                return true;
            default:
                return false;
        }
    }

    private void handleVibrationFeedback() {
        SystemVibrator systemVibrator;
        if (this.mVibrationEnabled && (systemVibrator = this.mVibrator) != null) {
            systemVibrator.vibrate(VibrationEffect.get(5), VIBRATION_ATTRIBUTES);
        }
    }
}
