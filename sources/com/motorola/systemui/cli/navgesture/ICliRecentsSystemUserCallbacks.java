package com.motorola.systemui.cli.navgesture;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICliRecentsSystemUserCallbacks extends IInterface {
    void registerNonSystemUserCallbacks(IBinder iBinder, int i) throws RemoteException;

    public static abstract class Stub extends Binder implements ICliRecentsSystemUserCallbacks {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks");
        }

        public static ICliRecentsSystemUserCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ICliRecentsSystemUserCallbacks)) {
                return new Proxy(iBinder);
            }
            return (ICliRecentsSystemUserCallbacks) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks");
                return true;
            } else if (i != 1) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks");
                registerNonSystemUserCallbacks(parcel.readStrongBinder(), parcel.readInt());
                return true;
            }
        }

        private static class Proxy implements ICliRecentsSystemUserCallbacks {
            public static ICliRecentsSystemUserCallbacks sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void registerNonSystemUserCallbacks(IBinder iBinder, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.systemui.cli.navgesture.ICliRecentsSystemUserCallbacks");
                    obtain.writeStrongBinder(iBinder);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().registerNonSystemUserCallbacks(iBinder, i);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static ICliRecentsSystemUserCallbacks getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
