package com.android.systemui.p006qs.tiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.Prefs;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.moto.ExtendedMobileDataInfo;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.p006qs.MotoConfirmationStubActivity;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.SignalTileView;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.NetworkController;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.CellularTile */
public class CellularTile extends QSTileImpl<QSTile.SignalState> {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("CellularTile", 3));
    /* access modifiers changed from: private */
    public final NetworkController mController;
    /* access modifiers changed from: private */
    public final DataUsageController mDataController;
    /* access modifiers changed from: private */
    public final CellularDetailAdapter mDetailAdapter;
    /* access modifiers changed from: private */
    public final MotoExtTelephonyManager mMotoExtTM;
    private int mPhoneCount = 1;
    /* access modifiers changed from: private */
    public final CellSignalCallback mSignalCallback;
    protected final SubscriptionManager mSubscriptionManager;

    public int getMetricsCategory() {
        return 115;
    }

    public CellularTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, NetworkController networkController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        CellSignalCallback cellSignalCallback = new CellSignalCallback(this, (C12491) null);
        this.mSignalCallback = cellSignalCallback;
        this.mController = networkController;
        this.mDataController = networkController.getMobileDataController();
        this.mDetailAdapter = new CellularDetailAdapter(this, (C12491) null);
        networkController.observe(getLifecycle(), cellSignalCallback);
        this.mPhoneCount = NetworkStateTracker.sPhoneCount;
        this.mSubscriptionManager = SubscriptionManager.from(qSHost.getContext());
        this.mMotoExtTM = new MotoExtTelephonyManager(this.mContext);
    }

    public QSTile.SignalState newTileState() {
        return new QSTile.SignalState();
    }

    public QSIconView createTileView(Context context) {
        return new SignalTileView(context);
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    private Intent verifyAndGetIntentForMsimSettings() {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
        if (this.mPhoneCount <= 1 || activeSubscriptionInfoList == null) {
            return null;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        HashMap hashMap = new HashMap();
        boolean z = false;
        int i = 0;
        for (SubscriptionInfo next : activeSubscriptionInfoList) {
            int currentUiccCardProvisioningStatus = this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(next.getSubscriptionId());
            if (currentUiccCardProvisioningStatus != 1) {
                i++;
            }
            hashMap.put(Integer.valueOf(next.getSubscriptionId()), Integer.valueOf(currentUiccCardProvisioningStatus));
        }
        if (activeSubscriptionInfoList.size() > 1) {
            if (hashMap.get(Integer.valueOf(defaultDataSubscriptionId)) != null && ((Integer) hashMap.get(Integer.valueOf(defaultDataSubscriptionId))).intValue() == 1) {
                z = true;
            }
            if (i == 0 || i > 1 || (i > 0 && !z)) {
                return getMSimSettingsIntent();
            }
            return null;
        } else if (activeSubscriptionInfoList.size() <= 0) {
            return null;
        } else {
            if (i == 1 || defaultDataSubscriptionId != activeSubscriptionInfoList.get(0).getSubscriptionId()) {
                return getMSimSettingsIntent();
            }
            return null;
        }
    }

    public Intent getLongClickIntent() {
        if (((QSTile.SignalState) getState()).state == 0) {
            return new Intent("android.settings.WIRELESS_SETTINGS");
        }
        Intent verifyAndGetIntentForMsimSettings = verifyAndGetIntentForMsimSettings();
        if (verifyAndGetIntentForMsimSettings != null) {
            return verifyAndGetIntentForMsimSettings;
        }
        return getCellularSettingIntent();
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        Intent verifyAndGetIntentForMsimSettings;
        if (((QSTile.SignalState) getState()).state != 0) {
            if (((QSTile.SignalState) getState()).state == 1 && this.mSignalCallback.mInfo.simState != CallbackInfo.SimState.AVAILABLE && (verifyAndGetIntentForMsimSettings = verifyAndGetIntentForMsimSettings()) != null) {
                this.mActivityStarter.postStartActivityDismissingKeyguard(verifyAndGetIntentForMsimSettings, 0);
            } else if (this.mDataController.isMobileDataEnabled()) {
                maybeShowDisableDialog();
            } else {
                this.mDataController.setMobileDataEnabled(true);
            }
        }
    }

    private void maybeShowDisableDialog() {
        if ((!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) && MotoConfirmationStubActivity.isDialogEnabled(this.mContext)) {
            this.mHost.collapsePanels();
            MotoConfirmationStubActivity.sendDialogIntent(this.mContext);
        } else if (Prefs.getBoolean(this.mContext, "QsHasTurnedOffMobileData", false)) {
            this.mDataController.setMobileDataEnabled(false);
        } else {
            Context context = this.mContext;
            if (MotoFeature.getInstance(context).isSupportCli() && MotoFeature.isLidClosed(this.mContext)) {
                context = MotoFeature.getInstance(this.mContext).getCliBaseContext(this.mContext);
            }
            AlertDialog create = new AlertDialog.Builder(context).setTitle(R$string.mobile_data_disable_title).setMessage(this.mContext.getString(R$string.mobile_data_disable_message)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039650, new CellularTile$$ExternalSyntheticLambda0(this)).create();
            create.getWindow().setType(2009);
            SystemUIDialog.setShowForAllUsers(create, true);
            SystemUIDialog.registerDismissListener(create);
            SystemUIDialog.setWindowOnTop(create);
            create.show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$maybeShowDisableDialog$0(DialogInterface dialogInterface, int i) {
        this.mDataController.setMobileDataEnabled(false);
        Prefs.putBoolean(this.mContext, "QsHasTurnedOffMobileData", true);
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick(View view) {
        if (this.mDataController.isMobileDataSupported()) {
            showDetail(true);
        } else {
            this.mActivityStarter.postStartActivityDismissingKeyguard(getCellularSettingIntent(), 0);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_cellular_detail_title);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.SignalState signalState, Object obj) {
        CharSequence charSequence;
        QSTile.Icon icon;
        CallbackInfo callbackInfo = (CallbackInfo) obj;
        CallbackInfo callbackInfo2 = this.mSignalCallback.mInfo;
        if (DEBUG) {
            Log.d("CellularTile", "handleUpdateState - cb = " + callbackInfo2);
        }
        Resources resources = this.mContext.getResources();
        signalState.label = resources.getString(R$string.mobile_data);
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        boolean z = this.mDataController.isMobileDataSupported(defaultDataSubscriptionId) && this.mDataController.isMobileDataEnabled(defaultDataSubscriptionId);
        signalState.value = z;
        signalState.activityIn = z && callbackInfo2.activityIn;
        signalState.activityOut = z && callbackInfo2.activityOut;
        signalState.expandedAccessibilityClassName = Switch.class.getName();
        CallbackInfo.SimState simState = callbackInfo2.simState;
        CallbackInfo.SimState simState2 = CallbackInfo.SimState.AVAILABLE;
        if (simState != simState2) {
            if (callbackInfo2.isSimError) {
                icon = QSTileImpl.ResourceIcon.get(R$drawable.zz_moto_ic_qs_invalid_sim);
            } else {
                icon = QSTileImpl.ResourceIcon.get(R$drawable.ic_qs_no_sim);
            }
            signalState.icon = icon;
        } else {
            signalState.icon = QSTileImpl.ResourceIcon.get(R$drawable.ic_swap_vert);
        }
        if (callbackInfo2.airplaneModeEnabled) {
            signalState.state = 0;
            signalState.secondaryLabel = resources.getString(R$string.status_bar_airplane);
            signalState.value = false;
        } else {
            CallbackInfo.SimState simState3 = callbackInfo2.simState;
            if (simState3 != simState2) {
                signalState.state = 0;
                int i = C12491.f121xa0935a77[simState3.ordinal()];
                if (i == 1) {
                    signalState.secondaryLabel = resources.getString(R$string.keyguard_missing_sim_message_short);
                } else if (i == 2) {
                    signalState.secondaryLabel = resources.getString(R$string.keyguard_sim_error_message_short);
                } else if (i == 3) {
                    signalState.secondaryLabel = resources.getString(R$string.cellular_tile_secondary_label_disabled);
                    signalState.state = 1;
                } else if (i == 4) {
                    signalState.secondaryLabel = resources.getString(R$string.cellular_tile_secondary_label_locked);
                    signalState.state = 1;
                } else if (i == 5) {
                    signalState.secondaryLabel = resources.getString(R$string.cellular_tile_secondary_label_noDDS);
                    signalState.state = 1;
                }
            } else if (z) {
                signalState.state = 2;
                if (callbackInfo2.multipleSubs) {
                    charSequence = callbackInfo2.dataSimDisplayName;
                } else {
                    charSequence = "";
                }
                signalState.secondaryLabel = appendMobileDataType(charSequence, getMobileDataContentName(callbackInfo2));
            } else {
                signalState.state = 1;
                signalState.secondaryLabel = resources.getString(R$string.cell_data_off);
            }
        }
        signalState.contentDescription = signalState.label;
        if (signalState.state == 1) {
            signalState.stateDescription = "";
        } else {
            signalState.stateDescription = signalState.secondaryLabel;
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.CellularTile$1 */
    static /* synthetic */ class C12491 {

        /* renamed from: $SwitchMap$com$android$systemui$qs$tiles$CellularTile$CallbackInfo$SimState */
        static final /* synthetic */ int[] f121xa0935a77;

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|12) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState[] r0 = com.android.systemui.p006qs.tiles.CellularTile.CallbackInfo.SimState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f121xa0935a77 = r0
                com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState r1 = com.android.systemui.p006qs.tiles.CellularTile.CallbackInfo.SimState.NO_SIM     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f121xa0935a77     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState r1 = com.android.systemui.p006qs.tiles.CellularTile.CallbackInfo.SimState.SIM_ERROR     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f121xa0935a77     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState r1 = com.android.systemui.p006qs.tiles.CellularTile.CallbackInfo.SimState.DISABLED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f121xa0935a77     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState r1 = com.android.systemui.p006qs.tiles.CellularTile.CallbackInfo.SimState.LOCKED     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f121xa0935a77     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState r1 = com.android.systemui.p006qs.tiles.CellularTile.CallbackInfo.SimState.DDS_NOT_DEFINED     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.tiles.CellularTile.C12491.<clinit>():void");
        }
    }

    private CharSequence appendMobileDataType(CharSequence charSequence, CharSequence charSequence2) {
        if (TextUtils.isEmpty(charSequence2)) {
            return Html.fromHtml(charSequence.toString(), 0);
        }
        if (TextUtils.isEmpty(charSequence)) {
            return Html.fromHtml(charSequence2.toString(), 0);
        }
        return Html.fromHtml(this.mContext.getString(R$string.mobile_carrier_text_format, new Object[]{charSequence, charSequence2}), 0);
    }

    private CharSequence getMobileDataContentName(CallbackInfo callbackInfo) {
        if (callbackInfo.roaming && !TextUtils.isEmpty(callbackInfo.dataContentDescription)) {
            return this.mContext.getString(R$string.mobile_data_text_format, new Object[]{this.mContext.getString(R$string.data_connection_roaming), callbackInfo.dataContentDescription.toString()});
        } else if (callbackInfo.roaming) {
            return this.mContext.getString(R$string.data_connection_roaming);
        } else {
            CharSequence charSequence = callbackInfo.dataContentDescription;
            return charSequence == null ? "" : charSequence;
        }
    }

    public boolean isAvailable() {
        return (!FeatureFlags.isProviderModelSettingEnabled(this.mContext) || MotoFeature.getInstance(this.mContext).isCustomPanelView()) && this.mController.hasMobileDataFeature() && this.mHost.getUserContext().getUserId() == 0;
    }

    /* renamed from: com.android.systemui.qs.tiles.CellularTile$CallbackInfo */
    private static final class CallbackInfo {
        boolean activityIn;
        boolean activityOut;
        boolean airplaneModeEnabled;
        CharSequence dataContentDescription;
        CharSequence dataSimDisplayName;
        CharSequence dataSubscriptionName;
        boolean isSimError;
        boolean multipleSubs;
        boolean roaming;
        SimState simState;

        /* renamed from: com.android.systemui.qs.tiles.CellularTile$CallbackInfo$SimState */
        public enum SimState {
            AVAILABLE,
            NO_SIM,
            DISABLED,
            LOCKED,
            DDS_NOT_DEFINED,
            SIM_ERROR
        }

        private CallbackInfo() {
        }

        /* synthetic */ CallbackInfo(C12491 r1) {
            this();
        }

        public String toString() {
            return "CallbackInfo[" + "airplaneModeEnabled=" + this.airplaneModeEnabled + ",dataContentDescription=" + this.dataContentDescription + ",activityIn=" + this.activityIn + ",activityOut=" + this.activityOut + ",simState=" + this.simState + ",roaming=" + this.roaming + ",isSimError=" + this.isSimError + ']';
        }
    }

    /* access modifiers changed from: private */
    public boolean hasErrorSim() {
        int size = this.mController.getSimStates().size();
        for (int i = 0; i < size; i++) {
            if (this.mController.getSimStates().isSimError(i)) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: com.android.systemui.qs.tiles.CellularTile$CellSignalCallback */
    private final class CellSignalCallback implements NetworkController.SignalCallback {
        final CallbackInfo mInfo;
        List<Integer> mSubs;

        private CellSignalCallback() {
            this.mInfo = new CallbackInfo((C12491) null);
            this.mSubs = new ArrayList();
        }

        /* synthetic */ CellSignalCallback(CellularTile cellularTile, C12491 r2) {
            this();
        }

        public void setMobileDataIndicators(NetworkController.MobileDataIndicators mobileDataIndicators) {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            if (!this.mSubs.contains(Integer.valueOf(defaultDataSubscriptionId))) {
                this.mInfo.simState = CallbackInfo.SimState.DDS_NOT_DEFINED;
            } else if (mobileDataIndicators.subId == defaultDataSubscriptionId && mobileDataIndicators.qsIcon != null) {
                SubscriptionInfo activeSubscriptionInfo = CellularTile.this.mSubscriptionManager.getActiveSubscriptionInfo(defaultDataSubscriptionId);
                if (activeSubscriptionInfo != null) {
                    this.mInfo.dataSimDisplayName = activeSubscriptionInfo.getDisplayName();
                }
                this.mInfo.dataSubscriptionName = CellularTile.this.mController.getMobileDataNetworkName();
                CallbackInfo callbackInfo = this.mInfo;
                callbackInfo.dataContentDescription = mobileDataIndicators.description != null ? mobileDataIndicators.typeContentDescriptionHtml : null;
                ExtendedMobileDataInfo extendedMobileDataInfo = mobileDataIndicators.extendedInfo;
                callbackInfo.activityIn = extendedMobileDataInfo.mQsIn;
                callbackInfo.activityOut = extendedMobileDataInfo.mQsOut;
                callbackInfo.roaming = mobileDataIndicators.roaming;
                callbackInfo.multipleSubs = CellularTile.this.mController.getNumberSubscriptions() > 1;
                this.mInfo.simState = CallbackInfo.SimState.AVAILABLE;
                if (mobileDataIndicators.extendedInfo != null) {
                    if (CellularTile.this.mController.getSimStates().isSimAbsent(mobileDataIndicators.extendedInfo.slotId) || CellularTile.this.mController.getSimStates().isSimPermDisabled(mobileDataIndicators.extendedInfo.slotId)) {
                        this.mInfo.simState = CallbackInfo.SimState.NO_SIM;
                    }
                    this.mInfo.isSimError = CellularTile.this.mController.getSimStates().isSimError(mobileDataIndicators.extendedInfo.slotId);
                    CallbackInfo callbackInfo2 = this.mInfo;
                    if (callbackInfo2.isSimError) {
                        callbackInfo2.simState = CallbackInfo.SimState.SIM_ERROR;
                    }
                    if (CellularTile.this.mMotoExtTM.getCurrentUiccCardProvisioningStatus(mobileDataIndicators.subId) != 1) {
                        this.mInfo.simState = CallbackInfo.SimState.DISABLED;
                    }
                    if (CellularTile.this.mController.getSimStates().isSimLocked(mobileDataIndicators.extendedInfo.slotId)) {
                        this.mInfo.simState = CallbackInfo.SimState.LOCKED;
                    }
                }
                CellularTile.this.refreshState(this.mInfo);
            }
        }

        public void setNoSims(boolean z, boolean z2) {
            if (CellularTile.DEBUG) {
                Log.i("CellularTile", "setNoSims show = " + z + " simDetected = " + z2);
            }
            if (z) {
                this.mInfo.simState = CallbackInfo.SimState.NO_SIM;
                this.mSubs.clear();
                this.mInfo.isSimError = CellularTile.this.hasErrorSim();
                if (CellularTile.DEBUG) {
                    Log.i("CellularTile", "setNoSims isSimError = " + this.mInfo.isSimError);
                }
                CallbackInfo callbackInfo = this.mInfo;
                if (callbackInfo.isSimError) {
                    callbackInfo.simState = CallbackInfo.SimState.SIM_ERROR;
                }
            }
            CellularTile.this.refreshState(this.mInfo);
        }

        public void setIsAirplaneMode(NetworkController.IconState iconState) {
            CallbackInfo callbackInfo = this.mInfo;
            callbackInfo.airplaneModeEnabled = iconState.visible;
            CellularTile.this.refreshState(callbackInfo);
        }

        public void setMobileDataEnabled(boolean z) {
            CellularTile.this.mDetailAdapter.setMobileDataEnabled(z);
        }

        public void setSubs(List<SubscriptionInfo> list) {
            this.mSubs.clear();
            for (int i = 0; i < list.size(); i++) {
                this.mSubs.add(Integer.valueOf(list.get(i).getSubscriptionId()));
            }
        }
    }

    static Intent getCellularSettingIntent() {
        Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
        if (SubscriptionManager.getDefaultDataSubscriptionId() != -1) {
            intent.putExtra("android.provider.extra.SUB_ID", SubscriptionManager.getDefaultDataSubscriptionId());
        }
        return intent;
    }

    private static Intent getMSimSettingsIntent() {
        return new Intent("com.motorola.msimsettings.DDS_SETTINGS");
    }

    /* renamed from: com.android.systemui.qs.tiles.CellularTile$CellularDetailAdapter */
    private final class CellularDetailAdapter implements DetailAdapter {
        public int getMetricsCategory() {
            return R$styleable.AppCompatTheme_windowActionBar;
        }

        private CellularDetailAdapter() {
        }

        /* synthetic */ CellularDetailAdapter(CellularTile cellularTile, C12491 r2) {
            this();
        }

        public CharSequence getTitle() {
            return CellularTile.this.mContext.getString(R$string.quick_settings_cellular_detail_title);
        }

        public Boolean getToggleState() {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            if (CellularTile.this.mDataController.isMobileDataSupported(defaultDataSubscriptionId)) {
                DataUsageController unused = CellularTile.this.mDataController;
                if (DataUsageController.isDisableMobileDataSupported(CellularTile.this.mContext, defaultDataSubscriptionId)) {
                    return Boolean.valueOf(CellularTile.this.mDataController.isMobileDataEnabled(defaultDataSubscriptionId));
                }
            }
            return null;
        }

        public Intent getSettingsIntent() {
            return CellularTile.getCellularSettingIntent();
        }

        public void setToggleState(boolean z) {
            MetricsLogger.action(CellularTile.this.mContext, 155, z);
            if (z || !MotoConfirmationStubActivity.isDialogEnabled(CellularTile.this.mContext)) {
                if (CellularTile.this.mDataController.isMobileDataSupported(SubscriptionManager.getDefaultDataSubscriptionId())) {
                    CellularTile.this.mDataController.setMobileDataEnabled(z);
                    return;
                }
                return;
            }
            MotoConfirmationStubActivity.sendDialogIntent(CellularTile.this.mContext);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            int i = 0;
            if (view == null) {
                view = LayoutInflater.from(CellularTile.this.mContext).inflate(R$layout.data_usage, viewGroup, false);
            }
            DataUsageDetailView dataUsageDetailView = (DataUsageDetailView) view;
            DataUsageController.DataUsageInfo dataUsageInfo = CellularTile.this.mDataController.getDataUsageInfo();
            if (dataUsageInfo == null) {
                return dataUsageDetailView;
            }
            dataUsageDetailView.bind(dataUsageInfo);
            View findViewById = dataUsageDetailView.findViewById(R$id.roaming_text);
            if (!CellularTile.this.mSignalCallback.mInfo.roaming) {
                i = 4;
            }
            findViewById.setVisibility(i);
            return dataUsageDetailView;
        }

        public void setMobileDataEnabled(boolean z) {
            CellularTile.this.fireToggleStateChanged(z);
        }
    }
}
