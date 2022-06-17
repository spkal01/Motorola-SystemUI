package com.android.systemui.settings.brightness;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;

public class ToggleSeekBar extends SeekBar {
    private String mAccessibilityLabel;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin = null;

    public ToggleSeekBar(Context context) {
        super(context);
    }

    public ToggleSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ToggleSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mEnforcedAdmin;
        if (enforcedAdmin != null) {
            ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(RestrictedLockUtils.getShowAdminSupportDetailsIntent(this.mContext, enforcedAdmin), 0);
            return true;
        }
        if (!isEnabled()) {
            setEnabled(true);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setAccessibilityLabel(String str) {
        this.mAccessibilityLabel = str;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        String str = this.mAccessibilityLabel;
        if (str != null) {
            accessibilityNodeInfo.setText(str);
        }
    }

    public void setEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mEnforcedAdmin = enforcedAdmin;
    }
}
