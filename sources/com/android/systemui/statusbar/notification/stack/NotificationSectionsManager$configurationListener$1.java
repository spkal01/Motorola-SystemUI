package com.android.systemui.statusbar.notification.stack;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.systemui.statusbar.policy.ConfigurationController;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationSectionsManager.kt */
public final class NotificationSectionsManager$configurationListener$1 implements ConfigurationController.ConfigurationListener {
    final /* synthetic */ NotificationSectionsManager this$0;

    NotificationSectionsManager$configurationListener$1(NotificationSectionsManager notificationSectionsManager) {
        this.this$0 = notificationSectionsManager;
    }

    public void onLocaleListChanged() {
        NotificationSectionsManager notificationSectionsManager = this.this$0;
        ViewGroup access$getParent$p = notificationSectionsManager.parent;
        if (access$getParent$p != null) {
            LayoutInflater from = LayoutInflater.from(access$getParent$p.getContext());
            Intrinsics.checkNotNullExpressionValue(from, "from(parent.context)");
            notificationSectionsManager.reinflateViews(from);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("parent");
        throw null;
    }
}
