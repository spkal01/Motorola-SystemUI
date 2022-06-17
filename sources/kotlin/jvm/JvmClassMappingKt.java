package kotlin.jvm;

import java.util.Objects;
import kotlin.jvm.internal.ClassBasedDeclarationContainer;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;

/* compiled from: JvmClassMapping.kt */
public final class JvmClassMappingKt {
    @NotNull
    public static final <T> Class<T> getJavaClass(@NotNull KClass<T> kClass) {
        Intrinsics.checkNotNullParameter(kClass, "$this$java");
        Class<?> jClass = ((ClassBasedDeclarationContainer) kClass).getJClass();
        Objects.requireNonNull(jClass, "null cannot be cast to non-null type java.lang.Class<T>");
        return jClass;
    }

    @NotNull
    public static final <T> Class<T> getJavaObjectType(@NotNull KClass<T> kClass) {
        Intrinsics.checkNotNullParameter(kClass, "$this$javaObjectType");
        Class<?> jClass = ((ClassBasedDeclarationContainer) kClass).getJClass();
        if (!jClass.isPrimitive()) {
            return jClass;
        }
        String name = jClass.getName();
        switch (name.hashCode()) {
            case -1325958191:
                return name.equals("double") ? Double.class : jClass;
            case 104431:
                if (name.equals("int")) {
                    return Integer.class;
                }
                return jClass;
            case 3039496:
                if (name.equals("byte")) {
                    return Byte.class;
                }
                return jClass;
            case 3052374:
                if (name.equals("char")) {
                    return Character.class;
                }
                return jClass;
            case 3327612:
                if (name.equals("long")) {
                    return Long.class;
                }
                return jClass;
            case 3625364:
                if (name.equals("void")) {
                    return Void.class;
                }
                return jClass;
            case 64711720:
                if (name.equals("boolean")) {
                    return Boolean.class;
                }
                return jClass;
            case 97526364:
                if (name.equals("float")) {
                    return Float.class;
                }
                return jClass;
            case 109413500:
                if (name.equals("short")) {
                    return Short.class;
                }
                return jClass;
            default:
                return jClass;
        }
    }

    @NotNull
    public static final <T> KClass<T> getKotlinClass(@NotNull Class<T> cls) {
        Intrinsics.checkNotNullParameter(cls, "$this$kotlin");
        return Reflection.getOrCreateKotlinClass(cls);
    }
}
