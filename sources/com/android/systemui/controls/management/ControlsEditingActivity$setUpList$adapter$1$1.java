package com.android.systemui.controls.management;

import androidx.recyclerview.widget.RecyclerView;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsEditingActivity.kt */
public final class ControlsEditingActivity$setUpList$adapter$1$1 extends RecyclerView.AdapterDataObserver {
    final /* synthetic */ RecyclerView $recyclerView;
    private boolean hasAnimated;

    ControlsEditingActivity$setUpList$adapter$1$1(RecyclerView recyclerView) {
        this.$recyclerView = recyclerView;
    }

    public void onChanged() {
        if (!this.hasAnimated) {
            this.hasAnimated = true;
            ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
            RecyclerView recyclerView = this.$recyclerView;
            Intrinsics.checkNotNullExpressionValue(recyclerView, "recyclerView");
            controlsAnimations.enterAnimation(recyclerView).start();
        }
    }
}
