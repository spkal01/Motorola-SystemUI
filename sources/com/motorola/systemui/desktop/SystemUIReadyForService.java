package com.motorola.systemui.desktop;

import android.app.Service;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.window.WindowContext;
import com.android.systemui.Dependency;
import com.android.systemui.p006qs.external.MotoDesktopProcessTileServices;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.motorola.systemui.desktop.util.DesktopDisplayContext;
import com.motorola.taskbar.ISystemUIReadyForService;
import com.motorola.taskbar.ISystemUIReadyForServiceCallback;

public abstract class SystemUIReadyForService extends Service {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private ISystemUIReadyForService.Stub mBinder = new ISystemUIReadyForService.Stub() {
        public void setTaskBarServiceCallback(ISystemUIReadyForServiceCallback iSystemUIReadyForServiceCallback) throws RemoteException {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "setTaskBarServiceCallback displayId: " + SystemUIReadyForService.this.mDisplayId);
            }
            ISystemUIReadyForServiceCallback unused = SystemUIReadyForService.this.mISystemUIReadyForServiceCallback = iSystemUIReadyForServiceCallback;
            SystemUIReadyForServiceCallback.setISystemUIReadyForServiceCallback(SystemUIReadyForService.this.mISystemUIReadyForServiceCallback);
        }

        public int getUnreadNotificationCount() throws RemoteException {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "getUnreadNotificationCount, displayId: " + SystemUIReadyForService.this.mDisplayId);
            }
            return SystemUIReadyForService.this.mNotificationEntryManager.getUnReadNotificationSize();
        }

        public void requestQSNPanel(int i) throws RemoteException {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "requestQSNPanel: " + i + "; displayId: " + SystemUIReadyForService.this.mDisplayId);
            }
            if (i == -1) {
                SystemUIReadyForService.this.mModule.getDesktopStatusBar().requestHidePanel();
            } else if (i == 0) {
                SystemUIReadyForService.this.mModule.getDesktopStatusBar().requestTogglePanel();
            } else if (i == 1) {
                SystemUIReadyForService.this.mModule.getDesktopStatusBar().requestShowPanel();
            }
        }

        public void onTileChanged(ComponentName componentName) {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "onTileChanged: " + SystemUIReadyForService.this.mDisplayId + "; component: " + componentName);
            }
            SystemUIReadyForService.this.mMotoDesktopProcessTileServices.onTileChanged(componentName);
        }

        public void updateQsTile(ComponentName componentName, Tile tile) {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "updateQsTile: " + SystemUIReadyForService.this.mDisplayId + "; component: " + componentName + "; tile: " + tile);
            }
            SystemUIReadyForService.this.mMotoDesktopProcessTileServices.updateQsTile(componentName, tile);
        }

        public void onTileDialogHidden(ComponentName componentName) {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "onTileDialogHidden: " + SystemUIReadyForService.this.mDisplayId + "; component: " + componentName);
            }
            SystemUIReadyForService.this.mMotoDesktopProcessTileServices.onTileDialogHidden(componentName);
        }
    };
    /* access modifiers changed from: private */
    public DesktopDisplayContext mDesktopDisplayContext;
    private final Configuration mDesktopDisplayContextLastConfiguration = new Configuration();
    private ComponentCallbacks mDisplayContextComponentCallbacks = new ComponentCallbacks() {
        public void onLowMemory() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            Log.d("SystemUIReadyForService", "DisplayContext onConfigurationChanged: " + SystemUIReadyForService.this.mDisplayId + "; configuration: " + configuration);
            SystemUIReadyForService.this.handleConfigurationChanged(configuration);
        }
    };
    protected int mDisplayId;
    /* access modifiers changed from: private */
    public ISystemUIReadyForServiceCallback mISystemUIReadyForServiceCallback;
    protected DesktopDisplayRootModule mModule;
    /* access modifiers changed from: private */
    public MotoDesktopProcessTileServices mMotoDesktopProcessTileServices;
    /* access modifiers changed from: private */
    public NotificationEntryManager mNotificationEntryManager = null;
    private final NotificationEntryManager.UnReadNotificationListener mUnReadNotificationListener = new NotificationEntryManager.UnReadNotificationListener() {
        public void onUnReadNotificationSizeChanged(int i) {
            if (SystemUIReadyForService.DEBUG) {
                Log.d("SystemUIReadyForService", "onUnReadNotificationSizeChanged: " + i + "; displayId: " + SystemUIReadyForService.this.mDisplayId);
            }
            if (SystemUIReadyForService.this.mISystemUIReadyForServiceCallback != null) {
                try {
                    SystemUIReadyForService.this.mISystemUIReadyForServiceCallback.onUnreadNotificationCountChanged(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private WindowContext mWindowContext;
    private ComponentCallbacks mWindowContextComponentCallbacks = new ComponentCallbacks() {
        public void onLowMemory() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            Log.d("SystemUIReadyForService", "WindowContext onConfigurationChanged: " + SystemUIReadyForService.this.mDisplayId + "; configuration: " + configuration);
            SystemUIReadyForService systemUIReadyForService = SystemUIReadyForService.this;
            systemUIReadyForService.handleConfigurationChanged(systemUIReadyForService.mDesktopDisplayContext.getResources().getConfiguration());
        }
    };

    public void onCreate() {
        if (DEBUG) {
            Log.d("SystemUIReadyForService", "onCreate");
        }
        super.onCreate();
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d("SystemUIReadyForService", "onDestroy displayId: " + this.mDisplayId);
        }
        SystemUIReadyForServiceCallback.setISystemUIReadyForServiceCallback((ISystemUIReadyForServiceCallback) null);
        WindowContext windowContext = this.mWindowContext;
        if (windowContext != null) {
            windowContext.unregisterComponentCallbacks(this.mWindowContextComponentCallbacks);
        }
        DesktopDisplayContext desktopDisplayContext = this.mDesktopDisplayContext;
        if (desktopDisplayContext != null) {
            desktopDisplayContext.unregisterComponentCallbacks(this.mDisplayContextComponentCallbacks);
        }
        super.onDestroy();
        System.exit(0);
    }

    public IBinder onBind(Intent intent) {
        int intExtra = intent.getIntExtra("extra_displayid", 0);
        if (DesktopSystemUIFactory.getDesktopFactory() == null) {
            this.mDisplayId = intExtra;
            DesktopSystemUIFactory create = DesktopSystemUIFactory.create(getApplication(), intExtra);
            if (create == null) {
                Log.w("SystemUIReadyForService", "initializeDisplay create factory failed: " + intExtra);
                return null;
            }
            this.mWindowContext = create.getWindowContext();
            this.mDesktopDisplayContext = create.getDesktopDisplayContext();
            DesktopDisplayRootModule desktopDisplayRootModule = new DesktopDisplayRootModule(create, intExtra);
            this.mModule = desktopDisplayRootModule;
            desktopDisplayRootModule.start();
            this.mNotificationEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
            this.mMotoDesktopProcessTileServices = (MotoDesktopProcessTileServices) Dependency.get(MotoDesktopProcessTileServices.class);
            this.mNotificationEntryManager.setUnReadNotificationListener(this.mUnReadNotificationListener);
            this.mWindowContext.registerComponentCallbacks(this.mWindowContextComponentCallbacks);
            this.mDesktopDisplayContext.registerComponentCallbacks(this.mDisplayContextComponentCallbacks);
            Log.d("SystemUIReadyForService", "onBind successfully displayId = " + intExtra);
        } else if (this.mDisplayId != intExtra) {
            Log.w("SystemUIReadyForService", "onBind displayId not match: displayId = " + intExtra + ", mDisplayId = " + this.mDisplayId);
            return null;
        }
        return this.mBinder;
    }

    /* access modifiers changed from: private */
    public void handleConfigurationChanged(Configuration configuration) {
        DesktopSystemUIFactory desktopFactory;
        Configuration configuration2 = this.mDesktopDisplayContextLastConfiguration;
        int updateFrom = configuration2.updateFrom(Configuration.generateDelta(configuration2, configuration));
        Log.d("SystemUIReadyForService", "handleConfigurationChanged: " + this.mDisplayId + "; configChanges: " + Integer.toHexString(updateFrom));
        if (updateFrom != 0 && (desktopFactory = DesktopSystemUIFactory.getDesktopFactory()) != null) {
            desktopFactory.getSysUIComponent().getConfigurationController().onConfigurationChanged(this.mDesktopDisplayContextLastConfiguration);
        }
    }
}
