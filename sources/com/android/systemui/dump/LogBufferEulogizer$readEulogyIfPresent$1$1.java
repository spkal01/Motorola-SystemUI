package com.android.systemui.dump;

import java.io.PrintWriter;
import java.util.function.Consumer;

/* compiled from: LogBufferEulogizer.kt */
final class LogBufferEulogizer$readEulogyIfPresent$1$1 implements Consumer<String> {
    final /* synthetic */ PrintWriter $pw;

    LogBufferEulogizer$readEulogyIfPresent$1$1(PrintWriter printWriter) {
        this.$pw = printWriter;
    }

    public final void accept(String str) {
        this.$pw.println(str);
    }
}
