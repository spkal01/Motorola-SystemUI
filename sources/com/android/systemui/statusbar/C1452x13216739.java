package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

/* renamed from: com.android.systemui.statusbar.LockscreenShadeTransitionController$goToLockedShadeInternal$cancelHandler$1 */
/* compiled from: LockscreenShadeTransitionController.kt */
final class C1452x13216739 implements Runnable {
    final /* synthetic */ Runnable $cancelAction;
    final /* synthetic */ LockscreenShadeTransitionController this$0;

    C1452x13216739(LockscreenShadeTransitionController lockscreenShadeTransitionController, Runnable runnable) {
        this.this$0 = lockscreenShadeTransitionController;
        this.$cancelAction = runnable;
    }

    public final void run() {
        NotificationEntry access$getDraggedDownEntry$p = this.this$0.draggedDownEntry;
        if (access$getDraggedDownEntry$p != null) {
            LockscreenShadeTransitionController lockscreenShadeTransitionController = this.this$0;
            access$getDraggedDownEntry$p.setUserLocked(false);
            access$getDraggedDownEntry$p.notifyHeightChanged(false);
            lockscreenShadeTransitionController.draggedDownEntry = null;
        }
        Runnable runnable = this.$cancelAction;
        if (runnable != null) {
            runnable.run();
        }
    }
}
