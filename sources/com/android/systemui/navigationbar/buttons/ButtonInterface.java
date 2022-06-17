package com.android.systemui.navigationbar.buttons;

import android.graphics.drawable.Drawable;

public interface ButtonInterface {
    void abortCurrentGesture();

    void setDarkIntensity(float f);

    void setDelayTouchFeedback(boolean z);

    void setImageDrawable(Drawable drawable);

    void setVertical(boolean z);
}
