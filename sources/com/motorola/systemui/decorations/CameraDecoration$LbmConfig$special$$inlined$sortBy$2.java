package com.motorola.systemui.decorations;

import com.motorola.systemui.decorations.CameraDecoration;
import java.util.Comparator;

/* compiled from: Comparisons.kt */
public final class CameraDecoration$LbmConfig$special$$inlined$sortBy$2<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        return ComparisonsKt__ComparisonsKt.compareValues(Float.valueOf(((CameraDecoration.LbmConfig.Config) t).getLight()), Float.valueOf(((CameraDecoration.LbmConfig.Config) t2).getLight()));
    }
}
