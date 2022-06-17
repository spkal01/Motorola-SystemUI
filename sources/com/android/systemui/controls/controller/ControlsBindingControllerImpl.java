package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.controls.Control;
import android.service.controls.IControlsSubscriber;
import android.service.controls.IControlsSubscription;
import android.service.controls.actions.ControlAction;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.controls.controller.ControlsBindingController;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@VisibleForTesting
/* compiled from: ControlsBindingControllerImpl.kt */
public class ControlsBindingControllerImpl implements ControlsBindingController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public static final ControlsBindingControllerImpl$Companion$emptyCallback$1 emptyCallback = new ControlsBindingControllerImpl$Companion$emptyCallback$1();
    @NotNull
    private final ControlsBindingControllerImpl$actionCallbackService$1 actionCallbackService = new ControlsBindingControllerImpl$actionCallbackService$1(this);
    /* access modifiers changed from: private */
    @NotNull
    public final DelayableExecutor backgroundExecutor;
    @NotNull
    private final Context context;
    /* access modifiers changed from: private */
    @Nullable
    public ControlsProviderLifecycleManager currentProvider;
    /* access modifiers changed from: private */
    @NotNull
    public UserHandle currentUser;
    /* access modifiers changed from: private */
    @NotNull
    public final Lazy<ControlsController> lazyController;
    @Nullable
    private LoadSubscriber loadSubscriber;
    @Nullable
    private StatefulControlSubscriber statefulControlSubscriber;

    public ControlsBindingControllerImpl(@NotNull Context context2, @NotNull DelayableExecutor delayableExecutor, @NotNull Lazy<ControlsController> lazy, @NotNull UserTracker userTracker) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(delayableExecutor, "backgroundExecutor");
        Intrinsics.checkNotNullParameter(lazy, "lazyController");
        Intrinsics.checkNotNullParameter(userTracker, "userTracker");
        this.context = context2;
        this.backgroundExecutor = delayableExecutor;
        this.lazyController = lazy;
        this.currentUser = userTracker.getUserHandle();
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    @NotNull
    @VisibleForTesting
    /* renamed from: createProviderManager$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public ControlsProviderLifecycleManager mo12460xb2527126(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "component");
        return new ControlsProviderLifecycleManager(this.context, this.backgroundExecutor, this.actionCallbackService, this.currentUser, componentName);
    }

    private final ControlsProviderLifecycleManager retrieveLifecycleManager(ComponentName componentName) {
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.currentProvider;
        if (controlsProviderLifecycleManager != null) {
            if (!Intrinsics.areEqual((Object) controlsProviderLifecycleManager == null ? null : controlsProviderLifecycleManager.getComponentName(), (Object) componentName)) {
                unbind();
            }
        }
        ControlsProviderLifecycleManager controlsProviderLifecycleManager2 = this.currentProvider;
        if (controlsProviderLifecycleManager2 == null) {
            controlsProviderLifecycleManager2 = mo12460xb2527126(componentName);
        }
        this.currentProvider = controlsProviderLifecycleManager2;
        return controlsProviderLifecycleManager2;
    }

    @NotNull
    public Runnable bindAndLoad(@NotNull ComponentName componentName, @NotNull ControlsBindingController.LoadCallback loadCallback) {
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(loadCallback, "callback");
        LoadSubscriber loadSubscriber2 = this.loadSubscriber;
        if (loadSubscriber2 != null) {
            loadSubscriber2.loadCancel();
        }
        LoadSubscriber loadSubscriber3 = new LoadSubscriber(this, loadCallback, 100000);
        this.loadSubscriber = loadSubscriber3;
        retrieveLifecycleManager(componentName).maybeBindAndLoad(loadSubscriber3);
        return loadSubscriber3.loadCancel();
    }

    public void bindAndLoadSuggested(@NotNull ComponentName componentName, @NotNull ControlsBindingController.LoadCallback loadCallback) {
        Intrinsics.checkNotNullParameter(componentName, "component");
        Intrinsics.checkNotNullParameter(loadCallback, "callback");
        LoadSubscriber loadSubscriber2 = this.loadSubscriber;
        if (loadSubscriber2 != null) {
            loadSubscriber2.loadCancel();
        }
        LoadSubscriber loadSubscriber3 = new LoadSubscriber(this, loadCallback, 36);
        this.loadSubscriber = loadSubscriber3;
        retrieveLifecycleManager(componentName).maybeBindAndLoadSuggested(loadSubscriber3);
    }

    public void subscribe(@NotNull StructureInfo structureInfo) {
        Intrinsics.checkNotNullParameter(structureInfo, "structureInfo");
        unsubscribe();
        ControlsProviderLifecycleManager retrieveLifecycleManager = retrieveLifecycleManager(structureInfo.getComponentName());
        ControlsController controlsController = this.lazyController.get();
        Intrinsics.checkNotNullExpressionValue(controlsController, "lazyController.get()");
        StatefulControlSubscriber statefulControlSubscriber2 = new StatefulControlSubscriber(controlsController, retrieveLifecycleManager, this.backgroundExecutor, 100000);
        this.statefulControlSubscriber = statefulControlSubscriber2;
        List<ControlInfo> controls = structureInfo.getControls();
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controls, 10));
        for (ControlInfo controlId : controls) {
            arrayList.add(controlId.getControlId());
        }
        retrieveLifecycleManager.maybeBindAndSubscribe(arrayList, statefulControlSubscriber2);
    }

    public void unsubscribe() {
        StatefulControlSubscriber statefulControlSubscriber2 = this.statefulControlSubscriber;
        if (statefulControlSubscriber2 != null) {
            statefulControlSubscriber2.cancel();
        }
        this.statefulControlSubscriber = null;
    }

    public void action(@NotNull ComponentName componentName, @NotNull ControlInfo controlInfo, @NotNull ControlAction controlAction) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(controlInfo, "controlInfo");
        Intrinsics.checkNotNullParameter(controlAction, "action");
        if (this.statefulControlSubscriber == null) {
            Log.w("ControlsBindingControllerImpl", "No actions can occur outside of an active subscription. Ignoring.");
        } else {
            retrieveLifecycleManager(componentName).maybeBindAndSendAction(controlInfo.getControlId(), controlAction);
        }
    }

    public void changeUser(@NotNull UserHandle userHandle) {
        Intrinsics.checkNotNullParameter(userHandle, "newUser");
        if (!Intrinsics.areEqual((Object) userHandle, (Object) this.currentUser)) {
            unbind();
            this.currentUser = userHandle;
        }
    }

    /* access modifiers changed from: private */
    public final void unbind() {
        unsubscribe();
        LoadSubscriber loadSubscriber2 = this.loadSubscriber;
        if (loadSubscriber2 != null) {
            loadSubscriber2.loadCancel();
        }
        this.loadSubscriber = null;
        ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.currentProvider;
        if (controlsProviderLifecycleManager != null) {
            controlsProviderLifecycleManager.unbindService();
        }
        this.currentProvider = null;
    }

    public void onComponentRemoved(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        this.backgroundExecutor.execute(new ControlsBindingControllerImpl$onComponentRemoved$1(this, componentName));
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder("  ControlsBindingController:\n");
        sb.append("    currentUser=" + this.currentUser + 10);
        sb.append(Intrinsics.stringPlus("    StatefulControlSubscriber=", this.statefulControlSubscriber));
        sb.append("    Providers=" + this.currentProvider + 10);
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "StringBuilder(\"  ControlsBindingController:\\n\").apply {\n            append(\"    currentUser=$currentUser\\n\")\n            append(\"    StatefulControlSubscriber=$statefulControlSubscriber\")\n            append(\"    Providers=$currentProvider\\n\")\n        }.toString()");
        return sb2;
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private abstract class CallbackRunnable implements Runnable {
        @Nullable
        private final ControlsProviderLifecycleManager provider;
        final /* synthetic */ ControlsBindingControllerImpl this$0;
        @NotNull
        private final IBinder token;

        public abstract void doRun();

        public CallbackRunnable(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, IBinder iBinder) {
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(iBinder, "token");
            this.this$0 = controlsBindingControllerImpl;
            this.token = iBinder;
            this.provider = controlsBindingControllerImpl.currentProvider;
        }

        /* access modifiers changed from: protected */
        @Nullable
        public final ControlsProviderLifecycleManager getProvider() {
            return this.provider;
        }

        public void run() {
            ControlsProviderLifecycleManager controlsProviderLifecycleManager = this.provider;
            if (controlsProviderLifecycleManager == null) {
                Log.e("ControlsBindingControllerImpl", "No current provider set");
            } else if (!Intrinsics.areEqual((Object) controlsProviderLifecycleManager.getUser(), (Object) this.this$0.currentUser)) {
                Log.e("ControlsBindingControllerImpl", "User " + this.provider.getUser() + " is not current user");
            } else if (!Intrinsics.areEqual((Object) this.token, (Object) this.provider.getToken())) {
                Log.e("ControlsBindingControllerImpl", "Provider for token:" + this.token + " does not exist anymore");
            } else {
                doRun();
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnLoadRunnable extends CallbackRunnable {
        @NotNull
        private final ControlsBindingController.LoadCallback callback;
        @NotNull
        private final List<Control> list;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public OnLoadRunnable(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, @NotNull IBinder iBinder, @NotNull List<Control> list2, ControlsBindingController.LoadCallback loadCallback) {
            super(controlsBindingControllerImpl, iBinder);
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(list2, "list");
            Intrinsics.checkNotNullParameter(loadCallback, "callback");
            this.this$0 = controlsBindingControllerImpl;
            this.list = list2;
            this.callback = loadCallback;
        }

        public void doRun() {
            Log.d("ControlsBindingControllerImpl", "LoadSubscription: Complete and loading controls");
            this.callback.accept(this.list);
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnCancelAndLoadRunnable extends CallbackRunnable {
        @NotNull
        private final ControlsBindingController.LoadCallback callback;
        @NotNull
        private final List<Control> list;
        @NotNull
        private final IControlsSubscription subscription;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public OnCancelAndLoadRunnable(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, @NotNull IBinder iBinder, @NotNull List<Control> list2, @NotNull IControlsSubscription iControlsSubscription, ControlsBindingController.LoadCallback loadCallback) {
            super(controlsBindingControllerImpl, iBinder);
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(list2, "list");
            Intrinsics.checkNotNullParameter(iControlsSubscription, "subscription");
            Intrinsics.checkNotNullParameter(loadCallback, "callback");
            this.this$0 = controlsBindingControllerImpl;
            this.list = list2;
            this.subscription = iControlsSubscription;
            this.callback = loadCallback;
        }

        public void doRun() {
            Log.d("ControlsBindingControllerImpl", "LoadSubscription: Canceling and loading controls");
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                provider.cancelSubscription(this.subscription);
            }
            this.callback.accept(this.list);
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnSubscribeRunnable extends CallbackRunnable {
        private final long requestLimit;
        @NotNull
        private final IControlsSubscription subscription;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public OnSubscribeRunnable(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, @NotNull IBinder iBinder, IControlsSubscription iControlsSubscription, long j) {
            super(controlsBindingControllerImpl, iBinder);
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(iControlsSubscription, "subscription");
            this.this$0 = controlsBindingControllerImpl;
            this.subscription = iControlsSubscription;
            this.requestLimit = j;
        }

        public void doRun() {
            Log.d("ControlsBindingControllerImpl", "LoadSubscription: Starting subscription");
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                provider.startSubscription(this.subscription, this.requestLimit);
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnActionResponseRunnable extends CallbackRunnable {
        @NotNull
        private final String controlId;
        private final int response;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public OnActionResponseRunnable(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, @NotNull IBinder iBinder, String str, int i) {
            super(controlsBindingControllerImpl, iBinder);
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(str, "controlId");
            this.this$0 = controlsBindingControllerImpl;
            this.controlId = str;
            this.response = i;
        }

        @NotNull
        public final String getControlId() {
            return this.controlId;
        }

        public final int getResponse() {
            return this.response;
        }

        public void doRun() {
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                ((ControlsController) this.this$0.lazyController.get()).onActionResponse(provider.getComponentName(), getControlId(), getResponse());
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class OnLoadErrorRunnable extends CallbackRunnable {
        @NotNull
        private final ControlsBindingController.LoadCallback callback;
        @NotNull
        private final String error;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public OnLoadErrorRunnable(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, @NotNull IBinder iBinder, @NotNull String str, ControlsBindingController.LoadCallback loadCallback) {
            super(controlsBindingControllerImpl, iBinder);
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(str, "error");
            Intrinsics.checkNotNullParameter(loadCallback, "callback");
            this.this$0 = controlsBindingControllerImpl;
            this.error = str;
            this.callback = loadCallback;
        }

        @NotNull
        public final String getError() {
            return this.error;
        }

        public void doRun() {
            this.callback.error(this.error);
            ControlsProviderLifecycleManager provider = getProvider();
            if (provider != null) {
                Log.e("ControlsBindingControllerImpl", "onError receive from '" + provider.getComponentName() + "': " + getError());
            }
        }
    }

    /* compiled from: ControlsBindingControllerImpl.kt */
    private final class LoadSubscriber extends IControlsSubscriber.Stub {
        /* access modifiers changed from: private */
        @Nullable
        public Function0<Unit> _loadCancelInternal;
        @NotNull
        private ControlsBindingController.LoadCallback callback;
        /* access modifiers changed from: private */
        @NotNull
        public AtomicBoolean isTerminated = new AtomicBoolean(false);
        @NotNull
        private final ArrayList<Control> loadedControls = new ArrayList<>();
        private final long requestLimit;
        /* access modifiers changed from: private */
        public IControlsSubscription subscription;
        final /* synthetic */ ControlsBindingControllerImpl this$0;

        public LoadSubscriber(@NotNull ControlsBindingControllerImpl controlsBindingControllerImpl, ControlsBindingController.LoadCallback loadCallback, long j) {
            Intrinsics.checkNotNullParameter(controlsBindingControllerImpl, "this$0");
            Intrinsics.checkNotNullParameter(loadCallback, "callback");
            this.this$0 = controlsBindingControllerImpl;
            this.callback = loadCallback;
            this.requestLimit = j;
        }

        @NotNull
        public final ControlsBindingController.LoadCallback getCallback() {
            return this.callback;
        }

        public final long getRequestLimit() {
            return this.requestLimit;
        }

        @NotNull
        public final ArrayList<Control> getLoadedControls() {
            return this.loadedControls;
        }

        @NotNull
        public final Runnable loadCancel() {
            return new ControlsBindingControllerImpl$LoadSubscriber$loadCancel$1(this);
        }

        public void onSubscribe(@NotNull IBinder iBinder, @NotNull IControlsSubscription iControlsSubscription) {
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(iControlsSubscription, "subs");
            this.subscription = iControlsSubscription;
            this._loadCancelInternal = new ControlsBindingControllerImpl$LoadSubscriber$onSubscribe$1(this.this$0, this);
            this.this$0.backgroundExecutor.execute(new OnSubscribeRunnable(this.this$0, iBinder, iControlsSubscription, this.requestLimit));
        }

        public void onNext(@NotNull IBinder iBinder, @NotNull Control control) {
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(control, "c");
            this.this$0.backgroundExecutor.execute(new ControlsBindingControllerImpl$LoadSubscriber$onNext$1(this, control, this.this$0, iBinder));
        }

        public void onError(@NotNull IBinder iBinder, @NotNull String str) {
            Intrinsics.checkNotNullParameter(iBinder, "token");
            Intrinsics.checkNotNullParameter(str, "s");
            maybeTerminateAndRun(new OnLoadErrorRunnable(this.this$0, iBinder, str, this.callback));
        }

        public void onComplete(@NotNull IBinder iBinder) {
            Intrinsics.checkNotNullParameter(iBinder, "token");
            maybeTerminateAndRun(new OnLoadRunnable(this.this$0, iBinder, this.loadedControls, this.callback));
        }

        /* access modifiers changed from: private */
        public final void maybeTerminateAndRun(Runnable runnable) {
            if (!this.isTerminated.get()) {
                this._loadCancelInternal = C0886x5cb900b7.INSTANCE;
                this.callback = ControlsBindingControllerImpl.emptyCallback;
                ControlsProviderLifecycleManager access$getCurrentProvider$p = this.this$0.currentProvider;
                if (access$getCurrentProvider$p != null) {
                    access$getCurrentProvider$p.cancelLoadTimeout();
                }
                this.this$0.backgroundExecutor.execute(new C0887x5cb900b8(this, runnable));
            }
        }
    }
}
