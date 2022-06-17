package com.android.systemui.p006qs.tileimpl;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.tileimpl.HeightOverrideable;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.p005qs.QSTileView;
import java.util.Objects;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileViewImpl */
/* compiled from: QSTileViewImpl.kt */
public class QSTileViewImpl extends QSTileView implements HeightOverrideable {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final QSIconView _icon;
    @Nullable
    private String accessibilityClass;
    private ImageView chevronView;
    private final boolean collapsed;
    private final int colorActive;
    private Drawable colorBackgroundDrawable;
    private final int colorInactive;
    private final int colorLabelActive;
    private final int colorLabelInactive;
    private final int colorLabelUnavailable;
    private final int colorSecondaryLabelActive;
    private final int colorSecondaryLabelInactive;
    private final int colorSecondaryLabelUnavailable;
    private final int colorUnavailable;
    private ImageView customDrawableView;
    private int heightOverride = -1;
    protected TextView label;
    protected IgnorableChildLinearLayout labelContainer;
    private int lastState;
    @Nullable
    private CharSequence lastStateDescription;
    @NotNull
    private final int[] locInScreen;
    private int paintColor;
    protected RippleDrawable ripple;
    protected TextView secondaryLabel;
    private boolean showRippleEffect;
    protected ViewGroup sideView;
    @NotNull
    private final ValueAnimator singleAnimator;
    @Nullable
    private CharSequence stateDescriptionDeltas;
    private boolean tileState;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isSkipSetBackground() {
        return false;
    }

    public void updateBigTypePrc(boolean z) {
    }

    public void resetOverride() {
        HeightOverrideable.DefaultImpls.resetOverride(this);
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final QSIconView get_icon() {
        return this._icon;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public QSTileViewImpl(@NotNull Context context, @NotNull QSIconView qSIconView, boolean z) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(qSIconView, "_icon");
        this._icon = qSIconView;
        this.collapsed = z;
        this.colorActive = Utils.getColorAttrDefaultColor(context, 17956901);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(context, R$attr.offStateColor);
        this.colorInactive = colorAttrDefaultColor;
        this.colorUnavailable = Utils.applyAlpha(0.3f, colorAttrDefaultColor);
        this.colorLabelActive = Utils.getColorAttrDefaultColor(context, 16842809);
        int colorAttrDefaultColor2 = Utils.getColorAttrDefaultColor(context, 16842806);
        this.colorLabelInactive = colorAttrDefaultColor2;
        this.colorLabelUnavailable = Utils.applyAlpha(0.3f, colorAttrDefaultColor2);
        this.colorSecondaryLabelActive = Utils.getColorAttrDefaultColor(context, 16842810);
        int colorAttrDefaultColor3 = Utils.getColorAttrDefaultColor(context, 16842808);
        this.colorSecondaryLabelInactive = colorAttrDefaultColor3;
        this.colorSecondaryLabelUnavailable = Utils.applyAlpha(0.3f, colorAttrDefaultColor3);
        this.showRippleEffect = true;
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(350);
        valueAnimator.addUpdateListener(new QSTileViewImpl$singleAnimator$1$1(this));
        Unit unit = Unit.INSTANCE;
        this.singleAnimator = valueAnimator;
        this.lastState = -1;
        this.locInScreen = new int[2];
        updateLayout();
        addIconView();
        createAndAddLabels();
        createAndAddSideView();
    }

    /* renamed from: com.android.systemui.qs.tileimpl.QSTileViewImpl$Companion */
    /* compiled from: QSTileViewImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* renamed from: getTILE_STATE_RES_PREFIX$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
        public static /* synthetic */ void m48x68d94c60() {
        }

        private Companion() {
        }
    }

    public int getHeightOverride() {
        return this.heightOverride;
    }

    public void setHeightOverride(int i) {
        this.heightOverride = i;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final TextView getLabel() {
        TextView textView = this.label;
        if (textView != null) {
            return textView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("label");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setLabel(@NotNull TextView textView) {
        Intrinsics.checkNotNullParameter(textView, "<set-?>");
        this.label = textView;
    }

    /* access modifiers changed from: protected */
    @NotNull
    /* renamed from: getSecondaryLabel  reason: collision with other method in class */
    public final TextView m335getSecondaryLabel() {
        TextView textView = this.secondaryLabel;
        if (textView != null) {
            return textView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("secondaryLabel");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setSecondaryLabel(@NotNull TextView textView) {
        Intrinsics.checkNotNullParameter(textView, "<set-?>");
        this.secondaryLabel = textView;
    }

    /* access modifiers changed from: protected */
    @NotNull
    /* renamed from: getLabelContainer  reason: collision with other method in class */
    public final IgnorableChildLinearLayout m334getLabelContainer() {
        IgnorableChildLinearLayout ignorableChildLinearLayout = this.labelContainer;
        if (ignorableChildLinearLayout != null) {
            return ignorableChildLinearLayout;
        }
        Intrinsics.throwUninitializedPropertyAccessException("labelContainer");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setLabelContainer(@NotNull IgnorableChildLinearLayout ignorableChildLinearLayout) {
        Intrinsics.checkNotNullParameter(ignorableChildLinearLayout, "<set-?>");
        this.labelContainer = ignorableChildLinearLayout;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final ViewGroup getSideView() {
        ViewGroup viewGroup = this.sideView;
        if (viewGroup != null) {
            return viewGroup;
        }
        Intrinsics.throwUninitializedPropertyAccessException("sideView");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setSideView(@NotNull ViewGroup viewGroup) {
        Intrinsics.checkNotNullParameter(viewGroup, "<set-?>");
        this.sideView = viewGroup;
    }

    /* access modifiers changed from: protected */
    public final void setShowRippleEffect(boolean z) {
        this.showRippleEffect = z;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final RippleDrawable getRipple() {
        RippleDrawable rippleDrawable = this.ripple;
        if (rippleDrawable != null) {
            return rippleDrawable;
        }
        Intrinsics.throwUninitializedPropertyAccessException("ripple");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setRipple(@NotNull RippleDrawable rippleDrawable) {
        Intrinsics.checkNotNullParameter(rippleDrawable, "<set-?>");
        this.ripple = rippleDrawable;
    }

    public void updateLayout() {
        setId(LinearLayout.generateViewId());
        setOrientation(0);
        setGravity(8388627);
        setImportantForAccessibility(1);
        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
        setBackground(createTileBackground());
        setColor(getBackgroundColorForState(2));
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.qs_tile_padding);
        setPaddingRelative(getResources().getDimensionPixelSize(R$dimen.qs_tile_start_padding), dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
    }

    public int getIconSize() {
        return MotoFeature.isCliContext(this.mContext) ? getResources().getDimensionPixelSize(R$dimen.cli_qs_quick_tile_size) : getResources().getDimensionPixelSize(R$dimen.qs_icon_size);
    }

    public void addIconView() {
        int iconSize = getIconSize();
        addView(this._icon, new LinearLayout.LayoutParams(iconSize, iconSize));
    }

    @NotNull
    public RippleDrawable getRippleDrawable() {
        Drawable drawable = this.mContext.getDrawable(R$drawable.qs_tile_background);
        Objects.requireNonNull(drawable, "null cannot be cast to non-null type android.graphics.drawable.RippleDrawable");
        return (RippleDrawable) drawable;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(@Nullable Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateResources();
    }

    public void updateResources() {
        TextView label2 = getLabel();
        int i = R$dimen.qs_tile_text_size;
        FontSizeUtils.updateFontSize(label2, i);
        FontSizeUtils.updateFontSize(getSecondaryLabel(), i);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R$dimen.qs_icon_size);
        ViewGroup.LayoutParams layoutParams = this._icon.getLayoutParams();
        layoutParams.height = dimensionPixelSize;
        layoutParams.width = dimensionPixelSize;
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R$dimen.qs_tile_padding);
        setPaddingRelative(getResources().getDimensionPixelSize(R$dimen.qs_tile_start_padding), dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2);
        int dimensionPixelSize3 = getResources().getDimensionPixelSize(R$dimen.qs_label_container_margin);
        ViewGroup.LayoutParams layoutParams2 = getLabelContainer().getLayoutParams();
        Objects.requireNonNull(layoutParams2, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ((ViewGroup.MarginLayoutParams) layoutParams2).setMarginStart(dimensionPixelSize3);
        ViewGroup.LayoutParams layoutParams3 = getSideView().getLayoutParams();
        Objects.requireNonNull(layoutParams3, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ((ViewGroup.MarginLayoutParams) layoutParams3).setMarginStart(dimensionPixelSize3);
        ImageView imageView = this.chevronView;
        if (imageView != null) {
            ViewGroup.LayoutParams layoutParams4 = imageView.getLayoutParams();
            Objects.requireNonNull(layoutParams4, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams4;
            marginLayoutParams.height = dimensionPixelSize;
            marginLayoutParams.width = dimensionPixelSize;
            int dimensionPixelSize4 = getResources().getDimensionPixelSize(R$dimen.qs_drawable_end_margin);
            ImageView imageView2 = this.customDrawableView;
            if (imageView2 != null) {
                ViewGroup.LayoutParams layoutParams5 = imageView2.getLayoutParams();
                Objects.requireNonNull(layoutParams5, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
                ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) layoutParams5;
                marginLayoutParams2.height = dimensionPixelSize;
                marginLayoutParams2.setMarginEnd(dimensionPixelSize4);
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("chevronView");
        throw null;
    }

    public void createAndAddLabels() {
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.qs_tile_label, this, false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.systemui.qs.tileimpl.IgnorableChildLinearLayout");
        setLabelContainer((IgnorableChildLinearLayout) inflate);
        View requireViewById = getLabelContainer().requireViewById(R$id.tile_label);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "labelContainer.requireViewById(R.id.tile_label)");
        setLabel((TextView) requireViewById);
        View requireViewById2 = getLabelContainer().requireViewById(R$id.app_label);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "labelContainer.requireViewById(R.id.app_label)");
        setSecondaryLabel((TextView) requireViewById2);
        if (this.collapsed) {
            getLabelContainer().setIgnoreLastView(true);
            getLabelContainer().setForceUnspecifiedMeasure(true);
            getSecondaryLabel().setAlpha(0.0f);
        }
        setLabelColor(getLabelColorForState(2));
        setSecondaryLabelColor(getSecondaryLabelColorForState(2));
        addView(getLabelContainer());
    }

    public void createAndAddSideView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.qs_tile_side_icon, this, false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.ViewGroup");
        setSideView((ViewGroup) inflate);
        View requireViewById = getSideView().requireViewById(R$id.customDrawable);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "sideView.requireViewById(R.id.customDrawable)");
        this.customDrawableView = (ImageView) requireViewById;
        View requireViewById2 = getSideView().requireViewById(R$id.chevron);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "sideView.requireViewById(R.id.chevron)");
        this.chevronView = (ImageView) requireViewById2;
        setChevronColor(getChevronColorForState(2));
        addView(getSideView());
    }

    @NotNull
    public final Drawable createTileBackground() {
        setRipple(getRippleDrawable());
        Drawable findDrawableByLayerId = getRipple().findDrawableByLayerId(R$id.background);
        Intrinsics.checkNotNullExpressionValue(findDrawableByLayerId, "ripple.findDrawableByLayerId(R.id.background)");
        this.colorBackgroundDrawable = findDrawableByLayerId;
        return getRipple();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (getHeightOverride() != -1) {
            setBottom(getTop() + getHeightOverride());
        }
    }

    @NotNull
    public View updateAccessibilityOrder(@Nullable View view) {
        setAccessibilityTraversalAfter(view == null ? 0 : view.getId());
        return this;
    }

    @NotNull
    public QSIconView getIcon() {
        return this._icon;
    }

    @NotNull
    public View getIconWithBackground() {
        return getIcon();
    }

    public void init(@NotNull QSTile qSTile) {
        Intrinsics.checkNotNullParameter(qSTile, "tile");
        init(new QSTileViewImpl$init$1(qSTile, this), new QSTileViewImpl$init$2(qSTile, this));
    }

    public final void init(@Nullable View.OnClickListener onClickListener, @Nullable View.OnLongClickListener onLongClickListener) {
        setOnClickListener(onClickListener);
        setOnLongClickListener(onLongClickListener);
    }

    public void onStateChanged(@NotNull QSTile.State state) {
        Intrinsics.checkNotNullParameter(state, "state");
        post(new QSTileViewImpl$onStateChanged$1(this, state));
    }

    public int getDetailY() {
        return getTop() + (getHeight() / 2);
    }

    public void setClickable(boolean z) {
        RippleDrawable rippleDrawable;
        super.setClickable(z);
        if (!isSkipSetBackground()) {
            if (!z || !this.showRippleEffect) {
                Drawable drawable = this.colorBackgroundDrawable;
                rippleDrawable = drawable;
                if (drawable == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("colorBackgroundDrawable");
                    throw null;
                }
            } else {
                RippleDrawable ripple2 = getRipple();
                Drawable drawable2 = this.colorBackgroundDrawable;
                if (drawable2 != null) {
                    drawable2.setCallback(ripple2);
                    Unit unit = Unit.INSTANCE;
                    rippleDrawable = ripple2;
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("colorBackgroundDrawable");
                    throw null;
                }
            }
            setBackground(rippleDrawable);
        }
    }

    @NotNull
    public View getLabelContainer() {
        return getLabelContainer();
    }

    @NotNull
    public View getSecondaryLabel() {
        return getSecondaryLabel();
    }

    @NotNull
    public View getSecondaryIcon() {
        return getSideView();
    }

    public void onInitializeAccessibilityEvent(@NotNull AccessibilityEvent accessibilityEvent) {
        Intrinsics.checkNotNullParameter(accessibilityEvent, "event");
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (!TextUtils.isEmpty(this.accessibilityClass)) {
            accessibilityEvent.setClassName(this.accessibilityClass);
        }
        if (accessibilityEvent.getContentChangeTypes() == 64 && this.stateDescriptionDeltas != null) {
            accessibilityEvent.getText().add(this.stateDescriptionDeltas);
            this.stateDescriptionDeltas = null;
        }
    }

    public void onInitializeAccessibilityNodeInfo(@NotNull AccessibilityNodeInfo accessibilityNodeInfo) {
        Intrinsics.checkNotNullParameter(accessibilityNodeInfo, "info");
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setSelected(false);
        if (!TextUtils.isEmpty(this.accessibilityClass)) {
            accessibilityNodeInfo.setClassName(this.accessibilityClass);
            if (Intrinsics.areEqual((Object) Switch.class.getName(), (Object) this.accessibilityClass)) {
                accessibilityNodeInfo.setText(getResources().getString(this.tileState ? R$string.switch_bar_on : R$string.switch_bar_off));
                accessibilityNodeInfo.setChecked(this.tileState);
                accessibilityNodeInfo.setCheckable(true);
                if (isLongClickable()) {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK.getId(), getResources().getString(R$string.accessibility_long_click_tile)));
                }
            }
        }
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        sb.append("locInScreen=(" + this.locInScreen[0] + ", " + this.locInScreen[1] + ')');
        sb.append(Intrinsics.stringPlus(", iconView=", this._icon));
        sb.append(Intrinsics.stringPlus(", tileState=", Boolean.valueOf(this.tileState)));
        sb.append("]");
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "sb.toString()");
        return sb2;
    }

    /* access modifiers changed from: protected */
    public void handleStateChanged(@NotNull QSTile.State state) {
        String str;
        boolean z;
        Intrinsics.checkNotNullParameter(state, "state");
        boolean animationsEnabled = animationsEnabled();
        this.showRippleEffect = state.showRippleEffect;
        setClickable(state.state != 0);
        setLongClickable(state.handlesLongClick);
        getIcon().setIcon(state, animationsEnabled);
        setContentDescription(state.contentDescription);
        StringBuilder sb = new StringBuilder();
        String stateText = getStateText(state);
        if (!TextUtils.isEmpty(stateText)) {
            sb.append(stateText);
            if (TextUtils.isEmpty(state.secondaryLabel)) {
                state.secondaryLabel = stateText;
            }
        }
        if (!TextUtils.isEmpty(state.stateDescription)) {
            sb.append(", ");
            sb.append(state.stateDescription);
            int i = this.lastState;
            if (i != -1 && state.state == i && !Intrinsics.areEqual((Object) state.stateDescription, (Object) this.lastStateDescription)) {
                this.stateDescriptionDeltas = state.stateDescription;
            }
        }
        setStateDescription(sb.toString());
        this.lastStateDescription = state.stateDescription;
        if (state.state == 0) {
            str = null;
        } else {
            str = state.expandedAccessibilityClassName;
        }
        this.accessibilityClass = str;
        if ((state instanceof QSTile.BooleanState) && this.tileState != (z = ((QSTile.BooleanState) state).value)) {
            this.tileState = z;
        }
        if (!Objects.equals(getLabel().getText(), state.label)) {
            getLabel().setText(state.label);
        }
        if (!Objects.equals(getSecondaryLabel().getText(), state.secondaryLabel)) {
            getSecondaryLabel().setText(state.secondaryLabel);
            getSecondaryLabel().setVisibility(TextUtils.isEmpty(state.secondaryLabel) ? 8 : 0);
        }
        if (state.state != this.lastState) {
            this.singleAnimator.cancel();
            if (animationsEnabled) {
                ValueAnimator valueAnimator = this.singleAnimator;
                PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[4];
                propertyValuesHolderArr[0] = QSTileViewImplKt.colorValuesHolder("background", this.paintColor, getBackgroundColorForState(state.state));
                propertyValuesHolderArr[1] = QSTileViewImplKt.colorValuesHolder("label", getLabel().getCurrentTextColor(), getLabelColorForState(state.state));
                propertyValuesHolderArr[2] = QSTileViewImplKt.colorValuesHolder("secondaryLabel", getSecondaryLabel().getCurrentTextColor(), getSecondaryLabelColorForState(state.state));
                int[] iArr = new int[2];
                ImageView imageView = this.chevronView;
                if (imageView != null) {
                    ColorStateList imageTintList = imageView.getImageTintList();
                    iArr[0] = imageTintList == null ? 0 : imageTintList.getDefaultColor();
                    iArr[1] = getChevronColorForState(state.state);
                    propertyValuesHolderArr[3] = QSTileViewImplKt.colorValuesHolder("chevron", iArr);
                    valueAnimator.setValues(propertyValuesHolderArr);
                    this.singleAnimator.start();
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("chevronView");
                    throw null;
                }
            } else {
                setAllColors(getBackgroundColorForState(state.state), getLabelColorForState(state.state), getSecondaryLabelColorForState(state.state), getChevronColorForState(state.state));
            }
        }
        loadSideViewDrawableIfNecessary(state);
        getLabel().setEnabled(!state.disabledByPolicy);
        this.lastState = state.state;
    }

    public final void cancelSingleAnimator() {
        this.singleAnimator.cancel();
    }

    /* access modifiers changed from: protected */
    public final void setAllColors(int i, int i2, int i3, int i4) {
        setColor(i);
        setLabelColor(i2);
        setSecondaryLabelColor(i3);
        setChevronColor(i4);
    }

    public void setColor(int i) {
        Drawable drawable = this.colorBackgroundDrawable;
        if (drawable != null) {
            drawable.setTint(i);
            this.paintColor = i;
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("colorBackgroundDrawable");
        throw null;
    }

    public void setLabelColor(int i) {
        getLabel().setTextColor(i);
    }

    public void setSecondaryLabelColor(int i) {
        getSecondaryLabel().setTextColor(i);
    }

    private final void setChevronColor(int i) {
        ImageView imageView = this.chevronView;
        if (imageView != null) {
            imageView.setImageTintList(ColorStateList.valueOf(i));
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("chevronView");
            throw null;
        }
    }

    private final void loadSideViewDrawableIfNecessary(QSTile.State state) {
        Drawable drawable = state.sideViewCustomDrawable;
        if (drawable != null) {
            ImageView imageView = this.customDrawableView;
            if (imageView != null) {
                imageView.setImageDrawable(drawable);
                ImageView imageView2 = this.customDrawableView;
                if (imageView2 != null) {
                    imageView2.setVisibility(0);
                    ImageView imageView3 = this.chevronView;
                    if (imageView3 != null) {
                        imageView3.setVisibility(8);
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("chevronView");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
                throw null;
            }
        } else if (!(state instanceof QSTile.BooleanState) || ((QSTile.BooleanState) state).forceExpandIcon) {
            ImageView imageView4 = this.customDrawableView;
            if (imageView4 != null) {
                imageView4.setImageDrawable((Drawable) null);
                ImageView imageView5 = this.customDrawableView;
                if (imageView5 != null) {
                    imageView5.setVisibility(8);
                    ImageView imageView6 = this.chevronView;
                    if (imageView6 != null) {
                        imageView6.setVisibility(0);
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("chevronView");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
                throw null;
            }
        } else {
            ImageView imageView7 = this.customDrawableView;
            if (imageView7 != null) {
                imageView7.setImageDrawable((Drawable) null);
                ImageView imageView8 = this.customDrawableView;
                if (imageView8 != null) {
                    imageView8.setVisibility(8);
                    ImageView imageView9 = this.chevronView;
                    if (imageView9 != null) {
                        imageView9.setVisibility(8);
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("chevronView");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("customDrawableView");
                throw null;
            }
        }
    }

    private final String getStateText(QSTile.State state) {
        if (state.disabledByPolicy) {
            String string = getContext().getString(R$string.tile_disabled);
            Intrinsics.checkNotNullExpressionValue(string, "context.getString(R.string.tile_disabled)");
            return string;
        } else if (state.state != 0 && !(state instanceof QSTile.BooleanState)) {
            return "";
        } else {
            String str = getResources().getStringArray(SubtitleArrayMapping.INSTANCE.getSubtitleId(state.spec))[state.state];
            Intrinsics.checkNotNullExpressionValue(str, "{\n            var arrayResId = SubtitleArrayMapping.getSubtitleId(state.spec)\n            val array = resources.getStringArray(arrayResId)\n            array[state.state]\n        }");
            return str;
        }
    }

    /* access modifiers changed from: protected */
    public boolean animationsEnabled() {
        if (!isShown()) {
            return false;
        }
        if (!(getAlpha() == 1.0f)) {
            return false;
        }
        getLocationOnScreen(this.locInScreen);
        if (this.locInScreen[1] >= (-getHeight())) {
            return true;
        }
        return false;
    }

    public int getBackgroundColorForState(int i) {
        if (i == 0) {
            return this.colorUnavailable;
        }
        if (i == 1) {
            return this.colorInactive;
        }
        if (i == 2) {
            return this.colorActive;
        }
        Log.e("QSTileViewImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    public int getLabelColorForState(int i) {
        if (i == 0) {
            return this.colorLabelUnavailable;
        }
        if (i == 1) {
            return this.colorLabelInactive;
        }
        if (i == 2) {
            return this.colorLabelActive;
        }
        Log.e("QSTileViewImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    public int getSecondaryLabelColorForState(int i) {
        if (i == 0) {
            return this.colorSecondaryLabelUnavailable;
        }
        if (i == 1) {
            return this.colorSecondaryLabelInactive;
        }
        if (i == 2) {
            return this.colorSecondaryLabelActive;
        }
        Log.e("QSTileViewImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    /* access modifiers changed from: protected */
    public final int getChevronColorForState(int i) {
        return getSecondaryLabelColorForState(i);
    }
}
