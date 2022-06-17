package com.android.systemui.statusbar.policy;

import com.android.systemui.plugins.ActivityStarter;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

/* renamed from: com.android.systemui.statusbar.policy.SmartReplyStateInflaterKt$sam$com_android_systemui_plugins_ActivityStarter_OnDismissAction$0 */
/* compiled from: SmartReplyStateInflater.kt */
final class C2065xc0c4f386 implements ActivityStarter.OnDismissAction {
    private final /* synthetic */ Function0 function;

    C2065xc0c4f386(@NotNull Function0 function0) {
        this.function = function0;
    }

    public final /* synthetic */ boolean onDismiss() {
        return ((Boolean) this.function.invoke()).booleanValue();
    }
}
