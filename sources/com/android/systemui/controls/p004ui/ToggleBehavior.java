package com.android.systemui.controls.p004ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.templates.ToggleTemplate;
import android.util.Log;
import com.android.systemui.R$id;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ToggleBehavior */
/* compiled from: ToggleBehavior.kt */
public final class ToggleBehavior implements Behavior {
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    public ToggleTemplate template;

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
    public final ToggleTemplate getTemplate() {
        ToggleTemplate toggleTemplate = this.template;
        if (toggleTemplate != null) {
            return toggleTemplate;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }

    public final void setTemplate(@NotNull ToggleTemplate toggleTemplate) {
        Intrinsics.checkNotNullParameter(toggleTemplate, "<set-?>");
        this.template = toggleTemplate;
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
        controlViewHolder.getLayout().setOnClickListener(new ToggleBehavior$initialize$1(controlViewHolder, this));
    }

    public void bind(@NotNull ControlWithState controlWithState, int i) {
        ToggleTemplate toggleTemplate;
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        Intrinsics.checkNotNull(control2);
        setControl(control2);
        ControlViewHolder cvh2 = getCvh();
        CharSequence statusText = getControl().getStatusText();
        Intrinsics.checkNotNullExpressionValue(statusText, "control.getStatusText()");
        ControlViewHolder.setStatusText$default(cvh2, statusText, false, 2, (Object) null);
        TemperatureControlTemplate controlTemplate = getControl().getControlTemplate();
        if (controlTemplate instanceof ToggleTemplate) {
            Intrinsics.checkNotNullExpressionValue(controlTemplate, "controlTemplate");
            toggleTemplate = (ToggleTemplate) controlTemplate;
        } else if (controlTemplate instanceof TemperatureControlTemplate) {
            ControlTemplate template2 = controlTemplate.getTemplate();
            Objects.requireNonNull(template2, "null cannot be cast to non-null type android.service.controls.templates.ToggleTemplate");
            toggleTemplate = (ToggleTemplate) template2;
        } else {
            Log.e("ControlsUiController", Intrinsics.stringPlus("Unsupported template type: ", controlTemplate));
            return;
        }
        setTemplate(toggleTemplate);
        Drawable background = getCvh().getLayout().getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(R$id.clip_layer);
        Intrinsics.checkNotNullExpressionValue(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
        setClipLayer(findDrawableByLayerId);
        getClipLayer().setLevel(10000);
        ControlViewHolder.m28x1a61c355(getCvh(), getTemplate().isChecked(), i, false, 4, (Object) null);
    }
}
