package com.motorola.systemui.cli.navgesture.animation;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.view.View;
import com.motorola.systemui.cli.navgesture.CommonBasicActivity;
import com.motorola.systemui.cli.navgesture.ResourceObject;

public class AppTransitionManager implements ResourceObject {
    public void dispose() {
    }

    public int getStateElementAnimationsCount() {
        throw null;
    }

    public AppTransitionManager(Context context) {
    }

    public ActivityOptions getActivityLaunchOptions(CommonBasicActivity commonBasicActivity, View view) {
        return ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public Animator createStateElementAnimation(int i, float... fArr) {
        throw new RuntimeException("Unknown gesture animation " + i);
    }
}
