package com.android.systemui.p006qs.tiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Prefs;
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
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DataSaverController;

/* renamed from: com.android.systemui.qs.tiles.DataSaverTile */
public class DataSaverTile extends QSTileImpl<QSTile.BooleanState> implements DataSaverController.Listener {
    private final DataSaverController mDataSaverController;

    public int getMetricsCategory() {
        return 284;
    }

    public DataSaverTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, DataSaverController dataSaverController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mDataSaverController = dataSaverController;
        dataSaverController.observe(getLifecycle(), this);
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.DATA_SAVER_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        SystemUIDialog systemUIDialog;
        if (!this.mDataSaverController.dataSaverUnavailable()) {
            if (((QSTile.BooleanState) this.mState).value || Prefs.getBoolean(this.mContext, "QsDataSaverDialogShown", false)) {
                toggleDataSaver();
                return;
            }
            if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
                systemUIDialog = new SystemUIDialog(this.mContext);
            } else {
                systemUIDialog = new SystemUIDialog(MotoFeature.getCliContext(this.mContext));
            }
            systemUIDialog.setTitle(17040076);
            systemUIDialog.setMessage(17040074);
            systemUIDialog.setPositiveButton(17040075, new DataSaverTile$$ExternalSyntheticLambda0(this));
            systemUIDialog.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleClick$0(DialogInterface dialogInterface, int i) {
        toggleDataSaver();
        Prefs.putBoolean(this.mContext, "QsDataSaverDialogShown", true);
    }

    private void toggleDataSaver() {
        ((QSTile.BooleanState) this.mState).value = !this.mDataSaverController.isDataSaverEnabled();
        this.mDataSaverController.setDataSaverEnabled(((QSTile.BooleanState) this.mState).value);
        refreshState(Boolean.valueOf(((QSTile.BooleanState) this.mState).value));
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.data_saver);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        boolean z;
        int i;
        if (!handleUpdateStateUnavailable(booleanState)) {
            if (obj instanceof Boolean) {
                z = ((Boolean) obj).booleanValue();
            } else {
                z = this.mDataSaverController.isDataSaverEnabled();
            }
            booleanState.value = z;
            booleanState.state = z ? 2 : 1;
            String string = this.mContext.getString(R$string.data_saver);
            booleanState.label = string;
            booleanState.contentDescription = string;
            if (booleanState.value) {
                i = R$drawable.ic_data_saver;
            } else {
                i = R$drawable.ic_data_saver_off;
            }
            booleanState.icon = QSTileImpl.ResourceIcon.get(i);
            booleanState.expandedAccessibilityClassName = Switch.class.getName();
        }
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_data_saver_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_data_saver_changed_off);
    }

    public void onDataSaverChanged(boolean z) {
        refreshState(Boolean.valueOf(z));
    }

    private boolean handleUpdateStateUnavailable(QSTile.BooleanState booleanState) {
        if (!this.mDataSaverController.dataSaverUnavailable()) {
            return false;
        }
        booleanState.label = this.mContext.getString(R$string.data_saver);
        booleanState.value = false;
        booleanState.icon = QSTileImpl.ResourceIcon.get(R$drawable.ic_data_saver_off);
        booleanState.state = 0;
        booleanState.contentDescription = this.mContext.getString(R$string.accessibility_data_saver_unavailable);
        return true;
    }
}
