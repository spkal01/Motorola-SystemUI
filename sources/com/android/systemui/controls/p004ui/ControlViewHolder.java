package com.android.systemui.controls.p004ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.service.controls.Control;
import android.service.controls.actions.ControlAction;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.StatelessTemplate;
import android.service.controls.templates.TemperatureControlTemplate;
import android.service.controls.templates.ThumbnailTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.service.controls.templates.ToggleTemplate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.graphics.ColorUtils;
import com.android.systemui.R$color;
import com.android.systemui.R$fraction;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.controls.ControlsMetricsLogger;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder */
/* compiled from: ControlViewHolder.kt */
public final class ControlViewHolder {
    @NotNull
    private static final int[] ATTR_DISABLED = {-16842910};
    @NotNull
    private static final int[] ATTR_ENABLED = {16842910};
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private static final Set<Integer> FORCE_PANEL_DEVICES = SetsKt__SetsKt.setOf(49, 50);
    @NotNull
    private final GradientDrawable baseLayer;
    @Nullable
    private Behavior behavior;
    @NotNull
    private final DelayableExecutor bgExecutor;
    @NotNull
    private final ClipDrawable clipLayer;
    @NotNull
    private final Context context;
    @NotNull
    private final ControlActionCoordinator controlActionCoordinator;
    @NotNull
    private final ControlsController controlsController;
    @NotNull
    private final ControlsMetricsLogger controlsMetricsLogger;
    public ControlWithState cws;
    @NotNull
    private final ImageView icon;
    private boolean isLoading;
    @Nullable
    private ControlAction lastAction;
    /* access modifiers changed from: private */
    @Nullable
    public Dialog lastChallengeDialog;
    @NotNull
    private final ViewGroup layout;
    @NotNull
    private CharSequence nextStatusText = "";
    @NotNull
    private final Function0<Unit> onDialogCancel;
    /* access modifiers changed from: private */
    @Nullable
    public ValueAnimator stateAnimator;
    @NotNull
    private final TextView status;
    /* access modifiers changed from: private */
    @Nullable
    public Animator statusAnimator;
    @NotNull
    private final TextView subtitle;
    @NotNull
    private final TextView title;
    private final float toggleBackgroundIntensity;
    @NotNull
    private final DelayableExecutor uiExecutor;
    private final int uid;
    private boolean userInteractionInProgress;
    @Nullable
    private Dialog visibleDialog;

    public ControlViewHolder(@NotNull ViewGroup viewGroup, @NotNull ControlsController controlsController2, @NotNull DelayableExecutor delayableExecutor, @NotNull DelayableExecutor delayableExecutor2, @NotNull ControlActionCoordinator controlActionCoordinator2, @NotNull ControlsMetricsLogger controlsMetricsLogger2, int i) {
        Intrinsics.checkNotNullParameter(viewGroup, "layout");
        Intrinsics.checkNotNullParameter(controlsController2, "controlsController");
        Intrinsics.checkNotNullParameter(delayableExecutor, "uiExecutor");
        Intrinsics.checkNotNullParameter(delayableExecutor2, "bgExecutor");
        Intrinsics.checkNotNullParameter(controlActionCoordinator2, "controlActionCoordinator");
        Intrinsics.checkNotNullParameter(controlsMetricsLogger2, "controlsMetricsLogger");
        this.layout = viewGroup;
        this.controlsController = controlsController2;
        this.uiExecutor = delayableExecutor;
        this.bgExecutor = delayableExecutor2;
        this.controlActionCoordinator = controlActionCoordinator2;
        this.controlsMetricsLogger = controlsMetricsLogger2;
        this.uid = i;
        this.toggleBackgroundIntensity = viewGroup.getContext().getResources().getFraction(R$fraction.controls_toggle_bg_intensity, 1, 1);
        View requireViewById = viewGroup.requireViewById(R$id.icon);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "layout.requireViewById(R.id.icon)");
        this.icon = (ImageView) requireViewById;
        View requireViewById2 = viewGroup.requireViewById(R$id.status);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "layout.requireViewById(R.id.status)");
        TextView textView = (TextView) requireViewById2;
        this.status = textView;
        View requireViewById3 = viewGroup.requireViewById(R$id.title);
        Intrinsics.checkNotNullExpressionValue(requireViewById3, "layout.requireViewById(R.id.title)");
        this.title = (TextView) requireViewById3;
        View requireViewById4 = viewGroup.requireViewById(R$id.subtitle);
        Intrinsics.checkNotNullExpressionValue(requireViewById4, "layout.requireViewById(R.id.subtitle)");
        this.subtitle = (TextView) requireViewById4;
        Context context2 = viewGroup.getContext();
        Intrinsics.checkNotNullExpressionValue(context2, "layout.getContext()");
        this.context = context2;
        this.onDialogCancel = new ControlViewHolder$onDialogCancel$1(this);
        Drawable background = viewGroup.getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
        LayerDrawable layerDrawable = (LayerDrawable) background;
        layerDrawable.mutate();
        Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(R$id.clip_layer);
        Objects.requireNonNull(findDrawableByLayerId, "null cannot be cast to non-null type android.graphics.drawable.ClipDrawable");
        this.clipLayer = (ClipDrawable) findDrawableByLayerId;
        Drawable findDrawableByLayerId2 = layerDrawable.findDrawableByLayerId(R$id.background);
        Objects.requireNonNull(findDrawableByLayerId2, "null cannot be cast to non-null type android.graphics.drawable.GradientDrawable");
        this.baseLayer = (GradientDrawable) findDrawableByLayerId2;
        textView.setSelected(true);
    }

    @NotNull
    public final ViewGroup getLayout() {
        return this.layout;
    }

    @NotNull
    public final DelayableExecutor getUiExecutor() {
        return this.uiExecutor;
    }

    @NotNull
    public final DelayableExecutor getBgExecutor() {
        return this.bgExecutor;
    }

    @NotNull
    public final ControlActionCoordinator getControlActionCoordinator() {
        return this.controlActionCoordinator;
    }

    public final int getUid() {
        return this.uid;
    }

    /* renamed from: com.android.systemui.controls.ui.ControlViewHolder$Companion */
    /* compiled from: ControlViewHolder.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final KClass<? extends Behavior> findBehaviorClass(int i, @NotNull ControlTemplate controlTemplate, int i2) {
            Class<ToggleRangeBehavior> cls = ToggleRangeBehavior.class;
            Class<TouchBehavior> cls2 = TouchBehavior.class;
            Intrinsics.checkNotNullParameter(controlTemplate, "template");
            if (i != 1) {
                return Reflection.getOrCreateKotlinClass(StatusBehavior.class);
            }
            if (Intrinsics.areEqual((Object) controlTemplate, (Object) ControlTemplate.NO_TEMPLATE)) {
                return Reflection.getOrCreateKotlinClass(cls2);
            }
            if (controlTemplate instanceof ThumbnailTemplate) {
                return Reflection.getOrCreateKotlinClass(ThumbnailBehavior.class);
            }
            if (i2 == 50) {
                return Reflection.getOrCreateKotlinClass(cls2);
            }
            if (controlTemplate instanceof ToggleTemplate) {
                return Reflection.getOrCreateKotlinClass(ToggleBehavior.class);
            }
            if (controlTemplate instanceof StatelessTemplate) {
                return Reflection.getOrCreateKotlinClass(cls2);
            }
            if (controlTemplate instanceof ToggleRangeTemplate) {
                return Reflection.getOrCreateKotlinClass(cls);
            }
            if (controlTemplate instanceof RangeTemplate) {
                return Reflection.getOrCreateKotlinClass(cls);
            }
            return Reflection.getOrCreateKotlinClass(controlTemplate instanceof TemperatureControlTemplate ? TemperatureControlBehavior.class : DefaultBehavior.class);
        }
    }

    @NotNull
    public final ImageView getIcon() {
        return this.icon;
    }

    @NotNull
    public final TextView getStatus() {
        return this.status;
    }

    @NotNull
    public final TextView getTitle() {
        return this.title;
    }

    @NotNull
    public final TextView getSubtitle() {
        return this.subtitle;
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }

    @NotNull
    public final ClipDrawable getClipLayer() {
        return this.clipLayer;
    }

    @NotNull
    public final ControlWithState getCws() {
        ControlWithState controlWithState = this.cws;
        if (controlWithState != null) {
            return controlWithState;
        }
        Intrinsics.throwUninitializedPropertyAccessException("cws");
        throw null;
    }

    public final void setCws(@NotNull ControlWithState controlWithState) {
        Intrinsics.checkNotNullParameter(controlWithState, "<set-?>");
        this.cws = controlWithState;
    }

    @Nullable
    public final ControlAction getLastAction() {
        return this.lastAction;
    }

    public final void setLoading(boolean z) {
        this.isLoading = z;
    }

    public final void setVisibleDialog(@Nullable Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public final int getDeviceType() {
        Control control = getCws().getControl();
        Integer valueOf = control == null ? null : Integer.valueOf(control.getDeviceType());
        return valueOf == null ? getCws().getCi().getDeviceType() : valueOf.intValue();
    }

    public final int getControlStatus() {
        Control control = getCws().getControl();
        if (control == null) {
            return 0;
        }
        return control.getStatus();
    }

    @NotNull
    public final ControlTemplate getControlTemplate() {
        Control control = getCws().getControl();
        ControlTemplate controlTemplate = control == null ? null : control.getControlTemplate();
        if (controlTemplate != null) {
            return controlTemplate;
        }
        ControlTemplate controlTemplate2 = ControlTemplate.NO_TEMPLATE;
        Intrinsics.checkNotNullExpressionValue(controlTemplate2, "NO_TEMPLATE");
        return controlTemplate2;
    }

    public final void setUserInteractionInProgress(boolean z) {
        this.userInteractionInProgress = z;
    }

    public final void bindData(@NotNull ControlWithState controlWithState, boolean z) {
        Intrinsics.checkNotNullParameter(controlWithState, "cws");
        if (!this.userInteractionInProgress) {
            setCws(controlWithState);
            if (getControlStatus() == 0 || getControlStatus() == 2) {
                this.title.setText(controlWithState.getCi().getControlTitle());
                this.subtitle.setText(controlWithState.getCi().getControlSubtitle());
            } else {
                Control control = controlWithState.getControl();
                if (control != null) {
                    getTitle().setText(control.getTitle());
                    getSubtitle().setText(control.getSubtitle());
                }
            }
            boolean z2 = true;
            if (controlWithState.getControl() != null) {
                getLayout().setClickable(true);
                getLayout().setOnLongClickListener(new ControlViewHolder$bindData$2$1(this));
                getControlActionCoordinator().runPendingAction(controlWithState.getCi().getControlId());
            }
            boolean z3 = this.isLoading;
            this.isLoading = false;
            this.behavior = bindBehavior$default(this, this.behavior, Companion.findBehaviorClass(getControlStatus(), getControlTemplate(), getDeviceType()), 0, 4, (Object) null);
            updateContentDescription();
            if (!z3 || this.isLoading) {
                z2 = false;
            }
            if (z2) {
                this.controlsMetricsLogger.refreshEnd(this, z);
            }
        }
    }

    public final void actionResponse(int i) {
        this.controlActionCoordinator.enableActionOnTouch(getCws().getCi().getControlId());
        boolean z = this.lastChallengeDialog != null;
        if (i == 0) {
            this.lastChallengeDialog = null;
            setErrorStatus();
        } else if (i == 1) {
            this.lastChallengeDialog = null;
        } else if (i == 2) {
            this.lastChallengeDialog = null;
            setErrorStatus();
        } else if (i == 3) {
            Dialog createConfirmationDialog = ChallengeDialogs.INSTANCE.createConfirmationDialog(this, this.onDialogCancel);
            this.lastChallengeDialog = createConfirmationDialog;
            if (createConfirmationDialog != null) {
                createConfirmationDialog.show();
            }
        } else if (i == 4) {
            Dialog createPinDialog = ChallengeDialogs.INSTANCE.createPinDialog(this, false, z, this.onDialogCancel);
            this.lastChallengeDialog = createPinDialog;
            if (createPinDialog != null) {
                createPinDialog.show();
            }
        } else if (i == 5) {
            Dialog createPinDialog2 = ChallengeDialogs.INSTANCE.createPinDialog(this, true, z, this.onDialogCancel);
            this.lastChallengeDialog = createPinDialog2;
            if (createPinDialog2 != null) {
                createPinDialog2.show();
            }
        }
    }

    public final void dismiss() {
        Dialog dialog = this.lastChallengeDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
        this.lastChallengeDialog = null;
        Dialog dialog2 = this.visibleDialog;
        if (dialog2 != null) {
            dialog2.dismiss();
        }
        this.visibleDialog = null;
    }

    public final void setErrorStatus() {
        animateStatusChange(true, new ControlViewHolder$setErrorStatus$1(this, this.context.getResources().getString(R$string.controls_error_failed)));
    }

    private final void updateContentDescription() {
        ViewGroup viewGroup = this.layout;
        StringBuilder sb = new StringBuilder();
        sb.append(this.title.getText());
        sb.append(' ');
        sb.append(this.subtitle.getText());
        sb.append(' ');
        sb.append(this.status.getText());
        viewGroup.setContentDescription(sb.toString());
    }

    public final void action(@NotNull ControlAction controlAction) {
        Intrinsics.checkNotNullParameter(controlAction, "action");
        this.lastAction = controlAction;
        this.controlsController.action(getCws().getComponentName(), getCws().getCi(), controlAction);
    }

    public final boolean usePanel() {
        return FORCE_PANEL_DEVICES.contains(Integer.valueOf(getDeviceType())) || Intrinsics.areEqual((Object) getControlTemplate(), (Object) ControlTemplate.NO_TEMPLATE);
    }

    public static /* synthetic */ Behavior bindBehavior$default(ControlViewHolder controlViewHolder, Behavior behavior2, KClass kClass, int i, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return controlViewHolder.bindBehavior(behavior2, kClass, i);
    }

    @NotNull
    public final Behavior bindBehavior(@Nullable Behavior behavior2, @NotNull KClass<? extends Behavior> kClass, int i) {
        Intrinsics.checkNotNullParameter(kClass, "clazz");
        if (behavior2 == null || !Intrinsics.areEqual((Object) Reflection.getOrCreateKotlinClass(behavior2.getClass()), (Object) kClass)) {
            behavior2 = (Behavior) JvmClassMappingKt.getJavaClass(kClass).newInstance();
            behavior2.initialize(this);
            this.layout.setAccessibilityDelegate((View.AccessibilityDelegate) null);
        }
        behavior2.bind(getCws(), i);
        Intrinsics.checkNotNullExpressionValue(behavior2, "behavior.also {\n            it.bind(cws, offset)\n        }");
        return behavior2;
    }

    /* renamed from: applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core$default */
    public static /* synthetic */ void m28x1a61c355(ControlViewHolder controlViewHolder, boolean z, int i, boolean z2, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            z2 = true;
        }
        controlViewHolder.mo12775x3918d5b8(z, i, z2);
    }

    /* renamed from: applyRenderInfo$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo12775x3918d5b8(boolean z, int i, boolean z2) {
        int i2;
        if (getControlStatus() == 1 || getControlStatus() == 0) {
            i2 = getDeviceType();
        } else {
            i2 = -1000;
        }
        RenderInfo lookup = RenderInfo.Companion.lookup(this.context, getCws().getComponentName(), i2, i);
        ColorStateList colorStateList = this.context.getResources().getColorStateList(lookup.getForeground(), this.context.getTheme());
        CharSequence charSequence = this.nextStatusText;
        Control control = getCws().getControl();
        if (Intrinsics.areEqual((Object) charSequence, (Object) this.status.getText())) {
            z2 = false;
        }
        animateStatusChange(z2, new ControlViewHolder$applyRenderInfo$1(this, z, charSequence, lookup, colorStateList, control));
        animateBackgroundChange(z2, z, lookup.getEnabledBackground());
    }

    public final void setStatusTextSize(float f) {
        this.status.setTextSize(0, f);
    }

    public static /* synthetic */ void setStatusText$default(ControlViewHolder controlViewHolder, CharSequence charSequence, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        controlViewHolder.setStatusText(charSequence, z);
    }

    public final void setStatusText(@NotNull CharSequence charSequence, boolean z) {
        Intrinsics.checkNotNullParameter(charSequence, "text");
        if (z) {
            this.status.setAlpha(1.0f);
            this.status.setText(charSequence);
            updateContentDescription();
        }
        this.nextStatusText = charSequence;
    }

    private final void animateBackgroundChange(boolean z, boolean z2, int i) {
        List list;
        int i2;
        ColorStateList customColor;
        Resources resources = this.context.getResources();
        int i3 = R$color.control_default_background;
        int color = resources.getColor(i3, this.context.getTheme());
        if (z2) {
            Control control = getCws().getControl();
            Integer num = null;
            if (!(control == null || (customColor = control.getCustomColor()) == null)) {
                num = Integer.valueOf(customColor.getColorForState(new int[]{16842910}, customColor.getDefaultColor()));
            }
            if (num == null) {
                i2 = this.context.getResources().getColor(i, this.context.getTheme());
            } else {
                i2 = num.intValue();
            }
            list = CollectionsKt__CollectionsKt.listOf(Integer.valueOf(i2), 255);
        } else {
            list = CollectionsKt__CollectionsKt.listOf(Integer.valueOf(this.context.getResources().getColor(i3, this.context.getTheme())), 0);
        }
        int intValue = ((Number) list.get(0)).intValue();
        int intValue2 = ((Number) list.get(1)).intValue();
        if (this.behavior instanceof ToggleRangeBehavior) {
            color = ColorUtils.blendARGB(color, intValue, this.toggleBackgroundIntensity);
        }
        int i4 = color;
        Drawable drawable = this.clipLayer.getDrawable();
        if (drawable != null) {
            getClipLayer().setAlpha(0);
            ValueAnimator valueAnimator = this.stateAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (z) {
                startBackgroundAnimation(drawable, intValue2, intValue, i4);
            } else {
                applyBackgroundChange(drawable, intValue2, intValue, i4, 1.0f);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = ((android.graphics.drawable.GradientDrawable) r10).getColor();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void startBackgroundAnimation(android.graphics.drawable.Drawable r10, int r11, int r12, int r13) {
        /*
            r9 = this;
            boolean r0 = r10 instanceof android.graphics.drawable.GradientDrawable
            if (r0 == 0) goto L_0x0014
            r0 = r10
            android.graphics.drawable.GradientDrawable r0 = (android.graphics.drawable.GradientDrawable) r0
            android.content.res.ColorStateList r0 = r0.getColor()
            if (r0 != 0) goto L_0x000e
            goto L_0x0014
        L_0x000e:
            int r0 = r0.getDefaultColor()
            r2 = r0
            goto L_0x0015
        L_0x0014:
            r2 = r12
        L_0x0015:
            android.graphics.drawable.GradientDrawable r0 = r9.baseLayer
            android.content.res.ColorStateList r0 = r0.getColor()
            if (r0 != 0) goto L_0x001f
            r4 = r13
            goto L_0x0024
        L_0x001f:
            int r0 = r0.getDefaultColor()
            r4 = r0
        L_0x0024:
            android.view.ViewGroup r0 = r9.layout
            float r6 = r0.getAlpha()
            r0 = 2
            int[] r0 = new int[r0]
            r1 = 0
            android.graphics.drawable.ClipDrawable r3 = r9.clipLayer
            int r3 = r3.getAlpha()
            r0[r1] = r3
            r1 = 1
            r0[r1] = r11
            android.animation.ValueAnimator r11 = android.animation.ValueAnimator.ofInt(r0)
            com.android.systemui.controls.ui.ControlViewHolder$startBackgroundAnimation$1$1 r0 = new com.android.systemui.controls.ui.ControlViewHolder$startBackgroundAnimation$1$1
            r1 = r0
            r3 = r12
            r5 = r13
            r7 = r9
            r8 = r10
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            r11.addUpdateListener(r0)
            com.android.systemui.controls.ui.ControlViewHolder$startBackgroundAnimation$1$2 r10 = new com.android.systemui.controls.ui.ControlViewHolder$startBackgroundAnimation$1$2
            r10.<init>(r9)
            r11.addListener(r10)
            r12 = 700(0x2bc, double:3.46E-321)
            r11.setDuration(r12)
            android.view.animation.Interpolator r10 = com.android.systemui.animation.Interpolators.CONTROL_STATE
            r11.setInterpolator(r10)
            r11.start()
            kotlin.Unit r10 = kotlin.Unit.INSTANCE
            r9.stateAnimator = r11
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlViewHolder.startBackgroundAnimation(android.graphics.drawable.Drawable, int, int, int):void");
    }

    /* access modifiers changed from: private */
    public final void applyBackgroundChange(Drawable drawable, int i, int i2, int i3, float f) {
        drawable.setAlpha(i);
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(i2);
        }
        this.baseLayer.setColor(i3);
        this.layout.setAlpha(f);
    }

    private final void animateStatusChange(boolean z, Function0<Unit> function0) {
        Animator animator = this.statusAnimator;
        if (animator != null) {
            animator.cancel();
        }
        if (!z) {
            function0.invoke();
        } else if (this.isLoading) {
            function0.invoke();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.status, "alpha", new float[]{0.45f});
            ofFloat.setRepeatMode(2);
            ofFloat.setRepeatCount(-1);
            ofFloat.setDuration(500);
            ofFloat.setInterpolator(Interpolators.LINEAR);
            ofFloat.setStartDelay(900);
            ofFloat.start();
            Unit unit = Unit.INSTANCE;
            this.statusAnimator = ofFloat;
        } else {
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.status, "alpha", new float[]{0.0f});
            ofFloat2.setDuration(200);
            Interpolator interpolator = Interpolators.LINEAR;
            ofFloat2.setInterpolator(interpolator);
            ofFloat2.addListener(new ControlViewHolder$animateStatusChange$fadeOut$1$1(function0));
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.status, "alpha", new float[]{1.0f});
            ofFloat3.setDuration(200);
            ofFloat3.setInterpolator(interpolator);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(new Animator[]{ofFloat2, ofFloat3});
            animatorSet.addListener(new ControlViewHolder$animateStatusChange$2$1(this));
            animatorSet.start();
            Unit unit2 = Unit.INSTANCE;
            this.statusAnimator = animatorSet;
        }
    }

    /* renamed from: updateStatusRow$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo12802xcd94ff85(boolean z, @NotNull CharSequence charSequence, @NotNull Drawable drawable, @NotNull ColorStateList colorStateList, @Nullable Control control) {
        Icon customIcon;
        Intrinsics.checkNotNullParameter(charSequence, "text");
        Intrinsics.checkNotNullParameter(drawable, "drawable");
        Intrinsics.checkNotNullParameter(colorStateList, "color");
        setEnabled(z);
        this.status.setText(charSequence);
        updateContentDescription();
        this.status.setTextColor(colorStateList);
        Unit unit = null;
        if (!(control == null || (customIcon = control.getCustomIcon()) == null)) {
            getIcon().setImageIcon(customIcon);
            getIcon().setImageTintList(customIcon.getTintList());
            unit = Unit.INSTANCE;
        }
        if (unit == null) {
            if (drawable instanceof StateListDrawable) {
                if (getIcon().getDrawable() == null || !(getIcon().getDrawable() instanceof StateListDrawable)) {
                    getIcon().setImageDrawable(drawable);
                }
                getIcon().setImageState(z ? ATTR_ENABLED : ATTR_DISABLED, true);
            } else {
                getIcon().setImageDrawable(drawable);
            }
            if (getDeviceType() != 52) {
                getIcon().setImageTintList(colorStateList);
            }
        }
    }

    private final void setEnabled(boolean z) {
        this.status.setEnabled(z);
        this.icon.setEnabled(z);
    }
}
