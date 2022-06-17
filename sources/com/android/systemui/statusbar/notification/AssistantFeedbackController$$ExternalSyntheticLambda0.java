package com.android.systemui.statusbar.notification;

import java.util.concurrent.Executor;

public final /* synthetic */ class AssistantFeedbackController$$ExternalSyntheticLambda0 implements Executor {
    public final /* synthetic */ AssistantFeedbackController f$0;

    public /* synthetic */ AssistantFeedbackController$$ExternalSyntheticLambda0(AssistantFeedbackController assistantFeedbackController) {
        this.f$0 = assistantFeedbackController;
    }

    public final void execute(Runnable runnable) {
        this.f$0.postToHandler(runnable);
    }
}
