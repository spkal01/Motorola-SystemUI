package com.android.systemui.controls.p004ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.ControlTemplate;
import com.android.systemui.R$id;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.TouchBehavior */
/* compiled from: TouchBehavior.kt */
public final class TouchBehavior implements Behavior {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public Drawable clipLayer;
    public Control control;
    public ControlViewHolder cvh;
    /* access modifiers changed from: private */
    public int lastColorOffset;
    /* access modifiers changed from: private */
    public boolean statelessTouch;
    public ControlTemplate template;

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
    public final ControlTemplate getTemplate() {
        ControlTemplate controlTemplate = this.template;
        if (controlTemplate != null) {
            return controlTemplate;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }

    public final void setTemplate(@NotNull ControlTemplate controlTemplate) {
        Intrinsics.checkNotNullParameter(controlTemplate, "<set-?>");
        this.template = controlTemplate;
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

    /* access modifiers changed from: private */
    public final boolean getEnabled() {
        return this.lastColorOffset > 0 || this.statelessTouch;
    }

    /* renamed from: com.android.systemui.controls.ui.TouchBehavior$Companion */
    /* compiled from: TouchBehavior.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public void initialize(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        setCvh(controlViewHolder);
        controlViewHolder.getLayout().setOnClickListener(new TouchBehavior$initialize$1(controlViewHolder, this));
    }

    public void bind(@NotNull ControlWithState controlWithState, int i) {
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        Intrinsics.checkNotNull(control2);
        setControl(control2);
        this.lastColorOffset = i;
        ControlViewHolder cvh2 = getCvh();
        CharSequence statusText = getControl().getStatusText();
        Intrinsics.checkNotNullExpressionValue(statusText, "control.getStatusText()");
        int i2 = 0;
        ControlViewHolder.setStatusText$default(cvh2, statusText, false, 2, (Object) null);
        ControlTemplate controlTemplate = getControl().getControlTemplate();
        Intrinsics.checkNotNullExpressionValue(controlTemplate, "control.getControlTemplate()");
        setTemplate(controlTemplate);
        Drawable background = getCvh().getLayout().getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(R$id.clip_layer);
        Intrinsics.checkNotNullExpressionValue(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
        setClipLayer(findDrawableByLayerId);
        Drawable clipLayer2 = getClipLayer();
        if (getEnabled()) {
            i2 = 10000;
        }
        clipLayer2.setLevel(i2);
        ControlViewHolder.m28x1a61c355(getCvh(), getEnabled(), i, false, 4, (Object) null);
    }
}
