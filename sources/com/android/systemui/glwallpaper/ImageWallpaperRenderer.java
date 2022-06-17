package com.android.systemui.glwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.opengl.GLES20;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;
import com.android.systemui.R$raw;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ImageWallpaperRenderer {
    /* access modifiers changed from: private */
    public static final String TAG = "ImageWallpaperRenderer";
    /* access modifiers changed from: private */
    public static int mScreenHeight;
    /* access modifiers changed from: private */
    public static int mScreenWidth;
    private DisplayManager mDisplayManager;
    private Consumer<Bitmap> mOnBitmapUpdated;
    private final ImageGLProgram mProgram;
    private final Rect mSurfaceSize = new Rect();
    private final WallpaperTexture mTexture;
    private final ImageGLWallpaper mWallpaper;
    private int mWhich;

    public void finish() {
    }

    private boolean isCliDisplay(int i, Context context) {
        return MotoFeature.getInstance(context).isSupportCli() && i == 1;
    }

    public ImageWallpaperRenderer(Context context) {
        WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        if (wallpaperManager == null) {
            Log.w(TAG, "WallpaperManager not available");
        }
        this.mWhich = 1;
        int displayId = context.getDisplayId();
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mDisplayManager = displayManager;
        Display display = displayManager.getDisplay(displayId);
        if (DesktopFeature.isDesktopMode(display)) {
            String str = TAG;
            Log.i(str, "ImageWallpaperRenderer displayId = " + displayId + "; display = " + display);
            this.mWhich = 8;
        } else if (isCliDisplay(displayId, context)) {
            this.mWhich = 4;
        }
        this.mTexture = new WallpaperTexture(wallpaperManager, this.mWhich);
        ImageGLProgram imageGLProgram = new ImageGLProgram(context);
        this.mProgram = imageGLProgram;
        this.mWallpaper = new ImageGLWallpaper(imageGLProgram);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
    }

    public void setOnBitmapChanged(Consumer<Bitmap> consumer) {
        this.mOnBitmapUpdated = consumer;
    }

    public boolean isWcgContent() {
        return this.mTexture.isWcgContent();
    }

    public void onSurfaceCreated() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.mProgram.useGLProgram(R$raw.image_wallpaper_vertex_shader, R$raw.image_wallpaper_fragment_shader);
        this.mTexture.use(new ImageWallpaperRenderer$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSurfaceCreated$0(Bitmap bitmap) {
        if (bitmap == null) {
            Log.w(TAG, "reload texture failed!");
        } else {
            Consumer<Bitmap> consumer = this.mOnBitmapUpdated;
            if (consumer != null) {
                consumer.accept(bitmap);
            }
        }
        this.mWallpaper.setup(bitmap);
    }

    public void onSurfaceChanged(int i, int i2) {
        GLES20.glViewport(0, 0, i, i2);
    }

    public void onDrawFrame() {
        GLES20.glClear(16384);
        GLES20.glViewport(0, 0, this.mSurfaceSize.width(), this.mSurfaceSize.height());
        this.mWallpaper.useTexture();
        this.mWallpaper.draw();
    }

    public Size reportSurfaceSize() {
        this.mTexture.use((Consumer<Bitmap>) null);
        this.mSurfaceSize.set(this.mTexture.getTextureDimensions());
        return new Size(this.mSurfaceSize.width(), this.mSurfaceSize.height());
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mSurfaceSize=");
        printWriter.print(this.mSurfaceSize);
        printWriter.print(str);
        printWriter.print("mWcgContent=");
        printWriter.print(isWcgContent());
        this.mWallpaper.dump(str, fileDescriptor, printWriter, strArr);
    }

    static class WallpaperTexture {
        private Bitmap mBitmap;
        private final Rect mDimensions;
        private final AtomicInteger mRefCount;
        private final WallpaperManager mWallpaperManager;
        private boolean mWcgContent;
        private int mWhich;

        private WallpaperTexture(WallpaperManager wallpaperManager, int i) {
            this.mWhich = 1;
            this.mWallpaperManager = wallpaperManager;
            this.mRefCount = new AtomicInteger();
            this.mDimensions = new Rect();
            this.mWhich = i;
        }

        public void use(Consumer<Bitmap> consumer) {
            Bitmap bitmap;
            this.mRefCount.incrementAndGet();
            synchronized (this.mRefCount) {
                if (this.mBitmap == null) {
                    this.mBitmap = this.mWallpaperManager.getBitmapForRender(this.mWhich, false);
                    this.mWcgContent = this.mWallpaperManager.wallpaperSupportsWcg(1);
                    this.mWallpaperManager.forgetLoadedWallpaper();
                    Bitmap bitmap2 = this.mBitmap;
                    if (bitmap2 != null) {
                        this.mDimensions.set(0, 0, bitmap2.getWidth(), this.mBitmap.getHeight());
                        if (SystemProperties.get("ro.product.name", "unknown").contains("factory")) {
                            this.mDimensions.set(0, 0, ImageWallpaperRenderer.mScreenWidth, ImageWallpaperRenderer.mScreenHeight);
                        }
                    } else {
                        Log.w(ImageWallpaperRenderer.TAG, "Can't get bitmap");
                    }
                }
            }
            if (consumer != null) {
                consumer.accept(this.mBitmap);
            }
            synchronized (this.mRefCount) {
                if (this.mRefCount.decrementAndGet() == 0 && (bitmap = this.mBitmap) != null) {
                    bitmap.recycle();
                    this.mBitmap = null;
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean isWcgContent() {
            return this.mWcgContent;
        }

        private String getHash() {
            Bitmap bitmap = this.mBitmap;
            return bitmap != null ? Integer.toHexString(bitmap.hashCode()) : "null";
        }

        /* access modifiers changed from: private */
        public Rect getTextureDimensions() {
            return this.mDimensions;
        }

        public String toString() {
            return "{" + getHash() + ", " + this.mRefCount.get() + "}";
        }
    }
}
