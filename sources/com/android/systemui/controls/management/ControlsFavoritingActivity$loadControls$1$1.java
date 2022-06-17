package com.android.systemui.controls.management;

import android.animation.Animator;
import android.content.res.Resources;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;
import com.android.systemui.R$string;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlsController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$loadControls$1$1 implements Consumer<ControlsController.LoadData> {
    final /* synthetic */ CharSequence $emptyZoneString;
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$loadControls$1$1(ControlsFavoritingActivity controlsFavoritingActivity, CharSequence charSequence) {
        this.this$0 = controlsFavoritingActivity;
        this.$emptyZoneString = charSequence;
    }

    public final void accept(@NotNull ControlsController.LoadData loadData) {
        Intrinsics.checkNotNullParameter(loadData, "data");
        List<ControlStatus> allControls = loadData.getAllControls();
        List<String> favoritesIds = loadData.getFavoritesIds();
        final boolean errorOnLoad = loadData.getErrorOnLoad();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (T next : allControls) {
            Object structure = ((ControlStatus) next).getControl().getStructure();
            if (structure == null) {
                structure = "";
            }
            Object obj = linkedHashMap.get(structure);
            if (obj == null) {
                obj = new ArrayList();
                linkedHashMap.put(structure, obj);
            }
            ((List) obj).add(next);
        }
        ControlsFavoritingActivity controlsFavoritingActivity = this.this$0;
        CharSequence charSequence = this.$emptyZoneString;
        ArrayList arrayList = new ArrayList(linkedHashMap.size());
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            Intrinsics.checkNotNullExpressionValue(charSequence, "emptyZoneString");
            arrayList.add(new StructureContainer((CharSequence) entry.getKey(), new AllModel((List) entry.getValue(), favoritesIds, charSequence, controlsFavoritingActivity.controlsModelCallback)));
        }
        Comparator access$getComparator$p = this.this$0.comparator;
        if (access$getComparator$p != null) {
            controlsFavoritingActivity.listOfStructures = CollectionsKt___CollectionsKt.sortedWith(arrayList, access$getComparator$p);
            List access$getListOfStructures$p = this.this$0.listOfStructures;
            ControlsFavoritingActivity controlsFavoritingActivity2 = this.this$0;
            Iterator it = access$getListOfStructures$p.iterator();
            final int i = 0;
            while (true) {
                if (!it.hasNext()) {
                    i = -1;
                    break;
                } else if (Intrinsics.areEqual((Object) ((StructureContainer) it.next()).getStructureName(), (Object) controlsFavoritingActivity2.structureExtra)) {
                    break;
                } else {
                    i++;
                }
            }
            if (i == -1) {
                i = 0;
            }
            if (this.this$0.getIntent().getBooleanExtra("extra_single_structure", false)) {
                ControlsFavoritingActivity controlsFavoritingActivity3 = this.this$0;
                controlsFavoritingActivity3.listOfStructures = CollectionsKt__CollectionsJVMKt.listOf(controlsFavoritingActivity3.listOfStructures.get(i));
            }
            Executor access$getExecutor$p = this.this$0.executor;
            final ControlsFavoritingActivity controlsFavoritingActivity4 = this.this$0;
            access$getExecutor$p.execute(new Runnable() {
                public final void run() {
                    ViewPager2 access$getStructurePager$p = controlsFavoritingActivity4.structurePager;
                    if (access$getStructurePager$p != null) {
                        access$getStructurePager$p.setAdapter(new StructureAdapter(controlsFavoritingActivity4.listOfStructures));
                        ViewPager2 access$getStructurePager$p2 = controlsFavoritingActivity4.structurePager;
                        if (access$getStructurePager$p2 != null) {
                            access$getStructurePager$p2.setCurrentItem(i);
                            int i = 0;
                            if (errorOnLoad) {
                                TextView access$getStatusText$p = controlsFavoritingActivity4.statusText;
                                if (access$getStatusText$p != null) {
                                    Resources resources = controlsFavoritingActivity4.getResources();
                                    int i2 = R$string.controls_favorite_load_error;
                                    Object[] objArr = new Object[1];
                                    Object access$getAppName$p = controlsFavoritingActivity4.appName;
                                    if (access$getAppName$p == null) {
                                        access$getAppName$p = "";
                                    }
                                    objArr[0] = access$getAppName$p;
                                    access$getStatusText$p.setText(resources.getString(i2, objArr));
                                    TextView access$getSubtitleView$p = controlsFavoritingActivity4.subtitleView;
                                    if (access$getSubtitleView$p != null) {
                                        access$getSubtitleView$p.setVisibility(8);
                                    } else {
                                        Intrinsics.throwUninitializedPropertyAccessException("subtitleView");
                                        throw null;
                                    }
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException("statusText");
                                    throw null;
                                }
                            } else if (controlsFavoritingActivity4.listOfStructures.isEmpty()) {
                                TextView access$getStatusText$p2 = controlsFavoritingActivity4.statusText;
                                if (access$getStatusText$p2 != null) {
                                    access$getStatusText$p2.setText(controlsFavoritingActivity4.getResources().getString(R$string.controls_favorite_load_none));
                                    TextView access$getSubtitleView$p2 = controlsFavoritingActivity4.subtitleView;
                                    if (access$getSubtitleView$p2 != null) {
                                        access$getSubtitleView$p2.setVisibility(8);
                                    } else {
                                        Intrinsics.throwUninitializedPropertyAccessException("subtitleView");
                                        throw null;
                                    }
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException("statusText");
                                    throw null;
                                }
                            } else {
                                TextView access$getStatusText$p3 = controlsFavoritingActivity4.statusText;
                                if (access$getStatusText$p3 != null) {
                                    access$getStatusText$p3.setVisibility(8);
                                    ManagementPageIndicator access$getPageIndicator$p = controlsFavoritingActivity4.pageIndicator;
                                    if (access$getPageIndicator$p != null) {
                                        access$getPageIndicator$p.setNumPages(controlsFavoritingActivity4.listOfStructures.size());
                                        ManagementPageIndicator access$getPageIndicator$p2 = controlsFavoritingActivity4.pageIndicator;
                                        if (access$getPageIndicator$p2 != null) {
                                            access$getPageIndicator$p2.setLocation(0.0f);
                                            ManagementPageIndicator access$getPageIndicator$p3 = controlsFavoritingActivity4.pageIndicator;
                                            if (access$getPageIndicator$p3 != null) {
                                                if (controlsFavoritingActivity4.listOfStructures.size() <= 1) {
                                                    i = 4;
                                                }
                                                access$getPageIndicator$p3.setVisibility(i);
                                                ControlsAnimations controlsAnimations = ControlsAnimations.INSTANCE;
                                                ManagementPageIndicator access$getPageIndicator$p4 = controlsFavoritingActivity4.pageIndicator;
                                                if (access$getPageIndicator$p4 != null) {
                                                    Animator enterAnimation = controlsAnimations.enterAnimation(access$getPageIndicator$p4);
                                                    enterAnimation.addListener(new ControlsFavoritingActivity$loadControls$1$1$2$1$1(controlsFavoritingActivity4));
                                                    enterAnimation.start();
                                                    ViewPager2 access$getStructurePager$p3 = controlsFavoritingActivity4.structurePager;
                                                    if (access$getStructurePager$p3 != null) {
                                                        controlsAnimations.enterAnimation(access$getStructurePager$p3).start();
                                                    } else {
                                                        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
                                                        throw null;
                                                    }
                                                } else {
                                                    Intrinsics.throwUninitializedPropertyAccessException("pageIndicator");
                                                    throw null;
                                                }
                                            } else {
                                                Intrinsics.throwUninitializedPropertyAccessException("pageIndicator");
                                                throw null;
                                            }
                                        } else {
                                            Intrinsics.throwUninitializedPropertyAccessException("pageIndicator");
                                            throw null;
                                        }
                                    } else {
                                        Intrinsics.throwUninitializedPropertyAccessException("pageIndicator");
                                        throw null;
                                    }
                                } else {
                                    Intrinsics.throwUninitializedPropertyAccessException("statusText");
                                    throw null;
                                }
                            }
                        } else {
                            Intrinsics.throwUninitializedPropertyAccessException("structurePager");
                            throw null;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("structurePager");
                        throw null;
                    }
                }
            });
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("comparator");
        throw null;
    }
}
