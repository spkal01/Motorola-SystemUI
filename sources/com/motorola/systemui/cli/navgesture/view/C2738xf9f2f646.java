package com.motorola.systemui.cli.navgesture.view;

import com.motorola.systemui.cli.navgesture.view.ISwipeDismissView;
import com.motorola.systemui.cli.navgesture.view.RecentsViewContainer;

/* renamed from: com.motorola.systemui.cli.navgesture.view.RecentsViewContainer$RecentsViewAdapter$RecentsViewHolder$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C2738xf9f2f646 implements ISwipeDismissView.OnDismissedListener {
    public final /* synthetic */ RecentsViewContainer.IOnTaskItemOperationCallback f$0;
    public final /* synthetic */ RecentsViewContainer.TaskParamsOverride f$1;

    public /* synthetic */ C2738xf9f2f646(RecentsViewContainer.IOnTaskItemOperationCallback iOnTaskItemOperationCallback, RecentsViewContainer.TaskParamsOverride taskParamsOverride) {
        this.f$0 = iOnTaskItemOperationCallback;
        this.f$1 = taskParamsOverride;
    }

    public final void onDismissed(ISwipeDismissView iSwipeDismissView) {
        this.f$0.performDismiss(this.f$1.task);
    }
}
