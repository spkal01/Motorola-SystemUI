package com.android.p011wm.shell.legacysplitscreen;

import com.android.internal.policy.DividerSnapAlgorithm;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.legacysplitscreen.DividerView$$ExternalSyntheticLambda3 */
public final /* synthetic */ class DividerView$$ExternalSyntheticLambda3 implements Consumer {
    public final /* synthetic */ DividerView f$0;
    public final /* synthetic */ DividerSnapAlgorithm.SnapTarget f$1;

    public /* synthetic */ DividerView$$ExternalSyntheticLambda3(DividerView dividerView, DividerSnapAlgorithm.SnapTarget snapTarget) {
        this.f$0 = dividerView;
        this.f$1 = snapTarget;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$getFlingAnimator$3(this.f$1, (Boolean) obj);
    }
}
