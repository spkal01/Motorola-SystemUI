package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.service.controls.Control;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$loadForComponent$2$accept$1 implements Runnable {
    final /* synthetic */ ComponentName $componentName;
    final /* synthetic */ List<Control> $controls;
    final /* synthetic */ Consumer<ControlsController.LoadData> $dataCallback;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$loadForComponent$2$accept$1(ComponentName componentName, List<Control> list, ControlsControllerImpl controlsControllerImpl, Consumer<ControlsController.LoadData> consumer) {
        this.$componentName = componentName;
        this.$controls = list;
        this.this$0 = controlsControllerImpl;
        this.$dataCallback = consumer;
    }

    public final void run() {
        List<ControlInfo> controlsForComponent = Favorites.INSTANCE.getControlsForComponent(this.$componentName);
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controlsForComponent, 10));
        for (ControlInfo controlId : controlsForComponent) {
            arrayList.add(controlId.getControlId());
        }
        Favorites favorites = Favorites.INSTANCE;
        if (favorites.updateControls(this.$componentName, this.$controls)) {
            this.this$0.persistenceWrapper.storeFavorites(favorites.getAllStructures());
        }
        Set access$findRemoved = this.this$0.findRemoved(CollectionsKt___CollectionsKt.toSet(arrayList), this.$controls);
        List<Control> list = this.$controls;
        ComponentName componentName = this.$componentName;
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (Control control : list) {
            arrayList2.add(new ControlStatus(control, componentName, arrayList.contains(control.getControlId()), false, 8, (DefaultConstructorMarker) null));
        }
        ArrayList arrayList3 = new ArrayList();
        List<StructureInfo> structuresForComponent = Favorites.INSTANCE.getStructuresForComponent(this.$componentName);
        ControlsControllerImpl controlsControllerImpl = this.this$0;
        ComponentName componentName2 = this.$componentName;
        for (StructureInfo structureInfo : structuresForComponent) {
            for (ControlInfo controlInfo : structureInfo.getControls()) {
                if (access$findRemoved.contains(controlInfo.getControlId())) {
                    arrayList3.add(ControlsControllerImpl.createRemovedStatus$default(controlsControllerImpl, componentName2, controlInfo, structureInfo.getStructure(), false, 8, (Object) null));
                }
            }
        }
        this.$dataCallback.accept(ControlsControllerKt.createLoadDataObject$default(CollectionsKt___CollectionsKt.plus(arrayList3, arrayList2), arrayList, false, 4, (Object) null));
    }
}
