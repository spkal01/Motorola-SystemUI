package com.motorola.gesturetouch;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.R$id;

class PositionTouchController {
    private static final boolean DEBUG = (!Build.IS_USER);
    private final int BOUNCE_ANIMATION_DURATION_MS = 50;
    private final int POSITION_ANIMATION_DURATION_MS = 750;
    /* access modifiers changed from: private */
    public ValueAnimator mBounceAnimator;
    private boolean mCanMove;
    /* access modifiers changed from: private */
    public View mClickAbleArea;
    private Context mContext;
    private View mDragAreaView;
    /* access modifiers changed from: private */
    public View mEdgeTouchPillView;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    private int mInitLayoutParamsX;
    private int mInitLayoutParamsY;
    private int mInitX;
    private int mInitY;
    /* access modifiers changed from: private */
    public boolean mIsReversed = false;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mLayoutParams;
    private int mMaxY;
    private int mMinY;
    private int mMovePointerId;
    /* access modifiers changed from: private */
    public ValueAnimator mPillAnimator;
    /* access modifiers changed from: private */
    public EdgeTouchPillController mPillController;
    private boolean mPositionMode;
    private GestureTouchSettingsManager mSettingsManager;

    public PositionTouchController(Context context, View view, WindowManager.LayoutParams layoutParams, EdgeTouchPillController edgeTouchPillController, GestureTouchSettingsManager gestureTouchSettingsManager) {
        this.mContext = context;
        this.mEdgeTouchPillView = view;
        this.mClickAbleArea = view.findViewById(R$id.clickable_area);
        this.mLayoutParams = layoutParams;
        this.mPillController = edgeTouchPillController;
        this.mSettingsManager = gestureTouchSettingsManager;
    }

    public void updatePositionMode(boolean z) {
        if (this.mPositionMode != z) {
            this.mPositionMode = z;
            if (z) {
                startPillAnimation();
            } else {
                lambda$onTouchEvent$0();
            }
        }
    }

    public void onUserSwitch(Context context) {
        this.mContext = context;
    }

    public void updateDragView(View view) {
        this.mDragAreaView = view;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int i = 0;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked != 6) {
                                return;
                            }
                        }
                    }
                } else if (this.mCanMove) {
                    while (true) {
                        if (i >= motionEvent.getPointerCount()) {
                            i = -1;
                            break;
                        } else if (motionEvent.getPointerId(i) == this.mMovePointerId) {
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (i != -1) {
                        int rawX = ((int) motionEvent.getRawX(i)) - this.mInitX;
                        int rawY = ((int) motionEvent.getRawY(i)) - this.mInitY;
                        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
                        layoutParams.x = this.mInitLayoutParamsX + rawX;
                        int i2 = this.mInitLayoutParamsY + rawY;
                        layoutParams.y = i2;
                        int i3 = this.mMaxY;
                        if (i2 > i3) {
                            layoutParams.y = i3;
                        } else {
                            int i4 = this.mMinY;
                            if (i2 < i4) {
                                layoutParams.y = i4;
                            }
                        }
                        this.mHandler.post(new PositionTouchController$$ExternalSyntheticLambda0(this));
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
            if (DEBUG) {
                Log.i("GestureTouch", "UP");
            }
            if (motionEvent.getActionMasked() == 1) {
                bouncePillToEdge();
                return;
            } else if (motionEvent.getPointerId(motionEvent.getActionIndex()) == this.mMovePointerId || motionEvent.getActionMasked() == 3) {
                bouncePillToEdge();
                return;
            } else {
                return;
            }
        }
        if (DEBUG) {
            Log.i("GestureTouch", "DOWN mInitX");
        }
        int rawX2 = (int) (motionEvent.getActionMasked() == 0 ? motionEvent.getRawX() : motionEvent.getRawX(motionEvent.getActionIndex()));
        int rawY2 = (int) (motionEvent.getActionMasked() == 0 ? motionEvent.getRawY() : motionEvent.getRawY(motionEvent.getActionIndex()));
        int[] iArr = new int[2];
        this.mEdgeTouchPillView.getLocationOnScreen(iArr);
        if (new Rect(iArr[0], iArr[1], iArr[0] + this.mEdgeTouchPillView.getWidth(), iArr[1] + this.mEdgeTouchPillView.getHeight()).contains(rawX2, rawY2)) {
            this.mInitX = rawX2;
            this.mInitY = rawY2;
            int[] iArr2 = new int[2];
            this.mDragAreaView.getLocationOnScreen(iArr2);
            int i5 = iArr2[1];
            this.mMinY = i5;
            this.mMaxY = (i5 + this.mDragAreaView.getHeight()) - this.mEdgeTouchPillView.getHeight();
            this.mInitLayoutParamsX = iArr[0];
            this.mInitLayoutParamsY = this.mLayoutParams.y;
            this.mCanMove = true;
            if (motionEvent.getActionMasked() != 0) {
                i = motionEvent.getPointerId(motionEvent.getActionIndex());
            }
            this.mMovePointerId = i;
            this.mHandler.post(new PositionTouchController$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTouchEvent$1() {
        this.mPillController.mWindowManager.updateViewLayout(this.mEdgeTouchPillView, this.mLayoutParams);
    }

    public void bouncePillToEdge() {
        int i;
        this.mMovePointerId = -1;
        this.mCanMove = false;
        startPillAnimation();
        int i2 = this.mPillController.getDisplayBounds().x;
        int[] iArr = new int[2];
        this.mEdgeTouchPillView.getLocationOnScreen(iArr);
        if (iArr[0] < i2 / 2) {
            i = 0;
        } else {
            i = i2 - this.mLayoutParams.width;
        }
        startBounceAnimation(iArr[0], i);
    }

    private void startBounceAnimation(int i, int i2) {
        ValueAnimator valueAnimator = getbouncePillToEdgeAnimator(i, i2);
        this.mBounceAnimator = valueAnimator;
        valueAnimator.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                ValueAnimator unused = PositionTouchController.this.mBounceAnimator = null;
                PositionTouchController.this.storePosition();
            }

            public void onAnimationCancel(Animator animator) {
                ValueAnimator unused = PositionTouchController.this.mBounceAnimator = null;
                PositionTouchController.this.storePosition();
            }
        });
        this.mBounceAnimator.start();
    }

    /* access modifiers changed from: private */
    public void storePosition() {
        int i = this.mPillController.getDisplayBounds().x;
        int[] iArr = new int[2];
        this.mEdgeTouchPillView.getLocationOnScreen(iArr);
        this.mSettingsManager.writePositionData(true, iArr[0] < i / 2 ? 0 : 1);
        this.mSettingsManager.writePositionData(false, iArr[1]);
    }

    private void startPillAnimation() {
        if (this.mPillAnimator == null) {
            ValueAnimator pillPositionAnimator = getPillPositionAnimator();
            this.mPillAnimator = pillPositionAnimator;
            pillPositionAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    PositionTouchController positionTouchController = PositionTouchController.this;
                    boolean unused = positionTouchController.mIsReversed = !positionTouchController.mIsReversed;
                }

                public void onAnimationEnd(Animator animator) {
                    PositionTouchController.this.mHandler.post(new PositionTouchController$2$$ExternalSyntheticLambda0(this));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    if (PositionTouchController.this.mPillAnimator != null) {
                        if (PositionTouchController.this.mIsReversed) {
                            PositionTouchController.this.mPillAnimator.reverse();
                        } else {
                            PositionTouchController.this.mPillAnimator.start();
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    ValueAnimator unused = PositionTouchController.this.mPillAnimator = null;
                    boolean unused2 = PositionTouchController.this.mIsReversed = false;
                    PositionTouchController.this.mClickAbleArea.setScaleY(1.0f);
                    PositionTouchController.this.mClickAbleArea.setScaleX(1.0f);
                }
            });
            this.mPillAnimator.start();
        } else if (DEBUG) {
            Log.i("GestureTouch", "startPillAnimation is running, return");
        }
    }

    /* renamed from: stopPillAnimation */
    public void lambda$onTouchEvent$0() {
        if (this.mPillAnimator != null) {
            if (DEBUG) {
                Log.i("GestureTouch", "stopPillAnimation");
            }
            this.mPillAnimator.cancel();
        }
    }

    public ValueAnimator getPillPositionAnimator() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 1.2f});
        ofFloat.setDuration(750);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float f = (Float) valueAnimator.getAnimatedValue();
                PositionTouchController.this.mClickAbleArea.setScaleY(f.floatValue());
                PositionTouchController.this.mClickAbleArea.setScaleX(f.floatValue());
            }
        });
        return ofFloat;
    }

    public ValueAnimator getbouncePillToEdgeAnimator(int i, int i2) {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{i, i2});
        ofInt.setDuration(50);
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PositionTouchController.this.mLayoutParams.x = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                PositionTouchController.this.mPillController.mWindowManager.updateViewLayout(PositionTouchController.this.mEdgeTouchPillView, PositionTouchController.this.mLayoutParams);
            }
        });
        return ofInt;
    }
}
