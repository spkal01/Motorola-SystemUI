package com.android.systemui.shared.system.smartspace;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SmartspaceState.kt */
public final class SmartspaceState implements Parcelable {
    @NotNull
    public static final CREATOR CREATOR = new CREATOR((DefaultConstructorMarker) null);
    @NotNull
    private Rect boundsOnScreen;
    private int selectedPage;

    public int describeContents() {
        return 0;
    }

    public SmartspaceState() {
        this.boundsOnScreen = new Rect();
    }

    @NotNull
    public final Rect getBoundsOnScreen() {
        return this.boundsOnScreen;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public SmartspaceState(@NotNull Parcel parcel) {
        this();
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        Parcelable readParcelable = parcel.readParcelable(C14201.INSTANCE.getClass().getClassLoader());
        Intrinsics.checkNotNullExpressionValue(readParcelable, "parcel.readParcelable(Rect::javaClass.javaClass.classLoader)");
        this.boundsOnScreen = (Rect) readParcelable;
        this.selectedPage = parcel.readInt();
    }

    public void writeToParcel(@Nullable Parcel parcel, int i) {
        if (parcel != null) {
            parcel.writeParcelable(this.boundsOnScreen, 0);
        }
        if (parcel != null) {
            parcel.writeInt(this.selectedPage);
        }
    }

    @NotNull
    public String toString() {
        return "boundsOnScreen: " + this.boundsOnScreen + ", selectedPage: " + this.selectedPage;
    }

    /* compiled from: SmartspaceState.kt */
    public static final class CREATOR implements Parcelable.Creator<SmartspaceState> {
        public /* synthetic */ CREATOR(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private CREATOR() {
        }

        @NotNull
        public SmartspaceState createFromParcel(@NotNull Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new SmartspaceState(parcel);
        }

        @NotNull
        public SmartspaceState[] newArray(int i) {
            return new SmartspaceState[i];
        }
    }
}
