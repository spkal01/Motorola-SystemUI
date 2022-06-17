package com.android.systemui.media;

import android.view.View;

public final /* synthetic */ class MediaControlPanel$$ExternalSyntheticLambda8 implements View.OnClickListener {
    public final /* synthetic */ MediaControlPanel f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ MediaControlPanel$$ExternalSyntheticLambda8(MediaControlPanel mediaControlPanel, Runnable runnable) {
        this.f$0 = mediaControlPanel;
        this.f$1 = runnable;
    }

    public final void onClick(View view) {
        this.f$0.lambda$bindPlayer$9(this.f$1, view);
    }
}
