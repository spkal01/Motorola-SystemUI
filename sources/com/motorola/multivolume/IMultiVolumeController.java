package com.motorola.multivolume;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IMultiVolumeController extends IInterface {
    void appRowsChanged(List<AppVolumeState> list) throws RemoteException;

    void multiVolumeRowsChanged(int i, double d, List<AppVolumeState> list) throws RemoteException;

    void musicRowChanged(int i, double d) throws RemoteException;

    void safeMediaVolumeHandled(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements IMultiVolumeController {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.multivolume.IMultiVolumeController");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.motorola.multivolume.IMultiVolumeController");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.motorola.multivolume.IMultiVolumeController");
                musicRowChanged(parcel.readInt(), parcel.readDouble());
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.motorola.multivolume.IMultiVolumeController");
                appRowsChanged(parcel.createTypedArrayList(AppVolumeState.CREATOR));
                return true;
            } else if (i == 3) {
                parcel.enforceInterface("com.motorola.multivolume.IMultiVolumeController");
                multiVolumeRowsChanged(parcel.readInt(), parcel.readDouble(), parcel.createTypedArrayList(AppVolumeState.CREATOR));
                return true;
            } else if (i != 4) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.motorola.multivolume.IMultiVolumeController");
                safeMediaVolumeHandled(parcel.readInt());
                return true;
            }
        }
    }
}
