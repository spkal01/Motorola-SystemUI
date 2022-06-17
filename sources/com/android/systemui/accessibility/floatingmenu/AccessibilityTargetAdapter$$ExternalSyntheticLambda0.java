package com.android.systemui.accessibility.floatingmenu;

import android.view.View;
import com.android.internal.accessibility.dialog.AccessibilityTarget;

public final /* synthetic */ class AccessibilityTargetAdapter$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ AccessibilityTarget f$0;

    public /* synthetic */ AccessibilityTargetAdapter$$ExternalSyntheticLambda0(AccessibilityTarget accessibilityTarget) {
        this.f$0 = accessibilityTarget;
    }

    public final void onClick(View view) {
        this.f$0.onSelected();
    }
}
