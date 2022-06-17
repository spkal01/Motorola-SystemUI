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

/* renamed from: com.android.systemui.qs.tileimpl.QSTileViewPrcImpl */
/* compiled from: QSTileViewPrcImpl.kt */
public class QSTileViewPrcImpl extends QSTileViewImpl {
    private final boolean DEBUG = (!Build.IS_USER);
    @NotNull
    private final QSIconView _iconPrc;
    private final boolean collapsed;
    private int colorActiveFixed;
    private int colorInactiveFixed;
    private int colorLabelActiveUnFixed;
    private int colorLabelInactiveUnFixed;
    private int colorLabelUnavailableUnFixed;
    private int colorUnavailableFixed;
    @NotNull
    private final QSTileViewPrcImpl$mConfigListener$1 mConfigListener;
    @NotNull
    private ConfigurationController mConfigurationController;
    private boolean mIsBigTileTypePrc;
    private int mQsTileState;

    public boolean isSkipSetBackground() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public QSTileViewPrcImpl(@NotNull Context context, @NotNull QSIconView qSIconView, boolean z) {
        super(context, qSIconView, z);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(qSIconView, "_iconPrc");
        this._iconPrc = qSIconView;
        this.collapsed = z;
        this.colorActiveFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileActiveColorForUnfiexed);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(context, R$attr.prcQSTileInactiveColorForUnfiexed);
        this.colorInactiveFixed = colorAttrDefaultColor;
        this.colorUnavailableFixed = colorAttrDefaultColor;
        this.colorLabelActiveUnFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcUnFixedTileLabelActiveColor);
        this.colorLabelInactiveUnFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcUnFixedTileLabelInactiveColor);
        this.colorLabelUnavailableUnFixed = Utils.getColorAttrDefaultColor(context, R$attr.prcUnFixedTileLabelUnavailableColor);
        this.mQsTileState = 2;
        this.mConfigListener = new QSTileViewPrcImpl$mConfigListener$1(this);
        getSideView().setVisibility(8);
        Object obj = Dependency.get(ConfigurationController.class);
        Intrinsics.checkNotNullExpressionValue(obj, "get<ConfigurationController>(ConfigurationController::class.java)");
        this.mConfigurationController = (ConfigurationController) obj;
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

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        updateThemeColor();
    }

    public void updateLayout() {
        setId(LinearLayout.generateViewId());
        setOrientation(1);
        setGravity(17);
        setImportantForAccessibility(1);
        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
        setBackground(createTileBackground());
        setColor(getBackgroundColorForState(2));
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_horizontal_padding);
        setPaddingRelative(dimensionPixelSize, 0, dimensionPixelSize, 0);
    }

    public int getIconSize() {
        return getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_icon_size);
    }

    @NotNull
    public RippleDrawable getRippleDrawable() {
        Drawable drawable = this.mContext.getDrawable(R$drawable.prc_qs_tile_background);
        Objects.requireNonNull(drawable, "null cannot be cast to non-null type android.graphics.drawable.RippleDrawable");
        return (RippleDrawable) drawable;
    }

    public void updateResources() {
        FontSizeUtils.updateFontSize(getLabel(), R$dimen.prc_qs_tile_text_size);
        int iconSize = getIconSize();
        ViewGroup.LayoutParams layoutParams = get_icon().getLayoutParams();
        layoutParams.height = iconSize;
        layoutParams.width = iconSize;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_horizontal_padding);
        setPaddingRelative(dimensionPixelSize, 0, dimensionPixelSize, 0);
    }

    public void createAndAddLabels() {
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.zz_moto_prc_qs_tile_label, this, false);
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
            Log.i("QSTileViewPrcImpl", "QSTileLog handleStateChanged = " + state.spec + " label = " + state.label + " state = " + state.state);
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
        Log.e("QSTileViewPrcImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    public int getLabelColorForState(int i) {
        if (i == 0) {
            return this.colorLabelUnavailableUnFixed;
        }
        if (i == 1) {
            return this.colorLabelInactiveUnFixed;
        }
        if (i == 2) {
            return this.colorLabelActiveUnFixed;
        }
        Log.e("QSTileViewPrcImpl", Intrinsics.stringPlus("Invalid state ", Integer.valueOf(i)));
        return 0;
    }

    public void updateBigTypePrc(boolean z) {
        this.mIsBigTileTypePrc = z;
        if (z) {
            updateBigTileTypeLayout();
        } else {
            updateNormalTileTypeLayout();
        }
    }

    private final void updateBigTileTypeLayout() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_lable_margin_top);
        setOrientation(0);
        ViewGroup.LayoutParams layoutParams = getLabelContainer().getLayoutParams();
        Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        marginLayoutParams.setMarginStart(dimensionPixelSize);
        marginLayoutParams.topMargin = 0;
        marginLayoutParams.width = -2;
        getLabel().getLayoutParams().width = -2;
    }

    private final void updateNormalTileTypeLayout() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.zz_moto_qs_prc_tile_lable_margin_top);
        setOrientation(1);
        ViewGroup.LayoutParams layoutParams = getLabelContainer().getLayoutParams();
        Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
        marginLayoutParams.setMarginStart(0);
        marginLayoutParams.topMargin = dimensionPixelSize;
        marginLayoutParams.width = -1;
        getLabel().getLayoutParams().width = -1;
    }

    public final void updateThemeColor() {
        cancelSingleAnimator();
        this.colorActiveFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcQSTileActiveColorForUnfiexed);
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcQSTileInactiveColorForUnfiexed);
        this.colorInactiveFixed = colorAttrDefaultColor;
        this.colorUnavailableFixed = colorAttrDefaultColor;
        this.colorLabelActiveUnFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcUnFixedTileLabelActiveColor);
        this.colorLabelInactiveUnFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcUnFixedTileLabelInactiveColor);
        this.colorLabelUnavailableUnFixed = Utils.getColorAttrDefaultColor(getContext(), R$attr.prcUnFixedTileLabelUnavailableColor);
        getRipple().setColor(ColorStateList.valueOf(Utils.getColorAttrDefaultColor(getContext(), R$attr.colorControlHighlight)));
        setAllColors(getBackgroundColorForState(this.mQsTileState), getLabelColorForState(this.mQsTileState), getSecondaryLabelColorForState(this.mQsTileState), getChevronColorForState(this.mQsTileState));
    }
}
