package com.android.systemui.p006qs.tiles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.FlashlightController;

/* renamed from: com.android.systemui.qs.tiles.FlashlightTile */
public class FlashlightTile extends QSTileImpl<QSTile.BooleanState> implements FlashlightController.FlashlightListener {
    private final FlashlightController mFlashlightController;
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(17303698);

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowActionModeOverlay;
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
    }

    public FlashlightTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, FlashlightController flashlightController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mFlashlightController = flashlightController;
        flashlightController.observe(getLifecycle(), this);
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
    }

    public QSTile.BooleanState newTileState() {
        QSTile.BooleanState booleanState = new QSTile.BooleanState();
        booleanState.handlesLongClick = false;
        return booleanState;
    }

    public Intent getLongClickIntent() {
        return new Intent("android.media.action.STILL_IMAGE_CAMERA");
    }

    public boolean isAvailable() {
        return this.mFlashlightController.hasFlashlight();
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (!ActivityManager.isUserAMonkey()) {
            boolean z = !((QSTile.BooleanState) this.mState).value;
            refreshState(Boolean.valueOf(z));
            this.mFlashlightController.setFlashlight(z);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_flashlight_label);
    }

    /* access modifiers changed from: protected */
    public void handleLongClick(View view) {
        handleClick(view);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        Context context = this.mHost.getContext();
        int i = R$string.quick_settings_flashlight_label;
        booleanState.label = context.getString(i);
        booleanState.secondaryLabel = "";
        booleanState.stateDescription = "";
        int i2 = 1;
        if (!this.mFlashlightController.isAvailable()) {
            booleanState.icon = this.mIcon;
            booleanState.slash.isSlashed = true;
            String string = this.mContext.getString(R$string.quick_settings_flashlight_camera_in_use);
            booleanState.secondaryLabel = string;
            booleanState.stateDescription = string;
            booleanState.state = 0;
            return;
        }
        if (obj instanceof Boolean) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (booleanValue != booleanState.value || booleanState.icon == null) {
                booleanState.value = booleanValue;
            } else {
                return;
            }
        } else {
            booleanState.value = this.mFlashlightController.isEnabled();
        }
        booleanState.icon = this.mIcon;
        booleanState.slash.isSlashed = !booleanState.value;
        booleanState.contentDescription = this.mContext.getString(i);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (booleanState.value) {
            i2 = 2;
        }
        booleanState.state = i2;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_flashlight_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_flashlight_changed_off);
    }

    public void onFlashlightChanged(boolean z) {
        refreshState(Boolean.valueOf(z));
    }

    public void onFlashlightError() {
        refreshState(Boolean.FALSE);
    }

    public void onFlashlightAvailabilityChanged(boolean z) {
        refreshState();
    }
}
