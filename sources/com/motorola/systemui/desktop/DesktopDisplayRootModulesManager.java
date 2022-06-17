package com.motorola.systemui.desktop;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.external.MotoMainProcessTileServiceManager;
import com.android.systemui.p006qs.external.TileServices;
import com.android.systemui.statusbar.CommandQueue;
import com.motorola.taskbar.ISystemUIReadyForService;
import com.motorola.taskbar.ISystemUIReadyForServiceCallback;
import com.motorola.taskbar.MotoTaskBarController;

public class DesktopDisplayRootModulesManager extends SystemUI implements CommandQueue.Callbacks {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private static DesktopDisplayRootModulesManager sInstance;
    private static final Class<?>[] sSystemUIReadyForServices = {SystemUIReadyForService1.class, SystemUIReadyForService2.class};
    /* access modifiers changed from: private */
    public final Handler mBgHandler;
    private final CommandQueue mCommandQueue;
    private SparseArray<Class<?>> mDesktopDisplayClasses;
    private final DisplayManager mDisplayManager = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class));
    private final MotoFeature mMotoFeature;
    /* access modifiers changed from: private */
    public MotoTaskBarController mMotoTaskBarController;
    private SparseArray<SystemUIReadyForServiceConnection> mSystemUIReadyForServiceConnections;
    /* access modifiers changed from: private */
    public TileServices mTileServices;

    public DesktopDisplayRootModulesManager(Context context, CommandQueue commandQueue, Handler handler) {
        super(context);
        Class<?>[] clsArr = sSystemUIReadyForServices;
        this.mDesktopDisplayClasses = new SparseArray<>(clsArr.length);
        this.mSystemUIReadyForServiceConnections = new SparseArray<>(clsArr.length);
        this.mCommandQueue = commandQueue;
        this.mBgHandler = handler;
        this.mMotoFeature = MotoFeature.getInstance(context);
        sInstance = this;
    }

    public void start() {
        if (DesktopFeature.isDesktopSupported()) {
            this.mTileServices = ((QSTileHost) Dependency.get(QSTileHost.class)).getTileServices();
            this.mMotoTaskBarController = (MotoTaskBarController) Dependency.get(MotoTaskBarController.class);
            this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
            removeDesktopDisplay(0, true);
            for (Display display : this.mDisplayManager.getDisplays()) {
                if (DesktopFeature.isDesktopMode(display)) {
                    onDisplayReady(display.getDisplayId());
                }
            }
        }
    }

    public void onDisplayRemoved(int i) {
        Log.i("DesktopDRModulesManager", "onDisplayRemoved: " + i);
        removeDesktopDisplay(i, false);
        this.mTileServices.onDisplayRemoved(i);
    }

    public void onDisplayReady(int i) {
        Display display;
        if (i != 0 && i != -1) {
            if ((!this.mMotoFeature.isSupportCli() || i != 1) && (display = this.mDisplayManager.getDisplay(i)) != null && DesktopFeature.isDesktopMode(display)) {
                createReadyForServiceConnection(i);
            }
        }
    }

    private void createReadyForServiceConnection(int i) {
        if (this.mSystemUIReadyForServiceConnections.get(i) != null) {
            Log.w("DesktopDRModulesManager", "createReadyForServiceConnection already exist: " + i);
            return;
        }
        Class<?> cls = null;
        int i2 = 0;
        while (true) {
            Class<?>[] clsArr = sSystemUIReadyForServices;
            if (i2 >= clsArr.length) {
                break;
            } else if (this.mDesktopDisplayClasses.indexOfValue(clsArr[i2]) == -1) {
                cls = clsArr[i2];
                break;
            } else {
                i2++;
            }
        }
        if (cls == null) {
            Log.w("DesktopDRModulesManager", "createReadyForServiceConnection not idle service: " + i);
            return;
        }
        Log.i("DesktopDRModulesManager", "createReadyForServiceConnection: " + i);
        SystemUIReadyForServiceConnection systemUIReadyForServiceConnection = new SystemUIReadyForServiceConnection(i, cls);
        if (bindSystemUIReadyForService(systemUIReadyForServiceConnection)) {
            this.mDesktopDisplayClasses.put(i, cls);
            this.mSystemUIReadyForServiceConnections.put(i, systemUIReadyForServiceConnection);
        }
    }

    public void requestQSNPanel(int i, int i2) {
        SystemUIReadyForServiceConnection systemUIReadyForServiceConnection = this.mSystemUIReadyForServiceConnections.get(i);
        if (systemUIReadyForServiceConnection != null) {
            systemUIReadyForServiceConnection.requestQSNPanel(i2);
            return;
        }
        Log.w("DesktopDRModulesManager", "requestQSNPanel not exist: " + i);
    }

    public int getUnreadNotificationCount(int i) {
        SystemUIReadyForServiceConnection systemUIReadyForServiceConnection = this.mSystemUIReadyForServiceConnections.get(i);
        if (systemUIReadyForServiceConnection != null) {
            return systemUIReadyForServiceConnection.getUnreadNotificationCount();
        }
        Log.w("DesktopDRModulesManager", "getUnreadNotificationCount not exist: " + i);
        return 0;
    }

    public void onTileChanged(ComponentName componentName) {
        if (DEBUG) {
            Log.d("DesktopDRModulesManager", "onTileChanged: " + componentName);
        }
        int size = this.mSystemUIReadyForServiceConnections.size();
        for (int i = 0; i < size; i++) {
            SystemUIReadyForServiceConnection valueAt = this.mSystemUIReadyForServiceConnections.valueAt(i);
            if (valueAt != null) {
                valueAt.onTileChanged(componentName);
            }
        }
    }

    public void updateQsTile(ComponentName componentName, Tile tile) {
        if (DEBUG) {
            Log.d("DesktopDRModulesManager", "updateQsTile: " + componentName);
        }
        int size = this.mSystemUIReadyForServiceConnections.size();
        for (int i = 0; i < size; i++) {
            SystemUIReadyForServiceConnection valueAt = this.mSystemUIReadyForServiceConnections.valueAt(i);
            if (valueAt != null) {
                valueAt.updateQsTile(componentName, tile);
            }
        }
    }

    public void onTileDialogHidden(ComponentName componentName) {
        if (DEBUG) {
            Log.d("DesktopDRModulesManager", "onTileDialogHidden: " + componentName);
        }
        int size = this.mSystemUIReadyForServiceConnections.size();
        for (int i = 0; i < size; i++) {
            SystemUIReadyForServiceConnection valueAt = this.mSystemUIReadyForServiceConnections.valueAt(i);
            if (valueAt != null) {
                valueAt.onTileDialogHidden(componentName);
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeDesktopDisplay(int i, boolean z) {
        if (z) {
            int i2 = 0;
            for (int i3 = 0; i3 < this.mSystemUIReadyForServiceConnections.size(); i3++) {
                SystemUIReadyForServiceConnection valueAt = this.mSystemUIReadyForServiceConnections.valueAt(i3);
                valueAt.destroy();
                this.mContext.unbindService(valueAt);
            }
            this.mDesktopDisplayClasses.clear();
            this.mSystemUIReadyForServiceConnections.clear();
            while (true) {
                Class<?>[] clsArr = sSystemUIReadyForServices;
                if (i2 < clsArr.length) {
                    this.mContext.stopService(new Intent(this.mContext, clsArr[i2]));
                    i2++;
                } else {
                    return;
                }
            }
        } else {
            SystemUIReadyForServiceConnection systemUIReadyForServiceConnection = this.mSystemUIReadyForServiceConnections.get(i);
            if (systemUIReadyForServiceConnection != null) {
                systemUIReadyForServiceConnection.destroy();
                this.mContext.unbindService(systemUIReadyForServiceConnection);
                this.mContext.stopService(new Intent(this.mContext, systemUIReadyForServiceConnection.mServiceClass));
                this.mDesktopDisplayClasses.remove(i);
                this.mSystemUIReadyForServiceConnections.remove(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean bindSystemUIReadyForService(SystemUIReadyForServiceConnection systemUIReadyForServiceConnection) {
        if (systemUIReadyForServiceConnection.mBinding) {
            Log.w("DesktopDRModulesManager", "bindSystemUIReadyForService already binding: " + systemUIReadyForServiceConnection.mDisplayId);
            return false;
        } else if (systemUIReadyForServiceConnection.mDestroyed) {
            Log.w("DesktopDRModulesManager", "bindSystemUIReadyForService already destroyed: " + systemUIReadyForServiceConnection.mDisplayId);
            return false;
        } else {
            Intent intent = new Intent(this.mContext, systemUIReadyForServiceConnection.mServiceClass);
            intent.putExtra("extra_displayid", systemUIReadyForServiceConnection.mDisplayId);
            if (!this.mContext.bindService(intent, systemUIReadyForServiceConnection, 1)) {
                Log.w("DesktopDRModulesManager", "bindSystemUIReadyForService error: " + systemUIReadyForServiceConnection.mDisplayId);
                return false;
            }
            Log.d("DesktopDRModulesManager", "bindSystemUIReadyForService binding: " + systemUIReadyForServiceConnection.mDisplayId);
            boolean unused = systemUIReadyForServiceConnection.mBinding = true;
            return true;
        }
    }

    private class SystemUIReadyForServiceConnection implements ServiceConnection {
        /* access modifiers changed from: private */
        public boolean mBinding = false;
        /* access modifiers changed from: private */
        public boolean mDestroyed = false;
        /* access modifiers changed from: private */
        public final int mDisplayId;
        private ISystemUIReadyForService mISystemUIReadyForService;
        private final ISystemUIReadyForServiceCallback mISystemUIReadyForServiceCallback = new ISystemUIReadyForServiceCallback.Stub() {
            public void onUnreadNotificationCountChanged(int i) {
                if (DesktopDisplayRootModulesManager.DEBUG) {
                    Log.d("DesktopDRModulesManager", "onUnreadNotificationCountChanged: " + i + "; displayId: " + SystemUIReadyForServiceConnection.this.mDisplayId);
                }
                DesktopDisplayRootModulesManager.this.mBgHandler.post(new C2742x4ca57d70(this, i));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onUnreadNotificationCountChanged$0(int i) {
                DesktopDisplayRootModulesManager.this.mMotoTaskBarController.onUnreadNotificationCountChanged(SystemUIReadyForServiceConnection.this.mDisplayId, i);
            }

            public void onTileStartListening(ComponentName componentName) {
                MotoMainProcessTileServiceManager findMotoMainProcessTileServiceManager = DesktopDisplayRootModulesManager.this.mTileServices.findMotoMainProcessTileServiceManager(componentName);
                if (findMotoMainProcessTileServiceManager == null) {
                    Log.w("DesktopDRModulesManager", "onTileStartListening null: " + SystemUIReadyForServiceConnection.this.mDisplayId + "; component:" + componentName);
                    return;
                }
                findMotoMainProcessTileServiceManager.getMotoMainProcessIQSTileService().setListening(SystemUIReadyForServiceConnection.this.mDisplayId, true);
            }

            public void onTileStopListening(ComponentName componentName) {
                MotoMainProcessTileServiceManager findMotoMainProcessTileServiceManager = DesktopDisplayRootModulesManager.this.mTileServices.findMotoMainProcessTileServiceManager(componentName);
                if (findMotoMainProcessTileServiceManager == null) {
                    Log.w("DesktopDRModulesManager", "onTileStopListening null: " + SystemUIReadyForServiceConnection.this.mDisplayId + "; component:" + componentName);
                    return;
                }
                findMotoMainProcessTileServiceManager.getMotoMainProcessIQSTileService().setListening(SystemUIReadyForServiceConnection.this.mDisplayId, false);
            }

            public void onTileClick(ComponentName componentName, IBinder iBinder) {
                MotoMainProcessTileServiceManager findMotoMainProcessTileServiceManager = DesktopDisplayRootModulesManager.this.mTileServices.findMotoMainProcessTileServiceManager(componentName);
                if (findMotoMainProcessTileServiceManager == null) {
                    Log.w("DesktopDRModulesManager", "onTileClick null: " + SystemUIReadyForServiceConnection.this.mDisplayId + "; component:" + componentName);
                    return;
                }
                findMotoMainProcessTileServiceManager.getMotoMainProcessIQSTileService().onClickFromDesktop(SystemUIReadyForServiceConnection.this.mDisplayId, iBinder);
            }

            public void setTileBindRequested(ComponentName componentName, boolean z) {
                MotoMainProcessTileServiceManager findMotoMainProcessTileServiceManager = DesktopDisplayRootModulesManager.this.mTileServices.findMotoMainProcessTileServiceManager(componentName);
                if (findMotoMainProcessTileServiceManager == null) {
                    Log.w("DesktopDRModulesManager", "setTileBindRequested null: " + SystemUIReadyForServiceConnection.this.mDisplayId + "; component:" + componentName);
                    return;
                }
                findMotoMainProcessTileServiceManager.setBindRequested(SystemUIReadyForServiceConnection.this.mDisplayId, z);
            }
        };
        private int mPendingAction = -2;
        /* access modifiers changed from: private */
        public final Class<?> mServiceClass;

        public SystemUIReadyForServiceConnection(int i, Class<?> cls) {
            this.mDisplayId = i;
            this.mServiceClass = cls;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.mBinding = false;
            if (iBinder == null) {
                Log.e("DesktopDRModulesManager", "onServiceConnected with null service: " + this.mDisplayId);
                DesktopDisplayRootModulesManager.this.removeDesktopDisplay(this.mDisplayId, false);
                return;
            }
            Log.i("DesktopDRModulesManager", "onServiceConnected: " + this.mDisplayId);
            ISystemUIReadyForService asInterface = ISystemUIReadyForService.Stub.asInterface(iBinder);
            this.mISystemUIReadyForService = asInterface;
            try {
                asInterface.setTaskBarServiceCallback(this.mISystemUIReadyForServiceCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int i = this.mPendingAction;
            if (i != -2) {
                requestQSNPanel(i);
                this.mPendingAction = -2;
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("DesktopDRModulesManager", "onServiceDisconnected: " + this.mDisplayId);
            this.mISystemUIReadyForService = null;
            this.mBinding = false;
        }

        public void onBindingDied(ComponentName componentName) {
            Log.e("DesktopDRModulesManager", "onBindingDied: " + this.mDisplayId);
            this.mISystemUIReadyForService = null;
            this.mBinding = false;
            boolean unused = DesktopDisplayRootModulesManager.this.bindSystemUIReadyForService(this);
        }

        public void onNullBinding(ComponentName componentName) {
            Log.e("DesktopDRModulesManager", "onNullBinding: " + this.mDisplayId);
            this.mISystemUIReadyForService = null;
            this.mBinding = false;
        }

        public void requestQSNPanel(int i) {
            ISystemUIReadyForService iSystemUIReadyForService = this.mISystemUIReadyForService;
            if (iSystemUIReadyForService != null) {
                try {
                    iSystemUIReadyForService.requestQSNPanel(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                this.mPendingAction = i;
            }
        }

        public int getUnreadNotificationCount() {
            ISystemUIReadyForService iSystemUIReadyForService = this.mISystemUIReadyForService;
            if (iSystemUIReadyForService == null) {
                return 0;
            }
            try {
                return iSystemUIReadyForService.getUnreadNotificationCount();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        public void onTileChanged(ComponentName componentName) {
            ISystemUIReadyForService iSystemUIReadyForService = this.mISystemUIReadyForService;
            if (iSystemUIReadyForService != null) {
                try {
                    iSystemUIReadyForService.onTileChanged(componentName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateQsTile(ComponentName componentName, Tile tile) {
            ISystemUIReadyForService iSystemUIReadyForService = this.mISystemUIReadyForService;
            if (iSystemUIReadyForService != null) {
                try {
                    iSystemUIReadyForService.updateQsTile(componentName, tile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void onTileDialogHidden(ComponentName componentName) {
            ISystemUIReadyForService iSystemUIReadyForService = this.mISystemUIReadyForService;
            if (iSystemUIReadyForService != null) {
                try {
                    iSystemUIReadyForService.onTileDialogHidden(componentName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void destroy() {
            this.mDestroyed = true;
        }
    }
}
