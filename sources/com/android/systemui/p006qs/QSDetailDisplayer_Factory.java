package com.android.systemui.p006qs;

import dagger.internal.Factory;

/* renamed from: com.android.systemui.qs.QSDetailDisplayer_Factory */
public final class QSDetailDisplayer_Factory implements Factory<QSDetailDisplayer> {

    /* renamed from: com.android.systemui.qs.QSDetailDisplayer_Factory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final QSDetailDisplayer_Factory INSTANCE = new QSDetailDisplayer_Factory();
    }

    public QSDetailDisplayer get() {
        return newInstance();
    }

    public static QSDetailDisplayer_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static QSDetailDisplayer newInstance() {
        return new QSDetailDisplayer();
    }
}
