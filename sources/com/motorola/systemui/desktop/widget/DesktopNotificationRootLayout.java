package com.motorola.systemui.desktop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.StatusBar;
import com.motorola.systemui.desktop.DesktopStatusBar;

public class DesktopNotificationRootLayout extends FrameLayout {
    public DesktopNotificationRootLayout(Context context) {
        super(context);
    }

    public DesktopNotificationRootLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DesktopNotificationRootLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public DesktopNotificationRootLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            getContext().getDisplayId();
            StatusBar statusBar = (StatusBar) Dependency.get(StatusBar.class);
            if (statusBar != null && (statusBar instanceof DesktopStatusBar)) {
                DesktopStatusBar desktopStatusBar = (DesktopStatusBar) statusBar;
                if (!desktopStatusBar.isRemoteInputActive()) {
                    desktopStatusBar.requestHidePanel();
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
