package com.motorola.systemui.desktop.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.window.WindowContext;
import com.android.systemui.R$style;

public class DesktopDisplayContext extends ContextWrapper {
    public final int mDisplayId;

    public DesktopDisplayContext(Context context, int i) {
        super(DesktopUtils.createDisplayWindowContext(context, i, 2041));
        setTheme(R$style.Theme_SystemUI);
        this.mDisplayId = i;
    }

    public WindowContext getWindowContext() {
        WindowContext baseContext = getBaseContext();
        if (baseContext instanceof WindowContext) {
            return baseContext;
        }
        return null;
    }
}
