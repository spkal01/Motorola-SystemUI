package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import android.graphics.Rect;
import com.android.systemui.R$string;
import com.motorola.systemui.cli.navgesture.MainThreadInitializedObject;
import com.motorola.systemui.cli.navgesture.ResourceObject;

public interface LayoutCalculator extends ResourceObject {
    public static final MainThreadInitializedObject<LayoutCalculator> INSTANCE = MainThreadInitializedObject.fromResourceObject(R$string.layout_calculator_class);

    int getSwipeUpDestinationAndLength(DeviceProfile deviceProfile, Context context, Rect rect);
}
