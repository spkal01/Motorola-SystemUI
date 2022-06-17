package com.android.systemui.model;

import com.android.systemui.model.SysUiState;
import java.util.function.Consumer;

public final /* synthetic */ class SysUiState$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ int f$0;

    public /* synthetic */ SysUiState$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void accept(Object obj) {
        ((SysUiState.SysUiStateCallback) obj).onSystemUiStateChanged(this.f$0);
    }
}
