package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.os.Looper;
import com.motorola.systemui.cli.navgesture.executors.AppExecutors;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class MainThreadInitializedObject<T> {
    private final Function<Context, T> mProvider;
    private T mValue;

    public MainThreadInitializedObject(Function<Context, T> function) {
        this.mProvider = function;
    }

    /* renamed from: get */
    public T lambda$get$0(Context context) {
        if (this.mValue == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.mValue = this.mProvider.apply(context.getApplicationContext());
            } else {
                try {
                    return AppExecutors.m97ui().submit(new MainThreadInitializedObject$$ExternalSyntheticLambda0(this, context)).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this.mValue;
    }

    public static <T extends ResourceObject> MainThreadInitializedObject<T> fromResourceObject(int i) {
        return new MainThreadInitializedObject<>(new MainThreadInitializedObject$$ExternalSyntheticLambda1(i));
    }
}
