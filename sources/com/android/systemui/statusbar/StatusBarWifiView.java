package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.moto.CarrierIcons;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;

public class StatusBarWifiView extends FrameLayout implements DarkIconDispatcher.DarkReceiver, StatusIconDisplayable {
    private View mAirplaneSpacer;
    private StatusBarIconView mDotView;
    private ImageView mIn;
    private View mInoutContainer;
    private int mLastWifiActivityIconId = -1;
    private ImageView mOut;
    private View mSignalSpacer;
    private String mSlot;
    private StatusBarSignalPolicy.WifiIconState mState;
    private int mVisibleState = -1;
    private ImageView mWifiActivityView;
    private ImageView mWifiEdge;
    private LinearLayout mWifiGroup;
    private ImageView mWifiIcon;

    public static StatusBarWifiView fromContext(Context context, String str) {
        StatusBarWifiView statusBarWifiView = (StatusBarWifiView) LayoutInflater.from(context).inflate(R$layout.status_bar_wifi_group, (ViewGroup) null);
        statusBarWifiView.setSlot(str);
        statusBarWifiView.init();
        statusBarWifiView.setVisibleState(0);
        return statusBarWifiView;
    }

    public StatusBarWifiView(Context context) {
        super(context);
    }

    public StatusBarWifiView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StatusBarWifiView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public StatusBarWifiView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setSlot(String str) {
        this.mSlot = str;
    }

    public void setStaticDrawableColor(int i) {
        ColorStateList valueOf = ColorStateList.valueOf(i);
        this.mWifiIcon.setImageTintList(valueOf);
        this.mIn.setImageTintList(valueOf);
        this.mOut.setImageTintList(valueOf);
        this.mWifiEdge.setImageTintList(valueOf);
        this.mDotView.setDecorColor(i);
    }

    public void setDecorColor(int i) {
        this.mDotView.setDecorColor(i);
    }

    public String getSlot() {
        return this.mSlot;
    }

    public boolean isIconVisible() {
        StatusBarSignalPolicy.WifiIconState wifiIconState = this.mState;
        return wifiIconState != null && wifiIconState.visible;
    }

    public void setVisibleState(int i, boolean z) {
        if (i != this.mVisibleState) {
            this.mVisibleState = i;
            if (i == 0) {
                this.mWifiGroup.setVisibility(0);
                this.mDotView.setVisibility(8);
            } else if (i != 1) {
                this.mWifiGroup.setVisibility(4);
                this.mDotView.setVisibility(8);
            } else {
                this.mWifiGroup.setVisibility(8);
                this.mDotView.setVisibility(0);
            }
        }
    }

    public int getVisibleState() {
        return this.mVisibleState;
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

    private void init() {
        this.mWifiGroup = (LinearLayout) findViewById(R$id.wifi_group);
        this.mWifiIcon = (ImageView) findViewById(R$id.wifi_signal);
        this.mIn = (ImageView) findViewById(R$id.wifi_in);
        this.mOut = (ImageView) findViewById(R$id.wifi_out);
        this.mSignalSpacer = findViewById(R$id.wifi_signal_spacer);
        this.mWifiEdge = (ImageView) findViewById(R$id.wifi_epdg);
        this.mAirplaneSpacer = findViewById(R$id.wifi_airplane_spacer);
        this.mInoutContainer = findViewById(R$id.inout_container);
        ImageView imageView = (ImageView) findViewById(R$id.wifi_inout);
        this.mWifiActivityView = imageView;
        if (imageView != null) {
            this.mInoutContainer.setVisibility(8);
        }
        initDotView();
    }

    private void initDotView() {
        StatusBarIconView statusBarIconView = new StatusBarIconView(this.mContext, this.mSlot, (StatusBarNotification) null);
        this.mDotView = statusBarIconView;
        statusBarIconView.setVisibleState(1);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_size);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize);
        layoutParams.gravity = 8388627;
        addView(this.mDotView, layoutParams);
    }

    public void applyWifiState(StatusBarSignalPolicy.WifiIconState wifiIconState) {
        boolean z = true;
        if (wifiIconState == null) {
            if (getVisibility() == 8) {
                z = false;
            }
            setVisibility(8);
            this.mState = null;
        } else {
            StatusBarSignalPolicy.WifiIconState wifiIconState2 = this.mState;
            if (wifiIconState2 == null) {
                this.mState = wifiIconState.copy();
                initViewState();
            } else {
                z = !wifiIconState2.equals(wifiIconState) ? updateState(wifiIconState.copy()) : false;
            }
        }
        if (z) {
            requestLayout();
        }
    }

    private boolean updateState(StatusBarSignalPolicy.WifiIconState wifiIconState) {
        setContentDescription(wifiIconState.contentDescription);
        int i = this.mState.resId;
        int i2 = wifiIconState.resId;
        if (i != i2 && i2 >= 0) {
            this.mWifiIcon.setImageDrawable(this.mContext.getDrawable(i2));
        }
        ImageView imageView = this.mWifiActivityView;
        boolean z = true;
        int i3 = 8;
        if (imageView != null) {
            boolean z2 = wifiIconState.activityIn;
            int i4 = (!z2 || !wifiIconState.activityOut) ? z2 ? 1 : wifiIconState.activityOut ? 2 : 0 : 3;
            if (i4 != this.mLastWifiActivityIconId) {
                if (i4 != 0) {
                    imageView.setImageDrawable(this.mContext.getDrawable(CarrierIcons.ActivityIcon.sbWifiActivity(i4)));
                }
                this.mLastWifiActivityIconId = i4;
            }
            this.mWifiActivityView.setVisibility(i4 != 0 ? 0 : 8);
        } else {
            this.mIn.setVisibility(wifiIconState.activityIn ? 0 : 8);
            this.mOut.setVisibility(wifiIconState.activityOut ? 0 : 8);
            this.mInoutContainer.setVisibility((wifiIconState.activityIn || wifiIconState.activityOut) ? 0 : 8);
        }
        this.mAirplaneSpacer.setVisibility(wifiIconState.airplaneSpacerVisible ? 0 : 8);
        this.mSignalSpacer.setVisibility(wifiIconState.signalSpacerVisible ? 0 : 8);
        this.mWifiEdge.setVisibility(wifiIconState.epdgState ? 0 : 8);
        boolean z3 = wifiIconState.activityIn;
        StatusBarSignalPolicy.WifiIconState wifiIconState2 = this.mState;
        if (z3 == wifiIconState2.activityIn && wifiIconState.activityOut == wifiIconState2.activityOut) {
            z = false;
        }
        boolean z4 = wifiIconState2.visible;
        boolean z5 = wifiIconState.visible;
        if (z4 != z5) {
            z |= true;
            if (z5) {
                i3 = 0;
            }
            setVisibility(i3);
        }
        this.mState = wifiIconState;
        return z;
    }

    private void initViewState() {
        setContentDescription(this.mState.contentDescription);
        int i = this.mState.resId;
        if (i >= 0) {
            this.mWifiIcon.setImageDrawable(this.mContext.getDrawable(i));
        }
        ImageView imageView = this.mWifiActivityView;
        int i2 = 8;
        if (imageView != null) {
            StatusBarSignalPolicy.WifiIconState wifiIconState = this.mState;
            boolean z = wifiIconState.activityIn;
            int i3 = (!z || !wifiIconState.activityOut) ? z ? 1 : wifiIconState.activityOut ? 2 : 0 : 3;
            if (i3 != this.mLastWifiActivityIconId) {
                if (i3 != 0) {
                    imageView.setImageDrawable(this.mContext.getDrawable(CarrierIcons.ActivityIcon.sbWifiActivity(i3)));
                }
                this.mLastWifiActivityIconId = i3;
            }
            this.mWifiActivityView.setVisibility(i3 != 0 ? 0 : 8);
        } else {
            this.mIn.setVisibility(this.mState.activityIn ? 0 : 8);
            this.mOut.setVisibility(this.mState.activityOut ? 0 : 8);
            View view = this.mInoutContainer;
            StatusBarSignalPolicy.WifiIconState wifiIconState2 = this.mState;
            view.setVisibility((wifiIconState2.activityIn || wifiIconState2.activityOut) ? 0 : 8);
        }
        this.mAirplaneSpacer.setVisibility(this.mState.airplaneSpacerVisible ? 0 : 8);
        this.mSignalSpacer.setVisibility(this.mState.signalSpacerVisible ? 0 : 8);
        this.mWifiEdge.setVisibility(this.mState.epdgState ? 0 : 8);
        if (this.mState.visible) {
            i2 = 0;
        }
        setVisibility(i2);
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        ColorStateList valueOf = ColorStateList.valueOf(DarkIconDispatcher.getTint(rect, this, i));
        this.mWifiIcon.setImageTintList(valueOf);
        this.mIn.setImageTintList(valueOf);
        this.mOut.setImageTintList(valueOf);
        this.mDotView.setDecorColor(DarkIconDispatcher.getTint(rect, this, i));
        this.mDotView.setIconColor(DarkIconDispatcher.getTint(rect, this, i), false);
        this.mWifiEdge.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(rect, this, i)));
    }

    public String toString() {
        return "StatusBarWifiView(slot=" + this.mSlot + " state=" + this.mState + ")";
    }
}
