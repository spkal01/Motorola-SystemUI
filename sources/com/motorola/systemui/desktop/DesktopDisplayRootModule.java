package com.motorola.systemui.desktop;

import com.android.systemui.BootCompleteCacheImpl;

public class DesktopDisplayRootModule {
    private BootCompleteCacheImpl mBootCompleteCache;
    private DesktopStatusBar mDesktopStatusBar;
    private final DesktopSystemUIFactory mDesktopSystemUIFactory;
    private final int mDisplayId;

    public DesktopDisplayRootModule(DesktopSystemUIFactory desktopSystemUIFactory, int i) {
        this.mDesktopSystemUIFactory = desktopSystemUIFactory;
        this.mDisplayId = i;
        this.mBootCompleteCache = desktopSystemUIFactory.getSysUIComponent().provideBootCacheImpl();
        this.mDesktopStatusBar = (DesktopStatusBar) desktopSystemUIFactory.getSysUIComponent().getStatusBar();
    }

    public DesktopStatusBar getDesktopStatusBar() {
        return this.mDesktopStatusBar;
    }

    public void start() {
        this.mBootCompleteCache.setBootComplete();
        this.mDesktopStatusBar.start();
    }
}
