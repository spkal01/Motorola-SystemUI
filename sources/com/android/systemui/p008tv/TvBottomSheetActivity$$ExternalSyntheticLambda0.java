package com.android.systemui.p008tv;

import java.util.function.Consumer;

/* renamed from: com.android.systemui.tv.TvBottomSheetActivity$$ExternalSyntheticLambda0 */
public final /* synthetic */ class TvBottomSheetActivity$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ TvBottomSheetActivity f$0;

    public /* synthetic */ TvBottomSheetActivity$$ExternalSyntheticLambda0(TvBottomSheetActivity tvBottomSheetActivity) {
        this.f$0 = tvBottomSheetActivity;
    }

    public final void accept(Object obj) {
        this.f$0.onBlurChanged(((Boolean) obj).booleanValue());
    }
}
