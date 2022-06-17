package com.motorola.systemui.cli.navgesture.util;

public class NavBarPosition {
    private final int mDisplayRotation;
    private final int mMode;

    public NavBarPosition(int i, int i2) {
        this.mMode = i;
        this.mDisplayRotation = i2;
    }

    public boolean isRightEdge() {
        return this.mMode != 2 && this.mDisplayRotation == 1;
    }

    public boolean isLeftEdge() {
        return this.mMode != 2 && this.mDisplayRotation == 3;
    }

    public float getRotation() {
        if (isLeftEdge()) {
            return 90.0f;
        }
        return (float) (isRightEdge() ? -90 : 0);
    }
}
