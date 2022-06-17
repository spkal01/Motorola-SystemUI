package com.android.systemui;

import android.app.WallpaperColors;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.service.wallpaper.WallpaperService;
import android.util.ArraySet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Size;
import android.view.DisplayInfo;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.glwallpaper.EglHelper;
import com.android.systemui.glwallpaper.ImageWallpaperRenderer;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ImageWallpaper extends WallpaperService {
    /* access modifiers changed from: private */
    public static boolean DEBUG_COLOR = (!Build.IS_USER);
    /* access modifiers changed from: private */
    public static final RectF LOCAL_COLOR_BOUNDS = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    /* access modifiers changed from: private */
    public static final String TAG = ImageWallpaper.class.getSimpleName();
    /* access modifiers changed from: private */
    public final ArraySet<RectF> mColorAreas = new ArraySet<>();
    /* access modifiers changed from: private */
    public final ArrayList<RectF> mLocalColorsToAdd = new ArrayList<>();
    /* access modifiers changed from: private */
    public Bitmap mMiniBitmap;
    /* access modifiers changed from: private */
    public volatile int mPages = 1;
    /* access modifiers changed from: private */
    public HandlerThread mWorker;

    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mWorker = handlerThread;
        handlerThread.start();
    }

    public WallpaperService.Engine onCreateEngine() {
        return new GLEngine();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mWorker.quitSafely();
        this.mWorker = null;
        this.mMiniBitmap = null;
    }

    class GLEngine extends WallpaperService.Engine {
        @VisibleForTesting
        static final int MIN_SURFACE_HEIGHT = 128;
        @VisibleForTesting
        static final int MIN_SURFACE_WIDTH = 128;
        private EglHelper mEglHelper;
        private final Runnable mFinishRenderingTask = new ImageWallpaper$GLEngine$$ExternalSyntheticLambda0(this);
        private int mHeight = 1;
        private int mImgHeight = 1;
        private int mImgWidth = 1;
        private float mPageOffset = 1.0f;
        private float mPageWidth = 1.0f;
        private ImageWallpaperRenderer mRenderer;
        private int mWidth = 1;

        public boolean shouldZoomOutWallpaper() {
            return true;
        }

        public boolean supportsLocalColorExtraction() {
            return true;
        }

        GLEngine() {
            super(ImageWallpaper.this);
        }

        @VisibleForTesting
        GLEngine(Handler handler) {
            super(ImageWallpaper.this, ImageWallpaper$GLEngine$$ExternalSyntheticLambda9.INSTANCE, handler);
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            this.mEglHelper = getEglHelperInstance();
            this.mRenderer = getRendererInstance();
            setFixedSizeAllowed(true);
            updateSurfaceSize();
            Rect bounds = ((WindowManager) getDisplayContext().getSystemService(WindowManager.class)).getCurrentWindowMetrics().getBounds();
            this.mHeight = bounds.height();
            this.mWidth = bounds.width();
            this.mRenderer.setOnBitmapChanged(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda8(this));
        }

        /* access modifiers changed from: package-private */
        public EglHelper getEglHelperInstance() {
            return new EglHelper();
        }

        /* access modifiers changed from: package-private */
        public ImageWallpaperRenderer getRendererInstance() {
            return new ImageWallpaperRenderer(getDisplayContext());
        }

        public void onOffsetsChanged(float f, float f2, float f3, float f4, int i, int i2) {
            int i3 = 1;
            if (f3 > 0.0f && f3 <= 1.0f) {
                i3 = 1 + Math.round(1.0f / f3);
            }
            if (i3 != ImageWallpaper.this.mPages) {
                int unused = ImageWallpaper.this.mPages = i3;
                if (ImageWallpaper.this.mMiniBitmap != null && !ImageWallpaper.this.mMiniBitmap.isRecycled()) {
                    updateShift();
                    ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda1(this));
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onOffsetsChanged$0() {
            if (ImageWallpaper.this.mMiniBitmap != null && !ImageWallpaper.this.mMiniBitmap.isRecycled()) {
                computeAndNotifyLocalColors(new ArrayList(ImageWallpaper.this.mColorAreas), ImageWallpaper.this.mMiniBitmap);
            }
        }

        private void updateShift() {
            if (this.mImgHeight == 0 || ImageWallpaper.this.mPages == 1) {
                this.mPageOffset = 0.0f;
                this.mPageWidth = 1.0f;
            } else if (getDisplayContext() == null) {
                Log.e(ImageWallpaper.TAG, "Engine is destroyed. getDisplayContext() is null.");
            } else {
                DisplayInfo displayInfo = new DisplayInfo();
                getDisplayContext().getDisplay().getDisplayInfo(displayInfo);
                int naturalWidth = displayInfo.getNaturalWidth();
                int i = this.mImgWidth;
                float min = Math.min(i > 0 ? ((float) naturalWidth) / ((float) i) : 1.0f, 1.0f);
                this.mPageWidth = min;
                this.mPageOffset = (1.0f - min) / ((float) (ImageWallpaper.this.mPages - 1));
            }
        }

        /* access modifiers changed from: private */
        public void updateMiniBitmap(Bitmap bitmap) {
            if (bitmap != null) {
                int min = Math.min(bitmap.getWidth(), bitmap.getHeight());
                float f = min > 128 ? 128.0f / ((float) min) : 1.0f;
                this.mImgHeight = bitmap.getHeight();
                this.mImgWidth = bitmap.getWidth();
                Bitmap unused = ImageWallpaper.this.mMiniBitmap = Bitmap.createScaledBitmap(bitmap, (int) Math.max(((float) bitmap.getWidth()) * f, 1.0f), (int) Math.max(f * ((float) bitmap.getHeight()), 1.0f), false);
                computeAndNotifyLocalColors(ImageWallpaper.this.mLocalColorsToAdd, ImageWallpaper.this.mMiniBitmap);
                ImageWallpaper.this.mLocalColorsToAdd.clear();
            }
        }

        private void updateSurfaceSize() {
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Size reportSurfaceSize = this.mRenderer.reportSurfaceSize();
            surfaceHolder.setFixedSize(Math.max(128, reportSurfaceSize.getWidth()), Math.max(128, reportSurfaceSize.getHeight()));
        }

        public void onDestroy() {
            Bitmap unused = ImageWallpaper.this.mMiniBitmap = null;
            ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda2(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onDestroy$1() {
            this.mRenderer.finish();
            this.mRenderer = null;
            this.mEglHelper.finish();
            this.mEglHelper = null;
        }

        public void addLocalColorsAreas(List<RectF> list) {
            ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda6(this, list));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addLocalColorsAreas$2(List list) {
            if (ImageWallpaper.this.mColorAreas.size() + ImageWallpaper.this.mLocalColorsToAdd.size() == 0) {
                setOffsetNotificationsEnabled(true);
            }
            Bitmap access$100 = ImageWallpaper.this.mMiniBitmap;
            if (access$100 == null) {
                ImageWallpaper.this.mLocalColorsToAdd.addAll(list);
            } else {
                computeAndNotifyLocalColors(list, access$100);
            }
        }

        private void computeAndNotifyLocalColors(List<RectF> list, Bitmap bitmap) {
            List<WallpaperColors> localWallpaperColors = getLocalWallpaperColors(list, bitmap);
            ImageWallpaper.this.mColorAreas.addAll(list);
            try {
                notifyLocalColorsChanged(list, localWallpaperColors);
            } catch (RuntimeException e) {
                Log.e(ImageWallpaper.TAG, e.getMessage(), e);
            }
        }

        public void removeLocalColorsAreas(List<RectF> list) {
            ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda7(this, list));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$removeLocalColorsAreas$3(List list) {
            ImageWallpaper.this.mColorAreas.removeAll(list);
            ImageWallpaper.this.mLocalColorsToAdd.removeAll(list);
            if (ImageWallpaper.this.mColorAreas.size() + ImageWallpaper.this.mLocalColorsToAdd.size() == 0) {
                setOffsetNotificationsEnabled(false);
            }
        }

        private RectF pageToImgRect(RectF rectF) {
            float access$000 = 1.0f / ((float) ImageWallpaper.this.mPages);
            float f = (rectF.left % access$000) / access$000;
            float f2 = (rectF.right % access$000) / access$000;
            int floor = (int) Math.floor((double) (rectF.centerX() / access$000));
            RectF rectF2 = new RectF();
            rectF2.bottom = rectF.bottom;
            rectF2.top = rectF.top;
            float f3 = (float) floor;
            rectF2.left = MathUtils.constrain((f * this.mPageWidth) + (this.mPageOffset * f3), 0.0f, 1.0f);
            rectF2.right = MathUtils.constrain((f2 * this.mPageWidth) + (f3 * this.mPageOffset), 0.0f, 1.0f);
            if (ImageWallpaper.DEBUG_COLOR) {
                String access$300 = ImageWallpaper.TAG;
                Log.i(access$300, "pageToImgRect area = " + rectF.toString() + " imgArea = " + rectF2.toString() + " mPageOffset = " + this.mPageOffset + " mPageWidth = " + this.mPageWidth + " mPages = " + ImageWallpaper.this.mPages);
            }
            if (rectF2.left >= rectF2.right) {
                rectF2.left = 0.0f;
                rectF2.right = 1.0f;
            }
            return rectF2;
        }

        private List<WallpaperColors> getLocalWallpaperColors(List<RectF> list, Bitmap bitmap) {
            ArrayList arrayList = new ArrayList(list.size());
            updateShift();
            for (int i = 0; i < list.size(); i++) {
                RectF pageToImgRect = pageToImgRect(list.get(i));
                if (pageToImgRect == null || !ImageWallpaper.LOCAL_COLOR_BOUNDS.contains(pageToImgRect)) {
                    arrayList.add((Object) null);
                } else {
                    Rect rect = new Rect((int) Math.floor((double) (pageToImgRect.left * ((float) bitmap.getWidth()))), (int) Math.floor((double) (pageToImgRect.top * ((float) bitmap.getHeight()))), (int) Math.ceil((double) (pageToImgRect.right * ((float) bitmap.getWidth()))), (int) Math.ceil((double) (pageToImgRect.bottom * ((float) bitmap.getHeight()))));
                    if (rect.isEmpty()) {
                        arrayList.add((Object) null);
                    } else {
                        arrayList.add(WallpaperColors.fromBitmap(Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())));
                    }
                }
            }
            return arrayList;
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            if (ImageWallpaper.this.mWorker != null) {
                ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda5(this, surfaceHolder));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfaceCreated$4(SurfaceHolder surfaceHolder) {
            this.mEglHelper.init(surfaceHolder, needSupportWideColorGamut());
            this.mRenderer.onSurfaceCreated();
        }

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            if (ImageWallpaper.this.mWorker != null) {
                ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda4(this, i2, i3));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfaceChanged$5(int i, int i2) {
            this.mRenderer.onSurfaceChanged(i, i2);
        }

        public void onSurfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
            if (ImageWallpaper.this.mWorker == null) {
                Log.d(ImageWallpaper.TAG, "onSurfaceRedrawNeeded return.");
            } else {
                ImageWallpaper.this.mWorker.getThreadHandler().post(new ImageWallpaper$GLEngine$$ExternalSyntheticLambda3(this));
            }
        }

        /* access modifiers changed from: private */
        public void drawFrame() {
            if (this.mEglHelper == null) {
                Log.d(ImageWallpaper.TAG, "drawFrame return, mEglHelper is NULL.");
                return;
            }
            preRender();
            requestRender();
            postRender();
        }

        public void preRender() {
            Trace.beginSection("ImageWallpaper#preRender");
            preRenderInternal();
            Trace.endSection();
        }

        private void preRenderInternal() {
            boolean z;
            Rect surfaceFrame = getSurfaceHolder().getSurfaceFrame();
            cancelFinishRenderingTask();
            if (!this.mEglHelper.hasEglContext()) {
                this.mEglHelper.destroyEglSurface();
                if (!this.mEglHelper.createEglContext()) {
                    Log.w(ImageWallpaper.TAG, "recreate egl context failed!");
                } else {
                    z = true;
                    if (this.mEglHelper.hasEglContext() && !this.mEglHelper.hasEglSurface() && !this.mEglHelper.createEglSurface(getSurfaceHolder(), needSupportWideColorGamut())) {
                        Log.w(ImageWallpaper.TAG, "recreate egl surface failed!");
                    }
                    if (!this.mEglHelper.hasEglContext() && !this.mEglHelper.hasEglSurface() && z) {
                        this.mRenderer.onSurfaceCreated();
                        this.mRenderer.onSurfaceChanged(surfaceFrame.width(), surfaceFrame.height());
                        return;
                    }
                }
            }
            z = false;
            Log.w(ImageWallpaper.TAG, "recreate egl surface failed!");
            if (!this.mEglHelper.hasEglContext() && !this.mEglHelper.hasEglSurface()) {
            }
        }

        public void requestRender() {
            Trace.beginSection("ImageWallpaper#requestRender");
            requestRenderInternal();
            Trace.endSection();
        }

        private void requestRenderInternal() {
            Rect surfaceFrame = getSurfaceHolder().getSurfaceFrame();
            if (this.mEglHelper.hasEglContext() && this.mEglHelper.hasEglSurface() && surfaceFrame.width() > 0 && surfaceFrame.height() > 0) {
                this.mRenderer.onDrawFrame();
                if (!this.mEglHelper.swapBuffer()) {
                    Log.e(ImageWallpaper.TAG, "drawFrame failed!");
                    return;
                }
                return;
            }
            String access$300 = ImageWallpaper.TAG;
            Log.e(access$300, "requestRender: not ready, has context=" + this.mEglHelper.hasEglContext() + ", has surface=" + this.mEglHelper.hasEglSurface() + ", frame=" + surfaceFrame);
        }

        public void postRender() {
            Trace.beginSection("ImageWallpaper#postRender");
            scheduleFinishRendering();
            Trace.endSection();
        }

        private void cancelFinishRenderingTask() {
            if (ImageWallpaper.this.mWorker != null) {
                ImageWallpaper.this.mWorker.getThreadHandler().removeCallbacks(this.mFinishRenderingTask);
            }
        }

        private void scheduleFinishRendering() {
            if (ImageWallpaper.this.mWorker != null) {
                cancelFinishRenderingTask();
                ImageWallpaper.this.mWorker.getThreadHandler().postDelayed(this.mFinishRenderingTask, 1000);
            }
        }

        /* access modifiers changed from: private */
        public void finishRendering() {
            Trace.beginSection("ImageWallpaper#finishRendering");
            EglHelper eglHelper = this.mEglHelper;
            if (eglHelper != null) {
                eglHelper.destroyEglSurface();
                this.mEglHelper.destroyEglContext();
            }
            Trace.endSection();
        }

        private boolean needSupportWideColorGamut() {
            return this.mRenderer.isWcgContent();
        }

        /* access modifiers changed from: protected */
        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            super.dump(str, fileDescriptor, printWriter, strArr);
            printWriter.print(str);
            printWriter.print("Engine=");
            printWriter.println(this);
            printWriter.print(str);
            printWriter.print("valid surface=");
            Object obj = "null";
            printWriter.println((getSurfaceHolder() == null || getSurfaceHolder().getSurface() == null) ? obj : Boolean.valueOf(getSurfaceHolder().getSurface().isValid()));
            printWriter.print(str);
            printWriter.print("surface frame=");
            if (getSurfaceHolder() != null) {
                obj = getSurfaceHolder().getSurfaceFrame();
            }
            printWriter.println(obj);
            this.mEglHelper.dump(str, fileDescriptor, printWriter, strArr);
            this.mRenderer.dump(str, fileDescriptor, printWriter, strArr);
        }
    }
}
