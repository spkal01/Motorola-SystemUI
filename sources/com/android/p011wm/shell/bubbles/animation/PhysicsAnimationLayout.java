package com.android.p011wm.shell.bubbles.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.p011wm.shell.C2219R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout */
public class PhysicsAnimationLayout extends FrameLayout {
    protected PhysicsAnimationController mController;
    protected final HashMap<DynamicAnimation.ViewProperty, Runnable> mEndActionForProperty = new HashMap<>();

    /* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsAnimationController */
    static abstract class PhysicsAnimationController {
        protected PhysicsAnimationLayout mLayout;

        /* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsAnimationController$ChildAnimationConfigurator */
        interface ChildAnimationConfigurator {
            void configureAnimationForChildAtIndex(int i, PhysicsPropertyAnimator physicsPropertyAnimator);
        }

        /* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsAnimationController$MultiAnimationStarter */
        interface MultiAnimationStarter {
            void startAll(Runnable... runnableArr);
        }

        /* access modifiers changed from: package-private */
        public abstract Set<DynamicAnimation.ViewProperty> getAnimatedProperties();

        /* access modifiers changed from: package-private */
        public abstract int getNextAnimationInChain(DynamicAnimation.ViewProperty viewProperty, int i);

        /* access modifiers changed from: package-private */
        public abstract float getOffsetForChainedPropertyAnimation(DynamicAnimation.ViewProperty viewProperty, int i);

        /* access modifiers changed from: package-private */
        public abstract SpringForce getSpringForce(DynamicAnimation.ViewProperty viewProperty, View view);

        /* access modifiers changed from: package-private */
        public abstract void onActiveControllerForLayout(PhysicsAnimationLayout physicsAnimationLayout);

        /* access modifiers changed from: package-private */
        public abstract void onChildAdded(View view, int i);

        /* access modifiers changed from: package-private */
        public abstract void onChildRemoved(View view, int i, Runnable runnable);

        /* access modifiers changed from: package-private */
        public abstract void onChildReordered(View view, int i, int i2);

        PhysicsAnimationController() {
        }

        /* access modifiers changed from: protected */
        public boolean isActiveController() {
            PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
            return physicsAnimationLayout != null && this == physicsAnimationLayout.mController;
        }

        /* access modifiers changed from: protected */
        public void setLayout(PhysicsAnimationLayout physicsAnimationLayout) {
            this.mLayout = physicsAnimationLayout;
            onActiveControllerForLayout(physicsAnimationLayout);
        }

        /* access modifiers changed from: protected */
        public PhysicsPropertyAnimator animationForChild(View view) {
            int i = C2219R.C2222id.physics_animator_tag;
            PhysicsPropertyAnimator physicsPropertyAnimator = (PhysicsPropertyAnimator) view.getTag(i);
            if (physicsPropertyAnimator == null) {
                PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
                Objects.requireNonNull(physicsAnimationLayout);
                physicsPropertyAnimator = new PhysicsPropertyAnimator(view);
                view.setTag(i, physicsPropertyAnimator);
            }
            physicsPropertyAnimator.clearAnimator();
            physicsPropertyAnimator.setAssociatedController(this);
            return physicsPropertyAnimator;
        }

        /* access modifiers changed from: protected */
        public PhysicsPropertyAnimator animationForChildAtIndex(int i) {
            return animationForChild(this.mLayout.getChildAt(i));
        }

        /* access modifiers changed from: protected */
        public MultiAnimationStarter animationsForChildrenFromIndex(int i, ChildAnimationConfigurator childAnimationConfigurator) {
            HashSet hashSet = new HashSet();
            ArrayList arrayList = new ArrayList();
            while (i < this.mLayout.getChildCount()) {
                PhysicsPropertyAnimator animationForChildAtIndex = animationForChildAtIndex(i);
                childAnimationConfigurator.configureAnimationForChildAtIndex(i, animationForChildAtIndex);
                hashSet.addAll(animationForChildAtIndex.getAnimatedProperties());
                arrayList.add(animationForChildAtIndex);
                i++;
            }
            return new C2272x49ea1e05(this, hashSet, arrayList);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$animationsForChildrenFromIndex$1(Set set, List list, Runnable[] runnableArr) {
            C2274x49ea1e07 physicsAnimationLayout$PhysicsAnimationController$$ExternalSyntheticLambda2 = new C2274x49ea1e07(runnableArr);
            if (this.mLayout.getChildCount() == 0) {
                physicsAnimationLayout$PhysicsAnimationController$$ExternalSyntheticLambda2.run();
                return;
            }
            if (runnableArr != null) {
                setEndActionForMultipleProperties(physicsAnimationLayout$PhysicsAnimationController$$ExternalSyntheticLambda2, (DynamicAnimation.ViewProperty[]) set.toArray(new DynamicAnimation.ViewProperty[0]));
            }
            Iterator it = list.iterator();
            while (it.hasNext()) {
                ((PhysicsPropertyAnimator) it.next()).start(new Runnable[0]);
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$animationsForChildrenFromIndex$0(Runnable[] runnableArr) {
            for (Runnable run : runnableArr) {
                run.run();
            }
        }

        /* access modifiers changed from: protected */
        public void setEndActionForProperty(Runnable runnable, DynamicAnimation.ViewProperty viewProperty) {
            this.mLayout.mEndActionForProperty.put(viewProperty, runnable);
        }

        /* access modifiers changed from: protected */
        public void setEndActionForMultipleProperties(Runnable runnable, DynamicAnimation.ViewProperty... viewPropertyArr) {
            C2273x49ea1e06 physicsAnimationLayout$PhysicsAnimationController$$ExternalSyntheticLambda1 = new C2273x49ea1e06(this, viewPropertyArr, runnable);
            for (DynamicAnimation.ViewProperty endActionForProperty : viewPropertyArr) {
                setEndActionForProperty(physicsAnimationLayout$PhysicsAnimationController$$ExternalSyntheticLambda1, endActionForProperty);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setEndActionForMultipleProperties$2(DynamicAnimation.ViewProperty[] viewPropertyArr, Runnable runnable) {
            if (!this.mLayout.arePropertiesAnimating(viewPropertyArr)) {
                runnable.run();
                for (DynamicAnimation.ViewProperty removeEndActionForProperty : viewPropertyArr) {
                    removeEndActionForProperty(removeEndActionForProperty);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void removeEndActionForProperty(DynamicAnimation.ViewProperty viewProperty) {
            this.mLayout.mEndActionForProperty.remove(viewProperty);
        }
    }

    public PhysicsAnimationLayout(Context context) {
        super(context);
    }

    public void setActiveController(PhysicsAnimationController physicsAnimationController) {
        cancelAllAnimations();
        this.mEndActionForProperty.clear();
        this.mController = physicsAnimationController;
        physicsAnimationController.setLayout(this);
        for (DynamicAnimation.ViewProperty upAnimationsForProperty : this.mController.getAnimatedProperties()) {
            setUpAnimationsForProperty(upAnimationsForProperty);
        }
    }

    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        addViewInternal(view, i, layoutParams, false);
    }

    public void removeView(View view) {
        if (this.mController != null) {
            int indexOfChild = indexOfChild(view);
            super.removeView(view);
            addTransientView(view, indexOfChild);
            this.mController.onChildRemoved(view, indexOfChild, new PhysicsAnimationLayout$$ExternalSyntheticLambda1(this, view));
            return;
        }
        super.removeView(view);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeView$0(View view) {
        cancelAnimationsOnView(view);
        removeTransientView(view);
    }

    public void removeViewAt(int i) {
        removeView(getChildAt(i));
    }

    public void reorderView(View view, int i) {
        if (view != null) {
            int indexOfChild = indexOfChild(view);
            super.removeView(view);
            addViewInternal(view, i, view.getLayoutParams(), true);
            PhysicsAnimationController physicsAnimationController = this.mController;
            if (physicsAnimationController != null) {
                physicsAnimationController.onChildReordered(view, indexOfChild, i);
            }
        }
    }

    public boolean arePropertiesAnimating(DynamicAnimation.ViewProperty... viewPropertyArr) {
        for (int i = 0; i < getChildCount(); i++) {
            if (arePropertiesAnimatingOnView(getChildAt(i), viewPropertyArr)) {
                return true;
            }
        }
        return false;
    }

    public boolean arePropertiesAnimatingOnView(View view, DynamicAnimation.ViewProperty... viewPropertyArr) {
        ObjectAnimator targetAnimatorFromView = getTargetAnimatorFromView(view);
        for (DynamicAnimation.ViewProperty viewProperty : viewPropertyArr) {
            SpringAnimation springAnimationFromView = getSpringAnimationFromView(viewProperty, view);
            if (springAnimationFromView != null && springAnimationFromView.isRunning()) {
                return true;
            }
            if ((viewProperty.equals(DynamicAnimation.TRANSLATION_X) || viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) && targetAnimatorFromView != null && targetAnimatorFromView.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public void cancelAllAnimations() {
        PhysicsAnimationController physicsAnimationController = this.mController;
        if (physicsAnimationController != null) {
            cancelAllAnimationsOfProperties((DynamicAnimation.ViewProperty[]) physicsAnimationController.getAnimatedProperties().toArray(new DynamicAnimation.ViewProperty[0]));
        }
    }

    public void cancelAllAnimationsOfProperties(DynamicAnimation.ViewProperty... viewPropertyArr) {
        if (this.mController != null) {
            for (int i = 0; i < getChildCount(); i++) {
                for (DynamicAnimation.ViewProperty springAnimationAtIndex : viewPropertyArr) {
                    SpringAnimation springAnimationAtIndex2 = getSpringAnimationAtIndex(springAnimationAtIndex, i);
                    if (springAnimationAtIndex2 != null) {
                        springAnimationAtIndex2.cancel();
                    }
                }
                ViewPropertyAnimator viewPropertyAnimatorFromView = getViewPropertyAnimatorFromView(getChildAt(i));
                if (viewPropertyAnimatorFromView != null) {
                    viewPropertyAnimatorFromView.cancel();
                }
            }
        }
    }

    public void cancelAnimationsOnView(View view) {
        ObjectAnimator targetAnimatorFromView = getTargetAnimatorFromView(view);
        if (targetAnimatorFromView != null) {
            targetAnimatorFromView.cancel();
        }
        for (DynamicAnimation.ViewProperty springAnimationFromView : this.mController.getAnimatedProperties()) {
            SpringAnimation springAnimationFromView2 = getSpringAnimationFromView(springAnimationFromView, view);
            if (springAnimationFromView2 != null) {
                springAnimationFromView2.cancel();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isActiveController(PhysicsAnimationController physicsAnimationController) {
        return this.mController == physicsAnimationController;
    }

    /* access modifiers changed from: protected */
    public boolean isFirstChildXLeftOfCenter(float f) {
        if (getChildCount() <= 0 || f + ((float) (getChildAt(0).getWidth() / 2)) >= ((float) (getWidth() / 2))) {
            return false;
        }
        return true;
    }

    protected static String getReadablePropertyName(DynamicAnimation.ViewProperty viewProperty) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            return "TRANSLATION_X";
        }
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            return "TRANSLATION_Y";
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_X)) {
            return "SCALE_X";
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_Y)) {
            return "SCALE_Y";
        }
        return viewProperty.equals(DynamicAnimation.ALPHA) ? "ALPHA" : "Unknown animation property.";
    }

    private void addViewInternal(View view, int i, ViewGroup.LayoutParams layoutParams, boolean z) {
        super.addView(view, i, layoutParams);
        PhysicsAnimationController physicsAnimationController = this.mController;
        if (physicsAnimationController != null && !z) {
            for (DynamicAnimation.ViewProperty upAnimationForChild : physicsAnimationController.getAnimatedProperties()) {
                setUpAnimationForChild(upAnimationForChild, view);
            }
            this.mController.onChildAdded(view, i);
        }
    }

    private SpringAnimation getSpringAnimationAtIndex(DynamicAnimation.ViewProperty viewProperty, int i) {
        return getSpringAnimationFromView(viewProperty, getChildAt(i));
    }

    /* access modifiers changed from: private */
    public SpringAnimation getSpringAnimationFromView(DynamicAnimation.ViewProperty viewProperty, View view) {
        return (SpringAnimation) view.getTag(getTagIdForProperty(viewProperty));
    }

    private ViewPropertyAnimator getViewPropertyAnimatorFromView(View view) {
        return (ViewPropertyAnimator) view.getTag(C2219R.C2222id.reorder_animator_tag);
    }

    /* access modifiers changed from: private */
    public ObjectAnimator getTargetAnimatorFromView(View view) {
        return (ObjectAnimator) view.getTag(C2219R.C2222id.target_animator_tag);
    }

    private void setUpAnimationsForProperty(DynamicAnimation.ViewProperty viewProperty) {
        for (int i = 0; i < getChildCount(); i++) {
            setUpAnimationForChild(viewProperty, getChildAt(i));
        }
    }

    private void setUpAnimationForChild(DynamicAnimation.ViewProperty viewProperty, View view) {
        SpringAnimation springAnimation = new SpringAnimation(view, viewProperty);
        springAnimation.addUpdateListener(new PhysicsAnimationLayout$$ExternalSyntheticLambda0(this, view, viewProperty));
        springAnimation.setSpring(this.mController.getSpringForce(viewProperty, view));
        springAnimation.addEndListener(new AllAnimationsForPropertyFinishedEndListener(viewProperty));
        view.setTag(getTagIdForProperty(viewProperty), springAnimation);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setUpAnimationForChild$1(View view, DynamicAnimation.ViewProperty viewProperty, DynamicAnimation dynamicAnimation, float f, float f2) {
        SpringAnimation springAnimationAtIndex;
        int indexOfChild = indexOfChild(view);
        int nextAnimationInChain = this.mController.getNextAnimationInChain(viewProperty, indexOfChild);
        if (nextAnimationInChain != -1 && indexOfChild >= 0) {
            float offsetForChainedPropertyAnimation = this.mController.getOffsetForChainedPropertyAnimation(viewProperty, nextAnimationInChain);
            if (nextAnimationInChain < getChildCount() && (springAnimationAtIndex = getSpringAnimationAtIndex(viewProperty, nextAnimationInChain)) != null) {
                springAnimationAtIndex.animateToFinalPosition(f + offsetForChainedPropertyAnimation);
            }
        }
    }

    /* access modifiers changed from: private */
    public int getTagIdForProperty(DynamicAnimation.ViewProperty viewProperty) {
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_X)) {
            return C2219R.C2222id.translation_x_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.TRANSLATION_Y)) {
            return C2219R.C2222id.translation_y_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_X)) {
            return C2219R.C2222id.scale_x_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.SCALE_Y)) {
            return C2219R.C2222id.scale_y_dynamicanimation_tag;
        }
        if (viewProperty.equals(DynamicAnimation.ALPHA)) {
            return C2219R.C2222id.alpha_dynamicanimation_tag;
        }
        return -1;
    }

    /* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$AllAnimationsForPropertyFinishedEndListener */
    protected class AllAnimationsForPropertyFinishedEndListener implements DynamicAnimation.OnAnimationEndListener {
        private DynamicAnimation.ViewProperty mProperty;

        AllAnimationsForPropertyFinishedEndListener(DynamicAnimation.ViewProperty viewProperty) {
            this.mProperty = viewProperty;
        }

        public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            Runnable runnable;
            if (!PhysicsAnimationLayout.this.arePropertiesAnimating(this.mProperty) && PhysicsAnimationLayout.this.mEndActionForProperty.containsKey(this.mProperty) && (runnable = PhysicsAnimationLayout.this.mEndActionForProperty.get(this.mProperty)) != null) {
                runnable.run();
            }
        }
    }

    /* renamed from: com.android.wm.shell.bubbles.animation.PhysicsAnimationLayout$PhysicsPropertyAnimator */
    protected class PhysicsPropertyAnimator {
        private Map<DynamicAnimation.ViewProperty, Float> mAnimatedProperties = new HashMap();
        private PhysicsAnimationController mAssociatedController;
        /* access modifiers changed from: private */
        public PointF mCurrentPointOnPath = new PointF();
        private final FloatProperty<PhysicsPropertyAnimator> mCurrentPointOnPathXProperty = new FloatProperty<PhysicsPropertyAnimator>("PathX") {
            public void setValue(PhysicsPropertyAnimator physicsPropertyAnimator, float f) {
                PhysicsPropertyAnimator.this.mCurrentPointOnPath.x = f;
            }

            public Float get(PhysicsPropertyAnimator physicsPropertyAnimator) {
                return Float.valueOf(PhysicsPropertyAnimator.this.mCurrentPointOnPath.x);
            }
        };
        private final FloatProperty<PhysicsPropertyAnimator> mCurrentPointOnPathYProperty = new FloatProperty<PhysicsPropertyAnimator>("PathY") {
            public void setValue(PhysicsPropertyAnimator physicsPropertyAnimator, float f) {
                PhysicsPropertyAnimator.this.mCurrentPointOnPath.y = f;
            }

            public Float get(PhysicsPropertyAnimator physicsPropertyAnimator) {
                return Float.valueOf(PhysicsPropertyAnimator.this.mCurrentPointOnPath.y);
            }
        };
        /* access modifiers changed from: private */
        public float mDampingRatio = -1.0f;
        /* access modifiers changed from: private */
        public float mDefaultStartVelocity = -3.4028235E38f;
        private Map<DynamicAnimation.ViewProperty, Runnable[]> mEndActionsForProperty = new HashMap();
        private Map<DynamicAnimation.ViewProperty, Float> mInitialPropertyValues = new HashMap();
        private ObjectAnimator mPathAnimator;
        private Runnable[] mPositionEndActions;
        private Map<DynamicAnimation.ViewProperty, Float> mPositionStartVelocities = new HashMap();
        private long mStartDelay = 0;
        /* access modifiers changed from: private */
        public float mStiffness = -1.0f;
        /* access modifiers changed from: private */
        public View mView;

        protected PhysicsPropertyAnimator(View view) {
            this.mView = view;
        }

        public PhysicsPropertyAnimator property(DynamicAnimation.ViewProperty viewProperty, float f, Runnable... runnableArr) {
            this.mAnimatedProperties.put(viewProperty, Float.valueOf(f));
            this.mEndActionsForProperty.put(viewProperty, runnableArr);
            return this;
        }

        public PhysicsPropertyAnimator alpha(float f, Runnable... runnableArr) {
            return property(DynamicAnimation.ALPHA, f, runnableArr);
        }

        public PhysicsPropertyAnimator translationX(float f, Runnable... runnableArr) {
            this.mPathAnimator = null;
            return property(DynamicAnimation.TRANSLATION_X, f, runnableArr);
        }

        public PhysicsPropertyAnimator translationX(float f, float f2, Runnable... runnableArr) {
            this.mInitialPropertyValues.put(DynamicAnimation.TRANSLATION_X, Float.valueOf(f));
            return translationX(f2, runnableArr);
        }

        public PhysicsPropertyAnimator translationY(float f, Runnable... runnableArr) {
            this.mPathAnimator = null;
            return property(DynamicAnimation.TRANSLATION_Y, f, runnableArr);
        }

        public PhysicsPropertyAnimator translationY(float f, float f2, Runnable... runnableArr) {
            this.mInitialPropertyValues.put(DynamicAnimation.TRANSLATION_Y, Float.valueOf(f));
            return translationY(f2, runnableArr);
        }

        public PhysicsPropertyAnimator position(float f, float f2, Runnable... runnableArr) {
            this.mPositionEndActions = runnableArr;
            translationX(f, new Runnable[0]);
            return translationY(f2, new Runnable[0]);
        }

        public PhysicsPropertyAnimator followAnimatedTargetAlongPath(Path path, int i, TimeInterpolator timeInterpolator, final Runnable... runnableArr) {
            ObjectAnimator objectAnimator = this.mPathAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, this.mCurrentPointOnPathXProperty, this.mCurrentPointOnPathYProperty, path);
            this.mPathAnimator = ofFloat;
            if (runnableArr != null) {
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        for (Runnable runnable : runnableArr) {
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    }
                });
            }
            this.mPathAnimator.setDuration((long) i);
            this.mPathAnimator.setInterpolator(timeInterpolator);
            clearTranslationValues();
            return this;
        }

        private void clearTranslationValues() {
            Map<DynamicAnimation.ViewProperty, Float> map = this.mAnimatedProperties;
            DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.TRANSLATION_X;
            map.remove(viewProperty);
            Map<DynamicAnimation.ViewProperty, Float> map2 = this.mAnimatedProperties;
            DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.TRANSLATION_Y;
            map2.remove(viewProperty2);
            this.mInitialPropertyValues.remove(viewProperty);
            this.mInitialPropertyValues.remove(viewProperty2);
            PhysicsAnimationLayout.this.mEndActionForProperty.remove(viewProperty);
            PhysicsAnimationLayout.this.mEndActionForProperty.remove(viewProperty2);
        }

        public PhysicsPropertyAnimator scaleX(float f, Runnable... runnableArr) {
            return property(DynamicAnimation.SCALE_X, f, runnableArr);
        }

        public PhysicsPropertyAnimator scaleY(float f, Runnable... runnableArr) {
            return property(DynamicAnimation.SCALE_Y, f, runnableArr);
        }

        public PhysicsPropertyAnimator withStiffness(float f) {
            this.mStiffness = f;
            return this;
        }

        public PhysicsPropertyAnimator withPositionStartVelocities(float f, float f2) {
            this.mPositionStartVelocities.put(DynamicAnimation.TRANSLATION_X, Float.valueOf(f));
            this.mPositionStartVelocities.put(DynamicAnimation.TRANSLATION_Y, Float.valueOf(f2));
            return this;
        }

        public PhysicsPropertyAnimator withStartDelay(long j) {
            this.mStartDelay = j;
            return this;
        }

        public void start(Runnable... runnableArr) {
            if (!PhysicsAnimationLayout.this.isActiveController(this.mAssociatedController)) {
                Log.w("Bubbs.PAL", "Only the active animation controller is allowed to start animations. Use PhysicsAnimationLayout#setActiveController to set the active animation controller.");
                return;
            }
            Set<DynamicAnimation.ViewProperty> animatedProperties = getAnimatedProperties();
            if (runnableArr != null && runnableArr.length > 0) {
                this.mAssociatedController.setEndActionForMultipleProperties(new C2279x4b8fea79(runnableArr), (DynamicAnimation.ViewProperty[]) animatedProperties.toArray(new DynamicAnimation.ViewProperty[0]));
            }
            if (this.mPositionEndActions != null) {
                PhysicsAnimationLayout physicsAnimationLayout = PhysicsAnimationLayout.this;
                DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.TRANSLATION_X;
                SpringAnimation access$300 = physicsAnimationLayout.getSpringAnimationFromView(viewProperty, this.mView);
                PhysicsAnimationLayout physicsAnimationLayout2 = PhysicsAnimationLayout.this;
                DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.TRANSLATION_Y;
                C2278x4b8fea78 physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda3 = new C2278x4b8fea78(this, access$300, physicsAnimationLayout2.getSpringAnimationFromView(viewProperty2, this.mView));
                this.mEndActionsForProperty.put(viewProperty, new Runnable[]{physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda3});
                this.mEndActionsForProperty.put(viewProperty2, new Runnable[]{physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda3});
            }
            if (this.mPathAnimator != null) {
                startPathAnimation();
            }
            for (DynamicAnimation.ViewProperty next : animatedProperties) {
                if (this.mPathAnimator == null || (!next.equals(DynamicAnimation.TRANSLATION_X) && !next.equals(DynamicAnimation.TRANSLATION_Y))) {
                    if (this.mInitialPropertyValues.containsKey(next)) {
                        next.setValue(this.mView, this.mInitialPropertyValues.get(next).floatValue());
                    }
                    SpringForce springForce = PhysicsAnimationLayout.this.mController.getSpringForce(next, this.mView);
                    View view = this.mView;
                    float floatValue = this.mAnimatedProperties.get(next).floatValue();
                    float floatValue2 = this.mPositionStartVelocities.getOrDefault(next, Float.valueOf(this.mDefaultStartVelocity)).floatValue();
                    long j = this.mStartDelay;
                    float f = this.mStiffness;
                    if (f < 0.0f) {
                        f = springForce.getStiffness();
                    }
                    float f2 = f;
                    float f3 = this.mDampingRatio;
                    animateValueForChild(next, view, floatValue, floatValue2, j, f2, f3 >= 0.0f ? f3 : springForce.getDampingRatio(), this.mEndActionsForProperty.get(next));
                } else {
                    return;
                }
            }
            clearAnimator();
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$start$0(Runnable[] runnableArr) {
            for (Runnable run : runnableArr) {
                run.run();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$start$1(SpringAnimation springAnimation, SpringAnimation springAnimation2) {
            if (!springAnimation.isRunning() && !springAnimation2.isRunning()) {
                Runnable[] runnableArr = this.mPositionEndActions;
                if (runnableArr != null) {
                    for (Runnable run : runnableArr) {
                        run.run();
                    }
                }
                this.mPositionEndActions = null;
            }
        }

        /* access modifiers changed from: protected */
        public Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
            HashSet hashSet = new HashSet(this.mAnimatedProperties.keySet());
            if (this.mPathAnimator != null) {
                hashSet.add(DynamicAnimation.TRANSLATION_X);
                hashSet.add(DynamicAnimation.TRANSLATION_Y);
            }
            return hashSet;
        }

        /* access modifiers changed from: protected */
        public void animateValueForChild(DynamicAnimation.ViewProperty viewProperty, View view, float f, float f2, long j, float f3, float f4, Runnable... runnableArr) {
            long j2 = j;
            final Runnable[] runnableArr2 = runnableArr;
            if (view != null) {
                DynamicAnimation.ViewProperty viewProperty2 = viewProperty;
                SpringAnimation springAnimation = (SpringAnimation) view.getTag(PhysicsAnimationLayout.this.getTagIdForProperty(viewProperty));
                if (springAnimation != null) {
                    if (runnableArr2 != null) {
                        springAnimation.addEndListener(new OneTimeEndListener() {
                            public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                                super.onAnimationEnd(dynamicAnimation, z, f, f2);
                                for (Runnable run : runnableArr2) {
                                    run.run();
                                }
                            }
                        });
                    }
                    SpringForce spring = springAnimation.getSpring();
                    if (spring != null) {
                        C2276x4b8fea76 physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda1 = new C2276x4b8fea76(spring, f3, f4, f2, springAnimation, f);
                        if (j2 > 0) {
                            PhysicsAnimationLayout.this.postDelayed(physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda1, j2);
                        } else {
                            physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda1.run();
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$animateValueForChild$2(SpringForce springForce, float f, float f2, float f3, SpringAnimation springAnimation, float f4) {
            springForce.setStiffness(f);
            springForce.setDampingRatio(f2);
            if (f3 > -3.4028235E38f) {
                springAnimation.setStartVelocity(f3);
            }
            springForce.setFinalPosition(f4);
            springAnimation.start();
        }

        private void updateValueForChild(DynamicAnimation.ViewProperty viewProperty, View view, float f) {
            SpringAnimation springAnimation;
            SpringForce spring;
            if (view != null && (springAnimation = (SpringAnimation) view.getTag(PhysicsAnimationLayout.this.getTagIdForProperty(viewProperty))) != null && (spring = springAnimation.getSpring()) != null) {
                spring.setFinalPosition(f);
                springAnimation.start();
            }
        }

        /* access modifiers changed from: protected */
        public void startPathAnimation() {
            final SpringForce springForce = PhysicsAnimationLayout.this.mController.getSpringForce(DynamicAnimation.TRANSLATION_X, this.mView);
            final SpringForce springForce2 = PhysicsAnimationLayout.this.mController.getSpringForce(DynamicAnimation.TRANSLATION_Y, this.mView);
            long j = this.mStartDelay;
            if (j > 0) {
                this.mPathAnimator.setStartDelay(j);
            }
            final C2277x4b8fea77 physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda2 = new C2277x4b8fea77(this);
            this.mPathAnimator.addUpdateListener(new C2275x4b8fea75(physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda2));
            this.mPathAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    float f;
                    float f2;
                    PhysicsPropertyAnimator physicsPropertyAnimator = PhysicsPropertyAnimator.this;
                    DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.TRANSLATION_X;
                    View access$500 = physicsPropertyAnimator.mView;
                    float f3 = PhysicsPropertyAnimator.this.mCurrentPointOnPath.x;
                    float access$600 = PhysicsPropertyAnimator.this.mDefaultStartVelocity;
                    float access$700 = PhysicsPropertyAnimator.this.mStiffness >= 0.0f ? PhysicsPropertyAnimator.this.mStiffness : springForce.getStiffness();
                    if (PhysicsPropertyAnimator.this.mDampingRatio >= 0.0f) {
                        f = PhysicsPropertyAnimator.this.mDampingRatio;
                    } else {
                        f = springForce.getDampingRatio();
                    }
                    physicsPropertyAnimator.animateValueForChild(viewProperty, access$500, f3, access$600, 0, access$700, f, new Runnable[0]);
                    PhysicsPropertyAnimator physicsPropertyAnimator2 = PhysicsPropertyAnimator.this;
                    DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.TRANSLATION_Y;
                    View access$5002 = physicsPropertyAnimator2.mView;
                    float f4 = PhysicsPropertyAnimator.this.mCurrentPointOnPath.y;
                    float access$6002 = PhysicsPropertyAnimator.this.mDefaultStartVelocity;
                    float access$7002 = PhysicsPropertyAnimator.this.mStiffness >= 0.0f ? PhysicsPropertyAnimator.this.mStiffness : springForce2.getStiffness();
                    if (PhysicsPropertyAnimator.this.mDampingRatio >= 0.0f) {
                        f2 = PhysicsPropertyAnimator.this.mDampingRatio;
                    } else {
                        f2 = springForce2.getDampingRatio();
                    }
                    physicsPropertyAnimator2.animateValueForChild(viewProperty2, access$5002, f4, access$6002, 0, access$7002, f2, new Runnable[0]);
                }

                public void onAnimationEnd(Animator animator) {
                    physicsAnimationLayout$PhysicsPropertyAnimator$$ExternalSyntheticLambda2.run();
                }
            });
            ObjectAnimator access$900 = PhysicsAnimationLayout.this.getTargetAnimatorFromView(this.mView);
            if (access$900 != null) {
                access$900.cancel();
            }
            this.mView.setTag(C2219R.C2222id.target_animator_tag, this.mPathAnimator);
            this.mPathAnimator.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startPathAnimation$3() {
            updateValueForChild(DynamicAnimation.TRANSLATION_X, this.mView, this.mCurrentPointOnPath.x);
            updateValueForChild(DynamicAnimation.TRANSLATION_Y, this.mView, this.mCurrentPointOnPath.y);
        }

        /* access modifiers changed from: private */
        public void clearAnimator() {
            this.mInitialPropertyValues.clear();
            this.mAnimatedProperties.clear();
            this.mPositionStartVelocities.clear();
            this.mDefaultStartVelocity = -3.4028235E38f;
            this.mStartDelay = 0;
            this.mStiffness = -1.0f;
            this.mDampingRatio = -1.0f;
            this.mEndActionsForProperty.clear();
            this.mPathAnimator = null;
            this.mPositionEndActions = null;
        }

        /* access modifiers changed from: private */
        public void setAssociatedController(PhysicsAnimationController physicsAnimationController) {
            this.mAssociatedController = physicsAnimationController;
        }
    }
}
