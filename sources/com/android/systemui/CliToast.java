package com.android.systemui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.moto.MotoFeature;
import java.util.ArrayList;
import java.util.List;

public class CliToast {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static CliToast mInstance;
    private boolean mAllowOverwrite = false;
    private Context mContext;
    private ToastItem mCurrentItem;
    private Runnable mDismissRunnable = new CliToast$$ExternalSyntheticLambda0(this);
    private int mDuration;
    private Handler mHandler;
    private ImageView mLeftIconView;
    private WindowManager.LayoutParams mParams;
    private ImageView mRightIconView;
    private boolean mShowing;
    private TextView mTextView;
    private View mToastView;
    private WindowManager mWM;
    private Drawable mWaitDisplayDrawable;
    private Pos mWaitDisplayPos = Pos.LEFT;
    private String mWaitDisplayText;
    private List<ToastItem> mWaitingToasts = new ArrayList();

    public enum Pos {
        LEFT,
        RIGHT
    }

    public static CliToast getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CliToast(MotoFeature.getCliContext(context.getApplicationContext()));
        }
        return mInstance;
    }

    public CliToast(Context context) {
        this.mContext = context;
        this.mWM = (WindowManager) context.getSystemService("window");
        View inflate = LayoutInflater.from(context).inflate(R$layout.cli_charger_toast_layout, (ViewGroup) null);
        this.mToastView = inflate;
        this.mTextView = (TextView) inflate.findViewById(R$id.message);
        this.mLeftIconView = (ImageView) this.mToastView.findViewById(R$id.left_icon);
        this.mRightIconView = (ImageView) this.mToastView.findViewById(R$id.right_icon);
        initParams();
    }

    private void initParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.mParams = layoutParams;
        layoutParams.height = -2;
        layoutParams.width = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = 16973828;
        layoutParams.type = 2020;
        layoutParams.flags = 152;
        layoutParams.gravity = 80;
        layoutParams.y = this.mContext.getResources().getDimensionPixelSize(R$dimen.cli_toast_margin_bottom);
    }

    public WindowManager.LayoutParams getWindowParams() {
        return this.mParams;
    }

    public CliToast makeText(CharSequence charSequence, int i) {
        return makeTextWithDrawable((CharSequence) charSequence.toString(), (Drawable) null, i);
    }

    public CliToast makeTextWithDrawable(CharSequence charSequence, Drawable drawable, int i) {
        return makeTextWithDrawable(charSequence, drawable, Pos.LEFT, i);
    }

    public CliToast makeTextWithDrawable(CharSequence charSequence, int i, int i2) {
        return makeTextWithDrawable(charSequence, this.mContext.getDrawable(i), Pos.LEFT, i2);
    }

    public CliToast makeTextWithDrawable(CharSequence charSequence, Drawable drawable, Pos pos, int i) {
        this.mWaitDisplayText = charSequence.toString();
        this.mWaitDisplayDrawable = drawable;
        this.mWaitDisplayPos = pos;
        this.mDuration = i;
        return mInstance;
    }

    public void show() {
        ToastItem toastItem = new ToastItem(this.mWaitDisplayText, this.mWaitDisplayDrawable, this.mWaitDisplayPos, this.mDuration);
        this.mWaitDisplayText = null;
        this.mWaitDisplayDrawable = null;
        if ((this.mShowing || this.mWaitingToasts.size() > 0) && !this.mAllowOverwrite) {
            this.mWaitingToasts.add(toastItem);
        } else {
            show(toastItem);
        }
    }

    private void show(ToastItem toastItem) {
        if (this.mToastView.getParent() != null) {
            if (DEBUG) {
                Log.v("CliToast", "CLI-Toast: Remove " + this.mToastView);
            }
            this.mWM.removeView(this.mToastView);
        }
        if (TextUtils.isEmpty(toastItem.text)) {
            if (DEBUG) {
                Log.v("CliToast", "CLI-Toast: Don't show cli toast, the content is empty.");
            }
            toastItem.clear();
            return;
        }
        startDismissRunnable(toastItem.duration);
        try {
            this.mTextView.setText(toastItem.text);
            Drawable access$200 = toastItem.drawable;
            if (access$200 == null) {
                if (this.mRightIconView.getVisibility() == 0) {
                    this.mRightIconView.setVisibility(8);
                }
                if (this.mLeftIconView.getVisibility() == 0) {
                    this.mLeftIconView.setVisibility(8);
                }
            } else if (toastItem.position == Pos.LEFT) {
                this.mLeftIconView.setImageDrawable(access$200);
                this.mLeftIconView.setVisibility(0);
                this.mRightIconView.setVisibility(8);
            } else if (toastItem.position == Pos.RIGHT) {
                this.mRightIconView.setImageDrawable(access$200);
                this.mRightIconView.setVisibility(0);
                this.mLeftIconView.setVisibility(8);
            }
            this.mWM.addView(this.mToastView, this.mParams);
            if (DEBUG) {
                Log.v("CliToast", "CLI-Toast: Add " + this.mToastView);
            }
            this.mShowing = true;
            this.mCurrentItem = toastItem;
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
            this.mShowing = false;
            toastItem.clear();
            Log.e("CliToast", "BadTokenException: Unable to add window.");
        }
    }

    private void showNextToast() {
        show(this.mWaitingToasts.get(0));
        this.mWaitingToasts.remove(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        View view = this.mToastView;
        if (view != null && view.getParent() != null) {
            if (DEBUG) {
                Log.v("CliToast", "CLI-Toast: Remove " + this.mToastView);
            }
            this.mWM.removeViewImmediate(this.mToastView);
            this.mCurrentItem.clear();
            this.mCurrentItem = null;
            this.mShowing = false;
            if (this.mWaitingToasts.size() > 0) {
                showNextToast();
            }
        }
    }

    private void startDismissRunnable(int i) {
        if (this.mHandler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        }
        this.mHandler.removeCallbacks(this.mDismissRunnable);
        this.mHandler.postDelayed(this.mDismissRunnable, i == 1 ? 7000 : 4000);
    }

    class ToastItem {
        /* access modifiers changed from: private */
        public Drawable drawable;
        /* access modifiers changed from: private */
        public int duration;
        /* access modifiers changed from: private */
        public Pos position;
        /* access modifiers changed from: private */
        public String text;

        public ToastItem(String str, Drawable drawable2, Pos pos, int i) {
            this.text = str;
            this.drawable = drawable2;
            this.position = pos;
            this.duration = i;
        }

        public void clear() {
            this.text = null;
            this.drawable = null;
            this.position = null;
            this.duration = -1;
        }
    }
}
