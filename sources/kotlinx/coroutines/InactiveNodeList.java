package kotlinx.coroutines;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: JobSupport.kt */
public final class InactiveNodeList implements Incomplete {
    @NotNull
    private final NodeList list;

    public boolean isActive() {
        return false;
    }

    public InactiveNodeList(@NotNull NodeList nodeList) {
        Intrinsics.checkParameterIsNotNull(nodeList, "list");
        this.list = nodeList;
    }

    @NotNull
    public NodeList getList() {
        return this.list;
    }

    @NotNull
    public String toString() {
        return DebugKt.getDEBUG() ? getList().getString("New") : super.toString();
    }
}
