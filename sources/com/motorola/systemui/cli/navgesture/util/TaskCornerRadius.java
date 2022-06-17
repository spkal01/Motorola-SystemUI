package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import com.android.systemui.R$dimen;
import com.android.systemui.shared.system.QuickStepContract;
import com.motorola.systemui.cli.navgesture.Themes;

public class TaskCornerRadius {
    public static float get(Context context) {
        if (QuickStepContract.supportsRoundedCornersOnWindows(context.getResources())) {
            return getDialogCornerRadius(context);
        }
        return context.getResources().getDimension(R$dimen.task_corner_radius_small);
    }

    public static float getDialogCornerRadius(Context context) {
        return Themes.getDimension(context, 16844145, context.getResources().getDimension(R$dimen.default_dialog_corner_radius));
    }
}
