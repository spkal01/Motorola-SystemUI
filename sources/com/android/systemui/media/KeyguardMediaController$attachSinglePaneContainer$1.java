package com.android.systemui.media;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;

/* compiled from: KeyguardMediaController.kt */
/* synthetic */ class KeyguardMediaController$attachSinglePaneContainer$1 extends FunctionReferenceImpl implements Function1<Boolean, Unit> {
    KeyguardMediaController$attachSinglePaneContainer$1(KeyguardMediaController keyguardMediaController) {
        super(1, keyguardMediaController, KeyguardMediaController.class, "onMediaHostVisibilityChanged", "onMediaHostVisibilityChanged(Z)V", 0);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke(((Boolean) obj).booleanValue());
        return Unit.INSTANCE;
    }

    public final void invoke(boolean z) {
        ((KeyguardMediaController) this.receiver).onMediaHostVisibilityChanged(z);
    }
}
