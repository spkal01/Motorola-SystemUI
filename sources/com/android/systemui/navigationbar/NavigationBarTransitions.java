package com.android.systemui.navigationbar;

import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindowManager;
import android.view.View;
import androidx.appcompat.R$styleable;
import com.android.systemui.Dependency;
import com.android.systemui.R$bool;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.navigationbar.buttons.ButtonDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.BarTransitions;
import com.android.systemui.statusbar.phone.LightBarTransitionsController;
import com.android.systemui.util.Utils;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class NavigationBarTransitions extends BarTransitions implements LightBarTransitionsController.DarkIntensityApplier {
    private final boolean mAllowAutoDimWallpaperNotVisible;
    private boolean mAutoDim;
    private List<DarkIntensityListener> mDarkIntensityListeners;
    /* access modifiers changed from: private */
    public final Handler mHandler = Handler.getMain();
    private final LightBarTransitionsController mLightTransitionsController;
    private boolean mLightsOut;
    private int mNavBarMode = 0;
    private View mNavButtons;
    private final NavigationBarView mView;
    private final IWallpaperVisibilityListener mWallpaperVisibilityListener;
    /* access modifiers changed from: private */
    public boolean mWallpaperVisible;

    public interface DarkIntensityListener {
        void onDarkIntensity(float f);
    }

    public NavigationBarTransitions(NavigationBarView navigationBarView, CommandQueue commandQueue) {
        super(navigationBarView, R$drawable.nav_background);
        C10801 r1 = new IWallpaperVisibilityListener.Stub() {
            public void onWallpaperVisibilityChanged(boolean z, int i) throws RemoteException {
                boolean unused = NavigationBarTransitions.this.mWallpaperVisible = z;
                NavigationBarTransitions.this.mHandler.post(new NavigationBarTransitions$1$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onWallpaperVisibilityChanged$0() {
                NavigationBarTransitions.this.applyLightsOut(true, false);
            }
        };
        this.mWallpaperVisibilityListener = r1;
        this.mView = navigationBarView;
        this.mLightTransitionsController = new LightBarTransitionsController(navigationBarView.getContext(), this, commandQueue);
        this.mAllowAutoDimWallpaperNotVisible = navigationBarView.getContext().getResources().getBoolean(R$bool.config_navigation_bar_enable_auto_dim_no_visible_wallpaper);
        this.mDarkIntensityListeners = new ArrayList();
        try {
            this.mWallpaperVisible = ((IWindowManager) Dependency.get(IWindowManager.class)).registerWallpaperVisibilityListener(r1, 0);
        } catch (RemoteException unused) {
        }
        this.mView.addOnLayoutChangeListener(new NavigationBarTransitions$$ExternalSyntheticLambda0(this));
        View currentView = this.mView.getCurrentView();
        if (currentView != null) {
            this.mNavButtons = currentView.findViewById(R$id.nav_buttons);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        View currentView = this.mView.getCurrentView();
        if (currentView != null) {
            this.mNavButtons = currentView.findViewById(R$id.nav_buttons);
            applyLightsOut(false, true);
        }
    }

    public void init() {
        applyModeBackground(-1, getMode(), false);
        applyLightsOut(false, true);
    }

    public void destroy() {
        try {
            ((IWindowManager) Dependency.get(IWindowManager.class)).unregisterWallpaperVisibilityListener(this.mWallpaperVisibilityListener, 0);
        } catch (RemoteException unused) {
        }
    }

    public void setAutoDim(boolean z) {
        if ((!z || !Utils.isGesturalModeOnDefaultDisplay(this.mView.getContext(), this.mNavBarMode) || this.mView.getContext().getDisplayId() == 0) && this.mAutoDim != z) {
            this.mAutoDim = z;
            applyLightsOut(true, false);
        }
    }

    /* access modifiers changed from: package-private */
    public void setBackgroundFrame(Rect rect) {
        this.mBarBackground.setFrame(rect);
    }

    /* access modifiers changed from: package-private */
    public void setBackgroundOverrideAlpha(float f) {
        this.mBarBackground.setOverrideAlpha(f);
    }

    /* access modifiers changed from: protected */
    public boolean isLightsOut(int i) {
        if (!MotoFeature.getInstance(this.mView.getContext()).isOledProduct() || this.mLightTransitionsController.getCurrentDarkIntensity() != 0.0f) {
            if (super.isLightsOut(i) || (this.mAllowAutoDimWallpaperNotVisible && this.mAutoDim && !this.mWallpaperVisible && i != 5)) {
                return true;
            }
            return false;
        } else if (super.isLightsOut(i) || (this.mAllowAutoDimWallpaperNotVisible && this.mAutoDim && i != 5)) {
            return true;
        } else {
            return false;
        }
    }

    public LightBarTransitionsController getLightTransitionsController() {
        return this.mLightTransitionsController;
    }

    /* access modifiers changed from: protected */
    public void onTransition(int i, int i2, boolean z) {
        super.onTransition(i, i2, z);
        applyLightsOut(z, false);
        this.mView.onBarTransition(i2);
    }

    /* access modifiers changed from: private */
    public void applyLightsOut(boolean z, boolean z2) {
        applyLightsOut(isLightsOut(getMode()), z, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyLightsOut(boolean r3, boolean r4, boolean r5) {
        /*
            r2 = this;
            if (r5 != 0) goto L_0x0007
            boolean r5 = r2.mLightsOut
            if (r3 != r5) goto L_0x0007
            return
        L_0x0007:
            r2.mLightsOut = r3
            android.view.View r5 = r2.mNavButtons
            if (r5 != 0) goto L_0x000e
            return
        L_0x000e:
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
            com.android.systemui.statusbar.phone.LightBarTransitionsController r5 = r2.mLightTransitionsController
            float r5 = r5.getCurrentDarkIntensity()
            r0 = 1092616192(0x41200000, float:10.0)
            float r5 = r5 / r0
            com.android.systemui.navigationbar.NavigationBarView r0 = r2.mView
            android.content.Context r0 = r0.getContext()
            com.android.systemui.moto.MotoFeature r0 = com.android.systemui.moto.MotoFeature.getInstance(r0)
            boolean r0 = r0.isOledProduct()
            r1 = 1065353216(0x3f800000, float:1.0)
            if (r0 == 0) goto L_0x003d
            r0 = 0
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 != 0) goto L_0x003d
            if (r3 == 0) goto L_0x0043
            r0 = 1057803469(0x3f0ccccd, float:0.55)
        L_0x003a:
            float r1 = r5 + r0
            goto L_0x0043
        L_0x003d:
            if (r3 == 0) goto L_0x0043
            r0 = 1058642330(0x3f19999a, float:0.6)
            goto L_0x003a
        L_0x0043:
            if (r4 != 0) goto L_0x004b
            android.view.View r2 = r2.mNavButtons
            r2.setAlpha(r1)
            goto L_0x0064
        L_0x004b:
            if (r3 == 0) goto L_0x0050
            r3 = 1500(0x5dc, float:2.102E-42)
            goto L_0x0052
        L_0x0050:
            r3 = 250(0xfa, float:3.5E-43)
        L_0x0052:
            android.view.View r2 = r2.mNavButtons
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.alpha(r1)
            long r3 = (long) r3
            android.view.ViewPropertyAnimator r2 = r2.setDuration(r3)
            r2.start()
        L_0x0064:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.navigationbar.NavigationBarTransitions.applyLightsOut(boolean, boolean, boolean):void");
    }

    public void reapplyDarkIntensity() {
        applyDarkIntensity(this.mLightTransitionsController.getCurrentDarkIntensity());
    }

    public void applyDarkIntensity(float f) {
        SparseArray<ButtonDispatcher> buttonDispatchers = this.mView.getButtonDispatchers();
        for (int size = buttonDispatchers.size() - 1; size >= 0; size--) {
            buttonDispatchers.valueAt(size).setDarkIntensity(f);
        }
        this.mView.getRotationButtonController().setDarkIntensity(f);
        for (DarkIntensityListener onDarkIntensity : this.mDarkIntensityListeners) {
            onDarkIntensity.onDarkIntensity(f);
        }
        if (this.mAutoDim) {
            applyLightsOut(false, true);
        }
    }

    public int getTintAnimationDuration() {
        return Utils.isGesturalModeOnDefaultDisplay(this.mView.getContext(), this.mNavBarMode) ? Math.max(1700, 400) : R$styleable.AppCompatTheme_windowFixedHeightMajor;
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
    }

    public float addDarkIntensityListener(DarkIntensityListener darkIntensityListener) {
        this.mDarkIntensityListeners.add(darkIntensityListener);
        return this.mLightTransitionsController.getCurrentDarkIntensity();
    }

    public void removeDarkIntensityListener(DarkIntensityListener darkIntensityListener) {
        this.mDarkIntensityListeners.remove(darkIntensityListener);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("NavigationBarTransitions:");
        printWriter.println("  mMode: " + getMode());
        printWriter.println("  mAlwaysOpaque: " + isAlwaysOpaque());
        printWriter.println("  mAllowAutoDimWallpaperNotVisible: " + this.mAllowAutoDimWallpaperNotVisible);
        printWriter.println("  mWallpaperVisible: " + this.mWallpaperVisible);
        printWriter.println("  mLightsOut: " + this.mLightsOut);
        printWriter.println("  mAutoDim: " + this.mAutoDim);
        printWriter.println("  bg overrideAlpha: " + this.mBarBackground.getOverrideAlpha());
        printWriter.println("  bg color: " + this.mBarBackground.getColor());
        printWriter.println("  bg frame: " + this.mBarBackground.getFrame());
    }
}
