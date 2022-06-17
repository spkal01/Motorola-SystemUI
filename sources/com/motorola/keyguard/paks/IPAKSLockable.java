package com.motorola.keyguard.paks;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IPAKSLockable extends IInterface {
    void lock(String str) throws RemoteException;

    void permanentLock(String str) throws RemoteException;

    void unlock() throws RemoteException;

    public static abstract class Stub extends Binder implements IPAKSLockable {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.keyguard.paks.IPAKSLockable");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.motorola.keyguard.paks.IPAKSLockable");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.motorola.keyguard.paks.IPAKSLockable");
                lock(parcel.readString());
                parcel2.writeNoException();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.motorola.keyguard.paks.IPAKSLockable");
                unlock();
                parcel2.writeNoException();
                return true;
            } else if (i != 3) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.motorola.keyguard.paks.IPAKSLockable");
                permanentLock(parcel.readString());
                parcel2.writeNoException();
                return true;
            }
        }
    }
}
