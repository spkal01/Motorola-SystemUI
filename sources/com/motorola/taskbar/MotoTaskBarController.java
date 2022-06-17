package com.motorola.taskbar;

import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.InsetsState;
import android.view.WindowManagerGlobal;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.view.AppearanceRegion;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.Dependency;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.navigationbar.NavigationBarController;
import com.android.systemui.navigationbar.NavigationBarView;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.statusbar.AutoHideUiElement;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.KeyboardShortcuts;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.volume.VolumeDialogImpl;
import com.motorola.internal.app.MotoDesktopManager;
import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager;
import com.motorola.taskbar.ITaskBarProxy;
import com.motorola.taskbar.ITaskBarService;

public class MotoTaskBarController implements CommandQueue.Callbacks {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public static final String TAG = "MotoTaskBarController";
    private final Handler mBgHandler;
    /* access modifiers changed from: private */
    public boolean mBindingTaskBarService = false;
    /* access modifiers changed from: private */
    public Runnable mCheckBindSuccessRunnable = new Runnable() {
        public void run() {
            MotoTaskBarController.this.mMainHandler.removeCallbacks(MotoTaskBarController.this.mCheckBindSuccessRunnable);
            if (MotoTaskBarController.this.mBindingTaskBarService) {
                MotoTaskBarController.this.mContext.unbindService(MotoTaskBarController.this.mTaskBarConnection);
                boolean unused = MotoTaskBarController.this.mBindingTaskBarService = false;
                MotoTaskBarController.this.bindMotoTaskBarServiceIfNeed();
                Log.w(MotoTaskBarController.TAG, "Bind taskbar service time out");
            }
        }
    };
    private final CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final DataUsageController mDataController;
    /* access modifiers changed from: private */
    public final DesktopDisplayRootModulesManager mDesktopDisplayRootModulesManager;
    private SparseArray<DisplayInfo> mDisplayInfos = new SparseArray<>();
    private final DisplayManager mDisplayManager;
    /* access modifiers changed from: private */
    public final ITaskBarProxy mITaskBarProxy = new ITaskBarProxy.Stub() {
        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$requestSwitchVolumeDialog$0(int i, Rect rect) {
            MotoTaskBarController.this.switchVolumeDialog(i, rect);
        }

        public void requestSwitchVolumeDialog(int i, Rect rect) {
            MotoTaskBarController.this.mMainHandler.post(new MotoTaskBarController$1$$ExternalSyntheticLambda0(this, i, rect));
        }

        public boolean isMobileDataEnabled() {
            return MotoTaskBarController.this.mDataController.isMobileDataEnabled();
        }

        public void setMobileDataEnabled(boolean z) {
            MotoTaskBarController.this.mDataController.setMobileDataEnabled(z);
        }

        public Bundle getMobileState() {
            return MotoTaskBarController.this.mMobileState.toBundle();
        }

        public void touchAutoHide(int i) {
            MotoTaskBarController.this.handleTouchAutoHide(i);
        }

        public void requestQSNPanel(int i, int i2) {
            MotoTaskBarController.this.mDesktopDisplayRootModulesManager.requestQSNPanel(i, i2);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$requestNavIcon$1(boolean z, int i) {
            MotoTaskBarController.this.requestNavIcon(z, i);
        }

        public void requestNavIcon(boolean z, int i) {
            MotoTaskBarController.this.mMainHandler.post(new MotoTaskBarController$1$$ExternalSyntheticLambda3(this, z, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$requestNavTrackpadGuide$2(boolean z) {
            MotoTaskBarController.this.requestNavTrackpadGuide(z);
        }

        public void requestNavTrackpadGuide(boolean z) {
            MotoTaskBarController.this.mMainHandler.post(new MotoTaskBarController$1$$ExternalSyntheticLambda2(this, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTrackpadStateChanged$3(boolean z) {
            MotoTaskBarController.this.onTrackpadStateChanged(z);
        }

        public void onTrackpadStateChanged(boolean z) {
            MotoTaskBarController.this.mMainHandler.post(new MotoTaskBarController$1$$ExternalSyntheticLambda1(this, z));
        }

        public int getUnreadNotificationCount(int i) {
            return MotoTaskBarController.this.mDesktopDisplayRootModulesManager.getUnreadNotificationCount(i);
        }
    };
    /* access modifiers changed from: private */
    public ITaskBarService mITaskBarService = null;
    private final boolean mIsMotoTaskBarAvailable;
    private boolean mIsNavGuideShow = false;
    /* access modifiers changed from: private */
    public final Handler mMainHandler;
    /* access modifiers changed from: private */
    public MobileState mMobileState = new MobileState();
    /* access modifiers changed from: private */
    public final NetworkController mNetworkController;
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() {
        public void onOverviewShown(boolean z) {
            if (MotoTaskBarController.this.mITaskBarService != null) {
                try {
                    MotoTaskBarController.this.mITaskBarService.onOverviewShown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private boolean mReqTrackpadIconShow = false;
    private final CellSignalCallback mSignalCallback = new CellSignalCallback();
    /* access modifiers changed from: private */
    public final ServiceConnection mTaskBarConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(MotoTaskBarController.TAG, "onServiceConnected");
            boolean unused = MotoTaskBarController.this.mBindingTaskBarService = false;
            MotoTaskBarController.this.mMainHandler.removeCallbacks(MotoTaskBarController.this.mCheckBindSuccessRunnable);
            ITaskBarService unused2 = MotoTaskBarController.this.mITaskBarService = ITaskBarService.Stub.asInterface(iBinder);
            try {
                MotoTaskBarController.this.mITaskBarService.setTaskBarProxy(MotoTaskBarController.this.mITaskBarProxy);
                MotoTaskBarController.this.mITaskBarService.onSystemUIReady();
                MotoTaskBarController.this.onBindedTaskBarService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(MotoTaskBarController.TAG, "onServiceDisconnected");
            ITaskBarService unused = MotoTaskBarController.this.mITaskBarService = null;
            boolean unused2 = MotoTaskBarController.this.mBindingTaskBarService = false;
        }

        public void onBindingDied(ComponentName componentName) {
            Log.e(MotoTaskBarController.TAG, "onBindingDied");
            ITaskBarService unused = MotoTaskBarController.this.mITaskBarService = null;
            boolean unused2 = MotoTaskBarController.this.mBindingTaskBarService = false;
            MotoTaskBarController.this.bindMotoTaskBarServiceIfNeed();
        }

        public void onNullBinding(ComponentName componentName) {
            Log.e(MotoTaskBarController.TAG, "onNullBinding");
            ITaskBarService unused = MotoTaskBarController.this.mITaskBarService = null;
            boolean unused2 = MotoTaskBarController.this.mBindingTaskBarService = false;
        }
    };
    private SparseArray<Integer> mTaskBarVisibilities = new SparseArray<>();
    private boolean mTrackpadActivityShow = false;

    /* access modifiers changed from: private */
    public static int barMode(boolean z, int i) {
        if (z) {
            return 1;
        }
        if ((i & 6) == 6) {
            return 3;
        }
        if ((i & 4) != 0) {
            return 6;
        }
        return (i & 2) != 0 ? 4 : 0;
    }

    public MotoTaskBarController(Context context, Handler handler, Handler handler2, CommandQueue commandQueue, DesktopDisplayRootModulesManager desktopDisplayRootModulesManager, NetworkController networkController) {
        this.mContext = context;
        this.mMainHandler = handler;
        this.mBgHandler = handler2;
        this.mIsMotoTaskBarAvailable = isMotoTaskBarPkgAvailable();
        this.mNetworkController = networkController;
        this.mDataController = networkController.getMobileDataController();
        this.mCommandQueue = commandQueue;
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
        this.mDesktopDisplayRootModulesManager = desktopDisplayRootModulesManager;
    }

    public void init() {
        if (isMotoTaskBarAvailable()) {
            this.mNetworkController.addCallback(this.mSignalCallback);
            ((OverviewProxyService) Dependency.get(OverviewProxyService.class)).addCallback(this.mOverviewProxyListener);
            CommandQueue commandQueue = this.mCommandQueue;
            if (commandQueue != null) {
                commandQueue.addCallback((CommandQueue.Callbacks) this);
            }
            bindMotoTaskBarServiceIfNeed();
        }
    }

    public boolean isMotoTaskBarAvailable() {
        return this.mIsMotoTaskBarAvailable && MotoDesktopManager.isDesktopSupported();
    }

    public boolean isDesktopModeDisplay(int i) {
        Display display = this.mDisplayManager.getDisplay(i);
        if (display == null) {
            return false;
        }
        return DesktopFeature.isDesktopMode(display);
    }

    private boolean isMotoTaskBarPkgAvailable() {
        boolean z = false;
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo("com.motorola.systemui.desk", 0);
            if (applicationInfo != null && applicationInfo.enabled) {
                z = true;
            }
        } catch (Exception unused) {
        }
        if (DEBUG) {
            Log.d(TAG, "Package(com.motorola.systemui.desk) available: " + z);
        }
        return z;
    }

    public void onDisplayRemoved(int i) {
        if (this.mIsMotoTaskBarAvailable) {
            DisplayInfo displayInfo = this.mDisplayInfos.get(i);
            if (displayInfo != null) {
                displayInfo.destroy();
            }
            this.mDisplayInfos.remove(i);
            this.mTaskBarVisibilities.remove(i);
            ITaskBarService iTaskBarService = this.mITaskBarService;
            if (iTaskBarService != null) {
                try {
                    iTaskBarService.onDisplayRemoved(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                bindMotoTaskBarServiceIfNeed();
            }
        }
    }

    public void onDisplayReady(int i) {
        DisplayInfo createDisplayInfo;
        if (this.mIsMotoTaskBarAvailable && (createDisplayInfo = createDisplayInfo(i)) != null) {
            this.mDisplayInfos.append(i, createDisplayInfo);
            ITaskBarService iTaskBarService = this.mITaskBarService;
            if (iTaskBarService != null) {
                try {
                    iTaskBarService.onDisplayReady(i);
                    onTaskBarDisplayReady(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                bindMotoTaskBarServiceIfNeed();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setTaskBarTransitionMode(int i, int i2) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.setTaskBarTransitionMode(i, i2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setWindowState(int i, int i2, int i3) {
        DisplayInfo displayInfo;
        if (i != 0 && (displayInfo = this.mDisplayInfos.get(i)) != null) {
            displayInfo.setWindowState(i2, i3);
            if (i2 == 2) {
                onTaskbarWindowStateChanged(i, i3);
            }
        }
    }

    public void onSystemBarAttributesChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z, int i3, boolean z2) {
        DisplayInfo displayInfo;
        if (i != 0 && (displayInfo = this.mDisplayInfos.get(i)) != null) {
            displayInfo.onSystemBarAppearanceChanged(i2, appearanceRegionArr, z);
        }
    }

    public void showTransient(int i, int[] iArr) {
        DisplayInfo displayInfo;
        if (i != 0 && (displayInfo = this.mDisplayInfos.get(i)) != null) {
            displayInfo.showTransient(iArr);
        }
    }

    public void abortTransient(int i, int[] iArr) {
        DisplayInfo displayInfo;
        if (i != 0 && (displayInfo = this.mDisplayInfos.get(i)) != null) {
            displayInfo.abortTransient(iArr);
        }
    }

    public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
        if (i == 0) {
            updateImeVisible(i, (i2 & 2) != 0);
            return;
        }
        DisplayInfo displayInfo = this.mDisplayInfos.get(i);
        if (displayInfo != null) {
            displayInfo.setImeWindowStatus(iBinder, i2, i3, z);
        }
    }

    public void addDesktopIcon(String str, int i, StatusBarIcon statusBarIcon, PendingIntent pendingIntent) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.addDesktopIcon(str, i, statusBarIcon, pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeDesktopIcon(String str, int i) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.removeDesktopIcon(str, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setTaskBarImeSwitchButtonVisible(int i, boolean z) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.setTaskBarImeSwitchButtonVisible(i, z);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateImeVisible(int i, boolean z) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.updateImeVisible(i, z);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onTaskBarDisplayReady(int i) {
        Integer num = this.mTaskBarVisibilities.get(i);
        if (num != null) {
            setTaskBarViewVisibility(i, num.intValue());
        }
    }

    public void setTaskBarViewVisibility(int i, int i2) {
        this.mTaskBarVisibilities.append(i, Integer.valueOf(i2));
        ITaskBarService iTaskBarService = this.mITaskBarService;
        if (iTaskBarService != null) {
            try {
                iTaskBarService.setTaskBarViewVisibility(i, i2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onTaskbarWindowStateChanged(int i, int i2) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.onTaskbarWindowStateChanged(i, i2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean shouldShowTaskBar(int i) {
        if (i == 0) {
            return false;
        }
        try {
            if (WindowManagerGlobal.getWindowManagerService().hasNavigationBar(i) && isDesktopModeDisplay(i)) {
                return true;
            }
            return false;
        } catch (RemoteException unused) {
            Log.w(TAG, "shouldShowTaskBar Cannot get WindowManager.");
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void onBindedTaskBarService() {
        for (Display display : ((DisplayManager) this.mContext.getSystemService("display")).getDisplays()) {
            if (shouldShowTaskBar(display.getDisplayId())) {
                int displayId = display.getDisplayId();
                DisplayInfo displayInfo = this.mDisplayInfos.get(displayId);
                if (displayInfo == null) {
                    displayInfo = createDisplayInfo(displayId);
                } else {
                    onTaskbarWindowStateChanged(displayInfo.mDisplayId, displayInfo.mNavigationBarWindowState);
                }
                if (displayInfo != null) {
                    this.mDisplayInfos.append(displayId, displayInfo);
                    onTaskBarDisplayReady(displayId);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void switchVolumeDialog(int i, Rect rect) {
        DisplayInfo displayInfo;
        if (i != 0 && (displayInfo = this.mDisplayInfos.get(i)) != null) {
            displayInfo.switchVolumeDialog(rect);
        }
    }

    /* access modifiers changed from: private */
    public void handleTouchAutoHide(int i) {
        DisplayInfo displayInfo;
        if (i != 0 && (displayInfo = this.mDisplayInfos.get(i)) != null) {
            displayInfo.touchAutoHide();
        }
    }

    /* access modifiers changed from: private */
    public Context createDisplayContext(int i) {
        return this.mContext.createDisplayContext(((DisplayManager) this.mContext.getSystemService("display")).getDisplay(i));
    }

    /* access modifiers changed from: private */
    public void bindMotoTaskBarServiceIfNeed() {
        if (!this.mBindingTaskBarService && this.mITaskBarService == null) {
            Intent intent = new Intent();
            intent.setPackage("com.motorola.systemui.desk");
            intent.setAction("com.motorola.taskbar.action.BIND_SERVICE");
            if (!this.mContext.bindService(intent, this.mTaskBarConnection, 1)) {
                Log.i(TAG, "bind Task Bar Service error");
                this.mITaskBarService = null;
                return;
            }
            this.mBindingTaskBarService = true;
            this.mMainHandler.postDelayed(this.mCheckBindSuccessRunnable, 1000);
        }
    }

    private final DisplayInfo createDisplayInfo(int i) {
        Display display = ((DisplayManager) this.mContext.getSystemService("display")).getDisplay(i);
        if (display == null) {
            return null;
        }
        return new DisplayInfo(display);
    }

    private class DisplayInfo {
        private int mAppearance;
        AutoHideController mAutoHideController;
        final int mDisplayId;
        private int mNavigationBarMode;
        /* access modifiers changed from: private */
        public int mNavigationBarWindowState = 0;
        private int mNavigationIconHints = 0;
        /* access modifiers changed from: private */
        public boolean mTransientShown;
        private final VolumeDialog.Callback mVolumeDialogCallback = new VolumeDialog.Callback() {
            public void onZenPrioritySettingsClicked() {
            }

            public void onZenSettingsClicked() {
            }
        };
        private VolumeDialogImpl mVolumeDialogImpl;

        DisplayInfo(Display display) {
            this.mDisplayId = display.getDisplayId();
            AutoHideController autoHideController = new AutoHideController(MotoTaskBarController.this.mContext.createDisplayContext(display), MotoTaskBarController.this.mMainHandler, (IWindowManager) Dependency.get(IWindowManager.class));
            this.mAutoHideController = autoHideController;
            autoHideController.setNavigationBar(new TaskBarAutoHideUiElement());
            restoreAppearanceAndTransientState();
        }

        /* access modifiers changed from: private */
        public void checkNavBarModes() {
            MotoTaskBarController.this.setTaskBarTransitionMode(this.mDisplayId, this.mNavigationBarMode);
        }

        private boolean updateBarMode(int i) {
            if (this.mNavigationBarMode == i) {
                return false;
            }
            this.mNavigationBarMode = i;
            checkNavBarModes();
            this.mAutoHideController.touchAutoHide();
            return true;
        }

        public void setWindowState(int i, int i2) {
            if (i == 2 && this.mNavigationBarWindowState != i2) {
                this.mNavigationBarWindowState = i2;
                if (MotoTaskBarController.DEBUG) {
                    String access$1000 = MotoTaskBarController.TAG;
                    Log.d(access$1000, "setWindowState: " + this.mDisplayId + "; state:" + StatusBarManager.windowStateToString(i2));
                }
            }
        }

        public void onSystemBarAppearanceChanged(int i, AppearanceRegion[] appearanceRegionArr, boolean z) {
            if (this.mAppearance != i) {
                this.mAppearance = i;
                updateBarMode(MotoTaskBarController.barMode(this.mTransientShown, i));
            }
        }

        public void showTransient(int[] iArr) {
            if (InsetsState.containsType(iArr, 1) && !this.mTransientShown) {
                this.mTransientShown = true;
                handleTransientChanged();
            }
        }

        public void abortTransient(int[] iArr) {
            if (InsetsState.containsType(iArr, 1)) {
                clearTransient();
            }
        }

        /* access modifiers changed from: private */
        public void clearTransient() {
            if (this.mTransientShown) {
                this.mTransientShown = false;
                handleTransientChanged();
            }
        }

        private void handleTransientChanged() {
            updateBarMode(MotoTaskBarController.barMode(this.mTransientShown, this.mAppearance));
        }

        private void restoreAppearanceAndTransientState() {
            this.mNavigationBarMode = MotoTaskBarController.barMode(this.mTransientShown, this.mAppearance);
            checkNavBarModes();
            this.mAutoHideController.touchAutoHide();
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x001f  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x0021  */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0025 A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0026  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setImeWindowStatus(android.os.IBinder r4, int r5, int r6, boolean r7) {
            /*
                r3 = this;
                int r4 = r3.mNavigationIconHints
                r0 = 2
                r5 = r5 & r0
                r1 = 0
                r2 = 1
                if (r5 == 0) goto L_0x000a
                r5 = r2
                goto L_0x000b
            L_0x000a:
                r5 = r1
            L_0x000b:
                if (r6 == 0) goto L_0x0019
                if (r6 == r2) goto L_0x0019
                if (r6 == r0) goto L_0x0019
                r5 = 3
                if (r6 == r5) goto L_0x0016
                r5 = r4
                goto L_0x001d
            L_0x0016:
                r5 = r4 & -2
                goto L_0x001d
            L_0x0019:
                if (r5 == 0) goto L_0x0016
                r5 = r4 | 1
            L_0x001d:
                if (r7 == 0) goto L_0x0021
                r5 = r5 | r0
                goto L_0x0023
            L_0x0021:
                r5 = r5 & -3
            L_0x0023:
                if (r5 != r4) goto L_0x0026
                return
            L_0x0026:
                r3.mNavigationIconHints = r5
                r4 = r5 & 2
                if (r4 == 0) goto L_0x002e
                r4 = r2
                goto L_0x002f
            L_0x002e:
                r4 = r1
            L_0x002f:
                com.motorola.taskbar.MotoTaskBarController r6 = com.motorola.taskbar.MotoTaskBarController.this
                int r7 = r3.mDisplayId
                r6.setTaskBarImeSwitchButtonVisible(r7, r4)
                r4 = r5 & 1
                if (r4 == 0) goto L_0x003b
                r1 = r2
            L_0x003b:
                com.motorola.taskbar.MotoTaskBarController r4 = com.motorola.taskbar.MotoTaskBarController.this
                int r3 = r3.mDisplayId
                r4.updateImeVisible(r3, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.taskbar.MotoTaskBarController.DisplayInfo.setImeWindowStatus(android.os.IBinder, int, int, boolean):void");
        }

        public void destroy() {
            VolumeDialogImpl volumeDialogImpl = this.mVolumeDialogImpl;
            if (volumeDialogImpl != null) {
                volumeDialogImpl.destroy();
            }
        }

        public void switchVolumeDialog(Rect rect) {
            if (this.mVolumeDialogImpl == null) {
                VolumeDialogImpl volumeDialogImpl = new VolumeDialogImpl(MotoTaskBarController.this.createDisplayContext(this.mDisplayId));
                this.mVolumeDialogImpl = volumeDialogImpl;
                volumeDialogImpl.setStreamImportant(1, false);
                this.mVolumeDialogImpl.setAutomute(true);
                this.mVolumeDialogImpl.setSilentMode(false);
                this.mVolumeDialogImpl.init(2020, this.mVolumeDialogCallback);
                this.mVolumeDialogImpl.setTaskBarFlag();
            }
            this.mVolumeDialogImpl.switchVolumeDialog(rect);
        }

        public void touchAutoHide() {
            this.mAutoHideController.touchAutoHide();
        }

        private class TaskBarAutoHideUiElement implements AutoHideUiElement {
            public boolean shouldHideOnTouch() {
                return true;
            }

            private TaskBarAutoHideUiElement() {
            }

            public void synchronizeState() {
                DisplayInfo.this.checkNavBarModes();
            }

            public boolean isVisible() {
                return DisplayInfo.this.mTransientShown;
            }

            public void hide() {
                DisplayInfo.this.clearTransient();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onMobileStateChanged(MobileState mobileState) {
        ITaskBarService iTaskBarService;
        if (this.mIsMotoTaskBarAvailable && (iTaskBarService = this.mITaskBarService) != null) {
            try {
                iTaskBarService.onMobileStateChanged(mobileState.toBundle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final class CallbackInfo {
        boolean isSimError;
        boolean noSim;

        private CallbackInfo() {
        }
    }

    private final class CellSignalCallback implements NetworkController.SignalCallback {
        private final CallbackInfo mInfo;

        private CellSignalCallback() {
            this.mInfo = new CallbackInfo();
        }

        public void setMobileDataIndicators(NetworkController.MobileDataIndicators mobileDataIndicators) {
            if (mobileDataIndicators.qsIcon != null) {
                MotoTaskBarController.this.mMobileState.dataSubscriptionName = MotoTaskBarController.this.mNetworkController.getMobileDataNetworkName();
                MotoTaskBarController.this.mMobileState.dataContentDescription = mobileDataIndicators.description != null ? mobileDataIndicators.typeContentDescriptionHtml : null;
                MotoTaskBarController.this.mMobileState.activityIn = mobileDataIndicators.activityIn;
                MotoTaskBarController.this.mMobileState.activityOut = mobileDataIndicators.activityOut;
                MotoTaskBarController.this.mMobileState.roaming = mobileDataIndicators.roaming;
                MobileState access$400 = MotoTaskBarController.this.mMobileState;
                boolean z = true;
                if (MotoTaskBarController.this.mNetworkController.getNumberSubscriptions() <= 1) {
                    z = false;
                }
                access$400.multipleSubs = z;
                if (mobileDataIndicators.extendedInfo != null) {
                    this.mInfo.noSim = MotoTaskBarController.this.mNetworkController.getSimStates().isSimAbsent(mobileDataIndicators.extendedInfo.slotId);
                    if (NetworkStateTracker.shouldDifferSimError()) {
                        this.mInfo.isSimError = MotoTaskBarController.this.mNetworkController.getSimStates().isSimError(mobileDataIndicators.extendedInfo.slotId);
                        CallbackInfo callbackInfo = this.mInfo;
                        callbackInfo.noSim |= callbackInfo.isSimError;
                    }
                }
                MotoTaskBarController motoTaskBarController = MotoTaskBarController.this;
                motoTaskBarController.onMobileStateChanged(motoTaskBarController.mMobileState);
            }
        }

        public void setNoSims(boolean z, boolean z2) {
            MotoTaskBarController.this.mMobileState.noSim = z;
            if (NetworkStateTracker.shouldDifferSimError()) {
                boolean z3 = false;
                boolean isSimAbsent = MotoTaskBarController.this.mNetworkController.getSimStates().isSimAbsent(0);
                boolean isSimError = MotoTaskBarController.this.mNetworkController.getSimStates().isSimError(0);
                if (MotoTaskBarController.DEBUG) {
                    String access$1000 = MotoTaskBarController.TAG;
                    Log.i(access$1000, "setNoSims isSimAbsent = " + isSimAbsent + " isSimError = " + isSimError + " show = " + z);
                }
                MobileState access$400 = MotoTaskBarController.this.mMobileState;
                if (z || isSimAbsent || isSimError) {
                    z3 = true;
                }
                access$400.noSim = z3;
                MotoTaskBarController.this.mMobileState.isSimError = isSimError;
            }
            MotoTaskBarController motoTaskBarController = MotoTaskBarController.this;
            motoTaskBarController.onMobileStateChanged(motoTaskBarController.mMobileState);
        }

        public void setIsAirplaneMode(NetworkController.IconState iconState) {
            MotoTaskBarController.this.mMobileState.airplaneModeEnabled = iconState.visible;
            MotoTaskBarController motoTaskBarController = MotoTaskBarController.this;
            motoTaskBarController.onMobileStateChanged(motoTaskBarController.mMobileState);
        }
    }

    public class MobileState {
        boolean activityIn;
        boolean activityOut;
        boolean airplaneModeEnabled;
        CharSequence dataContentDescription;
        CharSequence dataSubscriptionName;
        boolean isSimError;
        boolean multipleSubs;
        boolean noSim;
        boolean roaming;

        public MobileState() {
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putBoolean("airplaneModeEnabled", this.airplaneModeEnabled);
            bundle.putCharSequence("dataSubscriptionName", this.dataSubscriptionName);
            bundle.putCharSequence("dataContentDescription", this.dataContentDescription);
            bundle.putBoolean("activityIn", this.activityIn);
            bundle.putBoolean("activityOut", this.activityOut);
            bundle.putBoolean("noSim", this.noSim);
            bundle.putBoolean("roaming", this.roaming);
            bundle.putBoolean("multipleSubs", this.multipleSubs);
            bundle.putBoolean("isSimError", this.isSimError);
            return bundle;
        }
    }

    public void resetTaskBarAudoHideTimeout(int i) {
        handleTouchAutoHide(i);
    }

    public void dismissKeyboardShortcutsMenuForDisplay(int i) {
        if (MotoDesktopManager.isDesktopSupported()) {
            KeyboardShortcuts.dismiss();
        }
    }

    public void toggleKeyboardShortcutsMenuForDisplay(int i, int i2) {
        if (MotoDesktopManager.isDesktopSupported()) {
            KeyboardShortcuts.toggle(this.mContext.createDisplayContext(((DisplayManager) this.mContext.getSystemService("display")).getDisplay(i2)), i);
        }
    }

    /* access modifiers changed from: private */
    public void requestNavIcon(boolean z, int i) {
        NavigationBarView navigationBarView;
        if (isMotoTaskBarAvailable() && (navigationBarView = getNavigationBarView()) != null) {
            this.mReqTrackpadIconShow = z;
            ((NavigationModeController) Dependency.get(NavigationModeController.class)).requestNavGestureOverlay(isTrackpadIconShow());
            navigationBarView.setTrackpadIconShow(isTrackpadIconShow());
        }
    }

    public void onNavIconClicked() {
        if (isMotoTaskBarAvailable()) {
            if (this.mITaskBarService != null) {
                try {
                    ((StatusBarManager) this.mContext.getSystemService(StatusBarManager.class)).collapsePanels();
                    this.mITaskBarService.onNavIconClicked();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                bindMotoTaskBarServiceIfNeed();
            }
        }
    }

    public void requestNavTrackpadGuide(boolean z) {
        if (isMotoTaskBarAvailable()) {
            this.mIsNavGuideShow = z;
            handleTrackpadGuideShow(z);
        }
    }

    public void handleTrackpadGuideShow(boolean z) {
        NavigationBarView navigationBarView;
        if (!isMotoTaskBarAvailable()) {
            return;
        }
        if ((this.mIsNavGuideShow || !z) && (navigationBarView = getNavigationBarView()) != null) {
            navigationBarView.requestNavTrackpadGuide(z);
        }
    }

    public boolean isNavGuideShow() {
        return this.mIsNavGuideShow;
    }

    private NavigationBarView getNavigationBarView() {
        NavigationBarView defaultNavigationBarView;
        NavigationBarController navigationBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
        if (navigationBarController == null || (defaultNavigationBarView = navigationBarController.getDefaultNavigationBarView()) == null) {
            return null;
        }
        return defaultNavigationBarView;
    }

    /* access modifiers changed from: private */
    public void onTrackpadStateChanged(boolean z) {
        NavigationBarView navigationBarView;
        if (isMotoTaskBarAvailable() && (navigationBarView = getNavigationBarView()) != null) {
            this.mTrackpadActivityShow = z;
            navigationBarView.setTrackpadIconShow(isTrackpadIconShow());
        }
    }

    public boolean isTrackpadIconShow() {
        return this.mReqTrackpadIconShow && !this.mTrackpadActivityShow;
    }

    public void onUnreadNotificationCountChanged(int i, int i2) {
        ITaskBarService iTaskBarService = this.mITaskBarService;
        if (iTaskBarService != null) {
            try {
                iTaskBarService.onUnreadNotificationCountChanged(i, i2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
