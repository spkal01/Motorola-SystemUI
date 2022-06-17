package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;
import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$startSeeding$1$accept$1 implements Runnable {
    final /* synthetic */ Consumer<SeedResponse> $callback;
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ List<Control> $controls;
    final /* synthetic */ boolean $didAnyFail;
    final /* synthetic */ List<ComponentName> $remaining;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$startSeeding$1$accept$1(List<Control> list, ControlsControllerImpl controlsControllerImpl, Consumer<SeedResponse> consumer, ComponentName componentName, List<ComponentName> list2, boolean z) {
        this.$controls = list;
        this.this$0 = controlsControllerImpl;
        this.$callback = consumer;
        this.$componentName = componentName;
        this.$remaining = list2;
        this.$didAnyFail = z;
    }

    public final void run() {
        ArrayMap arrayMap = new ArrayMap();
        for (Control control : this.$controls) {
            Object structure = control.getStructure();
            if (structure == null) {
                structure = "";
            }
            List list = (List) arrayMap.get(structure);
            if (list == null) {
                list = new ArrayList();
            }
            if (list.size() < 6) {
                String controlId = control.getControlId();
                Intrinsics.checkNotNullExpressionValue(controlId, "it.controlId");
                CharSequence title = control.getTitle();
                Intrinsics.checkNotNullExpressionValue(title, "it.title");
                CharSequence subtitle = control.getSubtitle();
                Intrinsics.checkNotNullExpressionValue(subtitle, "it.subtitle");
                list.add(new ControlInfo(controlId, title, subtitle, control.getDeviceType()));
                arrayMap.put(structure, list);
            }
        }
        ComponentName componentName = this.$componentName;
        for (Map.Entry entry : arrayMap.entrySet()) {
            CharSequence charSequence = (CharSequence) entry.getKey();
            List list2 = (List) entry.getValue();
            Favorites favorites = Favorites.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(charSequence, "s");
            Intrinsics.checkNotNullExpressionValue(list2, "cs");
            favorites.replaceControls(new StructureInfo(componentName, charSequence, list2));
        }
        this.this$0.persistenceWrapper.storeFavorites(Favorites.INSTANCE.getAllStructures());
        Consumer<SeedResponse> consumer = this.$callback;
        String packageName = this.$componentName.getPackageName();
        Intrinsics.checkNotNullExpressionValue(packageName, "componentName.packageName");
        consumer.accept(new SeedResponse(packageName, true));
        this.this$0.startSeeding(this.$remaining, this.$callback, this.$didAnyFail);
    }
}
