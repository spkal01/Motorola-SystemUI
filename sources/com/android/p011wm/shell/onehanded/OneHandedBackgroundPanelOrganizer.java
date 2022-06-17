package com.android.p011wm.shell.onehanded;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.animation.LinearInterpolator;
import android.window.DisplayAreaAppearedInfo;
import android.window.DisplayAreaInfo;
import android.window.DisplayAreaOrganizer;
import androidx.appcompat.view.ContextThemeWrapper;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.onehanded.OneHandedState;
import com.android.p011wm.shell.onehanded.OneHandedSurfaceTransactionHelper;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Executor;

/* renamed from: com.android.wm.shell.onehanded.OneHandedBackgroundPanelOrganizer */
public class OneHandedBackgroundPanelOrganizer extends DisplayAreaOrganizer implements OneHandedAnimationCallback, OneHandedState.OnStateChangedListener {
    private ValueAnimator mAlphaAnimator;
    SurfaceControl mBackgroundSurface;
    private Rect mBkgBounds;
    private final Context mContext;
    private int mCurrentState;
    private SurfaceControl mParentLeash;
    private Rect mStableInsets;
    private final SurfaceSession mSurfaceSession = new SurfaceSession();
    private float[] mThemeColor;
    private final OneHandedSurfaceTransactionHelper.SurfaceControlTransactionFactory mTransactionFactory;
    private float mTranslationFraction;

    public OneHandedBackgroundPanelOrganizer(Context context, DisplayLayout displayLayout, OneHandedSettingsUtil oneHandedSettingsUtil, Executor executor) {
        super(executor);
        this.mContext = context;
        this.mTranslationFraction = oneHandedSettingsUtil.getTranslationFraction(context);
        this.mTransactionFactory = C2333x55a9da47.INSTANCE;
        updateThemeColors();
    }

    public void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo, SurfaceControl surfaceControl) {
        this.mParentLeash = surfaceControl;
    }

    public List<DisplayAreaAppearedInfo> registerOrganizer(int i) {
        List<DisplayAreaAppearedInfo> registerOrganizer = OneHandedBackgroundPanelOrganizer.super.registerOrganizer(i);
        for (int i2 = 0; i2 < registerOrganizer.size(); i2++) {
            DisplayAreaAppearedInfo displayAreaAppearedInfo = registerOrganizer.get(i2);
            onDisplayAreaAppeared(displayAreaAppearedInfo.getDisplayAreaInfo(), displayAreaAppearedInfo.getLeash());
        }
        return registerOrganizer;
    }

    public void unregisterOrganizer() {
        OneHandedBackgroundPanelOrganizer.super.unregisterOrganizer();
        removeBackgroundPanelLayer();
        this.mParentLeash = null;
    }

    public void onAnimationUpdate(SurfaceControl.Transaction transaction, float f, float f2) {
        transaction.setPosition(this.mBackgroundSurface, 0.0f, (float) ((this.mStableInsets.top - this.mBkgBounds.height()) + Math.round(f2)));
    }

    /* access modifiers changed from: package-private */
    public boolean isRegistered() {
        return this.mParentLeash != null;
    }

    /* access modifiers changed from: package-private */
    public void createBackgroundSurface() {
        this.mBackgroundSurface = new SurfaceControl.Builder(this.mSurfaceSession).setBufferSize(this.mBkgBounds.width(), this.mBkgBounds.height()).setColorLayer().setFormat(3).setOpaque(true).setName("one-handed-background-panel").setCallsite("OneHandedBackgroundPanelOrganizer").build();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        this.mAlphaAnimator = ofFloat;
        ofFloat.setInterpolator(new LinearInterpolator());
        this.mAlphaAnimator.setDuration(200);
        this.mAlphaAnimator.addUpdateListener(new OneHandedBackgroundPanelOrganizer$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: detachBackgroundFromParent */
    public void lambda$createBackgroundSurface$0(ValueAnimator valueAnimator) {
        if (this.mBackgroundSurface != null && this.mParentLeash != null) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            SurfaceControl.Transaction transaction = this.mTransactionFactory.getTransaction();
            if (floatValue == 0.0f) {
                transaction.reparent(this.mBackgroundSurface, (SurfaceControl) null).apply();
            } else {
                transaction.setAlpha(this.mBackgroundSurface, ((Float) valueAnimator.getAnimatedValue()).floatValue()).apply();
            }
        }
    }

    public void onDisplayChanged(DisplayLayout displayLayout) {
        this.mStableInsets = displayLayout.stableInsets();
        if (displayLayout.height() > displayLayout.width()) {
            this.mBkgBounds = new Rect(0, 0, displayLayout.width(), Math.round(((float) displayLayout.height()) * this.mTranslationFraction) + this.mStableInsets.top);
        } else {
            this.mBkgBounds = new Rect(0, 0, displayLayout.height(), Math.round(((float) displayLayout.width()) * this.mTranslationFraction) + this.mStableInsets.top);
        }
    }

    /* access modifiers changed from: package-private */
    public void onStart() {
        if (this.mBackgroundSurface == null) {
            createBackgroundSurface();
        }
        showBackgroundPanelLayer();
    }

    public void onStopFinished() {
        ValueAnimator valueAnimator = this.mAlphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void showBackgroundPanelLayer() {
        if (this.mParentLeash != null) {
            if (this.mBackgroundSurface == null) {
                createBackgroundSurface();
            }
            if (this.mAlphaAnimator.isRunning()) {
                this.mAlphaAnimator.end();
            }
            this.mTransactionFactory.getTransaction().reparent(this.mBackgroundSurface, this.mParentLeash).setAlpha(this.mBackgroundSurface, 1.0f).setLayer(this.mBackgroundSurface, -1).setColor(this.mBackgroundSurface, this.mThemeColor).show(this.mBackgroundSurface).apply();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeBackgroundPanelLayer() {
        if (this.mBackgroundSurface != null) {
            this.mTransactionFactory.getTransaction().remove(this.mBackgroundSurface).apply();
            this.mBackgroundSurface = null;
        }
    }

    public void onConfigurationChanged() {
        updateThemeColors();
        if (this.mCurrentState == 2) {
            showBackgroundPanelLayer();
        }
    }

    private void updateThemeColors() {
        int color = new ContextThemeWrapper(this.mContext, 16974563).getColor(C2219R.C2220color.one_handed_tutorial_background_color);
        this.mThemeColor = new float[]{adjustColor(Color.red(color)), adjustColor(Color.green(color)), adjustColor(Color.blue(color))};
    }

    private float adjustColor(int i) {
        return ((float) Math.max(i - 10, 0)) / 255.0f;
    }

    public void onStateChanged(int i) {
        this.mCurrentState = i;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter printWriter) {
        printWriter.println("OneHandedBackgroundPanelOrganizer");
        printWriter.print("  mBackgroundSurface=");
        printWriter.println(this.mBackgroundSurface);
        printWriter.print("  mBkgBounds=");
        printWriter.println(this.mBkgBounds);
        printWriter.print("  mThemeColor=");
        printWriter.println(this.mThemeColor);
        printWriter.print("  mTranslationFraction=");
        printWriter.println(this.mTranslationFraction);
    }
}
