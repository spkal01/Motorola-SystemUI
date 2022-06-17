package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks;
import com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks;

public class CliNavGestureSystemUser extends ICliRecentsSystemUserCallbacks.Stub {
    private Context mContext;
    private CliNavGestureImpl mImpl;
    /* access modifiers changed from: private */
    public final SparseArray<ICliRecentsNotSystemUserCallbacks> mNonSystemUserRecents = new SparseArray<>();

    public CliNavGestureSystemUser(Context context, CliNavGestureImpl cliNavGestureImpl) {
        this.mContext = context;
        this.mImpl = cliNavGestureImpl;
    }

    public void registerNonSystemUserCallbacks(IBinder iBinder, int i) {
        try {
            final ICliRecentsNotSystemUserCallbacks asInterface = ICliRecentsNotSystemUserCallbacks.Stub.asInterface(iBinder);
            iBinder.linkToDeath(new IBinder.DeathRecipient() {
                public void binderDied() {
                    CliNavGestureSystemUser.this.mNonSystemUserRecents.removeAt(CliNavGestureSystemUser.this.mNonSystemUserRecents.indexOfValue(asInterface));
                    Log.i("CliNavGestureSystemUser", "binderDied remove callback");
                }
            }, 0);
            this.mNonSystemUserRecents.put(i, asInterface);
        } catch (RemoteException e) {
            Log.e("CliNavGestureSystemUser", "Failed to register NonSystemUserCallbacks", e);
        }
    }

    public ICliRecentsNotSystemUserCallbacks getNonSystemUserNavGestureForUser(int i) {
        return this.mNonSystemUserRecents.get(i);
    }
}
