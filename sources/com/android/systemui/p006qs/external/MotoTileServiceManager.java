package com.android.systemui.p006qs.external;

import android.service.quicksettings.IQSTileService;
import com.android.systemui.p006qs.external.TileLifecycleManager;

/* renamed from: com.android.systemui.qs.external.MotoTileServiceManager */
public abstract class MotoTileServiceManager {
    public abstract void clearPendingBind();

    public abstract IQSTileService getTileService();

    public abstract void handleDestroy();

    public abstract boolean hasPendingBind();

    public abstract boolean isActiveTile();

    public abstract boolean isToggleableTile();

    public abstract void setBindRequested(boolean z);

    public abstract void setTileChangeListener(TileLifecycleManager.TileChangeListener tileChangeListener);
}
