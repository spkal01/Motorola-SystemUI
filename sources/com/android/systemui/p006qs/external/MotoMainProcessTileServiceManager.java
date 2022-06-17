package com.android.systemui.p006qs.external;

import android.content.ComponentName;
import android.os.Build;
import android.service.quicksettings.IQSTileService;
import android.view.textclassifier.Log;
import com.android.systemui.Dependency;
import com.android.systemui.p006qs.external.TileLifecycleManager;
import com.motorola.systemui.desktop.DesktopDisplayRootModulesManager;

/* renamed from: com.android.systemui.qs.external.MotoMainProcessTileServiceManager */
public class MotoMainProcessTileServiceManager extends MotoTileServiceManager {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final BestTrue mBindRequested = new BestTrue(false);
    /* access modifiers changed from: private */
    public TileLifecycleManager.TileChangeListener mChangeListener;
    private final ComponentName mComponentName;
    /* access modifiers changed from: private */
    public final DesktopDisplayRootModulesManager mDesktopDisplayRootModulesManager;
    private final MotoMainProcessIQSTileService mMotoMainProcessIQSTileService;
    private final TileServiceManager mRealTileServiceManager;
    private final TileServices mTileServices;

    public MotoMainProcessTileServiceManager(TileServices tileServices, TileServiceManager tileServiceManager, ComponentName componentName) {
        this.mTileServices = tileServices;
        this.mRealTileServiceManager = tileServiceManager;
        this.mComponentName = componentName;
        this.mMotoMainProcessIQSTileService = new MotoMainProcessIQSTileService(tileServiceManager.getTileLifecycleManager(), componentName);
        this.mDesktopDisplayRootModulesManager = (DesktopDisplayRootModulesManager) Dependency.get(DesktopDisplayRootModulesManager.class);
    }

    public IQSTileService getTileService() {
        return this.mMotoMainProcessIQSTileService;
    }

    public boolean isToggleableTile() {
        return this.mRealTileServiceManager.isToggleableTile();
    }

    public void setTileChangeListener(TileLifecycleManager.TileChangeListener tileChangeListener) {
        this.mChangeListener = tileChangeListener;
        this.mRealTileServiceManager.setTileChangeListener(new TileLifecycleManager.TileChangeListener() {
            public void onTileChanged(ComponentName componentName) {
                if (MotoMainProcessTileServiceManager.this.mChangeListener != null) {
                    MotoMainProcessTileServiceManager.this.mChangeListener.onTileChanged(componentName);
                }
                MotoMainProcessTileServiceManager.this.mDesktopDisplayRootModulesManager.onTileChanged(componentName);
            }
        });
    }

    public boolean isActiveTile() {
        return this.mRealTileServiceManager.isActiveTile();
    }

    public void clearPendingBind() {
        this.mRealTileServiceManager.clearPendingBind();
    }

    public void setBindRequested(boolean z) {
        setBindRequested(0, z);
    }

    public boolean hasPendingBind() {
        return this.mRealTileServiceManager.hasPendingBind();
    }

    public void handleDestroy() {
        this.mRealTileServiceManager.handleDestroy();
        this.mMotoMainProcessIQSTileService.handleDestroy();
    }

    public void setBindRequested(int i, boolean z) {
        boolean z2 = this.mBindRequested.get();
        boolean put = this.mBindRequested.put(i, z);
        boolean isBindRequested = this.mRealTileServiceManager.isBindRequested();
        if (DEBUG) {
            Log.d("MotoMPIQSTileServiceManager", "setBindRequested: " + i + "; oldListening: " + z2 + "; newBindRequested: " + put + "; reallyBindRequested: " + isBindRequested + "; componentName: " + this.mComponentName);
        }
        if (z2 != put || put != isBindRequested) {
            setBindRequestedInternal(put);
        }
    }

    public void onDisplayRemoved(int i) {
        boolean z = this.mBindRequested.get();
        boolean delete = this.mBindRequested.delete(i);
        boolean isBindRequested = this.mRealTileServiceManager.isBindRequested();
        if (DEBUG) {
            Log.d("MotoMPIQSTileServiceManager", "onDisplayRemoved: " + i + "; oldListening: " + z + "; newBindRequested: " + delete + "; reallyBindRequested: " + isBindRequested + "; componentName: " + this.mComponentName);
        }
        if (!(z == delete && delete == isBindRequested)) {
            setBindRequestedInternal(delete);
        }
        this.mMotoMainProcessIQSTileService.onDisplayRemoved(i);
    }

    private void setBindRequestedInternal(boolean z) {
        if (DEBUG) {
            Log.d("MotoMPIQSTileServiceManager", "setBindRequestedInternal: " + z + "; componentName: " + this.mComponentName);
        }
        this.mRealTileServiceManager.setBindRequested(z);
    }

    public TileServiceManager getTileServiceManager() {
        return this.mRealTileServiceManager;
    }

    public MotoMainProcessIQSTileService getMotoMainProcessIQSTileService() {
        return this.mMotoMainProcessIQSTileService;
    }
}
