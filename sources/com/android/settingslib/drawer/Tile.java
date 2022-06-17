package com.android.settingslib.drawer;

import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class Tile implements Parcelable {
    public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator<Tile>() {
        public Tile createFromParcel(Parcel parcel) {
            boolean readBoolean = parcel.readBoolean();
            parcel.setDataPosition(0);
            return readBoolean ? new ProviderTile(parcel) : new ActivityTile(parcel);
        }

        public Tile[] newArray(int i) {
            return new Tile[i];
        }
    };
    public static final Comparator<Tile> TILE_COMPARATOR = Tile$$ExternalSyntheticLambda0.INSTANCE;
    private String mCategory;
    protected ComponentInfo mComponentInfo;
    private final String mComponentName;
    private final String mComponentPackage;
    private final Intent mIntent;
    long mLastUpdateTime;
    private Bundle mMetaData;
    public ArrayList<UserHandle> userHandle = new ArrayList<>();

    public int describeContents() {
        return 0;
    }

    Tile(Parcel parcel) {
        parcel.readBoolean();
        String readString = parcel.readString();
        this.mComponentPackage = readString;
        String readString2 = parcel.readString();
        this.mComponentName = readString2;
        this.mIntent = new Intent().setClassName(readString, readString2);
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.userHandle.add((UserHandle) UserHandle.CREATOR.createFromParcel(parcel));
        }
        this.mCategory = parcel.readString();
        this.mMetaData = parcel.readBundle();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this instanceof ProviderTile);
        parcel.writeString(this.mComponentPackage);
        parcel.writeString(this.mComponentName);
        int size = this.userHandle.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.userHandle.get(i2).writeToParcel(parcel, i);
        }
        parcel.writeString(this.mCategory);
        parcel.writeBundle(this.mMetaData);
    }

    public int getOrder() {
        if (hasOrder()) {
            return this.mMetaData.getInt("com.android.settings.order");
        }
        return 0;
    }

    public boolean hasOrder() {
        return this.mMetaData.containsKey("com.android.settings.order") && (this.mMetaData.get("com.android.settings.order") instanceof Integer);
    }

    public Bundle getMetaData() {
        return this.mMetaData;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(Tile tile, Tile tile2) {
        return tile2.getOrder() - tile.getOrder();
    }
}
