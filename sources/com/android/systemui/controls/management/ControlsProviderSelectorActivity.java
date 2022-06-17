package com.android.systemui.controls.management;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.p004ui.ControlsActivity;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.util.LifecycleActivity;
import java.util.concurrent.Executor;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsProviderSelectorActivity.kt */
public final class ControlsProviderSelectorActivity extends LifecycleActivity {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private final Executor backExecutor;
    private boolean backShouldExit;
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    @NotNull
    private final ControlsController controlsController;
    @NotNull
    private final ControlsProviderSelectorActivity$currentUserTracker$1 currentUserTracker;
    @NotNull
    private final Executor executor;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsListingController listingController;
    /* access modifiers changed from: private */
    public RecyclerView recyclerView;
    @NotNull
    private final ControlsUiController uiController;

    public ControlsProviderSelectorActivity(@NotNull Executor executor2, @NotNull Executor executor3, @NotNull ControlsListingController controlsListingController, @NotNull ControlsController controlsController2, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull ControlsUiController controlsUiController) {
        Intrinsics.checkNotNullParameter(executor2, "executor");
        Intrinsics.checkNotNullParameter(executor3, "backExecutor");
        Intrinsics.checkNotNullParameter(controlsListingController, "listingController");
        Intrinsics.checkNotNullParameter(controlsController2, "controlsController");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(controlsUiController, "uiController");
        this.executor = executor2;
        this.backExecutor = executor3;
        this.listingController = controlsListingController;
        this.controlsController = controlsController2;
        this.broadcastDispatcher = broadcastDispatcher2;
        this.uiController = controlsUiController;
        this.currentUserTracker = new ControlsProviderSelectorActivity$currentUserTracker$1(this, broadcastDispatcher2);
    }

    /* compiled from: ControlsProviderSelectorActivity.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.controls_management);
        Lifecycle lifecycle = getLifecycle();
        ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
        View requireViewById = requireViewById(R$id.controls_management_root);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "requireViewById<ViewGroup>(R.id.controls_management_root)");
        Window window = getWindow();
        Intrinsics.checkNotNullExpressionValue(window, "window");
        Intent intent = getIntent();
        Intrinsics.checkNotNullExpressionValue(intent, "intent");
        lifecycle.addObserver(controlsAnimations.observerForAnimations((ViewGroup) requireViewById, window, intent));
        ViewStub viewStub = (ViewStub) requireViewById(R$id.stub);
        viewStub.setLayoutResource(R$layout.controls_management_apps);
        viewStub.inflate();
        View requireViewById2 = requireViewById(R$id.list);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "requireViewById(R.id.list)");
        RecyclerView recyclerView2 = (RecyclerView) requireViewById2;
        this.recyclerView = recyclerView2;
        if (recyclerView2 != null) {
            recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            TextView textView = (TextView) requireViewById(R$id.title);
            textView.setText(textView.getResources().getText(R$string.controls_providers_title));
            Button button = (Button) requireViewById(R$id.other_apps);
            button.setVisibility(0);
            button.setText(17039360);
            button.setOnClickListener(new ControlsProviderSelectorActivity$onCreate$3$1(this));
            requireViewById(R$id.done).setVisibility(8);
            this.backShouldExit = getIntent().getBooleanExtra("back_should_exit", false);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("recyclerView");
        throw null;
    }

    public void onBackPressed() {
        if (!this.backShouldExit) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getApplicationContext(), ControlsActivity.class));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, new Pair[0]).toBundle());
        }
        animateExitAndFinish();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.currentUserTracker.startTracking();
        RecyclerView recyclerView2 = this.recyclerView;
        if (recyclerView2 != null) {
            recyclerView2.setAlpha(0.0f);
            RecyclerView recyclerView3 = this.recyclerView;
            if (recyclerView3 != null) {
                Executor executor2 = this.backExecutor;
                Executor executor3 = this.executor;
                Lifecycle lifecycle = getLifecycle();
                ControlsListingController controlsListingController = this.listingController;
                LayoutInflater from = LayoutInflater.from(this);
                Intrinsics.checkNotNullExpressionValue(from, "from(this)");
                ControlsProviderSelectorActivity$onStart$1 controlsProviderSelectorActivity$onStart$1 = new ControlsProviderSelectorActivity$onStart$1(this);
                Resources resources = getResources();
                Intrinsics.checkNotNullExpressionValue(resources, "resources");
                FavoritesRenderer favoritesRenderer = new FavoritesRenderer(resources, new ControlsProviderSelectorActivity$onStart$2(this.controlsController));
                Resources resources2 = getResources();
                Intrinsics.checkNotNullExpressionValue(resources2, "resources");
                AppAdapter appAdapter = new AppAdapter(executor2, executor3, lifecycle, controlsListingController, from, controlsProviderSelectorActivity$onStart$1, favoritesRenderer, resources2);
                appAdapter.registerAdapterDataObserver(new ControlsProviderSelectorActivity$onStart$3$1(this));
                Unit unit = Unit.INSTANCE;
                recyclerView3.setAdapter(appAdapter);
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("recyclerView");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("recyclerView");
        throw null;
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.currentUserTracker.stopTracking();
    }

    public final void launchFavoritingActivity(@Nullable ComponentName componentName) {
        this.executor.execute(new ControlsProviderSelectorActivity$launchFavoritingActivity$1(componentName, this));
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.currentUserTracker.stopTracking();
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public final void animateExitAndFinish() {
        ViewGroup viewGroup = (ViewGroup) requireViewById(R$id.controls_management_root);
        ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(viewGroup, "rootView");
        ControlsAnimations.exitAnimation(viewGroup, new ControlsProviderSelectorActivity$animateExitAndFinish$1(this)).start();
    }
}
