package com.android.systemui.p006qs.external;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.service.quicksettings.IQSTileService;
import android.view.textclassifier.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p006qs.external.TileLifecycleManager;
import com.android.systemui.settings.UserTracker;
import com.motorola.systemui.desktop.SystemUIReadyForServiceCallback;

/* renamed from: com.android.systemui.qs.external.MotoDesktopProcessTileServiceManager */
public class MotoDesktopProcessTileServiceManager extends MotoTileServiceManager {
    private static final boolean DEBUG = (!Build.IS_USER);
    private TileLifecycleManager.TileChangeListener mChangeListener;
    private final ComponentName mComponentName;
    private final int mDisplayId;
    private final MotoDesktopProcessIQSTileService mMotoDesktopProcessIQSTileService;

    public MotoDesktopProcessTileServiceManager(Context context, int i, ComponentName componentName, BroadcastDispatcher broadcastDispatcher, UserTracker userTracker) {
        this.mDisplayId = i;
        this.mComponentName = componentName;
        this.mMotoDesktopProcessIQSTileService = new MotoDesktopProcessIQSTileService(context, i, componentName, broadcastDispatcher, userTracker);
    }

    public IQSTileService getTileService() {
        return this.mMotoDesktopProcessIQSTileService;
    }

    public boolean isToggleableTile() {
        return this.mMotoDesktopProcessIQSTileService.isToggleableTile();
    }

    public void setTileChangeListener(TileLifecycleManager.TileChangeListener tileChangeListener) {
        this.mChangeListener = tileChangeListener;
    }

    public void notifyTileChangeListener() {
        TileLifecycleManager.TileChangeListener tileChangeListener = this.mChangeListener;
        if (tileChangeListener != null) {
            tileChangeListener.onTileChanged(this.mComponentName);
        }
    }

    public boolean isActiveTile() {
        return this.mMotoDesktopProcessIQSTileService.isActiveTile();
    }

    public void clearPendingBind() {
        if (DEBUG) {
            Log.d("MotoDtPTileServiceManager", "clearPendingBind: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
    }

    public void setBindRequested(boolean z) {
        SystemUIReadyForServiceCallback.setTileBindRequested(this.mComponentName, z);
    }

    public boolean hasPendingBind() {
        if (!DEBUG) {
            return false;
        }
        Log.d("MotoDtPTileServiceManager", "hasPendingBind: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        return false;
    }

    public void handleDestroy() {
        this.mMotoDesktopProcessIQSTileService.handleDestroy();
    }
}
