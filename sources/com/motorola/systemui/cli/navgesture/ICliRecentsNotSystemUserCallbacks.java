package com.motorola.systemui.cli.navgesture;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ICliRecentsNotSystemUserCallbacks extends IInterface {
    void onOverviewToggle() throws RemoteException;

    void preloadOverView() throws RemoteException;

    void setSystemUiFlag(int i, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ICliRecentsNotSystemUserCallbacks {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
        }

        public static ICliRecentsNotSystemUserCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ICliRecentsNotSystemUserCallbacks)) {
                return new Proxy(iBinder);
            }
            return (ICliRecentsNotSystemUserCallbacks) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                onOverviewToggle();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                setSystemUiFlag(parcel.readInt(), parcel.readInt() != 0);
                return true;
            } else if (i != 3) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                preloadOverView();
                return true;
            }
        }

        private static class Proxy implements ICliRecentsNotSystemUserCallbacks {
            public static ICliRecentsNotSystemUserCallbacks sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onOverviewToggle() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onOverviewToggle();
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void setSystemUiFlag(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(2, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().setSystemUiFlag(i, z);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void preloadOverView() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.motorola.systemui.cli.navgesture.ICliRecentsNotSystemUserCallbacks");
                    if (this.mRemote.transact(3, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().preloadOverView();
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static ICliRecentsNotSystemUserCallbacks getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
