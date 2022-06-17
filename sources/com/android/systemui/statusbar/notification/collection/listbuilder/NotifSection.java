package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSectioner;
import com.android.systemui.statusbar.notification.collection.render.NodeController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotifSection.kt */
public final class NotifSection {
    private final int index;
    @NotNull
    private final NotifSectioner sectioner;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NotifSection)) {
            return false;
        }
        NotifSection notifSection = (NotifSection) obj;
        return Intrinsics.areEqual((Object) this.sectioner, (Object) notifSection.sectioner) && this.index == notifSection.index;
    }

    public int hashCode() {
        return (this.sectioner.hashCode() * 31) + Integer.hashCode(this.index);
    }

    @NotNull
    public String toString() {
        return "NotifSection(sectioner=" + this.sectioner + ", index=" + this.index + ')';
    }

    public NotifSection(@NotNull NotifSectioner notifSectioner, int i) {
        Intrinsics.checkNotNullParameter(notifSectioner, "sectioner");
        this.sectioner = notifSectioner;
        this.index = i;
    }

    @NotNull
    public final NotifSectioner getSectioner() {
        return this.sectioner;
    }

    public final int getIndex() {
        return this.index;
    }

    @NotNull
    public final String getLabel() {
        return "Section(" + this.index + ", \"" + this.sectioner.getName() + "\")";
    }

    @Nullable
    public final NodeController getHeaderController() {
        return this.sectioner.getHeaderNodeController();
    }
}
