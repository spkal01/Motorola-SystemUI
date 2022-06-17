package com.android.systemui.statusbar.phone;

import android.graphics.RectF;
import android.hardware.display.AmbientDisplayConfiguration;
import android.media.session.MediaSessionLegacyHelper;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$id;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.DragDownHelper;
import com.android.systemui.statusbar.LockscreenShadeTransitionController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.NotificationShadeWindowController;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.InjectionInflationController;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class NotificationShadeWindowViewController {
    private PhoneStatusBarTransitions mBarTransitions;
    /* access modifiers changed from: private */
    public View mBrightnessMirror;
    private final KeyguardBypassController mBypassController;
    private final CommandQueue mCommandQueue;
    private final NotificationWakeUpCoordinator mCoordinator;
    private final NotificationShadeDepthController mDepthController;
    /* access modifiers changed from: private */
    public final DockManager mDockManager;
    /* access modifiers changed from: private */
    public boolean mDoubleTapEnabled;
    private final DozeLog mDozeLog;
    private final DozeParameters mDozeParameters;
    /* access modifiers changed from: private */
    public DragDownHelper mDragDownHelper;
    private final DynamicPrivacyController mDynamicPrivacyController;
    /* access modifiers changed from: private */
    public boolean mExpandAnimationRunning;
    /* access modifiers changed from: private */
    public boolean mExpandingBelowNotch;
    /* access modifiers changed from: private */
    public final FalsingCollector mFalsingCollector;
    /* access modifiers changed from: private */
    public GestureDetector mGestureDetector;
    private final InjectionInflationController mInjectionInflationController;
    /* access modifiers changed from: private */
    public boolean mIsTrackingBarGesture = false;
    private final KeyguardStateController mKeyguardStateController;
    private final LockscreenShadeTransitionController mLockscreenShadeTransitionController;
    private final NotificationEntryManager mNotificationEntryManager;
    private final NotificationLockscreenUserManager mNotificationLockscreenUserManager;
    /* access modifiers changed from: private */
    public final NotificationPanelViewController mNotificationPanelViewController;
    private NotificationShadeWindowController mNotificationShadeWindowController;
    /* access modifiers changed from: private */
    public final NotificationStackScrollLayoutController mNotificationStackScrollLayoutController;
    private final PluginManager mPluginManager;
    private final PulseExpansionHandler mPulseExpansionHandler;
    /* access modifiers changed from: private */
    public StatusBar mService;
    private final ShadeController mShadeController;
    /* access modifiers changed from: private */
    public boolean mSingleTapEnabled;
    /* access modifiers changed from: private */
    public NotificationStackScrollLayout mStackScrollLayout;
    /* access modifiers changed from: private */
    public final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    /* access modifiers changed from: private */
    public final SysuiStatusBarStateController mStatusBarStateController;
    /* access modifiers changed from: private */
    public PhoneStatusBarView mStatusBarView;
    private final SuperStatusBarViewFactory mStatusBarViewFactory;
    private int[] mTempLocation = new int[2];
    private RectF mTempRect = new RectF();
    private boolean mTouchActive;
    /* access modifiers changed from: private */
    public boolean mTouchCancelled;
    private final TunerService mTunerService;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowView mView;

    public NotificationShadeWindowViewController(InjectionInflationController injectionInflationController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, PulseExpansionHandler pulseExpansionHandler, DynamicPrivacyController dynamicPrivacyController, KeyguardBypassController keyguardBypassController, LockscreenShadeTransitionController lockscreenShadeTransitionController, FalsingCollector falsingCollector, PluginManager pluginManager, TunerService tunerService, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationEntryManager notificationEntryManager, KeyguardStateController keyguardStateController, SysuiStatusBarStateController sysuiStatusBarStateController, DozeLog dozeLog, DozeParameters dozeParameters, CommandQueue commandQueue, ShadeController shadeController, DockManager dockManager, NotificationShadeDepthController notificationShadeDepthController, NotificationShadeWindowView notificationShadeWindowView, NotificationPanelViewController notificationPanelViewController, SuperStatusBarViewFactory superStatusBarViewFactory, NotificationStackScrollLayoutController notificationStackScrollLayoutController, StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        NotificationShadeWindowView notificationShadeWindowView2 = notificationShadeWindowView;
        this.mInjectionInflationController = injectionInflationController;
        this.mCoordinator = notificationWakeUpCoordinator;
        this.mPulseExpansionHandler = pulseExpansionHandler;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mBypassController = keyguardBypassController;
        this.mLockscreenShadeTransitionController = lockscreenShadeTransitionController;
        this.mFalsingCollector = falsingCollector;
        this.mPluginManager = pluginManager;
        this.mTunerService = tunerService;
        this.mNotificationLockscreenUserManager = notificationLockscreenUserManager;
        this.mNotificationEntryManager = notificationEntryManager;
        this.mKeyguardStateController = keyguardStateController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mDozeLog = dozeLog;
        this.mDozeParameters = dozeParameters;
        this.mCommandQueue = commandQueue;
        this.mView = notificationShadeWindowView2;
        this.mShadeController = shadeController;
        this.mDockManager = dockManager;
        this.mNotificationPanelViewController = notificationPanelViewController;
        this.mDepthController = notificationShadeDepthController;
        this.mStatusBarViewFactory = superStatusBarViewFactory;
        this.mNotificationStackScrollLayoutController = notificationStackScrollLayoutController;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mBrightnessMirror = notificationShadeWindowView2.findViewById(R$id.brightness_mirror_container);
    }

    public void setupExpandedStatusBar() {
        this.mStackScrollLayout = (NotificationStackScrollLayout) this.mView.findViewById(R$id.notification_stack_scroller);
        this.mTunerService.addTunable(new NotificationShadeWindowViewController$$ExternalSyntheticLambda0(this), "doze_pulse_on_double_tap", "doze_tap_gesture");
        this.mGestureDetector = new GestureDetector(this.mView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (!NotificationShadeWindowViewController.this.mSingleTapEnabled || NotificationShadeWindowViewController.this.mDockManager.isDocked()) {
                    return false;
                }
                NotificationShadeWindowViewController.this.mService.wakeUpIfDozing(SystemClock.uptimeMillis(), NotificationShadeWindowViewController.this.mView, "SINGLE_TAP");
                return true;
            }

            public boolean onDoubleTap(MotionEvent motionEvent) {
                if (!NotificationShadeWindowViewController.this.mDoubleTapEnabled && !NotificationShadeWindowViewController.this.mSingleTapEnabled) {
                    return false;
                }
                NotificationShadeWindowViewController.this.mService.wakeUpIfDozing(SystemClock.uptimeMillis(), NotificationShadeWindowViewController.this.mView, "DOUBLE_TAP");
                return true;
            }
        });
        this.mView.setInteractionEventHandler(new NotificationShadeWindowView.InteractionEventHandler() {
            public Boolean handleDispatchTouchEvent(MotionEvent motionEvent) {
                if (NotificationShadeWindowViewController.this.mStatusBarView == null) {
                    Log.w("NotifShadeWindowVC", "Ignoring touch while statusBarView not yet set.");
                    return Boolean.FALSE;
                }
                boolean z = motionEvent.getActionMasked() == 0;
                boolean z2 = motionEvent.getActionMasked() == 1;
                boolean z3 = motionEvent.getActionMasked() == 3;
                boolean access$600 = NotificationShadeWindowViewController.this.mExpandingBelowNotch;
                if (z2 || z3) {
                    boolean unused = NotificationShadeWindowViewController.this.mExpandingBelowNotch = false;
                }
                if (!z3 && NotificationShadeWindowViewController.this.mService.shouldIgnoreTouch()) {
                    return Boolean.FALSE;
                }
                if (z) {
                    NotificationShadeWindowViewController.this.setTouchActive(true);
                    boolean unused2 = NotificationShadeWindowViewController.this.mTouchCancelled = false;
                } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
                    NotificationShadeWindowViewController.this.setTouchActive(false);
                }
                if (NotificationShadeWindowViewController.this.mTouchCancelled || NotificationShadeWindowViewController.this.mExpandAnimationRunning) {
                    return Boolean.FALSE;
                }
                NotificationShadeWindowViewController.this.mFalsingCollector.onTouchEvent(motionEvent);
                NotificationShadeWindowViewController.this.mGestureDetector.onTouchEvent(motionEvent);
                NotificationShadeWindowViewController.this.mStatusBarKeyguardViewManager.onTouch(motionEvent);
                if (NotificationShadeWindowViewController.this.mBrightnessMirror != null && NotificationShadeWindowViewController.this.mBrightnessMirror.getVisibility() == 0 && motionEvent.getActionMasked() == 5) {
                    return Boolean.FALSE;
                }
                if (z) {
                    NotificationShadeWindowViewController.this.mNotificationStackScrollLayoutController.closeControlsIfOutsideTouch(motionEvent);
                }
                if (NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing()) {
                    NotificationShadeWindowViewController.this.mService.mDozeScrimController.extendPulse();
                }
                if (z && motionEvent.getY() >= ((float) NotificationShadeWindowViewController.this.mView.getBottom())) {
                    boolean unused3 = NotificationShadeWindowViewController.this.mExpandingBelowNotch = true;
                    access$600 = true;
                }
                if (access$600) {
                    return Boolean.valueOf(NotificationShadeWindowViewController.this.mStatusBarView.dispatchTouchEvent(motionEvent));
                }
                if (!NotificationShadeWindowViewController.this.mIsTrackingBarGesture && z && NotificationShadeWindowViewController.this.mNotificationPanelViewController.isFullyCollapsed()) {
                    float rawX = motionEvent.getRawX();
                    float rawY = motionEvent.getRawY();
                    NotificationShadeWindowViewController notificationShadeWindowViewController = NotificationShadeWindowViewController.this;
                    if (!notificationShadeWindowViewController.isIntersecting(notificationShadeWindowViewController.mStatusBarView, rawX, rawY)) {
                        return null;
                    }
                    if (!NotificationShadeWindowViewController.this.mService.isSameStatusBarState(0)) {
                        return Boolean.TRUE;
                    }
                    boolean unused4 = NotificationShadeWindowViewController.this.mIsTrackingBarGesture = true;
                    return Boolean.valueOf(NotificationShadeWindowViewController.this.mStatusBarView.dispatchTouchEvent(motionEvent));
                } else if (!NotificationShadeWindowViewController.this.mIsTrackingBarGesture) {
                    return null;
                } else {
                    boolean dispatchTouchEvent = NotificationShadeWindowViewController.this.mStatusBarView.dispatchTouchEvent(motionEvent);
                    if (z2 || z3) {
                        boolean unused5 = NotificationShadeWindowViewController.this.mIsTrackingBarGesture = false;
                    }
                    return Boolean.valueOf(dispatchTouchEvent);
                }
            }

            public void dispatchTouchEventComplete() {
                NotificationShadeWindowViewController.this.mFalsingCollector.onMotionEventComplete();
            }

            public boolean shouldInterceptTouchEvent(MotionEvent motionEvent) {
                if (NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing() && !NotificationShadeWindowViewController.this.mService.isPulsing() && !NotificationShadeWindowViewController.this.mDockManager.isDocked()) {
                    return true;
                }
                if (!NotificationShadeWindowViewController.this.mNotificationPanelViewController.isFullyExpanded() || !NotificationShadeWindowViewController.this.mDragDownHelper.isDragDownEnabled() || NotificationShadeWindowViewController.this.mService.isBouncerShowing() || NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing()) {
                    return false;
                }
                return NotificationShadeWindowViewController.this.mDragDownHelper.onInterceptTouchEvent(motionEvent);
            }

            public void didIntercept(MotionEvent motionEvent) {
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setAction(3);
                NotificationShadeWindowViewController.this.mStackScrollLayout.onInterceptTouchEvent(obtain);
                NotificationShadeWindowViewController.this.mNotificationPanelViewController.getView().onInterceptTouchEvent(obtain);
                obtain.recycle();
            }

            public boolean handleTouchEvent(MotionEvent motionEvent) {
                boolean z = NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing() ? !NotificationShadeWindowViewController.this.mService.isPulsing() : false;
                return ((!NotificationShadeWindowViewController.this.mDragDownHelper.isDragDownEnabled() || z) && !NotificationShadeWindowViewController.this.mDragDownHelper.isDraggingDown()) ? z : NotificationShadeWindowViewController.this.mDragDownHelper.onTouchEvent(motionEvent);
            }

            public void didNotHandleTouchEvent(MotionEvent motionEvent) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 1 || actionMasked == 3) {
                    NotificationShadeWindowViewController.this.mService.setInteracting(1, false);
                }
            }

            public boolean interceptMediaKey(KeyEvent keyEvent) {
                return NotificationShadeWindowViewController.this.mService.interceptMediaKey(keyEvent);
            }

            public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
                return NotificationShadeWindowViewController.this.mService.dispatchKeyEventPreIme(keyEvent);
            }

            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                boolean z = keyEvent.getAction() == 0;
                int keyCode = keyEvent.getKeyCode();
                if (keyCode != 4) {
                    if (keyCode != 62) {
                        if (keyCode != 82) {
                            if ((keyCode == 24 || keyCode == 25) && NotificationShadeWindowViewController.this.mStatusBarStateController.isDozing()) {
                                MediaSessionLegacyHelper.getHelper(NotificationShadeWindowViewController.this.mView.getContext()).sendVolumeKeyEvent(keyEvent, Integer.MIN_VALUE, true);
                                return true;
                            }
                        } else if (!z) {
                            return NotificationShadeWindowViewController.this.mService.onMenuPressed();
                        }
                    } else if (!z) {
                        return NotificationShadeWindowViewController.this.mService.onSpacePressed();
                    }
                    return false;
                }
                if (!z) {
                    NotificationShadeWindowViewController.this.mService.onBackPressed();
                }
                return true;
            }
        });
        this.mView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            public void onChildViewRemoved(View view, View view2) {
            }

            public void onChildViewAdded(View view, View view2) {
                if (view2.getId() == R$id.brightness_mirror_container) {
                    View unused = NotificationShadeWindowViewController.this.mBrightnessMirror = view2;
                }
            }
        });
        setDragDownHelper(this.mLockscreenShadeTransitionController.getTouchHelper());
        this.mDepthController.setRoot(this.mView);
        this.mNotificationPanelViewController.addExpansionListener(this.mDepthController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupExpandedStatusBar$0(String str, String str2) {
        AmbientDisplayConfiguration ambientDisplayConfiguration = new AmbientDisplayConfiguration(this.mView.getContext());
        str.hashCode();
        if (str.equals("doze_tap_gesture")) {
            this.mSingleTapEnabled = ambientDisplayConfiguration.tapGestureEnabled(-2);
        } else if (str.equals("doze_pulse_on_double_tap")) {
            this.mDoubleTapEnabled = ambientDisplayConfiguration.doubleTapGestureEnabled(-2);
        }
    }

    public void setTouchActive(boolean z) {
        this.mTouchActive = z;
    }

    public void cancelCurrentTouch() {
        if (this.mTouchActive) {
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            obtain.setSource(4098);
            this.mView.dispatchTouchEvent(obtain);
            obtain.recycle();
            this.mTouchCancelled = true;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("  mExpandAnimationRunning=");
        printWriter.println(this.mExpandAnimationRunning);
        printWriter.print("  mTouchCancelled=");
        printWriter.println(this.mTouchCancelled);
        printWriter.print("  mTouchActive=");
        printWriter.println(this.mTouchActive);
    }

    public void setExpandAnimationRunning(boolean z) {
        if (this.mExpandAnimationRunning != z) {
            this.mExpandAnimationRunning = z;
            this.mNotificationShadeWindowController.setLaunchingActivity(z);
        }
    }

    public void cancelExpandHelper() {
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScrollLayout;
        if (notificationStackScrollLayout != null) {
            notificationStackScrollLayout.cancelExpandHelper();
        }
    }

    public PhoneStatusBarTransitions getBarTransitions() {
        return this.mBarTransitions;
    }

    public void setStatusBarView(PhoneStatusBarView phoneStatusBarView) {
        SuperStatusBarViewFactory superStatusBarViewFactory;
        this.mStatusBarView = phoneStatusBarView;
        if (phoneStatusBarView != null && (superStatusBarViewFactory = this.mStatusBarViewFactory) != null) {
            this.mBarTransitions = new PhoneStatusBarTransitions(phoneStatusBarView, superStatusBarViewFactory.getStatusBarWindowView().findViewById(R$id.status_bar_container));
        }
    }

    public void setService(StatusBar statusBar, NotificationShadeWindowController notificationShadeWindowController) {
        this.mService = statusBar;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setDragDownHelper(DragDownHelper dragDownHelper) {
        this.mDragDownHelper = dragDownHelper;
    }

    /* access modifiers changed from: private */
    public boolean isIntersecting(View view, float f, float f2) {
        int[] locationOnScreen = view.getLocationOnScreen();
        this.mTempLocation = locationOnScreen;
        this.mTempRect.set((float) locationOnScreen[0], (float) locationOnScreen[1], (float) (locationOnScreen[0] + view.getWidth()), (float) (this.mTempLocation[1] + view.getHeight()));
        return this.mTempRect.contains(f, f2);
    }
}
