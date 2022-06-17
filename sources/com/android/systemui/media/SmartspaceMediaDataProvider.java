package com.android.systemui.media;

import android.app.smartspace.SmartspaceTarget;
import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SmartspaceMediaDataProvider.kt */
public final class SmartspaceMediaDataProvider implements BcSmartspaceDataPlugin {
    @NotNull
    private final List<BcSmartspaceDataPlugin.SmartspaceTargetListener> smartspaceMediaTargetListeners = new ArrayList();
    @NotNull
    private List<SmartspaceTarget> smartspaceMediaTargets = CollectionsKt__CollectionsKt.emptyList();

    public void registerListener(@NotNull BcSmartspaceDataPlugin.SmartspaceTargetListener smartspaceTargetListener) {
        Intrinsics.checkNotNullParameter(smartspaceTargetListener, "smartspaceTargetListener");
        this.smartspaceMediaTargetListeners.add(smartspaceTargetListener);
    }

    public void unregisterListener(@Nullable BcSmartspaceDataPlugin.SmartspaceTargetListener smartspaceTargetListener) {
        List<BcSmartspaceDataPlugin.SmartspaceTargetListener> list = this.smartspaceMediaTargetListeners;
        Objects.requireNonNull(list, "null cannot be cast to non-null type kotlin.collections.MutableCollection<T>");
        TypeIntrinsics.asMutableCollection(list).remove(smartspaceTargetListener);
    }

    public void onTargetsAvailable(@NotNull List<SmartspaceTarget> list) {
        Intrinsics.checkNotNullParameter(list, "targets");
        ArrayList arrayList = new ArrayList();
        for (SmartspaceTarget next : list) {
            if (next.getFeatureType() == 15) {
                arrayList.add(next);
            }
        }
        this.smartspaceMediaTargets = arrayList;
        for (BcSmartspaceDataPlugin.SmartspaceTargetListener onSmartspaceTargetsUpdated : this.smartspaceMediaTargetListeners) {
            onSmartspaceTargetsUpdated.onSmartspaceTargetsUpdated(this.smartspaceMediaTargets);
        }
    }
}
