package com.motorola.gesturetouch;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IProcessObserver;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import java.util.ArrayList;
import java.util.List;

public class GestureTouchSettingsManager extends BroadcastReceiver {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private static GestureTouchSettingsManager sInstance;
    private final String CHANGE_PILL_POSITION_MODE;
    private final String EDGEPILL_X_POSITION;
    private final String EDGEPILL_Y_POSITION;
    private final String EDGETOUCH_PACKAGE;
    private final String FPSTOUCH_PACKAGE;
    private final String GESTURE_VIBRATION_ENABLE;
    private final String IN_TUTORIAL_MODE;
    private final String SHORTCUTS_PANEL_STATUS_EXPANDED;
    private final String SHOW_WHATNEW;
    private final String SWIP_UP_DOWN_ENABLE;
    private final String TOUCH_GESTURE_ENABLE;
    private final int WHAT_NEW_ANIMAT_STATE;
    private final int WHAT_NEW_CLOSE_STATE;
    private final int WHAT_NEW_NORMAL_STATE;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mCurrentUser;
    private boolean mEdgeTouchAppEnabled;
    /* access modifiers changed from: private */
    public boolean mEdgeTouchShortcutState;
    private boolean mFpsTouchAppEnabled;
    /* access modifiers changed from: private */
    public boolean mGestureEnabled;
    private final Handler mHandler;
    private IActivityManager mIam;
    /* access modifiers changed from: private */
    public boolean mInTutorialMode;
    /* access modifiers changed from: private */
    public boolean mIsNeedShowWhatNew;
    /* access modifiers changed from: private */
    public boolean mIsNeedShowWhatNewAnimator;
    /* access modifiers changed from: private */
    public boolean mIsPositionMode;
    /* access modifiers changed from: private */
    public final Handler mMainHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public int mPidOfEdgeTouch;
    private ContentObserver mPillGestureObserver;
    private ContentObserver mPillSwipeUpDownObserver;
    private ContentObserver mPillTutorialModeObserver;
    private ContentObserver mPillVibrationObserver;
    /* access modifiers changed from: private */
    public int mPillXPosition = 1;
    /* access modifiers changed from: private */
    public int mPillYPosition;
    private ContentObserver mPositionModeObserver;
    private ContentObserver mPositionXObserver;
    private ContentObserver mPositionYObserver;
    private ProcessObserver mProcessObserver;
    private ContentObserver mSNGestureObserver;
    /* access modifiers changed from: private */
    public List<GestureSettingsListener> mSettingsListener;
    private ContentObserver mShortcutStateObserver;
    private boolean mSupportSystemNavigationKeys;
    /* access modifiers changed from: private */
    public boolean mSwipeUpDownEnabled;
    /* access modifiers changed from: private */
    public boolean mSystemNavigationKeysEnabled;
    /* access modifiers changed from: private */
    public boolean mVibrationEnabled;
    private ContentObserver mWhatNewObserver;

    public interface GestureSettingsListener {
        void dateChanged();

        void onUserSwitch(Context context);

        void updateAppState(boolean z);

        void updateGestureTouchEnabled(boolean z);

        void updatePositionModeState(boolean z);

        void updatePositionX(int i);

        void updatePositionY(int i);

        void updateSNGState(boolean z);

        void updateShortcutState(boolean z);

        void updateSwipeEnabled(boolean z);

        void updateTutorialMode(boolean z);

        void updateVibrationEnabled(boolean z);

        void updateWhatNewState(boolean z, boolean z2);
    }

    public static GestureTouchSettingsManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GestureTouchSettingsManager(context);
        }
        return sInstance;
    }

    public GestureTouchSettingsManager(Context context) {
        boolean z = true;
        Handler handler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
        this.mHandler = handler;
        this.TOUCH_GESTURE_ENABLE = "touch_gesture_enable";
        this.SWIP_UP_DOWN_ENABLE = "swipe_up_down_enable";
        this.GESTURE_VIBRATION_ENABLE = "gesture_vibration_enable";
        this.CHANGE_PILL_POSITION_MODE = "change_pill_position_mode";
        this.EDGEPILL_X_POSITION = "edgepill_x_position";
        this.EDGEPILL_Y_POSITION = "edgepill_y_position";
        this.IN_TUTORIAL_MODE = "in_tutorial_mode";
        this.SHORTCUTS_PANEL_STATUS_EXPANDED = "shortcuts_panel_status_expanded";
        this.SHOW_WHATNEW = "show_whatsnew";
        this.EDGETOUCH_PACKAGE = "com.motorola.motoedgeassistant";
        this.FPSTOUCH_PACKAGE = "com.motorola.motofpstouch";
        this.mCurrentUser = 0;
        this.WHAT_NEW_CLOSE_STATE = 0;
        this.WHAT_NEW_NORMAL_STATE = 1;
        this.WHAT_NEW_ANIMAT_STATE = 2;
        this.mPillGestureObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean access$100 = GestureTouchSettingsManager.this.isFeatureEnabled("touch_gesture_enable");
                if (access$100 != GestureTouchSettingsManager.this.mGestureEnabled) {
                    boolean unused = GestureTouchSettingsManager.this.mGestureEnabled = access$100;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$1$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateGestureTouchEnabled : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateGestureTouchEnabled.updateGestureTouchEnabled(GestureTouchSettingsManager.this.mGestureEnabled);
                }
            }
        };
        this.mPillSwipeUpDownObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean access$100 = GestureTouchSettingsManager.this.isFeatureEnabled("swipe_up_down_enable");
                if (access$100 != GestureTouchSettingsManager.this.mSwipeUpDownEnabled) {
                    boolean unused = GestureTouchSettingsManager.this.mSwipeUpDownEnabled = access$100;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$2$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateSwipeEnabled : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateSwipeEnabled.updateSwipeEnabled(GestureTouchSettingsManager.this.mSwipeUpDownEnabled);
                }
            }
        };
        this.mPillVibrationObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean access$100 = GestureTouchSettingsManager.this.isFeatureEnabled("gesture_vibration_enable");
                if (access$100 != GestureTouchSettingsManager.this.mVibrationEnabled) {
                    boolean unused = GestureTouchSettingsManager.this.mVibrationEnabled = access$100;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$3$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateVibrationEnabled : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateVibrationEnabled.updateVibrationEnabled(GestureTouchSettingsManager.this.mVibrationEnabled);
                }
            }
        };
        this.mPillTutorialModeObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean access$100 = GestureTouchSettingsManager.this.isFeatureEnabled("in_tutorial_mode");
                if (access$100) {
                    GestureTouchSettingsManager.this.updatePidOfEdgeTouch();
                    GestureTouchSettingsManager.this.registerProcessObserver();
                } else {
                    GestureTouchSettingsManager.this.unregisterPorcessObserver();
                }
                if (access$100 != GestureTouchSettingsManager.this.mInTutorialMode) {
                    boolean unused = GestureTouchSettingsManager.this.mInTutorialMode = access$100;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$4$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateTutorialMode : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateTutorialMode.updateTutorialMode(GestureTouchSettingsManager.this.mInTutorialMode);
                }
            }
        };
        this.mShortcutStateObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean access$100 = GestureTouchSettingsManager.this.isFeatureEnabled("shortcuts_panel_status_expanded");
                if (access$100 != GestureTouchSettingsManager.this.mEdgeTouchShortcutState) {
                    boolean unused = GestureTouchSettingsManager.this.mEdgeTouchShortcutState = access$100;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$5$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateShortcutState : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateShortcutState.updateShortcutState(GestureTouchSettingsManager.this.mEdgeTouchShortcutState);
                }
            }
        };
        this.mWhatNewObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                int access$1200 = GestureTouchSettingsManager.this.getWhatNewMode();
                boolean z2 = true;
                boolean z3 = access$1200 != 0;
                if (access$1200 != 2) {
                    z2 = false;
                }
                if (z3 != GestureTouchSettingsManager.this.mIsNeedShowWhatNew || z2 != GestureTouchSettingsManager.this.mIsNeedShowWhatNewAnimator) {
                    boolean unused = GestureTouchSettingsManager.this.mIsNeedShowWhatNew = z3;
                    boolean unused2 = GestureTouchSettingsManager.this.mIsNeedShowWhatNewAnimator = z2;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$6$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateWhatNewState : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateWhatNewState.updateWhatNewState(GestureTouchSettingsManager.this.mIsNeedShowWhatNew, GestureTouchSettingsManager.this.mIsNeedShowWhatNewAnimator);
                }
            }
        };
        this.mPositionModeObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean access$100 = GestureTouchSettingsManager.this.isFeatureEnabled("change_pill_position_mode");
                if (access$100) {
                    GestureTouchSettingsManager.this.updatePidOfEdgeTouch();
                    GestureTouchSettingsManager.this.registerProcessObserver();
                } else {
                    GestureTouchSettingsManager.this.unregisterPorcessObserver();
                }
                if (access$100 != GestureTouchSettingsManager.this.mIsPositionMode) {
                    boolean unused = GestureTouchSettingsManager.this.mIsPositionMode = access$100;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$7$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updatePositionModeState : GestureTouchSettingsManager.this.mSettingsListener) {
                    updatePositionModeState.updatePositionModeState(GestureTouchSettingsManager.this.mIsPositionMode);
                }
            }
        };
        this.mPositionXObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                int access$1600 = GestureTouchSettingsManager.this.getPositionData(true);
                if (GestureTouchSettingsManager.DEBUG) {
                    Log.i("GestureTouch", "mPositionXObserver mPillXPosition = " + access$1600);
                }
                if (access$1600 != GestureTouchSettingsManager.this.mPillXPosition) {
                    int unused = GestureTouchSettingsManager.this.mPillXPosition = access$1600;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$8$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updatePositionX : GestureTouchSettingsManager.this.mSettingsListener) {
                    updatePositionX.updatePositionX(GestureTouchSettingsManager.this.mPillXPosition);
                }
            }
        };
        this.mPositionYObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                int access$1600 = GestureTouchSettingsManager.this.getPositionData(false);
                if (GestureTouchSettingsManager.DEBUG) {
                    Log.i("GestureTouch", "mPositionYObserver mPillYPosition = " + access$1600);
                }
                if (access$1600 != GestureTouchSettingsManager.this.mPillYPosition) {
                    int unused = GestureTouchSettingsManager.this.mPillYPosition = access$1600;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$9$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updatePositionY : GestureTouchSettingsManager.this.mSettingsListener) {
                    updatePositionY.updatePositionY(GestureTouchSettingsManager.this.mPillYPosition);
                }
            }
        };
        this.mSNGestureObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                boolean z2 = false;
                if (Settings.Secure.getIntForUser(GestureTouchSettingsManager.this.mContext.getContentResolver(), "system_navigation_keys_enabled", 0, -2) == 1) {
                    z2 = true;
                }
                if (z2 != GestureTouchSettingsManager.this.mSystemNavigationKeysEnabled) {
                    boolean unused = GestureTouchSettingsManager.this.mSystemNavigationKeysEnabled = z2;
                    GestureTouchSettingsManager.this.mMainHandler.post(new GestureTouchSettingsManager$10$$ExternalSyntheticLambda0(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onChange$0() {
                for (GestureSettingsListener updateSNGState : GestureTouchSettingsManager.this.mSettingsListener) {
                    updateSNGState.updateSNGState(GestureTouchSettingsManager.this.mSystemNavigationKeysEnabled);
                }
            }
        };
        this.mContext = context;
        this.mSettingsListener = new ArrayList();
        registerSettingObserver();
        this.mEdgeTouchAppEnabled = isPackageAvailable("com.motorola.motoedgeassistant");
        this.mFpsTouchAppEnabled = isPackageAvailable("com.motorola.motofpstouch");
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart("com.motorola.motoedgeassistant", 0);
        intentFilter.addDataSchemeSpecificPart("com.motorola.motofpstouch", 0);
        this.mContext.registerReceiverForAllUsers(this, intentFilter, (String) null, (Handler) null);
        this.mContext.registerReceiverForAllUsers(this, new IntentFilter("android.intent.action.USER_SWITCHED"), (String) null, (Handler) null);
        this.mContext.registerReceiverForAllUsers(this, new IntentFilter("android.intent.action.DATE_CHANGED"), (String) null, (Handler) null);
        initSettingsValue();
        this.mIam = ActivityManager.getService();
        this.mSupportSystemNavigationKeys = this.mContext.getResources().getBoolean(17891744);
        this.mSystemNavigationKeysEnabled = (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "system_navigation_keys_enabled", 0, -2) != 1 || !this.mSupportSystemNavigationKeys) ? false : z;
    }

    public void initSettingsValue() {
        this.mHandler.post(new GestureTouchSettingsManager$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initSettingsValue$1() {
        this.mGestureEnabled = isFeatureEnabled("touch_gesture_enable");
        this.mSwipeUpDownEnabled = isFeatureEnabled("swipe_up_down_enable");
        this.mVibrationEnabled = isFeatureEnabled("gesture_vibration_enable");
        this.mInTutorialMode = isFeatureEnabled("in_tutorial_mode");
        this.mEdgeTouchShortcutState = isFeatureEnabled("shortcuts_panel_status_expanded");
        this.mIsNeedShowWhatNew = getWhatNewMode() != 0;
        this.mIsNeedShowWhatNewAnimator = getWhatNewMode() == 2;
        this.mIsPositionMode = isFeatureEnabled("change_pill_position_mode");
        this.mPillXPosition = getPositionData(true);
        this.mPillYPosition = getPositionData(false);
        this.mMainHandler.post(new GestureTouchSettingsManager$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initSettingsValue$0() {
        for (GestureSettingsListener onUserSwitch : this.mSettingsListener) {
            onUserSwitch.onUserSwitch(this.mContext);
        }
    }

    private void registerSettingObserver() {
        if (DEBUG) {
            Log.i("GestureTouch", "registerSettingObserver mCurrentUser = " + this.mCurrentUser);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "touch_gesture_enable"), true, this.mPillGestureObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "swipe_up_down_enable"), true, this.mPillSwipeUpDownObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "gesture_vibration_enable"), true, this.mPillVibrationObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "in_tutorial_mode"), true, this.mPillTutorialModeObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "shortcuts_panel_status_expanded"), true, this.mShortcutStateObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "show_whatsnew"), true, this.mWhatNewObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "change_pill_position_mode"), true, this.mPositionModeObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "edgepill_x_position"), true, this.mPositionXObserver, this.mCurrentUser);
            contentResolver.registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "edgepill_y_position"), true, this.mPositionYObserver, this.mCurrentUser);
            if (this.mSupportSystemNavigationKeys) {
                contentResolver.registerContentObserver(Settings.Secure.getUriFor("system_navigation_keys_enabled"), false, this.mSNGestureObserver, this.mCurrentUser);
            }
        } catch (Exception unused) {
            Log.i("GestureTouch", "registerSettingObserver FAILED");
        }
    }

    private void unregisterSettingObserver() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (DEBUG) {
            Log.i("GestureTouch", "unregisterSettingObserver");
        }
        try {
            contentResolver.unregisterContentObserver(this.mPillGestureObserver);
            contentResolver.unregisterContentObserver(this.mPillSwipeUpDownObserver);
            contentResolver.unregisterContentObserver(this.mPillVibrationObserver);
            contentResolver.unregisterContentObserver(this.mPillTutorialModeObserver);
            contentResolver.unregisterContentObserver(this.mShortcutStateObserver);
            contentResolver.unregisterContentObserver(this.mWhatNewObserver);
            contentResolver.unregisterContentObserver(this.mPositionModeObserver);
            contentResolver.unregisterContentObserver(this.mPositionXObserver);
            contentResolver.unregisterContentObserver(this.mPositionYObserver);
            if (this.mSupportSystemNavigationKeys) {
                contentResolver.unregisterContentObserver(this.mSNGestureObserver);
            }
        } catch (Exception unused) {
            Log.i("GestureTouch", "unRegisterSettingObserver FAILED");
        }
    }

    /* access modifiers changed from: private */
    public void registerProcessObserver() {
        if (DEBUG) {
            Log.i("GestureTouch", "registerProcessObserver");
        }
        if (this.mProcessObserver == null) {
            this.mProcessObserver = new ProcessObserver();
        }
        try {
            IActivityManager iActivityManager = this.mIam;
            if (iActivityManager != null) {
                iActivityManager.registerProcessObserver(this.mProcessObserver);
            }
        } catch (RemoteException e) {
            Log.e("GestureTouch", "Can't register ProcessObserver :" + e);
            this.mProcessObserver = null;
        }
    }

    /* access modifiers changed from: private */
    public void unregisterPorcessObserver() {
        IActivityManager iActivityManager;
        if (DEBUG) {
            Log.i("GestureTouch", "unregisterPorcessObserver");
        }
        ProcessObserver processObserver = this.mProcessObserver;
        if (processObserver != null && (iActivityManager = this.mIam) != null) {
            try {
                iActivityManager.unregisterProcessObserver(processObserver);
            } catch (RemoteException e) {
                Log.e("GestureTouch", "Can't unregister ProcessObserver :" + e);
            }
            this.mProcessObserver = null;
        }
    }

    public boolean isAppAvaliable() {
        return this.mFpsTouchAppEnabled | this.mEdgeTouchAppEnabled;
    }

    public boolean isGestureEnabled() {
        return this.mGestureEnabled;
    }

    public boolean isSwipeUpDownEnabled() {
        return this.mSwipeUpDownEnabled;
    }

    public boolean isVibrationEnabled() {
        return this.mVibrationEnabled;
    }

    public boolean isInTutorialMode() {
        return this.mInTutorialMode;
    }

    public boolean isNeedHidePill() {
        return this.mEdgeTouchShortcutState;
    }

    public boolean isNeedShowWhatNew() {
        return this.mIsNeedShowWhatNew;
    }

    public boolean isNeedShowWhatNewAnimator() {
        return this.mIsNeedShowWhatNewAnimator;
    }

    public boolean isPositionMode() {
        return this.mIsPositionMode;
    }

    public int getPillXPosition() {
        return this.mPillXPosition;
    }

    public int getPillYPosition() {
        return this.mPillYPosition;
    }

    public boolean isSystemNavigationKeysEnabled() {
        return this.mSystemNavigationKeysEnabled;
    }

    /* access modifiers changed from: private */
    public boolean isFeatureEnabled(String str) {
        boolean z;
        try {
            Cursor query = this.mContext.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), str), (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null) {
                if (query.moveToNext()) {
                    z = true;
                    if (query.getInt(query.getColumnIndex(str)) == 1) {
                        query.close();
                    }
                }
                z = false;
                query.close();
            } else {
                z = false;
            }
            if (DEBUG) {
                Log.i("GestureTouch", "FeatureEnabled Key: " + str + " isEnable " + z);
            }
            return z;
        } catch (Exception unused) {
            Log.i("GestureTouch", "isFeatureEnabled FAILED key = " + str);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public int getWhatNewMode() {
        int i = 0;
        try {
            Cursor query = this.mContext.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "show_whatsnew"), (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null) {
                if (query.moveToNext()) {
                    i = query.getInt(query.getColumnIndex("show_whatsnew"));
                }
                query.close();
            }
            if (DEBUG) {
                Log.i("GestureTouch", "getWhatNewMode() whatNewMode " + i);
            }
            return i;
        } catch (Exception unused) {
            Log.i("GestureTouch", "getWhatNewMode FAILED");
            return 0;
        }
    }

    public void writeCheckInData(String str) {
        if (DEBUG) {
            Log.i("GestureTouch", "writeCheckInData Key: " + str);
        }
        this.mHandler.post(new GestureTouchSettingsManager$$ExternalSyntheticLambda2(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$writeCheckInData$2(String str) {
        try {
            Uri withAppendedPath = Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), str);
            Cursor query = this.mContext.getContentResolver().query(withAppendedPath, (String[]) null, (String) null, (String[]) null, (String) null);
            int i = 0;
            if (query != null) {
                if (query.moveToNext()) {
                    i = query.getInt(query.getColumnIndex(str));
                }
                query.close();
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(str, Integer.valueOf(i + 1));
            this.mContext.getContentResolver().update(withAppendedPath, contentValues, (String) null, (String[]) null);
        } catch (Exception unused) {
            Log.i("GestureTouch", "writeCheckInData FAILED");
        }
    }

    public void writePositionData(boolean z, int i) {
        this.mHandler.post(new GestureTouchSettingsManager$$ExternalSyntheticLambda3(this, z, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$writePositionData$3(boolean z, int i) {
        Uri uri;
        try {
            ContentValues contentValues = new ContentValues();
            if (z) {
                uri = Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "edgepill_x_position");
                contentValues.put("edgepill_x_position", Integer.valueOf(i));
            } else {
                uri = Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), "edgepill_y_position");
                contentValues.put("edgepill_y_position", Integer.valueOf(i));
            }
            if (DEBUG) {
                Log.i("GestureTouch", "writePositionData KEY = " + uri + " position = " + i);
            }
            this.mContext.getContentResolver().update(uri, contentValues, (String) null, (String[]) null);
            this.mContext.getContentResolver().notifyChange(uri, (ContentObserver) null);
        } catch (Exception unused) {
            Log.i("GestureTouch", "writePositionData FAILED");
        }
    }

    /* access modifiers changed from: private */
    public int getPositionData(boolean z) {
        int i;
        String str = z ? "edgepill_x_position" : "edgepill_y_position";
        try {
            Cursor query = this.mContext.getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), str), (String[]) null, (String) null, (String[]) null, (String) null);
            if (z) {
                i = 1;
            } else {
                i = this.mContext.getResources().getDimensionPixelSize(R$dimen.pill_default_y_position);
            }
            if (query != null) {
                if (query.moveToNext()) {
                    i = query.getInt(query.getColumnIndex(str));
                }
                query.close();
            }
            return i;
        } catch (Exception unused) {
            Log.i("GestureTouch", "getPositionData FAILED");
            if (z) {
                return 1;
            }
            return this.mContext.getResources().getDimensionPixelSize(R$dimen.pill_default_y_position);
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean z = DEBUG;
        if (z) {
            Log.d("GestureTouch", "onReceive action = " + action);
        }
        if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
            this.mEdgeTouchAppEnabled = isPackageAvailable("com.motorola.motoedgeassistant");
            this.mFpsTouchAppEnabled = isPackageAvailable("com.motorola.motofpstouch");
            for (GestureSettingsListener updateAppState : this.mSettingsListener) {
                updateAppState.updateAppState(this.mEdgeTouchAppEnabled || this.mFpsTouchAppEnabled);
            }
        } else if ("android.intent.action.USER_SWITCHED".equals(action)) {
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", ActivityManager.getCurrentUser());
            if (intExtra != this.mCurrentUser) {
                this.mCurrentUser = intExtra;
                this.mContext = this.mContext.createContextAsUser(UserHandle.of(intExtra), 0);
                unregisterSettingObserver();
                registerSettingObserver();
                this.mEdgeTouchAppEnabled = isPackageAvailable("com.motorola.motoedgeassistant");
                this.mFpsTouchAppEnabled = isPackageAvailable("com.motorola.motofpstouch");
                initSettingsValue();
                if (z) {
                    Log.d("GestureTouch", "switch user mCurrentUser = " + this.mCurrentUser + " mEdgeTouchAppEnabled = " + this.mEdgeTouchAppEnabled + " mFpsTouchAppEnabled = " + this.mFpsTouchAppEnabled + " mGestureEnabled = " + this.mGestureEnabled);
                }
            }
        } else if ("android.intent.action.DATE_CHANGED".equals(action)) {
            for (GestureSettingsListener dateChanged : this.mSettingsListener) {
                dateChanged.dateChanged();
            }
        }
    }

    public String getTouchAppPackageName() {
        if (this.mEdgeTouchAppEnabled) {
            return "com.motorola.motoedgeassistant";
        }
        if (this.mFpsTouchAppEnabled) {
            return "com.motorola.motofpstouch";
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void updatePidOfEdgeTouch() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningAppProcesses();
        for (int i = 0; i < runningAppProcesses.size(); i++) {
            if (runningAppProcesses.get(i).processName.equals(getTouchAppPackageName())) {
                this.mPidOfEdgeTouch = runningAppProcesses.get(i).pid;
            } else {
                this.mPidOfEdgeTouch = -1;
            }
        }
        if (DEBUG) {
            Log.i("GestureTouch", "mPidOfEdgeTouch = " + this.mPidOfEdgeTouch);
        }
    }

    private boolean isSystemApp(String str) {
        boolean z = false;
        try {
            if ((this.mContext.getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0) {
                z = true;
            }
            if (DEBUG) {
                Log.d("GestureTouch", "isSystemApp systemApp = " + z);
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("GestureTouch", "isSystemApp NameNotFoundException pkg = " + str);
        }
        return z;
    }

    private boolean isPackageAvailable(String str) {
        boolean z = false;
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(str, 0);
            if (applicationInfo != null && applicationInfo.enabled && isSystemApp(str)) {
                z = true;
            }
        } catch (Exception unused) {
        }
        if (DEBUG) {
            Log.d("GestureTouch", "Package(" + str + ") available: " + z);
        }
        return z;
    }

    private class ProcessObserver extends IProcessObserver.Stub {
        public void onForegroundActivitiesChanged(int i, int i2, boolean z) {
        }

        public void onForegroundServicesChanged(int i, int i2, int i3) {
        }

        private ProcessObserver() {
        }

        public void onProcessDied(int i, int i2) {
            if (GestureTouchSettingsManager.this.mPidOfEdgeTouch == i) {
                GestureTouchSettingsManager.this.resetTutorialOrPositionState();
            }
        }
    }

    /* access modifiers changed from: private */
    public void resetTutorialOrPositionState() {
        if (this.mIsPositionMode) {
            disableFeature("change_pill_position_mode");
        }
        if (this.mInTutorialMode) {
            disableFeature("in_tutorial_mode");
        }
    }

    private void disableFeature(String str) {
        if (DEBUG) {
            Log.i("GestureTouch", "disableFeature key = " + str);
        }
        try {
            Uri withAppendedPath = Uri.withAppendedPath(Uri.parse("content://com.motorola.motoedgeassistant.appsettingsprovider"), str);
            ContentValues contentValues = new ContentValues();
            contentValues.put(str, 0);
            this.mContext.getContentResolver().update(withAppendedPath, contentValues, (String) null, (String[]) null);
        } catch (Exception unused) {
            Log.i("GestureTouch", "disableFeature FAILED key = " + str);
        }
    }

    public void addSettingsListener(GestureSettingsListener gestureSettingsListener) {
        this.mSettingsListener.add(gestureSettingsListener);
    }
}
