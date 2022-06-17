package kotlin.p015io;

import java.io.File;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: kotlin.io.ExceptionsKt */
/* compiled from: Exceptions.kt */
public final class ExceptionsKt {
    /* access modifiers changed from: private */
    public static final String constructMessage(File file, File file2, String str) {
        StringBuilder sb = new StringBuilder(file.toString());
        if (file2 != null) {
            sb.append(" -> " + file2);
        }
        if (str != null) {
            sb.append(": " + str);
        }
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "sb.toString()");
        return sb2;
    }
}
