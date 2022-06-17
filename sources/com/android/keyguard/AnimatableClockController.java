package com.android.keyguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.NumberFormat;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import com.android.systemui.R$dimen;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.ViewController;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class AnimatableClockController extends ViewController<AnimatableClockView> {
    private final BatteryController.BatteryStateChangeCallback mBatteryCallback;
    private final BatteryController mBatteryController;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final float mBurmeseLineSpacing;
    private final NumberFormat mBurmeseNf;
    private final String mBurmeseNumerals;
    private final KeyguardBypassController mBypassController;
    private final float mDefaultLineSpacing;
    /* access modifiers changed from: private */
    public float mDozeAmount;
    private final int mDozingColor = -1;
    /* access modifiers changed from: private */
    public boolean mIsCharging;
    /* access modifiers changed from: private */
    public boolean mIsDozing;
    boolean mKeyguardShowing;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback;
    private Locale mLocale;
    private final BroadcastReceiver mLocaleBroadcastReceiver;
    private int mLockScreenColor;
    /* access modifiers changed from: private */
    public final StatusBarStateController mStatusBarStateController;
    private final StatusBarStateController.StateListener mStatusBarStatePersistentListener;

    public AnimatableClockController(AnimatableClockView animatableClockView, StatusBarStateController statusBarStateController, BroadcastDispatcher broadcastDispatcher, BatteryController batteryController, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController) {
        super(animatableClockView);
        NumberFormat instance = NumberFormat.getInstance(Locale.forLanguageTag("my"));
        this.mBurmeseNf = instance;
        this.mBatteryCallback = new BatteryController.BatteryStateChangeCallback() {
            public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
                AnimatableClockController animatableClockController = AnimatableClockController.this;
                if (animatableClockController.mKeyguardShowing && !animatableClockController.mIsCharging && z2) {
                    StatusBarStateController access$100 = AnimatableClockController.this.mStatusBarStateController;
                    Objects.requireNonNull(access$100);
                    ((AnimatableClockView) AnimatableClockController.this.mView).animateCharge(new AnimatableClockController$1$$ExternalSyntheticLambda0(access$100));
                }
                boolean unused = AnimatableClockController.this.mIsCharging = z2;
            }
        };
        this.mLocaleBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                AnimatableClockController.this.updateLocale();
            }
        };
        this.mStatusBarStatePersistentListener = new StatusBarStateController.StateListener() {
            public void onDozeAmountChanged(float f, float f2) {
                boolean z = false;
                boolean z2 = (AnimatableClockController.this.mDozeAmount == 0.0f && f == 1.0f) || (AnimatableClockController.this.mDozeAmount == 1.0f && f == 0.0f);
                if (f > AnimatableClockController.this.mDozeAmount) {
                    z = true;
                }
                float unused = AnimatableClockController.this.mDozeAmount = f;
                if (AnimatableClockController.this.mIsDozing != z) {
                    boolean unused2 = AnimatableClockController.this.mIsDozing = z;
                    ((AnimatableClockView) AnimatableClockController.this.mView).animateDoze(AnimatableClockController.this.mIsDozing, !z2);
                }
            }
        };
        this.mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            public void onKeyguardVisibilityChanged(boolean z) {
                AnimatableClockController animatableClockController = AnimatableClockController.this;
                animatableClockController.mKeyguardShowing = z;
                if (!z) {
                    animatableClockController.reset();
                }
            }
        };
        this.mStatusBarStateController = statusBarStateController;
        this.mIsDozing = statusBarStateController.isDozing();
        this.mDozeAmount = statusBarStateController.getDozeAmount();
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mBypassController = keyguardBypassController;
        this.mBatteryController = batteryController;
        this.mBurmeseNumerals = instance.format(1234567890);
        this.mBurmeseLineSpacing = getContext().getResources().getFloat(R$dimen.keyguard_clock_line_spacing_scale_burmese);
        this.mDefaultLineSpacing = getContext().getResources().getFloat(R$dimen.keyguard_clock_line_spacing_scale);
    }

    /* access modifiers changed from: private */
    public void reset() {
        ((AnimatableClockView) this.mView).animateDoze(this.mIsDozing, false);
    }

    /* access modifiers changed from: protected */
    public void onInit() {
        this.mIsDozing = this.mStatusBarStateController.isDozing();
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        updateLocale();
        this.mBroadcastDispatcher.registerReceiver(this.mLocaleBroadcastReceiver, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
        this.mDozeAmount = this.mStatusBarStateController.getDozeAmount();
        this.mBatteryController.addCallback(this.mBatteryCallback);
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardUpdateMonitorCallback);
        this.mStatusBarStateController.removeCallback(this.mStatusBarStatePersistentListener);
        this.mStatusBarStateController.addCallback(this.mStatusBarStatePersistentListener);
        refreshTime();
        initColors();
        ((AnimatableClockView) this.mView).animateDoze(this.mIsDozing, false);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        this.mBroadcastDispatcher.unregisterReceiver(this.mLocaleBroadcastReceiver);
        this.mKeyguardUpdateMonitor.removeCallback(this.mKeyguardUpdateMonitorCallback);
        this.mBatteryController.removeCallback(this.mBatteryCallback);
        if (!((AnimatableClockView) this.mView).isAttachedToWindow()) {
            this.mStatusBarStateController.removeCallback(this.mStatusBarStatePersistentListener);
        }
    }

    public void animateAppear() {
        if (!this.mIsDozing) {
            ((AnimatableClockView) this.mView).animateAppearOnLockscreen();
        }
    }

    public void refreshTime() {
        ((AnimatableClockView) this.mView).refreshTime();
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
        ((AnimatableClockView) this.mView).onTimeZoneChanged(timeZone);
    }

    public void refreshFormat() {
        ((AnimatableClockView) this.mView).refreshFormat();
    }

    /* access modifiers changed from: private */
    public void updateLocale() {
        Locale locale = Locale.getDefault();
        if (!Objects.equals(locale, this.mLocale)) {
            this.mLocale = locale;
            if (NumberFormat.getInstance(locale).format(1234567890).equals(this.mBurmeseNumerals)) {
                ((AnimatableClockView) this.mView).setLineSpacingScale(this.mBurmeseLineSpacing);
            } else {
                ((AnimatableClockView) this.mView).setLineSpacingScale(this.mDefaultLineSpacing);
            }
            ((AnimatableClockView) this.mView).refreshFormat();
        }
    }

    private void initColors() {
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(getContext(), R$attr.wallpaperTextColorAccent);
        this.mLockScreenColor = colorAttrDefaultColor;
        ((AnimatableClockView) this.mView).setColors(-1, colorAttrDefaultColor);
        ((AnimatableClockView) this.mView).animateDoze(this.mIsDozing, false);
    }
}
