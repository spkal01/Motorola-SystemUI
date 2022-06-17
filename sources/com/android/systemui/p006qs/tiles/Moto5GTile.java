package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.R$bool;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.motorola.settingslib.moto5gmenu.Moto5gMenuUtils;
import com.motorola.settingslib.moto5gmenu.Moto5gNetworkUtils;
import java.util.List;

/* renamed from: com.android.systemui.qs.tiles.Moto5GTile */
public class Moto5GTile extends QSTileImpl<QSTile.BooleanState> {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private boolean m5GAllowed;
    private final Moto5GSignalCallback m5GSignalCallback;
    /* access modifiers changed from: private */
    public boolean mAirplaneMode;
    private CarrierConfigManager mConfigManager;
    private final NetworkController mController;
    private final DataUsageController mDataController;
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(R$drawable.zz_moto_ic_qs_5g);
    /* access modifiers changed from: private */
    public boolean mMobileDataEnabled;
    /* access modifiers changed from: private */
    public int mSubId = Integer.MAX_VALUE;
    protected final SubscriptionManager mSubscriptionManager;
    /* access modifiers changed from: private */
    public TelephonyManager mTelephonyManager;

    public int getMetricsCategory() {
        return 0;
    }

    public Moto5GTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, NetworkController networkController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        Moto5GSignalCallback moto5GSignalCallback = new Moto5GSignalCallback();
        this.m5GSignalCallback = moto5GSignalCallback;
        this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
        this.mConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
        this.mController = networkController;
        this.mDataController = networkController.getMobileDataController();
        networkController.observe(getLifecycle(), moto5GSignalCallback);
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public Intent getLongClickIntent() {
        if (((QSTile.BooleanState) getState()).state == 0) {
            return new Intent("android.settings.WIRELESS_SETTINGS");
        }
        return getCellularSettingIntent();
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (((QSTile.BooleanState) getState()).state != 0) {
            set5GEnable(!Moto5gNetworkUtils.carrierAllowed5g(this.mContext, this.mSubId));
        }
    }

    public CharSequence getTileLabel() {
        return ((QSTile.BooleanState) getState()).label;
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        int i;
        String str;
        String str2;
        int i2 = 1;
        this.mMobileDataEnabled = this.mDataController.isMobileDataSupported(this.mSubId) && this.mDataController.isMobileDataEnabled(this.mSubId);
        Resources resources = this.mContext.getResources();
        if (!this.mMobileDataEnabled || this.mAirplaneMode || (i = this.mSubId) == -1) {
            this.m5GAllowed = false;
            booleanState.state = 0;
            int i3 = R$string.moto_5G_data_unavailable;
            booleanState.secondaryLabel = resources.getString(i3);
            booleanState.contentDescription = resources.getString(i3);
        } else {
            boolean carrierAllowed5g = Moto5gNetworkUtils.carrierAllowed5g(this.mContext, i);
            this.m5GAllowed = carrierAllowed5g;
            if (carrierAllowed5g) {
                i2 = 2;
            }
            booleanState.state = i2;
            if (carrierAllowed5g) {
                str = resources.getString(R$string.moto_5G_data_on);
            } else {
                str = resources.getString(R$string.moto_5G_data_off);
            }
            booleanState.secondaryLabel = str;
            if (this.m5GAllowed) {
                str2 = resources.getString(R$string.accessibility_5g_data_on);
            } else {
                str2 = resources.getString(R$string.accessibility_5g_data_off);
            }
            booleanState.contentDescription = str2;
        }
        booleanState.value = this.m5GAllowed;
        booleanState.icon = this.mIcon;
        booleanState.label = resources.getString(R$string.moto_5G_data);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        Log.d("Moto5GTile", "handleUpdateState: mSubId: " + this.mSubId + " mMobileDataEnabled: " + this.mMobileDataEnabled + " mAirplaneMode:" + this.mAirplaneMode + " m5GAllowed: " + this.m5GAllowed);
    }

    public boolean isAvailable() {
        boolean z = this.mContext.getResources().getBoolean(R$bool.config_show_5g_tile);
        boolean z2 = MotoFeature.isPrcProduct() && z && Moto5gNetworkUtils.modemSupports5g() && !isPrcCarrierProduct();
        Log.d("Moto5GTile", "isAvailable:" + z2 + ", mSubId:" + this.mSubId + ", show5GTile: " + z);
        return z2;
    }

    private void set5GEnable(boolean z) {
        ThreadUtils.postOnBackgroundThread(new Moto5GTile$$ExternalSyntheticLambda0(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$set5GEnable$0(boolean z) {
        Moto5gMenuUtils.set5gEnabled(this.mContext, this.mTelephonyManager, this.mSubId, z);
        refreshState();
    }

    private Intent getCellularSettingIntent() {
        Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
        if (SubscriptionManager.getDefaultDataSubscriptionId() != -1) {
            intent.putExtra("android.provider.extra.SUB_ID", SubscriptionManager.getDefaultDataSubscriptionId());
        }
        return intent;
    }

    /* renamed from: com.android.systemui.qs.tiles.Moto5GTile$Moto5GSignalCallback */
    private final class Moto5GSignalCallback implements NetworkController.SignalCallback {
        private Moto5GSignalCallback() {
        }

        public void setIsAirplaneMode(NetworkController.IconState iconState) {
            String str;
            if (Moto5GTile.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("setIsAirplaneMode: icon = ");
                if (iconState == null) {
                    str = "";
                } else {
                    str = iconState.toString();
                }
                sb.append(str);
                Log.d("Moto5GTile", sb.toString());
            }
            boolean access$200 = Moto5GTile.this.mAirplaneMode;
            boolean z = iconState.visible;
            if (access$200 != z) {
                boolean unused = Moto5GTile.this.mAirplaneMode = z;
                Moto5GTile.this.refreshState();
            }
        }

        public void setMobileDataEnabled(boolean z) {
            if (Moto5GTile.DEBUG) {
                Log.d("Moto5GTile", "setMobileDataEnabled: enabled" + z);
            }
            if (Moto5GTile.this.mMobileDataEnabled != z) {
                Moto5GTile.this.refreshState();
            }
        }

        public void setSubs(List<SubscriptionInfo> list) {
            if (Moto5GTile.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("setSubs: subs");
                sb.append(list == null ? " null" : list.toString());
                Log.d("Moto5GTile", sb.toString());
            }
            int i = Integer.MAX_VALUE;
            if (list != null) {
                i = SubscriptionManager.getDefaultDataSubscriptionId();
            }
            if (Moto5GTile.this.mSubId != i) {
                int unused = Moto5GTile.this.mSubId = i;
                Moto5GTile moto5GTile = Moto5GTile.this;
                TelephonyManager unused2 = moto5GTile.mTelephonyManager = TelephonyManager.from(moto5GTile.mContext).createForSubscriptionId(Moto5GTile.this.mSubId);
                Moto5GTile.this.refreshState();
            }
        }
    }

    private boolean isPrcCarrierProduct() {
        String roCarrier = MotoFeature.getInstance(this.mContext).getRoCarrier();
        Log.d("Moto5GTile", "isPrcCarrierProduct: roCarrier = " + roCarrier);
        return roCarrier.equals("cmcc") || roCarrier.equals("cucn") || roCarrier.equals("ctcn");
    }
}
