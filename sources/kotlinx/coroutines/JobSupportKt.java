package kotlinx.coroutines;

import kotlinx.coroutines.internal.Symbol;
import org.jetbrains.annotations.Nullable;

/* compiled from: JobSupport.kt */
public final class JobSupportKt {
    /* access modifiers changed from: private */
    public static final Empty EMPTY_ACTIVE = new Empty(true);
    /* access modifiers changed from: private */
    public static final Empty EMPTY_NEW = new Empty(false);
    /* access modifiers changed from: private */
    public static final Symbol SEALED = new Symbol("SEALED");

    @Nullable
    public static final Object boxIncomplete(@Nullable Object obj) {
        return obj instanceof Incomplete ? new IncompleteStateBox((Incomplete) obj) : obj;
    }
}
