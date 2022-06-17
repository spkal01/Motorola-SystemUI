package com.android.systemui.media;

import android.content.ComponentName;
import android.media.MediaDescription;
import android.util.Log;
import com.android.systemui.media.ResumeMediaBrowser;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MediaResumeListener.kt */
public final class MediaResumeListener$tryUpdateResumptionList$1 extends ResumeMediaBrowser.Callback {
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ String $key;
    final /* synthetic */ MediaResumeListener this$0;

    MediaResumeListener$tryUpdateResumptionList$1(ComponentName componentName, MediaResumeListener mediaResumeListener, String str) {
        this.$componentName = componentName;
        this.this$0 = mediaResumeListener;
        this.$key = str;
    }

    public void onConnected() {
        Log.d("MediaResumeListener", Intrinsics.stringPlus("Connected to ", this.$componentName));
    }

    public void onError() {
        Log.e("MediaResumeListener", Intrinsics.stringPlus("Cannot resume with ", this.$componentName));
        this.this$0.mediaBrowser = null;
    }

    public void addTrack(@NotNull MediaDescription mediaDescription, @NotNull ComponentName componentName, @NotNull ResumeMediaBrowser resumeMediaBrowser) {
        Intrinsics.checkNotNullParameter(mediaDescription, "desc");
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(resumeMediaBrowser, "browser");
        Log.d("MediaResumeListener", Intrinsics.stringPlus("Can get resumable media from ", this.$componentName));
        MediaDataManager access$getMediaDataManager$p = this.this$0.mediaDataManager;
        if (access$getMediaDataManager$p != null) {
            access$getMediaDataManager$p.setResumeAction(this.$key, this.this$0.getResumeAction(this.$componentName));
            this.this$0.updateResumptionList(this.$componentName);
            this.this$0.mediaBrowser = null;
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mediaDataManager");
        throw null;
    }
}
