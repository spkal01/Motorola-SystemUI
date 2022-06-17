package com.android.systemui.plugins;

import android.animation.Animator;
import android.view.View;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_TOAST", version = 1)
public interface ToastPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_TOAST";
    public static final int VERSION = 1;

    public interface Toast {
        Integer getGravity() {
            return null;
        }

        Integer getHorizontalMargin() {
            return null;
        }

        Animator getInAnimation() {
            return null;
        }

        Animator getOutAnimation() {
            return null;
        }

        Integer getVerticalMargin() {
            return null;
        }

        View getView() {
            return null;
        }

        Integer getXOffset() {
            return null;
        }

        Integer getYOffset() {
            return null;
        }

        void onOrientationChange(int i) {
        }
    }

    Toast createToast(CharSequence charSequence, String str, int i);
}
