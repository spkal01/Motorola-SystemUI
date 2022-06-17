package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.RegistrationManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import androidx.constraintlayout.widget.R$styleable;
import androidx.mediarouter.media.MediaRoute2Provider$$ExternalSyntheticLambda0;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.AccessibilityContentDescriptions;
import com.android.settingslib.SignalIcon$MobileIconGroup;
import com.android.settingslib.SignalIcon$MobileState;
import com.android.settingslib.Utils;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.mobile.MobileMappings;
import com.android.settingslib.mobile.MobileStatusTracker;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.CarrierIcons;
import com.android.systemui.moto.CarrierLabelUpdateMonitor;
import com.android.systemui.moto.CarrierNetworkType;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.ExtendedMobileDataInfo;
import com.android.systemui.moto.MotoAccessibilityContentDescriptions;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.moto.MotoSystemUIUtils;
import com.android.systemui.moto.NetworkConfig;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.moto.NotifyLowSignalStrength;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.util.CarrierConfigTracker;
import com.motorola.android.telephony.MotoExtPhoneStateListener;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.rro.RROsController;
import com.motorola.systemui.statusbar.policy.CellLocationController;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MobileSignalController extends SignalController<SignalIcon$MobileState, SignalIcon$MobileIconGroup> {
    /* access modifiers changed from: private */
    public static final SimpleDateFormat SSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    private MobileStatusTracker.Callback mCallback;
    private final CarrierConfigTracker mCarrierConfigTracker;
    private CellLocationController mCellLocationController;
    private MobileMappings.Config mConfig;
    Context mCurrentContext = null;
    private int mDataNetType = 0;
    private int mDataState = 0;
    final SparseArray<CarrierIcons.DataTypeGroup> mDataTypeLookup;
    private SignalIcon$MobileIconGroup mDefaultIcons;
    private final MobileStatusTracker.SubscriptionDefaults mDefaults;
    @VisibleForTesting
    protected ExtendedMobileDataInfo mExtendedInfo;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public final ImsMmTelManager mImsMmTelManager;
    /* access modifiers changed from: private */
    public int mImsType = 1;
    @VisibleForTesting
    boolean mInflateSignalStrengths = false;
    private boolean mIsSIMLocked = false;
    private int mLastLevel;
    /* access modifiers changed from: private */
    public int mLastWlanCrossSimLevel;
    /* access modifiers changed from: private */
    public int mLastWlanLevel;
    /* access modifiers changed from: private */
    public int mLastWwanLevel;
    private final String[] mMobileStatusHistory = new String[64];
    private int mMobileStatusHistoryIndex;
    @VisibleForTesting
    MobileStatusTracker mMobileStatusTracker;
    private NetworkConfig mMotoConfig;
    private MotoExt5GStateListener mMotoExt5GStateListener = null;
    private MotoExtTelephonyManager mMotoExtTM;
    private final String mNetworkNameDefault;
    private final String mNetworkNameSeparator;
    private final String mNetworkNamebracketsBegin;
    private final String mNetworkNamebracketsEnd;
    Map<String, SignalIcon$MobileIconGroup> mNetworkToIconLookup;
    private final ContentObserver mObserver;
    private final TelephonyManager mPhone;
    private final boolean mProviderModelBehavior;
    private final boolean mProviderModelSetting;
    private String mRROPkg = null;
    private RROsController mRROsController;
    /* access modifiers changed from: private */
    public final Handler mReceiverHandler;
    /* access modifiers changed from: private */
    public RegistrationManager.RegistrationCallback mRegistrationCallback;
    /* access modifiers changed from: private */
    public ServiceState mServiceState;
    private SignalStrength mSignalStrength;
    private BroadcastReceiver mSpnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("subscription", -1) == MobileSignalController.this.mSubscriptionInfo.getSubscriptionId()) {
                MobileSignalController.this.handleBroadcast(intent);
            }
        }
    };
    SubscriptionInfo mSubscriptionInfo;
    private TelephonyDisplayInfo mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
    /* access modifiers changed from: private */
    public final Runnable mTryRegisterIms = new Runnable() {
        private int mRetryCount;

        public void run() {
            try {
                this.mRetryCount++;
                ImsMmTelManager access$1200 = MobileSignalController.this.mImsMmTelManager;
                Handler access$1000 = MobileSignalController.this.mReceiverHandler;
                Objects.requireNonNull(access$1000);
                access$1200.registerImsRegistrationCallback(new MediaRoute2Provider$$ExternalSyntheticLambda0(access$1000), MobileSignalController.this.mRegistrationCallback);
                Log.d(MobileSignalController.this.mTag, "registerImsRegistrationCallback succeeded");
            } catch (ImsException | RuntimeException e) {
                if (this.mRetryCount < 12) {
                    Log.e(MobileSignalController.this.mTag, this.mRetryCount + " registerImsRegistrationCallback failed", e);
                    MobileSignalController.this.mReceiverHandler.postDelayed(MobileSignalController.this.mTryRegisterIms, 5000);
                }
            }
        }
    };

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public MobileSignalController(Context context, MobileMappings.Config config, boolean z, TelephonyManager telephonyManager, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl, SubscriptionInfo subscriptionInfo, MobileStatusTracker.SubscriptionDefaults subscriptionDefaults, Looper looper, CarrierConfigTracker carrierConfigTracker, FeatureFlags featureFlags) {
        super("MobileSignalController(" + subscriptionInfo.getSubscriptionId() + ")", context, 0, callbackHandler, networkControllerImpl);
        Context context2 = context;
        boolean z2 = z;
        Looper looper2 = looper;
        boolean z3 = true;
        this.mCarrierConfigTracker = carrierConfigTracker;
        this.mConfig = config;
        this.mPhone = telephonyManager;
        this.mMotoConfig = NetworkConfig.readConfig(context);
        this.mDefaults = subscriptionDefaults;
        this.mSubscriptionInfo = subscriptionInfo;
        this.mNetworkNameSeparator = getTextIfExists(R$string.status_bar_network_name_separator).toString();
        String charSequence = getTextIfExists(17040554).toString();
        this.mNetworkNameDefault = charSequence;
        this.mReceiverHandler = new Handler(looper2);
        this.mMotoExt5GStateListener = new MotoExt5GStateListener(subscriptionInfo.getSubscriptionId(), looper2);
        this.mMotoExtTM = new MotoExtTelephonyManager(context, subscriptionInfo.getSubscriptionId());
        this.mNetworkNamebracketsBegin = getTextIfExists(R$string.status_bar_network_name_spn_brackets_begin).toString();
        this.mNetworkNamebracketsEnd = getTextIfExists(R$string.status_bar_network_name_spn_brackets_end).toString();
        this.mDataTypeLookup = new SparseArray<>();
        resetIconMap(this.mMotoConfig);
        charSequence = subscriptionInfo.getCarrierName() != null ? subscriptionInfo.getCarrierName().toString() : charSequence;
        T t = this.mLastState;
        T t2 = this.mCurrentState;
        ((SignalIcon$MobileState) t2).networkName = charSequence;
        ((SignalIcon$MobileState) t).networkName = charSequence;
        ((SignalIcon$MobileState) t2).networkNameData = charSequence;
        ((SignalIcon$MobileState) t).networkNameData = charSequence;
        ((SignalIcon$MobileState) t2).enabled = z2;
        ((SignalIcon$MobileState) t).enabled = z2;
        SignalIcon$MobileIconGroup signalIcon$MobileIconGroup = this.mDefaultIcons;
        ((SignalIcon$MobileState) t2).iconGroup = signalIcon$MobileIconGroup;
        ((SignalIcon$MobileState) t).iconGroup = signalIcon$MobileIconGroup;
        this.mObserver = new ContentObserver(new Handler(looper2)) {
            public void onChange(boolean z) {
                MobileSignalController.this.updateTelephony();
            }
        };
        this.mCurrentContext = context2;
        this.mHandler = new Handler(Looper.getMainLooper());
        ((SignalIcon$MobileState) this.mCurrentState).wifiEnabled = this.mNetworkController.isWifiEnabled();
        ((SignalIcon$MobileState) this.mCurrentState).wifiConnected = this.mNetworkController.isWifiConnected();
        int currentUiccCardProvisioningStatus = this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(this.mSubscriptionInfo.getSubscriptionId());
        ((SignalIcon$MobileState) this.mCurrentState).uiccCardState = currentUiccCardProvisioningStatus;
        ((SignalIcon$MobileState) this.mLastState).uiccCardState = currentUiccCardProvisioningStatus;
        ExtendedMobileDataInfo extendedMobileDataInfo = new ExtendedMobileDataInfo();
        this.mExtendedInfo = extendedMobileDataInfo;
        extendedMobileDataInfo.slotId = subscriptionInfo.getSimSlotIndex();
        ExtendedMobileDataInfo extendedMobileDataInfo2 = this.mExtendedInfo;
        NetworkConfig networkConfig = this.mMotoConfig;
        extendedMobileDataInfo2.enableCustomize = networkConfig.enableCustomizations;
        extendedMobileDataInfo2.enableActivityIconOnSB = networkConfig.enableActivityIconOnSB;
        extendedMobileDataInfo2.showSeparatedSignalBars = networkConfig.showSeparatedSignalBars;
        if (!networkConfig.showBothWifiAndMobileNetwork && !NetworkStateTracker.isMultipleSimDevice() && !MotoFeature.isPrcProduct()) {
            z3 = false;
        }
        extendedMobileDataInfo2.mobileShowMobileWhenWifiActive = z3;
        ((SignalIcon$MobileState) this.mCurrentState).slotId = this.mSubscriptionInfo.getSimSlotIndex();
        ((SignalIcon$MobileState) this.mCurrentState).subId = this.mSubscriptionInfo.getSubscriptionId();
        ((SignalIcon$MobileState) this.mCurrentState).slotId = this.mSubscriptionInfo.getSimSlotIndex();
        ((SignalIcon$MobileState) this.mCurrentState).subId = this.mSubscriptionInfo.getSubscriptionId();
        this.mExtendedInfo.subContext = this.mCurrentContext;
        this.mCallback = new MobileStatusTracker.Callback() {
            private String mLastStatus;

            public void onMobileStatusChanged(boolean z, MobileStatusTracker.MobileStatus mobileStatus) {
                if (Log.isLoggable(MobileSignalController.this.mTag, 3)) {
                    String str = MobileSignalController.this.mTag;
                    Log.d(str, "onMobileStatusChanged= updateTelephony=" + z + " mobileStatus=" + mobileStatus.toString());
                }
                String mobileStatus2 = mobileStatus.toString();
                if (!mobileStatus2.equals(this.mLastStatus)) {
                    this.mLastStatus = mobileStatus2;
                    MobileSignalController.this.recordLastMobileStatus(MobileSignalController.SSDF.format(Long.valueOf(System.currentTimeMillis())) + "," + mobileStatus2);
                    if (SignalController.DEBUG) {
                        String str2 = MobileSignalController.this.mTag;
                        Log.d(str2, "onMobileStatusChanged= updateTelephony=" + z + " mobileStatus=" + mobileStatus.toString());
                    }
                }
                MobileSignalController.this.updateMobileStatus(mobileStatus);
                if (z) {
                    MobileSignalController.this.updateTelephony();
                } else {
                    MobileSignalController.this.notifyListenersIfNecessary();
                }
            }
        };
        this.mRegistrationCallback = new RegistrationManager.RegistrationCallback() {
            public void onRegistered(ImsRegistrationAttributes imsRegistrationAttributes) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onRegistered: attributes=" + imsRegistrationAttributes);
                int transportType = imsRegistrationAttributes.getTransportType();
                int attributeFlags = imsRegistrationAttributes.getAttributeFlags();
                if (transportType == 1) {
                    int unused = MobileSignalController.this.mImsType = 1;
                    MobileSignalController mobileSignalController = MobileSignalController.this;
                    int access$600 = mobileSignalController.getCallStrengthIcon(mobileSignalController.mLastWwanLevel, false);
                    MobileSignalController mobileSignalController2 = MobileSignalController.this;
                    NetworkController.IconState iconState = new NetworkController.IconState(true, access$600, mobileSignalController2.getCallStrengthDescription(mobileSignalController2.mLastWwanLevel, false));
                    MobileSignalController mobileSignalController3 = MobileSignalController.this;
                    mobileSignalController3.notifyCallStateChange(iconState, mobileSignalController3.mSubscriptionInfo.getSubscriptionId());
                } else if (transportType != 2) {
                } else {
                    if (attributeFlags == 0) {
                        int unused2 = MobileSignalController.this.mImsType = 2;
                        MobileSignalController mobileSignalController4 = MobileSignalController.this;
                        int access$6002 = mobileSignalController4.getCallStrengthIcon(mobileSignalController4.mLastWlanLevel, true);
                        MobileSignalController mobileSignalController5 = MobileSignalController.this;
                        NetworkController.IconState iconState2 = new NetworkController.IconState(true, access$6002, mobileSignalController5.getCallStrengthDescription(mobileSignalController5.mLastWlanLevel, true));
                        MobileSignalController mobileSignalController6 = MobileSignalController.this;
                        mobileSignalController6.notifyCallStateChange(iconState2, mobileSignalController6.mSubscriptionInfo.getSubscriptionId());
                    } else if (attributeFlags == 1) {
                        int unused3 = MobileSignalController.this.mImsType = 3;
                        MobileSignalController mobileSignalController7 = MobileSignalController.this;
                        int access$6003 = mobileSignalController7.getCallStrengthIcon(mobileSignalController7.mLastWlanCrossSimLevel, false);
                        MobileSignalController mobileSignalController8 = MobileSignalController.this;
                        NetworkController.IconState iconState3 = new NetworkController.IconState(true, access$6003, mobileSignalController8.getCallStrengthDescription(mobileSignalController8.mLastWlanCrossSimLevel, false));
                        MobileSignalController mobileSignalController9 = MobileSignalController.this;
                        mobileSignalController9.notifyCallStateChange(iconState3, mobileSignalController9.mSubscriptionInfo.getSubscriptionId());
                    }
                }
            }

            public void onUnregistered(ImsReasonInfo imsReasonInfo) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onDeregistered: info=" + imsReasonInfo);
                int unused = MobileSignalController.this.mImsType = 1;
                MobileSignalController mobileSignalController = MobileSignalController.this;
                int access$600 = mobileSignalController.getCallStrengthIcon(mobileSignalController.mLastWwanLevel, false);
                MobileSignalController mobileSignalController2 = MobileSignalController.this;
                NetworkController.IconState iconState = new NetworkController.IconState(true, access$600, mobileSignalController2.getCallStrengthDescription(mobileSignalController2.mLastWwanLevel, false));
                MobileSignalController mobileSignalController3 = MobileSignalController.this;
                mobileSignalController3.notifyCallStateChange(iconState, mobileSignalController3.mSubscriptionInfo.getSubscriptionId());
            }
        };
        this.mImsMmTelManager = ImsMmTelManager.createForSubscriptionId(subscriptionInfo.getSubscriptionId());
        this.mMobileStatusTracker = new MobileStatusTracker(telephonyManager, looper, subscriptionInfo, subscriptionDefaults, this.mCallback);
        this.mProviderModelBehavior = featureFlags.isCombinedStatusBarSignalIconsEnabled();
        this.mProviderModelSetting = false;
        this.mCellLocationController = (CellLocationController) Dependency.get(CellLocationController.class);
        this.mRROsController = (RROsController) Dependency.get(RROsController.class);
    }

    public void setConfiguration(NetworkConfig networkConfig, Context context) {
        this.mMotoConfig = networkConfig;
        this.mConfig = networkConfig.mConfig;
        T t = this.mCurrentState;
        boolean z = true;
        ((SignalIcon$MobileState) t).configChange = !((SignalIcon$MobileState) t).configChange;
        this.mRROPkg = this.mRROsController.getRROPkg();
        ExtendedMobileDataInfo extendedMobileDataInfo = this.mExtendedInfo;
        extendedMobileDataInfo.subContext = context;
        NetworkConfig networkConfig2 = this.mMotoConfig;
        extendedMobileDataInfo.showSeparatedSignalBars = networkConfig2.showSeparatedSignalBars;
        extendedMobileDataInfo.enableActivityIconOnSB = networkConfig2.enableActivityIconOnSB;
        extendedMobileDataInfo.enableCustomActivityIconOnQS = networkConfig2.enableCustomActivityIconOnQS;
        updateInflateSignalStrength();
        resetIconMap(this.mMotoConfig);
        ExtendedMobileDataInfo extendedMobileDataInfo2 = this.mExtendedInfo;
        if (!this.mMotoConfig.showBothWifiAndMobileNetwork && !NetworkStateTracker.isMultipleSimDevice() && !MotoFeature.isPrcProduct()) {
            z = false;
        }
        extendedMobileDataInfo2.mobileShowMobileWhenWifiActive = z;
        updateTelephony();
    }

    public void resetIconMap(NetworkConfig networkConfig) {
        this.mNetworkToIconLookup = MobileMappings.mapIconSets(networkConfig.mConfig);
        this.mDefaultIcons = MobileMappings.getDefaultIcons(networkConfig.mConfig);
        CarrierNetworkType.loadCarrierNetworkMap(this.mContext, this.mMotoConfig, this.mDataTypeLookup, this.mSubscriptionInfo.getCarrierId());
    }

    public void setAirplaneMode(boolean z) {
        ((SignalIcon$MobileState) this.mCurrentState).airplaneMode = z;
        notifyListenersIfNecessary();
    }

    public void setUserSetupComplete(boolean z) {
        ((SignalIcon$MobileState) this.mCurrentState).userSetup = z;
        notifyListenersIfNecessary();
    }

    public void updateConnectivity(BitSet bitSet, BitSet bitSet2) {
        boolean z = bitSet2.get(this.mTransportType);
        ((SignalIcon$MobileState) this.mCurrentState).isDefault = bitSet.get(this.mTransportType);
        T t = this.mCurrentState;
        ((SignalIcon$MobileState) t).inetCondition = (z || !((SignalIcon$MobileState) t).isDefault || MotoFeature.isPrcProduct()) ? 1 : 0;
        if (((SignalIcon$MobileState) this.mCurrentState).dataSim) {
            notifyListenersIfNecessary();
        }
    }

    public void setCarrierNetworkChangeMode(boolean z) {
        ((SignalIcon$MobileState) this.mCurrentState).carrierNetworkChangeMode = z;
        updateTelephony();
    }

    public void onWifiConnectionStateChanged(boolean z, boolean z2) {
        T t = this.mCurrentState;
        if (((SignalIcon$MobileState) t).wifiEnabled != z || ((SignalIcon$MobileState) t).wifiConnected != z2) {
            ((SignalIcon$MobileState) t).wifiEnabled = z;
            ((SignalIcon$MobileState) t).wifiConnected = z2;
            updateTelephony();
        }
    }

    public void registerListener() {
        this.mMobileStatusTracker.setListening(true);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("mobile_data"), true, this.mObserver);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("mobile_data" + this.mSubscriptionInfo.getSubscriptionId()), true, this.mObserver);
        if (this.mProviderModelBehavior) {
            this.mReceiverHandler.post(this.mTryRegisterIms);
        }
        registerSpnReceiver();
        this.mMotoExtTM.listen(this.mMotoExt5GStateListener, 65536);
        this.mCellLocationController.registerListeners(((SignalIcon$MobileState) this.mCurrentState).slotId);
    }

    public void unregisterListener() {
        this.mMobileStatusTracker.setListening(false);
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
        this.mImsMmTelManager.unregisterImsRegistrationCallback(this.mRegistrationCallback);
        unregisterSpnReceiver();
        this.mMotoExtTM.listen(this.mMotoExt5GStateListener, 0);
        this.mCellLocationController.unregisterListeners();
    }

    private int getNetworkType() {
        if (!isUseQCOMApiToGetDataType()) {
            int overrideNetworkType = this.mTelephonyDisplayInfo.getOverrideNetworkType();
            if (overrideNetworkType == 0) {
                return this.mTelephonyDisplayInfo.getNetworkType();
            }
            return getOverrideNetworkType(overrideNetworkType);
        } else if (isQCOMApi5GDataValid()) {
            return getNrType(((SignalIcon$MobileState) this.mCurrentState).nrDataIconType);
        } else {
            int overrideNetworkType2 = this.mTelephonyDisplayInfo.getOverrideNetworkType();
            if (overrideNetworkType2 == 1 || overrideNetworkType2 == 2) {
                return getOverrideNetworkType(overrideNetworkType2);
            }
            return this.mTelephonyDisplayInfo.getNetworkType();
        }
    }

    private boolean isUseQCOMApiToGetDataType() {
        return this.mMotoConfig.enableOEM5GApi && Build.SOC_MANUFACTURER_IS_QCOM;
    }

    private boolean isQCOMApi5GDataValid() {
        T t = this.mCurrentState;
        return ((SignalIcon$MobileState) t).nrDataIconType == 1 || ((SignalIcon$MobileState) t).nrDataIconType == 2;
    }

    public int getNrType(int i) {
        return (i != 1 && !this.mRROsController.isVisibleCarrier()) ? R$styleable.Constraint_layout_goneMarginTop : R$styleable.Constraint_layout_goneMarginStart;
    }

    private int getOverrideNetworkType(int i) {
        if (i == 1) {
            return 19;
        }
        if (i != 2) {
            if (i == 3) {
                return R$styleable.Constraint_layout_goneMarginStart;
            }
            if (i != 5) {
                return 0;
            }
            if (this.mRROsController.isVisibleCarrier()) {
                return R$styleable.Constraint_layout_goneMarginStart;
            }
            return R$styleable.Constraint_layout_goneMarginTop;
        } else if (isModemSupports5G()) {
            return R$styleable.Constraint_layout_goneMarginRight;
        } else {
            return this.mTelephonyDisplayInfo.getNetworkType();
        }
    }

    private boolean isModemSupports5G() {
        boolean z = (this.mPhone.getSupportedRadioAccessFamily() & 524288) > 0;
        if (SignalController.DEBUG) {
            String str = this.mTag;
            Log.i(str, "modemSupports5g " + z);
        }
        return z;
    }

    private void updateInflateSignalStrength() {
        this.mInflateSignalStrengths = SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, this.mSubscriptionInfo.getSubscriptionId());
    }

    private int getNumLevels() {
        int numSignalStrengthLevels;
        if (this.mMotoConfig.enableCustomizations) {
            numSignalStrengthLevels = ((SignalIcon$MobileState) this.mCurrentState).maxLevel;
        } else if (!this.mInflateSignalStrengths) {
            return CellSignalStrength.getNumSignalStrengthLevels();
        } else {
            numSignalStrengthLevels = CellSignalStrength.getNumSignalStrengthLevels();
        }
        return numSignalStrengthLevels + 1;
    }

    public int getCurrentIconId() {
        NetworkConfig networkConfig = this.mMotoConfig;
        boolean z = false;
        boolean z2 = true;
        if (networkConfig.showSeparatedSignalBars) {
            T t = this.mCurrentState;
            return CarrierIcons.SignalStrengthIcon.getCurrentIconId(true, ((SignalIcon$MobileState) t).connected, ((SignalIcon$MobileState) t).maxLevel, ((SignalIcon$MobileState) t).level, ((SignalIcon$MobileState) t).inetCondition == 1 || !((SignalIcon$MobileState) t).dataSim, ((SignalIcon$MobileState) t).isDualSignal, networkConfig.showExclamationMarked, networkConfig.showVzwSignalIcon);
        }
        T t2 = this.mCurrentState;
        if (((SignalIcon$MobileState) t2).iconGroup == TelephonyIcons.CARRIER_NETWORK_CHANGE) {
            return SignalDrawable.getCarrierChangeState(getNumLevels());
        }
        if (((SignalIcon$MobileState) t2).connected) {
            int i = ((SignalIcon$MobileState) t2).level;
            if (this.mInflateSignalStrengths) {
                i++;
            }
            if (((SignalIcon$MobileState) t2).inetCondition == 0 && ((SignalIcon$MobileState) t2).dataSim) {
                z = true;
            }
            return SignalDrawable.getState(i, getNumLevels(), z);
        } else if (!((SignalIcon$MobileState) t2).enabled) {
            return 0;
        } else {
            if (((SignalIcon$MobileState) t2).inetCondition != 0 || !((SignalIcon$MobileState) t2).dataSim) {
                z2 = false;
            }
            return SignalDrawable.getState(0, getNumLevels(), z2);
        }
    }

    public int getQsCurrentIconId() {
        return getCurrentIconId();
    }

    public void notifyListeners(NetworkController.SignalCallback signalCallback) {
        String str;
        int i;
        NetworkController.IconState iconState;
        if (!this.mNetworkController.isCarrierMergedWifi(this.mSubscriptionInfo.getSubscriptionId())) {
            SignalIcon$MobileIconGroup signalIcon$MobileIconGroup = (SignalIcon$MobileIconGroup) getIcons();
            String charSequence = getTextIfExists(getContentDescriptionId()).toString();
            Html.fromHtml(getTextIfExists(signalIcon$MobileIconGroup.dataContentDescription).toString(), 0).toString();
            T t = this.mCurrentState;
            boolean z = ((SignalIcon$MobileState) t).dataConnected || ((SignalIcon$MobileState) t).roaming || ((((SignalIcon$MobileState) t).iconGroup == TelephonyIcons.DATA_DISABLED || ((SignalIcon$MobileState) t).iconGroup == TelephonyIcons.NOT_DEFAULT_DATA) && ((SignalIcon$MobileState) t).userSetup);
            NetworkController.IconState iconState2 = new NetworkController.IconState(((SignalIcon$MobileState) t).enabled && !((SignalIcon$MobileState) t).airplaneMode && ((SignalIcon$MobileState) t).uiccCardState == 1, getCurrentIconId(), charSequence);
            T t2 = this.mCurrentState;
            String str2 = null;
            if (((SignalIcon$MobileState) t2).dataSim) {
                int i2 = (z || this.mConfig.alwaysShowDataRatIcon) ? signalIcon$MobileIconGroup.qsDataType : 0;
                iconState = new NetworkController.IconState(((SignalIcon$MobileState) t2).enabled && !((SignalIcon$MobileState) t2).isEmergency, getQsCurrentIconId(), charSequence);
                T t3 = this.mCurrentState;
                if (!((SignalIcon$MobileState) t3).isEmergency) {
                    str2 = ((SignalIcon$MobileState) t3).networkName;
                }
                i = i2;
                str = str2;
            } else {
                i = 0;
                iconState = null;
                str = null;
            }
            T t4 = this.mCurrentState;
            if (((SignalIcon$MobileState) t4).dataConnected && !((SignalIcon$MobileState) t4).carrierNetworkChangeMode) {
                boolean z2 = ((SignalIcon$MobileState) t4).activityIn;
            }
            if (((SignalIcon$MobileState) t4).dataConnected && !((SignalIcon$MobileState) t4).carrierNetworkChangeMode) {
                boolean z3 = ((SignalIcon$MobileState) t4).activityOut;
            }
            if (!z) {
                boolean z4 = this.mConfig.alwaysShowDataRatIcon;
            }
            boolean z5 = ((SignalIcon$MobileState) t4).enabled && !((SignalIcon$MobileState) t4).airplaneMode;
            if (this.mMotoConfig.enableCustomizations) {
                int dataState = NetworkStateTracker.getDataState(this.mServiceState, (SignalIcon$MobileState) this.mCurrentState, this.mExtendedInfo, this.mMotoConfig, this.mDataState, isDataDisabled(), isVoice1xOverrideMode() && this.mMotoConfig.showNoIconDuringCdmaVoiceCall);
                CarrierIcons.DataTypeGroup dataGroup = getDataGroup();
                int sbData = dataGroup.sbData(((SignalIcon$MobileState) this.mCurrentState).isDualSignal, dataState);
                this.mExtendedInfo.typeIconForQSCarrier = dataGroup.sbData(false, dataState);
                CharSequence textIfExists = getTextIfExists(dataGroup.sbDataDescription());
                String obj = Html.fromHtml(textIfExists.toString(), 0).toString();
                boolean z6 = sbData != 0;
                ExtendedMobileDataInfo extendedMobileDataInfo = this.mExtendedInfo;
                ServiceState serviceState = this.mServiceState;
                T t5 = this.mCurrentState;
                boolean z7 = ((SignalIcon$MobileState) t5).dataConnected;
                boolean z8 = ((SignalIcon$MobileState) t5).activityIn;
                boolean z9 = ((SignalIcon$MobileState) t5).activityOut;
                int i3 = this.mDataState;
                boolean z10 = ((SignalIcon$MobileState) t5).wifiConnected;
                boolean z11 = ((SignalIcon$MobileState) t5).isDefault;
                boolean isCdma = isCdma();
                boolean isRoaming = isRoaming();
                boolean z12 = this.mMotoConfig.enableFemtocellIndicator && (((SignalIcon$MobileState) this.mCurrentState).isFemtoCell || MotoSystemUIUtils.updateCdmaFemtoIcon(this.mServiceState));
                boolean z13 = ((SignalIcon$MobileState) this.mCurrentState).isDualSignal;
                String str3 = obj;
                NetworkConfig networkConfig = this.mMotoConfig;
                int i4 = i;
                NetworkStateTracker.updateExtendedInfo(extendedMobileDataInfo, serviceState, z7, z8, z9, i3, z6, z10, z11, isCdma, isRoaming, z12, z13, 0, networkConfig.showAttRat, networkConfig.showVzwRat);
                if (this.mMotoConfig.hideActivityIconWhile5G && this.mDataNetType > 100) {
                    this.mExtendedInfo.clearActivityIcon();
                }
                signalCallback.setMobileDataIndicators(new NetworkController.MobileDataIndicators(iconState2, iconState, sbData, i4, false, false, str3, textIfExists, str, signalIcon$MobileIconGroup.isWide, this.mSubscriptionInfo.getSubscriptionId(), ((SignalIcon$MobileState) this.mCurrentState).roaming, z5, this.mExtendedInfo));
            }
        }
    }

    public void updateDualSignalFlag(boolean z) {
        ((SignalIcon$MobileState) this.mCurrentState).isDualSignal = z;
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: protected */
    public SignalIcon$MobileState cleanState() {
        return new SignalIcon$MobileState();
    }

    private boolean isCdma() {
        if (!this.mMotoConfig.enableCustomizations) {
            SignalStrength signalStrength = this.mSignalStrength;
            if (signalStrength == null || signalStrength.isGsm()) {
                return false;
            }
            return true;
        }
        SignalStrength signalStrength2 = this.mSignalStrength;
        boolean z = signalStrength2 != null && !signalStrength2.isGsm();
        ServiceState serviceState = this.mServiceState;
        if (serviceState == null) {
            return z;
        }
        int rilDataRadioTechnology = serviceState.getRilDataRadioTechnology();
        return (rilDataRadioTechnology >= 4 && rilDataRadioTechnology <= 8) || rilDataRadioTechnology == 12 || rilDataRadioTechnology == 13 || ((rilDataRadioTechnology == 0 || (rilDataRadioTechnology == 14 && this.mServiceState.getRilVoiceRadioTechnology() != 14)) && z);
    }

    public boolean isEmergencyOnly() {
        ServiceState serviceState = this.mServiceState;
        return serviceState != null && serviceState.isEmergencyOnly();
    }

    /* access modifiers changed from: package-private */
    public String getNetworkNameForCarrierWiFi() {
        return this.mPhone.getSimOperatorName();
    }

    private boolean isRoaming() {
        if (this.mMotoConfig.roamingIndicationDisabled || isCarrierNetworkChangeActive()) {
            return false;
        }
        if (!isCdma()) {
            ServiceState serviceState = this.mServiceState;
            if (serviceState == null || !serviceState.getRoaming()) {
                return false;
            }
            return true;
        } else if (this.mPhone.getCdmaEnhancedRoamingIndicatorDisplayNumber() != 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCarrierNetworkChangeActive() {
        return ((SignalIcon$MobileState) this.mCurrentState).carrierNetworkChangeMode;
    }

    public void handleBroadcast(Intent intent) {
        int intExtra;
        String action = intent.getAction();
        if (action.equals("android.telephony.action.SERVICE_PROVIDERS_UPDATED")) {
            updateNetworkName(intent.getBooleanExtra("android.telephony.extra.SHOW_SPN", false), intent.getStringExtra("android.telephony.extra.SPN"), intent.getStringExtra("android.telephony.extra.DATA_SPN"), intent.getBooleanExtra("android.telephony.extra.SHOW_PLMN", false), intent.getStringExtra("android.telephony.extra.PLMN"));
            if (this.mMotoConfig.enableATTrejectCode) {
                T t = this.mCurrentState;
                ((SignalIcon$MobileState) t).rejectCode = 0;
                if (((SignalIcon$MobileState) this.mLastState).rejectCode != ((SignalIcon$MobileState) t).rejectCode) {
                    this.mNetworkController.recalculateEmergency();
                }
                if (SignalController.DEBUG) {
                    Log.d("rejectCode", "Receive rejectcode from spn = " + ((SignalIcon$MobileState) this.mCurrentState).rejectCode);
                }
            }
            notifyListenersIfNecessary();
        } else if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
            updateDataSim();
            notifyListenersIfNecessary();
        } else if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
            int intExtra2 = intent.getIntExtra("subscription", -1);
            if (intExtra2 == this.mSubscriptionInfo.getSubscriptionId()) {
                updateSubscriptionInfo(intExtra2);
                this.mIsSIMLocked = "LOCKED".equals(intent.getStringExtra("ss"));
                ((SignalIcon$MobileState) this.mCurrentState).uiccCardState = this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(intExtra2);
                notifyListenersIfNecessary();
            }
        } else {
            String str = "";
            if ("com.motorola.cellbroadcastreceiver.CB_INFO_ON_SYSTEMUI".equals(action)) {
                boolean booleanExtra = intent.getBooleanExtra("enable", false);
                if (SignalController.DEBUG) {
                    String str2 = this.mTag;
                    Log.d(str2, "ACTION_ENABLE_CB_INFO_ON_SYSTEMUI enable = " + booleanExtra);
                }
                T t2 = this.mCurrentState;
                if (((SignalIcon$MobileState) t2).isCellBroadcastEnabled != booleanExtra) {
                    ((SignalIcon$MobileState) t2).isCellBroadcastEnabled = booleanExtra;
                    if (!booleanExtra) {
                        ((SignalIcon$MobileState) t2).cellBroadcastMessage = str;
                    }
                    this.mNetworkController.refreshPanelCarrierLabel();
                }
            } else if ("com.android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED".equals(action)) {
                if (((SignalIcon$MobileState) this.mCurrentState).isCellBroadcastEnabled) {
                    String stringExtra = intent.getExtras() != null ? intent.getStringExtra("message") : str;
                    if (stringExtra != null) {
                        str = stringExtra;
                    }
                    if (SignalController.DEBUG) {
                        String str3 = this.mTag;
                        Log.d(str3, "CB_AREA_INFO_RECEIVED_ACTION cbMsg = " + str);
                    }
                    if (!((SignalIcon$MobileState) this.mCurrentState).cellBroadcastMessage.equals(str)) {
                        ((SignalIcon$MobileState) this.mCurrentState).cellBroadcastMessage = str;
                        this.mNetworkController.refreshPanelCarrierLabel();
                    }
                }
            } else if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED") && (intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1)) == this.mSubscriptionInfo.getSubscriptionId()) {
                updateSubscriptionInfo(intExtra);
            }
        }
    }

    private void updateSubscriptionInfo(int i) {
        SubscriptionInfo activeSubscriptionInfo = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfo(i);
        if (activeSubscriptionInfo != null && !this.mSubscriptionInfo.equals(activeSubscriptionInfo)) {
            if (SignalController.DEBUG) {
                String str = this.mTag;
                Log.d(str, "mSubscriptionInfo:" + this.mSubscriptionInfo + " new SubscriptionInfo:" + activeSubscriptionInfo);
            }
            this.mSubscriptionInfo = activeSubscriptionInfo;
        }
    }

    private void updateDataSim() {
        int activeDataSubId = this.mDefaults.getActiveDataSubId();
        boolean z = true;
        if (SubscriptionManager.isValidSubscriptionId(activeDataSubId)) {
            SignalIcon$MobileState signalIcon$MobileState = (SignalIcon$MobileState) this.mCurrentState;
            if (activeDataSubId != this.mSubscriptionInfo.getSubscriptionId()) {
                z = false;
            }
            signalIcon$MobileState.dataSim = z;
            return;
        }
        ((SignalIcon$MobileState) this.mCurrentState).dataSim = true;
    }

    /* access modifiers changed from: package-private */
    public void updateNetworkName(boolean z, String str, String str2, boolean z2, String str3) {
        String str4;
        if (SignalController.DEBUG) {
            Log.d("CarrierLabel", "updateNetworkName showSpn=" + z + " spn=" + str + " dataSpn=" + str2 + " showPlmn=" + z2 + " plmn=" + str3);
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        if (z2 && str3 != null) {
            sb.append(str3);
            sb2.append(str3);
        }
        if (z && str != null) {
            if (sb.length() != 0) {
                sb.append(" ");
                sb.append(this.mNetworkNamebracketsBegin);
            }
            sb.append(str);
            if (z2 && str3 != null) {
                sb.append(this.mNetworkNamebracketsEnd);
            }
        }
        if (sb.length() != 0) {
            ((SignalIcon$MobileState) this.mCurrentState).networkName = sb.toString();
        } else {
            SignalIcon$MobileState signalIcon$MobileState = (SignalIcon$MobileState) this.mCurrentState;
            if (Utils.isInService(this.mServiceState)) {
                str4 = "";
            } else {
                str4 = this.mNetworkNameDefault;
            }
            signalIcon$MobileState.networkName = str4;
        }
        if (z && str2 != null) {
            if (sb2.length() != 0) {
                sb2.append(this.mNetworkNameSeparator);
            }
            sb2.append(str2);
        }
        if (sb2.length() != 0) {
            ((SignalIcon$MobileState) this.mCurrentState).networkNameData = sb2.toString();
        } else {
            ((SignalIcon$MobileState) this.mCurrentState).networkNameData = this.mNetworkNameDefault;
        }
        ((SignalIcon$MobileState) this.mCurrentState).shortFormLabel = CarrierLabelUpdateMonitor.getShortFormNetworkName(this.mMotoConfig, isRoaming(), true, z, str, z2, str3);
        this.mNetworkController.refreshShortFormLabel();
        this.mNetworkController.refreshPanelCarrierLabel();
    }

    private int getCdmaLevel(SignalStrength signalStrength) {
        List<CellSignalStrengthCdma> cellSignalStrengths = signalStrength.getCellSignalStrengths(CellSignalStrengthCdma.class);
        if (!cellSignalStrengths.isEmpty()) {
            return cellSignalStrengths.get(0).getLevel();
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void updateMobileStatus(MobileStatusTracker.MobileStatus mobileStatus) {
        T t = this.mCurrentState;
        ((SignalIcon$MobileState) t).activityIn = mobileStatus.activityIn;
        ((SignalIcon$MobileState) t).activityOut = mobileStatus.activityOut;
        ((SignalIcon$MobileState) t).dataSim = mobileStatus.dataSim;
        ((SignalIcon$MobileState) t).carrierNetworkChangeMode = mobileStatus.carrierNetworkChangeMode;
        this.mDataState = mobileStatus.dataState;
        notifyMobileLevelChangeIfNecessary(mobileStatus.signalStrength);
        this.mSignalStrength = mobileStatus.signalStrength;
        this.mTelephonyDisplayInfo = mobileStatus.telephonyDisplayInfo;
        ServiceState serviceState = this.mServiceState;
        int state = serviceState != null ? serviceState.getState() : -1;
        if (this.mMotoConfig.enableEriSounds) {
            this.mNetworkController.updateEri(mobileStatus.serviceState, this.mServiceState);
        }
        ServiceState serviceState2 = mobileStatus.serviceState;
        this.mServiceState = serviceState2;
        updateMobileServiceState(serviceState2);
        if (!DesktopFeature.isDesktopDisplayContext(this.mContext)) {
            NotifyLowSignalStrength instance = NotifyLowSignalStrength.getInstance(this.mContext);
            SignalStrength signalStrength = this.mSignalStrength;
            T t2 = this.mCurrentState;
            instance.notifyLowSignalStrengthIfNeeded(signalStrength, ((SignalIcon$MobileState) t2).subId, ((SignalIcon$MobileState) t2).slotId);
        }
        updateVolteIconState();
        ServiceState serviceState3 = this.mServiceState;
        int state2 = serviceState3 != null ? serviceState3.getState() : -1;
        if (this.mProviderModelBehavior && state != state2) {
            if (state == -1 || state == 0 || state2 == 0) {
                notifyCallStateChange(new NetworkController.IconState((state2 != 0) & (true ^ hideNoCalling()), R$drawable.ic_qs_no_calling_sms, getTextIfExists(AccessibilityContentDescriptions.NO_CALLING).toString()), this.mSubscriptionInfo.getSubscriptionId());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateNoCallingState() {
        ServiceState serviceState = this.mServiceState;
        notifyCallStateChange(new NetworkController.IconState(((serviceState != null ? serviceState.getState() : -1) != 0) & (true ^ hideNoCalling()), R$drawable.ic_qs_no_calling_sms, getTextIfExists(AccessibilityContentDescriptions.NO_CALLING).toString()), this.mSubscriptionInfo.getSubscriptionId());
    }

    private boolean hideNoCalling() {
        return this.mNetworkController.hasDefaultNetwork() && this.mCarrierConfigTracker.getNoCallingConfig(this.mSubscriptionInfo.getSubscriptionId());
    }

    private void updateVolteIconState() {
        int i = this.mDataNetType;
        final boolean z = false;
        if ((i == 13 || i == 19) || is5GNetType()) {
            z = true;
        }
        final boolean isInService = Utils.isInService(this.mServiceState);
        this.mHandler.post(new Runnable() {
            public void run() {
                ImsIconController.getInstance(MobileSignalController.this.mContext).updateVolteIcon(z && isInService, ((SignalIcon$MobileState) MobileSignalController.this.mCurrentState).slotId);
            }
        });
    }

    private boolean is5GNetType() {
        int i = this.mDataNetType;
        return i == 20 || i == 101 || i == 102 || i == 103;
    }

    private void registerSpnReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.SERVICE_PROVIDERS_UPDATED");
        this.mCurrentContext.registerReceiver(this.mSpnReceiver, intentFilter);
    }

    private void unregisterSpnReceiver() {
        this.mNetworkController.refreshShortFormLabel();
        this.mNetworkController.refreshPanelCarrierLabel();
        this.mCurrentContext.unregisterReceiver(this.mSpnReceiver);
    }

    /* access modifiers changed from: private */
    public void updateMobileServiceState(ServiceState serviceState) {
        if (TextUtils.equals("", ((SignalIcon$MobileState) this.mCurrentState).networkName) && !Utils.isInService(this.mServiceState)) {
            ((SignalIcon$MobileState) this.mCurrentState).networkName = this.mNetworkNameDefault;
        }
        if (serviceState != null) {
            int i = 3;
            if (serviceState.getDataRegState() == 0) {
                this.mDataNetType = getNetworkType();
                if (this.mDataState != 2) {
                    this.mDataState = 3;
                }
            } else {
                this.mDataNetType = getVoiceTechnology(serviceState);
                if (this.mDataState != 2) {
                    if (serviceState.getVoiceRegState() != 0) {
                        i = 0;
                    }
                    this.mDataState = i;
                }
            }
            if (this.mDataNetType == 18) {
                this.mDataNetType = getVoiceTechnology(serviceState);
                if (SignalController.DEBUG) {
                    String str = this.mTag;
                    Log.i(str, "Update IWLAN network with voice type: " + this.mDataNetType);
                }
            }
            ((SignalIcon$MobileState) this.mCurrentState).isFemtoCell = serviceState.isFemtoCell();
            if (SignalController.DEBUG) {
                String str2 = this.mTag;
                Log.i(str2, "updateMobileServiceState type: " + this.mDataNetType + " isFemtoCell = " + ((SignalIcon$MobileState) this.mCurrentState).isFemtoCell + " mDataState = " + this.mDataState + " isQCOM = " + Build.SOC_MANUFACTURER_IS_QCOM);
            }
        }
    }

    private int getVoiceTechnology(ServiceState serviceState) {
        if (serviceState.getVoiceRegState() != 0) {
            return 0;
        }
        if (!isUseQCOMApiToGetDataType() || !isQCOMApi5GDataValid()) {
            return serviceState.getVoiceNetworkType();
        }
        return getNrType(((SignalIcon$MobileState) this.mCurrentState).nrDataIconType);
    }

    private CarrierIcons.DataTypeGroup getDataGroup() {
        ServiceState serviceState;
        int voiceNetworkType;
        int i = this.mDataNetType;
        if (!(!TextUtils.equals(this.mMotoConfig.operatorName, "vzw") || this.mPhone.isVolteAvailable() || is5GNetType() || (serviceState = this.mServiceState) == null || ((SignalIcon$MobileState) this.mCurrentState).callState == 0 || (voiceNetworkType = serviceState.getVoiceNetworkType()) == 0 || voiceNetworkType == 18)) {
            i = voiceNetworkType;
        }
        if (SignalController.DEBUG) {
            String str = this.mTag;
            Log.d(str, "Data type = " + i + " RROPkg = " + this.mRROPkg);
        }
        if (this.mDataTypeLookup.indexOfKey(i) >= 0) {
            return this.mDataTypeLookup.get(i);
        }
        return this.mDataTypeLookup.get(1);
    }

    private int getContentDescriptionId() {
        if (!this.mMotoConfig.enableCustomizations) {
            return getContentDescription();
        }
        T t = this.mCurrentState;
        if (((SignalIcon$MobileState) t).connected) {
            return MotoAccessibilityContentDescriptions.getContentDescription(((SignalIcon$MobileState) t).maxLevel, ((SignalIcon$MobileState) t).level);
        }
        return MotoAccessibilityContentDescriptions.getContentDescription(((SignalIcon$MobileState) t).maxLevel, 0);
    }

    private boolean isLte() {
        ServiceState serviceState = this.mServiceState;
        return serviceState != null && (serviceState.getRilVoiceRadioTechnology() == 14 || this.mServiceState.getRilDataRadioTechnology() == 14);
    }

    private boolean isVoice1xOverrideMode() {
        ServiceState serviceState;
        if (!this.mMotoConfig.enable1xOverrideDuringCdmaVoiceCall) {
            return false;
        }
        if (!isCdma() && !isLte()) {
            return false;
        }
        SignalStrength signalStrength = this.mSignalStrength;
        boolean z = signalStrength != null && !signalStrength.isGsm();
        if (((SignalIcon$MobileState) this.mCurrentState).callState == 0 || !z || (serviceState = this.mServiceState) == null || serviceState.getRilVoiceRadioTechnology() == 14) {
            return false;
        }
        int i = this.mDataNetType;
        if ((i == 7 || i == 4) && new MotoExtTelephonyManager(this.mCurrentContext, this.mSubscriptionInfo.getSubscriptionId()).getActiveCallType() == 0) {
            return true;
        }
        return false;
    }

    public String getLabel(String str, boolean z) {
        String str2;
        T t = this.mCurrentState;
        String str3 = "";
        if (!((SignalIcon$MobileState) t).enabled) {
            return str3;
        }
        if (((SignalIcon$MobileState) t).dataConnected) {
            str2 = ((SignalIcon$MobileState) t).networkName;
        } else {
            if (((SignalIcon$MobileState) t).isEmergency) {
                if (((SignalIcon$MobileState) t).rejectCode != 17) {
                    str2 = ((SignalIcon$MobileState) t).networkName;
                }
            } else if (((SignalIcon$MobileState) t).airplaneMode) {
                if (this.mPhone.isWifiCallingAvailable()) {
                    if (SignalController.DEBUG) {
                        Log.d(this.mTag, "Wifi Calling Available");
                    }
                    str2 = ((SignalIcon$MobileState) this.mCurrentState).networkName;
                } else if (this.mMotoConfig.showAirplaneModeForWFC && TextUtils.isEmpty(str)) {
                    str2 = this.mCurrentContext.getString(R$string.airplane_mode);
                }
            } else if (this.mIsSIMLocked) {
                str2 = ((SignalIcon$MobileState) t).networkName;
            } else if (!this.mMotoConfig.enableCustomizations) {
                str2 = this.mCurrentContext.getString(R$string.data_connection_no_internet);
            } else if (((SignalIcon$MobileState) t).connected) {
                str2 = ((SignalIcon$MobileState) t).networkName;
            }
            str2 = str3;
        }
        if (!(str.length() == 0 || str2.length() == 0)) {
            str = str + this.mMotoConfig.networkNameSeparator;
        }
        if (z) {
            return str + str2;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        if (((SignalIcon$MobileState) this.mCurrentState).dataConnected) {
            str3 = str2;
        }
        sb.append(str3);
        return sb.toString();
    }

    public SignalIcon$MobileState getMobileState() {
        return (SignalIcon$MobileState) this.mCurrentState;
    }

    public SubscriptionInfo getSubscriptionInfo() {
        return this.mSubscriptionInfo;
    }

    /* access modifiers changed from: private */
    public int getCallStrengthIcon(int i, boolean z) {
        if (z) {
            return TelephonyIcons.WIFI_CALL_STRENGTH_ICONS[i];
        }
        return TelephonyIcons.MOBILE_CALL_STRENGTH_ICONS[i];
    }

    /* access modifiers changed from: private */
    public String getCallStrengthDescription(int i, boolean z) {
        if (z) {
            return getTextIfExists(AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH[i]).toString();
        }
        return getTextIfExists(AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[i]).toString();
    }

    /* access modifiers changed from: package-private */
    public void refreshCallIndicator(NetworkController.SignalCallback signalCallback) {
        ServiceState serviceState = this.mServiceState;
        NetworkController.IconState iconState = new NetworkController.IconState(((serviceState == null || serviceState.getState() == 0) ? false : true) & (!hideNoCalling()), R$drawable.ic_qs_no_calling_sms, getTextIfExists(AccessibilityContentDescriptions.NO_CALLING).toString());
        signalCallback.setCallIndicator(iconState, this.mSubscriptionInfo.getSubscriptionId());
        int i = this.mImsType;
        if (i == 1) {
            iconState = new NetworkController.IconState(true, getCallStrengthIcon(this.mLastWwanLevel, false), getCallStrengthDescription(this.mLastWwanLevel, false));
        } else if (i == 2) {
            iconState = new NetworkController.IconState(true, getCallStrengthIcon(this.mLastWlanLevel, true), getCallStrengthDescription(this.mLastWlanLevel, true));
        } else if (i == 3) {
            iconState = new NetworkController.IconState(true, getCallStrengthIcon(this.mLastWlanCrossSimLevel, false), getCallStrengthDescription(this.mLastWlanCrossSimLevel, false));
        }
        signalCallback.setCallIndicator(iconState, this.mSubscriptionInfo.getSubscriptionId());
    }

    /* access modifiers changed from: package-private */
    public void notifyWifiLevelChange(int i) {
        if (this.mProviderModelBehavior) {
            this.mLastWlanLevel = i;
            if (this.mImsType == 2) {
                notifyCallStateChange(new NetworkController.IconState(true, getCallStrengthIcon(i, true), getCallStrengthDescription(i, true)), this.mSubscriptionInfo.getSubscriptionId());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyDefaultMobileLevelChange(int i) {
        if (this.mProviderModelBehavior) {
            this.mLastWlanCrossSimLevel = i;
            if (this.mImsType == 3) {
                notifyCallStateChange(new NetworkController.IconState(true, getCallStrengthIcon(i, false), getCallStrengthDescription(i, false)), this.mSubscriptionInfo.getSubscriptionId());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyMobileLevelChangeIfNecessary(SignalStrength signalStrength) {
        int signalLevel;
        if (this.mProviderModelBehavior && (signalLevel = getSignalLevel(signalStrength)) != this.mLastLevel) {
            this.mLastLevel = signalLevel;
            this.mLastWwanLevel = signalLevel;
            if (this.mImsType == 1) {
                notifyCallStateChange(new NetworkController.IconState(true, getCallStrengthIcon(signalLevel, false), getCallStrengthDescription(signalLevel, false)), this.mSubscriptionInfo.getSubscriptionId());
            }
            if (((SignalIcon$MobileState) this.mCurrentState).dataSim) {
                this.mNetworkController.notifyDefaultMobileLevelChange(signalLevel);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getSignalLevel(SignalStrength signalStrength) {
        if (signalStrength == null) {
            return 0;
        }
        if ((this.mSignalStrength.isGsm() || !this.mConfig.alwaysShowCdmaRssi) && !isVoice1xOverrideMode()) {
            return signalStrength.getLevel();
        }
        return getCdmaLevel(signalStrength);
    }

    /* access modifiers changed from: private */
    public final void updateTelephony() {
        ServiceState serviceState;
        ServiceState serviceState2;
        if (SignalController.DEBUG) {
            Log.d(this.mTag, "updateTelephonySignalStrength: hasService=" + Utils.isInService(this.mServiceState) + " ss=" + this.mSignalStrength + " voice1xOverrideMode: " + isVoice1xOverrideMode() + " displayInfo=" + this.mTelephonyDisplayInfo);
        }
        checkDefaultData();
        boolean z = true;
        ((SignalIcon$MobileState) this.mCurrentState).connected = Utils.isInService(this.mServiceState) && this.mSignalStrength != null;
        T t = this.mCurrentState;
        if (((SignalIcon$MobileState) t).connected) {
            ((SignalIcon$MobileState) t).level = getSignalLevel(this.mSignalStrength);
            ((SignalIcon$MobileState) this.mCurrentState).maxLevel = this.mSignalStrength.getMaxLevel();
        }
        String iconKey = MobileMappings.getIconKey(this.mTelephonyDisplayInfo);
        if (this.mNetworkToIconLookup.get(iconKey) != null) {
            ((SignalIcon$MobileState) this.mCurrentState).iconGroup = this.mNetworkToIconLookup.get(iconKey);
        } else {
            ((SignalIcon$MobileState) this.mCurrentState).iconGroup = this.mDefaultIcons;
        }
        T t2 = this.mCurrentState;
        SignalIcon$MobileState signalIcon$MobileState = (SignalIcon$MobileState) t2;
        if (!((SignalIcon$MobileState) t2).connected || this.mDataState != 2) {
            z = false;
        }
        signalIcon$MobileState.dataConnected = z;
        ((SignalIcon$MobileState) t2).dataNetType = this.mDataNetType;
        ((SignalIcon$MobileState) t2).roaming = isRoaming();
        ((SignalIcon$MobileState) this.mCurrentState).dataRoamingEnabled = this.mPhone.isDataRoamingEnabled();
        if (isCarrierNetworkChangeActive()) {
            ((SignalIcon$MobileState) this.mCurrentState).iconGroup = TelephonyIcons.CARRIER_NETWORK_CHANGE;
        } else if (isDataDisabled() && !this.mConfig.alwaysShowDataRatIcon) {
            if (this.mSubscriptionInfo.getSubscriptionId() != this.mDefaults.getDefaultDataSubId()) {
                ((SignalIcon$MobileState) this.mCurrentState).iconGroup = TelephonyIcons.NOT_DEFAULT_DATA;
            } else {
                ((SignalIcon$MobileState) this.mCurrentState).iconGroup = TelephonyIcons.DATA_DISABLED;
            }
        }
        boolean isEmergencyOnly = isEmergencyOnly();
        T t3 = this.mCurrentState;
        if (isEmergencyOnly != ((SignalIcon$MobileState) t3).isEmergency) {
            ((SignalIcon$MobileState) t3).isEmergency = isEmergencyOnly();
            this.mNetworkController.recalculateEmergency();
        }
        if (((SignalIcon$MobileState) this.mCurrentState).networkName.equals(this.mNetworkNameDefault) && (serviceState2 = this.mServiceState) != null && !TextUtils.isEmpty(serviceState2.getOperatorAlphaShort())) {
            ((SignalIcon$MobileState) this.mCurrentState).networkName = this.mServiceState.getOperatorAlphaShort();
        }
        if (((SignalIcon$MobileState) this.mCurrentState).networkNameData.equals(this.mNetworkNameDefault) && (serviceState = this.mServiceState) != null && ((SignalIcon$MobileState) this.mCurrentState).dataSim && !TextUtils.isEmpty(serviceState.getOperatorAlphaShort())) {
            ((SignalIcon$MobileState) this.mCurrentState).networkNameData = this.mServiceState.getOperatorAlphaShort();
        }
        this.mNetworkController.refreshShortFormLabel();
        this.mNetworkController.refreshPanelCarrierLabel();
        notifyListenersIfNecessary();
    }

    private void checkDefaultData() {
        T t = this.mCurrentState;
        if (((SignalIcon$MobileState) t).iconGroup != TelephonyIcons.NOT_DEFAULT_DATA) {
            ((SignalIcon$MobileState) t).defaultDataOff = false;
            return;
        }
        ((SignalIcon$MobileState) t).defaultDataOff = this.mNetworkController.isDataControllerDisabled();
    }

    /* access modifiers changed from: package-private */
    public void onMobileDataChanged() {
        checkDefaultData();
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: package-private */
    public boolean isDataDisabled() {
        return !this.mPhone.isDataConnectionAllowed();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setActivity(int i) {
        T t = this.mCurrentState;
        boolean z = false;
        ((SignalIcon$MobileState) t).activityIn = i == 3 || i == 1;
        SignalIcon$MobileState signalIcon$MobileState = (SignalIcon$MobileState) t;
        if (i == 3 || i == 2) {
            z = true;
        }
        signalIcon$MobileState.activityOut = z;
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: private */
    public void recordLastMobileStatus(String str) {
        String[] strArr = this.mMobileStatusHistory;
        int i = this.mMobileStatusHistoryIndex;
        strArr[i] = str;
        this.mMobileStatusHistoryIndex = (i + 1) % 64;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setImsType(int i) {
        this.mImsType = i;
    }

    class MotoExt5GStateListener extends MotoExtPhoneStateListener {
        public MotoExt5GStateListener(int i, Looper looper) {
            super(Integer.valueOf(i), looper);
        }

        public void onNrDataIconTypeChanged(int i) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onNrDataIconTypeChanged iconType=" + i);
            }
            MobileSignalController mobileSignalController = MobileSignalController.this;
            ((SignalIcon$MobileState) mobileSignalController.mCurrentState).nrDataIconType = i;
            mobileSignalController.updateMobileServiceState(mobileSignalController.mServiceState);
            MobileSignalController.this.updateTelephony();
        }
    }

    public void dump(PrintWriter printWriter) {
        super.dump(printWriter);
        printWriter.println("  mSubscription=" + this.mSubscriptionInfo + ",");
        printWriter.println("  mServiceState=" + this.mServiceState + ",");
        printWriter.println("  mSignalStrength=" + this.mSignalStrength + ",");
        printWriter.println("  mTelephonyDisplayInfo=" + this.mTelephonyDisplayInfo + ",");
        printWriter.println("  mDataState=" + this.mDataState + ",");
        printWriter.println("  mInflateSignalStrengths=" + this.mInflateSignalStrengths + ",");
        printWriter.println("  isDataDisabled=" + isDataDisabled() + ",");
        printWriter.println("  MobileStatusHistory");
        printWriter.println("  NetworkConfig:" + this.mMotoConfig + ",");
        int i = 0;
        for (int i2 = 0; i2 < 64; i2++) {
            if (this.mMobileStatusHistory[i2] != null) {
                i++;
            }
        }
        int i3 = this.mMobileStatusHistoryIndex + 64;
        while (true) {
            i3--;
            if (i3 >= (this.mMobileStatusHistoryIndex + 64) - i) {
                printWriter.println("  Previous MobileStatus(" + ((this.mMobileStatusHistoryIndex + 64) - i3) + "): " + this.mMobileStatusHistory[i3 & 63]);
            } else {
                return;
            }
        }
    }
}
