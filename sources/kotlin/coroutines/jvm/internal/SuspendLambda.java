package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.FunctionBase;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ContinuationImpl.kt */
public abstract class SuspendLambda extends ContinuationImpl implements FunctionBase<Object> {
    private final int arity;

    public int getArity() {
        return this.arity;
    }

    public SuspendLambda(int i, @Nullable Continuation<Object> continuation) {
        super(continuation);
        this.arity = i;
    }

    @NotNull
    public String toString() {
        if (getCompletion() != null) {
            return super.toString();
        }
        String renderLambdaToString = Reflection.renderLambdaToString((FunctionBase) this);
        Intrinsics.checkNotNullExpressionValue(renderLambdaToString, "Reflection.renderLambdaToString(this)");
        return renderLambdaToString;
    }
}
