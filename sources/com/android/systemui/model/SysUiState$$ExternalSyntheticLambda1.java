package com.android.systemui.model;

import com.android.systemui.model.SysUiState;
import java.util.function.Consumer;

public final /* synthetic */ class SysUiState$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SysUiState$$ExternalSyntheticLambda1(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final void accept(Object obj) {
        ((SysUiState.SysUiStateCallback) obj).onSystemUiStateChanged(this.f$0, this.f$1);
    }
}
