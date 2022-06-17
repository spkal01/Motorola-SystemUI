package androidx.leanback.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillValue;
import android.widget.EditText;
import android.widget.TextView;
import androidx.core.widget.TextViewCompat;

@SuppressLint({"AppCompatCustomView"})
public class GuidedActionEditText extends EditText {
    private GuidedActionAutofillSupport$OnAutofillListener mAutofillListener;
    private ImeKeyMonitor$ImeKeyListener mKeyListener;
    private final Drawable mNoPaddingDrawable;
    private final Drawable mSavedBackground;

    public int getAutofillType() {
        return 1;
    }

    static final class NoPaddingDrawable extends Drawable {
        public void draw(Canvas canvas) {
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        NoPaddingDrawable() {
        }

        public boolean getPadding(Rect rect) {
            rect.set(0, 0, 0, 0);
            return true;
        }
    }

    public GuidedActionEditText(Context context) {
        this(context, (AttributeSet) null);
    }

    public GuidedActionEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842862);
    }

    public GuidedActionEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSavedBackground = getBackground();
        NoPaddingDrawable noPaddingDrawable = new NoPaddingDrawable();
        this.mNoPaddingDrawable = noPaddingDrawable;
        setBackground(noPaddingDrawable);
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        ImeKeyMonitor$ImeKeyListener imeKeyMonitor$ImeKeyListener = this.mKeyListener;
        boolean onKeyPreIme = imeKeyMonitor$ImeKeyListener != null ? imeKeyMonitor$ImeKeyListener.onKeyPreIme(this, i, keyEvent) : false;
        return !onKeyPreIme ? super.onKeyPreIme(i, keyEvent) : onKeyPreIme;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName((isFocused() ? EditText.class : TextView.class).getName());
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (z) {
            setBackground(this.mSavedBackground);
        } else {
            setBackground(this.mNoPaddingDrawable);
        }
        if (!z) {
            setFocusable(false);
        }
    }

    public void autofill(AutofillValue autofillValue) {
        super.autofill(autofillValue);
        GuidedActionAutofillSupport$OnAutofillListener guidedActionAutofillSupport$OnAutofillListener = this.mAutofillListener;
        if (guidedActionAutofillSupport$OnAutofillListener != null) {
            guidedActionAutofillSupport$OnAutofillListener.onAutofill(this);
        }
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, callback));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isInTouchMode() || isFocusableInTouchMode() || isTextSelectable()) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }
}
