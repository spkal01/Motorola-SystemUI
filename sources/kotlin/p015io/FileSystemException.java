package kotlin.p015io;

import java.io.File;
import java.io.IOException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: kotlin.io.FileSystemException */
/* compiled from: Exceptions.kt */
public class FileSystemException extends IOException {
    @NotNull
    private final File file;
    @Nullable
    private final File other;
    @Nullable
    private final String reason;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FileSystemException(@NotNull File file2, @Nullable File file3, @Nullable String str) {
        super(ExceptionsKt.constructMessage(file2, file3, str));
        Intrinsics.checkNotNullParameter(file2, "file");
        this.file = file2;
        this.other = file3;
        this.reason = str;
    }
}
