package com.android.systemui.p006qs.customize;

import android.content.Context;
import com.android.systemui.p006qs.tileimpl.QSTileViewPrcImpl;
import com.android.systemui.plugins.p005qs.QSIconView;
import com.android.systemui.plugins.p005qs.QSTile;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.qs.customize.PrcCustomizeTileView */
/* compiled from: PrcCustomizeTileView.kt */
public final class PrcCustomizeTileView extends QSTileViewPrcImpl {
    private boolean showAppLabel;
    private boolean showSideView = true;

    private final int getVisibilityState(CharSequence charSequence) {
        return 8;
    }

    /* access modifiers changed from: protected */
    public boolean animationsEnabled() {
        return false;
    }

    public boolean isLongClickable() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PrcCustomizeTileView(@NotNull Context context, @NotNull QSIconView qSIconView) {
        super(context, qSIconView, false);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(qSIconView, "icon");
    }

    public final void setShowAppLabel(boolean z) {
        this.showAppLabel = z;
        getSecondaryLabel().setVisibility(getVisibilityState(getSecondaryLabel().getText()));
    }

    public final void setShowSideView(boolean z) {
        this.showSideView = z;
        if (!z) {
            getSideView().setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void handleStateChanged(@NotNull QSTile.State state) {
        Intrinsics.checkNotNullParameter(state, "state");
        super.handleStateChanged(state);
        setShowRippleEffect(false);
        getSecondaryLabel().setVisibility(getVisibilityState(state.secondaryLabel));
        if (!this.showSideView) {
            getSideView().setVisibility(8);
        }
    }

    public final void changeState(@NotNull QSTile.State state) {
        Intrinsics.checkNotNullParameter(state, "state");
        handleStateChanged(state);
    }
}
