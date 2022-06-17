package kotlinx.coroutines;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import kotlinx.coroutines.android.AndroidExceptionPreHandler;

public final /* synthetic */ class CoroutineExceptionHandlerImplKt$$ExternalSyntheticServiceLoad0 {
    /* renamed from: m */
    public static /* synthetic */ Iterator m110m() {
        try {
            return Arrays.asList(new CoroutineExceptionHandler[]{new AndroidExceptionPreHandler()}).iterator();
        } catch (Throwable th) {
            throw new ServiceConfigurationError(th.getMessage(), th);
        }
    }
}
