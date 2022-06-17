package com.android.systemui.volume;

import android.view.View;
import com.android.systemui.volume.VolumeDialogImpl;

public final /* synthetic */ class VolumeDialogImpl$$ExternalSyntheticLambda9 implements View.OnClickListener {
    public final /* synthetic */ VolumeDialogImpl f$0;
    public final /* synthetic */ VolumeDialogImpl.AppVolumeRow f$1;

    public /* synthetic */ VolumeDialogImpl$$ExternalSyntheticLambda9(VolumeDialogImpl volumeDialogImpl, VolumeDialogImpl.AppVolumeRow appVolumeRow) {
        this.f$0 = volumeDialogImpl;
        this.f$1 = appVolumeRow;
    }

    public final void onClick(View view) {
        this.f$0.lambda$initAppVolumeRow$25(this.f$1, view);
    }
}
