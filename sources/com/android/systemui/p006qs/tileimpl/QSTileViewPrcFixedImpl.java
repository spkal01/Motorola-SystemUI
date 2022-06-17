package com.android.systemui.p006qs.tileimpl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.tileimpl.QSTileViewPrcFixedImpl */
/* compiled from: QSTileViewPrcFixedImpl.kt */
public class QSTileViewPrcFixedImpl extends QSTileViewImpl {
    private final boolean DEBUG = (!Build.IS_USER);
    @NotNull
    private final QSIconView _iconPrc;
    private final boolean collapsed;
    private int colorActiveFixed;
    private int colorInactiveFixed;
    private int colorLabelActiveFixed;
    private int colorLabelInactiveFixed;
    private int colorLabelUnavailableFixed;
    private int colorUnavailableFixed;
    protected FrameLayout iconFrame;
    @NotNull
    private final int[] locInScreen;
    protected ImageView mBg;
    @NotNull
    private final QSTileViewPrcFixedImpl$mConfigListener$1 mConfigListener;
    @NotNull
    private ConfigurationController mConfigurationController;
    private int mQsTileState;

    public void addIconView() {
    }

    public boolean isSkipSetBackground() {
        return true;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public QSTileViewPrcFixedImpl(@NotNull Context context, @NotNull QSIconView qSIconView, boolean z) {
        super(context, qSIconView, z);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(qSIconView, "_iconPrc");
        this._iconPrc = qSIconView;
        this.collapsed = z;
        this.colorActiveFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileActiveColorForFixed);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileInactiveColorForFixed);
        this.colorInactiveFixed = colorAttrDefaultColor;
        this.colorUnavailableFixed = Utils.applyAlpha(0.3f, colorAttrDefaultColor);
        this.colorLabelActiveFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcFixedTileLabelActiveColor);
        this.colorLabelInactiveFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcFixedTileLabelInactiveColor);
        this.colorLabelUnavailableFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcFixedTileLabelUnavailableColor);
        this.mQsTileState = 2;
        this.mConfigListener = new QSTileViewPrcFixedImpl$mConfigListener$1(this);
        this.locInScreen = new int[2];
        getSideView().setVisibility(8);
        Object obj = Dependency.get(ConfigurationController.class);
        Intrinsics.checkNotNullExpressionValue(obj, "get<ConfigurationController>(ConfigurationController::class.java)");
        this.mConfigurationController = (ConfigurationController) obj;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final FrameLayout getIconFrame() {
        FrameLayout frameLayout = this.iconFrame;
        if (frameLayout != null) {
            return frameLayout;
        }
        Intrinsics.throwUninitializedPropertyAccessException("iconFrame");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setIconFrame(@NotNull FrameLayout frameLayout) {
        Intrinsics.checkNotNullParameter(frameLayout, "<set-?>");
        this.iconFrame = frameLayout;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final ImageView getMBg() {
        ImageView imageView = this.mBg;
        if (imageView != null) {
            return imageView;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mBg");
        throw null;
    }

    /* access modifiers changed from: protected */
    public final void setMBg(@NotNull ImageView imageView) {
        Intrinsics.checkNotNullParameter(imageView, "<set-?>");
        this.mBg = imageView;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mConfigurationController.addCallback(this.mConfigListener);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mConfigurationController.removeCallback(this.mConfigListener);
    }

    public void updateLayout() {
        setId(LinearLayout.generateViewId());
        setOrientation(1);
        setGravity(17);
        setImportantForAccessibility(1);
        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
        setIconFrame(new FrameLayout(getContext()));
        setMBg(new ImageView(getContext()));
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_prc_fixed_qs_tile_background_size);
        createTileBackground();
        getMBg().setImageDrawable(getRipple());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize, 17);
        getMBg().setLayoutParams(layoutParams);
        getIconFrame().addView(getMBg(), layoutParams);
        setColor(getBackgroundColorForState(2));
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_horizontal_padding);
        setPaddingRelative(dimensionPixelSize2, 0, dimensionPixelSize2, 0);
        int iconSize = getIconSize();
        getIconFrame().addView(get_icon(), new FrameLayout.LayoutParams(iconSize, iconSize, 17));
        getIconFrame().setClipChildren(false);
        getIconFrame().setClipToPadding(false);
        addView(getIconFrame(), 0, new LinearLayout.LayoutParams(-2, -2));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        updateThemeColor();
    }

    public int getIconSize() {
        return getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_icon_size);
    }

    @NotNull
    public RippleDrawable getRippleDrawable() {
        Drawable drawable = this.mContext.getDrawable(R$drawable.prc_qs_fixed_tile_background);
        Objects.requireNonNull(drawable, "null cannot be cast to non-null type android.graphics.drawable.RippleDrawable");
        return (RippleDrawable) drawable;
    }

    public void updateResources() {
        TextView label = getLabel();
        int i = R$dimen.prc_qs_tile_text_size;
        FontSizeUtils.updateFontSize(label, i);
        FontSizeUtils.updateFontSize(getSecondaryLabel(), i);
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_icon_size);
        ViewGroup.LayoutParams layoutParams = get_icon().getLayoutParams();
        layoutParams.height = dimensionPixelSize;
        layoutParams.width = dimensionPixelSize;
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_horizontal_padding);
        setPaddingRelative(dimensionPixelSize2, 0, dimensionPixelSize2, 0);
    }

    public void createAndAddLabels() {
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.zz_moto_prc_qs_fixed_tile_label, this, false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type com.android.systemui.qs.tileimpl.IgnorableChildLinearLayout");
        setLabelContainer((IgnorableChildLinearLayout) inflate);
        View requireViewById = getLabelContainer().requireViewById(R$id.tile_label);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "labelContainer.requireViewById(R.id.tile_label)");
        setLabel((TextView) requireViewById);
        View requireViewById2 = getLabelContainer().requireViewById(R$id.app_label);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "labelContainer.requireViewById(R.id.app_label)");
        setSecondaryLabel((TextView) requireViewById2);
        setLabelColor(getLabelColorForState(2));
        setSecondaryLabelColor(getSecondaryLabelColorForState(2));
        getSecondaryLabel().setVisibility(8);
        addView(getLabelContainer());
    }

    /* access modifiers changed from: protected */
    public void handleStateChanged(@NotNull QSTile.State state) {
        Intrinsics.checkNotNullParameter(state, "state");
        if (this.DEBUG) {
            Log.i("QSTileViewPrcFixedImpl", "QSTileLog handleStateChanged = " + state.spec + " label = " + state.label + " state = " + state.state);
        }
        super.handleStateChanged(state);
        this.mQsTileState = state.state;
        getSecondaryLabel().setVisibility(8);
        getSideView().setVisibility(8);
    }

    public int getBackgroundColorForState(int i) {
        if (i == 0) {
            return this.colorUnavailableFixed;
        }
        if (i == 1) {
            return this.colorInactiveFixed;
        }
        if (i == 2) {
            return this.colorActiveFixed;
        }
        Log.e("QSTileViewPrcFixedImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    public int getLabelColorForState(int i) {
        if (i == 0) {
            return this.colorLabelUnavailableFixed;
        }
        if (i == 1) {
            return this.colorLabelInactiveFixed;
        }
        if (i == 2) {
            return this.colorLabelActiveFixed;
        }
        Log.e("QSTileViewPrcFixedImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    public final void updateThemeColor() {
        this.colorActiveFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcQSTileActiveColorForFixed);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcQSTileInactiveColorForFixed);
        this.colorInactiveFixed = colorAttrDefaultColor;
        this.colorUnavailableFixed = Utils.applyAlpha(0.3f, colorAttrDefaultColor);
        this.colorLabelActiveFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcFixedTileLabelActiveColor);
        this.colorLabelInactiveFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcFixedTileLabelInactiveColor);
        this.colorLabelUnavailableFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcFixedTileLabelUnavailableColor);
        getRipple().setColor(ColorStateList.valueOf(Utils.getColorAttrDefaultColor(getContext(), R$attr.colorControlHighlight)));
        setAllColors(getBackgroundColorForState(this.mQsTileState), getLabelColorForState(this.mQsTileState), getSecondaryLabelColorForState(this.mQsTileState), getChevronColorForState(this.mQsTileState));
    }
}
