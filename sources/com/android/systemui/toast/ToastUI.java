package com.android.systemui.toast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.INotificationManager;
import android.app.ITransientNotificationCallback;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.widget.ToastPresenter;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.CommandQueue;
import java.util.Objects;

public class ToastUI extends SystemUI implements CommandQueue.Callbacks {
    private final AccessibilityManager mAccessibilityManager;
    private ITransientNotificationCallback mCallback;
    private final CommandQueue mCommandQueue;
    private final IAccessibilityManager mIAccessibilityManager;
    private final INotificationManager mNotificationManager;
    private int mOrientation;
    private ToastPresenter mPresenter;
    SystemUIToast mToast;
    private final ToastFactory mToastFactory;
    private final ToastLogger mToastLogger;
    /* access modifiers changed from: private */
    public ToastOutAnimatorListener mToastOutAnimatorListener;

    public ToastUI(Context context, CommandQueue commandQueue, ToastFactory toastFactory, ToastLogger toastLogger) {
        this(context, commandQueue, INotificationManager.Stub.asInterface(ServiceManager.getService("notification")), IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility")), toastFactory, toastLogger);
    }

    ToastUI(Context context, CommandQueue commandQueue, INotificationManager iNotificationManager, IAccessibilityManager iAccessibilityManager, ToastFactory toastFactory, ToastLogger toastLogger) {
        super(context);
        this.mOrientation = 1;
        this.mCommandQueue = commandQueue;
        this.mNotificationManager = iNotificationManager;
        this.mIAccessibilityManager = iAccessibilityManager;
        this.mToastFactory = toastFactory;
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class);
        this.mToastLogger = toastLogger;
    }

    public void start() {
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
    }

    public void showToast(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback) {
        ToastUI$$ExternalSyntheticLambda1 toastUI$$ExternalSyntheticLambda1 = new ToastUI$$ExternalSyntheticLambda1(this, i, charSequence, str, iTransientNotificationCallback, iBinder, iBinder2, i2);
        ToastOutAnimatorListener toastOutAnimatorListener = this.mToastOutAnimatorListener;
        if (toastOutAnimatorListener != null) {
            toastOutAnimatorListener.setShowNextToastRunnable(toastUI$$ExternalSyntheticLambda1);
        } else if (this.mPresenter != null) {
            hideCurrentToast(toastUI$$ExternalSyntheticLambda1);
        } else {
            toastUI$$ExternalSyntheticLambda1.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showToast$0(int i, CharSequence charSequence, String str, ITransientNotificationCallback iTransientNotificationCallback, IBinder iBinder, IBinder iBinder2, int i2) {
        String str2 = str;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i);
        Context createContextAsUser = this.mContext.createContextAsUser(userHandleForUid, 0);
        ToastFactory toastFactory = this.mToastFactory;
        SystemUIToast createToast = toastFactory.createToast(this.mContext, charSequence, str, userHandleForUid.getIdentifier(), this.mOrientation);
        this.mToast = createToast;
        if (createToast.getInAnimation() != null) {
            this.mToast.getInAnimation().start();
        }
        this.mCallback = iTransientNotificationCallback;
        ToastPresenter toastPresenter = new ToastPresenter(createContextAsUser, this.mIAccessibilityManager, this.mNotificationManager, str2);
        this.mPresenter = toastPresenter;
        toastPresenter.getLayoutParams().setTrustedOverlay();
        this.mToastLogger.logOnShowToast(i, str2, charSequence.toString(), iBinder.toString());
        this.mPresenter.show(this.mToast.getView(), iBinder, iBinder2, i2, this.mToast.getGravity().intValue(), this.mToast.getXOffset().intValue(), this.mToast.getYOffset().intValue(), (float) this.mToast.getHorizontalMargin().intValue(), (float) this.mToast.getVerticalMargin().intValue(), this.mCallback, this.mToast.hasCustomAnimation());
    }

    public void hideToast(String str, IBinder iBinder) {
        ToastPresenter toastPresenter = this.mPresenter;
        if (toastPresenter == null || !Objects.equals(toastPresenter.getPackageName(), str) || !Objects.equals(this.mPresenter.getToken(), iBinder)) {
            Log.w("ToastUI", "Attempt to hide non-current toast from package " + str);
            return;
        }
        this.mToastLogger.logOnHideToast(str, iBinder.toString());
        hideCurrentToast((Runnable) null);
    }

    private void hideCurrentToast(Runnable runnable) {
        if (this.mToast.getOutAnimation() != null) {
            Animator outAnimation = this.mToast.getOutAnimation();
            ToastOutAnimatorListener toastOutAnimatorListener = new ToastOutAnimatorListener(this.mPresenter, this.mCallback, runnable);
            this.mToastOutAnimatorListener = toastOutAnimatorListener;
            outAnimation.addListener(toastOutAnimatorListener);
            outAnimation.start();
        } else {
            this.mPresenter.hide(this.mCallback);
            if (runnable != null) {
                runnable.run();
            }
        }
        this.mToast = null;
        this.mPresenter = null;
        this.mCallback = null;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        int i = configuration.orientation;
        if (i != this.mOrientation) {
            this.mOrientation = i;
            SystemUIToast systemUIToast = this.mToast;
            if (systemUIToast != null) {
                ToastLogger toastLogger = this.mToastLogger;
                String charSequence = systemUIToast.mText.toString();
                boolean z = true;
                if (this.mOrientation != 1) {
                    z = false;
                }
                toastLogger.logOrientationChange(charSequence, z);
                this.mToast.onOrientationChange(this.mOrientation);
                this.mPresenter.updateLayoutParams(this.mToast.getXOffset().intValue(), this.mToast.getYOffset().intValue(), (float) this.mToast.getHorizontalMargin().intValue(), (float) this.mToast.getVerticalMargin().intValue(), this.mToast.getGravity().intValue());
            }
        }
    }

    class ToastOutAnimatorListener extends AnimatorListenerAdapter {
        final ITransientNotificationCallback mPrevCallback;
        final ToastPresenter mPrevPresenter;
        Runnable mShowNextToastRunnable;

        ToastOutAnimatorListener(ToastPresenter toastPresenter, ITransientNotificationCallback iTransientNotificationCallback, Runnable runnable) {
            this.mPrevPresenter = toastPresenter;
            this.mPrevCallback = iTransientNotificationCallback;
            this.mShowNextToastRunnable = runnable;
        }

        /* access modifiers changed from: package-private */
        public void setShowNextToastRunnable(Runnable runnable) {
            this.mShowNextToastRunnable = runnable;
        }

        public void onAnimationEnd(Animator animator) {
            this.mPrevPresenter.hide(this.mPrevCallback);
            Runnable runnable = this.mShowNextToastRunnable;
            if (runnable != null) {
                runnable.run();
            }
            ToastOutAnimatorListener unused = ToastUI.this.mToastOutAnimatorListener = null;
        }
    }

    public void showToastForDisplay(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback, int i3) {
        ToastUI$$ExternalSyntheticLambda0 toastUI$$ExternalSyntheticLambda0 = new ToastUI$$ExternalSyntheticLambda0(this, i, i3, charSequence, str, iTransientNotificationCallback, iBinder, iBinder2, i2);
        ToastOutAnimatorListener toastOutAnimatorListener = this.mToastOutAnimatorListener;
        if (toastOutAnimatorListener != null) {
            toastOutAnimatorListener.setShowNextToastRunnable(toastUI$$ExternalSyntheticLambda0);
        } else if (this.mPresenter != null) {
            hideCurrentToast(toastUI$$ExternalSyntheticLambda0);
        } else {
            toastUI$$ExternalSyntheticLambda0.run();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showToastForDisplay$1(int i, int i2, CharSequence charSequence, String str, ITransientNotificationCallback iTransientNotificationCallback, IBinder iBinder, IBinder iBinder2, int i3) {
        String str2 = str;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i);
        Display display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(i2);
        if (display != null) {
            Context createDisplayContext = this.mContext.createContextAsUser(UserHandle.getUserHandleForUid(i), 0).createDisplayContext(display);
            SystemUIToast createToastForDisplay = this.mToastFactory.createToastForDisplay(createDisplayContext, charSequence, str, userHandleForUid.getIdentifier(), this.mOrientation);
            this.mToast = createToastForDisplay;
            if (createToastForDisplay.getInAnimation() != null) {
                this.mToast.getInAnimation().start();
            }
            this.mCallback = iTransientNotificationCallback;
            ToastPresenter toastPresenter = new ToastPresenter(createDisplayContext, this.mIAccessibilityManager, this.mNotificationManager, str2);
            this.mPresenter = toastPresenter;
            toastPresenter.getLayoutParams().setTrustedOverlay();
            this.mToastLogger.logOnShowToast(i, str2, charSequence.toString(), iBinder.toString());
            this.mPresenter.show(this.mToast.getView(), iBinder, iBinder2, i3, this.mToast.getGravity().intValue(), this.mToast.getXOffset().intValue(), this.mToast.getYOffset().intValue(), (float) this.mToast.getHorizontalMargin().intValue(), (float) this.mToast.getVerticalMargin().intValue(), this.mCallback, this.mToast.hasCustomAnimation());
        }
    }
}
