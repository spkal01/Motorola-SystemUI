package com.motorola.systemui.cli.navgesture.view;

import android.view.MotionEvent;
import com.motorola.systemui.cli.navgesture.view.RecentsViewContainer;
import java.util.function.Consumer;

public final /* synthetic */ class RecentsViewContainer$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ RecentsViewContainer.RecentsTaskViewHelper f$0;

    public /* synthetic */ RecentsViewContainer$$ExternalSyntheticLambda2(RecentsViewContainer.RecentsTaskViewHelper recentsTaskViewHelper) {
        this.f$0 = recentsTaskViewHelper;
    }

    public final void accept(Object obj) {
        this.f$0.onTouchEvent((MotionEvent) obj);
    }
}
