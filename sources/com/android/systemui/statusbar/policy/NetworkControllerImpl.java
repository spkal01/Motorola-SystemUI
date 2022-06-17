package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkScoreManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.UiccAccessRule;
import android.util.Log;
import android.util.SparseArray;
import androidx.mediarouter.media.MediaRoute2Provider$$ExternalSyntheticLambda0;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.SignalIcon$MobileState;
import com.android.settingslib.Utils;
import com.android.settingslib.mobile.MobileMappings;
import com.android.settingslib.mobile.MobileStatusTracker;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.demomode.DemoMode;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.moto.NetworkConfig;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.moto.SimStates;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.WifiSignalController;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.util.CarrierConfigTracker;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

public class NetworkControllerImpl extends BroadcastReceiver implements NetworkController, DemoMode, DataUsageController.NetworkNameProvider, Dumpable {
    static final boolean CHATTY = Log.isLoggable("NetworkControllerChat", 3);
    static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("NetworkController", 3));
    /* access modifiers changed from: private */
    public static final SimpleDateFormat SSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    /* access modifiers changed from: private */
    public boolean forceUpdateNoSims;
    private final AccessPointControllerImpl mAccessPoints;
    private int mActiveMobileDataSubscription;
    private List<SubscriptionInfo> mActivitySubscriptions;
    private boolean mAirplaneMode;
    private final Executor mBgExecutor;
    private final Looper mBgLooper;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private BroadcastReceiver mCBEnableReceiver;
    private BroadcastReceiver mCBInfoReceiver;
    /* access modifiers changed from: private */
    public final CallbackHandler mCallbackHandler;
    private final CarrierConfigTracker mCarrierConfigTracker;
    private final Runnable mClearForceValidated;
    private MobileMappings.Config mConfig;
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    private final BitSet mConnectedTransports;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private List<SubscriptionInfo> mCurrentSubscriptions;
    private int mCurrentUserId;
    private final DataSaverController mDataSaverController;
    private final DataUsageController mDataUsageController;
    private MobileSignalController mDefaultSignalController;
    private boolean mDemoInetCondition;
    private final DemoModeController mDemoModeController;
    private WifiSignalController.WifiState mDemoWifiState;
    private final DumpManager mDumpManager;
    private BroadcastReceiver mEPDGReceiver;
    private int mEmergencySource;
    @VisibleForTesting
    final EthernetSignalController mEthernetSignalController;
    private final FeatureFlags mFeatureFlags;
    private boolean mForceCellularValidated;
    private final boolean mHasMobileDataFeature;
    private boolean mHasNoSubs;
    private final String[] mHistory;
    private int mHistoryIndex;
    /* access modifiers changed from: private */
    public boolean mInetCondition;
    private boolean mIsEmergency;
    /* access modifiers changed from: private */
    public NetworkCapabilities mLastDefaultNetworkCapabilities;
    @VisibleForTesting
    ServiceState mLastServiceState;
    private int mLayoutDirection;
    @VisibleForTesting
    boolean mListening;
    private Locale mLocale;
    private final Object mLock;
    @VisibleForTesting
    final SparseArray<MobileSignalController> mMobileSignalControllers;
    /* access modifiers changed from: private */
    public NetworkConfig mMotoConfig;
    private final MotoExtTelephonyManager mMotoExtTM;
    private NetworkStateTracker mNetworkTracker;
    /* access modifiers changed from: private */
    public boolean mNoDefaultNetwork;
    /* access modifiers changed from: private */
    public boolean mNoNetworksAvailable;
    private BroadcastReceiver mPasspointReceiver;
    private final TelephonyManager mPhone;
    private TelephonyCallback.ActiveDataSubscriptionIdListener mPhoneStateListener;
    private SimStates mPrevSimStates;
    private final boolean mProviderModelBehavior;
    private final boolean mProviderModelSetting;
    private final Handler mReceiverHandler;
    private final Runnable mRegisterListeners;
    private boolean mSimDetected;
    private SimStates mSimStates;
    private final MobileStatusTracker.SubscriptionDefaults mSubDefaults;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener;
    private final SubscriptionManager mSubscriptionManager;
    private final TelephonyListenerManager mTelephonyListenerManager;
    private boolean mUserSetup;
    private final CurrentUserTracker mUserTracker;
    private final BitSet mValidatedTransports;
    protected boolean mWifiConnected;
    protected boolean mWifiEnabled;
    /* access modifiers changed from: private */
    public final WifiManager mWifiManager;
    @VisibleForTesting
    final WifiSignalController mWifiSignalController;

    private class EPDGBroadcastReceiver extends BroadcastReceiver {
        private EPDGBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.i("NetworkController", "Receive ePDG intent: " + intent);
            NetworkControllerImpl.this.mWifiSignalController.handleEPDGBroadcast(intent);
        }
    }

    /* access modifiers changed from: private */
    public void registerEPDGReceiver() {
        if (this.mEPDGReceiver == null) {
            EPDGBroadcastReceiver ePDGBroadcastReceiver = new EPDGBroadcastReceiver();
            this.mEPDGReceiver = ePDGBroadcastReceiver;
            this.mContext.registerReceiver(ePDGBroadcastReceiver, new IntentFilter("com.motorola.internal.intent.action.EPDG_CONNECTION_STATE_CHANGED"), (String) null, this.mReceiverHandler);
        }
    }

    /* access modifiers changed from: private */
    public void unRegisterEPDGReceiver() {
        BroadcastReceiver broadcastReceiver = this.mEPDGReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mWifiSignalController.handleEPDGBroadcast(false);
            this.mEPDGReceiver = null;
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NetworkControllerImpl(android.content.Context r23, android.os.Looper r24, java.util.concurrent.Executor r25, android.telephony.SubscriptionManager r26, com.android.systemui.statusbar.policy.CallbackHandler r27, com.android.systemui.statusbar.policy.DeviceProvisionedController r28, com.android.systemui.broadcast.BroadcastDispatcher r29, android.net.ConnectivityManager r30, android.telephony.TelephonyManager r31, com.android.systemui.telephony.TelephonyListenerManager r32, android.net.wifi.WifiManager r33, android.net.NetworkScoreManager r34, com.android.systemui.statusbar.policy.AccessPointControllerImpl r35, com.android.systemui.demomode.DemoModeController r36, com.android.systemui.util.CarrierConfigTracker r37, com.android.systemui.statusbar.FeatureFlags r38, com.android.systemui.dump.DumpManager r39) {
        /*
            r22 = this;
            r14 = r22
            r0 = r22
            r1 = r23
            r9 = r24
            r10 = r25
            r7 = r26
            r11 = r27
            r15 = r28
            r16 = r29
            r2 = r30
            r3 = r31
            r4 = r32
            r5 = r33
            r6 = r34
            r12 = r35
            r17 = r36
            r18 = r37
            r19 = r38
            r20 = r39
            com.android.settingslib.mobile.MobileMappings$Config r8 = com.android.settingslib.mobile.MobileMappings.Config.readConfig(r23)
            com.android.settingslib.net.DataUsageController r13 = new com.android.settingslib.net.DataUsageController
            r24 = r13
            r14 = r23
            r21 = r0
            r0 = r24
            r0.<init>(r14)
            com.android.settingslib.mobile.MobileStatusTracker$SubscriptionDefaults r0 = new com.android.settingslib.mobile.MobileStatusTracker$SubscriptionDefaults
            r14 = r0
            r0.<init>()
            r0 = r21
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            r0 = r22
            android.os.Handler r1 = r0.mReceiverHandler
            java.lang.Runnable r0 = r0.mRegisterListeners
            r1.post(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.<init>(android.content.Context, android.os.Looper, java.util.concurrent.Executor, android.telephony.SubscriptionManager, com.android.systemui.statusbar.policy.CallbackHandler, com.android.systemui.statusbar.policy.DeviceProvisionedController, com.android.systemui.broadcast.BroadcastDispatcher, android.net.ConnectivityManager, android.telephony.TelephonyManager, com.android.systemui.telephony.TelephonyListenerManager, android.net.wifi.WifiManager, android.net.NetworkScoreManager, com.android.systemui.statusbar.policy.AccessPointControllerImpl, com.android.systemui.demomode.DemoModeController, com.android.systemui.util.CarrierConfigTracker, com.android.systemui.statusbar.FeatureFlags, com.android.systemui.dump.DumpManager):void");
    }

    @VisibleForTesting
    NetworkControllerImpl(Context context, ConnectivityManager connectivityManager, TelephonyManager telephonyManager, TelephonyListenerManager telephonyListenerManager, WifiManager wifiManager, NetworkScoreManager networkScoreManager, SubscriptionManager subscriptionManager, MobileMappings.Config config, Looper looper, Executor executor, CallbackHandler callbackHandler, AccessPointControllerImpl accessPointControllerImpl, DataUsageController dataUsageController, MobileStatusTracker.SubscriptionDefaults subscriptionDefaults, DeviceProvisionedController deviceProvisionedController, BroadcastDispatcher broadcastDispatcher, DemoModeController demoModeController, CarrierConfigTracker carrierConfigTracker, FeatureFlags featureFlags, DumpManager dumpManager) {
        Handler handler;
        Context context2 = context;
        WifiManager wifiManager2 = wifiManager;
        Looper looper2 = looper;
        CallbackHandler callbackHandler2 = callbackHandler;
        DataUsageController dataUsageController2 = dataUsageController;
        final DeviceProvisionedController deviceProvisionedController2 = deviceProvisionedController;
        this.mLock = new Object();
        this.mActiveMobileDataSubscription = -1;
        this.mMobileSignalControllers = new SparseArray<>();
        this.mConnectedTransports = new BitSet();
        this.mValidatedTransports = new BitSet();
        this.mAirplaneMode = false;
        this.mNoDefaultNetwork = false;
        this.mNoNetworksAvailable = true;
        this.forceUpdateNoSims = false;
        this.mLocale = null;
        this.mCurrentSubscriptions = new ArrayList();
        this.mActivitySubscriptions = new ArrayList();
        this.mHistory = new String[16];
        this.mPrevSimStates = null;
        this.mSimStates = null;
        this.mWifiEnabled = false;
        this.mWifiConnected = false;
        this.mConfigurationListener = new ConfigurationController.ConfigurationListener() {
            public void onConfigChanged(Configuration configuration) {
                NetworkControllerImpl.this.updateConfig();
            }

            public void onOverlayChanged() {
                if (NetworkControllerImpl.DEBUG) {
                    Log.i("NetworkController", "RROs onOverlayChanged");
                }
                NetworkControllerImpl.this.updateConfig();
                if (!NetworkControllerImpl.this.hasAnySim()) {
                    boolean unused = NetworkControllerImpl.this.forceUpdateNoSims = true;
                    NetworkControllerImpl.this.updateNoSims();
                }
                if (NetworkControllerImpl.this.mMotoConfig.showEPDGIndicator) {
                    NetworkControllerImpl.this.registerEPDGReceiver();
                } else {
                    NetworkControllerImpl.this.unRegisterEPDGReceiver();
                }
            }
        };
        this.mEPDGReceiver = null;
        this.mPasspointReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.i("NetworkController", "Receive passpoint intent: " + intent);
                NetworkControllerImpl.this.mWifiSignalController.handlePasspointBroadcast(intent);
            }
        };
        this.mClearForceValidated = new NetworkControllerImpl$$ExternalSyntheticLambda6(this);
        this.mCBEnableReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (NetworkControllerImpl.DEBUG) {
                    Log.i("NetworkController", "mCBEnableReceiver.onReceive: onReceive: " + intent + " action=" + action);
                }
                int intExtra = intent.getIntExtra("subscription", -1);
                if (SubscriptionManager.isValidSubscriptionId(intExtra) && NetworkControllerImpl.this.mMobileSignalControllers.indexOfKey(intExtra) >= 0) {
                    NetworkControllerImpl.this.mMobileSignalControllers.get(intExtra).handleBroadcast(intent);
                }
            }
        };
        this.mCBInfoReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (NetworkControllerImpl.DEBUG) {
                    Log.i("NetworkController", "mCBInfoReceiver: onReceive: " + intent + " action=" + action);
                }
                int i = -1;
                if ("com.android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED".equals(intent.getAction()) && intent.getExtras() != null) {
                    i = intent.getIntExtra("subId", -1);
                }
                if (SubscriptionManager.isValidSubscriptionId(i) && NetworkControllerImpl.this.mMobileSignalControllers.indexOfKey(i) >= 0) {
                    NetworkControllerImpl.this.mMobileSignalControllers.get(i).handleBroadcast(intent);
                }
            }
        };
        this.mRegisterListeners = new NetworkControllerImpl$$ExternalSyntheticLambda5(this);
        this.mContext = context2;
        this.mTelephonyListenerManager = telephonyListenerManager;
        NetworkConfig readConfig = NetworkConfig.readConfig(context);
        this.mMotoConfig = readConfig;
        this.mConfig = readConfig.mConfig;
        Handler handler2 = new Handler(looper2);
        this.mReceiverHandler = handler2;
        this.mBgLooper = looper2;
        this.mBgExecutor = executor;
        this.mCallbackHandler = callbackHandler2;
        this.mDataSaverController = new DataSaverControllerImpl(context2);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mSubscriptionManager = subscriptionManager;
        this.mSubDefaults = subscriptionDefaults;
        this.mConnectivityManager = connectivityManager;
        boolean isDataCapable = telephonyManager.isDataCapable();
        this.mHasMobileDataFeature = isDataCapable;
        this.mDemoModeController = demoModeController;
        this.mCarrierConfigTracker = carrierConfigTracker;
        this.mFeatureFlags = featureFlags;
        this.mDumpManager = dumpManager;
        this.mPhone = telephonyManager;
        this.mWifiManager = wifiManager2;
        this.mLayoutDirection = context.getResources().getConfiguration().getLayoutDirection();
        this.mLocale = context.getResources().getConfiguration().locale;
        this.mAccessPoints = accessPointControllerImpl;
        this.mDataUsageController = dataUsageController2;
        dataUsageController2.setNetworkController(this);
        dataUsageController2.setCallback(new DataUsageController.Callback() {
            public void onMobileDataEnabled(boolean z) {
                NetworkControllerImpl.this.mCallbackHandler.setMobileDataEnabled(z);
                NetworkControllerImpl.this.notifyControllersMobileDataChanged();
            }
        });
        Handler handler3 = handler2;
        WifiSignalController wifiSignalController = r0;
        WifiSignalController wifiSignalController2 = new WifiSignalController(context, isDataCapable, callbackHandler, this, wifiManager, connectivityManager, networkScoreManager, featureFlags);
        this.mWifiSignalController = wifiSignalController;
        this.mEthernetSignalController = new EthernetSignalController(context2, callbackHandler2, this);
        updateAirplaneMode(true);
        C20434 r0 = new CurrentUserTracker(broadcastDispatcher) {
            public void onUserSwitched(int i) {
                NetworkControllerImpl.this.onUserSwitched(i);
            }
        };
        this.mUserTracker = r0;
        r0.startTracking();
        deviceProvisionedController2.addCallback(new DeviceProvisionedController.DeviceProvisionedListener() {
            public void onUserSetupChanged() {
                NetworkControllerImpl networkControllerImpl = NetworkControllerImpl.this;
                DeviceProvisionedController deviceProvisionedController = deviceProvisionedController2;
                networkControllerImpl.setUserSetupComplete(deviceProvisionedController.isUserSetup(deviceProvisionedController.getCurrentUser()));
            }
        });
        C20456 r02 = new WifiManager.ScanResultsCallback() {
            public void onScanResultsAvailable() {
                boolean unused = NetworkControllerImpl.this.mNoNetworksAvailable = true;
                Iterator<ScanResult> it = NetworkControllerImpl.this.mWifiManager.getScanResults().iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (!it.next().SSID.equals(((WifiSignalController.WifiState) NetworkControllerImpl.this.mWifiSignalController.getState()).ssid)) {
                            boolean unused2 = NetworkControllerImpl.this.mNoNetworksAvailable = false;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (NetworkControllerImpl.this.mNoDefaultNetwork) {
                    NetworkControllerImpl.this.mCallbackHandler.setConnectivityStatus(NetworkControllerImpl.this.mNoDefaultNetwork, true ^ NetworkControllerImpl.this.mInetCondition, NetworkControllerImpl.this.mNoNetworksAvailable);
                }
            }
        };
        if (wifiManager2 != null) {
            Objects.requireNonNull(handler3);
            handler = handler3;
            wifiManager2.registerScanResultsCallback(new MediaRoute2Provider$$ExternalSyntheticLambda0(handler), r02);
        } else {
            handler = handler3;
        }
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(1) {
            private Network mLastNetwork;
            private NetworkCapabilities mLastNetworkCapabilities;

            public void onLost(Network network) {
                this.mLastNetwork = null;
                this.mLastNetworkCapabilities = null;
                NetworkCapabilities unused = NetworkControllerImpl.this.mLastDefaultNetworkCapabilities = null;
                NetworkControllerImpl.this.recordLastNetworkCallback(NetworkControllerImpl.SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + "onLost: " + "network=" + network);
                NetworkControllerImpl.this.updateConnectivity();
            }

            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                NetworkCapabilities networkCapabilities2 = this.mLastNetworkCapabilities;
                boolean z = networkCapabilities2 != null && networkCapabilities2.hasCapability(16);
                boolean hasCapability = networkCapabilities.hasCapability(16);
                if (network.equals(this.mLastNetwork) && hasCapability == z) {
                    int[] access$1800 = NetworkControllerImpl.this.getProcessedTransportTypes(networkCapabilities);
                    Arrays.sort(access$1800);
                    NetworkCapabilities networkCapabilities3 = this.mLastNetworkCapabilities;
                    int[] access$18002 = networkCapabilities3 != null ? NetworkControllerImpl.this.getProcessedTransportTypes(networkCapabilities3) : null;
                    if (access$18002 != null) {
                        Arrays.sort(access$18002);
                    }
                    if (Arrays.equals(access$1800, access$18002)) {
                        return;
                    }
                }
                this.mLastNetwork = network;
                this.mLastNetworkCapabilities = networkCapabilities;
                NetworkCapabilities unused = NetworkControllerImpl.this.mLastDefaultNetworkCapabilities = networkCapabilities;
                NetworkControllerImpl.this.recordLastNetworkCallback(NetworkControllerImpl.SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + "onCapabilitiesChanged: " + "network=" + network + "," + "networkCapabilities=" + networkCapabilities);
                NetworkControllerImpl.this.updateConnectivity();
            }
        }, handler);
        this.mPhoneStateListener = new NetworkControllerImpl$$ExternalSyntheticLambda0(this);
        demoModeController.addCallback((DemoMode) this);
        this.mProviderModelBehavior = featureFlags.isCombinedStatusBarSignalIconsEnabled();
        this.mProviderModelSetting = featureFlags.isProviderModelSettingEnabled();
        dumpManager.registerDumpable("NetworkController", this);
        this.mNetworkTracker = new NetworkStateTracker(telephonyManager, subscriptionManager, context, callbackHandler, this.mMotoConfig.enableEriSounds);
        this.mSimStates = new SimStates(telephonyManager);
        this.mMotoExtTM = new MotoExtTelephonyManager(context2);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this.mConfigurationListener);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i) {
        this.mBgExecutor.execute(new NetworkControllerImpl$$ExternalSyntheticLambda7(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        if (keepCellularValidationBitInSwitch(this.mActiveMobileDataSubscription, i)) {
            if (DEBUG) {
                Log.d("NetworkController", ": mForceCellularValidated to true.");
            }
            this.mForceCellularValidated = true;
            this.mReceiverHandler.removeCallbacks(this.mClearForceValidated);
            this.mReceiverHandler.postDelayed(this.mClearForceValidated, 2000);
        }
        this.mActiveMobileDataSubscription = i;
        doUpdateMobileControllers();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        if (DEBUG) {
            Log.d("NetworkController", ": mClearForceValidated");
        }
        this.mForceCellularValidated = false;
        updateConnectivity();
    }

    /* access modifiers changed from: package-private */
    public boolean isInGroupDataSwitch(int i, int i2) {
        SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(i);
        SubscriptionInfo activeSubscriptionInfo2 = this.mSubscriptionManager.getActiveSubscriptionInfo(i2);
        return (activeSubscriptionInfo == null || activeSubscriptionInfo2 == null || activeSubscriptionInfo.getGroupUuid() == null || !activeSubscriptionInfo.getGroupUuid().equals(activeSubscriptionInfo2.getGroupUuid())) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public boolean keepCellularValidationBitInSwitch(int i, int i2) {
        if (!this.mValidatedTransports.get(0) || !isInGroupDataSwitch(i, i2)) {
            return false;
        }
        return true;
    }

    public DataSaverController getDataSaverController() {
        return this.mDataSaverController;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    /* renamed from: registerListeners */
    public void lambda$new$5() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            this.mMobileSignalControllers.valueAt(i).registerListener();
        }
        if (this.mSubscriptionListener == null) {
            this.mSubscriptionListener = new SubListener(this.mBgLooper);
        }
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mTelephonyListenerManager.addActiveDataSubscriptionIdListener(this.mPhoneStateListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.intent.action.SERVICE_STATE");
        intentFilter.addAction("android.telephony.action.SERVICE_PROVIDERS_UPDATED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, this.mReceiverHandler);
        this.mListening = true;
        if (this.mMotoConfig.isCellBroadcastSupported) {
            this.mContext.registerReceiver(this.mCBEnableReceiver, new IntentFilter("com.motorola.cellbroadcastreceiver.CB_INFO_ON_SYSTEMUI"), "com.motorola.permission.CB_ENABLE", this.mReceiverHandler);
            this.mContext.registerReceiver(this.mCBInfoReceiver, new IntentFilter("com.android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED"), "android.permission.RECEIVE_EMERGENCY_BROADCAST", this.mReceiverHandler);
        }
        this.mReceiverHandler.post(new NetworkControllerImpl$$ExternalSyntheticLambda3(this));
        Handler handler = this.mReceiverHandler;
        WifiSignalController wifiSignalController = this.mWifiSignalController;
        Objects.requireNonNull(wifiSignalController);
        handler.post(new NetworkControllerImpl$$ExternalSyntheticLambda9(wifiSignalController));
        this.mReceiverHandler.post(new NetworkControllerImpl$$ExternalSyntheticLambda4(this));
        this.mContext.registerReceiver(this.mPasspointReceiver, new IntentFilter("intent.mot.passpoint.connected"), (String) null, this.mReceiverHandler);
        updateMobileControllers();
        updateActiveSubscriptions();
        ((DualSimIconController) Dependency.get(DualSimIconController.class)).updateActivitySubsCount(this.mActivitySubscriptions.size());
        this.mReceiverHandler.post(new NetworkControllerImpl$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$registerListeners$3() {
        if (this.mLastServiceState == null) {
            this.mLastServiceState = this.mPhone.getServiceState();
            if (this.mMobileSignalControllers.size() == 0) {
                recalculateEmergency();
            }
        }
    }

    private void unregisterListeners() {
        if (this.mListening) {
            this.mListening = false;
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                this.mMobileSignalControllers.valueAt(i).unregisterListener();
            }
            this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
            this.mBroadcastDispatcher.unregisterReceiver(this);
            this.mContext.unregisterReceiver(this.mPasspointReceiver);
        }
    }

    public DataUsageController getMobileDataController() {
        return this.mDataUsageController;
    }

    public void addCarrierLabel(NetworkStateTracker.PanelCarrierLabelListener panelCarrierLabelListener) {
        this.mCallbackHandler.setCarrierLabelListening(panelCarrierLabelListener, true);
        this.mCallbackHandler.updatePanelCarrierLabel();
    }

    private boolean isRejectCodeEnable() {
        if (this.mMobileSignalControllers.size() == 0) {
            return false;
        }
        int i = 0;
        while (i < this.mMobileSignalControllers.size()) {
            MobileSignalController valueAt = this.mMobileSignalControllers.valueAt(i);
            if (((SignalIcon$MobileState) valueAt.getState()).rejectCode != 17) {
                i++;
            } else if (!DEBUG) {
                return true;
            } else {
                Log.d("NetworkController", "Found rejectcode " + valueAt.mTag);
                return true;
            }
        }
        return false;
    }

    public void addLabelShortFormView(NetworkStateTracker.ShortFormLabelListener shortFormLabelListener) {
        this.mCallbackHandler.setOperaterNameListening(shortFormLabelListener, true);
        this.mCallbackHandler.updateShortFormLabel();
    }

    public void playEriSoundAfterBoot() {
        if (this.mMotoConfig.enableEriSounds) {
            this.mCallbackHandler.playEriSoundAfterBoot();
        }
    }

    public void updateEri(ServiceState serviceState, ServiceState serviceState2) {
        this.mCallbackHandler.updateEri(serviceState, serviceState2);
    }

    public String getNetworkSeparator() {
        return this.mMotoConfig.networkNameSeparator;
    }

    public SparseArray<MobileSignalController> getMobileSignalControllers() {
        return this.mMobileSignalControllers;
    }

    public void refreshShortFormLabel() {
        this.mCallbackHandler.updateShortFormLabel();
    }

    public void refreshPanelCarrierLabel() {
        this.mCallbackHandler.updatePanelCarrierLabel();
    }

    public boolean isShowActivityIconOnSB() {
        return this.mMotoConfig.enableActivityIconOnSB || MotoFeature.isPrcProduct();
    }

    public SimStates getSimStates() {
        return this.mSimStates;
    }

    public boolean isShowActivityIconOnQS() {
        return this.mMotoConfig.enableCustomActivityIconOnQS;
    }

    public boolean isShortFormLabelEnabled() {
        return this.mMotoConfig.networkNameShortFormSupported;
    }

    public boolean hasMobileDataFeature() {
        return this.mHasMobileDataFeature;
    }

    public boolean hasVoiceCallingFeature() {
        return this.mPhone.getPhoneType() != 0;
    }

    /* access modifiers changed from: private */
    public int[] getProcessedTransportTypes(NetworkCapabilities networkCapabilities) {
        int[] transportTypes = networkCapabilities.getTransportTypes();
        int i = 0;
        while (true) {
            if (i < transportTypes.length) {
                if (transportTypes[i] == 0 && Utils.tryGetWifiInfoForVcn(networkCapabilities) != null) {
                    transportTypes[i] = 1;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        return transportTypes;
    }

    private MobileSignalController getDataController() {
        return getControllerWithSubId(this.mSubDefaults.getDefaultDataSubId());
    }

    private MobileSignalController getControllerWithSubId(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            if (DEBUG) {
                Log.e("NetworkController", "No data sim selected");
            }
            return this.mDefaultSignalController;
        } else if (this.mMobileSignalControllers.indexOfKey(i) >= 0) {
            return this.mMobileSignalControllers.get(i);
        } else {
            if (DEBUG) {
                Log.e("NetworkController", "Cannot find controller for data sub: " + i);
            }
            return this.mDefaultSignalController;
        }
    }

    public String getMobileDataNetworkName() {
        MobileSignalController dataController = getDataController();
        return dataController != null ? ((SignalIcon$MobileState) dataController.getState()).networkNameData : "";
    }

    public int getNumberSubscriptions() {
        return this.mMobileSignalControllers.size();
    }

    /* access modifiers changed from: package-private */
    public boolean isDataControllerDisabled() {
        MobileSignalController dataController = getDataController();
        if (dataController == null) {
            return false;
        }
        return dataController.isDataDisabled();
    }

    /* access modifiers changed from: package-private */
    public boolean isCarrierMergedWifi(int i) {
        return this.mWifiSignalController.isCarrierMergedWifi(i);
    }

    /* access modifiers changed from: package-private */
    public boolean hasDefaultNetwork() {
        return !this.mNoDefaultNetwork;
    }

    /* access modifiers changed from: package-private */
    public boolean isEthernetDefault() {
        return this.mConnectedTransports.get(3);
    }

    /* access modifiers changed from: package-private */
    public String getNetworkNameForCarrierWiFi(int i) {
        MobileSignalController controllerWithSubId = getControllerWithSubId(i);
        return controllerWithSubId != null ? controllerWithSubId.getNetworkNameForCarrierWiFi() : "";
    }

    /* access modifiers changed from: package-private */
    public void notifyWifiLevelChange(int i) {
        for (int i2 = 0; i2 < this.mMobileSignalControllers.size(); i2++) {
            this.mMobileSignalControllers.valueAt(i2).notifyWifiLevelChange(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyDefaultMobileLevelChange(int i) {
        for (int i2 = 0; i2 < this.mMobileSignalControllers.size(); i2++) {
            this.mMobileSignalControllers.valueAt(i2).notifyDefaultMobileLevelChange(i);
        }
    }

    /* access modifiers changed from: private */
    public void notifyControllersMobileDataChanged() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            this.mMobileSignalControllers.valueAt(i).onMobileDataChanged();
        }
    }

    public boolean isEmergencyOnly() {
        if (this.mMobileSignalControllers.size() == 0) {
            this.mEmergencySource = 0;
            ServiceState serviceState = this.mLastServiceState;
            if (serviceState == null || !serviceState.isEmergencyOnly()) {
                return false;
            }
            return true;
        }
        int defaultVoiceSubId = this.mSubDefaults.getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                MobileSignalController valueAt = this.mMobileSignalControllers.valueAt(i);
                if (!((SignalIcon$MobileState) valueAt.getState()).isEmergency) {
                    this.mEmergencySource = valueAt.mSubscriptionInfo.getSubscriptionId() + 100;
                    if (DEBUG) {
                        Log.d("NetworkController", "Found emergency " + valueAt.mTag);
                    }
                    return false;
                }
            }
        }
        MobileSignalController mobileSignalController = this.mMobileSignalControllers.get(defaultVoiceSubId);
        if (mobileSignalController != null) {
            this.mEmergencySource = defaultVoiceSubId + 200;
            if (DEBUG) {
                Log.d("NetworkController", "Getting emergency from " + defaultVoiceSubId);
            }
            return ((SignalIcon$MobileState) mobileSignalController.getState()).isEmergency;
        } else if (this.mMobileSignalControllers.size() == 1) {
            this.mEmergencySource = this.mMobileSignalControllers.keyAt(0) + 400;
            if (DEBUG) {
                Log.d("NetworkController", "Getting assumed emergency from " + this.mMobileSignalControllers.keyAt(0));
            }
            return ((SignalIcon$MobileState) this.mMobileSignalControllers.valueAt(0).getState()).isEmergency;
        } else {
            if (DEBUG) {
                Log.e("NetworkController", "Cannot find controller for voice sub: " + defaultVoiceSubId);
            }
            this.mEmergencySource = defaultVoiceSubId + 300;
            return true;
        }
    }

    public boolean isRtl() {
        return this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    /* access modifiers changed from: package-private */
    public void recalculateEmergency() {
        boolean isEmergencyOnly = isEmergencyOnly();
        this.mIsEmergency = isEmergencyOnly;
        this.mCallbackHandler.setEmergencyCallsOnly(isEmergencyOnly && !isRejectCodeEnable());
    }

    public void addCallback(NetworkController.SignalCallback signalCallback) {
        signalCallback.setSubs(this.mCurrentSubscriptions);
        signalCallback.setIsAirplaneMode(new NetworkController.IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, R$string.accessibility_airplane_mode, this.mContext));
        signalCallback.setNoSims(this.mHasNoSubs, this.mSimDetected);
        if (this.mProviderModelSetting) {
            signalCallback.setConnectivityStatus(this.mNoDefaultNetwork, !this.mInetCondition, this.mNoNetworksAvailable);
        }
        this.mWifiSignalController.notifyListeners(signalCallback);
        this.mEthernetSignalController.notifyListeners(signalCallback);
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController valueAt = this.mMobileSignalControllers.valueAt(i);
            valueAt.notifyListeners(signalCallback);
            if (this.mProviderModelBehavior) {
                valueAt.refreshCallIndicator(signalCallback);
            }
        }
        this.mCallbackHandler.setListening(signalCallback, true);
    }

    public void removeCallback(NetworkController.SignalCallback signalCallback) {
        this.mCallbackHandler.setListening(signalCallback, false);
    }

    public void setWifiEnabled(final boolean z) {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                NetworkControllerImpl.this.mWifiManager.setWifiEnabled(z);
                return null;
            }
        }.execute(new Void[0]);
    }

    public void onWifiConnectionStateChanged(boolean z, boolean z2) {
        if (this.mWifiEnabled != z || this.mWifiConnected != z2) {
            this.mWifiEnabled = z;
            this.mWifiConnected = z2;
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                this.mMobileSignalControllers.valueAt(i).onWifiConnectionStateChanged(this.mWifiEnabled, this.mWifiConnected);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isWifiEnabled() {
        return this.mWifiEnabled;
    }

    /* access modifiers changed from: package-private */
    public boolean isWifiConnected() {
        return this.mWifiConnected;
    }

    /* access modifiers changed from: private */
    public void onUserSwitched(int i) {
        this.mCurrentUserId = i;
        this.mAccessPoints.onUserSwitched(i);
        updateConnectivity();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(android.content.Context r5, android.content.Intent r6) {
        /*
            r4 = this;
            boolean r5 = CHATTY
            java.lang.String r0 = "NetworkController"
            if (r5 == 0) goto L_0x001a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r1 = "onReceive: intent="
            r5.append(r1)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r0, r5)
        L_0x001a:
            java.lang.String r5 = r6.getAction()
            r5.hashCode()
            int r1 = r5.hashCode()
            r2 = -1
            r3 = 0
            switch(r1) {
                case -2104353374: goto L_0x006e;
                case -1465084191: goto L_0x0063;
                case -1172645946: goto L_0x0058;
                case -1138588223: goto L_0x004d;
                case -1076576821: goto L_0x0042;
                case -229777127: goto L_0x0037;
                case -25388475: goto L_0x002c;
                default: goto L_0x002a;
            }
        L_0x002a:
            r5 = r2
            goto L_0x0078
        L_0x002c:
            java.lang.String r1 = "android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x0035
            goto L_0x002a
        L_0x0035:
            r5 = 6
            goto L_0x0078
        L_0x0037:
            java.lang.String r1 = "android.intent.action.SIM_STATE_CHANGED"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x0040
            goto L_0x002a
        L_0x0040:
            r5 = 5
            goto L_0x0078
        L_0x0042:
            java.lang.String r1 = "android.intent.action.AIRPLANE_MODE"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x004b
            goto L_0x002a
        L_0x004b:
            r5 = 4
            goto L_0x0078
        L_0x004d:
            java.lang.String r1 = "android.telephony.action.CARRIER_CONFIG_CHANGED"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x0056
            goto L_0x002a
        L_0x0056:
            r5 = 3
            goto L_0x0078
        L_0x0058:
            java.lang.String r1 = "android.net.conn.CONNECTIVITY_CHANGE"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x0061
            goto L_0x002a
        L_0x0061:
            r5 = 2
            goto L_0x0078
        L_0x0063:
            java.lang.String r1 = "android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x006c
            goto L_0x002a
        L_0x006c:
            r5 = 1
            goto L_0x0078
        L_0x006e:
            java.lang.String r1 = "android.intent.action.SERVICE_STATE"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x0077
            goto L_0x002a
        L_0x0077:
            r5 = r3
        L_0x0078:
            switch(r5) {
                case 0: goto L_0x0131;
                case 1: goto L_0x012d;
                case 2: goto L_0x0129;
                case 3: goto L_0x0106;
                case 4: goto L_0x00ff;
                case 5: goto L_0x00c3;
                case 6: goto L_0x00a8;
                default: goto L_0x007b;
            }
        L_0x007b:
            java.lang.String r5 = "android.telephony.extra.SUBSCRIPTION_INDEX"
            int r5 = r6.getIntExtra(r5, r2)
            boolean r0 = android.telephony.SubscriptionManager.isValidSubscriptionId(r5)
            if (r0 == 0) goto L_0x00a1
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r0 = r4.mMobileSignalControllers
            int r0 = r0.indexOfKey(r5)
            if (r0 < 0) goto L_0x009c
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r4 = r4.mMobileSignalControllers
            java.lang.Object r4 = r4.get(r5)
            com.android.systemui.statusbar.policy.MobileSignalController r4 = (com.android.systemui.statusbar.policy.MobileSignalController) r4
            r4.handleBroadcast(r6)
            goto L_0x0146
        L_0x009c:
            r4.updateMobileControllers()
            goto L_0x0146
        L_0x00a1:
            com.android.systemui.statusbar.policy.WifiSignalController r4 = r4.mWifiSignalController
            r4.handleBroadcast(r6)
            goto L_0x0146
        L_0x00a8:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x00be
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            java.lang.Object r5 = r5.valueAt(r3)
            com.android.systemui.statusbar.policy.MobileSignalController r5 = (com.android.systemui.statusbar.policy.MobileSignalController) r5
            r5.handleBroadcast(r6)
            int r3 = r3 + 1
            goto L_0x00a8
        L_0x00be:
            r4.updateConfig()
            goto L_0x0146
        L_0x00c3:
            java.lang.String r5 = "rebroadcastOnUnlock"
            boolean r5 = r6.getBooleanExtra(r5, r3)
            if (r5 == 0) goto L_0x00cd
            goto L_0x0146
        L_0x00cd:
            r4.updateActiveSubscriptions()
            java.lang.Class<com.android.systemui.moto.DualSimIconController> r5 = com.android.systemui.moto.DualSimIconController.class
            java.lang.Object r5 = com.android.systemui.Dependency.get(r5)
            com.android.systemui.moto.DualSimIconController r5 = (com.android.systemui.moto.DualSimIconController) r5
            java.util.List<android.telephony.SubscriptionInfo> r0 = r4.mActivitySubscriptions
            int r0 = r0.size()
            r5.updateActivitySubsCount(r0)
            com.android.systemui.moto.SimStates r5 = r4.mSimStates
            r5.updateFromIntent(r6)
            r4.updateMobileControllers()
        L_0x00e9:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0146
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            java.lang.Object r5 = r5.valueAt(r3)
            com.android.systemui.statusbar.policy.MobileSignalController r5 = (com.android.systemui.statusbar.policy.MobileSignalController) r5
            r5.handleBroadcast(r6)
            int r3 = r3 + 1
            goto L_0x00e9
        L_0x00ff:
            r4.refreshLocale()
            r4.updateAirplaneMode(r3)
            goto L_0x0146
        L_0x0106:
            boolean r5 = DEBUG
            if (r5 == 0) goto L_0x010f
            java.lang.String r5 = "ACTION_CARRIER_CONFIG_CHANGED"
            android.util.Log.i(r0, r5)
        L_0x010f:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0125
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            java.lang.Object r5 = r5.valueAt(r3)
            com.android.systemui.statusbar.policy.MobileSignalController r5 = (com.android.systemui.statusbar.policy.MobileSignalController) r5
            r5.handleBroadcast(r6)
            int r3 = r3 + 1
            goto L_0x010f
        L_0x0125:
            r4.updateConfig()
            goto L_0x0146
        L_0x0129:
            r4.updateConnectivity()
            goto L_0x0146
        L_0x012d:
            r4.recalculateEmergency()
            goto L_0x0146
        L_0x0131:
            android.os.Bundle r5 = r6.getExtras()
            android.telephony.ServiceState r5 = android.telephony.ServiceState.newFromBundle(r5)
            r4.mLastServiceState = r5
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r4.mMobileSignalControllers
            int r5 = r5.size()
            if (r5 != 0) goto L_0x0146
            r4.recalculateEmergency()
        L_0x0146:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.onReceive(android.content.Context, android.content.Intent):void");
    }

    public void updateConfig() {
        NetworkConfig readConfig = NetworkConfig.readConfig(this.mContext);
        this.mMotoConfig = readConfig;
        this.mConfig = readConfig.mConfig;
        this.mReceiverHandler.post(new NetworkControllerImpl$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void handleConfigurationChanged() {
        updateMobileControllers();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                MobileSignalController valueAt = this.mMobileSignalControllers.valueAt(i);
                valueAt.setConfiguration(this.mMotoConfig, MotoFeature.getSubContext(this.mContext, valueAt.mSubscriptionInfo));
                if (this.mProviderModelBehavior) {
                    valueAt.refreshCallIndicator(this.mCallbackHandler);
                }
            }
        }
        refreshLocale();
    }

    /* access modifiers changed from: private */
    public void updateMobileControllers() {
        if (this.mListening) {
            doUpdateMobileControllers();
        }
    }

    private void filterMobileSubscriptionInSameGroup(List<SubscriptionInfo> list) {
        if (list.size() == 2) {
            SubscriptionInfo subscriptionInfo = list.get(0);
            SubscriptionInfo subscriptionInfo2 = list.get(1);
            if (subscriptionInfo.getGroupUuid() != null && subscriptionInfo.getGroupUuid().equals(subscriptionInfo2.getGroupUuid())) {
                if (!subscriptionInfo.isOpportunistic() && !subscriptionInfo2.isOpportunistic()) {
                    return;
                }
                if (CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean")) {
                    if (!subscriptionInfo.isOpportunistic()) {
                        subscriptionInfo = subscriptionInfo2;
                    }
                    list.remove(subscriptionInfo);
                    return;
                }
                if (subscriptionInfo.getSubscriptionId() == this.mActiveMobileDataSubscription) {
                    subscriptionInfo = subscriptionInfo2;
                }
                list.remove(subscriptionInfo);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void doUpdateMobileControllers() {
        doUpdateMobileControllers(false);
    }

    /* access modifiers changed from: package-private */
    public void doUpdateMobileControllers(boolean z) {
        if (DEBUG) {
            Log.d("NetworkController", "doUpdateMobileControllers");
        }
        List completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList == null) {
            completeActiveSubscriptionInfoList = Collections.emptyList();
        }
        boolean z2 = false;
        Iterator it = completeActiveSubscriptionInfoList.iterator();
        while (true) {
            if (it.hasNext()) {
                if (this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(((SubscriptionInfo) it.next()).getSubscriptionId()) != 1) {
                    z2 = true;
                    break;
                }
            } else {
                break;
            }
        }
        filterMobileSubscriptionInSameGroup(completeActiveSubscriptionInfoList);
        if (z2 || !hasCorrectMobileControllers(completeActiveSubscriptionInfoList) || z) {
            this.forceUpdateNoSims = z;
            synchronized (this.mLock) {
                setCurrentSubscriptionsLocked(completeActiveSubscriptionInfoList);
            }
            recalculateEmergency();
            return;
        }
        if (DEBUG) {
            Log.d("NetworkController", "hasCorrectMobileControllers");
        }
        updateNoSims();
    }

    public void forceUpdateMobileControllers() {
        if (this.mListening) {
            doUpdateMobileControllers(true);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void updateNoSims() {
        SimStates simStates;
        boolean z = this.mHasMobileDataFeature && this.mMobileSignalControllers.size() == 0;
        boolean hasAnySim = hasAnySim();
        if (z != this.mHasNoSubs || hasAnySim != this.mSimDetected || (((simStates = this.mSimStates) != null && !simStates.equals(this.mPrevSimStates)) || (this.forceUpdateNoSims && z && !hasAnySim))) {
            this.mHasNoSubs = z;
            this.mSimDetected = hasAnySim;
            this.forceUpdateNoSims = false;
            SimStates simStates2 = this.mSimStates;
            if (simStates2 != null) {
                this.mPrevSimStates = simStates2.clone();
            }
            this.mCallbackHandler.setNoSims(this.mHasNoSubs, this.mSimDetected);
        }
    }

    /* access modifiers changed from: private */
    public boolean hasAnySim() {
        int activeModemCount = this.mPhone.getActiveModemCount();
        for (int i = 0; i < activeModemCount; i++) {
            int simState = this.mPhone.getSimState(i);
            if (simState != 1 && simState != 0) {
                return true;
            }
        }
        return false;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void setCurrentSubscriptionsLocked(List<SubscriptionInfo> list) {
        SparseArray sparseArray;
        int i;
        boolean z;
        int i2;
        int i3;
        List<SubscriptionInfo> list2;
        List<SubscriptionInfo> list3 = list;
        Collections.sort(list3, new Comparator<SubscriptionInfo>() {
            public int compare(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
                int i;
                int i2;
                if (subscriptionInfo.getSimSlotIndex() == subscriptionInfo2.getSimSlotIndex()) {
                    i2 = subscriptionInfo.getSubscriptionId();
                    i = subscriptionInfo2.getSubscriptionId();
                } else {
                    i2 = subscriptionInfo.getSimSlotIndex();
                    i = subscriptionInfo2.getSimSlotIndex();
                }
                return i2 - i;
            }
        });
        this.mCurrentSubscriptions = list3;
        SparseArray sparseArray2 = new SparseArray();
        for (int i4 = 0; i4 < this.mMobileSignalControllers.size(); i4++) {
            sparseArray2.put(this.mMobileSignalControllers.keyAt(i4), this.mMobileSignalControllers.valueAt(i4));
        }
        this.mMobileSignalControllers.clear();
        int size = list.size();
        boolean showDualSimIcon = ((DualSimIconController) Dependency.get(DualSimIconController.class)).getShowDualSimIcon();
        int i5 = 0;
        while (i5 < size) {
            SubscriptionInfo subscriptionInfo = list3.get(i5);
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            MobileSignalController mobileSignalController = (MobileSignalController) sparseArray2.get(subscriptionId);
            if (mobileSignalController == null || hasSubcriptionChanged(mobileSignalController, subscriptionInfo)) {
                Context subContext = MotoFeature.getSubContext(this.mContext, subscriptionInfo);
                MobileMappings.Config readConfig = MobileMappings.Config.readConfig(subContext);
                boolean z2 = this.mHasMobileDataFeature;
                TelephonyManager createForSubscriptionId = this.mPhone.createForSubscriptionId(subscriptionId);
                CallbackHandler callbackHandler = this.mCallbackHandler;
                MobileStatusTracker.SubscriptionDefaults subscriptionDefaults = this.mSubDefaults;
                sparseArray = sparseArray2;
                MobileSignalController mobileSignalController2 = r0;
                int i6 = subscriptionId;
                int i7 = i5;
                Looper looper = this.mReceiverHandler.getLooper();
                z = showDualSimIcon;
                i = size;
                MobileSignalController mobileSignalController3 = new MobileSignalController(subContext, readConfig, z2, createForSubscriptionId, callbackHandler, this, subscriptionInfo, subscriptionDefaults, looper, this.mCarrierConfigTracker, this.mFeatureFlags);
                mobileSignalController2.setUserSetupComplete(this.mUserSetup);
                mobileSignalController2.onWifiConnectionStateChanged(this.mWifiEnabled, this.mWifiConnected);
                this.mMobileSignalControllers.put(i6, mobileSignalController2);
                list2 = list;
                i2 = i6;
                i3 = i7;
                if (list2.get(i3).getSimSlotIndex() == 0) {
                    this.mDefaultSignalController = mobileSignalController2;
                }
                if (this.mListening) {
                    mobileSignalController2.registerListener();
                }
            } else {
                this.mMobileSignalControllers.put(subscriptionId, mobileSignalController);
                sparseArray2.remove(subscriptionId);
                i2 = subscriptionId;
                i3 = i5;
                z = showDualSimIcon;
                i = size;
                list2 = list3;
                sparseArray = sparseArray2;
            }
            this.mMobileSignalControllers.get(i2).updateDualSignalFlag(z);
            i5 = i3 + 1;
            list3 = list2;
            showDualSimIcon = z;
            size = i;
            sparseArray2 = sparseArray;
        }
        List<SubscriptionInfo> list4 = list3;
        SparseArray sparseArray3 = sparseArray2;
        if (this.mListening) {
            int i8 = 0;
            while (i8 < sparseArray3.size()) {
                SparseArray sparseArray4 = sparseArray3;
                int keyAt = sparseArray4.keyAt(i8);
                if (sparseArray4.get(keyAt) == this.mDefaultSignalController) {
                    this.mDefaultSignalController = null;
                }
                ((MobileSignalController) sparseArray4.get(keyAt)).unregisterListener();
                i8++;
                sparseArray3 = sparseArray4;
            }
        }
        this.mCallbackHandler.setSubs(list4);
        updateNoSims();
        pushConnectivityToSignals();
        updateAirplaneMode(true);
        notifyAllListeners();
    }

    /* access modifiers changed from: private */
    public void setUserSetupComplete(boolean z) {
        this.mReceiverHandler.post(new NetworkControllerImpl$$ExternalSyntheticLambda8(this, z));
    }

    /* access modifiers changed from: private */
    /* renamed from: handleSetUserSetupComplete */
    public void lambda$setUserSetupComplete$4(boolean z) {
        this.mUserSetup = z;
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            this.mMobileSignalControllers.valueAt(i).setUserSetupComplete(this.mUserSetup);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0018  */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasCorrectMobileControllers(java.util.List<android.telephony.SubscriptionInfo> r5) {
        /*
            r4 = this;
            int r0 = r5.size()
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r1 = r4.mMobileSignalControllers
            int r1 = r1.size()
            r2 = 0
            if (r0 == r1) goto L_0x000e
            return r2
        L_0x000e:
            java.util.Iterator r5 = r5.iterator()
        L_0x0012:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x003d
            java.lang.Object r0 = r5.next()
            android.telephony.SubscriptionInfo r0 = (android.telephony.SubscriptionInfo) r0
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r1 = r4.mMobileSignalControllers
            int r3 = r0.getSubscriptionId()
            int r1 = r1.indexOfKey(r3)
            if (r1 < 0) goto L_0x003c
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r1 = r4.mMobileSignalControllers
            int r3 = r0.getSubscriptionId()
            java.lang.Object r1 = r1.get(r3)
            com.android.systemui.statusbar.policy.MobileSignalController r1 = (com.android.systemui.statusbar.policy.MobileSignalController) r1
            boolean r0 = r4.hasSubcriptionChanged(r1, r0)
            if (r0 == 0) goto L_0x0012
        L_0x003c:
            return r2
        L_0x003d:
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.hasCorrectMobileControllers(java.util.List):boolean");
    }

    private boolean hasSubcriptionChanged(MobileSignalController mobileSignalController, SubscriptionInfo subscriptionInfo) {
        return (mobileSignalController != null && mobileSignalController.mSubscriptionInfo.getSimSlotIndex() == subscriptionInfo.getSimSlotIndex() && mobileSignalController.mSubscriptionInfo.getMcc() == subscriptionInfo.getMcc() && mobileSignalController.mSubscriptionInfo.getMnc() == subscriptionInfo.getMnc()) ? false : true;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setNoNetworksAvailable(boolean z) {
        this.mNoNetworksAvailable = z;
    }

    private void updateAirplaneMode(boolean z) {
        boolean z2 = true;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 1) {
            z2 = false;
        }
        if (z2 != this.mAirplaneMode || z) {
            this.mAirplaneMode = z2;
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                this.mMobileSignalControllers.valueAt(i).setAirplaneMode(this.mAirplaneMode);
            }
            ((DualSimIconController) Dependency.get(DualSimIconController.class)).updateAirplaneMode(this.mAirplaneMode);
            notifyListeners();
        }
    }

    private void refreshLocale() {
        int layoutDirection = this.mContext.getResources().getConfiguration().getLayoutDirection();
        Locale locale = this.mContext.getResources().getConfiguration().locale;
        if (!locale.equals(this.mLocale) || layoutDirection != this.mLayoutDirection) {
            this.mLayoutDirection = layoutDirection;
            this.mLocale = locale;
            this.mWifiSignalController.refreshLocale();
            notifyAllListeners();
        }
    }

    private void notifyAllListeners() {
        notifyListeners();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            this.mMobileSignalControllers.valueAt(i).notifyListeners();
        }
        this.mWifiSignalController.notifyListeners();
        this.mEthernetSignalController.notifyListeners();
    }

    private void notifyListeners() {
        this.mCallbackHandler.setIsAirplaneMode(new NetworkController.IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, R$string.accessibility_airplane_mode, this.mContext));
        updateNoSims();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r2v0 */
    /* JADX WARNING: type inference failed for: r2v2 */
    /* JADX WARNING: type inference failed for: r2v3, types: [int] */
    /* JADX WARNING: type inference failed for: r2v5 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateConnectivity() {
        /*
            r9 = this;
            java.util.BitSet r0 = r9.mConnectedTransports
            r0.clear()
            java.util.BitSet r0 = r9.mValidatedTransports
            r0.clear()
            android.net.NetworkCapabilities r0 = r9.mLastDefaultNetworkCapabilities
            r1 = 3
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L_0x0056
            int[] r0 = r0.getTransportTypes()
            int r4 = r0.length
            r5 = r2
        L_0x0017:
            if (r5 >= r4) goto L_0x0056
            r6 = r0[r5]
            if (r6 == 0) goto L_0x0022
            if (r6 == r3) goto L_0x0022
            if (r6 == r1) goto L_0x0022
            goto L_0x0053
        L_0x0022:
            r7 = 16
            if (r6 != 0) goto L_0x0041
            android.net.NetworkCapabilities r8 = r9.mLastDefaultNetworkCapabilities
            android.net.wifi.WifiInfo r8 = com.android.settingslib.Utils.tryGetWifiInfoForVcn(r8)
            if (r8 == 0) goto L_0x0041
            java.util.BitSet r6 = r9.mConnectedTransports
            r6.set(r3)
            android.net.NetworkCapabilities r6 = r9.mLastDefaultNetworkCapabilities
            boolean r6 = r6.hasCapability(r7)
            if (r6 == 0) goto L_0x0053
            java.util.BitSet r6 = r9.mValidatedTransports
            r6.set(r3)
            goto L_0x0053
        L_0x0041:
            java.util.BitSet r8 = r9.mConnectedTransports
            r8.set(r6)
            android.net.NetworkCapabilities r8 = r9.mLastDefaultNetworkCapabilities
            boolean r7 = r8.hasCapability(r7)
            if (r7 == 0) goto L_0x0053
            java.util.BitSet r7 = r9.mValidatedTransports
            r7.set(r6)
        L_0x0053:
            int r5 = r5 + 1
            goto L_0x0017
        L_0x0056:
            boolean r0 = r9.mForceCellularValidated
            if (r0 == 0) goto L_0x005f
            java.util.BitSet r0 = r9.mValidatedTransports
            r0.set(r2)
        L_0x005f:
            boolean r0 = CHATTY
            if (r0 == 0) goto L_0x0091
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "updateConnectivity: mConnectedTransports="
            r0.append(r4)
            java.util.BitSet r4 = r9.mConnectedTransports
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "NetworkController"
            android.util.Log.d(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "updateConnectivity: mValidatedTransports="
            r0.append(r5)
            java.util.BitSet r5 = r9.mValidatedTransports
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r4, r0)
        L_0x0091:
            java.util.BitSet r0 = r9.mValidatedTransports
            boolean r0 = r0.get(r2)
            if (r0 != 0) goto L_0x00ac
            java.util.BitSet r0 = r9.mValidatedTransports
            boolean r0 = r0.get(r3)
            if (r0 != 0) goto L_0x00ac
            java.util.BitSet r0 = r9.mValidatedTransports
            boolean r0 = r0.get(r1)
            if (r0 == 0) goto L_0x00aa
            goto L_0x00ac
        L_0x00aa:
            r0 = r2
            goto L_0x00ad
        L_0x00ac:
            r0 = r3
        L_0x00ad:
            r9.mInetCondition = r0
            r9.pushConnectivityToSignals()
            boolean r0 = r9.mProviderModelBehavior
            if (r0 == 0) goto L_0x00f7
            java.util.BitSet r0 = r9.mConnectedTransports
            boolean r0 = r0.get(r2)
            if (r0 != 0) goto L_0x00d0
            java.util.BitSet r0 = r9.mConnectedTransports
            boolean r0 = r0.get(r3)
            if (r0 != 0) goto L_0x00d0
            java.util.BitSet r0 = r9.mConnectedTransports
            boolean r0 = r0.get(r1)
            if (r0 != 0) goto L_0x00d0
            r0 = r3
            goto L_0x00d1
        L_0x00d0:
            r0 = r2
        L_0x00d1:
            r9.mNoDefaultNetwork = r0
            com.android.systemui.statusbar.policy.CallbackHandler r1 = r9.mCallbackHandler
            boolean r4 = r9.mInetCondition
            r3 = r3 ^ r4
            boolean r4 = r9.mNoNetworksAvailable
            r1.setConnectivityStatus(r0, r3, r4)
        L_0x00dd:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r0 = r9.mMobileSignalControllers
            int r0 = r0.size()
            if (r2 >= r0) goto L_0x00f3
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r0 = r9.mMobileSignalControllers
            java.lang.Object r0 = r0.valueAt(r2)
            com.android.systemui.statusbar.policy.MobileSignalController r0 = (com.android.systemui.statusbar.policy.MobileSignalController) r0
            r0.updateNoCallingState()
            int r2 = r2 + 1
            goto L_0x00dd
        L_0x00f3:
            r9.notifyAllListeners()
            goto L_0x0120
        L_0x00f7:
            boolean r0 = r9.mProviderModelSetting
            if (r0 == 0) goto L_0x0120
            java.util.BitSet r0 = r9.mConnectedTransports
            boolean r0 = r0.get(r2)
            if (r0 != 0) goto L_0x0114
            java.util.BitSet r0 = r9.mConnectedTransports
            boolean r0 = r0.get(r3)
            if (r0 != 0) goto L_0x0114
            java.util.BitSet r0 = r9.mConnectedTransports
            boolean r0 = r0.get(r1)
            if (r0 != 0) goto L_0x0114
            r2 = r3
        L_0x0114:
            r9.mNoDefaultNetwork = r2
            com.android.systemui.statusbar.policy.CallbackHandler r0 = r9.mCallbackHandler
            boolean r1 = r9.mInetCondition
            r1 = r1 ^ r3
            boolean r9 = r9.mNoNetworksAvailable
            r0.setConnectivityStatus(r2, r1, r9)
        L_0x0120:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.updateConnectivity():void");
    }

    private void pushConnectivityToSignals() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            this.mMobileSignalControllers.valueAt(i).updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        }
        this.mWifiSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        this.mEthernetSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NetworkController state:");
        printWriter.println("  - telephony ------");
        printWriter.print("  hasVoiceCallingFeature()=");
        printWriter.println(hasVoiceCallingFeature());
        printWriter.println("  mListening=" + this.mListening);
        printWriter.println("  - connectivity ------");
        printWriter.print("  mConnectedTransports=");
        printWriter.println(this.mConnectedTransports);
        printWriter.print("  mValidatedTransports=");
        printWriter.println(this.mValidatedTransports);
        printWriter.print("  mInetCondition=");
        printWriter.println(this.mInetCondition);
        printWriter.print("  mAirplaneMode=");
        printWriter.println(this.mAirplaneMode);
        printWriter.print("  mLocale=");
        printWriter.println(this.mLocale);
        printWriter.print("  mLastServiceState=");
        printWriter.println(this.mLastServiceState);
        printWriter.print("  mIsEmergency=");
        printWriter.println(this.mIsEmergency);
        printWriter.print("  mEmergencySource=");
        printWriter.println(emergencyToString(this.mEmergencySource));
        printWriter.println("  - DefaultNetworkCallback -----");
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            if (this.mHistory[i2] != null) {
                i++;
            }
        }
        int i3 = this.mHistoryIndex + 16;
        while (true) {
            i3--;
            if (i3 < (this.mHistoryIndex + 16) - i) {
                break;
            }
            printWriter.println("  Previous NetworkCallback(" + ((this.mHistoryIndex + 16) - i3) + "): " + this.mHistory[i3 & 15]);
        }
        printWriter.println("  - config ------");
        for (int i4 = 0; i4 < this.mMobileSignalControllers.size(); i4++) {
            this.mMobileSignalControllers.valueAt(i4).dump(printWriter);
        }
        this.mWifiSignalController.dump(printWriter);
        this.mEthernetSignalController.dump(printWriter);
        this.mAccessPoints.dump(printWriter);
        this.mCallbackHandler.dump(printWriter);
    }

    private static final String emergencyToString(int i) {
        if (i > 300) {
            return "ASSUMED_VOICE_CONTROLLER(" + (i - 200) + ")";
        } else if (i > 300) {
            return "NO_SUB(" + (i - 300) + ")";
        } else if (i > 200) {
            return "VOICE_CONTROLLER(" + (i - 200) + ")";
        } else if (i <= 100) {
            return i == 0 ? "NO_CONTROLLERS" : "UNKNOWN_SOURCE";
        } else {
            return "FIRST_CONTROLLER(" + (i - 100) + ")";
        }
    }

    public void onDemoModeStarted() {
        if (DEBUG) {
            Log.d("NetworkController", "Entering demo mode");
        }
        unregisterListeners();
        this.mDemoInetCondition = this.mInetCondition;
        WifiSignalController.WifiState wifiState = (WifiSignalController.WifiState) this.mWifiSignalController.getState();
        this.mDemoWifiState = wifiState;
        wifiState.ssid = "DemoMode";
    }

    public void onDemoModeFinished() {
        if (DEBUG) {
            Log.d("NetworkController", "Exiting demo mode");
        }
        updateMobileControllers();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            this.mMobileSignalControllers.valueAt(i).resetLastState();
        }
        this.mWifiSignalController.resetLastState();
        this.mReceiverHandler.post(this.mRegisterListeners);
        notifyAllListeners();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDemoCommand(java.lang.String r18, android.os.Bundle r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r19
            com.android.systemui.demomode.DemoModeController r2 = r0.mDemoModeController
            boolean r2 = r2.isInDemoMode()
            if (r2 != 0) goto L_0x000d
            return
        L_0x000d:
            java.lang.String r2 = "airplane"
            java.lang.String r2 = r1.getString(r2)
            java.lang.String r3 = "show"
            if (r2 == 0) goto L_0x002b
            boolean r2 = r2.equals(r3)
            com.android.systemui.statusbar.policy.CallbackHandler r4 = r0.mCallbackHandler
            com.android.systemui.statusbar.policy.NetworkController$IconState r5 = new com.android.systemui.statusbar.policy.NetworkController$IconState
            int r6 = com.android.settingslib.mobile.TelephonyIcons.FLIGHT_MODE_ICON
            int r7 = com.android.systemui.R$string.accessibility_airplane_mode
            android.content.Context r8 = r0.mContext
            r5.<init>(r2, r6, r7, r8)
            r4.setIsAirplaneMode(r5)
        L_0x002b:
            java.lang.String r2 = "fully"
            java.lang.String r2 = r1.getString(r2)
            r4 = 0
            if (r2 == 0) goto L_0x006f
            boolean r2 = java.lang.Boolean.parseBoolean(r2)
            r0.mDemoInetCondition = r2
            java.util.BitSet r2 = new java.util.BitSet
            r2.<init>()
            boolean r5 = r0.mDemoInetCondition
            if (r5 == 0) goto L_0x004a
            com.android.systemui.statusbar.policy.WifiSignalController r5 = r0.mWifiSignalController
            int r5 = r5.mTransportType
            r2.set(r5)
        L_0x004a:
            com.android.systemui.statusbar.policy.WifiSignalController r5 = r0.mWifiSignalController
            r5.updateConnectivity(r2, r2)
            r5 = r4
        L_0x0050:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r6 = r0.mMobileSignalControllers
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x006f
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r6 = r0.mMobileSignalControllers
            java.lang.Object r6 = r6.valueAt(r5)
            com.android.systemui.statusbar.policy.MobileSignalController r6 = (com.android.systemui.statusbar.policy.MobileSignalController) r6
            boolean r7 = r0.mDemoInetCondition
            if (r7 == 0) goto L_0x0069
            int r7 = r6.mTransportType
            r2.set(r7)
        L_0x0069:
            r6.updateConnectivity(r2, r2)
            int r5 = r5 + 1
            goto L_0x0050
        L_0x006f:
            java.lang.String r2 = "wifi"
            java.lang.String r2 = r1.getString(r2)
            r5 = 3
            java.lang.String r6 = "inout"
            java.lang.String r7 = "out"
            java.lang.String r8 = "in"
            java.lang.String r9 = "null"
            java.lang.String r10 = "activity"
            java.lang.String r11 = "level"
            r14 = 1
            if (r2 == 0) goto L_0x0111
            boolean r2 = r2.equals(r3)
            java.lang.String r15 = r1.getString(r11)
            if (r15 == 0) goto L_0x00b2
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r13 = r0.mDemoWifiState
            boolean r16 = r15.equals(r9)
            if (r16 == 0) goto L_0x0099
            r12 = -1
            goto L_0x00a5
        L_0x0099:
            int r15 = java.lang.Integer.parseInt(r15)
            int r16 = com.android.systemui.statusbar.policy.WifiIcons.WIFI_LEVEL_COUNT
            int r12 = r16 + -1
            int r12 = java.lang.Math.min(r15, r12)
        L_0x00a5:
            r13.level = r12
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r12 = r0.mDemoWifiState
            int r13 = r12.level
            if (r13 < 0) goto L_0x00af
            r13 = r14
            goto L_0x00b0
        L_0x00af:
            r13 = r4
        L_0x00b0:
            r12.connected = r13
        L_0x00b2:
            java.lang.String r12 = r1.getString(r10)
            if (r12 == 0) goto L_0x00f7
            int r13 = r12.hashCode()
            switch(r13) {
                case 3365: goto L_0x00d3;
                case 110414: goto L_0x00ca;
                case 100357129: goto L_0x00c1;
                default: goto L_0x00bf;
            }
        L_0x00bf:
            r12 = -1
            goto L_0x00db
        L_0x00c1:
            boolean r12 = r12.equals(r6)
            if (r12 != 0) goto L_0x00c8
            goto L_0x00bf
        L_0x00c8:
            r12 = 2
            goto L_0x00db
        L_0x00ca:
            boolean r12 = r12.equals(r7)
            if (r12 != 0) goto L_0x00d1
            goto L_0x00bf
        L_0x00d1:
            r12 = r14
            goto L_0x00db
        L_0x00d3:
            boolean r12 = r12.equals(r8)
            if (r12 != 0) goto L_0x00da
            goto L_0x00bf
        L_0x00da:
            r12 = r4
        L_0x00db:
            switch(r12) {
                case 0: goto L_0x00f1;
                case 1: goto L_0x00ea;
                case 2: goto L_0x00e4;
                default: goto L_0x00de;
            }
        L_0x00de:
            com.android.systemui.statusbar.policy.WifiSignalController r12 = r0.mWifiSignalController
            r12.setActivity(r4)
            goto L_0x00fc
        L_0x00e4:
            com.android.systemui.statusbar.policy.WifiSignalController r12 = r0.mWifiSignalController
            r12.setActivity(r5)
            goto L_0x00fc
        L_0x00ea:
            com.android.systemui.statusbar.policy.WifiSignalController r12 = r0.mWifiSignalController
            r13 = 2
            r12.setActivity(r13)
            goto L_0x00fc
        L_0x00f1:
            com.android.systemui.statusbar.policy.WifiSignalController r12 = r0.mWifiSignalController
            r12.setActivity(r14)
            goto L_0x00fc
        L_0x00f7:
            com.android.systemui.statusbar.policy.WifiSignalController r12 = r0.mWifiSignalController
            r12.setActivity(r4)
        L_0x00fc:
            java.lang.String r12 = "ssid"
            java.lang.String r12 = r1.getString(r12)
            if (r12 == 0) goto L_0x0108
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r13 = r0.mDemoWifiState
            r13.ssid = r12
        L_0x0108:
            com.android.systemui.statusbar.policy.WifiSignalController$WifiState r12 = r0.mDemoWifiState
            r12.enabled = r2
            com.android.systemui.statusbar.policy.WifiSignalController r2 = r0.mWifiSignalController
            r2.notifyListeners()
        L_0x0111:
            java.lang.String r2 = "sims"
            java.lang.String r2 = r1.getString(r2)
            r12 = 8
            if (r2 == 0) goto L_0x016c
            int r2 = java.lang.Integer.parseInt(r2)
            int r2 = android.util.MathUtils.constrain(r2, r14, r12)
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r15 = r0.mMobileSignalControllers
            int r15 = r15.size()
            if (r2 == r15) goto L_0x016c
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r15 = r0.mMobileSignalControllers
            r15.clear()
            android.telephony.SubscriptionManager r15 = r0.mSubscriptionManager
            int r15 = r15.getActiveSubscriptionInfoCountMax()
            r5 = r15
        L_0x013c:
            int r14 = r15 + r2
            if (r5 >= r14) goto L_0x014a
            android.telephony.SubscriptionInfo r14 = r0.addSignalController(r5, r5)
            r13.add(r14)
            int r5 = r5 + 1
            goto L_0x013c
        L_0x014a:
            com.android.systemui.statusbar.policy.CallbackHandler r2 = r0.mCallbackHandler
            r2.setSubs(r13)
            r2 = r4
        L_0x0150:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r0.mMobileSignalControllers
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x016c
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r5 = r0.mMobileSignalControllers
            int r5 = r5.keyAt(r2)
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r13 = r0.mMobileSignalControllers
            java.lang.Object r5 = r13.get(r5)
            com.android.systemui.statusbar.policy.MobileSignalController r5 = (com.android.systemui.statusbar.policy.MobileSignalController) r5
            r5.notifyListeners()
            int r2 = r2 + 1
            goto L_0x0150
        L_0x016c:
            java.lang.String r2 = "nosim"
            java.lang.String r2 = r1.getString(r2)
            if (r2 == 0) goto L_0x0181
            boolean r2 = r2.equals(r3)
            r0.mHasNoSubs = r2
            com.android.systemui.statusbar.policy.CallbackHandler r5 = r0.mCallbackHandler
            boolean r13 = r0.mSimDetected
            r5.setNoSims(r2, r13)
        L_0x0181:
            java.lang.String r2 = "mobile"
            java.lang.String r2 = r1.getString(r2)
            if (r2 == 0) goto L_0x0382
            boolean r2 = r2.equals(r3)
            java.lang.String r5 = "datatype"
            java.lang.String r5 = r1.getString(r5)
            java.lang.String r13 = "slot"
            java.lang.String r13 = r1.getString(r13)
            boolean r14 = android.text.TextUtils.isEmpty(r13)
            if (r14 == 0) goto L_0x01a1
            r13 = r4
            goto L_0x01a5
        L_0x01a1:
            int r13 = java.lang.Integer.parseInt(r13)
        L_0x01a5:
            int r12 = android.util.MathUtils.constrain(r13, r4, r12)
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
        L_0x01ae:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r14 = r0.mMobileSignalControllers
            int r14 = r14.size()
            if (r14 > r12) goto L_0x01c4
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r14 = r0.mMobileSignalControllers
            int r14 = r14.size()
            android.telephony.SubscriptionInfo r14 = r0.addSignalController(r14, r14)
            r13.add(r14)
            goto L_0x01ae
        L_0x01c4:
            boolean r14 = r13.isEmpty()
            if (r14 != 0) goto L_0x01cf
            com.android.systemui.statusbar.policy.CallbackHandler r14 = r0.mCallbackHandler
            r14.setSubs(r13)
        L_0x01cf:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r13 = r0.mMobileSignalControllers
            java.lang.Object r12 = r13.valueAt(r12)
            com.android.systemui.statusbar.policy.MobileSignalController r12 = (com.android.systemui.statusbar.policy.MobileSignalController) r12
            com.android.settingslib.SignalIcon$State r13 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r13 = (com.android.settingslib.SignalIcon$MobileState) r13
            if (r5 == 0) goto L_0x01e1
            r14 = 1
            goto L_0x01e2
        L_0x01e1:
            r14 = r4
        L_0x01e2:
            r13.dataSim = r14
            com.android.settingslib.SignalIcon$State r13 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r13 = (com.android.settingslib.SignalIcon$MobileState) r13
            if (r5 == 0) goto L_0x01ee
            r14 = 1
            goto L_0x01ef
        L_0x01ee:
            r14 = r4
        L_0x01ef:
            r13.isDefault = r14
            com.android.settingslib.SignalIcon$State r13 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r13 = (com.android.settingslib.SignalIcon$MobileState) r13
            if (r5 == 0) goto L_0x01fb
            r14 = 1
            goto L_0x01fc
        L_0x01fb:
            r14 = r4
        L_0x01fc:
            r13.dataConnected = r14
            if (r5 == 0) goto L_0x02b5
            com.android.settingslib.SignalIcon$State r13 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r13 = (com.android.settingslib.SignalIcon$MobileState) r13
            java.lang.String r14 = "1x"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0212
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.ONE_X
            goto L_0x02b3
        L_0x0212:
            java.lang.String r14 = "3g"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x021e
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.THREE_G
            goto L_0x02b3
        L_0x021e:
            java.lang.String r14 = "4g"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x022a
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.FOUR_G
            goto L_0x02b3
        L_0x022a:
            java.lang.String r14 = "4g+"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0236
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.FOUR_G_PLUS
            goto L_0x02b3
        L_0x0236:
            java.lang.String r14 = "5g"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0242
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.NR_5G
            goto L_0x02b3
        L_0x0242:
            java.lang.String r14 = "5ge"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x024e
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.LTE_CA_5G_E
            goto L_0x02b3
        L_0x024e:
            java.lang.String r14 = "5g+"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0259
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.NR_5G_PLUS
            goto L_0x02b3
        L_0x0259:
            java.lang.String r14 = "e"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0264
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.f61E
            goto L_0x02b3
        L_0x0264:
            java.lang.String r14 = "g"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x026f
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.f62G
            goto L_0x02b3
        L_0x026f:
            java.lang.String r14 = "h"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x027a
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.f63H
            goto L_0x02b3
        L_0x027a:
            java.lang.String r14 = "h+"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0285
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.H_PLUS
            goto L_0x02b3
        L_0x0285:
            java.lang.String r14 = "lte"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x0290
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.LTE
            goto L_0x02b3
        L_0x0290:
            java.lang.String r14 = "lte+"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x029b
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.LTE_PLUS
            goto L_0x02b3
        L_0x029b:
            java.lang.String r14 = "dis"
            boolean r14 = r5.equals(r14)
            if (r14 == 0) goto L_0x02a6
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.DATA_DISABLED
            goto L_0x02b3
        L_0x02a6:
            java.lang.String r14 = "not"
            boolean r5 = r5.equals(r14)
            if (r5 == 0) goto L_0x02b1
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.NOT_DEFAULT_DATA
            goto L_0x02b3
        L_0x02b1:
            com.android.settingslib.SignalIcon$MobileIconGroup r5 = com.android.settingslib.mobile.TelephonyIcons.UNKNOWN
        L_0x02b3:
            r13.iconGroup = r5
        L_0x02b5:
            java.lang.String r5 = "roam"
            boolean r13 = r1.containsKey(r5)
            if (r13 == 0) goto L_0x02cd
            com.android.settingslib.SignalIcon$State r13 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r13 = (com.android.settingslib.SignalIcon$MobileState) r13
            java.lang.String r5 = r1.getString(r5)
            boolean r5 = r3.equals(r5)
            r13.roaming = r5
        L_0x02cd:
            java.lang.String r5 = r1.getString(r11)
            if (r5 == 0) goto L_0x0304
            com.android.settingslib.SignalIcon$State r11 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r11 = (com.android.settingslib.SignalIcon$MobileState) r11
            boolean r9 = r5.equals(r9)
            if (r9 == 0) goto L_0x02e1
            r5 = -1
            goto L_0x02ed
        L_0x02e1:
            int r5 = java.lang.Integer.parseInt(r5)
            int r9 = android.telephony.CellSignalStrength.getNumSignalStrengthLevels()
            int r5 = java.lang.Math.min(r5, r9)
        L_0x02ed:
            r11.level = r5
            com.android.settingslib.SignalIcon$State r5 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r5 = (com.android.settingslib.SignalIcon$MobileState) r5
            com.android.settingslib.SignalIcon$State r9 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r9 = (com.android.settingslib.SignalIcon$MobileState) r9
            int r9 = r9.level
            if (r9 < 0) goto L_0x0301
            r9 = 1
            goto L_0x0302
        L_0x0301:
            r9 = r4
        L_0x0302:
            r5.connected = r9
        L_0x0304:
            java.lang.String r5 = "inflate"
            boolean r9 = r1.containsKey(r5)
            if (r9 == 0) goto L_0x032c
            r9 = r4
        L_0x030d:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r11 = r0.mMobileSignalControllers
            int r11 = r11.size()
            if (r9 >= r11) goto L_0x032c
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r11 = r0.mMobileSignalControllers
            java.lang.Object r11 = r11.valueAt(r9)
            com.android.systemui.statusbar.policy.MobileSignalController r11 = (com.android.systemui.statusbar.policy.MobileSignalController) r11
            java.lang.String r13 = r1.getString(r5)
            java.lang.String r14 = "true"
            boolean r13 = r14.equals(r13)
            r11.mInflateSignalStrengths = r13
            int r9 = r9 + 1
            goto L_0x030d
        L_0x032c:
            java.lang.String r5 = r1.getString(r10)
            if (r5 == 0) goto L_0x0374
            com.android.settingslib.SignalIcon$State r9 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r9 = (com.android.settingslib.SignalIcon$MobileState) r9
            r10 = 1
            r9.dataConnected = r10
            int r9 = r5.hashCode()
            switch(r9) {
                case 3365: goto L_0x0356;
                case 110414: goto L_0x034d;
                case 100357129: goto L_0x0344;
                default: goto L_0x0342;
            }
        L_0x0342:
            r13 = -1
            goto L_0x035e
        L_0x0344:
            boolean r5 = r5.equals(r6)
            if (r5 != 0) goto L_0x034b
            goto L_0x0342
        L_0x034b:
            r13 = 2
            goto L_0x035e
        L_0x034d:
            boolean r5 = r5.equals(r7)
            if (r5 != 0) goto L_0x0354
            goto L_0x0342
        L_0x0354:
            r13 = 1
            goto L_0x035e
        L_0x0356:
            boolean r5 = r5.equals(r8)
            if (r5 != 0) goto L_0x035d
            goto L_0x0342
        L_0x035d:
            r13 = r4
        L_0x035e:
            switch(r13) {
                case 0: goto L_0x036f;
                case 1: goto L_0x036a;
                case 2: goto L_0x0365;
                default: goto L_0x0361;
            }
        L_0x0361:
            r12.setActivity(r4)
            goto L_0x0377
        L_0x0365:
            r5 = 3
            r12.setActivity(r5)
            goto L_0x0377
        L_0x036a:
            r5 = 2
            r12.setActivity(r5)
            goto L_0x0377
        L_0x036f:
            r5 = 1
            r12.setActivity(r5)
            goto L_0x0377
        L_0x0374:
            r12.setActivity(r4)
        L_0x0377:
            com.android.settingslib.SignalIcon$State r5 = r12.getState()
            com.android.settingslib.SignalIcon$MobileState r5 = (com.android.settingslib.SignalIcon$MobileState) r5
            r5.enabled = r2
            r12.notifyListeners()
        L_0x0382:
            java.lang.String r2 = "carriernetworkchange"
            java.lang.String r1 = r1.getString(r2)
            if (r1 == 0) goto L_0x03a4
            boolean r1 = r1.equals(r3)
        L_0x038e:
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r2 = r0.mMobileSignalControllers
            int r2 = r2.size()
            if (r4 >= r2) goto L_0x03a4
            android.util.SparseArray<com.android.systemui.statusbar.policy.MobileSignalController> r2 = r0.mMobileSignalControllers
            java.lang.Object r2 = r2.valueAt(r4)
            com.android.systemui.statusbar.policy.MobileSignalController r2 = (com.android.systemui.statusbar.policy.MobileSignalController) r2
            r2.setCarrierNetworkChangeMode(r1)
            int r4 = r4 + 1
            goto L_0x038e
        L_0x03a4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.dispatchDemoCommand(java.lang.String, android.os.Bundle):void");
    }

    public List<String> demoCommands() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("network");
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void recordLastNetworkCallback(String str) {
        String[] strArr = this.mHistory;
        int i = this.mHistoryIndex;
        strArr[i] = str;
        this.mHistoryIndex = (i + 1) % 16;
    }

    private SubscriptionInfo addSignalController(int i, int i2) {
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo(i, "", i2, "", "", 0, 0, "", 0, (Bitmap) null, (String) null, (String) null, "", false, (UiccAccessRule[]) null, (String) null);
        MobileSignalController mobileSignalController = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone.createForSubscriptionId(subscriptionInfo.getSubscriptionId()), this.mCallbackHandler, this, subscriptionInfo, this.mSubDefaults, this.mReceiverHandler.getLooper(), this.mCarrierConfigTracker, this.mFeatureFlags);
        this.mMobileSignalControllers.put(i, mobileSignalController);
        ((SignalIcon$MobileState) mobileSignalController.getState()).userSetup = true;
        mobileSignalController.onWifiConnectionStateChanged(this.mWifiEnabled, this.mWifiConnected);
        return subscriptionInfo;
    }

    public boolean hasEmergencyCryptKeeperText() {
        return EncryptionHelper.IS_DATA_ENCRYPTED;
    }

    public boolean isRadioOn() {
        return !this.mAirplaneMode;
    }

    private class SubListener extends SubscriptionManager.OnSubscriptionsChangedListener {
        SubListener(Looper looper) {
            super(looper);
        }

        public void onSubscriptionsChanged() {
            NetworkControllerImpl.this.updateMobileControllers();
        }
    }

    private void updateActiveSubscriptions() {
        this.mActivitySubscriptions.clear();
        List<SubscriptionInfo> completeActiveSubscriptionInfoList = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        if (completeActiveSubscriptionInfoList != null && completeActiveSubscriptionInfoList.size() != 0) {
            for (SubscriptionInfo subscriptionInfo : completeActiveSubscriptionInfoList) {
                if (this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(subscriptionInfo.getSubscriptionId()) == 1) {
                    this.mActivitySubscriptions.add(subscriptionInfo);
                }
            }
        }
    }
}
