package kotlin.jvm.internal;

public class MutablePropertyReference0Impl extends MutablePropertyReference0 {
    public MutablePropertyReference0Impl(Object obj, Class cls, String str, String str2, int i) {
        super(obj, cls, str, str2, i);
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [kotlin.reflect.KProperty0$Getter, kotlin.reflect.KCallable] */
    public Object get() {
        return getGetter().call(new Object[0]);
    }

    /* JADX WARNING: type inference failed for: r2v1, types: [kotlin.reflect.KCallable, kotlin.reflect.KMutableProperty0$Setter] */
    public void set(Object obj) {
        getSetter().call(obj);
    }
}
