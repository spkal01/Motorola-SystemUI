package com.android.systemui.recents;

import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.function.Function;

public final /* synthetic */ class ScreenPinningRequest$RequestWindowView$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ ScreenPinningRequest$RequestWindowView$$ExternalSyntheticLambda0 INSTANCE = new ScreenPinningRequest$RequestWindowView$$ExternalSyntheticLambda0();

    private /* synthetic */ ScreenPinningRequest$RequestWindowView$$ExternalSyntheticLambda0() {
    }

    public final Object apply(Object obj) {
        return ((StatusBar) ((Lazy) obj).get()).getNavigationBarView();
    }
}
