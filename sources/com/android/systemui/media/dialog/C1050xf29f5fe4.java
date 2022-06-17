package com.android.systemui.media.dialog;

import android.view.View;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.media.dialog.PRCMediaOutputAdapter;

/* renamed from: com.android.systemui.media.dialog.PRCMediaOutputAdapter$MediaDeviceViewHolder$$ExternalSyntheticLambda3 */
public final /* synthetic */ class C1050xf29f5fe4 implements View.OnClickListener {
    public final /* synthetic */ PRCMediaOutputAdapter.MediaDeviceViewHolder f$0;
    public final /* synthetic */ MediaDevice f$1;

    public /* synthetic */ C1050xf29f5fe4(PRCMediaOutputAdapter.MediaDeviceViewHolder mediaDeviceViewHolder, MediaDevice mediaDevice) {
        this.f$0 = mediaDeviceViewHolder;
        this.f$1 = mediaDevice;
    }

    public final void onClick(View view) {
        this.f$0.lambda$onBind$1(this.f$1, view);
    }
}
