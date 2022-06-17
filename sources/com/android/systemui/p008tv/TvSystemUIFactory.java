package com.android.systemui.p008tv;

import android.content.Context;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.GlobalRootComponent;

/* renamed from: com.android.systemui.tv.TvSystemUIFactory */
public class TvSystemUIFactory extends SystemUIFactory {
    /* access modifiers changed from: protected */
    public GlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerTvGlobalRootComponent.builder().context(context).build();
    }
}
