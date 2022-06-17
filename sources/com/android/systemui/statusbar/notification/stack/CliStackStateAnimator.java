package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class CliStackStateAnimator {
    /* access modifiers changed from: private */
    public AnimationFilter mAnimationFilter = new AnimationFilter();
    /* access modifiers changed from: private */
    public Stack<AnimatorListenerAdapter> mAnimationListenerPool = new Stack<>();
    private final AnimationProperties mAnimationProperties;
    /* access modifiers changed from: private */
    public HashSet<Animator> mAnimatorSet = new HashSet<>();
    private long mCurrentAdditionalDelay;
    private long mCurrentLength;
    public CliNotificationStackLayout mHostLayout;
    private ArrayList<NotificationStackScrollLayout.AnimationEvent> mNewEvents = new ArrayList<>();

    public CliStackStateAnimator(CliNotificationStackLayout cliNotificationStackLayout) {
        this.mHostLayout = cliNotificationStackLayout;
        this.mAnimationProperties = new AnimationProperties() {
            public Interpolator getCustomInterpolator(View view, Property property) {
                return null;
            }

            public boolean wasAdded(View view) {
                return false;
            }

            public AnimationFilter getAnimationFilter() {
                return CliStackStateAnimator.this.mAnimationFilter;
            }

            public AnimatorListenerAdapter getAnimationFinishListener(Property property) {
                return CliStackStateAnimator.this.getGlobalAnimationFinishedListener();
            }
        };
    }

    public boolean isRunning() {
        return !this.mAnimatorSet.isEmpty();
    }

    public void startAnimationForEvents(ArrayList<NotificationStackScrollLayout.AnimationEvent> arrayList, long j) {
        processAnimationEvents(arrayList);
        int childCount = this.mHostLayout.getChildCount();
        this.mAnimationFilter.applyCombination(this.mNewEvents);
        this.mCurrentAdditionalDelay = j;
        this.mCurrentLength = NotificationStackScrollLayout.AnimationEvent.combineLength(this.mNewEvents);
        int i = 0;
        for (int indexRow = this.mHostLayout.getIndexRow(); indexRow < childCount; indexRow++) {
            ExpandableView expandableView = (ExpandableView) this.mHostLayout.getChildAt(indexRow);
            ExpandableViewState viewState = expandableView.getViewState();
            if (!(viewState == null || expandableView.getVisibility() == 8)) {
                if (this.mAnimationProperties.wasAdded(expandableView) && i < 5) {
                    i++;
                }
                initAnimationProperties(expandableView, viewState, i);
                viewState.animateTo(expandableView, this.mAnimationProperties);
            }
        }
        if (!isRunning()) {
            onAnimationFinished();
        }
        this.mNewEvents.clear();
    }

    private void initAnimationProperties(ExpandableView expandableView, ExpandableViewState expandableViewState, int i) {
        boolean wasAdded = this.mAnimationProperties.wasAdded(expandableView);
        AnimationProperties animationProperties = this.mAnimationProperties;
        animationProperties.duration = this.mCurrentLength;
        animationProperties.delay = 0;
        if (!wasAdded) {
            if (!this.mAnimationFilter.hasDelays) {
                return;
            }
            if (expandableViewState.yTranslation == expandableView.getTranslationY() && expandableViewState.zTranslation == expandableView.getTranslationZ() && expandableViewState.alpha == expandableView.getAlpha() && expandableViewState.height == expandableView.getActualHeight() && expandableViewState.clipTopAmount == expandableView.getClipTopAmount()) {
                return;
            }
        }
        this.mAnimationProperties.delay = this.mCurrentAdditionalDelay;
    }

    /* access modifiers changed from: private */
    public AnimatorListenerAdapter getGlobalAnimationFinishedListener() {
        if (!this.mAnimationListenerPool.empty()) {
            return this.mAnimationListenerPool.pop();
        }
        return new AnimatorListenerAdapter() {
            private boolean mWasCancelled;

            public void onAnimationEnd(Animator animator) {
                CliStackStateAnimator.this.mAnimatorSet.remove(animator);
                if (CliStackStateAnimator.this.mAnimatorSet.isEmpty() && !this.mWasCancelled) {
                    CliStackStateAnimator.this.onAnimationFinished();
                }
                CliStackStateAnimator.this.mAnimationListenerPool.push(this);
            }

            public void onAnimationCancel(Animator animator) {
                this.mWasCancelled = true;
            }

            public void onAnimationStart(Animator animator) {
                this.mWasCancelled = false;
                CliStackStateAnimator.this.mAnimatorSet.add(animator);
            }
        };
    }

    /* access modifiers changed from: private */
    public void onAnimationFinished() {
        this.mHostLayout.onChildAnimationFinished();
    }

    private void processAnimationEvents(ArrayList<NotificationStackScrollLayout.AnimationEvent> arrayList) {
        Iterator<NotificationStackScrollLayout.AnimationEvent> it = arrayList.iterator();
        while (it.hasNext()) {
            NotificationStackScrollLayout.AnimationEvent next = it.next();
            if (next.animationType == 10) {
                ((ExpandableNotificationRow) next.mChangingView).prepareExpansionChanged();
            }
            this.mNewEvents.add(next);
        }
    }
}
