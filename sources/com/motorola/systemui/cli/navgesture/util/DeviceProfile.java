package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

public class DeviceProfile {
    public final int availableHeightPx;
    public final int availableWidthPx;
    public final int heightPx;
    public final boolean isLandscape;
    private final Rect mInsets = new Rect();
    public final int widthPx;

    protected DeviceProfile(Context context, Point point, Point point2, int i, int i2, boolean z) {
        this.widthPx = i;
        this.heightPx = i2;
        if (z) {
            this.availableWidthPx = point2.x;
            this.availableHeightPx = point.y;
        } else {
            this.availableWidthPx = point.x;
            this.availableHeightPx = point2.y;
        }
        this.isLandscape = z;
    }

    public void updateInsets(Rect rect) {
        this.mInsets.set(rect);
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    public DeviceProfile copy(Context context) {
        Point point = new Point(this.availableWidthPx, this.availableHeightPx);
        return new DeviceProfile(context, point, point, this.widthPx, this.heightPx, this.isLandscape);
    }
}
