package com.android.launcher3.util;

import java.lang.reflect.Field;

public class ReflectUtils {
    public static Object getFieldValue(Object obj, String str) {
        return getFieldValue(obj.getClass(), obj, str);
    }

    public static Object getFieldValue(Class<?> cls, Object obj, String str) {
        try {
            return getFieldValueOrThrow(obj, cls.getDeclaredField(str));
        } catch (Exception unused) {
            return null;
        }
    }

    public static Object getFieldValueOrThrow(Object obj, Field field) {
        if (field == null) {
            return null;
        }
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Exception on get field value", e);
        }
    }
}
