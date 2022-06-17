package com.android.systemui;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.graph.ThemedBatteryDrawable;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.SysuiLifecycle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;

public class BatteryMeterView extends LinearLayout implements BatteryController.BatteryStateChangeCallback, TunerService.Tunable, DarkIconDispatcher.DarkReceiver, ConfigurationController.ConfigurationListener {
    private BatteryController mBatteryController;
    private final ImageView mBatteryIconView;
    private TextView mBatteryPercentView;
    private boolean mBatteryStateUnknown;
    private boolean mCharging;
    private int mCurrentOrientation;
    private Rect mDarkArea;
    private float mDarkIntensity;
    private final ThemedBatteryDrawable mDrawable;
    private DualToneHandler mDualToneHandler;
    private boolean mIgnoreTunerUpdates;
    private boolean mIsChargingDisabledByAdaptive;
    private boolean mIsExpandedBattery;
    private boolean mIsInnerStatusbarPercentage;
    private boolean mIsSubscribedForTunerUpdates;
    private int mLeft;
    private int mLevel;
    private int mNonAdaptedBackgroundColor;
    private int mNonAdaptedForegroundColor;
    private int mNonAdaptedSingleToneColor;
    private final int mPercentageStyleId;
    private int mRight;
    /* access modifiers changed from: private */
    public SettingObserver mSettingObserver;
    private boolean mShowPercentAvailable;
    private int mShowPercentMode;
    private final String mSlotBattery;
    private int mTextColor;
    private Drawable mUnknownStateDrawable;
    /* access modifiers changed from: private */
    public int mUser;
    private final CurrentUserTracker mUserTracker;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowPercentMode = 0;
        this.mCurrentOrientation = 1;
        this.mIsChargingDisabledByAdaptive = false;
        setOrientation(0);
        setGravity(8388627);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.BatteryMeterView, i, 0);
        int color = obtainStyledAttributes.getColor(R$styleable.BatteryMeterView_frameColor, context.getColor(R$color.meter_background_color));
        this.mPercentageStyleId = obtainStyledAttributes.getResourceId(R$styleable.BatteryMeterView_textAppearance, 0);
        ThemedBatteryDrawable themedBatteryDrawable = new ThemedBatteryDrawable(context, color);
        this.mDrawable = themedBatteryDrawable;
        obtainStyledAttributes.recycle();
        this.mCurrentOrientation = context.getResources().getConfiguration().orientation;
        this.mSettingObserver = new SettingObserver(new Handler(context.getMainLooper()));
        this.mShowPercentAvailable = context.getResources().getBoolean(17891393);
        setupLayoutTransition();
        this.mSlotBattery = context.getString(17041492);
        ImageView imageView = new ImageView(context);
        this.mBatteryIconView = imageView;
        imageView.setImageDrawable(themedBatteryDrawable);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(getResources().getDimensionPixelSize(R$dimen.status_bar_battery_icon_width), getResources().getDimensionPixelSize(R$dimen.status_bar_battery_icon_height));
        marginLayoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelOffset(R$dimen.battery_margin_bottom));
        addView(imageView, marginLayoutParams);
        updateShowPercent();
        this.mDualToneHandler = new DualToneHandler(context);
        onDarkChanged(new Rect(), 0.0f, -1);
        this.mUserTracker = new CurrentUserTracker((BroadcastDispatcher) Dependency.get(BroadcastDispatcher.class)) {
            public void onUserSwitched(int i) {
                int unused = BatteryMeterView.this.mUser = i;
                BatteryMeterView.this.getContext().getContentResolver().unregisterContentObserver(BatteryMeterView.this.mSettingObserver);
                BatteryMeterView.this.getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_show_battery_percent"), false, BatteryMeterView.this.mSettingObserver, i);
                BatteryMeterView.this.updateShowPercent();
            }
        };
        setClipChildren(false);
        setClipToPadding(false);
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isCliContext(this.mContext)) {
            ((ConfigurationController) Dependency.get(ConfigurationController.class)).observe(SysuiLifecycle.viewAttachLifecycle(this), this);
        }
    }

    private void setupLayoutTransition() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setDuration(200);
        layoutTransition.setAnimator(2, ObjectAnimator.ofFloat((Object) null, "alpha", new float[]{0.0f, 1.0f}));
        layoutTransition.setInterpolator(2, Interpolators.ALPHA_IN);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) null, "alpha", new float[]{1.0f, 0.0f});
        layoutTransition.setInterpolator(3, Interpolators.ALPHA_OUT);
        layoutTransition.setAnimator(3, ofFloat);
        setLayoutTransition(layoutTransition);
    }

    public void setForceShowPercent(boolean z) {
        setPercentShowMode(z ? 1 : 0);
    }

    public void setPercentShowMode(int i) {
        if (i != this.mShowPercentMode) {
            this.mShowPercentMode = i;
            updateShowPercent();
        }
    }

    public void setIgnoreTunerUpdates(boolean z) {
        this.mIgnoreTunerUpdates = z;
        updateTunerSubscription();
    }

    private void updateTunerSubscription() {
        if (this.mIgnoreTunerUpdates) {
            unsubscribeFromTunerUpdates();
        } else {
            subscribeForTunerUpdates();
        }
    }

    private void subscribeForTunerUpdates() {
        if (!this.mIsSubscribedForTunerUpdates && !this.mIgnoreTunerUpdates) {
            ((TunerService) Dependency.get(TunerService.class)).addTunable(this, "icon_blacklist");
            this.mIsSubscribedForTunerUpdates = true;
        }
    }

    private void unsubscribeFromTunerUpdates() {
        if (this.mIsSubscribedForTunerUpdates) {
            ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
            this.mIsSubscribedForTunerUpdates = false;
        }
    }

    public void setColorsFromContext(Context context) {
        if (context != null) {
            this.mDualToneHandler.setColorsFromContext(context);
        }
    }

    public void onTuningChanged(String str, String str2) {
        if ("icon_blacklist".equals(str)) {
            setVisibility(StatusBarIconController.getIconHideList(getContext(), str2).contains(this.mSlotBattery) ? 8 : 0);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        BatteryController batteryController = (BatteryController) Dependency.get(BatteryController.class);
        this.mBatteryController = batteryController;
        batteryController.addCallback(this);
        this.mUser = ActivityManager.getCurrentUser();
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("status_bar_show_battery_percent"), false, this.mSettingObserver, this.mUser);
        getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("battery_estimates_last_update_time"), false, this.mSettingObserver);
        updateShowPercent();
        subscribeForTunerUpdates();
        this.mUserTracker.startTracking();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mUserTracker.stopTracking();
        this.mBatteryController.removeCallback(this);
        getContext().getContentResolver().unregisterContentObserver(this.mSettingObserver);
        unsubscribeFromTunerUpdates();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            int[] iArr = new int[2];
            getLocationOnScreen(iArr);
            int i5 = iArr[0];
            this.mLeft = i5;
            this.mRight = i5 + getWidth();
            onDarkChanged(this.mDarkArea, this.mDarkIntensity, -1);
        }
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        if (this.mCharging != z) {
            this.mDrawable.setCharging(z);
            this.mCharging = z;
        }
        if (this.mLevel != i) {
            this.mDrawable.setBatteryLevel(i);
            this.mLevel = i;
        }
        updatePercentText();
    }

    public void onPowerSaveChanged(boolean z) {
        this.mDrawable.setPowerSaveEnabled(z);
    }

    public void onAdaptiveChargingChanged(boolean z) {
        if (this.mIsChargingDisabledByAdaptive != z) {
            this.mDrawable.setChargingDisabledByAdaptive(z);
            this.mIsChargingDisabledByAdaptive = z;
            scaleBatteryMeterViews();
        }
    }

    private TextView loadPercentView() {
        return (TextView) LayoutInflater.from(getContext()).inflate(R$layout.battery_percentage_view, (ViewGroup) null);
    }

    public void updatePercentView() {
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            removeView(textView);
            this.mBatteryPercentView = null;
        }
        updateShowPercent();
    }

    /* access modifiers changed from: private */
    public void updatePercentText() {
        int i;
        if (this.mBatteryStateUnknown) {
            setContentDescription(getContext().getString(R$string.accessibility_battery_unknown));
            return;
        }
        BatteryController batteryController = this.mBatteryController;
        if (batteryController != null) {
            if (this.mBatteryPercentView == null) {
                Context context = getContext();
                if (this.mCharging) {
                    i = R$string.accessibility_battery_level_charging;
                } else {
                    i = R$string.accessibility_battery_level;
                }
                setContentDescription(context.getString(i, new Object[]{Integer.valueOf(this.mLevel)}));
            } else if (this.mShowPercentMode != 3 || this.mCharging) {
                setPercentTextAtCurrentLevel();
            } else {
                batteryController.getEstimatedTimeRemainingString(new BatteryMeterView$$ExternalSyntheticLambda0(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePercentText$0(String str) {
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            if (str == null || this.mShowPercentMode != 3) {
                setPercentTextAtCurrentLevel();
                return;
            }
            textView.setText(str);
            setContentDescription(getContext().getString(R$string.accessibility_battery_level_with_estimate, new Object[]{Integer.valueOf(this.mLevel), str}));
        }
    }

    private void setPercentTextAtCurrentLevel() {
        int i;
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            textView.setText(NumberFormat.getPercentInstance().format((double) (((float) this.mLevel) / 100.0f)));
            Context context = getContext();
            if (this.mCharging) {
                i = R$string.accessibility_battery_level_charging;
            } else {
                i = R$string.accessibility_battery_level;
            }
            setContentDescription(context.getString(i, new Object[]{Integer.valueOf(this.mLevel)}));
        }
    }

    /* access modifiers changed from: private */
    public void updateShowPercent() {
        int i;
        boolean z = this.mBatteryPercentView != null;
        if (((this.mShowPercentAvailable && (((Integer) DejankUtils.whitelistIpcs(new BatteryMeterView$$ExternalSyntheticLambda1(this))).intValue() != 0) && this.mShowPercentMode != 2) || (i = this.mShowPercentMode) == 1 || i == 3) && !this.mBatteryStateUnknown) {
            if (!innerBatteryPercentage() || this.mIsExpandedBattery) {
                if (this.mDrawable.getShowPercent()) {
                    this.mDrawable.setShowPercent(false);
                }
                if (!z) {
                    TextView loadPercentView = loadPercentView();
                    this.mBatteryPercentView = loadPercentView;
                    int i2 = this.mPercentageStyleId;
                    if (i2 != 0) {
                        loadPercentView.setTextAppearance(i2);
                    }
                    int i3 = this.mTextColor;
                    if (i3 != 0) {
                        this.mBatteryPercentView.setTextColor(i3);
                    }
                    updatePercentText();
                    addView(this.mBatteryPercentView, new ViewGroup.LayoutParams(-2, -2));
                }
            } else if (!this.mDrawable.getShowPercent()) {
                this.mDrawable.setShowPercent(true);
            }
        } else if (!innerBatteryPercentage() || this.mIsExpandedBattery) {
            if (z) {
                removeView(this.mBatteryPercentView);
                this.mBatteryPercentView = null;
            }
        } else if (this.mDrawable.getShowPercent()) {
            this.mDrawable.setShowPercent(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ Integer lambda$updateShowPercent$1() {
        return Integer.valueOf(Settings.System.getIntForUser(getContext().getContentResolver(), "status_bar_show_battery_percent", 0, this.mUser));
    }

    public void onDensityOrFontScaleChanged() {
        scaleBatteryMeterViews();
    }

    private Drawable getUnknownStateDrawable() {
        if (this.mUnknownStateDrawable == null) {
            Drawable drawable = this.mContext.getDrawable(R$drawable.ic_battery_unknown);
            this.mUnknownStateDrawable = drawable;
            drawable.setTint(this.mTextColor);
        }
        return this.mUnknownStateDrawable;
    }

    public void onBatteryUnknownStateChanged(boolean z) {
        if (this.mBatteryStateUnknown != z) {
            this.mBatteryStateUnknown = z;
            if (z) {
                this.mBatteryIconView.setImageDrawable(getUnknownStateDrawable());
            } else {
                this.mBatteryIconView.setImageDrawable(this.mDrawable);
            }
            updateShowPercent();
        }
    }

    public void onConfigChanged(Configuration configuration) {
        int i = configuration.orientation;
        if (i != this.mCurrentOrientation) {
            this.mCurrentOrientation = i;
            updatePercentView();
        }
    }

    private void scaleBatteryMeterViews() {
        Resources resources = getContext().getResources();
        TypedValue typedValue = new TypedValue();
        resources.getValue(R$dimen.status_bar_icon_scale_factor, typedValue, true);
        float f = typedValue.getFloat();
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.status_bar_battery_icon_height);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.status_bar_battery_icon_width);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R$dimen.battery_margin_bottom);
        if (this.mIsChargingDisabledByAdaptive) {
            int dimensionPixelSize4 = resources.getDimensionPixelSize(R$dimen.status_bar_battery_icon_adaptive_add_width);
            dimensionPixelSize2 += dimensionPixelSize4;
            this.mBatteryIconView.setPadding(0, 0, (int) (((float) dimensionPixelSize4) * f), 0);
        } else {
            this.mBatteryIconView.setPadding(0, 0, 0, 0);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (((float) dimensionPixelSize2) * f), (int) (((float) dimensionPixelSize) * f));
        layoutParams.setMargins(0, 0, 0, dimensionPixelSize3);
        this.mBatteryIconView.setLayoutParams(layoutParams);
        updatePercentView();
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        this.mDarkArea = rect;
        this.mDarkIntensity = f;
        if (!DarkIconDispatcher.isInArea(rect, this.mLeft, this.mRight)) {
            f = 0.0f;
        }
        this.mNonAdaptedSingleToneColor = this.mDualToneHandler.getSingleColor(f);
        this.mNonAdaptedForegroundColor = this.mDualToneHandler.getFillColor(f);
        int backgroundColor = this.mDualToneHandler.getBackgroundColor(f);
        this.mNonAdaptedBackgroundColor = backgroundColor;
        updateColors(this.mNonAdaptedForegroundColor, backgroundColor, this.mNonAdaptedSingleToneColor);
    }

    public void updateColors(int i, int i2, int i3) {
        this.mDrawable.setColors(this.mDarkIntensity, i, i2, i3);
        this.mTextColor = i3;
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            textView.setTextColor(i3);
        }
        Drawable drawable = this.mUnknownStateDrawable;
        if (drawable != null) {
            drawable.setTint(i3);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        CharSequence charSequence = null;
        if (this.mDrawable == null) {
            str = null;
        } else {
            str = this.mDrawable.getPowerSaveEnabled() + "";
        }
        TextView textView = this.mBatteryPercentView;
        if (textView != null) {
            charSequence = textView.getText();
        }
        printWriter.println("  BatteryMeterView:");
        printWriter.println("    mDrawable.getPowerSave: " + str);
        printWriter.println("    mBatteryPercentView.getText(): " + charSequence);
        printWriter.println("    mTextColor: #" + Integer.toHexString(this.mTextColor));
        printWriter.println("    mBatteryStateUnknown: " + this.mBatteryStateUnknown);
        printWriter.println("    mLevel: " + this.mLevel);
        printWriter.println("    mMode: " + this.mShowPercentMode);
        printWriter.println("    mPercentageStyleId: " + this.mPercentageStyleId);
    }

    private final class SettingObserver extends ContentObserver {
        public SettingObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            BatteryMeterView.this.updateShowPercent();
            if (TextUtils.equals(uri.getLastPathSegment(), "battery_estimates_last_update_time")) {
                BatteryMeterView.this.updatePercentText();
            }
            ((DualSimIconController) Dependency.get(DualSimIconController.class)).updateShowBatteryPercent();
        }
    }

    public void setIsExpandedBattery(boolean z) {
        this.mIsExpandedBattery = z;
        if (innerBatteryPercentage() && this.mIsExpandedBattery && this.mDrawable.getShowPercent()) {
            this.mDrawable.setShowPercent(false);
        }
    }

    public void innerStausbarPercentage(boolean z) {
        this.mIsInnerStatusbarPercentage = z;
        updatePercentView();
    }

    private boolean innerBatteryPercentage() {
        return (MotoFeature.getInstance(this.mContext).isInnerBatteryPercentage() || this.mIsInnerStatusbarPercentage) && this.mCurrentOrientation == 1;
    }
}
