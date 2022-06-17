package com.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.WMComponent;
import com.android.systemui.util.concurrency.ThreadFactory;

public interface GlobalRootComponent {

    public interface Builder {
        GlobalRootComponent build();

        Builder context(Context context);
    }

    ThreadFactory createThreadFactory();

    SysUIComponent.Builder getSysUIComponent();

    WMComponent.Builder getWMComponentBuilder();
}
