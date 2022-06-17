package com.android.systemui.controls.p004ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.TemperatureControlTemplate;
import com.android.systemui.R$id;
import com.android.systemui.controls.p004ui.ControlViewHolder;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.TemperatureControlBehavior */
/* compiled from: TemperatureControlBehavior.kt */
public final class TemperatureControlBehavior implements Behavior {
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    @Nullable
    private Behavior subBehavior;

    @NotNull
    public final Drawable getClipLayer() {
        Drawable drawable = this.clipLayer;
        if (drawable != null) {
            return drawable;
        }
        Intrinsics.throwUninitializedPropertyAccessException("clipLayer");
        throw null;
    }

    public final void setClipLayer(@NotNull Drawable drawable) {
        Intrinsics.checkNotNullParameter(drawable, "<set-?>");
        this.clipLayer = drawable;
    }

    @NotNull
    public final Control getControl() {
        Control control2 = this.control;
        if (control2 != null) {
            return control2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("control");
        throw null;
    }

    public final void setControl(@NotNull Control control2) {
        Intrinsics.checkNotNullParameter(control2, "<set-?>");
        this.control = control2;
    }

    @NotNull
    public final ControlViewHolder getCvh() {
        ControlViewHolder controlViewHolder = this.cvh;
        if (controlViewHolder != null) {
            return controlViewHolder;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cvh");
        throw null;
    }

    public final void setCvh(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "<set-?>");
        this.cvh = controlViewHolder;
    }

    public void initialize(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        setCvh(controlViewHolder);
    }

    public void bind(@NotNull ControlWithState controlWithState, int i) {
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        Intrinsics.checkNotNull(control2);
        setControl(control2);
        ControlViewHolder cvh2 = getCvh();
        CharSequence statusText = getControl().getStatusText();
        Intrinsics.checkNotNullExpressionValue(statusText, "control.getStatusText()");
        int i2 = 0;
        ControlViewHolder.setStatusText$default(cvh2, statusText, false, 2, (Object) null);
        Drawable background = getCvh().getLayout().getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(R$id.clip_layer);
        Intrinsics.checkNotNullExpressionValue(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
        setClipLayer(findDrawableByLayerId);
        TemperatureControlTemplate controlTemplate = getControl().getControlTemplate();
        Objects.requireNonNull(controlTemplate, "null cannot be cast to non-null type android.service.controls.templates.TemperatureControlTemplate");
        TemperatureControlTemplate temperatureControlTemplate = controlTemplate;
        int currentActiveMode = temperatureControlTemplate.getCurrentActiveMode();
        ControlTemplate template = temperatureControlTemplate.getTemplate();
        if (Intrinsics.areEqual((Object) template, (Object) ControlTemplate.getNoTemplateObject()) || Intrinsics.areEqual((Object) template, (Object) ControlTemplate.getErrorTemplate())) {
            boolean z = (currentActiveMode == 0 || currentActiveMode == 1) ? false : true;
            Drawable clipLayer2 = getClipLayer();
            if (z) {
                i2 = 10000;
            }
            clipLayer2.setLevel(i2);
            ControlViewHolder.m28x1a61c355(getCvh(), z, currentActiveMode, false, 4, (Object) null);
            getCvh().getLayout().setOnClickListener(new TemperatureControlBehavior$bind$1(this, temperatureControlTemplate));
            return;
        }
        ControlViewHolder cvh3 = getCvh();
        Behavior behavior = this.subBehavior;
        ControlViewHolder.Companion companion = ControlViewHolder.Companion;
        int status = getControl().getStatus();
        Intrinsics.checkNotNullExpressionValue(template, "subTemplate");
        this.subBehavior = cvh3.bindBehavior(behavior, companion.findBehaviorClass(status, template, getControl().getDeviceType()), currentActiveMode);
    }
}
