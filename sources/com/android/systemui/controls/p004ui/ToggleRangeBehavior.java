package com.android.systemui.controls.p004ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.util.Log;
import android.util.MathUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.animation.Interpolators;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Objects;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior */
/* compiled from: ToggleRangeBehavior.kt */
public final class ToggleRangeBehavior implements Behavior {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public Drawable clipLayer;
    private int colorOffset;
    public Context context;
    public Control control;
    @NotNull
    private String currentRangeValue = "";
    @NotNull
    private CharSequence currentStatusText = "";
    public ControlViewHolder cvh;
    private boolean isChecked;
    private boolean isToggleable;
    /* access modifiers changed from: private */
    @Nullable
    public ValueAnimator rangeAnimator;
    public RangeTemplate rangeTemplate;
    public String templateId;

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
    public final String getTemplateId() {
        String str = this.templateId;
        if (str != null) {
            return str;
        }
        Intrinsics.throwUninitializedPropertyAccessException("templateId");
        throw null;
    }

    public final void setTemplateId(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.templateId = str;
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

    @NotNull
    public final RangeTemplate getRangeTemplate() {
        RangeTemplate rangeTemplate2 = this.rangeTemplate;
        if (rangeTemplate2 != null) {
            return rangeTemplate2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("rangeTemplate");
        throw null;
    }

    public final void setRangeTemplate(@NotNull RangeTemplate rangeTemplate2) {
        Intrinsics.checkNotNullParameter(rangeTemplate2, "<set-?>");
        this.rangeTemplate = rangeTemplate2;
    }

    @NotNull
    public final Context getContext() {
        Context context2 = this.context;
        if (context2 != null) {
            return context2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("context");
        throw null;
    }

    public final void setContext(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "<set-?>");
        this.context = context2;
    }

    public final boolean isChecked() {
        return this.isChecked;
    }

    public final boolean isToggleable() {
        return this.isToggleable;
    }

    /* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$Companion */
    /* compiled from: ToggleRangeBehavior.kt */
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
        setContext(controlViewHolder.getContext());
        ToggleRangeGestureListener toggleRangeGestureListener = new ToggleRangeGestureListener(this, controlViewHolder.getLayout());
        controlViewHolder.getLayout().setOnTouchListener(new ToggleRangeBehavior$initialize$1(new GestureDetector(getContext(), toggleRangeGestureListener), toggleRangeGestureListener, this));
    }

    private final void setup(ToggleRangeTemplate toggleRangeTemplate) {
        RangeTemplate range = toggleRangeTemplate.getRange();
        Intrinsics.checkNotNullExpressionValue(range, "template.getRange()");
        setRangeTemplate(range);
        this.isToggleable = true;
        this.isChecked = toggleRangeTemplate.isChecked();
    }

    private final void setup(RangeTemplate rangeTemplate2) {
        setRangeTemplate(rangeTemplate2);
        this.isChecked = !(getRangeTemplate().getCurrentValue() == getRangeTemplate().getMinValue());
    }

    private final boolean setupTemplate(ControlTemplate controlTemplate) {
        if (controlTemplate instanceof ToggleRangeTemplate) {
            setup((ToggleRangeTemplate) controlTemplate);
            return true;
        } else if (controlTemplate instanceof RangeTemplate) {
            setup((RangeTemplate) controlTemplate);
            return true;
        } else if (controlTemplate instanceof TemperatureControlTemplate) {
            ControlTemplate template = ((TemperatureControlTemplate) controlTemplate).getTemplate();
            Intrinsics.checkNotNullExpressionValue(template, "template.getTemplate()");
            return setupTemplate(template);
        } else {
            Log.e("ControlsUiController", Intrinsics.stringPlus("Unsupported template type: ", controlTemplate));
            return false;
        }
    }

    public void bind(@NotNull ControlWithState controlWithState, int i) {
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        Control control2 = controlWithState.getControl();
        Intrinsics.checkNotNull(control2);
        setControl(control2);
        this.colorOffset = i;
        CharSequence statusText = getControl().getStatusText();
        Intrinsics.checkNotNullExpressionValue(statusText, "control.getStatusText()");
        this.currentStatusText = statusText;
        getCvh().getLayout().setOnLongClickListener((View.OnLongClickListener) null);
        Drawable background = getCvh().getLayout().getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        Drawable findDrawableByLayerId = ((LayerDrawable) background).findDrawableByLayerId(R$id.clip_layer);
        Intrinsics.checkNotNullExpressionValue(findDrawableByLayerId, "ld.findDrawableByLayerId(R.id.clip_layer)");
        setClipLayer(findDrawableByLayerId);
        ControlTemplate controlTemplate = getControl().getControlTemplate();
        Intrinsics.checkNotNullExpressionValue(controlTemplate, "template");
        if (setupTemplate(controlTemplate)) {
            String templateId2 = controlTemplate.getTemplateId();
            Intrinsics.checkNotNullExpressionValue(templateId2, "template.getTemplateId()");
            setTemplateId(templateId2);
            updateRange(rangeToLevelValue(getRangeTemplate().getCurrentValue()), this.isChecked, false);
            ControlViewHolder.m28x1a61c355(getCvh(), this.isChecked, i, false, 4, (Object) null);
            getCvh().getLayout().setAccessibilityDelegate(new ToggleRangeBehavior$bind$1(this));
        }
    }

    public final void beginUpdateRange() {
        getCvh().setUserInteractionInProgress(true);
        getCvh().setStatusTextSize((float) getContext().getResources().getDimensionPixelSize(R$dimen.control_status_expanded));
    }

    public final void updateRange(int i, boolean z, boolean z2) {
        int max = Math.max(0, Math.min(10000, i));
        if (getClipLayer().getLevel() == 0 && max > 0) {
            getCvh().mo12775x3918d5b8(z, this.colorOffset, false);
        }
        ValueAnimator valueAnimator = this.rangeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z2) {
            boolean z3 = max == 0 || max == 10000;
            if (getClipLayer().getLevel() != max) {
                getCvh().getControlActionCoordinator().drag(z3);
                getClipLayer().setLevel(max);
            }
        } else if (max != getClipLayer().getLevel()) {
            ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{getCvh().getClipLayer().getLevel(), max});
            ofInt.addUpdateListener(new ToggleRangeBehavior$updateRange$1$1(this));
            ofInt.addListener(new ToggleRangeBehavior$updateRange$1$2(this));
            ofInt.setDuration(700);
            ofInt.setInterpolator(Interpolators.CONTROL_STATE);
            ofInt.start();
            Unit unit = Unit.INSTANCE;
            this.rangeAnimator = ofInt;
        }
        if (z) {
            this.currentRangeValue = format(getRangeTemplate().getFormatString().toString(), "%.1f", levelToRangeValue(max));
            if (z2) {
                getCvh().setStatusText(this.currentRangeValue, true);
                return;
            }
            ControlViewHolder cvh2 = getCvh();
            ControlViewHolder.setStatusText$default(cvh2, this.currentStatusText + ' ' + this.currentRangeValue, false, 2, (Object) null);
            return;
        }
        ControlViewHolder.setStatusText$default(getCvh(), this.currentStatusText, false, 2, (Object) null);
    }

    private final String format(String str, String str2, float f) {
        try {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String format = String.format(str, Arrays.copyOf(new Object[]{Float.valueOf(findNearestStep(f))}, 1));
            Intrinsics.checkNotNullExpressionValue(format, "java.lang.String.format(format, *args)");
            return format;
        } catch (IllegalFormatException e) {
            Log.w("ControlsUiController", "Illegal format in range template", e);
            if (Intrinsics.areEqual((Object) str2, (Object) "")) {
                return "";
            }
            return format(str2, "", f);
        }
    }

    /* access modifiers changed from: private */
    public final float levelToRangeValue(int i) {
        return MathUtils.constrainedMap(getRangeTemplate().getMinValue(), getRangeTemplate().getMaxValue(), 0.0f, 10000.0f, (float) i);
    }

    /* access modifiers changed from: private */
    public final int rangeToLevelValue(float f) {
        return (int) MathUtils.constrainedMap(0.0f, 10000.0f, getRangeTemplate().getMinValue(), getRangeTemplate().getMaxValue(), f);
    }

    public final void endUpdateRange() {
        getCvh().setStatusTextSize((float) getContext().getResources().getDimensionPixelSize(R$dimen.control_status_normal));
        ControlViewHolder cvh2 = getCvh();
        cvh2.setStatusText(this.currentStatusText + ' ' + this.currentRangeValue, true);
        ControlActionCoordinator controlActionCoordinator = getCvh().getControlActionCoordinator();
        ControlViewHolder cvh3 = getCvh();
        String templateId2 = getRangeTemplate().getTemplateId();
        Intrinsics.checkNotNullExpressionValue(templateId2, "rangeTemplate.getTemplateId()");
        controlActionCoordinator.setValue(cvh3, templateId2, findNearestStep(levelToRangeValue(getClipLayer().getLevel())));
        getCvh().setUserInteractionInProgress(false);
    }

    public final float findNearestStep(float f) {
        float minValue = getRangeTemplate().getMinValue();
        float f2 = Float.MAX_VALUE;
        while (minValue <= getRangeTemplate().getMaxValue()) {
            float abs = Math.abs(f - minValue);
            if (abs >= f2) {
                return minValue - getRangeTemplate().getStepValue();
            }
            minValue += getRangeTemplate().getStepValue();
            f2 = abs;
        }
        return getRangeTemplate().getMaxValue();
    }

    /* renamed from: com.android.systemui.controls.ui.ToggleRangeBehavior$ToggleRangeGestureListener */
    /* compiled from: ToggleRangeBehavior.kt */
    public final class ToggleRangeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean isDragging;
        final /* synthetic */ ToggleRangeBehavior this$0;
        @NotNull

        /* renamed from: v */
        private final View f87v;

        public boolean onDown(@NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(motionEvent, "e");
            return true;
        }

        public ToggleRangeGestureListener(@NotNull ToggleRangeBehavior toggleRangeBehavior, View view) {
            Intrinsics.checkNotNullParameter(toggleRangeBehavior, "this$0");
            Intrinsics.checkNotNullParameter(view, "v");
            this.this$0 = toggleRangeBehavior;
            this.f87v = view;
        }

        public final boolean isDragging() {
            return this.isDragging;
        }

        public final void setDragging(boolean z) {
            this.isDragging = z;
        }

        public void onLongPress(@NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(motionEvent, "e");
            if (!this.isDragging) {
                this.this$0.getCvh().getControlActionCoordinator().longPress(this.this$0.getCvh());
            }
        }

        public boolean onScroll(@NotNull MotionEvent motionEvent, @NotNull MotionEvent motionEvent2, float f, float f2) {
            Intrinsics.checkNotNullParameter(motionEvent, "e1");
            Intrinsics.checkNotNullParameter(motionEvent2, "e2");
            if (!this.isDragging) {
                this.f87v.getParent().requestDisallowInterceptTouchEvent(true);
                this.this$0.beginUpdateRange();
                this.isDragging = true;
            }
            ToggleRangeBehavior toggleRangeBehavior = this.this$0;
            toggleRangeBehavior.updateRange(toggleRangeBehavior.getClipLayer().getLevel() + ((int) (((float) 10000) * ((-f) / ((float) this.f87v.getWidth())))), true, true);
            return true;
        }

        public boolean onSingleTapUp(@NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(motionEvent, "e");
            if (!this.this$0.isToggleable()) {
                return false;
            }
            this.this$0.getCvh().getControlActionCoordinator().toggle(this.this$0.getCvh(), this.this$0.getTemplateId(), this.this$0.isChecked());
            return true;
        }
    }
}
