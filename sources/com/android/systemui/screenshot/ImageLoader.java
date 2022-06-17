package com.android.systemui.screenshot;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageLoader {
    private final ContentResolver mResolver;

    static class Result {
        Bitmap bitmap;
        File fileName;
        Uri uri;

        Result() {
        }

        public String toString() {
            return "Result{" + "uri=" + this.uri + ", fileName=" + this.fileName + ", bitmap=" + this.bitmap + '}';
        }
    }

    ImageLoader(ContentResolver contentResolver) {
        this.mResolver = contentResolver;
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Result> load(File file) {
        return CallbackToFutureAdapter.getFuture(new ImageLoader$$ExternalSyntheticLambda0(file));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ Object lambda$load$1(File file, CallbackToFutureAdapter.Completer completer) throws Exception {
        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            Result result = new Result();
            result.fileName = file;
            result.bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            completer.set(result);
            bufferedInputStream.close();
            return "BitmapFactory#decodeStream";
        } catch (IOException e) {
            completer.setException(e);
            return "BitmapFactory#decodeStream";
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }
}
