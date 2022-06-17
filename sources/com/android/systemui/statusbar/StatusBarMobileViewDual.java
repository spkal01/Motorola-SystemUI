package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.DualToneHandler;
import com.android.systemui.R$bool;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;

public class StatusBarMobileViewDual extends StatusBarMobileView {
    private ColorStateList mColorStateList;
    private float mDarkIntensity;
    private DualToneHandler mDualToneHandler;
    private ImageView mInOutMoto1;
    private ImageView mInOutMoto2;
    private ImageView mMobile1;
    private ImageView mMobile2;
    private SignalDrawable mMobileDrawable1;
    private SignalDrawable mMobileDrawable2;
    private View mMobileGroupSlot1;
    private View mMobileGroupSlot2;
    private ImageView mMobileRoamingView1;
    private ImageView mMobileRoamingView2;
    private ImageView mMobileType1;
    private ImageView mMobileType2;
    private View mRetailRat1;
    private View mRetailRat2;
    private boolean mShowDualRat;
    private View mVzwRat1;
    private View mVzwRat2;

    public static StatusBarMobileView fromContext(Context context) {
        StatusBarMobileViewDual statusBarMobileViewDual = (StatusBarMobileViewDual) LayoutInflater.from(context).inflate(R$layout.status_bar_mobile_signal_group_dual, (ViewGroup) null);
        statusBarMobileViewDual.init();
        statusBarMobileViewDual.setVisibleState(0);
        return statusBarMobileViewDual;
    }

    public StatusBarMobileViewDual(Context context) {
        super(context);
    }

    public StatusBarMobileViewDual(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StatusBarMobileViewDual(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mMobileGroup = (LinearLayout) findViewById(R$id.mobile_group);
        this.mMobileGroupSlot1 = findViewById(R$id.mobile_group_slot1);
        this.mMobileGroupSlot2 = findViewById(R$id.mobile_group_slot2);
        this.mMobileRoamingView1 = (ImageView) findViewById(R$id.mobile_roaming_slot1);
        this.mMobileRoamingView2 = (ImageView) findViewById(R$id.mobile_roaming_slot2);
        this.mMobile1 = (ImageView) findViewById(R$id.mobile_signal_slot1);
        SignalDrawable signalDrawable = new SignalDrawable(getContext());
        this.mMobileDrawable1 = signalDrawable;
        signalDrawable.setDoubleSignalFlag(true);
        this.mMobile1.setImageDrawable(this.mMobileDrawable1);
        this.mMobile2 = (ImageView) findViewById(R$id.mobile_signal_slot2);
        SignalDrawable signalDrawable2 = new SignalDrawable(getContext());
        this.mMobileDrawable2 = signalDrawable2;
        signalDrawable2.setDoubleSignalFlag(true);
        this.mMobile2.setImageDrawable(this.mMobileDrawable2);
        this.mMobileType1 = (ImageView) findViewById(R$id.mobile_type_slot1);
        this.mMobileType2 = (ImageView) findViewById(R$id.mobile_type_slot2);
        this.mInOutMoto1 = (ImageView) findViewById(R$id.mobile_inout_slot1);
        this.mInOutMoto2 = (ImageView) findViewById(R$id.mobile_inout_slot2);
        this.mRetailRat1 = findViewById(R$id.rat_slot1);
        this.mRetailRat2 = findViewById(R$id.rat_slot2);
        this.mVzwRat1 = findViewById(R$id.rat_slot1_vzw);
        this.mVzwRat2 = findViewById(R$id.rat_slot2_vzw);
        this.mDualToneHandler = new DualToneHandler(getContext());
        this.mShowDualRat = getContext().getResources().getBoolean(R$bool.zz_moto_config_show_dual_rat);
        initDotView();
    }

    public void applyMobileState(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        if (mobileIconState == null) {
            setVisibility(8);
            setState((StatusBarSignalPolicy.MobileIconState) null);
        } else if (getState() == null) {
            setState(mobileIconState.copy());
            initViewState();
        } else if (!getState().equals(mobileIconState)) {
            updateState(mobileIconState.copy());
        }
    }

    private void reloadRatView(boolean z) {
        ColorStateList imageTintList = this.mMobileType1.getImageTintList();
        this.mMobileType1.setVisibility(8);
        this.mMobileType2.setVisibility(8);
        this.mVzwRat1.setVisibility(8);
        this.mVzwRat2.setVisibility(8);
        this.mRetailRat1.setVisibility(8);
        this.mRetailRat2.setVisibility(8);
        if (z) {
            this.mMobileType1 = (ImageView) findViewById(R$id.mobile_type_slot1_vzw);
            this.mMobileType2 = (ImageView) findViewById(R$id.mobile_type_slot2_vzw);
            this.mVzwRat1.setVisibility(0);
            this.mVzwRat2.setVisibility(0);
        } else {
            this.mMobileType1 = (ImageView) findViewById(R$id.mobile_type_slot1);
            this.mMobileType2 = (ImageView) findViewById(R$id.mobile_type_slot2);
            this.mRetailRat1.setVisibility(0);
            this.mRetailRat2.setVisibility(0);
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mMobileGroupSlot1.getLayoutParams();
        int i = 8388611;
        layoutParams.gravity = (z ? 8388611 : 8388613) | 80;
        this.mMobileGroupSlot1.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mMobileGroupSlot2.getLayoutParams();
        if (!z) {
            i = 8388613;
        }
        layoutParams2.gravity = i | 80;
        this.mMobileGroupSlot2.setLayoutParams(layoutParams2);
        this.mMobileType1.setImageTintList(imageTintList);
        this.mMobileType2.setImageTintList(imageTintList);
    }

    private void initViewState() {
        StatusBarSignalPolicy.MobileIconState state = getState();
        StatusBarSignalPolicy.MobileIconState mobileIconState = state.mNext;
        if (state.isShowVzwRat) {
            reloadRatView(true);
        } else {
            reloadRatView(false);
        }
        setContentDescription(state.contentDescription);
        if (state.visible) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
        if (state.showSeparatedSignalBars) {
            this.mMobile1.setImageResource(state.strengthId);
            this.mMobile1.setImageTintList(this.mColorStateList);
        } else {
            this.mMobileDrawable1.setLevel(state.strengthId);
        }
        if (state.typeId > 0) {
            this.mMobile1.setContentDescription(getMobileSignalContentDescription(state));
            this.mMobileType1.setContentDescription(state.typeContentDescription);
        }
        updateIconState(state, this.mMobileType1, state.typeId);
        updateActivityIconState(state, this.mInOutMoto1, state.mMobileDataActivityIconId, mobileIconState.mMobileDataActivityIconId);
        updateIconState(state, this.mMobileRoamingView1, state.mMobileRoamingIconId, state.mMobileRoamingIconContentDescription);
        if (mobileIconState.showSeparatedSignalBars) {
            this.mMobile2.setImageResource(mobileIconState.strengthId);
            this.mMobile2.setImageTintList(this.mColorStateList);
        } else {
            this.mMobileDrawable2.setLevel(mobileIconState.strengthId);
        }
        if (mobileIconState.typeId > 0) {
            this.mMobile2.setContentDescription(getMobileSignalContentDescription(mobileIconState));
            this.mMobileType2.setContentDescription(mobileIconState.typeContentDescription);
        }
        updateIconState(mobileIconState, this.mMobileType2, mobileIconState.typeId);
        updateActivityIconState(state, this.mInOutMoto2, mobileIconState.mMobileDataActivityIconId, state.mMobileDataActivityIconId);
        updateIconState(mobileIconState, this.mMobileRoamingView2, mobileIconState.mMobileRoamingIconId, mobileIconState.mMobileRoamingIconContentDescription);
    }

    private void updateState(StatusBarSignalPolicy.MobileIconState mobileIconState) {
        StatusBarSignalPolicy.MobileIconState state = getState();
        StatusBarSignalPolicy.MobileIconState mobileIconState2 = state.mNext;
        boolean z = state.isShowVzwRat;
        boolean z2 = mobileIconState.isShowVzwRat;
        if (z != z2) {
            reloadRatView(z2);
        }
        setContentDescription(mobileIconState.contentDescription);
        boolean z3 = state.visible;
        boolean z4 = mobileIconState.visible;
        if (z3 != z4) {
            setVisibility(z4 ? 0 : 8);
        }
        boolean z5 = state.showSeparatedSignalBars;
        boolean z6 = mobileIconState.showSeparatedSignalBars;
        if (z5 != z6) {
            if (z6) {
                this.mMobile1.setImageTintList(this.mColorStateList);
            } else {
                this.mMobileDrawable1.setDarkIntensity(this.mDarkIntensity);
                this.mMobileDrawable1.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mDarkIntensity)));
                this.mMobile1.setImageDrawable(this.mMobileDrawable1);
                this.mMobile1.setAlpha(1.0f);
            }
        }
        int i = state.strengthId;
        int i2 = mobileIconState.strengthId;
        if (i != i2) {
            if (mobileIconState.showSeparatedSignalBars) {
                this.mMobile1.setImageResource(i2);
            } else {
                this.mMobileDrawable1.setLevel(i2);
            }
        }
        if (!(state.typeId == mobileIconState.typeId && state.isShowVzwRat == mobileIconState.isShowVzwRat)) {
            this.mMobileType1.setContentDescription(mobileIconState.typeContentDescription);
            updateIconState(mobileIconState, this.mMobileType1, mobileIconState.typeId);
        }
        int i3 = state.mMobileDataActivityIconId;
        int i4 = mobileIconState.mMobileDataActivityIconId;
        if (i3 != i4 || (this.mShowDualRat && mobileIconState2.mMobileDataActivityIconId != mobileIconState.mNext.mMobileDataActivityIconId)) {
            updateActivityIconState(mobileIconState, this.mInOutMoto1, i4, mobileIconState.mNext.mMobileDataActivityIconId);
        }
        int i5 = state.mMobileRoamingIconId;
        int i6 = mobileIconState.mMobileRoamingIconId;
        if (i5 != i6) {
            updateIconState(mobileIconState, this.mMobileRoamingView1, i6, mobileIconState.mMobileRoamingIconContentDescription);
        }
        this.mMobile1.setContentDescription(getMobileSignalContentDescription(state));
        boolean z7 = mobileIconState2.showSeparatedSignalBars;
        boolean z8 = mobileIconState.mNext.showSeparatedSignalBars;
        if (z7 != z8) {
            if (z8) {
                this.mMobile2.setImageTintList(this.mColorStateList);
            } else {
                this.mMobileDrawable2.setDarkIntensity(this.mDarkIntensity);
                this.mMobileDrawable2.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mDarkIntensity)));
                this.mMobile2.setImageDrawable(this.mMobileDrawable2);
                this.mMobile2.setAlpha(1.0f);
            }
        }
        int i7 = mobileIconState2.strengthId;
        StatusBarSignalPolicy.MobileIconState mobileIconState3 = mobileIconState.mNext;
        int i8 = mobileIconState3.strengthId;
        if (i7 != i8) {
            if (mobileIconState3.showSeparatedSignalBars) {
                this.mMobile2.setImageResource(i8);
            } else {
                this.mMobileDrawable2.setLevel(i8);
            }
        }
        int i9 = mobileIconState2.typeId;
        StatusBarSignalPolicy.MobileIconState mobileIconState4 = mobileIconState.mNext;
        if (!(i9 == mobileIconState4.typeId && mobileIconState2.isShowVzwRat == mobileIconState4.isShowVzwRat)) {
            this.mMobileType2.setContentDescription(mobileIconState4.typeContentDescription);
            updateIconState(mobileIconState, this.mMobileType2, mobileIconState.mNext.typeId);
        }
        int i10 = mobileIconState2.mMobileDataActivityIconId;
        int i11 = mobileIconState.mNext.mMobileDataActivityIconId;
        if (i10 != i11 || (this.mShowDualRat && state.mMobileDataActivityIconId != mobileIconState.mMobileDataActivityIconId)) {
            updateActivityIconState(mobileIconState, this.mInOutMoto2, i11, mobileIconState.mMobileDataActivityIconId);
        }
        int i12 = mobileIconState2.mMobileRoamingIconId;
        StatusBarSignalPolicy.MobileIconState mobileIconState5 = mobileIconState.mNext;
        int i13 = mobileIconState5.mMobileRoamingIconId;
        if (i12 != i13) {
            updateIconState(mobileIconState, this.mMobileRoamingView2, i13, mobileIconState5.mMobileRoamingIconContentDescription);
        }
        this.mMobile2.setContentDescription(getMobileSignalContentDescription(mobileIconState2));
        setState(mobileIconState);
    }

    private void updateIconState(StatusBarSignalPolicy.MobileIconState mobileIconState, ImageView imageView, int i) {
        Drawable drawable = imageView.getDrawable();
        if (i != 0) {
            if (drawable != null) {
                drawable.setAutoMirrored(mobileIconState.mMobileIsBidiDirectionEnabled);
            }
            imageView.setImageResource(i);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    private void updateActivityIconState(StatusBarSignalPolicy.MobileIconState mobileIconState, ImageView imageView, int i, int i2) {
        Drawable drawable = imageView.getDrawable();
        if (i != 0) {
            if (drawable != null) {
                drawable.setAutoMirrored(mobileIconState.mMobileIsBidiDirectionEnabled);
            }
            imageView.setImageResource(i);
            imageView.setVisibility(0);
        } else if (!this.mShowDualRat || i2 == 0) {
            imageView.setVisibility(8);
        } else {
            imageView.setImageResource(i2);
            imageView.setVisibility(4);
        }
    }

    private void updateIconState(StatusBarSignalPolicy.MobileIconState mobileIconState, ImageView imageView, int i, String str) {
        Drawable drawable = imageView.getDrawable();
        if (i != 0) {
            if (drawable != null) {
                drawable.setAutoMirrored(mobileIconState.mMobileIsBidiDirectionEnabled);
            }
            imageView.setImageResource(i);
            imageView.setContentDescription(str);
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        int tint = DarkIconDispatcher.getTint(rect, this, i);
        this.mDarkIntensity = DarkIconDispatcher.getDarkIntensity(rect, this, f);
        ColorStateList valueOf = ColorStateList.valueOf(tint);
        this.mColorStateList = valueOf;
        this.mMobileType1.setImageTintList(valueOf);
        this.mMobileRoamingView1.setImageTintList(valueOf);
        this.mInOutMoto1.setImageTintList(valueOf);
        this.mMobileType2.setImageTintList(valueOf);
        this.mMobileRoamingView2.setImageTintList(valueOf);
        this.mInOutMoto2.setImageTintList(valueOf);
        StatusBarSignalPolicy.MobileIconState mobileIconState = this.mState;
        if (mobileIconState.showSeparatedSignalBars || mobileIconState.mNext.showSeparatedSignalBars) {
            this.mMobile1.setImageTintList(valueOf);
            this.mMobile2.setImageTintList(valueOf);
        } else {
            this.mMobileDrawable1.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mDarkIntensity)));
            this.mMobileDrawable2.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mDarkIntensity)));
        }
        StatusBarIconView statusBarIconView = this.mDotView;
        if (statusBarIconView != null) {
            statusBarIconView.setDecorColor(tint);
            this.mDotView.setIconColor(tint, false);
        }
    }

    public void setStaticDrawableColor(int i) {
        ColorStateList valueOf = ColorStateList.valueOf(i);
        this.mColorStateList = valueOf;
        this.mDarkIntensity = i == -1 ? 0.0f : 1.0f;
        this.mMobileType1.setImageTintList(valueOf);
        this.mMobileRoamingView1.setImageTintList(valueOf);
        this.mInOutMoto1.setImageTintList(valueOf);
        this.mMobileType2.setImageTintList(valueOf);
        this.mMobileRoamingView2.setImageTintList(valueOf);
        this.mInOutMoto2.setImageTintList(valueOf);
        StatusBarSignalPolicy.MobileIconState mobileIconState = this.mState;
        if (mobileIconState.showSeparatedSignalBars || mobileIconState.mNext.showSeparatedSignalBars) {
            this.mMobile1.setImageTintList(valueOf);
            this.mMobile2.setImageTintList(valueOf);
        } else {
            this.mMobileDrawable1.setTintList(valueOf);
            this.mMobileDrawable2.setTintList(valueOf);
        }
        StatusBarIconView statusBarIconView = this.mDotView;
        if (statusBarIconView != null) {
            statusBarIconView.setDecorColor(i);
        }
    }
}
