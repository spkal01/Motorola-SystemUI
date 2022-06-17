package com.android.systemui.util;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import org.jetbrains.annotations.Nullable;

/* compiled from: DualHeightHorizontalLinearLayout.kt */
/* synthetic */ class DualHeightHorizontalLinearLayout$updateResources$2 extends MutablePropertyReference0Impl {
    DualHeightHorizontalLinearLayout$updateResources$2(Object obj) {
        super(obj, DualHeightHorizontalLinearLayout.class, "singleLineHeightPx", "getSingleLineHeightPx()I", 0);
    }

    @Nullable
    public Object get() {
        return Integer.valueOf(((DualHeightHorizontalLinearLayout) this.receiver).singleLineHeightPx);
    }

    public void set(@Nullable Object obj) {
        ((DualHeightHorizontalLinearLayout) this.receiver).singleLineHeightPx = ((Number) obj).intValue();
    }
}
