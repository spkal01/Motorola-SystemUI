package com.android.systemui.statusbar.phone;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Region;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.keyguard.CliKeyguardClockSwitch;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.SystemUI;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.CliQSDetail;
import com.android.systemui.p006qs.CliQSPanelNew;
import com.android.systemui.p006qs.PageIndicator;
import com.android.systemui.p006qs.QuickStatusBarHeader;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.row.CliHeadsUpView;
import com.android.systemui.statusbar.notification.row.CliHorizontalSlideView;
import com.android.systemui.statusbar.notification.row.CliMaxHeightHeadsUpView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.CliNotificationStackLayout;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.util.InjectionInflationController;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.systemui.cli.media.CliMediaNotificationController;
import com.motorola.systemui.cli.media.CliMediaOutputRouteLayout;
import com.motorola.systemui.cli.media.CliMediaTutorialLayout;
import com.motorola.systemui.cli.media.CliMediaViewPager;
import com.motorola.systemui.cli.media.CliMediaViewPagerOwn;
import com.motorola.systemui.cli.media.CliMediaVisibleListener;
import java.util.List;

public class CliStatusBar extends SystemUI implements ConfigurationController.ConfigurationListener, KeyguardStateController.Callback, OnHeadsUpChangedListener, CommandQueue.Callbacks, ViewTreeObserver.OnComputeInternalInsetsListener, CliHorizontalSlideView.onSlideOut {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            CliPanelDragView cliPanelDragView;
            if (CliStatusBar.DEBUG) {
                Log.v("Cli_StatusBar", "Received " + intent);
            }
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction()) && (cliPanelDragView = CliStatusBar.this.mCliPanelDragView) != null) {
                cliPanelDragView.closeContent();
            }
        }
    };
    protected NotificationIconContainer mCarouselIcons;
    private ContentObserver mCliDisplaySettingsObserver;
    private CliMediaViewPager mCliLockMediaPanel;
    private CliMediaNotificationController mCliMediaControllerForKeyguard;
    private CliMediaNotificationController mCliMediaControllerForQS;
    private CliMediaViewPager mCliMediaViewPager;
    private CliMediaVisibleListener mCliMediaVisibleListener = new CliMediaVisibleListener() {
        public void visibilityChanged(boolean z) {
            CliStatusBar.this.updateLockIconVisility();
        }
    };
    protected CliNotificationShadeWindowView mCliNotificationShadeWindowView;
    protected CliPanelDragView mCliPanelDragView;
    protected CliQSDetail mCliQSDetail;
    protected CliQSPanelNew mCliQSPanel;
    final ScreenLifecycle.Observer mCliScreenObserver = new ScreenLifecycle.Observer() {
        public void onScreenTurnedOff() {
            CliStatusBar.this.mCliNotificationShadeWindowView.resetCliKeyguard();
            CliNotificationStackLayout cliNotificationStackLayout = CliStatusBar.this.mCliStackScroller;
            if (cliNotificationStackLayout != null) {
                cliNotificationStackLayout.reset();
            }
        }

        public void onLidOpen() {
            if (CliStatusBar.this.mKeyguardViewMediator.isShowingAndNotOccluded()) {
                CliStatusBar.this.mStatusBar.maybeEscalateHeadsUp();
            }
            CliNotificationStackLayout cliNotificationStackLayout = CliStatusBar.this.mCliStackScroller;
            if (cliNotificationStackLayout != null) {
                cliNotificationStackLayout.reset();
            }
        }
    };
    protected CliNotificationStackLayout mCliStackScroller;
    protected CliPhoneStatusBarView mCliStatusBarView;
    protected CliStatusBarWindowView mCliStatusBarWindow;
    protected CliStatusBarWindowController mCliStatusBarWindowController;
    protected final DeviceProvisionedController mDeviceProvisionedController;
    protected final DozeScrimController mDozeScrimController;
    private DozeServiceHost mDozeServiceHost;
    private final FalsingManager mFalsingManager;
    private final NotificationGroupManagerLegacy mGroupManager;
    private C1780H mHandler;
    protected final HeadsUpManagerPhone mHeadsUpManager;
    private CliHorizontalSlideView mHeadsUpView;
    private final HighPriorityProvider mHighPriorityProvider;
    InjectionInflationController mInjectionInflater;
    private boolean mIsStowedSensorRegistered = false;
    private final KeyguardBypassController mKeyguardBypassController;
    protected final KeyguardStateController mKeyguardStateController;
    protected final KeyguardViewMediator mKeyguardViewMediator;
    private View mLockIcon;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    /* access modifiers changed from: private */
    public View mLogoView;
    private CliMediaOutputRouteLayout mMediaOutputLayout;
    private int mNHeadsUpHorizonMargin;
    private int mNHeadsUpVerticalMargin;
    private int mNaturalCliBarHeight = -1;
    private final PowerManager mPM;
    private ScrimController mScrimController;
    private boolean mShouldAdjustInsets = false;
    private SensorManager mSm;
    /* access modifiers changed from: private */
    public StatusBar mStatusBar;
    protected StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    /* access modifiers changed from: private */
    public boolean mStowed = false;
    private final SensorEventListener mStowedListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] fArr = sensorEvent.values;
            boolean z = false;
            if (fArr[0] <= 1.0f ? fArr[0] == 1.0f : fArr[0] != 4.0f) {
                z = true;
            }
            boolean unused = CliStatusBar.this.mStowed;
            boolean unused2 = CliStatusBar.this.mStowed = z;
            if (CliStatusBar.DEBUG) {
                Log.v("Cli_StatusBar", "mStowed=" + CliStatusBar.this.mStowed);
            }
        }
    };
    private Sensor mStowedSensor;
    private Region mTouchableRegion = new Region();
    private KeyguardUpdateMonitor mUpdateMonitor;
    /* access modifiers changed from: private */
    public boolean mUserSetup = true;
    private final DeviceProvisionedController.DeviceProvisionedListener mUserSetupObserver = new DeviceProvisionedController.DeviceProvisionedListener() {
        public void onUserSetupChanged() {
            CliStatusBar cliStatusBar = CliStatusBar.this;
            DeviceProvisionedController deviceProvisionedController = cliStatusBar.mDeviceProvisionedController;
            boolean unused = cliStatusBar.mUserSetup = deviceProvisionedController.isUserSetup(deviceProvisionedController.getCurrentUser());
            if (CliStatusBar.this.mUserSetup) {
                CliStatusBar.this.mLogoView.setVisibility(8);
                CliStatusBar.this.makeLogoVisible(false);
            } else {
                CliStatusBar.this.mLogoView.setVisibility(0);
                CliStatusBar.this.makeLogoVisible(true);
            }
            if (CliStatusBar.DEBUG) {
                Log.d("Cli_StatusBar", "mUserSetup=" + CliStatusBar.this.mUserSetup);
            }
        }
    };

    public interface DozeUpdateTimeCallback {
        void dozeTimeTick();
    }

    public CliStatusBar(Context context, HeadsUpManagerPhone headsUpManagerPhone, KeyguardViewMediator keyguardViewMediator, NotificationGroupManagerLegacy notificationGroupManagerLegacy, DozeScrimController dozeScrimController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager, NotificationLockscreenUserManager notificationLockscreenUserManager, InjectionInflationController injectionInflationController, DozeServiceHost dozeServiceHost, ScrimController scrimController, HighPriorityProvider highPriorityProvider) {
        super(MotoFeature.getInstance(context).getCliBaseContext(context));
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mKeyguardViewMediator = keyguardViewMediator;
        this.mGroupManager = notificationGroupManagerLegacy;
        this.mDozeScrimController = dozeScrimController;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mFalsingManager = falsingManager;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mStatusBar = (StatusBar) Dependency.get(StatusBar.class);
        this.mCliStatusBarWindowController = (CliStatusBarWindowController) Dependency.get(CliStatusBarWindowController.class);
        this.mKeyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        this.mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
        this.mPM = (PowerManager) this.mContext.getSystemService("power");
        this.mInjectionInflater = injectionInflationController;
        this.mHighPriorityProvider = highPriorityProvider;
        this.mDozeServiceHost = dozeServiceHost;
        this.mScrimController = scrimController;
        this.mUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
    }

    public void start() {
        Log.d("Cli_StatusBar", "CliStatusBar start");
        ((CommandQueue) Dependency.get(CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        createAndAddWindows();
        inflateQsPanel(this.mContext, false);
        startKeyguard();
        initCarouselIcons();
        initNotificationListContainer();
        this.mKeyguardStateController.addCallback(this);
        ((ScreenLifecycle) Dependency.get(ScreenLifecycle.class)).addObserver(this.mCliScreenObserver);
        this.mHeadsUpManager.addListener(this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        updateResources();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, intentFilter, (String) null, (Handler) null);
        this.mDeviceProvisionedController.addCallback(this.mUserSetupObserver);
        ((NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class)).getController().addCallback(this.mCliStatusBarWindowController);
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        this.mSm = sensorManager;
        List<Sensor> sensorList = sensorManager.getSensorList(65539);
        if (sensorList.size() > 0) {
            this.mStowedSensor = sensorList.get(0);
        }
        registerCliDisplaySettingsObserver();
        this.mHandler = new C1780H();
    }

    private void updateResources() {
        loadDimens();
    }

    private void loadDimens() {
        Resources resources = this.mContext.getResources();
        this.mNHeadsUpHorizonMargin = resources.getDimensionPixelSize(R$dimen.cli_headup_padding_horizon);
        this.mNHeadsUpVerticalMargin = resources.getDimensionPixelSize(R$dimen.cli_headup_padding_vertical);
    }

    public void onUiModeChanged() {
        if (DEBUG) {
            Log.d("Cli_StatusBar", "UI mode changed");
        }
        inflateQsPanel(this.mContext, true);
    }

    public void onOverlayChanged() {
        if (DEBUG) {
            Log.d("Cli_StatusBar", "Overlay changed");
        }
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, 1500);
    }

    private void registerCliDisplaySettingsObserver() {
        this.mCliDisplaySettingsObserver = new ContentObserver(new Handler(this.mContext.getMainLooper())) {
            public void onChange(boolean z) {
                if (CliStatusBar.DEBUG) {
                    Log.v("Cli_StatusBar", "Cli display density or font setting observer changed");
                }
                CliStatusBar.this.updateConfiguration();
                CliStatusBar.this.onCliDensityOrFontScaleChanged();
            }
        };
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.System.getUriFor("cli_font_scale"), false, this.mCliDisplaySettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("cli_display_density_forced"), false, this.mCliDisplaySettingsObserver, -1);
    }

    public void onKeyguardShowingChanged() {
        updateLockIconVisility();
        updateStowedSensorState(this.mKeyguardStateController.isShowing());
    }

    public void wakeupAndResetScreen(boolean z, boolean z2) {
        PowerManager powerManager = this.mPM;
        if (powerManager != null) {
            if (z) {
                powerManager.wakeUp(SystemClock.uptimeMillis(), "Cli_StatusBar");
            }
            if (z2) {
                this.mPM.userActivity(SystemClock.uptimeMillis(), false);
            }
        }
    }

    private void updateStowedSensorState(boolean z) {
        Sensor sensor = this.mStowedSensor;
        if (sensor == null) {
            return;
        }
        if (z) {
            if (!this.mIsStowedSensorRegistered) {
                this.mSm.registerListener(this.mStowedListener, sensor, 3);
                this.mIsStowedSensorRegistered = true;
            }
        } else if (this.mIsStowedSensorRegistered) {
            this.mSm.unregisterListener(this.mStowedListener, sensor);
            this.mIsStowedSensorRegistered = false;
        }
    }

    /* access modifiers changed from: protected */
    public void startKeyguard() {
        this.mStatusBarKeyguardViewManager.registerCliStatusBar(this, getBouncerContainer());
    }

    private void initCarouselIcons() {
        NotificationIconContainer notificationIconContainer = (NotificationIconContainer) this.mCliNotificationShadeWindowView.findViewById(R$id.carousel_icon_container);
        this.mCarouselIcons = notificationIconContainer;
        notificationIconContainer.setIsCarousel(true);
    }

    private void initNotificationListContainer() {
        CliNotificationStackLayout cliNotificationStackLayout = (CliNotificationStackLayout) this.mCliNotificationShadeWindowView.findViewById(R$id.notification_stack);
        this.mCliStackScroller = cliNotificationStackLayout;
        cliNotificationStackLayout.init(this, this.mCarouselIcons, this.mGroupManager, this.mCliStatusBarWindowController, this.mFalsingManager, this.mStatusBar.getNotificationAcitvityStarter(), this.mLockscreenUserManager, this.mHighPriorityProvider);
        this.mStatusBar.mNotificationPanelViewController.getNotificationStackScrollLayoutController().getNotificationListContainer().setCliNotificationListContainer(this.mCliStackScroller);
    }

    private void createAndAddWindows() {
        addCliStatusBarWindow();
        addCliNotificationShadeWindow();
    }

    private void addCliStatusBarWindow() {
        makeCliStatusBarView();
        this.mCliStatusBarWindowController.addCli(this.mCliStatusBarWindow, getCliStatusBarHeight());
    }

    private void addCliNotificationShadeWindow() {
        makeCliNotificationShadeView();
        this.mCliStatusBarWindowController.addCliPanel(this.mCliNotificationShadeWindowView);
    }

    private void makeCliStatusBarView() {
        this.mContext.setTheme(R$style.Theme_SystemUI);
        inflateStatusBarWindow();
        this.mCliStatusBarWindow.setService(this);
        this.mCliStatusBarView.setBar(this);
    }

    private void makeCliNotificationShadeView() {
        inflateNotificationShadeWindow();
        this.mCliNotificationShadeWindowView.setService(this);
        this.mCliPanelDragView.setService(this);
        this.mCliStatusBarView.setPanelView(this.mCliPanelDragView);
    }

    private void inflateStatusBarWindow() {
        CliStatusBarWindowView cliStatusBarWindowView = (CliStatusBarWindowView) LayoutInflater.from(this.mContext).inflate(R$layout.cli_super_status_bar, (ViewGroup) null);
        this.mCliStatusBarWindow = cliStatusBarWindowView;
        ((StatusIconContainer) cliStatusBarWindowView.findViewById(R$id.cli_statusIcons)).setMaxIconAndDotIcon(4, 1);
        this.mCliStatusBarView = (CliPhoneStatusBarView) this.mCliStatusBarWindow.findViewById(R$id.cli_status_bar);
    }

    private void inflateNotificationShadeWindow() {
        CliNotificationShadeWindowView cliNotificationShadeWindowView = (CliNotificationShadeWindowView) LayoutInflater.from(this.mContext).inflate(R$layout.cli_super_notification_shade, (ViewGroup) null);
        this.mCliNotificationShadeWindowView = cliNotificationShadeWindowView;
        CliPanelDragView notificationPanelView = cliNotificationShadeWindowView.getNotificationPanelView();
        this.mCliPanelDragView = notificationPanelView;
        notificationPanelView.setOnOpenListener(new CliStatusBar$$ExternalSyntheticLambda1(this));
        this.mCliPanelDragView.setOnCloseListener(new CliStatusBar$$ExternalSyntheticLambda0(this));
        ((StatusIconContainer) this.mCliPanelDragView.findViewById(R$id.cli_statusIcons)).setMaxIconAndDotIcon(4, 1);
        CliHorizontalSlideView cliHorizontalSlideView = (CliHorizontalSlideView) this.mCliNotificationShadeWindowView.findViewById(R$id.cli_headsup);
        this.mHeadsUpView = cliHorizontalSlideView;
        cliHorizontalSlideView.setOnSlideOutListener(this);
        this.mLogoView = this.mCliNotificationShadeWindowView.findViewById(R$id.cli_logo_view);
        this.mDozeServiceHost.setCliDozeUpdateTimeCallback(((CliKeyguardClockSwitch) this.mCliNotificationShadeWindowView.findViewById(R$id.cli_keyguard_clock_container)).getDozeUpdateTimeCallback());
        CliMediaViewPager cliMediaViewPager = (CliMediaViewPager) this.mCliPanelDragView.findViewById(R$id.cli_clock_media_panel);
        this.mCliLockMediaPanel = cliMediaViewPager;
        CliMediaViewPagerOwn cliMediaViewPagerOwn = CliMediaViewPagerOwn.Keyguard;
        cliMediaViewPager.setMediaViewPagerOwn(cliMediaViewPagerOwn);
        this.mCliLockMediaPanel.setIsOnKeyguard(true);
        this.mCliLockMediaPanel.registerVisibleChangeListener(this.mCliMediaVisibleListener);
        this.mCliLockMediaPanel.setScrimController(this.mScrimController);
        this.mCliPanelDragView.setKeyguardMediaPanel(this.mCliLockMediaPanel);
        CliMediaNotificationController cliMediaNotificationController = this.mCliMediaControllerForKeyguard;
        if (cliMediaNotificationController != null) {
            cliMediaNotificationController.recycle();
            this.mCliMediaControllerForKeyguard = null;
        }
        CliMediaNotificationController cliMediaNotificationController2 = new CliMediaNotificationController(this.mContext, cliMediaViewPagerOwn);
        this.mCliMediaControllerForKeyguard = cliMediaNotificationController2;
        cliMediaNotificationController2.setCliViewPager(this.mCliLockMediaPanel);
        this.mCliLockMediaPanel.setPageIndicator((PageIndicator) this.mCliPanelDragView.findViewById(R$id.cli_clock_media_panel_indicator));
        this.mLockIcon = this.mCliNotificationShadeWindowView.findViewById(R$id.cli_locked);
        CliMediaTutorialLayout cliMediaTutorialLayout = (CliMediaTutorialLayout) this.mCliPanelDragView.findViewById(R$id.cli_media_panel_tutorial);
        cliMediaTutorialLayout.setCliMediaViewPagerOwn(cliMediaViewPagerOwn);
        this.mCliLockMediaPanel.setTutorialView(cliMediaTutorialLayout);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$inflateNotificationShadeWindow$0() {
        CliQSPanelNew cliQSPanelNew = this.mCliQSPanel;
        if (cliQSPanelNew != null) {
            cliQSPanelNew.setListening(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$inflateNotificationShadeWindow$1() {
        CliQSPanelNew cliQSPanelNew = this.mCliQSPanel;
        if (cliQSPanelNew != null) {
            cliQSPanelNew.setListening(false);
        }
    }

    /* access modifiers changed from: private */
    public void updateConfiguration() {
        Configuration configuration = this.mContext.getResources().getConfiguration();
        String stringForUser = MotorolaSettings.Secure.getStringForUser(this.mContext.getContentResolver(), "cli_display_density_forced", -2);
        if (stringForUser == null || stringForUser.isEmpty()) {
            configuration.densityDpi = 0;
        } else {
            configuration.densityDpi = Integer.parseInt(stringForUser);
        }
        configuration.fontScale = MotorolaSettings.System.getFloatForUser(this.mContext.getContentResolver(), "cli_font_scale", 1.0f, -2);
        Resources resources = this.mContext.getResources();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        if (DEBUG) {
            Log.d("Cli_StatusBar", "Cli display Change?  densityDpi: " + configuration.densityDpi + " fontScale: " + configuration.fontScale);
        }
    }

    public void onCliDensityOrFontScaleChanged() {
        updateCliStatusBarHeight();
        this.mStatusBar.mPresenter.onDensityOrFontScaleChanged();
        this.mCliStatusBarView.onCliDensityOrFontScaleChanged();
        this.mCliPanelDragView.onCliDensityOrFontScaleChanged();
        inflateQsPanel(this.mContext, true);
    }

    private void updateCliStatusBarHeight() {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_height);
        if (this.mNaturalCliBarHeight != dimensionPixelSize) {
            this.mNaturalCliBarHeight = dimensionPixelSize;
            this.mCliStatusBarWindowController.updateStatusBarHeight(dimensionPixelSize);
        }
    }

    /* access modifiers changed from: protected */
    public void inflateQsPanel(Context context, boolean z) {
        FrameLayout frameLayout = (FrameLayout) this.mCliPanelDragView.findViewById(R$id.cli_status_bar_panel);
        if (z) {
            frameLayout.removeAllViews();
            CliQSPanelNew cliQSPanelNew = this.mCliQSPanel;
            if (cliQSPanelNew != null) {
                cliQSPanelNew.setListening(false);
            }
        }
        LayoutInflater from = LayoutInflater.from(context);
        InjectionInflationController injectionInflationController = this.mInjectionInflater;
        int i = R$style.Theme_SystemUI_QuickSettings;
        CliQSPanelNew cliQSPanelNew2 = (CliQSPanelNew) injectionInflationController.injectable(from.cloneInContext(new ContextThemeWrapper(context, i))).inflate(R$layout.cli_qs_panel_new, (ViewGroup) null);
        this.mCliQSPanel = cliQSPanelNew2;
        cliQSPanelNew2.setListening(this.mCliPanelDragView.isOpen());
        frameLayout.addView(this.mCliQSPanel);
        CliMediaViewPager cliMediaViewPager = (CliMediaViewPager) this.mInjectionInflater.injectable(LayoutInflater.from(new ContextThemeWrapper(context, i))).inflate(R$layout.cli_media_view_pager, (ViewGroup) null);
        this.mCliMediaViewPager = cliMediaViewPager;
        CliMediaViewPagerOwn cliMediaViewPagerOwn = CliMediaViewPagerOwn.QS;
        cliMediaViewPager.setMediaViewPagerOwn(cliMediaViewPagerOwn);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, 0);
        layoutParams.gravity = 80;
        layoutParams.bottomMargin = this.mContext.getResources().getDimensionPixelSize(R$dimen.cli_media_view_margin_bottom);
        frameLayout.addView(this.mCliMediaViewPager, layoutParams);
        this.mCliMediaViewPager.registerVisibleChangeListener(this.mCliQSPanel.getCliMediaVisibleListener());
        PageIndicator pageIndicator = new PageIndicator(context, (AttributeSet) null);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-2, -2);
        layoutParams2.gravity = 81;
        layoutParams2.bottomMargin = this.mContext.getResources().getDimensionPixelSize(R$dimen.cli_media_view_indicator_margin_bottom);
        frameLayout.addView(pageIndicator, layoutParams2);
        this.mCliMediaViewPager.setPageIndicator(pageIndicator);
        CliMediaNotificationController cliMediaNotificationController = this.mCliMediaControllerForQS;
        if (cliMediaNotificationController != null) {
            cliMediaNotificationController.recycle();
            this.mCliMediaControllerForQS = null;
        }
        CliMediaNotificationController cliMediaNotificationController2 = new CliMediaNotificationController(context, cliMediaViewPagerOwn);
        this.mCliMediaControllerForQS = cliMediaNotificationController2;
        cliMediaNotificationController2.setCliViewPager(this.mCliMediaViewPager);
        this.mCliPanelDragView.setMediaViewPager(this.mCliMediaViewPager);
        CliMediaTutorialLayout cliMediaTutorialLayout = (CliMediaTutorialLayout) this.mInjectionInflater.injectable(LayoutInflater.from(new ContextThemeWrapper(context, i))).inflate(R$layout.cli_media_panel_tutorial, (ViewGroup) null);
        cliMediaTutorialLayout.setCliMediaViewPagerOwn(cliMediaViewPagerOwn);
        new LinearLayout.LayoutParams(-1, -1);
        frameLayout.addView(cliMediaTutorialLayout);
        this.mCliMediaViewPager.setTutorialView(cliMediaTutorialLayout);
        CliMediaOutputRouteLayout cliMediaOutputRouteLayout = (CliMediaOutputRouteLayout) this.mInjectionInflater.injectable(LayoutInflater.from(new ContextThemeWrapper(context, i))).inflate(R$layout.cli_media_output_panel, (ViewGroup) null);
        this.mMediaOutputLayout = cliMediaOutputRouteLayout;
        frameLayout.addView(cliMediaOutputRouteLayout);
        this.mMediaOutputLayout.setVisibility(8);
        this.mMediaOutputLayout.setMediaViewPager(this.mCliMediaViewPager);
        CliQSDetail cliQSDetail = (CliQSDetail) this.mInjectionInflater.injectable(LayoutInflater.from(new ContextThemeWrapper(context, i))).inflate(R$layout.cli_qs_detail, (ViewGroup) null);
        this.mCliQSDetail = cliQSDetail;
        frameLayout.addView(cliQSDetail);
        this.mCliQSDetail.setQsPanel(this.mCliQSPanel, (QuickStatusBarHeader) null, (View) null);
    }

    /* access modifiers changed from: protected */
    public ViewGroup getBouncerContainer() {
        return this.mCliNotificationShadeWindowView;
    }

    public int getCliStatusBarHeight() {
        if (this.mNaturalCliBarHeight < 0) {
            this.mNaturalCliBarHeight = this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_height);
        }
        return this.mNaturalCliBarHeight;
    }

    /* access modifiers changed from: package-private */
    public boolean panelEnabled() {
        return this.mUserSetup;
    }

    public void makeExpandedVisible(boolean z) {
        this.mCliStatusBarWindowController.setPanelVisible(z);
        if (!z && this.mCliQSDetail.isShowingDetail()) {
            this.mCliQSDetail.closeDetail();
        }
        if (!z && this.mCliMediaViewPager.isMediaPanelExpanded()) {
            this.mCliMediaViewPager.setExpandedState(false);
        }
        if (!z && this.mMediaOutputLayout.getVisibility() != 8) {
            this.mMediaOutputLayout.setVisibility(8);
        }
        this.mStatusBar.setInteracting(1, z);
    }

    /* access modifiers changed from: private */
    public void makeLogoVisible(boolean z) {
        this.mCliStatusBarWindowController.setLogoVisible(z);
    }

    public void dismissKeyguard() {
        this.mStatusBar.makeExpandedInvisible();
    }

    public boolean isExpandedVisible() {
        return this.mCliStatusBarWindowController.isPanelVisible();
    }

    public void animateExpandedVisible(boolean z) {
        if (z) {
            this.mCliPanelDragView.openContent();
        } else {
            this.mCliPanelDragView.closeContent();
        }
    }

    public void collapseCliStack() {
        this.mCliStackScroller.setCardVisibility(false, false);
    }

    public boolean onBackPressed() {
        if (this.mStatusBarKeyguardViewManager.onBackPressed(true)) {
            return true;
        }
        CliQSDetail cliQSDetail = this.mCliQSDetail;
        if (cliQSDetail == null || !cliQSDetail.isShowingDetail()) {
            return false;
        }
        this.mCliQSDetail.closeDetail();
        return false;
    }

    public void updateNavigationBar() {
        this.mCliStatusBarWindowController.updateNavigationBar();
    }

    private void updateTouchableRegion() {
        boolean z = this.mHeadsUpView.getChildCount() != 0;
        if (z != this.mShouldAdjustInsets) {
            if (z) {
                this.mCliPanelDragView.getViewTreeObserver().addOnComputeInternalInsetsListener(this);
            } else {
                this.mCliPanelDragView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
            }
            this.mShouldAdjustInsets = z;
        }
    }

    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        if (this.mCliStatusBarWindowController.shouldSetTouchRegion() && this.mHeadsUpView.getChildCount() != 0) {
            this.mTouchableRegion.set(0, 0, this.mCliStatusBarWindow.getWidth(), getCliStatusBarHeight() + this.mHeadsUpView.getChildAt(0).getHeight());
            internalInsetsInfo.setTouchableInsets(3);
            internalInsetsInfo.touchableRegion.set(this.mTouchableRegion);
        }
    }

    public void onHeadsUpPinned(NotificationEntry notificationEntry) {
        boolean z = DEBUG;
        if (z) {
            Log.d("Cli_StatusBar", "onHeadsUpPinned count=" + this.mHeadsUpView.getChildCount());
        }
        ExpandableNotificationRow row = notificationEntry != null ? notificationEntry.getRow() : null;
        if (row == null || !HeadsUpUtil.isCliHeadsUpNotification(notificationEntry.getSbn())) {
            Log.i("Cli_StatusBar", "onHeadsUpPinned headsUp is null!");
            return;
        }
        if (row.getCliRemoteView() == null && z) {
            Log.d("Cli_StatusBar", "HeadsUp came from updated!");
        }
        if (addHeadUpView(notificationEntry, row)) {
            this.mCliStatusBarWindowController.setForceStatusBarVisible(true);
            this.mCliStatusBarWindowController.setHeadsUpShowing(true);
            if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                this.mStatusBarKeyguardViewManager.reset(true);
            }
            if (isExpandedVisible()) {
                animateExpandedVisible(false);
            }
            if (z) {
                Log.d("Cli_StatusBar", "force headsup=" + notificationEntry.cliForcedHeadsUp + ";mStowed=" + this.mStowed);
            }
            this.mHeadsUpView.setVisibility(0);
            if (notificationEntry.cliForcedHeadsUp && !this.mStowed) {
                wakeupAndResetScreen(true, true);
                notificationEntry.cliForcedHeadsUp = false;
            }
            updateTouchableRegion();
            return;
        }
        Log.i("Cli_StatusBar", "onHeadsUpPinned add headsUp failed!");
    }

    public void onHeadsUpUnPinned(NotificationEntry notificationEntry) {
        if (DEBUG) {
            Log.d("Cli_StatusBar", "onHeadsUpUnPinned count=" + this.mHeadsUpView.getChildCount());
        }
        ExpandableNotificationRow row = notificationEntry != null ? notificationEntry.getRow() : null;
        if (row == null || row.getCliHeadsUp() != this.mHeadsUpView.getChildAt(0)) {
            Log.i("Cli_StatusBar", "onHeadsUpUnPinned headsUp is null!");
            return;
        }
        this.mHeadsUpView.removeView(row.getCliHeadsUp());
        if (this.mHeadsUpView.getChildCount() == 0) {
            this.mHeadsUpView.setVisibility(8);
            this.mCliStatusBarWindowController.setForceStatusBarVisible(false);
            this.mCliStatusBarWindowController.setHeadsUpShowing(false);
        }
        updateTouchableRegion();
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        if (DEBUG) {
            Log.d("Cli_StatusBar", "onHeadsUpStateChanged isHeadsUp=" + z);
        }
    }

    public void slideToTop(CliHeadsUpView cliHeadsUpView) {
        if (cliHeadsUpView != null) {
            if (DEBUG) {
                Log.d("Cli_StatusBar", "slideToTop count=" + this.mHeadsUpView.getChildCount());
            }
            this.mHeadsUpView.removeView(cliHeadsUpView);
            if (this.mHeadsUpView.getChildCount() == 0) {
                this.mHeadsUpView.setVisibility(8);
                this.mCliStatusBarWindowController.setForceStatusBarVisible(false);
                this.mCliStatusBarWindowController.setHeadsUpShowing(false);
            }
            updateTouchableRegion();
        }
    }

    private boolean addHeadUpView(final NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow) {
        CliMaxHeightHeadsUpView cliHeadsUp = expandableNotificationRow.getCliHeadsUp();
        if (cliHeadsUp == null) {
            return false;
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = this.mNHeadsUpVerticalMargin;
        int i = this.mNHeadsUpHorizonMargin;
        layoutParams.rightMargin = i;
        layoutParams.leftMargin = i;
        this.mHeadsUpView.removeAllViews();
        this.mHeadsUpView.addView(cliHeadsUp, layoutParams);
        this.mHeadsUpView.setOnDoubleClick(new CliHeadsUpView.OnDoubleClickListener() {
            public void onDoubleClick(View view) {
                if (CliStatusBar.DEBUG) {
                    Log.d("Cli_StatusBar", "onDoubleClick view=" + view);
                }
                if ((view instanceof CliHeadsUpView) && ((CliHeadsUpView) view).getContainingNotification() != null && notificationEntry.getSbn() != null) {
                    CliStatusBar.this.mStatusBar.triggerNotificationClickAndRequestUnlockInternal(notificationEntry.getSbn().getKey(), (PendingIntent) null, (Intent) null);
                }
            }
        });
        this.mHeadsUpView.enableSlide(!notificationEntry.getSbn().isOngoing());
        return true;
    }

    public void userActivity() {
        wakeupAndResetScreen(false, true);
    }

    public void wallpaperChanged() {
        CliPanelDragView cliPanelDragView = this.mCliPanelDragView;
        if (cliPanelDragView != null && !cliPanelDragView.loadWallpaperBitmap()) {
            Log.e("Cli_StatusBar", "Can't load CLI wallpaper.");
        }
    }

    public boolean getStowedState() {
        return this.mStowed;
    }

    /* access modifiers changed from: private */
    public void updateLockIconVisility() {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("update cli-statusbar lockIcon visility:  isSecure=");
            sb.append(this.mKeyguardStateController.isMethodSecure());
            sb.append("  isShowing=");
            sb.append(this.mKeyguardStateController.isShowing());
            sb.append("  canSkipBouncer=");
            sb.append(this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser()));
            sb.append("  mediaVisibility=");
            sb.append(this.mCliLockMediaPanel.getVisibility() == 0);
            Log.d("Cli_StatusBar", sb.toString());
        }
        if (this.mLockIcon != null) {
            if (!this.mKeyguardStateController.isMethodSecure() || !this.mKeyguardStateController.isShowing() || this.mUpdateMonitor.getUserCanSkipBouncer(KeyguardUpdateMonitor.getCurrentUser()) || this.mCliLockMediaPanel.getVisibility() != 0) {
                this.mLockIcon.setVisibility(8);
            } else {
                this.mLockIcon.setVisibility(0);
            }
        }
    }

    /* renamed from: com.android.systemui.statusbar.phone.CliStatusBar$H */
    private final class C1780H extends Handler {
        private C1780H() {
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                CliStatusBar cliStatusBar = CliStatusBar.this;
                cliStatusBar.inflateQsPanel(cliStatusBar.mContext, true);
                CliQSPanelNew cliQSPanelNew = CliStatusBar.this.mCliQSPanel;
                if (cliQSPanelNew != null) {
                    cliQSPanelNew.requestLayout();
                }
            }
        }
    }
}
