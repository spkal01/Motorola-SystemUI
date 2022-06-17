package kotlin.text;

import androidx.appcompat.R$styleable;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: Indent.kt */
class StringsKt__IndentKt extends StringsKt__AppendableKt {
    @NotNull
    public static String trimIndent(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "$this$trimIndent");
        return replaceIndent(str, "");
    }

    @NotNull
    public static final String replaceIndent(@NotNull String str, @NotNull String str2) {
        String str3;
        String invoke;
        Intrinsics.checkNotNullParameter(str, "$this$replaceIndent");
        Intrinsics.checkNotNullParameter(str2, "newIndent");
        List<String> lines = StringsKt__StringsKt.lines(str);
        ArrayList<String> arrayList = new ArrayList<>();
        for (T next : lines) {
            if (!StringsKt__StringsJVMKt.isBlank((String) next)) {
                arrayList.add(next);
            }
        }
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(arrayList, 10));
        for (String indentWidth$StringsKt__IndentKt : arrayList) {
            arrayList2.add(Integer.valueOf(indentWidth$StringsKt__IndentKt(indentWidth$StringsKt__IndentKt)));
        }
        Integer num = (Integer) CollectionsKt___CollectionsKt.minOrNull(arrayList2);
        int i = 0;
        int intValue = num != null ? num.intValue() : 0;
        int length = str.length() + (str2.length() * lines.size());
        Function1<String, String> indentFunction$StringsKt__IndentKt = getIndentFunction$StringsKt__IndentKt(str2);
        int lastIndex = CollectionsKt__CollectionsKt.getLastIndex(lines);
        ArrayList arrayList3 = new ArrayList();
        for (T next2 : lines) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt__CollectionsKt.throwIndexOverflow();
            }
            String str4 = (String) next2;
            if ((i == 0 || i == lastIndex) && StringsKt__StringsJVMKt.isBlank(str4)) {
                str3 = null;
            } else {
                String drop = StringsKt___StringsKt.drop(str4, intValue);
                if (!(drop == null || (invoke = indentFunction$StringsKt__IndentKt.invoke(drop)) == null)) {
                    str4 = invoke;
                }
                str3 = str4;
            }
            if (str3 != null) {
                arrayList3.add(str3);
            }
            i = i2;
        }
        String sb = ((StringBuilder) CollectionsKt___CollectionsKt.joinTo$default(arrayList3, new StringBuilder(length), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, R$styleable.AppCompatTheme_windowMinWidthMajor, (Object) null)).toString();
        Intrinsics.checkNotNullExpressionValue(sb, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return sb;
    }

    private static final Function1<String, String> getIndentFunction$StringsKt__IndentKt(String str) {
        if (str.length() == 0) {
            return StringsKt__IndentKt$getIndentFunction$1.INSTANCE;
        }
        return new StringsKt__IndentKt$getIndentFunction$2(str);
    }

    private static final int indentWidth$StringsKt__IndentKt(String str) {
        int length = str.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                i = -1;
                break;
            } else if (!CharsKt__CharJVMKt.isWhitespace(str.charAt(i))) {
                break;
            } else {
                i++;
            }
        }
        return i == -1 ? str.length() : i;
    }
}
