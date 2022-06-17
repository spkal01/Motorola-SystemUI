package com.android.systemui.p006qs.external;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.service.quicksettings.IQSService;
import android.service.quicksettings.Tile;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.external.TileServices */
public class TileServices extends IQSService.Stub {
    private static final Comparator<TileServiceManager> SERVICE_SORT = new Comparator<TileServiceManager>() {
        public int compare(TileServiceManager tileServiceManager, TileServiceManager tileServiceManager2) {
            return -Integer.compare(tileServiceManager.getBindPriority(), tileServiceManager2.getBindPriority());
        }
    };
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final DesktopDisplayRootModulesManager mDesktopDisplayRootModulesManager;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public final QSTileHost mHost;
    private final Handler mMainHandler;
    private int mMaxBound = 5;
    private final ArrayMap<CustomTile, MotoMainProcessTileServiceManager> mMotoServices = new ArrayMap<>();
    private final BroadcastReceiver mRequestListeningReceiver;
    private final ArrayMap<CustomTile, TileServiceManager> mServices = new ArrayMap<>();
    private final ArrayMap<ComponentName, CustomTile> mTiles = new ArrayMap<>();
    private final ArrayMap<IBinder, CustomTile> mTokenMap = new ArrayMap<>();
    private final UserTracker mUserTracker;

    public TileServices(QSTileHost qSTileHost, Looper looper, BroadcastDispatcher broadcastDispatcher, UserTracker userTracker) {
        C12362 r0 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.service.quicksettings.action.REQUEST_LISTENING".equals(intent.getAction())) {
                    TileServices.this.requestListening((ComponentName) intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME"));
                }
            }
        };
        this.mRequestListeningReceiver = r0;
        this.mHost = qSTileHost;
        this.mContext = qSTileHost.getContext();
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mHandler = new Handler(looper);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mUserTracker = userTracker;
        broadcastDispatcher.registerReceiver(r0, new IntentFilter("android.service.quicksettings.action.REQUEST_LISTENING"), (Executor) null, UserHandle.ALL);
        this.mDesktopDisplayRootModulesManager = (DesktopDisplayRootModulesManager) Dependency.get(DesktopDisplayRootModulesManager.class);
    }

    public Context getContext() {
        return this.mContext;
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public TileServiceManager getTileWrapper(CustomTile customTile) {
        ComponentName component = customTile.getComponent();
        TileServiceManager onCreateTileService = onCreateTileService(component, customTile.getQsTile(), this.mBroadcastDispatcher);
        synchronized (this.mServices) {
            this.mServices.put(customTile, onCreateTileService);
            this.mTiles.put(component, customTile);
            this.mTokenMap.put(onCreateTileService.getToken(), customTile);
        }
        onCreateTileService.startLifecycleManagerAndAddTile();
        return onCreateTileService;
    }

    /* access modifiers changed from: protected */
    public TileServiceManager onCreateTileService(ComponentName componentName, Tile tile, BroadcastDispatcher broadcastDispatcher) {
        return new TileServiceManager(this, this.mHandler, componentName, tile, broadcastDispatcher, this.mUserTracker);
    }

    public void freeService(CustomTile customTile, TileServiceManager tileServiceManager) {
        synchronized (this.mServices) {
            tileServiceManager.setBindAllowed(false);
            tileServiceManager.handleDestroy();
            this.mServices.remove(customTile);
            this.mMotoServices.remove(customTile);
            this.mTokenMap.remove(tileServiceManager.getToken());
            this.mTiles.remove(customTile.getComponent());
            this.mMainHandler.post(new TileServices$$ExternalSyntheticLambda0(this, customTile.getComponent().getClassName()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$freeService$0(String str) {
        this.mHost.getIconController().removeAllIconsForSlot(str);
    }

    public void recalculateBindAllowance() {
        ArrayList arrayList;
        synchronized (this.mServices) {
            arrayList = new ArrayList(this.mServices.values());
        }
        int size = arrayList.size();
        if (size > this.mMaxBound) {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                ((TileServiceManager) arrayList.get(i)).calculateBindPriority(currentTimeMillis);
            }
            Collections.sort(arrayList, SERVICE_SORT);
        }
        int i2 = 0;
        while (i2 < this.mMaxBound && i2 < size) {
            ((TileServiceManager) arrayList.get(i2)).setBindAllowed(true);
            i2++;
        }
        while (i2 < size) {
            ((TileServiceManager) arrayList.get(i2)).setBindAllowed(false);
            i2++;
        }
    }

    private void verifyCaller(CustomTile customTile) {
        try {
            if (Binder.getCallingUid() != this.mContext.getPackageManager().getPackageUidAsUser(customTile.getComponent().getPackageName(), Binder.getCallingUserHandle().getIdentifier())) {
                throw new SecurityException("Component outside caller's uid");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new SecurityException(e);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:12|13|14|15|16|17) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x003c */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestListening(android.content.ComponentName r4) {
        /*
            r3 = this;
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r0 = r3.mServices
            monitor-enter(r0)
            com.android.systemui.qs.external.CustomTile r1 = r3.getTileForComponent(r4)     // Catch:{ all -> 0x003e }
            if (r1 != 0) goto L_0x0021
            java.lang.String r3 = "TileServices"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x003e }
            r1.<init>()     // Catch:{ all -> 0x003e }
            java.lang.String r2 = "Couldn't find tile for "
            r1.append(r2)     // Catch:{ all -> 0x003e }
            r1.append(r4)     // Catch:{ all -> 0x003e }
            java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x003e }
            android.util.Log.d(r3, r4)     // Catch:{ all -> 0x003e }
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x0021:
            android.util.ArrayMap<com.android.systemui.qs.external.CustomTile, com.android.systemui.qs.external.TileServiceManager> r3 = r3.mServices     // Catch:{ all -> 0x003e }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x003e }
            com.android.systemui.qs.external.TileServiceManager r3 = (com.android.systemui.p006qs.external.TileServiceManager) r3     // Catch:{ all -> 0x003e }
            boolean r4 = r3.isActiveTile()     // Catch:{ all -> 0x003e }
            if (r4 != 0) goto L_0x0031
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x0031:
            r4 = 1
            r3.setBindRequested(r4)     // Catch:{ all -> 0x003e }
            android.service.quicksettings.IQSTileService r3 = r3.getTileService()     // Catch:{ RemoteException -> 0x003c }
            r3.onStartListening()     // Catch:{ RemoteException -> 0x003c }
        L_0x003c:
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            return
        L_0x003e:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.external.TileServices.requestListening(android.content.ComponentName):void");
    }

    public void updateQsTile(Tile tile, IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            synchronized (this.mServices) {
                TileServiceManager tileServiceManager = this.mServices.get(tileForToken);
                if (tileServiceManager != null) {
                    if (tileServiceManager.isLifecycleStarted()) {
                        tileServiceManager.clearPendingBind();
                        tileServiceManager.setLastUpdate(System.currentTimeMillis());
                        tileForToken.updateTileState(tile);
                        tileForToken.refreshState();
                        this.mDesktopDisplayRootModulesManager.updateQsTile(tileForToken.getComponent(), tile);
                        return;
                    }
                }
                Log.e("TileServices", "TileServiceManager not started for " + tileForToken.getComponent(), new IllegalStateException());
            }
        }
    }

    public void onStartSuccessful(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            synchronized (this.mServices) {
                TileServiceManager tileServiceManager = this.mServices.get(tileForToken);
                if (tileServiceManager != null) {
                    if (tileServiceManager.isLifecycleStarted()) {
                        tileServiceManager.clearPendingBind();
                        tileForToken.refreshState();
                        return;
                    }
                }
                Log.e("TileServices", "TileServiceManager not started for " + tileForToken.getComponent(), new IllegalStateException());
            }
        }
    }

    public void onShowDialog(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            tileForToken.onDialogShown();
            this.mHost.forceCollapsePanels();
            this.mServices.get(tileForToken).setShowingDialog(true);
        }
    }

    public void onDialogHidden(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            this.mServices.get(tileForToken).setShowingDialog(false);
            tileForToken.onDialogHidden();
            this.mDesktopDisplayRootModulesManager.onTileDialogHidden(tileForToken.getComponent());
        }
    }

    public void onStartActivity(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            this.mHost.forceCollapsePanels();
        }
    }

    public void updateStatusIcon(IBinder iBinder, Icon icon, String str) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            try {
                final ComponentName component = tileForToken.getComponent();
                String packageName = component.getPackageName();
                UserHandle callingUserHandle = IQSService.Stub.getCallingUserHandle();
                if (this.mContext.getPackageManager().getPackageInfoAsUser(packageName, 0, callingUserHandle.getIdentifier()).applicationInfo.isSystemApp()) {
                    final StatusBarIcon statusBarIcon = icon != null ? new StatusBarIcon(callingUserHandle, packageName, icon, 0, 0, str) : null;
                    this.mMainHandler.post(new Runnable() {
                        public void run() {
                            StatusBarIconController iconController = TileServices.this.mHost.getIconController();
                            iconController.setIcon(component.getClassName(), statusBarIcon);
                            iconController.setExternalIcon(component.getClassName());
                        }
                    });
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
    }

    public Tile getTile(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken == null) {
            return null;
        }
        verifyCaller(tileForToken);
        return tileForToken.getQsTile();
    }

    public void startUnlockAndRun(IBinder iBinder) {
        CustomTile tileForToken = getTileForToken(iBinder);
        if (tileForToken != null) {
            verifyCaller(tileForToken);
            tileForToken.startUnlockAndRun();
        }
    }

    public boolean isLocked() {
        return ((KeyguardStateController) Dependency.get(KeyguardStateController.class)).isShowing();
    }

    public boolean isSecure() {
        KeyguardStateController keyguardStateController = (KeyguardStateController) Dependency.get(KeyguardStateController.class);
        return keyguardStateController.isMethodSecure() && keyguardStateController.isShowing();
    }

    private CustomTile getTileForToken(IBinder iBinder) {
        CustomTile customTile;
        synchronized (this.mServices) {
            customTile = this.mTokenMap.get(iBinder);
        }
        return customTile;
    }

    private CustomTile getTileForComponent(ComponentName componentName) {
        CustomTile customTile;
        synchronized (this.mServices) {
            customTile = this.mTiles.get(componentName);
        }
        return customTile;
    }

    public MotoTileServiceManager getMotoTileServiceManager(CustomTile customTile) {
        MotoMainProcessTileServiceManager motoMainProcessTileServiceManager = new MotoMainProcessTileServiceManager(this, getTileWrapper(customTile), customTile.getComponent());
        synchronized (this.mServices) {
            this.mMotoServices.put(customTile, motoMainProcessTileServiceManager);
        }
        return motoMainProcessTileServiceManager;
    }

    public void freeService(CustomTile customTile, MotoTileServiceManager motoTileServiceManager) {
        if (motoTileServiceManager instanceof MotoMainProcessTileServiceManager) {
            MotoMainProcessTileServiceManager motoMainProcessTileServiceManager = (MotoMainProcessTileServiceManager) motoTileServiceManager;
            motoMainProcessTileServiceManager.handleDestroy();
            freeService(customTile, motoMainProcessTileServiceManager.getTileServiceManager());
        }
    }

    public MotoMainProcessTileServiceManager findMotoMainProcessTileServiceManager(ComponentName componentName) {
        synchronized (this.mServices) {
            CustomTile customTile = this.mTiles.get(componentName);
            if (customTile == null) {
                return null;
            }
            MotoMainProcessTileServiceManager motoMainProcessTileServiceManager = this.mMotoServices.get(customTile);
            return motoMainProcessTileServiceManager;
        }
    }

    public void onDisplayRemoved(int i) {
        ArrayMap arrayMap = new ArrayMap();
        synchronized (this.mServices) {
            arrayMap.putAll(this.mMotoServices);
        }
        for (int i2 = 0; i2 < arrayMap.size(); i2++) {
            ((MotoMainProcessTileServiceManager) arrayMap.valueAt(i2)).onDisplayRemoved(i);
        }
    }
}
