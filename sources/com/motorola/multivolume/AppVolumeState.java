package com.motorola.multivolume;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public final class AppVolumeState implements Parcelable {
    public static final Parcelable.Creator<AppVolumeState> CREATOR = new Parcelable.Creator<AppVolumeState>() {
        public AppVolumeState createFromParcel(Parcel parcel) {
            return new AppVolumeState(parcel);
        }

        public AppVolumeState[] newArray(int i) {
            return new AppVolumeState[i];
        }
    };
    public int MAX_PROGRESS = 1500;
    public int MIN_PROGRESS = 0;
    public boolean active = false;
    public int appLevel = -1;
    public boolean forceToShow = false;
    public boolean foreground = false;
    public boolean foregroundSettings = false;
    public Drawable icon = null;
    public String label = null;
    public int lastSetLevel = -1;
    public String packageName = null;
    public int packagePid = -1;
    public int packageUid = -1;
    public double percentage = -1.0d;
    public boolean playing = false;
    public int progress = -1;
    public double ratio = -1.0d;
    public boolean shouldBeVisible = false;
    public double storedPercentage = -1.0d;
    public long timeInMills = -1;
    public int uiType = 1;
    public long userAttempt = -1;

    public int describeContents() {
        return 0;
    }

    public AppVolumeState() {
    }

    protected AppVolumeState(Parcel parcel) {
        this.packageUid = parcel.readInt();
        this.packagePid = parcel.readInt();
        this.packageName = parcel.readString();
        this.label = parcel.readString();
        this.icon = new BitmapDrawable((Bitmap) parcel.readParcelable(AppVolumeState.class.getClassLoader()));
        this.uiType = parcel.readInt();
        this.playing = parcel.readBoolean();
        this.foreground = parcel.readBoolean();
        this.foregroundSettings = parcel.readBoolean();
        this.active = parcel.readBoolean();
        this.shouldBeVisible = parcel.readBoolean();
        this.forceToShow = parcel.readBoolean();
        this.timeInMills = parcel.readLong();
        this.userAttempt = parcel.readLong();
        this.MAX_PROGRESS = parcel.readInt();
        this.MIN_PROGRESS = parcel.readInt();
        this.progress = parcel.readInt();
        this.appLevel = parcel.readInt();
        this.lastSetLevel = parcel.readInt();
        this.ratio = parcel.readDouble();
        this.storedPercentage = parcel.readDouble();
        this.percentage = parcel.readDouble();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.packageUid);
        parcel.writeInt(this.packagePid);
        parcel.writeString(this.packageName);
        parcel.writeString(this.label);
        Drawable drawable = this.icon;
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            parcel.writeParcelable((Parcelable) null, i);
        } else {
            parcel.writeParcelable(((BitmapDrawable) drawable).getBitmap(), i);
        }
        parcel.writeInt(this.uiType);
        parcel.writeBoolean(this.playing);
        parcel.writeBoolean(this.foreground);
        parcel.writeBoolean(this.foregroundSettings);
        parcel.writeBoolean(this.active);
        parcel.writeBoolean(this.shouldBeVisible);
        parcel.writeBoolean(this.forceToShow);
        parcel.writeLong(this.timeInMills);
        parcel.writeLong(this.userAttempt);
        parcel.writeInt(this.MAX_PROGRESS);
        parcel.writeInt(this.MIN_PROGRESS);
        parcel.writeInt(this.progress);
        parcel.writeInt(this.appLevel);
        parcel.writeInt(this.lastSetLevel);
        parcel.writeDouble(this.ratio);
        parcel.writeDouble(this.storedPercentage);
        parcel.writeDouble(this.percentage);
    }

    public AppVolumeState copy() {
        AppVolumeState appVolumeState = new AppVolumeState();
        appVolumeState.packageUid = this.packageUid;
        appVolumeState.packagePid = this.packagePid;
        appVolumeState.packageName = this.packageName;
        appVolumeState.label = this.label;
        appVolumeState.icon = this.icon;
        appVolumeState.uiType = this.uiType;
        appVolumeState.playing = this.playing;
        appVolumeState.foreground = this.foreground;
        appVolumeState.foregroundSettings = this.foregroundSettings;
        appVolumeState.active = this.active;
        appVolumeState.shouldBeVisible = this.shouldBeVisible;
        appVolumeState.forceToShow = this.forceToShow;
        appVolumeState.timeInMills = this.timeInMills;
        appVolumeState.userAttempt = this.userAttempt;
        appVolumeState.MAX_PROGRESS = this.MAX_PROGRESS;
        appVolumeState.MIN_PROGRESS = this.MIN_PROGRESS;
        appVolumeState.progress = this.progress;
        appVolumeState.appLevel = this.appLevel;
        appVolumeState.lastSetLevel = this.lastSetLevel;
        appVolumeState.ratio = this.ratio;
        appVolumeState.storedPercentage = this.storedPercentage;
        appVolumeState.percentage = this.percentage;
        return appVolumeState;
    }

    public String toString() {
        return "label: " + this.label + ", uid: " + this.packageUid + ", packageName: " + this.packageName + ", playing: " + this.playing + ", foreground: " + this.foreground + ", foregroundSettings: " + this.foregroundSettings + ", active: " + this.active + ", shouldBeVisible: " + this.shouldBeVisible + ", forceToShow: " + this.forceToShow + ", progress: " + this.progress + ", appLevel: " + this.appLevel + ", lastSetLevel: " + this.lastSetLevel + ", ratio: " + this.ratio + ", storedPercentage: " + this.storedPercentage + ", percentage: " + this.percentage + ", userAttempt: " + this.userAttempt + ", timeInMills: " + this.timeInMills;
    }

    public String getAbbreviation() {
        return this.label + ":" + this.packageName + ":" + this.packageUid + ":" + this.packagePid + '[' + this.progress + ".." + this.appLevel + ".." + this.ratio + ".." + this.storedPercentage + ".." + this.percentage + ".." + this.playing + ".." + this.foreground + ".." + this.foregroundSettings + ".." + this.active + ".." + this.shouldBeVisible + ".." + this.forceToShow + ".." + this.timeInMills + ']';
    }

    public static String getAbbreviations(List<AppVolumeState> list, int i) {
        StringBuilder sb = new StringBuilder("App Volume States: {");
        for (int i2 = 0; i2 < list.size(); i2++) {
            if (i2 > 0) {
                sep(sb, i);
            }
            sb.append(list.get(i2).getAbbreviation());
        }
        sb.append('}');
        return sb.toString();
    }

    private static void sep(StringBuilder sb, int i) {
        if (i > 0) {
            sb.append(10);
            for (int i2 = 0; i2 < i; i2++) {
                sb.append(' ');
            }
            return;
        }
        sb.append(',');
    }
}
