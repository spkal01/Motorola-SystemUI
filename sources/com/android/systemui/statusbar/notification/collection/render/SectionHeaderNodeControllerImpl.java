package com.android.systemui.statusbar.notification.collection.render;

import android.view.LayoutInflater;
import android.view.View;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import com.android.systemui.statusbar.notification.stack.SectionHeaderView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SectionHeaderController.kt */
public final class SectionHeaderNodeControllerImpl implements NodeController, SectionHeaderController {
    @Nullable
    private SectionHeaderView _view;
    /* access modifiers changed from: private */
    @NotNull
    public final ActivityStarter activityStarter;
    @Nullable
    private View.OnClickListener clearAllClickListener;
    /* access modifiers changed from: private */
    @NotNull
    public final String clickIntentAction;
    private final int headerTextResId;
    @NotNull
    private final LayoutInflater layoutInflater;
    @NotNull
    private final String nodeLabel;
    @NotNull
    private final View.OnClickListener onHeaderClickListener = new SectionHeaderNodeControllerImpl$onHeaderClickListener$1(this);

    public SectionHeaderNodeControllerImpl(@NotNull String str, @NotNull LayoutInflater layoutInflater2, int i, @NotNull ActivityStarter activityStarter2, @NotNull String str2) {
        Intrinsics.checkNotNullParameter(str, "nodeLabel");
        Intrinsics.checkNotNullParameter(layoutInflater2, "layoutInflater");
        Intrinsics.checkNotNullParameter(activityStarter2, "activityStarter");
        Intrinsics.checkNotNullParameter(str2, "clickIntentAction");
        this.nodeLabel = str;
        this.layoutInflater = layoutInflater2;
        this.headerTextResId = i;
        this.activityStarter = activityStarter2;
        this.clickIntentAction = str2;
    }

    public void addChildAt(@NotNull NodeController nodeController, int i) {
        NodeController.DefaultImpls.addChildAt(this, nodeController, i);
    }

    @Nullable
    public View getChildAt(int i) {
        return NodeController.DefaultImpls.getChildAt(this, i);
    }

    public int getChildCount() {
        return NodeController.DefaultImpls.getChildCount(this);
    }

    public void moveChildTo(@NotNull NodeController nodeController, int i) {
        NodeController.DefaultImpls.moveChildTo(this, nodeController, i);
    }

    public void removeChild(@NotNull NodeController nodeController, boolean z) {
        NodeController.DefaultImpls.removeChild(this, nodeController, z);
    }

    @NotNull
    public String getNodeLabel() {
        return this.nodeLabel;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0047  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reinflateView(@org.jetbrains.annotations.NotNull android.view.ViewGroup r6) {
        /*
            r5 = this;
            java.lang.String r0 = "parent"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r6, r0)
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = r5._view
            r1 = -1
            if (r0 != 0) goto L_0x000c
        L_0x000a:
            r2 = r1
            goto L_0x0023
        L_0x000c:
            android.view.ViewGroup r2 = r0.getTransientContainer()
            if (r2 != 0) goto L_0x0013
            goto L_0x0016
        L_0x0013:
            r2.removeView(r0)
        L_0x0016:
            android.view.ViewParent r2 = r0.getParent()
            if (r2 != r6) goto L_0x000a
            int r2 = r6.indexOfChild(r0)
            r6.removeView(r0)
        L_0x0023:
            android.view.LayoutInflater r0 = r5.layoutInflater
            int r3 = com.android.systemui.R$layout.status_bar_notification_section_header
            r4 = 0
            android.view.View r0 = r0.inflate(r3, r6, r4)
            java.lang.String r3 = "null cannot be cast to non-null type com.android.systemui.statusbar.notification.stack.SectionHeaderView"
            java.util.Objects.requireNonNull(r0, r3)
            com.android.systemui.statusbar.notification.stack.SectionHeaderView r0 = (com.android.systemui.statusbar.notification.stack.SectionHeaderView) r0
            int r3 = r5.headerTextResId
            r0.setHeaderText(r3)
            android.view.View$OnClickListener r3 = r5.onHeaderClickListener
            r0.setOnHeaderClickListener(r3)
            android.view.View$OnClickListener r3 = r5.clearAllClickListener
            if (r3 != 0) goto L_0x0042
            goto L_0x0045
        L_0x0042:
            r0.setOnClearAllClickListener(r3)
        L_0x0045:
            if (r2 == r1) goto L_0x004a
            r6.addView(r0, r2)
        L_0x004a:
            r5._view = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.render.SectionHeaderNodeControllerImpl.reinflateView(android.view.ViewGroup):void");
    }

    @Nullable
    public SectionHeaderView getHeaderView() {
        return this._view;
    }

    public void setOnClearAllClickListener(@NotNull View.OnClickListener onClickListener) {
        Intrinsics.checkNotNullParameter(onClickListener, "listener");
        this.clearAllClickListener = onClickListener;
        SectionHeaderView sectionHeaderView = this._view;
        if (sectionHeaderView != null) {
            sectionHeaderView.setOnClearAllClickListener(onClickListener);
        }
    }

    @NotNull
    public View getView() {
        SectionHeaderView sectionHeaderView = this._view;
        Intrinsics.checkNotNull(sectionHeaderView);
        return sectionHeaderView;
    }
}
