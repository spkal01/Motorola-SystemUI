package com.android.systemui.media;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.media.MediaDescription;
import android.media.session.MediaSession;
import android.util.Log;
import com.android.systemui.media.ResumeMediaBrowser;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: MediaResumeListener.kt */
public final class MediaResumeListener$mediaBrowserCallback$1 extends ResumeMediaBrowser.Callback {
    final /* synthetic */ MediaResumeListener this$0;

    MediaResumeListener$mediaBrowserCallback$1(MediaResumeListener mediaResumeListener) {
        this.this$0 = mediaResumeListener;
    }

    public void addTrack(@NotNull MediaDescription mediaDescription, @NotNull ComponentName componentName, @NotNull ResumeMediaBrowser resumeMediaBrowser) {
        Intrinsics.checkNotNullParameter(mediaDescription, "desc");
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(resumeMediaBrowser, "browser");
        MediaSession.Token token = resumeMediaBrowser.getToken();
        PendingIntent appIntent = resumeMediaBrowser.getAppIntent();
        PackageManager packageManager = this.this$0.context.getPackageManager();
        CharSequence packageName = componentName.getPackageName();
        Intrinsics.checkNotNullExpressionValue(packageName, "component.packageName");
        Runnable access$getResumeAction = this.this$0.getResumeAction(componentName);
        try {
            CharSequence applicationLabel = packageManager.getApplicationLabel(packageManager.getApplicationInfo(componentName.getPackageName(), 0));
            Intrinsics.checkNotNullExpressionValue(applicationLabel, "pm.getApplicationLabel(\n                        pm.getApplicationInfo(component.packageName, 0))");
            packageName = applicationLabel;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MediaResumeListener", "Error getting package information", e);
        }
        Log.d("MediaResumeListener", Intrinsics.stringPlus("Adding resume controls ", mediaDescription));
        MediaDataManager access$getMediaDataManager$p = this.this$0.mediaDataManager;
        if (access$getMediaDataManager$p != null) {
            int access$getCurrentUserId$p = this.this$0.currentUserId;
            Intrinsics.checkNotNullExpressionValue(token, "token");
            String obj = packageName.toString();
            Intrinsics.checkNotNullExpressionValue(appIntent, "appIntent");
            String packageName2 = componentName.getPackageName();
            Intrinsics.checkNotNullExpressionValue(packageName2, "component.packageName");
            access$getMediaDataManager$p.addResumptionControls(access$getCurrentUserId$p, mediaDescription, access$getResumeAction, token, obj, appIntent, packageName2);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mediaDataManager");
        throw null;
    }
}
