package kotlin.coroutines.jvm.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* compiled from: DebugMetadata.kt */
public @interface DebugMetadata {
    /* renamed from: c */
    String mo31517c() default "";

    /* renamed from: f */
    String mo31518f() default "";

    /* renamed from: l */
    int[] mo31519l() default {};

    /* renamed from: m */
    String mo31520m() default "";

    /* renamed from: v */
    int mo31521v() default 1;
}
