package com.android.systemui.dump;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: DumpManager.kt */
final class RegisteredDumpable<T> {
    private final T dumpable;
    @NotNull
    private final String name;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RegisteredDumpable)) {
            return false;
        }
        RegisteredDumpable registeredDumpable = (RegisteredDumpable) obj;
        return Intrinsics.areEqual((Object) this.name, (Object) registeredDumpable.name) && Intrinsics.areEqual((Object) this.dumpable, (Object) registeredDumpable.dumpable);
    }

    public int hashCode() {
        int hashCode = this.name.hashCode() * 31;
        T t = this.dumpable;
        return hashCode + (t == null ? 0 : t.hashCode());
    }

    @NotNull
    public String toString() {
        return "RegisteredDumpable(name=" + this.name + ", dumpable=" + this.dumpable + ')';
    }

    public RegisteredDumpable(@NotNull String str, T t) {
        Intrinsics.checkNotNullParameter(str, "name");
        this.name = str;
        this.dumpable = t;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    public final T getDumpable() {
        return this.dumpable;
    }
}
