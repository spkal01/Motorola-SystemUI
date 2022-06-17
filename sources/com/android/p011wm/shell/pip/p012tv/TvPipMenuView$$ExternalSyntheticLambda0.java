package com.android.p011wm.shell.pip.p012tv;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

/* renamed from: com.android.wm.shell.pip.tv.TvPipMenuView$$ExternalSyntheticLambda0 */
public final /* synthetic */ class TvPipMenuView$$ExternalSyntheticLambda0 implements Icon.OnDrawableLoadedListener {
    public final /* synthetic */ TvPipMenuActionButton f$0;

    public /* synthetic */ TvPipMenuView$$ExternalSyntheticLambda0(TvPipMenuActionButton tvPipMenuActionButton) {
        this.f$0 = tvPipMenuActionButton;
    }

    public final void onDrawableLoaded(Drawable drawable) {
        TvPipMenuView.lambda$setAdditionalActions$0(this.f$0, drawable);
    }
}
