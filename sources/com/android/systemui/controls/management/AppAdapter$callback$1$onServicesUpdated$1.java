package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlsServiceInfo;
import java.text.Collator;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AppAdapter.kt */
final class AppAdapter$callback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ List<ControlsServiceInfo> $serviceInfos;
    final /* synthetic */ Executor $uiExecutor;
    final /* synthetic */ AppAdapter this$0;

    AppAdapter$callback$1$onServicesUpdated$1(AppAdapter appAdapter, List<ControlsServiceInfo> list, Executor executor) {
        this.this$0 = appAdapter;
        this.$serviceInfos = list;
        this.$uiExecutor = executor;
    }

    public final void run() {
        Collator instance = Collator.getInstance(this.this$0.resources.getConfiguration().getLocales().get(0));
        Intrinsics.checkNotNullExpressionValue(instance, "collator");
        this.this$0.listOfServices = CollectionsKt___CollectionsKt.sortedWith(this.$serviceInfos, new C0889xf045535a(instance));
        Executor executor = this.$uiExecutor;
        final AppAdapter appAdapter = this.this$0;
        executor.execute(new Runnable() {
            public final void run() {
                AppAdapter.this.notifyDataSetChanged();
            }
        });
    }
}
