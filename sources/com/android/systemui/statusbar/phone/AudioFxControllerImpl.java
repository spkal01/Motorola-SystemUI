package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.statusbar.phone.AudioFxController;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class AudioFxControllerImpl implements AudioFxController {
    private final ContentObserver mAudioFXSettingObserver;
    private final Uri mAudioFxUri;
    /* access modifiers changed from: private */
    @GuardedBy({"mCallbacks"})
    public final CopyOnWriteArrayList<AudioFxController.Callback> mCallbacks = new CopyOnWriteArrayList<>();
    private final Context mContext;
    private final Handler mHandler;
    private boolean mIsAudioFxAvailable = false;
    private boolean mIsEnabled = false;

    public AudioFxControllerImpl(Context context) {
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mAudioFXSettingObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                AudioFxControllerImpl.this.updateAudioFxState();
                Iterator it = AudioFxControllerImpl.this.mCallbacks.iterator();
                while (it.hasNext()) {
                    ((AudioFxController.Callback) it.next()).onAudioFxChanged();
                }
            }
        };
        this.mContext = context;
        this.mAudioFxUri = getAudioFxPreferenceDataUri();
    }

    public boolean isAudioFxAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("com.motorola.software.audiofx");
    }

    public boolean isAudioFxEnabled() {
        return this.mIsEnabled;
    }

    public void addCallback(AudioFxController.Callback callback) {
        this.mCallbacks.add(callback);
        if (this.mCallbacks.size() == 1) {
            setListening(true);
            updateAudioFxState();
        }
        callback.onAudioFxChanged();
    }

    public void removeCallback(AudioFxController.Callback callback) {
        if (this.mCallbacks.remove(callback) && this.mCallbacks.size() == 0) {
            setListening(false);
        }
    }

    private Uri getAudioFxPreferenceDataUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority("com.motorola.audioeffects.audiofxdynamicprefprovider");
        builder.appendPath("state");
        builder.appendPath("moto_sound");
        return builder.build();
    }

    private void setListening(boolean z) {
        if (z) {
            try {
                this.mContext.getContentResolver().registerContentObserver(this.mAudioFxUri, false, this.mAudioFXSettingObserver, -1);
            } catch (Exception unused) {
                Log.e("AudioFxControllerImpl", "Register audiofx provider uri failed.");
            }
        } else {
            this.mContext.getContentResolver().unregisterContentObserver(this.mAudioFXSettingObserver);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0040, code lost:
        if (r2 != null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0042, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004d, code lost:
        if (r2 == null) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0050, code lost:
        r10.mIsEnabled = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0052, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAudioFxState() {
        /*
            r10 = this;
            java.lang.String r0 = "AudioFxControllerImpl"
            r1 = 0
            r2 = 0
            android.content.Context r3 = r10.mContext     // Catch:{ Exception -> 0x0048 }
            android.content.ContentResolver r4 = r3.getContentResolver()     // Catch:{ Exception -> 0x0048 }
            android.net.Uri r5 = r10.mAudioFxUri     // Catch:{ Exception -> 0x0048 }
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            android.database.Cursor r2 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0048 }
            if (r2 == 0) goto L_0x0040
            boolean r3 = r2.moveToFirst()     // Catch:{ Exception -> 0x0048 }
            if (r3 == 0) goto L_0x0040
            java.lang.String r3 = "value"
            int r3 = r2.getColumnIndex(r3)     // Catch:{ Exception -> 0x0048 }
            if (r3 < 0) goto L_0x0040
            int r3 = r2.getInt(r3)     // Catch:{ Exception -> 0x0048 }
            r4 = 1
            if (r4 != r3) goto L_0x002c
            r1 = r4
        L_0x002c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0048 }
            r3.<init>()     // Catch:{ Exception -> 0x0048 }
            java.lang.String r4 = "Audio Fx state changed: "
            r3.append(r4)     // Catch:{ Exception -> 0x0048 }
            r3.append(r1)     // Catch:{ Exception -> 0x0048 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0048 }
            android.util.Log.d(r0, r3)     // Catch:{ Exception -> 0x0048 }
        L_0x0040:
            if (r2 == 0) goto L_0x0050
        L_0x0042:
            r2.close()
            goto L_0x0050
        L_0x0046:
            r10 = move-exception
            goto L_0x0053
        L_0x0048:
            java.lang.String r3 = "Failed to get cursor value"
            android.util.Log.d(r0, r3)     // Catch:{ all -> 0x0046 }
            if (r2 == 0) goto L_0x0050
            goto L_0x0042
        L_0x0050:
            r10.mIsEnabled = r1
            return
        L_0x0053:
            if (r2 == 0) goto L_0x0058
            r2.close()
        L_0x0058:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.AudioFxControllerImpl.updateAudioFxState():void");
    }
}
