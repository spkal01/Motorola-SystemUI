package com.android.systemui.statusbar.policy;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.systemui.R$array;
import com.android.systemui.controls.ControlsServiceInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.dagger.ControlsComponent;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.policy.DeviceControlsController;
import com.android.systemui.util.settings.SecureSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: DeviceControlsControllerImpl.kt */
public final class DeviceControlsControllerImpl implements DeviceControlsController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @Nullable
    private DeviceControlsController.Callback callback;
    @NotNull
    private final Context context;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsComponent controlsComponent;
    /* access modifiers changed from: private */
    @NotNull
    public final DeviceControlsControllerImpl$listingCallback$1 listingCallback = new DeviceControlsControllerImpl$listingCallback$1(this);
    @Nullable
    private Integer position;
    @NotNull
    private final SecureSettings secureSettings;
    @NotNull
    private final UserContextProvider userContextProvider;

    public DeviceControlsControllerImpl(@NotNull Context context2, @NotNull ControlsComponent controlsComponent2, @NotNull UserContextProvider userContextProvider2, @NotNull SecureSettings secureSettings2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(controlsComponent2, "controlsComponent");
        Intrinsics.checkNotNullParameter(userContextProvider2, "userContextProvider");
        Intrinsics.checkNotNullParameter(secureSettings2, "secureSettings");
        this.context = context2;
        this.controlsComponent = controlsComponent2;
        this.userContextProvider = userContextProvider2;
        this.secureSettings = secureSettings2;
    }

    @Nullable
    /* renamed from: getPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final Integer mo23583x51e59865() {
        return this.position;
    }

    /* renamed from: setPosition$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final void mo23584xa7d03371(@Nullable Integer num) {
        this.position = num;
    }

    /* compiled from: DeviceControlsControllerImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    private final void checkMigrationToQs() {
        this.controlsComponent.getControlsController().ifPresent(new DeviceControlsControllerImpl$checkMigrationToQs$1(this));
    }

    public void setCallback(@NotNull DeviceControlsController.Callback callback2) {
        Intrinsics.checkNotNullParameter(callback2, "callback");
        removeCallback();
        this.callback = callback2;
        if (this.secureSettings.getInt("controls_enabled", 1) == 0) {
            fireControlsUpdate();
            return;
        }
        checkMigrationToQs();
        this.controlsComponent.getControlsListingController().ifPresent(new DeviceControlsControllerImpl$setCallback$1(this));
    }

    public void removeCallback() {
        this.position = null;
        this.callback = null;
        this.controlsComponent.getControlsListingController().ifPresent(new DeviceControlsControllerImpl$removeCallback$1(this));
    }

    /* access modifiers changed from: private */
    public final void fireControlsUpdate() {
        Log.i("DeviceControlsControllerImpl", Intrinsics.stringPlus("Setting DeviceControlsTile position: ", this.position));
        DeviceControlsController.Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onControlsUpdate(this.position);
        }
    }

    /* access modifiers changed from: private */
    public final void seedFavorites(List<ControlsServiceInfo> list) {
        String[] stringArray = this.context.getResources().getStringArray(R$array.config_controlsPreferredPackages);
        SharedPreferences sharedPreferences = this.userContextProvider.getUserContext().getSharedPreferences("controls_prefs", 0);
        Set<String> stringSet = sharedPreferences.getStringSet("SeedingCompleted", SetsKt__SetsKt.emptySet());
        ControlsController controlsController = this.controlsComponent.getControlsController().get();
        Intrinsics.checkNotNullExpressionValue(controlsController, "controlsComponent.getControlsController().get()");
        ControlsController controlsController2 = controlsController;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < Math.min(2, stringArray.length); i++) {
            String str = stringArray[i];
            for (ControlsServiceInfo controlsServiceInfo : list) {
                if (str.equals(controlsServiceInfo.componentName.getPackageName()) && !stringSet.contains(str)) {
                    ComponentName componentName = controlsServiceInfo.componentName;
                    Intrinsics.checkNotNullExpressionValue(componentName, "it.componentName");
                    if (controlsController2.countFavoritesForComponent(componentName) > 0) {
                        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "prefs");
                        Intrinsics.checkNotNullExpressionValue(str, "pkg");
                        addPackageToSeededSet(sharedPreferences, str);
                    } else {
                        ComponentName componentName2 = controlsServiceInfo.componentName;
                        Intrinsics.checkNotNullExpressionValue(componentName2, "it.componentName");
                        arrayList.add(componentName2);
                    }
                }
            }
        }
        if (!arrayList.isEmpty()) {
            controlsController2.seedFavoritesForComponents(arrayList, new DeviceControlsControllerImpl$seedFavorites$2(this, sharedPreferences));
        }
    }

    /* access modifiers changed from: private */
    public final void addPackageToSeededSet(SharedPreferences sharedPreferences, String str) {
        Set<String> stringSet = sharedPreferences.getStringSet("SeedingCompleted", SetsKt__SetsKt.emptySet());
        Intrinsics.checkNotNullExpressionValue(stringSet, "seededPackages");
        Set<T> mutableSet = CollectionsKt___CollectionsKt.toMutableSet(stringSet);
        mutableSet.add(str);
        sharedPreferences.edit().putStringSet("SeedingCompleted", mutableSet).apply();
    }
}
