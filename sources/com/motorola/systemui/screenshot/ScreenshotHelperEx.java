package com.motorola.systemui.screenshot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.motorola.systemui.feedback.FeedbackHelper;

public class ScreenshotHelperEx {
    public static void onTakeScreenshotSuccess(Context context, String str, int i) {
        if (i == 1) {
            FeedbackHelper.startFeedback(context, "com.motorola.help.ACTION_LAUNCH_FEEDBACK_FROM_QUICK_SETTINGS", (Uri) null, str);
        }
    }

    public static void onScreenshotFinished(Context context, boolean z, Uri uri, String str, int i) {
        if (i != 1) {
            if (i == 2) {
                Intent intent = new Intent("com.motorola.gamemode.SCREENSHOT_COMPLETED");
                intent.setPackage("com.motorola.gamemode");
                intent.putExtra("screenshot_succeeded", z);
                intent.putExtra("game_package", str);
                if (uri != null) {
                    intent.setDataAndType(uri, "image/png");
                    context.grantUriPermission("com.motorola.gamemode", uri, 3);
                }
                context.sendBroadcast(intent);
            }
        } else if (z) {
            FeedbackHelper.sendScreenshotUri(context, "com.motorola.help.ACTION_SCREENSHOT_URI", uri);
        }
    }
}
