package com.motorola.systemui.cli.navgesture.animation;

import com.motorola.systemui.cli.navgesture.animation.OverviewCommandHelper;
import java.util.function.Predicate;

/* renamed from: com.motorola.systemui.cli.navgesture.animation.OverviewCommandHelper$RecentsActivityCommand$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C2705x67c32af9 implements Predicate {
    public final /* synthetic */ OverviewCommandHelper.RecentsActivityCommand f$0;

    public /* synthetic */ C2705x67c32af9(OverviewCommandHelper.RecentsActivityCommand recentsActivityCommand) {
        this.f$0 = recentsActivityCommand;
    }

    public final boolean test(Object obj) {
        return this.f$0.onActivityReady((Boolean) obj);
    }
}
