package com.android.systemui.doze;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.Dependency;
import com.android.systemui.R$integer;
import com.android.systemui.biometrics.UdfpsController;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.doze.IMotoDisplay;
import com.android.systemui.doze.IMotoDisplayCallback;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.StatusBar;
import com.motorola.android.provider.MotorolaSettings;

public class MotoDisplayManager {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static boolean sAospAD = false;
    private AODSettingsObserver mAODSettingsObserver;
    private Runnable mAODVirtualSensorListener;
    /* access modifiers changed from: private */
    public volatile int mCallbackId;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public MotoDisplayDataProvider mDataProvider;
    private DebugSettingsObserver mDebugSettingsObserver;
    DozeHost mDozeHost;
    /* access modifiers changed from: private */
    public DozeMachine mDozeMachine;
    private DozeParameters mDozeParameters;
    private DozeScreenBrightness mDozeScreenBrightness;
    /* access modifiers changed from: private */
    public boolean mDozing;
    /* access modifiers changed from: private */
    public boolean mEnableDemo = false;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    MotoDisplayManager.this.handleShow();
                    return;
                case 2:
                    MotoDisplayManager.this.handleShowContinue();
                    return;
                case 3:
                    MotoDisplayManager.this.handleHide();
                    return;
                case 4:
                    MotoDisplayManager.this.handleShowUdfps();
                    return;
                case 5:
                    MotoDisplayManager.this.handleHideUdfps();
                    return;
                case 6:
                    MotoDisplayManager.this.handleTouchEventToTriggerUDFPS((Bundle) message.obj);
                    return;
                default:
                    return;
            }
        }
    };
    private int mHideDelayDefault;
    private boolean mIsAodShow = false;
    private int mLastScreenBrightness = -1;
    private boolean mLastScreenOn = false;
    private final ServiceConnection mMotoDisplayConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IMotoDisplay unused = MotoDisplayManager.this.mMotoDisplayService = IMotoDisplay.Stub.asInterface(iBinder);
            Log.d("MotoDisplayManager", "ServiceConnection - Connected MotoDisplayService " + MotoDisplayManager.this.mMotoDisplayService);
            MotoDisplayManager.this.showContinue();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MotoDisplayManager", "ServiceConnection - Disconnected MotoDisplayService " + MotoDisplayManager.this.mMotoDisplayService);
            if (MotoDisplayManager.this.mMotoDisplayService != null) {
                Log.wtf("MotoDisplayManager", "ServiceConnection - MotoDisplayService died");
                MotoDisplayManager.this.hide(0);
            }
        }

        public void onNullBinding(ComponentName componentName) {
            Log.wtf("MotoDisplayManager", "ServiceConnection - MotoDisplayService onNullBinding");
            MotoDisplayManager.this.hide(0);
        }

        public void onBindingDied(ComponentName componentName) {
            Log.wtf("MotoDisplayManager", "ServiceConnection - MotoDisplayService onBindingDied");
            MotoDisplayManager.this.hide(0);
        }
    };
    /* access modifiers changed from: private */
    public IMotoDisplay mMotoDisplayService;
    private ScreenLifecycle mScreenLifecycle;
    private ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() {
        public void onScreenTurnedOn() {
            MotoDisplayManager.this.screenChange();
        }

        public void onScreenTurningOn() {
            MotoDisplayManager.this.screenChange();
        }

        public void onScreenTurnedOff() {
            MotoDisplayManager.this.screenChange();
        }

        public void onScreenTurningOff() {
            MotoDisplayManager.this.screenChange();
        }

        public void onLidClosed() {
            boolean unused = MotoDisplayManager.this.mShouldPulse = true;
            if (MotoDisplayManager.this.mDozing) {
                if (MotoDisplayManager.this.mDozeMachine.getState() == DozeMachine.State.DOZE_PULSING) {
                    MotoDisplayManager.this.mDozeHost.notifyFinishPulse();
                }
                MotoDisplayManager.this.mDozeMachine.requestPulseForCli(12);
                MotoDisplayManager.this.restoreShouldPulse();
            }
        }

        public void onLidOpen() {
            MotoDisplayManager.this.restoreShouldPulse();
        }
    };
    /* access modifiers changed from: private */
    public boolean mShouldPulse;
    private boolean mShouldUnbind = false;
    private StatusBar mStatusBar;
    /* access modifiers changed from: private */
    public boolean mSubsidyLocked;
    private UdfpsControlByExternal mUdfpsControlByExternal;
    private UdfpsController mUdfpsController;
    private UserReceiver mUserReceiver;
    private WakefulnessLifecycle mWakefulnessLifecycle;

    public interface UdfpsControlByExternal {
        void hideUdfps();

        void showUdfps();
    }

    static /* synthetic */ int access$008(MotoDisplayManager motoDisplayManager) {
        int i = motoDisplayManager.mCallbackId;
        motoDisplayManager.mCallbackId = i + 1;
        return i;
    }

    private final class MotoDisplayCallback extends IMotoDisplayCallback.Stub {

        /* renamed from: id */
        private int f88id;

        MotoDisplayCallback() {
            MotoDisplayManager.access$008(MotoDisplayManager.this);
            this.f88id = MotoDisplayManager.this.mCallbackId;
            Log.i("MotoDisplayManager", "new MotoDisplayCallback id:" + this.f88id);
        }

        public void requestHide() {
            Log.i("MotoDisplayManager", "MotoDisplayCallback requestHide");
            if (checkId()) {
                MotoDisplayManager.this.requestHideInternal();
            }
        }

        public void requestUnlock(IBinder iBinder, boolean z, boolean z2) {
            Log.i("MotoDisplayManager", "MotoDisplayCallback requestUnlockInternal<callback>" + iBinder + "<dismiss>" + z + "<collapse>" + z2);
            if (MotoDisplayManager.this.mSubsidyLocked) {
                Log.d("MotoDisplayManager", "Jio subsidy locked, don't request unlock(AOD).");
            } else if (checkId()) {
                MotoDisplayManager.this.requestUnlockInternal(iBinder, z, z2);
            }
        }

        public void triggerNotificationClickAndRequestUnlock(String str, PendingIntent pendingIntent, Intent intent) {
            Log.i("MotoDisplayManager", "MotoDisplayCallback triggerNotificationClickAndRequestUnlock <notificationKey>" + str + "<pendingIntent>" + pendingIntent + "<fillInIntent>" + intent);
            if (MotoDisplayManager.this.mSubsidyLocked) {
                Log.d("MotoDisplayManager", "Jio subsidy locked, don't trigger notification click and request unlock(AOD).");
            } else if (checkId()) {
                MotoDisplayManager.this.triggerNotificationClickAndRequestUnlockInternal(str, pendingIntent, intent);
            }
        }

        public void requestScreenOff() {
            Log.i("MotoDisplayManager", "MotoDisplayCallback requestScreenOff");
            if (checkId()) {
                MotoDisplayManager.this.requestScreenOffInternal();
            }
        }

        public void triggerVirtualSensor() {
            Log.i("MotoDisplayManager", "MotoDisplayCallback triggerVirtualSensor");
            if (MotoDisplayManager.this.mSubsidyLocked) {
                Log.d("MotoDisplayManager", "Jio subsidy locked, don't trigger virtual sensor(AOD).");
            } else if (MotoDisplayManager.this.mDozeHost.isDozeSuppressed()) {
                if (!Build.IS_USER) {
                    Log.d("MotoDisplayManager", "Doze suppressed. Not trigger.");
                }
            } else if (checkId()) {
                MotoDisplayManager.this.triggerVirtualSensorInternal();
            }
        }

        public void setScreenBrightness(int i) {
            Log.i("MotoDisplayManager", "MotoDisplayCallback setScreenBrightness<brightness>" + i);
            if (MotoDisplayManager.this.mSubsidyLocked) {
                Log.d("MotoDisplayManager", "Jio subsidy locked, don't set screen brightness(AOD).");
            } else if (MotoDisplayManager.this.mDozeHost.isDozeSuppressed()) {
                if (!Build.IS_USER) {
                    Log.d("MotoDisplayManager", "Doze suppressed. Not set brightness.");
                }
            } else if (checkId()) {
                MotoDisplayManager.this.setScreenBrightnessInternal(i);
            }
        }

        public Bundle queryData(Bundle bundle) {
            if (MotoDisplayManager.DEBUG) {
                Log.d("MotoDisplayManager", "MotoDisplayCallback queryData<request>" + bundle);
            }
            if (checkId()) {
                return MotoDisplayManager.this.mDataProvider.queryDataInternal(bundle);
            }
            return null;
        }

        private boolean checkId() {
            boolean z = MotoDisplayManager.this.mCallbackId == this.f88id;
            if (!z) {
                Log.wtf("MotoDisplayManager", "wrong requester id:" + this.f88id);
            }
            return z;
        }

        public void showUdfpsFromExternal() {
            Log.i("MotoDisplayManager", "MotoDisplayCallback showUdfpsFromExternal");
            if (checkId()) {
                MotoDisplayManager.this.mHandler.removeMessages(5);
                MotoDisplayManager.this.mHandler.sendEmptyMessage(4);
            }
        }

        public void hideUdfpsFromExternal() {
            Log.i("MotoDisplayManager", "MotoDisplayCallback hideUdfpsFromExternal");
            if (checkId()) {
                MotoDisplayManager.this.mHandler.removeMessages(4);
                MotoDisplayManager.this.mHandler.sendEmptyMessage(5);
            }
        }

        public void triggerUdfpsStartAuth(Bundle bundle) {
            Log.i("MotoDisplayManager", "MotoDisplayCallback triggerUdfpsStartAuth");
            if (checkId()) {
                MotoDisplayManager.this.mHandler.removeMessages(6);
                MotoDisplayManager.this.mHandler.sendMessage(MotoDisplayManager.this.mHandler.obtainMessage(6, bundle));
            }
        }
    }

    public boolean isCliAndLidClose() {
        return MotoFeature.getInstance(this.mContext).isSupportCli() && MotoFeature.isLidClosed(this.mContext);
    }

    public MotoDisplayManager(Context context, DozeHost dozeHost) {
        this.mContext = context;
        this.mDozeHost = dozeHost;
        this.mStatusBar = (StatusBar) Dependency.get(StatusBar.class);
        this.mAODSettingsObserver = new AODSettingsObserver();
        UserReceiver userReceiver = new UserReceiver();
        this.mUserReceiver = userReceiver;
        userReceiver.register(this.mContext);
        if (DEBUG) {
            this.mDebugSettingsObserver = new DebugSettingsObserver();
        }
        ScreenLifecycle screenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mScreenLifecycle = screenLifecycle;
        screenLifecycle.addObserver(this.mScreenObserver);
        this.mWakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);
        this.mDozeParameters = (DozeParameters) Dependency.get(DozeParameters.class);
        this.mHideDelayDefault = this.mContext.getResources().getInteger(R$integer.config_moto_display_hide_delay_default);
        this.mLastScreenBrightness = this.mContext.getResources().getInteger(17694934);
        this.mDataProvider = new MotoDisplayDataProvider(context);
        rigisterObserverSubsidyLock();
    }

    private void bindService() {
        if (this.mMotoDisplayService == null) {
            String string = this.mContext.getResources().getString(17039986);
            if (this.mEnableDemo) {
                string = "com.motorola.aod/com.motorola.aod.MotoDisplayService";
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
            Intent intent = new Intent();
            intent.setComponent(unflattenFromString);
            if (this.mContext.bindServiceAsUser(intent, this.mMotoDisplayConnection, 1, UserHandle.CURRENT)) {
                this.mShouldUnbind = true;
                if (DEBUG) {
                    Log.v("MotoDisplayManager", "moto display service bind");
                    return;
                }
                return;
            }
            Log.e("MotoDisplayManager", "moto display service bind fail");
        }
    }

    private void unbindService() {
        if (this.mShouldUnbind) {
            this.mShouldUnbind = false;
            try {
                this.mContext.unbindService(this.mMotoDisplayConnection);
                if (DEBUG) {
                    Log.v("MotoDisplayManager", "moto display service unbind");
                }
            } catch (IllegalArgumentException e) {
                Log.e("MotoDisplayManager", "moto display service unbind fail " + e);
            }
            this.mMotoDisplayService = null;
        }
    }

    /* access modifiers changed from: private */
    public void handleShowContinue() {
        if (!this.mIsAodShow || this.mMotoDisplayService == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't show: mIsAodShow=");
            sb.append(this.mIsAodShow);
            sb.append(this.mMotoDisplayService == null ? " mMotoDisplayService is null" : "");
            Log.w("MotoDisplayManager", sb.toString());
            handleHide();
            return;
        }
        if (DEBUG) {
            Log.v("MotoDisplayManager", "handleShowContinue");
        }
        boolean z = !this.mDozeParameters.getAlwaysOn();
        this.mLastScreenOn = isScreenOnReally();
        try {
            Log.i("MotoDisplayManager", "mMotoDisplayService.show " + this.mMotoDisplayService);
            this.mMotoDisplayService.show(new MotoDisplayCallback(), z ? 1 : 0, this.mLastScreenOn, this.mDataProvider.getExtraDataForShow());
        } catch (RemoteException e) {
            Log.e("MotoDisplayManager", "show fail" + e);
        }
    }

    /* access modifiers changed from: private */
    public void showContinue() {
        if (!this.mHandler.hasMessages(2)) {
            this.mHandler.sendEmptyMessage(2);
        }
    }

    /* access modifiers changed from: private */
    public void handleShow() {
        boolean z = DEBUG;
        if (z) {
            Log.v("MotoDisplayManager", "handleShow");
        }
        if (!this.mIsAodShow) {
            Log.d("MotoDisplayManager", "handleShow break, mIsAodShow is " + this.mIsAodShow);
        } else if (sAospAD) {
            Log.d("MotoDisplayManager", "handleShow but sAospAD");
        } else if (this.mMotoDisplayService == null) {
            bindService();
        } else if (z) {
            Log.v("MotoDisplayManager", "mMotoDisplayService is already bind, no need to bind again");
        }
    }

    public void show() {
        if (DEBUG) {
            Log.v("MotoDisplayManager", "show");
        }
        this.mIsAodShow = true;
        this.mHandler.removeMessages(3);
        if (!this.mHandler.hasMessages(1)) {
            this.mHandler.sendEmptyMessage(1);
        }
    }

    /* access modifiers changed from: private */
    public void handleHide() {
        if (DEBUG) {
            Log.v("MotoDisplayManager", "handleHide");
        }
        if (this.mIsAodShow) {
            Log.d("MotoDisplayManager", "handleHide break, mIsAodShow is " + this.mIsAodShow);
            return;
        }
        if (this.mMotoDisplayService != null) {
            try {
                Log.i("MotoDisplayManager", "mMotoDisplayService.hide " + this.mMotoDisplayService);
                this.mMotoDisplayService.hide();
            } catch (RemoteException e) {
                Log.e("MotoDisplayManager", "hide fail " + e);
            }
        } else {
            Log.w("MotoDisplayManager", "can't hide: mMotoDisplayService has been null");
        }
        unbindService();
    }

    public void hide(int i) {
        this.mIsAodShow = false;
        if (i < 0) {
            i = this.mHideDelayDefault;
        }
        boolean z = DEBUG;
        if (z) {
            Log.v("MotoDisplayManager", "hide delay:" + i);
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        if (this.mHandler.getLooper().isCurrentThread()) {
            if (z) {
                Log.v("MotoDisplayManager", "hide direct");
            }
            this.mHandler.removeMessages(3);
            handleHide();
        } else if (!this.mHandler.hasMessages(3)) {
            this.mHandler.sendEmptyMessageDelayed(3, (long) i);
        }
    }

    public void notifyEvent(String str, boolean z, String str2, String str3, Bundle bundle) {
        if (!TextUtils.isEmpty(str)) {
            this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda8(this, str, z, str2, str3, bundle));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyEvent$0(String str, boolean z, String str2, String str3, Bundle bundle) {
        if (this.mMotoDisplayService != null) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("event", str);
            bundle2.putBoolean("pulse", z);
            if (!TextUtils.isEmpty(str2)) {
                bundle2.putString("simple", str2);
            }
            if (!TextUtils.isEmpty(str3)) {
                bundle2.putString("detail", str3);
            }
            if (bundle != null) {
                bundle2.putBundle("extras", bundle);
            }
            if (DEBUG) {
                Log.v("MotoDisplayManager", "notifyEvent data " + bundle2);
            }
            try {
                this.mMotoDisplayService.notifyEvent(bundle2);
            } catch (RemoteException e) {
                Log.e("MotoDisplayManager", "notifyEvent fail" + e);
            }
        }
    }

    public void hideOnStartedWakingUp() {
        if (DEBUG) {
            Log.d("MotoDisplayManager", "hideOnStartedWakingUp isScreenOnReally = " + isScreenOnReally());
        }
        if (!isScreenOnReally()) {
            hide(-1);
        }
    }

    private boolean isScreenOnReally() {
        return this.mScreenLifecycle.getScreenState() == 2;
    }

    /* access modifiers changed from: private */
    public void screenChange() {
        if (DEBUG) {
            Log.v("MotoDisplayManager", "screenChange");
        }
        boolean isScreenOnReally = isScreenOnReally();
        if (isScreenOnReally != this.mLastScreenOn) {
            this.mLastScreenOn = isScreenOnReally;
            IMotoDisplay iMotoDisplay = this.mMotoDisplayService;
            if (iMotoDisplay != null) {
                try {
                    iMotoDisplay.screenChange(isScreenOnReally);
                } catch (RemoteException e) {
                    Log.e("MotoDisplayManager", "screenChange fail " + e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void requestHideInternal() {
        this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHideInternal$1() {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.wakeUp();
        } else {
            Log.e("MotoDisplayManager", "Doze machine is destroyed");
        }
    }

    /* access modifiers changed from: private */
    public void requestUnlockInternal(IBinder iBinder, boolean z, boolean z2) {
        this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda6(this, iBinder, z, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestUnlockInternal$2(IBinder iBinder, boolean z, boolean z2) {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.wakeUp();
            this.mStatusBar.handleDozeUnlock(IRemoteCallback.Stub.asInterface(iBinder), z, z2);
            return;
        }
        Log.e("MotoDisplayManager", "Doze machine is destroyed");
    }

    /* access modifiers changed from: private */
    public void triggerNotificationClickAndRequestUnlockInternal(String str, PendingIntent pendingIntent, Intent intent) {
        this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda7(this, str, pendingIntent, intent));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerNotificationClickAndRequestUnlockInternal$3(String str, PendingIntent pendingIntent, Intent intent) {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.wakeUp();
        } else {
            Log.e("MotoDisplayManager", "Doze machine is destroyed");
        }
        this.mStatusBar.triggerNotificationClickAndRequestUnlockInternal(str, pendingIntent, intent);
    }

    /* access modifiers changed from: private */
    public void requestScreenOffInternal() {
        if (!this.mDozeParameters.getAlwaysOn()) {
            this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda4(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestScreenOffInternal$4() {
        this.mDozeHost.notifyFinishPulse();
    }

    public void triggerVirtualSensorInternal() {
        if (!this.mDozeParameters.getAlwaysOn()) {
            this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerVirtualSensorInternal$5() {
        Runnable runnable = this.mAODVirtualSensorListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public void setScreenBrightnessInternal(int i) {
        this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda5(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setScreenBrightnessInternal$6(int i) {
        this.mLastScreenBrightness = i;
        DozeScreenBrightness dozeScreenBrightness = this.mDozeScreenBrightness;
        if (dozeScreenBrightness != null) {
            dozeScreenBrightness.updateBrightnessAndReady(true);
        }
    }

    public int getLastScreenBrightness() {
        return this.mLastScreenBrightness;
    }

    public void setAODVirtualSensorListener(Runnable runnable) {
        this.mAODVirtualSensorListener = runnable;
    }

    public void setDozeScreenBrightness(DozeScreenBrightness dozeScreenBrightness) {
        this.mDozeScreenBrightness = dozeScreenBrightness;
    }

    public void setDozeMachine(DozeMachine dozeMachine) {
        this.mDozeMachine = dozeMachine;
    }

    public static boolean isAospAD() {
        return sAospAD;
    }

    private final class AODSettingsObserver extends ContentObserver {
        final Uri mAospAdUri;

        AODSettingsObserver() {
            super(MotoDisplayManager.this.mHandler);
            Uri uriFor = Settings.Secure.getUriFor("doze_enabled");
            this.mAospAdUri = uriFor;
            MotoDisplayManager.this.mContext.getContentResolver().registerContentObserver(uriFor, false, this, -1);
            update(uriFor);
        }

        public void onChange(boolean z, Uri uri) {
            if (uri != null) {
                update(uri);
            }
        }

        /* access modifiers changed from: package-private */
        public void update(Uri uri) {
            if (this.mAospAdUri.equals(uri)) {
                MotoDisplayManager.this.updateAospAD();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAospAD() {
        boolean z = false;
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "doze_enabled", 0, -2);
        if (intForUser != 2) {
            z = true;
        }
        sAospAD = z;
        Log.i("MotoDisplayManager", "sAospAD value: " + intForUser);
    }

    private final class UserReceiver extends BroadcastReceiver {
        private UserReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                MotoDisplayManager.this.updateAospAD();
            }
        }

        public void register(Context context) {
            context.registerReceiver(this, new IntentFilter("android.intent.action.USER_SWITCHED"));
        }
    }

    private final class DebugSettingsObserver extends ContentObserver {
        final Uri mEnableDemoUri;

        DebugSettingsObserver() {
            super(MotoDisplayManager.this.mHandler);
            Uri uriFor = MotorolaSettings.Global.getUriFor("motodisplaymanager_debug_enable_demo");
            this.mEnableDemoUri = uriFor;
            MotoDisplayManager.this.mContext.getContentResolver().registerContentObserver(uriFor, false, this, -1);
            update(uriFor);
        }

        public void onChange(boolean z, Uri uri) {
            if (uri != null) {
                update(uri);
            }
        }

        /* access modifiers changed from: package-private */
        public void update(Uri uri) {
            if (this.mEnableDemoUri.equals(uri)) {
                boolean z = false;
                int i = MotorolaSettings.Global.getInt(MotoDisplayManager.this.mContext.getContentResolver(), "motodisplaymanager_debug_enable_demo", 0);
                MotoDisplayManager motoDisplayManager = MotoDisplayManager.this;
                if (i == 1) {
                    z = true;
                }
                boolean unused = motoDisplayManager.mEnableDemo = z;
                Log.d("MotoDisplayManager", "mEnableDemo: " + MotoDisplayManager.this.mEnableDemo);
            }
        }
    }

    public void requestWakeup() {
        this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestWakeup$7() {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.wakeUp();
        }
    }

    public void setDarkenBrightnessForCli() {
        DozeScreenBrightness dozeScreenBrightness = this.mDozeScreenBrightness;
        if (dozeScreenBrightness != null) {
            dozeScreenBrightness.setDarkenBrightnessForCli();
        }
    }

    public void resetCliDozeBrightness() {
        this.mHandler.post(new MotoDisplayManager$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetCliDozeBrightness$8() {
        DozeScreenBrightness dozeScreenBrightness = this.mDozeScreenBrightness;
        if (dozeScreenBrightness != null) {
            dozeScreenBrightness.updateBrightnessAndReady(true);
        }
    }

    public boolean shouldPulseForLidClosed() {
        return this.mShouldPulse;
    }

    public void restoreShouldPulse() {
        this.mShouldPulse = false;
    }

    public void setDozing(boolean z) {
        this.mDozing = z;
    }

    private void rigisterObserverSubsidyLock() {
        C09324 r0 = new ContentObserver((Handler) null) {
            public void onChange(boolean z) {
                MotoDisplayManager motoDisplayManager = MotoDisplayManager.this;
                boolean z2 = false;
                if (MotorolaSettings.Global.getInt(motoDisplayManager.mContext.getContentResolver(), "jio_subsidy_locked", 0) == 1) {
                    z2 = true;
                }
                boolean unused = motoDisplayManager.mSubsidyLocked = z2;
                Log.d("MotoDisplayManager", "Subsidy locked: " + MotoDisplayManager.this.mSubsidyLocked);
            }
        };
        boolean z = true;
        if (MotorolaSettings.Global.getInt(this.mContext.getContentResolver(), "jio_subsidy_locked", 0) != 1) {
            z = false;
        }
        this.mSubsidyLocked = z;
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Global.getUriFor("jio_subsidy_locked"), false, r0, -1);
    }

    public void setUdfpsControlByExternal(UdfpsControlByExternal udfpsControlByExternal) {
        this.mUdfpsControlByExternal = udfpsControlByExternal;
    }

    public void handleShowUdfps() {
        UdfpsControlByExternal udfpsControlByExternal = this.mUdfpsControlByExternal;
        if (udfpsControlByExternal != null) {
            udfpsControlByExternal.showUdfps();
        }
    }

    public void handleHideUdfps() {
        UdfpsControlByExternal udfpsControlByExternal = this.mUdfpsControlByExternal;
        if (udfpsControlByExternal != null) {
            udfpsControlByExternal.hideUdfps();
        }
    }

    public void setUdfpsCOntroller(UdfpsController udfpsController) {
        this.mUdfpsController = udfpsController;
    }

    /* access modifiers changed from: private */
    public void handleTouchEventToTriggerUDFPS(Bundle bundle) {
        if (this.mUdfpsController == null || bundle == null) {
            Log.e("MotoDisplayManager", "mUdfpsController or event are null, can't trigger Udfps auth.");
            return;
        }
        MotionEvent motionEvent = (MotionEvent) bundle.getParcelable("udfps_motion_event");
        if (motionEvent == null) {
            Log.e("MotoDisplayManager", "Invalid motion event data.");
            return;
        }
        if (DEBUG) {
            Log.v("MotoDisplayManager", "handleTouchEventToTriggerUDFPS event: " + motionEvent);
        }
        this.mUdfpsController.onTouch(motionEvent);
    }
}
