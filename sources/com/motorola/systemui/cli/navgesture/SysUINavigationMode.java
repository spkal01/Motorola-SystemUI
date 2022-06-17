package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import com.android.systemui.Dependency;
import com.android.systemui.navigationbar.NavigationModeController;
import java.util.ArrayList;
import java.util.List;

public class SysUINavigationMode implements NavigationModeController.ModeChangedListener {
    private static SysUINavigationMode sInstance;
    private final List<NavigationModeChangeListener> mChangeListeners;
    private int mNavBarMode;

    public interface NavigationModeChangeListener {
        void onNavigationModeChanged(int i);
    }

    public boolean isOverviewDisabled() {
        return false;
    }

    public static SysUINavigationMode getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SysUINavigationMode(context);
        }
        return sInstance;
    }

    public SysUINavigationMode(Context context) {
        this.mNavBarMode = 0;
        this.mChangeListeners = new ArrayList();
        this.mNavBarMode = ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(this);
    }

    public void onNavigationModeChanged(int i) {
        this.mNavBarMode = i;
        dispatchModeChange();
    }

    public boolean isGestureMode() {
        return this.mNavBarMode == 2;
    }

    private void dispatchModeChange() {
        for (NavigationModeChangeListener onNavigationModeChanged : this.mChangeListeners) {
            onNavigationModeChanged.onNavigationModeChanged(this.mNavBarMode);
        }
    }

    public int addModeChangeListener(NavigationModeChangeListener navigationModeChangeListener) {
        this.mChangeListeners.add(navigationModeChangeListener);
        return this.mNavBarMode;
    }

    public void removeModeChangeListener(NavigationModeChangeListener navigationModeChangeListener) {
        this.mChangeListeners.remove(navigationModeChangeListener);
    }
}
