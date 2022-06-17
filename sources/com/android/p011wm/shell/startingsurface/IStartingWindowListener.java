package com.android.p011wm.shell.startingsurface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* renamed from: com.android.wm.shell.startingsurface.IStartingWindowListener */
public interface IStartingWindowListener extends IInterface {
    void onTaskLaunching(int i, int i2, int i3) throws RemoteException;

    /* renamed from: com.android.wm.shell.startingsurface.IStartingWindowListener$Stub */
    public static abstract class Stub extends Binder implements IStartingWindowListener {
        public static IStartingWindowListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.wm.shell.startingsurface.IStartingWindowListener");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IStartingWindowListener)) {
                return new Proxy(iBinder);
            }
            return (IStartingWindowListener) queryLocalInterface;
        }

        /* renamed from: com.android.wm.shell.startingsurface.IStartingWindowListener$Stub$Proxy */
        private static class Proxy implements IStartingWindowListener {
            public static IStartingWindowListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onTaskLaunching(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wm.shell.startingsurface.IStartingWindowListener");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onTaskLaunching(i, i2, i3);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static IStartingWindowListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
