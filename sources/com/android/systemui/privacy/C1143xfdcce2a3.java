package com.android.systemui.privacy;

import com.android.systemui.privacy.PrivacyDialog;
import java.util.Comparator;

/* renamed from: com.android.systemui.privacy.PrivacyDialogController$filterAndSelect$lambda-6$$inlined$sortedByDescending$1 */
/* compiled from: Comparisons.kt */
public final class C1143xfdcce2a3<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        return ComparisonsKt__ComparisonsKt.compareValues(Long.valueOf(((PrivacyDialog.PrivacyElement) t2).getLastActiveTimestamp()), Long.valueOf(((PrivacyDialog.PrivacyElement) t).getLastActiveTimestamp()));
    }
}
