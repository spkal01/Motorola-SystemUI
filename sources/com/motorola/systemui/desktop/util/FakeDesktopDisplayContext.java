package com.motorola.systemui.desktop.util;

import android.content.Context;

public class FakeDesktopDisplayContext extends DesktopDisplayContext {
    public int getDisplayId() {
        return Integer.MAX_VALUE;
    }

    public FakeDesktopDisplayContext(Context context, int i) {
        super(context, 0);
    }
}
