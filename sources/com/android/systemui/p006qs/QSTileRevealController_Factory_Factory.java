package com.android.systemui.p006qs;

import android.content.Context;
import com.android.systemui.p006qs.QSTileRevealController;
import com.android.systemui.p006qs.customize.QSCustomizerController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.QSTileRevealController_Factory_Factory */
public final class QSTileRevealController_Factory_Factory implements Factory<QSTileRevealController.Factory> {
    private final Provider<Context> contextProvider;
    private final Provider<QSCustomizerController> qsCustomizerControllerProvider;

    public QSTileRevealController_Factory_Factory(Provider<Context> provider, Provider<QSCustomizerController> provider2) {
        this.contextProvider = provider;
        this.qsCustomizerControllerProvider = provider2;
    }

    public QSTileRevealController.Factory get() {
        return newInstance(this.contextProvider.get(), this.qsCustomizerControllerProvider.get());
    }

    public static QSTileRevealController_Factory_Factory create(Provider<Context> provider, Provider<QSCustomizerController> provider2) {
        return new QSTileRevealController_Factory_Factory(provider, provider2);
    }

    public static QSTileRevealController.Factory newInstance(Context context, QSCustomizerController qSCustomizerController) {
        return new QSTileRevealController.Factory(context, qSCustomizerController);
    }
}
