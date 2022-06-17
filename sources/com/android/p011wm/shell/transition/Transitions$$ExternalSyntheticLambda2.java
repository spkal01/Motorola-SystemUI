package com.android.p011wm.shell.transition;

import com.android.p011wm.shell.transition.Transitions;
import java.util.function.Function;

/* renamed from: com.android.wm.shell.transition.Transitions$$ExternalSyntheticLambda2 */
public final /* synthetic */ class Transitions$$ExternalSyntheticLambda2 implements Function {
    public static final /* synthetic */ Transitions$$ExternalSyntheticLambda2 INSTANCE = new Transitions$$ExternalSyntheticLambda2();

    private /* synthetic */ Transitions$$ExternalSyntheticLambda2() {
    }

    public final Object apply(Object obj) {
        return ((Transitions.ActiveTransition) obj).mToken;
    }
}
