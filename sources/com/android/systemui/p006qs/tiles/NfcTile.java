package com.android.systemui.p006qs.tiles;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;
import java.util.Objects;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.tiles.NfcTile */
public class NfcTile extends QSTileImpl<QSTile.BooleanState> {
    private NfcAdapter mAdapter;
    private BroadcastDispatcher mBroadcastDispatcher;
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(R$drawable.zz_moto_ic_qs_nfc);
    private boolean mListening;
    private BroadcastReceiver mNfcReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NfcTile.this.refreshState();
        }
    };

    public int getMetricsCategory() {
        return 800;
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
    }

    public NfcTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, BroadcastDispatcher broadcastDispatcher) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mBroadcastDispatcher = broadcastDispatcher;
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mListening = z;
        if (z) {
            this.mBroadcastDispatcher.registerReceiver(this.mNfcReceiver, new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED"), (Executor) null, UserHandle.CURRENT);
        } else {
            this.mBroadcastDispatcher.unregisterReceiver(this.mNfcReceiver);
        }
    }

    public boolean isAvailable() {
        if (this.mContext.getString(R$string.quick_settings_tiles_stock).contains("nfc")) {
            return this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc");
        }
        return false;
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.NFC_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        try {
            if (hasStateChangeNFCRestriction()) {
                return;
            }
        } catch (NullPointerException e) {
            Log.e("NfcTile", e.getMessage());
        }
        if (getAdapter() != null) {
            if (!getAdapter().isEnabled()) {
                getAdapter().enable();
            } else {
                getAdapter().disable();
            }
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_nfc_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        int i = 1;
        booleanState.value = getAdapter() != null && getAdapter().isEnabled();
        if (getAdapter() == null) {
            i = 0;
        } else if (booleanState.value) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.icon = this.mIcon;
        booleanState.label = this.mContext.getString(R$string.quick_settings_nfc_label);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.quick_settings_nfc_on);
        }
        return this.mContext.getString(R$string.quick_settings_nfc_off);
    }

    private NfcAdapter getAdapter() {
        if (this.mAdapter == null) {
            try {
                this.mAdapter = NfcAdapter.getNfcAdapter(this.mContext);
            } catch (UnsupportedOperationException unused) {
                this.mAdapter = null;
            }
        }
        return this.mAdapter;
    }

    private boolean hasStateChangeNFCRestriction() throws NullPointerException {
        Context context = this.mContext;
        Objects.requireNonNull(context, "No context");
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null || !devicePolicyManager.isDeviceManaged()) {
            return false;
        }
        MotoDevicePolicyManager motoDevicePolicyManager = (MotoDevicePolicyManager) this.mContext.getSystemService("mot_device_policy");
        if (motoDevicePolicyManager != null) {
            return motoDevicePolicyManager.hasUserRestriction("no_state_change_nfc");
        }
        Log.e("NfcTile", "MotoDevicePolicyManager not found.");
        return false;
    }
}
