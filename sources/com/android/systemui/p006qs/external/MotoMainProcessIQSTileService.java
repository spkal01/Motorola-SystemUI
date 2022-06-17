package com.android.systemui.p006qs.external;

import android.content.ComponentName;
import android.os.Build;
import android.os.IBinder;
import android.service.quicksettings.IQSTileService;
import android.view.textclassifier.Log;

/* renamed from: com.android.systemui.qs.external.MotoMainProcessIQSTileService */
public class MotoMainProcessIQSTileService implements IQSTileService {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final ComponentName mComponentName;
    private final BestTrue mListening = new BestTrue(false);
    private final TileLifecycleManager mTileLifecycleManager;

    public MotoMainProcessIQSTileService(TileLifecycleManager tileLifecycleManager, ComponentName componentName) {
        this.mTileLifecycleManager = tileLifecycleManager;
        this.mComponentName = componentName;
    }

    public void onTileAdded() {
        this.mTileLifecycleManager.onTileAdded();
    }

    public void onTileRemoved() {
        this.mTileLifecycleManager.onTileRemoved();
    }

    public void onStartListening() {
        setListening(0, true);
    }

    public void onStopListening() {
        setListening(0, false);
    }

    public void onClick(IBinder iBinder) {
        this.mTileLifecycleManager.onClick(iBinder);
    }

    public void onUnlockComplete() {
        this.mTileLifecycleManager.onUnlockComplete();
    }

    public IBinder asBinder() {
        return this.mTileLifecycleManager.asBinder();
    }

    public void handleDestroy() {
        this.mTileLifecycleManager.handleDestroy();
    }

    public void setListening(int i, boolean z) {
        boolean z2 = this.mListening.get();
        boolean put = this.mListening.put(i, z);
        boolean isListening = this.mTileLifecycleManager.isListening();
        if (DEBUG) {
            Log.d("MotoMPIQSTileService", "setListening: " + i + "; listening: " + z + "; oldListening: " + z2 + "; newListening: " + put + "; reallyListening: " + isListening + "; mComponentName: " + this.mComponentName);
        }
        if (z2 != put || put != isListening) {
            setListeningInternal(put);
        }
    }

    public void onDisplayRemoved(int i) {
        boolean z = this.mListening.get();
        boolean delete = this.mListening.delete(i);
        boolean isListening = this.mTileLifecycleManager.isListening();
        if (DEBUG) {
            Log.d("MotoMPIQSTileService", "onDisplayRemoved: " + i + "; oldListening: " + z + "; newListening: " + delete + "; reallyListening: " + isListening + "; mComponentName: " + this.mComponentName);
        }
        if (z != delete || delete != isListening) {
            setListeningInternal(delete);
        }
    }

    private void setListeningInternal(boolean z) {
        if (DEBUG) {
            Log.d("MotoMPIQSTileService", "setListeningInternal: " + z + "; mComponentName: " + this.mComponentName);
        }
        if (z) {
            this.mTileLifecycleManager.onStartListening();
        } else {
            this.mTileLifecycleManager.onStopListening();
        }
    }

    public void onClickFromDesktop(int i, IBinder iBinder) {
        if (DEBUG) {
            Log.d("MotoMPIQSTileService", "onClickFromDesktop: " + i + "; component: " + this.mComponentName);
        }
        onClick(iBinder);
    }
}
