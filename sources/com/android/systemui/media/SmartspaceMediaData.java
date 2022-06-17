package com.android.systemui.media;

import android.app.smartspace.SmartspaceAction;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SmartspaceMediaData.kt */
public final class SmartspaceMediaData {
    private final int backgroundColor;
    @Nullable
    private final SmartspaceAction cardAction;
    private final boolean isActive;
    private final boolean isValid;
    @NotNull
    private final String packageName;
    @NotNull
    private final List<SmartspaceAction> recommendations;
    @NotNull
    private final String targetId;

    public static /* synthetic */ SmartspaceMediaData copy$default(SmartspaceMediaData smartspaceMediaData, String str, boolean z, boolean z2, String str2, SmartspaceAction smartspaceAction, List<SmartspaceAction> list, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = smartspaceMediaData.targetId;
        }
        if ((i2 & 2) != 0) {
            z = smartspaceMediaData.isActive;
        }
        boolean z3 = z;
        if ((i2 & 4) != 0) {
            z2 = smartspaceMediaData.isValid;
        }
        boolean z4 = z2;
        if ((i2 & 8) != 0) {
            str2 = smartspaceMediaData.packageName;
        }
        String str3 = str2;
        if ((i2 & 16) != 0) {
            smartspaceAction = smartspaceMediaData.cardAction;
        }
        SmartspaceAction smartspaceAction2 = smartspaceAction;
        if ((i2 & 32) != 0) {
            list = smartspaceMediaData.recommendations;
        }
        List<SmartspaceAction> list2 = list;
        if ((i2 & 64) != 0) {
            i = smartspaceMediaData.backgroundColor;
        }
        return smartspaceMediaData.copy(str, z3, z4, str3, smartspaceAction2, list2, i);
    }

    @NotNull
    public final SmartspaceMediaData copy(@NotNull String str, boolean z, boolean z2, @NotNull String str2, @Nullable SmartspaceAction smartspaceAction, @NotNull List<SmartspaceAction> list, int i) {
        Intrinsics.checkNotNullParameter(str, "targetId");
        Intrinsics.checkNotNullParameter(str2, "packageName");
        Intrinsics.checkNotNullParameter(list, "recommendations");
        return new SmartspaceMediaData(str, z, z2, str2, smartspaceAction, list, i);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SmartspaceMediaData)) {
            return false;
        }
        SmartspaceMediaData smartspaceMediaData = (SmartspaceMediaData) obj;
        return Intrinsics.areEqual((Object) this.targetId, (Object) smartspaceMediaData.targetId) && this.isActive == smartspaceMediaData.isActive && this.isValid == smartspaceMediaData.isValid && Intrinsics.areEqual((Object) this.packageName, (Object) smartspaceMediaData.packageName) && Intrinsics.areEqual((Object) this.cardAction, (Object) smartspaceMediaData.cardAction) && Intrinsics.areEqual((Object) this.recommendations, (Object) smartspaceMediaData.recommendations) && this.backgroundColor == smartspaceMediaData.backgroundColor;
    }

    public int hashCode() {
        int hashCode = this.targetId.hashCode() * 31;
        boolean z = this.isActive;
        boolean z2 = true;
        if (z) {
            z = true;
        }
        int i = (hashCode + (z ? 1 : 0)) * 31;
        boolean z3 = this.isValid;
        if (!z3) {
            z2 = z3;
        }
        int hashCode2 = (((i + (z2 ? 1 : 0)) * 31) + this.packageName.hashCode()) * 31;
        SmartspaceAction smartspaceAction = this.cardAction;
        return ((((hashCode2 + (smartspaceAction == null ? 0 : smartspaceAction.hashCode())) * 31) + this.recommendations.hashCode()) * 31) + Integer.hashCode(this.backgroundColor);
    }

    @NotNull
    public String toString() {
        return "SmartspaceMediaData(targetId=" + this.targetId + ", isActive=" + this.isActive + ", isValid=" + this.isValid + ", packageName=" + this.packageName + ", cardAction=" + this.cardAction + ", recommendations=" + this.recommendations + ", backgroundColor=" + this.backgroundColor + ')';
    }

    public SmartspaceMediaData(@NotNull String str, boolean z, boolean z2, @NotNull String str2, @Nullable SmartspaceAction smartspaceAction, @NotNull List<SmartspaceAction> list, int i) {
        Intrinsics.checkNotNullParameter(str, "targetId");
        Intrinsics.checkNotNullParameter(str2, "packageName");
        Intrinsics.checkNotNullParameter(list, "recommendations");
        this.targetId = str;
        this.isActive = z;
        this.isValid = z2;
        this.packageName = str2;
        this.cardAction = smartspaceAction;
        this.recommendations = list;
        this.backgroundColor = i;
    }

    @NotNull
    public final String getTargetId() {
        return this.targetId;
    }

    public final boolean isActive() {
        return this.isActive;
    }

    public final boolean isValid() {
        return this.isValid;
    }

    @NotNull
    public final String getPackageName() {
        return this.packageName;
    }

    @Nullable
    public final SmartspaceAction getCardAction() {
        return this.cardAction;
    }

    @NotNull
    public final List<SmartspaceAction> getRecommendations() {
        return this.recommendations;
    }

    public final int getBackgroundColor() {
        return this.backgroundColor;
    }
}
