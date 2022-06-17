package com.android.systemui.accessibility;

import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.accessibility.IRemoteMagnificationAnimationCallback;
import android.view.accessibility.IWindowMagnificationConnection;
import android.view.accessibility.IWindowMagnificationConnectionCallback;

class WindowMagnificationConnectionImpl extends IWindowMagnificationConnection.Stub {
    private IWindowMagnificationConnectionCallback mConnectionCallback;
    private final Handler mHandler;
    private final ModeSwitchesController mModeSwitchesController;
    private final WindowMagnification mWindowMagnification;

    WindowMagnificationConnectionImpl(WindowMagnification windowMagnification, Handler handler, ModeSwitchesController modeSwitchesController) {
        this.mWindowMagnification = windowMagnification;
        this.mHandler = handler;
        this.mModeSwitchesController = modeSwitchesController;
    }

    public void enableWindowMagnification(int i, float f, float f2, float f3, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) {
        this.mHandler.post(new WindowMagnificationConnectionImpl$$ExternalSyntheticLambda3(this, i, f, f2, f3, iRemoteMagnificationAnimationCallback));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$enableWindowMagnification$0(int i, float f, float f2, float f3, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) {
        this.mWindowMagnification.enableWindowMagnification(i, f, f2, f3, iRemoteMagnificationAnimationCallback);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setScale$1(int i, float f) {
        this.mWindowMagnification.setScale(i, f);
    }

    public void setScale(int i, float f) {
        this.mHandler.post(new WindowMagnificationConnectionImpl$$ExternalSyntheticLambda1(this, i, f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$disableWindowMagnification$2(int i, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) {
        this.mWindowMagnification.disableWindowMagnification(i, iRemoteMagnificationAnimationCallback);
    }

    public void disableWindowMagnification(int i, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) {
        this.mHandler.post(new WindowMagnificationConnectionImpl$$ExternalSyntheticLambda5(this, i, iRemoteMagnificationAnimationCallback));
    }

    public void moveWindowMagnifier(int i, float f, float f2) {
        this.mHandler.post(new WindowMagnificationConnectionImpl$$ExternalSyntheticLambda2(this, i, f, f2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$moveWindowMagnifier$3(int i, float f, float f2) {
        this.mWindowMagnification.moveWindowMagnifier(i, f, f2);
    }

    public void showMagnificationButton(int i, int i2) {
        this.mHandler.post(new WindowMagnificationConnectionImpl$$ExternalSyntheticLambda4(this, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMagnificationButton$4(int i, int i2) {
        this.mModeSwitchesController.showButton(i, i2);
    }

    public void removeMagnificationButton(int i) {
        this.mHandler.post(new WindowMagnificationConnectionImpl$$ExternalSyntheticLambda0(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeMagnificationButton$5(int i) {
        this.mModeSwitchesController.removeButton(i);
    }

    public void setConnectionCallback(IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback) {
        this.mConnectionCallback = iWindowMagnificationConnectionCallback;
    }

    /* access modifiers changed from: package-private */
    public void onWindowMagnifierBoundsChanged(int i, Rect rect) {
        IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback = this.mConnectionCallback;
        if (iWindowMagnificationConnectionCallback != null) {
            try {
                iWindowMagnificationConnectionCallback.onWindowMagnifierBoundsChanged(i, rect);
            } catch (RemoteException e) {
                Log.e("WindowMagnificationConnectionImpl", "Failed to inform bounds changed", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onSourceBoundsChanged(int i, Rect rect) {
        IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback = this.mConnectionCallback;
        if (iWindowMagnificationConnectionCallback != null) {
            try {
                iWindowMagnificationConnectionCallback.onSourceBoundsChanged(i, rect);
            } catch (RemoteException e) {
                Log.e("WindowMagnificationConnectionImpl", "Failed to inform source bounds changed", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onPerformScaleAction(int i, float f) {
        IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback = this.mConnectionCallback;
        if (iWindowMagnificationConnectionCallback != null) {
            try {
                iWindowMagnificationConnectionCallback.onPerformScaleAction(i, f);
            } catch (RemoteException e) {
                Log.e("WindowMagnificationConnectionImpl", "Failed to inform performing scale action", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onAccessibilityActionPerformed(int i) {
        IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback = this.mConnectionCallback;
        if (iWindowMagnificationConnectionCallback != null) {
            try {
                iWindowMagnificationConnectionCallback.onAccessibilityActionPerformed(i);
            } catch (RemoteException e) {
                Log.e("WindowMagnificationConnectionImpl", "Failed to inform an accessibility action is already performed", e);
            }
        }
    }
}
