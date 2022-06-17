package com.motorola.systemui.cli.navgesture.util;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.recents.utilities.RectFEvaluator;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.systemui.shared.system.TransactionCompat;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationTargetSet;
import com.motorola.systemui.cli.navgesture.recents.ITaskViewAware;
import java.util.function.BiFunction;

public class ClipAnimationHelper {
    private static final String LOG_TAG = "ClipAnimationHelper";
    private int mBoostModeTargetLayers = -1;
    private final RectF mClipRectF = new RectF();
    private float mCurrentCornerRadius;
    private final RectF mCurrentRectWithInsets = new RectF();
    public final Rect mHomeStackBounds = new Rect();
    private final RectFEvaluator mRectFEvaluator = new RectFEvaluator();
    private final Rect mSourceInsets = new Rect();
    private final RectF mSourceRect = new RectF();
    private final Rect mSourceStackBounds = new Rect();
    private final RectF mSourceWindowClipInsets = new RectF();
    private final RectF mSourceWindowClipInsetsForLiveTile = new RectF();
    private final boolean mSupportsRoundedCornersOnWindows;
    private final RectF mTargetRect = new RectF();
    private BiFunction<RemoteAnimationTargetCompat, Float, Float> mTaskAlphaCallback = ClipAnimationHelper$$ExternalSyntheticLambda0.INSTANCE;
    private final float mTaskCornerRadius;
    private final Matrix mTmpMatrix = new Matrix();
    private final Rect mTmpRect = new Rect();
    private final RectF mTmpRectF = new RectF();
    private boolean mUseRoundedCornersOnWindows;
    private final float mWindowCornerRadius;

    /* access modifiers changed from: private */
    public static /* synthetic */ Float lambda$new$0(RemoteAnimationTargetCompat remoteAnimationTargetCompat, Float f) {
        return f;
    }

    public ClipAnimationHelper(Context context) {
        this.mWindowCornerRadius = QuickStepContract.getWindowCornerRadius(context.getResources());
        boolean supportsRoundedCornersOnWindows = QuickStepContract.supportsRoundedCornersOnWindows(context.getResources());
        this.mSupportsRoundedCornersOnWindows = supportsRoundedCornersOnWindows;
        this.mTaskCornerRadius = TaskCornerRadius.get(context);
        this.mUseRoundedCornersOnWindows = supportsRoundedCornersOnWindows;
    }

    private void updateSourceStack(RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.mSourceInsets.set(remoteAnimationTargetCompat.contentInsets);
        this.mSourceStackBounds.set(remoteAnimationTargetCompat.sourceContainerBounds);
        Rect rect = this.mSourceStackBounds;
        Point point = remoteAnimationTargetCompat.position;
        rect.offsetTo(point.x, point.y);
    }

    public void updateSource(Rect rect, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        this.mHomeStackBounds.set(rect);
        updateSourceStack(remoteAnimationTargetCompat);
    }

    public void updateTargetRect(Rect rect) {
        RectF rectF = this.mSourceRect;
        Rect rect2 = this.mSourceInsets;
        rectF.set((float) rect2.left, (float) rect2.top, (float) (this.mSourceStackBounds.width() - this.mSourceInsets.right), (float) (this.mSourceStackBounds.height() - this.mSourceInsets.bottom));
        this.mTargetRect.set(rect);
        RectF rectF2 = this.mTargetRect;
        Rect rect3 = this.mHomeStackBounds;
        int i = rect3.left;
        Rect rect4 = this.mSourceStackBounds;
        rectF2.offset((float) (i - rect4.left), (float) (rect3.top - rect4.top));
        RectF rectF3 = new RectF(this.mTargetRect);
        Utilities.scaleRectFAboutCenter(rectF3, this.mSourceRect.width() / this.mTargetRect.width());
        RectF rectF4 = this.mSourceRect;
        rectF3.offsetTo(rectF4.left, rectF4.top);
        this.mSourceWindowClipInsets.set(Math.max(rectF3.left, 0.0f), Math.max(rectF3.top, 0.0f), Math.max(((float) this.mSourceStackBounds.width()) - rectF3.right, 0.0f), Math.max(((float) this.mSourceStackBounds.height()) - rectF3.bottom, 0.0f));
        this.mSourceWindowClipInsetsForLiveTile.set(this.mSourceWindowClipInsets);
        this.mSourceRect.set(rectF3);
    }

    public void prepareAnimation(boolean z) {
        this.mBoostModeTargetLayers = z ^ true ? 1 : 0;
    }

    public RectF applyTransform(RemoteAnimationTargetSet remoteAnimationTargetSet, TransformParams transformParams) {
        float f;
        float f2;
        RemoteAnimationTargetSet remoteAnimationTargetSet2 = remoteAnimationTargetSet;
        TransformParams transformParams2 = transformParams;
        float f3 = transformParams2.progress;
        if (transformParams2.currentRect == null) {
            this.mTmpRectF.set(this.mTargetRect);
            Utilities.scaleRectFAboutCenter(this.mTmpRectF, transformParams2.offsetScale);
            RectF evaluate = this.mRectFEvaluator.evaluate(f3, this.mSourceRect, this.mTmpRectF);
            evaluate.offset(transformParams2.offsetX, 0.0f);
            f3 = Math.min(1.0f, f3);
            RectF rectF = transformParams2.forLiveTile ? this.mSourceWindowClipInsetsForLiveTile : this.mSourceWindowClipInsets;
            RectF rectF2 = this.mClipRectF;
            rectF2.left = rectF.left * f3;
            rectF2.top = rectF.top * f3;
            rectF2.right = ((float) this.mSourceStackBounds.width()) - (rectF.right * f3);
            this.mClipRectF.bottom = ((float) this.mSourceStackBounds.height()) - (rectF.bottom * f3);
            transformParams2.setCurrentRectAndTargetAlpha(evaluate, 1.0f);
        }
        SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargetSet2.unfilteredApps.length];
        int i = 0;
        int i2 = 0;
        while (true) {
            RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = remoteAnimationTargetSet2.unfilteredApps;
            if (i2 < remoteAnimationTargetCompatArr.length) {
                RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[i2];
                Matrix matrix = this.mTmpMatrix;
                Point point = remoteAnimationTargetCompat.position;
                matrix.setTranslate((float) point.x, (float) point.y);
                Rect rect = this.mTmpRect;
                rect.set(remoteAnimationTargetCompat.sourceContainerBounds);
                rect.offsetTo(i, i);
                int layer = RemoteAnimationProvider.getLayer(remoteAnimationTargetCompat, this.mBoostModeTargetLayers);
                Log.d(LOG_TAG, "applyTransform layer = " + layer + "; mBoostModeTargetLayers = " + this.mBoostModeTargetLayers + "; app type = " + remoteAnimationTargetCompat.activityType + "; mode = " + remoteAnimationTargetCompat.mode);
                float max = Math.max(transformParams2.currentRect.width(), this.mTargetRect.width()) / ((float) rect.width());
                if (remoteAnimationTargetCompat.mode == remoteAnimationTargetSet2.targetMode) {
                    f = this.mTaskAlphaCallback.apply(remoteAnimationTargetCompat, Float.valueOf(transformParams2.targetAlpha)).floatValue();
                    if (remoteAnimationTargetCompat.activityType != 2) {
                        this.mTmpMatrix.setRectToRect(this.mSourceRect, transformParams2.currentRect, Matrix.ScaleToFit.FILL);
                        Matrix matrix2 = this.mTmpMatrix;
                        Point point2 = remoteAnimationTargetCompat.position;
                        matrix2.postTranslate((float) point2.x, (float) point2.y);
                        this.mClipRectF.roundOut(rect);
                        if (this.mSupportsRoundedCornersOnWindows) {
                            f2 = transformParams2.cornerRadius;
                            if (f2 > -1.0f) {
                                max = transformParams2.currentRect.width() / ((float) rect.width());
                            } else {
                                f2 = Utilities.mapRange(f3, this.mUseRoundedCornersOnWindows ? this.mWindowCornerRadius : 0.0f, this.mTaskCornerRadius);
                            }
                            this.mCurrentCornerRadius = f2;
                            SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                            builder.withAlpha(f);
                            builder.withMatrix(this.mTmpMatrix);
                            builder.withWindowCrop(rect);
                            builder.withLayer(layer);
                            builder.withCornerRadius(f2 / max);
                            surfaceParamsArr[i2] = builder.build();
                            i2++;
                            i = 0;
                        }
                    } else if (remoteAnimationTargetSet2.hasRecents) {
                        f = 1.0f - (transformParams2.targetAlpha * f3);
                    }
                } else {
                    f = 1.0f;
                }
                f2 = 0.0f;
                SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder2 = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
                builder2.withAlpha(f);
                builder2.withMatrix(this.mTmpMatrix);
                builder2.withWindowCrop(rect);
                builder2.withLayer(layer);
                builder2.withCornerRadius(f2 / max);
                surfaceParamsArr[i2] = builder2.build();
                i2++;
                i = 0;
            } else {
                applySurfaceParams(transformParams2.syncTransactionApplier, surfaceParamsArr);
                return transformParams2.currentRect;
            }
        }
    }

    private void applySurfaceParams(SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat, SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr) {
        if (syncRtSurfaceTransactionApplierCompat != null) {
            syncRtSurfaceTransactionApplierCompat.scheduleApply(surfaceParamsArr);
            return;
        }
        TransactionCompat transactionCompat = new TransactionCompat();
        for (SyncRtSurfaceTransactionApplierCompat.SurfaceParams surfaceParams : surfaceParamsArr) {
            if (surfaceParams.surface.isValid()) {
                SyncRtSurfaceTransactionApplierCompat.applyParams(transactionCompat, surfaceParams);
            }
        }
        transactionCompat.apply();
    }

    public void setTaskAlphaCallback(BiFunction<RemoteAnimationTargetCompat, Float, Float> biFunction) {
        this.mTaskAlphaCallback = biFunction;
    }

    public void fromTaskThumbnailView(ITaskViewAware iTaskViewAware, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        View thumbnail = iTaskViewAware.getThumbnail();
        if (thumbnail != null) {
            fromTaskThumbnailView(thumbnail, iTaskViewAware.getThumbnailData(), remoteAnimationTargetCompat);
        }
    }

    public void fromTaskThumbnailView(View view, ThumbnailData thumbnailData, RemoteAnimationTargetCompat remoteAnimationTargetCompat) {
        ViewGroup rootView = BaseGestureActivity.fromContext(view.getContext()).getRootView();
        int[] iArr = new int[2];
        rootView.getLocationOnScreen(iArr);
        this.mHomeStackBounds.set(0, 0, rootView.getWidth(), rootView.getHeight());
        this.mHomeStackBounds.offset(iArr[0], iArr[1]);
        if (remoteAnimationTargetCompat != null) {
            updateSourceStack(remoteAnimationTargetCompat);
        } else {
            this.mSourceStackBounds.set(this.mHomeStackBounds);
            Rect rect = new Rect();
            if (thumbnailData != null) {
                rect = thumbnailData.insets;
            }
            this.mSourceInsets.set(rect);
        }
        Rect rect2 = new Rect();
        Utilities.getDescendantCoordRelativeToAncestor(view, rootView, rect2);
        updateTargetRect(rect2);
        if (remoteAnimationTargetCompat == null) {
            float width = this.mTargetRect.width() / this.mSourceRect.width();
            RectF rectF = this.mSourceWindowClipInsets;
            rectF.left *= width;
            rectF.top *= width;
            rectF.right *= width;
            rectF.bottom *= width;
        }
    }

    public ScaleTranslation getScaleAndTranslation() {
        float width = this.mSourceRect.width() / this.mTargetRect.width();
        if (Float.isNaN(width) || Float.isInfinite(width)) {
            String str = LOG_TAG;
            Log.w(str, "getScaleAndTranslation: invalid scale:  source rect: " + this.mSourceRect + " target rect: " + this.mTargetRect);
            width = 0.0f;
        }
        return new ScaleTranslation(width, 0.0f, (this.mSourceRect.centerY() - this.mSourceRect.top) - this.mTargetRect.centerY());
    }

    public RectF getTargetRect() {
        return this.mTargetRect;
    }

    public static class TransformParams {
        float cornerRadius = -1.0f;
        RectF currentRect = null;
        boolean forLiveTile = false;
        float offsetScale = 1.0f;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float progress = 0.0f;
        SyncRtSurfaceTransactionApplierCompat syncTransactionApplier;
        float targetAlpha = 0.0f;

        public TransformParams setProgress(float f) {
            this.progress = f;
            this.currentRect = null;
            return this;
        }

        public TransformParams setCurrentRectAndTargetAlpha(RectF rectF, float f) {
            this.currentRect = rectF;
            this.targetAlpha = f;
            return this;
        }

        public TransformParams setOffsetX(float f) {
            this.offsetX = f;
            return this;
        }

        public TransformParams setOffsetScale(float f) {
            this.offsetScale = f;
            return this;
        }

        public TransformParams setSyncTransactionApplier(SyncRtSurfaceTransactionApplierCompat syncRtSurfaceTransactionApplierCompat) {
            this.syncTransactionApplier = syncRtSurfaceTransactionApplierCompat;
            return this;
        }
    }
}
