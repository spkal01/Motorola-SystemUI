package com.motorola.systemui.cli.navgesture.util;

import java.util.function.BiConsumer;

public class DebugLog {
    /* renamed from: d */
    public static void m98d(String str, String str2) {
        getTaggedMsg(str, str2, DebugLog$$ExternalSyntheticLambda0.INSTANCE);
    }

    /* renamed from: e */
    public static void m99e(String str, String str2) {
        getTaggedMsg(str, str2, DebugLog$$ExternalSyntheticLambda1.INSTANCE);
    }

    /* renamed from: v */
    public static void m100v(String str, String str2) {
        getTaggedMsg(str, str2, DebugLog$$ExternalSyntheticLambda2.INSTANCE);
    }

    /* renamed from: w */
    public static void m101w(String str, String str2) {
        getTaggedMsg(str, str2, DebugLog$$ExternalSyntheticLambda3.INSTANCE);
    }

    private static void getTaggedMsg(String str, String str2, BiConsumer<String, String> biConsumer) {
        biConsumer.accept(str, str2);
    }
}
