package androidx.core.p002os;

import android.os.Build;

/* renamed from: androidx.core.os.BuildCompat */
public class BuildCompat {
    protected static boolean isAtLeastPreReleaseCodename(String str, String str2) {
        if (!"REL".equals(str2) && str2.compareTo(str) >= 0) {
            return true;
        }
        return false;
    }

    @Deprecated
    public static boolean isAtLeastR() {
        return Build.VERSION.SDK_INT >= 30;
    }

    public static boolean isAtLeastS() {
        return Build.VERSION.SDK_INT >= 31 || isAtLeastPreReleaseCodename("S", Build.VERSION.CODENAME);
    }
}
