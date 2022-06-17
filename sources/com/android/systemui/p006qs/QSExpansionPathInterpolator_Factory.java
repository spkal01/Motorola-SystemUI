package com.android.systemui.p006qs;

import dagger.internal.Factory;

/* renamed from: com.android.systemui.qs.QSExpansionPathInterpolator_Factory */
public final class QSExpansionPathInterpolator_Factory implements Factory<QSExpansionPathInterpolator> {

    /* renamed from: com.android.systemui.qs.QSExpansionPathInterpolator_Factory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final QSExpansionPathInterpolator_Factory INSTANCE = new QSExpansionPathInterpolator_Factory();
    }

    public QSExpansionPathInterpolator get() {
        return newInstance();
    }

    public static QSExpansionPathInterpolator_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static QSExpansionPathInterpolator newInstance() {
        return new QSExpansionPathInterpolator();
    }
}
