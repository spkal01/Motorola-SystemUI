package com.android.systemui.controls.p004ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.service.controls.Control;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Space;
import android.widget.TextView;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.controls.ControlsMetricsLogger;
import com.android.systemui.controls.CustomIconCache;
import com.android.systemui.controls.controller.ControlInfo;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.controller.StructureInfo;
import com.android.systemui.controls.management.ControlsEditingActivity;
import com.android.systemui.controls.management.ControlsFavoritingActivity;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.Lazy;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl implements ControlsUiController {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private static final ComponentName EMPTY_COMPONENT;
    /* access modifiers changed from: private */
    @NotNull
    public static final StructureInfo EMPTY_STRUCTURE;
    /* access modifiers changed from: private */
    public Context activityContext;
    @NotNull
    private final ActivityStarter activityStarter;
    private List<StructureInfo> allStructures;
    @NotNull
    private final DelayableExecutor bgExecutor;
    private final Collator collator;
    @NotNull
    private final Context context;
    @NotNull
    private final ControlActionCoordinator controlActionCoordinator;
    /* access modifiers changed from: private */
    @NotNull
    public final Map<ControlKey, ControlViewHolder> controlViewsById = new LinkedHashMap();
    /* access modifiers changed from: private */
    @NotNull
    public final Map<ControlKey, ControlWithState> controlsById = new LinkedHashMap();
    @NotNull
    private final Lazy<ControlsController> controlsController;
    @NotNull
    private final Lazy<ControlsListingController> controlsListingController;
    @NotNull
    private final ControlsMetricsLogger controlsMetricsLogger;
    private boolean hidden = true;
    @NotNull
    private final CustomIconCache iconCache;
    @NotNull
    private final KeyguardStateController keyguardStateController;
    private ControlsListingController.ControlsListingCallback listingCallback;
    @NotNull
    private final Comparator<SelectionItem> localeComparator;
    /* access modifiers changed from: private */
    public Runnable onDismiss;
    @NotNull
    private final Consumer<Boolean> onSeedingComplete;
    /* access modifiers changed from: private */
    public ViewGroup parent;
    /* access modifiers changed from: private */
    @Nullable
    public ListPopupWindow popup;
    /* access modifiers changed from: private */
    @NotNull
    public final ContextThemeWrapper popupThemedContext;
    private boolean retainCache;
    /* access modifiers changed from: private */
    @NotNull
    public StructureInfo selectedStructure = EMPTY_STRUCTURE;
    @NotNull
    private final ShadeController shadeController;
    @NotNull
    private final SharedPreferences sharedPreferences;
    @NotNull
    private final DelayableExecutor uiExecutor;

    public ControlsUiControllerImpl(@NotNull Lazy<ControlsController> lazy, @NotNull Context context2, @NotNull DelayableExecutor delayableExecutor, @NotNull DelayableExecutor delayableExecutor2, @NotNull Lazy<ControlsListingController> lazy2, @NotNull SharedPreferences sharedPreferences2, @NotNull ControlActionCoordinator controlActionCoordinator2, @NotNull ActivityStarter activityStarter2, @NotNull ShadeController shadeController2, @NotNull CustomIconCache customIconCache, @NotNull ControlsMetricsLogger controlsMetricsLogger2, @NotNull KeyguardStateController keyguardStateController2) {
        Intrinsics.checkNotNullParameter(lazy, "controlsController");
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(delayableExecutor, "uiExecutor");
        Intrinsics.checkNotNullParameter(delayableExecutor2, "bgExecutor");
        Intrinsics.checkNotNullParameter(lazy2, "controlsListingController");
        Intrinsics.checkNotNullParameter(sharedPreferences2, "sharedPreferences");
        Intrinsics.checkNotNullParameter(controlActionCoordinator2, "controlActionCoordinator");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(shadeController2, "shadeController");
        Intrinsics.checkNotNullParameter(customIconCache, "iconCache");
        Intrinsics.checkNotNullParameter(controlsMetricsLogger2, "controlsMetricsLogger");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        this.controlsController = lazy;
        this.context = context2;
        this.uiExecutor = delayableExecutor;
        this.bgExecutor = delayableExecutor2;
        this.controlsListingController = lazy2;
        this.sharedPreferences = sharedPreferences2;
        this.controlActionCoordinator = controlActionCoordinator2;
        this.activityStarter = activityStarter2;
        this.shadeController = shadeController2;
        this.iconCache = customIconCache;
        this.controlsMetricsLogger = controlsMetricsLogger2;
        this.keyguardStateController = keyguardStateController2;
        this.popupThemedContext = new ContextThemeWrapper(context2, R$style.Control_ListPopupWindow);
        Collator instance = Collator.getInstance(context2.getResources().getConfiguration().getLocales().get(0));
        this.collator = instance;
        Intrinsics.checkNotNullExpressionValue(instance, "collator");
        this.localeComparator = new ControlsUiControllerImpl$special$$inlined$compareBy$1(instance);
        this.onSeedingComplete = new ControlsUiControllerImpl$onSeedingComplete$1(this);
    }

    @NotNull
    public final Lazy<ControlsController> getControlsController() {
        return this.controlsController;
    }

    @NotNull
    public final DelayableExecutor getUiExecutor() {
        return this.uiExecutor;
    }

    @NotNull
    public final DelayableExecutor getBgExecutor() {
        return this.bgExecutor;
    }

    @NotNull
    public final Lazy<ControlsListingController> getControlsListingController() {
        return this.controlsListingController;
    }

    @NotNull
    public final ControlActionCoordinator getControlActionCoordinator() {
        return this.controlActionCoordinator;
    }

    /* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$Companion */
    /* compiled from: ControlsUiControllerImpl.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    static {
        ComponentName componentName = new ComponentName("", "");
        EMPTY_COMPONENT = componentName;
        EMPTY_STRUCTURE = new StructureInfo(componentName, "", new ArrayList());
    }

    private final ControlsListingController.ControlsListingCallback createCallback(Function1<? super List<SelectionItem>, Unit> function1) {
        return new ControlsUiControllerImpl$createCallback$1(this, function1);
    }

    public void show(@NotNull ViewGroup viewGroup, @NotNull Runnable runnable, @NotNull Context context2) {
        Intrinsics.checkNotNullParameter(viewGroup, "parent");
        Intrinsics.checkNotNullParameter(runnable, "onDismiss");
        Intrinsics.checkNotNullParameter(context2, "activityContext");
        Log.d("ControlsUiController", "show()");
        this.parent = viewGroup;
        this.onDismiss = runnable;
        this.activityContext = context2;
        this.hidden = false;
        this.retainCache = false;
        this.controlActionCoordinator.setActivityContext(context2);
        List<StructureInfo> favorites = this.controlsController.get().getFavorites();
        this.allStructures = favorites;
        if (favorites != null) {
            this.selectedStructure = getPreferredStructure(favorites);
            if (this.controlsController.get().addSeedingFavoritesCallback(this.onSeedingComplete)) {
                this.listingCallback = createCallback(new ControlsUiControllerImpl$show$1(this));
            } else {
                if (this.selectedStructure.getControls().isEmpty()) {
                    List<StructureInfo> list = this.allStructures;
                    if (list == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("allStructures");
                        throw null;
                    } else if (list.size() <= 1) {
                        this.listingCallback = createCallback(new ControlsUiControllerImpl$show$2(this));
                    }
                }
                List<ControlInfo> controls = this.selectedStructure.getControls();
                ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(controls, 10));
                for (ControlInfo controlWithState : controls) {
                    arrayList.add(new ControlWithState(this.selectedStructure.getComponentName(), controlWithState, (Control) null));
                }
                Map<ControlKey, ControlWithState> map = this.controlsById;
                for (Object next : arrayList) {
                    map.put(new ControlKey(this.selectedStructure.getComponentName(), ((ControlWithState) next).getCi().getControlId()), next);
                }
                this.listingCallback = createCallback(new ControlsUiControllerImpl$show$5(this));
                this.controlsController.get().subscribeToFavorites(this.selectedStructure);
            }
            ControlsListingController controlsListingController2 = this.controlsListingController.get();
            ControlsListingController.ControlsListingCallback controlsListingCallback = this.listingCallback;
            if (controlsListingCallback != null) {
                controlsListingController2.addCallback(controlsListingCallback);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("allStructures");
            throw null;
        }
    }

    /* access modifiers changed from: private */
    public final void reload(ViewGroup viewGroup) {
        if (!this.hidden) {
            ControlsListingController controlsListingController2 = this.controlsListingController.get();
            ControlsListingController.ControlsListingCallback controlsListingCallback = this.listingCallback;
            if (controlsListingCallback != null) {
                controlsListingController2.removeCallback(controlsListingCallback);
                this.controlsController.get().unsubscribe();
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(viewGroup, "alpha", new float[]{1.0f, 0.0f});
                ofFloat.setInterpolator(new AccelerateInterpolator(1.0f));
                ofFloat.setDuration(200);
                ofFloat.addListener(new ControlsUiControllerImpl$reload$1(this, viewGroup));
                ofFloat.start();
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
            throw null;
        }
    }

    /* access modifiers changed from: private */
    public final void showSeedingView(List<SelectionItem> list) {
        LayoutInflater from = LayoutInflater.from(this.context);
        int i = R$layout.controls_no_favorites;
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            from.inflate(i, viewGroup, true);
            ViewGroup viewGroup2 = this.parent;
            if (viewGroup2 != null) {
                ((TextView) viewGroup2.requireViewById(R$id.controls_subtitle)).setText(this.context.getResources().getString(R$string.controls_seeding_in_progress));
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("parent");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
    }

    /* access modifiers changed from: private */
    public final void showInitialSetupView(List<SelectionItem> list) {
        startProviderSelectorActivity();
        Runnable runnable = this.onDismiss;
        if (runnable != null) {
            runnable.run();
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("onDismiss");
            throw null;
        }
    }

    /* access modifiers changed from: private */
    public final void startFavoritingActivity(StructureInfo structureInfo) {
        startTargetedActivity(structureInfo, ControlsFavoritingActivity.class);
    }

    /* access modifiers changed from: private */
    public final void startEditingActivity(StructureInfo structureInfo) {
        startTargetedActivity(structureInfo, ControlsEditingActivity.class);
    }

    private final void startTargetedActivity(StructureInfo structureInfo, Class<?> cls) {
        Context context2 = this.activityContext;
        if (context2 != null) {
            Intent intent = new Intent(context2, cls);
            putIntentExtras(intent, structureInfo);
            startActivity(intent);
            this.retainCache = true;
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("activityContext");
        throw null;
    }

    private final void putIntentExtras(Intent intent, StructureInfo structureInfo) {
        intent.putExtra("extra_app_label", getControlsListingController().get().getAppLabel(structureInfo.getComponentName()));
        intent.putExtra("extra_structure", structureInfo.getStructure());
        intent.putExtra("android.intent.extra.COMPONENT_NAME", structureInfo.getComponentName());
    }

    private final void startProviderSelectorActivity() {
        Context context2 = this.activityContext;
        if (context2 != null) {
            Intent intent = new Intent(context2, ControlsProviderSelectorActivity.class);
            intent.putExtra("back_should_exit", true);
            startActivity(intent);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("activityContext");
        throw null;
    }

    private final SelectionItem findSelectionItem(StructureInfo structureInfo, List<SelectionItem> list) {
        T t;
        boolean z;
        Iterator<T> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                t = null;
                break;
            }
            t = it.next();
            SelectionItem selectionItem = (SelectionItem) t;
            if (!Intrinsics.areEqual((Object) selectionItem.getComponentName(), (Object) structureInfo.getComponentName()) || !Intrinsics.areEqual((Object) selectionItem.getStructure(), (Object) structureInfo.getStructure())) {
                z = false;
                continue;
            } else {
                z = true;
                continue;
            }
            if (z) {
                break;
            }
        }
        return (SelectionItem) t;
    }

    private final void startActivity(Intent intent) {
        intent.putExtra("extra_animate", true);
        if (this.keyguardStateController.isUnlocked()) {
            Context context2 = this.activityContext;
            if (context2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("activityContext");
                throw null;
            } else if (context2 != null) {
                context2.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context2, new Pair[0]).toBundle());
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("activityContext");
                throw null;
            }
        } else {
            this.activityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
    }

    /* access modifiers changed from: private */
    public final void showControlsView(List<SelectionItem> list) {
        this.controlViewsById.clear();
        LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt___RangesKt.coerceAtLeast(MapsKt__MapsJVMKt.mapCapacity(CollectionsKt__IterablesKt.collectionSizeOrDefault(list, 10)), 16));
        for (T next : list) {
            linkedHashMap.put(((SelectionItem) next).getComponentName(), next);
        }
        ArrayList arrayList = new ArrayList();
        List<StructureInfo> list2 = this.allStructures;
        if (list2 != null) {
            for (StructureInfo structureInfo : list2) {
                SelectionItem selectionItem = (SelectionItem) linkedHashMap.get(structureInfo.getComponentName());
                SelectionItem copy$default = selectionItem == null ? null : SelectionItem.copy$default(selectionItem, (CharSequence) null, structureInfo.getStructure(), (Drawable) null, (ComponentName) null, 0, 29, (Object) null);
                if (copy$default != null) {
                    arrayList.add(copy$default);
                }
            }
            CollectionsKt__MutableCollectionsJVMKt.sortWith(arrayList, this.localeComparator);
            SelectionItem findSelectionItem = findSelectionItem(this.selectedStructure, arrayList);
            if (findSelectionItem == null) {
                findSelectionItem = list.get(0);
            }
            this.controlsMetricsLogger.refreshBegin(findSelectionItem.getUid(), !this.keyguardStateController.isUnlocked());
            createListView(findSelectionItem);
            createDropDown(arrayList, findSelectionItem);
            createMenu();
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("allStructures");
        throw null;
    }

    private final void createMenu() {
        String[] strArr = {this.context.getResources().getString(R$string.controls_menu_add), this.context.getResources().getString(R$string.controls_menu_edit)};
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        ref$ObjectRef.element = new ArrayAdapter(this.context, R$layout.controls_more_item, strArr);
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            ImageView imageView = (ImageView) viewGroup.requireViewById(R$id.controls_more);
            imageView.setOnClickListener(new ControlsUiControllerImpl$createMenu$1(this, imageView, ref$ObjectRef));
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    private final void createListView(SelectionItem selectionItem) {
        LayoutInflater from = LayoutInflater.from(this.context);
        int i = R$layout.controls_with_favorites;
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            from.inflate(i, viewGroup, true);
            ViewGroup viewGroup2 = this.parent;
            if (viewGroup2 != null) {
                ImageView imageView = (ImageView) viewGroup2.requireViewById(R$id.controls_close);
                imageView.setOnClickListener(new ControlsUiControllerImpl$createListView$1$1(this));
                imageView.setVisibility(0);
                int findMaxColumns = findMaxColumns();
                ViewGroup viewGroup3 = this.parent;
                if (viewGroup3 != null) {
                    View requireViewById = viewGroup3.requireViewById(R$id.global_actions_controls_list);
                    Objects.requireNonNull(requireViewById, "null cannot be cast to non-null type android.view.ViewGroup");
                    ViewGroup viewGroup4 = (ViewGroup) requireViewById;
                    Intrinsics.checkNotNullExpressionValue(from, "inflater");
                    ViewGroup createRow = createRow(from, viewGroup4);
                    for (ControlInfo controlId : this.selectedStructure.getControls()) {
                        ControlKey controlKey = new ControlKey(this.selectedStructure.getComponentName(), controlId.getControlId());
                        ControlWithState controlWithState = this.controlsById.get(controlKey);
                        if (controlWithState != null) {
                            if (createRow.getChildCount() == findMaxColumns) {
                                createRow = createRow(from, viewGroup4);
                            }
                            View inflate = from.inflate(R$layout.controls_base_item, createRow, false);
                            Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.ViewGroup");
                            ViewGroup viewGroup5 = (ViewGroup) inflate;
                            createRow.addView(viewGroup5);
                            if (createRow.getChildCount() == 1) {
                                ViewGroup.LayoutParams layoutParams = viewGroup5.getLayoutParams();
                                Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
                                ((ViewGroup.MarginLayoutParams) layoutParams).setMarginStart(0);
                            }
                            ControlsController controlsController2 = getControlsController().get();
                            Intrinsics.checkNotNullExpressionValue(controlsController2, "controlsController.get()");
                            ControlViewHolder controlViewHolder = new ControlViewHolder(viewGroup5, controlsController2, getUiExecutor(), getBgExecutor(), getControlActionCoordinator(), this.controlsMetricsLogger, selectionItem.getUid());
                            controlViewHolder.bindData(controlWithState, false);
                            this.controlViewsById.put(controlKey, controlViewHolder);
                        }
                    }
                    int size = this.selectedStructure.getControls().size() % findMaxColumns;
                    int dimensionPixelSize = this.context.getResources().getDimensionPixelSize(R$dimen.control_spacing);
                    for (int i2 = size == 0 ? 0 : findMaxColumns - size; i2 > 0; i2--) {
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, 0, 1.0f);
                        layoutParams2.setMarginStart(dimensionPixelSize);
                        createRow.addView(new Space(this.context), layoutParams2);
                    }
                    return;
                }
                Intrinsics.throwUninitializedPropertyAccessException("parent");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x002d, code lost:
        r3 = r5.screenWidthDp;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int findMaxColumns() {
        /*
            r5 = this;
            android.content.Context r5 = r5.context
            android.content.res.Resources r5 = r5.getResources()
            int r0 = com.android.systemui.R$integer.controls_max_columns
            int r0 = r5.getInteger(r0)
            int r1 = com.android.systemui.R$integer.controls_max_columns_adjust_below_width_dp
            int r1 = r5.getInteger(r1)
            android.util.TypedValue r2 = new android.util.TypedValue
            r2.<init>()
            int r3 = com.android.systemui.R$dimen.controls_max_columns_adjust_above_font_scale
            r4 = 1
            r5.getValue(r3, r2, r4)
            float r2 = r2.getFloat()
            android.content.res.Configuration r5 = r5.getConfiguration()
            int r3 = r5.orientation
            if (r3 != r4) goto L_0x002a
            goto L_0x002b
        L_0x002a:
            r4 = 0
        L_0x002b:
            if (r4 == 0) goto L_0x003b
            int r3 = r5.screenWidthDp
            if (r3 == 0) goto L_0x003b
            if (r3 > r1) goto L_0x003b
            float r5 = r5.fontScale
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 < 0) goto L_0x003b
            int r0 = r0 + -1
        L_0x003b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlsUiControllerImpl.findMaxColumns():int");
    }

    @NotNull
    public StructureInfo getPreferredStructure(@NotNull List<StructureInfo> list) {
        ComponentName componentName;
        boolean z;
        Intrinsics.checkNotNullParameter(list, "structures");
        if (list.isEmpty()) {
            return EMPTY_STRUCTURE;
        }
        T t = null;
        String string = this.sharedPreferences.getString("controls_component", (String) null);
        if (string == null) {
            componentName = null;
        } else {
            componentName = ComponentName.unflattenFromString(string);
        }
        if (componentName == null) {
            componentName = EMPTY_COMPONENT;
        }
        String string2 = this.sharedPreferences.getString("controls_structure", "");
        Iterator<T> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            T next = it.next();
            StructureInfo structureInfo = (StructureInfo) next;
            if (!Intrinsics.areEqual((Object) componentName, (Object) structureInfo.getComponentName()) || !Intrinsics.areEqual((Object) string2, (Object) structureInfo.getStructure())) {
                z = false;
                continue;
            } else {
                z = true;
                continue;
            }
            if (z) {
                t = next;
                break;
            }
        }
        StructureInfo structureInfo2 = (StructureInfo) t;
        return structureInfo2 == null ? list.get(0) : structureInfo2;
    }

    /* access modifiers changed from: private */
    public final void updatePreferences(StructureInfo structureInfo) {
        if (!Intrinsics.areEqual((Object) structureInfo, (Object) EMPTY_STRUCTURE)) {
            this.sharedPreferences.edit().putString("controls_component", structureInfo.getComponentName().flattenToString()).putString("controls_structure", structureInfo.getStructure().toString()).commit();
        }
    }

    /* access modifiers changed from: private */
    public final void switchAppOrStructure(SelectionItem selectionItem) {
        boolean z;
        List<StructureInfo> list = this.allStructures;
        if (list != null) {
            for (StructureInfo structureInfo : list) {
                if (!Intrinsics.areEqual((Object) structureInfo.getStructure(), (Object) selectionItem.getStructure()) || !Intrinsics.areEqual((Object) structureInfo.getComponentName(), (Object) selectionItem.getComponentName())) {
                    z = false;
                    continue;
                } else {
                    z = true;
                    continue;
                }
                if (z) {
                    if (!Intrinsics.areEqual((Object) structureInfo, (Object) this.selectedStructure)) {
                        this.selectedStructure = structureInfo;
                        updatePreferences(structureInfo);
                        ViewGroup viewGroup = this.parent;
                        if (viewGroup != null) {
                            reload(viewGroup);
                            return;
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException("parent");
                            throw null;
                        }
                    } else {
                        return;
                    }
                }
            }
            throw new NoSuchElementException("Collection contains no element matching the predicate.");
        }
        Intrinsics.throwUninitializedPropertyAccessException("allStructures");
        throw null;
    }

    public void closeDialogs(boolean z) {
        if (z) {
            ListPopupWindow listPopupWindow = this.popup;
            if (listPopupWindow != null) {
                listPopupWindow.dismissImmediate();
            }
        } else {
            ListPopupWindow listPopupWindow2 = this.popup;
            if (listPopupWindow2 != null) {
                listPopupWindow2.dismiss();
            }
        }
        this.popup = null;
        for (Map.Entry<ControlKey, ControlViewHolder> value : this.controlViewsById.entrySet()) {
            ((ControlViewHolder) value.getValue()).dismiss();
        }
        this.controlActionCoordinator.closeDialogs();
    }

    public void hide() {
        this.hidden = true;
        closeDialogs(true);
        this.controlsController.get().unsubscribe();
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            viewGroup.removeAllViews();
            this.controlsById.clear();
            this.controlViewsById.clear();
            ControlsListingController controlsListingController2 = this.controlsListingController.get();
            ControlsListingController.ControlsListingCallback controlsListingCallback = this.listingCallback;
            if (controlsListingCallback != null) {
                controlsListingController2.removeCallback(controlsListingCallback);
                if (!this.retainCache) {
                    RenderInfo.Companion.clearCache();
                    return;
                }
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("listingCallback");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }

    public void onRefreshState(@NotNull ComponentName componentName, @NotNull List<Control> list) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(list, "controls");
        boolean z = !this.keyguardStateController.isUnlocked();
        for (Control control : list) {
            Map<ControlKey, ControlWithState> map = this.controlsById;
            String controlId = control.getControlId();
            Intrinsics.checkNotNullExpressionValue(controlId, "c.getControlId()");
            ControlWithState controlWithState = map.get(new ControlKey(componentName, controlId));
            if (controlWithState != null) {
                Log.d("ControlsUiController", Intrinsics.stringPlus("onRefreshState() for id: ", control.getControlId()));
                CustomIconCache customIconCache = this.iconCache;
                String controlId2 = control.getControlId();
                Intrinsics.checkNotNullExpressionValue(controlId2, "c.controlId");
                customIconCache.store(componentName, controlId2, control.getCustomIcon());
                ControlWithState controlWithState2 = new ControlWithState(componentName, controlWithState.getCi(), control);
                String controlId3 = control.getControlId();
                Intrinsics.checkNotNullExpressionValue(controlId3, "c.getControlId()");
                ControlKey controlKey = new ControlKey(componentName, controlId3);
                this.controlsById.put(controlKey, controlWithState2);
                ControlViewHolder controlViewHolder = this.controlViewsById.get(controlKey);
                if (controlViewHolder != null) {
                    getUiExecutor().execute(new ControlsUiControllerImpl$onRefreshState$1$1$1$1(controlViewHolder, controlWithState2, z));
                }
            }
        }
    }

    public void onActionResponse(@NotNull ComponentName componentName, @NotNull String str, int i) {
        Intrinsics.checkNotNullParameter(componentName, "componentName");
        Intrinsics.checkNotNullParameter(str, "controlId");
        this.uiExecutor.execute(new ControlsUiControllerImpl$onActionResponse$1(this, new ControlKey(componentName, str), i));
    }

    private final ViewGroup createRow(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View inflate = layoutInflater.inflate(R$layout.controls_row, viewGroup, false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.ViewGroup");
        ViewGroup viewGroup2 = (ViewGroup) inflate;
        viewGroup.addView(viewGroup2);
        return viewGroup2;
    }

    private final void createDropDown(List<SelectionItem> list, SelectionItem selectionItem) {
        for (SelectionItem selectionItem2 : list) {
            RenderInfo.Companion.registerComponentIcon(selectionItem2.getComponentName(), selectionItem2.getIcon());
        }
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        T itemAdapter = new ItemAdapter(this.context, R$layout.controls_spinner_item);
        itemAdapter.addAll(list);
        Unit unit = Unit.INSTANCE;
        ref$ObjectRef.element = itemAdapter;
        ViewGroup viewGroup = this.parent;
        if (viewGroup != null) {
            TextView textView = (TextView) viewGroup.requireViewById(R$id.app_or_structure_spinner);
            textView.setText(selectionItem.getTitle());
            Drawable background = textView.getBackground();
            Objects.requireNonNull(background, "null cannot be cast to non-null type android.graphics.drawable.LayerDrawable");
            ((LayerDrawable) background).getDrawable(0).setTint(textView.getContext().getResources().getColor(R$color.control_spinner_dropdown, (Resources.Theme) null));
            if (list.size() == 1) {
                textView.setBackground((Drawable) null);
                return;
            }
            ViewGroup viewGroup2 = this.parent;
            if (viewGroup2 != null) {
                ViewGroup viewGroup3 = (ViewGroup) viewGroup2.requireViewById(R$id.controls_header);
                viewGroup3.setOnClickListener(new ControlsUiControllerImpl$createDropDown$2(this, viewGroup3, ref$ObjectRef));
                return;
            }
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
}
