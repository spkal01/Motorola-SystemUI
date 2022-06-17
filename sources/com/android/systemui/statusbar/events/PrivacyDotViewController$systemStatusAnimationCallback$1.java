package com.android.systemui.statusbar.events;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;
import kotlin.Unit;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyDotViewController.kt */
public final class PrivacyDotViewController$systemStatusAnimationCallback$1 implements SystemStatusAnimationCallback {
    final /* synthetic */ PrivacyDotViewController this$0;

    PrivacyDotViewController$systemStatusAnimationCallback$1(PrivacyDotViewController privacyDotViewController) {
        this.this$0 = privacyDotViewController;
    }

    @Nullable
    public Animator onSystemStatusAnimationTransitionToPersistentDot(@Nullable String str) {
        Object access$getLock$p = this.this$0.lock;
        PrivacyDotViewController privacyDotViewController = this.this$0;
        synchronized (access$getLock$p) {
            privacyDotViewController.setNextViewState(ViewState.copy$default(privacyDotViewController.nextViewState, false, true, false, false, (Rect) null, (Rect) null, (Rect) null, (Rect) null, false, 0, 0, 0, (View) null, str, 8189, (Object) null));
            Unit unit = Unit.INSTANCE;
        }
        return null;
    }

    @Nullable
    public Animator onHidePersistentDot() {
        Object access$getLock$p = this.this$0.lock;
        PrivacyDotViewController privacyDotViewController = this.this$0;
        synchronized (access$getLock$p) {
            privacyDotViewController.setNextViewState(ViewState.copy$default(privacyDotViewController.nextViewState, false, false, false, false, (Rect) null, (Rect) null, (Rect) null, (Rect) null, false, 0, 0, 0, (View) null, (String) null, 16381, (Object) null));
            Unit unit = Unit.INSTANCE;
        }
        return null;
    }
}
