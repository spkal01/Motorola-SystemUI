package com.android.p011wm.shell.bubbles;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.view.View;

/* renamed from: com.android.wm.shell.bubbles.BubbleViewProvider */
public interface BubbleViewProvider {
    Bitmap getAppBadge();

    Bitmap getBubbleIcon();

    int getDotColor();

    Path getDotPath();

    BubbleExpandedView getExpandedView();

    View getIconView();

    String getKey();

    int getTaskId();

    void setExpandedContentAlpha(float f);

    void setTaskViewVisibility(boolean z);

    boolean showDot();
}
