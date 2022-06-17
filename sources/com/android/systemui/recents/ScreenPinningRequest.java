package com.android.systemui.recents;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.RemoteException;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.R$styleable;
import com.android.systemui.Dependency;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.leak.RotationUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Optional;

public class ScreenPinningRequest implements View.OnClickListener, NavigationModeController.ModeChangedListener {
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityService;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);
    private final OverviewProxyService mOverviewProxyService = ((OverviewProxyService) Dependency.get(OverviewProxyService.class));
    private RequestWindowView mRequestWindow;
    /* access modifiers changed from: private */
    public final Optional<Lazy<StatusBar>> mStatusBarOptionalLazy;
    /* access modifiers changed from: private */
    public final WindowManager mWindowManager;
    private int taskId;

    public ScreenPinningRequest(Context context, Optional<Lazy<StatusBar>> optional) {
        this.mContext = context;
        this.mStatusBarOptionalLazy = optional;
        this.mAccessibilityService = (AccessibilityManager) context.getSystemService("accessibility");
        this.mWindowManager = (WindowManager) context.getSystemService("window");
    }

    public void clearPrompt() {
        RequestWindowView requestWindowView = this.mRequestWindow;
        if (requestWindowView != null) {
            this.mWindowManager.removeView(requestWindowView);
            this.mRequestWindow = null;
        }
    }

    public void showPrompt(int i, boolean z) {
        try {
            clearPrompt();
        } catch (IllegalArgumentException unused) {
        }
        this.taskId = i;
        RequestWindowView requestWindowView = new RequestWindowView(this.mContext, z);
        this.mRequestWindow = requestWindowView;
        requestWindowView.setSystemUiVisibility(256);
        this.mWindowManager.addView(this.mRequestWindow, getWindowLayoutParams());
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
    }

    public void onConfigurationChanged() {
        RequestWindowView requestWindowView = this.mRequestWindow;
        if (requestWindowView != null) {
            requestWindowView.onConfigurationChanged();
        }
    }

    /* access modifiers changed from: protected */
    public WindowManager.LayoutParams getWindowLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2024, 264, -3);
        layoutParams.token = new Binder();
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("ScreenPinningConfirmation");
        layoutParams.gravity = R$styleable.AppCompatTheme_windowActionModeOverlay;
        layoutParams.setFitInsetsTypes(0);
        return layoutParams;
    }

    public void onClick(View view) {
        if (view.getId() == R$id.screen_pinning_ok_button || this.mRequestWindow == view) {
            try {
                ActivityTaskManager.getService().startSystemLockTaskMode(this.taskId);
            } catch (RemoteException unused) {
            }
        }
        clearPrompt();
    }

    public FrameLayout.LayoutParams getRequestLayoutParams(int i) {
        return new FrameLayout.LayoutParams(-2, -2, i == 3 ? 19 : i == 1 ? 21 : 81);
    }

    private class RequestWindowView extends FrameLayout {
        private final BroadcastDispatcher mBroadcastDispatcher = ((BroadcastDispatcher) Dependency.get(BroadcastDispatcher.class));
        /* access modifiers changed from: private */
        public final ColorDrawable mColor;
        private ValueAnimator mColorAnim;
        /* access modifiers changed from: private */
        public ViewGroup mLayout;
        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")) {
                    RequestWindowView requestWindowView = RequestWindowView.this;
                    requestWindowView.post(requestWindowView.mUpdateLayoutRunnable);
                } else if (intent.getAction().equals("android.intent.action.USER_SWITCHED") || intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    ScreenPinningRequest.this.clearPrompt();
                }
            }
        };
        private boolean mShowCancel;
        /* access modifiers changed from: private */
        public final Runnable mUpdateLayoutRunnable = new Runnable() {
            public void run() {
                if (RequestWindowView.this.mLayout != null && RequestWindowView.this.mLayout.getParent() != null) {
                    ViewGroup access$500 = RequestWindowView.this.mLayout;
                    RequestWindowView requestWindowView = RequestWindowView.this;
                    access$500.setLayoutParams(ScreenPinningRequest.this.getRequestLayoutParams(requestWindowView.getRotation(requestWindowView.mContext)));
                }
            }
        };

        public RequestWindowView(Context context, boolean z) {
            super(context);
            ColorDrawable colorDrawable = new ColorDrawable(0);
            this.mColor = colorDrawable;
            setClickable(true);
            setOnClickListener(ScreenPinningRequest.this);
            setBackground(colorDrawable);
            this.mShowCancel = z;
        }

        public void onAttachedToWindow() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ScreenPinningRequest.this.mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
            float f = displayMetrics.density;
            int rotation = getRotation(this.mContext);
            inflateView(rotation);
            int color = this.mContext.getColor(R$color.screen_pinning_request_window_bg);
            if (ActivityManager.isHighEndGfx()) {
                this.mLayout.setAlpha(0.0f);
                if (rotation == 3) {
                    this.mLayout.setTranslationX(f * -96.0f);
                } else if (rotation == 1) {
                    this.mLayout.setTranslationX(f * 96.0f);
                } else {
                    this.mLayout.setTranslationY(f * 96.0f);
                }
                this.mLayout.animate().alpha(1.0f).translationX(0.0f).translationY(0.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
                ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), new Object[]{0, Integer.valueOf(color)});
                this.mColorAnim = ofObject;
                ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        RequestWindowView.this.mColor.setColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                    }
                });
                this.mColorAnim.setDuration(1000);
                this.mColorAnim.start();
            } else {
                this.mColor.setColor(color);
            }
            IntentFilter intentFilter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
        }

        private void inflateView(int i) {
            int i2;
            int i3;
            Context context = getContext();
            boolean z = true;
            if (i == 3) {
                i2 = R$layout.screen_pinning_request_sea_phone;
            } else if (i == 1) {
                i2 = R$layout.screen_pinning_request_land_phone;
            } else {
                i2 = R$layout.screen_pinning_request;
            }
            ViewGroup viewGroup = (ViewGroup) View.inflate(context, i2, (ViewGroup) null);
            this.mLayout = viewGroup;
            viewGroup.setClickable(true);
            int i4 = 0;
            this.mLayout.setLayoutDirection(0);
            this.mLayout.findViewById(R$id.screen_pinning_text_area).setLayoutDirection(3);
            View findViewById = this.mLayout.findViewById(R$id.screen_pinning_buttons);
            WindowManagerWrapper instance = WindowManagerWrapper.getInstance();
            if (QuickStepContract.isGesturalMode(ScreenPinningRequest.this.mNavBarMode) || !instance.hasSoftNavigationBar(this.mContext.getDisplayId())) {
                findViewById.setVisibility(8);
            } else {
                findViewById.setLayoutDirection(3);
                swapChildrenIfRtlAndVertical(findViewById);
            }
            ((Button) this.mLayout.findViewById(R$id.screen_pinning_ok_button)).setOnClickListener(ScreenPinningRequest.this);
            if (this.mShowCancel) {
                ((Button) this.mLayout.findViewById(R$id.screen_pinning_cancel_button)).setOnClickListener(ScreenPinningRequest.this);
            } else {
                ((Button) this.mLayout.findViewById(R$id.screen_pinning_cancel_button)).setVisibility(4);
            }
            NavigationBarView navigationBarView = (NavigationBarView) ScreenPinningRequest.this.mStatusBarOptionalLazy.map(ScreenPinningRequest$RequestWindowView$$ExternalSyntheticLambda0.INSTANCE).orElse((Object) null);
            if (navigationBarView == null || !navigationBarView.isRecentsButtonVisible()) {
                z = false;
            }
            boolean isTouchExplorationEnabled = ScreenPinningRequest.this.mAccessibilityService.isTouchExplorationEnabled();
            if (QuickStepContract.isGesturalMode(ScreenPinningRequest.this.mNavBarMode)) {
                i3 = R$string.screen_pinning_description_gestural;
            } else if (z) {
                this.mLayout.findViewById(R$id.screen_pinning_recents_group).setVisibility(0);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg_light).setVisibility(4);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg).setVisibility(4);
                if (isTouchExplorationEnabled) {
                    i3 = R$string.screen_pinning_description_accessible;
                } else {
                    i3 = R$string.screen_pinning_description;
                }
            } else {
                this.mLayout.findViewById(R$id.screen_pinning_recents_group).setVisibility(4);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg_light).setVisibility(0);
                this.mLayout.findViewById(R$id.screen_pinning_home_bg).setVisibility(0);
                if (isTouchExplorationEnabled) {
                    i3 = R$string.screen_pinning_description_recents_invisible_accessible;
                } else {
                    i3 = R$string.screen_pinning_description_recents_invisible;
                }
            }
            if (navigationBarView != null) {
                ((ImageView) this.mLayout.findViewById(R$id.screen_pinning_back_icon)).setImageDrawable(navigationBarView.getBackDrawable());
                ((ImageView) this.mLayout.findViewById(R$id.screen_pinning_home_icon)).setImageDrawable(navigationBarView.getHomeDrawable());
            }
            int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.screen_pinning_description_bullet_gap_width);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append(getContext().getText(i3), new BulletSpan(dimensionPixelSize), 0);
            spannableStringBuilder.append(System.lineSeparator());
            spannableStringBuilder.append(getContext().getText(R$string.screen_pinning_exposes_personal_data), new BulletSpan(dimensionPixelSize), 0);
            spannableStringBuilder.append(System.lineSeparator());
            spannableStringBuilder.append(getContext().getText(R$string.screen_pinning_can_open_other_apps), new BulletSpan(dimensionPixelSize), 0);
            ((TextView) this.mLayout.findViewById(R$id.screen_pinning_description)).setText(spannableStringBuilder);
            if (isTouchExplorationEnabled) {
                i4 = 4;
            }
            this.mLayout.findViewById(R$id.screen_pinning_back_bg).setVisibility(i4);
            this.mLayout.findViewById(R$id.screen_pinning_back_bg_light).setVisibility(i4);
            addView(this.mLayout, ScreenPinningRequest.this.getRequestLayoutParams(i));
        }

        private void swapChildrenIfRtlAndVertical(View view) {
            if (this.mContext.getResources().getConfiguration().getLayoutDirection() == 1) {
                LinearLayout linearLayout = (LinearLayout) view;
                if (linearLayout.getOrientation() == 1) {
                    int childCount = linearLayout.getChildCount();
                    ArrayList arrayList = new ArrayList(childCount);
                    for (int i = 0; i < childCount; i++) {
                        arrayList.add(linearLayout.getChildAt(i));
                    }
                    linearLayout.removeAllViews();
                    for (int i2 = childCount - 1; i2 >= 0; i2--) {
                        linearLayout.addView((View) arrayList.get(i2));
                    }
                }
            }
        }

        public void onDetachedFromWindow() {
            this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged() {
            removeAllViews();
            inflateView(getRotation(this.mContext));
        }

        /* access modifiers changed from: private */
        public int getRotation(Context context) {
            if (context.getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                return 0;
            }
            return RotationUtils.getRotation(context);
        }
    }
}
