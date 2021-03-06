package com.android.systemui.plugins;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@DependsOn(target = DarkReceiver.class)
@ProvidesInterface(version = 1)
public interface DarkIconDispatcher {
    public static final int DEFAULT_ICON_TINT = -1;
    public static final int VERSION = 1;
    public static final int[] sTmpInt2 = new int[2];
    public static final Rect sTmpRect = new Rect();

    @ProvidesInterface(version = 1)
    public interface DarkReceiver {
        public static final int VERSION = 1;

        void onDarkChanged(Rect rect, float f, int i);
    }

    void addDarkReceiver(ImageView imageView);

    void addDarkReceiver(DarkReceiver darkReceiver);

    void applyDark(DarkReceiver darkReceiver);

    void removeDarkReceiver(ImageView imageView);

    void removeDarkReceiver(DarkReceiver darkReceiver);

    void setIconsDarkArea(Rect rect);

    static int getTint(Rect rect, View view, int i) {
        if (isInArea(rect, view)) {
            return i;
        }
        return -1;
    }

    static float getDarkIntensity(Rect rect, View view, float f) {
        if (isInArea(rect, view)) {
            return f;
        }
        return 0.0f;
    }

    static boolean isInArea(Rect rect, View view) {
        if (rect.isEmpty()) {
            return true;
        }
        int width = view.getWidth();
        if (width == 0) {
            width = view.getContext().getResources().getDimensionPixelSize(17105536);
        }
        sTmpRect.set(rect);
        int[] iArr = sTmpInt2;
        view.getLocationOnScreen(iArr);
        int i = iArr[0];
        int max = Math.max(0, Math.min(i + width, rect.right) - Math.max(i, rect.left));
        boolean z = rect.top <= 0;
        if (!(max * 2 > width) || !z) {
            return false;
        }
        return true;
    }

    static boolean isInArea(Rect rect, int i, int i2) {
        if (rect.isEmpty()) {
            return true;
        }
        sTmpRect.set(rect);
        int max = Math.max(0, Math.min(i2, rect.right) - Math.max(i, rect.left));
        boolean z = rect.top <= 0;
        if (!(max * 2 > i2 - i) || !z) {
            return false;
        }
        return true;
    }
}
