package com.android.systemui.shared.recents;

import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.view.MotionEvent;
import com.android.systemui.shared.recents.model.Task;

public interface ISystemUiProxy extends IInterface {
    void expandNotificationPanel() throws RemoteException;

    Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException;

    @Deprecated
    void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException;

    void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException;

    void notifyAccessibilityButtonClicked(int i) throws RemoteException;

    void notifyAccessibilityButtonLongClicked() throws RemoteException;

    void notifyPrioritizedRotation(int i) throws RemoteException;

    void notifySwipeToHomeFinished() throws RemoteException;

    void notifySwipeUpGestureStarted() throws RemoteException;

    void onAssistantGestureCompletion(float f) throws RemoteException;

    void onAssistantProgress(float f) throws RemoteException;

    void onBackPressed() throws RemoteException;

    void onOverviewShown(boolean z) throws RemoteException;

    void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void setHomeRotationEnabled(boolean z) throws RemoteException;

    void setNavBarButtonAlpha(float f, boolean z) throws RemoteException;

    void setSplitScreenMinimized(boolean z) throws RemoteException;

    void startAssistant(Bundle bundle) throws RemoteException;

    void startScreenPinning(int i) throws RemoteException;

    void stopScreenPinning() throws RemoteException;

    public static abstract class Stub extends Binder implements ISystemUiProxy {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.systemui.shared.recents.ISystemUiProxy");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: android.view.MotionEvent} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: android.os.Bundle} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: com.android.systemui.shared.recents.model.Task$TaskKey} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: android.graphics.Insets} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v14 */
        /* JADX WARNING: type inference failed for: r3v15 */
        /* JADX WARNING: type inference failed for: r3v16 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r6, android.os.Parcel r7, android.os.Parcel r8, int r9) throws android.os.RemoteException {
            /*
                r5 = this;
                r0 = 1
                java.lang.String r1 = "com.android.systemui.shared.recents.ISystemUiProxy"
                r2 = 1598968902(0x5f4e5446, float:1.4867585E19)
                if (r6 == r2) goto L_0x01d0
                r2 = 2
                if (r6 == r2) goto L_0x01c2
                r2 = 10
                r3 = 0
                if (r6 == r2) goto L_0x01a9
                r2 = 26
                if (r6 == r2) goto L_0x019b
                r2 = 7
                r4 = 0
                if (r6 == r2) goto L_0x018a
                r2 = 8
                if (r6 == r2) goto L_0x0173
                r2 = 13
                if (r6 == r2) goto L_0x0165
                r2 = 14
                if (r6 == r2) goto L_0x014c
                r2 = 29
                if (r6 == r2) goto L_0x0103
                r2 = 30
                if (r6 == r2) goto L_0x00f9
                switch(r6) {
                    case 16: goto L_0x00eb;
                    case 17: goto L_0x00e1;
                    case 18: goto L_0x00d7;
                    case 19: goto L_0x00c9;
                    case 20: goto L_0x00b4;
                    default: goto L_0x002f;
                }
            L_0x002f:
                switch(r6) {
                    case 22: goto L_0x0077;
                    case 23: goto L_0x0066;
                    case 24: goto L_0x005c;
                    default: goto L_0x0032;
                }
            L_0x0032:
                switch(r6) {
                    case 45: goto L_0x0052;
                    case 46: goto L_0x0041;
                    case 47: goto L_0x003a;
                    default: goto L_0x0035;
                }
            L_0x0035:
                boolean r5 = super.onTransact(r6, r7, r8, r9)
                return r5
            L_0x003a:
                r7.enforceInterface(r1)
                r5.notifySwipeUpGestureStarted()
                return r0
            L_0x0041:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x004b
                r4 = r0
            L_0x004b:
                r5.setHomeRotationEnabled(r4)
                r8.writeNoException()
                return r0
            L_0x0052:
                r7.enforceInterface(r1)
                r5.onBackPressed()
                r8.writeNoException()
                return r0
            L_0x005c:
                r7.enforceInterface(r1)
                r5.notifySwipeToHomeFinished()
                r8.writeNoException()
                return r0
            L_0x0066:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0070
                r4 = r0
            L_0x0070:
                r5.setSplitScreenMinimized(r4)
                r8.writeNoException()
                return r0
            L_0x0077:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0089
                android.os.Parcelable$Creator r6 = android.graphics.Bitmap.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                android.graphics.Bitmap r6 = (android.graphics.Bitmap) r6
                goto L_0x008a
            L_0x0089:
                r6 = r3
            L_0x008a:
                int r9 = r7.readInt()
                if (r9 == 0) goto L_0x0099
                android.os.Parcelable$Creator r9 = android.graphics.Rect.CREATOR
                java.lang.Object r9 = r9.createFromParcel(r7)
                android.graphics.Rect r9 = (android.graphics.Rect) r9
                goto L_0x009a
            L_0x0099:
                r9 = r3
            L_0x009a:
                int r1 = r7.readInt()
                if (r1 == 0) goto L_0x00a9
                android.os.Parcelable$Creator r1 = android.graphics.Insets.CREATOR
                java.lang.Object r1 = r1.createFromParcel(r7)
                r3 = r1
                android.graphics.Insets r3 = (android.graphics.Insets) r3
            L_0x00a9:
                int r7 = r7.readInt()
                r5.handleImageAsScreenshot(r6, r9, r3, r7)
                r8.writeNoException()
                return r0
            L_0x00b4:
                r7.enforceInterface(r1)
                float r6 = r7.readFloat()
                int r7 = r7.readInt()
                if (r7 == 0) goto L_0x00c2
                r4 = r0
            L_0x00c2:
                r5.setNavBarButtonAlpha(r6, r4)
                r8.writeNoException()
                return r0
            L_0x00c9:
                r7.enforceInterface(r1)
                float r6 = r7.readFloat()
                r5.onAssistantGestureCompletion(r6)
                r8.writeNoException()
                return r0
            L_0x00d7:
                r7.enforceInterface(r1)
                r5.stopScreenPinning()
                r8.writeNoException()
                return r0
            L_0x00e1:
                r7.enforceInterface(r1)
                r5.notifyAccessibilityButtonLongClicked()
                r8.writeNoException()
                return r0
            L_0x00eb:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                r5.notifyAccessibilityButtonClicked(r6)
                r8.writeNoException()
                return r0
            L_0x00f9:
                r7.enforceInterface(r1)
                r5.expandNotificationPanel()
                r8.writeNoException()
                return r0
            L_0x0103:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0115
                android.os.Parcelable$Creator r6 = android.os.Bundle.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                android.os.Bundle r6 = (android.os.Bundle) r6
                goto L_0x0116
            L_0x0115:
                r6 = r3
            L_0x0116:
                int r9 = r7.readInt()
                if (r9 == 0) goto L_0x0125
                android.os.Parcelable$Creator r9 = android.graphics.Rect.CREATOR
                java.lang.Object r9 = r9.createFromParcel(r7)
                android.graphics.Rect r9 = (android.graphics.Rect) r9
                goto L_0x0126
            L_0x0125:
                r9 = r3
            L_0x0126:
                int r1 = r7.readInt()
                if (r1 == 0) goto L_0x0135
                android.os.Parcelable$Creator r1 = android.graphics.Insets.CREATOR
                java.lang.Object r1 = r1.createFromParcel(r7)
                android.graphics.Insets r1 = (android.graphics.Insets) r1
                goto L_0x0136
            L_0x0135:
                r1 = r3
            L_0x0136:
                int r2 = r7.readInt()
                if (r2 == 0) goto L_0x0145
                android.os.Parcelable$Creator<com.android.systemui.shared.recents.model.Task$TaskKey> r2 = com.android.systemui.shared.recents.model.Task.TaskKey.CREATOR
                java.lang.Object r7 = r2.createFromParcel(r7)
                r3 = r7
                com.android.systemui.shared.recents.model.Task$TaskKey r3 = (com.android.systemui.shared.recents.model.Task.TaskKey) r3
            L_0x0145:
                r5.handleImageBundleAsScreenshot(r6, r9, r1, r3)
                r8.writeNoException()
                return r0
            L_0x014c:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x015e
                android.os.Parcelable$Creator r6 = android.os.Bundle.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                r3 = r6
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x015e:
                r5.startAssistant(r3)
                r8.writeNoException()
                return r0
            L_0x0165:
                r7.enforceInterface(r1)
                float r6 = r7.readFloat()
                r5.onAssistantProgress(r6)
                r8.writeNoException()
                return r0
            L_0x0173:
                r7.enforceInterface(r1)
                android.graphics.Rect r5 = r5.getNonMinimizedSplitScreenSecondaryBounds()
                r8.writeNoException()
                if (r5 == 0) goto L_0x0186
                r8.writeInt(r0)
                r5.writeToParcel(r8, r0)
                goto L_0x0189
            L_0x0186:
                r8.writeInt(r4)
            L_0x0189:
                return r0
            L_0x018a:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x0194
                r4 = r0
            L_0x0194:
                r5.onOverviewShown(r4)
                r8.writeNoException()
                return r0
            L_0x019b:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                r5.notifyPrioritizedRotation(r6)
                r8.writeNoException()
                return r0
            L_0x01a9:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                if (r6 == 0) goto L_0x01bb
                android.os.Parcelable$Creator r6 = android.view.MotionEvent.CREATOR
                java.lang.Object r6 = r6.createFromParcel(r7)
                r3 = r6
                android.view.MotionEvent r3 = (android.view.MotionEvent) r3
            L_0x01bb:
                r5.onStatusBarMotionEvent(r3)
                r8.writeNoException()
                return r0
            L_0x01c2:
                r7.enforceInterface(r1)
                int r6 = r7.readInt()
                r5.startScreenPinning(r6)
                r8.writeNoException()
                return r0
            L_0x01d0:
                r8.writeString(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.shared.recents.ISystemUiProxy.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }
}
