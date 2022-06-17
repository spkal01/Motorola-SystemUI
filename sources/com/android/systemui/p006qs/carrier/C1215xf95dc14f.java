package com.android.systemui.p006qs.carrier;

import com.android.systemui.p006qs.carrier.QSCarrierGroupController;
import dagger.internal.Factory;

/* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController_SubscriptionManagerSlotIndexResolver_Factory */
public final class C1215xf95dc14f implements Factory<QSCarrierGroupController.SubscriptionManagerSlotIndexResolver> {

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController_SubscriptionManagerSlotIndexResolver_Factory$InstanceHolder */
    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final C1215xf95dc14f INSTANCE = new C1215xf95dc14f();
    }

    public QSCarrierGroupController.SubscriptionManagerSlotIndexResolver get() {
        return newInstance();
    }

    public static C1215xf95dc14f create() {
        return InstanceHolder.INSTANCE;
    }

    public static QSCarrierGroupController.SubscriptionManagerSlotIndexResolver newInstance() {
        return new QSCarrierGroupController.SubscriptionManagerSlotIndexResolver();
    }
}
