package kotlin.text;

import org.jetbrains.annotations.NotNull;

/* compiled from: StringNumberConversionsJVM.kt */
final class ScreenFloatValueRegEx {
    @NotNull
    public static final ScreenFloatValueRegEx INSTANCE = new ScreenFloatValueRegEx();
    @NotNull
    public static final Regex value;

    static {
        String str = "[eE][+-]?" + "(\\p{Digit}+)";
        value = new Regex("[\\x00-\\x20]*[+-]?(NaN|Infinity|((" + ('(' + "(\\p{Digit}+)" + "(\\.)?(" + "(\\p{Digit}+)" + "?)(" + str + ")?)|" + "(\\.(" + "(\\p{Digit}+)" + ")(" + str + ")?)|" + "((" + ("(0[xX]" + "(\\p{XDigit}+)" + "(\\.)?)|" + "(0[xX]" + "(\\p{XDigit}+)" + "?(\\.)" + "(\\p{XDigit}+)" + ')') + ")[pP][+-]?" + "(\\p{Digit}+)" + ')') + ")[fFdD]?))[\\x00-\\x20]*");
    }

    private ScreenFloatValueRegEx() {
    }
}
