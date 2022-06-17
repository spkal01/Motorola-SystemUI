package com.android.p011wm.shell.pip;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* renamed from: com.android.wm.shell.pip.IPipAnimationListener */
public interface IPipAnimationListener extends IInterface {
    void onPipAnimationStarted() throws RemoteException;

    void onPipCornerRadiusChanged(int i) throws RemoteException;

    /* renamed from: com.android.wm.shell.pip.IPipAnimationListener$Stub */
    public static abstract class Stub extends Binder implements IPipAnimationListener {
        public static IPipAnimationListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.wm.shell.pip.IPipAnimationListener");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IPipAnimationListener)) {
                return new Proxy(iBinder);
            }
            return (IPipAnimationListener) queryLocalInterface;
        }

        /* renamed from: com.android.wm.shell.pip.IPipAnimationListener$Stub$Proxy */
        private static class Proxy implements IPipAnimationListener {
            public static IPipAnimationListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onPipAnimationStarted() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wm.shell.pip.IPipAnimationListener");
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onPipAnimationStarted();
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onPipCornerRadiusChanged(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wm.shell.pip.IPipAnimationListener");
                    obtain.writeInt(i);
                    if (this.mRemote.transact(2, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onPipCornerRadiusChanged(i);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static IPipAnimationListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
