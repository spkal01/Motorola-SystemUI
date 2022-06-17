package com.android.systemui.media;

import android.view.View;

public final /* synthetic */ class MediaControlPanel$$ExternalSyntheticLambda9 implements View.OnClickListener {
    public final /* synthetic */ MediaControlPanel f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ MediaData f$2;

    public /* synthetic */ MediaControlPanel$$ExternalSyntheticLambda9(MediaControlPanel mediaControlPanel, String str, MediaData mediaData) {
        this.f$0 = mediaControlPanel;
        this.f$1 = str;
        this.f$2 = mediaData;
    }

    public final void onClick(View view) {
        this.f$0.lambda$bindPlayer$11(this.f$1, this.f$2, view);
    }
}
