package com.android.systemui.p006qs.tiles;

import android.content.DialogInterface;
import com.android.systemui.statusbar.phone.SystemUIDialog;

/* renamed from: com.android.systemui.qs.tiles.HotspotTile$$ExternalSyntheticLambda0 */
public final /* synthetic */ class HotspotTile$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ HotspotTile f$0;
    public final /* synthetic */ SystemUIDialog f$1;

    public /* synthetic */ HotspotTile$$ExternalSyntheticLambda0(HotspotTile hotspotTile, SystemUIDialog systemUIDialog) {
        this.f$0 = hotspotTile;
        this.f$1 = systemUIDialog;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createWarningDialog$0(this.f$1, dialogInterface, i);
    }
}
