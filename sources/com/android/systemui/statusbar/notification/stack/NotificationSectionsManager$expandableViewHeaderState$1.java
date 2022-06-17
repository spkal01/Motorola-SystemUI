package com.android.systemui.statusbar.notification.stack;

import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotificationSectionsManager.kt */
public final class NotificationSectionsManager$expandableViewHeaderState$1 implements NotificationSectionsManager.SectionUpdateState<T> {
    final /* synthetic */ T $header;
    @Nullable
    private Integer currentPosition;
    @NotNull
    private final T header;
    @Nullable
    private Integer targetPosition;
    final /* synthetic */ NotificationSectionsManager this$0;

    NotificationSectionsManager$expandableViewHeaderState$1(T t, NotificationSectionsManager notificationSectionsManager) {
        this.$header = t;
        this.this$0 = notificationSectionsManager;
        this.header = t;
    }

    @Nullable
    public Integer getCurrentPosition() {
        return this.currentPosition;
    }

    public void setCurrentPosition(@Nullable Integer num) {
        this.currentPosition = num;
    }

    @Nullable
    public Integer getTargetPosition() {
        return this.targetPosition;
    }

    public void setTargetPosition(@Nullable Integer num) {
        this.targetPosition = num;
    }

    public void adjustViewPosition() {
        Integer targetPosition2 = getTargetPosition();
        Integer currentPosition2 = getCurrentPosition();
        if (targetPosition2 == null) {
            if (currentPosition2 != null) {
                ViewGroup access$getParent$p = this.this$0.parent;
                if (access$getParent$p != null) {
                    access$getParent$p.removeView(this.$header);
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("parent");
                    throw null;
                }
            }
        } else if (currentPosition2 == null) {
            ViewGroup transientContainer = this.$header.getTransientContainer();
            if (transientContainer != null) {
                transientContainer.removeTransientView(this.$header);
            }
            this.$header.setTransientContainer((ViewGroup) null);
            ViewGroup access$getParent$p2 = this.this$0.parent;
            if (access$getParent$p2 != null) {
                access$getParent$p2.addView(this.$header, targetPosition2.intValue());
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("parent");
                throw null;
            }
        } else {
            ViewGroup access$getParent$p3 = this.this$0.parent;
            if (access$getParent$p3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("parent");
                throw null;
            } else if (access$getParent$p3 instanceof NotificationStackScrollLayout) {
                ((NotificationStackScrollLayout) access$getParent$p3).changeViewPosition(this.$header, targetPosition2.intValue());
            } else if (access$getParent$p3 instanceof DesktopNotificationStackScrollLayout) {
                ((DesktopNotificationStackScrollLayout) access$getParent$p3).changeViewPosition(this.$header, targetPosition2.intValue());
            }
        }
    }
}
