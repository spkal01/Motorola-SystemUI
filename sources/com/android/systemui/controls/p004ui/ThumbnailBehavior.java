package com.android.systemui.controls.p004ui;

import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.ThumbnailTemplate;
import android.util.TypedValue;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.controls.ui.ThumbnailBehavior */
/* compiled from: ThumbnailBehavior.kt */
public final class ThumbnailBehavior implements Behavior {
    public Control control;
    public ControlViewHolder cvh;
    private int shadowColor;
    private float shadowOffsetX;
    private float shadowOffsetY;
    private float shadowRadius;
    public ThumbnailTemplate template;

    @NotNull
    public final ThumbnailTemplate getTemplate() {
        ThumbnailTemplate thumbnailTemplate = this.template;
        if (thumbnailTemplate != null) {
            return thumbnailTemplate;
        }
        Intrinsics.throwUninitializedPropertyAccessException("template");
        throw null;
    }

    public final void setTemplate(@NotNull ThumbnailTemplate thumbnailTemplate) {
        Intrinsics.checkNotNullParameter(thumbnailTemplate, "<set-?>");
        this.template = thumbnailTemplate;
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
        return getTemplate().isActive();
    }

    public void initialize(@NotNull ControlViewHolder controlViewHolder) {
        Intrinsics.checkNotNullParameter(controlViewHolder, "cvh");
        setCvh(controlViewHolder);
        TypedValue typedValue = new TypedValue();
        controlViewHolder.getContext().getResources().getValue(R$dimen.controls_thumbnail_shadow_x, typedValue, true);
        this.shadowOffsetX = typedValue.getFloat();
        controlViewHolder.getContext().getResources().getValue(R$dimen.controls_thumbnail_shadow_y, typedValue, true);
        this.shadowOffsetY = typedValue.getFloat();
        controlViewHolder.getContext().getResources().getValue(R$dimen.controls_thumbnail_shadow_radius, typedValue, true);
        this.shadowRadius = typedValue.getFloat();
        this.shadowColor = controlViewHolder.getContext().getResources().getColor(R$color.control_thumbnail_shadow_color);
        controlViewHolder.getLayout().setOnClickListener(new ThumbnailBehavior$initialize$1(controlViewHolder, this));
    }

    public void bind(@NotNull ControlWithState controlWithState, int i) {
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        Intrinsics.checkNotNull(control2);
        setControl(control2);
        ControlViewHolder cvh2 = getCvh();
        CharSequence statusText = getControl().getStatusText();
        Intrinsics.checkNotNullExpressionValue(statusText, "control.getStatusText()");
        ControlViewHolder.setStatusText$default(cvh2, statusText, false, 2, (Object) null);
        ThumbnailTemplate controlTemplate = getControl().getControlTemplate();
        Objects.requireNonNull(controlTemplate, "null cannot be cast to non-null type android.service.controls.templates.ThumbnailTemplate");
        setTemplate(controlTemplate);
        Drawable background = getCvh().getLayout().getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(R$id.clip_layer);
        Objects.requireNonNull(findDrawableByLayerId, "null cannot be cast to non-null type android.graphics.drawable.ClipDrawable");
        ClipDrawable clipDrawable = (ClipDrawable) findDrawableByLayerId;
        clipDrawable.setLevel(getEnabled() ? 10000 : 0);
        if (getTemplate().isActive()) {
            getCvh().getTitle().setVisibility(4);
            getCvh().getSubtitle().setVisibility(4);
            getCvh().getStatus().setShadowLayer(this.shadowOffsetX, this.shadowOffsetY, this.shadowRadius, this.shadowColor);
            getCvh().getBgExecutor().execute(new ThumbnailBehavior$bind$1(this, clipDrawable, i));
        } else {
            getCvh().getTitle().setVisibility(0);
            getCvh().getSubtitle().setVisibility(0);
            getCvh().getStatus().setShadowLayer(0.0f, 0.0f, 0.0f, this.shadowColor);
        }
        ControlViewHolder.m28x1a61c355(getCvh(), getEnabled(), i, false, 4, (Object) null);
    }
}
