package com.android.systemui.media.dialog;

import android.widget.CompoundButton;
import com.android.settingslib.media.MediaDevice;
import com.android.systemui.media.dialog.MediaOutputGroupAdapter;

/* renamed from: com.android.systemui.media.dialog.MediaOutputGroupAdapter$GroupViewHolder$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1046x1869e5b0 implements CompoundButton.OnCheckedChangeListener {
    public final /* synthetic */ MediaOutputGroupAdapter.GroupViewHolder f$0;
    public final /* synthetic */ MediaDevice f$1;

    public /* synthetic */ C1046x1869e5b0(MediaOutputGroupAdapter.GroupViewHolder groupViewHolder, MediaDevice mediaDevice) {
        this.f$0 = groupViewHolder;
        this.f$1 = mediaDevice;
    }

    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f$0.lambda$onBind$0(this.f$1, compoundButton, z);
    }
}
