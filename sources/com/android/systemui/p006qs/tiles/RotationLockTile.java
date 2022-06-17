package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import androidx.lifecycle.LifecycleOwner;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.RotationLockController;

/* renamed from: com.android.systemui.qs.tiles.RotationLockTile */
public class RotationLockTile extends QSTileImpl<QSTile.BooleanState> {
    private final RotationLockController.RotationLockControllerCallback mCallback;
    private final RotationLockController mController;
    private DeviceProvisionedController mDeviceProvisionedController;
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(17303694);

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowFixedWidthMinor;
    }

    public RotationLockTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, RotationLockController rotationLockController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        C12621 r1 = new RotationLockController.RotationLockControllerCallback() {
            public void onRotationLockStateChanged(boolean z, boolean z2) {
                RotationLockTile.this.refreshState(Boolean.valueOf(z));
            }
        };
        this.mCallback = r1;
        this.mController = rotationLockController;
        rotationLockController.observe((LifecycleOwner) this, r1);
        this.mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.AUTO_ROTATE_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (isProvisioned()) {
            boolean z = !((QSTile.BooleanState) this.mState).value;
            this.mController.setRotationLocked(!z);
            refreshState(Boolean.valueOf(z));
        }
    }

    private boolean isProvisioned() {
        return this.mDeviceProvisionedController.isDeviceProvisioned() && this.mDeviceProvisionedController.isCurrentUserSetup();
    }

    public CharSequence getTileLabel() {
        return ((QSTile.BooleanState) getState()).label;
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        boolean isRotationLocked = this.mController.isRotationLocked();
        booleanState.value = !isRotationLocked;
        booleanState.label = this.mContext.getString(R$string.quick_settings_rotation_unlocked_label);
        booleanState.icon = this.mIcon;
        booleanState.contentDescription = getAccessibilityString(isRotationLocked);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.state = booleanState.value ? 2 : 1;
    }

    public static boolean isCurrentOrientationLockPortrait(RotationLockController rotationLockController, Resources resources) {
        int rotationLockOrientation = rotationLockController.getRotationLockOrientation();
        if (rotationLockOrientation == 0) {
            if (resources.getConfiguration().orientation != 2) {
                return true;
            }
            return false;
        } else if (rotationLockOrientation != 2) {
            return true;
        } else {
            return false;
        }
    }

    private String getAccessibilityString(boolean z) {
        return this.mContext.getString(R$string.accessibility_quick_settings_rotation);
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        return getAccessibilityString(((QSTile.BooleanState) this.mState).value);
    }
}
