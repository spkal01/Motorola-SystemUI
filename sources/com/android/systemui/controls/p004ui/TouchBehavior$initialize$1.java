package com.android.systemui.controls.p004ui;

import android.service.controls.templates.StatelessTemplate;
import android.view.View;
import com.android.systemui.util.concurrency.DelayableExecutor;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.TouchBehavior$initialize$1 */
/* compiled from: TouchBehavior.kt */
final class TouchBehavior$initialize$1 implements View.OnClickListener {
    final /* synthetic */ ControlViewHolder $cvh;
    final /* synthetic */ TouchBehavior this$0;

    TouchBehavior$initialize$1(ControlViewHolder controlViewHolder, TouchBehavior touchBehavior) {
        this.$cvh = controlViewHolder;
        this.this$0 = touchBehavior;
    }

    public final void onClick(View view) {
        ControlActionCoordinator controlActionCoordinator = this.$cvh.getControlActionCoordinator();
        ControlViewHolder controlViewHolder = this.$cvh;
        String templateId = this.this$0.getTemplate().getTemplateId();
        Intrinsics.checkNotNullExpressionValue(templateId, "template.getTemplateId()");
        controlActionCoordinator.touch(controlViewHolder, templateId, this.this$0.getControl());
        if (this.this$0.getTemplate() instanceof StatelessTemplate) {
            this.this$0.statelessTouch = true;
            ControlViewHolder.m28x1a61c355(this.$cvh, this.this$0.getEnabled(), this.this$0.lastColorOffset, false, 4, (Object) null);
            DelayableExecutor uiExecutor = this.$cvh.getUiExecutor();
            final TouchBehavior touchBehavior = this.this$0;
            final ControlViewHolder controlViewHolder2 = this.$cvh;
            uiExecutor.executeDelayed(new Runnable() {
                public final void run() {
                    touchBehavior.statelessTouch = false;
                    ControlViewHolder.m28x1a61c355(controlViewHolder2, touchBehavior.getEnabled(), touchBehavior.lastColorOffset, false, 4, (Object) null);
                }
            }, 3000);
        }
    }
}
