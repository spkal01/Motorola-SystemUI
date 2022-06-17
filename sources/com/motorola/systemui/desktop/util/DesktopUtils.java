package com.motorola.systemui.desktop.util;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class DesktopUtils {
    public static Context createDisplayWindowContext(Context context, int i, int i2) {
        Display display = ((DisplayManager) context.getSystemService("display")).getDisplay(i);
        if (display != null) {
            return context.createWindowContext(display, i2, (Bundle) null);
        }
        Log.w("DesktopUtils", "createDisplayWindowContext with null display: " + i);
        return context;
    }
}
