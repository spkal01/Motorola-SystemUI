package com.android.p011wm.shell.pip;

import android.media.session.MediaSessionManager;
import java.util.List;

/* renamed from: com.android.wm.shell.pip.PipMediaController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class PipMediaController$$ExternalSyntheticLambda0 implements MediaSessionManager.OnActiveSessionsChangedListener {
    public final /* synthetic */ PipMediaController f$0;

    public /* synthetic */ PipMediaController$$ExternalSyntheticLambda0(PipMediaController pipMediaController) {
        this.f$0 = pipMediaController;
    }

    public final void onActiveSessionsChanged(List list) {
        this.f$0.resolveActiveMediaController(list);
    }
}
