package com.android.p011wm.shell.onehanded;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* renamed from: com.android.wm.shell.onehanded.IOneHanded */
public interface IOneHanded extends IInterface {
    void startOneHanded() throws RemoteException;

    void stopOneHanded() throws RemoteException;

    /* renamed from: com.android.wm.shell.onehanded.IOneHanded$Stub */
    public static abstract class Stub extends Binder implements IOneHanded {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.wm.shell.onehanded.IOneHanded");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.android.wm.shell.onehanded.IOneHanded");
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.android.wm.shell.onehanded.IOneHanded");
                startOneHanded();
                return true;
            } else if (i != 3) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.android.wm.shell.onehanded.IOneHanded");
                stopOneHanded();
                return true;
            }
        }
    }
}
