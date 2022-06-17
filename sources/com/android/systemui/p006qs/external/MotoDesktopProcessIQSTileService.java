package com.android.systemui.p006qs.external;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.quicksettings.IQSService;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.view.textclassifier.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.UserTracker;
import com.motorola.systemui.desktop.SystemUIReadyForServiceCallback;

/* renamed from: com.android.systemui.qs.external.MotoDesktopProcessIQSTileService */
public class MotoDesktopProcessIQSTileService implements IQSTileService {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final ComponentName mComponentName;
    private final Context mContext;
    private final int mDisplayId;
    private final TileLifecycleManager mFakeTileLifecycleManager;

    public IBinder asBinder() {
        return null;
    }

    public MotoDesktopProcessIQSTileService(Context context, int i, ComponentName componentName, BroadcastDispatcher broadcastDispatcher, UserTracker userTracker) {
        this.mContext = context;
        this.mDisplayId = i;
        this.mComponentName = componentName;
        this.mFakeTileLifecycleManager = new TileLifecycleManager(new Handler(), context, (IQSService) null, new Tile(), new Intent().setComponent(componentName), userTracker.getUserHandle(), broadcastDispatcher);
    }

    public void onTileAdded() {
        if (DEBUG) {
            Log.d("MotoDtPIQSTileService", "onTileAdded: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
    }

    public void onTileRemoved() {
        if (DEBUG) {
            Log.d("MotoDtPIQSTileService", "onTileRemoved: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
    }

    public void onStartListening() {
        if (DEBUG) {
            Log.d("MotoDtPIQSTileService", "onStartListening: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
        SystemUIReadyForServiceCallback.onTileStartListening(this.mComponentName);
    }

    public void onStopListening() {
        if (DEBUG) {
            Log.d("MotoDtPIQSTileService", "onStopListening: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
        SystemUIReadyForServiceCallback.onTileStopListening(this.mComponentName);
    }

    public void onClick(IBinder iBinder) {
        if (DEBUG) {
            Log.d("MotoDtPIQSTileService", "onClick: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
        SystemUIReadyForServiceCallback.onTileClick(this.mComponentName, iBinder);
    }

    public void onUnlockComplete() {
        if (DEBUG) {
            Log.d("MotoDtPIQSTileService", "onUnlockComplete: " + this.mDisplayId + "; mComponentName: " + this.mComponentName);
        }
    }

    public boolean isToggleableTile() {
        return this.mFakeTileLifecycleManager.isToggleableTile();
    }

    public boolean isActiveTile() {
        return this.mFakeTileLifecycleManager.isActiveTile();
    }

    public void handleDestroy() {
        this.mFakeTileLifecycleManager.handleDestroy();
    }
}
