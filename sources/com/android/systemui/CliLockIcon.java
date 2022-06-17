package com.android.systemui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.AlphaOptimizedImageView;
import com.android.systemui.statusbar.policy.ConfigurationController;

public class CliLockIcon extends AlphaOptimizedImageView implements DarkIconDispatcher.DarkReceiver, ConfigurationController.ConfigurationListener {
    public boolean hasOverlappingRendering() {
        return false;
    }

    public CliLockIcon(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public CliLockIcon(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CliLockIcon(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        onDarkChanged(new Rect(), 0.0f, -1);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onDarkChanged(Rect rect, float f, int i) {
        setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(rect, this, i)));
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }
}
