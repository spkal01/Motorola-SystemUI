package com.android.systemui.media;

import android.app.smartspace.SmartspaceSession;
import android.app.smartspace.SmartspaceTarget;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MediaDataManager.kt */
final class MediaDataManager$2$1 implements SmartspaceSession.OnTargetsAvailableListener {
    final /* synthetic */ MediaDataManager this$0;

    MediaDataManager$2$1(MediaDataManager mediaDataManager) {
        this.this$0 = mediaDataManager;
    }

    public final void onTargetsAvailable(List<SmartspaceTarget> list) {
        SmartspaceMediaDataProvider access$getSmartspaceMediaDataProvider$p = this.this$0.smartspaceMediaDataProvider;
        Intrinsics.checkNotNullExpressionValue(list, "targets");
        access$getSmartspaceMediaDataProvider$p.onTargetsAvailable(list);
    }
}
