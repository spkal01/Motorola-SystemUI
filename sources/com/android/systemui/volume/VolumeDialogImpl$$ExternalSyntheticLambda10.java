package com.android.systemui.volume;

import android.view.View;
import com.android.systemui.volume.VolumeDialogImpl;

public final /* synthetic */ class VolumeDialogImpl$$ExternalSyntheticLambda10 implements View.OnClickListener {
    public final /* synthetic */ VolumeDialogImpl f$0;
    public final /* synthetic */ VolumeDialogImpl.VolumeRow f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ VolumeDialogImpl$$ExternalSyntheticLambda10(VolumeDialogImpl volumeDialogImpl, VolumeDialogImpl.VolumeRow volumeRow, int i) {
        this.f$0 = volumeDialogImpl;
        this.f$1 = volumeRow;
        this.f$2 = i;
    }

    public final void onClick(View view) {
        this.f$0.lambda$initRow$6(this.f$1, this.f$2, view);
    }
}
