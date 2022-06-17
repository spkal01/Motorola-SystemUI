package com.android.systemui.controls.p004ui;

import android.service.controls.templates.TemperatureControlTemplate;
import android.view.View;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.TemperatureControlBehavior$bind$1 */
/* compiled from: TemperatureControlBehavior.kt */
final class TemperatureControlBehavior$bind$1 implements View.OnClickListener {
    final /* synthetic */ TemperatureControlTemplate $template;
    final /* synthetic */ TemperatureControlBehavior this$0;

    TemperatureControlBehavior$bind$1(TemperatureControlBehavior temperatureControlBehavior, TemperatureControlTemplate temperatureControlTemplate) {
        this.this$0 = temperatureControlBehavior;
        this.$template = temperatureControlTemplate;
    }

    public final void onClick(View view) {
        ControlActionCoordinator controlActionCoordinator = this.this$0.getCvh().getControlActionCoordinator();
        ControlViewHolder cvh = this.this$0.getCvh();
        String templateId = this.$template.getTemplateId();
        Intrinsics.checkNotNullExpressionValue(templateId, "template.getTemplateId()");
        controlActionCoordinator.touch(cvh, templateId, this.this$0.getControl());
    }
}
