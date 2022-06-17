package com.android.p011wm.shell.pip.phone;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.BatchedInputEventReceiver;
import android.view.Choreographer;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.InputEvent;
import com.android.p011wm.shell.common.ShellExecutor;
import java.io.PrintWriter;

/* renamed from: com.android.wm.shell.pip.phone.PipInputConsumer */
public class PipInputConsumer {
    private static final String TAG = "PipInputConsumer";
    private InputEventReceiver mInputEventReceiver;
    /* access modifiers changed from: private */
    public InputListener mListener;
    private final ShellExecutor mMainExecutor;
    private final String mName;
    private RegistrationListener mRegistrationListener;
    private final IBinder mToken;
    private final IWindowManager mWindowManager;
    private int mXRVDDisplayId;
    private boolean mXrvdFeatureEnabled = false;

    /* renamed from: com.android.wm.shell.pip.phone.PipInputConsumer$InputListener */
    public interface InputListener {
        boolean onInputEvent(InputEvent inputEvent);
    }

    /* renamed from: com.android.wm.shell.pip.phone.PipInputConsumer$RegistrationListener */
    public interface RegistrationListener {
        void onRegistrationChanged(boolean z);
    }

    /* renamed from: com.android.wm.shell.pip.phone.PipInputConsumer$InputEventReceiver */
    private final class InputEventReceiver extends BatchedInputEventReceiver {
        InputEventReceiver(InputChannel inputChannel, Looper looper, Choreographer choreographer) {
            super(inputChannel, looper, choreographer);
        }

        public void onInputEvent(InputEvent inputEvent) {
            boolean z = true;
            try {
                if (PipInputConsumer.this.mListener != null) {
                    z = PipInputConsumer.this.mListener.onInputEvent(inputEvent);
                }
            } finally {
                finishInputEvent(inputEvent, z);
            }
        }
    }

    public PipInputConsumer(IWindowManager iWindowManager, String str, ShellExecutor shellExecutor) {
        this.mWindowManager = iWindowManager;
        this.mToken = new Binder();
        this.mName = str;
        this.mMainExecutor = shellExecutor;
    }

    public void SetXrvdFeatureEnabled(Context context) {
        if (context != null) {
            this.mXrvdFeatureEnabled = context.getResources().getBoolean(17891589);
        }
    }

    public void setInputListener(InputListener inputListener) {
        this.mListener = inputListener;
    }

    public void setRegistrationListener(RegistrationListener registrationListener) {
        this.mRegistrationListener = registrationListener;
        this.mMainExecutor.execute(new PipInputConsumer$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setRegistrationListener$0() {
        RegistrationListener registrationListener = this.mRegistrationListener;
        if (registrationListener != null) {
            registrationListener.onRegistrationChanged(this.mInputEventReceiver != null);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        if (r2.ownerPackageName.equals("com.qualcomm.qti.xrvd.service") != false) goto L_0x003f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x007b A[Catch:{ RemoteException -> 0x0095 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void registerInputConsumer() {
        /*
            r6 = this;
            boolean r0 = r6.mXrvdFeatureEnabled
            r1 = 0
            if (r0 == 0) goto L_0x0040
            java.lang.String r0 = TAG
            java.lang.String r2 = "registerInputConsumer enter"
            android.util.Log.d(r0, r2)
            android.view.IWindowManager r0 = r6.mWindowManager     // Catch:{ RemoteException -> 0x0034 }
            int r0 = r0.getXrvdFocusedDisplayId()     // Catch:{ RemoteException -> 0x0034 }
            android.hardware.display.DisplayManagerGlobal r2 = android.hardware.display.DisplayManagerGlobal.getInstance()     // Catch:{ RemoteException -> 0x0032 }
            android.view.DisplayInfo r2 = r2.getDisplayInfo(r0)     // Catch:{ RemoteException -> 0x0032 }
            if (r2 == 0) goto L_0x0040
            int r3 = r2.type     // Catch:{ RemoteException -> 0x0032 }
            r4 = 5
            if (r3 != r4) goto L_0x0040
            int r3 = r2.ownerUid     // Catch:{ RemoteException -> 0x0032 }
            r4 = 1000(0x3e8, float:1.401E-42)
            if (r3 != r4) goto L_0x0040
            java.lang.String r2 = r2.ownerPackageName     // Catch:{ RemoteException -> 0x0032 }
            java.lang.String r3 = "com.qualcomm.qti.xrvd.service"
            boolean r2 = r2.equals(r3)     // Catch:{ RemoteException -> 0x0032 }
            if (r2 != 0) goto L_0x003f
            goto L_0x0040
        L_0x0032:
            r1 = move-exception
            goto L_0x0038
        L_0x0034:
            r0 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
        L_0x0038:
            java.lang.String r2 = TAG
            java.lang.String r3 = "Failed to get display id "
            android.util.Log.e(r2, r3, r1)
        L_0x003f:
            r1 = r0
        L_0x0040:
            com.android.wm.shell.pip.phone.PipInputConsumer$InputEventReceiver r0 = r6.mInputEventReceiver
            if (r0 == 0) goto L_0x0072
            boolean r0 = r6.mXrvdFeatureEnabled
            if (r0 == 0) goto L_0x0071
            java.lang.String r0 = TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "registerInputConsumer input receiver not null: "
            r2.append(r3)
            int r3 = r6.mXRVDDisplayId
            r2.append(r3)
            java.lang.String r3 = " top:"
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            android.util.Log.d(r0, r2)
            int r0 = r6.mXRVDDisplayId
            if (r1 != r0) goto L_0x006d
            return
        L_0x006d:
            r6.unregisterInputConsumer()
            goto L_0x0072
        L_0x0071:
            return
        L_0x0072:
            android.view.InputChannel r0 = new android.view.InputChannel
            r0.<init>()
            boolean r2 = r6.mXrvdFeatureEnabled     // Catch:{ RemoteException -> 0x0095 }
            if (r2 == 0) goto L_0x0082
            java.lang.String r2 = TAG     // Catch:{ RemoteException -> 0x0095 }
            java.lang.String r3 = "registerInputConsumer default display"
            android.util.Log.d(r2, r3)     // Catch:{ RemoteException -> 0x0095 }
        L_0x0082:
            android.view.IWindowManager r2 = r6.mWindowManager     // Catch:{ RemoteException -> 0x0095 }
            java.lang.String r3 = r6.mName     // Catch:{ RemoteException -> 0x0095 }
            r2.destroyInputConsumer(r3, r1)     // Catch:{ RemoteException -> 0x0095 }
            android.view.IWindowManager r2 = r6.mWindowManager     // Catch:{ RemoteException -> 0x0095 }
            android.os.IBinder r3 = r6.mToken     // Catch:{ RemoteException -> 0x0095 }
            java.lang.String r4 = r6.mName     // Catch:{ RemoteException -> 0x0095 }
            r2.createInputConsumer(r3, r4, r1, r0)     // Catch:{ RemoteException -> 0x0095 }
            r6.mXRVDDisplayId = r1     // Catch:{ RemoteException -> 0x0095 }
            goto L_0x009d
        L_0x0095:
            r1 = move-exception
            java.lang.String r2 = TAG
            java.lang.String r3 = "Failed to create input consumer"
            android.util.Log.e(r2, r3, r1)
        L_0x009d:
            com.android.wm.shell.common.ShellExecutor r1 = r6.mMainExecutor
            com.android.wm.shell.pip.phone.PipInputConsumer$$ExternalSyntheticLambda2 r2 = new com.android.wm.shell.pip.phone.PipInputConsumer$$ExternalSyntheticLambda2
            r2.<init>(r6, r0)
            r1.execute(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.pip.phone.PipInputConsumer.registerInputConsumer():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$registerInputConsumer$1(InputChannel inputChannel) {
        this.mInputEventReceiver = new InputEventReceiver(inputChannel, Looper.myLooper(), Choreographer.getSfInstance());
        RegistrationListener registrationListener = this.mRegistrationListener;
        if (registrationListener != null) {
            registrationListener.onRegistrationChanged(true);
        }
    }

    public void unregisterInputConsumer() {
        if (this.mXrvdFeatureEnabled) {
            Log.d(TAG, "unregisterInputConsumer enter");
        }
        if (this.mInputEventReceiver != null) {
            int i = 0;
            try {
                if (this.mXrvdFeatureEnabled) {
                    Log.d(TAG, "unregisterInputConsumer default display");
                    i = this.mXRVDDisplayId;
                }
                this.mWindowManager.destroyInputConsumer(this.mName, i);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to destroy input consumer", e);
            }
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
            this.mMainExecutor.execute(new PipInputConsumer$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$unregisterInputConsumer$2() {
        RegistrationListener registrationListener = this.mRegistrationListener;
        if (registrationListener != null) {
            registrationListener.onRegistrationChanged(false);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        printWriter.println(str + TAG);
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("registered=");
        sb.append(this.mInputEventReceiver != null);
        printWriter.println(sb.toString());
    }
}
