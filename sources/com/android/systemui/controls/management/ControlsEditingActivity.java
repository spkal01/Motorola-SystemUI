package com.android.systemui.controls.management;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.CustomIconCache;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.controls.controller.StructureInfo;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.util.LifecycleActivity;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ControlsEditingActivity.kt */
public final class ControlsEditingActivity extends LifecycleActivity {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final int EMPTY_TEXT_ID = R$string.controls_favorite_removed;
    /* access modifiers changed from: private */
    public static final int SUBTITLE_ID = R$string.controls_favorite_rearrange;
    @NotNull
    private final BroadcastDispatcher broadcastDispatcher;
    private ComponentName component;
    /* access modifiers changed from: private */
    @NotNull
    public final ControlsControllerImpl controller;
    @NotNull
    private final ControlsEditingActivity$currentUserTracker$1 currentUserTracker;
    @NotNull
    private final CustomIconCache customIconCache;
    @NotNull
    private final ControlsEditingActivity$favoritesModelCallback$1 favoritesModelCallback = new ControlsEditingActivity$favoritesModelCallback$1(this);
    private FavoritesModel model;
    /* access modifiers changed from: private */
    public View saveButton;
    private CharSequence structure;
    /* access modifiers changed from: private */
    public TextView subtitle;
    @NotNull
    private final ControlsUiController uiController;

    public ControlsEditingActivity(@NotNull ControlsControllerImpl controlsControllerImpl, @NotNull BroadcastDispatcher broadcastDispatcher2, @NotNull CustomIconCache customIconCache2, @NotNull ControlsUiController controlsUiController) {
        Intrinsics.checkNotNullParameter(controlsControllerImpl, "controller");
        Intrinsics.checkNotNullParameter(broadcastDispatcher2, "broadcastDispatcher");
        Intrinsics.checkNotNullParameter(customIconCache2, "customIconCache");
        Intrinsics.checkNotNullParameter(controlsUiController, "uiController");
        this.controller = controlsControllerImpl;
        this.broadcastDispatcher = broadcastDispatcher2;
        this.customIconCache = customIconCache2;
        this.uiController = controlsUiController;
        this.currentUserTracker = new ControlsEditingActivity$currentUserTracker$1(this, broadcastDispatcher2);
    }

    /* compiled from: ControlsEditingActivity.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        Unit unit;
        super.onCreate(bundle);
        ComponentName componentName = (ComponentName) getIntent().getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        Unit unit2 = null;
        if (componentName == null) {
            unit = null;
        } else {
            this.component = componentName;
            unit = Unit.INSTANCE;
        }
        if (unit == null) {
            finish();
        }
        CharSequence charSequenceExtra = getIntent().getCharSequenceExtra("extra_structure");
        if (charSequenceExtra != null) {
            this.structure = charSequenceExtra;
            unit2 = Unit.INSTANCE;
        }
        if (unit2 == null) {
            finish();
        }
        bindViews();
        bindButtons();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        setUpList();
        this.currentUserTracker.startTracking();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.currentUserTracker.stopTracking();
    }

    public void onBackPressed() {
        animateExitAndFinish();
    }

    /* access modifiers changed from: private */
    public final void animateExitAndFinish() {
        ViewGroup viewGroup = (ViewGroup) requireViewById(R$id.controls_management_root);
        ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(viewGroup, "rootView");
        ControlsAnimations.exitAnimation(viewGroup, new ControlsEditingActivity$animateExitAndFinish$1(this)).start();
    }

    private final void bindViews() {
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
        viewStub.setLayoutResource(R$layout.controls_management_editing);
        viewStub.inflate();
        TextView textView = (TextView) requireViewById(R$id.title);
        CharSequence charSequence = this.structure;
        if (charSequence != null) {
            textView.setText(charSequence);
            CharSequence charSequence2 = this.structure;
            if (charSequence2 != null) {
                setTitle(charSequence2);
                View requireViewById2 = requireViewById(R$id.subtitle);
                TextView textView2 = (TextView) requireViewById2;
                textView2.setText(SUBTITLE_ID);
                Unit unit = Unit.INSTANCE;
                Intrinsics.checkNotNullExpressionValue(requireViewById2, "requireViewById<TextView>(R.id.subtitle).apply {\n            setText(SUBTITLE_ID)\n        }");
                this.subtitle = textView2;
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("structure");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("structure");
        throw null;
    }

    private final void bindButtons() {
        View requireViewById = requireViewById(R$id.done);
        Button button = (Button) requireViewById;
        button.setEnabled(false);
        button.setText(R$string.save);
        button.setOnClickListener(new ControlsEditingActivity$bindButtons$1$1(this));
        Unit unit = Unit.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(requireViewById, "requireViewById<Button>(R.id.done).apply {\n            isEnabled = false\n            setText(R.string.save)\n            setOnClickListener {\n                saveFavorites()\n                startActivity(\n                    Intent(applicationContext, ControlsActivity::class.java),\n                    ActivityOptions\n                        .makeSceneTransitionAnimation(this@ControlsEditingActivity).toBundle()\n                )\n                animateExitAndFinish()\n            }\n        }");
        this.saveButton = requireViewById;
    }

    /* access modifiers changed from: private */
    public final void saveFavorites() {
        ControlsControllerImpl controlsControllerImpl = this.controller;
        ComponentName componentName = this.component;
        if (componentName != null) {
            CharSequence charSequence = this.structure;
            if (charSequence != null) {
                FavoritesModel favoritesModel = this.model;
                if (favoritesModel != null) {
                    controlsControllerImpl.replaceFavoritesForStructure(new StructureInfo(componentName, charSequence, favoritesModel.getFavorites()));
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("model");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("structure");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("component");
            throw null;
        }
    }

    private final void setUpList() {
        ControlsControllerImpl controlsControllerImpl = this.controller;
        ComponentName componentName = this.component;
        if (componentName != null) {
            CharSequence charSequence = this.structure;
            if (charSequence != null) {
                List<ControlInfo> favoritesForStructure = controlsControllerImpl.getFavoritesForStructure(componentName, charSequence);
                CustomIconCache customIconCache2 = this.customIconCache;
                ComponentName componentName2 = this.component;
                if (componentName2 != null) {
                    this.model = new FavoritesModel(customIconCache2, componentName2, favoritesForStructure, this.favoritesModelCallback);
                    float f = getResources().getFloat(R$dimen.control_card_elevation);
                    RecyclerView recyclerView = (RecyclerView) requireViewById(R$id.list);
                    recyclerView.setAlpha(0.0f);
                    ControlAdapter controlAdapter = new ControlAdapter(f);
                    controlAdapter.registerAdapterDataObserver(new ControlsEditingActivity$setUpList$adapter$1$1(recyclerView));
                    int dimensionPixelSize = getResources().getDimensionPixelSize(R$dimen.controls_card_margin);
                    MarginItemDecorator marginItemDecorator = new MarginItemDecorator(dimensionPixelSize, dimensionPixelSize);
                    recyclerView.setAdapter(controlAdapter);
                    ControlsEditingActivity$setUpList$1$1 controlsEditingActivity$setUpList$1$1 = new ControlsEditingActivity$setUpList$1$1(recyclerView.getContext());
                    controlsEditingActivity$setUpList$1$1.setSpanSizeLookup(controlAdapter.getSpanSizeLookup());
                    Unit unit = Unit.INSTANCE;
                    recyclerView.setLayoutManager(controlsEditingActivity$setUpList$1$1);
                    recyclerView.addItemDecoration(marginItemDecorator);
                    FavoritesModel favoritesModel = this.model;
                    if (favoritesModel != null) {
                        controlAdapter.changeModel(favoritesModel);
                        FavoritesModel favoritesModel2 = this.model;
                        if (favoritesModel2 != null) {
                            favoritesModel2.attachAdapter(controlAdapter);
                            FavoritesModel favoritesModel3 = this.model;
                            if (favoritesModel3 != null) {
                                new ItemTouchHelper(favoritesModel3.getItemTouchHelperCallback()).attachToRecyclerView(recyclerView);
                            } else {
                                Intrinsics.throwUninitializedPropertyAccessException("model");
                                throw null;
                            }
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException("model");
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("model");
                        throw null;
                    }
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("component");
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("structure");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("component");
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.currentUserTracker.stopTracking();
        super.onDestroy();
    }
}
