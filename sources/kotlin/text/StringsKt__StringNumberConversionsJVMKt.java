package kotlin.text;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: StringNumberConversionsJVM.kt */
class StringsKt__StringNumberConversionsJVMKt extends StringsKt__StringBuilderKt {
    @Nullable
    public static Float toFloatOrNull(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "$this$toFloatOrNull");
        try {
            if (ScreenFloatValueRegEx.value.matches(str)) {
                return Float.valueOf(Float.parseFloat(str));
            }
            return null;
        } catch (NumberFormatException unused) {
            return null;
        }
    }
}
