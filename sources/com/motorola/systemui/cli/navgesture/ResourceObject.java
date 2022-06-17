package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;

public interface ResourceObject {

    public static class Overrides {
        public static <T extends ResourceObject> T getObject(Context context, int i) {
            String string = context.getString(i);
            Log.d("ResourceObject", "ResourceObject className = " + string);
            try {
                Class<?> cls = Class.forName(string);
                try {
                    return (ResourceObject) cls.getDeclaredConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    return (ResourceObject) cls.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e2) {
                e.addSuppressed(e2);
                throw new RuntimeException("Bad resource object class", e);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException("Cannot find the resource object class", e3);
            }
        }
    }
}
