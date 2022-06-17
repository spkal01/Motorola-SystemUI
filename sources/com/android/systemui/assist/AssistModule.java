package com.android.systemui.assist;

import android.content.Context;
import com.android.internal.app.AssistUtils;

public abstract class AssistModule {
    static AssistUtils provideAssistUtils(Context context) {
        return new AssistUtils(context);
    }
}
