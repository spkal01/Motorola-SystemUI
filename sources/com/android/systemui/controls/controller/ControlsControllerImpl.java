package com.android.systemui.controls.controller;

import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.UserHandle;
import android.service.controls.Control;
import android.service.controls.actions.ControlAction;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dumpable;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl implements Dumpable, ControlsController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private AuxiliaryPersistenceWrapper auxiliaryPersistenceWrapper;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsBindingController bindingController;
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    @NotNull
    private final Context context;
    /* access modifiers changed from: private */
    @NotNull
    public UserHandle currentUser;
    /* access modifiers changed from: private */
    @NotNull
    public final DelayableExecutor executor;
    @NotNull
    private final ControlsControllerImpl$listingCallback$1 listingCallback;
    @NotNull
    private final ControlsListingController listingController;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsFavoritePersistenceWrapper persistenceWrapper;
    @NotNull
    private final BroadcastReceiver restoreFinishedReceiver;
    /* access modifiers changed from: private */
    @NotNull
    public final List<Consumer<Boolean>> seedingCallbacks = new ArrayList();
    /* access modifiers changed from: private */
    public boolean seedingInProgress;
    @NotNull
    private final ContentObserver settingObserver;
    @NotNull
    private final ControlsUiController uiController;
    /* access modifiers changed from: private */
    public boolean userChanging = true;
    /* access modifiers changed from: private */
    @NotNull
    public UserStructure userStructure;
    @NotNull
    private final ControlsControllerImpl$userSwitchReceiver$1 userSwitchReceiver;

    @VisibleForTesting
    /* renamed from: getAuxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m19x61774fea() {
    }

    @VisibleForTesting
    /* renamed from: getRestoreFinishedReceiver$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m20x34c469bf() {
    }

    @VisibleForTesting
    /* renamed from: getSettingObserver$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m21x3a354bd6() {
    }

    public ControlsControllerImpl(@NotNull Context context2, @NotNull DelayableExecutor delayableExecutor, @NotNull ControlsUiController controlsUiController, @NotNull ControlsBindingController controlsBindingController, @NotNull ControlsListingController controlsListingController, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull Optional<ControlsFavoritePersistenceWrapper> optional, @NotNull DumpManager dumpManager, @NotNull UserTracker userTracker) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(delayableExecutor, "executor");
        Intrinsics.checkNotNullParameter(controlsUiController, "uiController");
        Intrinsics.checkNotNullParameter(controlsBindingController, "bindingController");
        Intrinsics.checkNotNullParameter(controlsListingController, "listingController");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(optional, "optionalWrapper");
        Intrinsics.checkNotNullParameter(dumpManager, "dumpManager");
        Intrinsics.checkNotNullParameter(userTracker, "userTracker");
        this.context = context2;
        this.executor = delayableExecutor;
        this.uiController = controlsUiController;
        this.bindingController = controlsBindingController;
        this.listingController = controlsListingController;
        this.broadcastDispatcher = broadcastDispatcher2;
        UserHandle userHandle = userTracker.getUserHandle();
        this.currentUser = userHandle;
        this.userStructure = new UserStructure(context2, userHandle);
        ControlsFavoritePersistenceWrapper orElseGet = optional.orElseGet(new Supplier<ControlsFavoritePersistenceWrapper>(this) {
            final /* synthetic */ ControlsControllerImpl this$0;

            {
                this.this$0 = r1;
            }

            public final ControlsFavoritePersistenceWrapper get() {
                File file = this.this$0.userStructure.getFile();
                Intrinsics.checkNotNullExpressionValue(file, "userStructure.file");
                return new ControlsFavoritePersistenceWrapper(file, this.this$0.executor, new BackupManager(this.this$0.userStructure.getUserContext()));
            }
        });
        Intrinsics.checkNotNullExpressionValue(orElseGet, "optionalWrapper.orElseGet {\n            ControlsFavoritePersistenceWrapper(\n                    userStructure.file,\n                    executor,\n                    BackupManager(userStructure.userContext)\n            )\n        }");
        this.persistenceWrapper = orElseGet;
        File auxiliaryFile = this.userStructure.getAuxiliaryFile();
        Intrinsics.checkNotNullExpressionValue(auxiliaryFile, "userStructure.auxiliaryFile");
        this.auxiliaryPersistenceWrapper = new AuxiliaryPersistenceWrapper(auxiliaryFile, delayableExecutor);
        ControlsControllerImpl$userSwitchReceiver$1 controlsControllerImpl$userSwitchReceiver$1 = new ControlsControllerImpl$userSwitchReceiver$1(this);
        this.userSwitchReceiver = controlsControllerImpl$userSwitchReceiver$1;
        ControlsControllerImpl$restoreFinishedReceiver$1 controlsControllerImpl$restoreFinishedReceiver$1 = new ControlsControllerImpl$restoreFinishedReceiver$1(this);
        this.restoreFinishedReceiver = controlsControllerImpl$restoreFinishedReceiver$1;
        this.settingObserver = new ControlsControllerImpl$settingObserver$1(this);
        ControlsControllerImpl$listingCallback$1 controlsControllerImpl$listingCallback$1 = new ControlsControllerImpl$listingCallback$1(this);
        this.listingCallback = controlsControllerImpl$listingCallback$1;
        String name = ControlsControllerImpl.class.getName();
        Intrinsics.checkNotNullExpressionValue(name, "javaClass.name");
        dumpManager.registerDumpable(name, this);
        resetFavorites();
        this.userChanging = false;
        broadcastDispatcher2.registerReceiver(controlsControllerImpl$userSwitchReceiver$1, new IntentFilter("android.intent.action.USER_SWITCHED"), delayableExecutor, UserHandle.ALL);
        context2.registerReceiver(controlsControllerImpl$restoreFinishedReceiver$1, new IntentFilter("com.android.systemui.backup.RESTORE_FINISHED"), "com.android.systemui.permission.SELF", (Handler) null);
        controlsListingController.addCallback(controlsControllerImpl$listingCallback$1);
    }

    /* compiled from: ControlsControllerImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public int getCurrentUserId() {
        return this.currentUser.getIdentifier();
    }

    @NotNull
    /* renamed from: getAuxiliaryPersistenceWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final AuxiliaryPersistenceWrapper mo12498x4fd0e26a() {
        return this.auxiliaryPersistenceWrapper;
    }

    /* access modifiers changed from: private */
    public final void setValuesForUser(UserHandle userHandle) {
        Log.d("ControlsControllerImpl", Intrinsics.stringPlus("Changing to user: ", userHandle));
        this.currentUser = userHandle;
        UserStructure userStructure2 = new UserStructure(this.context, userHandle);
        this.userStructure = userStructure2;
        ControlsFavoritePersistenceWrapper controlsFavoritePersistenceWrapper = this.persistenceWrapper;
        File file = userStructure2.getFile();
        Intrinsics.checkNotNullExpressionValue(file, "userStructure.file");
        controlsFavoritePersistenceWrapper.changeFileAndBackupManager(file, new BackupManager(this.userStructure.getUserContext()));
        AuxiliaryPersistenceWrapper auxiliaryPersistenceWrapper2 = this.auxiliaryPersistenceWrapper;
        File auxiliaryFile = this.userStructure.getAuxiliaryFile();
        Intrinsics.checkNotNullExpressionValue(auxiliaryFile, "userStructure.auxiliaryFile");
        auxiliaryPersistenceWrapper2.changeFile(auxiliaryFile);
        resetFavorites();
        this.bindingController.changeUser(userHandle);
        this.listingController.changeUser(userHandle);
        this.userChanging = false;
    }

    /* access modifiers changed from: private */
    public final void resetFavorites() {
        Favorites favorites = Favorites.INSTANCE;
        favorites.clear();
        favorites.load(this.persistenceWrapper.readFavorites());
    }

    private final boolean confirmAvailability() {
        if (!this.userChanging) {
            return true;
        }
        Log.w("ControlsControllerImpl", "Controls not available while user is changing");
        return false;
    }

    public void loadForComponent(@NotNull ComponentName componentName, @NotNull Consumer<ControlsController.LoadData> consumer, @NotNull Consumer<Runnable> consumer2) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(consumer, "dataCallback");
        Intrinsics.checkNotNullParameter(consumer2, "cancelWrapper");
        if (!confirmAvailability()) {
            if (this.userChanging) {
                this.executor.executeDelayed(new ControlsControllerImpl$loadForComponent$1(this, componentName, consumer, consumer2), 500, TimeUnit.MILLISECONDS);
            }
            consumer.accept(ControlsControllerKt.createLoadDataObject(CollectionsKt__CollectionsKt.emptyList(), CollectionsKt__CollectionsKt.emptyList(), true));
        }
        consumer2.accept(this.bindingController.bindAndLoad(componentName, new ControlsControllerImpl$loadForComponent$2(this, componentName, consumer)));
    }

    public boolean addSeedingFavoritesCallback(@NotNull Consumer<Boolean> consumer) {
        Intrinsics.checkNotNullParameter(consumer, "callback");
        if (!this.seedingInProgress) {
            return false;
        }
        this.executor.execute(new ControlsControllerImpl$addSeedingFavoritesCallback$1(this, consumer));
        return true;
    }

    public void seedFavoritesForComponents(@NotNull List<ComponentName> list, @NotNull Consumer<SeedResponse> consumer) {
        Intrinsics.checkNotNullParameter(list, "componentNames");
        Intrinsics.checkNotNullParameter(consumer, "callback");
        if (!this.seedingInProgress && !list.isEmpty()) {
            if (confirmAvailability()) {
                this.seedingInProgress = true;
                startSeeding(list, consumer, false);
            } else if (this.userChanging) {
                this.executor.executeDelayed(new ControlsControllerImpl$seedFavoritesForComponents$1(this, list, consumer), 500, TimeUnit.MILLISECONDS);
            } else {
                for (ComponentName packageName : list) {
                    String packageName2 = packageName.getPackageName();
                    Intrinsics.checkNotNullExpressionValue(packageName2, "it.packageName");
                    consumer.accept(new SeedResponse(packageName2, false));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final void startSeeding(List<ComponentName> list, Consumer<SeedResponse> consumer, boolean z) {
        if (list.isEmpty()) {
            endSeedingCall(!z);
            return;
        }
        ComponentName componentName = list.get(0);
        Log.d("ControlsControllerImpl", Intrinsics.stringPlus("Beginning request to seed favorites for: ", componentName));
        this.bindingController.bindAndLoadSuggested(componentName, new ControlsControllerImpl$startSeeding$1(this, consumer, componentName, CollectionsKt___CollectionsKt.drop(list, 1), z));
    }

    private final void endSeedingCall(boolean z) {
        this.seedingInProgress = false;
        for (Consumer accept : this.seedingCallbacks) {
            accept.accept(Boolean.valueOf(z));
        }
        this.seedingCallbacks.clear();
    }

    static /* synthetic */ ControlStatus createRemovedStatus$default(ControlsControllerImpl controlsControllerImpl, ComponentName componentName, ControlInfo controlInfo, CharSequence charSequence, boolean z, int i, Object obj) {
        if ((i & 8) != 0) {
            z = true;
        }
        return controlsControllerImpl.createRemovedStatus(componentName, controlInfo, charSequence, z);
    }

    /* access modifiers changed from: private */
    public final ControlStatus createRemovedStatus(ComponentName componentName, ControlInfo controlInfo, CharSequence charSequence, boolean z) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(componentName.getPackageName());
        Control build = new Control.StatelessBuilder(controlInfo.getControlId(), PendingIntent.getActivity(this.context, componentName.hashCode(), intent, 67108864)).setTitle(controlInfo.getControlTitle()).setSubtitle(controlInfo.getControlSubtitle()).setStructure(charSequence).setDeviceType(controlInfo.getDeviceType()).build();
        Intrinsics.checkNotNullExpressionValue(build, "control");
        return new ControlStatus(build, componentName, true, z);
    }

    public void subscribeToFavorites(@NotNull StructureInfo structureInfo) {
        Intrinsics.checkNotNullParameter(structureInfo, "structureInfo");
        if (confirmAvailability()) {
            this.bindingController.subscribe(structureInfo);
        }
    }

    public void unsubscribe() {
        if (confirmAvailability()) {
            this.bindingController.unsubscribe();
        }
    }

    public void addFavorite(@NotNull ComponentName componentName, @NotNull CharSequence charSequence, @NotNull ControlInfo controlInfo) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(charSequence, "structureName");
        Intrinsics.checkNotNullParameter(controlInfo, "controlInfo");
        if (confirmAvailability()) {
            this.executor.execute(new ControlsControllerImpl$addFavorite$1(componentName, charSequence, controlInfo, this));
        }
    }

    public void replaceFavoritesForStructure(@NotNull StructureInfo structureInfo) {
        Intrinsics.checkNotNullParameter(structureInfo, "structureInfo");
        if (confirmAvailability()) {
            this.executor.execute(new ControlsControllerImpl$replaceFavoritesForStructure$1(structureInfo, this));
        }
    }

    public void refreshStatus(@NotNull ComponentName componentName, @NotNull Control control) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(control, "control");
        if (!confirmAvailability()) {
            Log.d("ControlsControllerImpl", "Controls not available");
            return;
        }
        if (control.getStatus() == 1) {
            this.executor.execute(new ControlsControllerImpl$refreshStatus$1(componentName, control, this));
        }
        this.uiController.onRefreshState(componentName, CollectionsKt__CollectionsJVMKt.listOf(control));
    }

    public void onActionResponse(@NotNull ComponentName componentName, @NotNull String str, int i) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(str, "controlId");
        if (confirmAvailability()) {
            this.uiController.onActionResponse(componentName, str, i);
        }
    }

    public void action(@NotNull ComponentName componentName, @NotNull ControlInfo controlInfo, @NotNull ControlAction controlAction) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(controlInfo, "controlInfo");
        Intrinsics.checkNotNullParameter(controlAction, "action");
        if (confirmAvailability()) {
            this.bindingController.action(componentName, controlInfo, controlAction);
        }
    }

    @NotNull
    public List<StructureInfo> getFavorites() {
        return Favorites.INSTANCE.getAllStructures();
    }

    public int countFavoritesForComponent(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        return Favorites.INSTANCE.getControlsForComponent(componentName).size();
    }

    @NotNull
    public List<StructureInfo> getFavoritesForComponent(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        return Favorites.INSTANCE.getStructuresForComponent(componentName);
    }

    @NotNull
    public List<ControlInfo> getFavoritesForStructure(@NotNull ComponentName componentName, @NotNull CharSequence charSequence) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(charSequence, "structureName");
        return Favorites.INSTANCE.getControlsForStructure(new StructureInfo(componentName, charSequence, CollectionsKt__CollectionsKt.emptyList()));
    }

    @NotNull
    public StructureInfo getPreferredStructure() {
        return this.uiController.getPreferredStructure(getFavorites());
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println("ControlsController state:");
        printWriter.println(Intrinsics.stringPlus("  Changing users: ", Boolean.valueOf(this.userChanging)));
        printWriter.println(Intrinsics.stringPlus("  Current user: ", Integer.valueOf(this.currentUser.getIdentifier())));
        printWriter.println("  Favorites:");
        for (StructureInfo structureInfo : Favorites.INSTANCE.getAllStructures()) {
            printWriter.println(Intrinsics.stringPlus("    ", structureInfo));
            for (ControlInfo stringPlus : structureInfo.getControls()) {
                printWriter.println(Intrinsics.stringPlus("      ", stringPlus));
            }
        }
        printWriter.println(this.bindingController.toString());
    }

    /* access modifiers changed from: private */
    public final Set<String> findRemoved(Set<String> set, List<Control> list) {
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10));
        for (Control controlId : list) {
            arrayList.add(controlId.getControlId());
        }
        return SetsKt___SetsKt.minus(set, arrayList);
    }
}
