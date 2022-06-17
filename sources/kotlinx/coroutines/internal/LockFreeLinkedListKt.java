package kotlinx.coroutines.internal;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: LockFreeLinkedList.kt */
public final class LockFreeLinkedListKt {
    @NotNull
    private static final Object ALREADY_REMOVED = new Symbol("ALREADY_REMOVED");
    @NotNull
    private static final Object CONDITION_FALSE = new Symbol("CONDITION_FALSE");
    @NotNull
    private static final Object LIST_EMPTY = new Symbol("LIST_EMPTY");
    private static final Object REMOVE_PREPARED = new Symbol("REMOVE_PREPARED");

    @NotNull
    public static final Object getCONDITION_FALSE() {
        return CONDITION_FALSE;
    }

    @NotNull
    public static final LockFreeLinkedListNode unwrap(@NotNull Object obj) {
        LockFreeLinkedListNode lockFreeLinkedListNode;
        Intrinsics.checkParameterIsNotNull(obj, "$this$unwrap");
        Removed removed = (Removed) (!(obj instanceof Removed) ? null : obj);
        return (removed == null || (lockFreeLinkedListNode = removed.ref) == null) ? (LockFreeLinkedListNode) obj : lockFreeLinkedListNode;
    }
}
