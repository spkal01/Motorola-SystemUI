package com.android.systemui.p006qs.tiles;

import android.content.DialogInterface;
import com.android.systemui.statusbar.phone.SystemUIDialog;

/* renamed from: com.android.systemui.qs.tiles.WifiTile$$ExternalSyntheticLambda0 */
public final /* synthetic */ class WifiTile$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ WifiTile f$0;
    public final /* synthetic */ SystemUIDialog f$1;

    public /* synthetic */ WifiTile$$ExternalSyntheticLambda0(WifiTile wifiTile, SystemUIDialog systemUIDialog) {
        this.f$0 = wifiTile;
        this.f$1 = systemUIDialog;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createWarningDialog$0(this.f$1, dialogInterface, i);
    }
}
