package com.android.systemui.volume;

import android.content.pm.ApplicationInfo;
import java.util.function.Predicate;

public final /* synthetic */ class VolumeDialogImpl$$ExternalSyntheticLambda27 implements Predicate {
    public final /* synthetic */ int f$0;

    public /* synthetic */ VolumeDialogImpl$$ExternalSyntheticLambda27(int i) {
        this.f$0 = i;
    }

    public final boolean test(Object obj) {
        return VolumeDialogImpl.lambda$getApplicationForUid$26(this.f$0, (ApplicationInfo) obj);
    }
}
