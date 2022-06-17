package com.android.systemui.controls.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.controls.IControlsActionCallback;
import android.service.controls.IControlsSubscriber;
import android.service.controls.IControlsSubscription;
import android.service.controls.actions.ControlAction;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsProviderLifecycleManager.kt */
public final class ControlsProviderLifecycleManager implements IBinder.DeathRecipient {
    /* access modifiers changed from: private */
    public static final int BIND_FLAGS = 67109121;
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public final String TAG = ControlsProviderLifecycleManager.class.getSimpleName();
    /* access modifiers changed from: private */
    @NotNull
    public final IControlsActionCallback.Stub actionCallbackService;
    /* access modifiers changed from: private */
    public int bindTryCount;
    @NotNull
    private final ComponentName componentName;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    @NotNull
    private final DelayableExecutor executor;
    /* access modifiers changed from: private */
    @NotNull
    public final Intent intent;
    @Nullable
    private Runnable onLoadCanceller;
    @GuardedBy({"queuedServiceMethods"})
    @NotNull
    private final Set<ServiceMethod> queuedServiceMethods = new ArraySet();
    /* access modifiers changed from: private */
    public boolean requiresBound;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsProviderLifecycleManager$serviceConnection$1 serviceConnection;
    @NotNull
    private final IBinder token = new Binder();
    @NotNull
    private final UserHandle user;
    /* access modifiers changed from: private */
    @Nullable
    public ServiceWrapper wrapper;

    public ControlsProviderLifecycleManager(@NotNull Context context2, @NotNull DelayableExecutor delayableExecutor, @NotNull IControlsActionCallback.Stub stub, @NotNull UserHandle userHandle, @NotNull ComponentName componentName2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(delayableExecutor, "executor");
        Intrinsics.checkNotNullParameter(stub, "actionCallbackService");
        Intrinsics.checkNotNullParameter(userHandle, "user");
        Intrinsics.checkNotNullParameter(componentName2, "componentName");
        this.context = context2;
        this.executor = delayableExecutor;
        this.actionCallbackService = stub;
        this.user = userHandle;
        this.componentName = componentName2;
        Intent intent2 = new Intent();
        intent2.setComponent(getComponentName());
        Bundle bundle = new Bundle();
        bundle.putBinder("CALLBACK_TOKEN", getToken());
        Unit unit = Unit.INSTANCE;
        intent2.putExtra("CALLBACK_BUNDLE", bundle);
        this.intent = intent2;
        this.serviceConnection = new ControlsProviderLifecycleManager$serviceConnection$1(this);
    }

    @NotNull
    public final UserHandle getUser() {
        return this.user;
    }

    @NotNull
    public final ComponentName getComponentName() {
        return this.componentName;
    }

    @NotNull
    public final IBinder getToken() {
        return this.token;
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* access modifiers changed from: private */
    public final void bindService(boolean z) {
        this.executor.execute(new ControlsProviderLifecycleManager$bindService$1(this, z));
    }

    /* access modifiers changed from: private */
    public final void handlePendingServiceMethods() {
        ArraySet<ServiceMethod> arraySet;
        synchronized (this.queuedServiceMethods) {
            arraySet = new ArraySet<>(this.queuedServiceMethods);
            this.queuedServiceMethods.clear();
        }
        for (ServiceMethod run : arraySet) {
            run.run();
        }
    }

    public void binderDied() {
        if (this.wrapper != null) {
            this.wrapper = null;
            if (this.requiresBound) {
                Log.d(this.TAG, "binderDied");
            }
        }
    }

    /* access modifiers changed from: private */
    public final void queueServiceMethod(ServiceMethod serviceMethod) {
        synchronized (this.queuedServiceMethods) {
            this.queuedServiceMethods.add(serviceMethod);
        }
    }

    private final void invokeOrQueue(ServiceMethod serviceMethod) {
        Unit unit;
        if (this.wrapper == null) {
            unit = null;
        } else {
            serviceMethod.run();
            unit = Unit.INSTANCE;
        }
        if (unit == null) {
            queueServiceMethod(serviceMethod);
            bindService(true);
        }
    }

    public final void maybeBindAndLoad(@NotNull IControlsSubscriber.Stub stub) {
        Intrinsics.checkNotNullParameter(stub, "subscriber");
        this.onLoadCanceller = this.executor.executeDelayed(new ControlsProviderLifecycleManager$maybeBindAndLoad$1(this, stub), 20, TimeUnit.SECONDS);
        invokeOrQueue(new Load(this, stub));
    }

    public final void maybeBindAndLoadSuggested(@NotNull IControlsSubscriber.Stub stub) {
        Intrinsics.checkNotNullParameter(stub, "subscriber");
        this.onLoadCanceller = this.executor.executeDelayed(new ControlsProviderLifecycleManager$maybeBindAndLoadSuggested$1(this, stub), 20, TimeUnit.SECONDS);
        invokeOrQueue(new Suggest(this, stub));
    }

    public final void cancelLoadTimeout() {
        Runnable runnable = this.onLoadCanceller;
        if (runnable != null) {
            runnable.run();
        }
        this.onLoadCanceller = null;
    }

    public final void maybeBindAndSubscribe(@NotNull List<String> list, @NotNull IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkNotNullParameter(list, "controlIds");
        Intrinsics.checkNotNullParameter(iControlsSubscriber, "subscriber");
        invokeOrQueue(new Subscribe(this, list, iControlsSubscriber));
    }

    public final void maybeBindAndSendAction(@NotNull String str, @NotNull ControlAction controlAction) {
        Intrinsics.checkNotNullParameter(str, "controlId");
        Intrinsics.checkNotNullParameter(controlAction, "action");
        invokeOrQueue(new Action(this, str, controlAction));
    }

    public final void startSubscription(@NotNull IControlsSubscription iControlsSubscription, long j) {
        Intrinsics.checkNotNullParameter(iControlsSubscription, "subscription");
        Log.d(this.TAG, Intrinsics.stringPlus("startSubscription: ", iControlsSubscription));
        ServiceWrapper serviceWrapper = this.wrapper;
        if (serviceWrapper != null) {
            serviceWrapper.request(iControlsSubscription, j);
        }
    }

    public final void cancelSubscription(@NotNull IControlsSubscription iControlsSubscription) {
        Intrinsics.checkNotNullParameter(iControlsSubscription, "subscription");
        Log.d(this.TAG, Intrinsics.stringPlus("cancelSubscription: ", iControlsSubscription));
        ServiceWrapper serviceWrapper = this.wrapper;
        if (serviceWrapper != null) {
            serviceWrapper.cancel(iControlsSubscription);
        }
    }

    public final void unbindService() {
        Runnable runnable = this.onLoadCanceller;
        if (runnable != null) {
            runnable.run();
        }
        this.onLoadCanceller = null;
        bindService(false);
    }

    @NotNull
    public String toString() {
        String str = "ControlsProviderLifecycleManager(" + Intrinsics.stringPlus("component=", getComponentName()) + Intrinsics.stringPlus(", user=", getUser()) + ")";
        Intrinsics.checkNotNullExpressionValue(str, "StringBuilder(\"ControlsProviderLifecycleManager(\").apply {\n            append(\"component=$componentName\")\n            append(\", user=$user\")\n            append(\")\")\n        }.toString()");
        return str;
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public abstract class ServiceMethod {
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public abstract boolean mo12543x93b7231b();

        public ServiceMethod(ControlsProviderLifecycleManager controlsProviderLifecycleManager) {
            Intrinsics.checkNotNullParameter(controlsProviderLifecycleManager, "this$0");
            this.this$0 = controlsProviderLifecycleManager;
        }

        public final void run() {
            if (!mo12543x93b7231b()) {
                this.this$0.queueServiceMethod(this);
                this.this$0.binderDied();
            }
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Load extends ServiceMethod {
        @NotNull
        private final IControlsSubscriber.Stub subscriber;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public Load(@NotNull ControlsProviderLifecycleManager controlsProviderLifecycleManager, IControlsSubscriber.Stub stub) {
            super(controlsProviderLifecycleManager);
            Intrinsics.checkNotNullParameter(controlsProviderLifecycleManager, "this$0");
            Intrinsics.checkNotNullParameter(stub, "subscriber");
            this.this$0 = controlsProviderLifecycleManager;
            this.subscriber = stub;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo12543x93b7231b() {
            Log.d(this.this$0.TAG, Intrinsics.stringPlus("load ", this.this$0.getComponentName()));
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p == null) {
                return false;
            }
            return access$getWrapper$p.load(this.subscriber);
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Suggest extends ServiceMethod {
        @NotNull
        private final IControlsSubscriber.Stub subscriber;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public Suggest(@NotNull ControlsProviderLifecycleManager controlsProviderLifecycleManager, IControlsSubscriber.Stub stub) {
            super(controlsProviderLifecycleManager);
            Intrinsics.checkNotNullParameter(controlsProviderLifecycleManager, "this$0");
            Intrinsics.checkNotNullParameter(stub, "subscriber");
            this.this$0 = controlsProviderLifecycleManager;
            this.subscriber = stub;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo12543x93b7231b() {
            Log.d(this.this$0.TAG, Intrinsics.stringPlus("suggest ", this.this$0.getComponentName()));
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p == null) {
                return false;
            }
            return access$getWrapper$p.loadSuggested(this.subscriber);
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Subscribe extends ServiceMethod {
        @NotNull
        private final List<String> list;
        @NotNull
        private final IControlsSubscriber subscriber;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public Subscribe(@NotNull ControlsProviderLifecycleManager controlsProviderLifecycleManager, @NotNull List<String> list2, IControlsSubscriber iControlsSubscriber) {
            super(controlsProviderLifecycleManager);
            Intrinsics.checkNotNullParameter(controlsProviderLifecycleManager, "this$0");
            Intrinsics.checkNotNullParameter(list2, "list");
            Intrinsics.checkNotNullParameter(iControlsSubscriber, "subscriber");
            this.this$0 = controlsProviderLifecycleManager;
            this.list = list2;
            this.subscriber = iControlsSubscriber;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo12543x93b7231b() {
            String access$getTAG$p = this.this$0.TAG;
            Log.d(access$getTAG$p, "subscribe " + this.this$0.getComponentName() + " - " + this.list);
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p == null) {
                return false;
            }
            return access$getWrapper$p.subscribe(this.list, this.subscriber);
        }
    }

    /* compiled from: ControlsProviderLifecycleManager.kt */
    public final class Action extends ServiceMethod {
        @NotNull
        private final ControlAction action;
        @NotNull

        /* renamed from: id */
        private final String f83id;
        final /* synthetic */ ControlsProviderLifecycleManager this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public Action(@NotNull ControlsProviderLifecycleManager controlsProviderLifecycleManager, @NotNull String str, ControlAction controlAction) {
            super(controlsProviderLifecycleManager);
            Intrinsics.checkNotNullParameter(controlsProviderLifecycleManager, "this$0");
            Intrinsics.checkNotNullParameter(str, "id");
            Intrinsics.checkNotNullParameter(controlAction, "action");
            this.this$0 = controlsProviderLifecycleManager;
            this.f83id = str;
            this.action = controlAction;
        }

        /* renamed from: callWrapper$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
        public boolean mo12543x93b7231b() {
            String access$getTAG$p = this.this$0.TAG;
            Log.d(access$getTAG$p, "onAction " + this.this$0.getComponentName() + " - " + this.f83id);
            ServiceWrapper access$getWrapper$p = this.this$0.wrapper;
            if (access$getWrapper$p == null) {
                return false;
            }
            return access$getWrapper$p.action(this.f83id, this.action, this.this$0.actionCallbackService);
        }
    }
}
