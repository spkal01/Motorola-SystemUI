package com.android.systemui.controls.p004ui;

import android.view.ViewGroup;
import com.android.systemui.controls.controller.StructureInfo;
import java.util.Iterator;
import java.util.function.Consumer;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$onSeedingComplete$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$onSeedingComplete$1 implements Consumer<Boolean> {
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$onSeedingComplete$1(ControlsUiControllerImpl controlsUiControllerImpl) {
        this.this$0 = controlsUiControllerImpl;
    }

    public final void accept(Boolean bool) {
        T t;
        Intrinsics.checkNotNullExpressionValue(bool, "accepted");
        if (bool.booleanValue()) {
            ControlsUiControllerImpl controlsUiControllerImpl = this.this$0;
            Iterator<T> it = controlsUiControllerImpl.getControlsController().get().getFavorites().iterator();
            if (!it.hasNext()) {
                t = null;
            } else {
                t = it.next();
                if (it.hasNext()) {
                    int size = ((StructureInfo) t).getControls().size();
                    do {
                        T next = it.next();
                        int size2 = ((StructureInfo) next).getControls().size();
                        if (size < size2) {
                            t = next;
                            size = size2;
                        }
                    } while (it.hasNext());
                }
            }
            StructureInfo structureInfo = (StructureInfo) t;
            if (structureInfo == null) {
                structureInfo = ControlsUiControllerImpl.EMPTY_STRUCTURE;
            }
            controlsUiControllerImpl.selectedStructure = structureInfo;
            ControlsUiControllerImpl controlsUiControllerImpl2 = this.this$0;
            controlsUiControllerImpl2.updatePreferences(controlsUiControllerImpl2.selectedStructure);
        }
        ControlsUiControllerImpl controlsUiControllerImpl3 = this.this$0;
        ViewGroup access$getParent$p = controlsUiControllerImpl3.parent;
        if (access$getParent$p != null) {
            controlsUiControllerImpl3.reload(access$getParent$p);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("parent");
            throw null;
        }
    }
}
