package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.collection.listbuilder.NotifSection;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SuppressedAttachState.kt */
public final class SuppressedAttachState {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @Nullable
    private GroupEntry parent;
    @Nullable
    private NotifSection section;
    private boolean wasPruneSuppressed;

    public /* synthetic */ SuppressedAttachState(NotifSection notifSection, GroupEntry groupEntry, boolean z, DefaultConstructorMarker defaultConstructorMarker) {
        this(notifSection, groupEntry, z);
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SuppressedAttachState)) {
            return false;
        }
        SuppressedAttachState suppressedAttachState = (SuppressedAttachState) obj;
        return Intrinsics.areEqual((Object) this.section, (Object) suppressedAttachState.section) && Intrinsics.areEqual((Object) this.parent, (Object) suppressedAttachState.parent) && this.wasPruneSuppressed == suppressedAttachState.wasPruneSuppressed;
    }

    public int hashCode() {
        NotifSection notifSection = this.section;
        int i = 0;
        int hashCode = (notifSection == null ? 0 : notifSection.hashCode()) * 31;
        GroupEntry groupEntry = this.parent;
        if (groupEntry != null) {
            i = groupEntry.hashCode();
        }
        int i2 = (hashCode + i) * 31;
        boolean z = this.wasPruneSuppressed;
        if (z) {
            z = true;
        }
        return i2 + (z ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "SuppressedAttachState(section=" + this.section + ", parent=" + this.parent + ", wasPruneSuppressed=" + this.wasPruneSuppressed + ')';
    }

    private SuppressedAttachState(NotifSection notifSection, GroupEntry groupEntry, boolean z) {
        this.section = notifSection;
        this.parent = groupEntry;
        this.wasPruneSuppressed = z;
    }

    @Nullable
    public final NotifSection getSection() {
        return this.section;
    }

    public final void setSection(@Nullable NotifSection notifSection) {
        this.section = notifSection;
    }

    @Nullable
    public final GroupEntry getParent() {
        return this.parent;
    }

    public final void setParent(@Nullable GroupEntry groupEntry) {
        this.parent = groupEntry;
    }

    public final boolean getWasPruneSuppressed() {
        return this.wasPruneSuppressed;
    }

    public final void setWasPruneSuppressed(boolean z) {
        this.wasPruneSuppressed = z;
    }

    public final void clone(@NotNull SuppressedAttachState suppressedAttachState) {
        Intrinsics.checkNotNullParameter(suppressedAttachState, "other");
        this.parent = suppressedAttachState.parent;
        this.section = suppressedAttachState.section;
        this.wasPruneSuppressed = suppressedAttachState.wasPruneSuppressed;
    }

    public final void reset() {
        this.parent = null;
        this.section = null;
        this.wasPruneSuppressed = false;
    }

    /* compiled from: SuppressedAttachState.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final SuppressedAttachState create() {
            return new SuppressedAttachState((NotifSection) null, (GroupEntry) null, false, (DefaultConstructorMarker) null);
        }
    }
}
