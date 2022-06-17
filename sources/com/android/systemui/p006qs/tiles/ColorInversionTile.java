package com.android.systemui.p006qs.tiles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.SecureSetting;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.util.settings.SecureSettings;

/* renamed from: com.android.systemui.qs.tiles.ColorInversionTile */
public class ColorInversionTile extends QSTileImpl<QSTile.BooleanState> {
    private final QSTile.Icon mIcon = QSTileImpl.ResourceIcon.get(R$drawable.ic_invert_colors);
    private final SecureSetting mSetting;

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_viewInflaterClass;
    }

    public ColorInversionTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, UserTracker userTracker, SecureSettings secureSettings) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mSetting = new SecureSetting(secureSettings, this.mHandler, "accessibility_display_inversion_enabled", userTracker.getUserId()) {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i, boolean z) {
                ColorInversionTile.this.handleRefreshState(Integer.valueOf(i));
            }
        };
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mSetting.setListening(z);
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
        this.mSetting.setUserId(i);
        handleRefreshState(Integer.valueOf(this.mSetting.getValue()));
    }

    public Intent getLongClickIntent() {
        Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
        Bundle bundle = new Bundle();
        bundle.putString(":settings:fragment_args_key", "toggle_inversion_preference");
        intent.putExtra(":settings:show_fragment_args", bundle);
        return intent;
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        this.mSetting.setValue(((QSTile.BooleanState) this.mState).value ^ true ? 1 : 0);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_inversion_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        int i = 1;
        boolean z = (obj instanceof Integer ? ((Integer) obj).intValue() : this.mSetting.getValue()) != 0;
        if (booleanState.slash == null) {
            booleanState.slash = new QSTile.SlashState();
        }
        booleanState.value = z;
        booleanState.slash.isSlashed = !z;
        if (z) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.label = this.mContext.getString(R$string.quick_settings_inversion_label);
        booleanState.icon = this.mIcon;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_color_inversion_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_color_inversion_changed_off);
    }
}
