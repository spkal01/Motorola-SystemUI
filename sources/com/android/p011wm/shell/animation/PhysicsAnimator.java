package com.android.p011wm.shell.animation;

import android.util.ArrayMap;
import android.util.Log;
import androidx.dynamicanimation.animation.AnimationHandler;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.wm.shell.animation.PhysicsAnimator */
/* compiled from: PhysicsAnimator.kt */
public final class PhysicsAnimator<T> {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public static Function1<Object, ? extends PhysicsAnimator<?>> instanceConstructor = PhysicsAnimator$Companion$instanceConstructor$1.INSTANCE;
    @NotNull
    private Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> cancelAction;
    /* access modifiers changed from: private */
    @Nullable
    public AnimationHandler customAnimationHandler;
    @NotNull
    private FlingConfig defaultFling;
    @NotNull
    private SpringConfig defaultSpring;
    @NotNull
    private final ArrayList<Function0<Unit>> endActions;
    @NotNull
    private final ArrayList<EndListener<T>> endListeners;
    /* access modifiers changed from: private */
    @NotNull
    public final ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation> flingAnimations;
    @NotNull
    private final ArrayMap<FloatPropertyCompat<? super T>, FlingConfig> flingConfigs;
    @NotNull
    private ArrayList<PhysicsAnimator<T>.InternalListener> internalListeners;
    /* access modifiers changed from: private */
    @NotNull
    public final ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation> springAnimations;
    @NotNull
    private final ArrayMap<FloatPropertyCompat<? super T>, SpringConfig> springConfigs;
    @NotNull
    private Function0<Unit> startAction;
    @NotNull
    private final ArrayList<UpdateListener<T>> updateListeners;
    @NotNull
    private final WeakReference<T> weakTarget;

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$EndListener */
    /* compiled from: PhysicsAnimator.kt */
    public interface EndListener<T> {
        void onAnimationEnd(T t, @NotNull FloatPropertyCompat<? super T> floatPropertyCompat, boolean z, boolean z2, float f, float f2, boolean z3);
    }

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$UpdateListener */
    /* compiled from: PhysicsAnimator.kt */
    public interface UpdateListener<T> {
        void onAnimationUpdateForProperty(T t, @NotNull ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate> arrayMap);
    }

    public /* synthetic */ PhysicsAnimator(Object obj, DefaultConstructorMarker defaultConstructorMarker) {
        this(obj);
    }

    public static final float estimateFlingEndValue(float f, float f2, @NotNull FlingConfig flingConfig) {
        return Companion.estimateFlingEndValue(f, f2, flingConfig);
    }

    @NotNull
    public static final <T> PhysicsAnimator<T> getInstance(@NotNull T t) {
        return Companion.getInstance(t);
    }

    private final boolean isValidValue(float f) {
        return f < Float.MAX_VALUE && f > -3.4028235E38f;
    }

    @NotNull
    public final PhysicsAnimator<T> flingThenSpring(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f, @NotNull FlingConfig flingConfig, @NotNull SpringConfig springConfig) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        Intrinsics.checkNotNullParameter(flingConfig, "flingConfig");
        Intrinsics.checkNotNullParameter(springConfig, "springConfig");
        return flingThenSpring$default(this, floatPropertyCompat, f, flingConfig, springConfig, false, 16, (Object) null);
    }

    private PhysicsAnimator(T t) {
        this.weakTarget = new WeakReference<>(t);
        this.springAnimations = new ArrayMap<>();
        this.flingAnimations = new ArrayMap<>();
        this.springConfigs = new ArrayMap<>();
        this.flingConfigs = new ArrayMap<>();
        this.updateListeners = new ArrayList<>();
        this.endListeners = new ArrayList<>();
        this.endActions = new ArrayList<>();
        this.defaultSpring = PhysicsAnimatorKt.globalDefaultSpring;
        this.defaultFling = PhysicsAnimatorKt.globalDefaultFling;
        this.internalListeners = new ArrayList<>();
        this.startAction = new PhysicsAnimator$startAction$1(this);
        this.cancelAction = new PhysicsAnimator$cancelAction$1(this);
    }

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$AnimationUpdate */
    /* compiled from: PhysicsAnimator.kt */
    public static final class AnimationUpdate {
        private final float value;
        private final float velocity;

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AnimationUpdate)) {
                return false;
            }
            AnimationUpdate animationUpdate = (AnimationUpdate) obj;
            return Intrinsics.areEqual((Object) Float.valueOf(this.value), (Object) Float.valueOf(animationUpdate.value)) && Intrinsics.areEqual((Object) Float.valueOf(this.velocity), (Object) Float.valueOf(animationUpdate.velocity));
        }

        public int hashCode() {
            return (Float.hashCode(this.value) * 31) + Float.hashCode(this.velocity);
        }

        @NotNull
        public String toString() {
            return "AnimationUpdate(value=" + this.value + ", velocity=" + this.velocity + ')';
        }

        public AnimationUpdate(float f, float f2) {
            this.value = f;
            this.velocity = f2;
        }
    }

    @NotNull
    /* renamed from: getInternalListeners$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
    public final ArrayList<PhysicsAnimator<T>.InternalListener> mo25469x79845134() {
        return this.internalListeners;
    }

    @NotNull
    public final PhysicsAnimator<T> spring(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2, float f3, float f4) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        if (PhysicsAnimatorKt.verboseLogging) {
            Log.d("PhysicsAnimator", "Springing " + Companion.getReadablePropertyName(floatPropertyCompat) + " to " + f + '.');
        }
        this.springConfigs.put(floatPropertyCompat, new SpringConfig(f3, f4, f2, f));
        return this;
    }

    public static /* synthetic */ PhysicsAnimator spring$default(PhysicsAnimator physicsAnimator, FloatPropertyCompat floatPropertyCompat, float f, float f2, SpringConfig springConfig, int i, Object obj) {
        if ((i & 8) != 0) {
            springConfig = physicsAnimator.defaultSpring;
        }
        return physicsAnimator.spring(floatPropertyCompat, f, f2, springConfig);
    }

    @NotNull
    public final PhysicsAnimator<T> spring(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2, @NotNull SpringConfig springConfig) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        Intrinsics.checkNotNullParameter(springConfig, "config");
        return spring(floatPropertyCompat, f, f2, springConfig.getStiffness(), springConfig.mo25507xc4c639c7());
    }

    @NotNull
    public final PhysicsAnimator<T> spring(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f, @NotNull SpringConfig springConfig) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        Intrinsics.checkNotNullParameter(springConfig, "config");
        return spring(floatPropertyCompat, f, 0.0f, springConfig);
    }

    @NotNull
    public final PhysicsAnimator<T> spring(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        return spring$default(this, floatPropertyCompat, f, 0.0f, (SpringConfig) null, 8, (Object) null);
    }

    public static /* synthetic */ PhysicsAnimator flingThenSpring$default(PhysicsAnimator physicsAnimator, FloatPropertyCompat floatPropertyCompat, float f, FlingConfig flingConfig, SpringConfig springConfig, boolean z, int i, Object obj) {
        if ((i & 16) != 0) {
            z = false;
        }
        return physicsAnimator.flingThenSpring(floatPropertyCompat, f, flingConfig, springConfig, z);
    }

    @NotNull
    public final PhysicsAnimator<T> flingThenSpring(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f, @NotNull FlingConfig flingConfig, @NotNull SpringConfig springConfig, boolean z) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        Intrinsics.checkNotNullParameter(flingConfig, "flingConfig");
        Intrinsics.checkNotNullParameter(springConfig, "springConfig");
        Object obj = this.weakTarget.get();
        if (obj == null) {
            Log.w("PhysicsAnimator", "Trying to animate a GC-ed target.");
            return this;
        }
        FlingConfig copy$default = FlingConfig.copy$default(flingConfig, 0.0f, 0.0f, 0.0f, 0.0f, 15, (Object) null);
        SpringConfig copy$default2 = SpringConfig.copy$default(springConfig, 0.0f, 0.0f, 0.0f, 0.0f, 15, (Object) null);
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        float min = i < 0 ? flingConfig.getMin() : flingConfig.getMax();
        if (!z || !isValidValue(min)) {
            copy$default.mo25500x3e6fa5c5(f);
        } else {
            float value = floatPropertyCompat.getValue(obj) + (f / (flingConfig.mo25493x77a27d58() * 4.2f));
            float min2 = (flingConfig.getMin() + flingConfig.getMax()) / ((float) 2);
            if ((i < 0 && value > min2) || (f > 0.0f && value < min2)) {
                float min3 = value < min2 ? flingConfig.getMin() : flingConfig.getMax();
                if (isValidValue(min3)) {
                    return spring(floatPropertyCompat, min3, f, springConfig);
                }
            }
            float value2 = min - floatPropertyCompat.getValue(obj);
            float friction$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell = flingConfig.mo25493x77a27d58() * 4.2f * value2;
            if (value2 > 0.0f && f >= 0.0f) {
                f = Math.max(friction$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell, f);
            } else if (value2 < 0.0f && i <= 0) {
                f = Math.min(friction$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell, f);
            }
            copy$default.mo25500x3e6fa5c5(f);
            copy$default2.mo25511xb5ce1325(min);
        }
        this.flingConfigs.put(floatPropertyCompat, copy$default);
        this.springConfigs.put(floatPropertyCompat, copy$default2);
        return this;
    }

    @NotNull
    public final PhysicsAnimator<T> addUpdateListener(@NotNull UpdateListener<T> updateListener) {
        Intrinsics.checkNotNullParameter(updateListener, "listener");
        this.updateListeners.add(updateListener);
        return this;
    }

    @NotNull
    public final PhysicsAnimator<T> addEndListener(@NotNull EndListener<T> endListener) {
        Intrinsics.checkNotNullParameter(endListener, "listener");
        this.endListeners.add(endListener);
        return this;
    }

    @NotNull
    public final PhysicsAnimator<T> withEndActions(@NotNull Function0<Unit>... function0Arr) {
        Intrinsics.checkNotNullParameter(function0Arr, "endActions");
        this.endActions.addAll(ArraysKt___ArraysKt.filterNotNull(function0Arr));
        return this;
    }

    @NotNull
    public final PhysicsAnimator<T> withEndActions(@NotNull Runnable... runnableArr) {
        Intrinsics.checkNotNullParameter(runnableArr, "endActions");
        ArrayList<Function0<Unit>> arrayList = this.endActions;
        List<Runnable> filterNotNull = ArraysKt___ArraysKt.filterNotNull(runnableArr);
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(filterNotNull, 10));
        for (Runnable physicsAnimator$withEndActions$1$1 : filterNotNull) {
            arrayList2.add(new PhysicsAnimator$withEndActions$1$1(physicsAnimator$withEndActions$1$1));
        }
        arrayList.addAll(arrayList2);
        return this;
    }

    public final void setDefaultSpringConfig(@NotNull SpringConfig springConfig) {
        Intrinsics.checkNotNullParameter(springConfig, "defaultSpring");
        this.defaultSpring = springConfig;
    }

    public final void setCustomAnimationHandler(@NotNull AnimationHandler animationHandler) {
        Intrinsics.checkNotNullParameter(animationHandler, "handler");
        this.customAnimationHandler = animationHandler;
    }

    public final void start() {
        this.startAction.invoke();
    }

    /* renamed from: startInternal$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
    public final void mo25479x2d523867() {
        Object obj = this.weakTarget.get();
        if (obj == null) {
            Log.w("PhysicsAnimator", "Trying to animate a GC-ed object.");
            return;
        }
        ArrayList<Function0> arrayList = new ArrayList<>();
        for (FloatPropertyCompat floatPropertyCompat : mo25468xb50c2714()) {
            FlingConfig flingConfig = this.flingConfigs.get(floatPropertyCompat);
            SpringConfig springConfig = this.springConfigs.get(floatPropertyCompat);
            float value = floatPropertyCompat.getValue(obj);
            if (flingConfig != null) {
                arrayList.add(new PhysicsAnimator$startInternal$1(flingConfig, this, floatPropertyCompat, obj, value));
            }
            if (springConfig != null) {
                if (flingConfig == null) {
                    SpringAnimation springAnimation = getSpringAnimation(floatPropertyCompat, obj);
                    if (this.customAnimationHandler != null && !Intrinsics.areEqual((Object) springAnimation.getAnimationHandler(), (Object) this.customAnimationHandler)) {
                        if (springAnimation.isRunning()) {
                            cancel(floatPropertyCompat);
                        }
                        AnimationHandler animationHandler = this.customAnimationHandler;
                        if (animationHandler == null) {
                            animationHandler = springAnimation.getAnimationHandler();
                            Intrinsics.checkNotNullExpressionValue(animationHandler, "springAnim.animationHandler");
                        }
                        springAnimation.setAnimationHandler(animationHandler);
                    }
                    springConfig.mo25504xe3030f23(springAnimation);
                    arrayList.add(new PhysicsAnimator$startInternal$2(springAnimation));
                } else {
                    this.endListeners.add(0, new PhysicsAnimator$startInternal$3(floatPropertyCompat, flingConfig.getMin(), flingConfig.getMax(), springConfig, this));
                }
            }
        }
        this.internalListeners.add(new InternalListener(this, obj, mo25468xb50c2714(), new ArrayList(this.updateListeners), new ArrayList(this.endListeners), new ArrayList(this.endActions)));
        for (Function0 invoke : arrayList) {
            invoke.invoke();
        }
        clearAnimator();
    }

    private final void clearAnimator() {
        this.springConfigs.clear();
        this.flingConfigs.clear();
        this.updateListeners.clear();
        this.endListeners.clear();
        this.endActions.clear();
    }

    /* access modifiers changed from: private */
    public final SpringAnimation getSpringAnimation(FloatPropertyCompat<? super T> floatPropertyCompat, T t) {
        ArrayMap<FloatPropertyCompat<? super T>, SpringAnimation> arrayMap = this.springAnimations;
        SpringAnimation springAnimation = arrayMap.get(floatPropertyCompat);
        if (springAnimation == null) {
            springAnimation = (SpringAnimation) configureDynamicAnimation(new SpringAnimation(t, floatPropertyCompat), floatPropertyCompat);
            arrayMap.put(floatPropertyCompat, springAnimation);
        }
        Intrinsics.checkNotNullExpressionValue(springAnimation, "springAnimations.getOrPut(\n                property,\n                { configureDynamicAnimation(SpringAnimation(target, property), property)\n                        as SpringAnimation })");
        return springAnimation;
    }

    /* access modifiers changed from: private */
    public final FlingAnimation getFlingAnimation(FloatPropertyCompat<? super T> floatPropertyCompat, T t) {
        ArrayMap<FloatPropertyCompat<? super T>, FlingAnimation> arrayMap = this.flingAnimations;
        FlingAnimation flingAnimation = arrayMap.get(floatPropertyCompat);
        if (flingAnimation == null) {
            flingAnimation = (FlingAnimation) configureDynamicAnimation(new FlingAnimation(t, floatPropertyCompat), floatPropertyCompat);
            arrayMap.put(floatPropertyCompat, flingAnimation);
        }
        Intrinsics.checkNotNullExpressionValue(flingAnimation, "flingAnimations.getOrPut(\n                property,\n                { configureDynamicAnimation(FlingAnimation(target, property), property)\n                        as FlingAnimation })");
        return flingAnimation;
    }

    private final DynamicAnimation<?> configureDynamicAnimation(DynamicAnimation<?> dynamicAnimation, FloatPropertyCompat<? super T> floatPropertyCompat) {
        dynamicAnimation.addUpdateListener(new PhysicsAnimator$configureDynamicAnimation$1(this, floatPropertyCompat));
        dynamicAnimation.addEndListener(new PhysicsAnimator$configureDynamicAnimation$2(this, floatPropertyCompat, dynamicAnimation));
        return dynamicAnimation;
    }

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$InternalListener */
    /* compiled from: PhysicsAnimator.kt */
    public final class InternalListener {
        @NotNull
        private List<? extends Function0<Unit>> endActions;
        @NotNull
        private List<? extends EndListener<T>> endListeners;
        private int numPropertiesAnimating;
        @NotNull
        private Set<? extends FloatPropertyCompat<? super T>> properties;
        private final T target;
        final /* synthetic */ PhysicsAnimator<T> this$0;
        @NotNull
        private final ArrayMap<FloatPropertyCompat<? super T>, AnimationUpdate> undispatchedUpdates = new ArrayMap<>();
        @NotNull
        private List<? extends UpdateListener<T>> updateListeners;

        public InternalListener(PhysicsAnimator physicsAnimator, @NotNull T t, @NotNull Set<? extends FloatPropertyCompat<? super T>> set, @NotNull List<? extends UpdateListener<T>> list, @NotNull List<? extends EndListener<T>> list2, List<? extends Function0<Unit>> list3) {
            Intrinsics.checkNotNullParameter(physicsAnimator, "this$0");
            Intrinsics.checkNotNullParameter(set, "properties");
            Intrinsics.checkNotNullParameter(list, "updateListeners");
            Intrinsics.checkNotNullParameter(list2, "endListeners");
            Intrinsics.checkNotNullParameter(list3, "endActions");
            this.this$0 = physicsAnimator;
            this.target = t;
            this.properties = set;
            this.updateListeners = list;
            this.endListeners = list2;
            this.endActions = list3;
            this.numPropertiesAnimating = set.size();
        }

        /* renamed from: onInternalAnimationUpdate$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final void mo25503x996316b9(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, float f, float f2) {
            Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
            if (this.properties.contains(floatPropertyCompat)) {
                this.undispatchedUpdates.put(floatPropertyCompat, new AnimationUpdate(f, f2));
                maybeDispatchUpdates();
            }
        }

        /* renamed from: onInternalAnimationEnd$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final boolean mo25502xf6e998bb(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat, boolean z, float f, float f2, boolean z2) {
            FloatPropertyCompat<? super T> floatPropertyCompat2 = floatPropertyCompat;
            Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
            if (!this.properties.contains(floatPropertyCompat)) {
                return false;
            }
            this.numPropertiesAnimating--;
            maybeDispatchUpdates();
            if (this.undispatchedUpdates.containsKey(floatPropertyCompat)) {
                for (UpdateListener onAnimationUpdateForProperty : this.updateListeners) {
                    T t = this.target;
                    ArrayMap arrayMap = new ArrayMap();
                    arrayMap.put(floatPropertyCompat, this.undispatchedUpdates.get(floatPropertyCompat));
                    Unit unit = Unit.INSTANCE;
                    onAnimationUpdateForProperty.onAnimationUpdateForProperty(t, arrayMap);
                }
                this.undispatchedUpdates.remove(floatPropertyCompat);
            }
            boolean z3 = !this.this$0.arePropertiesAnimating(this.properties);
            List<? extends EndListener<T>> list = this.endListeners;
            PhysicsAnimator<T> physicsAnimator = this.this$0;
            for (EndListener onAnimationEnd : list) {
                onAnimationEnd.onAnimationEnd(this.target, floatPropertyCompat, z2, z, f, f2, z3);
                if (physicsAnimator.isPropertyAnimating(floatPropertyCompat)) {
                    return false;
                }
            }
            if (z3 && !z) {
                for (Function0 invoke : this.endActions) {
                    invoke.invoke();
                }
            }
            return z3;
        }

        private final void maybeDispatchUpdates() {
            if (this.undispatchedUpdates.size() >= this.numPropertiesAnimating && this.undispatchedUpdates.size() > 0) {
                for (UpdateListener onAnimationUpdateForProperty : this.updateListeners) {
                    onAnimationUpdateForProperty.onAnimationUpdateForProperty(this.target, new ArrayMap(this.undispatchedUpdates));
                }
                this.undispatchedUpdates.clear();
            }
        }
    }

    public final boolean isRunning() {
        Set<FloatPropertyCompat<? super T>> keySet = this.springAnimations.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet, "springAnimations.keys");
        Set<FloatPropertyCompat<? super T>> keySet2 = this.flingAnimations.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet2, "flingAnimations.keys");
        return arePropertiesAnimating(CollectionsKt___CollectionsKt.union(keySet, keySet2));
    }

    public final boolean isPropertyAnimating(@NotNull FloatPropertyCompat<? super T> floatPropertyCompat) {
        Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
        SpringAnimation springAnimation = this.springAnimations.get(floatPropertyCompat);
        if (!(springAnimation == null ? false : springAnimation.isRunning())) {
            FlingAnimation flingAnimation = this.flingAnimations.get(floatPropertyCompat);
            if (flingAnimation == null ? false : flingAnimation.isRunning()) {
                return true;
            }
            return false;
        }
        return true;
    }

    @NotNull
    /* renamed from: getAnimatedProperties$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
    public final Set<FloatPropertyCompat<? super T>> mo25468xb50c2714() {
        Set<FloatPropertyCompat<? super T>> keySet = this.springConfigs.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet, "springConfigs.keys");
        Set<FloatPropertyCompat<? super T>> keySet2 = this.flingConfigs.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet2, "flingConfigs.keys");
        return CollectionsKt___CollectionsKt.union(keySet, keySet2);
    }

    /* renamed from: cancelInternal$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
    public final void mo25465xf11df47f(@NotNull Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkNotNullParameter(set, "properties");
        for (FloatPropertyCompat floatPropertyCompat : set) {
            FlingAnimation flingAnimation = this.flingAnimations.get(floatPropertyCompat);
            if (flingAnimation != null) {
                flingAnimation.cancel();
            }
            SpringAnimation springAnimation = this.springAnimations.get(floatPropertyCompat);
            if (springAnimation != null) {
                springAnimation.cancel();
            }
        }
    }

    public final void cancel() {
        Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> function1 = this.cancelAction;
        Set<FloatPropertyCompat<? super T>> keySet = this.flingAnimations.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet, "flingAnimations.keys");
        function1.invoke(keySet);
        Function1<? super Set<? extends FloatPropertyCompat<? super T>>, Unit> function12 = this.cancelAction;
        Set<FloatPropertyCompat<? super T>> keySet2 = this.springAnimations.keySet();
        Intrinsics.checkNotNullExpressionValue(keySet2, "springAnimations.keys");
        function12.invoke(keySet2);
    }

    public final void cancel(@NotNull FloatPropertyCompat<? super T>... floatPropertyCompatArr) {
        Intrinsics.checkNotNullParameter(floatPropertyCompatArr, "properties");
        this.cancelAction.invoke(ArraysKt___ArraysKt.toSet(floatPropertyCompatArr));
    }

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$SpringConfig */
    /* compiled from: PhysicsAnimator.kt */
    public static final class SpringConfig {
        private float dampingRatio;
        private float finalPosition;
        private float startVelocity;
        private float stiffness;

        public static /* synthetic */ SpringConfig copy$default(SpringConfig springConfig, float f, float f2, float f3, float f4, int i, Object obj) {
            if ((i & 1) != 0) {
                f = springConfig.stiffness;
            }
            if ((i & 2) != 0) {
                f2 = springConfig.dampingRatio;
            }
            if ((i & 4) != 0) {
                f3 = springConfig.startVelocity;
            }
            if ((i & 8) != 0) {
                f4 = springConfig.finalPosition;
            }
            return springConfig.copy(f, f2, f3, f4);
        }

        @NotNull
        public final SpringConfig copy(float f, float f2, float f3, float f4) {
            return new SpringConfig(f, f2, f3, f4);
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SpringConfig)) {
                return false;
            }
            SpringConfig springConfig = (SpringConfig) obj;
            return Intrinsics.areEqual((Object) Float.valueOf(this.stiffness), (Object) Float.valueOf(springConfig.stiffness)) && Intrinsics.areEqual((Object) Float.valueOf(this.dampingRatio), (Object) Float.valueOf(springConfig.dampingRatio)) && Intrinsics.areEqual((Object) Float.valueOf(this.startVelocity), (Object) Float.valueOf(springConfig.startVelocity)) && Intrinsics.areEqual((Object) Float.valueOf(this.finalPosition), (Object) Float.valueOf(springConfig.finalPosition));
        }

        public int hashCode() {
            return (((((Float.hashCode(this.stiffness) * 31) + Float.hashCode(this.dampingRatio)) * 31) + Float.hashCode(this.startVelocity)) * 31) + Float.hashCode(this.finalPosition);
        }

        @NotNull
        public String toString() {
            return "SpringConfig(stiffness=" + this.stiffness + ", dampingRatio=" + this.dampingRatio + ", startVelocity=" + this.startVelocity + ", finalPosition=" + this.finalPosition + ')';
        }

        public SpringConfig(float f, float f2, float f3, float f4) {
            this.stiffness = f;
            this.dampingRatio = f2;
            this.startVelocity = f3;
            this.finalPosition = f4;
        }

        public final float getStiffness() {
            return this.stiffness;
        }

        /* renamed from: getDampingRatio$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final float mo25507xc4c639c7() {
            return this.dampingRatio;
        }

        /* renamed from: setStartVelocity$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final void mo25512x3e6fa5c5(float f) {
            this.startVelocity = f;
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public /* synthetic */ SpringConfig(float f, float f2, float f3, float f4, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this(f, f2, (i & 4) != 0 ? 0.0f : f3, (i & 8) != 0 ? PhysicsAnimatorKt.UNSET : f4);
        }

        /* renamed from: getFinalPosition$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final float mo25508x5f9681b1() {
            return this.finalPosition;
        }

        /* renamed from: setFinalPosition$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final void mo25511xb5ce1325(float f) {
            this.finalPosition = f;
        }

        public SpringConfig() {
            this(PhysicsAnimatorKt.globalDefaultSpring.stiffness, PhysicsAnimatorKt.globalDefaultSpring.dampingRatio);
        }

        public SpringConfig(float f, float f2) {
            this(f, f2, 0.0f, 0.0f, 8, (DefaultConstructorMarker) null);
        }

        /* renamed from: applyToAnimation$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final void mo25504xe3030f23(@NotNull SpringAnimation springAnimation) {
            Intrinsics.checkNotNullParameter(springAnimation, "anim");
            SpringForce spring = springAnimation.getSpring();
            if (spring == null) {
                spring = new SpringForce();
            }
            spring.setStiffness(getStiffness());
            spring.setDampingRatio(mo25507xc4c639c7());
            spring.setFinalPosition(mo25508x5f9681b1());
            Unit unit = Unit.INSTANCE;
            springAnimation.setSpring(spring);
            float f = this.startVelocity;
            if (!(f == 0.0f)) {
                springAnimation.setStartVelocity(f);
            }
        }
    }

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$FlingConfig */
    /* compiled from: PhysicsAnimator.kt */
    public static final class FlingConfig {
        private float friction;
        private float max;
        private float min;
        private float startVelocity;

        public static /* synthetic */ FlingConfig copy$default(FlingConfig flingConfig, float f, float f2, float f3, float f4, int i, Object obj) {
            if ((i & 1) != 0) {
                f = flingConfig.friction;
            }
            if ((i & 2) != 0) {
                f2 = flingConfig.min;
            }
            if ((i & 4) != 0) {
                f3 = flingConfig.max;
            }
            if ((i & 8) != 0) {
                f4 = flingConfig.startVelocity;
            }
            return flingConfig.copy(f, f2, f3, f4);
        }

        @NotNull
        public final FlingConfig copy(float f, float f2, float f3, float f4) {
            return new FlingConfig(f, f2, f3, f4);
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FlingConfig)) {
                return false;
            }
            FlingConfig flingConfig = (FlingConfig) obj;
            return Intrinsics.areEqual((Object) Float.valueOf(this.friction), (Object) Float.valueOf(flingConfig.friction)) && Intrinsics.areEqual((Object) Float.valueOf(this.min), (Object) Float.valueOf(flingConfig.min)) && Intrinsics.areEqual((Object) Float.valueOf(this.max), (Object) Float.valueOf(flingConfig.max)) && Intrinsics.areEqual((Object) Float.valueOf(this.startVelocity), (Object) Float.valueOf(flingConfig.startVelocity));
        }

        public int hashCode() {
            return (((((Float.hashCode(this.friction) * 31) + Float.hashCode(this.min)) * 31) + Float.hashCode(this.max)) * 31) + Float.hashCode(this.startVelocity);
        }

        @NotNull
        public String toString() {
            return "FlingConfig(friction=" + this.friction + ", min=" + this.min + ", max=" + this.max + ", startVelocity=" + this.startVelocity + ')';
        }

        public FlingConfig(float f, float f2, float f3, float f4) {
            this.friction = f;
            this.min = f2;
            this.max = f3;
            this.startVelocity = f4;
        }

        /* renamed from: getFriction$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final float mo25493x77a27d58() {
            return this.friction;
        }

        public final float getMin() {
            return this.min;
        }

        public final void setMin(float f) {
            this.min = f;
        }

        public final float getMax() {
            return this.max;
        }

        public final void setMax(float f) {
            this.max = f;
        }

        /* renamed from: getStartVelocity$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final float mo25496xe8381451() {
            return this.startVelocity;
        }

        /* renamed from: setStartVelocity$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final void mo25500x3e6fa5c5(float f) {
            this.startVelocity = f;
        }

        public FlingConfig() {
            this(PhysicsAnimatorKt.globalDefaultFling.friction);
        }

        public FlingConfig(float f) {
            this(f, PhysicsAnimatorKt.globalDefaultFling.min, PhysicsAnimatorKt.globalDefaultFling.max);
        }

        public FlingConfig(float f, float f2, float f3) {
            this(f, f2, f3, 0.0f);
        }

        /* renamed from: applyToAnimation$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final void mo25490xe3030f23(@NotNull FlingAnimation flingAnimation) {
            Intrinsics.checkNotNullParameter(flingAnimation, "anim");
            flingAnimation.setFriction(mo25493x77a27d58());
            flingAnimation.setMinValue(getMin());
            flingAnimation.setMaxValue(getMax());
            flingAnimation.setStartVelocity(mo25496xe8381451());
        }
    }

    /* renamed from: com.android.wm.shell.animation.PhysicsAnimator$Companion */
    /* compiled from: PhysicsAnimator.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        /* renamed from: getInstanceConstructor$frameworks__base__libs__WindowManager__Shell__android_common__WindowManager_Shell */
        public final Function1<Object, PhysicsAnimator<?>> mo25487x26324817() {
            return PhysicsAnimator.instanceConstructor;
        }

        @NotNull
        public final <T> PhysicsAnimator<T> getInstance(@NotNull T t) {
            Intrinsics.checkNotNullParameter(t, "target");
            if (!PhysicsAnimatorKt.getAnimators().containsKey(t)) {
                PhysicsAnimatorKt.getAnimators().put(t, mo25487x26324817().invoke(t));
            }
            PhysicsAnimator<?> physicsAnimator = PhysicsAnimatorKt.getAnimators().get(t);
            Objects.requireNonNull(physicsAnimator, "null cannot be cast to non-null type com.android.wm.shell.animation.PhysicsAnimator<T of com.android.wm.shell.animation.PhysicsAnimator.Companion.getInstance>");
            return physicsAnimator;
        }

        public final float estimateFlingEndValue(float f, float f2, @NotNull FlingConfig flingConfig) {
            Intrinsics.checkNotNullParameter(flingConfig, "flingConfig");
            return Math.min(flingConfig.getMax(), Math.max(flingConfig.getMin(), f + (f2 / (flingConfig.mo25493x77a27d58() * 4.2f))));
        }

        @NotNull
        public final String getReadablePropertyName(@NotNull FloatPropertyCompat<?> floatPropertyCompat) {
            Intrinsics.checkNotNullParameter(floatPropertyCompat, "property");
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.TRANSLATION_X)) {
                return "translationX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.TRANSLATION_Y)) {
                return "translationY";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.TRANSLATION_Z)) {
                return "translationZ";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCALE_X)) {
                return "scaleX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCALE_Y)) {
                return "scaleY";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ROTATION)) {
                return "rotation";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ROTATION_X)) {
                return "rotationX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ROTATION_Y)) {
                return "rotationY";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCROLL_X)) {
                return "scrollX";
            }
            if (Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.SCROLL_Y)) {
                return "scrollY";
            }
            return Intrinsics.areEqual((Object) floatPropertyCompat, (Object) DynamicAnimation.ALPHA) ? "alpha" : "Custom FloatPropertyCompat instance";
        }
    }

    public final boolean arePropertiesAnimating(@NotNull Set<? extends FloatPropertyCompat<? super T>> set) {
        Intrinsics.checkNotNullParameter(set, "properties");
        if ((set instanceof Collection) && set.isEmpty()) {
            return false;
        }
        for (FloatPropertyCompat isPropertyAnimating : set) {
            if (isPropertyAnimating(isPropertyAnimating)) {
                return true;
            }
        }
        return false;
    }
}
