package com.motorola.systemui.desktop.dagger;

import android.content.Context;
import com.android.systemui.dagger.GlobalRootComponent;
import com.motorola.systemui.desktop.dagger.DesktopSysUIComponent;

public interface DesktopGlobalRootComponent extends GlobalRootComponent {

    public interface Builder extends GlobalRootComponent.Builder {
        DesktopGlobalRootComponent build();

        Builder context(Context context);
    }

    DesktopSysUIComponent.Builder getSysUIComponent();
}
