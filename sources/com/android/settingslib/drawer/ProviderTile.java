package com.android.settingslib.drawer;

import android.content.pm.ProviderInfo;
import android.os.Parcel;

public class ProviderTile extends Tile {
    private String mAuthority = ((ProviderInfo) this.mComponentInfo).authority;
    private String mKey = getMetaData().getString("com.android.settings.keyhint");

    ProviderTile(Parcel parcel) {
        super(parcel);
    }
}
