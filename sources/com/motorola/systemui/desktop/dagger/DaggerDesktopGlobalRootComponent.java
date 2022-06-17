package com.motorola.systemui.desktop.dagger;

import android.animation.AnimationHandler;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.INotificationManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.app.smartspace.SmartspaceManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.om.OverlayManager;
import android.content.pm.IPackageManager;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.NightDisplayListener;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.AudioManager;
import android.media.IAudioService;
import android.media.MediaRouter2Manager;
import android.media.session.MediaSessionManager;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.permission.PermissionManager;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.service.quickaccesswallet.QuickAccessWalletClient;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import android.view.CrossWindowBlurListeners;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.app.AssistUtils;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.NotificationMessagingUtil;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.CarrierTextManager;
import com.android.keyguard.CarrierTextManager_Builder_Factory;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardClockSwitchController;
import com.android.keyguard.KeyguardDisplayManager;
import com.android.keyguard.KeyguardDisplayManager_Factory;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardSecurityModel_Factory;
import com.android.keyguard.KeyguardSliceView;
import com.android.keyguard.KeyguardSliceViewController;
import com.android.keyguard.KeyguardSliceViewController_Factory;
import com.android.keyguard.KeyguardStatusView;
import com.android.keyguard.KeyguardStatusViewController;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitor_Factory;
import com.android.keyguard.clock.ClockManager;
import com.android.keyguard.clock.ClockManager_Factory;
import com.android.keyguard.clock.ClockModule_ProvideClockInfoListFactory;
import com.android.keyguard.dagger.KeyguardStatusViewComponent;
import com.android.keyguard.dagger.KeyguardStatusViewModule_GetKeyguardClockSwitchFactory;
import com.android.keyguard.dagger.KeyguardStatusViewModule_GetKeyguardSliceViewFactory;
import com.android.p011wm.shell.FullscreenTaskListener;
import com.android.p011wm.shell.RootTaskDisplayAreaOrganizer;
import com.android.p011wm.shell.ShellCommandHandler;
import com.android.p011wm.shell.ShellCommandHandlerImpl;
import com.android.p011wm.shell.ShellInit;
import com.android.p011wm.shell.ShellInitImpl;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.TaskViewFactory;
import com.android.p011wm.shell.TaskViewFactoryController;
import com.android.p011wm.shell.WindowManagerShellWrapper;
import com.android.p011wm.shell.apppairs.AppPairs;
import com.android.p011wm.shell.apppairs.AppPairsController;
import com.android.p011wm.shell.bubbles.BubbleController;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.common.FloatingContentCoordinator;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.draganddrop.DragAndDropController;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutoutController;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import com.android.p011wm.shell.onehanded.OneHanded;
import com.android.p011wm.shell.onehanded.OneHandedController;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.pip.PipAnimationController;
import com.android.p011wm.shell.pip.PipBoundsAlgorithm;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipSnapAlgorithm;
import com.android.p011wm.shell.pip.PipSurfaceTransactionHelper;
import com.android.p011wm.shell.pip.PipTaskOrganizer;
import com.android.p011wm.shell.pip.PipTransitionController;
import com.android.p011wm.shell.pip.PipUiEventLogger;
import com.android.p011wm.shell.pip.phone.PhonePipMenuController;
import com.android.p011wm.shell.pip.phone.PipAppOpsListener;
import com.android.p011wm.shell.pip.phone.PipMotionHelper;
import com.android.p011wm.shell.pip.phone.PipTouchHandler;
import com.android.p011wm.shell.sizecompatui.SizeCompatUIController;
import com.android.p011wm.shell.splitscreen.SplitScreen;
import com.android.p011wm.shell.splitscreen.SplitScreenController;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import com.android.p011wm.shell.startingsurface.StartingWindowController;
import com.android.p011wm.shell.startingsurface.StartingWindowTypeAlgorithm;
import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelper;
import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelperController;
import com.android.p011wm.shell.transition.ShellTransitions;
import com.android.p011wm.shell.transition.Transitions;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.users.EditUserInfoController;
import com.android.systemui.ActivityStarterDelegate;
import com.android.systemui.ActivityStarterDelegate_Factory;
import com.android.systemui.BootCompleteCacheImpl;
import com.android.systemui.BootCompleteCacheImpl_Factory;
import com.android.systemui.Dependency;
import com.android.systemui.Dependency_Factory;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.ForegroundServiceController_Factory;
import com.android.systemui.ForegroundServiceNotificationListener;
import com.android.systemui.ForegroundServiceNotificationListener_Factory;
import com.android.systemui.ForegroundServicesDialog;
import com.android.systemui.ForegroundServicesDialog_Factory;
import com.android.systemui.ImageWallpaper;
import com.android.systemui.ImageWallpaper_Factory;
import com.android.systemui.InitController;
import com.android.systemui.InitController_Factory;
import com.android.systemui.LatencyTester;
import com.android.systemui.LatencyTester_Factory;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.ScreenDecorations_Factory;
import com.android.systemui.SliceBroadcastRelayHandler;
import com.android.systemui.SliceBroadcastRelayHandler_Factory;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.SystemUIAppComponentFactory_MembersInjector;
import com.android.systemui.SystemUIService;
import com.android.systemui.SystemUIService_Factory;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.UiOffloadThread_Factory;
import com.android.systemui.accessibility.AccessibilityButtonModeObserver;
import com.android.systemui.accessibility.AccessibilityButtonModeObserver_Factory;
import com.android.systemui.accessibility.AccessibilityButtonTargetsObserver;
import com.android.systemui.accessibility.AccessibilityButtonTargetsObserver_Factory;
import com.android.systemui.accessibility.ModeSwitchesController;
import com.android.systemui.accessibility.SystemActions;
import com.android.systemui.accessibility.SystemActions_Factory;
import com.android.systemui.accessibility.WindowMagnification;
import com.android.systemui.accessibility.WindowMagnification_Factory;
import com.android.systemui.accessibility.floatingmenu.AccessibilityFloatingMenuController;
import com.android.systemui.appops.AppOpsControllerImpl;
import com.android.systemui.appops.AppOpsControllerImpl_Factory;
import com.android.systemui.assist.AssistLogger;
import com.android.systemui.assist.AssistLogger_Factory;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistManager_Factory;
import com.android.systemui.assist.AssistModule_ProvideAssistUtilsFactory;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.assist.PhoneStateMonitor_Factory;
import com.android.systemui.assist.p003ui.DefaultUiController;
import com.android.systemui.assist.p003ui.DefaultUiController_Factory;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.biometrics.AuthController_Factory;
import com.android.systemui.biometrics.SidefpsController;
import com.android.systemui.biometrics.SidefpsController_Factory;
import com.android.systemui.biometrics.UdfpsController;
import com.android.systemui.biometrics.UdfpsController_Factory;
import com.android.systemui.biometrics.UdfpsHapticsSimulator;
import com.android.systemui.biometrics.UdfpsHapticsSimulator_Factory;
import com.android.systemui.biometrics.UdfpsHbmProvider;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.broadcast.logging.BroadcastDispatcherLogger;
import com.android.systemui.broadcast.logging.BroadcastDispatcherLogger_Factory;
import com.android.systemui.classifier.BrightLineFalsingManager;
import com.android.systemui.classifier.BrightLineFalsingManager_Factory;
import com.android.systemui.classifier.DiagonalClassifier_Factory;
import com.android.systemui.classifier.DistanceClassifier_Factory;
import com.android.systemui.classifier.DoubleTapClassifier;
import com.android.systemui.classifier.DoubleTapClassifier_Factory;
import com.android.systemui.classifier.FalsingClassifier;
import com.android.systemui.classifier.FalsingCollectorImpl_Factory;
import com.android.systemui.classifier.FalsingDataProvider;
import com.android.systemui.classifier.FalsingDataProvider_Factory;
import com.android.systemui.classifier.FalsingManagerProxy;
import com.android.systemui.classifier.FalsingManagerProxy_Factory;
import com.android.systemui.classifier.FalsingModule_ProvidesBrightLineGestureClassifiersFactory;
import com.android.systemui.classifier.FalsingModule_ProvidesDoubleTapTimeoutMsFactory;
import com.android.systemui.classifier.FalsingModule_ProvidesDoubleTapTouchSlopFactory;
import com.android.systemui.classifier.FalsingModule_ProvidesSingleTapTouchSlopFactory;
import com.android.systemui.classifier.HistoryTracker;
import com.android.systemui.classifier.HistoryTracker_Factory;
import com.android.systemui.classifier.PointerCountClassifier_Factory;
import com.android.systemui.classifier.ProximityClassifier_Factory;
import com.android.systemui.classifier.SingleTapClassifier;
import com.android.systemui.classifier.SingleTapClassifier_Factory;
import com.android.systemui.classifier.TypeClassifier;
import com.android.systemui.classifier.TypeClassifier_Factory;
import com.android.systemui.classifier.ZigZagClassifier_Factory;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.colorextraction.SysuiColorExtractor_Factory;
import com.android.systemui.controls.ControlsMetricsLoggerImpl;
import com.android.systemui.controls.ControlsMetricsLoggerImpl_Factory;
import com.android.systemui.controls.CustomIconCache;
import com.android.systemui.controls.CustomIconCache_Factory;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl_Factory;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.controls.controller.ControlsControllerImpl_Factory;
import com.android.systemui.controls.controller.ControlsFavoritePersistenceWrapper;
import com.android.systemui.controls.dagger.ControlsComponent;
import com.android.systemui.controls.dagger.ControlsComponent_Factory;
import com.android.systemui.controls.dagger.ControlsModule_ProvidesControlsFeatureEnabledFactory;
import com.android.systemui.controls.management.ControlsEditingActivity;
import com.android.systemui.controls.management.ControlsEditingActivity_Factory;
import com.android.systemui.controls.management.ControlsFavoritingActivity;
import com.android.systemui.controls.management.ControlsFavoritingActivity_Factory;
import com.android.systemui.controls.management.ControlsListingControllerImpl;
import com.android.systemui.controls.management.ControlsListingControllerImpl_Factory;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity_Factory;
import com.android.systemui.controls.management.ControlsRequestDialog;
import com.android.systemui.controls.management.ControlsRequestDialog_Factory;
import com.android.systemui.controls.p004ui.ControlActionCoordinatorImpl;
import com.android.systemui.controls.p004ui.ControlActionCoordinatorImpl_Factory;
import com.android.systemui.controls.p004ui.ControlsActivity;
import com.android.systemui.controls.p004ui.ControlsActivity_Factory;
import com.android.systemui.controls.p004ui.ControlsUiControllerImpl;
import com.android.systemui.controls.p004ui.ControlsUiControllerImpl_Factory;
import com.android.systemui.dagger.C0913xb22f7179;
import com.android.systemui.dagger.C0914x217e5105;
import com.android.systemui.dagger.ContextComponentHelper;
import com.android.systemui.dagger.ContextComponentResolver;
import com.android.systemui.dagger.ContextComponentResolver_Factory;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.DependencyProvider_ProvideActivityManagerWrapperFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideAmbientDisplayConfigurationFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideAutoHideControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideConfigurationControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideDataSaverControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideDevicePolicyManagerWrapperFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideDualSimIconControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideHandlerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideINotificationManagerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideLeakDetectorFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideLocalBluetoothControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideLockPatternUtilsFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideMetricsLoggerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideMotoDisplayManagerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideNavigationBarControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideNotificationMessagingUtilFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidePluginManagerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideReduceBrightColorsListenerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideSharePreferencesFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideTaskStackChangeListenersFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideThemeOverlayManagerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideTimeTickHandlerFactory;
import com.android.systemui.dagger.DependencyProvider_ProviderLayoutInflaterFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidesBroadcastDispatcherFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidesChoreographerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidesModeSwitchesControllerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideAccessibilityManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideActivityManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideActivityTaskManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideAlarmManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideAudioManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideColorDisplayManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideConnectivityManagagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideContentResolverFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideCrossWindowBlurListenersFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideDevicePolicyManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideDisplayIdFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideDisplayManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideFaceManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIActivityManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIActivityTaskManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIAudioServiceFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIDreamManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIPackageManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIStatusBarServiceFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIWallPaperManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideIWindowManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideKeyguardManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideLauncherAppsFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideMediaRouter2ManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideMediaSessionManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideNetworkScoreManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideNotificationManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideOptionalVibratorFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideOverlayManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvidePackageManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvidePackageManagerWrapperFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvidePermissionManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvidePowerManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideResourcesFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideSensorPrivacyManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideShortcutManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideSmartspaceManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideSubcriptionManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideTelecomManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideTelephonyManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideTrustManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideUserManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideVibratorFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideViewConfigurationFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideWallpaperManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideWifiManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvideWindowManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvidesFingerprintManagerFactory;
import com.android.systemui.dagger.FrameworkServicesModule_ProvidesSensorManagerFactory;
import com.android.systemui.dagger.GlobalModule;
import com.android.systemui.dagger.GlobalModule_ProvideDisplayMetricsFactory;
import com.android.systemui.dagger.GlobalModule_ProvideIsTestHarnessFactory;
import com.android.systemui.dagger.GlobalModule_ProvideUiEventLoggerFactory;
import com.android.systemui.dagger.NightDisplayListenerModule;
import com.android.systemui.dagger.NightDisplayListenerModule_Builder_Factory;
import com.android.systemui.dagger.NightDisplayListenerModule_ProvideNightDisplayListenerFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideBatteryControllerFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideLeakReportEmailFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideRecentsFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideSensorPrivacyControllerFactory;
import com.android.systemui.dagger.WMComponent;
import com.android.systemui.demomode.DemoModeController;
import com.android.systemui.demomode.dagger.DemoModeModule_ProvideDemoModeControllerFactory;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.dock.DockManagerImpl_Factory;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import com.android.systemui.doze.DozeAuthRemover;
import com.android.systemui.doze.DozeAuthRemover_Factory;
import com.android.systemui.doze.DozeDockHandler;
import com.android.systemui.doze.DozeDockHandler_Factory;
import com.android.systemui.doze.DozeFalsingManagerAdapter;
import com.android.systemui.doze.DozeFalsingManagerAdapter_Factory;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.doze.DozeLog_Factory;
import com.android.systemui.doze.DozeLogger;
import com.android.systemui.doze.DozeLogger_Factory;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.doze.DozeMachine_Factory;
import com.android.systemui.doze.DozePauser;
import com.android.systemui.doze.DozePauser_Factory;
import com.android.systemui.doze.DozeScreenBrightness;
import com.android.systemui.doze.DozeScreenBrightness_Factory;
import com.android.systemui.doze.DozeScreenState;
import com.android.systemui.doze.DozeScreenState_Factory;
import com.android.systemui.doze.DozeService;
import com.android.systemui.doze.DozeService_Factory;
import com.android.systemui.doze.DozeTriggers;
import com.android.systemui.doze.DozeTriggers_Factory;
import com.android.systemui.doze.DozeUi;
import com.android.systemui.doze.DozeUi_Factory;
import com.android.systemui.doze.DozeWallpaperState;
import com.android.systemui.doze.DozeWallpaperState_Factory;
import com.android.systemui.doze.MotoDisplayManager;
import com.android.systemui.doze.dagger.DozeComponent;
import com.android.systemui.doze.dagger.DozeModule_ProvidesBrightnessSensorFactory;
import com.android.systemui.doze.dagger.DozeModule_ProvidesDozeMachinePartesFactory;
import com.android.systemui.doze.dagger.DozeModule_ProvidesDozeWakeLockFactory;
import com.android.systemui.doze.dagger.DozeModule_ProvidesWrappedServiceFactory;
import com.android.systemui.dump.DumpHandler;
import com.android.systemui.dump.DumpHandler_Factory;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.dump.DumpManager_Factory;
import com.android.systemui.dump.LogBufferEulogizer;
import com.android.systemui.dump.LogBufferEulogizer_Factory;
import com.android.systemui.dump.LogBufferFreezer;
import com.android.systemui.dump.LogBufferFreezer_Factory;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService_Factory;
import com.android.systemui.flags.FeatureFlagReader;
import com.android.systemui.flags.FeatureFlagReader_Factory;
import com.android.systemui.flags.SystemPropertiesHelper;
import com.android.systemui.flags.SystemPropertiesHelper_Factory;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.fragments.FragmentService_Factory;
import com.android.systemui.globalactions.GlobalActionsComponent;
import com.android.systemui.globalactions.GlobalActionsComponent_Factory;
import com.android.systemui.globalactions.GlobalActionsDialogFolio;
import com.android.systemui.globalactions.GlobalActionsDialogFolio_Factory;
import com.android.systemui.globalactions.GlobalActionsDialogLite;
import com.android.systemui.globalactions.GlobalActionsDialogLite_Factory;
import com.android.systemui.globalactions.GlobalActionsImpl;
import com.android.systemui.globalactions.GlobalActionsImpl_Factory;
import com.android.systemui.globalactions.GlobalActionsInfoProvider;
import com.android.systemui.globalactions.GlobalActionsInfoProvider_Factory;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.keyguard.DismissCallbackRegistry_Factory;
import com.android.systemui.keyguard.KeyguardLifecyclesDispatcher;
import com.android.systemui.keyguard.KeyguardLifecyclesDispatcher_Factory;
import com.android.systemui.keyguard.KeyguardService;
import com.android.systemui.keyguard.KeyguardService_Factory;
import com.android.systemui.keyguard.KeyguardUnlockAnimationController;
import com.android.systemui.keyguard.KeyguardUnlockAnimationController_Factory;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.ScreenLifecycle_Factory;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle_Factory;
import com.android.systemui.keyguard.WorkLockActivity;
import com.android.systemui.keyguard.WorkLockActivity_Factory;
import com.android.systemui.keyguard.dagger.KeyguardModule_NewKeyguardViewMediatorFactory;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogBufferFactory;
import com.android.systemui.log.LogBufferFactory_Factory;
import com.android.systemui.log.LogcatEchoTracker;
import com.android.systemui.log.dagger.LogModule_ProvideBroadcastDispatcherLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideDozeLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideLogcatEchoTrackerFactory;
import com.android.systemui.log.dagger.LogModule_ProvideNotifInteractionLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideNotificationSectionLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideNotificationsLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvidePrivacyLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideQuickSettingsLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideToastLogBufferFactory;
import com.android.systemui.media.KeyguardMediaController;
import com.android.systemui.media.KeyguardMediaController_Factory;
import com.android.systemui.media.LocalMediaManagerFactory;
import com.android.systemui.media.LocalMediaManagerFactory_Factory;
import com.android.systemui.media.MediaBrowserFactory;
import com.android.systemui.media.MediaBrowserFactory_Factory;
import com.android.systemui.media.MediaCarouselController;
import com.android.systemui.media.MediaCarouselController_Factory;
import com.android.systemui.media.MediaControlPanel;
import com.android.systemui.media.MediaControlPanel_Factory;
import com.android.systemui.media.MediaControllerFactory;
import com.android.systemui.media.MediaControllerFactory_Factory;
import com.android.systemui.media.MediaDataCombineLatest_Factory;
import com.android.systemui.media.MediaDataFilter;
import com.android.systemui.media.MediaDataFilter_Factory;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.media.MediaDataManager_Factory;
import com.android.systemui.media.MediaDeviceManager;
import com.android.systemui.media.MediaDeviceManager_Factory;
import com.android.systemui.media.MediaFeatureFlag;
import com.android.systemui.media.MediaFeatureFlag_Factory;
import com.android.systemui.media.MediaHierarchyManager;
import com.android.systemui.media.MediaHierarchyManager_Factory;
import com.android.systemui.media.MediaHost;
import com.android.systemui.media.MediaHostStatesManager;
import com.android.systemui.media.MediaHostStatesManager_Factory;
import com.android.systemui.media.MediaHost_MediaHostStateHolder_Factory;
import com.android.systemui.media.MediaResumeListener;
import com.android.systemui.media.MediaResumeListener_Factory;
import com.android.systemui.media.MediaSessionBasedFilter;
import com.android.systemui.media.MediaSessionBasedFilter_Factory;
import com.android.systemui.media.MediaTimeoutListener;
import com.android.systemui.media.MediaTimeoutListener_Factory;
import com.android.systemui.media.MediaViewController;
import com.android.systemui.media.MediaViewController_Factory;
import com.android.systemui.media.ResumeMediaBrowserFactory;
import com.android.systemui.media.ResumeMediaBrowserFactory_Factory;
import com.android.systemui.media.SeekBarViewModel;
import com.android.systemui.media.SeekBarViewModel_Factory;
import com.android.systemui.media.SmartspaceMediaDataProvider_Factory;
import com.android.systemui.media.dagger.MediaModule_ProvidesKeyguardMediaHostFactory;
import com.android.systemui.media.dagger.MediaModule_ProvidesQSMediaHostFactory;
import com.android.systemui.media.dagger.MediaModule_ProvidesQuickQSMediaHostFactory;
import com.android.systemui.media.dialog.MediaOutputDialogFactory;
import com.android.systemui.media.dialog.MediaOutputDialogFactory_Factory;
import com.android.systemui.media.dialog.MediaOutputDialogReceiver;
import com.android.systemui.media.dialog.MediaOutputDialogReceiver_Factory;
import com.android.systemui.media.systemsounds.HomeSoundEffectController;
import com.android.systemui.media.systemsounds.HomeSoundEffectController_Factory;
import com.android.systemui.model.SysUiState;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationBarOverlayController;
import com.android.systemui.navigationbar.NavigationBarOverlayController_Factory;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.navigationbar.NavigationModeController_Factory;
import com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler;
import com.android.systemui.navigationbar.gestural.EdgeBackGestureHandler_Factory_Factory;
import com.android.systemui.p006qs.AutoAddTracker;
import com.android.systemui.p006qs.AutoAddTracker_Builder_Factory;
import com.android.systemui.p006qs.CliQSPanelNew;
import com.android.systemui.p006qs.DesktopQSFooterViewController;
import com.android.systemui.p006qs.DesktopQSFooterViewController_Factory;
import com.android.systemui.p006qs.QSAnimator;
import com.android.systemui.p006qs.QSAnimator_Factory;
import com.android.systemui.p006qs.QSContainerImpl;
import com.android.systemui.p006qs.QSContainerImplController;
import com.android.systemui.p006qs.QSContainerImplController_Factory;
import com.android.systemui.p006qs.QSDetailDisplayer;
import com.android.systemui.p006qs.QSDetailDisplayer_Factory;
import com.android.systemui.p006qs.QSExpansionPathInterpolator;
import com.android.systemui.p006qs.QSExpansionPathInterpolator_Factory;
import com.android.systemui.p006qs.QSFooter;
import com.android.systemui.p006qs.QSFooterView;
import com.android.systemui.p006qs.QSFooterViewController;
import com.android.systemui.p006qs.QSFooterViewController_Factory;
import com.android.systemui.p006qs.QSFragment;
import com.android.systemui.p006qs.QSPanel;
import com.android.systemui.p006qs.QSPanelController;
import com.android.systemui.p006qs.QSPanelController_Factory;
import com.android.systemui.p006qs.QSPrcFixedPanel;
import com.android.systemui.p006qs.QSPrcFixedPanelController;
import com.android.systemui.p006qs.QSPrcFixedPanelController_Factory;
import com.android.systemui.p006qs.QSPrcPanelContainer;
import com.android.systemui.p006qs.QSPrcPanelContainerController;
import com.android.systemui.p006qs.QSPrcPanelContainerController_Factory;
import com.android.systemui.p006qs.QSSecurityFooter_Factory;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.QSTileHost_Factory;
import com.android.systemui.p006qs.QSTileRevealController_Factory_Factory;
import com.android.systemui.p006qs.QuickQSPanel;
import com.android.systemui.p006qs.QuickQSPanelController;
import com.android.systemui.p006qs.QuickQSPanelController_Factory;
import com.android.systemui.p006qs.QuickStatusBarHeader;
import com.android.systemui.p006qs.QuickStatusBarHeaderController_Factory;
import com.android.systemui.p006qs.ReduceBrightColorsController;
import com.android.systemui.p006qs.carrier.C1215xf95dc14f;
import com.android.systemui.p006qs.carrier.QSCarrierGroupController;
import com.android.systemui.p006qs.carrier.QSCarrierGroupController_Builder_Factory;
import com.android.systemui.p006qs.customize.QSCustomizer;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import com.android.systemui.p006qs.customize.QSCustomizerController_Factory;
import com.android.systemui.p006qs.customize.TileAdapter;
import com.android.systemui.p006qs.customize.TileAdapter_Factory;
import com.android.systemui.p006qs.customize.TileQueryHelper;
import com.android.systemui.p006qs.customize.TileQueryHelper_Factory;
import com.android.systemui.p006qs.dagger.QSFlagsModule_IsPMLiteEnabledFactory;
import com.android.systemui.p006qs.dagger.QSFlagsModule_IsReduceBrightColorsAvailableFactory;
import com.android.systemui.p006qs.dagger.QSFragmentComponent;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvideQSPanelFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvideRootViewFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvideThemedContextFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvideThemedLayoutInflaterFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesMultiUserSWitchFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSContainerImplFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSCutomizerFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSFooterFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSFooterViewFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSPrcFixedPanelFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSPrcPanelContainerFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSSecurityFooterViewFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQSUsingMediaPlayerFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQuickQSPanelFactory;
import com.android.systemui.p006qs.dagger.QSFragmentModule_ProvidesQuickStatusBarHeaderFactory;
import com.android.systemui.p006qs.dagger.QSModule_ProvideAutoTileManagerFactory;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.external.CustomTileStatePersister;
import com.android.systemui.p006qs.external.CustomTileStatePersister_Factory;
import com.android.systemui.p006qs.external.CustomTile_Builder_Factory;
import com.android.systemui.p006qs.external.MotoDesktopProcessTileServices;
import com.android.systemui.p006qs.external.MotoDesktopProcessTileServices_Factory;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.logging.QSLogger_Factory;
import com.android.systemui.p006qs.tileimpl.QSFactoryImpl;
import com.android.systemui.p006qs.tileimpl.QSFactoryImpl_Factory;
import com.android.systemui.p006qs.tiles.AirplaneModeTile;
import com.android.systemui.p006qs.tiles.AirplaneModeTile_Factory;
import com.android.systemui.p006qs.tiles.AlarmTile;
import com.android.systemui.p006qs.tiles.AlarmTile_Factory;
import com.android.systemui.p006qs.tiles.BatterySaverTile;
import com.android.systemui.p006qs.tiles.BatterySaverTile_Factory;
import com.android.systemui.p006qs.tiles.BluetoothTile;
import com.android.systemui.p006qs.tiles.BluetoothTile_Factory;
import com.android.systemui.p006qs.tiles.CameraToggleTile;
import com.android.systemui.p006qs.tiles.CameraToggleTile_Factory;
import com.android.systemui.p006qs.tiles.CastTile;
import com.android.systemui.p006qs.tiles.CastTile_Factory;
import com.android.systemui.p006qs.tiles.CellularTile;
import com.android.systemui.p006qs.tiles.CellularTile_Factory;
import com.android.systemui.p006qs.tiles.ColorInversionTile;
import com.android.systemui.p006qs.tiles.ColorInversionTile_Factory;
import com.android.systemui.p006qs.tiles.DataSaverTile;
import com.android.systemui.p006qs.tiles.DataSaverTile_Factory;
import com.android.systemui.p006qs.tiles.DeviceControlsTile;
import com.android.systemui.p006qs.tiles.DeviceControlsTile_Factory;
import com.android.systemui.p006qs.tiles.DndTile;
import com.android.systemui.p006qs.tiles.DndTile_Factory;
import com.android.systemui.p006qs.tiles.FlashlightTile;
import com.android.systemui.p006qs.tiles.FlashlightTile_Factory;
import com.android.systemui.p006qs.tiles.HotspotTile;
import com.android.systemui.p006qs.tiles.HotspotTile_Factory;
import com.android.systemui.p006qs.tiles.InternetTile;
import com.android.systemui.p006qs.tiles.InternetTile_Factory;
import com.android.systemui.p006qs.tiles.LocationTile;
import com.android.systemui.p006qs.tiles.LocationTile_Factory;
import com.android.systemui.p006qs.tiles.MicrophoneToggleTile;
import com.android.systemui.p006qs.tiles.MicrophoneToggleTile_Factory;
import com.android.systemui.p006qs.tiles.Moto5GTile;
import com.android.systemui.p006qs.tiles.Moto5GTile_Factory;
import com.android.systemui.p006qs.tiles.NfcTile;
import com.android.systemui.p006qs.tiles.NfcTile_Factory;
import com.android.systemui.p006qs.tiles.NightDisplayTile;
import com.android.systemui.p006qs.tiles.NightDisplayTile_Factory;
import com.android.systemui.p006qs.tiles.QuickAccessWalletTile;
import com.android.systemui.p006qs.tiles.QuickAccessWalletTile_Factory;
import com.android.systemui.p006qs.tiles.ReduceBrightColorsTile;
import com.android.systemui.p006qs.tiles.ReduceBrightColorsTile_Factory;
import com.android.systemui.p006qs.tiles.RotationLockTile;
import com.android.systemui.p006qs.tiles.RotationLockTile_Factory;
import com.android.systemui.p006qs.tiles.ScreenRecordTile;
import com.android.systemui.p006qs.tiles.ScreenRecordTile_Factory;
import com.android.systemui.p006qs.tiles.ScreenshotTile;
import com.android.systemui.p006qs.tiles.ScreenshotTile_Factory;
import com.android.systemui.p006qs.tiles.UiModeNightTile;
import com.android.systemui.p006qs.tiles.UiModeNightTile_Factory;
import com.android.systemui.p006qs.tiles.UserDetailView;
import com.android.systemui.p006qs.tiles.UserDetailView_Adapter_Factory;
import com.android.systemui.p006qs.tiles.UserTile;
import com.android.systemui.p006qs.tiles.UserTile_Factory;
import com.android.systemui.p006qs.tiles.WifiTile;
import com.android.systemui.p006qs.tiles.WifiTile_Factory;
import com.android.systemui.p006qs.tiles.WorkModeTile;
import com.android.systemui.p006qs.tiles.WorkModeTile_Factory;
import com.android.systemui.people.PeopleSpaceActivity;
import com.android.systemui.people.PeopleSpaceActivity_Factory;
import com.android.systemui.people.widget.LaunchConversationActivity;
import com.android.systemui.people.widget.LaunchConversationActivity_Factory;
import com.android.systemui.people.widget.PeopleSpaceWidgetManager;
import com.android.systemui.people.widget.PeopleSpaceWidgetManager_Factory;
import com.android.systemui.people.widget.PeopleSpaceWidgetPinnedReceiver;
import com.android.systemui.people.widget.PeopleSpaceWidgetPinnedReceiver_Factory;
import com.android.systemui.people.widget.PeopleSpaceWidgetProvider;
import com.android.systemui.people.widget.PeopleSpaceWidgetProvider_Factory;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.PluginDependencyProvider_Factory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.EnhancedEstimatesImpl;
import com.android.systemui.power.EnhancedEstimatesImpl_Factory;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.power.PowerNotificationWarnings_Factory;
import com.android.systemui.power.PowerUI;
import com.android.systemui.power.PowerUI_Factory;
import com.android.systemui.privacy.PrivacyDialogController;
import com.android.systemui.privacy.PrivacyDialogController_Factory;
import com.android.systemui.privacy.PrivacyItemController;
import com.android.systemui.privacy.PrivacyItemController_Factory;
import com.android.systemui.privacy.logging.PrivacyLogger;
import com.android.systemui.privacy.logging.PrivacyLogger_Factory;
import com.android.systemui.privacy.television.TvOngoingPrivacyChip;
import com.android.systemui.privacy.television.TvOngoingPrivacyChip_Factory;
import com.android.systemui.recents.OverviewProxyRecentsImpl;
import com.android.systemui.recents.OverviewProxyRecentsImpl_Factory;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyService_Factory;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.recents.RecentsModule_ProvideRecentsImplFactory;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingController_Factory;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenrecord.RecordingService_Factory;
import com.android.systemui.screenrecord.ScreenRecordDialog;
import com.android.systemui.screenrecord.ScreenRecordDialog_Factory;
import com.android.systemui.screenshot.ActionProxyReceiver;
import com.android.systemui.screenshot.ActionProxyReceiver_Factory;
import com.android.systemui.screenshot.DeleteScreenshotReceiver;
import com.android.systemui.screenshot.DeleteScreenshotReceiver_Factory;
import com.android.systemui.screenshot.ImageExporter_Factory;
import com.android.systemui.screenshot.ImageTileSet_Factory;
import com.android.systemui.screenshot.LongScreenshotActivity;
import com.android.systemui.screenshot.LongScreenshotActivity_Factory;
import com.android.systemui.screenshot.LongScreenshotData;
import com.android.systemui.screenshot.LongScreenshotData_Factory;
import com.android.systemui.screenshot.MotoGlobalScreenshot;
import com.android.systemui.screenshot.MotoGlobalScreenshot_Factory;
import com.android.systemui.screenshot.ScreenshotController;
import com.android.systemui.screenshot.ScreenshotController_Factory;
import com.android.systemui.screenshot.ScreenshotNotificationsController;
import com.android.systemui.screenshot.ScreenshotNotificationsController_Factory;
import com.android.systemui.screenshot.ScreenshotSmartActions;
import com.android.systemui.screenshot.ScreenshotSmartActions_Factory;
import com.android.systemui.screenshot.ScrollCaptureClient;
import com.android.systemui.screenshot.ScrollCaptureClient_Factory;
import com.android.systemui.screenshot.ScrollCaptureController;
import com.android.systemui.screenshot.ScrollCaptureController_Factory;
import com.android.systemui.screenshot.SmartActionsReceiver;
import com.android.systemui.screenshot.SmartActionsReceiver_Factory;
import com.android.systemui.screenshot.TakeScreenshotService;
import com.android.systemui.screenshot.TakeScreenshotService_Factory;
import com.android.systemui.sensorprivacy.SensorUseStartedActivity;
import com.android.systemui.sensorprivacy.SensorUseStartedActivity_Factory;
import com.android.systemui.sensorprivacy.television.TvUnblockSensorActivity;
import com.android.systemui.sensorprivacy.television.TvUnblockSensorActivity_Factory;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.settings.brightness.BrightnessController;
import com.android.systemui.settings.brightness.BrightnessController_Factory_Factory;
import com.android.systemui.settings.brightness.BrightnessDialog;
import com.android.systemui.settings.brightness.BrightnessDialog_Factory;
import com.android.systemui.settings.brightness.BrightnessSlider;
import com.android.systemui.settings.brightness.BrightnessSlider_Factory_Factory;
import com.android.systemui.settings.dagger.SettingsModule_ProvideUserTrackerFactory;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import com.android.systemui.shared.system.smartspace.SmartspaceTransitionController;
import com.android.systemui.shortcut.ShortcutKeyDispatcher;
import com.android.systemui.shortcut.ShortcutKeyDispatcher_Factory;
import com.android.systemui.statusbar.ActionClickLogger;
import com.android.systemui.statusbar.ActionClickLogger_Factory;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.BlurUtils_Factory;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.FeatureFlags_Factory;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.LockscreenShadeTransitionController_Factory;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.statusbar.MediaArtworkProcessor_Factory;
import com.android.systemui.statusbar.NotificationClickNotifier;
import com.android.systemui.statusbar.NotificationClickNotifier_Factory;
import com.android.systemui.statusbar.NotificationInteractionTracker;
import com.android.systemui.statusbar.NotificationInteractionTracker_Factory;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl_Factory;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.NotificationShadeDepthController_Factory;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.NotificationShelfController;
import com.android.systemui.statusbar.NotificationShelfController_Factory;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.PulseExpansionHandler_Factory;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.StatusBarStateControllerImpl_Factory;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SuperStatusBarViewFactory_Factory;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.VibratorHelper_Factory;
import com.android.systemui.statusbar.commandline.CommandRegistry;
import com.android.systemui.statusbar.commandline.CommandRegistry_Factory;
import com.android.systemui.statusbar.dagger.C1496x30c882de;
import com.android.systemui.statusbar.dagger.C1497xfa996c5e;
import com.android.systemui.statusbar.dagger.C1498x3f8faa0a;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideCommandQueueFactory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationListenerFactory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideOngoingCallControllerFactory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideSmartReplyControllerFactory;
import com.android.systemui.statusbar.events.PrivacyDotViewController;
import com.android.systemui.statusbar.events.PrivacyDotViewController_Factory;
import com.android.systemui.statusbar.events.SystemEventChipAnimationController;
import com.android.systemui.statusbar.events.SystemEventChipAnimationController_Factory;
import com.android.systemui.statusbar.events.SystemEventCoordinator;
import com.android.systemui.statusbar.events.SystemEventCoordinator_Factory;
import com.android.systemui.statusbar.events.SystemStatusAnimationScheduler;
import com.android.systemui.statusbar.events.SystemStatusAnimationScheduler_Factory;
import com.android.systemui.statusbar.lockscreen.LockscreenSmartspaceController;
import com.android.systemui.statusbar.lockscreen.LockscreenSmartspaceController_Factory;
import com.android.systemui.statusbar.notification.AnimatedImageNotificationManager;
import com.android.systemui.statusbar.notification.AnimatedImageNotificationManager_Factory;
import com.android.systemui.statusbar.notification.AssistantFeedbackController;
import com.android.systemui.statusbar.notification.AssistantFeedbackController_Factory;
import com.android.systemui.statusbar.notification.ConversationNotificationManager;
import com.android.systemui.statusbar.notification.ConversationNotificationManager_Factory;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor_Factory;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import com.android.systemui.statusbar.notification.DynamicChildBindController_Factory;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController_Factory;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController_Factory;
import com.android.systemui.statusbar.notification.InstantAppNotifier;
import com.android.systemui.statusbar.notification.InstantAppNotifier_Factory;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.NotificationClickerLogger;
import com.android.systemui.statusbar.notification.NotificationClickerLogger_Factory;
import com.android.systemui.statusbar.notification.NotificationClicker_Builder_Factory;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger_Factory;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationFilter_Factory;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager_Factory;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifCollection_Factory;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl_Factory;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifPipeline_Factory;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager_Factory;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder_Factory;
import com.android.systemui.statusbar.notification.collection.TargetSdkResolver;
import com.android.systemui.statusbar.notification.collection.TargetSdkResolver_Factory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescerLogger;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescerLogger_Factory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.AppOpsCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.AppOpsCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.BubbleCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.BubbleCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.ConversationCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.DeviceProvisionedCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.DeviceProvisionedCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.HeadsUpCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.HeadsUpCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.HideNotifsForOtherUsersCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.HideNotifsForOtherUsersCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.MediaCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.MediaCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinatorLogger;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinatorLogger_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.RankingCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.RankingCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.SharedCoordinatorLogger;
import com.android.systemui.statusbar.notification.collection.coordinator.SharedCoordinatorLogger_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.SmartspaceDedupingCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.SmartspaceDedupingCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.VisualStabilityCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.VisualStabilityCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.inflation.LowPriorityInflationHelper;
import com.android.systemui.statusbar.notification.collection.inflation.LowPriorityInflationHelper_Factory;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl_Factory;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer_Factory;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy_Factory;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger_Factory;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger_Factory;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider_Factory;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.collection.render.GroupMembershipManager;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import com.android.systemui.statusbar.notification.collection.render.NotifViewBarn;
import com.android.systemui.statusbar.notification.collection.render.NotifViewBarn_Factory;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderController;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderNodeControllerImpl;
import com.android.systemui.statusbar.notification.collection.render.SectionHeaderNodeControllerImpl_Factory;
import com.android.systemui.statusbar.notification.collection.render.ShadeViewDifferLogger;
import com.android.systemui.statusbar.notification.collection.render.ShadeViewDifferLogger_Factory;
import com.android.systemui.statusbar.notification.collection.render.ShadeViewManagerFactory;
import com.android.systemui.statusbar.notification.collection.render.ShadeViewManagerFactory_Factory;
import com.android.systemui.statusbar.notification.dagger.C1576x41b9fd82;
import com.android.systemui.statusbar.notification.dagger.C1577x30119a0;
import com.android.systemui.statusbar.notification.dagger.C1578x3fd4641;
import com.android.systemui.statusbar.notification.dagger.C1579x340f4262;
import com.android.systemui.statusbar.notification.dagger.C1580x8d68ee80;
import com.android.systemui.statusbar.notification.dagger.C1581xb614d321;
import com.android.systemui.statusbar.notification.dagger.C1582x812edf99;
import com.android.systemui.statusbar.notification.dagger.C1583xda791837;
import com.android.systemui.statusbar.notification.dagger.C1584x39c1fe98;
import com.android.systemui.statusbar.notification.dagger.C1585xcc90df13;
import com.android.systemui.statusbar.notification.dagger.C1586x9d7acab1;
import com.android.systemui.statusbar.notification.dagger.C1587x34a20792;
import com.android.systemui.statusbar.notification.dagger.C1588xb8672cea;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideCommonNotifCollectionFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideGroupExpansionManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideGroupMembershipManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationEntryManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationGutsManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationPanelLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationsControllerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideOnUserInteractionCallbackFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideVisualStabilityManagerFactory;
import com.android.systemui.statusbar.notification.dagger.SectionHeaderControllerSubcomponent;
import com.android.systemui.statusbar.notification.icon.IconBuilder;
import com.android.systemui.statusbar.notification.icon.IconBuilder_Factory;
import com.android.systemui.statusbar.notification.icon.IconManager;
import com.android.systemui.statusbar.notification.icon.IconManager_Factory;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl_Factory;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub_Factory;
import com.android.systemui.statusbar.notification.interruption.HeadsUpController;
import com.android.systemui.statusbar.notification.interruption.HeadsUpController_Factory;
import com.android.systemui.statusbar.notification.interruption.HeadsUpViewBinder;
import com.android.systemui.statusbar.notification.interruption.HeadsUpViewBinder_Factory;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl_Factory;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.logging.NotificationLogger_ExpansionStateLogger_Factory;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractorPluginBoundary;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractorPluginBoundary_Factory;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl_Factory;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController_Factory;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController_Factory;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialog_Builder_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineViewController;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineViewController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableViewController;
import com.android.systemui.statusbar.notification.row.ExpandableViewController_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineLogger;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineLogger_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline_Factory;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager_Factory;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCache;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCacheImpl;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCacheImpl_Factory;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater_Factory;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.OnUserInteractionCallback;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.row.RowContentBindStageLogger;
import com.android.systemui.statusbar.notification.row.RowContentBindStageLogger_Factory;
import com.android.systemui.statusbar.notification.row.RowContentBindStage_Factory;
import com.android.systemui.statusbar.notification.row.RowInflaterTask_Factory;
import com.android.systemui.statusbar.notification.row.dagger.C1636x3e2d0aca;
import com.android.systemui.statusbar.notification.row.dagger.C1637xdc9a80a2;
import com.android.systemui.statusbar.notification.row.dagger.C1638xc255c3ca;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import com.android.systemui.statusbar.notification.row.dagger.NotificationShelfComponent;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.AmbientState_Factory;
import com.android.systemui.statusbar.notification.stack.CliNotificationStackClient;
import com.android.systemui.statusbar.notification.stack.CliNotificationStackClient_Factory;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController;
import com.android.systemui.statusbar.notification.stack.DesktopNotificationStackScrollLayoutController_Factory;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsLogger_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper_Builder_Factory;
import com.android.systemui.statusbar.p007tv.TvStatusBar;
import com.android.systemui.statusbar.p007tv.TvStatusBar_Factory;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationHandler;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationHandler_Factory;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationPanel;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationPanelActivity;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationPanelActivity_Factory;
import com.android.systemui.statusbar.p007tv.notifications.TvNotificationPanel_Factory;
import com.android.systemui.statusbar.phone.AudioFxControllerImpl;
import com.android.systemui.statusbar.phone.AudioFxControllerImpl_Factory;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.BiometricUnlockController_Factory;
import com.android.systemui.statusbar.phone.CliStatusBar;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController;
import com.android.systemui.statusbar.phone.CliStatusBarWindowController_Factory;
import com.android.systemui.statusbar.phone.CollapsedStatusBarFragment;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl_Factory;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.DozeParameters_Factory;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeScrimController_Factory;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.DozeServiceHost_Factory;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardBypassController_Factory;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil_Factory;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl_Factory;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LightBarController_Factory;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger_Factory;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl_Factory;
import com.android.systemui.statusbar.phone.MultiUserSwitch;
import com.android.systemui.statusbar.phone.MultiUserSwitchController;
import com.android.systemui.statusbar.phone.MultiUserSwitchController_Factory;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.NotificationIconAreaController_Factory;
import com.android.systemui.statusbar.phone.NotificationShadeWindowControllerImpl;
import com.android.systemui.statusbar.phone.NotificationShadeWindowControllerImpl_Factory;
import com.android.systemui.statusbar.phone.NotificationTapHelper;
import com.android.systemui.statusbar.phone.NotificationTapHelper_Factory_Factory;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimController_Factory;
import com.android.systemui.statusbar.phone.ShadeControllerImpl;
import com.android.systemui.statusbar.phone.ShadeControllerImpl_Factory;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarContentInsetsProvider;
import com.android.systemui.statusbar.phone.StatusBarContentInsetsProvider_Factory;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl_Factory;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarLocationPublisher;
import com.android.systemui.statusbar.phone.StatusBarLocationPublisher_Factory;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarterLogger_Factory;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback_Factory;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarWindowController_Factory;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController;
import com.android.systemui.statusbar.phone.UnlockedScreenOffAnimationController_Factory;
import com.android.systemui.statusbar.phone.dagger.C1981x3053f5c5;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallController;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallLogger;
import com.android.systemui.statusbar.phone.ongoingcall.OngoingCallLogger_Factory;
import com.android.systemui.statusbar.policy.AccessPointControllerImpl;
import com.android.systemui.statusbar.policy.AccessPointControllerImpl_WifiPickerTrackerFactory_Factory;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityController_Factory;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper_Factory;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryStateNotifier;
import com.android.systemui.statusbar.policy.BatteryStateNotifier_Factory;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl_Factory;
import com.android.systemui.statusbar.policy.CallbackHandler;
import com.android.systemui.statusbar.policy.CallbackHandler_Factory;
import com.android.systemui.statusbar.policy.CastControllerImpl;
import com.android.systemui.statusbar.policy.CastControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceControlsControllerImpl;
import com.android.systemui.statusbar.policy.DeviceControlsControllerImpl_Factory;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl_Factory;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl_Factory;
import com.android.systemui.statusbar.policy.HotspotControllerImpl;
import com.android.systemui.statusbar.policy.HotspotControllerImpl_Factory;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl;
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl_Factory;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import com.android.systemui.statusbar.policy.LocationControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.NetworkControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl_Factory;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler_Factory;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.policy.RemoteInputUriController_Factory;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SecurityControllerImpl;
import com.android.systemui.statusbar.policy.SecurityControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SmartActionInflaterImpl;
import com.android.systemui.statusbar.policy.SmartActionInflaterImpl_Factory;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.policy.SmartReplyConstants_Factory;
import com.android.systemui.statusbar.policy.SmartReplyInflaterImpl;
import com.android.systemui.statusbar.policy.SmartReplyInflaterImpl_Factory;
import com.android.systemui.statusbar.policy.SmartReplyStateInflaterImpl;
import com.android.systemui.statusbar.policy.SmartReplyStateInflaterImpl_Factory;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl_Factory;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController_Factory;
import com.android.systemui.statusbar.policy.UserSwitcherController_UserDetailAdapter_Factory;
import com.android.systemui.statusbar.policy.WalletControllerImpl;
import com.android.systemui.statusbar.policy.WalletControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl_Factory;
import com.android.systemui.statusbar.policy.dagger.StatusBarPolicyModule_ProvideAccessPointControllerImplFactory;
import com.android.systemui.telephony.TelephonyCallback_Factory;
import com.android.systemui.telephony.TelephonyListenerManager;
import com.android.systemui.telephony.TelephonyListenerManager_Factory;
import com.android.systemui.theme.ThemeOverlayApplier;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.theme.ThemeOverlayController_Factory;
import com.android.systemui.toast.ToastFactory;
import com.android.systemui.toast.ToastFactory_Factory;
import com.android.systemui.toast.ToastLogger;
import com.android.systemui.toast.ToastLogger_Factory;
import com.android.systemui.toast.ToastUI;
import com.android.systemui.toast.ToastUI_Factory;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.tracing.ProtoTracer_Factory;
import com.android.systemui.tuner.TunablePadding;
import com.android.systemui.tuner.TunablePadding_TunablePaddingService_Factory;
import com.android.systemui.tuner.TunerActivity;
import com.android.systemui.tuner.TunerActivity_Factory;
import com.android.systemui.tuner.TunerServiceImpl;
import com.android.systemui.tuner.TunerServiceImpl_Factory;
import com.android.systemui.usb.UsbDebuggingActivity;
import com.android.systemui.usb.UsbDebuggingActivity_Factory;
import com.android.systemui.usb.UsbDebuggingSecondaryUserActivity;
import com.android.systemui.usb.UsbDebuggingSecondaryUserActivity_Factory;
import com.android.systemui.user.CreateUserActivity;
import com.android.systemui.user.CreateUserActivity_Factory;
import com.android.systemui.user.UserCreator;
import com.android.systemui.user.UserCreator_Factory;
import com.android.systemui.user.UserModule;
import com.android.systemui.user.UserModule_ProvideEditUserInfoControllerFactory;
import com.android.systemui.util.CarrierConfigTracker;
import com.android.systemui.util.CarrierConfigTracker_Factory;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.DeviceConfigProxy_Factory;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.util.InjectionInflationController_Factory;
import com.android.systemui.util.RingerModeTrackerImpl;
import com.android.systemui.util.RingerModeTrackerImpl_Factory;
import com.android.systemui.util.concurrency.C2117xb8fd9db4;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.concurrency.Execution;
import com.android.systemui.util.concurrency.ExecutionImpl_Factory;
import com.android.systemui.util.concurrency.GlobalConcurrencyModule_ProvideMainExecutorFactory;
import com.android.systemui.util.concurrency.GlobalConcurrencyModule_ProvideMainHandlerFactory;
import com.android.systemui.util.concurrency.GlobalConcurrencyModule_ProvideMainLooperFactory;
import com.android.systemui.util.concurrency.RepeatableExecutor;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideBackgroundDelayableExecutorFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideBackgroundExecutorFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideBgHandlerFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideBgLooperFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideDelayableExecutorFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideExecutorFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideLongRunningExecutorFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideLongRunningLooperFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideMainDelayableExecutorFactory;
import com.android.systemui.util.concurrency.SysUIConcurrencyModule_ProvideUiBackgroundExecutorFactory;
import com.android.systemui.util.concurrency.ThreadFactory;
import com.android.systemui.util.concurrency.ThreadFactoryImpl_Factory;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.util.leak.GarbageMonitor_Factory;
import com.android.systemui.util.leak.GarbageMonitor_MemoryTile_Factory;
import com.android.systemui.util.leak.GarbageMonitor_Service_Factory;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.util.leak.LeakReporter;
import com.android.systemui.util.leak.LeakReporter_Factory;
import com.android.systemui.util.p009io.Files;
import com.android.systemui.util.p009io.Files_Factory;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.AsyncSensorManager_Factory;
import com.android.systemui.util.sensors.ProximitySensor;
import com.android.systemui.util.sensors.ProximitySensor_Factory;
import com.android.systemui.util.sensors.ProximitySensor_ProximityCheck_Factory;
import com.android.systemui.util.sensors.SensorModule_ProvidePrimaryProxSensorFactory;
import com.android.systemui.util.sensors.SensorModule_ProvideSecondaryProxSensorFactory;
import com.android.systemui.util.sensors.ThresholdSensor;
import com.android.systemui.util.sensors.ThresholdSensorImpl_Builder_Factory;
import com.android.systemui.util.settings.GlobalSettingsImpl_Factory;
import com.android.systemui.util.settings.SecureSettingsImpl_Factory;
import com.android.systemui.util.time.SystemClock;
import com.android.systemui.util.time.SystemClockImpl_Factory;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.DelayedWakeLock_Builder_Factory;
import com.android.systemui.util.wakelock.WakeLock;
import com.android.systemui.util.wrapper.BuildInfo;
import com.android.systemui.util.wrapper.BuildInfo_Factory;
import com.android.systemui.volume.VolumeDialogComponent;
import com.android.systemui.volume.VolumeDialogComponent_Factory;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import com.android.systemui.volume.VolumeDialogControllerImpl_Factory;
import com.android.systemui.volume.VolumeUI;
import com.android.systemui.volume.VolumeUI_Factory;
import com.android.systemui.wallet.controller.QuickAccessWalletController;
import com.android.systemui.wallet.controller.QuickAccessWalletController_Factory;
import com.android.systemui.wallet.dagger.WalletModule_ProvideQuickAccessWalletClientFactory;
import com.android.systemui.wallet.p010ui.WalletActivity;
import com.android.systemui.wallet.p010ui.WalletActivity_Factory;
import com.android.systemui.wmshell.BubblesManager;
import com.android.systemui.wmshell.C2213x374a97b;
import com.android.systemui.wmshell.WMShell;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideAppPairsFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideBubbleControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideBubblesFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideDisplayControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideDisplayLayoutFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideDragAndDropControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideFloatingContentCoordinatorFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideFullscreenTaskListenerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideHideDisplayCutoutControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideHideDisplayCutoutFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideLegacySplitScreenFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideOneHandedControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideOneHandedFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvidePipAppOpsListenerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvidePipMediaControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvidePipSurfaceTransactionHelperFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvidePipUiEventLoggerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideRemoteTransitionsFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideRootTaskDisplayAreaOrganizerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideShellCommandHandlerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideShellCommandHandlerImplFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideShellInitFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideShellInitImplFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideShellTaskOrganizerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideSizeCompatUIControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideSplitScreenControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideSplitScreenFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideStartingSurfaceFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideStartingWindowControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideSyncTransactionQueueFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideSystemWindowsFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideTaskSurfaceHelperControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideTaskSurfaceHelperFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideTaskViewFactoryControllerFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideTaskViewFactoryFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideTransactionPoolFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideTransitionsFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProvideWindowManagerShellWrapperFactory;
import com.android.systemui.wmshell.WMShellBaseModule_ProviderTaskStackListenerImplFactory;
import com.android.systemui.wmshell.WMShellConcurrencyModule_ProvideShellAnimationExecutorFactory;
import com.android.systemui.wmshell.WMShellConcurrencyModule_ProvideShellMainExecutorFactory;
import com.android.systemui.wmshell.WMShellConcurrencyModule_ProvideShellMainHandlerFactory;
import com.android.systemui.wmshell.WMShellConcurrencyModule_ProvideSplashScreenExecutorFactory;
import com.android.systemui.wmshell.WMShellConcurrencyModule_ProvideSysUIMainExecutorFactory;
import com.android.systemui.wmshell.WMShellModule_ProvideAppPairsFactory;
import com.android.systemui.wmshell.WMShellModule_ProvideDisplayImeControllerFactory;
import com.android.systemui.wmshell.WMShellModule_ProvideLegacySplitScreenFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipAnimationControllerFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipBoundsStateFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipMotionHelperFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipSnapAlgorithmFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipTaskOrganizerFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipTouchHandlerFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidePipTransitionControllerFactory;
import com.android.systemui.wmshell.WMShellModule_ProvideStartingWindowTypeAlgorithmFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidesPipBoundsAlgorithmFactory;
import com.android.systemui.wmshell.WMShellModule_ProvidesPipPhoneMenuControllerFactory;
import com.android.systemui.wmshell.WMShell_Factory;
import com.motorola.rro.RROsControllerImpl;
import com.motorola.rro.RROsControllerImpl_Factory;
import com.motorola.systemui.cli.navgesture.CliNavGestureController;
import com.motorola.systemui.cli.navgesture.CliNavGestureController_Factory;
import com.motorola.systemui.cli.navgesture.MultiUserCliNavGestures;
import com.motorola.systemui.cli.navgesture.MultiUserCliNavGestures_Factory;
import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager;
import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager_Factory;
import com.motorola.systemui.desktop.dagger.DesktopGlobalRootComponent;
import com.motorola.systemui.desktop.dagger.DesktopSysUIComponent;
import com.motorola.systemui.desktop.dagger.statusbar.phone.DesktopStatusBarComponent;
import com.motorola.systemui.desktop.dagger.statusbar.phone.DesktopStatusBarPhoneModule_ProvideCliStatusBarFactory;
import com.motorola.systemui.desktop.dagger.statusbar.phone.DesktopStatusBarPhoneModule_ProvideStatusBarFactory;
import com.motorola.systemui.desktop.overwrites.statusbar.DesktopStatusBarStateControllerImpl;
import com.motorola.systemui.desktop.overwrites.statusbar.DesktopStatusBarStateControllerImpl_Factory;
import com.motorola.systemui.desktop.overwrites.statusbar.notification.DesktopHeadsUpController;
import com.motorola.systemui.desktop.overwrites.statusbar.notification.DesktopHeadsUpController_Factory;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopNotificationActivityStarter_Factory;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopStatusBarNotificationPresenter;
import com.motorola.systemui.desktop.overwrites.statusbar.phone.DesktopStatusBarNotificationPresenter_MembersInjector;
import com.motorola.systemui.desktop.util.TooltipPopupManager;
import com.motorola.systemui.desktop.util.TooltipPopupManager_Factory;
import com.motorola.systemui.screenshot.MotoTakeScreenshotService;
import com.motorola.systemui.screenshot.MotoTakeScreenshotService_Factory;
import com.motorola.systemui.statusbar.policy.CellLocationControllerImpl;
import com.motorola.systemui.statusbar.policy.CellLocationControllerImpl_Factory;
import com.motorola.systemui.statusbar.policy.NfcControllerImpl;
import com.motorola.systemui.statusbar.policy.NfcControllerImpl_Factory;
import com.motorola.taskbar.MotoTaskBarController;
import com.motorola.taskbar.MotoTaskBarController_Factory;
import dagger.Lazy;
import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.InstanceFactory;
import dagger.internal.MapProviderFactory;
import dagger.internal.Preconditions;
import dagger.internal.SetFactory;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class DaggerDesktopGlobalRootComponent implements DesktopGlobalRootComponent {
    private static final Provider ABSENT_JDK_OPTIONAL_PROVIDER = InstanceFactory.create(Optional.empty());
    /* access modifiers changed from: private */
    public Provider<AudioFxControllerImpl> audioFxControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<BuildInfo> buildInfoProvider;
    /* access modifiers changed from: private */
    public Provider<CellLocationControllerImpl> cellLocationControllerImplProvider;
    /* access modifiers changed from: private */
    public final Context context;
    /* access modifiers changed from: private */
    public Provider<Context> contextProvider;
    /* access modifiers changed from: private */
    public Provider<MultiUserCliNavGestures> multiUserCliNavGesturesProvider;
    /* access modifiers changed from: private */
    public Provider<AccessibilityManager> provideAccessibilityManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ActivityManager> provideActivityManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ActivityTaskManager> provideActivityTaskManagerProvider;
    /* access modifiers changed from: private */
    public Provider<AlarmManager> provideAlarmManagerProvider;
    /* access modifiers changed from: private */
    public Provider<AudioManager> provideAudioManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ColorDisplayManager> provideColorDisplayManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ConnectivityManager> provideConnectivityManagagerProvider;
    /* access modifiers changed from: private */
    public Provider<ContentResolver> provideContentResolverProvider;
    /* access modifiers changed from: private */
    public Provider<CrossWindowBlurListeners> provideCrossWindowBlurListenersProvider;
    /* access modifiers changed from: private */
    public Provider<DevicePolicyManager> provideDevicePolicyManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Integer> provideDisplayIdProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayManager> provideDisplayManagerProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayMetrics> provideDisplayMetricsProvider;
    /* access modifiers changed from: private */
    public Provider<Execution> provideExecutionProvider;
    /* access modifiers changed from: private */
    public Provider<FaceManager> provideFaceManagerProvider;
    /* access modifiers changed from: private */
    public Provider<IActivityManager> provideIActivityManagerProvider;
    /* access modifiers changed from: private */
    public Provider<IActivityTaskManager> provideIActivityTaskManagerProvider;
    /* access modifiers changed from: private */
    public Provider<IAudioService> provideIAudioServiceProvider;
    /* access modifiers changed from: private */
    public Provider<IDreamManager> provideIDreamManagerProvider;
    /* access modifiers changed from: private */
    public Provider<IPackageManager> provideIPackageManagerProvider;
    /* access modifiers changed from: private */
    public Provider<IStatusBarService> provideIStatusBarServiceProvider;
    /* access modifiers changed from: private */
    public Provider<IWindowManager> provideIWindowManagerProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardManager> provideKeyguardManagerProvider;
    /* access modifiers changed from: private */
    public Provider<LauncherApps> provideLauncherAppsProvider;
    /* access modifiers changed from: private */
    public Provider<Executor> provideMainExecutorProvider;
    /* access modifiers changed from: private */
    public Provider<Handler> provideMainHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<MediaRouter2Manager> provideMediaRouter2ManagerProvider;
    /* access modifiers changed from: private */
    public Provider<MediaSessionManager> provideMediaSessionManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NetworkScoreManager> provideNetworkScoreManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationManager> provideNotificationManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Optional<Vibrator>> provideOptionalVibratorProvider;
    /* access modifiers changed from: private */
    public Provider<OverlayManager> provideOverlayManagerProvider;
    /* access modifiers changed from: private */
    public Provider<PackageManager> providePackageManagerProvider;
    /* access modifiers changed from: private */
    public Provider<PackageManagerWrapper> providePackageManagerWrapperProvider;
    /* access modifiers changed from: private */
    public Provider<PermissionManager> providePermissionManagerProvider;
    /* access modifiers changed from: private */
    public Provider<PowerManager> providePowerManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Resources> provideResourcesProvider;
    /* access modifiers changed from: private */
    public Provider<SensorPrivacyManager> provideSensorPrivacyManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ShortcutManager> provideShortcutManagerProvider;
    /* access modifiers changed from: private */
    public Provider<SmartspaceManager> provideSmartspaceManagerProvider;
    /* access modifiers changed from: private */
    public Provider<SubscriptionManager> provideSubcriptionManagerProvider;
    /* access modifiers changed from: private */
    public Provider<TelecomManager> provideTelecomManagerProvider;
    /* access modifiers changed from: private */
    public Provider<TelephonyManager> provideTelephonyManagerProvider;
    /* access modifiers changed from: private */
    public Provider<TrustManager> provideTrustManagerProvider;
    /* access modifiers changed from: private */
    public Provider<UiEventLogger> provideUiEventLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<UserManager> provideUserManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Vibrator> provideVibratorProvider;
    /* access modifiers changed from: private */
    public Provider<ViewConfiguration> provideViewConfigurationProvider;
    /* access modifiers changed from: private */
    public Provider<WallpaperManager> provideWallpaperManagerProvider;
    /* access modifiers changed from: private */
    public Provider<WifiManager> provideWifiManagerProvider;
    /* access modifiers changed from: private */
    public Provider<WindowManager> provideWindowManagerProvider;
    /* access modifiers changed from: private */
    public Provider<FingerprintManager> providesFingerprintManagerProvider;
    /* access modifiers changed from: private */
    public Provider<SensorManager> providesSensorManagerProvider;
    /* access modifiers changed from: private */
    public Provider<QSExpansionPathInterpolator> qSExpansionPathInterpolatorProvider;

    private DaggerDesktopGlobalRootComponent(GlobalModule globalModule, Context context2) {
        this.context = context2;
        initialize(globalModule, context2);
    }

    public static DesktopGlobalRootComponent.Builder builder() {
        return new Builder();
    }

    private void initialize(GlobalModule globalModule, Context context2) {
        this.provideIWindowManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIWindowManagerFactory.create());
        this.contextProvider = InstanceFactory.create(context2);
        this.provideMainHandlerProvider = GlobalConcurrencyModule_ProvideMainHandlerFactory.create(GlobalConcurrencyModule_ProvideMainLooperFactory.create());
        this.provideIStatusBarServiceProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIStatusBarServiceFactory.create());
        this.provideWindowManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideWindowManagerFactory.create(this.contextProvider));
        this.provideLauncherAppsProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideLauncherAppsFactory.create(this.contextProvider));
        this.provideUiEventLoggerProvider = DoubleCheck.provider(GlobalModule_ProvideUiEventLoggerFactory.create());
        this.providePackageManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvidePackageManagerFactory.create(this.contextProvider));
        this.provideContentResolverProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideContentResolverFactory.create(this.contextProvider));
        this.provideUserManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideUserManagerFactory.create(this.contextProvider));
        this.provideMainExecutorProvider = GlobalConcurrencyModule_ProvideMainExecutorFactory.create(this.contextProvider);
        this.provideDisplayMetricsProvider = GlobalModule_ProvideDisplayMetricsFactory.create(globalModule, this.contextProvider);
        this.providePowerManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvidePowerManagerFactory.create(this.contextProvider));
        this.provideViewConfigurationProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideViewConfigurationFactory.create(this.contextProvider));
        this.provideResourcesProvider = FrameworkServicesModule_ProvideResourcesFactory.create(this.contextProvider);
        this.provideAudioManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideAudioManagerFactory.create(this.contextProvider));
        this.provideActivityTaskManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideActivityTaskManagerFactory.create());
        this.providesFingerprintManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvidesFingerprintManagerFactory.create(this.contextProvider));
        this.provideFaceManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideFaceManagerFactory.create(this.contextProvider));
        this.provideExecutionProvider = DoubleCheck.provider(ExecutionImpl_Factory.create());
        this.buildInfoProvider = DoubleCheck.provider(BuildInfo_Factory.create());
        this.provideNotificationManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideNotificationManagerFactory.create(this.contextProvider));
        this.provideDevicePolicyManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideDevicePolicyManagerFactory.create(this.contextProvider));
        this.provideKeyguardManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideKeyguardManagerFactory.create(this.contextProvider));
        this.providePackageManagerWrapperProvider = DoubleCheck.provider(FrameworkServicesModule_ProvidePackageManagerWrapperFactory.create());
        this.provideIActivityManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIActivityManagerFactory.create());
        this.providesSensorManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvidesSensorManagerFactory.create(this.contextProvider));
        this.provideTrustManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideTrustManagerFactory.create(this.contextProvider));
        this.provideTelephonyManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideTelephonyManagerFactory.create(this.contextProvider));
        this.provideIActivityTaskManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIActivityTaskManagerFactory.create());
        this.provideAccessibilityManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideAccessibilityManagerFactory.create(this.contextProvider));
        this.provideCrossWindowBlurListenersProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideCrossWindowBlurListenersFactory.create());
        this.provideAlarmManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideAlarmManagerFactory.create(this.contextProvider));
        this.provideWallpaperManagerProvider = FrameworkServicesModule_ProvideWallpaperManagerFactory.create(this.contextProvider);
        this.provideMediaSessionManagerProvider = FrameworkServicesModule_ProvideMediaSessionManagerFactory.create(this.contextProvider);
        this.provideMediaRouter2ManagerProvider = FrameworkServicesModule_ProvideMediaRouter2ManagerFactory.create(this.contextProvider);
        this.provideSensorPrivacyManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideSensorPrivacyManagerFactory.create(this.contextProvider));
        this.provideIPackageManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIPackageManagerFactory.create());
        this.provideIDreamManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIDreamManagerFactory.create());
        this.provideSmartspaceManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideSmartspaceManagerFactory.create(this.contextProvider));
        this.provideVibratorProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideVibratorFactory.create(this.contextProvider));
        this.provideDisplayManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideDisplayManagerFactory.create(this.contextProvider));
        this.provideTelecomManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideTelecomManagerFactory.create(this.contextProvider));
        this.provideActivityManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideActivityManagerFactory.create(this.contextProvider));
        this.provideOverlayManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideOverlayManagerFactory.create(this.contextProvider));
        this.provideOptionalVibratorProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideOptionalVibratorFactory.create(this.contextProvider));
        this.provideIAudioServiceProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideIAudioServiceFactory.create());
        this.provideSubcriptionManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideSubcriptionManagerFactory.create(this.contextProvider));
        this.provideConnectivityManagagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideConnectivityManagagerFactory.create(this.contextProvider));
        this.provideWifiManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideWifiManagerFactory.create(this.contextProvider));
        this.provideNetworkScoreManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideNetworkScoreManagerFactory.create(this.contextProvider));
        this.provideColorDisplayManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideColorDisplayManagerFactory.create(this.contextProvider));
        this.provideShortcutManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvideShortcutManagerFactory.create(this.contextProvider));
        this.cellLocationControllerImplProvider = DoubleCheck.provider(CellLocationControllerImpl_Factory.create(this.contextProvider));
        this.multiUserCliNavGesturesProvider = DoubleCheck.provider(MultiUserCliNavGestures_Factory.create(this.contextProvider));
        this.audioFxControllerImplProvider = DoubleCheck.provider(AudioFxControllerImpl_Factory.create(this.contextProvider));
        this.provideDisplayIdProvider = FrameworkServicesModule_ProvideDisplayIdFactory.create(this.contextProvider);
        this.qSExpansionPathInterpolatorProvider = DoubleCheck.provider(QSExpansionPathInterpolator_Factory.create());
        this.providePermissionManagerProvider = DoubleCheck.provider(FrameworkServicesModule_ProvidePermissionManagerFactory.create(this.contextProvider));
    }

    public WMComponent.Builder getWMComponentBuilder() {
        return new WMComponentBuilder();
    }

    public ThreadFactory createThreadFactory() {
        return ThreadFactoryImpl_Factory.newInstance();
    }

    public DesktopSysUIComponent.Builder getSysUIComponent() {
        return new DesktopSysUIComponentBuilder();
    }

    /* access modifiers changed from: private */
    public static <T> Provider<Optional<T>> absentJdkOptionalProvider() {
        return ABSENT_JDK_OPTIONAL_PROVIDER;
    }

    private static final class PresentJdkOptionalInstanceProvider<T> implements Provider<Optional<T>> {
        private final Provider<T> delegate;

        private PresentJdkOptionalInstanceProvider(Provider<T> provider) {
            this.delegate = (Provider) Preconditions.checkNotNull(provider);
        }

        public Optional<T> get() {
            return Optional.of(this.delegate.get());
        }

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static <T> Provider<Optional<T>> m102of(Provider<T> provider) {
            return new PresentJdkOptionalInstanceProvider(provider);
        }
    }

    private static final class PresentJdkOptionalLazyProvider<T> implements Provider<Optional<Lazy<T>>> {
        private final Provider<T> delegate;

        private PresentJdkOptionalLazyProvider(Provider<T> provider) {
            this.delegate = (Provider) Preconditions.checkNotNull(provider);
        }

        public Optional<Lazy<T>> get() {
            return Optional.of(DoubleCheck.lazy(this.delegate));
        }

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static <T> Provider<Optional<Lazy<T>>> m103of(Provider<T> provider) {
            return new PresentJdkOptionalLazyProvider(provider);
        }
    }

    private static final class Builder implements DesktopGlobalRootComponent.Builder {
        private Context context;

        private Builder() {
        }

        public Builder context(Context context2) {
            this.context = (Context) Preconditions.checkNotNull(context2);
            return this;
        }

        public DesktopGlobalRootComponent build() {
            Preconditions.checkBuilderRequirement(this.context, Context.class);
            return new DaggerDesktopGlobalRootComponent(new GlobalModule(), this.context);
        }
    }

    private final class WMComponentBuilder implements WMComponent.Builder {
        private WMComponentBuilder() {
        }

        public WMComponent build() {
            return new WMComponentImpl();
        }
    }

    private final class WMComponentImpl implements WMComponent {
        private Provider<Optional<AppPairsController>> optionalOfAppPairsControllerProvider;
        private Provider<Optional<LegacySplitScreenController>> optionalOfLegacySplitScreenControllerProvider;
        private Provider<Optional<PipTouchHandler>> optionalOfPipTouchHandlerProvider;
        private Provider<AppPairsController> provideAppPairsProvider;
        private Provider<Optional<AppPairs>> provideAppPairsProvider2;
        private Provider<Optional<BubbleController>> provideBubbleControllerProvider;
        private Provider<Optional<Bubbles>> provideBubblesProvider;
        private Provider<DisplayController> provideDisplayControllerProvider;
        private Provider<DisplayImeController> provideDisplayImeControllerProvider;
        private Provider<DisplayLayout> provideDisplayLayoutProvider;
        private Provider<DragAndDropController> provideDragAndDropControllerProvider;
        private Provider<FloatingContentCoordinator> provideFloatingContentCoordinatorProvider;
        private Provider<FullscreenTaskListener> provideFullscreenTaskListenerProvider;
        private Provider<Optional<HideDisplayCutoutController>> provideHideDisplayCutoutControllerProvider;
        private Provider<Optional<HideDisplayCutout>> provideHideDisplayCutoutProvider;
        private Provider<LegacySplitScreenController> provideLegacySplitScreenProvider;
        private Provider<Optional<LegacySplitScreen>> provideLegacySplitScreenProvider2;
        private Provider<Optional<OneHandedController>> provideOneHandedControllerProvider;
        private Provider<Optional<OneHanded>> provideOneHandedProvider;
        private Provider<PipAnimationController> providePipAnimationControllerProvider;
        private Provider<PipAppOpsListener> providePipAppOpsListenerProvider;
        private Provider<PipBoundsState> providePipBoundsStateProvider;
        private Provider<PipMediaController> providePipMediaControllerProvider;
        private Provider<PipMotionHelper> providePipMotionHelperProvider;
        private Provider<Optional<Pip>> providePipProvider;
        private Provider<PipSnapAlgorithm> providePipSnapAlgorithmProvider;
        private Provider<PipSurfaceTransactionHelper> providePipSurfaceTransactionHelperProvider;
        private Provider<PipTaskOrganizer> providePipTaskOrganizerProvider;
        private Provider<PipTouchHandler> providePipTouchHandlerProvider;
        private Provider<PipTransitionController> providePipTransitionControllerProvider;
        private Provider<PipUiEventLogger> providePipUiEventLoggerProvider;
        private Provider<ShellTransitions> provideRemoteTransitionsProvider;
        private Provider<RootTaskDisplayAreaOrganizer> provideRootTaskDisplayAreaOrganizerProvider;
        private Provider<ShellExecutor> provideShellAnimationExecutorProvider;
        private Provider<ShellCommandHandlerImpl> provideShellCommandHandlerImplProvider;
        private Provider<Optional<ShellCommandHandler>> provideShellCommandHandlerProvider;
        private Provider<ShellInitImpl> provideShellInitImplProvider;
        private Provider<ShellInit> provideShellInitProvider;
        private Provider<ShellExecutor> provideShellMainExecutorProvider;
        private Provider<AnimationHandler> provideShellMainExecutorSfVsyncAnimationHandlerProvider;
        private Provider<Handler> provideShellMainHandlerProvider;
        private Provider<ShellTaskOrganizer> provideShellTaskOrganizerProvider;
        private Provider<SizeCompatUIController> provideSizeCompatUIControllerProvider;
        private Provider<ShellExecutor> provideSplashScreenExecutorProvider;
        private Provider<Optional<SplitScreenController>> provideSplitScreenControllerProvider;
        private Provider<Optional<SplitScreen>> provideSplitScreenProvider;
        private Provider<Optional<StartingSurface>> provideStartingSurfaceProvider;
        private Provider<StartingWindowController> provideStartingWindowControllerProvider;
        private Provider<StartingWindowTypeAlgorithm> provideStartingWindowTypeAlgorithmProvider;
        private Provider<SyncTransactionQueue> provideSyncTransactionQueueProvider;
        private Provider<ShellExecutor> provideSysUIMainExecutorProvider;
        private Provider<SystemWindows> provideSystemWindowsProvider;
        private Provider<Optional<TaskSurfaceHelperController>> provideTaskSurfaceHelperControllerProvider;
        private Provider<Optional<TaskSurfaceHelper>> provideTaskSurfaceHelperProvider;
        private Provider<TaskViewFactoryController> provideTaskViewFactoryControllerProvider;
        private Provider<Optional<TaskViewFactory>> provideTaskViewFactoryProvider;
        private Provider<TransactionPool> provideTransactionPoolProvider;
        private Provider<Transitions> provideTransitionsProvider;
        private Provider<WindowManagerShellWrapper> provideWindowManagerShellWrapperProvider;
        private Provider<TaskStackListenerImpl> providerTaskStackListenerImplProvider;
        private Provider<PipBoundsAlgorithm> providesPipBoundsAlgorithmProvider;
        private Provider<PhonePipMenuController> providesPipPhoneMenuControllerProvider;

        private WMComponentImpl() {
            initialize();
        }

        private void initialize() {
            this.provideShellMainHandlerProvider = DoubleCheck.provider(WMShellConcurrencyModule_ProvideShellMainHandlerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            this.provideSysUIMainExecutorProvider = DoubleCheck.provider(WMShellConcurrencyModule_ProvideSysUIMainExecutorFactory.create(DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            this.provideShellMainExecutorProvider = DoubleCheck.provider(WMShellConcurrencyModule_ProvideShellMainExecutorFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideShellMainHandlerProvider, this.provideSysUIMainExecutorProvider));
            this.provideDisplayControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideDisplayControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.provideShellMainExecutorProvider));
            this.provideTransactionPoolProvider = DoubleCheck.provider(WMShellBaseModule_ProvideTransactionPoolFactory.create());
            this.provideDisplayImeControllerProvider = DoubleCheck.provider(WMShellModule_ProvideDisplayImeControllerFactory.create(DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.provideDisplayControllerProvider, this.provideShellMainExecutorProvider, this.provideTransactionPoolProvider));
            this.provideDragAndDropControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideDragAndDropControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideDisplayControllerProvider));
            this.provideSyncTransactionQueueProvider = DoubleCheck.provider(WMShellBaseModule_ProvideSyncTransactionQueueFactory.create(this.provideTransactionPoolProvider, this.provideShellMainExecutorProvider));
            this.provideSizeCompatUIControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideSizeCompatUIControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideDisplayControllerProvider, this.provideDisplayImeControllerProvider, this.provideSyncTransactionQueueProvider));
            this.provideShellTaskOrganizerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideShellTaskOrganizerFactory.create(this.provideShellMainExecutorProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideSizeCompatUIControllerProvider));
            this.provideFloatingContentCoordinatorProvider = DoubleCheck.provider(WMShellBaseModule_ProvideFloatingContentCoordinatorFactory.create());
            this.provideWindowManagerShellWrapperProvider = DoubleCheck.provider(WMShellBaseModule_ProvideWindowManagerShellWrapperFactory.create(this.provideShellMainExecutorProvider));
            this.providerTaskStackListenerImplProvider = DoubleCheck.provider(WMShellBaseModule_ProviderTaskStackListenerImplFactory.create(this.provideShellMainHandlerProvider));
            this.provideBubbleControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideBubbleControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideFloatingContentCoordinatorProvider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.provideWindowManagerShellWrapperProvider, DaggerDesktopGlobalRootComponent.this.provideLauncherAppsProvider, this.providerTaskStackListenerImplProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.provideShellTaskOrganizerProvider, this.provideDisplayControllerProvider, this.provideShellMainExecutorProvider, this.provideShellMainHandlerProvider));
            this.provideSystemWindowsProvider = DoubleCheck.provider(WMShellBaseModule_ProvideSystemWindowsFactory.create(this.provideDisplayControllerProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider));
            this.provideShellAnimationExecutorProvider = DoubleCheck.provider(WMShellConcurrencyModule_ProvideShellAnimationExecutorFactory.create());
            this.provideTransitionsProvider = DoubleCheck.provider(WMShellBaseModule_ProvideTransitionsFactory.create(this.provideShellTaskOrganizerProvider, this.provideTransactionPoolProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideShellMainExecutorProvider, this.provideShellAnimationExecutorProvider));
            this.provideShellMainExecutorSfVsyncAnimationHandlerProvider = DoubleCheck.provider(C2213x374a97b.create(this.provideShellMainExecutorProvider));
            Provider<LegacySplitScreenController> provider = DoubleCheck.provider(WMShellModule_ProvideLegacySplitScreenFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideDisplayControllerProvider, this.provideSystemWindowsProvider, this.provideDisplayImeControllerProvider, this.provideTransactionPoolProvider, this.provideShellTaskOrganizerProvider, this.provideSyncTransactionQueueProvider, this.providerTaskStackListenerImplProvider, this.provideTransitionsProvider, this.provideShellMainExecutorProvider, this.provideShellMainExecutorSfVsyncAnimationHandlerProvider));
            this.provideLegacySplitScreenProvider = provider;
            this.optionalOfLegacySplitScreenControllerProvider = PresentJdkOptionalInstanceProvider.m102of(provider);
            this.provideRootTaskDisplayAreaOrganizerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideRootTaskDisplayAreaOrganizerFactory.create(this.provideShellMainExecutorProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.provideSplitScreenControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideSplitScreenControllerFactory.create(this.provideShellTaskOrganizerProvider, this.provideSyncTransactionQueueProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideRootTaskDisplayAreaOrganizerProvider, this.provideShellMainExecutorProvider, this.provideDisplayImeControllerProvider, this.provideTransitionsProvider, this.provideTransactionPoolProvider));
            Provider<AppPairsController> provider2 = DoubleCheck.provider(WMShellModule_ProvideAppPairsFactory.create(this.provideShellTaskOrganizerProvider, this.provideSyncTransactionQueueProvider, this.provideDisplayControllerProvider, this.provideShellMainExecutorProvider, this.provideDisplayImeControllerProvider));
            this.provideAppPairsProvider = provider2;
            this.optionalOfAppPairsControllerProvider = PresentJdkOptionalInstanceProvider.m102of(provider2);
            this.providePipBoundsStateProvider = DoubleCheck.provider(WMShellModule_ProvidePipBoundsStateFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.providePipMediaControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvidePipMediaControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideShellMainHandlerProvider));
            this.providesPipPhoneMenuControllerProvider = DoubleCheck.provider(WMShellModule_ProvidesPipPhoneMenuControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providePipBoundsStateProvider, this.providePipMediaControllerProvider, this.provideSystemWindowsProvider, this.provideShellMainExecutorProvider, this.provideShellMainHandlerProvider));
            this.providePipSnapAlgorithmProvider = DoubleCheck.provider(WMShellModule_ProvidePipSnapAlgorithmFactory.create());
            this.providesPipBoundsAlgorithmProvider = DoubleCheck.provider(WMShellModule_ProvidesPipBoundsAlgorithmFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providePipBoundsStateProvider, this.providePipSnapAlgorithmProvider));
            Provider<PipSurfaceTransactionHelper> provider3 = DoubleCheck.provider(WMShellBaseModule_ProvidePipSurfaceTransactionHelperFactory.create());
            this.providePipSurfaceTransactionHelperProvider = provider3;
            this.providePipAnimationControllerProvider = DoubleCheck.provider(WMShellModule_ProvidePipAnimationControllerFactory.create(provider3));
            this.providePipTransitionControllerProvider = DoubleCheck.provider(WMShellModule_ProvidePipTransitionControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideTransitionsProvider, this.provideShellTaskOrganizerProvider, this.providePipAnimationControllerProvider, this.providesPipBoundsAlgorithmProvider, this.providePipBoundsStateProvider, this.providesPipPhoneMenuControllerProvider));
            this.providePipUiEventLoggerProvider = DoubleCheck.provider(WMShellBaseModule_ProvidePipUiEventLoggerFactory.create(DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider));
            this.providePipTaskOrganizerProvider = DoubleCheck.provider(WMShellModule_ProvidePipTaskOrganizerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideSyncTransactionQueueProvider, this.providePipBoundsStateProvider, this.providesPipBoundsAlgorithmProvider, this.providesPipPhoneMenuControllerProvider, this.providePipAnimationControllerProvider, this.providePipSurfaceTransactionHelperProvider, this.providePipTransitionControllerProvider, this.optionalOfLegacySplitScreenControllerProvider, this.provideDisplayControllerProvider, this.providePipUiEventLoggerProvider, this.provideShellTaskOrganizerProvider, this.provideShellMainExecutorProvider));
            this.providePipMotionHelperProvider = DoubleCheck.provider(WMShellModule_ProvidePipMotionHelperFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providePipBoundsStateProvider, this.providePipTaskOrganizerProvider, this.providesPipPhoneMenuControllerProvider, this.providePipSnapAlgorithmProvider, this.providePipTransitionControllerProvider, this.provideFloatingContentCoordinatorProvider));
            Provider<PipTouchHandler> provider4 = DoubleCheck.provider(WMShellModule_ProvidePipTouchHandlerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesPipPhoneMenuControllerProvider, this.providesPipBoundsAlgorithmProvider, this.providePipBoundsStateProvider, this.providePipTaskOrganizerProvider, this.providePipMotionHelperProvider, this.provideFloatingContentCoordinatorProvider, this.providePipUiEventLoggerProvider, this.provideShellMainExecutorProvider));
            this.providePipTouchHandlerProvider = provider4;
            this.optionalOfPipTouchHandlerProvider = PresentJdkOptionalInstanceProvider.m102of(provider4);
            this.provideFullscreenTaskListenerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideFullscreenTaskListenerFactory.create(this.provideSyncTransactionQueueProvider));
            this.provideSplashScreenExecutorProvider = DoubleCheck.provider(WMShellConcurrencyModule_ProvideSplashScreenExecutorFactory.create());
            this.provideStartingWindowTypeAlgorithmProvider = DoubleCheck.provider(WMShellModule_ProvideStartingWindowTypeAlgorithmFactory.create());
            Provider<StartingWindowController> provider5 = DoubleCheck.provider(WMShellBaseModule_ProvideStartingWindowControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideSplashScreenExecutorProvider, this.provideStartingWindowTypeAlgorithmProvider, this.provideTransactionPoolProvider));
            this.provideStartingWindowControllerProvider = provider5;
            Provider<ShellInitImpl> provider6 = DoubleCheck.provider(WMShellBaseModule_ProvideShellInitImplFactory.create(this.provideDisplayImeControllerProvider, this.provideDragAndDropControllerProvider, this.provideShellTaskOrganizerProvider, this.provideBubbleControllerProvider, this.optionalOfLegacySplitScreenControllerProvider, this.provideSplitScreenControllerProvider, this.optionalOfAppPairsControllerProvider, this.optionalOfPipTouchHandlerProvider, this.provideFullscreenTaskListenerProvider, this.provideTransitionsProvider, provider5, this.provideShellMainExecutorProvider));
            this.provideShellInitImplProvider = provider6;
            this.provideShellInitProvider = DoubleCheck.provider(WMShellBaseModule_ProvideShellInitFactory.create(provider6));
            this.providePipAppOpsListenerProvider = DoubleCheck.provider(WMShellBaseModule_ProvidePipAppOpsListenerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providePipTouchHandlerProvider, this.provideShellMainExecutorProvider));
            this.provideDisplayLayoutProvider = DoubleCheck.provider(WMShellBaseModule_ProvideDisplayLayoutFactory.create());
            this.provideOneHandedControllerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideOneHandedControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.provideDisplayControllerProvider, this.provideDisplayLayoutProvider, this.providerTaskStackListenerImplProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.provideShellMainExecutorProvider, this.provideShellMainHandlerProvider));
            this.providePipProvider = DoubleCheck.provider(WMShellModule_ProvidePipFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideDisplayControllerProvider, this.providePipAppOpsListenerProvider, this.providesPipBoundsAlgorithmProvider, this.providePipBoundsStateProvider, this.providePipMediaControllerProvider, this.providesPipPhoneMenuControllerProvider, this.providePipTaskOrganizerProvider, this.providePipTouchHandlerProvider, this.providePipTransitionControllerProvider, this.provideWindowManagerShellWrapperProvider, this.providerTaskStackListenerImplProvider, this.provideOneHandedControllerProvider, this.provideShellMainExecutorProvider));
            Provider<Optional<HideDisplayCutoutController>> provider7 = DoubleCheck.provider(WMShellBaseModule_ProvideHideDisplayCutoutControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideDisplayControllerProvider, this.provideShellMainExecutorProvider));
            this.provideHideDisplayCutoutControllerProvider = provider7;
            Provider<ShellCommandHandlerImpl> provider8 = DoubleCheck.provider(WMShellBaseModule_ProvideShellCommandHandlerImplFactory.create(this.provideShellTaskOrganizerProvider, this.optionalOfLegacySplitScreenControllerProvider, this.provideSplitScreenControllerProvider, this.providePipProvider, this.provideOneHandedControllerProvider, provider7, this.optionalOfAppPairsControllerProvider, this.provideShellMainExecutorProvider));
            this.provideShellCommandHandlerImplProvider = provider8;
            this.provideShellCommandHandlerProvider = DoubleCheck.provider(WMShellBaseModule_ProvideShellCommandHandlerFactory.create(provider8));
            this.provideOneHandedProvider = DoubleCheck.provider(WMShellBaseModule_ProvideOneHandedFactory.create(this.provideOneHandedControllerProvider));
            this.provideLegacySplitScreenProvider2 = DoubleCheck.provider(WMShellBaseModule_ProvideLegacySplitScreenFactory.create(this.optionalOfLegacySplitScreenControllerProvider));
            this.provideSplitScreenProvider = DoubleCheck.provider(WMShellBaseModule_ProvideSplitScreenFactory.create(this.provideSplitScreenControllerProvider));
            this.provideAppPairsProvider2 = DoubleCheck.provider(WMShellBaseModule_ProvideAppPairsFactory.create(this.optionalOfAppPairsControllerProvider));
            this.provideBubblesProvider = DoubleCheck.provider(WMShellBaseModule_ProvideBubblesFactory.create(this.provideBubbleControllerProvider));
            this.provideHideDisplayCutoutProvider = DoubleCheck.provider(WMShellBaseModule_ProvideHideDisplayCutoutFactory.create(this.provideHideDisplayCutoutControllerProvider));
            Provider<TaskViewFactoryController> provider9 = DoubleCheck.provider(WMShellBaseModule_ProvideTaskViewFactoryControllerFactory.create(this.provideShellTaskOrganizerProvider, this.provideShellMainExecutorProvider));
            this.provideTaskViewFactoryControllerProvider = provider9;
            this.provideTaskViewFactoryProvider = DoubleCheck.provider(WMShellBaseModule_ProvideTaskViewFactoryFactory.create(provider9));
            this.provideRemoteTransitionsProvider = DoubleCheck.provider(WMShellBaseModule_ProvideRemoteTransitionsFactory.create(this.provideTransitionsProvider));
            this.provideStartingSurfaceProvider = DoubleCheck.provider(WMShellBaseModule_ProvideStartingSurfaceFactory.create(this.provideStartingWindowControllerProvider));
            Provider<Optional<TaskSurfaceHelperController>> provider10 = DoubleCheck.provider(WMShellBaseModule_ProvideTaskSurfaceHelperControllerFactory.create(this.provideShellTaskOrganizerProvider, this.provideShellMainExecutorProvider));
            this.provideTaskSurfaceHelperControllerProvider = provider10;
            this.provideTaskSurfaceHelperProvider = DoubleCheck.provider(WMShellBaseModule_ProvideTaskSurfaceHelperFactory.create(provider10));
        }

        public ShellInit getShellInit() {
            return this.provideShellInitProvider.get();
        }

        public Optional<ShellCommandHandler> getShellCommandHandler() {
            return this.provideShellCommandHandlerProvider.get();
        }

        public Optional<OneHanded> getOneHanded() {
            return this.provideOneHandedProvider.get();
        }

        public Optional<Pip> getPip() {
            return this.providePipProvider.get();
        }

        public Optional<LegacySplitScreen> getLegacySplitScreen() {
            return this.provideLegacySplitScreenProvider2.get();
        }

        public Optional<SplitScreen> getSplitScreen() {
            return this.provideSplitScreenProvider.get();
        }

        public Optional<AppPairs> getAppPairs() {
            return this.provideAppPairsProvider2.get();
        }

        public Optional<Bubbles> getBubbles() {
            return this.provideBubblesProvider.get();
        }

        public Optional<HideDisplayCutout> getHideDisplayCutout() {
            return this.provideHideDisplayCutoutProvider.get();
        }

        public Optional<TaskViewFactory> getTaskViewFactory() {
            return this.provideTaskViewFactoryProvider.get();
        }

        public ShellTransitions getTransitions() {
            return this.provideRemoteTransitionsProvider.get();
        }

        public Optional<StartingSurface> getStartingSurface() {
            return this.provideStartingSurfaceProvider.get();
        }

        public Optional<TaskSurfaceHelper> getTaskSurfaceHelper() {
            return this.provideTaskSurfaceHelperProvider.get();
        }
    }

    private final class DesktopSysUIComponentBuilder implements DesktopSysUIComponent.Builder {
        private Optional<AppPairs> setAppPairs;
        private Optional<Bubbles> setBubbles;
        private Optional<HideDisplayCutout> setHideDisplayCutout;
        private Optional<LegacySplitScreen> setLegacySplitScreen;
        private Optional<OneHanded> setOneHanded;
        private Optional<Pip> setPip;
        private Optional<ShellCommandHandler> setShellCommandHandler;
        private Optional<SplitScreen> setSplitScreen;
        private Optional<StartingSurface> setStartingSurface;
        private Optional<TaskSurfaceHelper> setTaskSurfaceHelper;
        private Optional<TaskViewFactory> setTaskViewFactory;
        private ShellTransitions setTransitions;

        private DesktopSysUIComponentBuilder() {
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.pip.Pip>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setPip(java.util.Optional<com.android.p011wm.shell.pip.Pip> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setPip = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setPip(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.legacysplitscreen.LegacySplitScreen>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setLegacySplitScreen(java.util.Optional<com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setLegacySplitScreen = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setLegacySplitScreen(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.splitscreen.SplitScreen>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setSplitScreen(java.util.Optional<com.android.p011wm.shell.splitscreen.SplitScreen> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setSplitScreen = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setSplitScreen(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.apppairs.AppPairs>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setAppPairs(java.util.Optional<com.android.p011wm.shell.apppairs.AppPairs> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setAppPairs = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setAppPairs(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.onehanded.OneHanded>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setOneHanded(java.util.Optional<com.android.p011wm.shell.onehanded.OneHanded> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setOneHanded = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setOneHanded(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.bubbles.Bubbles>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setBubbles(java.util.Optional<com.android.p011wm.shell.bubbles.Bubbles> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setBubbles = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setBubbles(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.lang.Object, java.util.Optional<com.android.wm.shell.TaskViewFactory>] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setTaskViewFactory(java.util.Optional<com.android.p011wm.shell.TaskViewFactory> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setTaskViewFactory = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setTaskViewFactory(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.hidedisplaycutout.HideDisplayCutout>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setHideDisplayCutout(java.util.Optional<com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setHideDisplayCutout = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setHideDisplayCutout(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.ShellCommandHandler>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setShellCommandHandler(java.util.Optional<com.android.p011wm.shell.ShellCommandHandler> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setShellCommandHandler = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setShellCommandHandler(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        public DesktopSysUIComponentBuilder setTransitions(ShellTransitions shellTransitions) {
            this.setTransitions = (ShellTransitions) Preconditions.checkNotNull(shellTransitions);
            return this;
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.util.Optional<com.android.wm.shell.startingsurface.StartingSurface>, java.lang.Object] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setStartingSurface(java.util.Optional<com.android.p011wm.shell.startingsurface.StartingSurface> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setStartingSurface = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setStartingSurface(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [java.lang.Object, java.util.Optional<com.android.wm.shell.tasksurfacehelper.TaskSurfaceHelper>] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder setTaskSurfaceHelper(java.util.Optional<com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelper> r1) {
            /*
                r0 = this;
                java.lang.Object r1 = dagger.internal.Preconditions.checkNotNull(r1)
                java.util.Optional r1 = (java.util.Optional) r1
                r0.setTaskSurfaceHelper = r1
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent.DesktopSysUIComponentBuilder.setTaskSurfaceHelper(java.util.Optional):com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent$DesktopSysUIComponentBuilder");
        }

        public DesktopSysUIComponent build() {
            Preconditions.checkBuilderRequirement(this.setPip, Optional.class);
            Preconditions.checkBuilderRequirement(this.setLegacySplitScreen, Optional.class);
            Preconditions.checkBuilderRequirement(this.setSplitScreen, Optional.class);
            Preconditions.checkBuilderRequirement(this.setAppPairs, Optional.class);
            Preconditions.checkBuilderRequirement(this.setOneHanded, Optional.class);
            Preconditions.checkBuilderRequirement(this.setBubbles, Optional.class);
            Preconditions.checkBuilderRequirement(this.setTaskViewFactory, Optional.class);
            Preconditions.checkBuilderRequirement(this.setHideDisplayCutout, Optional.class);
            Preconditions.checkBuilderRequirement(this.setShellCommandHandler, Optional.class);
            Preconditions.checkBuilderRequirement(this.setTransitions, ShellTransitions.class);
            Preconditions.checkBuilderRequirement(this.setStartingSurface, Optional.class);
            Preconditions.checkBuilderRequirement(this.setTaskSurfaceHelper, Optional.class);
            DaggerDesktopGlobalRootComponent daggerDesktopGlobalRootComponent = DaggerDesktopGlobalRootComponent.this;
            DependencyProvider dependencyProvider = r2;
            DependencyProvider dependencyProvider2 = new DependencyProvider();
            NightDisplayListenerModule nightDisplayListenerModule = r2;
            NightDisplayListenerModule nightDisplayListenerModule2 = new NightDisplayListenerModule();
            UserModule userModule = r2;
            UserModule userModule2 = new UserModule();
            return new DesktopSysUIComponentImpl(dependencyProvider, nightDisplayListenerModule, userModule, this.setPip, this.setLegacySplitScreen, this.setSplitScreen, this.setAppPairs, this.setOneHanded, this.setBubbles, this.setTaskViewFactory, this.setHideDisplayCutout, this.setShellCommandHandler, this.setTransitions, this.setStartingSurface, this.setTaskSurfaceHelper);
        }
    }

    private final class DesktopSysUIComponentImpl implements DesktopSysUIComponent {
        private Provider<AccessibilityButtonModeObserver> accessibilityButtonModeObserverProvider;
        private Provider<AccessibilityButtonTargetsObserver> accessibilityButtonTargetsObserverProvider;
        private Provider<AccessibilityController> accessibilityControllerProvider;
        private Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider;
        private Provider<ActionClickLogger> actionClickLoggerProvider;
        private Provider<ActionProxyReceiver> actionProxyReceiverProvider;
        /* access modifiers changed from: private */
        public Provider<ActivityStarterDelegate> activityStarterDelegateProvider;
        private Provider<UserDetailView.Adapter> adapterProvider;
        private Provider<AirplaneModeTile> airplaneModeTileProvider;
        private Provider<AlarmTile> alarmTileProvider;
        /* access modifiers changed from: private */
        public Provider<AmbientState> ambientStateProvider;
        private Provider<AnimatedImageNotificationManager> animatedImageNotificationManagerProvider;
        private Provider<AppOpsControllerImpl> appOpsControllerImplProvider;
        private Provider<AppOpsCoordinator> appOpsCoordinatorProvider;
        private Provider<AssistLogger> assistLoggerProvider;
        private Provider<AssistManager> assistManagerProvider;
        private Provider<AssistantFeedbackController> assistantFeedbackControllerProvider;
        /* access modifiers changed from: private */
        public Provider<AsyncSensorManager> asyncSensorManagerProvider;
        /* access modifiers changed from: private */
        public Provider<AuthController> authControllerProvider;
        private Provider<BatterySaverTile> batterySaverTileProvider;
        private Provider<BatteryStateNotifier> batteryStateNotifierProvider;
        /* access modifiers changed from: private */
        public Provider<SystemClock> bindSystemClockProvider;
        /* access modifiers changed from: private */
        public Provider<BiometricUnlockController> biometricUnlockControllerProvider;
        private Provider<BluetoothControllerImpl> bluetoothControllerImplProvider;
        private Provider<BluetoothTile> bluetoothTileProvider;
        private Provider<BlurUtils> blurUtilsProvider;
        private Provider<BootCompleteCacheImpl> bootCompleteCacheImplProvider;
        private Provider<BrightLineFalsingManager> brightLineFalsingManagerProvider;
        private Provider<BrightnessDialog> brightnessDialogProvider;
        private Provider<BroadcastDispatcherLogger> broadcastDispatcherLoggerProvider;
        private Provider<BubbleCoordinator> bubbleCoordinatorProvider;
        private Provider builderProvider;
        /* access modifiers changed from: private */
        public Provider<DelayedWakeLock.Builder> builderProvider2;
        private Provider<NotificationClicker.Builder> builderProvider3;
        private Provider<CustomTile.Builder> builderProvider4;
        private Provider<NightDisplayListenerModule.Builder> builderProvider5;
        private Provider<AutoAddTracker.Builder> builderProvider6;
        private Provider<CallbackHandler> callbackHandlerProvider;
        private Provider<CameraToggleTile> cameraToggleTileProvider;
        /* access modifiers changed from: private */
        public Provider<CarrierConfigTracker> carrierConfigTrackerProvider;
        private Provider<CastControllerImpl> castControllerImplProvider;
        private Provider<CastTile> castTileProvider;
        private Provider<CellularTile> cellularTileProvider;
        private Provider<ChannelEditorDialogController> channelEditorDialogControllerProvider;
        private Provider<CliNavGestureController> cliNavGestureControllerProvider;
        private Provider<CliNotificationStackClient> cliNotificationStackClientProvider;
        private Provider<CliStatusBarWindowController> cliStatusBarWindowControllerProvider;
        /* access modifiers changed from: private */
        public Provider<ClockManager> clockManagerProvider;
        private Provider<ColorInversionTile> colorInversionTileProvider;
        private Provider<CommandRegistry> commandRegistryProvider;
        private Provider<ContextComponentResolver> contextComponentResolverProvider;
        private Provider<ControlActionCoordinatorImpl> controlActionCoordinatorImplProvider;
        private Provider<ControlsActivity> controlsActivityProvider;
        private Provider<ControlsBindingControllerImpl> controlsBindingControllerImplProvider;
        private Provider<ControlsComponent> controlsComponentProvider;
        private Provider<ControlsControllerImpl> controlsControllerImplProvider;
        private Provider<ControlsEditingActivity> controlsEditingActivityProvider;
        private Provider<ControlsFavoritingActivity> controlsFavoritingActivityProvider;
        private Provider<ControlsListingControllerImpl> controlsListingControllerImplProvider;
        private Provider<ControlsMetricsLoggerImpl> controlsMetricsLoggerImplProvider;
        private Provider<ControlsProviderSelectorActivity> controlsProviderSelectorActivityProvider;
        private Provider<ControlsRequestDialog> controlsRequestDialogProvider;
        private Provider<ControlsUiControllerImpl> controlsUiControllerImplProvider;
        private Provider<ConversationCoordinator> conversationCoordinatorProvider;
        private Provider<ConversationNotificationManager> conversationNotificationManagerProvider;
        private Provider<ConversationNotificationProcessor> conversationNotificationProcessorProvider;
        private Provider<CreateUserActivity> createUserActivityProvider;
        private Provider<InjectionInflationController.ViewInstanceCreator.Factory> createViewInstanceCreatorFactoryProvider;
        private Provider<CustomIconCache> customIconCacheProvider;
        private Provider<CustomTileStatePersister> customTileStatePersisterProvider;
        private Provider<DarkIconDispatcherImpl> darkIconDispatcherImplProvider;
        private Provider<DataSaverTile> dataSaverTileProvider;
        private Provider<DefaultUiController> defaultUiControllerProvider;
        private Provider<DeleteScreenshotReceiver> deleteScreenshotReceiverProvider;
        private Provider<Dependency> dependencyProvider2;
        private Provider<DesktopDisplayRootModulesManager> desktopDisplayRootModulesManagerProvider;
        private Provider<DesktopNotificationActivityStarter> desktopNotificationActivityStarterProvider;
        private Provider<DesktopStatusBarComponent.Builder> desktopStatusBarComponentBuilderProvider;
        /* access modifiers changed from: private */
        public Provider<DesktopStatusBarStateControllerImpl> desktopStatusBarStateControllerImplProvider;
        private Provider<DeviceConfigProxy> deviceConfigProxyProvider;
        private Provider<DeviceControlsControllerImpl> deviceControlsControllerImplProvider;
        private Provider<DeviceControlsTile> deviceControlsTileProvider;
        /* access modifiers changed from: private */
        public Provider<DeviceProvisionedControllerImpl> deviceProvisionedControllerImplProvider;
        private Provider<DeviceProvisionedCoordinator> deviceProvisionedCoordinatorProvider;
        private Provider diagonalClassifierProvider;
        private Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
        private Provider distanceClassifierProvider;
        private Provider<DndTile> dndTileProvider;
        /* access modifiers changed from: private */
        public Provider<DockManagerImpl> dockManagerImplProvider;
        private Provider<DoubleTapClassifier> doubleTapClassifierProvider;
        private Provider<DozeComponent.Builder> dozeComponentBuilderProvider;
        /* access modifiers changed from: private */
        public Provider<DozeLog> dozeLogProvider;
        private Provider<DozeLogger> dozeLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<DozeParameters> dozeParametersProvider;
        private Provider<DozeScrimController> dozeScrimControllerProvider;
        /* access modifiers changed from: private */
        public Provider<DozeServiceHost> dozeServiceHostProvider;
        private Provider<DozeService> dozeServiceProvider;
        private Provider<DumpHandler> dumpHandlerProvider;
        /* access modifiers changed from: private */
        public Provider<DumpManager> dumpManagerProvider;
        private Provider<DynamicChildBindController> dynamicChildBindControllerProvider;
        /* access modifiers changed from: private */
        public Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
        private Provider<EnhancedEstimatesImpl> enhancedEstimatesImplProvider;
        private Provider<ExpandableNotificationRowComponent.Builder> expandableNotificationRowComponentBuilderProvider;
        private Provider<NotificationLogger.ExpansionStateLogger> expansionStateLoggerProvider;
        private Provider<ExtensionControllerImpl> extensionControllerImplProvider;
        /* access modifiers changed from: private */
        public Provider<BrightnessSlider.Factory> factoryProvider;
        private Provider<EdgeBackGestureHandler.Factory> factoryProvider2;
        /* access modifiers changed from: private */
        public Provider falsingCollectorImplProvider;
        private Provider<FalsingDataProvider> falsingDataProvider;
        /* access modifiers changed from: private */
        public Provider<FalsingManagerProxy> falsingManagerProxyProvider;
        private Provider<FeatureFlagReader> featureFlagReaderProvider;
        /* access modifiers changed from: private */
        public Provider<FeatureFlags> featureFlagsProvider;
        private Provider<Files> filesProvider;
        private Provider<FlashlightControllerImpl> flashlightControllerImplProvider;
        private Provider<FlashlightTile> flashlightTileProvider;
        private Provider<ForegroundServiceController> foregroundServiceControllerProvider;
        /* access modifiers changed from: private */
        public Provider<ForegroundServiceDismissalFeatureController> foregroundServiceDismissalFeatureControllerProvider;
        private Provider<ForegroundServiceNotificationListener> foregroundServiceNotificationListenerProvider;
        /* access modifiers changed from: private */
        public Provider<ForegroundServiceSectionController> foregroundServiceSectionControllerProvider;
        private Provider<FragmentService.FragmentCreator.Factory> fragmentCreatorFactoryProvider;
        private Provider<FragmentService> fragmentServiceProvider;
        private Provider<GarbageMonitor> garbageMonitorProvider;
        private Provider<GlobalActionsComponent> globalActionsComponentProvider;
        private Provider<GlobalActionsDialogFolio> globalActionsDialogFolioProvider;
        /* access modifiers changed from: private */
        public Provider<GlobalActionsDialogLite> globalActionsDialogLiteProvider;
        private Provider<GlobalActionsImpl> globalActionsImplProvider;
        private Provider<GlobalActionsInfoProvider> globalActionsInfoProvider;
        private Provider globalSettingsImplProvider;
        private Provider<GroupCoalescerLogger> groupCoalescerLoggerProvider;
        private Provider<GroupCoalescer> groupCoalescerProvider;
        private Provider<HeadsUpController> headsUpControllerProvider;
        private Provider<HeadsUpCoordinator> headsUpCoordinatorProvider;
        private Provider<HeadsUpViewBinder> headsUpViewBinderProvider;
        private Provider<HideNotifsForOtherUsersCoordinator> hideNotifsForOtherUsersCoordinatorProvider;
        private Provider<HighPriorityProvider> highPriorityProvider;
        private Provider<HistoryTracker> historyTrackerProvider;
        private Provider<HomeSoundEffectController> homeSoundEffectControllerProvider;
        private Provider<HotspotControllerImpl> hotspotControllerImplProvider;
        private Provider<HotspotTile> hotspotTileProvider;
        private Provider<IconBuilder> iconBuilderProvider;
        private Provider<IconManager> iconManagerProvider;
        private Provider imageExporterProvider;
        private Provider imageTileSetProvider;
        private Provider<InitController> initControllerProvider;
        /* access modifiers changed from: private */
        public Provider<InjectionInflationController> injectionInflationControllerProvider;
        private Provider<InstantAppNotifier> instantAppNotifierProvider;
        private Provider<InternetTile> internetTileProvider;
        /* access modifiers changed from: private */
        public Provider<Boolean> isPMLiteEnabledProvider;
        private Provider<Boolean> isReduceBrightColorsAvailableProvider;
        /* access modifiers changed from: private */
        public Provider<KeyguardBypassController> keyguardBypassControllerProvider;
        private Provider<KeyguardCoordinator> keyguardCoordinatorProvider;
        private Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
        private Provider<KeyguardDisplayManager> keyguardDisplayManagerProvider;
        private Provider<KeyguardEnvironmentImpl> keyguardEnvironmentImplProvider;
        private Provider<KeyguardLifecyclesDispatcher> keyguardLifecyclesDispatcherProvider;
        private Provider<KeyguardMediaController> keyguardMediaControllerProvider;
        private Provider<KeyguardSecurityModel> keyguardSecurityModelProvider;
        private Provider<KeyguardService> keyguardServiceProvider;
        /* access modifiers changed from: private */
        public Provider<KeyguardStateControllerImpl> keyguardStateControllerImplProvider;
        private Provider<KeyguardStatusViewComponent.Factory> keyguardStatusViewComponentFactoryProvider;
        /* access modifiers changed from: private */
        public Provider<KeyguardUnlockAnimationController> keyguardUnlockAnimationControllerProvider;
        /* access modifiers changed from: private */
        public Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
        private Provider<LatencyTester> latencyTesterProvider;
        private Provider<LaunchConversationActivity> launchConversationActivityProvider;
        private Provider<LeakReporter> leakReporterProvider;
        /* access modifiers changed from: private */
        public Provider<LightBarController> lightBarControllerProvider;
        private Provider<LocalMediaManagerFactory> localMediaManagerFactoryProvider;
        private Provider<LocationControllerImpl> locationControllerImplProvider;
        private Provider<LocationTile> locationTileProvider;
        private Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
        private Provider<LockscreenShadeTransitionController> lockscreenShadeTransitionControllerProvider;
        /* access modifiers changed from: private */
        public Provider<LockscreenSmartspaceController> lockscreenSmartspaceControllerProvider;
        private Provider<LogBufferEulogizer> logBufferEulogizerProvider;
        private Provider<LogBufferFactory> logBufferFactoryProvider;
        private Provider<LogBufferFreezer> logBufferFreezerProvider;
        private Provider<LongScreenshotActivity> longScreenshotActivityProvider;
        private Provider<LongScreenshotData> longScreenshotDataProvider;
        private Provider<LowPriorityInflationHelper> lowPriorityInflationHelperProvider;
        private Provider<ManagedProfileControllerImpl> managedProfileControllerImplProvider;
        private Provider<Map<Class<?>, Provider<Activity>>> mapOfClassOfAndProviderOfActivityProvider;
        private Provider<Map<Class<?>, Provider<BroadcastReceiver>>> mapOfClassOfAndProviderOfBroadcastReceiverProvider;
        private Provider<Map<Class<?>, Provider<RecentsImplementation>>> mapOfClassOfAndProviderOfRecentsImplementationProvider;
        private Provider<Map<Class<?>, Provider<Service>>> mapOfClassOfAndProviderOfServiceProvider;
        private Provider<Map<Class<?>, Provider<SystemUI>>> mapOfClassOfAndProviderOfSystemUIProvider;
        private Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
        private Provider<MediaBrowserFactory> mediaBrowserFactoryProvider;
        private Provider<MediaCarouselController> mediaCarouselControllerProvider;
        private Provider<MediaControlPanel> mediaControlPanelProvider;
        private Provider<MediaControllerFactory> mediaControllerFactoryProvider;
        private Provider<MediaCoordinator> mediaCoordinatorProvider;
        private Provider<MediaDataFilter> mediaDataFilterProvider;
        private Provider<MediaDataManager> mediaDataManagerProvider;
        private Provider<MediaDeviceManager> mediaDeviceManagerProvider;
        private Provider<MediaFeatureFlag> mediaFeatureFlagProvider;
        /* access modifiers changed from: private */
        public Provider<MediaHierarchyManager> mediaHierarchyManagerProvider;
        private Provider<MediaHostStatesManager> mediaHostStatesManagerProvider;
        private Provider<MediaOutputDialogFactory> mediaOutputDialogFactoryProvider;
        private Provider<MediaOutputDialogReceiver> mediaOutputDialogReceiverProvider;
        private Provider<MediaResumeListener> mediaResumeListenerProvider;
        private Provider<MediaSessionBasedFilter> mediaSessionBasedFilterProvider;
        private Provider<MediaTimeoutListener> mediaTimeoutListenerProvider;
        private Provider<MediaViewController> mediaViewControllerProvider;
        private Provider<GarbageMonitor.MemoryTile> memoryTileProvider;
        private Provider<MicrophoneToggleTile> microphoneToggleTileProvider;
        private Provider<Moto5GTile> moto5GTileProvider;
        private Provider<MotoDesktopProcessTileServices> motoDesktopProcessTileServicesProvider;
        private Provider<MotoGlobalScreenshot> motoGlobalScreenshotProvider;
        private Provider<MotoTakeScreenshotService> motoTakeScreenshotServiceProvider;
        private Provider<MotoTaskBarController> motoTaskBarControllerProvider;
        private Provider<Set<FalsingClassifier>> namedSetOfFalsingClassifierProvider;
        private Provider<NavigationBarOverlayController> navigationBarOverlayControllerProvider;
        private Provider<NavigationModeController> navigationModeControllerProvider;
        /* access modifiers changed from: private */
        public Provider<NetworkControllerImpl> networkControllerImplProvider;
        private Provider<KeyguardViewMediator> newKeyguardViewMediatorProvider;
        private Provider<NextAlarmControllerImpl> nextAlarmControllerImplProvider;
        private Provider<NfcControllerImpl> nfcControllerImplProvider;
        private Provider<NfcTile> nfcTileProvider;
        private Provider<NightDisplayTile> nightDisplayTileProvider;
        private Provider<NotifBindPipelineInitializer> notifBindPipelineInitializerProvider;
        private Provider<NotifBindPipelineLogger> notifBindPipelineLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<NotifBindPipeline> notifBindPipelineProvider;
        private Provider<NotifCollectionLogger> notifCollectionLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<NotifCollection> notifCollectionProvider;
        private Provider<NotifCoordinators> notifCoordinatorsProvider;
        private Provider<NotifInflaterImpl> notifInflaterImplProvider;
        private Provider<NotifInflationErrorManager> notifInflationErrorManagerProvider;
        private Provider<NotifPipelineInitializer> notifPipelineInitializerProvider;
        /* access modifiers changed from: private */
        public Provider<NotifPipeline> notifPipelineProvider;
        private Provider<NotifRemoteViewCacheImpl> notifRemoteViewCacheImplProvider;
        private Provider<NotifViewBarn> notifViewBarnProvider;
        private Provider<NotificationClickNotifier> notificationClickNotifierProvider;
        private Provider<NotificationClickerLogger> notificationClickerLoggerProvider;
        private Provider<NotificationContentInflater> notificationContentInflaterProvider;
        private Provider<NotificationEntryManagerLogger> notificationEntryManagerLoggerProvider;
        private Provider<NotificationFilter> notificationFilterProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationGroupManagerLegacy> notificationGroupManagerLegacyProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationIconAreaController> notificationIconAreaControllerProvider;
        private Provider<NotificationInteractionTracker> notificationInteractionTrackerProvider;
        private Provider<NotificationInterruptStateProviderImpl> notificationInterruptStateProviderImplProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationLockscreenUserManagerImpl> notificationLockscreenUserManagerImplProvider;
        private Provider<NotificationPersonExtractorPluginBoundary> notificationPersonExtractorPluginBoundaryProvider;
        private Provider<NotificationRankingManager> notificationRankingManagerProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationRowBinderImpl> notificationRowBinderImplProvider;
        private Provider<NotificationSectionsFeatureManager> notificationSectionsFeatureManagerProvider;
        private Provider<NotificationSectionsLogger> notificationSectionsLoggerProvider;
        private Provider<NotificationSectionsManager> notificationSectionsManagerProvider;
        private Provider<NotificationShadeDepthController> notificationShadeDepthControllerProvider;
        private Provider<NotificationShadeWindowControllerImpl> notificationShadeWindowControllerImplProvider;
        private Provider<NotificationShelfComponent.Builder> notificationShelfComponentBuilderProvider;
        private Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
        private Provider<NotificationsControllerImpl> notificationsControllerImplProvider;
        private Provider<NotificationsControllerStub> notificationsControllerStubProvider;
        private Provider<OngoingCallLogger> ongoingCallLoggerProvider;
        private Provider<Optional<BcSmartspaceDataPlugin>> optionalOfBcSmartspaceDataPluginProvider;
        private Provider<Optional<ControlsFavoritePersistenceWrapper>> optionalOfControlsFavoritePersistenceWrapperProvider;
        private Provider<Optional<Lazy<StatusBar>>> optionalOfLazyOfStatusBarProvider;
        private Provider<Optional<Recents>> optionalOfRecentsProvider;
        private Provider<Optional<StatusBar>> optionalOfStatusBarProvider;
        private Provider<Optional<UdfpsHbmProvider>> optionalOfUdfpsHbmProvider;
        private Provider<OverviewProxyRecentsImpl> overviewProxyRecentsImplProvider;
        private Provider<OverviewProxyService> overviewProxyServiceProvider;
        /* access modifiers changed from: private */
        public Provider<PeopleNotificationIdentifierImpl> peopleNotificationIdentifierImplProvider;
        private Provider<PeopleSpaceActivity> peopleSpaceActivityProvider;
        private Provider<PeopleSpaceWidgetManager> peopleSpaceWidgetManagerProvider;
        private Provider<PeopleSpaceWidgetPinnedReceiver> peopleSpaceWidgetPinnedReceiverProvider;
        private Provider<PeopleSpaceWidgetProvider> peopleSpaceWidgetProvider;
        private Provider<PhoneStateMonitor> phoneStateMonitorProvider;
        private Provider<PluginDependencyProvider> pluginDependencyProvider;
        private Provider pointerCountClassifierProvider;
        private Provider<PowerNotificationWarnings> powerNotificationWarningsProvider;
        private Provider<PowerUI> powerUIProvider;
        private Provider<PreparationCoordinatorLogger> preparationCoordinatorLoggerProvider;
        private Provider<PreparationCoordinator> preparationCoordinatorProvider;
        /* access modifiers changed from: private */
        public Provider<PrivacyDialogController> privacyDialogControllerProvider;
        private Provider<PrivacyDotViewController> privacyDotViewControllerProvider;
        /* access modifiers changed from: private */
        public Provider<PrivacyItemController> privacyItemControllerProvider;
        /* access modifiers changed from: private */
        public Provider<PrivacyLogger> privacyLoggerProvider;
        private Provider<ProtoTracer> protoTracerProvider;
        private Provider<AccessPointControllerImpl> provideAccessPointControllerImplProvider;
        private Provider<AccessibilityFloatingMenuController> provideAccessibilityFloatingMenuControllerProvider;
        private Provider<ActivityManagerWrapper> provideActivityManagerWrapperProvider;
        /* access modifiers changed from: private */
        public Provider<Boolean> provideAllowNotificationLongPressProvider;
        /* access modifiers changed from: private */
        public Provider<AlwaysOnDisplayPolicy> provideAlwaysOnDisplayPolicyProvider;
        /* access modifiers changed from: private */
        public Provider<AmbientDisplayConfiguration> provideAmbientDisplayConfigurationProvider;
        private Provider<AssistUtils> provideAssistUtilsProvider;
        private Provider<AutoHideController> provideAutoHideControllerProvider;
        private Provider<AutoTileManager> provideAutoTileManagerProvider;
        private Provider<DelayableExecutor> provideBackgroundDelayableExecutorProvider;
        /* access modifiers changed from: private */
        public Provider<Executor> provideBackgroundExecutorProvider;
        private Provider<RepeatableExecutor> provideBackgroundRepeatableExecutorProvider;
        /* access modifiers changed from: private */
        public Provider<BatteryController> provideBatteryControllerProvider;
        /* access modifiers changed from: private */
        public Provider<Handler> provideBgHandlerProvider;
        /* access modifiers changed from: private */
        public Provider<Looper> provideBgLooperProvider;
        private Provider<LogBuffer> provideBroadcastDispatcherLogBufferProvider;
        /* access modifiers changed from: private */
        public Provider<Optional<BubblesManager>> provideBubblesManagerProvider;
        private Provider<CliStatusBar> provideCliStatusBarProvider;
        private Provider provideClockInfoListProvider;
        /* access modifiers changed from: private */
        public Provider<CommandQueue> provideCommandQueueProvider;
        private Provider<CommonNotifCollection> provideCommonNotifCollectionProvider;
        /* access modifiers changed from: private */
        public Provider<ConfigurationController> provideConfigurationControllerProvider;
        private Provider<DataSaverController> provideDataSaverControllerProvider;
        private Provider<DelayableExecutor> provideDelayableExecutorProvider;
        /* access modifiers changed from: private */
        public Provider<DemoModeController> provideDemoModeControllerProvider;
        private Provider<DevicePolicyManagerWrapper> provideDevicePolicyManagerWrapperProvider;
        private Provider<LogBuffer> provideDozeLogBufferProvider;
        private Provider<DualSimIconController> provideDualSimIconControllerProvider;
        private Provider<EditUserInfoController> provideEditUserInfoControllerProvider;
        private Provider<Executor> provideExecutorProvider;
        /* access modifiers changed from: private */
        public Provider<GroupExpansionManager> provideGroupExpansionManagerProvider;
        /* access modifiers changed from: private */
        public Provider<GroupMembershipManager> provideGroupMembershipManagerProvider;
        /* access modifiers changed from: private */
        public Provider<Handler> provideHandlerProvider;
        /* access modifiers changed from: private */
        public Provider<HeadsUpManagerPhone> provideHeadsUpManagerPhoneProvider;
        private Provider<INotificationManager> provideINotificationManagerProvider;
        private Provider<IndividualSensorPrivacyController> provideIndividualSensorPrivacyControllerProvider;
        private Provider<LeakDetector> provideLeakDetectorProvider;
        private Provider<String> provideLeakReportEmailProvider;
        private Provider<LocalBluetoothManager> provideLocalBluetoothControllerProvider;
        private Provider<LockPatternUtils> provideLockPatternUtilsProvider;
        private Provider<LogcatEchoTracker> provideLogcatEchoTrackerProvider;
        private Provider<Executor> provideLongRunningExecutorProvider;
        private Provider<Looper> provideLongRunningLooperProvider;
        /* access modifiers changed from: private */
        public Provider<DelayableExecutor> provideMainDelayableExecutorProvider;
        /* access modifiers changed from: private */
        public Provider<MetricsLogger> provideMetricsLoggerProvider;
        private Provider<MotoDisplayManager> provideMotoDisplayManagerProvider;
        private Provider<NavigationBarController> provideNavigationBarControllerProvider;
        private Provider<NightDisplayListener> provideNightDisplayListenerProvider;
        private Provider<LogBuffer> provideNotifInteractionLogBufferProvider;
        private Provider<NotifRemoteViewCache> provideNotifRemoteViewCacheProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationEntryManager> provideNotificationEntryManagerProvider;
        private Provider<NotificationGroupAlertTransferHelper> provideNotificationGroupAlertTransferHelperProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationGutsManager> provideNotificationGutsManagerProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationInterruptStateProvider> provideNotificationInterruptStateProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationListener> provideNotificationListenerProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationLogger> provideNotificationLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationMediaManager> provideNotificationMediaManagerProvider;
        private Provider<NotificationMessagingUtil> provideNotificationMessagingUtilProvider;
        private Provider<NotificationPanelLogger> provideNotificationPanelLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<NotificationRemoteInputManager> provideNotificationRemoteInputManagerProvider;
        private Provider<LogBuffer> provideNotificationSectionLogBufferProvider;
        private Provider<NotificationViewHierarchyManager> provideNotificationViewHierarchyManagerProvider;
        private Provider<NotificationsController> provideNotificationsControllerProvider;
        private Provider<LogBuffer> provideNotificationsLogBufferProvider;
        /* access modifiers changed from: private */
        public Provider<OnUserInteractionCallback> provideOnUserInteractionCallbackProvider;
        /* access modifiers changed from: private */
        public Provider<OngoingCallController> provideOngoingCallControllerProvider;
        /* access modifiers changed from: private */
        public Provider<PluginManager> providePluginManagerProvider;
        private Provider<ThresholdSensor> providePrimaryProxSensorProvider;
        private Provider<LogBuffer> providePrivacyLogBufferProvider;
        private Provider<QuickAccessWalletClient> provideQuickAccessWalletClientProvider;
        private Provider<LogBuffer> provideQuickSettingsLogBufferProvider;
        private Provider<RecentsImplementation> provideRecentsImplProvider;
        private Provider<Recents> provideRecentsProvider;
        private Provider<ReduceBrightColorsController> provideReduceBrightColorsListenerProvider;
        private Provider<ThresholdSensor> provideSecondaryProxSensorProvider;
        private Provider<SensorPrivacyController> provideSensorPrivacyControllerProvider;
        private Provider<SharedPreferences> provideSharePreferencesProvider;
        private Provider<SmartReplyController> provideSmartReplyControllerProvider;
        /* access modifiers changed from: private */
        public Provider<SmartspaceTransitionController> provideSmartspaceTransitionControllerProvider;
        private Provider<StatusBarKeyguardViewManager> provideStatusBarKeyguardViewManagerProvider;
        /* access modifiers changed from: private */
        public Provider<StatusBar> provideStatusBarProvider;
        private Provider<SysUiState> provideSysUiStateProvider;
        private Provider<TaskStackChangeListeners> provideTaskStackChangeListenersProvider;
        private Provider<ThemeOverlayApplier> provideThemeOverlayManagerProvider;
        private Provider<Handler> provideTimeTickHandlerProvider;
        private Provider<LogBuffer> provideToastLogBufferProvider;
        private Provider<Executor> provideUiBackgroundExecutorProvider;
        /* access modifiers changed from: private */
        public Provider<UserTracker> provideUserTrackerProvider;
        /* access modifiers changed from: private */
        public Provider<VisualStabilityManager> provideVisualStabilityManagerProvider;
        /* access modifiers changed from: private */
        public Provider<LayoutInflater> providerLayoutInflaterProvider;
        private Provider<SectionHeaderController> providesAlertingHeaderControllerProvider;
        private Provider<NodeController> providesAlertingHeaderNodeControllerProvider;
        private Provider<SectionHeaderControllerSubcomponent> providesAlertingHeaderSubcomponentProvider;
        private Provider<Set<FalsingClassifier>> providesBrightLineGestureClassifiersProvider;
        /* access modifiers changed from: private */
        public Provider<BroadcastDispatcher> providesBroadcastDispatcherProvider;
        private Provider<Choreographer> providesChoreographerProvider;
        private Provider<Boolean> providesControlsFeatureEnabledProvider;
        private Provider<Float> providesDoubleTapTouchSlopProvider;
        private Provider<SectionHeaderController> providesIncomingHeaderControllerProvider;
        private Provider<NodeController> providesIncomingHeaderNodeControllerProvider;
        private Provider<SectionHeaderControllerSubcomponent> providesIncomingHeaderSubcomponentProvider;
        private Provider<MediaHost> providesKeyguardMediaHostProvider;
        private Provider<ModeSwitchesController> providesModeSwitchesControllerProvider;
        private Provider<SectionHeaderController> providesPeopleHeaderControllerProvider;
        private Provider<NodeController> providesPeopleHeaderNodeControllerProvider;
        private Provider<SectionHeaderControllerSubcomponent> providesPeopleHeaderSubcomponentProvider;
        /* access modifiers changed from: private */
        public Provider<MediaHost> providesQSMediaHostProvider;
        /* access modifiers changed from: private */
        public Provider<MediaHost> providesQuickQSMediaHostProvider;
        /* access modifiers changed from: private */
        public Provider<SectionHeaderController> providesSilentHeaderControllerProvider;
        private Provider<NodeController> providesSilentHeaderNodeControllerProvider;
        private Provider<SectionHeaderControllerSubcomponent> providesSilentHeaderSubcomponentProvider;
        private Provider<Float> providesSingleTapTouchSlopProvider;
        private Provider proximityClassifierProvider;
        /* access modifiers changed from: private */
        public Provider<ProximitySensor> proximitySensorProvider;
        private Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
        /* access modifiers changed from: private */
        public Provider<QSDetailDisplayer> qSDetailDisplayerProvider;
        private Provider<QSFactoryImpl> qSFactoryImplProvider;
        /* access modifiers changed from: private */
        public Provider<QSLogger> qSLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<QSTileHost> qSTileHostProvider;
        private Provider<QuickAccessWalletController> quickAccessWalletControllerProvider;
        private Provider<QuickAccessWalletTile> quickAccessWalletTileProvider;
        private Provider<RROsControllerImpl> rROsControllerImplProvider;
        private Provider<RankingCoordinator> rankingCoordinatorProvider;
        private Provider<RecordingController> recordingControllerProvider;
        private Provider<RecordingService> recordingServiceProvider;
        private Provider<ReduceBrightColorsTile> reduceBrightColorsTileProvider;
        /* access modifiers changed from: private */
        public Provider<RemoteInputQuickSettingsDisabler> remoteInputQuickSettingsDisablerProvider;
        private Provider<RemoteInputUriController> remoteInputUriControllerProvider;
        private Provider<ResumeMediaBrowserFactory> resumeMediaBrowserFactoryProvider;
        private Provider<RingerModeTrackerImpl> ringerModeTrackerImplProvider;
        private Provider<RotationLockControllerImpl> rotationLockControllerImplProvider;
        private Provider<RotationLockTile> rotationLockTileProvider;
        private Provider<RowContentBindStageLogger> rowContentBindStageLoggerProvider;
        /* access modifiers changed from: private */
        public Provider<RowContentBindStage> rowContentBindStageProvider;
        private Provider<ScreenDecorations> screenDecorationsProvider;
        /* access modifiers changed from: private */
        public Provider<ScreenLifecycle> screenLifecycleProvider;
        private Provider<ScreenRecordDialog> screenRecordDialogProvider;
        private Provider<ScreenRecordTile> screenRecordTileProvider;
        private Provider<ScreenshotController> screenshotControllerProvider;
        private Provider<ScreenshotNotificationsController> screenshotNotificationsControllerProvider;
        private Provider<ScreenshotSmartActions> screenshotSmartActionsProvider;
        private Provider<ScreenshotTile> screenshotTileProvider;
        private Provider<ScrimController> scrimControllerProvider;
        private Provider<ScrollCaptureClient> scrollCaptureClientProvider;
        private Provider<ScrollCaptureController> scrollCaptureControllerProvider;
        private Provider<SectionHeaderControllerSubcomponent.Builder> sectionHeaderControllerSubcomponentBuilderProvider;
        /* access modifiers changed from: private */
        public Provider secureSettingsImplProvider;
        /* access modifiers changed from: private */
        public Provider<SecurityControllerImpl> securityControllerImplProvider;
        private Provider<SeekBarViewModel> seekBarViewModelProvider;
        private Provider<SensorUseStartedActivity> sensorUseStartedActivityProvider;
        private Provider<GarbageMonitor.Service> serviceProvider;
        private Provider<Optional<Bubbles>> setBubblesProvider;
        private Provider<Optional<HideDisplayCutout>> setHideDisplayCutoutProvider;
        private Provider<Optional<LegacySplitScreen>> setLegacySplitScreenProvider;
        private Provider<Optional<OneHanded>> setOneHandedProvider;
        private Provider<Optional<Pip>> setPipProvider;
        private Provider<Optional<ShellCommandHandler>> setShellCommandHandlerProvider;
        private Provider<Optional<SplitScreen>> setSplitScreenProvider;
        private Provider<Optional<StartingSurface>> setStartingSurfaceProvider;
        private Provider<Optional<TaskViewFactory>> setTaskViewFactoryProvider;
        private Provider<ShellTransitions> setTransitionsProvider;
        private Provider<ShadeControllerImpl> shadeControllerImplProvider;
        private Provider<ShadeListBuilderLogger> shadeListBuilderLoggerProvider;
        private Provider<ShadeListBuilder> shadeListBuilderProvider;
        private Provider<ShadeViewDifferLogger> shadeViewDifferLoggerProvider;
        private Provider<ShadeViewManagerFactory> shadeViewManagerFactoryProvider;
        private Provider<SharedCoordinatorLogger> sharedCoordinatorLoggerProvider;
        private Provider<ShortcutKeyDispatcher> shortcutKeyDispatcherProvider;
        private Provider<SidefpsController> sidefpsControllerProvider;
        private Provider<SingleTapClassifier> singleTapClassifierProvider;
        private Provider<SliceBroadcastRelayHandler> sliceBroadcastRelayHandlerProvider;
        private Provider<SmartActionInflaterImpl> smartActionInflaterImplProvider;
        private Provider<SmartActionsReceiver> smartActionsReceiverProvider;
        private Provider<SmartReplyConstants> smartReplyConstantsProvider;
        private Provider<SmartReplyInflaterImpl> smartReplyInflaterImplProvider;
        private Provider<SmartReplyStateInflaterImpl> smartReplyStateInflaterImplProvider;
        private Provider<SmartspaceDedupingCoordinator> smartspaceDedupingCoordinatorProvider;
        private Provider<StatusBarContentInsetsProvider> statusBarContentInsetsProvider;
        /* access modifiers changed from: private */
        public Provider<StatusBarIconControllerImpl> statusBarIconControllerImplProvider;
        /* access modifiers changed from: private */
        public Provider<StatusBarLocationPublisher> statusBarLocationPublisherProvider;
        private Provider<StatusBarNotificationActivityStarterLogger> statusBarNotificationActivityStarterLoggerProvider;
        private Provider<StatusBarRemoteInputCallback> statusBarRemoteInputCallbackProvider;
        /* access modifiers changed from: private */
        public Provider<StatusBarStateControllerImpl> statusBarStateControllerImplProvider;
        private Provider<StatusBarWindowController> statusBarWindowControllerProvider;
        /* access modifiers changed from: private */
        public Provider<QSCarrierGroupController.SubscriptionManagerSlotIndexResolver> subscriptionManagerSlotIndexResolverProvider;
        private Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider;
        private Provider<SystemActions> systemActionsProvider;
        private Provider<SystemEventChipAnimationController> systemEventChipAnimationControllerProvider;
        private Provider<SystemEventCoordinator> systemEventCoordinatorProvider;
        private Provider<SystemPropertiesHelper> systemPropertiesHelperProvider;
        /* access modifiers changed from: private */
        public Provider<SystemStatusAnimationScheduler> systemStatusAnimationSchedulerProvider;
        private Provider<SystemUIAuxiliaryDumpService> systemUIAuxiliaryDumpServiceProvider;
        private Provider<SystemUIService> systemUIServiceProvider;
        /* access modifiers changed from: private */
        public Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
        private Provider<TakeScreenshotService> takeScreenshotServiceProvider;
        private Provider<TargetSdkResolver> targetSdkResolverProvider;
        /* access modifiers changed from: private */
        public Provider<TelephonyListenerManager> telephonyListenerManagerProvider;
        private Provider<ThemeOverlayController> themeOverlayControllerProvider;
        private Provider<ToastFactory> toastFactoryProvider;
        private Provider<ToastLogger> toastLoggerProvider;
        private Provider<ToastUI> toastUIProvider;
        private Provider<TooltipPopupManager> tooltipPopupManagerProvider;
        private Provider<TunablePadding.TunablePaddingService> tunablePaddingServiceProvider;
        private Provider<TunerActivity> tunerActivityProvider;
        /* access modifiers changed from: private */
        public Provider<TunerServiceImpl> tunerServiceImplProvider;
        private Provider<TvNotificationHandler> tvNotificationHandlerProvider;
        private Provider<TvNotificationPanelActivity> tvNotificationPanelActivityProvider;
        private Provider<TvNotificationPanel> tvNotificationPanelProvider;
        private Provider<TvOngoingPrivacyChip> tvOngoingPrivacyChipProvider;
        private Provider<TvStatusBar> tvStatusBarProvider;
        private Provider<TvUnblockSensorActivity> tvUnblockSensorActivityProvider;
        private Provider<TypeClassifier> typeClassifierProvider;
        private Provider<UdfpsController> udfpsControllerProvider;
        private Provider<UdfpsHapticsSimulator> udfpsHapticsSimulatorProvider;
        private Provider<UiModeNightTile> uiModeNightTileProvider;
        private Provider<UiOffloadThread> uiOffloadThreadProvider;
        /* access modifiers changed from: private */
        public Provider<UnlockedScreenOffAnimationController> unlockedScreenOffAnimationControllerProvider;
        private Provider<UsbDebuggingActivity> usbDebuggingActivityProvider;
        private Provider<UsbDebuggingSecondaryUserActivity> usbDebuggingSecondaryUserActivityProvider;
        private Provider<UserCreator> userCreatorProvider;
        private Provider<UserSwitcherController.UserDetailAdapter> userDetailAdapterProvider;
        /* access modifiers changed from: private */
        public Provider<UserInfoControllerImpl> userInfoControllerImplProvider;
        /* access modifiers changed from: private */
        public Provider<UserSwitcherController> userSwitcherControllerProvider;
        private Provider<UserTile> userTileProvider;
        private Provider<VibratorHelper> vibratorHelperProvider;
        private Provider<VisualStabilityCoordinator> visualStabilityCoordinatorProvider;
        private Provider<VolumeDialogComponent> volumeDialogComponentProvider;
        private Provider<VolumeDialogControllerImpl> volumeDialogControllerImplProvider;
        private Provider<VolumeUI> volumeUIProvider;
        private Provider<WMShell> wMShellProvider;
        /* access modifiers changed from: private */
        public Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
        private Provider<WalletActivity> walletActivityProvider;
        private Provider<WalletControllerImpl> walletControllerImplProvider;
        private Provider<AccessPointControllerImpl.WifiPickerTrackerFactory> wifiPickerTrackerFactoryProvider;
        private Provider<WifiTile> wifiTileProvider;
        private Provider<WindowMagnification> windowMagnificationProvider;
        private Provider<WorkLockActivity> workLockActivityProvider;
        private Provider<WorkModeTile> workModeTileProvider;
        /* access modifiers changed from: private */
        public Provider<ZenModeControllerImpl> zenModeControllerImplProvider;
        private Provider zigZagClassifierProvider;

        private DesktopSysUIComponentImpl(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            DependencyProvider dependencyProvider3 = dependencyProvider;
            NightDisplayListenerModule nightDisplayListenerModule2 = nightDisplayListenerModule;
            UserModule userModule2 = userModule;
            Optional<Pip> optional12 = optional;
            Optional<LegacySplitScreen> optional13 = optional2;
            Optional<SplitScreen> optional14 = optional3;
            Optional<AppPairs> optional15 = optional4;
            Optional<OneHanded> optional16 = optional5;
            Optional<Bubbles> optional17 = optional6;
            Optional<TaskViewFactory> optional18 = optional7;
            Optional<HideDisplayCutout> optional19 = optional8;
            Optional<ShellCommandHandler> optional20 = optional9;
            ShellTransitions shellTransitions2 = shellTransitions;
            Optional<StartingSurface> optional21 = optional10;
            Optional<TaskSurfaceHelper> optional22 = optional11;
            initialize(dependencyProvider3, nightDisplayListenerModule2, userModule2, optional12, optional13, optional14, optional15, optional16, optional17, optional18, optional19, optional20, shellTransitions2, optional21, optional22);
            initialize2(dependencyProvider3, nightDisplayListenerModule2, userModule2, optional12, optional13, optional14, optional15, optional16, optional17, optional18, optional19, optional20, shellTransitions2, optional21, optional22);
            initialize3(dependencyProvider3, nightDisplayListenerModule2, userModule2, optional12, optional13, optional14, optional15, optional16, optional17, optional18, optional19, optional20, shellTransitions2, optional21, optional22);
            initialize4(dependencyProvider3, nightDisplayListenerModule2, userModule2, optional12, optional13, optional14, optional15, optional16, optional17, optional18, optional19, optional20, shellTransitions2, optional21, optional22);
            initialize5(dependencyProvider3, nightDisplayListenerModule2, userModule2, optional12, optional13, optional14, optional15, optional16, optional17, optional18, optional19, optional20, shellTransitions2, optional21, optional22);
            initialize6(dependencyProvider3, nightDisplayListenerModule2, userModule2, optional12, optional13, optional14, optional15, optional16, optional17, optional18, optional19, optional20, shellTransitions2, optional21, optional22);
        }

        private NotificationSectionsFeatureManager notificationSectionsFeatureManager() {
            return new NotificationSectionsFeatureManager(this.deviceConfigProxyProvider.get(), DaggerDesktopGlobalRootComponent.this.context);
        }

        private SectionHeaderController incomingHeaderSectionHeaderController() {
            return C1579x340f4262.providesIncomingHeaderController(this.providesIncomingHeaderSubcomponentProvider.get());
        }

        private SectionHeaderController peopleHeaderSectionHeaderController() {
            return C1582x812edf99.providesPeopleHeaderController(this.providesPeopleHeaderSubcomponentProvider.get());
        }

        private SectionHeaderController alertingHeaderSectionHeaderController() {
            return C1576x41b9fd82.providesAlertingHeaderController(this.providesAlertingHeaderSubcomponentProvider.get());
        }

        private SectionHeaderController silentHeaderSectionHeaderController() {
            return C1585xcc90df13.providesSilentHeaderController(this.providesSilentHeaderSubcomponentProvider.get());
        }

        /* access modifiers changed from: private */
        public NotificationSectionsManager notificationSectionsManager() {
            return new NotificationSectionsManager(this.desktopStatusBarStateControllerImplProvider.get(), this.provideConfigurationControllerProvider.get(), this.keyguardMediaControllerProvider.get(), notificationSectionsFeatureManager(), this.notificationSectionsLoggerProvider.get(), incomingHeaderSectionHeaderController(), peopleHeaderSectionHeaderController(), alertingHeaderSectionHeaderController(), silentHeaderSectionHeaderController());
        }

        private void initialize(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            DependencyProvider dependencyProvider3 = dependencyProvider;
            Provider<DumpManager> provider = DoubleCheck.provider(DumpManager_Factory.create());
            this.dumpManagerProvider = provider;
            this.bootCompleteCacheImplProvider = DoubleCheck.provider(BootCompleteCacheImpl_Factory.create(provider));
            this.provideConfigurationControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideConfigurationControllerFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.globalSettingsImplProvider = GlobalSettingsImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideContentResolverProvider);
            this.provideDemoModeControllerProvider = DoubleCheck.provider(DemoModeModule_ProvideDemoModeControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dumpManagerProvider, this.globalSettingsImplProvider));
            this.provideLeakDetectorProvider = DoubleCheck.provider(DependencyProvider_ProvideLeakDetectorFactory.create(dependencyProvider));
            Provider<Looper> provider2 = DoubleCheck.provider(SysUIConcurrencyModule_ProvideBgLooperFactory.create());
            this.provideBgLooperProvider = provider2;
            this.provideBgHandlerProvider = SysUIConcurrencyModule_ProvideBgHandlerFactory.create(provider2);
            this.provideUserTrackerProvider = DoubleCheck.provider(SettingsModule_ProvideUserTrackerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, this.dumpManagerProvider, this.provideBgHandlerProvider));
            Provider<TunerServiceImpl> provider3 = DoubleCheck.provider(TunerServiceImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideLeakDetectorProvider, this.provideDemoModeControllerProvider, this.provideUserTrackerProvider));
            this.tunerServiceImplProvider = provider3;
            this.tunerActivityProvider = TunerActivity_Factory.create(this.provideDemoModeControllerProvider, provider3);
            this.provideBackgroundExecutorProvider = DoubleCheck.provider(SysUIConcurrencyModule_ProvideBackgroundExecutorFactory.create(this.provideBgLooperProvider));
            Provider<LogcatEchoTracker> provider4 = DoubleCheck.provider(LogModule_ProvideLogcatEchoTrackerFactory.create(DaggerDesktopGlobalRootComponent.this.provideContentResolverProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create()));
            this.provideLogcatEchoTrackerProvider = provider4;
            Provider<LogBufferFactory> provider5 = DoubleCheck.provider(LogBufferFactory_Factory.create(this.dumpManagerProvider, provider4));
            this.logBufferFactoryProvider = provider5;
            Provider<LogBuffer> provider6 = DoubleCheck.provider(LogModule_ProvideBroadcastDispatcherLogBufferFactory.create(provider5));
            this.provideBroadcastDispatcherLogBufferProvider = provider6;
            this.broadcastDispatcherLoggerProvider = BroadcastDispatcherLogger_Factory.create(provider6);
            Provider<BroadcastDispatcher> provider7 = DoubleCheck.provider(DependencyProvider_ProvidesBroadcastDispatcherFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgLooperProvider, this.provideBackgroundExecutorProvider, this.dumpManagerProvider, this.broadcastDispatcherLoggerProvider, this.provideUserTrackerProvider));
            this.providesBroadcastDispatcherProvider = provider7;
            this.workLockActivityProvider = WorkLockActivity_Factory.create(provider7);
            this.providePluginManagerProvider = DoubleCheck.provider(DependencyProvider_ProvidePluginManagerFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.deviceConfigProxyProvider = DoubleCheck.provider(DeviceConfigProxy_Factory.create());
            this.enhancedEstimatesImplProvider = DoubleCheck.provider(EnhancedEstimatesImpl_Factory.create());
            this.provideBatteryControllerProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideBatteryControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.enhancedEstimatesImplProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, this.providesBroadcastDispatcherProvider, this.provideDemoModeControllerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideBgHandlerProvider));
            this.dockManagerImplProvider = DoubleCheck.provider(DockManagerImpl_Factory.create());
            this.falsingDataProvider = DoubleCheck.provider(FalsingDataProvider_Factory.create(DaggerDesktopGlobalRootComponent.this.provideDisplayMetricsProvider, this.provideBatteryControllerProvider, this.dockManagerImplProvider));
            this.provideMetricsLoggerProvider = DoubleCheck.provider(DependencyProvider_ProvideMetricsLoggerFactory.create(dependencyProvider));
            DistanceClassifier_Factory create = DistanceClassifier_Factory.create(this.falsingDataProvider, this.deviceConfigProxyProvider);
            this.distanceClassifierProvider = create;
            this.proximityClassifierProvider = ProximityClassifier_Factory.create(create, this.falsingDataProvider, this.deviceConfigProxyProvider);
            this.pointerCountClassifierProvider = PointerCountClassifier_Factory.create(this.falsingDataProvider);
            this.typeClassifierProvider = TypeClassifier_Factory.create(this.falsingDataProvider);
            this.diagonalClassifierProvider = DiagonalClassifier_Factory.create(this.falsingDataProvider, this.deviceConfigProxyProvider);
            ZigZagClassifier_Factory create2 = ZigZagClassifier_Factory.create(this.falsingDataProvider, this.deviceConfigProxyProvider);
            this.zigZagClassifierProvider = create2;
            this.providesBrightLineGestureClassifiersProvider = FalsingModule_ProvidesBrightLineGestureClassifiersFactory.create(this.distanceClassifierProvider, this.proximityClassifierProvider, this.pointerCountClassifierProvider, this.typeClassifierProvider, this.diagonalClassifierProvider, create2);
            this.namedSetOfFalsingClassifierProvider = SetFactory.builder(0, 1).addCollectionProvider(this.providesBrightLineGestureClassifiersProvider).build();
            FalsingModule_ProvidesSingleTapTouchSlopFactory create3 = FalsingModule_ProvidesSingleTapTouchSlopFactory.create(DaggerDesktopGlobalRootComponent.this.provideViewConfigurationProvider);
            this.providesSingleTapTouchSlopProvider = create3;
            this.singleTapClassifierProvider = SingleTapClassifier_Factory.create(this.falsingDataProvider, create3);
            FalsingModule_ProvidesDoubleTapTouchSlopFactory create4 = FalsingModule_ProvidesDoubleTapTouchSlopFactory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider);
            this.providesDoubleTapTouchSlopProvider = create4;
            this.doubleTapClassifierProvider = DoubleTapClassifier_Factory.create(this.falsingDataProvider, this.singleTapClassifierProvider, create4, FalsingModule_ProvidesDoubleTapTimeoutMsFactory.create());
            Provider<SystemClock> provider8 = DoubleCheck.provider(SystemClockImpl_Factory.create());
            this.bindSystemClockProvider = provider8;
            this.historyTrackerProvider = DoubleCheck.provider(HistoryTracker_Factory.create(provider8));
            this.ringerModeTrackerImplProvider = DoubleCheck.provider(RingerModeTrackerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideAudioManagerProvider, this.providesBroadcastDispatcherProvider, this.provideBackgroundExecutorProvider));
            this.desktopStatusBarStateControllerImplProvider = DoubleCheck.provider(DesktopStatusBarStateControllerImpl_Factory.create());
            this.provideLockPatternUtilsProvider = DoubleCheck.provider(DependencyProvider_ProvideLockPatternUtilsFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.protoTracerProvider = DoubleCheck.provider(ProtoTracer_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dumpManagerProvider));
            this.commandRegistryProvider = DoubleCheck.provider(CommandRegistry_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider));
            this.provideCommandQueueProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideCommandQueueFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.protoTracerProvider, this.commandRegistryProvider));
            this.providerLayoutInflaterProvider = DoubleCheck.provider(DependencyProvider_ProviderLayoutInflaterFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.provideMainDelayableExecutorProvider = DoubleCheck.provider(SysUIConcurrencyModule_ProvideMainDelayableExecutorFactory.create(GlobalConcurrencyModule_ProvideMainLooperFactory.create()));
            this.systemPropertiesHelperProvider = DoubleCheck.provider(SystemPropertiesHelper_Factory.create());
            Provider<FeatureFlagReader> provider9 = DoubleCheck.provider(FeatureFlagReader_Factory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, DaggerDesktopGlobalRootComponent.this.buildInfoProvider, this.systemPropertiesHelperProvider));
            this.featureFlagReaderProvider = provider9;
            this.featureFlagsProvider = DoubleCheck.provider(FeatureFlags_Factory.create(provider9, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.provideNotificationListenerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationListenerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideNotificationManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            Provider<LogBuffer> provider10 = DoubleCheck.provider(LogModule_ProvideNotificationsLogBufferFactory.create(this.logBufferFactoryProvider));
            this.provideNotificationsLogBufferProvider = provider10;
            this.notificationEntryManagerLoggerProvider = NotificationEntryManagerLogger_Factory.create(provider10);
            Provider<ExtensionControllerImpl> provider11 = DoubleCheck.provider(ExtensionControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideLeakDetectorProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.provideConfigurationControllerProvider));
            this.extensionControllerImplProvider = provider11;
            this.notificationPersonExtractorPluginBoundaryProvider = DoubleCheck.provider(NotificationPersonExtractorPluginBoundary_Factory.create(provider11));
            DelegateFactory delegateFactory = new DelegateFactory();
            this.notificationGroupManagerLegacyProvider = delegateFactory;
            Provider<GroupMembershipManager> provider12 = DoubleCheck.provider(NotificationsModule_ProvideGroupMembershipManagerFactory.create(this.featureFlagsProvider, delegateFactory));
            this.provideGroupMembershipManagerProvider = provider12;
            this.peopleNotificationIdentifierImplProvider = DoubleCheck.provider(PeopleNotificationIdentifierImpl_Factory.create(this.notificationPersonExtractorPluginBoundaryProvider, provider12));
            Factory<Bubbles> create5 = InstanceFactory.create(optional6);
            this.setBubblesProvider = create5;
            DelegateFactory.setDelegate(this.notificationGroupManagerLegacyProvider, DoubleCheck.provider(NotificationGroupManagerLegacy_Factory.create(this.desktopStatusBarStateControllerImplProvider, this.peopleNotificationIdentifierImplProvider, create5)));
            this.provideNotificationMessagingUtilProvider = DependencyProvider_ProvideNotificationMessagingUtilFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.notificationClickNotifierProvider = DoubleCheck.provider(NotificationClickNotifier_Factory.create(DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider));
            this.secureSettingsImplProvider = SecureSettingsImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideContentResolverProvider);
            this.deviceProvisionedControllerImplProvider = DoubleCheck.provider(DeviceProvisionedControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.providesBroadcastDispatcherProvider, this.globalSettingsImplProvider, this.secureSettingsImplProvider));
            this.keyguardStateControllerImplProvider = new DelegateFactory();
            this.notificationLockscreenUserManagerImplProvider = DoubleCheck.provider(NotificationLockscreenUserManagerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, DaggerDesktopGlobalRootComponent.this.provideDevicePolicyManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, this.notificationClickNotifierProvider, DaggerDesktopGlobalRootComponent.this.provideKeyguardManagerProvider, this.desktopStatusBarStateControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.deviceProvisionedControllerImplProvider, this.keyguardStateControllerImplProvider));
            DelegateFactory delegateFactory2 = new DelegateFactory();
            this.provideNotificationEntryManagerProvider = delegateFactory2;
            this.provideSmartReplyControllerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideSmartReplyControllerFactory.create(delegateFactory2, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, this.notificationClickNotifierProvider));
            this.provideStatusBarProvider = new DelegateFactory();
            this.provideHandlerProvider = DependencyProvider_ProvideHandlerFactory.create(dependencyProvider);
            this.remoteInputUriControllerProvider = DoubleCheck.provider(RemoteInputUriController_Factory.create(DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider));
            Provider<LogBuffer> provider13 = DoubleCheck.provider(LogModule_ProvideNotifInteractionLogBufferFactory.create(this.logBufferFactoryProvider));
            this.provideNotifInteractionLogBufferProvider = provider13;
            this.actionClickLoggerProvider = ActionClickLogger_Factory.create(provider13);
            this.provideNotificationRemoteInputManagerProvider = DoubleCheck.provider(C1497xfa996c5e.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.notificationLockscreenUserManagerImplProvider, this.provideSmartReplyControllerProvider, this.provideNotificationEntryManagerProvider, this.provideStatusBarProvider, this.desktopStatusBarStateControllerImplProvider, this.provideHandlerProvider, this.remoteInputUriControllerProvider, this.notificationClickNotifierProvider, this.actionClickLoggerProvider));
            this.notifCollectionLoggerProvider = NotifCollectionLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.filesProvider = DoubleCheck.provider(Files_Factory.create());
            this.logBufferEulogizerProvider = DoubleCheck.provider(LogBufferEulogizer_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dumpManagerProvider, this.bindSystemClockProvider, this.filesProvider));
            this.notifCollectionProvider = DoubleCheck.provider(NotifCollection_Factory.create(DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, this.bindSystemClockProvider, this.featureFlagsProvider, this.notifCollectionLoggerProvider, this.logBufferEulogizerProvider, this.dumpManagerProvider));
            this.shadeListBuilderLoggerProvider = ShadeListBuilderLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            Provider<NotificationInteractionTracker> provider14 = DoubleCheck.provider(NotificationInteractionTracker_Factory.create(this.notificationClickNotifierProvider, this.provideNotificationEntryManagerProvider));
            this.notificationInteractionTrackerProvider = provider14;
            Provider<ShadeListBuilder> provider15 = DoubleCheck.provider(ShadeListBuilder_Factory.create(this.bindSystemClockProvider, this.shadeListBuilderLoggerProvider, this.dumpManagerProvider, provider14));
            this.shadeListBuilderProvider = provider15;
            Provider<NotifPipeline> provider16 = DoubleCheck.provider(NotifPipeline_Factory.create(this.notifCollectionProvider, provider15));
            this.notifPipelineProvider = provider16;
            this.provideCommonNotifCollectionProvider = DoubleCheck.provider(NotificationsModule_ProvideCommonNotifCollectionFactory.create(this.featureFlagsProvider, provider16, this.provideNotificationEntryManagerProvider));
            NotifBindPipelineLogger_Factory create6 = NotifBindPipelineLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.notifBindPipelineLoggerProvider = create6;
            this.notifBindPipelineProvider = DoubleCheck.provider(NotifBindPipeline_Factory.create(this.provideCommonNotifCollectionProvider, create6, GlobalConcurrencyModule_ProvideMainLooperFactory.create()));
            NotifRemoteViewCacheImpl_Factory create7 = NotifRemoteViewCacheImpl_Factory.create(this.provideCommonNotifCollectionProvider);
            this.notifRemoteViewCacheImplProvider = create7;
            this.provideNotifRemoteViewCacheProvider = DoubleCheck.provider(create7);
            this.conversationNotificationManagerProvider = DoubleCheck.provider(ConversationNotificationManager_Factory.create(this.provideNotificationEntryManagerProvider, this.notificationGroupManagerLegacyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            this.conversationNotificationProcessorProvider = ConversationNotificationProcessor_Factory.create(DaggerDesktopGlobalRootComponent.this.provideLauncherAppsProvider, this.conversationNotificationManagerProvider);
            this.mediaFeatureFlagProvider = MediaFeatureFlag_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.smartReplyConstantsProvider = DoubleCheck.provider(SmartReplyConstants_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.deviceConfigProxyProvider));
            this.provideActivityManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideActivityManagerWrapperFactory.create(dependencyProvider));
            this.provideDevicePolicyManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideDevicePolicyManagerWrapperFactory.create(dependencyProvider));
            Provider<KeyguardDismissUtil> provider17 = DoubleCheck.provider(KeyguardDismissUtil_Factory.create());
            this.keyguardDismissUtilProvider = provider17;
            this.smartReplyInflaterImplProvider = SmartReplyInflaterImpl_Factory.create(this.smartReplyConstantsProvider, provider17, this.provideNotificationRemoteInputManagerProvider, this.provideSmartReplyControllerProvider, DaggerDesktopGlobalRootComponent.this.contextProvider);
            Provider<Optional<Lazy<StatusBar>>> access$2800 = PresentJdkOptionalLazyProvider.m103of(this.provideStatusBarProvider);
            this.optionalOfLazyOfStatusBarProvider = access$2800;
            this.activityStarterDelegateProvider = DoubleCheck.provider(ActivityStarterDelegate_Factory.create(access$2800));
            this.keyguardBypassControllerProvider = DoubleCheck.provider(KeyguardBypassController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.tunerServiceImplProvider, this.desktopStatusBarStateControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.keyguardStateControllerImplProvider, this.dumpManagerProvider));
            Provider<HeadsUpManagerPhone> provider18 = DoubleCheck.provider(SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.desktopStatusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.provideGroupMembershipManagerProvider, this.provideConfigurationControllerProvider));
            this.provideHeadsUpManagerPhoneProvider = provider18;
            this.smartActionInflaterImplProvider = SmartActionInflaterImpl_Factory.create(this.smartReplyConstantsProvider, this.activityStarterDelegateProvider, this.provideSmartReplyControllerProvider, provider18);
        }

        private void initialize2(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            DependencyProvider dependencyProvider3 = dependencyProvider;
            SmartReplyStateInflaterImpl_Factory create = SmartReplyStateInflaterImpl_Factory.create(this.smartReplyConstantsProvider, this.provideActivityManagerWrapperProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerWrapperProvider, this.provideDevicePolicyManagerWrapperProvider, this.smartReplyInflaterImplProvider, this.smartActionInflaterImplProvider);
            this.smartReplyStateInflaterImplProvider = create;
            this.notificationContentInflaterProvider = DoubleCheck.provider(NotificationContentInflater_Factory.create(this.provideNotifRemoteViewCacheProvider, this.provideNotificationRemoteInputManagerProvider, this.conversationNotificationProcessorProvider, this.mediaFeatureFlagProvider, this.provideBackgroundExecutorProvider, create));
            this.notifInflationErrorManagerProvider = DoubleCheck.provider(NotifInflationErrorManager_Factory.create());
            RowContentBindStageLogger_Factory create2 = RowContentBindStageLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.rowContentBindStageLoggerProvider = create2;
            this.rowContentBindStageProvider = DoubleCheck.provider(RowContentBindStage_Factory.create(this.notificationContentInflaterProvider, this.notifInflationErrorManagerProvider, create2));
            this.expandableNotificationRowComponentBuilderProvider = new Provider<ExpandableNotificationRowComponent.Builder>() {
                public ExpandableNotificationRowComponent.Builder get() {
                    return new ExpandableNotificationRowComponentBuilder();
                }
            };
            this.iconBuilderProvider = IconBuilder_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.iconManagerProvider = IconManager_Factory.create(this.provideCommonNotifCollectionProvider, DaggerDesktopGlobalRootComponent.this.provideLauncherAppsProvider, this.iconBuilderProvider);
            this.lowPriorityInflationHelperProvider = DoubleCheck.provider(LowPriorityInflationHelper_Factory.create(this.featureFlagsProvider, this.notificationGroupManagerLegacyProvider, this.rowContentBindStageProvider));
            this.notificationRowBinderImplProvider = DoubleCheck.provider(NotificationRowBinderImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideNotificationMessagingUtilProvider, this.provideNotificationRemoteInputManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.notifBindPipelineProvider, this.rowContentBindStageProvider, RowInflaterTask_Factory.create(), this.expandableNotificationRowComponentBuilderProvider, this.iconManagerProvider, this.lowPriorityInflationHelperProvider));
            Provider<ForegroundServiceDismissalFeatureController> provider = DoubleCheck.provider(ForegroundServiceDismissalFeatureController_Factory.create(this.deviceConfigProxyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.foregroundServiceDismissalFeatureControllerProvider = provider;
            DelegateFactory.setDelegate(this.provideNotificationEntryManagerProvider, DoubleCheck.provider(NotificationsModule_ProvideNotificationEntryManagerFactory.create(this.notificationEntryManagerLoggerProvider, this.notificationGroupManagerLegacyProvider, this.featureFlagsProvider, this.notificationRowBinderImplProvider, this.provideNotificationRemoteInputManagerProvider, this.provideLeakDetectorProvider, provider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider)));
            this.provideAmbientDisplayConfigurationProvider = DependencyProvider_ProvideAmbientDisplayConfigurationFactory.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.provideAlwaysOnDisplayPolicyProvider = DoubleCheck.provider(DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.wakefulnessLifecycleProvider = DoubleCheck.provider(WakefulnessLifecycle_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, FrameworkServicesModule_ProvideIWallPaperManagerFactory.create()));
            this.statusBarStateControllerImplProvider = DoubleCheck.provider(StatusBarStateControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
            this.falsingManagerProxyProvider = new DelegateFactory();
            this.keyguardUpdateMonitorProvider = new DelegateFactory();
            this.asyncSensorManagerProvider = DoubleCheck.provider(AsyncSensorManager_Factory.create(DaggerDesktopGlobalRootComponent.this.providesSensorManagerProvider, ThreadFactoryImpl_Factory.create(), this.providePluginManagerProvider));
            this.builderProvider = ThresholdSensorImpl_Builder_Factory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.asyncSensorManagerProvider, DaggerDesktopGlobalRootComponent.this.provideExecutionProvider);
            this.providePrimaryProxSensorProvider = SensorModule_ProvidePrimaryProxSensorFactory.create(DaggerDesktopGlobalRootComponent.this.providesSensorManagerProvider, this.builderProvider);
            SensorModule_ProvideSecondaryProxSensorFactory create3 = SensorModule_ProvideSecondaryProxSensorFactory.create(this.builderProvider);
            this.provideSecondaryProxSensorProvider = create3;
            ProximitySensor_Factory create4 = ProximitySensor_Factory.create(this.providePrimaryProxSensorProvider, create3, this.provideMainDelayableExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideExecutionProvider);
            this.proximitySensorProvider = create4;
            this.falsingCollectorImplProvider = DoubleCheck.provider(FalsingCollectorImpl_Factory.create(this.falsingDataProvider, this.falsingManagerProxyProvider, this.keyguardUpdateMonitorProvider, this.historyTrackerProvider, create4, this.desktopStatusBarStateControllerImplProvider, this.keyguardStateControllerImplProvider, this.provideBatteryControllerProvider, this.dockManagerImplProvider, this.provideMainDelayableExecutorProvider, this.bindSystemClockProvider));
            this.provideStatusBarKeyguardViewManagerProvider = DoubleCheck.provider(C2759xaf8d20c6.create());
            Provider<Executor> provider2 = DoubleCheck.provider(SysUIConcurrencyModule_ProvideUiBackgroundExecutorFactory.create());
            this.provideUiBackgroundExecutorProvider = provider2;
            this.dismissCallbackRegistryProvider = DoubleCheck.provider(DismissCallbackRegistry_Factory.create(provider2));
            this.telephonyListenerManagerProvider = DoubleCheck.provider(TelephonyListenerManager_Factory.create(DaggerDesktopGlobalRootComponent.this.provideTelephonyManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, TelephonyCallback_Factory.create()));
            this.userSwitcherControllerProvider = new DelegateFactory();
            this.adapterProvider = UserDetailView_Adapter_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.userSwitcherControllerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.falsingManagerProxyProvider);
            this.userDetailAdapterProvider = UserSwitcherController_UserDetailAdapter_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.adapterProvider);
            Provider<UserSwitcherController> provider3 = this.userSwitcherControllerProvider;
            Provider<UserSwitcherController> provider4 = provider3;
            DelegateFactory.setDelegate(provider4, DoubleCheck.provider(UserSwitcherController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, this.provideUserTrackerProvider, this.keyguardStateControllerImplProvider, this.deviceProvisionedControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideDevicePolicyManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.activityStarterDelegateProvider, this.providesBroadcastDispatcherProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.falsingManagerProxyProvider, this.telephonyListenerManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIActivityTaskManagerProvider, this.userDetailAdapterProvider, this.secureSettingsImplProvider, this.provideBackgroundExecutorProvider)));
            this.navigationModeControllerProvider = DoubleCheck.provider(NavigationModeController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.deviceProvisionedControllerImplProvider, this.provideConfigurationControllerProvider, this.provideUiBackgroundExecutorProvider));
            this.provideAssistUtilsProvider = DoubleCheck.provider(AssistModule_ProvideAssistUtilsFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.phoneStateMonitorProvider = DoubleCheck.provider(PhoneStateMonitor_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider, this.bootCompleteCacheImplProvider));
            this.provideNavigationBarControllerProvider = new DelegateFactory();
            this.notificationShadeWindowControllerImplProvider = new DelegateFactory();
            this.provideSysUiStateProvider = DoubleCheck.provider(DesktopSystemUIModule_ProvideSysUiStateFactory.create());
            this.setPipProvider = InstanceFactory.create(optional);
            this.setLegacySplitScreenProvider = InstanceFactory.create(optional2);
            this.setSplitScreenProvider = InstanceFactory.create(optional3);
            this.setOneHandedProvider = InstanceFactory.create(optional5);
            this.setTransitionsProvider = InstanceFactory.create(shellTransitions);
            this.setStartingSurfaceProvider = InstanceFactory.create(optional10);
            this.provideSmartspaceTransitionControllerProvider = DoubleCheck.provider(C2760xc242d32f.create());
            this.overviewProxyServiceProvider = DoubleCheck.provider(OverviewProxyService_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.provideNavigationBarControllerProvider, this.navigationModeControllerProvider, this.notificationShadeWindowControllerImplProvider, this.provideSysUiStateProvider, this.setPipProvider, this.setLegacySplitScreenProvider, this.setSplitScreenProvider, this.optionalOfLazyOfStatusBarProvider, this.setOneHandedProvider, this.providesBroadcastDispatcherProvider, this.setTransitionsProvider, this.setStartingSurfaceProvider, this.provideSmartspaceTransitionControllerProvider));
            this.assistLoggerProvider = DoubleCheck.provider(AssistLogger_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.provideAssistUtilsProvider, this.phoneStateMonitorProvider));
            this.defaultUiControllerProvider = DoubleCheck.provider(DefaultUiController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.assistLoggerProvider));
            this.assistManagerProvider = DoubleCheck.provider(AssistManager_Factory.create(this.deviceProvisionedControllerImplProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideAssistUtilsProvider, this.provideCommandQueueProvider, this.phoneStateMonitorProvider, this.overviewProxyServiceProvider, this.provideConfigurationControllerProvider, this.provideSysUiStateProvider, this.defaultUiControllerProvider, this.assistLoggerProvider));
            this.accessibilityManagerWrapperProvider = DoubleCheck.provider(AccessibilityManagerWrapper_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.accessibilityButtonModeObserverProvider = DoubleCheck.provider(AccessibilityButtonModeObserver_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.contextComponentResolverProvider = new DelegateFactory();
            this.provideRecentsImplProvider = RecentsModule_ProvideRecentsImplFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.contextComponentResolverProvider);
            Provider<Recents> provider5 = DoubleCheck.provider(SystemUIDefaultModule_ProvideRecentsFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideRecentsImplProvider, this.provideCommandQueueProvider));
            this.provideRecentsProvider = provider5;
            this.optionalOfRecentsProvider = PresentJdkOptionalInstanceProvider.m102of(provider5);
            this.shadeControllerImplProvider = DoubleCheck.provider(ShadeControllerImpl_Factory.create(this.provideCommandQueueProvider, this.desktopStatusBarStateControllerImplProvider, this.notificationShadeWindowControllerImplProvider, this.provideStatusBarKeyguardViewManagerProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.provideStatusBarProvider, this.assistManagerProvider, this.setBubblesProvider));
            this.blurUtilsProvider = DoubleCheck.provider(BlurUtils_Factory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, DaggerDesktopGlobalRootComponent.this.provideCrossWindowBlurListenersProvider, this.dumpManagerProvider));
            this.dozeParametersProvider = new DelegateFactory();
            Provider<LogBuffer> provider6 = DoubleCheck.provider(LogModule_ProvideDozeLogBufferFactory.create(this.logBufferFactoryProvider));
            this.provideDozeLogBufferProvider = provider6;
            DozeLogger_Factory create5 = DozeLogger_Factory.create(provider6);
            this.dozeLoggerProvider = create5;
            Provider<DozeLog> provider7 = DoubleCheck.provider(DozeLog_Factory.create(this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, create5));
            this.dozeLogProvider = provider7;
            this.dozeScrimControllerProvider = DoubleCheck.provider(DozeScrimController_Factory.create(this.dozeParametersProvider, provider7));
            this.newKeyguardViewMediatorProvider = new DelegateFactory();
            this.darkIconDispatcherImplProvider = DoubleCheck.provider(DarkIconDispatcherImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider));
            this.lightBarControllerProvider = DoubleCheck.provider(LightBarController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.darkIconDispatcherImplProvider, this.provideBatteryControllerProvider, this.navigationModeControllerProvider));
            this.builderProvider2 = DelayedWakeLock_Builder_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.unlockedScreenOffAnimationControllerProvider = new DelegateFactory();
            this.scrimControllerProvider = DoubleCheck.provider(ScrimController_Factory.create(this.lightBarControllerProvider, this.dozeParametersProvider, DaggerDesktopGlobalRootComponent.this.provideAlarmManagerProvider, this.keyguardStateControllerImplProvider, this.builderProvider2, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, this.dockManagerImplProvider, this.provideConfigurationControllerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.unlockedScreenOffAnimationControllerProvider));
            this.provideNotificationMediaManagerProvider = new DelegateFactory();
            this.screenLifecycleProvider = DoubleCheck.provider(ScreenLifecycle_Factory.create());
            this.authControllerProvider = new DelegateFactory();
            this.biometricUnlockControllerProvider = DoubleCheck.provider(BiometricUnlockController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dozeScrimControllerProvider, this.newKeyguardViewMediatorProvider, this.scrimControllerProvider, this.shadeControllerImplProvider, this.notificationShadeWindowControllerImplProvider, this.keyguardStateControllerImplProvider, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider, this.provideMetricsLoggerProvider, this.dumpManagerProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, this.provideNotificationMediaManagerProvider, this.wakefulnessLifecycleProvider, this.screenLifecycleProvider, this.authControllerProvider));
            Provider<Choreographer> provider8 = DoubleCheck.provider(DependencyProvider_ProvidesChoreographerFactory.create(dependencyProvider));
            this.providesChoreographerProvider = provider8;
            this.notificationShadeDepthControllerProvider = DoubleCheck.provider(NotificationShadeDepthController_Factory.create(this.desktopStatusBarStateControllerImplProvider, this.blurUtilsProvider, this.biometricUnlockControllerProvider, this.keyguardStateControllerImplProvider, provider8, DaggerDesktopGlobalRootComponent.this.provideWallpaperManagerProvider, this.notificationShadeWindowControllerImplProvider, this.dozeParametersProvider, this.dumpManagerProvider));
            this.systemActionsProvider = DoubleCheck.provider(SystemActions_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.notificationShadeWindowControllerImplProvider, this.provideStatusBarProvider, this.provideRecentsProvider));
            this.navigationBarOverlayControllerProvider = DoubleCheck.provider(NavigationBarOverlayController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            Provider<NavigationBarController> provider9 = this.provideNavigationBarControllerProvider;
            Provider<NavigationBarController> provider10 = provider9;
            DelegateFactory.setDelegate(provider10, DoubleCheck.provider(DependencyProvider_ProvideNavigationBarControllerFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.assistManagerProvider, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, this.accessibilityManagerWrapperProvider, this.deviceProvisionedControllerImplProvider, this.provideMetricsLoggerProvider, this.overviewProxyServiceProvider, this.navigationModeControllerProvider, this.accessibilityButtonModeObserverProvider, this.desktopStatusBarStateControllerImplProvider, this.provideSysUiStateProvider, this.providesBroadcastDispatcherProvider, this.provideCommandQueueProvider, this.setPipProvider, this.setLegacySplitScreenProvider, this.optionalOfRecentsProvider, this.provideStatusBarProvider, this.shadeControllerImplProvider, this.provideNotificationRemoteInputManagerProvider, this.notificationShadeDepthControllerProvider, this.systemActionsProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.navigationBarOverlayControllerProvider, this.provideConfigurationControllerProvider, this.provideUserTrackerProvider)));
            this.keyguardStatusViewComponentFactoryProvider = new Provider<KeyguardStatusViewComponent.Factory>() {
                public KeyguardStatusViewComponent.Factory get() {
                    return new KeyguardStatusViewComponentFactory();
                }
            };
            this.keyguardDisplayManagerProvider = KeyguardDisplayManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideNavigationBarControllerProvider, this.keyguardStatusViewComponentFactoryProvider, this.provideCommandQueueProvider, this.provideUiBackgroundExecutorProvider);
            this.keyguardUnlockAnimationControllerProvider = DoubleCheck.provider(KeyguardUnlockAnimationController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.keyguardStateControllerImplProvider, this.newKeyguardViewMediatorProvider, this.provideStatusBarKeyguardViewManagerProvider, this.provideSmartspaceTransitionControllerProvider, this.featureFlagsProvider));
            DelegateFactory.setDelegate(this.newKeyguardViewMediatorProvider, DoubleCheck.provider(KeyguardModule_NewKeyguardViewMediatorFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.falsingCollectorImplProvider, this.provideLockPatternUtilsProvider, this.providesBroadcastDispatcherProvider, this.provideStatusBarKeyguardViewManagerProvider, this.dismissCallbackRegistryProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, DaggerDesktopGlobalRootComponent.this.provideTrustManagerProvider, this.userSwitcherControllerProvider, this.provideUiBackgroundExecutorProvider, this.deviceConfigProxyProvider, this.navigationModeControllerProvider, this.keyguardDisplayManagerProvider, this.dozeParametersProvider, this.statusBarStateControllerImplProvider, this.keyguardStateControllerImplProvider, this.keyguardUnlockAnimationControllerProvider, this.unlockedScreenOffAnimationControllerProvider, this.notificationShadeDepthControllerProvider)));
            DelegateFactory.setDelegate(this.unlockedScreenOffAnimationControllerProvider, DoubleCheck.provider(UnlockedScreenOffAnimationController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerImplProvider, this.newKeyguardViewMediatorProvider, this.keyguardStateControllerImplProvider, this.dozeParametersProvider, this.provideAmbientDisplayConfigurationProvider)));
            DelegateFactory.setDelegate(this.dozeParametersProvider, DoubleCheck.provider(DozeParameters_Factory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.provideAmbientDisplayConfigurationProvider, this.provideAlwaysOnDisplayPolicyProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, this.provideBatteryControllerProvider, this.tunerServiceImplProvider, this.dumpManagerProvider, this.featureFlagsProvider, this.unlockedScreenOffAnimationControllerProvider)));
            this.sysuiColorExtractorProvider = DoubleCheck.provider(SysuiColorExtractor_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideConfigurationControllerProvider));
            DelegateFactory.setDelegate(this.notificationShadeWindowControllerImplProvider, DoubleCheck.provider(NotificationShadeWindowControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIActivityManagerProvider, this.dozeParametersProvider, this.desktopStatusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.newKeyguardViewMediatorProvider, this.keyguardBypassControllerProvider, this.sysuiColorExtractorProvider, this.dumpManagerProvider, this.keyguardStateControllerImplProvider, this.unlockedScreenOffAnimationControllerProvider, this.authControllerProvider)));
            this.mediaArtworkProcessorProvider = DoubleCheck.provider(MediaArtworkProcessor_Factory.create());
            MediaControllerFactory_Factory create6 = MediaControllerFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.mediaControllerFactoryProvider = create6;
            this.mediaTimeoutListenerProvider = DoubleCheck.provider(MediaTimeoutListener_Factory.create(create6, this.provideMainDelayableExecutorProvider));
            this.mediaBrowserFactoryProvider = MediaBrowserFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.resumeMediaBrowserFactoryProvider = ResumeMediaBrowserFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.mediaBrowserFactoryProvider);
            this.mediaResumeListenerProvider = DoubleCheck.provider(MediaResumeListener_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, this.provideBackgroundExecutorProvider, this.tunerServiceImplProvider, this.resumeMediaBrowserFactoryProvider, this.dumpManagerProvider));
            this.mediaSessionBasedFilterProvider = MediaSessionBasedFilter_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMediaSessionManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider);
            this.provideLocalBluetoothControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideLocalBluetoothControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgHandlerProvider));
            LocalMediaManagerFactory_Factory create7 = LocalMediaManagerFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideLocalBluetoothControllerProvider);
            this.localMediaManagerFactoryProvider = create7;
            this.mediaDeviceManagerProvider = MediaDeviceManager_Factory.create(this.mediaControllerFactoryProvider, create7, DaggerDesktopGlobalRootComponent.this.provideMediaRouter2ManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.dumpManagerProvider);
            this.mediaDataFilterProvider = MediaDataFilter_Factory.create(this.providesBroadcastDispatcherProvider, this.mediaResumeListenerProvider, this.notificationLockscreenUserManagerImplProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.bindSystemClockProvider);
            this.mediaDataManagerProvider = DoubleCheck.provider(MediaDataManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBackgroundExecutorProvider, this.provideMainDelayableExecutorProvider, this.mediaControllerFactoryProvider, this.dumpManagerProvider, this.providesBroadcastDispatcherProvider, this.mediaTimeoutListenerProvider, this.mediaResumeListenerProvider, this.mediaSessionBasedFilterProvider, this.mediaDeviceManagerProvider, MediaDataCombineLatest_Factory.create(), this.mediaDataFilterProvider, this.activityStarterDelegateProvider, SmartspaceMediaDataProvider_Factory.create(), this.bindSystemClockProvider, this.tunerServiceImplProvider));
            DelegateFactory.setDelegate(this.provideNotificationMediaManagerProvider, DoubleCheck.provider(C1496x30c882de.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideStatusBarProvider, this.notificationShadeWindowControllerImplProvider, this.provideNotificationEntryManagerProvider, this.mediaArtworkProcessorProvider, this.keyguardBypassControllerProvider, this.notifPipelineProvider, this.notifCollectionProvider, this.featureFlagsProvider, this.provideMainDelayableExecutorProvider, this.deviceConfigProxyProvider, this.mediaDataManagerProvider)));
            this.keyguardEnvironmentImplProvider = DoubleCheck.provider(KeyguardEnvironmentImpl_Factory.create());
            this.provideIndividualSensorPrivacyControllerProvider = DoubleCheck.provider(C0914x217e5105.create(DaggerDesktopGlobalRootComponent.this.provideSensorPrivacyManagerProvider));
        }

        private void initialize3(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            Provider<AppOpsControllerImpl> provider = DoubleCheck.provider(AppOpsControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgLooperProvider, this.dumpManagerProvider, DaggerDesktopGlobalRootComponent.this.provideAudioManagerProvider, this.provideIndividualSensorPrivacyControllerProvider, this.providesBroadcastDispatcherProvider, this.bindSystemClockProvider));
            this.appOpsControllerImplProvider = provider;
            Provider<ForegroundServiceController> provider2 = DoubleCheck.provider(ForegroundServiceController_Factory.create(provider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            this.foregroundServiceControllerProvider = provider2;
            this.notificationFilterProvider = DoubleCheck.provider(NotificationFilter_Factory.create(this.desktopStatusBarStateControllerImplProvider, this.keyguardEnvironmentImplProvider, provider2, this.notificationLockscreenUserManagerImplProvider, this.mediaFeatureFlagProvider));
            this.notificationSectionsFeatureManagerProvider = NotificationSectionsFeatureManager_Factory.create(this.deviceConfigProxyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider);
            Provider<HighPriorityProvider> provider3 = DoubleCheck.provider(HighPriorityProvider_Factory.create(this.peopleNotificationIdentifierImplProvider, this.provideGroupMembershipManagerProvider));
            this.highPriorityProvider = provider3;
            this.notificationRankingManagerProvider = NotificationRankingManager_Factory.create(this.provideNotificationMediaManagerProvider, this.notificationGroupManagerLegacyProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationFilterProvider, this.notificationEntryManagerLoggerProvider, this.notificationSectionsFeatureManagerProvider, this.peopleNotificationIdentifierImplProvider, provider3, this.keyguardEnvironmentImplProvider);
            this.targetSdkResolverProvider = DoubleCheck.provider(TargetSdkResolver_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            GroupCoalescerLogger_Factory create = GroupCoalescerLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.groupCoalescerLoggerProvider = create;
            this.groupCoalescerProvider = GroupCoalescer_Factory.create(this.provideMainDelayableExecutorProvider, this.bindSystemClockProvider, create);
            SharedCoordinatorLogger_Factory create2 = SharedCoordinatorLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.sharedCoordinatorLoggerProvider = create2;
            this.hideNotifsForOtherUsersCoordinatorProvider = HideNotifsForOtherUsersCoordinator_Factory.create(this.notificationLockscreenUserManagerImplProvider, create2);
            this.keyguardCoordinatorProvider = DoubleCheck.provider(KeyguardCoordinator_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideHandlerProvider, this.keyguardStateControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.providesBroadcastDispatcherProvider, this.desktopStatusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.highPriorityProvider));
            C27533 r1 = new Provider<SectionHeaderControllerSubcomponent.Builder>() {
                public SectionHeaderControllerSubcomponent.Builder get() {
                    return new SectionHeaderControllerSubcomponentBuilder();
                }
            };
            this.sectionHeaderControllerSubcomponentBuilderProvider = r1;
            Provider<SectionHeaderControllerSubcomponent> provider4 = DoubleCheck.provider(C1578x3fd4641.create(r1));
            this.providesAlertingHeaderSubcomponentProvider = provider4;
            this.providesAlertingHeaderNodeControllerProvider = C1577x30119a0.create(provider4);
            Provider<SectionHeaderControllerSubcomponent> provider5 = DoubleCheck.provider(C1587x34a20792.create(this.sectionHeaderControllerSubcomponentBuilderProvider));
            this.providesSilentHeaderSubcomponentProvider = provider5;
            C1586x9d7acab1 create3 = C1586x9d7acab1.create(provider5);
            this.providesSilentHeaderNodeControllerProvider = create3;
            this.rankingCoordinatorProvider = DoubleCheck.provider(RankingCoordinator_Factory.create(this.desktopStatusBarStateControllerImplProvider, this.highPriorityProvider, this.providesAlertingHeaderNodeControllerProvider, create3));
            this.appOpsCoordinatorProvider = DoubleCheck.provider(AppOpsCoordinator_Factory.create(this.foregroundServiceControllerProvider, this.appOpsControllerImplProvider, this.provideMainDelayableExecutorProvider));
            this.deviceProvisionedCoordinatorProvider = DoubleCheck.provider(DeviceProvisionedCoordinator_Factory.create(this.deviceProvisionedControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideIPackageManagerProvider));
            this.provideINotificationManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideINotificationManagerFactory.create(dependencyProvider));
            this.notificationInterruptStateProviderImplProvider = DoubleCheck.provider(NotificationInterruptStateProviderImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideContentResolverProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIDreamManagerProvider, this.provideAmbientDisplayConfigurationProvider, this.notificationFilterProvider, this.provideBatteryControllerProvider, this.desktopStatusBarStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            this.provideNotificationInterruptStateProvider = DoubleCheck.provider(C1588xb8672cea.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.notificationInterruptStateProviderImplProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIDreamManagerProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationFilterProvider));
            this.zenModeControllerImplProvider = DoubleCheck.provider(ZenModeControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.providesBroadcastDispatcherProvider));
            Provider<Optional<BubblesManager>> provider6 = DoubleCheck.provider(DesktopSystemUIModule_ProvideBubblesManagerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.setBubblesProvider, this.notificationShadeWindowControllerImplProvider, this.desktopStatusBarStateControllerImplProvider, this.shadeControllerImplProvider, this.provideConfigurationControllerProvider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, this.provideINotificationManagerProvider, this.provideNotificationInterruptStateProvider, this.zenModeControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerLegacyProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider, this.provideSysUiStateProvider, this.featureFlagsProvider, this.dumpManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider));
            this.provideBubblesManagerProvider = provider6;
            this.bubbleCoordinatorProvider = DoubleCheck.provider(BubbleCoordinator_Factory.create(provider6, this.setBubblesProvider, this.notifCollectionProvider));
            this.headsUpViewBinderProvider = DoubleCheck.provider(HeadsUpViewBinder_Factory.create(this.provideNotificationMessagingUtilProvider, this.rowContentBindStageProvider));
            Provider<SectionHeaderControllerSubcomponent> provider7 = DoubleCheck.provider(C1581xb614d321.create(this.sectionHeaderControllerSubcomponentBuilderProvider));
            this.providesIncomingHeaderSubcomponentProvider = provider7;
            C1580x8d68ee80 create4 = C1580x8d68ee80.create(provider7);
            this.providesIncomingHeaderNodeControllerProvider = create4;
            this.headsUpCoordinatorProvider = DoubleCheck.provider(HeadsUpCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.headsUpViewBinderProvider, this.provideNotificationInterruptStateProvider, this.provideNotificationRemoteInputManagerProvider, create4));
            Provider<SectionHeaderControllerSubcomponent> provider8 = DoubleCheck.provider(C1584x39c1fe98.create(this.sectionHeaderControllerSubcomponentBuilderProvider));
            this.providesPeopleHeaderSubcomponentProvider = provider8;
            C1583xda791837 create5 = C1583xda791837.create(provider8);
            this.providesPeopleHeaderNodeControllerProvider = create5;
            this.conversationCoordinatorProvider = DoubleCheck.provider(ConversationCoordinator_Factory.create(this.peopleNotificationIdentifierImplProvider, create5));
            this.preparationCoordinatorLoggerProvider = PreparationCoordinatorLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.notifInflaterImplProvider = DoubleCheck.provider(NotifInflaterImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, this.notifCollectionProvider, this.notifInflationErrorManagerProvider, this.notifPipelineProvider));
            Provider<NotifViewBarn> provider9 = DoubleCheck.provider(NotifViewBarn_Factory.create());
            this.notifViewBarnProvider = provider9;
            this.preparationCoordinatorProvider = DoubleCheck.provider(PreparationCoordinator_Factory.create(this.preparationCoordinatorLoggerProvider, this.notifInflaterImplProvider, this.notifInflationErrorManagerProvider, provider9, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider));
            this.mediaCoordinatorProvider = MediaCoordinator_Factory.create(this.mediaFeatureFlagProvider);
            this.optionalOfBcSmartspaceDataPluginProvider = DaggerDesktopGlobalRootComponent.absentJdkOptionalProvider();
            Provider<LockscreenSmartspaceController> provider10 = DoubleCheck.provider(LockscreenSmartspaceController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.featureFlagsProvider, DaggerDesktopGlobalRootComponent.this.provideSmartspaceManagerProvider, this.activityStarterDelegateProvider, this.falsingManagerProxyProvider, this.secureSettingsImplProvider, this.provideUserTrackerProvider, DaggerDesktopGlobalRootComponent.this.provideContentResolverProvider, this.provideConfigurationControllerProvider, this.desktopStatusBarStateControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideExecutionProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.optionalOfBcSmartspaceDataPluginProvider));
            this.lockscreenSmartspaceControllerProvider = provider10;
            this.smartspaceDedupingCoordinatorProvider = DoubleCheck.provider(SmartspaceDedupingCoordinator_Factory.create(this.statusBarStateControllerImplProvider, provider10, this.provideNotificationEntryManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.notifPipelineProvider, this.provideMainDelayableExecutorProvider, this.bindSystemClockProvider));
            Provider<DelayableExecutor> provider11 = DoubleCheck.provider(SysUIConcurrencyModule_ProvideDelayableExecutorFactory.create(this.provideBgLooperProvider));
            this.provideDelayableExecutorProvider = provider11;
            Provider<VisualStabilityCoordinator> provider12 = DoubleCheck.provider(VisualStabilityCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.wakefulnessLifecycleProvider, this.desktopStatusBarStateControllerImplProvider, provider11));
            this.visualStabilityCoordinatorProvider = provider12;
            this.notifCoordinatorsProvider = DoubleCheck.provider(NotifCoordinators_Factory.create(this.dumpManagerProvider, this.featureFlagsProvider, this.hideNotifsForOtherUsersCoordinatorProvider, this.keyguardCoordinatorProvider, this.rankingCoordinatorProvider, this.appOpsCoordinatorProvider, this.deviceProvisionedCoordinatorProvider, this.bubbleCoordinatorProvider, this.headsUpCoordinatorProvider, this.conversationCoordinatorProvider, this.preparationCoordinatorProvider, this.mediaCoordinatorProvider, this.smartspaceDedupingCoordinatorProvider, provider12));
            this.shadeViewDifferLoggerProvider = ShadeViewDifferLogger_Factory.create(this.provideNotificationsLogBufferProvider);
            this.notificationWakeUpCoordinatorProvider = DoubleCheck.provider(NotificationWakeUpCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.desktopStatusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider, this.unlockedScreenOffAnimationControllerProvider));
            C27544 r12 = new Provider<InjectionInflationController.ViewInstanceCreator.Factory>() {
                public InjectionInflationController.ViewInstanceCreator.Factory get() {
                    return new ViewInstanceCreatorFactory();
                }
            };
            this.createViewInstanceCreatorFactoryProvider = r12;
            this.injectionInflationControllerProvider = DoubleCheck.provider(InjectionInflationController_Factory.create(r12));
            this.notificationShelfComponentBuilderProvider = new Provider<NotificationShelfComponent.Builder>() {
                public NotificationShelfComponent.Builder get() {
                    return new NotificationShelfComponentBuilder();
                }
            };
            this.superStatusBarViewFactoryProvider = DoubleCheck.provider(SuperStatusBarViewFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.injectionInflationControllerProvider, this.notificationShelfComponentBuilderProvider));
            this.statusBarContentInsetsProvider = DoubleCheck.provider(StatusBarContentInsetsProvider_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideConfigurationControllerProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.dumpManagerProvider));
            this.statusBarWindowControllerProvider = DoubleCheck.provider(StatusBarWindowController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.superStatusBarViewFactoryProvider, this.statusBarContentInsetsProvider, DaggerDesktopGlobalRootComponent.this.provideResourcesProvider));
            this.notificationIconAreaControllerProvider = DoubleCheck.provider(NotificationIconAreaController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.desktopStatusBarStateControllerImplProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.provideNotificationMediaManagerProvider, this.provideNotificationListenerProvider, this.dozeParametersProvider, this.setBubblesProvider, this.provideDemoModeControllerProvider, this.darkIconDispatcherImplProvider, this.statusBarWindowControllerProvider, this.unlockedScreenOffAnimationControllerProvider));
            ShadeViewManagerFactory_Factory create6 = ShadeViewManagerFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.shadeViewDifferLoggerProvider, this.notifViewBarnProvider, this.notificationIconAreaControllerProvider);
            this.shadeViewManagerFactoryProvider = create6;
            this.notifPipelineInitializerProvider = DoubleCheck.provider(NotifPipelineInitializer_Factory.create(this.notifPipelineProvider, this.groupCoalescerProvider, this.notifCollectionProvider, this.shadeListBuilderProvider, this.notifCoordinatorsProvider, this.notifInflaterImplProvider, this.dumpManagerProvider, create6, this.featureFlagsProvider));
            this.notifBindPipelineInitializerProvider = NotifBindPipelineInitializer_Factory.create(this.notifBindPipelineProvider, this.rowContentBindStageProvider);
            this.provideNotificationGroupAlertTransferHelperProvider = DoubleCheck.provider(C1981x3053f5c5.create(this.rowContentBindStageProvider));
            this.provideVisualStabilityManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideVisualStabilityManagerFactory.create(this.featureFlagsProvider, this.provideNotificationEntryManagerProvider, this.provideHandlerProvider, this.desktopStatusBarStateControllerImplProvider, this.wakefulnessLifecycleProvider));
            this.headsUpControllerProvider = DoubleCheck.provider(HeadsUpController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.headsUpViewBinderProvider, this.provideNotificationInterruptStateProvider, this.provideHeadsUpManagerPhoneProvider, this.provideNotificationRemoteInputManagerProvider, this.desktopStatusBarStateControllerImplProvider, this.provideVisualStabilityManagerProvider, this.provideNotificationListenerProvider));
            NotificationClickerLogger_Factory create7 = NotificationClickerLogger_Factory.create(this.provideNotifInteractionLogBufferProvider);
            this.notificationClickerLoggerProvider = create7;
            this.builderProvider3 = NotificationClicker_Builder_Factory.create(create7);
            this.animatedImageNotificationManagerProvider = DoubleCheck.provider(AnimatedImageNotificationManager_Factory.create(this.provideNotificationEntryManagerProvider, this.provideHeadsUpManagerPhoneProvider, this.desktopStatusBarStateControllerImplProvider));
            Provider<PeopleSpaceWidgetManager> provider13 = DoubleCheck.provider(PeopleSpaceWidgetManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideLauncherAppsProvider, this.provideNotificationEntryManagerProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider, this.setBubblesProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DaggerDesktopGlobalRootComponent.this.provideNotificationManagerProvider, this.providesBroadcastDispatcherProvider, this.provideBackgroundExecutorProvider));
            this.peopleSpaceWidgetManagerProvider = provider13;
            this.notificationsControllerImplProvider = DoubleCheck.provider(NotificationsControllerImpl_Factory.create(this.featureFlagsProvider, this.provideNotificationListenerProvider, this.provideNotificationEntryManagerProvider, this.notificationRankingManagerProvider, this.notifPipelineProvider, this.targetSdkResolverProvider, this.notifPipelineInitializerProvider, this.notifBindPipelineInitializerProvider, this.deviceProvisionedControllerImplProvider, this.notificationRowBinderImplProvider, this.remoteInputUriControllerProvider, this.notificationGroupManagerLegacyProvider, this.provideNotificationGroupAlertTransferHelperProvider, this.provideHeadsUpManagerPhoneProvider, this.headsUpControllerProvider, this.headsUpViewBinderProvider, this.builderProvider3, this.animatedImageNotificationManagerProvider, provider13));
            this.notificationsControllerStubProvider = NotificationsControllerStub_Factory.create(this.provideNotificationListenerProvider);
            this.provideNotificationsControllerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationsControllerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.notificationsControllerImplProvider, this.notificationsControllerStubProvider));
            this.desktopStatusBarComponentBuilderProvider = new Provider<DesktopStatusBarComponent.Builder>() {
                public DesktopStatusBarComponent.Builder get() {
                    return new DesktopStatusBarComponentBuilder();
                }
            };
            this.provideOnUserInteractionCallbackProvider = DoubleCheck.provider(NotificationsModule_ProvideOnUserInteractionCallbackFactory.create(this.featureFlagsProvider, this.provideHeadsUpManagerPhoneProvider, this.desktopStatusBarStateControllerImplProvider, this.notifPipelineProvider, this.notifCollectionProvider, this.visualStabilityCoordinatorProvider, this.provideNotificationEntryManagerProvider, this.provideVisualStabilityManagerProvider, this.provideGroupMembershipManagerProvider));
            this.statusBarNotificationActivityStarterLoggerProvider = StatusBarNotificationActivityStarterLogger_Factory.create(this.provideNotifInteractionLogBufferProvider);
            this.desktopNotificationActivityStarterProvider = DoubleCheck.provider(DesktopNotificationActivityStarter_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideHandlerProvider, this.featureFlagsProvider, this.provideLockPatternUtilsProvider, this.provideNotificationEntryManagerProvider, this.notificationClickNotifierProvider, DaggerDesktopGlobalRootComponent.this.provideKeyguardManagerProvider, this.notifPipelineProvider, this.provideNotificationRemoteInputManagerProvider, this.provideOnUserInteractionCallbackProvider, this.statusBarNotificationActivityStarterLoggerProvider));
            this.dynamicPrivacyControllerProvider = DoubleCheck.provider(DynamicPrivacyController_Factory.create(this.notificationLockscreenUserManagerImplProvider, this.keyguardStateControllerImplProvider, this.desktopStatusBarStateControllerImplProvider));
            this.foregroundServiceSectionControllerProvider = DoubleCheck.provider(ForegroundServiceSectionController_Factory.create(this.provideNotificationEntryManagerProvider, this.foregroundServiceDismissalFeatureControllerProvider));
            this.dynamicChildBindControllerProvider = DynamicChildBindController_Factory.create(this.rowContentBindStageProvider);
            this.assistantFeedbackControllerProvider = DoubleCheck.provider(AssistantFeedbackController_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.deviceConfigProxyProvider));
            this.provideNotificationViewHierarchyManagerProvider = DoubleCheck.provider(C1498x3f8faa0a.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerLegacyProvider, this.provideVisualStabilityManagerProvider, this.desktopStatusBarStateControllerImplProvider, this.provideNotificationEntryManagerProvider, this.keyguardBypassControllerProvider, this.setBubblesProvider, this.dynamicPrivacyControllerProvider, this.foregroundServiceSectionControllerProvider, this.dynamicChildBindControllerProvider, this.lowPriorityInflationHelperProvider, this.assistantFeedbackControllerProvider));
            Provider<StatusBar> provider14 = this.provideStatusBarProvider;
            Provider<StatusBar> provider15 = provider14;
            DelegateFactory.setDelegate(provider15, DoubleCheck.provider(DesktopStatusBarPhoneModule_ProvideStatusBarFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideNotificationsControllerProvider, this.provideHeadsUpManagerPhoneProvider, this.provideNotificationRemoteInputManagerProvider, this.provideVisualStabilityManagerProvider, this.injectionInflationControllerProvider, this.desktopStatusBarComponentBuilderProvider, this.desktopNotificationActivityStarterProvider, this.superStatusBarViewFactoryProvider, this.notificationIconAreaControllerProvider, this.extensionControllerImplProvider, this.provideConfigurationControllerProvider, this.provideNotificationViewHierarchyManagerProvider, this.provideStatusBarKeyguardViewManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider)));
            this.lockscreenGestureLoggerProvider = DoubleCheck.provider(LockscreenGestureLogger_Factory.create());
            this.mediaHostStatesManagerProvider = DoubleCheck.provider(MediaHostStatesManager_Factory.create());
            this.mediaViewControllerProvider = MediaViewController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideConfigurationControllerProvider, this.mediaHostStatesManagerProvider);
            Provider<DelayableExecutor> provider16 = DoubleCheck.provider(SysUIConcurrencyModule_ProvideBackgroundDelayableExecutorFactory.create(this.provideBgLooperProvider));
            this.provideBackgroundDelayableExecutorProvider = provider16;
            Provider<RepeatableExecutor> provider17 = DoubleCheck.provider(C2117xb8fd9db4.create(provider16));
            this.provideBackgroundRepeatableExecutorProvider = provider17;
            this.seekBarViewModelProvider = SeekBarViewModel_Factory.create(provider17);
            this.mediaOutputDialogFactoryProvider = MediaOutputDialogFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMediaSessionManagerProvider, this.provideLocalBluetoothControllerProvider, this.shadeControllerImplProvider, this.activityStarterDelegateProvider, this.provideNotificationEntryManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider);
            this.mediaCarouselControllerProvider = new DelegateFactory();
            this.mediaControlPanelProvider = MediaControlPanel_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBackgroundExecutorProvider, this.activityStarterDelegateProvider, this.mediaViewControllerProvider, this.seekBarViewModelProvider, this.mediaDataManagerProvider, this.keyguardDismissUtilProvider, this.mediaOutputDialogFactoryProvider, this.mediaCarouselControllerProvider);
            DelegateFactory.setDelegate(this.mediaCarouselControllerProvider, DoubleCheck.provider(MediaCarouselController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.mediaControlPanelProvider, this.provideVisualStabilityManagerProvider, this.mediaHostStatesManagerProvider, this.activityStarterDelegateProvider, this.bindSystemClockProvider, this.provideMainDelayableExecutorProvider, this.mediaDataManagerProvider, this.provideConfigurationControllerProvider, this.falsingCollectorImplProvider, this.falsingManagerProxyProvider, this.dumpManagerProvider)));
            this.mediaHierarchyManagerProvider = DoubleCheck.provider(MediaHierarchyManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.statusBarStateControllerImplProvider, this.keyguardStateControllerImplProvider, this.keyguardBypassControllerProvider, this.mediaCarouselControllerProvider, this.notificationLockscreenUserManagerImplProvider, this.provideConfigurationControllerProvider, this.wakefulnessLifecycleProvider, this.provideStatusBarKeyguardViewManagerProvider));
            Provider<MediaHost> provider18 = DoubleCheck.provider(MediaModule_ProvidesKeyguardMediaHostFactory.create(MediaHost_MediaHostStateHolder_Factory.create(), this.mediaHierarchyManagerProvider, this.mediaDataManagerProvider, this.mediaHostStatesManagerProvider));
            this.providesKeyguardMediaHostProvider = provider18;
            this.keyguardMediaControllerProvider = DoubleCheck.provider(KeyguardMediaController_Factory.create(provider18, this.keyguardBypassControllerProvider, this.statusBarStateControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.featureFlagsProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideConfigurationControllerProvider));
            Provider<LogBuffer> provider19 = DoubleCheck.provider(LogModule_ProvideNotificationSectionLogBufferFactory.create(this.logBufferFactoryProvider));
            this.provideNotificationSectionLogBufferProvider = provider19;
            this.notificationSectionsLoggerProvider = DoubleCheck.provider(NotificationSectionsLogger_Factory.create(provider19));
            this.providesIncomingHeaderControllerProvider = C1579x340f4262.create(this.providesIncomingHeaderSubcomponentProvider);
            this.providesPeopleHeaderControllerProvider = C1582x812edf99.create(this.providesPeopleHeaderSubcomponentProvider);
            this.providesAlertingHeaderControllerProvider = C1576x41b9fd82.create(this.providesAlertingHeaderSubcomponentProvider);
            C1585xcc90df13 create8 = C1585xcc90df13.create(this.providesSilentHeaderSubcomponentProvider);
            this.providesSilentHeaderControllerProvider = create8;
            this.notificationSectionsManagerProvider = NotificationSectionsManager_Factory.create(this.desktopStatusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardMediaControllerProvider, this.notificationSectionsFeatureManagerProvider, this.notificationSectionsLoggerProvider, this.providesIncomingHeaderControllerProvider, this.providesPeopleHeaderControllerProvider, this.providesAlertingHeaderControllerProvider, create8);
            Provider<AmbientState> provider20 = DoubleCheck.provider(AmbientState_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.notificationSectionsManagerProvider, this.keyguardBypassControllerProvider));
            this.ambientStateProvider = provider20;
            this.lockscreenShadeTransitionControllerProvider = DoubleCheck.provider(LockscreenShadeTransitionController_Factory.create(this.statusBarStateControllerImplProvider, this.lockscreenGestureLoggerProvider, this.keyguardBypassControllerProvider, this.notificationLockscreenUserManagerImplProvider, this.falsingCollectorImplProvider, provider20, DaggerDesktopGlobalRootComponent.this.provideDisplayMetricsProvider, this.mediaHierarchyManagerProvider, this.scrimControllerProvider, this.notificationShadeDepthControllerProvider, this.featureFlagsProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideConfigurationControllerProvider, this.falsingManagerProxyProvider));
            this.udfpsHapticsSimulatorProvider = DoubleCheck.provider(UdfpsHapticsSimulator_Factory.create(this.commandRegistryProvider, DaggerDesktopGlobalRootComponent.this.provideVibratorProvider, this.keyguardUpdateMonitorProvider));
            this.optionalOfUdfpsHbmProvider = DaggerDesktopGlobalRootComponent.absentJdkOptionalProvider();
        }

        private void initialize4(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            DependencyProvider dependencyProvider3 = dependencyProvider;
            this.notificationRoundnessManagerProvider = DoubleCheck.provider(NotificationRoundnessManager_Factory.create(this.keyguardBypassControllerProvider, this.notificationSectionsFeatureManagerProvider));
            this.pulseExpansionHandlerProvider = DoubleCheck.provider(PulseExpansionHandler_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationRoundnessManagerProvider, this.provideConfigurationControllerProvider, this.desktopStatusBarStateControllerImplProvider, this.falsingManagerProxyProvider, this.lockscreenShadeTransitionControllerProvider, this.falsingCollectorImplProvider));
            this.dozeServiceHostProvider = DoubleCheck.provider(DozeServiceHost_Factory.create(this.dozeLogProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerImplProvider, this.deviceProvisionedControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.provideBatteryControllerProvider, this.scrimControllerProvider, this.biometricUnlockControllerProvider, this.newKeyguardViewMediatorProvider, this.assistManagerProvider, this.dozeScrimControllerProvider, this.keyguardUpdateMonitorProvider, this.pulseExpansionHandlerProvider, this.notificationShadeWindowControllerImplProvider, this.notificationWakeUpCoordinatorProvider, this.authControllerProvider, this.notificationIconAreaControllerProvider));
            this.provideMotoDisplayManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideMotoDisplayManagerFactory.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider, this.dozeServiceHostProvider));
            this.udfpsControllerProvider = DoubleCheck.provider(UdfpsController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideExecutionProvider, this.providerLayoutInflaterProvider, DaggerDesktopGlobalRootComponent.this.providesFingerprintManagerProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.desktopStatusBarStateControllerImplProvider, this.provideMainDelayableExecutorProvider, this.provideStatusBarProvider, this.provideStatusBarKeyguardViewManagerProvider, this.dumpManagerProvider, this.keyguardUpdateMonitorProvider, this.newKeyguardViewMediatorProvider, this.falsingManagerProxyProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, this.lockscreenShadeTransitionControllerProvider, this.screenLifecycleProvider, DaggerDesktopGlobalRootComponent.this.provideVibratorProvider, this.udfpsHapticsSimulatorProvider, this.optionalOfUdfpsHbmProvider, this.keyguardStateControllerImplProvider, this.keyguardBypassControllerProvider, DaggerDesktopGlobalRootComponent.this.provideDisplayManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideMotoDisplayManagerProvider, this.provideConfigurationControllerProvider));
            this.sidefpsControllerProvider = DoubleCheck.provider(SidefpsController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providerLayoutInflaterProvider, DaggerDesktopGlobalRootComponent.this.providesFingerprintManagerProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, this.provideMainDelayableExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideDisplayManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
            DelegateFactory.setDelegate(this.authControllerProvider, DoubleCheck.provider(AuthController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, DaggerDesktopGlobalRootComponent.this.provideActivityTaskManagerProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, DaggerDesktopGlobalRootComponent.this.providesFingerprintManagerProvider, DaggerDesktopGlobalRootComponent.this.provideFaceManagerProvider, this.udfpsControllerProvider, this.sidefpsControllerProvider, DaggerDesktopGlobalRootComponent.this.provideDisplayManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider)));
            DelegateFactory.setDelegate(this.keyguardUpdateMonitorProvider, DoubleCheck.provider(KeyguardUpdateMonitor_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), this.providesBroadcastDispatcherProvider, this.dumpManagerProvider, this.ringerModeTrackerImplProvider, this.provideBackgroundExecutorProvider, this.desktopStatusBarStateControllerImplProvider, this.provideLockPatternUtilsProvider, this.authControllerProvider, this.telephonyListenerManagerProvider, this.featureFlagsProvider, DaggerDesktopGlobalRootComponent.this.provideVibratorProvider)));
            DelegateFactory.setDelegate(this.keyguardStateControllerImplProvider, DoubleCheck.provider(KeyguardStateControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.keyguardUpdateMonitorProvider, this.provideLockPatternUtilsProvider, this.provideSmartspaceTransitionControllerProvider)));
            this.brightLineFalsingManagerProvider = BrightLineFalsingManager_Factory.create(this.falsingDataProvider, this.provideMetricsLoggerProvider, this.namedSetOfFalsingClassifierProvider, this.singleTapClassifierProvider, this.doubleTapClassifierProvider, this.historyTrackerProvider, this.keyguardStateControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, GlobalModule_ProvideIsTestHarnessFactory.create());
            DelegateFactory.setDelegate(this.falsingManagerProxyProvider, DoubleCheck.provider(FalsingManagerProxy_Factory.create(this.providePluginManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.deviceConfigProxyProvider, this.dumpManagerProvider, this.brightLineFalsingManagerProvider)));
            BrightnessSlider_Factory_Factory create = BrightnessSlider_Factory_Factory.create(this.falsingManagerProxyProvider);
            this.factoryProvider = create;
            this.brightnessDialogProvider = BrightnessDialog_Factory.create(this.providesBroadcastDispatcherProvider, create);
            Provider<RecordingController> provider = DoubleCheck.provider(RecordingController_Factory.create(this.providesBroadcastDispatcherProvider));
            this.recordingControllerProvider = provider;
            this.screenRecordDialogProvider = ScreenRecordDialog_Factory.create(provider);
            this.usbDebuggingActivityProvider = UsbDebuggingActivity_Factory.create(this.providesBroadcastDispatcherProvider);
            this.usbDebuggingSecondaryUserActivityProvider = UsbDebuggingSecondaryUserActivity_Factory.create(this.providesBroadcastDispatcherProvider);
            this.userCreatorProvider = UserCreator_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider);
            UserModule_ProvideEditUserInfoControllerFactory create2 = UserModule_ProvideEditUserInfoControllerFactory.create(userModule);
            this.provideEditUserInfoControllerProvider = create2;
            this.createUserActivityProvider = CreateUserActivity_Factory.create(this.userCreatorProvider, create2, DaggerDesktopGlobalRootComponent.this.provideIActivityManagerProvider);
            TvNotificationHandler_Factory create3 = TvNotificationHandler_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideNotificationListenerProvider);
            this.tvNotificationHandlerProvider = create3;
            this.tvNotificationPanelActivityProvider = TvNotificationPanelActivity_Factory.create(create3);
            this.peopleSpaceActivityProvider = PeopleSpaceActivity_Factory.create(this.peopleSpaceWidgetManagerProvider);
            this.imageExporterProvider = ImageExporter_Factory.create(DaggerDesktopGlobalRootComponent.this.provideContentResolverProvider);
            this.longScreenshotDataProvider = DoubleCheck.provider(LongScreenshotData_Factory.create());
            this.longScreenshotActivityProvider = LongScreenshotActivity_Factory.create(DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.imageExporterProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.longScreenshotDataProvider);
            this.launchConversationActivityProvider = LaunchConversationActivity_Factory.create(this.provideNotificationEntryManagerProvider, this.provideBubblesManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, this.provideCommandQueueProvider);
            this.sensorUseStartedActivityProvider = SensorUseStartedActivity_Factory.create(this.provideIndividualSensorPrivacyControllerProvider, this.keyguardStateControllerImplProvider, this.keyguardDismissUtilProvider, this.provideBgHandlerProvider);
            this.tvUnblockSensorActivityProvider = TvUnblockSensorActivity_Factory.create(this.provideIndividualSensorPrivacyControllerProvider);
            this.provideExecutorProvider = DoubleCheck.provider(SysUIConcurrencyModule_ProvideExecutorFactory.create(this.provideBgLooperProvider));
            this.controlsListingControllerImplProvider = DoubleCheck.provider(ControlsListingControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideExecutorProvider, this.provideUserTrackerProvider));
            this.controlsControllerImplProvider = new DelegateFactory();
            this.provideSharePreferencesProvider = DependencyProvider_ProvideSharePreferencesFactory.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.globalActionsComponentProvider = new DelegateFactory();
            this.provideQuickAccessWalletClientProvider = DoubleCheck.provider(WalletModule_ProvideQuickAccessWalletClientFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.globalActionsInfoProvider = GlobalActionsInfoProvider_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideQuickAccessWalletClientProvider, this.controlsControllerImplProvider, this.activityStarterDelegateProvider);
            this.globalActionsDialogLiteProvider = GlobalActionsDialogLite_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.globalActionsComponentProvider, DaggerDesktopGlobalRootComponent.this.provideAudioManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIDreamManagerProvider, DaggerDesktopGlobalRootComponent.this.provideDevicePolicyManagerProvider, this.provideLockPatternUtilsProvider, this.providesBroadcastDispatcherProvider, this.telephonyListenerManagerProvider, this.globalSettingsImplProvider, this.secureSettingsImplProvider, DaggerDesktopGlobalRootComponent.this.provideVibratorProvider, DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.provideConfigurationControllerProvider, this.keyguardStateControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DaggerDesktopGlobalRootComponent.this.provideTrustManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIActivityManagerProvider, DaggerDesktopGlobalRootComponent.this.provideTelecomManagerProvider, this.provideMetricsLoggerProvider, this.sysuiColorExtractorProvider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, this.notificationShadeWindowControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.globalActionsInfoProvider, this.ringerModeTrackerImplProvider, this.provideSysUiStateProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider, this.provideStatusBarProvider);
            this.globalActionsDialogFolioProvider = GlobalActionsDialogFolio_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.globalActionsComponentProvider, DaggerDesktopGlobalRootComponent.this.provideAudioManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIDreamManagerProvider, DaggerDesktopGlobalRootComponent.this.provideDevicePolicyManagerProvider, this.provideLockPatternUtilsProvider, this.providesBroadcastDispatcherProvider, this.telephonyListenerManagerProvider, this.globalSettingsImplProvider, this.secureSettingsImplProvider, DaggerDesktopGlobalRootComponent.this.provideVibratorProvider, DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.provideConfigurationControllerProvider, this.keyguardStateControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DaggerDesktopGlobalRootComponent.this.provideTrustManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIActivityManagerProvider, DaggerDesktopGlobalRootComponent.this.provideTelecomManagerProvider, this.provideMetricsLoggerProvider, this.sysuiColorExtractorProvider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, this.notificationShadeWindowControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.ringerModeTrackerImplProvider, this.provideSysUiStateProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider, this.provideStatusBarProvider);
            this.globalActionsImplProvider = GlobalActionsImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.globalActionsDialogLiteProvider, this.blurUtilsProvider, this.keyguardStateControllerImplProvider, this.deviceProvisionedControllerImplProvider, this.globalActionsDialogFolioProvider);
            DelegateFactory.setDelegate(this.globalActionsComponentProvider, DoubleCheck.provider(GlobalActionsComponent_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.extensionControllerImplProvider, this.globalActionsImplProvider, this.provideStatusBarKeyguardViewManagerProvider)));
            this.setTaskViewFactoryProvider = InstanceFactory.create(optional7);
            this.controlsUiControllerImplProvider = new DelegateFactory();
            this.controlsMetricsLoggerImplProvider = DoubleCheck.provider(ControlsMetricsLoggerImpl_Factory.create());
            this.controlActionCoordinatorImplProvider = DoubleCheck.provider(ControlActionCoordinatorImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideDelayableExecutorProvider, this.provideMainDelayableExecutorProvider, this.activityStarterDelegateProvider, this.keyguardStateControllerImplProvider, this.globalActionsComponentProvider, this.setTaskViewFactoryProvider, this.providesBroadcastDispatcherProvider, this.controlsUiControllerImplProvider, this.controlsMetricsLoggerImplProvider));
            this.customIconCacheProvider = DoubleCheck.provider(CustomIconCache_Factory.create());
            DelegateFactory.setDelegate(this.controlsUiControllerImplProvider, DoubleCheck.provider(ControlsUiControllerImpl_Factory.create(this.controlsControllerImplProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideMainDelayableExecutorProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsListingControllerImplProvider, this.provideSharePreferencesProvider, this.controlActionCoordinatorImplProvider, this.activityStarterDelegateProvider, this.shadeControllerImplProvider, this.customIconCacheProvider, this.controlsMetricsLoggerImplProvider, this.keyguardStateControllerImplProvider)));
            this.controlsBindingControllerImplProvider = DoubleCheck.provider(ControlsBindingControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsControllerImplProvider, this.provideUserTrackerProvider));
            this.optionalOfControlsFavoritePersistenceWrapperProvider = DaggerDesktopGlobalRootComponent.absentJdkOptionalProvider();
            DelegateFactory.setDelegate(this.controlsControllerImplProvider, DoubleCheck.provider(ControlsControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsUiControllerImplProvider, this.controlsBindingControllerImplProvider, this.controlsListingControllerImplProvider, this.providesBroadcastDispatcherProvider, this.optionalOfControlsFavoritePersistenceWrapperProvider, this.dumpManagerProvider, this.provideUserTrackerProvider)));
            this.controlsProviderSelectorActivityProvider = ControlsProviderSelectorActivity_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.controlsListingControllerImplProvider, this.controlsControllerImplProvider, this.providesBroadcastDispatcherProvider, this.controlsUiControllerImplProvider);
            this.controlsFavoritingActivityProvider = ControlsFavoritingActivity_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.controlsControllerImplProvider, this.controlsListingControllerImplProvider, this.providesBroadcastDispatcherProvider, this.controlsUiControllerImplProvider);
            this.controlsEditingActivityProvider = ControlsEditingActivity_Factory.create(this.controlsControllerImplProvider, this.providesBroadcastDispatcherProvider, this.customIconCacheProvider, this.controlsUiControllerImplProvider);
            this.controlsRequestDialogProvider = ControlsRequestDialog_Factory.create(this.controlsControllerImplProvider, this.providesBroadcastDispatcherProvider, this.controlsListingControllerImplProvider);
            this.controlsActivityProvider = ControlsActivity_Factory.create(this.controlsUiControllerImplProvider);
            this.walletActivityProvider = WalletActivity_Factory.create(this.keyguardStateControllerImplProvider, this.keyguardDismissUtilProvider, this.activityStarterDelegateProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.falsingCollectorImplProvider, this.provideUserTrackerProvider, this.keyguardUpdateMonitorProvider, this.provideStatusBarKeyguardViewManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider);
            this.mapOfClassOfAndProviderOfActivityProvider = MapProviderFactory.builder(20).put(TunerActivity.class, this.tunerActivityProvider).put(ForegroundServicesDialog.class, ForegroundServicesDialog_Factory.create()).put(WorkLockActivity.class, this.workLockActivityProvider).put(BrightnessDialog.class, this.brightnessDialogProvider).put(ScreenRecordDialog.class, this.screenRecordDialogProvider).put(UsbDebuggingActivity.class, this.usbDebuggingActivityProvider).put(UsbDebuggingSecondaryUserActivity.class, this.usbDebuggingSecondaryUserActivityProvider).put(CreateUserActivity.class, this.createUserActivityProvider).put(TvNotificationPanelActivity.class, this.tvNotificationPanelActivityProvider).put(PeopleSpaceActivity.class, this.peopleSpaceActivityProvider).put(LongScreenshotActivity.class, this.longScreenshotActivityProvider).put(LaunchConversationActivity.class, this.launchConversationActivityProvider).put(SensorUseStartedActivity.class, this.sensorUseStartedActivityProvider).put(TvUnblockSensorActivity.class, this.tvUnblockSensorActivityProvider).put(ControlsProviderSelectorActivity.class, this.controlsProviderSelectorActivityProvider).put(ControlsFavoritingActivity.class, this.controlsFavoritingActivityProvider).put(ControlsEditingActivity.class, this.controlsEditingActivityProvider).put(ControlsRequestDialog.class, this.controlsRequestDialogProvider).put(ControlsActivity.class, this.controlsActivityProvider).put(WalletActivity.class, this.walletActivityProvider).build();
            C27577 r1 = new Provider<DozeComponent.Builder>() {
                public DozeComponent.Builder get() {
                    return new DozeComponentFactory();
                }
            };
            this.dozeComponentBuilderProvider = r1;
            this.dozeServiceProvider = DozeService_Factory.create(r1, this.providePluginManagerProvider);
            Provider<KeyguardLifecyclesDispatcher> provider2 = DoubleCheck.provider(KeyguardLifecyclesDispatcher_Factory.create(this.screenLifecycleProvider, this.wakefulnessLifecycleProvider));
            this.keyguardLifecyclesDispatcherProvider = provider2;
            this.keyguardServiceProvider = KeyguardService_Factory.create(this.newKeyguardViewMediatorProvider, provider2);
            this.dumpHandlerProvider = DumpHandler_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dumpManagerProvider, this.logBufferEulogizerProvider);
            this.logBufferFreezerProvider = LogBufferFreezer_Factory.create(this.dumpManagerProvider, this.provideMainDelayableExecutorProvider);
            this.batteryStateNotifierProvider = BatteryStateNotifier_Factory.create(this.provideBatteryControllerProvider, DaggerDesktopGlobalRootComponent.this.provideNotificationManagerProvider, this.provideDelayableExecutorProvider, DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.systemUIServiceProvider = SystemUIService_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.dumpHandlerProvider, this.providesBroadcastDispatcherProvider, this.logBufferFreezerProvider, this.batteryStateNotifierProvider);
            this.systemUIAuxiliaryDumpServiceProvider = SystemUIAuxiliaryDumpService_Factory.create(this.dumpHandlerProvider);
            Provider<Looper> provider3 = DoubleCheck.provider(SysUIConcurrencyModule_ProvideLongRunningLooperFactory.create());
            this.provideLongRunningLooperProvider = provider3;
            Provider<Executor> provider4 = DoubleCheck.provider(SysUIConcurrencyModule_ProvideLongRunningExecutorFactory.create(provider3));
            this.provideLongRunningExecutorProvider = provider4;
            this.recordingServiceProvider = RecordingService_Factory.create(this.recordingControllerProvider, provider4, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DaggerDesktopGlobalRootComponent.this.provideNotificationManagerProvider, this.provideUserTrackerProvider, this.keyguardDismissUtilProvider);
            this.screenshotNotificationsControllerProvider = ScreenshotNotificationsController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider);
            this.screenshotSmartActionsProvider = DoubleCheck.provider(ScreenshotSmartActions_Factory.create());
            Provider<MotoGlobalScreenshot> provider5 = DoubleCheck.provider(MotoGlobalScreenshot_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.screenshotNotificationsControllerProvider, this.imageExporterProvider, this.screenshotSmartActionsProvider));
            this.motoGlobalScreenshotProvider = provider5;
            this.motoTakeScreenshotServiceProvider = MotoTakeScreenshotService_Factory.create(provider5, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider);
            this.scrollCaptureClientProvider = ScrollCaptureClient_Factory.create(DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.imageTileSetProvider = ImageTileSet_Factory.create(this.provideHandlerProvider);
            this.scrollCaptureControllerProvider = ScrollCaptureController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBackgroundExecutorProvider, this.scrollCaptureClientProvider, this.imageTileSetProvider);
            ScreenshotController_Factory create4 = ScreenshotController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.screenshotSmartActionsProvider, this.screenshotNotificationsControllerProvider, this.scrollCaptureClientProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.imageExporterProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.scrollCaptureControllerProvider, this.longScreenshotDataProvider, DaggerDesktopGlobalRootComponent.this.provideActivityManagerProvider);
            this.screenshotControllerProvider = create4;
            this.takeScreenshotServiceProvider = TakeScreenshotService_Factory.create(create4, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.screenshotNotificationsControllerProvider);
            this.mapOfClassOfAndProviderOfServiceProvider = MapProviderFactory.builder(8).put(DozeService.class, this.dozeServiceProvider).put(ImageWallpaper.class, ImageWallpaper_Factory.create()).put(KeyguardService.class, this.keyguardServiceProvider).put(SystemUIService.class, this.systemUIServiceProvider).put(SystemUIAuxiliaryDumpService.class, this.systemUIAuxiliaryDumpServiceProvider).put(RecordingService.class, this.recordingServiceProvider).put(MotoTakeScreenshotService.class, this.motoTakeScreenshotServiceProvider).put(TakeScreenshotService.class, this.takeScreenshotServiceProvider).build();
            this.provideLeakReportEmailProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideLeakReportEmailFactory.create());
            this.leakReporterProvider = DoubleCheck.provider(LeakReporter_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideLeakDetectorProvider, this.provideLeakReportEmailProvider));
            this.garbageMonitorProvider = DoubleCheck.provider(GarbageMonitor_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgLooperProvider, this.provideLeakDetectorProvider, this.leakReporterProvider));
            this.serviceProvider = DoubleCheck.provider(GarbageMonitor_Service_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.garbageMonitorProvider));
            this.instantAppNotifierProvider = DoubleCheck.provider(InstantAppNotifier_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.provideUiBackgroundExecutorProvider, this.setLegacySplitScreenProvider));
            this.latencyTesterProvider = DoubleCheck.provider(LatencyTester_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.biometricUnlockControllerProvider, DaggerDesktopGlobalRootComponent.this.providePowerManagerProvider, this.providesBroadcastDispatcherProvider));
            this.powerUIProvider = DoubleCheck.provider(PowerUI_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, this.provideCommandQueueProvider, this.provideStatusBarProvider));
            Provider<LogBuffer> provider6 = DoubleCheck.provider(LogModule_ProvidePrivacyLogBufferFactory.create(this.logBufferFactoryProvider));
            this.providePrivacyLogBufferProvider = provider6;
            PrivacyLogger_Factory create5 = PrivacyLogger_Factory.create(provider6);
            this.privacyLoggerProvider = create5;
            Provider<PrivacyItemController> provider7 = DoubleCheck.provider(PrivacyItemController_Factory.create(this.appOpsControllerImplProvider, this.provideMainDelayableExecutorProvider, this.provideBackgroundDelayableExecutorProvider, this.deviceConfigProxyProvider, this.provideUserTrackerProvider, create5, this.bindSystemClockProvider, this.dumpManagerProvider));
            this.privacyItemControllerProvider = provider7;
            this.systemEventCoordinatorProvider = DoubleCheck.provider(SystemEventCoordinator_Factory.create(this.bindSystemClockProvider, this.provideBatteryControllerProvider, provider7, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.statusBarLocationPublisherProvider = DoubleCheck.provider(StatusBarLocationPublisher_Factory.create());
            SystemEventChipAnimationController_Factory create6 = SystemEventChipAnimationController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.superStatusBarViewFactoryProvider, this.statusBarWindowControllerProvider, this.statusBarLocationPublisherProvider);
            this.systemEventChipAnimationControllerProvider = create6;
            this.systemStatusAnimationSchedulerProvider = DoubleCheck.provider(SystemStatusAnimationScheduler_Factory.create(this.systemEventCoordinatorProvider, create6, this.statusBarWindowControllerProvider, this.dumpManagerProvider, this.bindSystemClockProvider, this.provideMainDelayableExecutorProvider));
            this.privacyDotViewControllerProvider = DoubleCheck.provider(PrivacyDotViewController_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.desktopStatusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.statusBarContentInsetsProvider, this.systemStatusAnimationSchedulerProvider));
            this.screenDecorationsProvider = DoubleCheck.provider(ScreenDecorations_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.secureSettingsImplProvider, this.providesBroadcastDispatcherProvider, this.tunerServiceImplProvider, this.provideUserTrackerProvider, this.privacyDotViewControllerProvider, ThreadFactoryImpl_Factory.create()));
            this.shortcutKeyDispatcherProvider = DoubleCheck.provider(ShortcutKeyDispatcher_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.setLegacySplitScreenProvider));
            this.sliceBroadcastRelayHandlerProvider = DoubleCheck.provider(SliceBroadcastRelayHandler_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider));
            this.provideThemeOverlayManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideThemeOverlayManagerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideOverlayManagerProvider, this.dumpManagerProvider));
            this.themeOverlayControllerProvider = DoubleCheck.provider(ThemeOverlayController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, this.provideBgHandlerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.provideThemeOverlayManagerProvider, this.secureSettingsImplProvider, DaggerDesktopGlobalRootComponent.this.provideWallpaperManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, this.deviceProvisionedControllerImplProvider, this.provideUserTrackerProvider, this.dumpManagerProvider, this.featureFlagsProvider, this.wakefulnessLifecycleProvider));
            this.toastFactoryProvider = DoubleCheck.provider(ToastFactory_Factory.create(this.providerLayoutInflaterProvider, this.providePluginManagerProvider, this.dumpManagerProvider));
            this.provideToastLogBufferProvider = DoubleCheck.provider(LogModule_ProvideToastLogBufferFactory.create(this.logBufferFactoryProvider));
        }

        private void initialize5(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            this.toastLoggerProvider = ToastLogger_Factory.create(this.provideToastLogBufferProvider);
            this.toastUIProvider = DoubleCheck.provider(ToastUI_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.toastFactoryProvider, this.toastLoggerProvider));
            this.tvStatusBarProvider = DoubleCheck.provider(TvStatusBar_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.assistManagerProvider));
            this.tvNotificationPanelProvider = DoubleCheck.provider(TvNotificationPanel_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider));
            this.tvOngoingPrivacyChipProvider = DoubleCheck.provider(TvOngoingPrivacyChip_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.privacyItemControllerProvider));
            this.volumeDialogControllerImplProvider = DoubleCheck.provider(VolumeDialogControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, this.ringerModeTrackerImplProvider, ThreadFactoryImpl_Factory.create(), DaggerDesktopGlobalRootComponent.this.provideAudioManagerProvider, DaggerDesktopGlobalRootComponent.this.provideNotificationManagerProvider, DaggerDesktopGlobalRootComponent.this.provideOptionalVibratorProvider, DaggerDesktopGlobalRootComponent.this.provideIAudioServiceProvider, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider, this.wakefulnessLifecycleProvider));
            this.volumeDialogComponentProvider = DoubleCheck.provider(VolumeDialogComponent_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.newKeyguardViewMediatorProvider, this.volumeDialogControllerImplProvider, this.provideDemoModeControllerProvider));
            this.volumeUIProvider = DoubleCheck.provider(VolumeUI_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.volumeDialogComponentProvider));
            this.providesModeSwitchesControllerProvider = DoubleCheck.provider(DependencyProvider_ProvidesModeSwitchesControllerFactory.create(dependencyProvider, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.windowMagnificationProvider = DoubleCheck.provider(WindowMagnification_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideCommandQueueProvider, this.providesModeSwitchesControllerProvider, this.provideSysUiStateProvider, this.overviewProxyServiceProvider));
            this.setHideDisplayCutoutProvider = InstanceFactory.create(optional8);
            this.setShellCommandHandlerProvider = InstanceFactory.create(optional9);
            this.wMShellProvider = DoubleCheck.provider(WMShell_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.setPipProvider, this.setLegacySplitScreenProvider, this.setOneHandedProvider, this.setHideDisplayCutoutProvider, this.setShellCommandHandlerProvider, this.provideCommandQueueProvider, this.provideConfigurationControllerProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.screenLifecycleProvider, this.provideSysUiStateProvider, this.protoTracerProvider, this.wakefulnessLifecycleProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider));
            this.provideTaskStackChangeListenersProvider = DoubleCheck.provider(DependencyProvider_ProvideTaskStackChangeListenersFactory.create(dependencyProvider));
            this.homeSoundEffectControllerProvider = DoubleCheck.provider(HomeSoundEffectController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideAudioManagerProvider, this.provideTaskStackChangeListenersProvider, this.provideActivityManagerWrapperProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider));
            this.mapOfClassOfAndProviderOfSystemUIProvider = MapProviderFactory.builder(22).put(AuthController.class, this.authControllerProvider).put(GarbageMonitor.Service.class, this.serviceProvider).put(GlobalActionsComponent.class, this.globalActionsComponentProvider).put(InstantAppNotifier.class, this.instantAppNotifierProvider).put(KeyguardViewMediator.class, this.newKeyguardViewMediatorProvider).put(LatencyTester.class, this.latencyTesterProvider).put(PowerUI.class, this.powerUIProvider).put(Recents.class, this.provideRecentsProvider).put(ScreenDecorations.class, this.screenDecorationsProvider).put(ShortcutKeyDispatcher.class, this.shortcutKeyDispatcherProvider).put(SliceBroadcastRelayHandler.class, this.sliceBroadcastRelayHandlerProvider).put(StatusBar.class, this.provideStatusBarProvider).put(SystemActions.class, this.systemActionsProvider).put(ThemeOverlayController.class, this.themeOverlayControllerProvider).put(ToastUI.class, this.toastUIProvider).put(TvStatusBar.class, this.tvStatusBarProvider).put(TvNotificationPanel.class, this.tvNotificationPanelProvider).put(TvOngoingPrivacyChip.class, this.tvOngoingPrivacyChipProvider).put(VolumeUI.class, this.volumeUIProvider).put(WindowMagnification.class, this.windowMagnificationProvider).put(WMShell.class, this.wMShellProvider).put(HomeSoundEffectController.class, this.homeSoundEffectControllerProvider).build();
            this.overviewProxyRecentsImplProvider = DoubleCheck.provider(OverviewProxyRecentsImpl_Factory.create(this.optionalOfLazyOfStatusBarProvider));
            this.mapOfClassOfAndProviderOfRecentsImplementationProvider = MapProviderFactory.builder(1).put(OverviewProxyRecentsImpl.class, this.overviewProxyRecentsImplProvider).build();
            Provider<Optional<StatusBar>> access$1200 = PresentJdkOptionalInstanceProvider.m102of(this.provideStatusBarProvider);
            this.optionalOfStatusBarProvider = access$1200;
            this.actionProxyReceiverProvider = ActionProxyReceiver_Factory.create(access$1200, this.provideActivityManagerWrapperProvider, this.screenshotSmartActionsProvider);
            this.deleteScreenshotReceiverProvider = DeleteScreenshotReceiver_Factory.create(this.screenshotSmartActionsProvider, this.provideBackgroundExecutorProvider);
            this.smartActionsReceiverProvider = SmartActionsReceiver_Factory.create(this.screenshotSmartActionsProvider);
            this.mediaOutputDialogReceiverProvider = MediaOutputDialogReceiver_Factory.create(this.mediaOutputDialogFactoryProvider);
            this.peopleSpaceWidgetPinnedReceiverProvider = PeopleSpaceWidgetPinnedReceiver_Factory.create(this.peopleSpaceWidgetManagerProvider);
            this.peopleSpaceWidgetProvider = PeopleSpaceWidgetProvider_Factory.create(this.peopleSpaceWidgetManagerProvider);
            MapProviderFactory<K, V> build = MapProviderFactory.builder(6).put(ActionProxyReceiver.class, this.actionProxyReceiverProvider).put(DeleteScreenshotReceiver.class, this.deleteScreenshotReceiverProvider).put(SmartActionsReceiver.class, this.smartActionsReceiverProvider).put(MediaOutputDialogReceiver.class, this.mediaOutputDialogReceiverProvider).put(PeopleSpaceWidgetPinnedReceiver.class, this.peopleSpaceWidgetPinnedReceiverProvider).put(PeopleSpaceWidgetProvider.class, this.peopleSpaceWidgetProvider).build();
            this.mapOfClassOfAndProviderOfBroadcastReceiverProvider = build;
            DelegateFactory.setDelegate(this.contextComponentResolverProvider, DoubleCheck.provider(ContextComponentResolver_Factory.create(this.mapOfClassOfAndProviderOfActivityProvider, this.mapOfClassOfAndProviderOfServiceProvider, this.mapOfClassOfAndProviderOfSystemUIProvider, this.mapOfClassOfAndProviderOfRecentsImplementationProvider, build)));
            this.bluetoothControllerImplProvider = DoubleCheck.provider(BluetoothControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dumpManagerProvider, this.provideBgLooperProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), this.provideLocalBluetoothControllerProvider));
            this.locationControllerImplProvider = DoubleCheck.provider(LocationControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.appOpsControllerImplProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), this.provideBgHandlerProvider, this.providesBroadcastDispatcherProvider, this.bootCompleteCacheImplProvider, this.provideUserTrackerProvider));
            this.rotationLockControllerImplProvider = DoubleCheck.provider(RotationLockControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.callbackHandlerProvider = CallbackHandler_Factory.create(GlobalConcurrencyModule_ProvideMainLooperFactory.create());
            this.wifiPickerTrackerFactoryProvider = DoubleCheck.provider(AccessPointControllerImpl_WifiPickerTrackerFactory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideWifiManagerProvider, DaggerDesktopGlobalRootComponent.this.provideConnectivityManagagerProvider, DaggerDesktopGlobalRootComponent.this.provideNetworkScoreManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideBgHandlerProvider));
            this.provideAccessPointControllerImplProvider = DoubleCheck.provider(StatusBarPolicyModule_ProvideAccessPointControllerImplFactory.create(DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, this.provideUserTrackerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.wifiPickerTrackerFactoryProvider));
            this.carrierConfigTrackerProvider = DoubleCheck.provider(CarrierConfigTracker_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.networkControllerImplProvider = DoubleCheck.provider(NetworkControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgLooperProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideSubcriptionManagerProvider, this.callbackHandlerProvider, this.deviceProvisionedControllerImplProvider, this.providesBroadcastDispatcherProvider, DaggerDesktopGlobalRootComponent.this.provideConnectivityManagagerProvider, DaggerDesktopGlobalRootComponent.this.provideTelephonyManagerProvider, this.telephonyListenerManagerProvider, DaggerDesktopGlobalRootComponent.this.provideWifiManagerProvider, DaggerDesktopGlobalRootComponent.this.provideNetworkScoreManagerProvider, this.provideAccessPointControllerImplProvider, this.provideDemoModeControllerProvider, this.carrierConfigTrackerProvider, this.featureFlagsProvider, this.dumpManagerProvider));
            this.hotspotControllerImplProvider = DoubleCheck.provider(HotspotControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideBgHandlerProvider));
            this.castControllerImplProvider = DoubleCheck.provider(CastControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.dumpManagerProvider));
            this.flashlightControllerImplProvider = DoubleCheck.provider(FlashlightControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.userInfoControllerImplProvider = DoubleCheck.provider(UserInfoControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.provideNightDisplayListenerProvider = NightDisplayListenerModule_ProvideNightDisplayListenerFactory.create(nightDisplayListenerModule, DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgHandlerProvider);
            DependencyProvider dependencyProvider3 = dependencyProvider;
            this.provideReduceBrightColorsListenerProvider = DoubleCheck.provider(DependencyProvider_ProvideReduceBrightColorsListenerFactory.create(dependencyProvider3, this.provideBgHandlerProvider, this.provideUserTrackerProvider, DaggerDesktopGlobalRootComponent.this.provideColorDisplayManagerProvider, this.secureSettingsImplProvider));
            this.managedProfileControllerImplProvider = DoubleCheck.provider(ManagedProfileControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider));
            this.nextAlarmControllerImplProvider = DoubleCheck.provider(NextAlarmControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.provideAlarmManagerProvider, this.providesBroadcastDispatcherProvider, this.dumpManagerProvider));
            this.provideDataSaverControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideDataSaverControllerFactory.create(dependencyProvider3, this.networkControllerImplProvider));
            this.accessibilityControllerProvider = DoubleCheck.provider(AccessibilityController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.securityControllerImplProvider = DoubleCheck.provider(SecurityControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgHandlerProvider, this.providesBroadcastDispatcherProvider, this.provideBackgroundExecutorProvider));
            this.cliStatusBarWindowControllerProvider = DoubleCheck.provider(CliStatusBarWindowController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.newKeyguardViewMediatorProvider));
            this.statusBarIconControllerImplProvider = DoubleCheck.provider(StatusBarIconControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.provideDemoModeControllerProvider));
            C27588 r1 = new Provider<FragmentService.FragmentCreator.Factory>() {
                public FragmentService.FragmentCreator.Factory get() {
                    return new FragmentCreatorFactory();
                }
            };
            this.fragmentCreatorFactoryProvider = r1;
            this.fragmentServiceProvider = DoubleCheck.provider(FragmentService_Factory.create(r1, this.provideConfigurationControllerProvider));
            this.pluginDependencyProvider = DoubleCheck.provider(PluginDependencyProvider_Factory.create(this.providePluginManagerProvider));
            this.tunablePaddingServiceProvider = DoubleCheck.provider(TunablePadding_TunablePaddingService_Factory.create(this.tunerServiceImplProvider));
            this.uiOffloadThreadProvider = DoubleCheck.provider(UiOffloadThread_Factory.create());
            this.powerNotificationWarningsProvider = DoubleCheck.provider(PowerNotificationWarnings_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.activityStarterDelegateProvider));
            this.cliNotificationStackClientProvider = DoubleCheck.provider(CliNotificationStackClient_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider));
            this.accessibilityButtonTargetsObserverProvider = DoubleCheck.provider(AccessibilityButtonTargetsObserver_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.vibratorHelperProvider = DoubleCheck.provider(VibratorHelper_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.provideGroupExpansionManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideGroupExpansionManagerFactory.create(this.featureFlagsProvider, this.provideGroupMembershipManagerProvider, this.notificationGroupManagerLegacyProvider));
            this.statusBarRemoteInputCallbackProvider = DoubleCheck.provider(StatusBarRemoteInputCallback_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideGroupExpansionManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.keyguardStateControllerImplProvider, this.desktopStatusBarStateControllerImplProvider, this.provideStatusBarKeyguardViewManagerProvider, this.activityStarterDelegateProvider, this.shadeControllerImplProvider, this.provideCommandQueueProvider, this.actionClickLoggerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider));
            this.provideAccessibilityFloatingMenuControllerProvider = DoubleCheck.provider(C0913xb22f7179.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider, this.accessibilityButtonTargetsObserverProvider, this.accessibilityButtonModeObserverProvider, this.keyguardUpdateMonitorProvider));
            this.channelEditorDialogControllerProvider = DoubleCheck.provider(ChannelEditorDialogController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideINotificationManagerProvider, ChannelEditorDialog_Builder_Factory.create()));
            this.provideNotificationGutsManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationGutsManagerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideStatusBarProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideBgHandlerProvider, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, this.highPriorityProvider, this.provideINotificationManagerProvider, this.provideNotificationEntryManagerProvider, this.peopleSpaceWidgetManagerProvider, DaggerDesktopGlobalRootComponent.this.provideLauncherAppsProvider, DaggerDesktopGlobalRootComponent.this.provideShortcutManagerProvider, this.channelEditorDialogControllerProvider, this.provideUserTrackerProvider, this.assistantFeedbackControllerProvider, this.provideBubblesManagerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.provideOnUserInteractionCallbackProvider, this.shadeControllerImplProvider));
            this.expansionStateLoggerProvider = NotificationLogger_ExpansionStateLogger_Factory.create(this.provideUiBackgroundExecutorProvider);
            Provider<NotificationPanelLogger> provider = DoubleCheck.provider(NotificationsModule_ProvideNotificationPanelLoggerFactory.create());
            this.provideNotificationPanelLoggerProvider = provider;
            this.provideNotificationLoggerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationLoggerFactory.create(this.provideNotificationListenerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationEntryManagerProvider, this.desktopStatusBarStateControllerImplProvider, this.expansionStateLoggerProvider, provider));
            this.remoteInputQuickSettingsDisablerProvider = DoubleCheck.provider(RemoteInputQuickSettingsDisabler_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideConfigurationControllerProvider, this.provideCommandQueueProvider));
            this.provideAutoHideControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideAutoHideControllerFactory.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider));
            this.foregroundServiceNotificationListenerProvider = DoubleCheck.provider(ForegroundServiceNotificationListener_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.foregroundServiceControllerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider, this.bindSystemClockProvider));
            this.provideTimeTickHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideTimeTickHandlerFactory.create(dependencyProvider));
            this.clockManagerProvider = DoubleCheck.provider(ClockManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.injectionInflationControllerProvider, this.providePluginManagerProvider, this.sysuiColorExtractorProvider, this.dockManagerImplProvider, this.providesBroadcastDispatcherProvider));
            this.provideSensorPrivacyControllerProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideSensorPrivacyControllerFactory.create(DaggerDesktopGlobalRootComponent.this.provideSensorPrivacyManagerProvider));
            this.keyguardSecurityModelProvider = DoubleCheck.provider(KeyguardSecurityModel_Factory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.provideLockPatternUtilsProvider, this.keyguardUpdateMonitorProvider));
            this.provideCliStatusBarProvider = DoubleCheck.provider(DesktopStatusBarPhoneModule_ProvideCliStatusBarFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideHeadsUpManagerPhoneProvider, this.newKeyguardViewMediatorProvider, this.notificationGroupManagerLegacyProvider, this.dozeScrimControllerProvider, this.provideStatusBarKeyguardViewManagerProvider, this.keyguardBypassControllerProvider, this.falsingManagerProxyProvider, this.notificationLockscreenUserManagerImplProvider, this.injectionInflationControllerProvider, this.dozeServiceHostProvider, this.scrimControllerProvider, this.highPriorityProvider));
            this.factoryProvider2 = EdgeBackGestureHandler_Factory_Factory.create(this.overviewProxyServiceProvider, this.provideSysUiStateProvider, this.providePluginManagerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.providesBroadcastDispatcherProvider, this.protoTracerProvider, this.navigationModeControllerProvider, DaggerDesktopGlobalRootComponent.this.provideViewConfigurationProvider, DaggerDesktopGlobalRootComponent.this.provideWindowManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.falsingManagerProxyProvider);
            this.nfcControllerImplProvider = DoubleCheck.provider(NfcControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), this.provideBgLooperProvider, this.providesBroadcastDispatcherProvider));
            this.rROsControllerImplProvider = RROsControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), this.provideBgLooperProvider, this.providesBroadcastDispatcherProvider);
            this.provideDualSimIconControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideDualSimIconControllerFactory.create(dependencyProvider3, DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.desktopDisplayRootModulesManagerProvider = DoubleCheck.provider(DesktopDisplayRootModulesManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideCommandQueueProvider, this.provideBgHandlerProvider));
            this.motoTaskBarControllerProvider = DoubleCheck.provider(MotoTaskBarController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideBgHandlerProvider, this.provideCommandQueueProvider, this.desktopDisplayRootModulesManagerProvider, this.networkControllerImplProvider));
            this.cliNavGestureControllerProvider = DoubleCheck.provider(CliNavGestureController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.optionalOfLazyOfStatusBarProvider));
            this.tooltipPopupManagerProvider = DoubleCheck.provider(TooltipPopupManager_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.motoDesktopProcessTileServicesProvider = DoubleCheck.provider(MotoDesktopProcessTileServices_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesBroadcastDispatcherProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideUserTrackerProvider));
            this.qSTileHostProvider = new DelegateFactory();
            Provider<LogBuffer> provider2 = DoubleCheck.provider(LogModule_ProvideQuickSettingsLogBufferFactory.create(this.logBufferFactoryProvider));
            this.provideQuickSettingsLogBufferProvider = provider2;
            this.qSLoggerProvider = QSLogger_Factory.create(provider2);
            this.customTileStatePersisterProvider = CustomTileStatePersister_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.builderProvider4 = CustomTile_Builder_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.customTileStatePersisterProvider);
            this.wifiTileProvider = WifiTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.networkControllerImplProvider, this.provideAccessPointControllerImplProvider);
            this.internetTileProvider = InternetTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.networkControllerImplProvider);
            this.bluetoothTileProvider = BluetoothTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.bluetoothControllerImplProvider);
            this.cellularTileProvider = CellularTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.networkControllerImplProvider);
            this.dndTileProvider = DndTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.zenModeControllerImplProvider, this.provideSharePreferencesProvider);
            this.colorInversionTileProvider = ColorInversionTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideUserTrackerProvider, this.secureSettingsImplProvider);
            this.airplaneModeTileProvider = AirplaneModeTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.providesBroadcastDispatcherProvider, DaggerDesktopGlobalRootComponent.this.provideConnectivityManagagerProvider);
            this.workModeTileProvider = WorkModeTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.managedProfileControllerImplProvider);
            this.rotationLockTileProvider = RotationLockTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.rotationLockControllerImplProvider);
            this.flashlightTileProvider = FlashlightTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.flashlightControllerImplProvider);
            this.locationTileProvider = LocationTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.locationControllerImplProvider, this.keyguardStateControllerImplProvider);
            this.castTileProvider = CastTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.castControllerImplProvider, this.keyguardStateControllerImplProvider, this.networkControllerImplProvider, this.hotspotControllerImplProvider);
            this.hotspotTileProvider = HotspotTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider);
        }

        private void initialize6(DependencyProvider dependencyProvider, NightDisplayListenerModule nightDisplayListenerModule, UserModule userModule, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<SplitScreen> optional3, Optional<AppPairs> optional4, Optional<OneHanded> optional5, Optional<Bubbles> optional6, Optional<TaskViewFactory> optional7, Optional<HideDisplayCutout> optional8, Optional<ShellCommandHandler> optional9, ShellTransitions shellTransitions, Optional<StartingSurface> optional10, Optional<TaskSurfaceHelper> optional11) {
            this.userTileProvider = UserTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.userSwitcherControllerProvider, this.userInfoControllerImplProvider);
            this.batterySaverTileProvider = BatterySaverTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideBatteryControllerProvider, this.secureSettingsImplProvider);
            this.dataSaverTileProvider = DataSaverTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideDataSaverControllerProvider);
            this.builderProvider5 = NightDisplayListenerModule_Builder_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideBgHandlerProvider);
            this.nightDisplayTileProvider = NightDisplayTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.locationControllerImplProvider, DaggerDesktopGlobalRootComponent.this.provideColorDisplayManagerProvider, this.builderProvider5);
            this.nfcTileProvider = NfcTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.providesBroadcastDispatcherProvider);
            this.memoryTileProvider = GarbageMonitor_MemoryTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.garbageMonitorProvider);
            this.uiModeNightTileProvider = UiModeNightTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideConfigurationControllerProvider, this.provideBatteryControllerProvider, this.locationControllerImplProvider);
            this.screenRecordTileProvider = ScreenRecordTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.provideUserTrackerProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.recordingControllerProvider, this.keyguardDismissUtilProvider);
            Provider<Boolean> provider = DoubleCheck.provider(QSFlagsModule_IsReduceBrightColorsAvailableFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider));
            this.isReduceBrightColorsAvailableProvider = provider;
            this.reduceBrightColorsTileProvider = ReduceBrightColorsTile_Factory.create(provider, this.provideReduceBrightColorsListenerProvider, this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider);
            this.cameraToggleTileProvider = CameraToggleTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideMetricsLoggerProvider, this.falsingManagerProxyProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideIndividualSensorPrivacyControllerProvider, this.keyguardStateControllerImplProvider);
            this.microphoneToggleTileProvider = MicrophoneToggleTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideMetricsLoggerProvider, this.falsingManagerProxyProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideIndividualSensorPrivacyControllerProvider, this.keyguardStateControllerImplProvider);
            Provider<Boolean> provider2 = DoubleCheck.provider(ControlsModule_ProvidesControlsFeatureEnabledFactory.create(DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider));
            this.providesControlsFeatureEnabledProvider = provider2;
            this.controlsComponentProvider = DoubleCheck.provider(ControlsComponent_Factory.create(provider2, DaggerDesktopGlobalRootComponent.this.contextProvider, this.controlsControllerImplProvider, this.controlsUiControllerImplProvider, this.controlsListingControllerImplProvider, this.provideLockPatternUtilsProvider, this.keyguardStateControllerImplProvider, this.provideUserTrackerProvider, this.secureSettingsImplProvider));
            this.deviceControlsTileProvider = DeviceControlsTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.controlsComponentProvider, this.keyguardStateControllerImplProvider);
            this.alarmTileProvider = AlarmTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.provideUserTrackerProvider, this.nextAlarmControllerImplProvider);
            this.quickAccessWalletControllerProvider = DoubleCheck.provider(QuickAccessWalletController_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.secureSettingsImplProvider, this.provideQuickAccessWalletClientProvider, this.bindSystemClockProvider));
            this.quickAccessWalletTileProvider = QuickAccessWalletTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.keyguardStateControllerImplProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider, this.secureSettingsImplProvider, this.quickAccessWalletControllerProvider);
            this.moto5GTileProvider = Moto5GTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider, this.networkControllerImplProvider);
            ScreenshotTile_Factory create = ScreenshotTile_Factory.create(this.qSTileHostProvider, this.provideBgLooperProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.falsingManagerProxyProvider, this.provideMetricsLoggerProvider, this.desktopStatusBarStateControllerImplProvider, this.activityStarterDelegateProvider, this.qSLoggerProvider);
            this.screenshotTileProvider = create;
            this.qSFactoryImplProvider = DoubleCheck.provider(QSFactoryImpl_Factory.create(this.qSTileHostProvider, this.builderProvider4, this.wifiTileProvider, this.internetTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider, this.screenRecordTileProvider, this.reduceBrightColorsTileProvider, this.cameraToggleTileProvider, this.microphoneToggleTileProvider, this.deviceControlsTileProvider, this.alarmTileProvider, this.quickAccessWalletTileProvider, this.moto5GTileProvider, create));
            this.builderProvider6 = AutoAddTracker_Builder_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
            this.deviceControlsControllerImplProvider = DoubleCheck.provider(DeviceControlsControllerImpl_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.controlsComponentProvider, this.provideUserTrackerProvider, this.secureSettingsImplProvider));
            this.walletControllerImplProvider = DoubleCheck.provider(WalletControllerImpl_Factory.create(this.provideQuickAccessWalletClientProvider));
            this.provideAutoTileManagerProvider = QSModule_ProvideAutoTileManagerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.builderProvider6, this.qSTileHostProvider, this.provideBgHandlerProvider, this.secureSettingsImplProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider, this.managedProfileControllerImplProvider, this.provideNightDisplayListenerProvider, this.castControllerImplProvider, this.provideReduceBrightColorsListenerProvider, this.deviceControlsControllerImplProvider, this.walletControllerImplProvider, this.isReduceBrightColorsAvailableProvider, DaggerDesktopGlobalRootComponent.this.audioFxControllerImplProvider);
            Provider<QSTileHost> provider3 = this.qSTileHostProvider;
            Provider access$500 = DaggerDesktopGlobalRootComponent.this.contextProvider;
            Provider<StatusBarIconControllerImpl> provider4 = this.statusBarIconControllerImplProvider;
            Provider<QSFactoryImpl> provider5 = this.qSFactoryImplProvider;
            Provider access$600 = DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider;
            Provider<Looper> provider6 = this.provideBgLooperProvider;
            Provider<PluginManager> provider7 = this.providePluginManagerProvider;
            Provider<TunerServiceImpl> provider8 = this.tunerServiceImplProvider;
            Provider<AutoTileManager> provider9 = this.provideAutoTileManagerProvider;
            Provider<DumpManager> provider10 = this.dumpManagerProvider;
            Provider<BroadcastDispatcher> provider11 = this.providesBroadcastDispatcherProvider;
            Provider<Optional<StatusBar>> provider12 = this.optionalOfStatusBarProvider;
            Provider<QSLogger> provider13 = this.qSLoggerProvider;
            Provider access$1100 = DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider;
            Provider<UserTracker> provider14 = this.provideUserTrackerProvider;
            DelegateFactory.setDelegate(provider3, DoubleCheck.provider(QSTileHost_Factory.create(access$500, provider4, provider5, access$600, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, access$1100, provider14, this.secureSettingsImplProvider, this.customTileStatePersisterProvider)));
            this.dependencyProvider2 = DoubleCheck.provider(Dependency_Factory.create(this.dumpManagerProvider, this.activityStarterDelegateProvider, this.providesBroadcastDispatcherProvider, this.asyncSensorManagerProvider, this.bluetoothControllerImplProvider, this.locationControllerImplProvider, this.rotationLockControllerImplProvider, this.networkControllerImplProvider, this.zenModeControllerImplProvider, this.hotspotControllerImplProvider, this.castControllerImplProvider, this.flashlightControllerImplProvider, this.userSwitcherControllerProvider, this.userInfoControllerImplProvider, this.keyguardStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.provideBatteryControllerProvider, this.provideNightDisplayListenerProvider, this.provideReduceBrightColorsListenerProvider, this.managedProfileControllerImplProvider, this.nextAlarmControllerImplProvider, this.provideDataSaverControllerProvider, this.accessibilityControllerProvider, this.deviceProvisionedControllerImplProvider, this.providePluginManagerProvider, this.assistManagerProvider, this.securityControllerImplProvider, this.provideLeakDetectorProvider, this.leakReporterProvider, this.garbageMonitorProvider, this.tunerServiceImplProvider, this.notificationShadeWindowControllerImplProvider, this.statusBarWindowControllerProvider, this.cliStatusBarWindowControllerProvider, this.darkIconDispatcherImplProvider, this.provideConfigurationControllerProvider, this.statusBarIconControllerImplProvider, this.screenLifecycleProvider, this.wakefulnessLifecycleProvider, this.fragmentServiceProvider, this.extensionControllerImplProvider, this.pluginDependencyProvider, this.provideLocalBluetoothControllerProvider, this.volumeDialogControllerImplProvider, this.provideMetricsLoggerProvider, this.accessibilityManagerWrapperProvider, this.sysuiColorExtractorProvider, this.tunablePaddingServiceProvider, this.foregroundServiceControllerProvider, this.uiOffloadThreadProvider, this.powerNotificationWarningsProvider, this.lightBarControllerProvider, DaggerDesktopGlobalRootComponent.this.provideIWindowManagerProvider, this.overviewProxyServiceProvider, this.cliNotificationStackClientProvider, this.navigationModeControllerProvider, this.accessibilityButtonModeObserverProvider, this.accessibilityButtonTargetsObserverProvider, this.enhancedEstimatesImplProvider, this.vibratorHelperProvider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, DaggerDesktopGlobalRootComponent.this.provideDisplayMetricsProvider, this.lockscreenGestureLoggerProvider, this.keyguardEnvironmentImplProvider, this.shadeControllerImplProvider, this.statusBarRemoteInputCallbackProvider, this.appOpsControllerImplProvider, this.provideNavigationBarControllerProvider, this.provideAccessibilityFloatingMenuControllerProvider, this.desktopStatusBarStateControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.provideNotificationGroupAlertTransferHelperProvider, this.notificationGroupManagerLegacyProvider, this.provideVisualStabilityManagerProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationMediaManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.smartReplyConstantsProvider, this.provideNotificationListenerProvider, this.provideNotificationLoggerProvider, this.provideNotificationViewHierarchyManagerProvider, this.notificationFilterProvider, this.keyguardDismissUtilProvider, this.provideSmartReplyControllerProvider, this.remoteInputQuickSettingsDisablerProvider, this.provideNotificationEntryManagerProvider, DaggerDesktopGlobalRootComponent.this.provideSensorPrivacyManagerProvider, this.provideAutoHideControllerProvider, this.foregroundServiceNotificationListenerProvider, this.privacyItemControllerProvider, this.provideBgLooperProvider, this.provideBgHandlerProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, this.provideTimeTickHandlerProvider, this.provideLeakReportEmailProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.clockManagerProvider, this.provideActivityManagerWrapperProvider, this.provideDevicePolicyManagerWrapperProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerWrapperProvider, this.provideSensorPrivacyControllerProvider, this.dockManagerImplProvider, this.provideINotificationManagerProvider, this.provideSysUiStateProvider, DaggerDesktopGlobalRootComponent.this.provideAlarmManagerProvider, this.keyguardSecurityModelProvider, this.dozeParametersProvider, FrameworkServicesModule_ProvideIWallPaperManagerFactory.create(), this.provideCommandQueueProvider, this.provideRecentsProvider, this.provideStatusBarProvider, this.provideCliStatusBarProvider, this.recordingControllerProvider, this.protoTracerProvider, this.mediaOutputDialogFactoryProvider, this.deviceConfigProxyProvider, this.navigationBarOverlayControllerProvider, this.telephonyListenerManagerProvider, this.systemStatusAnimationSchedulerProvider, this.privacyDotViewControllerProvider, this.factoryProvider2, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, this.featureFlagsProvider, this.statusBarContentInsetsProvider, this.nfcControllerImplProvider, this.rROsControllerImplProvider, this.provideMotoDisplayManagerProvider, this.provideDualSimIconControllerProvider, this.motoTaskBarControllerProvider, DaggerDesktopGlobalRootComponent.this.cellLocationControllerImplProvider, this.mediaCarouselControllerProvider, this.cliNavGestureControllerProvider, DaggerDesktopGlobalRootComponent.this.multiUserCliNavGesturesProvider, this.tooltipPopupManagerProvider, this.motoDesktopProcessTileServicesProvider, this.desktopDisplayRootModulesManagerProvider, this.qSTileHostProvider));
            this.initControllerProvider = DoubleCheck.provider(InitController_Factory.create());
            this.provideClockInfoListProvider = ClockModule_ProvideClockInfoListFactory.create(this.clockManagerProvider);
            this.provideAllowNotificationLongPressProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory.create());
            this.qSDetailDisplayerProvider = DoubleCheck.provider(QSDetailDisplayer_Factory.create());
            this.providesQSMediaHostProvider = DoubleCheck.provider(MediaModule_ProvidesQSMediaHostFactory.create(MediaHost_MediaHostStateHolder_Factory.create(), this.mediaHierarchyManagerProvider, this.mediaDataManagerProvider, this.mediaHostStatesManagerProvider));
            this.providesQuickQSMediaHostProvider = DoubleCheck.provider(MediaModule_ProvidesQuickQSMediaHostFactory.create(MediaHost_MediaHostStateHolder_Factory.create(), this.mediaHierarchyManagerProvider, this.mediaDataManagerProvider, this.mediaHostStatesManagerProvider));
            this.ongoingCallLoggerProvider = DoubleCheck.provider(OngoingCallLogger_Factory.create(DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
            this.provideOngoingCallControllerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideOngoingCallControllerFactory.create(this.provideCommonNotifCollectionProvider, this.featureFlagsProvider, this.bindSystemClockProvider, this.activityStarterDelegateProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideIActivityManagerProvider, this.ongoingCallLoggerProvider));
            this.subscriptionManagerSlotIndexResolverProvider = DoubleCheck.provider(C1215xf95dc14f.create());
            this.privacyDialogControllerProvider = DoubleCheck.provider(PrivacyDialogController_Factory.create(DaggerDesktopGlobalRootComponent.this.providePermissionManagerProvider, DaggerDesktopGlobalRootComponent.this.providePackageManagerProvider, this.privacyItemControllerProvider, this.provideUserTrackerProvider, this.activityStarterDelegateProvider, this.provideBackgroundExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, this.privacyLoggerProvider, this.keyguardStateControllerImplProvider, this.appOpsControllerImplProvider));
            this.isPMLiteEnabledProvider = DoubleCheck.provider(QSFlagsModule_IsPMLiteEnabledFactory.create(this.featureFlagsProvider, this.globalSettingsImplProvider));
        }

        public BootCompleteCacheImpl provideBootCacheImpl() {
            return this.bootCompleteCacheImplProvider.get();
        }

        public ConfigurationController getConfigurationController() {
            return this.provideConfigurationControllerProvider.get();
        }

        public ContextComponentHelper getContextComponentHelper() {
            return this.contextComponentResolverProvider.get();
        }

        public Dependency createDependency() {
            return this.dependencyProvider2.get();
        }

        public DumpManager createDumpManager() {
            return this.dumpManagerProvider.get();
        }

        public InitController getInitController() {
            return this.initControllerProvider.get();
        }

        public void inject(SystemUIAppComponentFactory systemUIAppComponentFactory) {
            injectSystemUIAppComponentFactory(systemUIAppComponentFactory);
        }

        public StatusBar getStatusBar() {
            return this.provideStatusBarProvider.get();
        }

        public void inject(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter) {
            injectDesktopStatusBarNotificationPresenter(desktopStatusBarNotificationPresenter);
        }

        private SystemUIAppComponentFactory injectSystemUIAppComponentFactory(SystemUIAppComponentFactory systemUIAppComponentFactory) {
            SystemUIAppComponentFactory_MembersInjector.injectMComponentHelper(systemUIAppComponentFactory, this.contextComponentResolverProvider.get());
            return systemUIAppComponentFactory;
        }

        private DesktopStatusBarNotificationPresenter injectDesktopStatusBarNotificationPresenter(DesktopStatusBarNotificationPresenter desktopStatusBarNotificationPresenter) {
            DesktopStatusBarNotificationPresenter_MembersInjector.injectMViewHierarchyManager(desktopStatusBarNotificationPresenter, this.provideNotificationViewHierarchyManagerProvider.get());
            DesktopStatusBarNotificationPresenter_MembersInjector.injectMEntryManager(desktopStatusBarNotificationPresenter, this.provideNotificationEntryManagerProvider.get());
            DesktopStatusBarNotificationPresenter_MembersInjector.injectMLockscreenUserManager(desktopStatusBarNotificationPresenter, this.notificationLockscreenUserManagerImplProvider.get());
            return desktopStatusBarNotificationPresenter;
        }

        private final class ExpandableNotificationRowComponentBuilder implements ExpandableNotificationRowComponent.Builder {
            private ExpandableNotificationRow expandableNotificationRow;
            private NotificationListContainer listContainer;
            private NotificationEntry notificationEntry;
            private ExpandableNotificationRow.OnExpandClickListener onExpandClickListener;

            private ExpandableNotificationRowComponentBuilder() {
            }

            public ExpandableNotificationRowComponentBuilder expandableNotificationRow(ExpandableNotificationRow expandableNotificationRow2) {
                this.expandableNotificationRow = (ExpandableNotificationRow) Preconditions.checkNotNull(expandableNotificationRow2);
                return this;
            }

            public ExpandableNotificationRowComponentBuilder notificationEntry(NotificationEntry notificationEntry2) {
                this.notificationEntry = (NotificationEntry) Preconditions.checkNotNull(notificationEntry2);
                return this;
            }

            public ExpandableNotificationRowComponentBuilder onExpandClickListener(ExpandableNotificationRow.OnExpandClickListener onExpandClickListener2) {
                this.onExpandClickListener = (ExpandableNotificationRow.OnExpandClickListener) Preconditions.checkNotNull(onExpandClickListener2);
                return this;
            }

            public ExpandableNotificationRowComponentBuilder listContainer(NotificationListContainer notificationListContainer) {
                this.listContainer = (NotificationListContainer) Preconditions.checkNotNull(notificationListContainer);
                return this;
            }

            public ExpandableNotificationRowComponent build() {
                Preconditions.checkBuilderRequirement(this.expandableNotificationRow, ExpandableNotificationRow.class);
                Preconditions.checkBuilderRequirement(this.notificationEntry, NotificationEntry.class);
                Preconditions.checkBuilderRequirement(this.onExpandClickListener, ExpandableNotificationRow.OnExpandClickListener.class);
                Preconditions.checkBuilderRequirement(this.listContainer, NotificationListContainer.class);
                return new ExpandableNotificationRowComponentImpl(this.expandableNotificationRow, this.notificationEntry, this.onExpandClickListener, this.listContainer);
            }
        }

        private final class ExpandableNotificationRowComponentImpl implements ExpandableNotificationRowComponent {
            private Provider<ActivatableNotificationViewController> activatableNotificationViewControllerProvider;
            private Provider<ExpandableNotificationRowController> expandableNotificationRowControllerProvider;
            private Provider<ExpandableNotificationRow> expandableNotificationRowProvider;
            private Provider<ExpandableOutlineViewController> expandableOutlineViewControllerProvider;
            private Provider<ExpandableViewController> expandableViewControllerProvider;
            private Provider<NotificationTapHelper.Factory> factoryProvider;
            private Provider<NotificationListContainer> listContainerProvider;
            private Provider<NotificationEntry> notificationEntryProvider;
            private Provider<ExpandableNotificationRow.OnExpandClickListener> onExpandClickListenerProvider;
            private Provider<String> provideAppNameProvider;
            private Provider<String> provideNotificationKeyProvider;
            private Provider<StatusBarNotification> provideStatusBarNotificationProvider;

            private ExpandableNotificationRowComponentImpl(ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry, ExpandableNotificationRow.OnExpandClickListener onExpandClickListener, NotificationListContainer notificationListContainer) {
                initialize(expandableNotificationRow, notificationEntry, onExpandClickListener, notificationListContainer);
            }

            private void initialize(ExpandableNotificationRow expandableNotificationRow, NotificationEntry notificationEntry, ExpandableNotificationRow.OnExpandClickListener onExpandClickListener, NotificationListContainer notificationListContainer) {
                this.expandableNotificationRowProvider = InstanceFactory.create(expandableNotificationRow);
                this.listContainerProvider = InstanceFactory.create(notificationListContainer);
                this.factoryProvider = NotificationTapHelper_Factory_Factory.create(DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.provideMainDelayableExecutorProvider);
                ExpandableViewController_Factory create = ExpandableViewController_Factory.create(this.expandableNotificationRowProvider);
                this.expandableViewControllerProvider = create;
                ExpandableOutlineViewController_Factory create2 = ExpandableOutlineViewController_Factory.create(this.expandableNotificationRowProvider, create);
                this.expandableOutlineViewControllerProvider = create2;
                this.activatableNotificationViewControllerProvider = ActivatableNotificationViewController_Factory.create(this.expandableNotificationRowProvider, this.factoryProvider, create2, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.falsingCollectorImplProvider);
                Factory create3 = InstanceFactory.create(notificationEntry);
                this.notificationEntryProvider = create3;
                this.provideStatusBarNotificationProvider = C1638xc255c3ca.create(create3);
                this.provideAppNameProvider = C1636x3e2d0aca.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.provideStatusBarNotificationProvider);
                this.provideNotificationKeyProvider = C1637xdc9a80a2.create(this.provideStatusBarNotificationProvider);
                this.onExpandClickListenerProvider = InstanceFactory.create(onExpandClickListener);
                this.expandableNotificationRowControllerProvider = DoubleCheck.provider(ExpandableNotificationRowController_Factory.create(this.expandableNotificationRowProvider, this.listContainerProvider, this.activatableNotificationViewControllerProvider, DesktopSysUIComponentImpl.this.provideNotificationMediaManagerProvider, DesktopSysUIComponentImpl.this.providePluginManagerProvider, DesktopSysUIComponentImpl.this.bindSystemClockProvider, this.provideAppNameProvider, this.provideNotificationKeyProvider, DesktopSysUIComponentImpl.this.keyguardBypassControllerProvider, DesktopSysUIComponentImpl.this.provideGroupMembershipManagerProvider, DesktopSysUIComponentImpl.this.provideGroupExpansionManagerProvider, DesktopSysUIComponentImpl.this.rowContentBindStageProvider, DesktopSysUIComponentImpl.this.provideNotificationLoggerProvider, DesktopSysUIComponentImpl.this.provideHeadsUpManagerPhoneProvider, this.onExpandClickListenerProvider, DesktopSysUIComponentImpl.this.desktopStatusBarStateControllerImplProvider, DesktopSysUIComponentImpl.this.provideNotificationGutsManagerProvider, DesktopSysUIComponentImpl.this.provideAllowNotificationLongPressProvider, DesktopSysUIComponentImpl.this.provideOnUserInteractionCallbackProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.falsingCollectorImplProvider, DesktopSysUIComponentImpl.this.peopleNotificationIdentifierImplProvider, DesktopSysUIComponentImpl.this.provideBubblesManagerProvider));
            }

            public ExpandableNotificationRowController getExpandableNotificationRowController() {
                return this.expandableNotificationRowControllerProvider.get();
            }
        }

        private final class KeyguardStatusViewComponentFactory implements KeyguardStatusViewComponent.Factory {
            private KeyguardStatusViewComponentFactory() {
            }

            public KeyguardStatusViewComponent build(KeyguardStatusView keyguardStatusView) {
                Preconditions.checkNotNull(keyguardStatusView);
                return new KeyguardStatusViewComponentImpl(keyguardStatusView);
            }
        }

        private final class KeyguardStatusViewComponentImpl implements KeyguardStatusViewComponent {
            private Provider<KeyguardClockSwitch> getKeyguardClockSwitchProvider;
            private Provider<KeyguardSliceView> getKeyguardSliceViewProvider;
            private Provider<KeyguardSliceViewController> keyguardSliceViewControllerProvider;
            private final KeyguardStatusView presentation;
            private Provider<KeyguardStatusView> presentationProvider;

            private KeyguardStatusViewComponentImpl(KeyguardStatusView keyguardStatusView) {
                this.presentation = keyguardStatusView;
                initialize(keyguardStatusView);
            }

            private KeyguardClockSwitch keyguardClockSwitch() {
                return KeyguardStatusViewModule_GetKeyguardClockSwitchFactory.getKeyguardClockSwitch(this.presentation);
            }

            private void initialize(KeyguardStatusView keyguardStatusView) {
                Factory create = InstanceFactory.create(keyguardStatusView);
                this.presentationProvider = create;
                KeyguardStatusViewModule_GetKeyguardClockSwitchFactory create2 = KeyguardStatusViewModule_GetKeyguardClockSwitchFactory.create(create);
                this.getKeyguardClockSwitchProvider = create2;
                KeyguardStatusViewModule_GetKeyguardSliceViewFactory create3 = KeyguardStatusViewModule_GetKeyguardSliceViewFactory.create(create2);
                this.getKeyguardSliceViewProvider = create3;
                this.keyguardSliceViewControllerProvider = DoubleCheck.provider(KeyguardSliceViewController_Factory.create(create3, DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DesktopSysUIComponentImpl.this.dumpManagerProvider));
            }

            public KeyguardClockSwitchController getKeyguardClockSwitchController() {
                return new KeyguardClockSwitchController(keyguardClockSwitch(), (StatusBarStateController) DesktopSysUIComponentImpl.this.desktopStatusBarStateControllerImplProvider.get(), (SysuiColorExtractor) DesktopSysUIComponentImpl.this.sysuiColorExtractorProvider.get(), (ClockManager) DesktopSysUIComponentImpl.this.clockManagerProvider.get(), this.keyguardSliceViewControllerProvider.get(), (NotificationIconAreaController) DesktopSysUIComponentImpl.this.notificationIconAreaControllerProvider.get(), (BroadcastDispatcher) DesktopSysUIComponentImpl.this.providesBroadcastDispatcherProvider.get(), (BatteryController) DesktopSysUIComponentImpl.this.provideBatteryControllerProvider.get(), (KeyguardUpdateMonitor) DesktopSysUIComponentImpl.this.keyguardUpdateMonitorProvider.get(), (KeyguardBypassController) DesktopSysUIComponentImpl.this.keyguardBypassControllerProvider.get(), (LockscreenSmartspaceController) DesktopSysUIComponentImpl.this.lockscreenSmartspaceControllerProvider.get(), (KeyguardUnlockAnimationController) DesktopSysUIComponentImpl.this.keyguardUnlockAnimationControllerProvider.get(), (SmartspaceTransitionController) DesktopSysUIComponentImpl.this.provideSmartspaceTransitionControllerProvider.get());
            }

            public KeyguardStatusViewController getKeyguardStatusViewController() {
                return new KeyguardStatusViewController(this.presentation, this.keyguardSliceViewControllerProvider.get(), getKeyguardClockSwitchController(), (KeyguardStateController) DesktopSysUIComponentImpl.this.keyguardStateControllerImplProvider.get(), (KeyguardUpdateMonitor) DesktopSysUIComponentImpl.this.keyguardUpdateMonitorProvider.get(), (ConfigurationController) DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider.get(), (DozeParameters) DesktopSysUIComponentImpl.this.dozeParametersProvider.get(), (KeyguardUnlockAnimationController) DesktopSysUIComponentImpl.this.keyguardUnlockAnimationControllerProvider.get(), (SmartspaceTransitionController) DesktopSysUIComponentImpl.this.provideSmartspaceTransitionControllerProvider.get(), (UnlockedScreenOffAnimationController) DesktopSysUIComponentImpl.this.unlockedScreenOffAnimationControllerProvider.get());
            }
        }

        private final class SectionHeaderControllerSubcomponentBuilder implements SectionHeaderControllerSubcomponent.Builder {
            private String clickIntentAction;
            private Integer headerText;
            private String nodeLabel;

            private SectionHeaderControllerSubcomponentBuilder() {
            }

            public SectionHeaderControllerSubcomponentBuilder nodeLabel(String str) {
                this.nodeLabel = (String) Preconditions.checkNotNull(str);
                return this;
            }

            public SectionHeaderControllerSubcomponentBuilder headerText(int i) {
                this.headerText = (Integer) Preconditions.checkNotNull(Integer.valueOf(i));
                return this;
            }

            public SectionHeaderControllerSubcomponentBuilder clickIntentAction(String str) {
                this.clickIntentAction = (String) Preconditions.checkNotNull(str);
                return this;
            }

            public SectionHeaderControllerSubcomponent build() {
                Class<String> cls = String.class;
                Preconditions.checkBuilderRequirement(this.nodeLabel, cls);
                Preconditions.checkBuilderRequirement(this.headerText, Integer.class);
                Preconditions.checkBuilderRequirement(this.clickIntentAction, cls);
                return new SectionHeaderControllerSubcomponentImpl(this.nodeLabel, this.headerText, this.clickIntentAction);
            }
        }

        private final class SectionHeaderControllerSubcomponentImpl implements SectionHeaderControllerSubcomponent {
            private Provider<String> clickIntentActionProvider;
            private Provider<Integer> headerTextProvider;
            private Provider<String> nodeLabelProvider;
            private Provider<SectionHeaderNodeControllerImpl> sectionHeaderNodeControllerImplProvider;

            private SectionHeaderControllerSubcomponentImpl(String str, Integer num, String str2) {
                initialize(str, num, str2);
            }

            private void initialize(String str, Integer num, String str2) {
                this.nodeLabelProvider = InstanceFactory.create(str);
                this.headerTextProvider = InstanceFactory.create(num);
                this.clickIntentActionProvider = InstanceFactory.create(str2);
                this.sectionHeaderNodeControllerImplProvider = DoubleCheck.provider(SectionHeaderNodeControllerImpl_Factory.create(this.nodeLabelProvider, DesktopSysUIComponentImpl.this.providerLayoutInflaterProvider, this.headerTextProvider, DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, this.clickIntentActionProvider));
            }

            public NodeController getNodeController() {
                return this.sectionHeaderNodeControllerImplProvider.get();
            }

            public SectionHeaderController getHeaderController() {
                return this.sectionHeaderNodeControllerImplProvider.get();
            }
        }

        private final class ViewInstanceCreatorFactory implements InjectionInflationController.ViewInstanceCreator.Factory {
            private ViewInstanceCreatorFactory() {
            }

            public InjectionInflationController.ViewInstanceCreator build(Context context, AttributeSet attributeSet) {
                Preconditions.checkNotNull(context);
                Preconditions.checkNotNull(attributeSet);
                return new ViewInstanceCreatorImpl(context, attributeSet);
            }
        }

        private final class ViewInstanceCreatorImpl implements InjectionInflationController.ViewInstanceCreator {
            private final AttributeSet attributeSet;
            private final Context context;

            private ViewInstanceCreatorImpl(Context context2, AttributeSet attributeSet2) {
                this.context = context2;
                this.attributeSet = attributeSet2;
            }

            public NotificationStackScrollLayout createNotificationStackScrollLayout() {
                return new NotificationStackScrollLayout(this.context, this.attributeSet, DesktopSysUIComponentImpl.this.notificationSectionsManager(), (GroupMembershipManager) DesktopSysUIComponentImpl.this.provideGroupMembershipManagerProvider.get(), (GroupExpansionManager) DesktopSysUIComponentImpl.this.provideGroupExpansionManagerProvider.get(), (AmbientState) DesktopSysUIComponentImpl.this.ambientStateProvider.get(), (FeatureFlags) DesktopSysUIComponentImpl.this.featureFlagsProvider.get(), (UnlockedScreenOffAnimationController) DesktopSysUIComponentImpl.this.unlockedScreenOffAnimationControllerProvider.get());
            }

            public CliQSPanelNew createCliQSPanelNew() {
                return new CliQSPanelNew(this.context, this.attributeSet, (QSTileHost) DesktopSysUIComponentImpl.this.qSTileHostProvider.get(), (CommandQueue) DesktopSysUIComponentImpl.this.provideCommandQueueProvider.get(), (FalsingManager) DesktopSysUIComponentImpl.this.falsingManagerProxyProvider.get(), (BroadcastDispatcher) DesktopSysUIComponentImpl.this.providesBroadcastDispatcherProvider.get());
            }

            public DesktopNotificationStackScrollLayout createDesktopNotificationStackScrollLayout() {
                return new DesktopNotificationStackScrollLayout(this.context, this.attributeSet, DesktopSysUIComponentImpl.this.notificationSectionsManager(), (GroupMembershipManager) DesktopSysUIComponentImpl.this.provideGroupMembershipManagerProvider.get(), (GroupExpansionManager) DesktopSysUIComponentImpl.this.provideGroupExpansionManagerProvider.get(), (AmbientState) DesktopSysUIComponentImpl.this.ambientStateProvider.get(), (FeatureFlags) DesktopSysUIComponentImpl.this.featureFlagsProvider.get());
            }
        }

        private final class NotificationShelfComponentBuilder implements NotificationShelfComponent.Builder {
            private NotificationShelf notificationShelf;

            private NotificationShelfComponentBuilder() {
            }

            public NotificationShelfComponentBuilder notificationShelf(NotificationShelf notificationShelf2) {
                this.notificationShelf = (NotificationShelf) Preconditions.checkNotNull(notificationShelf2);
                return this;
            }

            public NotificationShelfComponent build() {
                Preconditions.checkBuilderRequirement(this.notificationShelf, NotificationShelf.class);
                return new NotificationShelfComponentImpl(this.notificationShelf);
            }
        }

        private final class NotificationShelfComponentImpl implements NotificationShelfComponent {
            private Provider<ActivatableNotificationViewController> activatableNotificationViewControllerProvider;
            private Provider<ExpandableOutlineViewController> expandableOutlineViewControllerProvider;
            private Provider<ExpandableViewController> expandableViewControllerProvider;
            private Provider<NotificationTapHelper.Factory> factoryProvider;
            private Provider<NotificationShelfController> notificationShelfControllerProvider;
            private Provider<NotificationShelf> notificationShelfProvider;

            private NotificationShelfComponentImpl(NotificationShelf notificationShelf) {
                initialize(notificationShelf);
            }

            private void initialize(NotificationShelf notificationShelf) {
                this.notificationShelfProvider = InstanceFactory.create(notificationShelf);
                this.factoryProvider = NotificationTapHelper_Factory_Factory.create(DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.provideMainDelayableExecutorProvider);
                ExpandableViewController_Factory create = ExpandableViewController_Factory.create(this.notificationShelfProvider);
                this.expandableViewControllerProvider = create;
                ExpandableOutlineViewController_Factory create2 = ExpandableOutlineViewController_Factory.create(this.notificationShelfProvider, create);
                this.expandableOutlineViewControllerProvider = create2;
                ActivatableNotificationViewController_Factory create3 = ActivatableNotificationViewController_Factory.create(this.notificationShelfProvider, this.factoryProvider, create2, DaggerDesktopGlobalRootComponent.this.provideAccessibilityManagerProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.falsingCollectorImplProvider);
                this.activatableNotificationViewControllerProvider = create3;
                this.notificationShelfControllerProvider = DoubleCheck.provider(NotificationShelfController_Factory.create(this.notificationShelfProvider, create3, DesktopSysUIComponentImpl.this.keyguardBypassControllerProvider, DesktopSysUIComponentImpl.this.statusBarStateControllerImplProvider));
            }

            public NotificationShelfController getNotificationShelfController() {
                return this.notificationShelfControllerProvider.get();
            }
        }

        private final class DesktopStatusBarComponentBuilder implements DesktopStatusBarComponent.Builder {
            private DesktopStatusBarComponentBuilder() {
            }

            public DesktopStatusBarComponent build() {
                return new DesktopStatusBarComponentImpl();
            }
        }

        private final class DesktopStatusBarComponentImpl implements DesktopStatusBarComponent {
            private Provider builderProvider;
            private Provider<DesktopHeadsUpController> desktopHeadsUpControllerProvider;
            private Provider<DesktopNotificationStackScrollLayoutController> desktopNotificationStackScrollLayoutControllerProvider;

            private DesktopStatusBarComponentImpl() {
                initialize();
            }

            private void initialize() {
                this.builderProvider = NotificationSwipeHelper_Builder_Factory.create(DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, DaggerDesktopGlobalRootComponent.this.provideViewConfigurationProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider);
                this.desktopHeadsUpControllerProvider = DoubleCheck.provider(DesktopHeadsUpController_Factory.create(DaggerDesktopGlobalRootComponent.this.provideDisplayIdProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DesktopSysUIComponentImpl.this.provideNotificationInterruptStateProvider, DesktopSysUIComponentImpl.this.deviceProvisionedControllerImplProvider, DesktopSysUIComponentImpl.this.notificationRowBinderImplProvider, DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider, DesktopSysUIComponentImpl.this.notificationGroupManagerLegacyProvider, DesktopSysUIComponentImpl.this.notifBindPipelineProvider, DesktopSysUIComponentImpl.this.provideNotificationListenerProvider));
                this.desktopNotificationStackScrollLayoutControllerProvider = DoubleCheck.provider(DesktopNotificationStackScrollLayoutController_Factory.create(DesktopSysUIComponentImpl.this.provideAllowNotificationLongPressProvider, DesktopSysUIComponentImpl.this.provideHeadsUpManagerPhoneProvider, DesktopSysUIComponentImpl.this.notificationRoundnessManagerProvider, DesktopSysUIComponentImpl.this.dynamicPrivacyControllerProvider, DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider, DesktopSysUIComponentImpl.this.zenModeControllerImplProvider, DesktopSysUIComponentImpl.this.notificationLockscreenUserManagerImplProvider, DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, this.builderProvider, DesktopSysUIComponentImpl.this.provideStatusBarProvider, DesktopSysUIComponentImpl.this.notificationGroupManagerLegacyProvider, DesktopSysUIComponentImpl.this.provideGroupExpansionManagerProvider, DesktopSysUIComponentImpl.this.providesSilentHeaderControllerProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider, DesktopSysUIComponentImpl.this.notifPipelineProvider, DesktopSysUIComponentImpl.this.notifCollectionProvider, DesktopSysUIComponentImpl.this.provideNotificationEntryManagerProvider, DaggerDesktopGlobalRootComponent.this.provideIStatusBarServiceProvider, DesktopSysUIComponentImpl.this.foregroundServiceDismissalFeatureControllerProvider, DesktopSysUIComponentImpl.this.foregroundServiceSectionControllerProvider, DesktopSysUIComponentImpl.this.providerLayoutInflaterProvider, DesktopSysUIComponentImpl.this.provideNotificationRemoteInputManagerProvider, this.desktopHeadsUpControllerProvider, DesktopSysUIComponentImpl.this.provideVisualStabilityManagerProvider));
            }

            public DesktopNotificationStackScrollLayoutController getNotificationStackScrollLayoutController() {
                return this.desktopNotificationStackScrollLayoutControllerProvider.get();
            }

            public DesktopHeadsUpController getHeadsUpController() {
                return this.desktopHeadsUpControllerProvider.get();
            }
        }

        private final class DozeComponentFactory implements DozeComponent.Builder {
            private DozeComponentFactory() {
            }

            public DozeComponent build(DozeMachine.Service service) {
                Preconditions.checkNotNull(service);
                return new DozeComponentImpl(service);
            }
        }

        private final class DozeComponentImpl implements DozeComponent {
            private Provider<DozeAuthRemover> dozeAuthRemoverProvider;
            private Provider<DozeDockHandler> dozeDockHandlerProvider;
            private Provider<DozeFalsingManagerAdapter> dozeFalsingManagerAdapterProvider;
            private Provider<DozeMachine> dozeMachineProvider;
            private Provider<DozeMachine.Service> dozeMachineServiceProvider;
            private Provider<DozePauser> dozePauserProvider;
            private Provider<DozeScreenBrightness> dozeScreenBrightnessProvider;
            private Provider<DozeScreenState> dozeScreenStateProvider;
            private Provider<DozeTriggers> dozeTriggersProvider;
            private Provider<DozeUi> dozeUiProvider;
            private Provider<DozeWallpaperState> dozeWallpaperStateProvider;
            private Provider<Optional<Sensor>> providesBrightnessSensorProvider;
            private Provider<DozeMachine.Part[]> providesDozeMachinePartesProvider;
            private Provider<WakeLock> providesDozeWakeLockProvider;
            private Provider<DozeMachine.Service> providesWrappedServiceProvider;
            private Provider<ProximitySensor.ProximityCheck> proximityCheckProvider;

            private DozeComponentImpl(DozeMachine.Service service) {
                initialize(service);
            }

            private void initialize(DozeMachine.Service service) {
                Factory create = InstanceFactory.create(service);
                this.dozeMachineServiceProvider = create;
                this.providesWrappedServiceProvider = DoubleCheck.provider(DozeModule_ProvidesWrappedServiceFactory.create(create, DesktopSysUIComponentImpl.this.dozeServiceHostProvider, DesktopSysUIComponentImpl.this.dozeParametersProvider));
                this.providesDozeWakeLockProvider = DoubleCheck.provider(DozeModule_ProvidesDozeWakeLockFactory.create(DesktopSysUIComponentImpl.this.builderProvider2, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider));
                this.dozePauserProvider = DoubleCheck.provider(DozePauser_Factory.create(DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DaggerDesktopGlobalRootComponent.this.provideAlarmManagerProvider, DesktopSysUIComponentImpl.this.provideAlwaysOnDisplayPolicyProvider));
                this.dozeFalsingManagerAdapterProvider = DoubleCheck.provider(DozeFalsingManagerAdapter_Factory.create(DesktopSysUIComponentImpl.this.falsingCollectorImplProvider));
                this.proximityCheckProvider = ProximitySensor_ProximityCheck_Factory.create(DesktopSysUIComponentImpl.this.proximitySensorProvider, DesktopSysUIComponentImpl.this.provideMainDelayableExecutorProvider);
                this.dozeTriggersProvider = DoubleCheck.provider(DozeTriggers_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DesktopSysUIComponentImpl.this.dozeServiceHostProvider, DesktopSysUIComponentImpl.this.provideAmbientDisplayConfigurationProvider, DesktopSysUIComponentImpl.this.dozeParametersProvider, DesktopSysUIComponentImpl.this.asyncSensorManagerProvider, this.providesDozeWakeLockProvider, DesktopSysUIComponentImpl.this.dockManagerImplProvider, DesktopSysUIComponentImpl.this.proximitySensorProvider, this.proximityCheckProvider, DesktopSysUIComponentImpl.this.dozeLogProvider, DesktopSysUIComponentImpl.this.providesBroadcastDispatcherProvider, DesktopSysUIComponentImpl.this.secureSettingsImplProvider, DesktopSysUIComponentImpl.this.authControllerProvider, DesktopSysUIComponentImpl.this.provideMainDelayableExecutorProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
                this.dozeUiProvider = DoubleCheck.provider(DozeUi_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideAlarmManagerProvider, this.providesDozeWakeLockProvider, DesktopSysUIComponentImpl.this.dozeServiceHostProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DesktopSysUIComponentImpl.this.dozeParametersProvider, DesktopSysUIComponentImpl.this.keyguardUpdateMonitorProvider, DesktopSysUIComponentImpl.this.dozeLogProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DesktopSysUIComponentImpl.this.desktopStatusBarStateControllerImplProvider, DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider));
                this.dozeScreenStateProvider = DoubleCheck.provider(DozeScreenState_Factory.create(this.providesWrappedServiceProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DesktopSysUIComponentImpl.this.dozeServiceHostProvider, DesktopSysUIComponentImpl.this.dozeParametersProvider, this.providesDozeWakeLockProvider));
                this.providesBrightnessSensorProvider = DozeModule_ProvidesBrightnessSensorFactory.create(DesktopSysUIComponentImpl.this.asyncSensorManagerProvider, DaggerDesktopGlobalRootComponent.this.contextProvider);
                this.dozeScreenBrightnessProvider = DoubleCheck.provider(DozeScreenBrightness_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.providesWrappedServiceProvider, DesktopSysUIComponentImpl.this.asyncSensorManagerProvider, this.providesBrightnessSensorProvider, DesktopSysUIComponentImpl.this.dozeServiceHostProvider, DesktopSysUIComponentImpl.this.provideHandlerProvider, DesktopSysUIComponentImpl.this.provideAlwaysOnDisplayPolicyProvider, DesktopSysUIComponentImpl.this.wakefulnessLifecycleProvider, DesktopSysUIComponentImpl.this.dozeParametersProvider));
                this.dozeWallpaperStateProvider = DoubleCheck.provider(DozeWallpaperState_Factory.create(FrameworkServicesModule_ProvideIWallPaperManagerFactory.create(), DesktopSysUIComponentImpl.this.biometricUnlockControllerProvider, DesktopSysUIComponentImpl.this.dozeParametersProvider));
                this.dozeDockHandlerProvider = DoubleCheck.provider(DozeDockHandler_Factory.create(DesktopSysUIComponentImpl.this.provideAmbientDisplayConfigurationProvider, DesktopSysUIComponentImpl.this.dockManagerImplProvider));
                Provider<DozeAuthRemover> provider = DoubleCheck.provider(DozeAuthRemover_Factory.create(DesktopSysUIComponentImpl.this.keyguardUpdateMonitorProvider));
                this.dozeAuthRemoverProvider = provider;
                this.providesDozeMachinePartesProvider = DozeModule_ProvidesDozeMachinePartesFactory.create(this.dozePauserProvider, this.dozeFalsingManagerAdapterProvider, this.dozeTriggersProvider, this.dozeUiProvider, this.dozeScreenStateProvider, this.dozeScreenBrightnessProvider, this.dozeWallpaperStateProvider, this.dozeDockHandlerProvider, provider);
                this.dozeMachineProvider = DoubleCheck.provider(DozeMachine_Factory.create(this.providesWrappedServiceProvider, DesktopSysUIComponentImpl.this.provideAmbientDisplayConfigurationProvider, this.providesDozeWakeLockProvider, DesktopSysUIComponentImpl.this.wakefulnessLifecycleProvider, DesktopSysUIComponentImpl.this.provideBatteryControllerProvider, DesktopSysUIComponentImpl.this.dozeLogProvider, DesktopSysUIComponentImpl.this.dockManagerImplProvider, DesktopSysUIComponentImpl.this.dozeServiceHostProvider, this.providesDozeMachinePartesProvider));
            }

            public DozeMachine getDozeMachine() {
                return this.dozeMachineProvider.get();
            }
        }

        private final class FragmentCreatorFactory implements FragmentService.FragmentCreator.Factory {
            private FragmentCreatorFactory() {
            }

            public FragmentService.FragmentCreator build() {
                return new FragmentCreatorImpl();
            }
        }

        private final class FragmentCreatorImpl implements FragmentService.FragmentCreator {
            private FragmentCreatorImpl() {
            }

            public QSFragment createQSFragment() {
                return new QSFragment((RemoteInputQuickSettingsDisabler) DesktopSysUIComponentImpl.this.remoteInputQuickSettingsDisablerProvider.get(), (InjectionInflationController) DesktopSysUIComponentImpl.this.injectionInflationControllerProvider.get(), (QSTileHost) DesktopSysUIComponentImpl.this.qSTileHostProvider.get(), (StatusBarStateController) DesktopSysUIComponentImpl.this.desktopStatusBarStateControllerImplProvider.get(), (CommandQueue) DesktopSysUIComponentImpl.this.provideCommandQueueProvider.get(), (QSDetailDisplayer) DesktopSysUIComponentImpl.this.qSDetailDisplayerProvider.get(), (MediaHost) DesktopSysUIComponentImpl.this.providesQSMediaHostProvider.get(), (MediaHost) DesktopSysUIComponentImpl.this.providesQuickQSMediaHostProvider.get(), (KeyguardBypassController) DesktopSysUIComponentImpl.this.keyguardBypassControllerProvider.get(), new QSFragmentComponentFactory(), (FeatureFlags) DesktopSysUIComponentImpl.this.featureFlagsProvider.get(), (FalsingManager) DesktopSysUIComponentImpl.this.falsingManagerProxyProvider.get(), (DumpManager) DesktopSysUIComponentImpl.this.dumpManagerProvider.get());
            }

            public CollapsedStatusBarFragment createCollapsedStatusBarFragment() {
                return new CollapsedStatusBarFragment((OngoingCallController) DesktopSysUIComponentImpl.this.provideOngoingCallControllerProvider.get(), (SystemStatusAnimationScheduler) DesktopSysUIComponentImpl.this.systemStatusAnimationSchedulerProvider.get(), (StatusBarLocationPublisher) DesktopSysUIComponentImpl.this.statusBarLocationPublisherProvider.get(), (NotificationIconAreaController) DesktopSysUIComponentImpl.this.notificationIconAreaControllerProvider.get(), (FeatureFlags) DesktopSysUIComponentImpl.this.featureFlagsProvider.get(), (StatusBarIconController) DesktopSysUIComponentImpl.this.statusBarIconControllerImplProvider.get(), (KeyguardStateController) DesktopSysUIComponentImpl.this.keyguardStateControllerImplProvider.get(), (NetworkController) DesktopSysUIComponentImpl.this.networkControllerImplProvider.get(), (StatusBarStateController) DesktopSysUIComponentImpl.this.desktopStatusBarStateControllerImplProvider.get(), (StatusBar) DesktopSysUIComponentImpl.this.provideStatusBarProvider.get(), (CommandQueue) DesktopSysUIComponentImpl.this.provideCommandQueueProvider.get());
            }
        }

        private final class QSFragmentComponentFactory implements QSFragmentComponent.Factory {
            private QSFragmentComponentFactory() {
            }

            public QSFragmentComponent create(QSFragment qSFragment) {
                Preconditions.checkNotNull(qSFragment);
                return new QSFragmentComponentImpl(qSFragment);
            }
        }

        private final class QSFragmentComponentImpl implements QSFragmentComponent {
            private Provider<CarrierTextManager.Builder> builderProvider;
            private Provider<QSCarrierGroupController.Builder> builderProvider2;
            private Provider<DesktopQSFooterViewController> desktopQSFooterViewControllerProvider;
            private Provider factoryProvider;
            private Provider<BrightnessController.Factory> factoryProvider2;
            private Provider<MultiUserSwitchController> multiUserSwitchControllerProvider;
            private Provider<QSPanel> provideQSPanelProvider;
            private Provider<View> provideRootViewProvider;
            private Provider<Context> provideThemedContextProvider;
            private Provider<LayoutInflater> provideThemedLayoutInflaterProvider;
            private Provider<MultiUserSwitch> providesMultiUserSWitchProvider;
            private Provider<QSContainerImpl> providesQSContainerImplProvider;
            private Provider<QSCustomizer> providesQSCutomizerProvider;
            private Provider<QSFooter> providesQSFooterProvider;
            private Provider<QSFooterView> providesQSFooterViewProvider;
            private Provider<QSPrcFixedPanel> providesQSPrcFixedPanelProvider;
            private Provider<QSPrcPanelContainer> providesQSPrcPanelContainerProvider;
            private Provider<View> providesQSSecurityFooterViewProvider;
            private Provider<Boolean> providesQSUsingMediaPlayerProvider;
            private Provider<QuickQSPanel> providesQuickQSPanelProvider;
            private Provider<QuickStatusBarHeader> providesQuickStatusBarHeaderProvider;
            private Provider<QSAnimator> qSAnimatorProvider;
            private Provider<QSContainerImplController> qSContainerImplControllerProvider;
            private Provider<QSCustomizerController> qSCustomizerControllerProvider;
            private Provider<QSFooterViewController> qSFooterViewControllerProvider;
            private Provider<QSPanelController> qSPanelControllerProvider;
            private Provider<QSPrcFixedPanelController> qSPrcFixedPanelControllerProvider;
            private Provider<QSPrcPanelContainerController> qSPrcPanelContainerControllerProvider;
            private Provider qSSecurityFooterProvider;
            private Provider<QSFragment> qsFragmentProvider;
            private Provider<QuickQSPanelController> quickQSPanelControllerProvider;
            private Provider quickStatusBarHeaderControllerProvider;
            private Provider<TileAdapter> tileAdapterProvider;
            private Provider<TileQueryHelper> tileQueryHelperProvider;

            private QSFragmentComponentImpl(QSFragment qSFragment) {
                initialize(qSFragment);
            }

            private void initialize(QSFragment qSFragment) {
                Factory create = InstanceFactory.create(qSFragment);
                this.qsFragmentProvider = create;
                QSFragmentModule_ProvideRootViewFactory create2 = QSFragmentModule_ProvideRootViewFactory.create(create);
                this.provideRootViewProvider = create2;
                this.provideQSPanelProvider = QSFragmentModule_ProvideQSPanelFactory.create(create2);
                QSFragmentModule_ProvideThemedContextFactory create3 = QSFragmentModule_ProvideThemedContextFactory.create(this.provideRootViewProvider);
                this.provideThemedContextProvider = create3;
                QSFragmentModule_ProvideThemedLayoutInflaterFactory create4 = QSFragmentModule_ProvideThemedLayoutInflaterFactory.create(create3);
                this.provideThemedLayoutInflaterProvider = create4;
                Provider<View> provider = DoubleCheck.provider(QSFragmentModule_ProvidesQSSecurityFooterViewFactory.create(create4, this.provideQSPanelProvider));
                this.providesQSSecurityFooterViewProvider = provider;
                this.qSSecurityFooterProvider = DoubleCheck.provider(QSSecurityFooter_Factory.create(provider, DesktopSysUIComponentImpl.this.provideUserTrackerProvider, DaggerDesktopGlobalRootComponent.this.provideMainHandlerProvider, DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, DesktopSysUIComponentImpl.this.securityControllerImplProvider, DesktopSysUIComponentImpl.this.provideBgLooperProvider));
                this.providesQSCutomizerProvider = DoubleCheck.provider(QSFragmentModule_ProvidesQSCutomizerFactory.create(this.provideRootViewProvider));
                this.tileQueryHelperProvider = DoubleCheck.provider(TileQueryHelper_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DesktopSysUIComponentImpl.this.provideUserTrackerProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, DesktopSysUIComponentImpl.this.provideBackgroundExecutorProvider));
                this.tileAdapterProvider = DoubleCheck.provider(TileAdapter_Factory.create(this.provideThemedContextProvider, DesktopSysUIComponentImpl.this.qSTileHostProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
                this.qSCustomizerControllerProvider = DoubleCheck.provider(QSCustomizerController_Factory.create(this.providesQSCutomizerProvider, this.tileQueryHelperProvider, DesktopSysUIComponentImpl.this.qSTileHostProvider, this.tileAdapterProvider, DesktopSysUIComponentImpl.this.screenLifecycleProvider, DesktopSysUIComponentImpl.this.keyguardStateControllerImplProvider, DesktopSysUIComponentImpl.this.lightBarControllerProvider, DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
                this.providesQSUsingMediaPlayerProvider = QSFragmentModule_ProvidesQSUsingMediaPlayerFactory.create(DaggerDesktopGlobalRootComponent.this.contextProvider);
                this.factoryProvider = DoubleCheck.provider(QSTileRevealController_Factory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, this.qSCustomizerControllerProvider));
                this.factoryProvider2 = BrightnessController_Factory_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DesktopSysUIComponentImpl.this.providesBroadcastDispatcherProvider);
                this.qSPanelControllerProvider = DoubleCheck.provider(QSPanelController_Factory.create(this.provideQSPanelProvider, this.qSSecurityFooterProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DesktopSysUIComponentImpl.this.qSTileHostProvider, this.qSCustomizerControllerProvider, this.providesQSUsingMediaPlayerProvider, DesktopSysUIComponentImpl.this.providesQSMediaHostProvider, this.factoryProvider, DesktopSysUIComponentImpl.this.dumpManagerProvider, DesktopSysUIComponentImpl.this.provideMetricsLoggerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DesktopSysUIComponentImpl.this.qSLoggerProvider, this.factoryProvider2, DesktopSysUIComponentImpl.this.factoryProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider, DesktopSysUIComponentImpl.this.mediaHierarchyManagerProvider));
                QSFragmentModule_ProvidesQuickStatusBarHeaderFactory create5 = QSFragmentModule_ProvidesQuickStatusBarHeaderFactory.create(this.provideRootViewProvider);
                this.providesQuickStatusBarHeaderProvider = create5;
                QSFragmentModule_ProvidesQuickQSPanelFactory create6 = QSFragmentModule_ProvidesQuickQSPanelFactory.create(create5);
                this.providesQuickQSPanelProvider = create6;
                Provider<QuickQSPanelController> provider2 = DoubleCheck.provider(QuickQSPanelController_Factory.create(create6, DesktopSysUIComponentImpl.this.qSTileHostProvider, this.qSCustomizerControllerProvider, this.providesQSUsingMediaPlayerProvider, DesktopSysUIComponentImpl.this.providesQuickQSMediaHostProvider, DesktopSysUIComponentImpl.this.provideMetricsLoggerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DesktopSysUIComponentImpl.this.qSLoggerProvider, DesktopSysUIComponentImpl.this.dumpManagerProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider));
                this.quickQSPanelControllerProvider = provider2;
                this.qSAnimatorProvider = DoubleCheck.provider(QSAnimator_Factory.create(this.qsFragmentProvider, this.providesQuickQSPanelProvider, this.providesQuickStatusBarHeaderProvider, this.qSPanelControllerProvider, provider2, DesktopSysUIComponentImpl.this.qSTileHostProvider, this.qSSecurityFooterProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DaggerDesktopGlobalRootComponent.this.qSExpansionPathInterpolatorProvider));
                this.providesQSContainerImplProvider = QSFragmentModule_ProvidesQSContainerImplFactory.create(this.provideRootViewProvider);
                this.builderProvider = CarrierTextManager_Builder_Factory.create(DaggerDesktopGlobalRootComponent.this.contextProvider, DaggerDesktopGlobalRootComponent.this.provideResourcesProvider, DaggerDesktopGlobalRootComponent.this.provideWifiManagerProvider, DaggerDesktopGlobalRootComponent.this.provideTelephonyManagerProvider, DesktopSysUIComponentImpl.this.telephonyListenerManagerProvider, DesktopSysUIComponentImpl.this.wakefulnessLifecycleProvider, DaggerDesktopGlobalRootComponent.this.provideMainExecutorProvider, DesktopSysUIComponentImpl.this.provideBackgroundExecutorProvider, DesktopSysUIComponentImpl.this.keyguardUpdateMonitorProvider);
                this.builderProvider2 = QSCarrierGroupController_Builder_Factory.create(DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, DesktopSysUIComponentImpl.this.provideBgHandlerProvider, GlobalConcurrencyModule_ProvideMainLooperFactory.create(), DesktopSysUIComponentImpl.this.networkControllerImplProvider, this.builderProvider, DaggerDesktopGlobalRootComponent.this.contextProvider, DesktopSysUIComponentImpl.this.carrierConfigTrackerProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider, DesktopSysUIComponentImpl.this.subscriptionManagerSlotIndexResolverProvider);
                Provider provider3 = DoubleCheck.provider(QuickStatusBarHeaderController_Factory.create(this.providesQuickStatusBarHeaderProvider, DesktopSysUIComponentImpl.this.privacyItemControllerProvider, DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DesktopSysUIComponentImpl.this.statusBarIconControllerImplProvider, DesktopSysUIComponentImpl.this.provideDemoModeControllerProvider, this.quickQSPanelControllerProvider, this.builderProvider2, DesktopSysUIComponentImpl.this.privacyLoggerProvider, DesktopSysUIComponentImpl.this.sysuiColorExtractorProvider, DesktopSysUIComponentImpl.this.privacyDialogControllerProvider, DaggerDesktopGlobalRootComponent.this.qSExpansionPathInterpolatorProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider));
                this.quickStatusBarHeaderControllerProvider = provider3;
                this.qSContainerImplControllerProvider = DoubleCheck.provider(QSContainerImplController_Factory.create(this.providesQSContainerImplProvider, this.qSPanelControllerProvider, provider3, DesktopSysUIComponentImpl.this.provideConfigurationControllerProvider));
                QSFragmentModule_ProvidesQSFooterViewFactory create7 = QSFragmentModule_ProvidesQSFooterViewFactory.create(this.provideRootViewProvider);
                this.providesQSFooterViewProvider = create7;
                QSFragmentModule_ProvidesMultiUserSWitchFactory create8 = QSFragmentModule_ProvidesMultiUserSWitchFactory.create(create7);
                this.providesMultiUserSWitchProvider = create8;
                this.multiUserSwitchControllerProvider = DoubleCheck.provider(MultiUserSwitchController_Factory.create(create8, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DesktopSysUIComponentImpl.this.userSwitcherControllerProvider, DesktopSysUIComponentImpl.this.qSDetailDisplayerProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider));
                Provider<QSFooterViewController> provider4 = DoubleCheck.provider(QSFooterViewController_Factory.create(this.providesQSFooterViewProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DesktopSysUIComponentImpl.this.userInfoControllerImplProvider, DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, DesktopSysUIComponentImpl.this.deviceProvisionedControllerImplProvider, DesktopSysUIComponentImpl.this.provideUserTrackerProvider, this.qSPanelControllerProvider, this.multiUserSwitchControllerProvider, this.quickQSPanelControllerProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DesktopSysUIComponentImpl.this.provideMetricsLoggerProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.isPMLiteEnabledProvider, DesktopSysUIComponentImpl.this.globalActionsDialogLiteProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
                this.qSFooterViewControllerProvider = provider4;
                this.providesQSFooterProvider = DoubleCheck.provider(QSFragmentModule_ProvidesQSFooterFactory.create(provider4));
                this.desktopQSFooterViewControllerProvider = DoubleCheck.provider(DesktopQSFooterViewController_Factory.create(this.providesQSFooterViewProvider, DaggerDesktopGlobalRootComponent.this.provideUserManagerProvider, DesktopSysUIComponentImpl.this.userInfoControllerImplProvider, DesktopSysUIComponentImpl.this.activityStarterDelegateProvider, DesktopSysUIComponentImpl.this.deviceProvisionedControllerImplProvider, DesktopSysUIComponentImpl.this.provideUserTrackerProvider, this.qSPanelControllerProvider, this.multiUserSwitchControllerProvider, this.quickQSPanelControllerProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DesktopSysUIComponentImpl.this.provideMetricsLoggerProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider));
                QSFragmentModule_ProvidesQSPrcPanelContainerFactory create9 = QSFragmentModule_ProvidesQSPrcPanelContainerFactory.create(this.provideRootViewProvider);
                this.providesQSPrcPanelContainerProvider = create9;
                this.qSPrcPanelContainerControllerProvider = DoubleCheck.provider(QSPrcPanelContainerController_Factory.create(create9, this.qSSecurityFooterProvider, DesktopSysUIComponentImpl.this.tunerServiceImplProvider, DesktopSysUIComponentImpl.this.qSTileHostProvider, this.qSCustomizerControllerProvider, this.providesQSUsingMediaPlayerProvider, DesktopSysUIComponentImpl.this.providesQSMediaHostProvider, this.factoryProvider, DesktopSysUIComponentImpl.this.dumpManagerProvider, DesktopSysUIComponentImpl.this.provideMetricsLoggerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DesktopSysUIComponentImpl.this.qSLoggerProvider, this.factoryProvider2, DesktopSysUIComponentImpl.this.factoryProvider, DesktopSysUIComponentImpl.this.falsingManagerProxyProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider, this.providesQSFooterViewProvider, DesktopSysUIComponentImpl.this.mediaHierarchyManagerProvider));
                QSFragmentModule_ProvidesQSPrcFixedPanelFactory create10 = QSFragmentModule_ProvidesQSPrcFixedPanelFactory.create(this.provideRootViewProvider);
                this.providesQSPrcFixedPanelProvider = create10;
                this.qSPrcFixedPanelControllerProvider = DoubleCheck.provider(QSPrcFixedPanelController_Factory.create(create10, DesktopSysUIComponentImpl.this.qSTileHostProvider, this.qSCustomizerControllerProvider, this.providesQSUsingMediaPlayerProvider, DesktopSysUIComponentImpl.this.providesQuickQSMediaHostProvider, DesktopSysUIComponentImpl.this.provideMetricsLoggerProvider, DaggerDesktopGlobalRootComponent.this.provideUiEventLoggerProvider, DesktopSysUIComponentImpl.this.qSLoggerProvider, DesktopSysUIComponentImpl.this.dumpManagerProvider, DesktopSysUIComponentImpl.this.featureFlagsProvider));
            }

            public QSPanelController getQSPanelController() {
                return this.qSPanelControllerProvider.get();
            }

            public QuickQSPanelController getQuickQSPanelController() {
                return this.quickQSPanelControllerProvider.get();
            }

            public QSAnimator getQSAnimator() {
                return this.qSAnimatorProvider.get();
            }

            public QSContainerImplController getQSContainerImplController() {
                return this.qSContainerImplControllerProvider.get();
            }

            public QSFooter getQSFooter() {
                return this.providesQSFooterProvider.get();
            }

            public QSCustomizerController getQSCustomizerController() {
                return this.qSCustomizerControllerProvider.get();
            }

            public DesktopQSFooterViewController getDesktopQSFooter() {
                return this.desktopQSFooterViewControllerProvider.get();
            }

            public QSPrcPanelContainerController getQSPrcPanelContainerController() {
                return this.qSPrcPanelContainerControllerProvider.get();
            }

            public QSPrcFixedPanelController getQSPrcFixedPanelController() {
                return this.qSPrcFixedPanelControllerProvider.get();
            }
        }
    }
}
