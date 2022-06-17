package com.android.systemui.p006qs.external;

import android.util.SparseBooleanArray;

/* renamed from: com.android.systemui.qs.external.BestTrue */
public class BestTrue {
    private boolean mCurrentValue;
    private final boolean mInitValue;
    private final SparseBooleanArray mValue = new SparseBooleanArray();

    public BestTrue(boolean z) {
        this.mInitValue = z;
        this.mCurrentValue = z;
    }

    public boolean get() {
        return this.mCurrentValue;
    }

    public boolean put(int i, boolean z) {
        this.mValue.put(i, z);
        if (z) {
            this.mCurrentValue = true;
        } else {
            this.mCurrentValue = reCalcValue();
        }
        return this.mCurrentValue;
    }

    public boolean delete(int i) {
        this.mValue.delete(i);
        boolean reCalcValue = reCalcValue();
        this.mCurrentValue = reCalcValue;
        return reCalcValue;
    }

    private boolean reCalcValue() {
        SparseBooleanArray clone = this.mValue.clone();
        if (clone.size() == 0) {
            return this.mInitValue;
        }
        for (int i = 0; i < clone.size(); i++) {
            if (clone.valueAt(i)) {
                return true;
            }
        }
        return false;
    }
}
