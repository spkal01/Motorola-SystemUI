package com.android.systemui.p006qs.external;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.util.ArrayMap;
import android.view.textclassifier.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.UserTracker;

/* renamed from: com.android.systemui.qs.external.MotoDesktopProcessTileServices */
public class MotoDesktopProcessTileServices {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final int mDisplayId;
    private final ArrayMap<CustomTile, MotoDesktopProcessTileServiceManager> mMotoServices = new ArrayMap<>();
    private final ArrayMap<ComponentName, CustomTile> mTiles = new ArrayMap<>();
    private final Handler mUIHandler;
    private final UserTracker mUserTracker;

    public MotoDesktopProcessTileServices(Context context, BroadcastDispatcher broadcastDispatcher, Handler handler, UserTracker userTracker) {
        this.mContext = context;
        this.mDisplayId = context.getDisplayId();
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mUserTracker = userTracker;
        this.mUIHandler = handler;
    }

    public MotoTileServiceManager getMotoTileServiceManager(CustomTile customTile) {
        MotoDesktopProcessTileServiceManager motoDesktopProcessTileServiceManager = new MotoDesktopProcessTileServiceManager(this.mContext, this.mDisplayId, customTile.getComponent(), this.mBroadcastDispatcher, this.mUserTracker);
        customTile.getQsTile();
        synchronized (this.mMotoServices) {
            this.mMotoServices.put(customTile, motoDesktopProcessTileServiceManager);
            this.mTiles.put(customTile.getComponent(), customTile);
        }
        return motoDesktopProcessTileServiceManager;
    }

    public void freeService(CustomTile customTile, MotoTileServiceManager motoTileServiceManager) {
        if (DEBUG) {
            Log.d("MotoDesktopProcessTileServices", "freeService: " + this.mDisplayId + "; tile: " + customTile.getComponent());
        }
        synchronized (this.mMotoServices) {
            motoTileServiceManager.handleDestroy();
            this.mMotoServices.remove(customTile);
            this.mTiles.remove(customTile.getComponent());
        }
    }

    private MotoDesktopProcessTileServiceManager findMotoDesktopProcessTileServiceManager(ComponentName componentName) {
        synchronized (this.mMotoServices) {
            CustomTile customTile = this.mTiles.get(componentName);
            if (customTile == null) {
                return null;
            }
            MotoDesktopProcessTileServiceManager motoDesktopProcessTileServiceManager = this.mMotoServices.get(customTile);
            return motoDesktopProcessTileServiceManager;
        }
    }

    private CustomTile findCustomTile(ComponentName componentName) {
        CustomTile customTile;
        synchronized (this.mMotoServices) {
            customTile = this.mTiles.get(componentName);
        }
        return customTile;
    }

    public void onTileChanged(ComponentName componentName) {
        if (DEBUG) {
            Log.d("MotoDesktopProcessTileServices", "onTileChanged: " + this.mDisplayId + "; component: " + componentName);
        }
        this.mUIHandler.post(new MotoDesktopProcessTileServices$$ExternalSyntheticLambda1(this, componentName));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTileChanged$0(ComponentName componentName) {
        MotoDesktopProcessTileServiceManager findMotoDesktopProcessTileServiceManager = findMotoDesktopProcessTileServiceManager(componentName);
        if (findMotoDesktopProcessTileServiceManager != null) {
            findMotoDesktopProcessTileServiceManager.notifyTileChangeListener();
        }
    }

    public void updateQsTile(ComponentName componentName, Tile tile) {
        if (DEBUG) {
            Log.d("MotoDesktopProcessTileServices", "updateQsTile: " + this.mDisplayId + "; component: " + componentName);
        }
        this.mUIHandler.post(new MotoDesktopProcessTileServices$$ExternalSyntheticLambda2(this, componentName, tile));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateQsTile$1(ComponentName componentName, Tile tile) {
        CustomTile findCustomTile = findCustomTile(componentName);
        if (findCustomTile != null) {
            findCustomTile.updateTileState(tile);
            findCustomTile.refreshState();
        }
    }

    public void onTileDialogHidden(ComponentName componentName) {
        if (DEBUG) {
            Log.d("MotoDesktopProcessTileServices", "onTileDialogHidden: " + this.mDisplayId + "; component: " + componentName);
        }
        this.mUIHandler.post(new MotoDesktopProcessTileServices$$ExternalSyntheticLambda0(this, componentName));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTileDialogHidden$2(ComponentName componentName) {
        CustomTile findCustomTile = findCustomTile(componentName);
        if (findCustomTile != null) {
            findCustomTile.onDialogHidden();
        }
    }
}
