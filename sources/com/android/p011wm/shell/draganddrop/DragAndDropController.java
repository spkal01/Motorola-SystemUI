package com.android.p011wm.shell.draganddrop;

import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import com.android.p011wm.shell.splitscreen.SplitScreenController;
import java.util.Optional;

/* renamed from: com.android.wm.shell.draganddrop.DragAndDropController */
public class DragAndDropController implements DisplayController.OnDisplaysChangedListener, View.OnDragListener {
    private static final String TAG = DragAndDropController.class.getSimpleName();
    private final Context mContext;
    private final DisplayController mDisplayController;
    private final SparseArray<PerDisplay> mDisplayDropTargets = new SparseArray<>();
    private SplitScreenController mSplitScreen;
    private final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();

    public DragAndDropController(Context context, DisplayController displayController) {
        this.mContext = context;
        this.mDisplayController = displayController;
    }

    public void initialize(Optional<SplitScreenController> optional) {
        this.mSplitScreen = optional.orElse((Object) null);
        this.mDisplayController.addDisplayWindowListener(this);
    }

    public void onDisplayAdded(int i) {
        if (ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP, -1006733970, 1, (String) null, Long.valueOf((long) i));
        }
        Context createWindowContext = this.mDisplayController.getDisplayContext(i).createWindowContext(2038, (Bundle) null);
        WindowManager windowManager = (WindowManager) createWindowContext.getSystemService(WindowManager.class);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2038, 16777224, -3);
        layoutParams.privateFlags |= -2147483568;
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.setTitle("ShellDropTarget");
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(createWindowContext).inflate(C2219R.layout.global_drop_target, (ViewGroup) null);
        frameLayout.setOnDragListener(this);
        frameLayout.setVisibility(4);
        DragLayout dragLayout = new DragLayout(createWindowContext, this.mSplitScreen);
        frameLayout.addView(dragLayout, new FrameLayout.LayoutParams(-1, -1));
        try {
            windowManager.addView(frameLayout, layoutParams);
            this.mDisplayDropTargets.put(i, new PerDisplay(i, createWindowContext, windowManager, frameLayout, dragLayout));
        } catch (WindowManager.InvalidDisplayException unused) {
            Slog.w(TAG, "Unable to add view for display id: " + i);
        }
    }

    public void onDisplayConfigurationChanged(int i, Configuration configuration) {
        if (ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP, 2057038970, 1, (String) null, Long.valueOf((long) i));
        }
        PerDisplay perDisplay = this.mDisplayDropTargets.get(i);
        if (perDisplay != null) {
            perDisplay.rootView.requestApplyInsets();
        }
    }

    public void onDisplayRemoved(int i) {
        if (ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP, -1382704050, 1, (String) null, Long.valueOf((long) i));
        }
        PerDisplay perDisplay = this.mDisplayDropTargets.get(i);
        if (perDisplay != null) {
            perDisplay.f183wm.removeViewImmediate(perDisplay.rootView);
            this.mDisplayDropTargets.remove(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x00cf A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00d0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onDrag(android.view.View r17, android.view.DragEvent r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            boolean r2 = com.android.p011wm.shell.protolog.ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled
            r4 = 2
            r6 = 1
            r7 = 0
            if (r2 == 0) goto L_0x0055
            int r2 = r18.getAction()
            java.lang.String r2 = android.view.DragEvent.actionToString(r2)
            java.lang.String r2 = java.lang.String.valueOf(r2)
            float r8 = r18.getX()
            double r8 = (double) r8
            float r10 = r18.getY()
            double r10 = (double) r10
            float r12 = r18.getOffsetX()
            double r12 = (double) r12
            float r14 = r18.getOffsetY()
            double r14 = (double) r14
            com.android.wm.shell.protolog.ShellProtoLogGroup r5 = com.android.p011wm.shell.protolog.ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP
            r3 = 5
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r2
            java.lang.Double r2 = java.lang.Double.valueOf(r8)
            r3[r6] = r2
            java.lang.Double r2 = java.lang.Double.valueOf(r10)
            r3[r4] = r2
            java.lang.Double r2 = java.lang.Double.valueOf(r12)
            r8 = 3
            r3[r8] = r2
            r2 = 4
            java.lang.Double r8 = java.lang.Double.valueOf(r14)
            r3[r2] = r8
            r2 = 1862198614(0x6efee556, float:3.9443221E28)
            r8 = 680(0x2a8, float:9.53E-43)
            r9 = 0
            com.android.p011wm.shell.protolog.ShellProtoLogImpl.m93v(r5, r2, r8, r9, r3)
        L_0x0055:
            android.view.Display r2 = r17.getDisplay()
            int r2 = r2.getDisplayId()
            android.util.SparseArray<com.android.wm.shell.draganddrop.DragAndDropController$PerDisplay> r3 = r0.mDisplayDropTargets
            java.lang.Object r3 = r3.get(r2)
            com.android.wm.shell.draganddrop.DragAndDropController$PerDisplay r3 = (com.android.p011wm.shell.draganddrop.DragAndDropController.PerDisplay) r3
            android.content.ClipDescription r5 = r18.getClipDescription()
            if (r3 != 0) goto L_0x006c
            return r7
        L_0x006c:
            int r8 = r18.getAction()
            if (r8 != r6) goto L_0x00ca
            android.content.ClipData r8 = r18.getClipData()
            int r8 = r8.getItemCount()
            if (r8 <= 0) goto L_0x0096
            java.lang.String r8 = "application/vnd.android.activity"
            boolean r8 = r5.hasMimeType(r8)
            if (r8 != 0) goto L_0x0094
            java.lang.String r8 = "application/vnd.android.shortcut"
            boolean r8 = r5.hasMimeType(r8)
            if (r8 != 0) goto L_0x0094
            java.lang.String r8 = "application/vnd.android.task"
            boolean r8 = r5.hasMimeType(r8)
            if (r8 == 0) goto L_0x0096
        L_0x0094:
            r8 = r6
            goto L_0x0097
        L_0x0096:
            r8 = r7
        L_0x0097:
            r3.isHandlingDrag = r8
            boolean r9 = com.android.p011wm.shell.protolog.ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled
            if (r9 == 0) goto L_0x00ca
            android.content.ClipData r9 = r18.getClipData()
            int r9 = r9.getItemCount()
            long r9 = (long) r9
            java.lang.String r5 = r0.getMimeTypes(r5)
            java.lang.String r5 = java.lang.String.valueOf(r5)
            com.android.wm.shell.protolog.ShellProtoLogGroup r11 = com.android.p011wm.shell.protolog.ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP
            r12 = 375908576(0x1667e8e0, float:1.8733514E-25)
            r13 = 7
            r14 = 3
            java.lang.Object[] r14 = new java.lang.Object[r14]
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r8)
            r14[r7] = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r9)
            r14[r6] = r8
            r14[r4] = r5
            r4 = 0
            com.android.p011wm.shell.protolog.ShellProtoLogImpl.m93v(r11, r12, r13, r4, r14)
            goto L_0x00cb
        L_0x00ca:
            r4 = 0
        L_0x00cb:
            boolean r5 = r3.isHandlingDrag
            if (r5 != 0) goto L_0x00d0
            return r7
        L_0x00d0:
            int r5 = r18.getAction()
            switch(r5) {
                case 1: goto L_0x0107;
                case 2: goto L_0x0101;
                case 3: goto L_0x00fc;
                case 4: goto L_0x00e4;
                case 5: goto L_0x00de;
                case 6: goto L_0x00d8;
                default: goto L_0x00d7;
            }
        L_0x00d7:
            goto L_0x0128
        L_0x00d8:
            com.android.wm.shell.draganddrop.DragLayout r0 = r3.dragLayout
            r0.hide(r1, r4)
            goto L_0x0128
        L_0x00de:
            com.android.wm.shell.draganddrop.DragLayout r0 = r3.dragLayout
            r0.show()
            goto L_0x0128
        L_0x00e4:
            com.android.wm.shell.draganddrop.DragLayout r2 = r3.dragLayout
            boolean r2 = r2.hasDropped()
            if (r2 != 0) goto L_0x0128
            int r2 = r3.activeDragCount
            int r2 = r2 - r6
            r3.activeDragCount = r2
            com.android.wm.shell.draganddrop.DragLayout r2 = r3.dragLayout
            com.android.wm.shell.draganddrop.DragAndDropController$$ExternalSyntheticLambda0 r4 = new com.android.wm.shell.draganddrop.DragAndDropController$$ExternalSyntheticLambda0
            r4.<init>(r0, r3)
            r2.hide(r1, r4)
            goto L_0x0128
        L_0x00fc:
            boolean r0 = r0.handleDrop(r1, r3)
            return r0
        L_0x0101:
            com.android.wm.shell.draganddrop.DragLayout r0 = r3.dragLayout
            r0.update(r1)
            goto L_0x0128
        L_0x0107:
            int r4 = r3.activeDragCount
            if (r4 == 0) goto L_0x0113
            java.lang.String r0 = TAG
            java.lang.String r1 = "Unexpected drag start during an active drag"
            android.util.Slog.w(r0, r1)
            return r7
        L_0x0113:
            int r4 = r4 + r6
            r3.activeDragCount = r4
            com.android.wm.shell.draganddrop.DragLayout r4 = r3.dragLayout
            com.android.wm.shell.common.DisplayController r5 = r0.mDisplayController
            com.android.wm.shell.common.DisplayLayout r2 = r5.getDisplayLayout(r2)
            android.content.ClipData r1 = r18.getClipData()
            r4.prepare(r2, r1)
            r0.setDropTargetWindowVisibility(r3, r7)
        L_0x0128:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.draganddrop.DragAndDropController.onDrag(android.view.View, android.view.DragEvent):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDrag$0(PerDisplay perDisplay) {
        if (perDisplay.activeDragCount == 0) {
            setDropTargetWindowVisibility(perDisplay, 4);
        }
    }

    private boolean handleDrop(DragEvent dragEvent, PerDisplay perDisplay) {
        SurfaceControl dragSurface = dragEvent.getDragSurface();
        perDisplay.activeDragCount--;
        return perDisplay.dragLayout.drop(dragEvent, dragSurface, new DragAndDropController$$ExternalSyntheticLambda1(this, perDisplay, dragSurface));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$handleDrop$1(PerDisplay perDisplay, SurfaceControl surfaceControl) {
        if (perDisplay.activeDragCount == 0) {
            setDropTargetWindowVisibility(perDisplay, 4);
        }
        this.mTransaction.reparent(surfaceControl, (SurfaceControl) null);
        this.mTransaction.apply();
    }

    private void setDropTargetWindowVisibility(PerDisplay perDisplay, int i) {
        if (ShellProtoLogCache.WM_SHELL_DRAG_AND_DROP_enabled) {
            long j = (long) perDisplay.displayId;
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_DRAG_AND_DROP, 1184615936, 5, (String) null, Long.valueOf(j), Long.valueOf((long) i));
        }
        perDisplay.rootView.setVisibility(i);
        if (i == 0) {
            perDisplay.rootView.requestApplyInsets();
        }
    }

    private String getMimeTypes(ClipDescription clipDescription) {
        String str = "";
        for (int i = 0; i < clipDescription.getMimeTypeCount(); i++) {
            if (i > 0) {
                str = str + ", ";
            }
            str = str + clipDescription.getMimeType(i);
        }
        return str;
    }

    /* renamed from: com.android.wm.shell.draganddrop.DragAndDropController$PerDisplay */
    private static class PerDisplay {
        int activeDragCount;
        final Context context;
        final int displayId;
        final DragLayout dragLayout;
        boolean isHandlingDrag;
        final FrameLayout rootView;

        /* renamed from: wm */
        final WindowManager f183wm;

        PerDisplay(int i, Context context2, WindowManager windowManager, FrameLayout frameLayout, DragLayout dragLayout2) {
            this.displayId = i;
            this.context = context2;
            this.f183wm = windowManager;
            this.rootView = frameLayout;
            this.dragLayout = dragLayout2;
        }
    }
}
