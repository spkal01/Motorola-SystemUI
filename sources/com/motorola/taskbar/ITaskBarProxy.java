package com.motorola.taskbar;

import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ITaskBarProxy extends IInterface {
    Bundle getMobileState() throws RemoteException;

    int getUnreadNotificationCount(int i) throws RemoteException;

    boolean isMobileDataEnabled() throws RemoteException;

    void onTrackpadStateChanged(boolean z) throws RemoteException;

    void requestNavIcon(boolean z, int i) throws RemoteException;

    void requestNavTrackpadGuide(boolean z) throws RemoteException;

    void requestQSNPanel(int i, int i2) throws RemoteException;

    void requestSwitchVolumeDialog(int i, Rect rect) throws RemoteException;

    void setMobileDataEnabled(boolean z) throws RemoteException;

    void touchAutoHide(int i) throws RemoteException;

    public static abstract class Stub extends Binder implements ITaskBarProxy {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.taskbar.ITaskBarProxy");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1598968902) {
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        requestSwitchVolumeDialog(parcel.readInt(), parcel.readInt() != 0 ? (Rect) Rect.CREATOR.createFromParcel(parcel) : null);
                        parcel2.writeNoException();
                        return true;
                    case 2:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        boolean isMobileDataEnabled = isMobileDataEnabled();
                        parcel2.writeNoException();
                        parcel2.writeInt(isMobileDataEnabled ? 1 : 0);
                        return true;
                    case 3:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        setMobileDataEnabled(z);
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        Bundle mobileState = getMobileState();
                        parcel2.writeNoException();
                        if (mobileState != null) {
                            parcel2.writeInt(1);
                            mobileState.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 5:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        touchAutoHide(parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 6:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        requestQSNPanel(parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 7:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        requestNavIcon(z, parcel.readInt());
                        parcel2.writeNoException();
                        return true;
                    case 8:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        requestNavTrackpadGuide(z);
                        parcel2.writeNoException();
                        return true;
                    case 9:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        onTrackpadStateChanged(z);
                        parcel2.writeNoException();
                        return true;
                    case 10:
                        parcel.enforceInterface("com.motorola.taskbar.ITaskBarProxy");
                        int unreadNotificationCount = getUnreadNotificationCount(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(unreadNotificationCount);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("com.motorola.taskbar.ITaskBarProxy");
                return true;
            }
        }
    }
}
