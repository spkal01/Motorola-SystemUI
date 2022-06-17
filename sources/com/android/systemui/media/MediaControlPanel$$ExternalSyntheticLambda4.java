package com.android.systemui.media;

import android.app.PendingIntent;
import android.view.View;

public final /* synthetic */ class MediaControlPanel$$ExternalSyntheticLambda4 implements View.OnClickListener {
    public final /* synthetic */ MediaControlPanel f$0;
    public final /* synthetic */ PendingIntent f$1;

    public /* synthetic */ MediaControlPanel$$ExternalSyntheticLambda4(MediaControlPanel mediaControlPanel, PendingIntent pendingIntent) {
        this.f$0 = mediaControlPanel;
        this.f$1 = pendingIntent;
    }

    public final void onClick(View view) {
        this.f$0.lambda$bindPlayer$7(this.f$1, view);
    }
}
