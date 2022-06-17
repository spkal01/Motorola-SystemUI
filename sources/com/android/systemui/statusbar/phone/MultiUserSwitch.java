package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import com.android.systemui.R$string;

public class MultiUserSwitch extends FrameLayout {
    public boolean hasOverlappingRendering() {
        return false;
    }

    public MultiUserSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public void refreshContentDescription(String str) {
        String str2;
        if (!TextUtils.isEmpty(str)) {
            str2 = this.mContext.getString(R$string.accessibility_quick_settings_user, new Object[]{str});
        } else {
            str2 = null;
        }
        if (!TextUtils.equals(getContentDescription(), str2)) {
            setContentDescription(str2);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Button.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }
}
