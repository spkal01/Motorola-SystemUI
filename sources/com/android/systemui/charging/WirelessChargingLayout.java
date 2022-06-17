package com.android.systemui.charging;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.statusbar.charging.ChargingRippleView;
import java.text.NumberFormat;

public class WirelessChargingLayout extends FrameLayout {
    /* access modifiers changed from: private */
    public ChargingRippleView mRippleView;

    public WirelessChargingLayout(Context context, int i, int i2, boolean z) {
        super(context);
        init(context, (AttributeSet) null, i, i2, z);
    }

    public WirelessChargingLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, false);
    }

    private void init(Context context, AttributeSet attributeSet, boolean z) {
        init(context, attributeSet, -1, -1, false);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2, boolean z) {
        int i3 = i;
        int i4 = i2;
        boolean z2 = i3 != -1;
        int i5 = R$style.ChargingAnim_WallpaperBackground;
        if (z) {
            i5 = R$style.ChargingAnim_DarkBackground;
        }
        FrameLayout.inflate(new ContextThemeWrapper(context, i5), R$layout.wireless_charging_layout, this);
        TextView textView = (TextView) findViewById(R$id.wireless_charging_percentage);
        if (i4 != -1) {
            textView.setText(NumberFormat.getPercentInstance().format((double) (((float) i4) / 100.0f)));
            textView.setAlpha(0.0f);
        }
        long integer = (long) context.getResources().getInteger(R$integer.wireless_charging_fade_offset);
        long integer2 = (long) context.getResources().getInteger(R$integer.wireless_charging_fade_duration);
        float f = context.getResources().getFloat(R$dimen.wireless_charging_anim_battery_level_text_size_start);
        float f2 = context.getResources().getFloat(R$dimen.wireless_charging_anim_battery_level_text_size_end) * (z2 ? 0.75f : 1.0f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, "textSize", new float[]{f, f2});
        ofFloat.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f));
        Resources resources = context.getResources();
        int i6 = R$integer.wireless_charging_battery_level_text_scale_animation_duration;
        int i7 = i6;
        ofFloat.setDuration((long) resources.getInteger(i6));
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView, "alpha", new float[]{0.0f, 1.0f});
        Interpolator interpolator = Interpolators.LINEAR;
        ofFloat2.setInterpolator(interpolator);
        Resources resources2 = context.getResources();
        String str = "textSize";
        int i8 = R$integer.wireless_charging_battery_level_text_opacity_duration;
        float f3 = f2;
        ofFloat2.setDuration((long) resources2.getInteger(i8));
        Resources resources3 = context.getResources();
        int i9 = R$integer.wireless_charging_anim_opacity_offset;
        int i10 = i9;
        ofFloat2.setStartDelay((long) resources3.getInteger(i9));
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textView, "alpha", new float[]{1.0f, 0.0f});
        ofFloat3.setDuration(integer2);
        ofFloat3.setInterpolator(interpolator);
        ofFloat3.setStartDelay(integer);
        AnimatorSet animatorSet = new AnimatorSet();
        long j = integer;
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3});
        ObjectAnimator ofArgb = ObjectAnimator.ofArgb(this, "backgroundColor", new int[]{0, 1275068416});
        int i11 = i8;
        ofArgb.setDuration(300);
        ofArgb.setInterpolator(interpolator);
        ObjectAnimator ofArgb2 = ObjectAnimator.ofArgb(this, "backgroundColor", new int[]{1275068416, 0});
        ofArgb2.setDuration(300);
        ofArgb2.setInterpolator(interpolator);
        ofArgb2.setStartDelay(1200);
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(new Animator[]{ofArgb, ofArgb2});
        animatorSet2.start();
        this.mRippleView = (ChargingRippleView) findViewById(R$id.wireless_charging_ripple);
        this.mRippleView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewDetachedFromWindow(View view) {
            }

            public void onViewAttachedToWindow(View view) {
                WirelessChargingLayout.this.mRippleView.setDuration(1500);
                WirelessChargingLayout.this.mRippleView.startRipple();
                WirelessChargingLayout.this.mRippleView.removeOnAttachStateChangeListener(this);
            }
        });
        if (!z2) {
            animatorSet.start();
            return;
        }
        TextView textView2 = (TextView) findViewById(R$id.reverse_wireless_charging_percentage);
        textView2.setVisibility(0);
        textView2.setText(NumberFormat.getPercentInstance().format((double) (((float) i3) / 100.0f)));
        textView2.setAlpha(0.0f);
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(textView2, str, new float[]{f, f3});
        ofFloat4.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f));
        ofFloat4.setDuration((long) context.getResources().getInteger(i7));
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(textView2, "alpha", new float[]{0.0f, 1.0f});
        ofFloat5.setInterpolator(interpolator);
        ofFloat5.setDuration((long) context.getResources().getInteger(i11));
        int i12 = i10;
        ofFloat5.setStartDelay((long) context.getResources().getInteger(i12));
        ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(textView2, "alpha", new float[]{1.0f, 0.0f});
        ofFloat6.setDuration(integer2);
        ofFloat6.setInterpolator(interpolator);
        long j2 = j;
        ofFloat6.setStartDelay(j2);
        AnimatorSet animatorSet3 = new AnimatorSet();
        animatorSet3.playTogether(new Animator[]{ofFloat4, ofFloat5, ofFloat6});
        ImageView imageView = (ImageView) findViewById(R$id.reverse_wireless_charging_icon);
        imageView.setVisibility(0);
        int round = Math.round(TypedValue.applyDimension(1, f3, getResources().getDisplayMetrics()));
        imageView.setPadding(round, 0, round, 0);
        ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(imageView, "alpha", new float[]{0.0f, 1.0f});
        ofFloat7.setInterpolator(interpolator);
        ofFloat7.setDuration((long) context.getResources().getInteger(i11));
        ofFloat7.setStartDelay((long) context.getResources().getInteger(i12));
        ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(imageView, "alpha", new float[]{1.0f, 0.0f});
        ofFloat8.setDuration(integer2);
        ofFloat8.setInterpolator(interpolator);
        ofFloat8.setStartDelay(j2);
        AnimatorSet animatorSet4 = new AnimatorSet();
        animatorSet4.playTogether(new Animator[]{ofFloat7, ofFloat8});
        animatorSet.start();
        animatorSet3.start();
        animatorSet4.start();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mRippleView != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            ChargingRippleView chargingRippleView = this.mRippleView;
            chargingRippleView.setColor(Utils.getColorAttr(chargingRippleView.getContext(), 16843829).getDefaultColor());
            this.mRippleView.setOrigin(new PointF((float) (measuredWidth / 2), (float) (measuredHeight / 2)));
            this.mRippleView.setRadius(((float) Math.max(measuredWidth, measuredHeight)) * 0.5f);
        }
        super.onLayout(z, i, i2, i3, i4);
    }
}
