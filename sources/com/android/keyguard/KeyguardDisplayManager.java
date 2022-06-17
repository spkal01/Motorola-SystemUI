package com.android.keyguard;

import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.dagger.KeyguardStatusViewComponent;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.statusbar.CommandQueue;
import com.motorola.internal.app.MotoDesktopManager;
import com.motorola.taskbar.MotoTaskBarController;
import dagger.Lazy;
import java.util.concurrent.Executor;

public class KeyguardDisplayManager implements PtKDMCallback, CommandQueue.Callbacks {
    /* access modifiers changed from: private */
    public static boolean DEBUG = KeyguardConstants.DEBUG;
    private CommandQueue mCommandQueue;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentFailedCount = 0;
    private final SparseArray<DisplayInfo> mDisplayInfos = new SparseArray<>();
    private final DisplayManager.DisplayListener mDisplayListener;
    private final SparseArray<DisplayMetrics> mDisplayMetricses = new SparseArray<>();
    private final DisplayManager mDisplayService;
    private boolean mIsDozing = false;
    private final KeyguardStatusViewComponent.Factory mKeyguardStatusViewComponentFactory;
    private KeyguardUpdateMonitorCallback mKvCallback = new KeyguardUpdateMonitorCallback() {
        public void onKeyguardShowingChanged(boolean z) {
            if (!z) {
                int unused = KeyguardDisplayManager.this.mCurrentFailedCount = 0;
                boolean unused2 = KeyguardDisplayManager.this.mOnceLockout = false;
            }
        }
    };
    private MediaRouter mMediaRouter = null;
    private final MediaRouter.SimpleCallback mMediaRouterCallback;
    private MotoTaskBarController mMotoTaskBarController;
    private final Lazy<NavigationBarController> mNavigationBarControllerLazy;
    /* access modifiers changed from: private */
    public boolean mOnceLockout;
    private final SparseArray<Presentation> mPresentations = new SparseArray<>();
    /* access modifiers changed from: private */
    public boolean mShowing;
    private final DisplayInfo mTmpDisplayInfo = new DisplayInfo();
    private KeyguardUpdateMonitor mUpdateMonitor;
    private IWindowManager mWindowManagerService;

    public KeyguardDisplayManager(Context context, Lazy<NavigationBarController> lazy, KeyguardStatusViewComponent.Factory factory, CommandQueue commandQueue, Executor executor) {
        C05832 r1 = new DisplayManager.DisplayListener() {
            public void onDisplayAdded(int i) {
                if (KeyguardDisplayManager.DEBUG) {
                    Log.d("KeyguardDisplayManager", "onDisplayAdded: displayId = " + i);
                }
            }

            public void onDisplayChanged(int i) {
                if (!KeyguardDisplayManager.this.isDisplayInfoChange(i) && !KeyguardDisplayManager.this.isDisplayMetricsChange(i) && KeyguardDisplayManager.DEBUG) {
                    Log.d("KeyguardDisplayManager", "RDP: onDisplayChanged - The display info and display metrics aren't change. Return.");
                }
            }

            public void onDisplayRemoved(int i) {
                KeyguardDisplayManager.this.hidePresentation(i);
            }
        };
        this.mDisplayListener = r1;
        this.mMediaRouterCallback = new MediaRouter.SimpleCallback() {
            public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                if (KeyguardDisplayManager.DEBUG) {
                    Log.d("KeyguardDisplayManager", "onRouteSelected: type=" + i + ", info=" + routeInfo);
                }
                KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
                keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
            }

            public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                if (KeyguardDisplayManager.DEBUG) {
                    Log.d("KeyguardDisplayManager", "onRouteUnselected: type=" + i + ", info=" + routeInfo);
                }
                KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
                keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
            }

            public void onRoutePresentationDisplayChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                if (KeyguardDisplayManager.DEBUG) {
                    Log.d("KeyguardDisplayManager", "onRoutePresentationDisplayChanged: info=" + routeInfo);
                }
                KeyguardDisplayManager keyguardDisplayManager = KeyguardDisplayManager.this;
                keyguardDisplayManager.updateDisplays(keyguardDisplayManager.mShowing);
            }
        };
        this.mContext = context;
        this.mNavigationBarControllerLazy = lazy;
        this.mKeyguardStatusViewComponentFactory = factory;
        executor.execute(new KeyguardDisplayManager$$ExternalSyntheticLambda1(this));
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mDisplayService = displayManager;
        displayManager.registerDisplayListener(r1, (Handler) null);
        this.mMotoTaskBarController = (MotoTaskBarController) Dependency.get(MotoTaskBarController.class);
        if (DesktopFeature.isDesktopSupported()) {
            this.mCommandQueue = commandQueue;
            commandQueue.addCallback((CommandQueue.Callbacks) this);
            KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
            this.mUpdateMonitor = keyguardUpdateMonitor;
            keyguardUpdateMonitor.registerCallback(this.mKvCallback);
        }
        this.mWindowManagerService = (IWindowManager) Dependency.get(IWindowManager.class);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mMediaRouter = (MediaRouter) this.mContext.getSystemService(MediaRouter.class);
    }

    private boolean isKeyguardShowable(Display display) {
        if (display == null) {
            if (DEBUG) {
                Log.i("KeyguardDisplayManager", "Cannot show Keyguard on null display");
            }
            return false;
        } else if (display.getDisplayId() == 0) {
            if (DEBUG) {
                Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on the default display");
            }
            return false;
        } else if (display.getDisplayId() == 1) {
            if (DEBUG) {
                Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on the CLI display");
            }
            return false;
        } else {
            display.getDisplayInfo(this.mTmpDisplayInfo);
            DisplayInfo displayInfo = this.mTmpDisplayInfo;
            if ((displayInfo.flags & 4) != 0) {
                if (DEBUG) {
                    Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on a private display");
                }
                return false;
            } else if (displayInfo.displayGroupId == 0 || MotoDesktopManager.isDesktopOrMobileUiMode(display)) {
                try {
                    if (isMotoKeyguardPresentation(display) && !this.mWindowManagerService.shouldShowSystemDecors(display.getDisplayId())) {
                        Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on mirror display");
                        return false;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                if (DEBUG) {
                    Log.i("KeyguardDisplayManager", "Do not show KeyguardPresentation on a non-default group display");
                }
                return false;
            }
        }
    }

    private boolean showPresentation(Display display) {
        Presentation presentation;
        if (!isKeyguardShowable(display)) {
            return false;
        }
        if (DEBUG) {
            Log.i("KeyguardDisplayManager", "Keyguard enabled on display: " + display);
        }
        int displayId = display.getDisplayId();
        if (this.mPresentations.get(displayId) == null) {
            if (isMotoKeyguardPresentation(display)) {
                presentation = createMotoPresentation(display);
            } else {
                presentation = createPresentation(display);
            }
            presentation.setOnDismissListener(new KeyguardDisplayManager$$ExternalSyntheticLambda0(this, presentation, displayId));
            try {
                presentation.show();
            } catch (WindowManager.InvalidDisplayException e) {
                Log.w("KeyguardDisplayManager", "Invalid display:", e);
                presentation = null;
            }
            if (presentation != null) {
                this.mPresentations.append(displayId, presentation);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                this.mDisplayMetricses.append(displayId, displayMetrics);
                DisplayInfo displayInfo = new DisplayInfo();
                display.getDisplayInfo(displayInfo);
                this.mDisplayInfos.append(displayId, displayInfo);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showPresentation$1(Presentation presentation, int i, DialogInterface dialogInterface) {
        if (presentation.equals(this.mPresentations.get(i))) {
            this.mPresentations.remove(i);
            if (DEBUG) {
                Log.d("KeyguardDisplayManager", "RDP: Remove presentation:" + dialogInterface + "  displayId=" + i);
            }
            this.mDisplayMetricses.remove(i);
            this.mDisplayInfos.remove(i);
        }
    }

    /* access modifiers changed from: package-private */
    public KeyguardPresentation createPresentation(Display display) {
        return new KeyguardPresentation(this.mContext, display, this.mKeyguardStatusViewComponentFactory);
    }

    /* access modifiers changed from: package-private */
    public MotoKeyguardPresentation createMotoPresentation(Display display) {
        return new MotoKeyguardPresentation(this.mContext, display, this, this.mIsDozing);
    }

    /* access modifiers changed from: private */
    public void hidePresentation(int i) {
        if (DEBUG) {
            Log.d("KeyguardDisplayManager", "RDP: hidePresentation: displayId=" + i);
        }
        Presentation presentation = this.mPresentations.get(i);
        if (presentation != null) {
            presentation.dismiss();
            this.mPresentations.remove(i);
            this.mDisplayMetricses.remove(i);
            this.mDisplayInfos.remove(i);
        }
    }

    public void show() {
        if (!this.mShowing) {
            if (DEBUG) {
                Log.v("KeyguardDisplayManager", "show");
            }
            MediaRouter mediaRouter = this.mMediaRouter;
            if (mediaRouter != null) {
                mediaRouter.addCallback(4, this.mMediaRouterCallback, 8);
            } else {
                Log.w("KeyguardDisplayManager", "MediaRouter not yet initialized");
            }
            updateDisplays(true);
        }
        this.mShowing = true;
    }

    public void hide() {
        if (this.mShowing) {
            if (DEBUG) {
                Log.v("KeyguardDisplayManager", "hide");
            }
            MediaRouter mediaRouter = this.mMediaRouter;
            if (mediaRouter != null) {
                mediaRouter.removeCallback(this.mMediaRouterCallback);
            }
            updateDisplays(false);
        }
        this.mShowing = false;
    }

    /* access modifiers changed from: protected */
    public boolean updateDisplays(boolean z) {
        boolean z2;
        if (z) {
            z2 = false;
            for (Display display : this.mDisplayService.getDisplays()) {
                updateNavigationBarVisibility(display.getDisplayId(), false);
                z2 |= showPresentation(display);
            }
        } else {
            z2 = this.mPresentations.size() > 0;
            for (int size = this.mPresentations.size() - 1; size >= 0; size--) {
                int keyAt = this.mPresentations.keyAt(size);
                updateNavigationBarVisibility(keyAt, true);
                this.mPresentations.valueAt(size).dismiss();
                this.mPresentations.removeAt(size);
                this.mDisplayMetricses.remove(size);
                this.mDisplayInfos.remove(size);
                if (DEBUG) {
                    Log.d("KeyguardDisplayManager", "RDP: dismiss displayId: " + keyAt);
                }
            }
            if (this.mPresentations.size() <= 0) {
                this.mShowing = false;
            }
        }
        return z2;
    }

    private void updateNavigationBarVisibility(int i, boolean z) {
        if (i != 0 && i != 1) {
            NavigationBarView navigationBarView = this.mNavigationBarControllerLazy.get().getNavigationBarView(i);
            int i2 = 0;
            if (navigationBarView == null) {
                MotoTaskBarController motoTaskBarController = this.mMotoTaskBarController;
                if (!z) {
                    i2 = 8;
                }
                motoTaskBarController.setTaskBarViewVisibility(i, i2);
            } else if (z) {
                navigationBarView.getRootView().setVisibility(0);
            } else {
                navigationBarView.getRootView().setVisibility(8);
            }
        }
    }

    @VisibleForTesting
    static final class KeyguardPresentation extends Presentation {
        /* access modifiers changed from: private */
        public View mClock;
        private final Context mContext;
        private KeyguardClockSwitchController mKeyguardClockSwitchController;
        private final KeyguardStatusViewComponent.Factory mKeyguardStatusViewComponentFactory;
        /* access modifiers changed from: private */
        public int mMarginLeft;
        /* access modifiers changed from: private */
        public int mMarginTop;
        Runnable mMoveTextRunnable = new Runnable() {
            public void run() {
                int access$700 = KeyguardPresentation.this.mMarginLeft + ((int) (Math.random() * ((double) (KeyguardPresentation.this.mUsableWidth - KeyguardPresentation.this.mClock.getWidth()))));
                int access$1000 = KeyguardPresentation.this.mMarginTop + ((int) (Math.random() * ((double) (KeyguardPresentation.this.mUsableHeight - KeyguardPresentation.this.mClock.getHeight()))));
                KeyguardPresentation.this.mClock.setTranslationX((float) access$700);
                KeyguardPresentation.this.mClock.setTranslationY((float) access$1000);
                KeyguardPresentation.this.mClock.postDelayed(KeyguardPresentation.this.mMoveTextRunnable, 10000);
            }
        };
        /* access modifiers changed from: private */
        public int mUsableHeight;
        /* access modifiers changed from: private */
        public int mUsableWidth;

        public void cancel() {
        }

        KeyguardPresentation(Context context, Display display, KeyguardStatusViewComponent.Factory factory) {
            super(context, display, R$style.Theme_SystemUI_KeyguardPresentation, 2009);
            this.mKeyguardStatusViewComponentFactory = factory;
            setCancelable(false);
            this.mContext = context;
        }

        public void onDetachedFromWindow() {
            this.mClock.removeCallbacks(this.mMoveTextRunnable);
        }

        public void onDisplayChanged() {
            updateBounds();
            getWindow().getDecorView().requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            updateBounds();
            setContentView(LayoutInflater.from(this.mContext).inflate(R$layout.keyguard_presentation, (ViewGroup) null));
            getWindow().getDecorView().setSystemUiVisibility(1792);
            getWindow().getAttributes().setFitInsetsTypes(0);
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setNavigationBarColor(0);
            int i = R$id.clock;
            View findViewById = findViewById(i);
            this.mClock = findViewById;
            findViewById.post(this.mMoveTextRunnable);
            KeyguardClockSwitchController keyguardClockSwitchController = this.mKeyguardStatusViewComponentFactory.build((KeyguardStatusView) findViewById(i)).getKeyguardClockSwitchController();
            this.mKeyguardClockSwitchController = keyguardClockSwitchController;
            keyguardClockSwitchController.setOnlyClock(true);
            this.mKeyguardClockSwitchController.init();
        }

        private void updateBounds() {
            Rect bounds = getWindow().getWindowManager().getMaximumWindowMetrics().getBounds();
            this.mUsableWidth = (bounds.width() * 80) / 100;
            this.mUsableHeight = (bounds.height() * 80) / 100;
            this.mMarginLeft = (bounds.width() * 20) / 200;
            this.mMarginTop = (bounds.height() * 20) / 200;
        }
    }

    public void setFailedCount(int i) {
        this.mCurrentFailedCount = i;
    }

    public int getFailedCount() {
        return this.mCurrentFailedCount;
    }

    public boolean onceLockout() {
        return this.mOnceLockout;
    }

    public void setOnceLockout(boolean z) {
        this.mOnceLockout = z;
    }

    public void updateDozingState(boolean z) {
        this.mIsDozing = z;
        for (int size = this.mPresentations.size() - 1; size >= 0; size--) {
            Presentation valueAt = this.mPresentations.valueAt(size);
            if (valueAt != null && (valueAt instanceof MotoKeyguardPresentation)) {
                ((MotoKeyguardPresentation) valueAt).updateViewForDoze(z);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isDisplayMetricsChange(int i) {
        Display display = this.mDisplayService.getDisplay(i);
        if (display == null) {
            return true;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return !displayMetrics.equals(this.mDisplayMetricses.get(i));
    }

    /* access modifiers changed from: private */
    public boolean isDisplayInfoChange(int i) {
        Display display = this.mDisplayService.getDisplay(i);
        if (display == null) {
            return true;
        }
        DisplayInfo displayInfo = new DisplayInfo();
        display.getDisplayInfo(displayInfo);
        DisplayInfo displayInfo2 = this.mDisplayInfos.get(i);
        if (displayInfo2 != null && displayInfo.appHeight == displayInfo2.appHeight && displayInfo.largestNominalAppHeight == displayInfo2.largestNominalAppHeight && displayInfo.logicalHeight == displayInfo2.logicalHeight && displayInfo.smallestNominalAppHeight == displayInfo2.smallestNominalAppHeight && displayInfo.appWidth == displayInfo2.appWidth && displayInfo.largestNominalAppWidth == displayInfo2.largestNominalAppWidth && displayInfo.logicalWidth == displayInfo2.logicalWidth && displayInfo.smallestNominalAppWidth == displayInfo2.smallestNominalAppWidth && displayInfo.logicalDensityDpi == displayInfo2.logicalDensityDpi && displayInfo.physicalXDpi == displayInfo2.physicalXDpi && displayInfo.physicalYDpi == displayInfo2.physicalYDpi) {
            return false;
        }
        return true;
    }

    private boolean isMotoKeyguardPresentation(Display display) {
        return DesktopFeature.isDesktopSupported() && ("com.motorola.mobiledesktop.core".equals(display.getOwnerPackageName()) || display.getType() == 2 || display.getType() == 3);
    }

    public void onDisplayReady(int i) {
        if (DEBUG) {
            Log.d("KeyguardDisplayManager", "onDisplayReady: displayId = " + i);
        }
        Display display = this.mDisplayService.getDisplay(i);
        if (this.mShowing) {
            updateNavigationBarVisibility(i, false);
            showPresentation(display);
        }
    }
}
