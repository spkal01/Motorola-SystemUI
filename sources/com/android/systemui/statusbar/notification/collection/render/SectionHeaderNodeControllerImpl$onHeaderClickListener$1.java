package com.android.systemui.statusbar.notification.collection.render;

import android.content.Intent;
import android.view.View;

/* compiled from: SectionHeaderController.kt */
final class SectionHeaderNodeControllerImpl$onHeaderClickListener$1 implements View.OnClickListener {
    final /* synthetic */ SectionHeaderNodeControllerImpl this$0;

    SectionHeaderNodeControllerImpl$onHeaderClickListener$1(SectionHeaderNodeControllerImpl sectionHeaderNodeControllerImpl) {
        this.this$0 = sectionHeaderNodeControllerImpl;
    }

    public final void onClick(View view) {
        this.this$0.activityStarter.startActivity(new Intent(this.this$0.clickIntentAction), true, true, 536870912);
    }
}
