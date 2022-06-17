package com.android.systemui.accessibility.floatingmenu;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.View;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.systemui.R$string;
import com.android.systemui.accessibility.floatingmenu.AnnotationLinkSpan;

class MigrationTooltipView extends BaseTooltipView {
    MigrationTooltipView(Context context, AccessibilityFloatingMenuView accessibilityFloatingMenuView) {
        super(context, accessibilityFloatingMenuView);
        Intent intent = new Intent("android.settings.ACCESSIBILITY_DETAILS_SETTINGS");
        intent.addFlags(268435456);
        intent.putExtra("android.intent.extra.COMPONENT_NAME", AccessibilityShortcutController.ACCESSIBILITY_BUTTON_COMPONENT_NAME.flattenToShortString());
        AnnotationLinkSpan.LinkInfo linkInfo = new AnnotationLinkSpan.LinkInfo("link", new MigrationTooltipView$$ExternalSyntheticLambda0(this, intent));
        setDescription(AnnotationLinkSpan.linkify(getContext().getText(R$string.accessibility_floating_button_migration_tooltip), linkInfo));
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Intent intent, View view) {
        getContext().startActivity(intent);
        hide();
    }
}
