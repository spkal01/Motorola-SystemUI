package com.android.systemui.media;

import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.Utils;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaResumeListener.kt */
public final class MediaResumeListener$setManager$1 implements TunerService.Tunable {
    final /* synthetic */ MediaResumeListener this$0;

    MediaResumeListener$setManager$1(MediaResumeListener mediaResumeListener) {
        this.this$0 = mediaResumeListener;
    }

    public void onTuningChanged(@Nullable String str, @Nullable String str2) {
        MediaResumeListener mediaResumeListener = this.this$0;
        mediaResumeListener.useMediaResumption = Utils.useMediaResumption(mediaResumeListener.context);
        MediaDataManager access$getMediaDataManager$p = this.this$0.mediaDataManager;
        if (access$getMediaDataManager$p != null) {
            access$getMediaDataManager$p.setMediaResumptionEnabled(this.this$0.useMediaResumption);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("mediaDataManager");
            throw null;
        }
    }
}
