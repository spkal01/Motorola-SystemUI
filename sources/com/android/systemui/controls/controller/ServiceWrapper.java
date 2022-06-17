package com.android.systemui.controls.controller;

import android.service.controls.IControlsActionCallback;
import android.service.controls.IControlsProvider;
import android.service.controls.IControlsSubscriber;
import android.service.controls.IControlsSubscription;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.ControlActionWrapper;
import android.util.Log;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ServiceWrapper.kt */
public final class ServiceWrapper {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final IControlsProvider service;

    /* compiled from: ServiceWrapper.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public ServiceWrapper(@NotNull IControlsProvider iControlsProvider) {
        Intrinsics.checkNotNullParameter(iControlsProvider, "service");
        this.service = iControlsProvider;
    }

    @NotNull
    public final IControlsProvider getService() {
        return this.service;
    }

    public final boolean load(@NotNull IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkNotNullParameter(iControlsSubscriber, "subscriber");
        try {
            getService().load(iControlsSubscriber);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean loadSuggested(@NotNull IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkNotNullParameter(iControlsSubscriber, "subscriber");
        try {
            getService().loadSuggested(iControlsSubscriber);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean subscribe(@NotNull List<String> list, @NotNull IControlsSubscriber iControlsSubscriber) {
        Intrinsics.checkNotNullParameter(list, "controlIds");
        Intrinsics.checkNotNullParameter(iControlsSubscriber, "subscriber");
        try {
            getService().subscribe(list, iControlsSubscriber);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean request(@NotNull IControlsSubscription iControlsSubscription, long j) {
        Intrinsics.checkNotNullParameter(iControlsSubscription, "subscription");
        try {
            iControlsSubscription.request(j);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean cancel(@NotNull IControlsSubscription iControlsSubscription) {
        Intrinsics.checkNotNullParameter(iControlsSubscription, "subscription");
        try {
            iControlsSubscription.cancel();
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }

    public final boolean action(@NotNull String str, @NotNull ControlAction controlAction, @NotNull IControlsActionCallback iControlsActionCallback) {
        Intrinsics.checkNotNullParameter(str, "controlId");
        Intrinsics.checkNotNullParameter(controlAction, "action");
        Intrinsics.checkNotNullParameter(iControlsActionCallback, "cb");
        try {
            getService().action(str, new ControlActionWrapper(controlAction), iControlsActionCallback);
            return true;
        } catch (Exception e) {
            Log.e("ServiceWrapper", "Caught exception from ControlsProviderService", e);
            return false;
        }
    }
}
