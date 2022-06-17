package com.android.p011wm.shell.startingsurface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.p011wm.shell.startingsurface.IStartingWindowListener;

/* renamed from: com.android.wm.shell.startingsurface.IStartingWindow */
public interface IStartingWindow extends IInterface {
    void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException;

    /* renamed from: com.android.wm.shell.startingsurface.IStartingWindow$Stub */
    public static abstract class Stub extends Binder implements IStartingWindow {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.wm.shell.startingsurface.IStartingWindow");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.android.wm.shell.startingsurface.IStartingWindow");
                return true;
            } else if (i != 44) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.android.wm.shell.startingsurface.IStartingWindow");
                setStartingWindowListener(IStartingWindowListener.Stub.asInterface(parcel.readStrongBinder()));
                return true;
            }
        }
    }
}
