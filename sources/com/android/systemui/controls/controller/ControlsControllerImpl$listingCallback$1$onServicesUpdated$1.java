package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.systemui.controls.ControlsServiceInfo;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsControllerImpl.kt */
final class ControlsControllerImpl$listingCallback$1$onServicesUpdated$1 implements Runnable {
    final /* synthetic */ List<ControlsServiceInfo> $serviceInfos;
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$listingCallback$1$onServicesUpdated$1(List<ControlsServiceInfo> list, ControlsControllerImpl controlsControllerImpl) {
        this.$serviceInfos = list;
        this.this$0 = controlsControllerImpl;
    }

    public final void run() {
        List<ControlsServiceInfo> list = this.$serviceInfos;
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (ControlsServiceInfo controlsServiceInfo : list) {
            arrayList.add(controlsServiceInfo.componentName);
        }
        Set<ComponentName> set = CollectionsKt___CollectionsKt.toSet(arrayList);
        List<StructureInfo> allStructures = Favorites.INSTANCE.getAllStructures();
        ArrayList arrayList2 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(allStructures, 10));
        for (StructureInfo componentName : allStructures) {
            arrayList2.add(componentName.getComponentName());
        }
        Set set2 = CollectionsKt___CollectionsKt.toSet(arrayList2);
        boolean z = false;
        SharedPreferences sharedPreferences = this.this$0.userStructure.getUserContext().getSharedPreferences("controls_prefs", 0);
        Set<String> stringSet = sharedPreferences.getStringSet("SeedingCompleted", new LinkedHashSet());
        ArrayList arrayList3 = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(set, 10));
        for (ComponentName packageName : set) {
            arrayList3.add(packageName.getPackageName());
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        Intrinsics.checkNotNullExpressionValue(stringSet, "completedSeedingPackageSet");
        edit.putStringSet("SeedingCompleted", CollectionsKt___CollectionsKt.intersect(stringSet, arrayList3)).apply();
        Set<ComponentName> subtract = CollectionsKt___CollectionsKt.subtract(set2, set);
        ControlsControllerImpl controlsControllerImpl = this.this$0;
        for (ComponentName componentName2 : subtract) {
            Favorites favorites = Favorites.INSTANCE;
            Intrinsics.checkNotNullExpressionValue(componentName2, "it");
            favorites.removeStructures(componentName2);
            controlsControllerImpl.bindingController.onComponentRemoved(componentName2);
            z = true;
        }
        if (!this.this$0.mo12498x4fd0e26a().getFavorites().isEmpty()) {
            Set<ComponentName> subtract2 = CollectionsKt___CollectionsKt.subtract(set, set2);
            ControlsControllerImpl controlsControllerImpl2 = this.this$0;
            for (ComponentName componentName3 : subtract2) {
                AuxiliaryPersistenceWrapper auxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core = controlsControllerImpl2.mo12498x4fd0e26a();
                Intrinsics.checkNotNullExpressionValue(componentName3, "it");
                List<StructureInfo> cachedFavoritesAndRemoveFor = auxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core.getCachedFavoritesAndRemoveFor(componentName3);
                if (!cachedFavoritesAndRemoveFor.isEmpty()) {
                    for (StructureInfo replaceControls : cachedFavoritesAndRemoveFor) {
                        Favorites.INSTANCE.replaceControls(replaceControls);
                    }
                    z = true;
                }
            }
            Set<ComponentName> intersect = CollectionsKt___CollectionsKt.intersect(set, set2);
            ControlsControllerImpl controlsControllerImpl3 = this.this$0;
            for (ComponentName componentName4 : intersect) {
                AuxiliaryPersistenceWrapper auxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core2 = controlsControllerImpl3.mo12498x4fd0e26a();
                Intrinsics.checkNotNullExpressionValue(componentName4, "it");
                auxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core2.getCachedFavoritesAndRemoveFor(componentName4);
            }
        }
        if (z) {
            Log.d("ControlsControllerImpl", "Detected change in available services, storing updated favorites");
            this.this$0.persistenceWrapper.storeFavorites(Favorites.INSTANCE.getAllStructures());
        }
    }
}
