package com.android.systemui.controls.controller;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/* compiled from: StatefulControlSubscriber.kt */
final class StatefulControlSubscriber$run$1 implements Runnable {

    /* renamed from: $f */
    final /* synthetic */ Function0<Unit> f84$f;

    StatefulControlSubscriber$run$1(Function0<Unit> function0) {
        this.f84$f = function0;
    }

    public final void run() {
        this.f84$f.invoke();
    }
}
