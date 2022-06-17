package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.preference.SwitchPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import java.util.Set;

public class StatusBarSwitch extends SwitchPreference implements TunerService.Tunable {
    private final String KEY_MUTE = "mute";
    private final String KEY_VOLUME = "volume";
    private Set<String> mHideList;

    public StatusBarSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void onAttached() {
        super.onAttached();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
    }

    public void onDetached() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        super.onDetached();
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            this.mHideList = StatusBarIconController.getIconHideList(getContext(), str2);
            if (MotoFeature.isPrcProduct() && getKey().equals("volume")) {
                if (this.mHideList.contains("volume") && !this.mHideList.contains("mute")) {
                    this.mHideList.add("mute");
                    setList(this.mHideList);
                } else if (!this.mHideList.contains("volume") && this.mHideList.contains("mute")) {
                    this.mHideList.remove("mute");
                    setList(this.mHideList);
                }
            }
            setChecked(!this.mHideList.contains(getKey()));
        }
    }

    /* access modifiers changed from: protected */
    public boolean persistBoolean(boolean z) {
        if (!z) {
            if (this.mHideList.contains(getKey())) {
                return true;
            }
            MetricsLogger.action(getContext(), 234, getKey());
            this.mHideList.add(getKey());
            if (MotoFeature.isPrcProduct() && getKey().equals("volume")) {
                MetricsLogger.action(getContext(), 234, "mute");
                this.mHideList.add("mute");
            }
            setList(this.mHideList);
            return true;
        } else if (!this.mHideList.remove(getKey())) {
            return true;
        } else {
            MetricsLogger.action(getContext(), 233, getKey());
            if (MotoFeature.isPrcProduct() && getKey().equals("volume")) {
                MetricsLogger.action(getContext(), 233, "mute");
                this.mHideList.remove("mute");
            }
            setList(this.mHideList);
            return true;
        }
    }

    private void setList(Set<String> set) {
        Settings.Secure.putStringForUser(getContext().getContentResolver(), "icon_blacklist", TextUtils.join(",", set), ActivityManager.getCurrentUser());
    }
}
