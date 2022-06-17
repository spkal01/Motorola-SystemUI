package com.motorola.screenshot.gifmaker;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.WindowManager;
import com.android.systemui.R$string;
import com.motorola.screenshot.gifmaker.aidl.IGifMakerPrivacyDialogCallback;
import com.motorola.screenshot.gifmaker.aidl.IGifMakerService;

public class GifMakerService extends Service {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public static final String TAG = GifMakerService.class.getSimpleName();
    /* access modifiers changed from: private */
    public Handler mMainThreadHandler;
    /* access modifiers changed from: private */
    public boolean mUserHasChosen;

    public void onCreate() {
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public IBinder onBind(Intent intent) {
        return new IGifMakerService.Stub() {
            public void showPrivacyDialog(IGifMakerPrivacyDialogCallback iGifMakerPrivacyDialogCallback) {
                if (GifMakerService.DEBUG) {
                    Log.d(GifMakerService.TAG, "showPrivacyDialog");
                }
                GifMakerService.this.ensureCallerIsAllowed();
                GifMakerService.this.mMainThreadHandler.post(new GifMakerService$1$$ExternalSyntheticLambda0(this, iGifMakerPrivacyDialogCallback));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$showPrivacyDialog$0(IGifMakerPrivacyDialogCallback iGifMakerPrivacyDialogCallback) {
                GifMakerService.this.buildPrivacyDialog(iGifMakerPrivacyDialogCallback);
            }

            public Bitmap acquireScreenshot(Rect rect, int i, int i2, int i3) {
                if (GifMakerService.DEBUG) {
                    Log.d(GifMakerService.TAG, "acquireScreenshot");
                }
                GifMakerService.this.ensureCallerIsAllowed();
                return GifMakerService.this.takeScreenshot(rect, i, i2, i3);
            }
        };
    }

    /* access modifiers changed from: private */
    public void ensureCallerIsAllowed() {
        String nameForUid = getPackageManager().getNameForUid(Binder.getCallingUid());
        boolean isSystemApp = isSystemApp(nameForUid);
        if (!"com.motorola.screenshoteditor".equals(nameForUid) || !isSystemApp) {
            throw new SecurityException("Caller is NOT allowed: " + nameForUid + " System package: " + isSystemApp);
        }
    }

    private boolean isSystemApp(String str) {
        boolean z = false;
        try {
            if ((getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0) {
                z = true;
            }
            if (DEBUG) {
                Log.d(TAG, "isSystemApp systemApp = " + z);
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(TAG, "isSystemApp NameNotFoundException pkg = " + str);
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void buildPrivacyDialog(final IGifMakerPrivacyDialogCallback iGifMakerPrivacyDialogCallback) {
        this.mUserHasChosen = false;
        AlertDialog create = new AlertDialog.Builder(getApplicationContext()).setTitle(R$string.screenrecord_start_label).setMessage(R$string.screenrecord_description).setPositiveButton(R$string.screenrecord_start, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                GifMakerService.this.handleUserChoice(dialogInterface, i, iGifMakerPrivacyDialogCallback);
            }
        }).setNegativeButton(R$string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                GifMakerService.this.handleUserChoice(dialogInterface, i, iGifMakerPrivacyDialogCallback);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                if (!GifMakerService.this.mUserHasChosen) {
                    GifMakerService.this.handleUserChoice(dialogInterface, -2, iGifMakerPrivacyDialogCallback);
                }
            }
        }).create();
        WindowManager.LayoutParams attributes = create.getWindow().getAttributes();
        attributes.layoutInDisplayCutoutMode = 3;
        create.getWindow().setAttributes(attributes);
        create.getWindow().setType(2008);
        create.show();
    }

    /* access modifiers changed from: private */
    public void handleUserChoice(DialogInterface dialogInterface, int i, IGifMakerPrivacyDialogCallback iGifMakerPrivacyDialogCallback) {
        if (i == -1) {
            try {
                if (DEBUG) {
                    Log.d(TAG, "PrivacyDialog: user accepted");
                }
                iGifMakerPrivacyDialogCallback.onUserAccepted();
            } catch (RemoteException e) {
                String str = TAG;
                Log.e(str, "RemoteException when calling back: " + e);
                dialogInterface.dismiss();
                if (i == -1) {
                    return;
                }
            } catch (Throwable th) {
                dialogInterface.dismiss();
                if (i != -1) {
                    stopSelf();
                }
                throw th;
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "PrivacyDialog: user declined");
            }
            iGifMakerPrivacyDialogCallback.onUserDeclined();
        }
        this.mUserHasChosen = true;
        dialogInterface.dismiss();
        if (i == -1) {
            return;
        }
        stopSelf();
    }

    /* access modifiers changed from: private */
    public Bitmap takeScreenshot(Rect rect, int i, int i2, int i3) {
        Bitmap bitmap;
        SurfaceControl.ScreenshotHardwareBuffer captureDisplay = SurfaceControl.captureDisplay(new SurfaceControl.DisplayCaptureArgs.Builder(SurfaceControl.getInternalDisplayToken()).setSourceCrop(rect).setSize(i, i2).setUseIdentityTransform(false).build());
        if (captureDisplay == null) {
            bitmap = null;
        } else {
            bitmap = captureDisplay.asBitmap();
        }
        if (bitmap != null) {
            bitmap.setHasAlpha(false);
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            bitmap.recycle();
            if (copy == null) {
                Log.e(TAG, "Failure on bitmap copying!");
            }
            return copy;
        }
        Log.e(TAG, "Failure on taking screenshot!");
        return null;
    }
}
