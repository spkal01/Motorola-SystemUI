package com.motorola.systemui.cli.navgesture.animation;

import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import com.motorola.systemui.cli.navgesture.util.Utilities;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class MultiStateCallback {
    public static final boolean DEBUG_STATES = (!Build.IS_USER);
    private final SparseArray<LinkedList<Runnable>> mCallbacks = new SparseArray<>();
    private int mState = 0;
    private final SparseArray<ArrayList<Consumer<Boolean>>> mStateChangeListeners = new SparseArray<>();
    private final String[] mStateNames;

    public MultiStateCallback(String[] strArr) {
        this.mStateNames = !DEBUG_STATES ? null : strArr;
    }

    public void setStateOnUiThread(int i) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            lambda$setStateOnUiThread$0(i);
        } else {
            Utilities.postAsyncCallback(AppExecutors.m97ui().getHandler(), new MultiStateCallback$$ExternalSyntheticLambda0(this, i));
        }
    }

    /* renamed from: setState */
    public void lambda$setStateOnUiThread$0(int i) {
        if (DEBUG_STATES) {
            Log.i("MultiStateCallback", "[" + System.identityHashCode(this) + "] Adding " + convertToFlagNames(i) + " to " + convertToFlagNames(this.mState));
        }
        int i2 = this.mState;
        this.mState = i | i2;
        int size = this.mCallbacks.size();
        for (int i3 = 0; i3 < size; i3++) {
            int keyAt = this.mCallbacks.keyAt(i3);
            if ((this.mState & keyAt) == keyAt) {
                LinkedList valueAt = this.mCallbacks.valueAt(i3);
                while (!valueAt.isEmpty()) {
                    ((Runnable) valueAt.pollFirst()).run();
                }
            }
        }
        notifyStateChangeListeners(i2);
    }

    private void notifyStateChangeListeners(int i) {
        int size = this.mStateChangeListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            int keyAt = this.mStateChangeListeners.keyAt(i2);
            boolean z = true;
            boolean z2 = (keyAt & i) == keyAt;
            if ((this.mState & keyAt) != keyAt) {
                z = false;
            }
            if (z2 != z) {
                Iterator it = this.mStateChangeListeners.valueAt(i2).iterator();
                while (it.hasNext()) {
                    ((Consumer) it.next()).accept(Boolean.valueOf(z));
                }
            }
        }
    }

    public void runOnceAtState(int i, Runnable runnable) {
        LinkedList linkedList;
        if ((this.mState & i) == i) {
            runnable.run();
            return;
        }
        if (this.mCallbacks.indexOfKey(i) >= 0) {
            linkedList = this.mCallbacks.get(i);
        } else {
            LinkedList linkedList2 = new LinkedList();
            this.mCallbacks.put(i, linkedList2);
            linkedList = linkedList2;
        }
        linkedList.add(runnable);
    }

    public void addChangeListener(int i, Consumer<Boolean> consumer) {
        ArrayList arrayList;
        if (this.mStateChangeListeners.indexOfKey(i) >= 0) {
            arrayList = this.mStateChangeListeners.get(i);
        } else {
            ArrayList arrayList2 = new ArrayList();
            this.mStateChangeListeners.put(i, arrayList2);
            arrayList = arrayList2;
        }
        arrayList.add(consumer);
    }

    public int getState() {
        return this.mState;
    }

    public boolean hasStates(int i) {
        return (this.mState & i) == i;
    }

    private String convertToFlagNames(int i) {
        StringJoiner stringJoiner = new StringJoiner(", ", "[", " (" + i + ")]");
        int i2 = 0;
        while (true) {
            String[] strArr = this.mStateNames;
            if (i2 >= strArr.length) {
                return stringJoiner.toString();
            }
            if (((1 << i2) & i) != 0) {
                stringJoiner.add(strArr[i2]);
            }
            i2++;
        }
    }
}
