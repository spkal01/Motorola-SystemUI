package com.android.keyguard;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import com.android.settingslib.Utils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import com.android.systemui.R$styleable;
import java.util.ArrayList;
import java.util.Stack;

public class PasswordTextView extends View {
    private static char DOT = '•';
    /* access modifiers changed from: private */
    public Interpolator mAppearInterpolator;
    /* access modifiers changed from: private */
    public int mCharPadding;
    /* access modifiers changed from: private */
    public Stack<CharState> mCharPool;
    /* access modifiers changed from: private */
    public Interpolator mDisappearInterpolator;
    /* access modifiers changed from: private */
    public int mDotSize;
    /* access modifiers changed from: private */
    public final Paint mDrawPaint;
    private Interpolator mFastOutSlowInInterpolator;
    private final int mGravity;
    private OnInputPinChangedListener mInputPinChangedListener;
    private PowerManager mPM;
    /* access modifiers changed from: private */
    public boolean mShowPassword;
    private String mText;
    /* access modifiers changed from: private */
    public ArrayList<CharState> mTextChars;
    private int mTextHeightRaw;
    /* access modifiers changed from: private */
    public boolean mUsePrcSixPin;
    private UserActivityListener mUserActivityListener;

    interface OnInputPinChangedListener {
        void inputPinChanged();
    }

    public interface UserActivityListener {
        void onUserActivity();
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public PasswordTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PasswordTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PasswordTextView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    /* JADX INFO: finally extract failed */
    public PasswordTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTextChars = new ArrayList<>();
        this.mText = "";
        this.mCharPool = new Stack<>();
        Paint paint = new Paint();
        this.mDrawPaint = paint;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.View);
        boolean z = true;
        try {
            boolean z2 = obtainStyledAttributes.getBoolean(19, true);
            boolean z3 = obtainStyledAttributes.getBoolean(20, true);
            setFocusable(z2);
            setFocusableInTouchMode(z3);
            obtainStyledAttributes.recycle();
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R$styleable.PasswordTextView);
            try {
                this.mTextHeightRaw = obtainStyledAttributes2.getInt(R$styleable.PasswordTextView_scaledTextSize, 0);
                this.mGravity = obtainStyledAttributes2.getInt(R$styleable.PasswordTextView_android_gravity, 17);
                this.mDotSize = obtainStyledAttributes2.getDimensionPixelSize(R$styleable.PasswordTextView_dotSize, getContext().getResources().getDimensionPixelSize(R$dimen.password_dot_size));
                this.mCharPadding = obtainStyledAttributes2.getDimensionPixelSize(R$styleable.PasswordTextView_charPadding, getContext().getResources().getDimensionPixelSize(R$dimen.password_char_padding));
                paint.setColor(obtainStyledAttributes2.getColor(R$styleable.PasswordTextView_android_textColor, -1));
                obtainStyledAttributes2.recycle();
                paint.setFlags(129);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTypeface(Typeface.create(context.getString(17039962), 0));
                this.mShowPassword = Settings.System.getInt(this.mContext.getContentResolver(), "show_password", 1) != 1 ? false : z;
                this.mAppearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563662);
                this.mDisappearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563663);
                this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563661);
                this.mPM = (PowerManager) this.mContext.getSystemService("power");
            } catch (Throwable th) {
                obtainStyledAttributes2.recycle();
                throw th;
            }
        } catch (Throwable th2) {
            obtainStyledAttributes.recycle();
            throw th2;
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        this.mTextHeightRaw = getContext().getResources().getInteger(R$integer.scaled_password_text_size);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r13) {
        /*
            r12 = this;
            float r0 = r12.getDrawingWidth()
            int r1 = r12.mGravity
            r2 = r1 & 7
            r3 = 3
            if (r2 != r3) goto L_0x0026
            r2 = 8388608(0x800000, float:1.17549435E-38)
            r1 = r1 & r2
            if (r1 == 0) goto L_0x0020
            int r1 = r12.getLayoutDirection()
            r2 = 1
            if (r1 != r2) goto L_0x0020
            int r1 = r12.getWidth()
            int r2 = r12.getPaddingRight()
            goto L_0x0033
        L_0x0020:
            int r0 = r12.getPaddingLeft()
            float r1 = (float) r0
            goto L_0x0052
        L_0x0026:
            r1 = r1 & 7
            r2 = 5
            if (r1 != r2) goto L_0x0037
            int r1 = r12.getWidth()
            int r2 = r12.getPaddingRight()
        L_0x0033:
            int r1 = r1 - r2
            float r1 = (float) r1
            float r1 = r1 - r0
            goto L_0x0052
        L_0x0037:
            int r1 = r12.getWidth()
            int r2 = r12.getPaddingRight()
            int r1 = r1 - r2
            float r1 = (float) r1
            float r1 = r1 - r0
            int r2 = r12.getWidth()
            float r2 = (float) r2
            r3 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r3
            float r0 = r0 / r3
            float r2 = r2 - r0
            r0 = 0
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0052
            r1 = r2
        L_0x0052:
            java.util.ArrayList<com.android.keyguard.PasswordTextView$CharState> r0 = r12.mTextChars
            int r0 = r0.size()
            android.graphics.Rect r2 = r12.getCharBounds()
            int r3 = r2.bottom
            int r4 = r2.top
            int r3 = r3 - r4
            int r4 = r12.getHeight()
            int r5 = r12.getPaddingBottom()
            int r4 = r4 - r5
            int r5 = r12.getPaddingTop()
            int r4 = r4 - r5
            int r4 = r4 / 2
            int r5 = r12.getPaddingTop()
            int r4 = r4 + r5
            float r4 = (float) r4
            int r5 = r12.getPaddingLeft()
            int r6 = r12.getPaddingTop()
            int r7 = r12.getWidth()
            int r8 = r12.getPaddingRight()
            int r7 = r7 - r8
            int r8 = r12.getHeight()
            int r9 = r12.getPaddingBottom()
            int r8 = r8 - r9
            r13.clipRect(r5, r6, r7, r8)
            int r5 = r2.right
            int r2 = r2.left
            int r5 = r5 - r2
            float r2 = (float) r5
            boolean r5 = r12.mUsePrcSixPin
            r6 = 0
            if (r5 == 0) goto L_0x00bf
            r11 = r6
        L_0x00a0:
            r5 = 6
            if (r11 >= r5) goto L_0x00d7
            if (r11 >= r0) goto L_0x00b7
            java.util.ArrayList<com.android.keyguard.PasswordTextView$CharState> r5 = r12.mTextChars
            java.lang.Object r5 = r5.get(r11)
            com.android.keyguard.PasswordTextView$CharState r5 = (com.android.keyguard.PasswordTextView.CharState) r5
            r6 = r13
            r7 = r1
            r8 = r3
            r9 = r4
            r10 = r2
            float r5 = r5.draw(r6, r7, r8, r9, r10)
            goto L_0x00bb
        L_0x00b7:
            float r5 = r12.drawStrokeCircle(r13, r1, r4, r2)
        L_0x00bb:
            float r1 = r1 + r5
            int r11 = r11 + 1
            goto L_0x00a0
        L_0x00bf:
            r11 = r6
        L_0x00c0:
            if (r11 >= r0) goto L_0x00d7
            java.util.ArrayList<com.android.keyguard.PasswordTextView$CharState> r5 = r12.mTextChars
            java.lang.Object r5 = r5.get(r11)
            com.android.keyguard.PasswordTextView$CharState r5 = (com.android.keyguard.PasswordTextView.CharState) r5
            r6 = r13
            r7 = r1
            r8 = r3
            r9 = r4
            r10 = r2
            float r5 = r5.draw(r6, r7, r8, r9, r10)
            float r1 = r1 + r5
            int r11 = r11 + 1
            goto L_0x00c0
        L_0x00d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.PasswordTextView.onDraw(android.graphics.Canvas):void");
    }

    public void reloadColors() {
        this.mDrawPaint.setColor(Utils.getColorAttr(getContext(), 16842806).getDefaultColor());
    }

    private Rect getCharBounds() {
        this.mDrawPaint.setTextSize(((float) this.mTextHeightRaw) * getResources().getDisplayMetrics().scaledDensity);
        Rect rect = new Rect();
        this.mDrawPaint.getTextBounds("0", 0, 1, rect);
        return rect;
    }

    private float getDrawingWidth() {
        int i;
        int size = this.mTextChars.size();
        Rect charBounds = getCharBounds();
        int i2 = charBounds.right - charBounds.left;
        int i3 = 0;
        if (this.mUsePrcSixPin) {
            i = 0;
            while (i3 < 6) {
                if (i3 != 0) {
                    i += this.mCharPadding;
                }
                i += i2;
                i3++;
            }
        } else {
            int i4 = 0;
            while (i3 < size) {
                CharState charState = this.mTextChars.get(i3);
                if (i3 != 0) {
                    i4 = (int) (((float) i4) + (((float) this.mCharPadding) * charState.currentWidthFactor));
                }
                i4 = (int) (((float) i4) + (((float) i2) * charState.currentWidthFactor));
                i3++;
            }
            i = i4;
        }
        return (float) i;
    }

    public void append(char c) {
        CharState charState;
        int size = this.mTextChars.size();
        if (!this.mUsePrcSixPin || size != 6) {
            CharSequence transformedText = getTransformedText();
            String str = this.mText + c;
            this.mText = str;
            int length = str.length();
            if (length > size) {
                charState = obtainCharState(c);
                this.mTextChars.add(charState);
            } else {
                CharState charState2 = this.mTextChars.get(length - 1);
                charState2.whichChar = c;
                charState = charState2;
            }
            charState.startAppearAnimation();
            if (length > 1) {
                CharState charState3 = this.mTextChars.get(length - 2);
                if (charState3.isDotSwapPending) {
                    charState3.swapToDotWhenAppearFinished();
                }
            }
            userActivity();
            sendAccessibilityEventTypeViewTextChanged(transformedText, transformedText.length(), 0, 1);
            OnInputPinChangedListener onInputPinChangedListener = this.mInputPinChangedListener;
            if (onInputPinChangedListener != null) {
                onInputPinChangedListener.inputPinChanged();
            }
        }
    }

    public void setUserActivityListener(UserActivityListener userActivityListener) {
        this.mUserActivityListener = userActivityListener;
    }

    private void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
        UserActivityListener userActivityListener = this.mUserActivityListener;
        if (userActivityListener != null) {
            userActivityListener.onUserActivity();
        }
    }

    public void deleteLastChar() {
        int length = this.mText.length();
        CharSequence transformedText = getTransformedText();
        if (length > 0) {
            int i = length - 1;
            this.mText = this.mText.substring(0, i);
            this.mTextChars.get(i).startRemoveAnimation(0, 0);
            sendAccessibilityEventTypeViewTextChanged(transformedText, transformedText.length() - 1, 1, 0);
        }
        userActivity();
    }

    public String getText() {
        return this.mText;
    }

    /* access modifiers changed from: private */
    public CharSequence getTransformedText() {
        int size = this.mTextChars.size();
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            CharState charState = this.mTextChars.get(i);
            if (charState.dotAnimator == null || charState.dotAnimationIsGrowing) {
                sb.append(charState.isCharVisibleForA11y() ? charState.whichChar : DOT);
            }
        }
        return sb;
    }

    private CharState obtainCharState(char c) {
        CharState charState;
        if (this.mCharPool.isEmpty()) {
            charState = new CharState();
        } else {
            charState = this.mCharPool.pop();
            charState.reset();
        }
        charState.whichChar = c;
        return charState;
    }

    public void reset(boolean z, boolean z2) {
        CharSequence transformedText = getTransformedText();
        this.mText = "";
        int size = this.mTextChars.size();
        int i = size - 1;
        int i2 = i / 2;
        int i3 = 0;
        while (i3 < size) {
            CharState charState = this.mTextChars.get(i3);
            if (z) {
                charState.startRemoveAnimation(Math.min(((long) (i3 <= i2 ? i3 * 2 : i - (((i3 - i2) - 1) * 2))) * 40, 200), Math.min(40 * ((long) i), 200) + 160);
                charState.removeDotSwapCallbacks();
            } else {
                this.mCharPool.push(charState);
            }
            i3++;
        }
        if (!z) {
            this.mTextChars.clear();
        }
        if (z2) {
            sendAccessibilityEventTypeViewTextChanged(transformedText, 0, transformedText.length(), 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void sendAccessibilityEventTypeViewTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            return;
        }
        if (isFocused() || (isSelected() && isShown())) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(16);
            obtain.setFromIndex(i);
            obtain.setRemovedCount(i2);
            obtain.setAddedCount(i3);
            obtain.setBeforeText(charSequence);
            CharSequence transformedText = getTransformedText();
            if (!TextUtils.isEmpty(transformedText)) {
                obtain.getText().add(transformedText);
            }
            obtain.setPassword(true);
            sendAccessibilityEventUnchecked(obtain);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(EditText.class.getName());
        accessibilityEvent.setPassword(true);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(EditText.class.getName());
        accessibilityNodeInfo.setPassword(true);
        accessibilityNodeInfo.setText(getTransformedText());
        accessibilityNodeInfo.setEditable(true);
        accessibilityNodeInfo.setInputType(16);
    }

    private class CharState {
        float currentDotSizeFactor;
        float currentTextSizeFactor;
        float currentTextTranslationY;
        float currentWidthFactor;
        boolean dotAnimationIsGrowing;
        Animator dotAnimator;
        Animator.AnimatorListener dotFinishListener;
        private ValueAnimator.AnimatorUpdateListener dotSizeUpdater;
        private Runnable dotSwapperRunnable;
        boolean isDotSwapPending;
        Animator.AnimatorListener removeEndListener;
        boolean textAnimationIsGrowing;
        ValueAnimator textAnimator;
        Animator.AnimatorListener textFinishListener;
        private ValueAnimator.AnimatorUpdateListener textSizeUpdater;
        ValueAnimator textTranslateAnimator;
        Animator.AnimatorListener textTranslateFinishListener;
        private ValueAnimator.AnimatorUpdateListener textTranslationUpdater;
        char whichChar;
        boolean widthAnimationIsGrowing;
        ValueAnimator widthAnimator;
        Animator.AnimatorListener widthFinishListener;
        private ValueAnimator.AnimatorUpdateListener widthUpdater;

        private CharState() {
            this.currentTextTranslationY = 1.0f;
            this.removeEndListener = new AnimatorListenerAdapter() {
                private boolean mCancelled;

                public void onAnimationCancel(Animator animator) {
                    this.mCancelled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    if (!this.mCancelled) {
                        PasswordTextView.this.mTextChars.remove(CharState.this);
                        PasswordTextView.this.mCharPool.push(CharState.this);
                        CharState.this.reset();
                        CharState charState = CharState.this;
                        charState.cancelAnimator(charState.textTranslateAnimator);
                        CharState.this.textTranslateAnimator = null;
                    }
                }

                public void onAnimationStart(Animator animator) {
                    this.mCancelled = false;
                }
            };
            this.dotFinishListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CharState.this.dotAnimator = null;
                }
            };
            this.textFinishListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CharState.this.textAnimator = null;
                }
            };
            this.textTranslateFinishListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CharState.this.textTranslateAnimator = null;
                }
            };
            this.widthFinishListener = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CharState.this.widthAnimator = null;
                }
            };
            this.dotSizeUpdater = new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CharState.this.currentDotSizeFactor = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.textSizeUpdater = new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    boolean isCharVisibleForA11y = CharState.this.isCharVisibleForA11y();
                    CharState charState = CharState.this;
                    float f = charState.currentTextSizeFactor;
                    charState.currentTextSizeFactor = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    if (isCharVisibleForA11y != CharState.this.isCharVisibleForA11y()) {
                        CharState charState2 = CharState.this;
                        charState2.currentTextSizeFactor = f;
                        CharSequence access$500 = PasswordTextView.this.getTransformedText();
                        CharState.this.currentTextSizeFactor = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        int indexOf = PasswordTextView.this.mTextChars.indexOf(CharState.this);
                        if (indexOf >= 0) {
                            PasswordTextView.this.sendAccessibilityEventTypeViewTextChanged(access$500, indexOf, 1, 1);
                        }
                    }
                    PasswordTextView.this.invalidate();
                }
            };
            this.textTranslationUpdater = new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CharState.this.currentTextTranslationY = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.widthUpdater = new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CharState.this.currentWidthFactor = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.dotSwapperRunnable = new Runnable() {
                public void run() {
                    CharState.this.performSwap();
                    CharState.this.isDotSwapPending = false;
                }
            };
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.whichChar = 0;
            this.currentTextSizeFactor = 0.0f;
            this.currentDotSizeFactor = 0.0f;
            this.currentWidthFactor = 0.0f;
            cancelAnimator(this.textAnimator);
            this.textAnimator = null;
            cancelAnimator(this.dotAnimator);
            this.dotAnimator = null;
            cancelAnimator(this.widthAnimator);
            this.widthAnimator = null;
            this.currentTextTranslationY = 1.0f;
            removeDotSwapCallbacks();
        }

        /* access modifiers changed from: package-private */
        public void startRemoveAnimation(long j, long j2) {
            boolean z = true;
            boolean z2 = (this.currentDotSizeFactor > 0.0f && this.dotAnimator == null) || (this.dotAnimator != null && this.dotAnimationIsGrowing);
            boolean z3 = (this.currentTextSizeFactor > 0.0f && this.textAnimator == null) || (this.textAnimator != null && this.textAnimationIsGrowing);
            if ((this.currentWidthFactor <= 0.0f || this.widthAnimator != null) && (this.widthAnimator == null || !this.widthAnimationIsGrowing)) {
                z = false;
            }
            if (z2) {
                if (PasswordTextView.this.mUsePrcSixPin) {
                    this.currentDotSizeFactor = 1.0f;
                }
                startDotDisappearAnimation(j);
            }
            if (z3) {
                startTextDisappearAnimation(j);
            }
            if (z) {
                if (PasswordTextView.this.mUsePrcSixPin) {
                    this.currentWidthFactor = 1.0f;
                }
                startWidthDisappearAnimation(j2);
            }
        }

        /* access modifiers changed from: package-private */
        public void startAppearAnimation() {
            boolean z = true;
            boolean z2 = !PasswordTextView.this.mShowPassword && (this.dotAnimator == null || !this.dotAnimationIsGrowing);
            boolean z3 = PasswordTextView.this.mShowPassword && (this.textAnimator == null || !this.textAnimationIsGrowing);
            if (this.widthAnimator != null && this.widthAnimationIsGrowing) {
                z = false;
            }
            if (z2) {
                if (PasswordTextView.this.mUsePrcSixPin) {
                    this.currentDotSizeFactor = 1.0f;
                }
                startDotAppearAnimation(0);
            }
            if (z3) {
                startTextAppearAnimation();
            }
            if (z) {
                if (PasswordTextView.this.mUsePrcSixPin) {
                    this.currentWidthFactor = 1.0f;
                }
                startWidthAppearAnimation();
            }
            if (PasswordTextView.this.mShowPassword) {
                postDotSwap(1300);
            }
        }

        private void postDotSwap(long j) {
            removeDotSwapCallbacks();
            PasswordTextView.this.postDelayed(this.dotSwapperRunnable, j);
            this.isDotSwapPending = true;
        }

        /* access modifiers changed from: private */
        public void removeDotSwapCallbacks() {
            PasswordTextView.this.removeCallbacks(this.dotSwapperRunnable);
            this.isDotSwapPending = false;
        }

        /* access modifiers changed from: package-private */
        public void swapToDotWhenAppearFinished() {
            removeDotSwapCallbacks();
            ValueAnimator valueAnimator = this.textAnimator;
            if (valueAnimator != null) {
                postDotSwap((valueAnimator.getDuration() - this.textAnimator.getCurrentPlayTime()) + 100);
            } else {
                performSwap();
            }
        }

        /* access modifiers changed from: private */
        public void performSwap() {
            startTextDisappearAnimation(0);
            startDotAppearAnimation(30);
        }

        private void startWidthDisappearAnimation(long j) {
            cancelAnimator(this.widthAnimator);
            this.widthAnimator = ValueAnimator.ofFloat(new float[]{this.currentWidthFactor, 0.0f});
            if (PasswordTextView.this.mUsePrcSixPin) {
                this.widthAnimator = ValueAnimator.ofFloat(new float[]{this.currentWidthFactor, 1.0f});
            }
            this.widthAnimator.addUpdateListener(this.widthUpdater);
            this.widthAnimator.addListener(this.widthFinishListener);
            this.widthAnimator.addListener(this.removeEndListener);
            this.widthAnimator.setDuration((long) (this.currentWidthFactor * 160.0f));
            this.widthAnimator.setStartDelay(j);
            this.widthAnimator.start();
            this.widthAnimationIsGrowing = false;
        }

        private void startTextDisappearAnimation(long j) {
            cancelAnimator(this.textAnimator);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.currentTextSizeFactor, 0.0f});
            this.textAnimator = ofFloat;
            ofFloat.addUpdateListener(this.textSizeUpdater);
            this.textAnimator.addListener(this.textFinishListener);
            this.textAnimator.setInterpolator(PasswordTextView.this.mDisappearInterpolator);
            this.textAnimator.setDuration((long) (this.currentTextSizeFactor * 160.0f));
            this.textAnimator.setStartDelay(j);
            this.textAnimator.start();
            this.textAnimationIsGrowing = false;
        }

        private void startDotDisappearAnimation(long j) {
            cancelAnimator(this.dotAnimator);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.currentDotSizeFactor, 0.0f});
            if (PasswordTextView.this.mUsePrcSixPin) {
                ofFloat = ValueAnimator.ofFloat(new float[]{this.currentDotSizeFactor, 1.0f});
            }
            ofFloat.addUpdateListener(this.dotSizeUpdater);
            ofFloat.addListener(this.dotFinishListener);
            ofFloat.setInterpolator(PasswordTextView.this.mDisappearInterpolator);
            ofFloat.setDuration((long) (Math.min(this.currentDotSizeFactor, 1.0f) * 160.0f));
            ofFloat.setStartDelay(j);
            ofFloat.start();
            this.dotAnimator = ofFloat;
            this.dotAnimationIsGrowing = false;
        }

        private void startWidthAppearAnimation() {
            cancelAnimator(this.widthAnimator);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.currentWidthFactor, 1.0f});
            this.widthAnimator = ofFloat;
            ofFloat.addUpdateListener(this.widthUpdater);
            this.widthAnimator.addListener(this.widthFinishListener);
            this.widthAnimator.setDuration((long) ((1.0f - this.currentWidthFactor) * 100.0f));
            this.widthAnimator.start();
            this.widthAnimationIsGrowing = true;
        }

        private void startTextAppearAnimation() {
            cancelAnimator(this.textAnimator);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.currentTextSizeFactor, 1.0f});
            this.textAnimator = ofFloat;
            ofFloat.addUpdateListener(this.textSizeUpdater);
            this.textAnimator.addListener(this.textFinishListener);
            this.textAnimator.setInterpolator(PasswordTextView.this.mAppearInterpolator);
            this.textAnimator.setDuration((long) ((1.0f - this.currentTextSizeFactor) * 100.0f));
            this.textAnimator.start();
            this.textAnimationIsGrowing = true;
            if (this.textTranslateAnimator == null) {
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
                this.textTranslateAnimator = ofFloat2;
                ofFloat2.addUpdateListener(this.textTranslationUpdater);
                this.textTranslateAnimator.addListener(this.textTranslateFinishListener);
                this.textTranslateAnimator.setInterpolator(PasswordTextView.this.mAppearInterpolator);
                this.textTranslateAnimator.setDuration(100);
                this.textTranslateAnimator.start();
            }
        }

        private void startDotAppearAnimation(long j) {
            cancelAnimator(this.dotAnimator);
            if (!PasswordTextView.this.mShowPassword) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.currentDotSizeFactor, 1.5f});
                ofFloat.addUpdateListener(this.dotSizeUpdater);
                ofFloat.setInterpolator(PasswordTextView.this.mAppearInterpolator);
                ofFloat.setDuration(160);
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{1.5f, 1.0f});
                ofFloat2.addUpdateListener(this.dotSizeUpdater);
                ofFloat2.setDuration(160);
                ofFloat2.addListener(this.dotFinishListener);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(new Animator[]{ofFloat, ofFloat2});
                animatorSet.setStartDelay(j);
                animatorSet.start();
                this.dotAnimator = animatorSet;
            } else {
                ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{this.currentDotSizeFactor, 1.0f});
                ofFloat3.addUpdateListener(this.dotSizeUpdater);
                ofFloat3.setDuration((long) ((1.0f - this.currentDotSizeFactor) * 100.0f));
                ofFloat3.addListener(this.dotFinishListener);
                ofFloat3.setStartDelay(j);
                ofFloat3.start();
                this.dotAnimator = ofFloat3;
            }
            this.dotAnimationIsGrowing = true;
        }

        /* access modifiers changed from: private */
        public void cancelAnimator(Animator animator) {
            if (animator != null) {
                animator.cancel();
            }
        }

        public float draw(Canvas canvas, float f, int i, float f2, float f3) {
            float f4 = this.currentTextSizeFactor;
            boolean z = true;
            boolean z2 = f4 > 0.0f;
            if (this.currentDotSizeFactor <= 0.0f) {
                z = false;
            }
            float f5 = f3 * this.currentWidthFactor;
            if (z2) {
                float f6 = (float) i;
                float f7 = ((f6 / 2.0f) * f4) + f2 + (f6 * this.currentTextTranslationY * 0.8f);
                canvas.save();
                canvas.translate((f5 / 2.0f) + f, f7);
                float f8 = this.currentTextSizeFactor;
                canvas.scale(f8, f8);
                if (PasswordTextView.this.mUsePrcSixPin) {
                    PasswordTextView.this.mDrawPaint.setStyle(Paint.Style.FILL);
                }
                canvas.drawText(Character.toString(this.whichChar), 0.0f, 0.0f, PasswordTextView.this.mDrawPaint);
                canvas.restore();
            }
            if (z) {
                canvas.save();
                canvas.translate(f + (f5 / 2.0f), f2);
                if (PasswordTextView.this.mUsePrcSixPin) {
                    PasswordTextView.this.mDrawPaint.setStyle(Paint.Style.FILL);
                }
                canvas.drawCircle(0.0f, 0.0f, ((float) (PasswordTextView.this.mDotSize / 2)) * this.currentDotSizeFactor, PasswordTextView.this.mDrawPaint);
                canvas.restore();
            }
            return f5 + (((float) PasswordTextView.this.mCharPadding) * this.currentWidthFactor);
        }

        public boolean isCharVisibleForA11y() {
            boolean z = this.textAnimator != null && this.textAnimationIsGrowing;
            if (this.currentTextSizeFactor > 0.0f || z) {
                return true;
            }
            return false;
        }
    }

    private float drawStrokeCircle(Canvas canvas, float f, float f2, float f3) {
        float f4 = f3 * 1.0f;
        canvas.save();
        canvas.translate(f + (f4 / 2.0f), f2);
        this.mDrawPaint.setStyle(Paint.Style.STROKE);
        this.mDrawPaint.setStrokeWidth(2.0f);
        canvas.drawCircle(0.0f, 0.0f, (float) (this.mDotSize / 2), this.mDrawPaint);
        canvas.restore();
        return f4 + ((float) this.mCharPadding);
    }

    public void setUsePrcSixPin() {
        this.mUsePrcSixPin = true;
        this.mShowPassword = false;
        this.mDotSize = getContext().getResources().getDimensionPixelSize(R$dimen.prc_six_pin_password_dot_size);
        this.mCharPadding = getContext().getResources().getDimensionPixelSize(R$dimen.prc_six_pin_password_char_padding);
        invalidate();
    }

    public void setOnInputPinChanged(OnInputPinChangedListener onInputPinChangedListener) {
        this.mInputPinChangedListener = onInputPinChangedListener;
    }
}
