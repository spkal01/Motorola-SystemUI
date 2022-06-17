package com.android.p011wm.shell.pip.phone;

import android.view.InputEvent;
import com.android.p011wm.shell.pip.phone.PipInputConsumer;

/* renamed from: com.android.wm.shell.pip.phone.PipController$$ExternalSyntheticLambda2 */
public final /* synthetic */ class PipController$$ExternalSyntheticLambda2 implements PipInputConsumer.InputListener {
    public final /* synthetic */ PipTouchHandler f$0;

    public /* synthetic */ PipController$$ExternalSyntheticLambda2(PipTouchHandler pipTouchHandler) {
        this.f$0 = pipTouchHandler;
    }

    public final boolean onInputEvent(InputEvent inputEvent) {
        return this.f$0.handleTouchEvent(inputEvent);
    }
}
