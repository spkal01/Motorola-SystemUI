package kotlin.internal;

import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PlatformImplementations.kt */
public class PlatformImplementations {

    /* compiled from: PlatformImplementations.kt */
    private static final class ReflectThrowable {
        @NotNull
        public static final ReflectThrowable INSTANCE = new ReflectThrowable();
        @Nullable
        public static final Method addSuppressed;
        @Nullable
        public static final Method getSuppressed;

        /* JADX WARNING: Removed duplicated region for block: B:10:0x0046 A[LOOP:0: B:1:0x0015->B:10:0x0046, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x004a A[EDGE_INSN: B:20:0x004a->B:12:0x004a ?: BREAK  , SYNTHETIC] */
        static {
            /*
                kotlin.internal.PlatformImplementations$ReflectThrowable r0 = new kotlin.internal.PlatformImplementations$ReflectThrowable
                r0.<init>()
                INSTANCE = r0
                java.lang.Class<java.lang.Throwable> r0 = java.lang.Throwable.class
                java.lang.reflect.Method[] r1 = r0.getMethods()
                java.lang.String r2 = "throwableMethods"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r1, r2)
                int r2 = r1.length
                r3 = 0
                r4 = r3
            L_0x0015:
                java.lang.String r5 = "it"
                r6 = 0
                if (r4 >= r2) goto L_0x0049
                r7 = r1[r4]
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r7, r5)
                java.lang.String r8 = r7.getName()
                java.lang.String r9 = "addSuppressed"
                boolean r8 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r8, (java.lang.Object) r9)
                if (r8 == 0) goto L_0x0042
                java.lang.Class[] r8 = r7.getParameterTypes()
                java.lang.String r9 = "it.parameterTypes"
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r8, r9)
                java.lang.Object r8 = kotlin.collections.ArraysKt___ArraysKt.singleOrNull(r8)
                java.lang.Class r8 = (java.lang.Class) r8
                boolean r8 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r8, (java.lang.Object) r0)
                if (r8 == 0) goto L_0x0042
                r8 = 1
                goto L_0x0043
            L_0x0042:
                r8 = r3
            L_0x0043:
                if (r8 == 0) goto L_0x0046
                goto L_0x004a
            L_0x0046:
                int r4 = r4 + 1
                goto L_0x0015
            L_0x0049:
                r7 = r6
            L_0x004a:
                addSuppressed = r7
                int r0 = r1.length
            L_0x004d:
                if (r3 >= r0) goto L_0x0065
                r2 = r1[r3]
                kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r2, r5)
                java.lang.String r4 = r2.getName()
                java.lang.String r7 = "getSuppressed"
                boolean r4 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r4, (java.lang.Object) r7)
                if (r4 == 0) goto L_0x0062
                r6 = r2
                goto L_0x0065
            L_0x0062:
                int r3 = r3 + 1
                goto L_0x004d
            L_0x0065:
                getSuppressed = r6
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlin.internal.PlatformImplementations.ReflectThrowable.<clinit>():void");
        }

        private ReflectThrowable() {
        }
    }

    public void addSuppressed(@NotNull Throwable th, @NotNull Throwable th2) {
        Intrinsics.checkNotNullParameter(th, "cause");
        Intrinsics.checkNotNullParameter(th2, "exception");
        Method method = ReflectThrowable.addSuppressed;
        if (method != null) {
            method.invoke(th, new Object[]{th2});
        }
    }
}
