package com.motorola.systemui.cli.navgesture.display;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import com.android.systemui.R$string;
import com.motorola.systemui.cli.navgesture.MainThreadInitializedObject;
import com.motorola.systemui.cli.navgesture.ResourceObject;

public interface SecondaryDisplay extends ResourceObject {
    public static final MainThreadInitializedObject<SecondaryDisplay> INSTANCE = MainThreadInitializedObject.fromResourceObject(R$string.secondary_display_class);

    void addChangeListener(DisplayInfoChangeListener displayInfoChangeListener);

    void getCurrentSizeRange(Point point, Point point2);

    int getDisplayId();

    Point getDisplaySize();

    int getRotation();

    boolean isMockDisplay() {
        return false;
    }

    Point navBarSize();

    void removeChangeListener(DisplayInfoChangeListener displayInfoChangeListener);

    Configuration newDisplayConfiguration(Context context) {
        return context.getResources().getConfiguration();
    }
}
