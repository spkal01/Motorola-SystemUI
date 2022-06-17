package com.android.systemui.util;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import org.jetbrains.annotations.Nullable;

/* compiled from: DualHeightHorizontalLinearLayout.kt */
/* synthetic */ class DualHeightHorizontalLinearLayout$updateResources$4 extends MutablePropertyReference0Impl {
    DualHeightHorizontalLinearLayout$updateResources$4(Object obj) {
        super(obj, DualHeightHorizontalLinearLayout.class, "singleLineVerticalPaddingPx", "getSingleLineVerticalPaddingPx()I", 0);
    }

    @Nullable
    public Object get() {
        return Integer.valueOf(((DualHeightHorizontalLinearLayout) this.receiver).singleLineVerticalPaddingPx);
    }

    public void set(@Nullable Object obj) {
        ((DualHeightHorizontalLinearLayout) this.receiver).singleLineVerticalPaddingPx = ((Number) obj).intValue();
    }
}
