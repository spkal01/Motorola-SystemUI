package com.android.systemui.volume;

import android.content.pm.ApplicationInfo;
import java.util.Comparator;

public final /* synthetic */ class VolumeDialogImpl$$ExternalSyntheticLambda25 implements Comparator {
    public static final /* synthetic */ VolumeDialogImpl$$ExternalSyntheticLambda25 INSTANCE = new VolumeDialogImpl$$ExternalSyntheticLambda25();

    private /* synthetic */ VolumeDialogImpl$$ExternalSyntheticLambda25() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((ApplicationInfo) obj).packageName.compareTo(((ApplicationInfo) obj2).packageName);
    }
}
