package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import androidx.annotation.Keep;
import com.android.systemui.R$dimen;
import com.motorola.systemui.cli.navgesture.util.DeviceProfile;
import com.motorola.systemui.cli.navgesture.util.LayoutCalculator;

@Keep
public class TaskViewLayoutCalculator implements LayoutCalculator {
    public TaskViewLayoutCalculator(Context context) {
    }

    public int getSwipeUpDestinationAndLength(DeviceProfile deviceProfile, Context context, Rect rect) {
        int i;
        Rect insets = deviceProfile.getInsets();
        Resources resources = context.getResources();
        float f = (float) deviceProfile.availableWidthPx;
        float f2 = (float) deviceProfile.availableHeightPx;
        if (deviceProfile.isLandscape) {
            i = resources.getDimensionPixelOffset(R$dimen.task_view_padding_horizontal_land);
        } else {
            i = resources.getDimensionPixelOffset(R$dimen.task_view_padding_horizontal);
        }
        float f3 = (float) ((deviceProfile.widthPx - insets.left) - insets.right);
        float f4 = (float) ((deviceProfile.heightPx - insets.top) - insets.bottom);
        float min = Math.min((f3 - ((float) i)) / f, ((f4 - 0.0f) - ((float) resources.getDimensionPixelOffset(R$dimen.task_view_padding_vertical_land))) / f2);
        float f5 = f * min;
        float f6 = min * f2;
        float f7 = ((float) insets.left) + ((f3 - f5) / 2.0f);
        float max = ((float) insets.top) + Math.max(0.0f, (f4 - f6) / 2.0f);
        rect.set(Math.round(f7), Math.round(max), Math.round(f7) + Math.round(f5), Math.round(max) + Math.round(f6));
        return deviceProfile.heightPx - rect.bottom;
    }
}
