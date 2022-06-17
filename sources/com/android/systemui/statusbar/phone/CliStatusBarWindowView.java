package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CliStatusBarWindowView extends FrameLayout {
    private static final boolean DEBUG = (!Build.IS_USER);
    private CliStatusBar mService;

    public CliStatusBarWindowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setService(CliStatusBar cliStatusBar) {
        this.mService = cliStatusBar;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
    }
}
