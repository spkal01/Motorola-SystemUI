package com.motorola.systemui.statusbar.onsview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.widget.TextView;
import androidx.constraintlayout.widget.R$styleable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MarqueeTextView extends TextView {
    static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable(TAG, 3));
    /* access modifiers changed from: private */
    public static String TAG = "CarrierLabel";
    private int mCount;
    /* access modifiers changed from: private */
    public OnMarqueeListener mOnMarqueeListener;
    private ResetHandler mResetHandler = new ResetHandler();
    private Choreographer.FrameCallback mRestartCallback = new Choreographer.FrameCallback() {
        public void doFrame(long j) {
            Class<?> cls;
            Field declaredField;
            if (MarqueeTextView.DEBUG) {
                Log.i(MarqueeTextView.TAG, "mRestartCallback doFrame");
            }
            try {
                Field declaredField2 = MarqueeTextView.this.getClass().getSuperclass().getDeclaredField("mMarquee");
                if (declaredField2 != null) {
                    declaredField2.setAccessible(true);
                    Object obj = declaredField2.get(MarqueeTextView.this);
                    if (obj != null && (declaredField = cls.getDeclaredField("mStatus")) != null) {
                        declaredField.setAccessible(true);
                        if (((Byte) declaredField.get(obj)).byteValue() == 2) {
                            Field declaredField3 = (cls = obj.getClass()).getDeclaredField("mRepeatLimit");
                            declaredField3.setAccessible(true);
                            int intValue = ((Integer) declaredField3.get(obj)).intValue();
                            if (intValue >= 0) {
                                intValue--;
                            }
                            if (MarqueeTextView.this.mOnMarqueeListener != null) {
                                MarqueeTextView.this.mOnMarqueeListener.onMarqueeRepeateChanged(intValue);
                            }
                            Method declaredMethod = cls.getDeclaredMethod("start", new Class[]{Integer.TYPE});
                            declaredMethod.setAccessible(true);
                            declaredMethod.invoke(obj, new Object[]{Integer.valueOf(intValue)});
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /* access modifiers changed from: private */
    public Runnable mRunnable = new Runnable() {
        public void run() {
            MarqueeTextView marqueeTextView = MarqueeTextView.this;
            marqueeTextView.setMarqueeListener(marqueeTextView.mOnMarqueeListener);
        }
    };
    private boolean mSetListenerSuccess = false;

    public interface OnMarqueeListener {
        void onMarqueeRepeateChanged(int i);
    }

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MarqueeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setSelected(boolean z) {
        super.setSelected(z);
    }

    public boolean post(Runnable runnable) {
        return super.post(runnable);
    }

    public void setMarqueeListener(OnMarqueeListener onMarqueeListener) {
        if (onMarqueeListener == null) {
            return;
        }
        if (this.mOnMarqueeListener != onMarqueeListener || !this.mSetListenerSuccess) {
            this.mOnMarqueeListener = onMarqueeListener;
            this.mSetListenerSuccess = setMarqueeListener();
            if (DEBUG) {
                String str = TAG;
                Log.i(str, "setMarqueeListener mCount = " + this.mCount + " mSetListenerSuccess = " + this.mSetListenerSuccess);
            }
            if (!this.mSetListenerSuccess) {
                int i = this.mCount + 1;
                this.mCount = i;
                if (i < 3) {
                    this.mResetHandler.obtainMessage(R$styleable.Constraint_layout_goneMarginRight).sendToTarget();
                } else {
                    this.mCount = 0;
                }
            } else {
                this.mCount = 0;
            }
        }
    }

    private class ResetHandler extends Handler {
        private ResetHandler() {
        }

        public void handleMessage(Message message) {
            if (message.what == 101) {
                postDelayed(MarqueeTextView.this.mRunnable, 5);
            }
        }
    }

    private boolean setMarqueeListener() {
        try {
            Field declaredField = getClass().getSuperclass().getDeclaredField("mMarquee");
            if (declaredField == null) {
                return false;
            }
            declaredField.setAccessible(true);
            Object obj = declaredField.get(this);
            if (obj == null) {
                return false;
            }
            Class<?> cls = obj.getClass();
            Field declaredField2 = cls.getDeclaredField("mStatus");
            if (declaredField2 != null) {
                declaredField2.setAccessible(true);
            }
            Field declaredField3 = cls.getDeclaredField("mRestartCallback");
            if (declaredField3 == null) {
                return false;
            }
            declaredField3.setAccessible(true);
            declaredField3.set(obj, this.mRestartCallback);
            if (DEBUG) {
                Log.i(TAG, "Set mRestartCallback");
            }
            return true;
        } catch (Exception e) {
            if (DEBUG) {
                Log.i(TAG, "add listener error");
            }
            e.printStackTrace();
            return false;
        }
    }
}
