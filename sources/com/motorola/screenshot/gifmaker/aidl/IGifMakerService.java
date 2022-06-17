package com.motorola.screenshot.gifmaker.aidl;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.motorola.screenshot.gifmaker.aidl.IGifMakerPrivacyDialogCallback;

public interface IGifMakerService extends IInterface {
    Bitmap acquireScreenshot(Rect rect, int i, int i2, int i3) throws RemoteException;

    void showPrivacyDialog(IGifMakerPrivacyDialogCallback iGifMakerPrivacyDialogCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IGifMakerService {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.motorola.screenshot.gifmaker.aidl.IGifMakerService");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString("com.motorola.screenshot.gifmaker.aidl.IGifMakerService");
                return true;
            } else if (i == 1) {
                parcel.enforceInterface("com.motorola.screenshot.gifmaker.aidl.IGifMakerService");
                showPrivacyDialog(IGifMakerPrivacyDialogCallback.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            } else if (i != 2) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel.enforceInterface("com.motorola.screenshot.gifmaker.aidl.IGifMakerService");
                Bitmap acquireScreenshot = acquireScreenshot(parcel.readInt() != 0 ? (Rect) Rect.CREATOR.createFromParcel(parcel) : null, parcel.readInt(), parcel.readInt(), parcel.readInt());
                parcel2.writeNoException();
                if (acquireScreenshot != null) {
                    parcel2.writeInt(1);
                    acquireScreenshot.writeToParcel(parcel2, 1);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            }
        }
    }
}
