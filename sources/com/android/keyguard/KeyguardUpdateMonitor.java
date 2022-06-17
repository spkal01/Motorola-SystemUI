package com.android.keyguard;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.UserSwitchObserver;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricSourceType;
import android.hardware.biometrics.CryptoObject;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback;
import android.hardware.display.AmbientDisplayConfiguration;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.UiccSlotInfo;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import androidx.lifecycle.Observer;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityContainer;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.fuelgauge.BatteryStatus;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$array;
import com.android.systemui.R$string;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.biometrics.UdfpsView;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.util.Assert;
import com.android.systemui.util.RingerModeTracker;
import com.google.android.collect.Lists;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import com.motorola.systemui.folio.FolioSensorManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import motorola.core_services.misc.MotoExtHwManager;

public class KeyguardUpdateMonitor implements TrustManager.TrustListener, Dumpable {
    public static final boolean CORE_APPS_ONLY;
    /* access modifiers changed from: private */
    public static final boolean DEBUG = KeyguardConstants.DEBUG;
    private static final boolean DEBUG_FACE;
    private static final boolean DEBUG_FINGERPRINT;
    private static final ComponentName FALLBACK_HOME_COMPONENT = new ComponentName("com.android.settings", "com.android.settings.FallbackHome");
    /* access modifiers changed from: private */
    public static final Object mFPSGateLock = new Object();
    /* access modifiers changed from: private */
    public static int mFingerprintUnlockAttempts;
    /* access modifiers changed from: private */
    public static int mFingerprintUnlockErrors;
    /* access modifiers changed from: private */
    public static int sCurrentUser;
    private final boolean mAcquiredHapticEnabled = false;
    /* access modifiers changed from: private */
    public int mActiveMobileDataSubscription = -1;
    private boolean mAssistantVisible;
    private String[] mAssuranceSubIMSIsGid1;
    private final AuthController mAuthController;
    private boolean mAuthInterruptActive;
    private final Executor mBackgroundExecutor;
    @VisibleForTesting
    BatteryStatus mBatteryStatus;
    private IBiometricEnabledOnKeyguardCallback mBiometricEnabledCallback = new IBiometricEnabledOnKeyguardCallback.Stub() {
        public void onChanged(boolean z, int i) throws RemoteException {
            KeyguardUpdateMonitor.this.mHandler.post(new KeyguardUpdateMonitor$3$$ExternalSyntheticLambda0(this, i, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onChanged$0(int i, boolean z) {
            KeyguardUpdateMonitor.this.mBiometricEnabledForUser.put(i, z);
            KeyguardUpdateMonitor.this.updateBiometricListeningState();
        }
    };
    /* access modifiers changed from: private */
    public SparseBooleanArray mBiometricEnabledForUser = new SparseBooleanArray();
    private BiometricManager mBiometricManager;
    private String[] mBoostSubIMSIsGid1;
    private boolean mBouncer;
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastAllReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.app.action.NEXT_ALARM_CLOCK_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
            } else if ("android.intent.action.USER_INFO_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(317, intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()), 0));
            } else if ("com.android.facelock.FACE_UNLOCK_STARTED".equals(action)) {
                Trace.beginSection("KeyguardUpdateMonitor.mBroadcastAllReceiver#onReceive ACTION_FACE_UNLOCK_STARTED");
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(327, 1, getSendingUserId()));
                Trace.endSection();
            } else if ("com.android.facelock.FACE_UNLOCK_STOPPED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(327, 0, getSendingUserId()));
            } else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(309, Integer.valueOf(getSendingUserId())));
            } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(334, getSendingUserId(), 0));
            } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(340, intent.getIntExtra("android.intent.extra.user_handle", -1), 0));
            } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(341, intent.getIntExtra("android.intent.extra.user_handle", -1), 0));
            } else if ("android.nfc.action.REQUIRE_UNLOCK_FOR_NFC".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(345);
            }
        }
    };
    private final BroadcastDispatcher mBroadcastDispatcher;
    @VisibleForTesting
    protected final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (KeyguardUpdateMonitor.DEBUG) {
                Log.d("KeyguardUpdateMonitor", "received broadcast " + action);
            }
            if ("android.intent.action.TIME_TICK".equals(action) || "android.intent.action.TIME_SET".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(301);
            } else if ("android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(339, intent.getStringExtra("time-zone")));
            } else if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(302, new BatteryStatus(intent)));
            } else if ("android.intent.action.SIM_STATE_CHANGED".equals(action) || "com.motorola.keyguard.ACTION_SIM_PIN_UNLOCK".equals(action)) {
                SimData fromIntent = SimData.fromIntent(intent);
                if (!intent.getBooleanExtra("rebroadcastOnUnlock", false)) {
                    Log.v("KeyguardUpdateMonitor", "action " + action + " state: " + intent.getStringExtra("ss") + " slotId: " + fromIntent.slotId + " subid: " + fromIntent.subId);
                    KeyguardUpdateMonitor.this.updateSwitchBootAnimation(fromIntent.subId, fromIntent.slotId, intent.getStringExtra("ss"));
                    KeyguardUpdateMonitor.this.mHandler.obtainMessage(304, fromIntent.subId, fromIntent.slotId, Integer.valueOf(fromIntent.simState)).sendToTarget();
                } else if (fromIntent.simState == 1) {
                    KeyguardUpdateMonitor.this.mHandler.obtainMessage(338, Boolean.TRUE).sendToTarget();
                }
            } else if ("android.intent.action.PHONE_STATE".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(306, intent.getStringExtra("state")));
            } else if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(329);
            } else if ("android.intent.action.SERVICE_STATE".equals(action)) {
                ServiceState newFromBundle = ServiceState.newFromBundle(intent.getExtras());
                int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                if (KeyguardUpdateMonitor.DEBUG) {
                    Log.v("KeyguardUpdateMonitor", "action " + action + " serviceState=" + newFromBundle + " subId=" + intExtra);
                }
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(330, intExtra, 0, newFromBundle));
            } else if ("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
            } else if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(337);
            } else if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                MotoSurveyIntent.sInstalled = KeyguardUpdateMonitor.surveyPackageExists(KeyguardUpdateMonitor.this.mContext);
            }
        }
    };
    private FolioSensorManager.Callback mCallback;
    /* access modifiers changed from: private */
    public final ArrayList<WeakReference<KeyguardUpdateMonitorCallback>> mCallbacks = Lists.newArrayList();
    private boolean mCameraGestureTriggered;
    /* access modifiers changed from: private */
    public final Context mContext;
    private boolean mCredentialAttempted;
    /* access modifiers changed from: private */
    public boolean mDeviceInteractive;
    private final DevicePolicyManager mDevicePolicyManager;
    /* access modifiers changed from: private */
    public boolean mDeviceProvisioned;
    private ContentObserver mDeviceProvisionedObserver;
    private DisplayClientState mDisplayClientState = new DisplayClientState();
    private final IDreamManager mDreamManager;
    /* access modifiers changed from: private */
    public boolean mFPSGatedByStowed = false;
    @VisibleForTesting
    final FaceManager.AuthenticationCallback mFaceAuthenticationCallback = new FaceManager.AuthenticationCallback() {
        public void onAuthenticationFailed() {
            KeyguardUpdateMonitor.this.handleFaceAuthFailed();
            if (KeyguardUpdateMonitor.this.mKeyguardBypassController != null) {
                KeyguardUpdateMonitor.this.mKeyguardBypassController.setUserHasDeviceEntryIntent(false);
            }
        }

        public void onAuthenticationSucceeded(FaceManager.AuthenticationResult authenticationResult) {
            Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
            KeyguardUpdateMonitor.this.handleFaceAuthenticated(authenticationResult.getUserId(), authenticationResult.isStrongBiometric());
            Trace.endSection();
            if (KeyguardUpdateMonitor.this.mKeyguardBypassController != null) {
                KeyguardUpdateMonitor.this.mKeyguardBypassController.setUserHasDeviceEntryIntent(false);
            }
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFaceHelp(i, charSequence.toString());
        }

        public void onAuthenticationError(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFaceError(i, charSequence.toString());
            if (KeyguardUpdateMonitor.this.mKeyguardBypassController != null) {
                KeyguardUpdateMonitor.this.mKeyguardBypassController.setUserHasDeviceEntryIntent(false);
            }
        }

        public void onAuthenticationAcquired(int i) {
            KeyguardUpdateMonitor.this.handleFaceAcquired(i);
        }
    };
    private final Runnable mFaceCancelNotReceived = new KeyguardUpdateMonitor$$ExternalSyntheticLambda4(this);
    private CancellationSignal mFaceCancelSignal;
    private final FaceManager.FaceDetectionCallback mFaceDetectionCallback = new KeyguardUpdateMonitor$$ExternalSyntheticLambda0(this);
    private boolean mFaceLockedOutPermanent;
    private final FaceManager.LockoutResetCallback mFaceLockoutResetCallback = new FaceManager.LockoutResetCallback() {
        public void onLockoutReset(int i) {
            KeyguardUpdateMonitor.this.handleFaceLockoutReset();
        }
    };
    private FaceManager mFaceManager;
    /* access modifiers changed from: private */
    public int mFaceRunningState = 0;
    private List<FaceSensorPropertiesInternal> mFaceSensorProperties;
    /* access modifiers changed from: private */
    public boolean mFaceUnlockAvailable;
    @VisibleForTesting
    final FingerprintManager.AuthenticationCallback mFingerprintAuthenticationCallback = new FingerprintManager.AuthenticationCallback() {
        private boolean mPlayedAcquiredHaptic;

        public void onAuthenticationFailed() {
            Optional.ofNullable(KeyguardUpdateMonitor.this.mFingerprintStateCallback).ifPresent(KeyguardUpdateMonitor$14$$ExternalSyntheticLambda2.INSTANCE);
            KeyguardUpdateMonitor.this.handleFingerprintAuthFailed();
        }

        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
            Trace.beginSection("KeyguardUpdateMonitor#onAuthenticationSucceeded");
            Optional.ofNullable(KeyguardUpdateMonitor.this.mFingerprintStateCallback).ifPresent(KeyguardUpdateMonitor$14$$ExternalSyntheticLambda1.INSTANCE);
            Log.v("KeyguardUpdateMonitor", "onAuthenticationSucceeded");
            KeyguardUpdateMonitor.this.handleFingerprintAuthenticated(authenticationResult.getUserId(), authenticationResult.isStrongBiometric());
            Trace.endSection();
            if (!this.mPlayedAcquiredHaptic && KeyguardUpdateMonitor.this.isUdfpsEnrolled()) {
                KeyguardUpdateMonitor.this.playAcquiredHaptic();
            }
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFingerprintHelp(i, charSequence.toString());
        }

        public void onAuthenticationError(int i, CharSequence charSequence) {
            KeyguardUpdateMonitor.this.handleFingerprintError(i, charSequence.toString());
        }

        public void onAuthenticationAcquired(int i) {
            Optional.ofNullable(KeyguardUpdateMonitor.this.mFingerprintStateCallback).ifPresent(new KeyguardUpdateMonitor$14$$ExternalSyntheticLambda0(i));
            KeyguardUpdateMonitor.this.handleFingerprintAcquired(i);
            if (i == 0 && KeyguardUpdateMonitor.this.isUdfpsEnrolled()) {
                this.mPlayedAcquiredHaptic = true;
                KeyguardUpdateMonitor.this.playAcquiredHaptic();
            }
        }

        public void onUdfpsPointerDown(int i) {
            Log.d("KeyguardUpdateMonitor", "onUdfpsPointerDown, sensorId: " + i);
            this.mPlayedAcquiredHaptic = false;
        }

        public void onUdfpsPointerUp(int i) {
            Log.d("KeyguardUpdateMonitor", "onUdfpsPointerUp, sensorId: " + i);
        }
    };
    private CancellationSignal mFingerprintCancelSignal;
    private final FingerprintManager.FingerprintDetectionCallback mFingerprintDetectionCallback = new KeyguardUpdateMonitor$$ExternalSyntheticLambda1(this);
    private boolean mFingerprintLockedOut;
    private boolean mFingerprintLockedOutPermanent;
    private final FingerprintManager.LockoutResetCallback mFingerprintLockoutResetCallback = new FingerprintManager.LockoutResetCallback() {
        public void onLockoutReset(int i) {
            KeyguardUpdateMonitor.this.handleFingerprintLockoutReset();
        }
    };
    private int mFingerprintRunningState = 0;
    /* access modifiers changed from: private */
    public UdfpsView.Callback mFingerprintStateCallback;
    /* access modifiers changed from: private */
    public FlashlightController mFlashlightController;
    private FolioSensorManager mFolioSensorManager;
    private final Runnable mFpCancelNotReceived = new KeyguardUpdateMonitor$$ExternalSyntheticLambda8(this);
    /* access modifiers changed from: private */
    public FingerprintManager mFpm;
    private boolean mGoingToSleep;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public int mHardwareFaceUnavailableRetryCount = 0;
    /* access modifiers changed from: private */
    public int mHardwareFingerprintUnavailableRetryCount = 0;
    private boolean mHasLockscreenWallpaper;
    /* access modifiers changed from: private */
    public HashMap<Integer, InvalidCardData> mInvalidCards = new HashMap<>();
    private final boolean mIsAutomotive;
    private boolean mIsDreaming;
    private boolean mIsFaceAuthUserRequested;
    private boolean mIsFaceEnrolled;
    private boolean mIsFolioClose = false;
    private boolean mIsLTVSensorRegistered;
    private final boolean mIsPrimaryUser;
    private boolean mIsRecentlySkipped = false;
    private boolean mIsStowedSensorRegistered;
    private boolean mIsUdfpsEnrolled;
    /* access modifiers changed from: private */
    public KeyguardBypassController mKeyguardBypassController;
    /* access modifiers changed from: private */
    public KeyguardDisplayManager mKeyguardDisplayManager;
    private boolean mKeyguardGoingAway;
    private boolean mKeyguardIsShowing;
    /* access modifiers changed from: private */
    public boolean mKeyguardIsVisible;
    private boolean mKeyguardOccluded;
    private final SensorEventListener mLTVListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            if (KeyguardUpdateMonitor.DEBUG) {
                Log.d("KeyguardUpdateMonitor", "LTV gesture, onSensorChanged: event = " + sensorEvent);
            }
            boolean z = false;
            if (sensorEvent.values[0] == 1.0f) {
                if (KeyguardUpdateMonitor.DEBUG) {
                    Log.d("KeyguardUpdateMonitor", "LTV gesture triggered.");
                }
                boolean isSecure = KeyguardUpdateMonitor.this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.sCurrentUser);
                if (KeyguardUpdateMonitor.this.mFlashlightController == null) {
                    try {
                        FlashlightController unused = KeyguardUpdateMonitor.this.mFlashlightController = (FlashlightController) Dependency.get(FlashlightController.class);
                        z = KeyguardUpdateMonitor.this.mFlashlightController.isEnabled();
                    } catch (Exception unused2) {
                        if (KeyguardUpdateMonitor.DEBUG) {
                            Log.d("KeyguardUpdateMonitor", "fail to get FlashlightController");
                        }
                    }
                } else {
                    z = KeyguardUpdateMonitor.this.mFlashlightController.isEnabled();
                }
                if (KeyguardUpdateMonitor.DEBUG) {
                    Log.d("KeyguardUpdateMonitor", "onSensorChanged, mLTVSetting = " + KeyguardUpdateMonitor.this.mLTVSetting + ", mFaceUnlockAvailable = " + KeyguardUpdateMonitor.this.mFaceUnlockAvailable + ", isFlashLightOn = " + z);
                }
                if (!KeyguardUpdateMonitor.this.mDeviceInteractive && KeyguardUpdateMonitor.this.mKeyguardIsVisible && isSecure && KeyguardUpdateMonitor.this.mLTVSetting && KeyguardUpdateMonitor.this.mFaceUnlockAvailable && !z) {
                    int access$7200 = KeyguardUpdateMonitor.this.getMotoDisplayEnabled();
                    if (access$7200 == 0) {
                        if (!KeyguardUpdateMonitor.this.isFaceDisabled(KeyguardUpdateMonitor.sCurrentUser)) {
                            if (KeyguardUpdateMonitor.DEBUG) {
                                Log.i("KeyguardUpdateMonitor", "lift to waking up...");
                            }
                            KeyguardUpdateMonitor.this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.policy:FACEUNLOCK");
                        } else if (KeyguardUpdateMonitor.DEBUG) {
                            Log.d("KeyguardUpdateMonitor", "faceunlock disabled, don't wake up.");
                        }
                    } else if ((access$7200 == 2 || access$7200 == 1) && KeyguardUpdateMonitor.this.mFaceRunningState != 1 && !KeyguardUpdateMonitor.this.isCameraGestureTriggered() && !KeyguardUpdateMonitor.this.getFodOnlyModeEnabled()) {
                        KeyguardUpdateMonitor.this.startListeningForFace();
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mLTVSetting;
    private ContentObserver mLTVSettingObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean z) {
            KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
            boolean z2 = false;
            if (MotorolaSettings.Secure.getIntForUser(keyguardUpdateMonitor.mContext.getContentResolver(), "property_lift_to_unlock", 0, -2) != 0) {
                z2 = true;
            }
            boolean unused = keyguardUpdateMonitor.mLTVSetting = z2;
            if (KeyguardUpdateMonitor.DEBUG) {
                Log.d("KeyguardUpdateMonitor", "onChange, mLTVSetting = " + KeyguardUpdateMonitor.this.mLTVSetting);
            }
        }
    };
    private Sensor mLiftSensor;
    private final KeyguardListenQueue mListenModels = new KeyguardListenQueue();
    private boolean mLockIconPressed;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    private int mLockScreenMode;
    private boolean mLogoutEnabled;
    private boolean mLtvSensorOn;
    private String[] mMetroSubIMSIsGid1;
    private int mMotoDisplayDefaultValue;
    private MotoExtHwManager mMotoExtHwManager;
    private boolean mNeedsSlowUnlockTransition;
    private boolean mOccludingAppRequestingFace;
    private boolean mOccludingAppRequestingFp;
    /* access modifiers changed from: private */
    public SparseArray<String> mPLMNBySubIdList = new SparseArray<>();
    /* access modifiers changed from: private */
    public SparseArray<String> mPLMNList = new SparseArray<>();
    private int mPhoneState;
    @VisibleForTesting
    public TelephonyCallback.ActiveDataSubscriptionIdListener mPhoneStateListener = new TelephonyCallback.ActiveDataSubscriptionIdListener() {
        public void onActiveDataSubscriptionIdChanged(int i) {
            int unused = KeyguardUpdateMonitor.this.mActiveMobileDataSubscription = i;
            KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
        }
    };
    /* access modifiers changed from: private */
    public PowerManager mPowerManager;
    private boolean mRdpAuthenticated;
    private Runnable mRetryFaceAuthentication = new Runnable() {
        public void run() {
            Log.w("KeyguardUpdateMonitor", "Retrying face after HW unavailable, attempt " + KeyguardUpdateMonitor.this.mHardwareFaceUnavailableRetryCount);
            KeyguardUpdateMonitor.this.updateFaceListeningState();
        }
    };
    /* access modifiers changed from: private */
    public Runnable mRetryFingerprintAuthentication = new Runnable() {
        public void run() {
            Log.w("KeyguardUpdateMonitor", "Retrying fingerprint after HW unavailable, attempt " + KeyguardUpdateMonitor.this.mHardwareFingerprintUnavailableRetryCount);
            if (KeyguardUpdateMonitor.this.mFpm.isHardwareDetected()) {
                KeyguardUpdateMonitor.this.updateFingerprintListeningState();
            } else if (KeyguardUpdateMonitor.this.mHardwareFingerprintUnavailableRetryCount < 20) {
                KeyguardUpdateMonitor.access$1308(KeyguardUpdateMonitor.this);
                KeyguardUpdateMonitor.this.mHandler.postDelayed(KeyguardUpdateMonitor.this.mRetryFingerprintAuthentication, 500);
            }
        }
    };
    private int mRingMode;
    private final Observer<Integer> mRingerModeObserver = new Observer<Integer>() {
        public void onChanged(Integer num) {
            KeyguardUpdateMonitor.this.mHandler.obtainMessage(305, num.intValue(), 0).sendToTarget();
        }
    };
    private RingerModeTracker mRingerModeTracker;
    /* access modifiers changed from: private */
    public SparseArray<String> mSPNBySubIdList = new SparseArray<>();
    private boolean mScreenOn;
    private Map<Integer, Intent> mSecondaryLockscreenRequirement = new HashMap();
    private boolean mSecureCameraLaunched;
    private KeyguardSecurityContainer.SecurityCallback mSecurityCallback;
    HashMap<Integer, ServiceState> mServiceStates = new HashMap<>();
    private ContentObserver mSideFpsTouchToUnlcokObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean z) {
            KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
            boolean z2 = true;
            if (MotorolaSettings.Global.getInt(keyguardUpdateMonitor.mContext.getContentResolver(), "sidefps_touch_to_unlock", 1) != 1) {
                z2 = false;
            }
            boolean unused = keyguardUpdateMonitor.mSideFpsTouchToUnlockEnabled = z2;
            Log.i("KeyguardUpdateMonitor", "Side fps touch to unlock enable change to : " + KeyguardUpdateMonitor.this.mSideFpsTouchToUnlockEnabled);
        }
    };
    /* access modifiers changed from: private */
    public boolean mSideFpsTouchToUnlockEnabled = true;
    HashMap<Integer, SimData> mSimDatas = new HashMap<>();
    private SparseBooleanArray mSimExist = new SparseBooleanArray();
    private SensorManager mSm;
    private BroadcastReceiver mSpnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("android.telephony.action.SERVICE_PROVIDERS_UPDATED")) {
                int intExtra = intent.getIntExtra("slot", 0);
                int intExtra2 = intent.getIntExtra("subscription", -1);
                String stringExtra = intent.getStringExtra("android.telephony.extra.PLMN");
                boolean booleanExtra = intent.getBooleanExtra("android.telephony.extra.SHOW_PLMN", false);
                KeyguardUpdateMonitor.this.mPLMNList.put(intExtra, stringExtra);
                if (KeyguardUpdateMonitor.DEBUG) {
                    Log.d("KeyguardUpdateMonitor", "Putting PLMN: " + stringExtra + "in slot: " + intExtra);
                }
                String stringExtra2 = intent.getStringExtra("android.telephony.extra.SPN");
                boolean booleanExtra2 = intent.getBooleanExtra("android.telephony.extra.SHOW_SPN", false);
                String str = null;
                KeyguardUpdateMonitor.this.mSPNBySubIdList.put(intExtra2, booleanExtra2 ? stringExtra2 : null);
                if (KeyguardUpdateMonitor.DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("SPN: ");
                    if (!booleanExtra2) {
                        stringExtra2 = null;
                    }
                    sb.append(stringExtra2);
                    sb.append(" subId: ");
                    sb.append(intExtra2);
                    Log.d("KeyguardUpdateMonitor", sb.toString());
                }
                KeyguardUpdateMonitor.this.mPLMNBySubIdList.put(intExtra2, booleanExtra ? stringExtra : null);
                if (KeyguardUpdateMonitor.DEBUG) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("PLMN: ");
                    if (booleanExtra) {
                        str = stringExtra;
                    }
                    sb2.append(str);
                    sb2.append(" subId: ");
                    sb2.append(intExtra2);
                    Log.d("KeyguardUpdateMonitor", sb2.toString());
                }
                if (!KeyguardUpdateMonitor.this.mInvalidCards.isEmpty()) {
                    Log.d("KeyguardUpdateMonitor", "Received SPN updates when invalid card exists, intent: " + intent);
                    InvalidCardData invalidCardData = (InvalidCardData) KeyguardUpdateMonitor.this.mInvalidCards.get(Integer.valueOf(intExtra2));
                    if (invalidCardData != null) {
                        if (!booleanExtra) {
                            stringExtra = "";
                        }
                        invalidCardData.plmn = stringExtra;
                        for (int i = 0; i < KeyguardUpdateMonitor.this.mCallbacks.size(); i++) {
                            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) KeyguardUpdateMonitor.this.mCallbacks.get(i)).get();
                            if (keyguardUpdateMonitorCallback != null) {
                                keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
                            }
                        }
                    }
                }
            }
        }
    };
    private String[] mSprintSubIMSIs;
    /* access modifiers changed from: private */
    public int mStatusBarState;
    private final StatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStateControllerListener;
    /* access modifiers changed from: private */
    public boolean mStowed = false;
    private final SensorEventListener mStowedListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            synchronized (KeyguardUpdateMonitor.mFPSGateLock) {
                float[] fArr = sensorEvent.values;
                if (fArr[0] > 1.0f) {
                    boolean unused = KeyguardUpdateMonitor.this.mStowed = fArr[0] != 4.0f;
                } else {
                    boolean unused2 = KeyguardUpdateMonitor.this.mStowed = fArr[0] == 1.0f;
                }
                if (!KeyguardUpdateMonitor.this.mStowed) {
                    if (KeyguardUpdateMonitor.this.mFPSGatedByStowed) {
                        Log.d("KeyguardUpdateMonitor", "STOWED cleared, reenabling FPS");
                        boolean unused3 = KeyguardUpdateMonitor.this.mFPSGatedByStowed = false;
                        KeyguardUpdateMonitor.this.updateFingerprintListeningState();
                    }
                } else if (!KeyguardUpdateMonitor.this.mFPSGatedByStowed && (KeyguardUpdateMonitor.mFingerprintUnlockAttempts >= 2 || KeyguardUpdateMonitor.mFingerprintUnlockErrors >= 2)) {
                    Log.d("KeyguardUpdateMonitor", "FPS disabeld.  GATED BY STOWED");
                    boolean unused4 = KeyguardUpdateMonitor.this.mFPSGatedByStowed = true;
                    KeyguardUpdateMonitor.this.updateFingerprintListeningState();
                }
            }
        }
    };
    private Sensor mStowedSensor;
    private StrongAuthTracker mStrongAuthTracker;
    private int mSubIdSkipped = -1;
    private List<SubscriptionInfo> mSubscriptionInfo;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        public void onSubscriptionsChanged() {
            KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(328);
            KeyguardUpdateMonitor.this.updateSimPinLanguage();
        }
    };
    private SubscriptionManager mSubscriptionManager;
    private boolean mSwitchingUser;
    private final TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() {
        public void onTaskStackChangedBackground() {
            try {
                ActivityTaskManager.RootTaskInfo rootTaskInfo = ActivityTaskManager.getService().getRootTaskInfo(0, 4);
                if (rootTaskInfo != null) {
                    KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(335, Boolean.valueOf(rootTaskInfo.visible)));
                }
            } catch (RemoteException e) {
                Log.e("KeyguardUpdateMonitor", "unable to check task stack", e);
            }
        }
    };
    @VisibleForTesting
    protected boolean mTelephonyCapable;
    private final TelephonyListenerManager mTelephonyListenerManager;
    private TelephonyManager mTelephonyManager;
    private ContentObserver mTimeFormatChangeObserver;
    private String[] mTmoSubIMSIs;
    private String[] mTmoSubIMSIsGid1;
    private TrustManager mTrustManager;
    private Runnable mUpdateBiometricListeningState = new KeyguardUpdateMonitor$$ExternalSyntheticLambda7(this);
    private boolean mUseMotoFaceUnlock;
    @VisibleForTesting
    SparseArray<BiometricAuthenticated> mUserFaceAuthenticated = new SparseArray<>();
    private SparseBooleanArray mUserFaceUnlockRunning = new SparseBooleanArray();
    @VisibleForTesting
    SparseArray<BiometricAuthenticated> mUserFingerprintAuthenticated = new SparseArray<>();
    private SparseBooleanArray mUserHasTrust = new SparseBooleanArray();
    private SparseBooleanArray mUserIsUnlocked = new SparseBooleanArray();
    private UserManager mUserManager;
    private final UserSwitchObserver mUserSwitchObserver = new UserSwitchObserver() {
        public void onUserSwitching(int i, IRemoteCallback iRemoteCallback) {
            KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(310, i, 0, iRemoteCallback));
        }

        public void onUserSwitchComplete(int i) throws RemoteException {
            KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(314, i, 0));
        }
    };
    private SparseBooleanArray mUserTrustIsManaged = new SparseBooleanArray();
    private SparseBooleanArray mUserTrustIsUsuallyManaged = new SparseBooleanArray();
    private final Vibrator mVibrator;
    private boolean mWakingUp;

    private static class MotoSurveyIntent {
        public static boolean sInstalled = false;
    }

    private boolean containsFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    public static boolean isSimPinOrPuk(int i) {
        return i == 2 || i == 3;
    }

    public static boolean isSimPinSecure(int i) {
        return i == 2 || i == 3 || i == 7;
    }

    @VisibleForTesting
    public void playAcquiredHaptic() {
    }

    static /* synthetic */ int access$1308(KeyguardUpdateMonitor keyguardUpdateMonitor) {
        int i = keyguardUpdateMonitor.mHardwareFingerprintUnavailableRetryCount;
        keyguardUpdateMonitor.mHardwareFingerprintUnavailableRetryCount = i + 1;
        return i;
    }

    static {
        boolean z = Build.IS_DEBUGGABLE;
        DEBUG_FACE = z;
        DEBUG_FINGERPRINT = z;
        try {
            CORE_APPS_ONLY = IPackageManager.Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public String getBroadcastPLMNForSlot(int i) {
        return this.mPLMNList.get(i);
    }

    public String getBroadcastSPNForSubId(int i) {
        return this.mSPNBySubIdList.get(i);
    }

    public String getBroadcastPLMNForSubId(int i) {
        return this.mPLMNBySubIdList.get(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        Log.e("KeyguardUpdateMonitor", "Fp cancellation not received, transitioning to STOPPED");
        this.mFingerprintRunningState = 0;
        updateFingerprintListeningState();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        Log.e("KeyguardUpdateMonitor", "Face cancellation not received, transitioning to STOPPED");
        this.mFaceRunningState = 0;
        updateFaceListeningState();
    }

    public int getSkippedSubId() {
        return this.mSubIdSkipped;
    }

    public void setSkippedSubId(int i) {
        this.mSubIdSkipped = i;
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "Sim lock skipped: " + i);
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(500, i, 0));
    }

    /* access modifiers changed from: private */
    public void sendSimLockSkipped(int i) {
        Intent intent = new Intent();
        intent.setPackage("com.motorola.msimsettings");
        intent.setAction("com.motorola.internal.intent.action.SIM_LOCK_SKIPPED");
        intent.putExtra("subId", i);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    public boolean isSubIdSkipped(int i) {
        if (i == this.mSubIdSkipped && i != -1) {
            return true;
        }
        if (IccCardConstants.State.intToState(getSimState(i)) == IccCardConstants.State.READY || !applyEnterpriseSimPinLockPolicy(i)) {
            return false;
        }
        return true;
    }

    private boolean applyEnterpriseSimPinLockPolicy(int i) {
        try {
            return ((MotoDevicePolicyManager) this.mContext.getSystemService("mot_device_policy")).applySimPinLockPolicy(SubscriptionManager.getSlotIndex(i));
        } catch (Exception e) {
            Log.e(KeyguardUpdateMonitor.class.getSimpleName(), "Fail invoking applySimPinLockPolicy", e);
            return false;
        }
    }

    public void clearSkippedSubId() {
        this.mSubIdSkipped = -1;
    }

    public int getLockedSimCount() {
        int i = 0;
        for (SubscriptionInfo subscriptionId : getSubscriptionInfo(false)) {
            if (IccCardConstants.State.intToState(getSimState(subscriptionId.getSubscriptionId())).isPinLocked()) {
                i++;
            }
        }
        return i;
    }

    public int getValidSimCount() {
        int i = 0;
        for (SubscriptionInfo subscriptionId : getSubscriptionInfo(false)) {
            IccCardConstants.State intToState = IccCardConstants.State.intToState(getSimState(subscriptionId.getSubscriptionId()));
            if (intToState == IccCardConstants.State.PIN_REQUIRED || intToState == IccCardConstants.State.PUK_REQUIRED || intToState == IccCardConstants.State.READY || intToState == IccCardConstants.State.NOT_READY || intToState == IccCardConstants.State.UNKNOWN) {
                i++;
            }
        }
        return i;
    }

    public IccCardConstants.State getFirstUnSkippedLockedSIMState(boolean z) {
        List<SubscriptionInfo> subscriptionInfo = getSubscriptionInfo(false);
        IccCardConstants.State state = IccCardConstants.State.UNKNOWN;
        int i = Integer.MAX_VALUE;
        for (int i2 = 0; i2 < subscriptionInfo.size(); i2++) {
            int subscriptionId = subscriptionInfo.get(i2).getSubscriptionId();
            int slotIndex = SubscriptionManager.getSlotIndex(subscriptionId);
            IccCardConstants.State intToState = IccCardConstants.State.intToState(getSimState(subscriptionId));
            if ((intToState == IccCardConstants.State.PIN_REQUIRED || (intToState == IccCardConstants.State.PUK_REQUIRED && z)) && i > slotIndex && SubscriptionManager.isValidSubscriptionId(subscriptionId) && !isSubIdSkipped(subscriptionId)) {
                i = slotIndex;
                state = intToState;
            }
        }
        return state;
    }

    private boolean isUserChangedLocale(Context context) {
        return !SystemProperties.get("persist.sys.locale").equals(SystemProperties.get("ro.product.locale"));
    }

    public boolean isSimLocalePresent(TelephonyManager telephonyManager) {
        IActivityManager iActivityManager = ActivityManagerNative.getDefault();
        try {
            String localeFromDefaultSim = telephonyManager.getLocaleFromDefaultSim();
            Log.i("KeyguardUpdateMonitor", "isSimLocalePresent simLangauage read from sim is:" + localeFromDefaultSim + "; Sim state:" + telephonyManager.getSimState());
            if (localeFromDefaultSim == null) {
                return false;
            }
            Configuration configuration = iActivityManager.getConfiguration();
            configuration.setLocale(Locale.forLanguageTag(localeFromDefaultSim));
            configuration.userSetLocale = true;
            iActivityManager.updatePersistentConfiguration(configuration);
            return true;
        } catch (RemoteException unused) {
            Log.w("KeyguardUpdateMonitor", "isSimLocalePresent RemoteException");
            return false;
        }
    }

    @VisibleForTesting
    static class BiometricAuthenticated {
        /* access modifiers changed from: private */
        public final boolean mAuthenticated;
        /* access modifiers changed from: private */
        public final boolean mIsStrongBiometric;

        BiometricAuthenticated(boolean z, boolean z2) {
            this.mAuthenticated = z;
            this.mIsStrongBiometric = z2;
        }
    }

    public static synchronized void setCurrentUser(int i) {
        synchronized (KeyguardUpdateMonitor.class) {
            sCurrentUser = i;
        }
    }

    public static synchronized int getCurrentUser() {
        int i;
        synchronized (KeyguardUpdateMonitor.class) {
            i = sCurrentUser;
        }
        return i;
    }

    public boolean hasSIM() {
        for (int i = 0; i < this.mSimExist.size(); i++) {
            if (this.mSimExist.valueAt(i)) {
                return true;
            }
        }
        return false;
    }

    public void onTrustChanged(boolean z, int i, int i2) {
        Assert.isMainThread();
        this.mUserHasTrust.put(i, z);
        updateBiometricListeningState();
        for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i3).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustChanged(i);
                if (z && i2 != 0) {
                    keyguardUpdateMonitorCallback.onTrustGrantedWithFlags(i2, i);
                }
            }
        }
    }

    public void onTrustError(CharSequence charSequence) {
        dispatchErrorMessage(charSequence);
    }

    /* access modifiers changed from: private */
    public void handleSimSubscriptionInfoChanged() {
        Assert.isMainThread();
        Log.v("KeyguardUpdateMonitor", "onSubscriptionInfoChanged()");
        List<SubscriptionInfo> completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : completeActiveSubscriptionInfoList) {
                Log.v("KeyguardUpdateMonitor", "SubInfo:" + subscriptionInfo);
            }
        } else {
            Log.v("KeyguardUpdateMonitor", "onSubscriptionInfoChanged: list is null");
        }
        List<SubscriptionInfo> subscriptionInfo2 = getSubscriptionInfo(true);
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        for (int i = 0; i < subscriptionInfo2.size(); i++) {
            SubscriptionInfo subscriptionInfo3 = subscriptionInfo2.get(i);
            hashSet.add(Integer.valueOf(subscriptionInfo3.getSubscriptionId()));
            if (refreshSimState(subscriptionInfo3.getSubscriptionId(), subscriptionInfo3.getSimSlotIndex())) {
                arrayList.add(subscriptionInfo3);
            }
        }
        Iterator<Map.Entry<Integer, SimData>> it = this.mSimDatas.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry next = it.next();
            if (!hashSet.contains(next.getKey())) {
                Log.i("KeyguardUpdateMonitor", "Previously active sub id " + next.getKey() + " is now invalid, will remove");
                it.remove();
                SimData simData = (SimData) next.getValue();
                for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
                    KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
                    if (keyguardUpdateMonitorCallback != null) {
                        keyguardUpdateMonitorCallback.onSimStateChanged(simData.subId, simData.slotId, simData.simState);
                    }
                }
            }
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SimData simData2 = this.mSimDatas.get(Integer.valueOf(((SubscriptionInfo) arrayList.get(i3)).getSubscriptionId()));
            for (int i4 = 0; i4 < this.mCallbacks.size(); i4++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback2 = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i4).get();
                if (keyguardUpdateMonitorCallback2 != null) {
                    keyguardUpdateMonitorCallback2.onSimStateChanged(simData2.subId, simData2.slotId, simData2.simState);
                }
            }
        }
        callbacksRefreshCarrierInfo();
    }

    /* access modifiers changed from: private */
    public void handleAirplaneModeChanged() {
        callbacksRefreshCarrierInfo();
    }

    private void callbacksRefreshCarrierInfo() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
            }
        }
    }

    public List<SubscriptionInfo> getSubscriptionInfo(boolean z) {
        List<SubscriptionInfo> list = this.mSubscriptionInfo;
        if (list == null || z) {
            list = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        }
        if (list == null) {
            this.mSubscriptionInfo = new ArrayList();
        } else {
            this.mSubscriptionInfo = list;
        }
        return new ArrayList(this.mSubscriptionInfo);
    }

    public List<SubscriptionInfo> getFilteredSubscriptionInfo(boolean z) {
        List<SubscriptionInfo> subscriptionInfo = getSubscriptionInfo(false);
        if (subscriptionInfo.size() == 2) {
            SubscriptionInfo subscriptionInfo2 = subscriptionInfo.get(0);
            SubscriptionInfo subscriptionInfo3 = subscriptionInfo.get(1);
            if (subscriptionInfo2.getGroupUuid() == null || !subscriptionInfo2.getGroupUuid().equals(subscriptionInfo3.getGroupUuid()) || (!subscriptionInfo2.isOpportunistic() && !subscriptionInfo3.isOpportunistic())) {
                return subscriptionInfo;
            }
            if (CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean")) {
                if (!subscriptionInfo2.isOpportunistic()) {
                    subscriptionInfo2 = subscriptionInfo3;
                }
                subscriptionInfo.remove(subscriptionInfo2);
            } else {
                if (subscriptionInfo2.getSubscriptionId() == this.mActiveMobileDataSubscription) {
                    subscriptionInfo2 = subscriptionInfo3;
                }
                subscriptionInfo.remove(subscriptionInfo2);
            }
        }
        return subscriptionInfo;
    }

    public void onTrustManagedChanged(boolean z, int i) {
        Assert.isMainThread();
        this.mUserTrustIsManaged.put(i, z);
        this.mUserTrustIsUsuallyManaged.put(i, this.mTrustManager.isTrustUsuallyManaged(i));
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustManagedChanged(i);
            }
        }
    }

    public void setCredentialAttempted() {
        this.mCredentialAttempted = true;
        updateBiometricListeningState();
    }

    public void setKeyguardGoingAway(boolean z) {
        this.mKeyguardGoingAway = z;
        updateBiometricListeningState();
    }

    public void setKeyguardOccluded(boolean z) {
        this.mKeyguardOccluded = z;
        updateBiometricListeningState();
    }

    public void requestFaceAuthOnOccludingApp(boolean z) {
        this.mOccludingAppRequestingFace = z;
        updateFaceListeningState();
    }

    public void requestFingerprintAuthOnOccludingApp(boolean z) {
        this.mOccludingAppRequestingFp = z;
        updateFingerprintListeningState();
    }

    public void onCameraLaunched() {
        this.mSecureCameraLaunched = true;
        updateBiometricListeningState();
    }

    public boolean isDreaming() {
        return this.mIsDreaming;
    }

    public void awakenFromDream() {
        IDreamManager iDreamManager;
        if (this.mIsDreaming && (iDreamManager = this.mDreamManager) != null) {
            try {
                iDreamManager.awaken();
            } catch (RemoteException unused) {
                Log.e("KeyguardUpdateMonitor", "Unable to awaken from dream");
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onFingerprintAuthenticated(int i, boolean z) {
        Assert.isMainThread();
        Trace.beginSection("KeyGuardUpdateMonitor#onFingerPrintAuthenticated");
        this.mUserFingerprintAuthenticated.put(i, new BiometricAuthenticated(true, z));
        if (getUserCanSkipBouncer(i)) {
            this.mTrustManager.unlockedByBiometricForUser(i, BiometricSourceType.FINGERPRINT);
        }
        this.mFingerprintCancelSignal = null;
        updateBiometricListeningState();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthenticated(i, BiometricSourceType.FINGERPRINT, z);
            }
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(336), 500);
        this.mAssistantVisible = false;
        reportSuccessfulBiometricUnlock(z, i);
        Trace.endSection();
    }

    private void reportSuccessfulBiometricUnlock(final boolean z, final int i) {
        this.mBackgroundExecutor.execute(new Runnable() {
            public void run() {
                KeyguardUpdateMonitor.this.mLockPatternUtils.reportSuccessfulBiometricUnlock(z, i);
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleFingerprintAuthFailed() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthFailed(BiometricSourceType.FINGERPRINT);
            }
        }
        int i2 = mFingerprintUnlockAttempts + 1;
        mFingerprintUnlockAttempts = i2;
        if (i2 >= 2) {
            synchronized (mFPSGateLock) {
                if (this.mStowed && !this.mFPSGatedByStowed) {
                    this.mFPSGatedByStowed = true;
                    updateFingerprintListeningState();
                    Log.d("KeyguardUpdateMonitor", "Too many failed attempts in a row.  Starting stowed listener.");
                }
            }
        }
        handleFingerprintHelp(-1, this.mContext.getString(R$string.kg_fingerprint_not_recognized));
    }

    /* access modifiers changed from: private */
    public void handleFingerprintAcquired(int i) {
        Assert.isMainThread();
        if (i == 0) {
            for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onBiometricAcquired(BiometricSourceType.FINGERPRINT);
                }
            }
        } else if (i == 1204) {
            Log.d("KeyguardUpdateMonitor", "Surpassed poor FPS quality threshold");
            synchronized (mFPSGateLock) {
                if (this.mStowed && !this.mFPSGatedByStowed) {
                    this.mFPSGatedByStowed = true;
                    updateFingerprintListeningState();
                }
            }
        } else if (i < 6) {
            int i3 = mFingerprintUnlockErrors + 1;
            mFingerprintUnlockErrors = i3;
            if (i3 >= 2) {
                synchronized (mFPSGateLock) {
                    if (this.mStowed && !this.mFPSGatedByStowed) {
                        this.mFPSGatedByStowed = true;
                        updateFingerprintListeningState();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintAuthenticated(int i, boolean z) {
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFingerPrintAuthenticated");
        try {
            int i2 = ActivityManager.getService().getCurrentUser().id;
            if (i2 != i) {
                try {
                    Log.d("KeyguardUpdateMonitor", "Fingerprint authenticated for wrong user: " + i);
                    setFingerprintRunningState(0);
                } catch (Throwable th) {
                    setFingerprintRunningState(0);
                    throw th;
                }
            } else if (isFingerprintDisabled(i2)) {
                Log.d("KeyguardUpdateMonitor", "Fingerprint disabled by DPM for userId: " + i2);
                setFingerprintRunningState(0);
            } else {
                synchronized (mFPSGateLock) {
                    Sensor sensor = this.mStowedSensor;
                    if (sensor != null && this.mFPSGatedByStowed) {
                        this.mFPSGatedByStowed = false;
                        this.mSm.unregisterListener(this.mStowedListener, sensor);
                    }
                }
                onFingerprintAuthenticated(i2, z);
                setFingerprintRunningState(0);
                Trace.endSection();
            }
        } catch (RemoteException e) {
            Log.e("KeyguardUpdateMonitor", "Failed to get current user id: ", e);
            setFingerprintRunningState(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintHelp(int i, String str) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricHelp(i, str, BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintError(int i, String str) {
        Assert.isMainThread();
        if (this.mHandler.hasCallbacks(this.mFpCancelNotReceived)) {
            this.mHandler.removeCallbacks(this.mFpCancelNotReceived);
        }
        this.mFingerprintCancelSignal = null;
        if (i == 5 && this.mFingerprintRunningState == 3) {
            setFingerprintRunningState(0);
            updateFingerprintListeningState();
        } else {
            setFingerprintRunningState(0);
        }
        if (i == 1) {
            this.mHandler.postDelayed(this.mRetryFingerprintAuthentication, 500);
        }
        if (i == 9) {
            this.mFingerprintLockedOutPermanent = true;
            requireStrongAuthIfAllLockedOut();
        }
        if (i == 7 || i == 9) {
            this.mFingerprintLockedOut = true;
            if (isUdfpsEnrolled()) {
                updateFingerprintListeningState();
            }
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricError(i, str, BiometricSourceType.FINGERPRINT);
            }
        }
        if (i != 5) {
            mFingerprintUnlockErrors++;
        }
        if (mFingerprintUnlockErrors >= 2) {
            synchronized (mFPSGateLock) {
                if (this.mStowed && !this.mFPSGatedByStowed) {
                    this.mFPSGatedByStowed = true;
                    updateFingerprintListeningState();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFingerprintLockoutReset() {
        this.mFingerprintLockedOut = false;
        this.mFingerprintLockedOutPermanent = false;
        if (isUdfpsEnrolled()) {
            this.mHandler.postDelayed(new KeyguardUpdateMonitor$$ExternalSyntheticLambda6(this), 600);
        } else {
            updateFingerprintListeningState();
        }
    }

    private void setFingerprintRunningState(int i) {
        boolean z = false;
        boolean z2 = this.mFingerprintRunningState == 1;
        if (i == 1) {
            z = true;
        }
        this.mFingerprintRunningState = i;
        Log.d("KeyguardUpdateMonitor", "fingerprintRunningState: " + this.mFingerprintRunningState);
        if (z2 != z) {
            notifyFingerprintRunningStateChanged();
        }
    }

    private void notifyFingerprintRunningStateChanged() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricRunningStateChanged(isFingerprintDetectionRunning(), BiometricSourceType.FINGERPRINT);
            }
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onFaceAuthenticated(int i, boolean z) {
        Trace.beginSection("KeyGuardUpdateMonitor#onFaceAuthenticated");
        Assert.isMainThread();
        this.mUserFaceAuthenticated.put(i, new BiometricAuthenticated(true, z));
        if (getUserCanSkipBouncer(i)) {
            this.mTrustManager.unlockedByBiometricForUser(i, BiometricSourceType.FACE);
        }
        this.mFaceCancelSignal = null;
        updateBiometricListeningState();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthenticated(i, BiometricSourceType.FACE, z);
            }
        }
        this.mAssistantVisible = false;
        reportSuccessfulBiometricUnlock(z, i);
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public void handleFaceAuthFailed() {
        Assert.isMainThread();
        this.mFaceCancelSignal = null;
        setFaceRunningState(0);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricAuthFailed(BiometricSourceType.FACE);
            }
        }
        handleFaceHelp(-2, this.mContext.getString(R$string.kg_face_not_recognized));
    }

    /* access modifiers changed from: private */
    public void handleFaceAcquired(int i) {
        Assert.isMainThread();
        if (i == 0) {
            if (DEBUG_FACE) {
                Log.d("KeyguardUpdateMonitor", "Face acquired");
            }
            for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onBiometricAcquired(BiometricSourceType.FACE);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceAuthenticated(int i, boolean z) {
        Trace.beginSection("KeyGuardUpdateMonitor#handlerFaceAuthenticated");
        try {
            if (this.mGoingToSleep) {
                Log.d("KeyguardUpdateMonitor", "Aborted successful auth because device is going to sleep.");
                return;
            }
            int i2 = ActivityManager.getService().getCurrentUser().id;
            if (i2 != i) {
                Log.d("KeyguardUpdateMonitor", "Face authenticated for wrong user: " + i);
            } else if (isFaceDisabled(i2)) {
                Log.d("KeyguardUpdateMonitor", "Face authentication disabled by DPM for userId: " + i2);
                setFaceRunningState(0);
            } else {
                if (DEBUG_FACE) {
                    Log.d("KeyguardUpdateMonitor", "Face auth succeeded for user " + i2);
                }
                onFaceAuthenticated(i2, z);
                setFaceRunningState(0);
                Trace.endSection();
            }
        } catch (RemoteException e) {
            Log.e("KeyguardUpdateMonitor", "Failed to get current user id: ", e);
        } finally {
            setFaceRunningState(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceHelp(int i, String str) {
        Assert.isMainThread();
        if (DEBUG_FACE) {
            Log.d("KeyguardUpdateMonitor", "Face help received: " + str);
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricHelp(i, str, BiometricSourceType.FACE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceError(int i, String str) {
        int i2;
        Assert.isMainThread();
        if (DEBUG_FACE) {
            Log.d("KeyguardUpdateMonitor", "Face error received: " + str);
        }
        if (this.mHandler.hasCallbacks(this.mFaceCancelNotReceived)) {
            this.mHandler.removeCallbacks(this.mFaceCancelNotReceived);
        }
        this.mFaceCancelSignal = null;
        if (i == 5 && this.mFaceRunningState == 3) {
            setFaceRunningState(0);
            updateFaceListeningState();
        } else {
            setFaceRunningState(0);
        }
        if ((i == 1 || i == 2) && (i2 = this.mHardwareFaceUnavailableRetryCount) < 20) {
            this.mHardwareFaceUnavailableRetryCount = i2 + 1;
            this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
            this.mHandler.postDelayed(this.mRetryFaceAuthentication, 500);
        }
        if (i == 9) {
            this.mFaceLockedOutPermanent = true;
            requireStrongAuthIfAllLockedOut();
        }
        for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i3).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricError(i, str, BiometricSourceType.FACE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceLockoutReset() {
        this.mFaceLockedOutPermanent = false;
        updateFaceListeningState();
    }

    private void setFaceRunningState(int i) {
        boolean z = false;
        boolean z2 = this.mFaceRunningState == 1;
        if (i == 1) {
            z = true;
        }
        this.mFaceRunningState = i;
        Log.d("KeyguardUpdateMonitor", "faceRunningState: " + this.mFaceRunningState);
        if (z2 != z) {
            notifyFaceRunningStateChanged();
        }
    }

    private void notifyFaceRunningStateChanged() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricRunningStateChanged(isFaceDetectionRunning(), BiometricSourceType.FACE);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleFaceUnlockStateChanged(boolean z, int i) {
        Assert.isMainThread();
        this.mUserFaceUnlockRunning.put(i, z);
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onFaceUnlockStateChanged(z, i);
            }
        }
    }

    public boolean isFingerprintDetectionRunning() {
        return this.mFingerprintRunningState == 1;
    }

    public boolean isFaceDetectionRunning() {
        return this.mFaceRunningState == 1;
    }

    private boolean isTrustDisabled(int i) {
        return isSimPinSecure();
    }

    private boolean isFingerprintDisabled(int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        return !(devicePolicyManager == null || (devicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null, i) & 32) == 0) || isSimPinSecure();
    }

    /* access modifiers changed from: private */
    public boolean isFaceDisabled(int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        if (this.mUseMotoFaceUnlock) {
            if (devicePolicyManager != null) {
                try {
                    if (devicePolicyManager.getAggregatedPasswordComplexityForUser(i, false) > 65536) {
                        return true;
                    }
                } catch (SecurityException e) {
                    Log.e("KeyguardUpdateMonitor", "isFaceDisabled error:", e);
                }
            }
            if (isCameraGestureTriggered()) {
                if (DEBUG) {
                    Log.d("KeyguardUpdateMonitor", "CameraGestureTriggered, face disabled ");
                }
                return true;
            }
        }
        return ((Boolean) DejankUtils.whitelistIpcs(new KeyguardUpdateMonitor$$ExternalSyntheticLambda18(this, devicePolicyManager, i))).booleanValue();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$isFaceDisabled$2(DevicePolicyManager devicePolicyManager, int i) {
        return Boolean.valueOf(!(devicePolicyManager == null || (devicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null, i) & 128) == 0) || isSimPinSecure());
    }

    private boolean getIsFaceAuthenticated() {
        BiometricAuthenticated biometricAuthenticated = this.mUserFaceAuthenticated.get(getCurrentUser());
        if (biometricAuthenticated != null) {
            return biometricAuthenticated.mAuthenticated;
        }
        return false;
    }

    private void requireStrongAuthIfAllLockedOut() {
        boolean z = true;
        boolean z2 = (this.mFaceLockedOutPermanent || !shouldListenForFace()) && !getIsFaceAuthenticated();
        if (!this.mFingerprintLockedOutPermanent && shouldListenForFingerprint(isUdfpsEnrolled())) {
            z = false;
        }
        if (z2 && z) {
            Log.d("KeyguardUpdateMonitor", "All biometrics locked out - requiring strong auth");
            this.mLockPatternUtils.requireStrongAuth(8, getCurrentUser());
        }
    }

    public boolean getUserCanSkipBouncer(int i) {
        return getUserHasTrust(i) || getUserUnlockedWithBiometric(i);
    }

    public boolean getUserHasTrust(int i) {
        return !isTrustDisabled(i) && this.mUserHasTrust.get(i);
    }

    public boolean getUserUnlockedWithBiometric(int i) {
        BiometricAuthenticated biometricAuthenticated = this.mUserFingerprintAuthenticated.get(i);
        BiometricAuthenticated biometricAuthenticated2 = this.mUserFaceAuthenticated.get(i);
        boolean z = biometricAuthenticated != null && biometricAuthenticated.mAuthenticated && isUnlockingWithBiometricAllowed(biometricAuthenticated.mIsStrongBiometric);
        boolean z2 = biometricAuthenticated2 != null && biometricAuthenticated2.mAuthenticated && isUnlockingWithBiometricAllowed(biometricAuthenticated2.mIsStrongBiometric);
        if (z || z2) {
            return true;
        }
        return false;
    }

    public boolean getUserTrustIsManaged(int i) {
        return this.mUserTrustIsManaged.get(i) && !isTrustDisabled(i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0089 A[LOOP:0: B:16:0x0089->B:21:0x00a4, LOOP_START, PHI: r3 
      PHI: (r3v1 int) = (r3v0 int), (r3v2 int) binds: [B:15:0x0087, B:21:0x00a4] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:25:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateSecondaryLockscreenRequirement(int r6) {
        /*
            r5 = this;
            java.util.Map<java.lang.Integer, android.content.Intent> r0 = r5.mSecondaryLockscreenRequirement
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            java.lang.Object r0 = r0.get(r1)
            android.content.Intent r0 = (android.content.Intent) r0
            android.app.admin.DevicePolicyManager r1 = r5.mDevicePolicyManager
            android.os.UserHandle r2 = android.os.UserHandle.of(r6)
            boolean r1 = r1.isSecondaryLockscreenEnabled(r2)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0077
            if (r0 != 0) goto L_0x0077
            android.app.admin.DevicePolicyManager r0 = r5.mDevicePolicyManager
            android.os.UserHandle r1 = android.os.UserHandle.of(r6)
            android.content.ComponentName r0 = r0.getProfileOwnerOrDeviceOwnerSupervisionComponent(r1)
            if (r0 != 0) goto L_0x003f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "No Profile Owner or Device Owner supervision app found for User "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "KeyguardUpdateMonitor"
            android.util.Log.e(r1, r0)
            goto L_0x0086
        L_0x003f:
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r4 = "android.app.action.BIND_SECONDARY_LOCKSCREEN_SERVICE"
            r1.<init>(r4)
            java.lang.String r0 = r0.getPackageName()
            android.content.Intent r0 = r1.setPackage(r0)
            android.content.Context r1 = r5.mContext
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            android.content.pm.ResolveInfo r0 = r1.resolveService(r0, r3)
            if (r0 == 0) goto L_0x0086
            android.content.pm.ServiceInfo r1 = r0.serviceInfo
            if (r1 == 0) goto L_0x0086
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            android.content.pm.ServiceInfo r0 = r0.serviceInfo
            android.content.ComponentName r0 = r0.getComponentName()
            android.content.Intent r0 = r1.setComponent(r0)
            java.util.Map<java.lang.Integer, android.content.Intent> r1 = r5.mSecondaryLockscreenRequirement
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)
            r1.put(r4, r0)
            goto L_0x0087
        L_0x0077:
            if (r1 != 0) goto L_0x0086
            if (r0 == 0) goto L_0x0086
            java.util.Map<java.lang.Integer, android.content.Intent> r0 = r5.mSecondaryLockscreenRequirement
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r4 = 0
            r0.put(r1, r4)
            goto L_0x0087
        L_0x0086:
            r2 = r3
        L_0x0087:
            if (r2 == 0) goto L_0x00a7
        L_0x0089:
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r0 = r5.mCallbacks
            int r0 = r0.size()
            if (r3 >= r0) goto L_0x00a7
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r0 = r5.mCallbacks
            java.lang.Object r0 = r0.get(r3)
            java.lang.ref.WeakReference r0 = (java.lang.ref.WeakReference) r0
            java.lang.Object r0 = r0.get()
            com.android.keyguard.KeyguardUpdateMonitorCallback r0 = (com.android.keyguard.KeyguardUpdateMonitorCallback) r0
            if (r0 == 0) goto L_0x00a4
            r0.onSecondaryLockscreenRequirementChanged(r6)
        L_0x00a4:
            int r3 = r3 + 1
            goto L_0x0089
        L_0x00a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.updateSecondaryLockscreenRequirement(int):void");
    }

    public Intent getSecondaryLockscreenRequirement(int i) {
        return this.mSecondaryLockscreenRequirement.get(Integer.valueOf(i));
    }

    public boolean isTrustUsuallyManaged(int i) {
        Assert.isMainThread();
        return this.mUserTrustIsUsuallyManaged.get(i);
    }

    public boolean isUnlockingWithBiometricAllowed(boolean z) {
        return this.mStrongAuthTracker.isUnlockingWithBiometricAllowed(z);
    }

    public boolean isUserInLockdown(int i) {
        return containsFlag(this.mStrongAuthTracker.getStrongAuthForUser(i), 32);
    }

    private boolean isEncryptedOrLockdown(int i) {
        int strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(i);
        boolean z = containsFlag(strongAuthForUser, 2) || containsFlag(strongAuthForUser, 32);
        if (containsFlag(strongAuthForUser, 1) || z) {
            return true;
        }
        return false;
    }

    public boolean isEncrypted(int i) {
        return containsFlag(this.mStrongAuthTracker.getStrongAuthForUser(i), 1);
    }

    public boolean userNeedsStrongAuth() {
        return (this.mStrongAuthTracker.getStrongAuthForUser(getCurrentUser()) == 0 || this.mStrongAuthTracker.getStrongAuthForUser(getCurrentUser()) == 4) ? false : true;
    }

    public boolean needsSlowUnlockTransition() {
        return this.mNeedsSlowUnlockTransition;
    }

    public StrongAuthTracker getStrongAuthTracker() {
        return this.mStrongAuthTracker;
    }

    /* access modifiers changed from: private */
    public void notifyStrongAuthStateChanged(int i) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStrongAuthStateChanged(i);
            }
        }
    }

    public boolean isScreenOn() {
        return this.mScreenOn;
    }

    private void dispatchErrorMessage(CharSequence charSequence) {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTrustAgentErrorMessage(charSequence);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setAssistantVisible(boolean z) {
        this.mAssistantVisible = z;
        updateBiometricListeningState();
    }

    static class DisplayClientState {
        DisplayClientState() {
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(int i, int i2, boolean z) {
        handleFingerprintAuthenticated(i2, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(int i, int i2, boolean z) {
        handleFaceAuthenticated(i2, z);
    }

    private static class SimData {
        public int simState;
        public int slotId;
        public int subId;

        SimData(int i, int i2, int i3) {
            this.simState = i;
            this.slotId = i2;
            this.subId = i3;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00d6, code lost:
            if ("IMSI".equals(r0) == false) goto L_0x00d9;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static com.android.keyguard.KeyguardUpdateMonitor.SimData fromIntent(android.content.Intent r11) {
            /*
                java.lang.String r0 = r11.getAction()
                java.lang.String r1 = "android.intent.action.SIM_STATE_CHANGED"
                boolean r0 = r1.equals(r0)
                if (r0 != 0) goto L_0x0021
                java.lang.String r0 = r11.getAction()
                java.lang.String r1 = "com.motorola.keyguard.ACTION_SIM_PIN_UNLOCK"
                boolean r0 = r1.equals(r0)
                if (r0 == 0) goto L_0x0019
                goto L_0x0021
            L_0x0019:
                java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
                java.lang.String r0 = "only handles intent ACTION_SIM_STATE_CHANGED"
                r11.<init>(r0)
                throw r11
            L_0x0021:
                java.lang.String r0 = "ss"
                java.lang.String r0 = r11.getStringExtra(r0)
                java.lang.String r1 = "android.telephony.extra.SLOT_INDEX"
                r2 = 0
                int r1 = r11.getIntExtra(r1, r2)
                r3 = -1
                java.lang.String r4 = "android.telephony.extra.SUBSCRIPTION_INDEX"
                int r3 = r11.getIntExtra(r4, r3)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "ACTION_SIM_STATE_CHANGED intent received on slotId = "
                r4.append(r5)
                r4.append(r1)
                java.lang.String r5 = "  state = "
                r4.append(r5)
                r4.append(r0)
                java.lang.String r5 = "  subId = "
                r4.append(r5)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                java.lang.String r5 = "KeyguardUpdateMonitor"
                android.util.Log.d(r5, r4)
                java.lang.String r4 = "ABSENT"
                boolean r4 = r4.equals(r0)
                r5 = 8
                r6 = 7
                r7 = 5
                java.lang.String r8 = "PERM_DISABLED"
                java.lang.String r9 = "reason"
                if (r4 == 0) goto L_0x007c
                java.lang.String r11 = r11.getStringExtra(r9)
                boolean r11 = r8.equals(r11)
                if (r11 == 0) goto L_0x0079
            L_0x0076:
                r2 = r6
                goto L_0x00d9
            L_0x0079:
                r11 = 1
            L_0x007a:
                r2 = r11
                goto L_0x00d9
            L_0x007c:
                java.lang.String r4 = "READY"
                boolean r4 = r4.equals(r0)
                if (r4 == 0) goto L_0x0086
            L_0x0084:
                r2 = r7
                goto L_0x00d9
            L_0x0086:
                java.lang.String r4 = "CARD_IO_ERROR"
                boolean r10 = r4.equals(r0)
                if (r10 == 0) goto L_0x0090
            L_0x008e:
                r2 = r5
                goto L_0x00d9
            L_0x0090:
                java.lang.String r10 = "LOCKED"
                boolean r10 = r10.equals(r0)
                if (r10 == 0) goto L_0x00c1
                java.lang.String r11 = r11.getStringExtra(r9)
                java.lang.String r0 = "PIN"
                boolean r0 = r0.equals(r11)
                if (r0 == 0) goto L_0x00a6
                r11 = 2
                goto L_0x007a
            L_0x00a6:
                java.lang.String r0 = "PUK"
                boolean r0 = r0.equals(r11)
                if (r0 == 0) goto L_0x00b0
                r11 = 3
                goto L_0x007a
            L_0x00b0:
                boolean r0 = r8.equals(r11)
                if (r0 == 0) goto L_0x00b7
                goto L_0x0076
            L_0x00b7:
                java.lang.String r0 = "NETWORK"
                boolean r11 = r0.equals(r11)
                if (r11 == 0) goto L_0x00d9
                r11 = 4
                goto L_0x007a
            L_0x00c1:
                boolean r11 = r4.equals(r0)
                if (r11 == 0) goto L_0x00c8
                goto L_0x008e
            L_0x00c8:
                java.lang.String r11 = "LOADED"
                boolean r11 = r11.equals(r0)
                if (r11 != 0) goto L_0x0084
                java.lang.String r11 = "IMSI"
                boolean r11 = r11.equals(r0)
                if (r11 == 0) goto L_0x00d9
                goto L_0x0084
            L_0x00d9:
                com.android.keyguard.KeyguardUpdateMonitor$SimData r11 = new com.android.keyguard.KeyguardUpdateMonitor$SimData
                r11.<init>(r2, r1, r3)
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.SimData.fromIntent(android.content.Intent):com.android.keyguard.KeyguardUpdateMonitor$SimData");
        }

        public String toString() {
            return "SimData{state=" + this.simState + ",slotId=" + this.slotId + ",subId=" + this.subId + "}";
        }
    }

    public static class StrongAuthTracker extends LockPatternUtils.StrongAuthTracker {
        private final Consumer<Integer> mStrongAuthRequiredChangedCallback;

        public StrongAuthTracker(Context context, Consumer<Integer> consumer) {
            super(context);
            this.mStrongAuthRequiredChangedCallback = consumer;
        }

        public boolean isUnlockingWithBiometricAllowed(boolean z) {
            return isBiometricAllowedForUser(z, KeyguardUpdateMonitor.getCurrentUser());
        }

        public boolean hasUserAuthenticatedSinceBoot() {
            return (getStrongAuthForUser(KeyguardUpdateMonitor.getCurrentUser()) & 1) == 0;
        }

        public void onStrongAuthRequiredChanged(int i) {
            this.mStrongAuthRequiredChangedCallback.accept(Integer.valueOf(i));
        }
    }

    /* access modifiers changed from: protected */
    public void handleStartedWakingUp() {
        Trace.beginSection("KeyguardUpdateMonitor#handleStartedWakingUp");
        Assert.isMainThread();
        onKeyguardDeviceInteractiveChanged();
        if (!this.mWakingUp) {
            this.mWakingUp = true;
            updateFingerprintListeningState();
        }
        updateFaceListeningState();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStartedWakingUp();
            }
        }
        Trace.endSection();
    }

    /* access modifiers changed from: protected */
    public void handleStartedGoingToSleep(int i) {
        Assert.isMainThread();
        this.mLockIconPressed = false;
        clearBiometricRecognized();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onStartedGoingToSleep(i);
            }
        }
        this.mGoingToSleep = true;
        this.mWakingUp = false;
        updateBiometricListeningState();
    }

    /* access modifiers changed from: protected */
    public void handleFinishedGoingToSleep(int i) {
        Assert.isMainThread();
        this.mGoingToSleep = false;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onFinishedGoingToSleep(i);
            }
        }
        onKeyguardDeviceInteractiveChanged();
        boolean isUnlockWithFacePossible = isUnlockWithFacePossible(getCurrentUser());
        if (this.mUseMotoFaceUnlock) {
            if (!isUnlockWithFacePossible || !this.mLTVSetting) {
                unRegisterLTVSensor();
            } else {
                registerLTVSensor();
            }
            this.mFaceUnlockAvailable = isUnlockWithFacePossible;
            setCameraGestureTriggered(false);
        }
        updateBiometricListeningState();
    }

    /* access modifiers changed from: private */
    public void handleScreenTurnedOn() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onScreenTurnedOn();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleScreenTurnedOff() {
        DejankUtils.startDetectingBlockingIpcs("KeyguardUpdateMonitor#handleScreenTurnedOff");
        Assert.isMainThread();
        this.mHardwareFingerprintUnavailableRetryCount = 0;
        this.mHardwareFaceUnavailableRetryCount = 0;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onScreenTurnedOff();
            }
        }
        DejankUtils.stopDetectingBlockingIpcs("KeyguardUpdateMonitor#handleScreenTurnedOff");
    }

    /* access modifiers changed from: private */
    public void handleDreamingStateChanged(int i) {
        Assert.isMainThread();
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mIsDreaming = z;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDreamingStateChanged(this.mIsDreaming);
            }
        }
        updateBiometricListeningState();
    }

    public void reportFingerprintUnlock(boolean z) {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "Fingerprint unlocked: " + z);
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(410, z ? 1 : 0, 0));
    }

    public void reportBackupUnlock(boolean z) {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "Backup unlocked: " + z);
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(411, z ? 1 : 0, 0));
    }

    /* access modifiers changed from: private */
    public void handleFingerprintUnlock(String str, boolean z) {
        if (MotoSurveyIntent.sInstalled) {
            Intent intent = new Intent();
            intent.setPackage("com.motorola.survey");
            intent.setAction("com.motorola.internal.intent.action.KEYGUARD_UNLOCK");
            intent.putExtra("type", str);
            intent.putExtra("result", z);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
        }
    }

    /* access modifiers changed from: private */
    public static boolean surveyPackageExists(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.motorola.survey", 0);
            if (applicationInfo == null || (applicationInfo.flags & 129) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void handleUserInfoChanged(int i) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserInfoChanged(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUserUnlocked(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.put(i, true);
        this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserUnlocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleUserStopped(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.put(i, this.mUserManager.isUserUnlocked(i));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void handleUserRemoved(int i) {
        Assert.isMainThread();
        this.mUserIsUnlocked.delete(i);
        this.mUserTrustIsUsuallyManaged.delete(i);
    }

    /* access modifiers changed from: private */
    public void handleKeyguardGoingAway(boolean z) {
        Assert.isMainThread();
        setKeyguardGoingAway(z);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setStrongAuthTracker(StrongAuthTracker strongAuthTracker) {
        StrongAuthTracker strongAuthTracker2 = this.mStrongAuthTracker;
        if (strongAuthTracker2 != null) {
            this.mLockPatternUtils.unregisterStrongAuthTracker(strongAuthTracker2);
        }
        this.mStrongAuthTracker = strongAuthTracker;
        this.mLockPatternUtils.registerStrongAuthTracker(strongAuthTracker);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void resetBiometricListeningState() {
        this.mFingerprintRunningState = 0;
        this.mFaceRunningState = 0;
    }

    /* access modifiers changed from: private */
    public void registerRingerTracker() {
        this.mRingerModeTracker.getRingerMode().observeForever(this.mRingerModeObserver);
    }

    @VisibleForTesting
    protected KeyguardUpdateMonitor(Context context, Looper looper, BroadcastDispatcher broadcastDispatcher, DumpManager dumpManager, RingerModeTracker ringerModeTracker, Executor executor, StatusBarStateController statusBarStateController, LockPatternUtils lockPatternUtils, AuthController authController, TelephonyListenerManager telephonyListenerManager, FeatureFlags featureFlags, Vibrator vibrator) {
        Context context2 = context;
        StatusBarStateController statusBarStateController2 = statusBarStateController;
        C06321 r3 = new StatusBarStateController.StateListener() {
            public void onStateChanged(int i) {
                int unused = KeyguardUpdateMonitor.this.mStatusBarState = i;
            }

            public void onExpandedChanged(boolean z) {
                for (int i = 0; i < KeyguardUpdateMonitor.this.mCallbacks.size(); i++) {
                    KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) ((WeakReference) KeyguardUpdateMonitor.this.mCallbacks.get(i)).get();
                    if (keyguardUpdateMonitorCallback != null) {
                        keyguardUpdateMonitorCallback.onShadeExpandedChanged(z);
                    }
                }
            }
        };
        this.mStatusBarStateControllerListener = r3;
        this.mContext = context2;
        this.mSubscriptionManager = SubscriptionManager.from(context);
        this.mTelephonyListenerManager = telephonyListenerManager;
        this.mDeviceProvisioned = isDeviceProvisionedInSettingsDb();
        this.mStrongAuthTracker = new StrongAuthTracker(context2, new KeyguardUpdateMonitor$$ExternalSyntheticLambda9(this));
        this.mBackgroundExecutor = executor;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mRingerModeTracker = ringerModeTracker;
        this.mStatusBarStateController = statusBarStateController2;
        statusBarStateController2.addCallback(r3);
        this.mStatusBarState = statusBarStateController.getState();
        this.mLockPatternUtils = lockPatternUtils;
        this.mAuthController = authController;
        dumpManager.registerDumpable(KeyguardUpdateMonitor.class.getName(), this);
        this.mVibrator = vibrator;
        this.mHandler = new Handler(looper) {
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 301) {
                    KeyguardUpdateMonitor.this.handleTimeUpdate();
                } else if (i == 302) {
                    KeyguardUpdateMonitor.this.handleBatteryUpdate((BatteryStatus) message.obj);
                } else if (i == 312) {
                    KeyguardUpdateMonitor.this.handleKeyguardReset();
                } else if (i == 314) {
                    KeyguardUpdateMonitor.this.handleUserSwitchComplete(message.arg1);
                } else if (i != 500) {
                    boolean z = false;
                    if (i == 410) {
                        KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
                        if (message.arg1 == 1) {
                            z = true;
                        }
                        keyguardUpdateMonitor.handleFingerprintUnlock("fingerprint", z);
                    } else if (i == 411) {
                        KeyguardUpdateMonitor keyguardUpdateMonitor2 = KeyguardUpdateMonitor.this;
                        if (message.arg1 == 1) {
                            z = true;
                        }
                        keyguardUpdateMonitor2.handleFingerprintUnlock("backup", z);
                    } else if (i != 600) {
                        if (i != 601) {
                            switch (i) {
                                case 304:
                                    KeyguardUpdateMonitor.this.handleSimStateChange(message.arg1, message.arg2, ((Integer) message.obj).intValue());
                                    return;
                                case 305:
                                    KeyguardUpdateMonitor.this.handleRingerModeChange(message.arg1);
                                    return;
                                case 306:
                                    KeyguardUpdateMonitor.this.handlePhoneStateChanged((String) message.obj);
                                    return;
                                default:
                                    switch (i) {
                                        case 308:
                                            KeyguardUpdateMonitor.this.handleDeviceProvisioned();
                                            return;
                                        case 309:
                                            KeyguardUpdateMonitor.this.handleDevicePolicyManagerStateChanged(message.arg1);
                                            return;
                                        case 310:
                                            KeyguardUpdateMonitor.this.handleUserSwitching(message.arg1, (IRemoteCallback) message.obj);
                                            return;
                                        default:
                                            switch (i) {
                                                case 317:
                                                    KeyguardUpdateMonitor.this.handleUserInfoChanged(message.arg1);
                                                    return;
                                                case 318:
                                                    KeyguardUpdateMonitor.this.handleReportEmergencyCallAction();
                                                    return;
                                                case 319:
                                                    Trace.beginSection("KeyguardUpdateMonitor#handler MSG_STARTED_WAKING_UP");
                                                    KeyguardUpdateMonitor.this.handleStartedWakingUp();
                                                    Trace.endSection();
                                                    return;
                                                case 320:
                                                    KeyguardUpdateMonitor.this.handleFinishedGoingToSleep(message.arg1);
                                                    return;
                                                case 321:
                                                    KeyguardUpdateMonitor.this.handleStartedGoingToSleep(message.arg1);
                                                    return;
                                                case 322:
                                                    KeyguardUpdateMonitor.this.handleKeyguardBouncerChanged(message.arg1);
                                                    return;
                                                default:
                                                    switch (i) {
                                                        case 327:
                                                            Trace.beginSection("KeyguardUpdateMonitor#handler MSG_FACE_UNLOCK_STATE_CHANGED");
                                                            KeyguardUpdateMonitor keyguardUpdateMonitor3 = KeyguardUpdateMonitor.this;
                                                            if (message.arg1 != 0) {
                                                                z = true;
                                                            }
                                                            keyguardUpdateMonitor3.handleFaceUnlockStateChanged(z, message.arg2);
                                                            Trace.endSection();
                                                            return;
                                                        case 328:
                                                            KeyguardUpdateMonitor.this.handleSimSubscriptionInfoChanged();
                                                            return;
                                                        case 329:
                                                            KeyguardUpdateMonitor.this.handleAirplaneModeChanged();
                                                            return;
                                                        case 330:
                                                            KeyguardUpdateMonitor.this.handleServiceStateChange(message.arg1, (ServiceState) message.obj);
                                                            return;
                                                        case 331:
                                                            KeyguardUpdateMonitor.this.handleScreenTurnedOn();
                                                            return;
                                                        case 332:
                                                            Trace.beginSection("KeyguardUpdateMonitor#handler MSG_SCREEN_TURNED_ON");
                                                            KeyguardUpdateMonitor.this.handleScreenTurnedOff();
                                                            Trace.endSection();
                                                            return;
                                                        case 333:
                                                            KeyguardUpdateMonitor.this.handleDreamingStateChanged(message.arg1);
                                                            return;
                                                        case 334:
                                                            KeyguardUpdateMonitor.this.handleUserUnlocked(message.arg1);
                                                            return;
                                                        case 335:
                                                            KeyguardUpdateMonitor.this.setAssistantVisible(((Boolean) message.obj).booleanValue());
                                                            return;
                                                        case 336:
                                                            KeyguardUpdateMonitor.this.updateBiometricListeningState();
                                                            return;
                                                        case 337:
                                                            KeyguardUpdateMonitor.this.updateLogoutEnabled();
                                                            return;
                                                        case 338:
                                                            KeyguardUpdateMonitor.this.updateTelephonyCapable(((Boolean) message.obj).booleanValue());
                                                            return;
                                                        case 339:
                                                            KeyguardUpdateMonitor.this.handleTimeZoneUpdate((String) message.obj);
                                                            return;
                                                        case 340:
                                                            KeyguardUpdateMonitor.this.handleUserStopped(message.arg1);
                                                            return;
                                                        case 341:
                                                            KeyguardUpdateMonitor.this.handleUserRemoved(message.arg1);
                                                            return;
                                                        case 342:
                                                            KeyguardUpdateMonitor.this.handleKeyguardGoingAway(((Boolean) message.obj).booleanValue());
                                                            return;
                                                        case 343:
                                                            KeyguardUpdateMonitor.this.handleLockScreenMode();
                                                            return;
                                                        case 344:
                                                            KeyguardUpdateMonitor.this.handleTimeFormatUpdate((String) message.obj);
                                                            return;
                                                        case 345:
                                                            KeyguardUpdateMonitor.this.handleRequireUnlockForNfc();
                                                            return;
                                                        default:
                                                            super.handleMessage(message);
                                                            return;
                                                    }
                                            }
                                    }
                            }
                        } else if (KeyguardUpdateMonitor.this.mKeyguardDisplayManager != null) {
                            KeyguardUpdateMonitor.this.mKeyguardDisplayManager.hide();
                        }
                    } else if (KeyguardUpdateMonitor.this.mKeyguardDisplayManager != null) {
                        KeyguardUpdateMonitor.this.mKeyguardDisplayManager.show();
                    } else {
                        Log.e("KeyguardUpdateMonitor", "RDP: Can't show secondary keyguard, because mKeyguardDisplayManager == null");
                    }
                } else {
                    KeyguardUpdateMonitor.this.sendSimLockSkipped(message.arg1);
                }
            }
        };
        if (!this.mDeviceProvisioned) {
            watchForDeviceProvisioning();
        }
        this.mBatteryStatus = new BatteryStatus(1, 100, 0, 0, 0, true);
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        int phoneCount = telephonyManager.getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            int simState = telephonyManager.getSimState(i);
            Log.d("KeyguardUpdateMonitor", "SlotId: " + i + " state: " + simState);
            if (isEsim(i)) {
                this.mSimExist.put(i, !(simState == 6 || simState == 0 || simState == 1));
            } else {
                this.mSimExist.put(i, simState != 1);
            }
        }
        Log.d("KeyguardUpdateMonitor", "Initial sim exist states: " + this.mSimExist);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        intentFilter.addAction("com.motorola.keyguard.ACTION_SIM_PIN_UNLOCK");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastReceiver, intentFilter, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.BOOT_COMPLETED");
        context2.registerReceiver(this.mBroadcastReceiver, intentFilter2, (String) null, this.mHandler);
        this.mBackgroundExecutor.execute(new KeyguardUpdateMonitor$$ExternalSyntheticLambda5(this));
        this.mHandler.post(new KeyguardUpdateMonitor$$ExternalSyntheticLambda3(this));
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.USER_INFO_CHANGED");
        intentFilter3.addAction("android.app.action.NEXT_ALARM_CLOCK_CHANGED");
        intentFilter3.addAction("com.android.facelock.FACE_UNLOCK_STARTED");
        intentFilter3.addAction("com.android.facelock.FACE_UNLOCK_STOPPED");
        this.mUseMotoFaceUnlock = SystemProperties.getBoolean("ro.face.moto_unlock_service", false);
        this.mMotoDisplayDefaultValue = new AmbientDisplayConfiguration(this.mContext).availableEx();
        intentFilter3.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        intentFilter3.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter3.addAction("android.intent.action.USER_STOPPED");
        intentFilter3.addAction("android.intent.action.USER_REMOVED");
        intentFilter3.addAction("android.nfc.action.REQUIRE_UNLOCK_FOR_NFC");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this.mBroadcastAllReceiver, intentFilter3, this.mHandler, UserHandle.ALL);
        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("android.telephony.action.SERVICE_PROVIDERS_UPDATED");
        context2.registerReceiver(this.mSpnReceiver, intentFilter4);
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        try {
            ActivityManager.getService().registerUserSwitchObserver(this.mUserSwitchObserver, "KeyguardUpdateMonitor");
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
        TrustManager trustManager = (TrustManager) context2.getSystemService(TrustManager.class);
        this.mTrustManager = trustManager;
        trustManager.registerTrustListener(this);
        setStrongAuthTracker(this.mStrongAuthTracker);
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint") && this.mContext.getDisplayId() == 0) {
            this.mFpm = (FingerprintManager) context2.getSystemService("fingerprint");
            this.mMotoExtHwManager = MotoExtHwManager.getInstance(this.mContext);
        }
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.biometrics.face") && this.mContext.getDisplayId() == 0) {
            FaceManager faceManager = (FaceManager) context2.getSystemService("face");
            this.mFaceManager = faceManager;
            this.mFaceSensorProperties = faceManager.getSensorPropertiesInternal();
        }
        if (!(this.mFpm == null && this.mFaceManager == null)) {
            BiometricManager biometricManager = (BiometricManager) context2.getSystemService(BiometricManager.class);
            this.mBiometricManager = biometricManager;
            biometricManager.registerEnabledOnKeyguardCallback(this.mBiometricEnabledCallback);
        }
        updateBiometricListeningState();
        FingerprintManager fingerprintManager = this.mFpm;
        if (fingerprintManager != null) {
            fingerprintManager.addLockoutResetCallback(this.mFingerprintLockoutResetCallback);
        }
        FaceManager faceManager2 = this.mFaceManager;
        if (faceManager2 != null) {
            faceManager2.addLockoutResetCallback(this.mFaceLockoutResetCallback);
        }
        this.mIsAutomotive = isAutomotive();
        TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mTaskStackListener);
        UserManager userManager = (UserManager) context2.getSystemService(UserManager.class);
        this.mUserManager = userManager;
        this.mIsPrimaryUser = userManager.isPrimaryUser();
        int currentUser = ActivityManager.getCurrentUser();
        this.mUserIsUnlocked.put(currentUser, this.mUserManager.isUserUnlocked(currentUser));
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context2.getSystemService(DevicePolicyManager.class);
        this.mDevicePolicyManager = devicePolicyManager;
        this.mLogoutEnabled = devicePolicyManager.isLogoutEnabled();
        updateSecondaryLockscreenRequirement(currentUser);
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            SparseBooleanArray sparseBooleanArray = this.mUserTrustIsUsuallyManaged;
            int i2 = userInfo.id;
            sparseBooleanArray.put(i2, this.mTrustManager.isTrustUsuallyManaged(i2));
        }
        updateAirplaneModeState();
        TelephonyManager telephonyManager2 = (TelephonyManager) context2.getSystemService("phone");
        this.mTelephonyManager = telephonyManager2;
        if (telephonyManager2 != null) {
            this.mTelephonyListenerManager.addActiveDataSubscriptionIdListener(this.mPhoneStateListener);
            for (int i3 = 0; i3 < this.mTelephonyManager.getActiveModemCount(); i3++) {
                int simState2 = this.mTelephonyManager.getSimState(i3);
                int[] subscriptionIds = this.mSubscriptionManager.getSubscriptionIds(i3);
                if (subscriptionIds != null) {
                    for (int obtainMessage : subscriptionIds) {
                        this.mHandler.obtainMessage(304, obtainMessage, i3, Integer.valueOf(simState2)).sendToTarget();
                    }
                }
            }
        }
        updateLockScreenMode(featureFlags.isKeyguardLayoutEnabled());
        this.mTimeFormatChangeObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                KeyguardUpdateMonitor.this.mHandler.sendMessage(KeyguardUpdateMonitor.this.mHandler.obtainMessage(344, Settings.System.getString(KeyguardUpdateMonitor.this.mContext.getContentResolver(), "time_12_24")));
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("time_12_24"), false, this.mTimeFormatChangeObserver, -1);
        SensorManager sensorManager = (SensorManager) context2.getSystemService("sensor");
        this.mSm = sensorManager;
        List<Sensor> sensorList = sensorManager.getSensorList(65539);
        if (sensorList.size() > 0) {
            Sensor sensor = sensorList.get(0);
            this.mStowedSensor = sensor;
            this.mSm.registerListener(this.mStowedListener, sensor, 3);
            this.mIsStowedSensorRegistered = true;
        } else {
            this.mStowedSensor = null;
        }
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        if (roCarrier.equals("tmo")) {
            this.mSprintSubIMSIs = this.mContext.getResources().getStringArray(R$array.zz_moto_sprint_sub_imsis);
            this.mTmoSubIMSIs = this.mContext.getResources().getStringArray(R$array.zz_moto_tmo_sub_imsis);
            this.mTmoSubIMSIsGid1 = this.mContext.getResources().getStringArray(R$array.zz_moto_tmo_sub_imsis_and_gid1);
            this.mMetroSubIMSIsGid1 = this.mContext.getResources().getStringArray(R$array.zz_moto_metro_sub_imsis_and_gid1);
            this.mAssuranceSubIMSIsGid1 = this.mContext.getResources().getStringArray(R$array.zz_moto_assurance_sub_imsis_and_gid1);
        } else if (roCarrier.equals("boost")) {
            this.mBoostSubIMSIsGid1 = this.mContext.getResources().getStringArray(R$array.zz_moto_boost_sub_imsis_and_gid1);
        }
        if (MotoFeature.getInstance(this.mContext).supportSideFps()) {
            this.mSideFpsTouchToUnlockEnabled = MotorolaSettings.Global.getInt(this.mContext.getContentResolver(), "sidefps_touch_to_unlock", 1) == 1;
            Log.i("KeyguardUpdateMonitor", "Side fps touch to unlock enable: " + this.mSideFpsTouchToUnlockEnabled);
            this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Global.getUriFor("sidefps_touch_to_unlock"), false, this.mSideFpsTouchToUnlcokObserver);
        }
        if (this.mUseMotoFaceUnlock) {
            this.mFaceUnlockAvailable = isUnlockWithFacePossible(sCurrentUser);
            SensorManager sensorManager2 = this.mSm;
            if (sensorManager2 != null) {
                this.mLtvSensorOn = !sensorManager2.getSensorList(65556).isEmpty();
            }
            if (this.mLtvSensorOn) {
                this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
                this.mLTVSetting = MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), "property_lift_to_unlock", 0, -2) != 0;
                this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("property_lift_to_unlock"), false, this.mLTVSettingObserver, -1);
                this.mLiftSensor = this.mSm.getDefaultSensor(65556, true);
                if (this.mFaceUnlockAvailable) {
                    registerLTVSensor();
                }
            }
        }
        if (MotoFeature.getInstance(this.mContext).isSupportFolio()) {
            FolioSensorManager instance = FolioSensorManager.getInstance(this.mContext);
            this.mFolioSensorManager = instance;
            KeyguardUpdateMonitor$$ExternalSyntheticLambda2 keyguardUpdateMonitor$$ExternalSyntheticLambda2 = new KeyguardUpdateMonitor$$ExternalSyntheticLambda2(this);
            this.mCallback = keyguardUpdateMonitor$$ExternalSyntheticLambda2;
            instance.addSensorChangeListener(keyguardUpdateMonitor$$ExternalSyntheticLambda2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5() {
        Intent registerReceiver;
        int defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        ServiceState serviceStateForSubscriber = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).getServiceStateForSubscriber(defaultSubscriptionId);
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(330, defaultSubscriptionId, 0, serviceStateForSubscriber));
        if (this.mBatteryStatus == null && (registerReceiver = this.mContext.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"))) != null && this.mBatteryStatus == null) {
            this.mBroadcastReceiver.onReceive(this.mContext, registerReceiver);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(boolean z) {
        if (this.mIsFolioClose != z) {
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "onSensorChanged - Folio close: " + z);
            }
            this.mIsFolioClose = z;
            updateFingerprintListeningState();
        }
    }

    private void updateLockScreenMode(boolean z) {
        if (z != this.mLockScreenMode) {
            this.mLockScreenMode = z ? 1 : 0;
            this.mHandler.sendEmptyMessage(343);
        }
    }

    private void updateUdfpsEnrolled(int i) {
        this.mIsUdfpsEnrolled = this.mAuthController.isUdfpsEnrolled(i);
    }

    private void updateFaceEnrolled(int i) {
        this.mIsFaceEnrolled = ((Boolean) DejankUtils.whitelistIpcs(new KeyguardUpdateMonitor$$ExternalSyntheticLambda17(this, i))).booleanValue();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$updateFaceEnrolled$7(int i) {
        FaceManager faceManager = this.mFaceManager;
        return Boolean.valueOf(faceManager != null && faceManager.isHardwareDetected() && this.mFaceManager.hasEnrolledTemplates(i) && this.mBiometricEnabledForUser.get(i));
    }

    public boolean isUdfpsEnrolled() {
        return this.mIsUdfpsEnrolled;
    }

    public boolean isUdfpsAvailable() {
        return this.mAuthController.getUdfpsProps() != null && !this.mAuthController.getUdfpsProps().isEmpty();
    }

    public boolean isFaceEnrolled() {
        return this.mIsFaceEnrolled;
    }

    private void updateAirplaneModeState() {
        if (WirelessUtils.isAirplaneModeOn(this.mContext) && !this.mHandler.hasMessages(329)) {
            this.mHandler.sendEmptyMessage(329);
        }
    }

    /* access modifiers changed from: private */
    public void updateBiometricListeningState() {
        updateFingerprintListeningState();
        updateFaceListeningState();
    }

    /* access modifiers changed from: private */
    public void updateFingerprintListeningState() {
        if (!this.mHandler.hasMessages(336)) {
            updateUdfpsEnrolled(getCurrentUser());
            boolean shouldListenForFingerprint = shouldListenForFingerprint(isUdfpsEnrolled());
            int i = this.mFingerprintRunningState;
            boolean z = true;
            if (!(i == 1 || i == 3)) {
                z = false;
            }
            if (z && !shouldListenForFingerprint) {
                stopListeningForFingerprint();
            } else if (!z && shouldListenForFingerprint) {
                if (this.mMotoExtHwManager != null && !this.mSideFpsTouchToUnlockEnabled && !userNeedsStrongAuth() && isUnlockWithFingerprintPossible(getCurrentUser())) {
                    if (DEBUG) {
                        Log.d("KeyguardUpdateMonitor", "notifyMotBiometricFingerprint: 3");
                    }
                    this.mMotoExtHwManager.notifyMotBiometricFingerprint(3);
                }
                startListeningForFingerprint();
            }
        }
    }

    public boolean isUserUnlocked(int i) {
        return this.mUserIsUnlocked.get(i);
    }

    private void registerLTVSensor() {
        SensorEventListener sensorEventListener;
        Sensor sensor;
        SensorManager sensorManager = this.mSm;
        if (sensorManager == null || (sensorEventListener = this.mLTVListener) == null || (sensor = this.mLiftSensor) == null) {
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "register to LTV sensor error, return");
            }
        } else if (!this.mIsLTVSensorRegistered) {
            boolean registerListener = sensorManager.registerListener(sensorEventListener, sensor, 0);
            if (registerListener) {
                this.mIsLTVSensorRegistered = true;
            }
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "register to LTV sensor, result = " + registerListener);
            }
        }
    }

    private void unRegisterLTVSensor() {
        SensorEventListener sensorEventListener;
        Sensor sensor;
        SensorManager sensorManager = this.mSm;
        if (sensorManager == null || (sensorEventListener = this.mLTVListener) == null || (sensor = this.mLiftSensor) == null) {
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "unregister to LTV sensor error, return");
            }
        } else if (this.mIsLTVSensorRegistered) {
            sensorManager.unregisterListener(sensorEventListener, sensor);
            this.mIsLTVSensorRegistered = false;
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "unregister to LTV sensor");
            }
        }
    }

    /* access modifiers changed from: private */
    public int getMotoDisplayEnabled() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "doze_enabled", this.mMotoDisplayDefaultValue, -2);
    }

    /* access modifiers changed from: private */
    public boolean getFodOnlyModeEnabled() {
        return MotorolaSettings.Secure.getIntForUser(this.mContext.getContentResolver(), "is_fod_only_enable", 0, -2) == 1;
    }

    public void onAuthInterruptDetected(boolean z) {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "onAuthInterruptDetected(" + z + ")");
        }
        if (this.mAuthInterruptActive != z) {
            this.mAuthInterruptActive = z;
            updateFaceListeningState();
        }
    }

    public void requestFaceAuth(boolean z) {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "requestFaceAuth() userInitiated=" + z);
        }
        this.mIsFaceAuthUserRequested = z | this.mIsFaceAuthUserRequested;
        updateFaceListeningState();
    }

    public void cancelFaceAuth() {
        stopListeningForFace();
    }

    /* access modifiers changed from: private */
    public void updateFaceListeningState() {
        if (!this.mHandler.hasMessages(336)) {
            this.mHandler.removeCallbacks(this.mRetryFaceAuthentication);
            boolean shouldListenForFace = shouldListenForFace();
            int i = this.mFaceRunningState;
            if (i == 1 && !shouldListenForFace) {
                this.mIsFaceAuthUserRequested = false;
                stopListeningForFace();
            } else if (i != 1 && shouldListenForFace) {
                startListeningForFace();
            }
        }
    }

    private boolean shouldListenForFingerprintAssistant() {
        BiometricAuthenticated biometricAuthenticated = this.mUserFingerprintAuthenticated.get(getCurrentUser());
        if (!this.mAssistantVisible || !this.mKeyguardOccluded) {
            return false;
        }
        if ((biometricAuthenticated == null || !biometricAuthenticated.mAuthenticated) && !this.mUserHasTrust.get(getCurrentUser(), false)) {
            return true;
        }
        return false;
    }

    private boolean shouldListenForFaceAssistant() {
        BiometricAuthenticated biometricAuthenticated = this.mUserFaceAuthenticated.get(getCurrentUser());
        if (!this.mAssistantVisible || !this.mKeyguardOccluded) {
            return false;
        }
        if ((biometricAuthenticated == null || !biometricAuthenticated.mAuthenticated) && !this.mUserHasTrust.get(getCurrentUser(), false)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0028, code lost:
        r1 = r0.mKeyguardOccluded;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldListenForFingerprint(boolean r28) {
        /*
            r27 = this;
            r0 = r27
            int r4 = getCurrentUser()
            boolean r1 = r0.getUserHasTrust(r4)
            r2 = 1
            r24 = r1 ^ 1
            boolean r21 = r27.shouldListenForFingerprintAssistant()
            boolean r1 = r0.mKeyguardIsVisible
            r3 = 0
            if (r1 != 0) goto L_0x003d
            boolean r1 = r0.mDeviceInteractive
            if (r1 == 0) goto L_0x003d
            boolean r1 = r0.mBouncer
            if (r1 == 0) goto L_0x0022
            boolean r1 = r0.mKeyguardGoingAway
            if (r1 == 0) goto L_0x003d
        L_0x0022:
            boolean r1 = r0.mGoingToSleep
            if (r1 != 0) goto L_0x003d
            if (r21 != 0) goto L_0x003d
            boolean r1 = r0.mKeyguardOccluded
            if (r1 == 0) goto L_0x0030
            boolean r5 = r0.mIsDreaming
            if (r5 != 0) goto L_0x003d
        L_0x0030:
            if (r1 == 0) goto L_0x003b
            if (r24 == 0) goto L_0x003b
            boolean r1 = r0.mOccludingAppRequestingFp
            if (r1 != 0) goto L_0x003d
            if (r28 == 0) goto L_0x003b
            goto L_0x003d
        L_0x003b:
            r1 = r3
            goto L_0x003e
        L_0x003d:
            r1 = r2
        L_0x003e:
            boolean r5 = DEBUG
            if (r5 == 0) goto L_0x00dc
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "shouldListenForFingerprint mKeyguardIsVisible = "
            r5.append(r6)
            boolean r6 = r0.mKeyguardIsVisible
            r5.append(r6)
            java.lang.String r6 = " mDeviceInteractive = "
            r5.append(r6)
            boolean r6 = r0.mDeviceInteractive
            r5.append(r6)
            java.lang.String r6 = " mBouncer = "
            r5.append(r6)
            boolean r6 = r0.mBouncer
            r5.append(r6)
            java.lang.String r6 = " mKeyguardGoingAway = "
            r5.append(r6)
            boolean r6 = r0.mKeyguardGoingAway
            r5.append(r6)
            java.lang.String r6 = " mGoingToSleep = "
            r5.append(r6)
            boolean r6 = r0.mGoingToSleep
            r5.append(r6)
            java.lang.String r6 = " mSwitchingUser = "
            r5.append(r6)
            boolean r6 = r0.mSwitchingUser
            r5.append(r6)
            java.lang.String r6 = " isFingerprintDisabled = "
            r5.append(r6)
            int r6 = getCurrentUser()
            boolean r6 = r0.isFingerprintDisabled(r6)
            r5.append(r6)
            java.lang.String r6 = " shouldListenForFingerprintAssistant = "
            r5.append(r6)
            boolean r6 = r27.shouldListenForFingerprintAssistant()
            r5.append(r6)
            java.lang.String r6 = " mKeyguardOccluded = "
            r5.append(r6)
            boolean r6 = r0.mKeyguardOccluded
            r5.append(r6)
            java.lang.String r6 = " mIsDreaming = "
            r5.append(r6)
            boolean r6 = r0.mIsDreaming
            r5.append(r6)
            java.lang.String r6 = " mIsPrimaryUser = "
            r5.append(r6)
            boolean r6 = r0.mIsPrimaryUser
            r5.append(r6)
            java.lang.String r6 = " shouldStartListenForSideFPS() = "
            r5.append(r6)
            boolean r6 = r27.shouldStartListenForSideFPS()
            r5.append(r6)
            java.lang.String r6 = " mFPSGatedByStowed = "
            r5.append(r6)
            boolean r6 = r0.mFPSGatedByStowed
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "KeyguardUpdateMonitor"
            android.util.Log.d(r6, r5)
        L_0x00dc:
            android.util.SparseBooleanArray r5 = r0.mBiometricEnabledForUser
            boolean r6 = r5.get(r4)
            boolean r8 = r0.getUserCanSkipBouncer(r4)
            boolean r13 = r0.isFingerprintDisabled(r4)
            boolean r5 = r0.mSwitchingUser
            if (r5 != 0) goto L_0x010e
            if (r13 != 0) goto L_0x010e
            boolean r5 = r0.mKeyguardGoingAway
            if (r5 == 0) goto L_0x00f8
            boolean r5 = r0.mDeviceInteractive
            if (r5 != 0) goto L_0x010e
        L_0x00f8:
            boolean r5 = r0.mIsPrimaryUser
            if (r5 == 0) goto L_0x010e
            boolean r5 = r0.mFPSGatedByStowed
            if (r5 != 0) goto L_0x010e
            boolean r5 = r27.shouldStartListenForSideFPS()
            if (r5 == 0) goto L_0x010e
            boolean r5 = r0.mIsFolioClose
            if (r5 != 0) goto L_0x010e
            if (r6 == 0) goto L_0x010e
            r5 = r2
            goto L_0x010f
        L_0x010e:
            r5 = r3
        L_0x010f:
            boolean r7 = r0.mFingerprintLockedOut
            if (r7 == 0) goto L_0x011e
            boolean r7 = r0.mBouncer
            if (r7 == 0) goto L_0x011e
            boolean r7 = r0.mCredentialAttempted
            if (r7 != 0) goto L_0x011c
            goto L_0x011e
        L_0x011c:
            r7 = r3
            goto L_0x011f
        L_0x011e:
            r7 = r2
        L_0x011f:
            boolean r12 = r0.isEncryptedOrLockdown(r4)
            boolean r25 = r27.userNeedsStrongAuth()
            if (r28 == 0) goto L_0x0138
            if (r8 != 0) goto L_0x0136
            if (r12 != 0) goto L_0x0136
            if (r25 != 0) goto L_0x0136
            if (r24 == 0) goto L_0x0136
            boolean r9 = r0.mFingerprintLockedOut
            if (r9 != 0) goto L_0x0136
            goto L_0x0138
        L_0x0136:
            r9 = r3
            goto L_0x0139
        L_0x0138:
            r9 = r2
        L_0x0139:
            if (r1 == 0) goto L_0x0144
            if (r5 == 0) goto L_0x0144
            if (r7 == 0) goto L_0x0144
            if (r9 == 0) goto L_0x0144
            r26 = r2
            goto L_0x0146
        L_0x0144:
            r26 = r3
        L_0x0146:
            boolean r1 = DEBUG_FINGERPRINT
            if (r1 != 0) goto L_0x014b
            goto L_0x0187
        L_0x014b:
            com.android.keyguard.KeyguardFingerprintListenModel r5 = new com.android.keyguard.KeyguardFingerprintListenModel
            r1 = r5
            long r2 = java.lang.System.currentTimeMillis()
            boolean r7 = r0.mBouncer
            boolean r9 = r0.mCredentialAttempted
            boolean r10 = r0.mDeviceInteractive
            boolean r11 = r0.mIsDreaming
            boolean r14 = r0.mFingerprintLockedOut
            boolean r15 = r0.mGoingToSleep
            r23 = r5
            boolean r5 = r0.mKeyguardGoingAway
            r16 = r5
            boolean r5 = r0.mKeyguardIsVisible
            r17 = r5
            boolean r5 = r0.mKeyguardOccluded
            r18 = r5
            boolean r5 = r0.mOccludingAppRequestingFp
            r19 = r5
            boolean r5 = r0.mIsPrimaryUser
            r20 = r5
            boolean r5 = r0.mSwitchingUser
            r22 = r5
            r0 = r23
            r5 = r26
            r23 = r28
            r1.<init>(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25)
            r1 = r0
            r0 = r27
            r0.maybeLogListenerModelData(r1)
        L_0x0187:
            return r26
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.shouldListenForFingerprint(boolean):boolean");
    }

    public boolean shouldListenForFace() {
        boolean z = false;
        if (this.mFaceManager == null) {
            return false;
        }
        boolean z2 = this.mKeyguardIsVisible && this.mDeviceInteractive && !this.mGoingToSleep && !(this.mStatusBarState == 2);
        int currentUser = getCurrentUser();
        int strongAuthForUser = this.mStrongAuthTracker.getStrongAuthForUser(currentUser);
        boolean z3 = containsFlag(strongAuthForUser, 2) || containsFlag(strongAuthForUser, 32);
        boolean z4 = containsFlag(strongAuthForUser, 1) || containsFlag(strongAuthForUser, 16);
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        boolean z5 = keyguardBypassController != null && keyguardBypassController.canBypass();
        boolean z6 = !getUserCanSkipBouncer(currentUser) || z5;
        if (!this.mUseMotoFaceUnlock || !this.mBouncer || !this.mGoingToSleep) {
            boolean z7 = !z4 || (z5 && !this.mBouncer);
            boolean z8 = !this.mFaceSensorProperties.isEmpty() && this.mFaceSensorProperties.get(0).supportsFaceDetection;
            if (z3 && !z8) {
                z7 = false;
            }
            boolean isFaceAuthenticated = getIsFaceAuthenticated();
            boolean isFaceDisabled = isFaceDisabled(currentUser);
            boolean z9 = this.mBiometricEnabledForUser.get(currentUser);
            boolean shouldListenForFaceAssistant = shouldListenForFaceAssistant();
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "shouldListenForFace:  mBouncer=" + this.mBouncer + " mAuthInterruptActive=" + this.mAuthInterruptActive + " awakeKeyguard=" + z2 + " shouldListenForFaceAssistant=" + shouldListenForFaceAssistant() + " mSwitchingUser=" + this.mSwitchingUser + " isFaceDisabled(user)= " + isFaceDisabled(currentUser) + " becauseCannotSkipBouncer=" + z6 + " mKeyguardGoingAway=" + this.mKeyguardGoingAway + " mBiometricEnabledForUser.get(user)=" + this.mBiometricEnabledForUser.get(currentUser) + " mLockIconPressed=" + this.mLockIconPressed + " strongAuthAllowsScanning=" + z7 + " mIsPrimaryUser=" + this.mIsPrimaryUser + " mSecureCameraLaunched=" + this.mSecureCameraLaunched);
            }
            if ((this.mBouncer || this.mAuthInterruptActive || this.mOccludingAppRequestingFace || z2 || shouldListenForFaceAssistant) && !this.mSwitchingUser && !isFaceDisabled && z6 && !this.mKeyguardGoingAway && z9 && !this.mLockIconPressed && z7 && this.mIsPrimaryUser && ((!this.mSecureCameraLaunched || this.mOccludingAppRequestingFace) && !isFaceAuthenticated)) {
                z = true;
            }
            if (DEBUG_FACE) {
                maybeLogListenerModelData(new KeyguardFaceListenModel(System.currentTimeMillis(), currentUser, z, this.mAuthInterruptActive, z6, z9, this.mBouncer, isFaceAuthenticated, isFaceDisabled, z2, this.mKeyguardGoingAway, shouldListenForFaceAssistant, this.mLockIconPressed, this.mOccludingAppRequestingFace, this.mIsPrimaryUser, z7, this.mSecureCameraLaunched, this.mSwitchingUser));
            }
            return z;
        }
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "Should not start face listening, because the phone is in bouncer and going to sleep.");
        }
        return false;
    }

    private void maybeLogListenerModelData(KeyguardListenModel keyguardListenModel) {
        boolean z = true;
        if ((!DEBUG_FACE || !(keyguardListenModel instanceof KeyguardFaceListenModel) || this.mFaceRunningState == 1) && (!DEBUG_FINGERPRINT || !(keyguardListenModel instanceof KeyguardFingerprintListenModel) || this.mFingerprintRunningState == 1)) {
            z = false;
        }
        if (z && keyguardListenModel.getListening()) {
            this.mListenModels.add(keyguardListenModel);
        }
    }

    private void startListeningForFingerprint() {
        if (this.mFpm != null) {
            int currentUser = getCurrentUser();
            boolean isUnlockWithFingerprintPossible = isUnlockWithFingerprintPossible(currentUser);
            if (this.mFingerprintCancelSignal != null) {
                Log.e("KeyguardUpdateMonitor", "Cancellation signal is not null, high chance of bug in fp auth lifecycle management. FP state: " + this.mFingerprintRunningState + ", unlockPossible: " + isUnlockWithFingerprintPossible);
            }
            int i = this.mFingerprintRunningState;
            if (i == 2) {
                setFingerprintRunningState(3);
            } else if (i != 3) {
                if (DEBUG) {
                    Log.v("KeyguardUpdateMonitor", "startListeningForFingerprint()");
                }
                if (isUnlockWithFingerprintPossible) {
                    this.mFingerprintCancelSignal = new CancellationSignal();
                    if (isEncryptedOrLockdown(currentUser)) {
                        this.mFpm.detectFingerprint(this.mFingerprintCancelSignal, this.mFingerprintDetectionCallback, currentUser);
                    } else {
                        this.mFpm.authenticate((FingerprintManager.CryptoObject) null, this.mFingerprintCancelSignal, this.mFingerprintAuthenticationCallback, (Handler) null, -1, currentUser);
                    }
                    setFingerprintRunningState(1);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void startListeningForFace() {
        if (this.mFaceManager != null) {
            int currentUser = getCurrentUser();
            boolean isUnlockWithFacePossible = isUnlockWithFacePossible(currentUser);
            if (this.mFaceCancelSignal != null) {
                Log.e("KeyguardUpdateMonitor", "Cancellation signal is not null, high chance of bug in face auth lifecycle management. Face state: " + this.mFaceRunningState + ", unlockPossible: " + isUnlockWithFacePossible);
            }
            int i = this.mFaceRunningState;
            if (i == 2) {
                setFaceRunningState(3);
            } else if (i != 3) {
                if (DEBUG) {
                    Log.v("KeyguardUpdateMonitor", "startListeningForFace(): " + this.mFaceRunningState);
                }
                if (isUnlockWithFacePossible) {
                    this.mFaceCancelSignal = new CancellationSignal();
                    boolean z = !this.mFaceSensorProperties.isEmpty() && this.mFaceSensorProperties.get(0).supportsFaceDetection;
                    if (!isEncryptedOrLockdown(currentUser) || !z) {
                        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
                        this.mFaceManager.authenticate((CryptoObject) null, this.mFaceCancelSignal, this.mFaceAuthenticationCallback, (Handler) null, currentUser, keyguardBypassController != null && keyguardBypassController.isBypassEnabled());
                    } else {
                        this.mFaceManager.detectFace(this.mFaceCancelSignal, this.mFaceDetectionCallback, currentUser);
                    }
                    setFaceRunningState(1);
                }
            }
        }
    }

    public boolean isUnlockingWithBiometricsPossible(int i) {
        return isUnlockWithFacePossible(i) || isUnlockWithFingerprintPossible(i);
    }

    private boolean isUnlockWithFingerprintPossible(int i) {
        FingerprintManager fingerprintManager = this.mFpm;
        return fingerprintManager != null && fingerprintManager.isHardwareDetected() && !isFingerprintDisabled(i) && this.mFpm.hasEnrolledTemplates(i);
    }

    private boolean isUnlockWithFacePossible(int i) {
        return isFaceAuthEnabledForUser(i) && !isFaceDisabled(i);
    }

    public boolean isFaceAuthEnabledForUser(int i) {
        updateFaceEnrolled(i);
        return this.mIsFaceEnrolled;
    }

    private void stopListeningForFingerprint() {
        if (this.mFpm != null) {
            if (DEBUG) {
                Log.v("KeyguardUpdateMonitor", "stopListeningForFingerprint()");
            }
            if (this.mFingerprintRunningState == 1) {
                CancellationSignal cancellationSignal = this.mFingerprintCancelSignal;
                if (cancellationSignal != null) {
                    cancellationSignal.cancel();
                    this.mFingerprintCancelSignal = null;
                    this.mHandler.removeCallbacks(this.mFpCancelNotReceived);
                    this.mHandler.postDelayed(this.mFpCancelNotReceived, 3000);
                }
                setFingerprintRunningState(2);
            }
            if (this.mFingerprintRunningState == 3) {
                setFingerprintRunningState(2);
            }
        }
    }

    private void stopListeningForFace() {
        if (this.mFaceManager != null) {
            if (DEBUG) {
                Log.v("KeyguardUpdateMonitor", "stopListeningForFace()");
            }
            if (this.mFaceRunningState == 1) {
                CancellationSignal cancellationSignal = this.mFaceCancelSignal;
                if (cancellationSignal != null) {
                    cancellationSignal.cancel();
                    this.mFaceCancelSignal = null;
                    this.mHandler.removeCallbacks(this.mFaceCancelNotReceived);
                    this.mHandler.postDelayed(this.mFaceCancelNotReceived, 3000);
                }
                setFaceRunningState(2);
            }
            if (this.mFaceRunningState == 3) {
                setFaceRunningState(2);
            }
            if (this.mUseMotoFaceUnlock) {
                unRegisterLTVSensor();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isDeviceProvisionedInSettingsDb() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private void watchForDeviceProvisioning() {
        this.mDeviceProvisionedObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                super.onChange(z);
                KeyguardUpdateMonitor keyguardUpdateMonitor = KeyguardUpdateMonitor.this;
                boolean unused = keyguardUpdateMonitor.mDeviceProvisioned = keyguardUpdateMonitor.isDeviceProvisionedInSettingsDb();
                if (KeyguardUpdateMonitor.this.mDeviceProvisioned) {
                    KeyguardUpdateMonitor.this.mHandler.sendEmptyMessage(308);
                }
                if (KeyguardUpdateMonitor.DEBUG) {
                    Log.d("KeyguardUpdateMonitor", "DEVICE_PROVISIONED state = " + KeyguardUpdateMonitor.this.mDeviceProvisioned);
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this.mDeviceProvisionedObserver);
        boolean isDeviceProvisionedInSettingsDb = isDeviceProvisionedInSettingsDb();
        if (isDeviceProvisionedInSettingsDb != this.mDeviceProvisioned) {
            this.mDeviceProvisioned = isDeviceProvisionedInSettingsDb;
            if (isDeviceProvisionedInSettingsDb) {
                this.mHandler.sendEmptyMessage(308);
            }
        }
    }

    public void setHasLockscreenWallpaper(boolean z) {
        Assert.isMainThread();
        if (z != this.mHasLockscreenWallpaper) {
            this.mHasLockscreenWallpaper = z;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onHasLockscreenWallpaperChanged(z);
                }
            }
        }
    }

    public boolean hasLockscreenWallpaper() {
        return this.mHasLockscreenWallpaper;
    }

    /* access modifiers changed from: private */
    public void handleDevicePolicyManagerStateChanged(int i) {
        Assert.isMainThread();
        updateFingerprintListeningState();
        updateSecondaryLockscreenRequirement(i);
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDevicePolicyManagerStateChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void handleUserSwitching(int i, IRemoteCallback iRemoteCallback) {
        Assert.isMainThread();
        clearBiometricRecognized();
        this.mUserTrustIsUsuallyManaged.put(i, this.mTrustManager.isTrustUsuallyManaged(i));
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserSwitching(i);
            }
        }
        try {
            iRemoteCallback.sendResult((Bundle) null);
        } catch (RemoteException unused) {
        }
    }

    /* access modifiers changed from: private */
    public void handleUserSwitchComplete(int i) {
        Assert.isMainThread();
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onUserSwitchComplete(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleDeviceProvisioned() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onDeviceProvisioned();
            }
        }
        if (this.mDeviceProvisionedObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mDeviceProvisionedObserver);
            this.mDeviceProvisionedObserver = null;
        }
    }

    /* access modifiers changed from: private */
    public void handlePhoneStateChanged(String str) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handlePhoneStateChanged(" + str + ")");
        }
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(str)) {
            this.mPhoneState = 0;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(str)) {
            this.mPhoneState = 2;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(str)) {
            this.mPhoneState = 1;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleRingerModeChange(int i) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleRingerModeChange(" + i + ")");
        }
        this.mRingMode = i;
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRingerModeChanged(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleTimeUpdate() {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleTimeUpdate");
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleLockScreenMode() {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleLockScreenMode(" + this.mLockScreenMode + ")");
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onLockScreenModeChanged(this.mLockScreenMode);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleTimeZoneUpdate(String str) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleTimeZoneUpdate");
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeZoneChanged(TimeZone.getTimeZone(str));
                keyguardUpdateMonitorCallback.onTimeChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleTimeFormatUpdate(String str) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleTimeFormatUpdate timeFormat=" + str);
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onTimeFormatChanged(str);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleBatteryUpdate(BatteryStatus batteryStatus) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleBatteryUpdate");
        }
        boolean isBatteryUpdateInteresting = isBatteryUpdateInteresting(this.mBatteryStatus, batteryStatus);
        this.mBatteryStatus = batteryStatus;
        if (isBatteryUpdateInteresting) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onRefreshBatteryInfo(batteryStatus);
                }
            }
        }
    }

    static class InvalidCardData {
        String plmn;
        int slotId;

        public InvalidCardData(int i, String str) {
            this.slotId = i;
            this.plmn = str;
        }
    }

    public HashMap<Integer, InvalidCardData> getInvalidCards() {
        return this.mInvalidCards;
    }

    private void handleInvalidCardInSimStateChange(int i, int i2, IccCardConstants.State state) {
        boolean containsKey = this.mInvalidCards.containsKey(Integer.valueOf(i));
        if (containsKey && state != IccCardConstants.State.CARD_IO_ERROR) {
            this.mInvalidCards.remove(Integer.valueOf(i));
        }
        if (!containsKey && state == IccCardConstants.State.CARD_IO_ERROR) {
            this.mInvalidCards.put(Integer.valueOf(i), new InvalidCardData(i2, (String) null));
            Log.d("KeyguardUpdateMonitor", "***Invalid card info is refreshed to CarrierText");
            for (int i3 = 0; i3 < this.mCallbacks.size(); i3++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i3).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateTelephonyCapable(boolean z) {
        Assert.isMainThread();
        if (z != this.mTelephonyCapable) {
            this.mTelephonyCapable = z;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onTelephonyCapable(this.mTelephonyCapable);
                }
            }
        }
    }

    public boolean isEsim(int i) {
        UiccSlotInfo[] uiccSlotsInfo = TelephonyManager.getDefault().getUiccSlotsInfo();
        boolean z = false;
        int i2 = 0;
        while (true) {
            if (uiccSlotsInfo == null || i2 >= uiccSlotsInfo.length) {
                break;
            }
            UiccSlotInfo uiccSlotInfo = uiccSlotsInfo[i2];
            if (uiccSlotInfo == null) {
                return false;
            }
            if (uiccSlotInfo.getLogicalSlotIdx() == i) {
                z = uiccSlotInfo.getIsEuicc();
                break;
            }
            i2++;
        }
        Log.d("KeyguardUpdateMonitor", "isEsim slotid = " + i + " isEsim = " + z);
        return z;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0145, code lost:
        if (r1 == 3) goto L_0x010d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x019a  */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleSimStateChange(int r9, int r10, int r11) {
        /*
            r8 = this;
            com.android.systemui.util.Assert.isMainThread()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "handleSimStateChange(subId="
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = ", slotId="
            r0.append(r1)
            r0.append(r10)
            java.lang.String r1 = ", state="
            r0.append(r1)
            r0.append(r11)
            java.lang.String r1 = ")"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "KeyguardUpdateMonitor"
            android.util.Log.d(r1, r0)
            boolean r0 = android.telephony.SubscriptionManager.isValidSlotIndex(r10)
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x00af
            boolean r0 = r8.hasSIM()
            boolean r4 = r8.isEsim(r10)
            if (r4 == 0) goto L_0x0052
            if (r11 == 0) goto L_0x004a
            r4 = 6
            if (r11 == r4) goto L_0x004a
            if (r11 != r3) goto L_0x0048
            goto L_0x004a
        L_0x0048:
            r4 = r2
            goto L_0x004b
        L_0x004a:
            r4 = r3
        L_0x004b:
            android.util.SparseBooleanArray r5 = r8.mSimExist
            r4 = r4 ^ r3
            r5.put(r10, r4)
            goto L_0x005c
        L_0x0052:
            android.util.SparseBooleanArray r4 = r8.mSimExist
            if (r11 == r3) goto L_0x0058
            r5 = r3
            goto L_0x0059
        L_0x0058:
            r5 = r2
        L_0x0059:
            r4.put(r10, r5)
        L_0x005c:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "mSimExist: "
            r4.append(r5)
            android.util.SparseBooleanArray r5 = r8.mSimExist
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r1, r4)
            boolean r4 = r8.hasSIM()
            if (r4 == r0) goto L_0x00af
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "***refresh carrier text because hasSim state is changed to "
            r0.append(r4)
            boolean r4 = r8.hasSIM()
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r1, r0)
            r0 = r2
        L_0x0091:
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r4 = r8.mCallbacks
            int r4 = r4.size()
            if (r0 >= r4) goto L_0x00af
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r4 = r8.mCallbacks
            java.lang.Object r4 = r4.get(r0)
            java.lang.ref.WeakReference r4 = (java.lang.ref.WeakReference) r4
            java.lang.Object r4 = r4.get()
            com.android.keyguard.KeyguardUpdateMonitorCallback r4 = (com.android.keyguard.KeyguardUpdateMonitorCallback) r4
            if (r4 == 0) goto L_0x00ac
            r4.onRefreshCarrierInfo()
        L_0x00ac:
            int r0 = r0 + 1
            goto L_0x0091
        L_0x00af:
            boolean r0 = android.telephony.SubscriptionManager.isValidSubscriptionId(r9)
            if (r0 != 0) goto L_0x00ee
            com.android.internal.telephony.IccCardConstants$State r0 = com.android.internal.telephony.IccCardConstants.State.intToState(r11)
            r8.handleInvalidCardInSimStateChange(r9, r10, r0)
            java.lang.String r0 = "invalid subId in handleSimStateChange()"
            android.util.Log.w(r1, r0)
            if (r11 != r3) goto L_0x00e5
            r8.updateTelephonyCapable(r3)
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r0 = r8.mSimDatas
            java.util.Collection r0 = r0.values()
            java.util.Iterator r0 = r0.iterator()
        L_0x00d0:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x00e3
            java.lang.Object r1 = r0.next()
            com.android.keyguard.KeyguardUpdateMonitor$SimData r1 = (com.android.keyguard.KeyguardUpdateMonitor.SimData) r1
            int r4 = r1.slotId
            if (r4 != r10) goto L_0x00d0
            r1.simState = r3
            goto L_0x00d0
        L_0x00e3:
            r0 = r3
            goto L_0x00ef
        L_0x00e5:
            r0 = 8
            if (r11 != r0) goto L_0x00ed
            r8.updateTelephonyCapable(r3)
            goto L_0x00ee
        L_0x00ed:
            return
        L_0x00ee:
            r0 = r2
        L_0x00ef:
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r1 = r8.mSimDatas
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)
            java.lang.Object r1 = r1.get(r4)
            com.android.keyguard.KeyguardUpdateMonitor$SimData r1 = (com.android.keyguard.KeyguardUpdateMonitor.SimData) r1
            r4 = 3
            r5 = 2
            if (r1 != 0) goto L_0x010f
            com.android.keyguard.KeyguardUpdateMonitor$SimData r1 = new com.android.keyguard.KeyguardUpdateMonitor$SimData
            r1.<init>(r11, r10, r9)
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r6 = r8.mSimDatas
            java.lang.Integer r7 = java.lang.Integer.valueOf(r9)
            r6.put(r7, r1)
        L_0x010d:
            r6 = r3
            goto L_0x0148
        L_0x010f:
            int r6 = r1.simState
            if (r6 != r11) goto L_0x011e
            int r6 = r1.subId
            if (r6 != r9) goto L_0x011e
            int r6 = r1.slotId
            if (r6 == r10) goto L_0x011c
            goto L_0x011e
        L_0x011c:
            r6 = r2
            goto L_0x011f
        L_0x011e:
            r6 = r3
        L_0x011f:
            int r7 = r1.subId
            if (r7 != r9) goto L_0x0130
            boolean r7 = r8.isSubIdSkipped(r7)
            if (r7 == 0) goto L_0x0130
            r7 = 5
            if (r11 != r7) goto L_0x0130
            boolean r7 = r8.mIsRecentlySkipped
            if (r7 != 0) goto L_0x013f
        L_0x0130:
            r1.simState = r11
            r1.subId = r9
            r1.slotId = r10
            boolean r7 = r8.isSubIdSkipped(r9)
            if (r7 == 0) goto L_0x013f
            r8.clearSkippedSubId()
        L_0x013f:
            if (r6 != 0) goto L_0x0148
            int r1 = r1.simState
            if (r1 == r5) goto L_0x010d
            if (r1 != r4) goto L_0x0148
            goto L_0x010d
        L_0x0148:
            int r1 = r8.getSkippedSubId()
            r7 = -1
            if (r1 == r7) goto L_0x0158
            int r1 = r8.getValidSimCount()
            if (r1 > r3) goto L_0x0158
            r8.clearSkippedSubId()
        L_0x0158:
            if (r6 != 0) goto L_0x015c
            if (r0 == 0) goto L_0x017d
        L_0x015c:
            if (r11 == 0) goto L_0x017d
            r0 = r2
        L_0x015f:
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r1 = r8.mCallbacks
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x017d
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r1 = r8.mCallbacks
            java.lang.Object r1 = r1.get(r0)
            java.lang.ref.WeakReference r1 = (java.lang.ref.WeakReference) r1
            java.lang.Object r1 = r1.get()
            com.android.keyguard.KeyguardUpdateMonitorCallback r1 = (com.android.keyguard.KeyguardUpdateMonitorCallback) r1
            if (r1 == 0) goto L_0x017a
            r1.onSimStateChanged(r9, r10, r11)
        L_0x017a:
            int r0 = r0 + 1
            goto L_0x015f
        L_0x017d:
            if (r6 == 0) goto L_0x01bd
            if (r11 != r3) goto L_0x01bd
            int r9 = r8.getNextSubIdForState(r5)
            boolean r10 = android.telephony.SubscriptionManager.isValidSubscriptionId(r9)
            if (r10 == 0) goto L_0x018d
            r11 = r5
            goto L_0x0198
        L_0x018d:
            int r9 = r8.getNextSubIdForState(r4)
            boolean r10 = android.telephony.SubscriptionManager.isValidSubscriptionId(r9)
            if (r10 == 0) goto L_0x0198
            r11 = r4
        L_0x0198:
            if (r11 == r3) goto L_0x01bd
            int r10 = android.telephony.SubscriptionManager.getSlotIndex(r9)
            r0 = r2
        L_0x019f:
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r1 = r8.mCallbacks
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x01bd
            java.util.ArrayList<java.lang.ref.WeakReference<com.android.keyguard.KeyguardUpdateMonitorCallback>> r1 = r8.mCallbacks
            java.lang.Object r1 = r1.get(r0)
            java.lang.ref.WeakReference r1 = (java.lang.ref.WeakReference) r1
            java.lang.Object r1 = r1.get()
            com.android.keyguard.KeyguardUpdateMonitorCallback r1 = (com.android.keyguard.KeyguardUpdateMonitorCallback) r1
            if (r1 == 0) goto L_0x01ba
            r1.onSimStateChanged(r9, r10, r11)
        L_0x01ba:
            int r0 = r0 + 1
            goto L_0x019f
        L_0x01bd:
            r8.mIsRecentlySkipped = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.handleSimStateChange(int, int, int):void");
    }

    /* access modifiers changed from: private */
    public void updateSwitchBootAnimation(int i, int i2, String str) {
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        if ("tmo".equals(roCarrier) || "boost".equals(roCarrier)) {
            if (DEBUG) {
                Log.d("KeyguardUpdateMonitor", "State = " + str);
            }
            if ("LOADED".equals(str)) {
                SystemProperties.set("persist.switch.bootanimation", getSubCarrierType(i));
            } else if ("ABSENT".equals(str)) {
                SystemProperties.set("persist.switch.bootanimation", "0");
            }
        }
    }

    private String getSubCarrierType(int i) {
        String mccMnc = getMccMnc(i);
        String groupIdLevel1 = this.mTelephonyManager.createForSubscriptionId(i).getGroupIdLevel1();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "The MccMnc = " + mccMnc + " gid1 = " + groupIdLevel1);
        }
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        if (!"tmo".equals(roCarrier)) {
            return (!"boost".equals(roCarrier) || !Arrays.stream(this.mBoostSubIMSIsGid1).anyMatch(new KeyguardUpdateMonitor$$ExternalSyntheticLambda16(mccMnc, groupIdLevel1))) ? "0" : "5";
        }
        if (Arrays.stream(this.mSprintSubIMSIs).anyMatch(new KeyguardUpdateMonitor$$ExternalSyntheticLambda12(mccMnc))) {
            return "1";
        }
        if (Arrays.stream(this.mTmoSubIMSIs).anyMatch(new KeyguardUpdateMonitor$$ExternalSyntheticLambda11(mccMnc)) || Arrays.stream(this.mTmoSubIMSIsGid1).anyMatch(new KeyguardUpdateMonitor$$ExternalSyntheticLambda14(mccMnc, groupIdLevel1))) {
            return "4";
        }
        if (Arrays.stream(this.mMetroSubIMSIsGid1).anyMatch(new KeyguardUpdateMonitor$$ExternalSyntheticLambda15(mccMnc, groupIdLevel1))) {
            return "2";
        }
        if (Arrays.stream(this.mAssuranceSubIMSIsGid1).anyMatch(new KeyguardUpdateMonitor$$ExternalSyntheticLambda13(mccMnc, groupIdLevel1))) {
            return "3";
        }
        return "0";
    }

    private String getMccMnc(int i) {
        SubscriptionInfo activeSubscriptionInfo;
        SubscriptionManager subscriptionManager = this.mSubscriptionManager;
        if (subscriptionManager == null || (activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(i)) == null) {
            return null;
        }
        return activeSubscriptionInfo.getMccString() + activeSubscriptionInfo.getMncString();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void handleServiceStateChange(int i, ServiceState serviceState) {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleServiceStateChange(subId=" + i + ", serviceState=" + serviceState);
        }
        int i2 = 0;
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            Log.w("KeyguardUpdateMonitor", "invalid subId in handleServiceStateChange()");
            while (i2 < this.mCallbacks.size()) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onServiceStateChanged(i, serviceState);
                }
                i2++;
            }
            return;
        }
        updateTelephonyCapable(true);
        this.mServiceStates.put(Integer.valueOf(i), serviceState);
        while (i2 < this.mCallbacks.size()) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback2 = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback2 != null) {
                keyguardUpdateMonitorCallback2.onRefreshCarrierInfo();
                keyguardUpdateMonitorCallback2.onServiceStateChanged(i, serviceState);
            }
            i2++;
        }
    }

    public boolean isOOS() {
        int phoneCount = TelephonyManager.getDefault().getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            int[] subId = SubscriptionManager.getSubId(i);
            if (subId != null && subId.length >= 1) {
                boolean z = DEBUG;
                if (z) {
                    Log.d("KeyguardUpdateMonitor", "isOOS slot id:" + i + " subId:" + subId[0]);
                }
                ServiceState serviceState = this.mServiceStates.get(Integer.valueOf(subId[0]));
                if (serviceState != null) {
                    if (serviceState.isEmergencyOnly()) {
                        if (z) {
                            Log.d("KeyguardUpdateMonitor", "isEmergencyOnly is true");
                        }
                        return false;
                    } else if (!(serviceState.getVoiceRegState() == 1 || serviceState.getVoiceRegState() == 3)) {
                        if (z) {
                            Log.d("KeyguardUpdateMonitor", "is emergency: " + serviceState.isEmergencyOnly() + "voice state: " + serviceState.getVoiceRegState());
                        }
                        return false;
                    }
                } else if (z) {
                    Log.d("KeyguardUpdateMonitor", "state is NULL");
                }
            }
        }
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "out of service");
        }
        return true;
    }

    public boolean isKeyguardVisible() {
        return this.mKeyguardIsVisible;
    }

    public void onKeyguardVisibilityChanged(boolean z) {
        if (!z && this.mKeyguardGoingAway) {
            this.mKeyguardGoingAway = false;
        }
        Assert.isMainThread();
        Log.d("KeyguardUpdateMonitor", "onKeyguardVisibilityChanged(" + z + ")");
        this.mKeyguardIsVisible = z;
        if (z) {
            this.mSecureCameraLaunched = false;
        }
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        if (keyguardBypassController != null) {
            keyguardBypassController.setUserHasDeviceEntryIntent(false);
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(z);
            }
        }
        if (this.mUseMotoFaceUnlock) {
            if (this.mKeyguardIsVisible) {
                setCameraGestureTriggered(false);
            } else {
                unRegisterLTVSensor();
            }
        }
        updateBiometricListeningState();
    }

    public void setKeyguardBouncerShowingForFPS(boolean z) {
        this.mBouncer = z;
    }

    public void onKeyguardShowingChanged(boolean z) {
        this.mKeyguardIsShowing = z;
        if (!z) {
            mFingerprintUnlockAttempts = 0;
            mFingerprintUnlockErrors = 0;
            this.mFPSGatedByStowed = false;
            updateFingerprintListeningState();
        }
        updateStowedSensorState();
        if (DesktopFeature.isDesktopSupported()) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onKeyguardShowingChanged(z);
                }
            }
        }
    }

    private void onKeyguardDeviceInteractiveChanged() {
        if (this.mDeviceInteractive && !this.mKeyguardIsShowing) {
            mFingerprintUnlockAttempts = 0;
            mFingerprintUnlockErrors = 0;
            this.mFPSGatedByStowed = false;
        }
        updateStowedSensorState();
    }

    private void updateStowedSensorState() {
        Sensor sensor = this.mStowedSensor;
        if (sensor == null) {
            return;
        }
        if (this.mKeyguardIsShowing || !this.mDeviceInteractive) {
            if (!this.mIsStowedSensorRegistered) {
                this.mSm.registerListener(this.mStowedListener, sensor, 3);
                this.mIsStowedSensorRegistered = true;
            }
        } else if (this.mIsStowedSensorRegistered) {
            this.mSm.unregisterListener(this.mStowedListener, sensor);
            this.mIsStowedSensorRegistered = false;
        }
    }

    /* access modifiers changed from: private */
    public void handleKeyguardReset() {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleKeyguardReset");
        }
        updateBiometricListeningState();
        this.mNeedsSlowUnlockTransition = resolveNeedsSlowUnlockTransition();
    }

    private boolean resolveNeedsSlowUnlockTransition() {
        if (isUserUnlocked(getCurrentUser())) {
            return false;
        }
        ResolveInfo resolveActivityAsUser = this.mContext.getPackageManager().resolveActivityAsUser(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0, getCurrentUser());
        if (resolveActivityAsUser != null) {
            return FALLBACK_HOME_COMPONENT.equals(resolveActivityAsUser.getComponentInfo().getComponentName());
        }
        Log.w("KeyguardUpdateMonitor", "resolveNeedsSlowUnlockTransition: returning false since activity could not be resolved.");
        return false;
    }

    /* access modifiers changed from: private */
    public void handleKeyguardBouncerChanged(int i) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "handleKeyguardBouncerChanged(" + i + ")");
        }
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mBouncer = z;
        if (z) {
            this.mSecureCameraLaunched = false;
        } else {
            this.mCredentialAttempted = false;
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i2).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onKeyguardBouncerChanged(this.mBouncer);
            }
        }
        updateBiometricListeningState();
    }

    /* access modifiers changed from: private */
    public void handleRequireUnlockForNfc() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onRequireUnlockForNfc();
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleReportEmergencyCallAction() {
        Assert.isMainThread();
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onEmergencyCallAction();
            }
        }
    }

    private boolean isBatteryUpdateInteresting(BatteryStatus batteryStatus, BatteryStatus batteryStatus2) {
        boolean isPluggedIn = batteryStatus2.isPluggedIn();
        boolean isPluggedIn2 = batteryStatus.isPluggedIn();
        boolean z = isPluggedIn2 && isPluggedIn && batteryStatus.status != batteryStatus2.status;
        boolean z2 = batteryStatus2.present;
        boolean z3 = batteryStatus.present;
        if (isPluggedIn2 == isPluggedIn && !z && batteryStatus.level == batteryStatus2.level) {
            return ((!isPluggedIn || batteryStatus2.maxChargingWattage == batteryStatus.maxChargingWattage) && z3 == z2 && batteryStatus2.health == batteryStatus.health) ? false : true;
        }
        return true;
    }

    private boolean isAutomotive() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }

    public void removeCallback(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.v("KeyguardUpdateMonitor", "*** unregister callback for " + keyguardUpdateMonitorCallback);
        }
        this.mCallbacks.removeIf(new KeyguardUpdateMonitor$$ExternalSyntheticLambda10(keyguardUpdateMonitorCallback));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$removeCallback$14(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback, WeakReference weakReference) {
        return weakReference.get() == keyguardUpdateMonitorCallback;
    }

    public void registerCallback(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.v("KeyguardUpdateMonitor", "*** register callback for " + keyguardUpdateMonitorCallback + "; size = " + this.mCallbacks.size());
        }
        int i = 0;
        while (i < this.mCallbacks.size()) {
            if (this.mCallbacks.get(i).get() != keyguardUpdateMonitorCallback) {
                i++;
            } else if (DEBUG) {
                Log.e("KeyguardUpdateMonitor", "Object tried to add another callback", new Exception("Called by"));
                return;
            } else {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference(keyguardUpdateMonitorCallback));
        sendUpdates(keyguardUpdateMonitorCallback);
        this.mHandler.post(new Runnable() {
            public void run() {
                KeyguardUpdateMonitor.this.removeCallback((KeyguardUpdateMonitorCallback) null);
            }
        });
    }

    public void setKeyguardBypassController(KeyguardBypassController keyguardBypassController) {
        this.mKeyguardBypassController = keyguardBypassController;
    }

    public boolean isSwitchingUser() {
        return this.mSwitchingUser;
    }

    public void setSwitchingUser(boolean z) {
        this.mSwitchingUser = z;
        this.mHandler.post(this.mUpdateBiometricListeningState);
    }

    private void sendUpdates(KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback) {
        keyguardUpdateMonitorCallback.onRefreshBatteryInfo(this.mBatteryStatus);
        keyguardUpdateMonitorCallback.onTimeChanged();
        keyguardUpdateMonitorCallback.onRingerModeChanged(this.mRingMode);
        keyguardUpdateMonitorCallback.onPhoneStateChanged(this.mPhoneState);
        keyguardUpdateMonitorCallback.onRefreshCarrierInfo();
        keyguardUpdateMonitorCallback.onClockVisibilityChanged();
        keyguardUpdateMonitorCallback.onKeyguardVisibilityChangedRaw(this.mKeyguardIsVisible);
        keyguardUpdateMonitorCallback.onTelephonyCapable(this.mTelephonyCapable);
        keyguardUpdateMonitorCallback.onLockScreenModeChanged(this.mLockScreenMode);
        for (Map.Entry<Integer, SimData> value : this.mSimDatas.entrySet()) {
            SimData simData = (SimData) value.getValue();
            keyguardUpdateMonitorCallback.onSimStateChanged(simData.subId, simData.slotId, simData.simState);
        }
    }

    public void sendKeyguardReset() {
        this.mHandler.obtainMessage(312).sendToTarget();
    }

    public void sendKeyguardBouncerChanged(boolean z) {
        if (DEBUG) {
            Log.d("KeyguardUpdateMonitor", "sendKeyguardBouncerChanged(" + z + ")");
        }
        Message obtainMessage = this.mHandler.obtainMessage(322);
        obtainMessage.arg1 = z ? 1 : 0;
        obtainMessage.sendToTarget();
    }

    public void reportSimUnlocked(int i) {
        Log.v("KeyguardUpdateMonitor", "reportSimUnlocked(subId=" + i + ")");
        handleSimStateChange(i, getSlotId(i), 5);
        if (getSkippedSubId() != -1) {
            for (SubscriptionInfo subscriptionId : getSubscriptionInfo(false)) {
                int subscriptionId2 = subscriptionId.getSubscriptionId();
                int simState = getSimState(subscriptionId2);
                if (simState != 2 && simState != 3) {
                    Log.i("KeyguardUpdateMonitor", "Default SMS subId is modified to " + subscriptionId2);
                    this.mSubscriptionManager.setDefaultSmsSubId(subscriptionId2);
                    return;
                }
            }
        }
    }

    public void reportEmergencyCallAction(boolean z) {
        if (!z) {
            this.mHandler.obtainMessage(318).sendToTarget();
            return;
        }
        Assert.isMainThread();
        handleReportEmergencyCallAction();
    }

    public boolean isDeviceProvisioned() {
        return this.mDeviceProvisioned;
    }

    public ServiceState getServiceState(int i) {
        return this.mServiceStates.get(Integer.valueOf(i));
    }

    public void clearBiometricRecognized() {
        Assert.isMainThread();
        this.mRdpAuthenticated = false;
        this.mUserFingerprintAuthenticated.clear();
        this.mUserFaceAuthenticated.clear();
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FINGERPRINT);
        this.mTrustManager.clearAllBiometricRecognized(BiometricSourceType.FACE);
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onBiometricsCleared();
            }
        }
    }

    public boolean isSimPinVoiceSecure() {
        return isSimPinSecure();
    }

    public boolean isSimPinSecure(boolean z) {
        for (SubscriptionInfo subscriptionId : getSubscriptionInfo(z)) {
            int subscriptionId2 = subscriptionId.getSubscriptionId();
            if (isSimPinSecure(getSimState(subscriptionId2)) && !isSubIdSkipped(subscriptionId2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSimPinSecure() {
        return isSimPinSecure(false);
    }

    public boolean isSimPinOrPuk() {
        for (SubscriptionInfo subscriptionId : getSubscriptionInfo(false)) {
            int subscriptionId2 = subscriptionId.getSubscriptionId();
            if (isSimPinOrPuk(getSimState(subscriptionId2)) && !isSubIdSkipped(subscriptionId2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSimReady() {
        for (SubscriptionInfo subscriptionId : getSubscriptionInfo(false)) {
            try {
                if (IccCardConstants.State.intToState(getSimState(subscriptionId.getSubscriptionId())) == IccCardConstants.State.READY) {
                    return true;
                }
            } catch (IllegalArgumentException unused) {
            }
        }
        return false;
    }

    public void updateSimPinLanguage() {
        if (KeyguardPrefs.getBoolean(this.mContext, "SimlocalSettings", false)) {
            return;
        }
        if (isUserChangedLocale(this.mContext) || isSimReady() || isDeviceProvisionedInSettingsDb()) {
            KeyguardPrefs.putBoolean(this.mContext, "SimlocalSettings", true);
            Log.i("KeyguardUpdateMonitor", "onSubscriptionsChanged isUserChangedLocale or isSimReady; isDeviceProvisionedInSettingsDb = " + isDeviceProvisionedInSettingsDb());
        } else if (!isSimPinSecure()) {
            Log.i("KeyguardUpdateMonitor", "onSubscriptionsChanged !isSimPinSecure");
        } else {
            boolean isSimLocalePresent = isSimLocalePresent((TelephonyManager) this.mContext.getSystemService("phone"));
            if (isSimLocalePresent) {
                KeyguardPrefs.putBoolean(this.mContext, "SimlocalSettings", true);
            }
            Log.i("KeyguardUpdateMonitor", "onSubscriptionsChanged SIM local present:" + isSimLocalePresent);
        }
    }

    public int getSimState(int i) {
        if (this.mSimDatas.containsKey(Integer.valueOf(i))) {
            return this.mSimDatas.get(Integer.valueOf(i)).simState;
        }
        return 0;
    }

    private int getSlotId(int i) {
        if (!this.mSimDatas.containsKey(Integer.valueOf(i))) {
            refreshSimState(i, SubscriptionManager.getSlotIndex(i));
        }
        return this.mSimDatas.get(Integer.valueOf(i)).slotId;
    }

    private boolean refreshSimState(int i, int i2) {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        boolean z = false;
        int simState = telephonyManager != null ? telephonyManager.getSimState(i2) : 0;
        SimData simData = this.mSimDatas.get(Integer.valueOf(i));
        if (simData == null) {
            this.mSimDatas.put(Integer.valueOf(i), new SimData(simState, i2, i));
            return true;
        } else if (SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        } else {
            if (simData.simState != simState) {
                z = true;
            }
            simData.simState = simState;
            return z;
        }
    }

    public void dispatchStartedWakingUp() {
        synchronized (this) {
            this.mDeviceInteractive = true;
        }
        this.mHandler.sendEmptyMessage(319);
    }

    public void dispatchStartedGoingToSleep(int i) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(321, i, 0));
    }

    public void dispatchFinishedGoingToSleep(int i) {
        synchronized (this) {
            this.mDeviceInteractive = false;
        }
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(320, i, 0));
    }

    public void dispatchScreenTurnedOn() {
        synchronized (this) {
            this.mScreenOn = true;
        }
        this.mHandler.sendEmptyMessage(331);
    }

    public void dispatchScreenTurnedOff() {
        synchronized (this) {
            this.mScreenOn = false;
        }
        this.mHandler.sendEmptyMessage(332);
    }

    public void dispatchDreamingStarted() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(333, 1, 0));
    }

    public void dispatchDreamingStopped() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(333, 0, 0));
    }

    public void dispatchKeyguardGoingAway(boolean z) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(342, Boolean.valueOf(z)));
    }

    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }

    public boolean isGoingToSleep() {
        return this.mGoingToSleep;
    }

    public int getNextSubIdForState(int i) {
        List<SubscriptionInfo> subscriptionInfo = getSubscriptionInfo(false);
        int i2 = -1;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < subscriptionInfo.size(); i4++) {
            int subscriptionId = subscriptionInfo.get(i4).getSubscriptionId();
            int slotId = getSlotId(subscriptionId);
            if (i == getSimState(subscriptionId) && i3 > slotId && (!(i == 2 || i == 3) || !isSubIdSkipped(subscriptionId))) {
                i2 = subscriptionId;
                i3 = slotId;
            }
        }
        return i2;
    }

    public SubscriptionInfo getSubscriptionInfoForSubId(int i) {
        List<SubscriptionInfo> subscriptionInfo = getSubscriptionInfo(false);
        for (int i2 = 0; i2 < subscriptionInfo.size(); i2++) {
            SubscriptionInfo subscriptionInfo2 = subscriptionInfo.get(i2);
            if (i == subscriptionInfo2.getSubscriptionId()) {
                return subscriptionInfo2;
            }
        }
        return null;
    }

    public boolean isLogoutEnabled() {
        return this.mLogoutEnabled;
    }

    /* access modifiers changed from: private */
    public void updateLogoutEnabled() {
        Assert.isMainThread();
        boolean isLogoutEnabled = this.mDevicePolicyManager.isLogoutEnabled();
        if (this.mLogoutEnabled != isLogoutEnabled) {
            this.mLogoutEnabled = isLogoutEnabled;
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
                if (keyguardUpdateMonitorCallback != null) {
                    keyguardUpdateMonitorCallback.onLogoutEnabledChanged();
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x02d9  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x02db  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x02f6  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x03e4  */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.io.FileDescriptor r18, java.io.PrintWriter r19, java.lang.String[] r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r19
            java.lang.String r2 = "KeyguardUpdateMonitor state:"
            r1.println(r2)
            java.lang.String r2 = "  SIM States:"
            r1.println(r2)
            java.util.HashMap<java.lang.Integer, com.android.keyguard.KeyguardUpdateMonitor$SimData> r2 = r0.mSimDatas
            java.util.Collection r2 = r2.values()
            java.util.Iterator r2 = r2.iterator()
        L_0x0018:
            boolean r3 = r2.hasNext()
            java.lang.String r4 = "    "
            if (r3 == 0) goto L_0x003d
            java.lang.Object r3 = r2.next()
            com.android.keyguard.KeyguardUpdateMonitor$SimData r3 = (com.android.keyguard.KeyguardUpdateMonitor.SimData) r3
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            java.lang.String r3 = r3.toString()
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            r1.println(r3)
            goto L_0x0018
        L_0x003d:
            java.lang.String r2 = "  Subs:"
            r1.println(r2)
            java.util.List<android.telephony.SubscriptionInfo> r2 = r0.mSubscriptionInfo
            if (r2 == 0) goto L_0x006a
            r2 = 0
        L_0x0047:
            java.util.List<android.telephony.SubscriptionInfo> r5 = r0.mSubscriptionInfo
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x006a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            java.util.List<android.telephony.SubscriptionInfo> r6 = r0.mSubscriptionInfo
            java.lang.Object r6 = r6.get(r2)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r1.println(r5)
            int r2 = r2 + 1
            goto L_0x0047
        L_0x006a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "  Current active data subId="
            r2.append(r5)
            int r5 = r0.mActiveMobileDataSubscription
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r1.println(r2)
            java.lang.String r2 = "  Service states:"
            r1.println(r2)
            java.util.HashMap<java.lang.Integer, android.telephony.ServiceState> r2 = r0.mServiceStates
            java.util.Set r2 = r2.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x008f:
            boolean r5 = r2.hasNext()
            if (r5 == 0) goto L_0x00c4
            java.lang.Object r5 = r2.next()
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r4)
            r6.append(r5)
            java.lang.String r7 = "="
            r6.append(r7)
            java.util.HashMap<java.lang.Integer, android.telephony.ServiceState> r7 = r0.mServiceStates
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            java.lang.Object r5 = r7.get(r5)
            r6.append(r5)
            java.lang.String r5 = r6.toString()
            r1.println(r5)
            goto L_0x008f
        L_0x00c4:
            android.hardware.fingerprint.FingerprintManager r2 = r0.mFpm
            java.lang.String r4 = "    enabledByUser="
            java.lang.String r5 = "    trustManaged="
            java.lang.String r6 = "    strongAuthFlags="
            java.lang.String r7 = "    listening: actual="
            java.lang.String r8 = "    possible="
            java.lang.String r9 = "    disabled(DPM)="
            java.lang.String r10 = "    authSinceBoot="
            java.lang.String r11 = "    auth'd="
            java.lang.String r12 = "    allowed="
            java.lang.String r13 = ")"
            if (r2 == 0) goto L_0x028f
            boolean r2 = r2.isHardwareDetected()
            if (r2 == 0) goto L_0x028f
            int r2 = android.app.ActivityManager.getCurrentUser()
            com.android.keyguard.KeyguardUpdateMonitor$StrongAuthTracker r15 = r0.mStrongAuthTracker
            int r15 = r15.getStrongAuthForUser(r2)
            android.util.SparseArray<com.android.keyguard.KeyguardUpdateMonitor$BiometricAuthenticated> r3 = r0.mUserFingerprintAuthenticated
            java.lang.Object r3 = r3.get(r2)
            com.android.keyguard.KeyguardUpdateMonitor$BiometricAuthenticated r3 = (com.android.keyguard.KeyguardUpdateMonitor.BiometricAuthenticated) r3
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r16 = r4
            java.lang.String r4 = "  Fingerprint state (user="
            r14.append(r4)
            r14.append(r2)
            r14.append(r13)
            java.lang.String r4 = r14.toString()
            r1.println(r4)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            if (r3 == 0) goto L_0x0123
            boolean r14 = r3.mIsStrongBiometric
            boolean r14 = r0.isUnlockingWithBiometricAllowed(r14)
            if (r14 == 0) goto L_0x0123
            r14 = 1
            goto L_0x0124
        L_0x0123:
            r14 = 0
        L_0x0124:
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            r1.println(r4)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r11)
            if (r3 == 0) goto L_0x0140
            boolean r3 = r3.mAuthenticated
            if (r3 == 0) goto L_0x0140
            r3 = 1
            goto L_0x0141
        L_0x0140:
            r3 = 0
        L_0x0141:
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            com.android.keyguard.KeyguardUpdateMonitor$StrongAuthTracker r4 = r17.getStrongAuthTracker()
            boolean r4 = r4.hasUserAuthenticatedSinceBoot()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r9)
            boolean r4 = r0.isFingerprintDisabled(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r8)
            boolean r4 = r0.isUnlockWithFingerprintPossible(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r7)
            int r4 = r0.mFingerprintRunningState
            r3.append(r4)
            java.lang.String r4 = " expected="
            r3.append(r4)
            boolean r4 = r17.isUdfpsEnrolled()
            boolean r4 = r0.shouldListenForFingerprint(r4)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            java.lang.String r4 = java.lang.Integer.toHexString(r15)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            boolean r4 = r0.getUserTrustIsManaged(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "    udfpsEnrolled="
            r3.append(r4)
            boolean r4 = r17.isUdfpsEnrolled()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "    mFingerprintLockedOut="
            r3.append(r4)
            boolean r4 = r0.mFingerprintLockedOut
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "    mFingerprintLockedOutPermanent="
            r3.append(r4)
            boolean r4 = r0.mFingerprintLockedOutPermanent
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = r16
            r3.append(r4)
            android.util.SparseBooleanArray r14 = r0.mBiometricEnabledForUser
            boolean r2 = r14.get(r2)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r1.println(r2)
            boolean r2 = r17.isUdfpsEnrolled()
            if (r2 == 0) goto L_0x028f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "        shouldListenForUdfps="
            r2.append(r3)
            r3 = 1
            boolean r14 = r0.shouldListenForFingerprint(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r1.println(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "        bouncerVisible="
            r2.append(r14)
            boolean r14 = r0.mBouncer
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r1.println(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "        mStatusBarState="
            r2.append(r14)
            int r14 = r0.mStatusBarState
            java.lang.String r14 = com.android.systemui.statusbar.StatusBarState.toShortString(r14)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            r1.println(r2)
            goto L_0x0290
        L_0x028f:
            r3 = 1
        L_0x0290:
            android.hardware.face.FaceManager r2 = r0.mFaceManager
            if (r2 == 0) goto L_0x03db
            boolean r2 = r2.isHardwareDetected()
            if (r2 == 0) goto L_0x03db
            int r2 = android.app.ActivityManager.getCurrentUser()
            com.android.keyguard.KeyguardUpdateMonitor$StrongAuthTracker r14 = r0.mStrongAuthTracker
            int r14 = r14.getStrongAuthForUser(r2)
            android.util.SparseArray<com.android.keyguard.KeyguardUpdateMonitor$BiometricAuthenticated> r15 = r0.mUserFaceAuthenticated
            java.lang.Object r15 = r15.get(r2)
            com.android.keyguard.KeyguardUpdateMonitor$BiometricAuthenticated r15 = (com.android.keyguard.KeyguardUpdateMonitor.BiometricAuthenticated) r15
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r16 = r4
            java.lang.String r4 = "  Face authentication state (user="
            r3.append(r4)
            r3.append(r2)
            r3.append(r13)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r12)
            if (r15 == 0) goto L_0x02db
            boolean r4 = r15.mIsStrongBiometric
            boolean r4 = r0.isUnlockingWithBiometricAllowed(r4)
            if (r4 == 0) goto L_0x02db
            r4 = 1
            goto L_0x02dc
        L_0x02db:
            r4 = 0
        L_0x02dc:
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r11)
            if (r15 == 0) goto L_0x02f8
            boolean r4 = r15.mAuthenticated
            if (r4 == 0) goto L_0x02f8
            r4 = 1
            goto L_0x02f9
        L_0x02f8:
            r4 = 0
        L_0x02f9:
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            com.android.keyguard.KeyguardUpdateMonitor$StrongAuthTracker r4 = r17.getStrongAuthTracker()
            boolean r4 = r4.hasUserAuthenticatedSinceBoot()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r9)
            boolean r4 = r0.isFaceDisabled(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r8)
            boolean r4 = r0.isUnlockWithFacePossible(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r7)
            int r4 = r0.mFaceRunningState
            r3.append(r4)
            java.lang.String r4 = " expected=("
            r3.append(r4)
            boolean r4 = r17.shouldListenForFace()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            java.lang.String r4 = java.lang.Integer.toHexString(r14)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            boolean r4 = r0.getUserTrustIsManaged(r2)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "    mFaceLockedOutPermanent="
            r3.append(r4)
            boolean r4 = r0.mFaceLockedOutPermanent
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r1.println(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = r16
            r3.append(r4)
            android.util.SparseBooleanArray r4 = r0.mBiometricEnabledForUser
            boolean r2 = r4.get(r2)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r1.println(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "    mSecureCameraLaunched="
            r2.append(r3)
            boolean r3 = r0.mSecureCameraLaunched
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.println(r2)
        L_0x03db:
            com.android.keyguard.KeyguardListenQueue r2 = r0.mListenModels
            r2.print(r1)
            boolean r0 = r0.mIsAutomotive
            if (r0 == 0) goto L_0x03e9
            java.lang.String r0 = "  Running on Automotive build"
            r1.println(r0)
        L_0x03e9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardUpdateMonitor.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    public void handleExternalAuthenticated() {
        this.mRdpAuthenticated = true;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            KeyguardUpdateMonitorCallback keyguardUpdateMonitorCallback = (KeyguardUpdateMonitorCallback) this.mCallbacks.get(i).get();
            if (keyguardUpdateMonitorCallback != null) {
                keyguardUpdateMonitorCallback.onExternalUnlockAuthenticated();
            }
        }
    }

    public boolean getRdpAuthenticateState() {
        return this.mRdpAuthenticated;
    }

    public void setKeyguardDisplayManager(KeyguardDisplayManager keyguardDisplayManager) {
        this.mKeyguardDisplayManager = keyguardDisplayManager;
    }

    public void showKeyguardPresentation() {
        this.mHandler.sendEmptyMessage(600);
    }

    public void hideKeyguardPresentation() {
        this.mHandler.sendEmptyMessage(601);
    }

    public void setSecurityCallback(KeyguardSecurityContainer.SecurityCallback securityCallback) {
        this.mSecurityCallback = securityCallback;
    }

    public void dismissSecurity() {
        KeyguardSecurityContainer.SecurityCallback securityCallback = this.mSecurityCallback;
        if (securityCallback != null) {
            securityCallback.dismiss(true, getCurrentUser(), false);
        }
    }

    public boolean isLockScreenDisabled() {
        return this.mLockPatternUtils.isLockScreenDisabled(getCurrentUser());
    }

    public LockPatternUtils getLockPatternUtils() {
        return this.mLockPatternUtils;
    }

    private boolean shouldStartListenForSideFPS() {
        if (!this.mSideFpsTouchToUnlockEnabled) {
            return this.mWakingUp;
        }
        return true;
    }

    public void updateFingerprintListeningAfterPowerKeyDown() {
        boolean z = DEBUG;
        if (z) {
            Log.d("KeyguardUpdateMonitor", "updateFingerprintListeningAfterPowerKeyDown:  mDeviceInteractive=" + this.mDeviceInteractive + " mSideFpsTouchToUnlockEnabled=" + this.mSideFpsTouchToUnlockEnabled);
        }
        if (!this.mDeviceInteractive && !this.mSideFpsTouchToUnlockEnabled) {
            if (z) {
                Log.i("KeyguardUpdateMonitor", "start fingerprint by power key.");
            }
            this.mWakingUp = true;
            updateFingerprintListeningState();
        }
    }

    public boolean isCameraGestureTriggered() {
        return this.mCameraGestureTriggered;
    }

    public void setCameraGestureTriggered(boolean z) {
        this.mCameraGestureTriggered = z;
    }

    public void setFingerprintStateCallback(UdfpsView.Callback callback) {
        this.mFingerprintStateCallback = callback;
    }

    public void setRecentlySkipped(boolean z) {
        this.mIsRecentlySkipped = z;
    }
}
