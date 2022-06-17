package com.motorola.systemui.prc.media;

import android.app.PendingIntent;
import android.view.View;

public final /* synthetic */ class MediaTileLayout$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ MediaTileLayout f$0;
    public final /* synthetic */ PendingIntent f$1;

    public /* synthetic */ MediaTileLayout$$ExternalSyntheticLambda0(MediaTileLayout mediaTileLayout, PendingIntent pendingIntent) {
        this.f$0 = mediaTileLayout;
        this.f$1 = pendingIntent;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setClickListenForAction$0(this.f$1, view);
    }
}
