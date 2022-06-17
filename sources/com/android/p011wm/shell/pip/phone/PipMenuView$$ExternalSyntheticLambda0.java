package com.android.p011wm.shell.pip.phone;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

/* renamed from: com.android.wm.shell.pip.phone.PipMenuView$$ExternalSyntheticLambda0 */
public final /* synthetic */ class PipMenuView$$ExternalSyntheticLambda0 implements Icon.OnDrawableLoadedListener {
    public final /* synthetic */ PipMenuActionView f$0;

    public /* synthetic */ PipMenuView$$ExternalSyntheticLambda0(PipMenuActionView pipMenuActionView) {
        this.f$0 = pipMenuActionView;
    }

    public final void onDrawableLoaded(Drawable drawable) {
        PipMenuView.lambda$updateActionViews$5(this.f$0, drawable);
    }
}
