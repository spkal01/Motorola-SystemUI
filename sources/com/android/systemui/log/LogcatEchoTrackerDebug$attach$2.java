package com.android.systemui.log;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LogcatEchoTrackerDebug.kt */
public final class LogcatEchoTrackerDebug$attach$2 extends ContentObserver {
    final /* synthetic */ LogcatEchoTrackerDebug this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    LogcatEchoTrackerDebug$attach$2(LogcatEchoTrackerDebug logcatEchoTrackerDebug, Handler handler) {
        super(handler);
        this.this$0 = logcatEchoTrackerDebug;
    }

    public void onChange(boolean z, @NotNull Uri uri) {
        Intrinsics.checkNotNullParameter(uri, "uri");
        super.onChange(z, uri);
        this.this$0.cachedTagLevels.clear();
    }
}
