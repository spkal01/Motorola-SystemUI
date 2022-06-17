package com.android.p011wm.shell.bubbles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.TaskView;
import com.android.p011wm.shell.animation.Interpolators;
import com.android.p011wm.shell.animation.PhysicsAnimator;
import com.android.p011wm.shell.bubbles.BadgedImageView;
import com.android.p011wm.shell.bubbles.Bubble;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.p011wm.shell.bubbles.animation.AnimatableScaleMatrix;
import com.android.p011wm.shell.bubbles.animation.ExpandedAnimationController;
import com.android.p011wm.shell.bubbles.animation.PhysicsAnimationLayout;
import com.android.p011wm.shell.bubbles.animation.StackAnimationController;
import com.android.p011wm.shell.common.FloatingContentCoordinator;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.magnetictarget.MagnetizedObject;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/* renamed from: com.android.wm.shell.bubbles.BubbleStackView */
public class BubbleStackView extends FrameLayout implements ViewTreeObserver.OnComputeInternalInsetsListener {
    private static final SurfaceSynchronizer DEFAULT_SURFACE_SYNCHRONIZER = new SurfaceSynchronizer() {
        public void syncSurfaceAndRun(final Runnable runnable) {
            Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
                private int mFrameWait = 2;

                public void doFrame(long j) {
                    int i = this.mFrameWait - 1;
                    this.mFrameWait = i;
                    if (i > 0) {
                        Choreographer.getInstance().postFrameCallback(this);
                    } else {
                        runnable.run();
                    }
                }
            });
        }
    };
    @VisibleForTesting
    static final int FLYOUT_HIDE_AFTER = 5000;
    private static final PhysicsAnimator.SpringConfig FLYOUT_IME_ANIMATION_SPRING_CONFIG = new PhysicsAnimator.SpringConfig(200.0f, 0.9f);
    private Runnable mAfterFlyoutHidden;
    private final DynamicAnimation.OnAnimationEndListener mAfterFlyoutTransitionSpring;
    private Runnable mAnimateInFlyout;
    private final Runnable mAnimateTemporarilyInvisibleImmediate;
    private SurfaceControl.ScreenshotHardwareBuffer mAnimatingOutBubbleBuffer;
    private final ValueAnimator mAnimatingOutSurfaceAlphaAnimator;
    private FrameLayout mAnimatingOutSurfaceContainer;
    /* access modifiers changed from: private */
    public boolean mAnimatingOutSurfaceReady;
    private SurfaceView mAnimatingOutSurfaceView;
    private View.OnClickListener mBubbleClickListener;
    /* access modifiers changed from: private */
    public PhysicsAnimationLayout mBubbleContainer;
    private final BubbleController mBubbleController;
    /* access modifiers changed from: private */
    public final BubbleData mBubbleData;
    private int mBubbleElevation;
    private BubbleOverflow mBubbleOverflow;
    private int mBubbleSize;
    /* access modifiers changed from: private */
    public BubbleViewProvider mBubbleToExpandAfterFlyoutCollapse;
    private RelativeTouchListener mBubbleTouchListener;
    private int mBubbleTouchPadding;
    /* access modifiers changed from: private */
    public int mCornerRadius;
    private Runnable mDelayedAnimation;
    private final ShellExecutor mDelayedAnimationExecutor;
    private final ValueAnimator mDismissBubbleAnimator;
    /* access modifiers changed from: private */
    public DismissView mDismissView;
    private Bubbles.BubbleExpandListener mExpandListener;
    /* access modifiers changed from: private */
    public ExpandedAnimationController mExpandedAnimationController;
    /* access modifiers changed from: private */
    public BubbleViewProvider mExpandedBubble;
    private final ValueAnimator mExpandedViewAlphaAnimator;
    private FrameLayout mExpandedViewContainer;
    private final AnimatableScaleMatrix mExpandedViewContainerMatrix = new AnimatableScaleMatrix();
    private int mExpandedViewPadding;
    /* access modifiers changed from: private */
    public boolean mExpandedViewTemporarilyHidden;
    /* access modifiers changed from: private */
    public BubbleFlyoutView mFlyout;
    private View.OnClickListener mFlyoutClickListener;
    private final FloatPropertyCompat mFlyoutCollapseProperty;
    /* access modifiers changed from: private */
    public float mFlyoutDragDeltaX;
    private RelativeTouchListener mFlyoutTouchListener;
    private final SpringAnimation mFlyoutTransitionSpring;
    /* access modifiers changed from: private */
    public Runnable mHideFlyout;
    private int mImeOffset;
    /* access modifiers changed from: private */
    public final MagnetizedObject.MagnetListener mIndividualBubbleMagnetListener;
    /* access modifiers changed from: private */
    public boolean mIsBubbleSwitchAnimating;
    /* access modifiers changed from: private */
    public boolean mIsDraggingStack;
    /* access modifiers changed from: private */
    public boolean mIsExpanded;
    /* access modifiers changed from: private */
    public boolean mIsExpansionAnimating;
    private boolean mIsGestureInProgress;
    /* access modifiers changed from: private */
    public MagnetizedObject.MagneticTarget mMagneticTarget;
    /* access modifiers changed from: private */
    public MagnetizedObject<?> mMagnetizedObject;
    /* access modifiers changed from: private */
    public ManageEducationView mManageEduView;
    private ViewGroup mManageMenu;
    private ImageView mManageSettingsIcon;
    private TextView mManageSettingsText;
    private PhysicsAnimator.SpringConfig mManageSpringConfig;
    private View.OnLayoutChangeListener mOrientationChangedListener;
    private int mPointerIndexDown;
    /* access modifiers changed from: private */
    public BubblePositioner mPositioner;
    private RelativeStackPosition mRelativeStackPositionBeforeRotation;
    private final PhysicsAnimator.SpringConfig mScaleInSpringConfig = new PhysicsAnimator.SpringConfig(300.0f, 0.9f);
    private final PhysicsAnimator.SpringConfig mScaleOutSpringConfig = new PhysicsAnimator.SpringConfig(900.0f, 1.0f);
    /* access modifiers changed from: private */
    public boolean mShowingManage;
    /* access modifiers changed from: private */
    public StackAnimationController mStackAnimationController;
    /* access modifiers changed from: private */
    public StackEducationView mStackEduView;
    /* access modifiers changed from: private */
    public final MagnetizedObject.MagnetListener mStackMagnetListener;
    /* access modifiers changed from: private */
    public boolean mStackOnLeftOrWillBe;
    private final SurfaceSynchronizer mSurfaceSynchronizer;
    private ViewTreeObserver.OnDrawListener mSystemGestureExcludeUpdater;
    private final List<Rect> mSystemGestureExclusionRects;
    private View mTaskbarScrim;
    private Rect mTempRect;
    private boolean mTemporarilyInvisible;
    private final PhysicsAnimator.SpringConfig mTranslateSpringConfig = new PhysicsAnimator.SpringConfig(50.0f, 1.0f);
    private Consumer<String> mUnbubbleConversationCallback;
    private View mViewBeingDismissed;
    /* access modifiers changed from: private */
    public boolean mViewUpdatedRequested;
    /* access modifiers changed from: private */
    public ViewTreeObserver.OnPreDrawListener mViewUpdater;

    /* renamed from: com.android.wm.shell.bubbles.BubbleStackView$SurfaceSynchronizer */
    public interface SurfaceSynchronizer {
        void syncSurfaceAndRun(Runnable runnable);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        animateFlyoutCollapsed(true, 0.0f);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("Stack view state:");
        String formatBubblesString = BubbleDebugConfig.formatBubblesString(getBubblesOnScreen(), getExpandedBubble());
        printWriter.print("  bubbles on screen:       ");
        printWriter.println(formatBubblesString);
        printWriter.print("  gestureInProgress:       ");
        printWriter.println(this.mIsGestureInProgress);
        printWriter.print("  showingDismiss:          ");
        printWriter.println(this.mDismissView.isShowing());
        printWriter.print("  isExpansionAnimating:    ");
        printWriter.println(this.mIsExpansionAnimating);
        printWriter.print("  expandedContainerVis:    ");
        printWriter.println(this.mExpandedViewContainer.getVisibility());
        printWriter.print("  expandedContainerAlpha:  ");
        printWriter.println(this.mExpandedViewContainer.getAlpha());
        printWriter.print("  expandedContainerMatrix: ");
        printWriter.println(this.mExpandedViewContainer.getAnimationMatrix());
        this.mStackAnimationController.dump(fileDescriptor, printWriter, strArr);
        this.mExpandedAnimationController.dump(fileDescriptor, printWriter, strArr);
        if (this.mExpandedBubble != null) {
            printWriter.println("Expanded bubble state:");
            printWriter.println("  expandedBubbleKey: " + this.mExpandedBubble.getKey());
            BubbleExpandedView expandedView = this.mExpandedBubble.getExpandedView();
            if (expandedView != null) {
                printWriter.println("  expandedViewVis:    " + expandedView.getVisibility());
                printWriter.println("  expandedViewAlpha:  " + expandedView.getAlpha());
                printWriter.println("  expandedViewTaskId: " + expandedView.getTaskId());
                TaskView taskView = expandedView.getTaskView();
                if (taskView != null) {
                    printWriter.println("  activityViewVis:    " + taskView.getVisibility());
                    printWriter.println("  activityViewAlpha:  " + taskView.getAlpha());
                    return;
                }
                printWriter.println("  activityView is null");
                return;
            }
            printWriter.println("Expanded bubble view state: expanded bubble view is null");
            return;
        }
        printWriter.println("Expanded bubble state: expanded bubble is null");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (this.mFlyoutDragDeltaX == 0.0f) {
            this.mFlyout.postDelayed(this.mHideFlyout, 5000);
        } else {
            this.mFlyout.hideFlyout();
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    @SuppressLint({"ClickableViewAccessibility"})
    public BubbleStackView(Context context, BubbleController bubbleController, BubbleData bubbleData, SurfaceSynchronizer surfaceSynchronizer, FloatingContentCoordinator floatingContentCoordinator, ShellExecutor shellExecutor) {
        super(context);
        SurfaceSynchronizer surfaceSynchronizer2;
        Context context2 = context;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mAnimatingOutSurfaceAlphaAnimator = ofFloat;
        this.mHideFlyout = new BubbleStackView$$ExternalSyntheticLambda18(this);
        this.mBubbleToExpandAfterFlyoutCollapse = null;
        this.mStackOnLeftOrWillBe = true;
        this.mIsGestureInProgress = false;
        this.mTemporarilyInvisible = false;
        this.mIsDraggingStack = false;
        this.mExpandedViewTemporarilyHidden = false;
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mExpandedViewAlphaAnimator = ofFloat2;
        this.mPointerIndexDown = -1;
        this.mViewUpdatedRequested = false;
        this.mIsExpansionAnimating = false;
        this.mIsBubbleSwitchAnimating = false;
        this.mTempRect = new Rect();
        this.mSystemGestureExclusionRects = Collections.singletonList(new Rect());
        this.mViewUpdater = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                BubbleStackView.this.getViewTreeObserver().removeOnPreDrawListener(BubbleStackView.this.mViewUpdater);
                BubbleStackView.this.updateExpandedView();
                boolean unused = BubbleStackView.this.mViewUpdatedRequested = false;
                return true;
            }
        };
        this.mSystemGestureExcludeUpdater = new BubbleStackView$$ExternalSyntheticLambda10(this);
        C22543 r8 = new FloatPropertyCompat("FlyoutCollapseSpring") {
            public float getValue(Object obj) {
                return BubbleStackView.this.mFlyoutDragDeltaX;
            }

            public void setValue(Object obj, float f) {
                BubbleStackView.this.setFlyoutStateForDragLength(f);
            }
        };
        this.mFlyoutCollapseProperty = r8;
        SpringAnimation springAnimation = new SpringAnimation(this, r8);
        this.mFlyoutTransitionSpring = springAnimation;
        this.mFlyoutDragDeltaX = 0.0f;
        BubbleStackView$$ExternalSyntheticLambda11 bubbleStackView$$ExternalSyntheticLambda11 = new BubbleStackView$$ExternalSyntheticLambda11(this);
        this.mAfterFlyoutTransitionSpring = bubbleStackView$$ExternalSyntheticLambda11;
        this.mIndividualBubbleMagnetListener = new MagnetizedObject.MagnetListener() {
            public void onStuckToTarget(MagnetizedObject.MagneticTarget magneticTarget) {
                if (BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble() != null) {
                    BubbleStackView bubbleStackView = BubbleStackView.this;
                    bubbleStackView.animateDismissBubble(bubbleStackView.mExpandedAnimationController.getDraggedOutBubble(), true);
                }
            }

            public void onUnstuckFromTarget(MagnetizedObject.MagneticTarget magneticTarget, float f, float f2, boolean z) {
                if (BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble() != null) {
                    BubbleStackView bubbleStackView = BubbleStackView.this;
                    bubbleStackView.animateDismissBubble(bubbleStackView.mExpandedAnimationController.getDraggedOutBubble(), false);
                    if (z) {
                        BubbleStackView.this.mExpandedAnimationController.snapBubbleBack(BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble(), f, f2);
                        BubbleStackView.this.mDismissView.hide();
                        return;
                    }
                    BubbleStackView.this.mExpandedAnimationController.onUnstuckFromTarget();
                }
            }

            public void onReleasedInTarget(MagnetizedObject.MagneticTarget magneticTarget) {
                if (BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble() != null) {
                    BubbleStackView.this.mExpandedAnimationController.dismissDraggedOutBubble(BubbleStackView.this.mExpandedAnimationController.getDraggedOutBubble(), (float) BubbleStackView.this.mDismissView.getHeight(), new BubbleStackView$4$$ExternalSyntheticLambda0(BubbleStackView.this));
                    BubbleStackView.this.mDismissView.hide();
                }
            }
        };
        this.mStackMagnetListener = new MagnetizedObject.MagnetListener() {
            public void onStuckToTarget(MagnetizedObject.MagneticTarget magneticTarget) {
                BubbleStackView bubbleStackView = BubbleStackView.this;
                bubbleStackView.animateDismissBubble(bubbleStackView.mBubbleContainer, true);
            }

            public void onUnstuckFromTarget(MagnetizedObject.MagneticTarget magneticTarget, float f, float f2, boolean z) {
                BubbleStackView bubbleStackView = BubbleStackView.this;
                bubbleStackView.animateDismissBubble(bubbleStackView.mBubbleContainer, false);
                if (z) {
                    BubbleStackView.this.mStackAnimationController.flingStackThenSpringToEdge(BubbleStackView.this.mStackAnimationController.getStackPosition().x, f, f2);
                    BubbleStackView.this.mDismissView.hide();
                    return;
                }
                BubbleStackView.this.mStackAnimationController.onUnstuckFromTarget();
            }

            public void onReleasedInTarget(MagnetizedObject.MagneticTarget magneticTarget) {
                BubbleStackView.this.mStackAnimationController.animateStackDismissal((float) BubbleStackView.this.mDismissView.getHeight(), new BubbleStackView$5$$ExternalSyntheticLambda0(this));
                BubbleStackView.this.mDismissView.hide();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onReleasedInTarget$0() {
                BubbleStackView.this.resetDismissAnimator();
                BubbleStackView.this.dismissMagnetizedObject();
            }
        };
        this.mBubbleClickListener = new View.OnClickListener() {
            public void onClick(View view) {
                Bubble bubbleWithView;
                boolean unused = BubbleStackView.this.mIsDraggingStack = false;
                if (!BubbleStackView.this.mIsExpansionAnimating && !BubbleStackView.this.mIsBubbleSwitchAnimating && (bubbleWithView = BubbleStackView.this.mBubbleData.getBubbleWithView(view)) != null) {
                    boolean equals = bubbleWithView.getKey().equals(BubbleStackView.this.mExpandedBubble.getKey());
                    if (BubbleStackView.this.isExpanded()) {
                        BubbleStackView.this.mExpandedAnimationController.onGestureFinished();
                    }
                    if (!BubbleStackView.this.isExpanded() || equals) {
                        if (!BubbleStackView.this.maybeShowStackEdu()) {
                            BubbleStackView.this.mBubbleData.setExpanded(!BubbleStackView.this.mBubbleData.isExpanded());
                        }
                    } else if (bubbleWithView != BubbleStackView.this.mBubbleData.getSelectedBubble()) {
                        BubbleStackView.this.mBubbleData.setSelectedBubble(bubbleWithView);
                    } else {
                        BubbleStackView.this.setSelectedBubble(bubbleWithView);
                    }
                }
            }
        };
        this.mBubbleTouchListener = new RelativeTouchListener() {
            public boolean onDown(View view, MotionEvent motionEvent) {
                if (BubbleStackView.this.mIsExpansionAnimating) {
                    return true;
                }
                if (BubbleStackView.this.mShowingManage) {
                    BubbleStackView.this.showManageMenu(false);
                }
                if (BubbleStackView.this.mBubbleData.isExpanded()) {
                    if (BubbleStackView.this.mManageEduView != null) {
                        BubbleStackView.this.mManageEduView.hide(false);
                    }
                    BubbleStackView.this.mExpandedAnimationController.prepareForBubbleDrag(view, BubbleStackView.this.mMagneticTarget, BubbleStackView.this.mIndividualBubbleMagnetListener);
                    BubbleStackView.this.hideCurrentInputMethod();
                    BubbleStackView bubbleStackView = BubbleStackView.this;
                    MagnetizedObject unused = bubbleStackView.mMagnetizedObject = bubbleStackView.mExpandedAnimationController.getMagnetizedBubbleDraggingOut();
                } else {
                    BubbleStackView.this.mStackAnimationController.cancelStackPositionAnimations();
                    BubbleStackView.this.mBubbleContainer.setActiveController(BubbleStackView.this.mStackAnimationController);
                    BubbleStackView.this.hideFlyoutImmediate();
                    if (BubbleStackView.this.mPositioner.showingInTaskbar()) {
                        MagnetizedObject unused2 = BubbleStackView.this.mMagnetizedObject = null;
                    } else {
                        BubbleStackView bubbleStackView2 = BubbleStackView.this;
                        MagnetizedObject unused3 = bubbleStackView2.mMagnetizedObject = bubbleStackView2.mStackAnimationController.getMagnetizedStack();
                        BubbleStackView.this.mMagnetizedObject.clearAllTargets();
                        BubbleStackView.this.mMagnetizedObject.addTarget(BubbleStackView.this.mMagneticTarget);
                        BubbleStackView.this.mMagnetizedObject.setMagnetListener(BubbleStackView.this.mStackMagnetListener);
                    }
                    boolean unused4 = BubbleStackView.this.mIsDraggingStack = true;
                    BubbleStackView.this.updateTemporarilyInvisibleAnimation(false);
                }
                boolean unused5 = BubbleStackView.this.passEventToMagnetizedObject(motionEvent);
                return true;
            }

            public void onMove(View view, MotionEvent motionEvent, float f, float f2, float f3, float f4) {
                if (BubbleStackView.this.mIsExpansionAnimating) {
                    return;
                }
                if (!BubbleStackView.this.mPositioner.showingInTaskbar() || BubbleStackView.this.mIsExpanded) {
                    BubbleStackView.this.mDismissView.show();
                    if (BubbleStackView.this.mIsExpanded && BubbleStackView.this.mExpandedBubble != null && view.equals(BubbleStackView.this.mExpandedBubble.getIconView())) {
                        BubbleStackView.this.hideExpandedViewIfNeeded();
                    }
                    if (!BubbleStackView.this.passEventToMagnetizedObject(motionEvent)) {
                        BubbleStackView.this.updateBubbleShadows(true);
                        if (BubbleStackView.this.mBubbleData.isExpanded() || BubbleStackView.this.mPositioner.showingInTaskbar()) {
                            BubbleStackView.this.mExpandedAnimationController.dragBubbleOut(view, f + f3, f2 + f4);
                            return;
                        }
                        if (BubbleStackView.this.mStackEduView != null) {
                            BubbleStackView.this.mStackEduView.hide(false);
                        }
                        BubbleStackView.this.mStackAnimationController.moveStackFromTouch(f + f3, f2 + f4);
                    }
                }
            }

            public void onUp(View view, MotionEvent motionEvent, float f, float f2, float f3, float f4, float f5, float f6) {
                if (BubbleStackView.this.mIsExpansionAnimating) {
                    return;
                }
                if (!BubbleStackView.this.mPositioner.showingInTaskbar() || BubbleStackView.this.mIsExpanded) {
                    if (!BubbleStackView.this.passEventToMagnetizedObject(motionEvent)) {
                        if (BubbleStackView.this.mBubbleData.isExpanded()) {
                            BubbleStackView.this.mExpandedAnimationController.snapBubbleBack(view, f5, f6);
                            BubbleStackView.this.showExpandedViewIfNeeded();
                        } else {
                            boolean access$3300 = BubbleStackView.this.mStackOnLeftOrWillBe;
                            BubbleStackView bubbleStackView = BubbleStackView.this;
                            int i = (bubbleStackView.mStackAnimationController.flingStackThenSpringToEdge(f + f3, f5, f6) > 0.0f ? 1 : (bubbleStackView.mStackAnimationController.flingStackThenSpringToEdge(f + f3, f5, f6) == 0.0f ? 0 : -1));
                            boolean z = true;
                            boolean unused = bubbleStackView.mStackOnLeftOrWillBe = i <= 0;
                            if (access$3300 == BubbleStackView.this.mStackOnLeftOrWillBe) {
                                z = false;
                            }
                            BubbleStackView.this.updateBadges(z);
                            BubbleStackView.this.logBubbleEvent((BubbleViewProvider) null, 7);
                        }
                        BubbleStackView.this.mDismissView.hide();
                    }
                    boolean unused2 = BubbleStackView.this.mIsDraggingStack = false;
                    BubbleStackView.this.updateTemporarilyInvisibleAnimation(false);
                }
            }
        };
        this.mFlyoutClickListener = new View.OnClickListener() {
            public void onClick(View view) {
                if (BubbleStackView.this.maybeShowStackEdu()) {
                    BubbleViewProvider unused = BubbleStackView.this.mBubbleToExpandAfterFlyoutCollapse = null;
                } else {
                    BubbleStackView bubbleStackView = BubbleStackView.this;
                    BubbleViewProvider unused2 = bubbleStackView.mBubbleToExpandAfterFlyoutCollapse = bubbleStackView.mBubbleData.getSelectedBubble();
                }
                BubbleStackView.this.mFlyout.removeCallbacks(BubbleStackView.this.mHideFlyout);
                BubbleStackView.this.mHideFlyout.run();
            }
        };
        this.mFlyoutTouchListener = new RelativeTouchListener() {
            public boolean onDown(View view, MotionEvent motionEvent) {
                BubbleStackView.this.mFlyout.removeCallbacks(BubbleStackView.this.mHideFlyout);
                return true;
            }

            public void onMove(View view, MotionEvent motionEvent, float f, float f2, float f3, float f4) {
                BubbleStackView.this.setFlyoutStateForDragLength(f3);
            }

            public void onUp(View view, MotionEvent motionEvent, float f, float f2, float f3, float f4, float f5, float f6) {
                boolean isStackOnLeftSide = BubbleStackView.this.mStackAnimationController.isStackOnLeftSide();
                boolean z = true;
                boolean z2 = !isStackOnLeftSide ? f5 > 2000.0f : f5 < -2000.0f;
                boolean z3 = !isStackOnLeftSide ? f3 > ((float) BubbleStackView.this.mFlyout.getWidth()) * 0.25f : f3 < ((float) (-BubbleStackView.this.mFlyout.getWidth())) * 0.25f;
                boolean z4 = !isStackOnLeftSide ? f5 < 0.0f : f5 > 0.0f;
                if (!z2 && (!z3 || z4)) {
                    z = false;
                }
                BubbleStackView.this.mFlyout.removeCallbacks(BubbleStackView.this.mHideFlyout);
                BubbleStackView.this.animateFlyoutCollapsed(z, f5);
                boolean unused = BubbleStackView.this.maybeShowStackEdu();
            }
        };
        this.mShowingManage = false;
        this.mManageSpringConfig = new PhysicsAnimator.SpringConfig(1500.0f, 0.75f);
        this.mAnimateTemporarilyInvisibleImmediate = new BubbleStackView$$ExternalSyntheticLambda26(this);
        this.mDelayedAnimationExecutor = shellExecutor;
        this.mBubbleController = bubbleController;
        this.mBubbleData = bubbleData;
        Resources resources = getResources();
        this.mBubbleSize = resources.getDimensionPixelSize(C2219R.dimen.bubble_size);
        int i = C2219R.dimen.bubble_elevation;
        this.mBubbleElevation = resources.getDimensionPixelSize(i);
        this.mBubbleTouchPadding = resources.getDimensionPixelSize(C2219R.dimen.bubble_touch_padding);
        this.mImeOffset = resources.getDimensionPixelSize(C2219R.dimen.pip_ime_offset);
        this.mExpandedViewPadding = resources.getDimensionPixelSize(C2219R.dimen.bubble_expanded_view_padding);
        int dimensionPixelSize = resources.getDimensionPixelSize(i);
        this.mPositioner = bubbleController.getPositioner();
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{16844145});
        this.mCornerRadius = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        obtainStyledAttributes.recycle();
        BubbleStackView$$ExternalSyntheticLambda31 bubbleStackView$$ExternalSyntheticLambda31 = new BubbleStackView$$ExternalSyntheticLambda31(this);
        this.mStackAnimationController = new StackAnimationController(floatingContentCoordinator, new BubbleStackView$$ExternalSyntheticLambda46(this), bubbleStackView$$ExternalSyntheticLambda31, new BubbleStackView$$ExternalSyntheticLambda28(this), this.mPositioner);
        this.mExpandedAnimationController = new ExpandedAnimationController(this.mPositioner, this.mExpandedViewPadding, bubbleStackView$$ExternalSyntheticLambda31);
        if (surfaceSynchronizer != null) {
            surfaceSynchronizer2 = surfaceSynchronizer;
        } else {
            surfaceSynchronizer2 = DEFAULT_SURFACE_SYNCHRONIZER;
        }
        this.mSurfaceSynchronizer = surfaceSynchronizer2;
        setLayoutDirection(0);
        PhysicsAnimationLayout physicsAnimationLayout = new PhysicsAnimationLayout(context2);
        this.mBubbleContainer = physicsAnimationLayout;
        physicsAnimationLayout.setActiveController(this.mStackAnimationController);
        float f = (float) dimensionPixelSize;
        this.mBubbleContainer.setElevation(f);
        this.mBubbleContainer.setClipChildren(false);
        addView(this.mBubbleContainer, new FrameLayout.LayoutParams(-1, -1));
        updateUserEdu();
        FrameLayout frameLayout = new FrameLayout(context2);
        this.mExpandedViewContainer = frameLayout;
        frameLayout.setElevation(f);
        this.mExpandedViewContainer.setClipChildren(false);
        addView(this.mExpandedViewContainer);
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        this.mAnimatingOutSurfaceContainer = frameLayout2;
        frameLayout2.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        addView(this.mAnimatingOutSurfaceContainer);
        SurfaceView surfaceView = new SurfaceView(getContext());
        this.mAnimatingOutSurfaceView = surfaceView;
        surfaceView.setUseAlpha();
        this.mAnimatingOutSurfaceView.setZOrderOnTop(true);
        this.mAnimatingOutSurfaceView.setCornerRadius((float) this.mCornerRadius);
        this.mAnimatingOutSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        this.mAnimatingOutSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            }

            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                boolean unused = BubbleStackView.this.mAnimatingOutSurfaceReady = true;
            }

            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                boolean unused = BubbleStackView.this.mAnimatingOutSurfaceReady = false;
            }
        });
        this.mAnimatingOutSurfaceContainer.addView(this.mAnimatingOutSurfaceView);
        this.mAnimatingOutSurfaceContainer.setPadding(this.mExpandedViewContainer.getPaddingLeft(), this.mExpandedViewContainer.getPaddingTop(), this.mExpandedViewContainer.getPaddingRight(), this.mExpandedViewContainer.getPaddingBottom());
        setUpManageMenu();
        setUpFlyout();
        springAnimation.setSpring(new SpringForce().setStiffness(200.0f).setDampingRatio(0.75f));
        springAnimation.addEndListener(bubbleStackView$$ExternalSyntheticLambda11);
        setUpDismissView();
        setClipChildren(false);
        setFocusable(true);
        this.mBubbleContainer.bringToFront();
        BubbleOverflow overflow = bubbleData.getOverflow();
        this.mBubbleOverflow = overflow;
        this.mBubbleContainer.addView(overflow.getIconView(), this.mBubbleContainer.getChildCount(), new FrameLayout.LayoutParams(this.mPositioner.getBubbleSize(), this.mPositioner.getBubbleSize()));
        updateOverflow();
        this.mBubbleOverflow.getIconView().setOnClickListener(new BubbleStackView$$ExternalSyntheticLambda8(this));
        View view = new View(getContext());
        this.mTaskbarScrim = view;
        view.setBackgroundColor(-16777216);
        addView(this.mTaskbarScrim);
        this.mTaskbarScrim.setAlpha(0.0f);
        this.mTaskbarScrim.setVisibility(8);
        this.mOrientationChangedListener = new BubbleStackView$$ExternalSyntheticLambda9(this);
        float dimensionPixelSize2 = ((float) getResources().getDimensionPixelSize(C2219R.dimen.dismiss_circle_small)) / ((float) getResources().getDimensionPixelSize(C2219R.dimen.dismiss_circle_size));
        ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.mDismissBubbleAnimator = ofFloat3;
        ofFloat3.addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda2(this, dimensionPixelSize2));
        setOnClickListener(new BubbleStackView$$ExternalSyntheticLambda3(this));
        ViewPropertyAnimator animate = animate();
        Interpolator interpolator = Interpolators.PANEL_CLOSE_ACCELERATED;
        animate.setInterpolator(interpolator).setDuration(320);
        ofFloat2.setDuration(150);
        ofFloat2.setInterpolator(interpolator);
        ofFloat2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (BubbleStackView.this.mExpandedBubble != null && BubbleStackView.this.mExpandedBubble.getExpandedView() != null) {
                    BubbleStackView.this.mExpandedBubble.getExpandedView().setSurfaceZOrderedOnTop(true);
                    BubbleStackView.this.mExpandedBubble.getExpandedView().setAlphaAnimating(true);
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (BubbleStackView.this.mExpandedBubble != null && BubbleStackView.this.mExpandedBubble.getExpandedView() != null && !BubbleStackView.this.mExpandedViewTemporarilyHidden) {
                    BubbleStackView.this.mExpandedBubble.getExpandedView().setSurfaceZOrderedOnTop(false);
                    BubbleStackView.this.mExpandedBubble.getExpandedView().setAlphaAnimating(false);
                }
            }
        });
        ofFloat2.addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda0(this));
        ofFloat.setDuration(150);
        ofFloat.setInterpolator(interpolator);
        ofFloat.addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda1(this));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                BubbleStackView.this.releaseAnimatingOutBubbleBuffer();
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        if (getBubbleCount() == 0 && !this.mBubbleData.isShowingOverflow()) {
            this.mBubbleController.onAllBubblesAnimatedOut();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        this.mBubbleData.setShowingOverflow(true);
        this.mBubbleData.setSelectedBubble(this.mBubbleOverflow);
        this.mBubbleData.setExpanded(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mPositioner.update();
        onDisplaySizeChanged();
        this.mExpandedAnimationController.updateResources();
        this.mStackAnimationController.updateResources();
        this.mBubbleOverflow.updateResources();
        RelativeStackPosition relativeStackPosition = this.mRelativeStackPositionBeforeRotation;
        if (relativeStackPosition != null) {
            this.mStackAnimationController.setStackPosition(relativeStackPosition);
            this.mRelativeStackPositionBeforeRotation = null;
        }
        if (this.mIsExpanded) {
            beforeExpandedViewAnimation();
            updateOverflowVisibility();
            updatePointerPosition();
            this.mExpandedAnimationController.expandFromStack(new BubbleStackView$$ExternalSyntheticLambda32(this));
            this.mExpandedViewContainer.setTranslationX(0.0f);
            this.mExpandedViewContainer.setTranslationY(this.mPositioner.getExpandedViewY());
            this.mExpandedViewContainer.setAlpha(1.0f);
        }
        removeOnLayoutChangeListener(this.mOrientationChangedListener);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(float f, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        DismissView dismissView = this.mDismissView;
        if (dismissView != null) {
            dismissView.setPivotX(((float) (dismissView.getRight() - this.mDismissView.getLeft())) / 2.0f);
            DismissView dismissView2 = this.mDismissView;
            dismissView2.setPivotY(((float) (dismissView2.getBottom() - this.mDismissView.getTop())) / 2.0f);
            float max = Math.max(floatValue, f);
            this.mDismissView.getCircle().setScaleX(max);
            this.mDismissView.getCircle().setScaleY(max);
        }
        View view = this.mViewBeingDismissed;
        if (view != null) {
            view.setAlpha(Math.max(floatValue, 0.7f));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(View view) {
        if (this.mShowingManage) {
            showManageMenu(false);
            return;
        }
        StackEducationView stackEducationView = this.mStackEduView;
        if (stackEducationView != null && stackEducationView.getVisibility() == 0) {
            this.mStackEduView.hide(false);
        } else if (this.mBubbleData.isExpanded()) {
            this.mBubbleData.setExpanded(false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(ValueAnimator valueAnimator) {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null) {
            bubbleViewProvider.setExpandedContentAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(ValueAnimator valueAnimator) {
        if (!this.mExpandedViewTemporarilyHidden) {
            this.mAnimatingOutSurfaceView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    public void setTemporarilyInvisible(boolean z) {
        this.mTemporarilyInvisible = z;
        updateTemporarilyInvisibleAnimation(z);
    }

    /* access modifiers changed from: private */
    public void updateTemporarilyInvisibleAnimation(boolean z) {
        removeCallbacks(this.mAnimateTemporarilyInvisibleImmediate);
        if (!this.mIsDraggingStack) {
            postDelayed(this.mAnimateTemporarilyInvisibleImmediate, (!(this.mTemporarilyInvisible && this.mFlyout.getVisibility() != 0) || z) ? 0 : 1000);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10() {
        if (!this.mTemporarilyInvisible || this.mFlyout.getVisibility() == 0) {
            animate().translationX(0.0f).start();
        } else if (this.mStackAnimationController.isStackOnLeftSide()) {
            animate().translationX((float) (-this.mBubbleSize)).start();
        } else {
            animate().translationX((float) this.mBubbleSize).start();
        }
    }

    private void setUpDismissView() {
        DismissView dismissView = this.mDismissView;
        if (dismissView != null) {
            removeView(dismissView);
        }
        this.mDismissView = new DismissView(getContext());
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2219R.dimen.bubble_elevation);
        addView(this.mDismissView);
        this.mDismissView.setElevation((float) dimensionPixelSize);
        this.mMagneticTarget = new MagnetizedObject.MagneticTarget(this.mDismissView.getCircle(), Settings.Secure.getInt(getContext().getContentResolver(), "bubble_dismiss_radius", this.mBubbleSize * 2));
        this.mBubbleContainer.bringToFront();
    }

    private void setUpManageMenu() {
        ViewGroup viewGroup = this.mManageMenu;
        if (viewGroup != null) {
            removeView(viewGroup);
        }
        ViewGroup viewGroup2 = (ViewGroup) LayoutInflater.from(getContext()).inflate(C2219R.layout.bubble_manage_menu, this, false);
        this.mManageMenu = viewGroup2;
        viewGroup2.setVisibility(4);
        PhysicsAnimator.getInstance(this.mManageMenu).setDefaultSpringConfig(this.mManageSpringConfig);
        this.mManageMenu.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) BubbleStackView.this.mCornerRadius);
            }
        });
        this.mManageMenu.setClipToOutline(true);
        this.mManageMenu.findViewById(C2219R.C2222id.bubble_manage_menu_dismiss_container).setOnClickListener(new BubbleStackView$$ExternalSyntheticLambda7(this));
        this.mManageMenu.findViewById(C2219R.C2222id.bubble_manage_menu_dont_bubble_container).setOnClickListener(new BubbleStackView$$ExternalSyntheticLambda5(this));
        this.mManageMenu.findViewById(C2219R.C2222id.bubble_manage_menu_settings_container).setOnClickListener(new BubbleStackView$$ExternalSyntheticLambda4(this));
        this.mManageSettingsIcon = (ImageView) this.mManageMenu.findViewById(C2219R.C2222id.bubble_manage_menu_settings_icon);
        this.mManageSettingsText = (TextView) this.mManageMenu.findViewById(C2219R.C2222id.bubble_manage_menu_settings_name);
        this.mManageMenu.setLayoutDirection(3);
        addView(this.mManageMenu);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setUpManageMenu$11(View view) {
        showManageMenu(false);
        dismissBubbleIfExists(this.mBubbleData.getSelectedBubble());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setUpManageMenu$12(View view) {
        showManageMenu(false);
        this.mUnbubbleConversationCallback.accept(this.mBubbleData.getSelectedBubble().getKey());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setUpManageMenu$13(View view) {
        showManageMenu(false);
        BubbleViewProvider selectedBubble = this.mBubbleData.getSelectedBubble();
        if (selectedBubble != null && this.mBubbleData.hasBubbleInStackWithKey(selectedBubble.getKey())) {
            Bubble bubble = (Bubble) selectedBubble;
            Intent settingsIntent = bubble.getSettingsIntent(this.mContext);
            this.mBubbleData.setExpanded(false);
            this.mContext.startActivityAsUser(settingsIntent, bubble.getUser());
            logBubbleEvent(selectedBubble, 9);
        }
    }

    private boolean shouldShowManageEdu() {
        return (!getPrefBoolean("HasSeenBubblesManageOnboarding") || BubbleDebugConfig.forceShowUserEducation(this.mContext)) && this.mExpandedBubble != null;
    }

    private void maybeShowManageEdu() {
        if (shouldShowManageEdu()) {
            if (this.mManageEduView == null) {
                ManageEducationView manageEducationView = new ManageEducationView(this.mContext);
                this.mManageEduView = manageEducationView;
                addView(manageEducationView);
            }
            this.mManageEduView.show(this.mExpandedBubble.getExpandedView(), this.mTempRect);
        }
    }

    private boolean shouldShowStackEdu() {
        return !getPrefBoolean("HasSeenBubblesOnboarding") || BubbleDebugConfig.forceShowUserEducation(this.mContext);
    }

    private boolean getPrefBoolean(String str) {
        Context context = this.mContext;
        return context.getSharedPreferences(context.getPackageName(), 0).getBoolean(str, false);
    }

    /* access modifiers changed from: private */
    public boolean maybeShowStackEdu() {
        if (!shouldShowStackEdu()) {
            return false;
        }
        if (this.mStackEduView == null) {
            StackEducationView stackEducationView = new StackEducationView(this.mContext);
            this.mStackEduView = stackEducationView;
            addView(stackEducationView);
        }
        this.mBubbleContainer.bringToFront();
        return this.mStackEduView.show(this.mPositioner.getDefaultStartPosition());
    }

    private void updateUserEdu() {
        maybeShowStackEdu();
        ManageEducationView manageEducationView = this.mManageEduView;
        if (manageEducationView != null) {
            manageEducationView.invalidate();
        }
        maybeShowManageEdu();
        StackEducationView stackEducationView = this.mStackEduView;
        if (stackEducationView != null) {
            stackEducationView.invalidate();
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void setUpFlyout() {
        BubbleFlyoutView bubbleFlyoutView = this.mFlyout;
        if (bubbleFlyoutView != null) {
            removeView(bubbleFlyoutView);
        }
        BubbleFlyoutView bubbleFlyoutView2 = new BubbleFlyoutView(getContext());
        this.mFlyout = bubbleFlyoutView2;
        bubbleFlyoutView2.setVisibility(8);
        this.mFlyout.setOnClickListener(this.mFlyoutClickListener);
        this.mFlyout.setOnTouchListener(this.mFlyoutTouchListener);
        addView(this.mFlyout, new FrameLayout.LayoutParams(-2, -2));
    }

    /* access modifiers changed from: package-private */
    public void updateFontScale() {
        setUpManageMenu();
        this.mFlyout.updateFontSize();
        for (Bubble next : this.mBubbleData.getBubbles()) {
            if (next.getExpandedView() != null) {
                next.getExpandedView().updateFontSize();
            }
        }
        BubbleOverflow bubbleOverflow = this.mBubbleOverflow;
        if (bubbleOverflow != null) {
            bubbleOverflow.getExpandedView().updateFontSize();
        }
    }

    private void updateOverflow() {
        this.mBubbleOverflow.update();
        this.mBubbleContainer.reorderView(this.mBubbleOverflow.getIconView(), this.mBubbleContainer.getChildCount() - 1);
        updateOverflowVisibility();
    }

    /* access modifiers changed from: package-private */
    public void updateOverflowButtonDot() {
        for (Bubble showDot : this.mBubbleData.getOverflowBubbles()) {
            if (showDot.showDot()) {
                this.mBubbleOverflow.setShowDot(true);
                return;
            }
        }
        this.mBubbleOverflow.setShowDot(false);
    }

    public void onThemeChanged() {
        setUpFlyout();
        setUpManageMenu();
        setUpDismissView();
        updateOverflow();
        updateUserEdu();
        updateExpandedViewTheme();
    }

    public void onOrientationChanged() {
        this.mRelativeStackPositionBeforeRotation = new RelativeStackPosition(this.mPositioner.getRestingPosition(), this.mStackAnimationController.getAllowableStackPositionRegion());
        this.mManageMenu.setVisibility(4);
        this.mShowingManage = false;
        addOnLayoutChangeListener(this.mOrientationChangedListener);
        hideFlyoutImmediate();
    }

    public void onLayoutDirectionChanged(int i) {
        this.mManageMenu.setLayoutDirection(i);
        this.mFlyout.setLayoutDirection(i);
        StackEducationView stackEducationView = this.mStackEduView;
        if (stackEducationView != null) {
            stackEducationView.setLayoutDirection(i);
        }
        ManageEducationView manageEducationView = this.mManageEduView;
        if (manageEducationView != null) {
            manageEducationView.setLayoutDirection(i);
        }
        updateExpandedViewDirection(i);
    }

    public void onDisplaySizeChanged() {
        updateOverflow();
        setUpManageMenu();
        setUpFlyout();
        setUpDismissView();
        this.mBubbleSize = this.mPositioner.getBubbleSize();
        for (Bubble next : this.mBubbleData.getBubbles()) {
            if (next.getIconView() == null) {
                Log.d("Bubbles", "Display size changed. Icon null: " + next);
            } else {
                BadgedImageView iconView = next.getIconView();
                int i = this.mBubbleSize;
                iconView.setLayoutParams(new FrameLayout.LayoutParams(i, i));
                if (next.getExpandedView() != null) {
                    next.getExpandedView().updateDimensions();
                }
            }
        }
        BadgedImageView iconView2 = this.mBubbleOverflow.getIconView();
        int i2 = this.mBubbleSize;
        iconView2.setLayoutParams(new FrameLayout.LayoutParams(i2, i2));
        this.mExpandedAnimationController.updateResources();
        this.mStackAnimationController.updateResources();
        this.mDismissView.updateResources();
        this.mMagneticTarget.setMagneticFieldRadiusPx(this.mBubbleSize * 2);
        this.mStackAnimationController.setStackPosition(new RelativeStackPosition(this.mPositioner.getRestingPosition(), this.mStackAnimationController.getAllowableStackPositionRegion()));
        if (this.mIsExpanded) {
            updateExpandedView();
        }
    }

    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        internalInsetsInfo.setTouchableInsets(3);
        this.mTempRect.setEmpty();
        getTouchableRegion(this.mTempRect);
        internalInsetsInfo.touchableRegion.set(this.mTempRect);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (!(bubbleViewProvider == null || bubbleViewProvider.getExpandedView() == null)) {
            this.mExpandedBubble.getExpandedView().onConfigChanged();
        }
        setUpManageMenu();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnComputeInternalInsetsListener(this);
        getViewTreeObserver().addOnDrawListener(this.mSystemGestureExcludeUpdater);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnPreDrawListener(this.mViewUpdater);
        getViewTreeObserver().removeOnDrawListener(this.mSystemGestureExcludeUpdater);
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
        BubbleOverflow bubbleOverflow = this.mBubbleOverflow;
        if (bubbleOverflow != null) {
            bubbleOverflow.cleanUpExpandedState();
        }
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        setupLocalMenu(accessibilityNodeInfo);
    }

    /* access modifiers changed from: package-private */
    public void updateExpandedViewTheme() {
        List<Bubble> bubbles = this.mBubbleData.getBubbles();
        if (!bubbles.isEmpty()) {
            bubbles.forEach(BubbleStackView$$ExternalSyntheticLambda44.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateExpandedViewTheme$14(Bubble bubble) {
        if (bubble.getExpandedView() != null) {
            bubble.getExpandedView().applyThemeAttrs();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateExpandedViewDirection(int i) {
        List<Bubble> bubbles = this.mBubbleData.getBubbles();
        if (!bubbles.isEmpty()) {
            bubbles.forEach(new BubbleStackView$$ExternalSyntheticLambda42(i));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateExpandedViewDirection$15(int i, Bubble bubble) {
        if (bubble.getExpandedView() != null) {
            bubble.getExpandedView().setLayoutDirection(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void setupLocalMenu(AccessibilityNodeInfo accessibilityNodeInfo) {
        Resources resources = this.mContext.getResources();
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(C2219R.C2222id.action_move_top_left, resources.getString(C2219R.string.bubble_accessibility_action_move_top_left)));
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(C2219R.C2222id.action_move_top_right, resources.getString(C2219R.string.bubble_accessibility_action_move_top_right)));
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(C2219R.C2222id.action_move_bottom_left, resources.getString(C2219R.string.bubble_accessibility_action_move_bottom_left)));
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(C2219R.C2222id.action_move_bottom_right, resources.getString(C2219R.string.bubble_accessibility_action_move_bottom_right)));
        accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
        if (this.mIsExpanded) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
        } else {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
        }
    }

    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        RectF allowableStackPositionRegion = this.mStackAnimationController.getAllowableStackPositionRegion();
        if (i == 1048576) {
            this.mBubbleData.dismissAll(6);
            announceForAccessibility(getResources().getString(C2219R.string.accessibility_bubble_dismissed));
            return true;
        } else if (i == 524288) {
            this.mBubbleData.setExpanded(false);
            return true;
        } else if (i == 262144) {
            this.mBubbleData.setExpanded(true);
            return true;
        } else if (i == C2219R.C2222id.action_move_top_left) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.left, allowableStackPositionRegion.top);
            return true;
        } else if (i == C2219R.C2222id.action_move_top_right) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.right, allowableStackPositionRegion.top);
            return true;
        } else if (i == C2219R.C2222id.action_move_bottom_left) {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.left, allowableStackPositionRegion.bottom);
            return true;
        } else if (i != C2219R.C2222id.action_move_bottom_right) {
            return false;
        } else {
            this.mStackAnimationController.springStackAfterFling(allowableStackPositionRegion.right, allowableStackPositionRegion.bottom);
            return true;
        }
    }

    public void updateContentDescription() {
        if (!this.mBubbleData.getBubbles().isEmpty()) {
            for (int i = 0; i < this.mBubbleData.getBubbles().size(); i++) {
                Bubble bubble = this.mBubbleData.getBubbles().get(i);
                String appName = bubble.getAppName();
                String title = bubble.getTitle();
                if (title == null) {
                    title = getResources().getString(C2219R.string.notification_bubble_title);
                }
                if (bubble.getIconView() != null) {
                    if (this.mIsExpanded || i > 0) {
                        bubble.getIconView().setContentDescription(getResources().getString(C2219R.string.bubble_content_description_single, new Object[]{title, appName}));
                    } else {
                        bubble.getIconView().setContentDescription(getResources().getString(C2219R.string.bubble_content_description_stack, new Object[]{title, appName, Integer.valueOf(this.mBubbleContainer.getChildCount() - 1)}));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSystemGestureExcludeRects() {
        Rect rect = this.mSystemGestureExclusionRects.get(0);
        if (getBubbleCount() > 0) {
            View childAt = this.mBubbleContainer.getChildAt(0);
            rect.set(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
            rect.offset((int) (childAt.getTranslationX() + 0.5f), (int) (childAt.getTranslationY() + 0.5f));
            this.mBubbleContainer.setSystemGestureExclusionRects(this.mSystemGestureExclusionRects);
            return;
        }
        rect.setEmpty();
        this.mBubbleContainer.setSystemGestureExclusionRects(Collections.emptyList());
    }

    public void setExpandListener(Bubbles.BubbleExpandListener bubbleExpandListener) {
        this.mExpandListener = bubbleExpandListener;
    }

    public void setUnbubbleConversationCallback(Consumer<String> consumer) {
        this.mUnbubbleConversationCallback = consumer;
    }

    public boolean isExpanded() {
        return this.mIsExpanded;
    }

    public boolean isExpansionAnimating() {
        return this.mIsExpansionAnimating;
    }

    @VisibleForTesting
    public BubbleViewProvider getExpandedBubble() {
        return this.mExpandedBubble;
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"ClickableViewAccessibility"})
    public void addBubble(Bubble bubble) {
        if (getBubbleCount() == 0 && shouldShowStackEdu()) {
            this.mStackAnimationController.setStackPosition(this.mPositioner.getDefaultStartPosition());
        }
        if (bubble.getIconView() != null) {
            this.mBubbleContainer.addView(bubble.getIconView(), 0, new FrameLayout.LayoutParams(this.mPositioner.getBubbleSize(), this.mPositioner.getBubbleSize()));
            if (getBubbleCount() == 0) {
                this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
            }
            bubble.getIconView().setDotBadgeOnLeft(!this.mStackOnLeftOrWillBe);
            bubble.getIconView().setOnClickListener(this.mBubbleClickListener);
            bubble.getIconView().setOnTouchListener(this.mBubbleTouchListener);
            updateBubbleShadows(false);
            animateInFlyoutForBubble(bubble);
            requestUpdate();
            logBubbleEvent(bubble, 1);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeBubble(Bubble bubble) {
        int i = 0;
        while (i < getBubbleCount()) {
            View childAt = this.mBubbleContainer.getChildAt(i);
            if (!(childAt instanceof BadgedImageView) || !((BadgedImageView) childAt).getKey().equals(bubble.getKey())) {
                i++;
            } else {
                this.mBubbleContainer.removeViewAt(i);
                if (this.mBubbleData.hasOverflowBubbleWithKey(bubble.getKey())) {
                    bubble.cleanupExpandedView();
                } else {
                    bubble.cleanupViews();
                }
                updatePointerPosition();
                logBubbleEvent(bubble, 5);
                return;
            }
        }
        Log.d("Bubbles", "was asked to remove Bubble, but didn't find the view! " + bubble);
    }

    private void updateOverflowVisibility() {
        this.mBubbleOverflow.setVisible((this.mIsExpanded || this.mBubbleData.isShowingOverflow()) ? 0 : 8);
    }

    /* access modifiers changed from: package-private */
    public void updateBubble(Bubble bubble) {
        animateInFlyoutForBubble(bubble);
        requestUpdate();
        logBubbleEvent(bubble, 2);
    }

    public void updateBubbleOrder(List<Bubble> list) {
        BubbleStackView$$ExternalSyntheticLambda37 bubbleStackView$$ExternalSyntheticLambda37 = new BubbleStackView$$ExternalSyntheticLambda37(this, list);
        if (this.mIsExpanded || isExpansionAnimating()) {
            bubbleStackView$$ExternalSyntheticLambda37.run();
            updateBadges(false);
            updateZOrder();
        } else if (!isExpansionAnimating()) {
            this.mStackAnimationController.animateReorder((List) list.stream().map(BubbleStackView$$ExternalSyntheticLambda45.INSTANCE).collect(Collectors.toList()), bubbleStackView$$ExternalSyntheticLambda37);
        }
        updatePointerPosition();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateBubbleOrder$16(List list) {
        for (int i = 0; i < list.size(); i++) {
            this.mBubbleContainer.reorderView(((Bubble) list.get(i)).getIconView(), i);
        }
    }

    public void setSelectedBubble(BubbleViewProvider bubbleViewProvider) {
        BubbleViewProvider bubbleViewProvider2;
        if (bubbleViewProvider == null) {
            this.mBubbleData.setShowingOverflow(false);
        } else if (this.mExpandedBubble != bubbleViewProvider) {
            if (bubbleViewProvider.getKey().equals("Overflow")) {
                this.mBubbleData.setShowingOverflow(true);
            } else {
                this.mBubbleData.setShowingOverflow(false);
            }
            if (this.mIsExpanded && this.mIsExpansionAnimating) {
                cancelAllExpandCollapseSwitchAnimations();
            }
            showManageMenu(false);
            if (!this.mIsExpanded || (bubbleViewProvider2 = this.mExpandedBubble) == null || bubbleViewProvider2.getExpandedView() == null || this.mExpandedViewTemporarilyHidden) {
                showNewlySelectedBubble(bubbleViewProvider);
                return;
            }
            BubbleViewProvider bubbleViewProvider3 = this.mExpandedBubble;
            if (!(bubbleViewProvider3 == null || bubbleViewProvider3.getExpandedView() == null)) {
                this.mExpandedBubble.getExpandedView().setSurfaceZOrderedOnTop(true);
            }
            try {
                screenshotAnimatingOutBubbleIntoSurface(new BubbleStackView$$ExternalSyntheticLambda43(this, bubbleViewProvider));
            } catch (Exception e) {
                showNewlySelectedBubble(bubbleViewProvider);
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setSelectedBubble$18(BubbleViewProvider bubbleViewProvider, Boolean bool) {
        this.mAnimatingOutSurfaceContainer.setVisibility(bool.booleanValue() ? 0 : 4);
        showNewlySelectedBubble(bubbleViewProvider);
    }

    private void showNewlySelectedBubble(BubbleViewProvider bubbleViewProvider) {
        BubbleViewProvider bubbleViewProvider2 = this.mExpandedBubble;
        this.mExpandedBubble = bubbleViewProvider;
        updatePointerPosition();
        if (this.mIsExpanded) {
            hideCurrentInputMethod();
            this.mExpandedViewContainer.setAlpha(0.0f);
            this.mSurfaceSynchronizer.syncSurfaceAndRun(new BubbleStackView$$ExternalSyntheticLambda36(this, bubbleViewProvider2, bubbleViewProvider));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showNewlySelectedBubble$19(BubbleViewProvider bubbleViewProvider, BubbleViewProvider bubbleViewProvider2) {
        if (bubbleViewProvider != null) {
            bubbleViewProvider.setTaskViewVisibility(false);
        }
        updateExpandedBubble();
        requestUpdate();
        logBubbleEvent(bubbleViewProvider, 4);
        logBubbleEvent(bubbleViewProvider2, 3);
        notifyExpansionChanged(bubbleViewProvider, false);
        notifyExpansionChanged(bubbleViewProvider2, true);
    }

    public void setExpanded(boolean z) {
        if (!z) {
            releaseAnimatingOutBubbleBuffer();
        }
        if (z != this.mIsExpanded) {
            hideCurrentInputMethod();
            this.mBubbleController.getSysuiProxy().onStackExpandChanged(z);
            if (this.mIsExpanded) {
                animateCollapse();
                logBubbleEvent(this.mExpandedBubble, 4);
            } else {
                animateExpansion();
                logBubbleEvent(this.mExpandedBubble, 3);
                logBubbleEvent(this.mExpandedBubble, 15);
            }
            notifyExpansionChanged(this.mExpandedBubble, this.mIsExpanded);
        }
    }

    /* access modifiers changed from: package-private */
    public void setBubbleVisibility(Bubble bubble, boolean z) {
        if (bubble.getIconView() != null) {
            bubble.getIconView().setVisibility(z ? 0 : 8);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideCurrentInputMethod() {
        this.mBubbleController.hideCurrentInputMethod();
    }

    /* access modifiers changed from: package-private */
    public void updateStackPosition() {
        this.mStackAnimationController.setStackPosition(this.mPositioner.getRestingPosition());
        this.mDismissView.hide();
    }

    private void beforeExpandedViewAnimation() {
        this.mIsExpansionAnimating = true;
        hideFlyoutImmediate();
        updateExpandedBubble();
        updateExpandedView();
    }

    /* access modifiers changed from: private */
    /* renamed from: afterExpandedViewAnimation */
    public void lambda$new$4() {
        this.mIsExpansionAnimating = false;
        updateExpandedView();
        requestUpdate();
    }

    /* access modifiers changed from: private */
    public void hideExpandedViewIfNeeded() {
        BubbleViewProvider bubbleViewProvider;
        if (!this.mExpandedViewTemporarilyHidden && (bubbleViewProvider = this.mExpandedBubble) != null && bubbleViewProvider.getExpandedView() != null) {
            this.mExpandedViewTemporarilyHidden = true;
            PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).spring(AnimatableScaleMatrix.SCALE_X, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(0.9f), this.mScaleOutSpringConfig).spring(AnimatableScaleMatrix.SCALE_Y, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(0.9f), this.mScaleOutSpringConfig).addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda12(this)).start();
            this.mExpandedViewAlphaAnimator.reverse();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideExpandedViewIfNeeded$20(AnimatableScaleMatrix animatableScaleMatrix, ArrayMap arrayMap) {
        this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
    }

    /* access modifiers changed from: private */
    public void showExpandedViewIfNeeded() {
        if (this.mExpandedViewTemporarilyHidden) {
            this.mExpandedViewTemporarilyHidden = false;
            PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).spring(AnimatableScaleMatrix.SCALE_X, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(1.0f), this.mScaleOutSpringConfig).spring(AnimatableScaleMatrix.SCALE_Y, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(1.0f), this.mScaleOutSpringConfig).addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda15(this)).start();
            this.mExpandedViewAlphaAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showExpandedViewIfNeeded$21(AnimatableScaleMatrix animatableScaleMatrix, ArrayMap arrayMap) {
        this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
    }

    private void animateExpansion() {
        int i;
        float f;
        int i2;
        float f2;
        cancelDelayedExpandCollapseSwitchAnimations();
        boolean showBubblesVertically = this.mPositioner.showBubblesVertically();
        this.mIsExpanded = true;
        StackEducationView stackEducationView = this.mStackEduView;
        if (stackEducationView != null) {
            stackEducationView.hide(true);
        }
        beforeExpandedViewAnimation();
        updateZOrder();
        updateBadges(false);
        this.mBubbleContainer.setActiveController(this.mExpandedAnimationController);
        updateOverflowVisibility();
        updatePointerPosition();
        this.mExpandedAnimationController.expandFromStack(new BubbleStackView$$ExternalSyntheticLambda29(this));
        if (this.mPositioner.showingInTaskbar() && this.mPositioner.getTaskbarPosition() != 2) {
            this.mTaskbarScrim.getLayoutParams().width = this.mPositioner.getTaskbarSize();
            View view = this.mTaskbarScrim;
            if (this.mStackOnLeftOrWillBe) {
                f2 = 0.0f;
            } else {
                f2 = (float) (this.mPositioner.getAvailableRect().right - this.mPositioner.getTaskbarSize());
            }
            view.setTranslationX(f2);
            this.mTaskbarScrim.setVisibility(0);
            this.mTaskbarScrim.animate().alpha(1.0f).start();
        }
        this.mExpandedViewContainer.setTranslationX(0.0f);
        this.mExpandedViewContainer.setTranslationY(this.mPositioner.getExpandedViewY());
        this.mExpandedViewContainer.setAlpha(1.0f);
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider == null || !"Overflow".equals(bubbleViewProvider.getKey())) {
            i = getBubbleIndex(this.mExpandedBubble);
        } else {
            i = this.mBubbleData.getBubbles().size();
        }
        float bubbleXOrYForOrientation = this.mExpandedAnimationController.getBubbleXOrYForOrientation(i);
        if (showBubblesVertically) {
            f = this.mStackAnimationController.getStackPosition().y;
        } else {
            f = this.mStackAnimationController.getStackPosition().x;
        }
        float abs = Math.abs(bubbleXOrYForOrientation - f);
        long j = 0;
        if (getWidth() > 0) {
            j = (long) (((abs / ((float) getWidth())) * 30.0f) + 210.00002f);
        }
        if (showBubblesVertically) {
            float f3 = (((float) this.mBubbleSize) / 2.0f) + bubbleXOrYForOrientation;
            if (this.mStackOnLeftOrWillBe) {
                i2 = this.mPositioner.getAvailableRect().left + this.mBubbleSize + this.mExpandedViewPadding;
            } else {
                i2 = (this.mPositioner.getAvailableRect().right - this.mBubbleSize) - this.mExpandedViewPadding;
            }
            this.mExpandedViewContainerMatrix.setScale(0.9f, 0.9f, (float) i2, f3);
        } else {
            this.mExpandedViewContainerMatrix.setScale(0.9f, 0.9f, (((float) this.mBubbleSize) / 2.0f) + bubbleXOrYForOrientation, this.mPositioner.getExpandedViewY());
        }
        this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
        if (this.mExpandedBubble.getExpandedView() != null) {
            this.mExpandedBubble.setExpandedContentAlpha(0.0f);
            this.mExpandedBubble.getExpandedView().setAlphaAnimating(true);
        }
        BubbleStackView$$ExternalSyntheticLambda40 bubbleStackView$$ExternalSyntheticLambda40 = new BubbleStackView$$ExternalSyntheticLambda40(this, showBubblesVertically, bubbleXOrYForOrientation);
        this.mDelayedAnimation = bubbleStackView$$ExternalSyntheticLambda40;
        this.mDelayedAnimationExecutor.executeDelayed(bubbleStackView$$ExternalSyntheticLambda40, j);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateExpansion$22() {
        if (this.mIsExpanded && this.mExpandedBubble.getExpandedView() != null) {
            maybeShowManageEdu();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateExpansion$25(boolean z, float f) {
        this.mExpandedViewAlphaAnimator.start();
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).cancel();
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).spring(AnimatableScaleMatrix.SCALE_X, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(1.0f), this.mScaleInSpringConfig).spring(AnimatableScaleMatrix.SCALE_Y, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(1.0f), this.mScaleInSpringConfig).addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda16(this, z, f)).withEndActions(new BubbleStackView$$ExternalSyntheticLambda23(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateExpansion$23(boolean z, float f, AnimatableScaleMatrix animatableScaleMatrix, ArrayMap arrayMap) {
        float f2;
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null && bubbleViewProvider.getIconView() != null) {
            if (z) {
                f2 = this.mExpandedBubble.getIconView().getTranslationY();
            } else {
                f2 = this.mExpandedBubble.getIconView().getTranslationX();
            }
            this.mExpandedViewContainerMatrix.postTranslate(f2 - f, 0.0f);
            this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateExpansion$24() {
        lambda$new$4();
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null && bubbleViewProvider.getExpandedView() != null) {
            this.mExpandedBubble.getExpandedView().setSurfaceZOrderedOnTop(false);
        }
    }

    private void animateCollapse() {
        int i;
        int i2;
        cancelDelayedExpandCollapseSwitchAnimations();
        showManageMenu(false);
        this.mIsExpanded = false;
        this.mIsExpansionAnimating = true;
        this.mBubbleContainer.cancelAllAnimations();
        PhysicsAnimator.getInstance(this.mAnimatingOutSurfaceContainer).cancel();
        this.mAnimatingOutSurfaceContainer.setScaleX(0.0f);
        this.mAnimatingOutSurfaceContainer.setScaleY(0.0f);
        this.mExpandedAnimationController.notifyPreparingToCollapse();
        this.mExpandedAnimationController.collapseBackToStack(this.mStackAnimationController.getStackPositionAlongNearestHorizontalEdge(), new BubbleStackView$$ExternalSyntheticLambda27(this));
        if (this.mTaskbarScrim.getVisibility() == 0) {
            this.mTaskbarScrim.animate().alpha(0.0f).start();
        }
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider == null || !"Overflow".equals(bubbleViewProvider.getKey())) {
            i = this.mBubbleData.getBubbles().indexOf(this.mExpandedBubble);
        } else {
            i = this.mBubbleData.getBubbles().size();
        }
        float bubbleXOrYForOrientation = this.mExpandedAnimationController.getBubbleXOrYForOrientation(i);
        this.mPositioner.showBubblesVertically();
        if (this.mPositioner.showBubblesVertically()) {
            float f = bubbleXOrYForOrientation + (((float) this.mBubbleSize) / 2.0f);
            if (this.mStackOnLeftOrWillBe) {
                i2 = this.mPositioner.getAvailableRect().left + this.mBubbleSize + this.mExpandedViewPadding;
            } else {
                i2 = (this.mPositioner.getAvailableRect().right - this.mBubbleSize) - this.mExpandedViewPadding;
            }
            this.mExpandedViewContainerMatrix.setScale(1.0f, 1.0f, (float) i2, f);
        } else {
            this.mExpandedViewContainerMatrix.setScale(1.0f, 1.0f, bubbleXOrYForOrientation + (((float) this.mBubbleSize) / 2.0f), this.mPositioner.getExpandedViewY());
        }
        this.mExpandedViewAlphaAnimator.reverse();
        if (this.mExpandedBubble.getExpandedView() != null) {
            this.mExpandedBubble.getExpandedView().setContentVisibility(false);
        }
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).cancel();
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).spring(AnimatableScaleMatrix.SCALE_X, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(0.9f), this.mScaleOutSpringConfig).spring(AnimatableScaleMatrix.SCALE_Y, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(0.9f), this.mScaleOutSpringConfig).addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda13(this)).withEndActions(new BubbleStackView$$ExternalSyntheticLambda19(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateCollapse$26() {
        this.mBubbleContainer.setActiveController(this.mStackAnimationController);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateCollapse$27(AnimatableScaleMatrix animatableScaleMatrix, ArrayMap arrayMap) {
        this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateCollapse$28() {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        beforeExpandedViewAnimation();
        ManageEducationView manageEducationView = this.mManageEduView;
        if (manageEducationView != null) {
            manageEducationView.hide(false);
        }
        updateOverflowVisibility();
        updateZOrder();
        updateBadges(true);
        lambda$new$4();
        if (bubbleViewProvider != null) {
            bubbleViewProvider.setTaskViewVisibility(false);
        }
        if (this.mPositioner.showingInTaskbar()) {
            this.mTaskbarScrim.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void animateSwitchBubbles() {
        int i;
        int i2;
        float f;
        if (this.mIsExpanded) {
            boolean z = true;
            this.mIsBubbleSwitchAnimating = true;
            PhysicsAnimator.getInstance(this.mAnimatingOutSurfaceContainer).cancel();
            this.mAnimatingOutSurfaceAlphaAnimator.reverse();
            this.mExpandedViewAlphaAnimator.start();
            if (this.mPositioner.showBubblesVertically()) {
                if (this.mStackAnimationController.isStackOnLeftSide()) {
                    f = this.mAnimatingOutSurfaceContainer.getTranslationX() + ((float) (this.mBubbleSize * 2));
                } else {
                    f = this.mAnimatingOutSurfaceContainer.getTranslationX();
                }
                PhysicsAnimator.getInstance(this.mAnimatingOutSurfaceContainer).spring(DynamicAnimation.TRANSLATION_X, f, this.mTranslateSpringConfig).start();
            } else {
                PhysicsAnimator.getInstance(this.mAnimatingOutSurfaceContainer).spring(DynamicAnimation.TRANSLATION_Y, this.mAnimatingOutSurfaceContainer.getTranslationY() - ((float) this.mBubbleSize), this.mTranslateSpringConfig).start();
            }
            BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
            if (bubbleViewProvider == null || !bubbleViewProvider.getKey().equals("Overflow")) {
                z = false;
            }
            ExpandedAnimationController expandedAnimationController = this.mExpandedAnimationController;
            if (z) {
                i = getBubbleCount();
            } else {
                i = this.mBubbleData.getBubbles().indexOf(this.mExpandedBubble);
            }
            float bubbleXOrYForOrientation = expandedAnimationController.getBubbleXOrYForOrientation(i);
            this.mExpandedViewContainer.setAlpha(1.0f);
            this.mExpandedViewContainer.setVisibility(0);
            if (this.mPositioner.showBubblesVertically()) {
                float f2 = bubbleXOrYForOrientation + (((float) this.mBubbleSize) / 2.0f);
                if (this.mStackOnLeftOrWillBe) {
                    i2 = this.mPositioner.getAvailableRect().left + this.mBubbleSize + this.mExpandedViewPadding;
                } else {
                    i2 = (this.mPositioner.getAvailableRect().right - this.mBubbleSize) - this.mExpandedViewPadding;
                }
                this.mExpandedViewContainerMatrix.setScale(0.0f, 0.0f, (float) i2, f2);
            } else {
                this.mExpandedViewContainerMatrix.setScale(0.9f, 0.9f, bubbleXOrYForOrientation + (((float) this.mBubbleSize) / 2.0f), this.mPositioner.getExpandedViewY());
            }
            this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
            this.mDelayedAnimationExecutor.executeDelayed(new BubbleStackView$$ExternalSyntheticLambda22(this), 25);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSwitchBubbles$31() {
        if (!this.mIsExpanded) {
            this.mIsBubbleSwitchAnimating = false;
            return;
        }
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).cancel();
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).spring(AnimatableScaleMatrix.SCALE_X, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(1.0f), this.mScaleInSpringConfig).spring(AnimatableScaleMatrix.SCALE_Y, AnimatableScaleMatrix.getAnimatableValueForScaleFactor(1.0f), this.mScaleInSpringConfig).addUpdateListener(new BubbleStackView$$ExternalSyntheticLambda14(this)).withEndActions(new BubbleStackView$$ExternalSyntheticLambda33(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSwitchBubbles$29(AnimatableScaleMatrix animatableScaleMatrix, ArrayMap arrayMap) {
        this.mExpandedViewContainer.setAnimationMatrix(this.mExpandedViewContainerMatrix);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSwitchBubbles$30() {
        this.mExpandedViewTemporarilyHidden = false;
        this.mIsBubbleSwitchAnimating = false;
    }

    private void cancelDelayedExpandCollapseSwitchAnimations() {
        this.mDelayedAnimationExecutor.removeCallbacks(this.mDelayedAnimation);
        this.mIsExpansionAnimating = false;
        this.mIsBubbleSwitchAnimating = false;
    }

    private void cancelAllExpandCollapseSwitchAnimations() {
        cancelDelayedExpandCollapseSwitchAnimations();
        PhysicsAnimator.getInstance(this.mAnimatingOutSurfaceView).cancel();
        PhysicsAnimator.getInstance(this.mExpandedViewContainerMatrix).cancel();
        this.mExpandedViewContainer.setAnimationMatrix((Matrix) null);
    }

    private void notifyExpansionChanged(BubbleViewProvider bubbleViewProvider, boolean z) {
        Bubbles.BubbleExpandListener bubbleExpandListener = this.mExpandListener;
        if (bubbleExpandListener != null && bubbleViewProvider != null) {
            bubbleExpandListener.onBubbleExpandChanged(z, bubbleViewProvider.getKey());
        }
    }

    public void onImeVisibilityChanged(boolean z, int i) {
        BubbleViewProvider bubbleViewProvider;
        this.mStackAnimationController.setImeHeight(z ? i + this.mImeOffset : 0);
        if (!this.mIsExpanded && getBubbleCount() > 0) {
            float animateForImeVisibility = this.mStackAnimationController.animateForImeVisibility(z) - this.mStackAnimationController.getStackPosition().y;
            if (this.mFlyout.getVisibility() == 0) {
                PhysicsAnimator.getInstance(this.mFlyout).spring(DynamicAnimation.TRANSLATION_Y, this.mFlyout.getTranslationY() + animateForImeVisibility, FLYOUT_IME_ANIMATION_SPRING_CONFIG).start();
            }
        } else if (this.mIsExpanded && (bubbleViewProvider = this.mExpandedBubble) != null && bubbleViewProvider.getExpandedView() != null) {
            this.mExpandedBubble.getExpandedView().setImeVisible(z);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (motionEvent.getAction() != 0 && motionEvent.getActionIndex() != this.mPointerIndexDown) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            this.mPointerIndexDown = motionEvent.getActionIndex();
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.mPointerIndexDown = -1;
        }
        boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
        if (!dispatchTouchEvent && !this.mIsExpanded && this.mIsGestureInProgress) {
            dispatchTouchEvent = this.mBubbleTouchListener.onTouch(this, motionEvent);
        }
        if (!(motionEvent.getAction() == 1 || motionEvent.getAction() == 3)) {
            z = true;
        }
        this.mIsGestureInProgress = z;
        return dispatchTouchEvent;
    }

    /* access modifiers changed from: package-private */
    public void setFlyoutStateForDragLength(float f) {
        if (this.mFlyout.getWidth() > 0) {
            boolean isStackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
            this.mFlyoutDragDeltaX = f;
            if (isStackOnLeftSide) {
                f = -f;
            }
            float width = f / ((float) this.mFlyout.getWidth());
            float f2 = 0.0f;
            this.mFlyout.setCollapsePercent(Math.min(1.0f, Math.max(0.0f, width)));
            int i = (width > 0.0f ? 1 : (width == 0.0f ? 0 : -1));
            if (i < 0 || width > 1.0f) {
                int i2 = (width > 1.0f ? 1 : (width == 1.0f ? 0 : -1));
                boolean z = false;
                int i3 = 1;
                boolean z2 = i2 > 0;
                if ((isStackOnLeftSide && i2 > 0) || (!isStackOnLeftSide && i < 0)) {
                    z = true;
                }
                float f3 = (z2 ? width - 1.0f : width * -1.0f) * ((float) (z ? -1 : 1));
                float width2 = (float) this.mFlyout.getWidth();
                if (z2) {
                    i3 = 2;
                }
                f2 = f3 * (width2 / (8.0f / ((float) i3)));
            }
            BubbleFlyoutView bubbleFlyoutView = this.mFlyout;
            bubbleFlyoutView.setTranslationX(bubbleFlyoutView.getRestingTranslationX() + f2);
        }
    }

    /* access modifiers changed from: private */
    public boolean passEventToMagnetizedObject(MotionEvent motionEvent) {
        MagnetizedObject<?> magnetizedObject = this.mMagnetizedObject;
        return magnetizedObject != null && magnetizedObject.maybeConsumeMotionEvent(motionEvent);
    }

    /* access modifiers changed from: private */
    public void dismissMagnetizedObject() {
        if (this.mIsExpanded) {
            dismissBubbleIfExists(this.mBubbleData.getBubbleWithView((View) this.mMagnetizedObject.getUnderlyingObject()));
            return;
        }
        this.mBubbleData.dismissAll(1);
    }

    private void dismissBubbleIfExists(BubbleViewProvider bubbleViewProvider) {
        if (bubbleViewProvider != null && this.mBubbleData.hasBubbleInStackWithKey(bubbleViewProvider.getKey())) {
            this.mBubbleData.dismissBubbleWithKey(bubbleViewProvider.getKey(), 1);
        }
    }

    /* access modifiers changed from: private */
    public void animateDismissBubble(View view, boolean z) {
        this.mViewBeingDismissed = view;
        if (view != null) {
            if (z) {
                this.mDismissBubbleAnimator.removeAllListeners();
                this.mDismissBubbleAnimator.start();
                return;
            }
            this.mDismissBubbleAnimator.removeAllListeners();
            this.mDismissBubbleAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    BubbleStackView.this.resetDismissAnimator();
                }

                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    BubbleStackView.this.resetDismissAnimator();
                }
            });
            this.mDismissBubbleAnimator.reverse();
        }
    }

    /* access modifiers changed from: private */
    public void resetDismissAnimator() {
        this.mDismissBubbleAnimator.removeAllListeners();
        this.mDismissBubbleAnimator.cancel();
        View view = this.mViewBeingDismissed;
        if (view != null) {
            view.setAlpha(1.0f);
            this.mViewBeingDismissed = null;
        }
        DismissView dismissView = this.mDismissView;
        if (dismissView != null) {
            dismissView.getCircle().setScaleX(1.0f);
            this.mDismissView.getCircle().setScaleY(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public void animateFlyoutCollapsed(boolean z, float f) {
        float f2;
        boolean isStackOnLeftSide = this.mStackAnimationController.isStackOnLeftSide();
        this.mFlyoutTransitionSpring.getSpring().setStiffness(this.mBubbleToExpandAfterFlyoutCollapse != null ? 1500.0f : 200.0f);
        SpringAnimation springAnimation = (SpringAnimation) ((SpringAnimation) this.mFlyoutTransitionSpring.setStartValue(this.mFlyoutDragDeltaX)).setStartVelocity(f);
        if (z) {
            int width = this.mFlyout.getWidth();
            if (isStackOnLeftSide) {
                width = -width;
            }
            f2 = (float) width;
        } else {
            f2 = 0.0f;
        }
        springAnimation.animateToFinalPosition(f2);
    }

    private boolean shouldShowFlyout(Bubble bubble) {
        StackEducationView stackEducationView;
        Bubble.FlyoutMessage flyoutMessage = bubble.getFlyoutMessage();
        BadgedImageView iconView = bubble.getIconView();
        if (flyoutMessage != null && flyoutMessage.message != null && bubble.showFlyout() && (((stackEducationView = this.mStackEduView) == null || stackEducationView.getVisibility() != 0) && !isExpanded() && !this.mIsExpansionAnimating && !this.mIsGestureInProgress && this.mBubbleToExpandAfterFlyoutCollapse == null && iconView != null)) {
            return true;
        }
        if (iconView == null || this.mFlyout.getVisibility() == 0) {
            return false;
        }
        iconView.removeDotSuppressionFlag(BadgedImageView.SuppressionFlag.FLYOUT_VISIBLE);
        return false;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void animateInFlyoutForBubble(Bubble bubble) {
        if (shouldShowFlyout(bubble)) {
            this.mFlyoutDragDeltaX = 0.0f;
            clearFlyoutOnHide();
            this.mAfterFlyoutHidden = new BubbleStackView$$ExternalSyntheticLambda35(this, bubble);
            bubble.getIconView().addDotSuppressionFlag(BadgedImageView.SuppressionFlag.FLYOUT_VISIBLE);
            post(new BubbleStackView$$ExternalSyntheticLambda34(this, bubble));
            this.mFlyout.removeCallbacks(this.mHideFlyout);
            this.mFlyout.postDelayed(this.mHideFlyout, 5000);
            logBubbleEvent(bubble, 16);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$32(Bubble bubble) {
        this.mAfterFlyoutHidden = null;
        BubbleViewProvider bubbleViewProvider = this.mBubbleToExpandAfterFlyoutCollapse;
        if (bubbleViewProvider != null) {
            this.mBubbleData.setSelectedBubble(bubbleViewProvider);
            this.mBubbleData.setExpanded(true);
            this.mBubbleToExpandAfterFlyoutCollapse = null;
        }
        if (bubble.getIconView() != null) {
            bubble.getIconView().removeDotSuppressionFlag(BadgedImageView.SuppressionFlag.FLYOUT_VISIBLE);
        }
        updateTemporarilyInvisibleAnimation(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$35(Bubble bubble) {
        if (!isExpanded() && bubble.getIconView() != null) {
            BubbleStackView$$ExternalSyntheticLambda25 bubbleStackView$$ExternalSyntheticLambda25 = new BubbleStackView$$ExternalSyntheticLambda25(this);
            if (this.mFlyout.getVisibility() == 0) {
                this.mFlyout.animateUpdate(bubble.getFlyoutMessage(), (float) getWidth(), this.mStackAnimationController.getStackPosition(), !bubble.showDot(), this.mAfterFlyoutHidden);
            } else {
                this.mFlyout.setVisibility(4);
                this.mFlyout.setupFlyoutStartingAsDot(bubble.getFlyoutMessage(), this.mStackAnimationController.getStackPosition(), (float) getWidth(), this.mStackAnimationController.isStackOnLeftSide(), bubble.getIconView().getDotColor(), bubbleStackView$$ExternalSyntheticLambda25, this.mAfterFlyoutHidden, bubble.getIconView().getDotCenter(), !bubble.showDot(), this.mPositioner);
            }
            this.mFlyout.bringToFront();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$34() {
        BubbleStackView$$ExternalSyntheticLambda17 bubbleStackView$$ExternalSyntheticLambda17 = new BubbleStackView$$ExternalSyntheticLambda17(this);
        this.mAnimateInFlyout = bubbleStackView$$ExternalSyntheticLambda17;
        this.mFlyout.postDelayed(bubbleStackView$$ExternalSyntheticLambda17, 200);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateInFlyoutForBubble$33() {
        int i;
        this.mFlyout.setVisibility(0);
        updateTemporarilyInvisibleAnimation(false);
        if (this.mStackAnimationController.isStackOnLeftSide()) {
            i = -this.mFlyout.getWidth();
        } else {
            i = this.mFlyout.getWidth();
        }
        this.mFlyoutDragDeltaX = (float) i;
        animateFlyoutCollapsed(false, 0.0f);
        this.mFlyout.postDelayed(this.mHideFlyout, 5000);
    }

    /* access modifiers changed from: private */
    public void hideFlyoutImmediate() {
        clearFlyoutOnHide();
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        this.mFlyout.removeCallbacks(this.mHideFlyout);
        this.mFlyout.hideFlyout();
    }

    private void clearFlyoutOnHide() {
        this.mFlyout.removeCallbacks(this.mAnimateInFlyout);
        Runnable runnable = this.mAfterFlyoutHidden;
        if (runnable != null) {
            runnable.run();
            this.mAfterFlyoutHidden = null;
        }
    }

    public void getTouchableRegion(Rect rect) {
        StackEducationView stackEducationView = this.mStackEduView;
        if (stackEducationView == null || stackEducationView.getVisibility() != 0) {
            if (this.mIsExpanded) {
                this.mBubbleContainer.getBoundsOnScreen(rect);
                rect.bottom -= (int) this.mStackAnimationController.getImeHeight();
            } else if (getBubbleCount() > 0 || this.mBubbleData.isShowingOverflow()) {
                this.mBubbleContainer.getChildAt(0).getBoundsOnScreen(rect);
                int i = rect.top;
                int i2 = this.mBubbleTouchPadding;
                rect.top = i - i2;
                rect.left -= i2;
                rect.right += i2;
                rect.bottom += i2;
            }
            if (this.mFlyout.getVisibility() == 0) {
                Rect rect2 = new Rect();
                this.mFlyout.getBoundsOnScreen(rect2);
                rect.union(rect2);
                return;
            }
            return;
        }
        rect.set(0, 0, getWidth(), getHeight());
    }

    private void requestUpdate() {
        if (!this.mViewUpdatedRequested && !this.mIsExpansionAnimating) {
            this.mViewUpdatedRequested = true;
            getViewTreeObserver().addOnPreDrawListener(this.mViewUpdater);
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void showManageMenu(boolean z) {
        this.mShowingManage = z;
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider == null || bubbleViewProvider.getExpandedView() == null) {
            this.mManageMenu.setVisibility(4);
            return;
        }
        if (z && this.mBubbleData.hasBubbleInStackWithKey(this.mExpandedBubble.getKey())) {
            Bubble bubbleInStackWithKey = this.mBubbleData.getBubbleInStackWithKey(this.mExpandedBubble.getKey());
            this.mManageSettingsIcon.setImageBitmap(bubbleInStackWithKey.getAppBadge());
            this.mManageSettingsText.setText(getResources().getString(C2219R.string.bubbles_app_settings, new Object[]{bubbleInStackWithKey.getAppName()}));
        }
        this.mExpandedBubble.getExpandedView().getManageButtonBoundsOnScreen(this.mTempRect);
        if (this.mExpandedBubble.getExpandedView().getTaskView() != null) {
            this.mExpandedBubble.getExpandedView().getTaskView().setObscuredTouchRect(this.mShowingManage ? new Rect(0, 0, getWidth(), getHeight()) : null);
        }
        boolean z2 = getResources().getConfiguration().getLayoutDirection() == 0;
        Rect rect = this.mTempRect;
        float width = (float) (z2 ? rect.left : rect.right - this.mManageMenu.getWidth());
        float height = (float) (this.mTempRect.bottom - this.mManageMenu.getHeight());
        float width2 = ((float) ((z2 ? 1 : -1) * this.mManageMenu.getWidth())) / 4.0f;
        if (z) {
            this.mManageMenu.setScaleX(0.5f);
            this.mManageMenu.setScaleY(0.5f);
            this.mManageMenu.setTranslationX(width - width2);
            ViewGroup viewGroup = this.mManageMenu;
            viewGroup.setTranslationY((((float) viewGroup.getHeight()) / 4.0f) + height);
            this.mManageMenu.setAlpha(0.0f);
            PhysicsAnimator.getInstance(this.mManageMenu).spring(DynamicAnimation.ALPHA, 1.0f).spring(DynamicAnimation.SCALE_X, 1.0f).spring(DynamicAnimation.SCALE_Y, 1.0f).spring(DynamicAnimation.TRANSLATION_X, width).spring(DynamicAnimation.TRANSLATION_Y, height).withEndActions(new BubbleStackView$$ExternalSyntheticLambda24(this)).start();
            this.mManageMenu.setVisibility(0);
            return;
        }
        PhysicsAnimator.getInstance(this.mManageMenu).spring(DynamicAnimation.ALPHA, 0.0f).spring(DynamicAnimation.SCALE_X, 0.5f).spring(DynamicAnimation.SCALE_Y, 0.5f).spring(DynamicAnimation.TRANSLATION_X, width - width2).spring(DynamicAnimation.TRANSLATION_Y, height + (((float) this.mManageMenu.getHeight()) / 4.0f)).withEndActions(new BubbleStackView$$ExternalSyntheticLambda30(this)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showManageMenu$36() {
        this.mManageMenu.getChildAt(0).requestAccessibilityFocus();
        this.mExpandedBubble.getExpandedView().updateObscuredTouchableRegion();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showManageMenu$37() {
        this.mManageMenu.setVisibility(4);
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null && bubbleViewProvider.getExpandedView() != null) {
            this.mExpandedBubble.getExpandedView().updateObscuredTouchableRegion();
        }
    }

    private void updateExpandedBubble() {
        BubbleViewProvider bubbleViewProvider;
        this.mExpandedViewContainer.removeAllViews();
        if (this.mIsExpanded && (bubbleViewProvider = this.mExpandedBubble) != null && bubbleViewProvider.getExpandedView() != null) {
            BubbleExpandedView expandedView = this.mExpandedBubble.getExpandedView();
            expandedView.setContentVisibility(false);
            expandedView.setAlphaAnimating(!this.mIsExpansionAnimating);
            this.mExpandedViewContainerMatrix.setScaleX(0.0f);
            this.mExpandedViewContainerMatrix.setScaleY(0.0f);
            this.mExpandedViewContainerMatrix.setTranslate(0.0f, 0.0f);
            this.mExpandedViewContainer.setVisibility(4);
            this.mExpandedViewContainer.setAlpha(0.0f);
            this.mExpandedViewContainer.addView(expandedView);
            expandedView.setManageClickListener(new BubbleStackView$$ExternalSyntheticLambda6(this));
            if (!this.mIsExpansionAnimating) {
                this.mSurfaceSynchronizer.syncSurfaceAndRun(new BubbleStackView$$ExternalSyntheticLambda21(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateExpandedBubble$38(View view) {
        showManageMenu(!this.mShowingManage);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateExpandedBubble$39() {
        post(new BubbleStackView$$ExternalSyntheticLambda20(this));
    }

    private void screenshotAnimatingOutBubbleIntoSurface(Consumer<Boolean> consumer) {
        BubbleViewProvider bubbleViewProvider;
        if (!this.mIsExpanded || (bubbleViewProvider = this.mExpandedBubble) == null || bubbleViewProvider.getExpandedView() == null) {
            consumer.accept(Boolean.FALSE);
            return;
        }
        BubbleExpandedView expandedView = this.mExpandedBubble.getExpandedView();
        if (this.mAnimatingOutBubbleBuffer != null) {
            releaseAnimatingOutBubbleBuffer();
        }
        try {
            this.mAnimatingOutBubbleBuffer = expandedView.snapshotActivitySurface();
        } catch (Exception e) {
            Log.wtf("Bubbles", e);
            consumer.accept(Boolean.FALSE);
        }
        SurfaceControl.ScreenshotHardwareBuffer screenshotHardwareBuffer = this.mAnimatingOutBubbleBuffer;
        if (screenshotHardwareBuffer == null || screenshotHardwareBuffer.getHardwareBuffer() == null) {
            consumer.accept(Boolean.FALSE);
            return;
        }
        PhysicsAnimator.getInstance(this.mAnimatingOutSurfaceContainer).cancel();
        this.mAnimatingOutSurfaceContainer.setScaleX(1.0f);
        this.mAnimatingOutSurfaceContainer.setScaleY(1.0f);
        this.mAnimatingOutSurfaceContainer.setTranslationX((float) this.mExpandedViewContainer.getPaddingLeft());
        this.mAnimatingOutSurfaceContainer.setTranslationY(0.0f);
        this.mAnimatingOutSurfaceContainer.setTranslationY((float) (this.mExpandedBubble.getExpandedView().getTaskViewLocationOnScreen()[1] - this.mAnimatingOutSurfaceView.getLocationOnScreen()[1]));
        this.mAnimatingOutSurfaceView.getLayoutParams().width = this.mAnimatingOutBubbleBuffer.getHardwareBuffer().getWidth();
        this.mAnimatingOutSurfaceView.getLayoutParams().height = this.mAnimatingOutBubbleBuffer.getHardwareBuffer().getHeight();
        this.mAnimatingOutSurfaceView.requestLayout();
        post(new BubbleStackView$$ExternalSyntheticLambda38(this, consumer));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$screenshotAnimatingOutBubbleIntoSurface$42(Consumer consumer) {
        SurfaceControl.ScreenshotHardwareBuffer screenshotHardwareBuffer = this.mAnimatingOutBubbleBuffer;
        if (screenshotHardwareBuffer == null || screenshotHardwareBuffer.getHardwareBuffer() == null || this.mAnimatingOutBubbleBuffer.getHardwareBuffer().isClosed()) {
            consumer.accept(Boolean.FALSE);
        } else if (!this.mIsExpanded || !this.mAnimatingOutSurfaceReady) {
            consumer.accept(Boolean.FALSE);
        } else {
            this.mAnimatingOutSurfaceView.getHolder().getSurface().attachAndQueueBufferWithColorSpace(this.mAnimatingOutBubbleBuffer.getHardwareBuffer(), this.mAnimatingOutBubbleBuffer.getColorSpace());
            this.mAnimatingOutSurfaceView.setAlpha(1.0f);
            this.mExpandedViewContainer.setVisibility(8);
            this.mSurfaceSynchronizer.syncSurfaceAndRun(new BubbleStackView$$ExternalSyntheticLambda39(this, consumer));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$screenshotAnimatingOutBubbleIntoSurface$41(Consumer consumer) {
        post(new BubbleStackView$$ExternalSyntheticLambda41(consumer));
    }

    /* access modifiers changed from: private */
    public void releaseAnimatingOutBubbleBuffer() {
        SurfaceControl.ScreenshotHardwareBuffer screenshotHardwareBuffer = this.mAnimatingOutBubbleBuffer;
        if (screenshotHardwareBuffer != null && !screenshotHardwareBuffer.getHardwareBuffer().isClosed()) {
            this.mAnimatingOutBubbleBuffer.getHardwareBuffer().close();
        }
    }

    /* access modifiers changed from: private */
    public void updateExpandedView() {
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        int i = 0;
        int[] expandedViewPadding = this.mPositioner.getExpandedViewPadding(this.mStackAnimationController.isStackOnLeftSide(), bubbleViewProvider != null && "Overflow".equals(bubbleViewProvider.getKey()));
        this.mExpandedViewContainer.setPadding(expandedViewPadding[0], expandedViewPadding[1], expandedViewPadding[2], expandedViewPadding[3]);
        if (this.mIsExpansionAnimating) {
            FrameLayout frameLayout = this.mExpandedViewContainer;
            if (!this.mIsExpanded) {
                i = 8;
            }
            frameLayout.setVisibility(i);
        }
        BubbleViewProvider bubbleViewProvider2 = this.mExpandedBubble;
        if (!(bubbleViewProvider2 == null || bubbleViewProvider2.getExpandedView() == null)) {
            this.mExpandedViewContainer.setTranslationY(this.mPositioner.getExpandedViewY());
            this.mExpandedViewContainer.setTranslationX(0.0f);
            this.mExpandedBubble.getExpandedView().updateView(this.mExpandedViewContainer.getLocationOnScreen());
            updatePointerPosition();
        }
        this.mStackOnLeftOrWillBe = this.mStackAnimationController.isStackOnLeftSide();
    }

    /* access modifiers changed from: private */
    public void updateBubbleShadows(boolean z) {
        int bubbleCount = getBubbleCount();
        for (int i = 0; i < bubbleCount; i++) {
            float maxBubbles = (float) ((this.mPositioner.getMaxBubbles() * this.mBubbleElevation) - i);
            BadgedImageView badgedImageView = (BadgedImageView) this.mBubbleContainer.getChildAt(i);
            MagnetizedObject<?> magnetizedObject = this.mMagnetizedObject;
            boolean z2 = magnetizedObject != null && magnetizedObject.getUnderlyingObject().equals(badgedImageView);
            if (z || z2) {
                badgedImageView.setZ(maxBubbles);
            } else {
                if (i >= 2) {
                    maxBubbles = 0.0f;
                }
                badgedImageView.setZ(maxBubbles);
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateShadows() {
        int bubbleCount = getBubbleCount();
        int i = 0;
        while (i < bubbleCount) {
            BadgedImageView badgedImageView = (BadgedImageView) this.mBubbleContainer.getChildAt(i);
            if (!(i < 2)) {
                badgedImageView.animate().translationZ(0.0f).start();
            }
            i++;
        }
    }

    private void updateZOrder() {
        int bubbleCount = getBubbleCount();
        int i = 0;
        while (i < bubbleCount) {
            ((BadgedImageView) this.mBubbleContainer.getChildAt(i)).setZ(i < 2 ? (float) ((this.mPositioner.getMaxBubbles() * this.mBubbleElevation) - i) : 0.0f);
            i++;
        }
    }

    /* access modifiers changed from: private */
    public void updateBadges(boolean z) {
        int bubbleCount = getBubbleCount();
        for (int i = 0; i < bubbleCount; i++) {
            BadgedImageView badgedImageView = (BadgedImageView) this.mBubbleContainer.getChildAt(i);
            boolean z2 = true;
            if (this.mIsExpanded) {
                if (!this.mPositioner.showBubblesVertically() || this.mStackOnLeftOrWillBe) {
                    z2 = false;
                }
                badgedImageView.showDotAndBadge(z2);
            } else if (z) {
                if (i == 0) {
                    badgedImageView.showDotAndBadge(!this.mStackOnLeftOrWillBe);
                } else {
                    badgedImageView.hideDotAndBadge(!this.mStackOnLeftOrWillBe);
                }
            }
        }
    }

    private void updatePointerPosition() {
        int bubbleIndex;
        BubbleViewProvider bubbleViewProvider = this.mExpandedBubble;
        if (bubbleViewProvider != null && bubbleViewProvider.getExpandedView() != null && (bubbleIndex = getBubbleIndex(this.mExpandedBubble)) != -1) {
            this.mExpandedBubble.getExpandedView().setPointerPosition(this.mExpandedAnimationController.getBubbleXOrYForOrientation(bubbleIndex), this.mStackOnLeftOrWillBe);
        }
    }

    public int getBubbleCount() {
        return this.mBubbleContainer.getChildCount() - 1;
    }

    /* access modifiers changed from: package-private */
    public int getBubbleIndex(BubbleViewProvider bubbleViewProvider) {
        if (bubbleViewProvider == null) {
            return 0;
        }
        return this.mBubbleContainer.indexOfChild(bubbleViewProvider.getIconView());
    }

    public float getNormalizedXPosition() {
        BigDecimal bigDecimal = new BigDecimal((double) (getStackPosition().x / ((float) this.mPositioner.getAvailableRect().width())));
        RoundingMode roundingMode = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    public float getNormalizedYPosition() {
        BigDecimal bigDecimal = new BigDecimal((double) (getStackPosition().y / ((float) this.mPositioner.getAvailableRect().height())));
        RoundingMode roundingMode = RoundingMode.CEILING;
        return bigDecimal.setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    public PointF getStackPosition() {
        return this.mStackAnimationController.getStackPosition();
    }

    /* access modifiers changed from: private */
    public void logBubbleEvent(BubbleViewProvider bubbleViewProvider, int i) {
        this.mBubbleData.logBubbleEvent(bubbleViewProvider, i, (bubbleViewProvider == null || !(bubbleViewProvider instanceof Bubble)) ? "null" : ((Bubble) bubbleViewProvider).getPackageName(), getBubbleCount(), getBubbleIndex(bubbleViewProvider), getNormalizedXPosition(), getNormalizedYPosition());
    }

    /* access modifiers changed from: package-private */
    public List<Bubble> getBubblesOnScreen() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < getBubbleCount(); i++) {
            View childAt = this.mBubbleContainer.getChildAt(i);
            if (childAt instanceof BadgedImageView) {
                arrayList.add(this.mBubbleData.getBubbleInStackWithKey(((BadgedImageView) childAt).getKey()));
            }
        }
        return arrayList;
    }

    /* renamed from: com.android.wm.shell.bubbles.BubbleStackView$RelativeStackPosition */
    public static class RelativeStackPosition {
        private boolean mOnLeft;
        private float mVerticalOffsetPercent;

        public RelativeStackPosition(boolean z, float f) {
            this.mOnLeft = z;
            this.mVerticalOffsetPercent = clampVerticalOffsetPercent(f);
        }

        public RelativeStackPosition(PointF pointF, RectF rectF) {
            this.mOnLeft = pointF.x < rectF.width() / 2.0f;
            this.mVerticalOffsetPercent = clampVerticalOffsetPercent((pointF.y - rectF.top) / rectF.height());
        }

        private float clampVerticalOffsetPercent(float f) {
            return Math.max(0.0f, Math.min(1.0f, f));
        }

        public PointF getAbsolutePositionInRegion(RectF rectF) {
            return new PointF(this.mOnLeft ? rectF.left : rectF.right, rectF.top + (this.mVerticalOffsetPercent * rectF.height()));
        }
    }
}
