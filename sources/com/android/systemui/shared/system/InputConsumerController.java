package com.android.systemui.shared.system;

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
import android.view.WindowManagerGlobal;

public class InputConsumerController {
    private static final String TAG = "InputConsumerController";
    private int mDisplayId;
    private InputEventReceiver mInputEventReceiver;
    /* access modifiers changed from: private */
    public InputListener mListener;
    private final String mName;
    private RegistrationListener mRegistrationListener;
    private final IBinder mToken;
    private final IWindowManager mWindowManager;

    public interface InputListener {
        boolean onInputEvent(InputEvent inputEvent);
    }

    public interface RegistrationListener {
        void onRegistrationChanged(boolean z);
    }

    private final class InputEventReceiver extends BatchedInputEventReceiver {
        InputEventReceiver(InputChannel inputChannel, Looper looper, Choreographer choreographer) {
            super(inputChannel, looper, choreographer);
        }

        public void onInputEvent(InputEvent inputEvent) {
            boolean z = true;
            try {
                if (InputConsumerController.this.mListener != null) {
                    z = InputConsumerController.this.mListener.onInputEvent(inputEvent);
                }
            } finally {
                finishInputEvent(inputEvent, z);
            }
        }
    }

    public InputConsumerController(IWindowManager iWindowManager, String str) {
        this.mDisplayId = 0;
        this.mWindowManager = iWindowManager;
        this.mToken = new Binder();
        this.mName = str;
    }

    public InputConsumerController(IWindowManager iWindowManager, String str, int i) {
        this(iWindowManager, str);
        this.mDisplayId = i;
    }

    public static InputConsumerController getRecentsAnimationInputConsumer(int i) {
        return new InputConsumerController(WindowManagerGlobal.getWindowManagerService(), "recents_animation_input_consumer", i);
    }

    public void setInputListener(InputListener inputListener) {
        this.mListener = inputListener;
    }

    public void registerInputConsumer() {
        registerInputConsumer(false);
    }

    public void registerInputConsumer(boolean z) {
        if (this.mInputEventReceiver == null) {
            InputChannel inputChannel = new InputChannel();
            try {
                this.mWindowManager.destroyInputConsumer(this.mName, this.mDisplayId);
                this.mWindowManager.createInputConsumer(this.mToken, this.mName, this.mDisplayId, inputChannel);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to create input consumer", e);
            }
            this.mInputEventReceiver = new InputEventReceiver(inputChannel, Looper.myLooper(), z ? Choreographer.getSfInstance() : Choreographer.getInstance());
            RegistrationListener registrationListener = this.mRegistrationListener;
            if (registrationListener != null) {
                registrationListener.onRegistrationChanged(true);
            }
        }
    }
}
