package com.android.systemui.accessibility;

import android.graphics.Rect;

interface WindowMagnifierCallback {
    void onAccessibilityActionPerformed(int i);

    void onPerformScaleAction(int i, float f);

    void onSourceBoundsChanged(int i, Rect rect);

    void onWindowMagnifierBoundsChanged(int i, Rect rect);
}
