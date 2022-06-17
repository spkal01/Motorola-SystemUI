package com.android.systemui.statusbar.phone;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.quickaccesswallet.GetWalletCardsError;
import android.service.quickaccesswallet.GetWalletCardsResponse;
import android.service.quickaccesswallet.QuickAccessWalletClient;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.Utils;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.camera.CameraIntents;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.dagger.ControlsComponent;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.p004ui.ControlsActivity;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.doze.util.BurnInHelperKt;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.IntentButtonProvider;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.PreviewInflater;
import com.android.systemui.tuner.LockscreenFragment;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.wallet.controller.QuickAccessWalletController;
import com.android.systemui.wallet.p010ui.WalletActivity;
import java.util.List;
import java.util.concurrent.Executor;

public class KeyguardBottomAreaView extends FrameLayout implements View.OnClickListener, KeyguardStateController.Callback, AccessibilityController.AccessibilityStateChangedCallback {
    /* access modifiers changed from: private */
    public static boolean DEBUG_NOTIFICATION = (!"user".equals(Build.TYPE));
    /* access modifiers changed from: private */
    public static final Intent PHONE_INTENT = new Intent("android.intent.action.DIAL");
    private AccessibilityController mAccessibilityController;
    private View.AccessibilityDelegate mAccessibilityDelegate;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityStarter mActivityStarter;
    private KeyguardAffordanceHelper mAffordanceHelper;
    private int mBurnInXOffset;
    private int mBurnInYOffset;
    private View mCameraPreview;
    private WalletCardRetriever mCardRetriever;
    /* access modifiers changed from: private */
    public boolean mControlServicesAvailable;
    private ImageView mControlsButton;
    private ControlsComponent mControlsComponent;
    private float mDarkAmount;
    private final BroadcastReceiver mDevicePolicyReceiver;
    private boolean mDozing;
    private FalsingManager mFalsingManager;
    private FlashlightController mFlashlightController;
    /* access modifiers changed from: private */
    public boolean mHasCard;
    private ViewGroup mIndicationArea;
    private int mIndicationBottomMargin;
    private int mIndicationPadding;
    private TextView mIndicationText;
    private TextView mIndicationTextBottom;
    /* access modifiers changed from: private */
    public boolean mIsGesturalMode;
    /* access modifiers changed from: private */
    public KeyguardStateController mKeyguardStateController;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public KeyguardAffordanceView mLeftAffordanceView;
    /* access modifiers changed from: private */
    public Drawable mLeftAssistIcon;
    private IntentButtonProvider.IntentButton mLeftButton;
    private String mLeftButtonStr;
    private ExtensionController.Extension<IntentButtonProvider.IntentButton> mLeftExtension;
    /* access modifiers changed from: private */
    public boolean mLeftIsVoiceAssist;
    private View mLeftPreview;
    private ControlsListingController.ControlsListingCallback mListingCallback;
    /* access modifiers changed from: private */
    public MotoDisplayManager mMotoDisplayManager;
    NavigationModeController.ModeChangedListener mNavigationChangedListener;
    private ViewGroup mOverlayContainer;
    private ViewGroup mPreviewContainer;
    private PreviewInflater mPreviewInflater;
    private boolean mPrewarmBound;
    private final ServiceConnection mPrewarmConnection;
    /* access modifiers changed from: private */
    public Messenger mPrewarmMessenger;
    /* access modifiers changed from: private */
    public QuickAccessWalletController mQuickAccessWalletController;
    /* access modifiers changed from: private */
    public KeyguardAffordanceView mRightAffordanceView;
    private IntentButtonProvider.IntentButton mRightButton;
    private String mRightButtonStr;
    private ExtensionController.Extension<IntentButtonProvider.IntentButton> mRightExtension;
    /* access modifiers changed from: private */
    public final boolean mShowCameraAffordance;
    /* access modifiers changed from: private */
    public final boolean mShowLeftAffordance;
    /* access modifiers changed from: private */
    public StatusBar mStatusBar;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback;
    /* access modifiers changed from: private */
    public boolean mUserSetupComplete;
    /* access modifiers changed from: private */
    public ImageView mWalletButton;

    /* access modifiers changed from: private */
    public static boolean isSuccessfulLaunch(int i) {
        return i == 0 || i == 3 || i == 2;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        this.mIsGesturalMode = i == 2;
        updateCameraVisibility();
        updateLeftAffordance();
    }

    public KeyguardBottomAreaView(Context context) {
        this(context, (AttributeSet) null);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public KeyguardBottomAreaView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHasCard = false;
        this.mCardRetriever = new WalletCardRetriever();
        this.mControlServicesAvailable = false;
        this.mPrewarmConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Messenger unused = KeyguardBottomAreaView.this.mPrewarmMessenger = new Messenger(iBinder);
            }

            public void onServiceDisconnected(ComponentName componentName) {
                Messenger unused = KeyguardBottomAreaView.this.mPrewarmMessenger = null;
            }
        };
        this.mRightButton = new DefaultRightButton();
        this.mLeftButton = new DefaultLeftButton();
        this.mListingCallback = new ControlsListingController.ControlsListingCallback() {
            public void onServicesUpdated(List<ControlsServiceInfo> list) {
                KeyguardBottomAreaView.this.post(new KeyguardBottomAreaView$2$$ExternalSyntheticLambda0(this, list));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onServicesUpdated$0(List list) {
                boolean z = !list.isEmpty();
                if (z != KeyguardBottomAreaView.this.mControlServicesAvailable) {
                    boolean unused = KeyguardBottomAreaView.this.mControlServicesAvailable = z;
                    KeyguardBottomAreaView.this.updateControlsVisibility();
                    KeyguardBottomAreaView.this.updateAffordanceColors();
                }
            }
        };
        this.mNavigationChangedListener = new KeyguardBottomAreaView$$ExternalSyntheticLambda2(this);
        this.mAccessibilityDelegate = new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                String str;
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                if (view == KeyguardBottomAreaView.this.mRightAffordanceView) {
                    str = KeyguardBottomAreaView.this.getResources().getString(R$string.camera_label);
                } else if (view == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                    str = KeyguardBottomAreaView.this.mLeftIsVoiceAssist ? KeyguardBottomAreaView.this.getResources().getString(R$string.voice_assist_label) : KeyguardBottomAreaView.this.getResources().getString(R$string.phone_label);
                } else {
                    str = null;
                }
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, str));
            }

            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                if (i == 16) {
                    if (view == KeyguardBottomAreaView.this.mRightAffordanceView) {
                        KeyguardBottomAreaView.this.launchCamera("lockscreen_affordance");
                        return true;
                    } else if (view == KeyguardBottomAreaView.this.mLeftAffordanceView) {
                        KeyguardBottomAreaView.this.launchLeftAffordance();
                        return true;
                    }
                }
                return super.performAccessibilityAction(view, i, bundle);
            }
        };
        this.mDevicePolicyReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                KeyguardBottomAreaView.this.post(new Runnable() {
                    public void run() {
                        KeyguardBottomAreaView.this.updateCameraVisibility();
                    }
                });
            }
        };
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            public void onUserSwitchComplete(int i) {
                KeyguardBottomAreaView.this.updateCameraVisibility();
            }

            public void onUserUnlocked() {
                KeyguardBottomAreaView.this.inflateCameraPreview();
                KeyguardBottomAreaView.this.updateCameraVisibility();
                KeyguardBottomAreaView.this.updateLeftAffordance();
            }
        };
        this.mShowLeftAffordance = getResources().getBoolean(R$bool.config_keyguardShowLeftAffordance);
        this.mShowCameraAffordance = getResources().getBoolean(R$bool.config_keyguardShowCameraAffordance);
    }

    public void initFrom(KeyguardBottomAreaView keyguardBottomAreaView) {
        setStatusBar(keyguardBottomAreaView.mStatusBar);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPreviewInflater = new PreviewInflater(this.mContext, new LockPatternUtils(this.mContext), new ActivityIntentHelper(this.mContext));
        this.mOverlayContainer = (ViewGroup) findViewById(R$id.overlay_container);
        this.mRightAffordanceView = (KeyguardAffordanceView) findViewById(R$id.camera_button);
        this.mLeftAffordanceView = (KeyguardAffordanceView) findViewById(R$id.left_button);
        this.mWalletButton = (ImageView) findViewById(R$id.wallet_button);
        this.mControlsButton = (ImageView) findViewById(R$id.controls_button);
        this.mIndicationArea = (ViewGroup) findViewById(R$id.keyguard_indication_area);
        this.mIndicationText = (TextView) findViewById(R$id.keyguard_indication_text);
        this.mIndicationTextBottom = (TextView) findViewById(R$id.keyguard_indication_text_bottom);
        this.mIndicationBottomMargin = getResources().getDimensionPixelSize(R$dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = getResources().getDimensionPixelSize(R$dimen.default_burn_in_prevention_offset);
        updateCameraVisibility();
        this.mMotoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
        KeyguardStateController keyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        this.mKeyguardStateController = keyguardStateController;
        keyguardStateController.addCallback(this);
        setClipChildren(false);
        setClipToPadding(false);
        this.mRightAffordanceView.setOnClickListener(this);
        this.mLeftAffordanceView.setOnClickListener(this);
        initAccessibility();
        this.mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        this.mFlashlightController = (FlashlightController) Dependency.get(FlashlightController.class);
        this.mAccessibilityController = (AccessibilityController) Dependency.get(AccessibilityController.class);
        this.mActivityIntentHelper = new ActivityIntentHelper(getContext());
        this.mIndicationPadding = getResources().getDimensionPixelSize(R$dimen.keyguard_indication_area_padding);
        updateWalletVisibility();
        updateControlsVisibility();
    }

    public void setPreviewContainer(ViewGroup viewGroup) {
        this.mPreviewContainer = viewGroup;
        inflateCameraPreview();
        updateLeftAffordance();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        Class<IntentButtonProvider> cls = IntentButtonProvider.class;
        Class<IntentButtonProvider.IntentButton> cls2 = IntentButtonProvider.IntentButton.class;
        Class cls3 = ExtensionController.class;
        super.onAttachedToWindow();
        this.mAccessibilityController.addStateChangedCallback(this);
        this.mRightExtension = ((ExtensionController) Dependency.get(cls3)).newExtension(cls2).withPlugin(cls, "com.android.systemui.action.PLUGIN_LOCKSCREEN_RIGHT_BUTTON", KeyguardBottomAreaView$$ExternalSyntheticLambda4.INSTANCE).withTunerFactory(new LockscreenFragment.LockButtonFactory(this.mContext, "sysui_keyguard_right")).withDefault(new KeyguardBottomAreaView$$ExternalSyntheticLambda11(this)).withCallback(new KeyguardBottomAreaView$$ExternalSyntheticLambda8(this)).build();
        this.mLeftExtension = ((ExtensionController) Dependency.get(cls3)).newExtension(cls2).withPlugin(cls, "com.android.systemui.action.PLUGIN_LOCKSCREEN_LEFT_BUTTON", KeyguardBottomAreaView$$ExternalSyntheticLambda3.INSTANCE).withTunerFactory(new LockscreenFragment.LockButtonFactory(this.mContext, "sysui_keyguard_left")).withDefault(new KeyguardBottomAreaView$$ExternalSyntheticLambda10(this)).withCallback(new KeyguardBottomAreaView$$ExternalSyntheticLambda7(this)).build();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        getContext().registerReceiverAsUser(this.mDevicePolicyReceiver, UserHandle.ALL, intentFilter, (String) null, (Handler) null);
        KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        keyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        this.mKeyguardStateController.addCallback(this);
        this.mIsGesturalMode = 2 == ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this.mNavigationChangedListener);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ IntentButtonProvider.IntentButton lambda$onAttachedToWindow$2() {
        return new DefaultRightButton();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ IntentButtonProvider.IntentButton lambda$onAttachedToWindow$5() {
        return new DefaultLeftButton();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardStateController.removeCallback(this);
        this.mAccessibilityController.removeStateChangedCallback(this);
        this.mRightExtension.destroy();
        this.mLeftExtension.destroy();
        getContext().unregisterReceiver(this.mDevicePolicyReceiver);
        this.mKeyguardUpdateMonitor.removeCallback(this.mUpdateMonitorCallback);
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).removeListener(this.mNavigationChangedListener);
        QuickAccessWalletController quickAccessWalletController = this.mQuickAccessWalletController;
        if (quickAccessWalletController != null) {
            quickAccessWalletController.unregisterWalletChangeObservers(QuickAccessWalletController.WalletChangeEvent.WALLET_PREFERENCE_CHANGE, QuickAccessWalletController.WalletChangeEvent.DEFAULT_PAYMENT_APP_CHANGE);
        }
        ControlsComponent controlsComponent = this.mControlsComponent;
        if (controlsComponent != null) {
            controlsComponent.getControlsListingController().ifPresent(new KeyguardBottomAreaView$$ExternalSyntheticLambda5(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDetachedFromWindow$7(ControlsListingController controlsListingController) {
        controlsListingController.removeCallback(this.mListingCallback);
    }

    private void initAccessibility() {
        this.mLeftAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
        this.mRightAffordanceView.setAccessibilityDelegate(this.mAccessibilityDelegate);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mIndicationBottomMargin = getResources().getDimensionPixelSize(R$dimen.keyguard_indication_margin_bottom);
        this.mBurnInYOffset = getResources().getDimensionPixelSize(R$dimen.default_burn_in_prevention_offset);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mIndicationArea.getLayoutParams();
        int i = marginLayoutParams.bottomMargin;
        int i2 = this.mIndicationBottomMargin;
        if (i != i2) {
            marginLayoutParams.bottomMargin = i2;
            this.mIndicationArea.setLayoutParams(marginLayoutParams);
        }
        this.mIndicationTextBottom.setTextSize(0, (float) getResources().getDimensionPixelSize(17105562));
        this.mIndicationText.setTextSize(0, (float) getResources().getDimensionPixelSize(17105562));
        ViewGroup.LayoutParams layoutParams = this.mRightAffordanceView.getLayoutParams();
        Resources resources = getResources();
        int i3 = R$dimen.keyguard_affordance_width;
        layoutParams.width = resources.getDimensionPixelSize(i3);
        Resources resources2 = getResources();
        int i4 = R$dimen.keyguard_affordance_height;
        layoutParams.height = resources2.getDimensionPixelSize(i4);
        this.mRightAffordanceView.setLayoutParams(layoutParams);
        updateRightAffordanceIcon();
        ViewGroup.LayoutParams layoutParams2 = this.mLeftAffordanceView.getLayoutParams();
        layoutParams2.width = getResources().getDimensionPixelSize(i3);
        layoutParams2.height = getResources().getDimensionPixelSize(i4);
        this.mLeftAffordanceView.setLayoutParams(layoutParams2);
        updateLeftAffordanceIcon();
        ViewGroup.LayoutParams layoutParams3 = this.mWalletButton.getLayoutParams();
        Resources resources3 = getResources();
        int i5 = R$dimen.keyguard_affordance_fixed_width;
        layoutParams3.width = resources3.getDimensionPixelSize(i5);
        Resources resources4 = getResources();
        int i6 = R$dimen.keyguard_affordance_fixed_height;
        layoutParams3.height = resources4.getDimensionPixelSize(i6);
        this.mWalletButton.setLayoutParams(layoutParams3);
        ViewGroup.LayoutParams layoutParams4 = this.mControlsButton.getLayoutParams();
        layoutParams4.width = getResources().getDimensionPixelSize(i5);
        layoutParams4.height = getResources().getDimensionPixelSize(i6);
        this.mControlsButton.setLayoutParams(layoutParams4);
        this.mIndicationPadding = getResources().getDimensionPixelSize(R$dimen.keyguard_indication_area_padding);
        updateWalletVisibility();
        updateAffordanceColors();
    }

    private void updateRightAffordanceIcon() {
        IntentButtonProvider.IntentButton.IconState icon = this.mRightButton.getIcon();
        this.mRightAffordanceView.setVisibility((this.mDozing || !icon.isVisible) ? 8 : 0);
        if (!(icon.drawable == this.mRightAffordanceView.getDrawable() && icon.tint == this.mRightAffordanceView.shouldTint())) {
            this.mRightAffordanceView.setImageDrawable(icon.drawable, icon.tint);
        }
        this.mRightAffordanceView.setContentDescription(icon.contentDescription);
    }

    public void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
        updateCameraVisibility();
    }

    public void setAffordanceHelper(KeyguardAffordanceHelper keyguardAffordanceHelper) {
        this.mAffordanceHelper = keyguardAffordanceHelper;
    }

    public void setUserSetupComplete(boolean z) {
        this.mUserSetupComplete = z;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
    }

    private Intent getCameraIntent() {
        return this.mRightButton.getIntent();
    }

    private boolean isMotoCameraAppDefault(Intent intent, int i) {
        String str;
        ResolveInfo resolveActivityAsUser = getContext().getPackageManager().resolveActivityAsUser(intent, 65664, i);
        if (resolveActivityAsUser == null) {
            return false;
        }
        ApplicationInfo applicationInfo = resolveActivityAsUser.activityInfo.applicationInfo;
        String motoCameraAppPackageName = getMotoCameraAppPackageName(getContext());
        if (motoCameraAppPackageName == null || (str = applicationInfo.processName) == null || !str.equals(motoCameraAppPackageName)) {
            return false;
        }
        return true;
    }

    public Intent checkConvertCameraIntentForMotoCamera(Intent intent, Context context) {
        Intent intent2;
        boolean equals = TextUtils.equals(intent.getAction(), CameraIntents.getSecureCameraIntent().getAction());
        String preloadedCamera = getPreloadedCamera(context);
        String motoCameraAppPackageName = getMotoCameraAppPackageName(context);
        intent.setPackage((String) null);
        if (preloadedCamera == null || motoCameraAppPackageName == null || !preloadedCamera.equals(motoCameraAppPackageName) || !isMotoCameraAppDefault(intent, KeyguardUpdateMonitor.getCurrentUser())) {
            return intent;
        }
        intent.setPackage(preloadedCamera);
        if (equals) {
            intent2 = CameraIntents.getSecureCameraIntent().cloneFilter();
            intent2.setAction("motorola.camera.intent.action.STILL_IMAGE_PREVIEW_SECURE");
            intent2.addFlags(8388608);
            if (!isMotoCameraAppDefault(intent2, KeyguardUpdateMonitor.getCurrentUser())) {
                return intent;
            }
        } else {
            intent2 = CameraIntents.getInsecureCameraIntent().cloneFilter();
            intent2.setAction("motorola.camera.intent.action.STILL_IMAGE_PREVIEW");
            if (!isMotoCameraAppDefault(intent2, KeyguardUpdateMonitor.getCurrentUser())) {
                return intent;
            }
        }
        return intent2;
    }

    public ResolveInfo resolveCameraIntent() {
        return this.mContext.getPackageManager().resolveActivityAsUser(getCameraIntent(), 65536, KeyguardUpdateMonitor.getCurrentUser());
    }

    /* access modifiers changed from: private */
    public void updateCameraVisibility() {
        KeyguardAffordanceView keyguardAffordanceView = this.mRightAffordanceView;
        if (keyguardAffordanceView != null) {
            keyguardAffordanceView.setVisibility((this.mDozing || !this.mShowCameraAffordance || !this.mRightButton.getIcon().isVisible) ? 8 : 0);
        }
    }

    private void updateLeftAffordanceIcon() {
        int i = 8;
        if (!this.mShowLeftAffordance || this.mDozing) {
            this.mLeftAffordanceView.setVisibility(8);
            return;
        }
        IntentButtonProvider.IntentButton.IconState icon = this.mLeftButton.getIcon();
        KeyguardAffordanceView keyguardAffordanceView = this.mLeftAffordanceView;
        if (icon.isVisible) {
            i = 0;
        }
        keyguardAffordanceView.setVisibility(i);
        if (!(icon.drawable == this.mLeftAffordanceView.getDrawable() && icon.tint == this.mLeftAffordanceView.shouldTint())) {
            this.mLeftAffordanceView.setImageDrawable(icon.drawable, icon.tint);
        }
        this.mLeftAffordanceView.setContentDescription(icon.contentDescription);
    }

    /* access modifiers changed from: private */
    public void updateWalletVisibility() {
        QuickAccessWalletController quickAccessWalletController;
        if (this.mDozing || (quickAccessWalletController = this.mQuickAccessWalletController) == null || !quickAccessWalletController.isWalletEnabled() || !this.mHasCard) {
            this.mWalletButton.setVisibility(8);
            if (this.mControlsButton.getVisibility() == 8) {
                this.mIndicationArea.setPadding(0, 0, 0, 0);
            }
        } else if (!this.mShowCameraAffordance) {
            this.mWalletButton.setVisibility(0);
            this.mWalletButton.setOnClickListener(new KeyguardBottomAreaView$$ExternalSyntheticLambda0(this));
            ViewGroup viewGroup = this.mIndicationArea;
            int i = this.mIndicationPadding;
            viewGroup.setPadding(i, 0, i, 0);
        }
    }

    /* access modifiers changed from: private */
    public void updateControlsVisibility() {
        ControlsComponent controlsComponent = this.mControlsComponent;
        if (controlsComponent != null) {
            boolean booleanValue = ((Boolean) controlsComponent.getControlsController().map(KeyguardBottomAreaView$$ExternalSyntheticLambda9.INSTANCE).orElse(Boolean.FALSE)).booleanValue();
            if (this.mDozing || !booleanValue || !this.mControlServicesAvailable || this.mControlsComponent.getVisibility() != ControlsComponent.Visibility.AVAILABLE) {
                this.mControlsButton.setVisibility(8);
                if (this.mWalletButton.getVisibility() == 8) {
                    this.mIndicationArea.setPadding(0, 0, 0, 0);
                }
            } else if (!this.mShowLeftAffordance) {
                this.mControlsButton.setVisibility(0);
                this.mControlsButton.setOnClickListener(new KeyguardBottomAreaView$$ExternalSyntheticLambda1(this));
                ViewGroup viewGroup = this.mIndicationArea;
                int i = this.mIndicationPadding;
                viewGroup.setPadding(i, 0, i, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ Boolean lambda$updateControlsVisibility$8(ControlsController controlsController) {
        return Boolean.valueOf(controlsController.getFavorites().size() > 0);
    }

    public boolean isLeftVoiceAssist() {
        return this.mLeftIsVoiceAssist;
    }

    /* access modifiers changed from: private */
    public boolean isPhoneVisible() {
        PackageManager packageManager = this.mContext.getPackageManager();
        if (!packageManager.hasSystemFeature("android.hardware.telephony") || packageManager.resolveActivity(PHONE_INTENT, 0) == null) {
            return false;
        }
        return true;
    }

    public void onStateChanged(boolean z, boolean z2) {
        this.mRightAffordanceView.setClickable(z2);
        this.mLeftAffordanceView.setClickable(z2);
        this.mRightAffordanceView.setFocusable(z);
        this.mLeftAffordanceView.setFocusable(z);
    }

    public void onClick(View view) {
        if (view == this.mRightAffordanceView) {
            launchCamera("lockscreen_affordance");
        } else if (view == this.mLeftAffordanceView) {
            launchLeftAffordance();
        }
    }

    public void bindCameraPrewarmService() {
        Bundle bundle;
        String string;
        ActivityInfo targetActivityInfo = this.mActivityIntentHelper.getTargetActivityInfo(getCameraIntent(), KeyguardUpdateMonitor.getCurrentUser(), true);
        if (targetActivityInfo != null && (bundle = targetActivityInfo.metaData) != null && (string = bundle.getString("android.media.still_image_camera_preview_service")) != null) {
            Intent intent = new Intent();
            intent.setClassName(targetActivityInfo.packageName, string);
            intent.setAction("android.service.media.CameraPrewarmService.ACTION_PREWARM");
            try {
                if (getContext().bindServiceAsUser(intent, this.mPrewarmConnection, 67108865, new UserHandle(-2))) {
                    this.mPrewarmBound = true;
                }
            } catch (SecurityException e) {
                Log.w("StatusBar/KeyguardBottomAreaView", "Unable to bind to prewarm service package=" + targetActivityInfo.packageName + " class=" + string, e);
            }
        }
    }

    public void unbindCameraPrewarmService(boolean z) {
        if (this.mPrewarmBound) {
            Messenger messenger = this.mPrewarmMessenger;
            if (messenger != null && z) {
                try {
                    messenger.send(Message.obtain((Handler) null, 1));
                } catch (RemoteException e) {
                    Log.w("StatusBar/KeyguardBottomAreaView", "Error sending camera fired message", e);
                }
            }
            this.mContext.unbindService(this.mPrewarmConnection);
            this.mPrewarmBound = false;
        }
    }

    public String getPreloadedCamera(Context context) {
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(new Intent("android.media.action.IMAGE_CAPTURE"), 64)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if ((activityInfo.applicationInfo.flags & 1) == 1) {
                return activityInfo.packageName;
            }
        }
        return null;
    }

    public String getMotoCameraAppPackageName(Context context) {
        String str;
        try {
            str = context.getResources().getString(17039984);
        } catch (Resources.NotFoundException e) {
            Log.e("StatusBar/KeyguardBottomAreaView", "Can't get moto camera package name", e);
            str = "";
        }
        if (DEBUG_NOTIFICATION) {
            Log.d("StatusBar/KeyguardBottomAreaView", "motoCameraAppPackageName " + str);
        }
        return str;
    }

    public void launchCamera(String str) {
        launchCamera(str, false);
    }

    public void launchCamera(String str, boolean z) {
        final Intent checkConvertCameraIntentForMotoCamera = checkConvertCameraIntentForMotoCamera(getCameraIntent().cloneFilter(), getContext());
        checkConvertCameraIntentForMotoCamera.putExtra("com.android.systemui.camera_launch_source", str);
        if (z) {
            checkConvertCameraIntentForMotoCamera.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        }
        boolean wouldLaunchResolverActivity = this.mActivityIntentHelper.wouldLaunchResolverActivity(checkConvertCameraIntentForMotoCamera, KeyguardUpdateMonitor.getCurrentUser());
        boolean isSecureCameraIntent = CameraIntents.isSecureCameraIntent(checkConvertCameraIntentForMotoCamera);
        if (DEBUG_NOTIFICATION) {
            Log.d("StatusBar/KeyguardBottomAreaView", "launchCamera: intent " + checkConvertCameraIntentForMotoCamera);
            Log.d("StatusBar/KeyguardBottomAreaView", "launchCamera: source " + str + " isSecure = " + isSecureCameraIntent);
            StringBuilder sb = new StringBuilder();
            sb.append("launchCamera: wouldLaunchResolverActivity ");
            sb.append(wouldLaunchResolverActivity);
            Log.d("StatusBar/KeyguardBottomAreaView", sb.toString());
        }
        if (wouldLaunchResolverActivity || this.mCameraPreview == null) {
            inflateCameraPreview();
        }
        if (isSecureCameraIntent && !wouldLaunchResolverActivity) {
            checkConvertCameraIntentForMotoCamera.addFlags(67108864);
            AsyncTask.execute(new Runnable() {
                /* JADX WARNING: Removed duplicated region for block: B:11:0x003d A[Catch:{ RemoteException -> 0x00fb }] */
                /* JADX WARNING: Removed duplicated region for block: B:22:0x008d A[Catch:{ RemoteException -> 0x00fb }] */
                /* JADX WARNING: Removed duplicated region for block: B:23:0x00bb A[Catch:{ RemoteException -> 0x00fb }] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r22 = this;
                        r1 = r22
                        java.lang.String r2 = "StatusBar/KeyguardBottomAreaView"
                        android.app.ActivityOptions r0 = android.app.ActivityOptions.makeBasic()
                        r3 = 1
                        r0.setDisallowEnterPictureInPictureWhileLaunching(r3)
                        r4 = 3
                        r0.setRotationAnimationHint(r4)
                        r4 = -96
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r5 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r6 = r5.getContext()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r5 = r5.getPreloadedCamera(r6)     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r6 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r7 = r6.getContext()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r6 = r6.getMotoCameraAppPackageName(r7)     // Catch:{ RemoteException -> 0x00fb }
                        r7 = 0
                        r8 = 268435456(0x10000000, float:2.5243549E-29)
                        if (r5 == 0) goto L_0x0035
                        boolean r6 = r5.equals(r6)     // Catch:{ RemoteException -> 0x00fb }
                        if (r6 != 0) goto L_0x0032
                        goto L_0x0035
                    L_0x0032:
                        r18 = r7
                        goto L_0x0037
                    L_0x0035:
                        r18 = r8
                    L_0x0037:
                        boolean r6 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.DEBUG_NOTIFICATION     // Catch:{ RemoteException -> 0x00fb }
                        if (r6 == 0) goto L_0x0060
                        r6 = r18 & r8
                        if (r6 <= 0) goto L_0x0047
                        java.lang.String r5 = "Calling default implementation using FLAG_ACTIVITY_NEW_TASK"
                        android.util.Log.d(r2, r5)     // Catch:{ RemoteException -> 0x00fb }
                        goto L_0x0060
                    L_0x0047:
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x00fb }
                        r6.<init>()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r7 = "Calling "
                        r6.append(r7)     // Catch:{ RemoteException -> 0x00fb }
                        r6.append(r5)     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r5 = " without FLAG_ACTIVITY_NEW_TASK"
                        r6.append(r5)     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r5 = r6.toString()     // Catch:{ RemoteException -> 0x00fb }
                        android.util.Log.d(r2, r5)     // Catch:{ RemoteException -> 0x00fb }
                    L_0x0060:
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r5 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r5 = r5.mContext     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.moto.MotoFeature r5 = com.android.systemui.moto.MotoFeature.getInstance(r5)     // Catch:{ RemoteException -> 0x00fb }
                        boolean r5 = r5.isSupportCli()     // Catch:{ RemoteException -> 0x00fb }
                        if (r5 == 0) goto L_0x0081
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r5 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r5 = r5.mContext     // Catch:{ RemoteException -> 0x00fb }
                        boolean r5 = com.android.systemui.moto.MotoFeature.isLidClosed(r5)     // Catch:{ RemoteException -> 0x00fb }
                        if (r5 == 0) goto L_0x0081
                        android.content.Intent r5 = r0     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView.convertCliCameraIntentIfNeeded(r5, r0)     // Catch:{ RemoteException -> 0x00fb }
                    L_0x0081:
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r5 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.StatusBar r5 = r5.mStatusBar     // Catch:{ RemoteException -> 0x00fb }
                        boolean r5 = r5.isFolioClosedAndDozing()     // Catch:{ RemoteException -> 0x00fb }
                        if (r5 == 0) goto L_0x00bb
                        android.content.Intent r0 = r0     // Catch:{ RemoteException -> 0x00fb }
                        r5 = r18 | r8
                        r0.addFlags(r5)     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r0 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r0 = r0.mContext     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Intent r5 = r0     // Catch:{ RemoteException -> 0x00fb }
                        r6 = 201326592(0xc000000, float:9.8607613E-32)
                        android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r0, r3, r5, r6)     // Catch:{ RemoteException -> 0x00fb }
                        android.os.Bundle r10 = new android.os.Bundle     // Catch:{ RemoteException -> 0x00fb }
                        r10.<init>()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r3 = "CAMERA_PENDING_INTENT"
                        r10.putParcelable(r3, r0)     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r0 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.doze.MotoDisplayManager r5 = r0.mMotoDisplayManager     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r6 = "FOLIO_LAUNCH_CAMERA_BY_WIGGLE"
                        r7 = 0
                        r8 = 0
                        r9 = 0
                        r5.notifyEvent(r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x00fb }
                        goto L_0x0101
                    L_0x00bb:
                        android.app.IActivityTaskManager r9 = android.app.ActivityTaskManager.getService()     // Catch:{ RemoteException -> 0x00fb }
                        r10 = 0
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r3 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r3 = r3.getContext()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r11 = r3.getBasePackageName()     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r3 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r3 = r3.getContext()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r12 = r3.getAttributionTag()     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Intent r13 = r0     // Catch:{ RemoteException -> 0x00fb }
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r3 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this     // Catch:{ RemoteException -> 0x00fb }
                        android.content.Context r3 = r3.getContext()     // Catch:{ RemoteException -> 0x00fb }
                        android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ RemoteException -> 0x00fb }
                        java.lang.String r14 = r13.resolveTypeIfNeeded(r3)     // Catch:{ RemoteException -> 0x00fb }
                        r15 = 0
                        r16 = 0
                        r17 = 0
                        r19 = 0
                        android.os.Bundle r20 = r0.toBundle()     // Catch:{ RemoteException -> 0x00fb }
                        android.os.UserHandle r0 = android.os.UserHandle.CURRENT     // Catch:{ RemoteException -> 0x00fb }
                        int r21 = r0.getIdentifier()     // Catch:{ RemoteException -> 0x00fb }
                        int r0 = r9.startActivityAsUser(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ RemoteException -> 0x00fb }
                        r4 = r0
                        goto L_0x0101
                    L_0x00fb:
                        r0 = move-exception
                        java.lang.String r3 = "Unable to start camera activity"
                        android.util.Log.w(r2, r3, r0)
                    L_0x0101:
                        boolean r0 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.isSuccessfulLaunch(r4)
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView r2 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.this
                        com.android.systemui.statusbar.phone.KeyguardBottomAreaView$4$1 r3 = new com.android.systemui.statusbar.phone.KeyguardBottomAreaView$4$1
                        r3.<init>(r0)
                        r2.post(r3)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.C18044.run():void");
                }
            });
        } else if (this.mStatusBar.isFolioClosedAndDozing()) {
            PendingIntent activity = PendingIntent.getActivity(this.mContext, 1, checkConvertCameraIntentForMotoCamera, 201326592);
            Bundle bundle = new Bundle();
            bundle.putParcelable("CAMERA_PENDING_INTENT", activity);
            this.mMotoDisplayManager.notifyEvent("FOLIO_LAUNCH_CAMERA_BY_WIGGLE", false, (String) null, (String) null, bundle);
        } else {
            this.mActivityStarter.startActivity(checkConvertCameraIntentForMotoCamera, false, (ActivityStarter.Callback) new ActivityStarter.Callback() {
                public void onActivityStarted(int i) {
                    KeyguardBottomAreaView.this.unbindCameraPrewarmService(KeyguardBottomAreaView.isSuccessfulLaunch(i));
                }
            });
        }
    }

    public void setDarkAmount(float f) {
        if (f != this.mDarkAmount) {
            this.mDarkAmount = f;
            dozeTimeTick();
        }
    }

    public void launchLeftAffordance() {
        if (this.mLeftIsVoiceAssist) {
            launchVoiceAssist();
        } else {
            launchPhone();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void launchVoiceAssist() {
        C18076 r1 = new Runnable() {
            public void run() {
                ((AssistManager) Dependency.get(AssistManager.class)).launchVoiceAssistFromKeyguard();
            }
        };
        if (!this.mKeyguardStateController.canDismissLockScreen()) {
            ((Executor) Dependency.get(Dependency.BACKGROUND_EXECUTOR)).execute(r1);
        } else {
            this.mStatusBar.executeRunnableDismissingKeyguard(r1, (Runnable) null, !TextUtils.isEmpty(this.mRightButtonStr) && ((TunerService) Dependency.get(TunerService.class)).getValue("sysui_keyguard_right_unlock", 1) != 0, false, true);
        }
    }

    /* access modifiers changed from: private */
    public boolean canLaunchVoiceAssist() {
        return ((AssistManager) Dependency.get(AssistManager.class)).canVoiceAssistBeLaunchedFromKeyguard();
    }

    private void launchPhone() {
        final TelecomManager from = TelecomManager.from(this.mContext);
        if (from.isInCall()) {
            AsyncTask.execute(new Runnable() {
                public void run() {
                    from.showInCallScreen(false);
                }
            });
            return;
        }
        boolean z = true;
        if (TextUtils.isEmpty(this.mLeftButtonStr) || ((TunerService) Dependency.get(TunerService.class)).getValue("sysui_keyguard_left_unlock", 1) == 0) {
            z = false;
        }
        this.mActivityStarter.startActivity(this.mLeftButton.getIntent(), z);
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (view == this && i == 0) {
            updateCameraVisibility();
        }
    }

    public KeyguardAffordanceView getLeftView() {
        return this.mLeftAffordanceView;
    }

    public KeyguardAffordanceView getRightView() {
        return this.mRightAffordanceView;
    }

    public View getLeftPreview() {
        return this.mLeftPreview;
    }

    public View getRightPreview() {
        return this.mCameraPreview;
    }

    public View getIndicationArea() {
        return this.mIndicationArea;
    }

    public void onUnlockedChanged() {
        updateCameraVisibility();
    }

    public void onKeyguardShowingChanged() {
        QuickAccessWalletController quickAccessWalletController;
        if (this.mKeyguardStateController.isShowing() && (quickAccessWalletController = this.mQuickAccessWalletController) != null) {
            quickAccessWalletController.queryWalletCards(this.mCardRetriever);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void inflateCameraPreview() {
        /*
            r4 = this;
            android.view.ViewGroup r0 = r4.mPreviewContainer
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            android.view.View r1 = r4.mCameraPreview
            r2 = 0
            if (r1 == 0) goto L_0x0015
            r0.removeView(r1)
            int r0 = r1.getVisibility()
            if (r0 != 0) goto L_0x0015
            r0 = 1
            goto L_0x0016
        L_0x0015:
            r0 = r2
        L_0x0016:
            com.android.systemui.statusbar.policy.PreviewInflater r1 = r4.mPreviewInflater
            android.content.Intent r3 = r4.getCameraIntent()
            android.view.View r1 = r1.inflatePreview((android.content.Intent) r3)
            r4.mCameraPreview = r1
            if (r1 == 0) goto L_0x0032
            android.view.ViewGroup r3 = r4.mPreviewContainer
            r3.addView(r1)
            android.view.View r1 = r4.mCameraPreview
            if (r0 == 0) goto L_0x002e
            goto L_0x002f
        L_0x002e:
            r2 = 4
        L_0x002f:
            r1.setVisibility(r2)
        L_0x0032:
            com.android.systemui.statusbar.phone.KeyguardAffordanceHelper r4 = r4.mAffordanceHelper
            if (r4 == 0) goto L_0x0039
            r4.updatePreviews()
        L_0x0039:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.KeyguardBottomAreaView.inflateCameraPreview():void");
    }

    private void updateLeftPreview() {
        Class cls = AssistManager.class;
        ViewGroup viewGroup = this.mPreviewContainer;
        if (viewGroup != null) {
            View view = this.mLeftPreview;
            if (view != null) {
                viewGroup.removeView(view);
            }
            if (!this.mLeftIsVoiceAssist) {
                this.mLeftPreview = this.mPreviewInflater.inflatePreview(this.mLeftButton.getIntent());
            } else if (((AssistManager) Dependency.get(cls)).getVoiceInteractorComponentName() != null) {
                this.mLeftPreview = this.mPreviewInflater.inflatePreviewFromService(((AssistManager) Dependency.get(cls)).getVoiceInteractorComponentName());
            }
            View view2 = this.mLeftPreview;
            if (view2 != null) {
                this.mPreviewContainer.addView(view2);
                this.mLeftPreview.setVisibility(4);
            }
            KeyguardAffordanceHelper keyguardAffordanceHelper = this.mAffordanceHelper;
            if (keyguardAffordanceHelper != null) {
                keyguardAffordanceHelper.updatePreviews();
            }
        }
    }

    public void startFinishDozeAnimation() {
        long j = 0;
        if (this.mWalletButton.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mWalletButton, 0);
        }
        if (this.mControlsButton.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mControlsButton, 0);
        }
        if (this.mLeftAffordanceView.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mLeftAffordanceView, 0);
            j = 48;
        }
        if (this.mRightAffordanceView.getVisibility() == 0) {
            startFinishDozeAnimationElement(this.mRightAffordanceView, j);
        }
    }

    private void startFinishDozeAnimationElement(View view, long j) {
        view.setAlpha(0.0f);
        view.setTranslationY((float) (view.getHeight() / 2));
        view.animate().alpha(1.0f).translationY(0.0f).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(j).setDuration(250);
    }

    public void updateLeftAffordance() {
        updateLeftAffordanceIcon();
        updateLeftPreview();
    }

    /* access modifiers changed from: private */
    /* renamed from: setRightButton */
    public void lambda$onAttachedToWindow$3(IntentButtonProvider.IntentButton intentButton) {
        this.mRightButton = intentButton;
        updateRightAffordanceIcon();
        updateCameraVisibility();
        inflateCameraPreview();
    }

    /* access modifiers changed from: private */
    /* renamed from: setLeftButton */
    public void lambda$onAttachedToWindow$6(IntentButtonProvider.IntentButton intentButton) {
        this.mLeftButton = intentButton;
        if (!(intentButton instanceof DefaultLeftButton)) {
            this.mLeftIsVoiceAssist = false;
        }
        updateLeftAffordance();
    }

    public void setDozing(boolean z, boolean z2) {
        this.mDozing = z;
        updateCameraVisibility();
        updateLeftAffordanceIcon();
        updateWalletVisibility();
        updateControlsVisibility();
        if (z) {
            this.mOverlayContainer.setVisibility(4);
            return;
        }
        this.mOverlayContainer.setVisibility(0);
        if (z2) {
            startFinishDozeAnimation();
        }
    }

    public void dozeTimeTick() {
        this.mIndicationArea.setTranslationY(((float) (BurnInHelperKt.getBurnInOffset(this.mBurnInYOffset * 2, false) - this.mBurnInYOffset)) * this.mDarkAmount);
    }

    public void setAntiBurnInOffsetX(int i) {
        if (this.mBurnInXOffset != i) {
            this.mBurnInXOffset = i;
            this.mIndicationArea.setTranslationX((float) i);
        }
    }

    public void setAffordanceAlpha(float f) {
        this.mLeftAffordanceView.setAlpha(f);
        this.mRightAffordanceView.setAlpha(f);
        this.mIndicationArea.setAlpha(f);
        this.mWalletButton.setAlpha(f);
        this.mControlsButton.setAlpha(f);
    }

    private class DefaultLeftButton implements IntentButtonProvider.IntentButton {
        private IntentButtonProvider.IntentButton.IconState mIconState;

        private DefaultLeftButton() {
            this.mIconState = new IntentButtonProvider.IntentButton.IconState();
        }

        public IntentButtonProvider.IntentButton.IconState getIcon() {
            KeyguardBottomAreaView keyguardBottomAreaView = KeyguardBottomAreaView.this;
            boolean unused = keyguardBottomAreaView.mLeftIsVoiceAssist = keyguardBottomAreaView.canLaunchVoiceAssist();
            boolean z = true;
            if (KeyguardBottomAreaView.this.mLeftIsVoiceAssist) {
                IntentButtonProvider.IntentButton.IconState iconState = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !KeyguardBottomAreaView.this.mShowLeftAffordance || KeyguardBottomAreaView.this.mIsGesturalMode) {
                    z = false;
                }
                iconState.isVisible = z;
                if (KeyguardBottomAreaView.this.mLeftAssistIcon == null) {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(R$drawable.ic_mic_26dp);
                } else {
                    this.mIconState.drawable = KeyguardBottomAreaView.this.mLeftAssistIcon;
                }
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R$string.accessibility_voice_assist_button);
            } else {
                IntentButtonProvider.IntentButton.IconState iconState2 = this.mIconState;
                if (!KeyguardBottomAreaView.this.mUserSetupComplete || !KeyguardBottomAreaView.this.mShowLeftAffordance || !KeyguardBottomAreaView.this.isPhoneVisible() || KeyguardBottomAreaView.this.mIsGesturalMode) {
                    z = false;
                }
                iconState2.isVisible = z;
                this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(17303683);
                this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R$string.accessibility_phone_button);
            }
            return this.mIconState;
        }

        public Intent getIntent() {
            return KeyguardBottomAreaView.PHONE_INTENT;
        }
    }

    private class DefaultRightButton implements IntentButtonProvider.IntentButton {
        private IntentButtonProvider.IntentButton.IconState mIconState;

        private DefaultRightButton() {
            this.mIconState = new IntentButtonProvider.IntentButton.IconState();
        }

        public IntentButtonProvider.IntentButton.IconState getIcon() {
            boolean z = true;
            boolean z2 = KeyguardBottomAreaView.this.mStatusBar != null && !KeyguardBottomAreaView.this.mStatusBar.isCameraAllowedByAdmin();
            IntentButtonProvider.IntentButton.IconState iconState = this.mIconState;
            if (z2 || !KeyguardBottomAreaView.this.mShowCameraAffordance || !KeyguardBottomAreaView.this.mUserSetupComplete || KeyguardBottomAreaView.this.resolveCameraIntent() == null || KeyguardBottomAreaView.this.mIsGesturalMode) {
                z = false;
            }
            iconState.isVisible = z;
            this.mIconState.drawable = KeyguardBottomAreaView.this.mContext.getDrawable(R$drawable.ic_camera_alt_24dp);
            this.mIconState.contentDescription = KeyguardBottomAreaView.this.mContext.getString(R$string.accessibility_camera_button);
            return this.mIconState;
        }

        public Intent getIntent() {
            boolean canDismissLockScreen = KeyguardBottomAreaView.this.mKeyguardStateController.canDismissLockScreen();
            if (!KeyguardBottomAreaView.this.mKeyguardStateController.isMethodSecure() || canDismissLockScreen) {
                return CameraIntents.getInsecureCameraIntent(KeyguardBottomAreaView.this.getContext());
            }
            return CameraIntents.getSecureCameraIntent(KeyguardBottomAreaView.this.getContext());
        }
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        int safeInsetBottom = windowInsets.getDisplayCutout() != null ? windowInsets.getDisplayCutout().getSafeInsetBottom() : 0;
        if (isPaddingRelative()) {
            setPaddingRelative(getPaddingStart(), getPaddingTop(), getPaddingEnd(), safeInsetBottom);
        } else {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), safeInsetBottom);
        }
        return windowInsets;
    }

    public void setFalsingManager(FalsingManager falsingManager) {
        this.mFalsingManager = falsingManager;
    }

    public void initWallet(QuickAccessWalletController quickAccessWalletController) {
        this.mQuickAccessWalletController = quickAccessWalletController;
        quickAccessWalletController.setupWalletChangeObservers(this.mCardRetriever, QuickAccessWalletController.WalletChangeEvent.WALLET_PREFERENCE_CHANGE, QuickAccessWalletController.WalletChangeEvent.DEFAULT_PAYMENT_APP_CHANGE);
        this.mQuickAccessWalletController.updateWalletPreference();
        this.mQuickAccessWalletController.queryWalletCards(this.mCardRetriever);
        updateWalletVisibility();
        updateAffordanceColors();
    }

    /* access modifiers changed from: private */
    public void updateAffordanceColors() {
        int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(this.mContext, 16842806);
        this.mWalletButton.getDrawable().setTint(colorAttrDefaultColor);
        this.mControlsButton.getDrawable().setTint(colorAttrDefaultColor);
        ColorStateList colorAttr = Utils.getColorAttr(this.mContext, 17956910);
        this.mWalletButton.setBackgroundTintList(colorAttr);
        this.mControlsButton.setBackgroundTintList(colorAttr);
    }

    public void initControls(ControlsComponent controlsComponent) {
        this.mControlsComponent = controlsComponent;
        controlsComponent.getControlsListingController().ifPresent(new KeyguardBottomAreaView$$ExternalSyntheticLambda6(this));
        updateAffordanceColors();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initControls$9(ControlsListingController controlsListingController) {
        controlsListingController.addCallback(this.mListingCallback);
    }

    /* access modifiers changed from: private */
    public void onWalletClick(View view) {
        if (!this.mFalsingManager.isFalseTap(1)) {
            if (this.mHasCard) {
                this.mContext.startActivity(new Intent(this.mContext, WalletActivity.class).setAction("android.intent.action.VIEW").addFlags(335544320));
            } else if (this.mQuickAccessWalletController.getWalletClient().createWalletIntent() == null) {
                Log.w("StatusBar/KeyguardBottomAreaView", "Could not get intent of the wallet app.");
            } else {
                this.mActivityStarter.postStartActivityDismissingKeyguard(this.mQuickAccessWalletController.getWalletClient().createWalletIntent(), 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onControlsClick(View view) {
        if (!this.mFalsingManager.isFalseTap(1)) {
            Intent putExtra = new Intent(this.mContext, ControlsActivity.class).addFlags(335544320).putExtra("extra_animate", true);
            if (this.mControlsComponent.getVisibility() == ControlsComponent.Visibility.AVAILABLE) {
                this.mContext.startActivity(putExtra);
            } else {
                this.mActivityStarter.postStartActivityDismissingKeyguard(putExtra, 0);
            }
        }
    }

    private class WalletCardRetriever implements QuickAccessWalletClient.OnWalletCardsRetrievedCallback {
        private WalletCardRetriever() {
        }

        public void onWalletCardsRetrieved(GetWalletCardsResponse getWalletCardsResponse) {
            boolean unused = KeyguardBottomAreaView.this.mHasCard = !getWalletCardsResponse.getWalletCards().isEmpty();
            Drawable tileIcon = KeyguardBottomAreaView.this.mQuickAccessWalletController.getWalletClient().getTileIcon();
            if (tileIcon != null) {
                KeyguardBottomAreaView.this.mWalletButton.setImageDrawable(tileIcon);
            }
            KeyguardBottomAreaView.this.updateWalletVisibility();
            KeyguardBottomAreaView.this.updateAffordanceColors();
        }

        public void onWalletCardRetrievalError(GetWalletCardsError getWalletCardsError) {
            boolean unused = KeyguardBottomAreaView.this.mHasCard = false;
            KeyguardBottomAreaView.this.updateWalletVisibility();
            KeyguardBottomAreaView.this.updateAffordanceColors();
        }
    }

    public static void convertCliCameraIntentIfNeeded(Intent intent, ActivityOptions activityOptions) {
        String str;
        if (intent != null) {
            String action = intent.getAction();
            boolean z = false;
            boolean z2 = CameraIntents.getSecureCameraIntent().getAction().equals(action) || "motorola.camera.intent.action.STILL_IMAGE_PREVIEW_SECURE".equals(action);
            if (CameraIntents.getInsecureCameraIntent().getAction().equals(action) || "motorola.camera.intent.action.STILL_IMAGE_PREVIEW".equals(action)) {
                z = true;
            }
            if (z2 || z) {
                if (z2) {
                    str = CameraIntents.getSecureCameraIntent().getAction();
                } else {
                    str = CameraIntents.getInsecureCameraIntent().getAction();
                }
                intent.setAction(str);
                if (activityOptions != null) {
                    activityOptions.setLaunchDisplayId(1);
                }
                if (DEBUG_NOTIFICATION) {
                    Log.d("StatusBar/KeyguardBottomAreaView", "Launch CLI Camera with intent: " + intent);
                }
            }
        }
    }
}
