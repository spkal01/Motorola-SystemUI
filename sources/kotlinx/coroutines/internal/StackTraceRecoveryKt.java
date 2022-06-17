package kotlinx.coroutines.internal;

import java.util.ArrayDeque;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
import org.jetbrains.annotations.NotNull;

/* compiled from: StackTraceRecovery.kt */
public final class StackTraceRecoveryKt {
    private static final String baseContinuationImplClassName;
    private static final String stackTraceRecoveryClassName;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            java.lang.String r0 = "kotlin.coroutines.jvm.internal.BaseContinuationImpl"
            kotlin.Result$Companion r1 = kotlin.Result.Companion     // Catch:{ all -> 0x0016 }
            java.lang.Class r1 = java.lang.Class.forName(r0)     // Catch:{ all -> 0x0016 }
            java.lang.String r2 = "Class.forName(baseContinuationImplClass)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r2)     // Catch:{ all -> 0x0016 }
            java.lang.String r1 = r1.getCanonicalName()     // Catch:{ all -> 0x0016 }
            java.lang.Object r1 = kotlin.Result.m849constructorimpl(r1)     // Catch:{ all -> 0x0016 }
            goto L_0x0021
        L_0x0016:
            r1 = move-exception
            kotlin.Result$Companion r2 = kotlin.Result.Companion
            java.lang.Object r1 = kotlin.ResultKt.createFailure(r1)
            java.lang.Object r1 = kotlin.Result.m849constructorimpl(r1)
        L_0x0021:
            java.lang.Throwable r2 = kotlin.Result.m851exceptionOrNullimpl(r1)
            if (r2 != 0) goto L_0x0028
            r0 = r1
        L_0x0028:
            java.lang.String r0 = (java.lang.String) r0
            baseContinuationImplClassName = r0
            kotlin.Result$Companion r0 = kotlin.Result.Companion     // Catch:{ all -> 0x003e }
            java.lang.Class<kotlinx.coroutines.internal.StackTraceRecoveryKt> r0 = kotlinx.coroutines.internal.StackTraceRecoveryKt.class
            java.lang.String r1 = "Class.forName(stackTraceRecoveryClass)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r1)     // Catch:{ all -> 0x003e }
            java.lang.String r0 = r0.getCanonicalName()     // Catch:{ all -> 0x003e }
            java.lang.Object r0 = kotlin.Result.m849constructorimpl(r0)     // Catch:{ all -> 0x003e }
            goto L_0x0049
        L_0x003e:
            r0 = move-exception
            kotlin.Result$Companion r1 = kotlin.Result.Companion
            java.lang.Object r0 = kotlin.ResultKt.createFailure(r0)
            java.lang.Object r0 = kotlin.Result.m849constructorimpl(r0)
        L_0x0049:
            java.lang.Throwable r1 = kotlin.Result.m851exceptionOrNullimpl(r0)
            if (r1 != 0) goto L_0x0050
            goto L_0x0052
        L_0x0050:
            java.lang.String r0 = "kotlinx.coroutines.internal.StackTraceRecoveryKt"
        L_0x0052:
            java.lang.String r0 = (java.lang.String) r0
            stackTraceRecoveryClassName = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.StackTraceRecoveryKt.<clinit>():void");
    }

    @NotNull
    public static final <E extends Throwable> E recoverStackTrace(@NotNull E e, @NotNull Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(e, "exception");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        return (!DebugKt.getRECOVER_STACK_TRACES() || !(continuation instanceof CoroutineStackFrame)) ? e : recoverFromStackFrame(e, (CoroutineStackFrame) continuation);
    }

    private static final <E extends Throwable> E recoverFromStackFrame(E e, CoroutineStackFrame coroutineStackFrame) {
        Pair causeAndStacktrace = causeAndStacktrace(e);
        E e2 = (Throwable) causeAndStacktrace.component1();
        StackTraceElement[] stackTraceElementArr = (StackTraceElement[]) causeAndStacktrace.component2();
        Throwable tryCopyException = ExceptionsConstuctorKt.tryCopyException(e2);
        if (tryCopyException == null) {
            return e;
        }
        ArrayDeque createStackTrace = createStackTrace(coroutineStackFrame);
        if (createStackTrace.isEmpty()) {
            return e;
        }
        if (e2 != e) {
            mergeRecoveredTraces(stackTraceElementArr, createStackTrace);
        }
        return createFinalException(e2, tryCopyException, createStackTrace);
    }

    private static final <E extends Throwable> E createFinalException(E e, E e2, ArrayDeque<StackTraceElement> arrayDeque) {
        arrayDeque.addFirst(artificialFrame("Coroutine boundary"));
        StackTraceElement[] stackTrace = e.getStackTrace();
        Intrinsics.checkExpressionValueIsNotNull(stackTrace, "causeTrace");
        String str = baseContinuationImplClassName;
        Intrinsics.checkExpressionValueIsNotNull(str, "baseContinuationImplClassName");
        int frameIndex = frameIndex(stackTrace, str);
        int i = 0;
        if (frameIndex == -1) {
            Object[] array = arrayDeque.toArray(new StackTraceElement[0]);
            if (array != null) {
                e2.setStackTrace((StackTraceElement[]) array);
                return e2;
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
        }
        StackTraceElement[] stackTraceElementArr = new StackTraceElement[(arrayDeque.size() + frameIndex)];
        for (int i2 = 0; i2 < frameIndex; i2++) {
            stackTraceElementArr[i2] = stackTrace[i2];
        }
        for (StackTraceElement stackTraceElement : arrayDeque) {
            stackTraceElementArr[frameIndex + i] = stackTraceElement;
            i++;
        }
        e2.setStackTrace(stackTraceElementArr);
        return e2;
    }

    private static final <E extends Throwable> Pair<E, StackTraceElement[]> causeAndStacktrace(@NotNull E e) {
        boolean z;
        Throwable cause = e.getCause();
        if (cause == null || !Intrinsics.areEqual((Object) cause.getClass(), (Object) e.getClass())) {
            return TuplesKt.m104to(e, new StackTraceElement[0]);
        }
        StackTraceElement[] stackTrace = e.getStackTrace();
        Intrinsics.checkExpressionValueIsNotNull(stackTrace, "currentTrace");
        int length = stackTrace.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            StackTraceElement stackTraceElement = stackTrace[i];
            Intrinsics.checkExpressionValueIsNotNull(stackTraceElement, "it");
            if (isArtificial(stackTraceElement)) {
                z = true;
                break;
            }
            i++;
        }
        if (z) {
            return TuplesKt.m104to(cause, stackTrace);
        }
        return TuplesKt.m104to(e, new StackTraceElement[0]);
    }

    @NotNull
    public static final <E extends Throwable> E unwrap(@NotNull E e) {
        E cause;
        Intrinsics.checkParameterIsNotNull(e, "exception");
        if (DebugKt.getRECOVER_STACK_TRACES() && (cause = e.getCause()) != null) {
            boolean z = true;
            if (!(!Intrinsics.areEqual((Object) cause.getClass(), (Object) e.getClass()))) {
                StackTraceElement[] stackTrace = e.getStackTrace();
                Intrinsics.checkExpressionValueIsNotNull(stackTrace, "exception.stackTrace");
                int length = stackTrace.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        z = false;
                        break;
                    }
                    StackTraceElement stackTraceElement = stackTrace[i];
                    Intrinsics.checkExpressionValueIsNotNull(stackTraceElement, "it");
                    if (isArtificial(stackTraceElement)) {
                        break;
                    }
                    i++;
                }
                if (z) {
                    return cause;
                }
            }
        }
        return e;
    }

    private static final ArrayDeque<StackTraceElement> createStackTrace(CoroutineStackFrame coroutineStackFrame) {
        ArrayDeque<StackTraceElement> arrayDeque = new ArrayDeque<>();
        StackTraceElement stackTraceElement = coroutineStackFrame.getStackTraceElement();
        if (stackTraceElement != null) {
            arrayDeque.add(stackTraceElement);
        }
        while (true) {
            coroutineStackFrame = coroutineStackFrame.getCallerFrame();
            if (coroutineStackFrame == null) {
                return arrayDeque;
            }
            StackTraceElement stackTraceElement2 = coroutineStackFrame.getStackTraceElement();
            if (stackTraceElement2 != null) {
                arrayDeque.add(stackTraceElement2);
            }
        }
    }

    @NotNull
    public static final StackTraceElement artificialFrame(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "message");
        return new StackTraceElement("\b\b\b(" + str, "\b", "\b", -1);
    }

    public static final boolean isArtificial(@NotNull StackTraceElement stackTraceElement) {
        Intrinsics.checkParameterIsNotNull(stackTraceElement, "$this$isArtificial");
        String className = stackTraceElement.getClassName();
        Intrinsics.checkExpressionValueIsNotNull(className, "className");
        return StringsKt__StringsJVMKt.startsWith$default(className, "\b\b\b", false, 2, (Object) null);
    }

    private static final boolean elementWiseEquals(@NotNull StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
        return stackTraceElement.getLineNumber() == stackTraceElement2.getLineNumber() && Intrinsics.areEqual((Object) stackTraceElement.getMethodName(), (Object) stackTraceElement2.getMethodName()) && Intrinsics.areEqual((Object) stackTraceElement.getFileName(), (Object) stackTraceElement2.getFileName()) && Intrinsics.areEqual((Object) stackTraceElement.getClassName(), (Object) stackTraceElement2.getClassName());
    }

    private static final int frameIndex(@NotNull StackTraceElement[] stackTraceElementArr, String str) {
        int length = stackTraceElementArr.length;
        for (int i = 0; i < length; i++) {
            if (Intrinsics.areEqual((Object) str, (Object) stackTraceElementArr[i].getClassName())) {
                return i;
            }
        }
        return -1;
    }

    private static final void mergeRecoveredTraces(StackTraceElement[] stackTraceElementArr, ArrayDeque<StackTraceElement> arrayDeque) {
        int length = stackTraceElementArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                i = -1;
                break;
            } else if (isArtificial(stackTraceElementArr[i])) {
                break;
            } else {
                i++;
            }
        }
        int i2 = i + 1;
        int length2 = stackTraceElementArr.length - 1;
        if (length2 >= i2) {
            while (true) {
                StackTraceElement stackTraceElement = stackTraceElementArr[length2];
                StackTraceElement last = arrayDeque.getLast();
                Intrinsics.checkExpressionValueIsNotNull(last, "result.last");
                if (elementWiseEquals(stackTraceElement, last)) {
                    arrayDeque.removeLast();
                }
                arrayDeque.addFirst(stackTraceElementArr[length2]);
                if (length2 != i2) {
                    length2--;
                } else {
                    return;
                }
            }
        }
    }
}
