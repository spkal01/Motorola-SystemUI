package com.android.systemui.media.dialog;

import com.android.settingslib.media.MediaDevice;
import com.android.systemui.media.dialog.MediaOutputBaseAdapter;

/* renamed from: com.android.systemui.media.dialog.MediaOutputBaseAdapter$MediaDeviceBaseViewHolder$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C1044x9ea0902 implements Runnable {
    public final /* synthetic */ MediaOutputBaseAdapter.MediaDeviceBaseViewHolder f$0;
    public final /* synthetic */ MediaDevice f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ C1044x9ea0902(MediaOutputBaseAdapter.MediaDeviceBaseViewHolder mediaDeviceBaseViewHolder, MediaDevice mediaDevice, boolean z, boolean z2) {
        this.f$0 = mediaDeviceBaseViewHolder;
        this.f$1 = mediaDevice;
        this.f$2 = z;
        this.f$3 = z2;
    }

    public final void run() {
        this.f$0.lambda$onBind$1(this.f$1, this.f$2, this.f$3);
    }
}
