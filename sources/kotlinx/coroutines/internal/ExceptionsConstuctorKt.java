package kotlinx.coroutines.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ExceptionsConstuctor.kt */
public final class ExceptionsConstuctorKt {
    private static final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private static final WeakHashMap<Class<? extends Throwable>, Function1<Throwable, Throwable>> exceptionCtors = new WeakHashMap<>();
    private static final int throwableFields = fieldsCountOrDefault(Throwable.class, -1);

    /*  JADX ERROR: StackOverflow in pass: MarkFinallyVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    @org.jetbrains.annotations.Nullable
    public static final <E extends java.lang.Throwable> E tryCopyException(@org.jetbrains.annotations.NotNull E r9) {
        /*
            java.lang.String r0 = "exception"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0)
            boolean r0 = r9 instanceof kotlinx.coroutines.CopyableThrowable
            r1 = 0
            if (r0 == 0) goto L_0x002d
            kotlin.Result$Companion r0 = kotlin.Result.Companion     // Catch:{ all -> 0x0017 }
            kotlinx.coroutines.CopyableThrowable r9 = (kotlinx.coroutines.CopyableThrowable) r9     // Catch:{ all -> 0x0017 }
            java.lang.Throwable r9 = r9.createCopy()     // Catch:{ all -> 0x0017 }
            java.lang.Object r9 = kotlin.Result.m849constructorimpl(r9)     // Catch:{ all -> 0x0017 }
            goto L_0x0022
        L_0x0017:
            r9 = move-exception
            kotlin.Result$Companion r0 = kotlin.Result.Companion
            java.lang.Object r9 = kotlin.ResultKt.createFailure(r9)
            java.lang.Object r9 = kotlin.Result.m849constructorimpl(r9)
        L_0x0022:
            boolean r0 = kotlin.Result.m853isFailureimpl(r9)
            if (r0 == 0) goto L_0x0029
            goto L_0x002a
        L_0x0029:
            r1 = r9
        L_0x002a:
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            return r1
        L_0x002d:
            java.util.concurrent.locks.ReentrantReadWriteLock r0 = cacheLock
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r0.readLock()
            r2.lock()
            java.util.WeakHashMap<java.lang.Class<? extends java.lang.Throwable>, kotlin.jvm.functions.Function1<java.lang.Throwable, java.lang.Throwable>> r3 = exceptionCtors     // Catch:{ all -> 0x0128 }
            java.lang.Class r4 = r9.getClass()     // Catch:{ all -> 0x0128 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0128 }
            kotlin.jvm.functions.Function1 r3 = (kotlin.jvm.functions.Function1) r3     // Catch:{ all -> 0x0128 }
            r2.unlock()
            if (r3 == 0) goto L_0x004e
            java.lang.Object r9 = r3.invoke(r9)
            java.lang.Throwable r9 = (java.lang.Throwable) r9
            return r9
        L_0x004e:
            int r2 = throwableFields
            java.lang.Class r3 = r9.getClass()
            r4 = 0
            int r3 = fieldsCountOrDefault(r3, r4)
            if (r2 == r3) goto L_0x00a1
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r2 = r0.readLock()
            int r3 = r0.getWriteHoldCount()
            if (r3 != 0) goto L_0x006a
            int r3 = r0.getReadHoldCount()
            goto L_0x006b
        L_0x006a:
            r3 = r4
        L_0x006b:
            r5 = r4
        L_0x006c:
            if (r5 >= r3) goto L_0x0074
            r2.unlock()
            int r5 = r5 + 1
            goto L_0x006c
        L_0x0074:
            java.util.concurrent.locks.ReentrantReadWriteLock$WriteLock r0 = r0.writeLock()
            r0.lock()
            java.util.WeakHashMap<java.lang.Class<? extends java.lang.Throwable>, kotlin.jvm.functions.Function1<java.lang.Throwable, java.lang.Throwable>> r5 = exceptionCtors     // Catch:{ all -> 0x0094 }
            java.lang.Class r9 = r9.getClass()     // Catch:{ all -> 0x0094 }
            kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$4$1 r6 = kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$4$1.INSTANCE     // Catch:{ all -> 0x0094 }
            r5.put(r9, r6)     // Catch:{ all -> 0x0094 }
            kotlin.Unit r9 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0094 }
        L_0x0088:
            if (r4 >= r3) goto L_0x0090
            r2.lock()
            int r4 = r4 + 1
            goto L_0x0088
        L_0x0090:
            r0.unlock()
            return r1
        L_0x0094:
            r9 = move-exception
        L_0x0095:
            if (r4 >= r3) goto L_0x009d
            r2.lock()
            int r4 = r4 + 1
            goto L_0x0095
        L_0x009d:
            r0.unlock()
            throw r9
        L_0x00a1:
            java.lang.Class r0 = r9.getClass()
            java.lang.reflect.Constructor[] r0 = r0.getConstructors()
            java.lang.String r2 = "exception.javaClass.constructors"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r2)
            kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$$inlined$sortedByDescending$1 r2 = new kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$$inlined$sortedByDescending$1
            r2.<init>()
            java.util.List r0 = kotlin.collections.ArraysKt___ArraysKt.sortedWith(r0, r2)
            java.util.Iterator r0 = r0.iterator()
            r2 = r1
        L_0x00bc:
            boolean r3 = r0.hasNext()
            if (r3 == 0) goto L_0x00d3
            java.lang.Object r2 = r0.next()
            java.lang.reflect.Constructor r2 = (java.lang.reflect.Constructor) r2
            java.lang.String r3 = "constructor"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r3)
            kotlin.jvm.functions.Function1 r2 = createConstructor(r2)
            if (r2 == 0) goto L_0x00bc
        L_0x00d3:
            java.util.concurrent.locks.ReentrantReadWriteLock r0 = cacheLock
            java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock r3 = r0.readLock()
            int r5 = r0.getWriteHoldCount()
            if (r5 != 0) goto L_0x00e4
            int r5 = r0.getReadHoldCount()
            goto L_0x00e5
        L_0x00e4:
            r5 = r4
        L_0x00e5:
            r6 = r4
        L_0x00e6:
            if (r6 >= r5) goto L_0x00ee
            r3.unlock()
            int r6 = r6 + 1
            goto L_0x00e6
        L_0x00ee:
            java.util.concurrent.locks.ReentrantReadWriteLock$WriteLock r0 = r0.writeLock()
            r0.lock()
            java.util.WeakHashMap<java.lang.Class<? extends java.lang.Throwable>, kotlin.jvm.functions.Function1<java.lang.Throwable, java.lang.Throwable>> r6 = exceptionCtors     // Catch:{ all -> 0x011b }
            java.lang.Class r7 = r9.getClass()     // Catch:{ all -> 0x011b }
            if (r2 == 0) goto L_0x00ff
            r8 = r2
            goto L_0x0101
        L_0x00ff:
            kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$5$1 r8 = kotlinx.coroutines.internal.ExceptionsConstuctorKt$tryCopyException$5$1.INSTANCE     // Catch:{ all -> 0x011b }
        L_0x0101:
            r6.put(r7, r8)     // Catch:{ all -> 0x011b }
            kotlin.Unit r6 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x011b }
        L_0x0106:
            if (r4 >= r5) goto L_0x010e
            r3.lock()
            int r4 = r4 + 1
            goto L_0x0106
        L_0x010e:
            r0.unlock()
            if (r2 == 0) goto L_0x011a
            java.lang.Object r9 = r2.invoke(r9)
            r1 = r9
            java.lang.Throwable r1 = (java.lang.Throwable) r1
        L_0x011a:
            return r1
        L_0x011b:
            r9 = move-exception
        L_0x011c:
            if (r4 >= r5) goto L_0x0124
            r3.lock()
            int r4 = r4 + 1
            goto L_0x011c
        L_0x0124:
            r0.unlock()
            throw r9
        L_0x0128:
            r9 = move-exception
            r2.unlock()
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.ExceptionsConstuctorKt.tryCopyException(java.lang.Throwable):java.lang.Throwable");
    }

    private static final Function1<Throwable, Throwable> createConstructor(Constructor<?> constructor) {
        Class<String> cls = String.class;
        Class[] parameterTypes = constructor.getParameterTypes();
        int length = parameterTypes.length;
        if (length == 0) {
            return new ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$4(constructor);
        }
        if (length == 1) {
            Class cls2 = parameterTypes[0];
            if (Intrinsics.areEqual((Object) cls2, (Object) Throwable.class)) {
                return new ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$2(constructor);
            }
            if (Intrinsics.areEqual((Object) cls2, (Object) cls)) {
                return new ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$3(constructor);
            }
            return null;
        } else if (length == 2 && Intrinsics.areEqual((Object) parameterTypes[0], (Object) cls) && Intrinsics.areEqual((Object) parameterTypes[1], (Object) Throwable.class)) {
            return new ExceptionsConstuctorKt$createConstructor$$inlined$safeCtor$1(constructor);
        } else {
            return null;
        }
    }

    private static final int fieldsCountOrDefault(@NotNull Class<?> cls, int i) {
        Integer num;
        JvmClassMappingKt.getKotlinClass(cls);
        try {
            Result.Companion companion = Result.Companion;
            num = Result.m849constructorimpl(Integer.valueOf(fieldsCount$default(cls, 0, 1, (Object) null)));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.Companion;
            num = Result.m849constructorimpl(ResultKt.createFailure(th));
        }
        Integer valueOf = Integer.valueOf(i);
        if (Result.m853isFailureimpl(num)) {
            num = valueOf;
        }
        return ((Number) num).intValue();
    }

    static /* synthetic */ int fieldsCount$default(Class cls, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 0;
        }
        return fieldsCount(cls, i);
    }

    private static final int fieldsCount(@NotNull Class<?> cls, int i) {
        Class<? super Object> superclass;
        do {
            Field[] declaredFields = r6.getDeclaredFields();
            Intrinsics.checkExpressionValueIsNotNull(declaredFields, "declaredFields");
            int i2 = 0;
            Class<? super Object> cls2 = cls;
            for (Field field : declaredFields) {
                Intrinsics.checkExpressionValueIsNotNull(field, "it");
                if (!Modifier.isStatic(field.getModifiers())) {
                    i2++;
                }
            }
            i += i2;
            superclass = cls2.getSuperclass();
            cls2 = superclass;
        } while (superclass != null);
        return i;
    }
}
