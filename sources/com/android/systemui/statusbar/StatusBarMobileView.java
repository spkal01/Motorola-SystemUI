package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.DualToneHandler;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;

public class StatusBarMobileView extends FrameLayout implements DarkIconDispatcher.DarkReceiver, StatusIconDisplayable {
    protected View mAttRat;
    private ColorStateList mColorList;
    private float mDarkIntensity;
    protected StatusBarIconView mDotView;
    private DualToneHandler mDualToneHandler;
    private ImageView mErrorSim;
    private boolean mForceHidden;
    private ImageView mInOutMoto;
    private ImageView mMobile;
    private SignalDrawable mMobileDrawable;
    protected LinearLayout mMobileGroup;
    private ImageView mMobileRoaming;
    private ImageView mMobileType;
    private boolean mProviderModel;
    protected View mRetailRat;
    private String mSlot;
    protected StatusBarSignalPolicy.MobileIconState mState;
    private int mVisibleState = 2;
    protected View mVzwRat;

    public static StatusBarMobileView fromContext(Context context, String str, boolean z) {
        StatusBarMobileView statusBarMobileView = (StatusBarMobileView) LayoutInflater.from(context).inflate(R$layout.status_bar_mobile_signal_group_moto, (ViewGroup) null);
        statusBarMobileView.setSlot(str);
        statusBarMobileView.init(z);
        statusBarMobileView.setVisibleState(0);
        return statusBarMobileView;
    }

    public StatusBarMobileView(Context context) {
        super(context);
    }

    public StatusBarMobileView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StatusBarMobileView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public StatusBarMobileView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void getDrawingRect(Rect rect) {
        super.getDrawingRect(rect);
        float translationX = getTranslationX();
        float translationY = getTranslationY();
        rect.left = (int) (((float) rect.left) + translationX);
        rect.right = (int) (((float) rect.right) + translationX);
        rect.top = (int) (((float) rect.top) + translationY);
        rect.bottom = (int) (((float) rect.bottom) + translationY);
    }

    private void init(boolean z) {
        this.mProviderModel = z;
        this.mDualToneHandler = new DualToneHandler(getContext());
        this.mMobileGroup = (LinearLayout) findViewById(R$id.mobile_group);
        this.mMobile = (ImageView) findViewById(R$id.mobile_signal);
        this.mRetailRat = findViewById(R$id.retail_rat);
        this.mAttRat = findViewById(R$id.att_rat);
        this.mVzwRat = findViewById(R$id.vzw_rat);
        this.mMobileType = (ImageView) findViewById(R$id.mobile_type);
        this.mMobileRoaming = (ImageView) findViewById(R$id.mobile_roaming);
        this.mInOutMoto = (ImageView) findViewById(R$id.mobile_inout);
        int i = R$id.error_sim_vzw;
        this.mErrorSim = (ImageView) findViewById(i);
        SignalDrawable signalDrawable = new SignalDrawable(getContext());
        this.mMobileDrawable = signalDrawable;
        this.mMobile.setImageDrawable(signalDrawable);
        this.mErrorSim = (ImageView) findViewById(i);
        initDotView();
    }

    /* access modifiers changed from: protected */
    public void initDotView() {
        StatusBarIconView statusBarIconView = new StatusBarIconView(this.mContext, this.mSlot, (StatusBarNotification) null);
        this.mDotView = statusBarIconView;
        statusBarIconView.setVisibleState(1);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_size);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize);
        layoutParams.gravity = 8388629;
        addView(this.mDotView, layoutParams);
    }

    public void applyMobileState(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        boolean z = true;
        if (mobileIconState == null) {
            if (getVisibility() == 8) {
                z = false;
            }
            setVisibility(8);
            this.mState = null;
        } else {
            StatusBarSignalPolicy.MobileIconState mobileIconState2 = this.mState;
            if (mobileIconState2 == null) {
                this.mState = mobileIconState.copy();
                initViewState();
            } else {
                z = !mobileIconState2.equals(mobileIconState) ? updateState(mobileIconState.copy()) : false;
            }
        }
        if (z) {
            requestLayout();
        }
    }

    private void reloadRatView(boolean z, boolean z2) {
        ColorStateList imageTintList = this.mMobileType.getImageTintList();
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
        this.mMobileType.setImageTintList(imageTintList);
        this.mInOutMoto.setImageTintList(imageTintList);
    }

    private void initViewState() {
        StatusBarSignalPolicy.MobileIconState mobileIconState = this.mState;
        boolean z = mobileIconState.isShowAttRat;
        int i = 0;
        if (z) {
            reloadRatView(false, z);
        } else {
            boolean z2 = mobileIconState.isShowVzwRat;
            if (z2) {
                reloadRatView(true, z2);
            }
        }
        setContentDescription(this.mState.contentDescription);
        if (!this.mState.visible || this.mForceHidden) {
            this.mMobileGroup.setVisibility(8);
            this.mVisibleState = 2;
        } else {
            this.mMobileGroup.setVisibility(0);
            this.mVisibleState = 0;
        }
        StatusBarSignalPolicy.MobileIconState mobileIconState2 = this.mState;
        if (mobileIconState2.showSeparatedSignalBars) {
            this.mMobile.setImageResource(mobileIconState2.strengthId);
        } else {
            this.mMobileDrawable.setLevel(mobileIconState2.strengthId);
        }
        this.mErrorSim.setVisibility(this.mState.isSimError ? 0 : 8);
        ImageView imageView = this.mMobile;
        if (this.mState.isSimError) {
            i = 8;
        }
        imageView.setVisibility(i);
        StatusBarSignalPolicy.MobileIconState mobileIconState3 = this.mState;
        if (mobileIconState3.typeId > 0) {
            this.mMobile.setContentDescription(getMobileSignalContentDescription(mobileIconState3));
            this.mMobileType.setContentDescription(this.mState.typeContentDescription);
            ImageView imageView2 = this.mMobileType;
            StatusBarSignalPolicy.MobileIconState mobileIconState4 = this.mState;
            updateIconState(imageView2, mobileIconState4.typeId, mobileIconState4.subContext);
        } else {
            this.mMobileType.setVisibility(8);
        }
        updateOtherIconVisibilityState();
    }

    private boolean updateState(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        boolean z;
        setContentDescription(mobileIconState.contentDescription);
        boolean z2 = false;
        int i = (!mobileIconState.visible || this.mForceHidden) ? 8 : 0;
        if (i == this.mMobileGroup.getVisibility() || this.mVisibleState == 1) {
            z = false;
        } else {
            this.mMobileGroup.setVisibility(i);
            this.mVisibleState = i == 0 ? 0 : 2;
            z = true;
        }
        boolean z3 = this.mState.isShowAttRat;
        boolean z4 = mobileIconState.isShowAttRat;
        if (z3 != z4) {
            reloadRatView(false, z4);
        }
        boolean z5 = this.mState.isShowVzwRat;
        boolean z6 = mobileIconState.isShowVzwRat;
        if (z5 != z6) {
            reloadRatView(true, z6);
        }
        boolean z7 = this.mState.showSeparatedSignalBars;
        boolean z8 = mobileIconState.showSeparatedSignalBars;
        if (z7 != z8) {
            if (z8) {
                this.mMobile.setImageTintList(this.mColorList);
            } else {
                this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mDarkIntensity)));
                this.mMobile.setImageDrawable(this.mMobileDrawable);
                this.mMobile.setAlpha(1.0f);
            }
        }
        int i2 = this.mState.strengthId;
        int i3 = mobileIconState.strengthId;
        if (i2 != i3) {
            if (mobileIconState.showSeparatedSignalBars) {
                this.mMobile.setImageResource(i3);
            } else {
                this.mMobileDrawable.setLevel(i3);
            }
        }
        this.mErrorSim.setVisibility(mobileIconState.isSimError ? 0 : 8);
        this.mMobile.setVisibility(mobileIconState.isSimError ? 8 : 0);
        StatusBarSignalPolicy.MobileIconState mobileIconState2 = this.mState;
        int i4 = mobileIconState2.typeId;
        int i5 = mobileIconState.typeId;
        if (!(i4 == i5 && mobileIconState2.isShowAttRat == mobileIconState.isShowAttRat && mobileIconState2.isShowVzwRat == mobileIconState.isShowVzwRat)) {
            z |= i5 == 0 || i4 == 0;
            if (i5 != 0) {
                this.mMobileType.setContentDescription(mobileIconState.typeContentDescription);
                updateIconState(this.mMobileType, mobileIconState.typeId, mobileIconState.subContext);
            } else {
                this.mMobileType.setVisibility(8);
            }
        }
        this.mMobile.setContentDescription(getMobileSignalContentDescription(mobileIconState));
        boolean z9 = mobileIconState.roaming;
        StatusBarSignalPolicy.MobileIconState mobileIconState3 = this.mState;
        if (!(z9 == mobileIconState3.roaming && mobileIconState.activityIn == mobileIconState3.activityIn && mobileIconState.activityOut == mobileIconState3.activityOut && mobileIconState.showTriangle == mobileIconState3.showTriangle)) {
            z2 = true;
        }
        boolean z10 = z | z2;
        this.mState = mobileIconState;
        updateOtherIconVisibilityState();
        return z10;
    }

    /* access modifiers changed from: protected */
    public StringBuilder getMobileSignalContentDescription(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        StringBuilder sb = new StringBuilder();
        String str = mobileIconState.contentDescription;
        if (str != null) {
            sb.append(str);
        }
        if (mobileIconState.roaming) {
            sb.append(", ");
            sb.append(this.mContext.getString(R$string.data_connection_roaming));
        }
        CharSequence charSequence = mobileIconState.typeContentDescription;
        if (charSequence != null && hasValidTypeContentDescription(charSequence.toString())) {
            sb.append(", ");
            sb.append(mobileIconState.typeContentDescription);
        }
        return sb;
    }

    private boolean hasValidTypeContentDescription(String str) {
        return TextUtils.equals(str, this.mContext.getString(R$string.data_connection_no_internet)) || TextUtils.equals(str, this.mContext.getString(R$string.cell_data_off_content_description)) || TextUtils.equals(str, this.mContext.getString(R$string.not_default_data_content_description));
    }

    private void updateOtherIconVisibilityState() {
        updateIconState(this.mInOutMoto, this.mState.mMobileDataActivityIconId);
        ImageView imageView = this.mMobileRoaming;
        StatusBarSignalPolicy.MobileIconState mobileIconState = this.mState;
        updateIconState(imageView, mobileIconState.mMobileRoamingIconId, mobileIconState.mMobileRoamingIconContentDescription);
    }

    private void updateIconState(ImageView imageView, int i) {
        if (i != 0) {
            imageView.setImageResource(i);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    private void updateIconState(ImageView imageView, int i, Context context) {
        if (i != 0) {
            if (context != null) {
                imageView.setImageDrawable(context.getDrawable(i));
            } else {
                imageView.setImageResource(i);
            }
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    private void updateIconState(ImageView imageView, int i, String str) {
        if (i != 0) {
            imageView.setImageResource(i);
            imageView.setContentDescription(str);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        this.mDarkIntensity = DarkIconDispatcher.getDarkIntensity(rect, this, f);
        int tint = DarkIconDispatcher.getTint(rect, this, i);
        ColorStateList valueOf = ColorStateList.valueOf(tint);
        this.mColorList = valueOf;
        if (this.mState.showSeparatedSignalBars) {
            this.mMobile.setImageTintList(valueOf);
        } else {
            this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mDarkIntensity)));
        }
        this.mInOutMoto.setImageTintList(valueOf);
        this.mMobileType.setImageTintList(valueOf);
        this.mMobileRoaming.setImageTintList(valueOf);
        this.mDotView.setDecorColor(tint);
        this.mDotView.setIconColor(tint, false);
        this.mErrorSim.setImageTintList(valueOf);
    }

    public String getSlot() {
        return this.mSlot;
    }

    public void setSlot(String str) {
        this.mSlot = str;
    }

    public void setStaticDrawableColor(int i) {
        ColorStateList valueOf = ColorStateList.valueOf(i);
        this.mColorList = valueOf;
        this.mDarkIntensity = i == -1 ? 0.0f : 1.0f;
        if (this.mState.showSeparatedSignalBars) {
            this.mMobile.setImageTintList(valueOf);
        } else {
            this.mMobileDrawable.setTintList(valueOf);
        }
        this.mInOutMoto.setImageTintList(valueOf);
        this.mMobileType.setImageTintList(valueOf);
        this.mMobileRoaming.setImageTintList(valueOf);
        this.mDotView.setDecorColor(i);
        this.mErrorSim.setImageTintList(valueOf);
    }

    public void setDecorColor(int i) {
        this.mDotView.setDecorColor(i);
    }

    public boolean isIconVisible() {
        return this.mState.visible && !this.mForceHidden;
    }

    public void setVisibleState(int i, boolean z) {
        if (i != this.mVisibleState) {
            this.mVisibleState = i;
            if (i == 0) {
                this.mMobileGroup.setVisibility(0);
                this.mDotView.setVisibility(8);
            } else if (i != 1) {
                this.mMobileGroup.setVisibility(8);
                this.mDotView.setVisibility(8);
            } else {
                this.mMobileGroup.setVisibility(4);
                this.mDotView.setVisibility(0);
            }
        }
    }

    public int getVisibleState() {
        return this.mVisibleState;
    }

    @VisibleForTesting
    public StatusBarSignalPolicy.MobileIconState getState() {
        return this.mState;
    }

    public void setState(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        this.mState = mobileIconState;
    }

    public String toString() {
        return "StatusBarMobileView(slot=" + this.mSlot + " state=" + this.mState + ")";
    }
}
