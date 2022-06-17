package com.android.systemui.statusbar.phone.ongoingcall;

/* compiled from: OngoingCallController.kt */
final class OngoingCallController$setUpUidObserver$1$onUidStateChanged$1 implements Runnable {
    final /* synthetic */ OngoingCallController this$0;

    OngoingCallController$setUpUidObserver$1$onUidStateChanged$1(OngoingCallController ongoingCallController) {
        this.this$0 = ongoingCallController;
    }

    public final void run() {
        for (OngoingCallListener onOngoingCallStateChanged : this.this$0.mListeners) {
            onOngoingCallStateChanged.onOngoingCallStateChanged(true);
        }
    }
}
