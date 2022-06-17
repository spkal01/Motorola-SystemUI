package com.android.systemui.media;

import com.android.systemui.media.MediaDeviceManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.BiConsumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MediaDeviceManager.kt */
final class MediaDeviceManager$dump$1$1 implements BiConsumer<String, MediaDeviceManager.Entry> {
    final /* synthetic */ String[] $args;
    final /* synthetic */ FileDescriptor $fd;
    final /* synthetic */ PrintWriter $pw;
    final /* synthetic */ PrintWriter $this_with;

    MediaDeviceManager$dump$1$1(PrintWriter printWriter, FileDescriptor fileDescriptor, PrintWriter printWriter2, String[] strArr) {
        this.$this_with = printWriter;
        this.$fd = fileDescriptor;
        this.$pw = printWriter2;
        this.$args = strArr;
    }

    public final void accept(@NotNull String str, @NotNull MediaDeviceManager.Entry entry) {
        Intrinsics.checkNotNullParameter(str, "key");
        Intrinsics.checkNotNullParameter(entry, "entry");
        this.$this_with.println(Intrinsics.stringPlus("  key=", str));
        entry.dump(this.$fd, this.$pw, this.$args);
    }
}
