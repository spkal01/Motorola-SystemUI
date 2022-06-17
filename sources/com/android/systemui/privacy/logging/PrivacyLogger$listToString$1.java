package com.android.systemui.privacy.logging;

import com.android.systemui.privacy.PrivacyItem;
import kotlin.jvm.internal.PropertyReference1Impl;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyLogger.kt */
/* synthetic */ class PrivacyLogger$listToString$1 extends PropertyReference1Impl {
    public static final PrivacyLogger$listToString$1 INSTANCE = new PrivacyLogger$listToString$1();

    PrivacyLogger$listToString$1() {
        super(PrivacyItem.class, "log", "getLog()Ljava/lang/String;", 0);
    }

    @Nullable
    public Object get(@Nullable Object obj) {
        return ((PrivacyItem) obj).getLog();
    }
}
