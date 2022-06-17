package com.android.keyguard;

import android.app.Presentation;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.PresentationKgPasswordView;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.motorola.android.provider.MotorolaSettings;

public final class MotoKeyguardPresentation extends Presentation implements KpMessageUpdateListener, PresentationKgPasswordView.ImeStatusCallback, View.OnClickListener {
    private static boolean DEBUG = KeyguardConstants.DEBUG;
    private View mBackToClock;
    /* access modifiers changed from: private */
    public LinearLayout mBottomMessageArea;
    /* access modifiers changed from: private */
    public int mBottomMessageAreaHeight;
    private int mBottomMessageMarginBottomDefault;
    private boolean mCanSkipBouncer;
    private Context mContext;
    private Mode mCurrentMode = Mode.CLOCK;
    private KeyguardSecurityModel.SecurityMode mCurrentSecurityMode;
    private ImageView mDesktopConnectedTimerIcon;
    /* access modifiers changed from: private */
    public Display mDisplay;
    private TextView mDivider;
    private View mDozeScrimView;
    private boolean mDozing;
    private float mFontScale;
    private boolean mImeShown;
    private View mKeyguardClockContainer;
    private LayoutInflater mLayoutInflater;
    private TextView mLoginCountDownMessage;
    private TextView mMessageTextView;
    /* access modifiers changed from: private */
    public PtClockView mNewClockView;
    private PtKDMCallback mPtKDMCallback;
    private PtKgSecurityView mPtKgSecurityView;
    private FrameLayout mRootView;
    private ScreenLifecycle mScreenLifecycle;
    private ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() {
        public void onScreenTurningOn() {
            MotoKeyguardPresentation.this.updateKeyguardView();
        }
    };
    private LinearLayout mSecurityContainer;
    private KeyguardSecurityModel mSecurityModel;
    private TextClock mTextClock;
    private KeyguardUpdateMonitor mUpdateMonitor;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onStartedGoingToSleep(int i) {
            MotoKeyguardPresentation.this.updateViewForDoze(true);
            MotoKeyguardPresentation motoKeyguardPresentation = MotoKeyguardPresentation.this;
            if (!motoKeyguardPresentation.isRdpDisplay(motoKeyguardPresentation.mDisplay)) {
                MotoKeyguardPresentation.this.mNewClockView.setVisibility(4);
            }
        }

        public void onStartedWakingUp() {
            MotoKeyguardPresentation.this.updateViewForDoze(false);
        }

        public void onBiometricAuthenticated(int i, BiometricSourceType biometricSourceType, boolean z) {
            MotoKeyguardPresentation.this.updateKeyguardView();
        }

        public void onStrongAuthStateChanged(int i) {
            MotoKeyguardPresentation.this.updateKeyguardView();
        }

        public void onTrustChanged(int i) {
            MotoKeyguardPresentation.this.updateKeyguardView();
        }

        public void onLogoutEnabledChanged() {
            MotoKeyguardPresentation.this.updateKeyguardView();
        }
    };
    private WallpaperManager mWallpaperManager;

    enum Mode {
        CLOCK,
        BOUNCER
    }

    /* access modifiers changed from: private */
    public void updateKeyguardView() {
    }

    public void cancel() {
    }

    public MotoKeyguardPresentation(Context context, Display display, PtKDMCallback ptKDMCallback, boolean z) {
        super(context.createDisplayContext(display), display, R$style.Theme_SystemUI_MotoKeyguardPresentation, 2009);
        this.mContext = context.createDisplayContext(display);
        setCancelable(false);
        this.mPtKDMCallback = ptKDMCallback;
        this.mDozing = z;
        ScreenLifecycle screenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mScreenLifecycle = screenLifecycle;
        screenLifecycle.addObserver(this.mScreenObserver);
        this.mDisplay = display;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutInflater from = LayoutInflater.from(this.mContext);
        this.mLayoutInflater = from;
        setContentView(from.inflate(R$layout.keyguard_presentation_rdp, (ViewGroup) null));
        setImmersivePolicy();
        this.mSecurityModel = (KeyguardSecurityModel) Dependency.get(KeyguardSecurityModel.class);
        this.mFontScale = MotorolaSettings.Global.getFloat(this.mContext.getContentResolver(), "desktop_font_size_scale", 1.0f);
        if (this.mDisplay != null) {
            setTitle("MotoKeyguardPresentation: " + this.mDisplay.getDisplayId());
        } else {
            setTitle("MotoKeyguardPresentation");
        }
        getWindow().getDecorView().setSystemUiVisibility(1792);
        int i = 0;
        getWindow().getAttributes().setFitInsetsTypes(0);
        getWindow().setNavigationBarContrastEnforced(false);
        getWindow().setNavigationBarColor(0);
        this.mRootView = (FrameLayout) findViewById(R$id.presentation);
        this.mKeyguardClockContainer = findViewById(R$id.keyguard_clock_container);
        this.mBackToClock = findViewById(R$id.back_to_clock);
        this.mMessageTextView = (TextView) findViewById(R$id.pt_kg_message_text);
        this.mDivider = (TextView) findViewById(R$id.divider);
        this.mLoginCountDownMessage = (TextView) findViewById(R$id.pt_kg_login_count_down_timer);
        this.mSecurityContainer = (LinearLayout) findViewById(R$id.presentation_security_container);
        this.mDesktopConnectedTimerIcon = (ImageView) findViewById(R$id.desktop_connected_timer_icon);
        this.mDozeScrimView = findViewById(R$id.doze_scrim_bg);
        this.mBottomMessageArea = (LinearLayout) findViewById(R$id.bottom_message_area);
        this.mTextClock = (TextClock) findViewById(R$id.default_clock_view);
        this.mNewClockView = (PtClockView) findViewById(R$id.new_clock_view);
        this.mRootView.setOnClickListener(this);
        this.mBackToClock.setOnClickListener(this);
        this.mWallpaperManager = (WallpaperManager) getContext().getSystemService(WallpaperManager.class);
        if (!loadWallpaperBitmap()) {
            Log.e("MotoKeyguardPresentation", "Can't load CLI wallpaper.");
        }
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        if (!isRdpDisplay(this.mDisplay)) {
            View view = this.mDozeScrimView;
            if (!this.mDozing) {
                i = 8;
            }
            view.setVisibility(i);
        }
        updateViewForDoze(this.mDozing);
        this.mUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        this.mBottomMessageMarginBottomDefault = getResources().getDimensionPixelSize(R$dimen.pt_kg_message_bottom);
        this.mBottomMessageArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (MotoKeyguardPresentation.this.mBottomMessageArea.getHeight() != MotoKeyguardPresentation.this.mBottomMessageAreaHeight) {
                    MotoKeyguardPresentation.this.updateSecurityContainerLayout();
                    MotoKeyguardPresentation motoKeyguardPresentation = MotoKeyguardPresentation.this;
                    int unused = motoKeyguardPresentation.mBottomMessageAreaHeight = motoKeyguardPresentation.mBottomMessageArea.getHeight();
                }
            }
        });
    }

    public void dismiss() {
        super.dismiss();
        this.mUpdateMonitor.removeCallback(this.mUpdateMonitorCallback);
    }

    public void onAttachedToWindow() {
        int i;
        super.onAttachedToWindow();
        this.mCurrentSecurityMode = getSecurityMode();
        if (DEBUG) {
            Log.d("MotoKeyguardPresentation", "RDP: onAttachedToWindow: securityMode=" + this.mCurrentSecurityMode);
        }
        int i2 = C06664.f55xdc0e830a[this.mCurrentSecurityMode.ordinal()];
        if (i2 == 1) {
            i = R$layout.kg_pin_view_presentation;
        } else if (i2 == 2) {
            i = R$layout.kg_pattern_view_presentation;
        } else if (i2 != 3) {
            i = -1;
        } else {
            i = R$layout.kg_password_view_presentation;
        }
        if (i != -1) {
            PtKgSecurityView ptKgSecurityView = (PtKgSecurityView) getLayoutInflater().inflate(i, (ViewGroup) null);
            this.mPtKgSecurityView = ptKgSecurityView;
            ptKgSecurityView.setKpMessageUpdateListener(this);
            this.mPtKgSecurityView.setCurrentSecurityMode(this.mCurrentSecurityMode);
            this.mPtKgSecurityView.setPtKDMCallback(this.mPtKDMCallback);
            if (this.mCurrentSecurityMode == KeyguardSecurityModel.SecurityMode.PIN) {
                this.mSecurityContainer.addView(this.mPtKgSecurityView, -1, (int) (((float) PtDisplayFontUtils.getScreenHeight(this.mDisplay)) * (PtDisplayFontUtils.getScreenHeight(this.mDisplay) > PtDisplayFontUtils.getScreenWidth(this.mDisplay) ? 0.54f : 0.68f) * PtDisplayFontUtils.caculateMultiple(this.mFontScale, this.mDisplay)));
            } else {
                this.mSecurityContainer.addView(this.mPtKgSecurityView, -1, -2);
            }
            if (this.mCurrentSecurityMode == KeyguardSecurityModel.SecurityMode.Password) {
                this.mPtKgSecurityView.setImeWindowStatus(this);
                this.mSecurityContainer.setGravity(0);
            }
        } else {
            setClickToUnlockListen(this.mCanSkipBouncer);
        }
        if (this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.Password) {
            this.mSecurityContainer.setGravity(16);
        }
        updateKeyguardView();
    }

    /* renamed from: com.android.keyguard.MotoKeyguardPresentation$4 */
    static /* synthetic */ class C06664 {

        /* renamed from: $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode */
        static final /* synthetic */ int[] f55xdc0e830a;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.android.keyguard.KeyguardSecurityModel$SecurityMode[] r0 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f55xdc0e830a = r0
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.PIN     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f55xdc0e830a     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Pattern     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f55xdc0e830a     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.keyguard.KeyguardSecurityModel$SecurityMode r1 = com.android.keyguard.KeyguardSecurityModel.SecurityMode.Password     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.MotoKeyguardPresentation.C06664.<clinit>():void");
        }
    }

    public void onDetachedFromWindow() {
        if (DEBUG) {
            Log.d("MotoKeyguardPresentation", "RDP: === onDetachedFromWindow ===");
        }
    }

    public void updateSecurityContainerLayout() {
        int screenHeight = PtDisplayFontUtils.getScreenHeight(this.mDisplay);
        int screenWidth = PtDisplayFontUtils.getScreenWidth(this.mDisplay);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mSecurityContainer.getLayoutParams();
        if (screenHeight <= screenWidth || this.mCurrentSecurityMode == KeyguardSecurityModel.SecurityMode.Password) {
            layoutParams.bottomMargin = 0;
            layoutParams.gravity = 16;
        } else {
            layoutParams.bottomMargin = ((FrameLayout.LayoutParams) this.mBottomMessageArea.getLayoutParams()).bottomMargin + this.mContext.getResources().getDimensionPixelSize(R$dimen.pt_kg_security_container_padding_bottom);
            layoutParams.gravity = 80;
        }
        this.mSecurityContainer.setLayoutParams(layoutParams);
    }

    private KeyguardSecurityModel.SecurityMode getSecurityMode() {
        return this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
    }

    public void update(String str) {
        this.mMessageTextView.setText(str);
    }

    public boolean loadWallpaperBitmap() {
        Bitmap bitmap;
        WallpaperManager wallpaperManager = this.mWallpaperManager;
        if (wallpaperManager == null || (bitmap = wallpaperManager.getBitmap(8)) == null || bitmap.isRecycled()) {
            return false;
        }
        try {
            this.mRootView.setBackground(new BitmapDrawable(bitmap));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setImmersivePolicy() {
        /*
            r7 = this;
            java.lang.String r0 = "MotoKeyguardPresentation"
            r1 = 0
            r2 = -2
            android.content.Context r3 = r7.mContext     // Catch:{ all -> 0x0019 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0019 }
            java.lang.String r4 = "immersive_mode_confirmations"
            java.lang.String r3 = android.provider.Settings.Secure.getStringForUser(r3, r4, r2)     // Catch:{ all -> 0x0019 }
            java.lang.String r4 = "confirmed"
            boolean r3 = r4.equals(r3)     // Catch:{ all -> 0x0017 }
            goto L_0x0038
        L_0x0017:
            r4 = move-exception
            goto L_0x001b
        L_0x0019:
            r4 = move-exception
            r3 = r1
        L_0x001b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Error loading confirmations, value="
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = "; throwable = "
            r5.append(r3)
            r5.append(r4)
            java.lang.String r3 = r5.toString()
            android.util.Log.w(r0, r3)
            r3 = 0
        L_0x0038:
            if (r3 != 0) goto L_0x00b5
            android.content.Context r3 = r7.mContext
            android.content.ContentResolver r3 = r3.getContentResolver()
            java.lang.String r4 = "policy_control"
            java.lang.String r3 = android.provider.Settings.Global.getStringForUser(r3, r4, r2)
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x0064
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "immersive.preconfirms="
            r1.append(r5)
            android.content.Context r5 = r7.mContext
            java.lang.String r5 = r5.getPackageName()
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            goto L_0x008a
        L_0x0064:
            android.content.Context r5 = r7.mContext
            java.lang.String r5 = r5.getPackageName()
            boolean r5 = r3.contains(r5)
            if (r5 != 0) goto L_0x008a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            java.lang.String r5 = ":immersive.preconfirms="
            r1.append(r5)
            android.content.Context r5 = r7.mContext
            java.lang.String r5 = r5.getPackageName()
            r1.append(r5)
            java.lang.String r1 = r1.toString()
        L_0x008a:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "setImmersivePolicy Last policy value = "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = "; policyValue = "
            r5.append(r3)
            r5.append(r1)
            java.lang.String r3 = r5.toString()
            android.util.Log.i(r0, r3)
            boolean r0 = android.text.TextUtils.isEmpty(r1)
            if (r0 != 0) goto L_0x00b5
            android.content.Context r7 = r7.mContext
            android.content.ContentResolver r7 = r7.getContentResolver()
            android.provider.Settings.Global.putStringForUser(r7, r4, r1, r2)
        L_0x00b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.MotoKeyguardPresentation.setImmersivePolicy():void");
    }

    /* access modifiers changed from: private */
    public boolean isRdpDisplay(Display display) {
        return display != null && (display.getFlags() & 2048) == 2048;
    }

    public void updateViewForDoze(boolean z) {
        if (DEBUG) {
            Log.d("MotoKeyguardPresentation", "updateViewForDoze: new=" + z + "  old=" + this.mDozing);
        }
        if (isRdpDisplay(this.mDisplay)) {
            this.mDozing = z;
            return;
        }
        int i = 0;
        this.mNewClockView.setVisibility(0);
        if (z != this.mDozing) {
            this.mDozing = z;
            View view = this.mDozeScrimView;
            if (!z) {
                i = 8;
            }
            view.setVisibility(i);
            updateKeyguardView();
        }
    }

    private void setClickToUnlockListen(boolean z) {
        if (DEBUG) {
            Log.d("MotoKeyguardPresentation", "setClickToUnlockListen(" + z + ")");
        }
        if (z || this.mCurrentSecurityMode == KeyguardSecurityModel.SecurityMode.None) {
            this.mRootView.setOnClickListener(new MotoKeyguardPresentation$$ExternalSyntheticLambda0(this));
        } else {
            this.mRootView.setOnClickListener((View.OnClickListener) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setClickToUnlockListen$0(View view) {
        this.mUpdateMonitor.handleExternalAuthenticated();
    }

    public void onClick(View view) {
        boolean userCanSkipBouncer = this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser());
        if (view.getId() == R$id.presentation) {
            if (this.mCurrentMode != Mode.CLOCK || (!userCanSkipBouncer && this.mCurrentSecurityMode != KeyguardSecurityModel.SecurityMode.None)) {
                showBouncer();
                return;
            }
            this.mUpdateMonitor.handleExternalAuthenticated();
            this.mCurrentMode = Mode.BOUNCER;
        } else if (view.getId() == R$id.back_to_clock) {
            showClock();
        }
    }

    private void showBouncer() {
        this.mKeyguardClockContainer.setVisibility(8);
        this.mSecurityContainer.setVisibility(0);
        this.mBottomMessageArea.setVisibility(0);
        this.mBackToClock.setVisibility(0);
        this.mPtKgSecurityView.showBouncer();
    }

    private void showClock() {
        this.mKeyguardClockContainer.setVisibility(0);
        this.mSecurityContainer.setVisibility(8);
        this.mBottomMessageArea.setVisibility(8);
        this.mBackToClock.setVisibility(8);
        this.mPtKgSecurityView.showClock();
    }

    public void updateImeStatus(boolean z) {
        this.mImeShown = z;
        updatePasswordLayout();
    }

    private void updatePasswordLayout() {
        int i;
        int i2;
        int screenHeight = PtDisplayFontUtils.getScreenHeight(this.mDisplay);
        int height = this.mKeyguardClockContainer.getHeight();
        int height2 = this.mSecurityContainer.getHeight();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mBottomMessageArea.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.mSecurityContainer.getLayoutParams();
        if (this.mImeShown) {
            int measuredHeight = (screenHeight / 2) + this.mBottomMessageArea.getMeasuredHeight();
            Resources resources = this.mContext.getResources();
            int i3 = R$dimen.pt_kg_bottom_container_margin_vertical;
            i2 = measuredHeight + resources.getDimensionPixelSize(i3);
            layoutParams2.gravity = 80;
            i = this.mSecurityContainer.getMeasuredHeight() + i2 + this.mContext.getResources().getDimensionPixelSize(i3);
        } else {
            int i4 = ((screenHeight - height) - height2) / 2;
            this.mBottomMessageArea.getHeight();
            i2 = this.mBottomMessageMarginBottomDefault;
            layoutParams2.gravity = 16;
            i = 0;
        }
        layoutParams.bottomMargin = i2;
        this.mBottomMessageArea.setLayoutParams(layoutParams);
        layoutParams2.bottomMargin = i;
        this.mSecurityContainer.setLayoutParams(layoutParams2);
    }
}
