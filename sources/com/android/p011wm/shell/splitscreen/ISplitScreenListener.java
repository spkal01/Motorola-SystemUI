package com.android.p011wm.shell.splitscreen;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* renamed from: com.android.wm.shell.splitscreen.ISplitScreenListener */
public interface ISplitScreenListener extends IInterface {
    void onStagePositionChanged(int i, int i2) throws RemoteException;

    void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException;

    /* renamed from: com.android.wm.shell.splitscreen.ISplitScreenListener$Stub */
    public static abstract class Stub extends Binder implements ISplitScreenListener {
        public static ISplitScreenListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.wm.shell.splitscreen.ISplitScreenListener");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISplitScreenListener)) {
                return new Proxy(iBinder);
            }
            return (ISplitScreenListener) queryLocalInterface;
        }

        /* renamed from: com.android.wm.shell.splitscreen.ISplitScreenListener$Stub$Proxy */
        private static class Proxy implements ISplitScreenListener {
            public static ISplitScreenListener sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onStagePositionChanged(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wm.shell.splitscreen.ISplitScreenListener");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onStagePositionChanged(i, i2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.wm.shell.splitscreen.ISplitScreenListener");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(z ? 1 : 0);
                    if (this.mRemote.transact(2, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onTaskStageChanged(i, i2, z);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static ISplitScreenListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
