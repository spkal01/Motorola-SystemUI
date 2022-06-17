package com.android.systemui.p006qs.carrier;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.R$color;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import java.util.Objects;

/* renamed from: com.android.systemui.qs.carrier.QSCarrier */
public class QSCarrier extends LinearLayout {
    protected View mAttRat;
    private TextView mCarrierText;
    private ImageView mInOutMoto;
    private boolean mIsShowAttRat;
    private boolean mIsShowVzwRat;
    private StatusBarSignalPolicy.MobileIconState mLastSignalState;
    private SignalDrawable mMobileDrawable;
    private View mMobileGroup;
    private ImageView mMobileRoaming;
    private ImageView mMobileSignal;
    private ImageView mMobileType;
    private boolean mProviderModelInitialized = false;
    protected View mRetailRat;
    private View mSpacer;
    protected View mVzwRat;

    public QSCarrier(Context context) {
        super(context);
    }

    public QSCarrier(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public QSCarrier(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public QSCarrier(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mMobileGroup = findViewById(R$id.mobile_combo);
        this.mMobileRoaming = (ImageView) findViewById(R$id.mobile_roaming);
        this.mMobileSignal = (ImageView) findViewById(R$id.mobile_signal);
        this.mMobileType = (ImageView) findViewById(R$id.mobile_type);
        this.mCarrierText = (TextView) findViewById(R$id.qs_carrier_text);
        this.mSpacer = findViewById(R$id.spacer);
        this.mRetailRat = findViewById(R$id.retail_rat);
        this.mAttRat = findViewById(R$id.att_rat);
        this.mVzwRat = findViewById(R$id.vzw_rat);
        this.mInOutMoto = (ImageView) findViewById(R$id.mobile_inout);
        SignalDrawable signalDrawable = new SignalDrawable(getContext());
        this.mMobileDrawable = signalDrawable;
        this.mMobileSignal.setImageDrawable(signalDrawable);
    }

    public boolean updateState(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        if (Objects.equals(mobileIconState, this.mLastSignalState)) {
            return false;
        }
        this.mLastSignalState = mobileIconState.copy();
        this.mMobileGroup.setVisibility(mobileIconState.visible ? 0 : 8);
        this.mSpacer.setVisibility(0);
        if (mobileIconState.visible) {
            this.mMobileRoaming.setVisibility(mobileIconState.roaming ? 0 : 8);
            ColorStateList colorAttr = Utils.getColorAttr(this.mContext, 16842806);
            this.mMobileRoaming.setImageTintList(colorAttr);
            this.mMobileSignal.setImageTintList(colorAttr);
            if (mobileIconState.showSeparatedSignalBars) {
                this.mMobileSignal.setImageResource(mobileIconState.strengthId);
                this.mMobileSignal.setImageTintList(colorAttr);
            } else {
                this.mMobileSignal.setImageTintList(colorAttr);
                this.mMobileSignal.setImageLevel(mobileIconState.strengthId);
                this.mMobileDrawable.setLevel(mobileIconState.strengthId);
            }
            StringBuilder sb = new StringBuilder();
            String str = mobileIconState.contentDescription;
            if (str != null) {
                sb.append(str);
                sb.append(", ");
            }
            if (mobileIconState.roaming) {
                sb.append(this.mContext.getString(R$string.data_connection_roaming));
                sb.append(", ");
            }
            boolean z = this.mIsShowAttRat;
            boolean z2 = mobileIconState.isShowAttRat;
            if (z != z2) {
                reloadRatView(false, z2);
                this.mIsShowAttRat = mobileIconState.isShowAttRat;
            }
            boolean z3 = this.mIsShowVzwRat;
            boolean z4 = mobileIconState.isShowVzwRat;
            if (z3 != z4) {
                reloadRatView(true, z4);
                this.mIsShowVzwRat = mobileIconState.isShowVzwRat;
            }
            if (mobileIconState.typeId > 0) {
                this.mMobileType.setContentDescription(mobileIconState.typeContentDescription);
                this.mMobileType.setImageTintList(colorAttr);
                this.mMobileType.setImageResource(mobileIconState.typeId);
                this.mMobileType.setVisibility(0);
            } else {
                this.mMobileType.setVisibility(8);
            }
            CharSequence charSequence = mobileIconState.typeContentDescription;
            if (charSequence != null && hasValidTypeContentDescription(charSequence.toString())) {
                sb.append(mobileIconState.typeContentDescription);
            }
            this.mMobileSignal.setContentDescription(sb);
            updateOtherIconVisibilityState(mobileIconState, colorAttr);
            updateForColorChanged();
        }
        return true;
    }

    private void reloadRatView(boolean z, boolean z2) {
        this.mMobileType.setVisibility(8);
        this.mInOutMoto.setVisibility(8);
        this.mRetailRat.setVisibility(8);
        this.mAttRat.setVisibility(8);
        this.mVzwRat.setVisibility(8);
        if (!z2) {
            this.mMobileType = (ImageView) findViewById(R$id.mobile_type);
            this.mInOutMoto = (ImageView) findViewById(R$id.mobile_inout);
            this.mRetailRat.setVisibility(0);
        } else if (z) {
            this.mMobileType = (ImageView) findViewById(R$id.mobile_type_vzw);
            this.mInOutMoto = (ImageView) findViewById(R$id.mobile_inout_vzw);
            this.mVzwRat.setVisibility(0);
        } else {
            this.mMobileType = (ImageView) findViewById(R$id.mobile_type_att);
            this.mInOutMoto = (ImageView) findViewById(R$id.mobile_inout_att);
            this.mAttRat.setVisibility(0);
        }
    }

    private void updateOtherIconVisibilityState(StatusBarSignalPolicy.MobileIconState mobileIconState, ColorStateList colorStateList) {
        updateIconState(this.mInOutMoto, mobileIconState.mMobileDataActivityIconId, colorStateList);
        updateIconState(this.mMobileRoaming, mobileIconState.mMobileRoamingIconId, colorStateList, mobileIconState.mMobileRoamingIconContentDescription);
    }

    private void updateIconState(ImageView imageView, int i, ColorStateList colorStateList) {
        imageView.getDrawable();
        if (i != 0) {
            imageView.setImageResource(i);
            imageView.setImageTintList(colorStateList);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    private void updateIconState(ImageView imageView, int i, ColorStateList colorStateList, String str) {
        imageView.getDrawable();
        if (i != 0) {
            imageView.setImageResource(i);
            imageView.setImageTintList(colorStateList);
            imageView.setContentDescription(str);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    private boolean hasValidTypeContentDescription(String str) {
        return TextUtils.equals(str, this.mContext.getString(R$string.data_connection_no_internet)) || TextUtils.equals(str, this.mContext.getString(com.android.settingslib.R$string.cell_data_off_content_description)) || TextUtils.equals(str, this.mContext.getString(com.android.settingslib.R$string.not_default_data_content_description));
    }

    /* access modifiers changed from: package-private */
    public View getRSSIView() {
        return this.mMobileGroup;
    }

    public void setCarrierText(CharSequence charSequence) {
        this.mCarrierText.setText(charSequence);
    }

    public boolean isCarrierTextVisible() {
        if (getVisibility() == 0 && this.mCarrierText.getVisibility() == 0 && !TextUtils.isEmpty(this.mCarrierText.getText())) {
            return true;
        }
        return false;
    }

    private void updateForColorChanged() {
        if (MotoFeature.getInstance(getContext()).isCustomPanelView()) {
            int color = getResources().getColor(R$color.prcQSPanelCarrierGroup);
            this.mCarrierText.setTextColor(color);
            this.mMobileSignal.setColorFilter(color);
            this.mMobileType.setColorFilter(color);
            this.mMobileRoaming.setColorFilter(color);
            this.mInOutMoto.setColorFilter(color);
            ((ImageView) findViewById(R$id.error_sim_vzw)).setColorFilter(color);
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateForColorChanged();
    }
}
