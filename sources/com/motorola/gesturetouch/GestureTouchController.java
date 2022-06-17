package com.motorola.gesturetouch;

public abstract class GestureTouchController {
    public boolean isInTutorialMode() {
        return false;
    }

    public boolean isLightTheme() {
        return false;
    }

    public abstract boolean isNeedShowWhatNew();
}
