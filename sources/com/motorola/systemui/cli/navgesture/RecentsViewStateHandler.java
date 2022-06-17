package com.motorola.systemui.cli.navgesture;

import android.view.View;
import android.view.animation.Interpolator;
import com.motorola.systemui.cli.navgesture.animation.AnimatorSetBuilder;
import com.motorola.systemui.cli.navgesture.animation.PropertySetter;
import com.motorola.systemui.cli.navgesture.states.LauncherState;
import com.motorola.systemui.cli.navgesture.states.StateHandler;
import com.motorola.systemui.cli.navgesture.states.StateManager;
import com.motorola.systemui.cli.navgesture.util.ScaleTranslation;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.Objects;

public class RecentsViewStateHandler implements StateHandler {
    private AbstractRecentGestureLauncher mActivity;
    private IRecentsView mRecentsView;

    public RecentsViewStateHandler(AbstractRecentGestureLauncher abstractRecentGestureLauncher) {
        this.mActivity = abstractRecentGestureLauncher;
        this.mRecentsView = abstractRecentGestureLauncher.getOverviewPanel();
    }

    public void setState(LauncherState launcherState) {
        ScaleTranslation overviewScaleTranslation = launcherState.getOverviewScaleTranslation(this.mActivity);
        Utilities.SCALE_PROPERTY.set(this.mRecentsView.asView(), Float.valueOf(overviewScaleTranslation.scale));
        float f = overviewScaleTranslation.translationX;
        if (this.mRecentsView.asView().getLayoutDirection() == 1) {
            f = -f;
        }
        this.mRecentsView.asView().setTranslationX(f);
        this.mRecentsView.asView().setTranslationY(overviewScaleTranslation.translationY);
        if (launcherState.overview()) {
            this.mRecentsView.updateEmptyMessage();
            this.mRecentsView.resetTaskVisuals();
        }
        IRecentsView.CONTENT_ALPHA.set(this.mRecentsView, Float.valueOf(launcherState.overview() ? 1.0f : 0.0f));
        this.mRecentsView.setFullscreenProgress(launcherState.getOverviewFullscreenProgress());
    }

    public void setStateWithAnimation(LauncherState launcherState, AnimatorSetBuilder animatorSetBuilder, StateManager.AnimationConfig animationConfig) {
        PropertySetter propertySetter = animationConfig.getPropertySetter(animatorSetBuilder);
        ScaleTranslation overviewScaleTranslation = launcherState.getOverviewScaleTranslation(this.mActivity);
        Interpolator interpolator = Interpolators.LINEAR;
        propertySetter.setFloat(this.mRecentsView.asView(), Utilities.SCALE_PROPERTY, overviewScaleTranslation.scale, interpolator);
        float f = overviewScaleTranslation.translationX;
        if (this.mRecentsView.asView().getLayoutDirection() == 1) {
            f = -f;
        }
        propertySetter.setFloat(this.mRecentsView.asView(), View.TRANSLATION_X, f, interpolator);
        propertySetter.setFloat(this.mRecentsView.asView(), View.TRANSLATION_Y, overviewScaleTranslation.translationY, interpolator);
        if (!launcherState.overview()) {
            IRecentsView iRecentsView = this.mRecentsView;
            Objects.requireNonNull(iRecentsView);
            animatorSetBuilder.addOnFinishRunnable(new RecentsViewStateHandler$$ExternalSyntheticLambda0(iRecentsView));
        } else {
            this.mRecentsView.updateEmptyMessage();
        }
        propertySetter.setFloat(this.mRecentsView, IRecentsView.CONTENT_ALPHA, launcherState.overview() ? 1.0f : 0.0f, Interpolators.AGGRESSIVE_EASE_IN_OUT);
        propertySetter.setFloat(this.mRecentsView, IRecentsView.FULLSCREEN_PROGRESS, launcherState.getOverviewFullscreenProgress(), interpolator);
    }
}
