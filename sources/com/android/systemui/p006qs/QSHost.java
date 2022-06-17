package com.android.systemui.p006qs;

import android.content.Context;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.p006qs.external.TileServices;

/* renamed from: com.android.systemui.qs.QSHost */
public interface QSHost {

    /* renamed from: com.android.systemui.qs.QSHost$Callback */
    public interface Callback {
        void onTilesChanged();
    }

    void collapsePanels();

    void forceCollapsePanels();

    Context getContext();

    InstanceId getNewInstanceId();

    TileServices getTileServices();

    UiEventLogger getUiEventLogger();

    Context getUserContext();

    int getUserId();

    int indexOf(String str);

    void openPanels();

    void removeTile(String str);

    void unmarkTileAsAutoAdded(String str);

    void warn(String str, Throwable th);
}
