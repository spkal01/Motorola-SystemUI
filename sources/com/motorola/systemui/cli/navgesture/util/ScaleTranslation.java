package com.motorola.systemui.cli.navgesture.util;

public class ScaleTranslation {
    public float scale;
    public float translationX;
    public float translationY;

    public ScaleTranslation(float f, float f2, float f3) {
        this.scale = f;
        this.translationX = f2;
        this.translationY = f3;
    }

    public String toString() {
        return "ScaleTranslation{scale=" + this.scale + ", translationX=" + this.translationX + ", translationY=" + this.translationY + '}';
    }
}
