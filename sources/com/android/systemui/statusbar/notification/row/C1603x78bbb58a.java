package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import java.util.Comparator;

/* renamed from: com.android.systemui.statusbar.notification.row.ChannelEditorDialogController$getDisplayableChannels$$inlined$compareBy$1 */
/* compiled from: Comparisons.kt */
public final class C1603x78bbb58a<T> implements Comparator<T> {
    public final int compare(T t, T t2) {
        NotificationChannel notificationChannel = (NotificationChannel) t;
        CharSequence name = notificationChannel.getName();
        String str = null;
        String obj = name == null ? null : name.toString();
        if (obj == null) {
            obj = notificationChannel.getId();
        }
        NotificationChannel notificationChannel2 = (NotificationChannel) t2;
        CharSequence name2 = notificationChannel2.getName();
        if (name2 != null) {
            str = name2.toString();
        }
        if (str == null) {
            str = notificationChannel2.getId();
        }
        return ComparisonsKt__ComparisonsKt.compareValues(obj, str);
    }
}
