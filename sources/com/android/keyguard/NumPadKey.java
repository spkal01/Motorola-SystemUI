package com.android.keyguard;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.Utils;
import com.android.systemui.R$array;
import com.android.systemui.R$attr;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.R$styleable;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;

public class NumPadKey extends ViewGroup {
    static String[] sKlondike;
    private NumPadAnimator mAnimator;
    protected int mDigit;
    protected final TextView mDigitText;
    private boolean mIsCli;
    boolean mIsDesktop;
    protected final TextView mKlondikeText;
    private boolean mLayoutHorizontally;
    private View.OnClickListener mListener;
    private final LockPatternUtils mLockPatternUtils;
    private int mOrientation;
    private final PowerManager mPM;
    protected PasswordTextView mTextView;
    protected int mTextViewResId;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }

    public NumPadKey(Context context) {
        this(context, (AttributeSet) null);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public NumPadKey(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, (!MotoFeature.getInstance(context).isSupportCli() || !MotoFeature.isCliContext(context)) ? R$attr.numPadKeyStyle : 0);
    }

    public NumPadKey(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R$layout.keyguard_num_pad_key);
    }

    /* JADX INFO: finally extract failed */
    protected NumPadKey(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        int i3;
        this.mDigit = -1;
        boolean z = false;
        this.mIsCli = false;
        this.mListener = new View.OnClickListener() {
            public void onClick(View view) {
                View findViewById;
                NumPadKey numPadKey = NumPadKey.this;
                if (numPadKey.mTextView == null && numPadKey.mTextViewResId > 0 && (findViewById = numPadKey.getRootView().findViewById(NumPadKey.this.mTextViewResId)) != null && (findViewById instanceof PasswordTextView)) {
                    NumPadKey.this.mTextView = (PasswordTextView) findViewById;
                }
                PasswordTextView passwordTextView = NumPadKey.this.mTextView;
                if (passwordTextView != null && passwordTextView.isEnabled()) {
                    NumPadKey numPadKey2 = NumPadKey.this;
                    numPadKey2.mTextView.append(Character.forDigit(numPadKey2.mDigit, 10));
                }
                NumPadKey.this.userActivity();
            }
        };
        this.mLayoutHorizontally = false;
        setFocusable(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.NumPadKey, i, i2);
        try {
            this.mDigit = obtainStyledAttributes.getInt(R$styleable.NumPadKey_digit, this.mDigit);
            this.mTextViewResId = obtainStyledAttributes.getResourceId(R$styleable.NumPadKey_textView, 0);
            obtainStyledAttributes.recycle();
            setOnClickListener(this.mListener);
            setOnHoverListener(new LiftToActivateListener((AccessibilityManager) context.getSystemService("accessibility")));
            this.mLockPatternUtils = new LockPatternUtils(context);
            this.mPM = (PowerManager) this.mContext.getSystemService("power");
            ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(i2, this, true);
            TextView textView = (TextView) findViewById(R$id.digit_text);
            this.mDigitText = textView;
            textView.setText(Integer.toString(this.mDigit));
            TextView textView2 = (TextView) findViewById(R$id.klondike_text);
            this.mKlondikeText = textView2;
            if (this.mDigit >= 0) {
                if (sKlondike == null) {
                    sKlondike = getResources().getStringArray(R$array.lockscreen_num_pad_klondike);
                }
                String[] strArr = sKlondike;
                if (strArr != null && strArr.length > (i3 = this.mDigit)) {
                    String str = strArr[i3];
                    if (str.length() > 0) {
                        textView2.setText(str);
                    } else if (textView2.getVisibility() != 8) {
                        textView2.setVisibility(4);
                    }
                }
            }
            setContentDescription(textView.getText().toString());
            if (MotoFeature.getInstance(context).isSupportCli() && MotoFeature.isCliContext(context)) {
                z = true;
            }
            this.mIsCli = z;
            Drawable background = getBackground();
            boolean isDesktopDisplayContext = DesktopFeature.isDesktopDisplayContext(context);
            this.mIsDesktop = isDesktopDisplayContext;
            if (!(background instanceof RippleDrawable)) {
                this.mAnimator = null;
            } else if (this.mIsCli) {
                this.mAnimator = null;
            } else if (isDesktopDisplayContext) {
                this.mAnimator = new NumPadAnimator(context, (RippleDrawable) background, R$style.PTNumPadKey);
            } else {
                this.mAnimator = new NumPadAnimator(context, (RippleDrawable) background, R$style.NumPadKey);
            }
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        this.mOrientation = configuration.orientation;
    }

    public void reloadColors() {
        int defaultColor = Utils.getColorAttr(getContext(), 16842806).getDefaultColor();
        int defaultColor2 = Utils.getColorAttr(getContext(), 16842808).getDefaultColor();
        this.mDigitText.setTextColor(defaultColor);
        this.mKlondikeText.setTextColor(defaultColor2);
        NumPadAnimator numPadAnimator = this.mAnimator;
        if (numPadAnimator != null) {
            numPadAnimator.reloadColors(getContext());
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            doHapticKeyClick();
            NumPadAnimator numPadAnimator = this.mAnimator;
            if (numPadAnimator != null) {
                numPadAnimator.start();
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        measureChildren(i, i2);
        int measuredWidth = getMeasuredWidth();
        if (this.mAnimator == null || this.mOrientation == 2) {
            measuredWidth = (int) (((float) measuredWidth) * 0.66f);
        }
        if (this.mIsCli) {
            measuredWidth = getMeasuredHeight();
        }
        if (this.mIsDesktop) {
            int measuredHeight = getMeasuredHeight();
            setMeasuredDimension(measuredHeight, measuredHeight);
            return;
        }
        setMeasuredDimension(getMeasuredWidth(), measuredWidth);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mLayoutHorizontally) {
            layoutHorizontally();
            return;
        }
        int measuredHeight = this.mDigitText.getMeasuredHeight();
        int measuredHeight2 = this.mKlondikeText.getMeasuredHeight();
        int height = (getHeight() / 2) - ((measuredHeight + measuredHeight2) / 2);
        int width = getWidth() / 2;
        int measuredWidth = width - (this.mDigitText.getMeasuredWidth() / 2);
        int i5 = measuredHeight + height;
        TextView textView = this.mDigitText;
        textView.layout(measuredWidth, height, textView.getMeasuredWidth() + measuredWidth, i5);
        int i6 = (int) (((float) i5) - (((float) measuredHeight2) * 0.35f));
        int measuredWidth2 = width - (this.mKlondikeText.getMeasuredWidth() / 2);
        TextView textView2 = this.mKlondikeText;
        textView2.layout(measuredWidth2, i6, textView2.getMeasuredWidth() + measuredWidth2, measuredHeight2 + i6);
        NumPadAnimator numPadAnimator = this.mAnimator;
        if (numPadAnimator != null) {
            numPadAnimator.onLayout(i4 - i2);
        }
    }

    public void doHapticKeyClick() {
        if (this.mLockPatternUtils.isTactileFeedbackEnabled()) {
            performHapticFeedback(1, 3);
        }
    }

    public void setLayoutHorizontally(boolean z) {
        this.mLayoutHorizontally = z;
    }

    private void layoutHorizontally() {
        int measuredWidth = this.mDigitText.getMeasuredWidth();
        int measuredHeight = this.mDigitText.getMeasuredHeight();
        int measuredWidth2 = this.mKlondikeText.getMeasuredWidth();
        int measuredHeight2 = this.mKlondikeText.getMeasuredHeight();
        int height = (getHeight() / 2) - (measuredHeight / 2);
        int width = getWidth() / 2;
        int i = width - measuredWidth;
        this.mDigitText.layout(i, height, measuredWidth + i, measuredHeight + height);
        int i2 = measuredHeight2 / 2;
        int i3 = width + i2;
        int height2 = (getHeight() / 2) - i2;
        this.mKlondikeText.layout(i3, height2, measuredWidth2 + i3, measuredHeight2 + height2);
    }
}
