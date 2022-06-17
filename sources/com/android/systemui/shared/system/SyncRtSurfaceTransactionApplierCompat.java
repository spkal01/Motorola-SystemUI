package com.android.systemui.shared.system;

import android.graphics.HardwareRenderer;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import java.util.function.Consumer;

public class SyncRtSurfaceTransactionApplierCompat {
    private Runnable mAfterApplyCallback;
    /* access modifiers changed from: private */
    public final Handler mApplyHandler;
    /* access modifiers changed from: private */
    public final SurfaceControl mBarrierSurfaceControl;
    private int mPendingSequenceNumber = 0;
    private int mSequenceNumber = 0;
    /* access modifiers changed from: private */
    public final ViewRootImpl mTargetViewRootImpl;

    public SyncRtSurfaceTransactionApplierCompat(View view) {
        SurfaceControl surfaceControl = null;
        ViewRootImpl viewRootImpl = view != null ? view.getViewRootImpl() : null;
        this.mTargetViewRootImpl = viewRootImpl;
        this.mBarrierSurfaceControl = viewRootImpl != null ? viewRootImpl.getSurfaceControl() : surfaceControl;
        this.mApplyHandler = new Handler(new Handler.Callback() {
            public boolean handleMessage(Message message) {
                if (message.what != 0) {
                    return false;
                }
                SyncRtSurfaceTransactionApplierCompat.this.onApplyMessage(message.arg1);
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    public void onApplyMessage(int i) {
        Runnable runnable;
        this.mSequenceNumber = i;
        if (i == this.mPendingSequenceNumber && (runnable = this.mAfterApplyCallback) != null) {
            this.mAfterApplyCallback = null;
            runnable.run();
        }
    }

    public void scheduleApply(final SurfaceParams... surfaceParamsArr) {
        ViewRootImpl viewRootImpl = this.mTargetViewRootImpl;
        if (viewRootImpl != null && viewRootImpl.getView() != null) {
            final int i = this.mPendingSequenceNumber + 1;
            this.mPendingSequenceNumber = i;
            this.mTargetViewRootImpl.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() {
                public void onFrameDraw(long j) {
                    if (SyncRtSurfaceTransactionApplierCompat.this.mBarrierSurfaceControl == null || !SyncRtSurfaceTransactionApplierCompat.this.mBarrierSurfaceControl.isValid()) {
                        Message.obtain(SyncRtSurfaceTransactionApplierCompat.this.mApplyHandler, 0, i, 0).sendToTarget();
                        return;
                    }
                    Trace.traceBegin(8, "Sync transaction frameNumber=" + j);
                    SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                    for (int length = surfaceParamsArr.length + -1; length >= 0; length--) {
                        surfaceParamsArr[length].applyTo(transaction);
                    }
                    if (SyncRtSurfaceTransactionApplierCompat.this.mTargetViewRootImpl != null) {
                        SyncRtSurfaceTransactionApplierCompat.this.mTargetViewRootImpl.mergeWithNextTransaction(transaction, j);
                    } else {
                        transaction.apply();
                    }
                    Trace.traceEnd(8);
                    Message.obtain(SyncRtSurfaceTransactionApplierCompat.this.mApplyHandler, 0, i, 0).sendToTarget();
                }
            });
            this.mTargetViewRootImpl.getView().invalidate();
        }
    }

    public void addAfterApplyCallback(final Runnable runnable) {
        if (this.mSequenceNumber == this.mPendingSequenceNumber) {
            runnable.run();
            return;
        }
        final Runnable runnable2 = this.mAfterApplyCallback;
        if (runnable2 == null) {
            this.mAfterApplyCallback = runnable;
        } else {
            this.mAfterApplyCallback = new Runnable() {
                public void run() {
                    runnable.run();
                    runnable2.run();
                }
            };
        }
    }

    public static void applyParams(TransactionCompat transactionCompat, SurfaceParams surfaceParams) {
        surfaceParams.applyTo(transactionCompat.mTransaction);
    }

    public static void create(final View view, final Consumer<SyncRtSurfaceTransactionApplierCompat> consumer) {
        if (view == null) {
            consumer.accept((Object) null);
        } else if (view.getViewRootImpl() != null) {
            consumer.accept(new SyncRtSurfaceTransactionApplierCompat(view));
        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                public void onViewDetachedFromWindow(View view) {
                }

                public void onViewAttachedToWindow(View view) {
                    view.removeOnAttachStateChangeListener(this);
                    consumer.accept(new SyncRtSurfaceTransactionApplierCompat(view));
                }
            });
        }
    }

    public static class SurfaceParams {
        public final float alpha;
        public final int backgroundBlurRadius;
        public final float cornerRadius;
        private final int flags;
        public final int layer;
        private final float[] mTmpValues;
        public final Matrix matrix;
        public final int relativeLayer;
        public final SurfaceControl relativeTo;
        public final float shadowRadius;
        public final SurfaceControl surface;
        public final boolean visible;
        public final Rect windowCrop;

        public static class Builder {
            float alpha;
            int backgroundBlurRadius;
            float cornerRadius;
            int flags;
            int layer;
            Matrix matrix;
            int relativeLayer;
            SurfaceControl relativeTo;
            float shadowRadius;
            final SurfaceControl surface;
            boolean visible;
            Rect windowCrop;

            public Builder(SurfaceControlCompat surfaceControlCompat) {
                this(surfaceControlCompat.mSurfaceControl);
            }

            public Builder(SurfaceControl surfaceControl) {
                this.surface = surfaceControl;
            }

            public Builder withAlpha(float f) {
                this.alpha = f;
                this.flags |= 1;
                return this;
            }

            public Builder withMatrix(Matrix matrix2) {
                this.matrix = new Matrix(matrix2);
                this.flags |= 2;
                return this;
            }

            public Builder withWindowCrop(Rect rect) {
                this.windowCrop = new Rect(rect);
                this.flags |= 4;
                return this;
            }

            public Builder withLayer(int i) {
                this.layer = i;
                this.flags |= 8;
                return this;
            }

            public Builder withCornerRadius(float f) {
                this.cornerRadius = f;
                this.flags |= 16;
                return this;
            }

            public SurfaceParams build() {
                return new SurfaceParams(this.surface, this.flags, this.alpha, this.matrix, this.windowCrop, this.layer, this.relativeTo, this.relativeLayer, this.cornerRadius, this.backgroundBlurRadius, this.visible, this.shadowRadius);
            }
        }

        private SurfaceParams(SurfaceControl surfaceControl, int i, float f, Matrix matrix2, Rect rect, int i2, SurfaceControl surfaceControl2, int i3, float f2, int i4, boolean z, float f3) {
            this.mTmpValues = new float[9];
            this.flags = i;
            this.surface = surfaceControl;
            this.alpha = f;
            this.matrix = matrix2;
            this.windowCrop = rect;
            this.layer = i2;
            this.relativeTo = surfaceControl2;
            this.relativeLayer = i3;
            this.cornerRadius = f2;
            this.backgroundBlurRadius = i4;
            this.visible = z;
            this.shadowRadius = f3;
        }

        public void applyTo(SurfaceControl.Transaction transaction) {
            if ((this.flags & 2) != 0) {
                transaction.setMatrix(this.surface, this.matrix, this.mTmpValues);
            }
            if ((this.flags & 4) != 0) {
                transaction.setWindowCrop(this.surface, this.windowCrop);
            }
            if ((this.flags & 1) != 0) {
                transaction.setAlpha(this.surface, this.alpha);
            }
            if ((this.flags & 8) != 0) {
                transaction.setLayer(this.surface, this.layer);
            }
            if ((this.flags & 16) != 0) {
                transaction.setCornerRadius(this.surface, this.cornerRadius);
            }
            if ((this.flags & 32) != 0) {
                transaction.setBackgroundBlurRadius(this.surface, this.backgroundBlurRadius);
            }
            if ((this.flags & 64) != 0) {
                if (this.visible) {
                    transaction.show(this.surface);
                } else {
                    transaction.hide(this.surface);
                }
            }
            if ((this.flags & 128) != 0) {
                transaction.setRelativeLayer(this.surface, this.relativeTo, this.relativeLayer);
            }
            if ((this.flags & 256) != 0) {
                transaction.setShadowRadius(this.surface, this.shadowRadius);
            }
        }
    }
}
