package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.util.Log;
import com.android.settingslib.SignalIcon$IconGroup;
import com.android.settingslib.SignalIcon$State;
import com.android.systemui.statusbar.policy.NetworkController;
import java.io.PrintWriter;
import java.util.BitSet;

public abstract class SignalController<T extends SignalIcon$State, I extends SignalIcon$IconGroup> {
    protected static final boolean CHATTY = NetworkControllerImpl.CHATTY;
    protected static final boolean DEBUG = NetworkControllerImpl.DEBUG;
    private final CallbackHandler mCallbackHandler;
    protected final Context mContext;
    protected final T mCurrentState = cleanState();
    private final SignalIcon$State[] mHistory = new SignalIcon$State[64];
    private int mHistoryIndex;
    protected final T mLastState = cleanState();
    protected final NetworkControllerImpl mNetworkController;
    protected final String mTag;
    protected final int mTransportType;

    /* access modifiers changed from: protected */
    public abstract T cleanState();

    public abstract void notifyListeners(NetworkController.SignalCallback signalCallback);

    public SignalController(String str, Context context, int i, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl) {
        this.mTag = "NetworkController." + str;
        this.mNetworkController = networkControllerImpl;
        this.mTransportType = i;
        this.mContext = context;
        this.mCallbackHandler = callbackHandler;
        for (int i2 = 0; i2 < 64; i2++) {
            this.mHistory[i2] = cleanState();
        }
    }

    public T getState() {
        return this.mCurrentState;
    }

    public void updateConnectivity(BitSet bitSet, BitSet bitSet2) {
        this.mCurrentState.inetCondition = bitSet2.get(this.mTransportType) ? 1 : 0;
        notifyListenersIfNecessary();
    }

    public void resetLastState() {
        this.mCurrentState.copyFrom(this.mLastState);
    }

    public boolean isDirty() {
        if (this.mLastState.equals(this.mCurrentState)) {
            return false;
        }
        if (!DEBUG) {
            return true;
        }
        String str = this.mTag;
        Log.d(str, "Change in state from: " + this.mLastState + "\n\tto: " + this.mCurrentState);
        return true;
    }

    public void saveLastState() {
        recordLastState();
        this.mCurrentState.time = System.currentTimeMillis();
        this.mLastState.copyFrom(this.mCurrentState);
    }

    public int getQsCurrentIconId() {
        T t = this.mCurrentState;
        if (t.connected) {
            int[][] iArr = getIcons().qsIcons;
            T t2 = this.mCurrentState;
            return iArr[t2.inetCondition][t2.level];
        } else if (t.enabled) {
            return getIcons().qsDiscState;
        } else {
            return getIcons().qsNullState;
        }
    }

    public int getCurrentIconId() {
        T t = this.mCurrentState;
        if (t.connected) {
            int[][] iArr = getIcons().sbIcons;
            T t2 = this.mCurrentState;
            return iArr[t2.inetCondition][t2.level];
        } else if (t.enabled) {
            return getIcons().sbDiscState;
        } else {
            return getIcons().sbNullState;
        }
    }

    public int getContentDescription() {
        if (!this.mCurrentState.connected) {
            return getIcons().discContentDesc;
        }
        int length = getIcons().contentDesc.length - 1;
        int[] iArr = getIcons().contentDesc;
        int i = this.mCurrentState.level;
        if (i <= length) {
            length = i;
        }
        return iArr[length];
    }

    public void notifyListenersIfNecessary() {
        if (isDirty()) {
            saveLastState();
            notifyListeners();
        }
    }

    /* access modifiers changed from: protected */
    public final void notifyCallStateChange(NetworkController.IconState iconState, int i) {
        this.mCallbackHandler.setCallIndicator(iconState, i);
    }

    /* access modifiers changed from: package-private */
    public CharSequence getTextIfExists(int i) {
        return i != 0 ? this.mContext.getText(i) : "";
    }

    /* access modifiers changed from: protected */
    public I getIcons() {
        return this.mCurrentState.iconGroup;
    }

    /* access modifiers changed from: protected */
    public void recordLastState() {
        this.mHistory[this.mHistoryIndex].copyFrom(this.mLastState);
        this.mHistoryIndex = (this.mHistoryIndex + 1) % 64;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  - " + this.mTag + " -----");
        StringBuilder sb = new StringBuilder();
        sb.append("  Current State: ");
        sb.append(this.mCurrentState);
        printWriter.println(sb.toString());
        int i = 0;
        for (int i2 = 0; i2 < 64; i2++) {
            if (this.mHistory[i2].time != 0) {
                i++;
            }
        }
        int i3 = this.mHistoryIndex + 64;
        while (true) {
            i3--;
            if (i3 >= (this.mHistoryIndex + 64) - i) {
                printWriter.println("  Previous State(" + ((this.mHistoryIndex + 64) - i3) + "): " + this.mHistory[i3 & 63]);
            } else {
                return;
            }
        }
    }

    public final void notifyListeners() {
        notifyListeners(this.mCallbackHandler);
    }
}
