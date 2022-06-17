package com.motorola.systemui.desktop;

import android.app.ActivityThread;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.window.WindowContext;
import com.android.p011wm.shell.transition.Transitions;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.WMComponent;
import com.android.systemui.moto.DesktopFeature;
import com.motorola.systemui.desktop.dagger.DaggerDesktopGlobalRootComponent;
import com.motorola.systemui.desktop.dagger.DesktopGlobalRootComponent;
import com.motorola.systemui.desktop.dagger.DesktopSysUIComponent;
import com.motorola.systemui.desktop.util.DesktopDisplayContext;
import com.motorola.systemui.desktop.util.FakeDesktopDisplayContext;
import java.util.Optional;

public class DesktopSystemUIFactory extends SystemUIFactory {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static DesktopSystemUIFactory mDesktopFactory;
    private DesktopDisplayContext mDesktopDisplayContext;
    private DesktopGlobalRootComponent mRootComponent;
    private DesktopSysUIComponent mSysUIComponent;

    public static <T extends DesktopSystemUIFactory> T getDesktopFactory() {
        return mDesktopFactory;
    }

    public static DesktopSystemUIFactory create(Context context, int i) {
        DesktopSystemUIFactory create = create(context, i, false);
        mDesktopFactory = create;
        return create;
    }

    public static DesktopSystemUIFactory create(Context context, int i, boolean z) {
        DesktopDisplayContext desktopDisplayContext;
        boolean z2 = z && i == Integer.MAX_VALUE;
        String str = ActivityThread.currentPackageName() + ":systemui_readyfor";
        if (!Process.myUserHandle().isSystem() || ((!DesktopFeature.isDesktopSupported() && !z2) || !ActivityThread.currentProcessName().startsWith(str))) {
            if (DEBUG) {
                Log.d("DesktopSystemUIFactory", "return: " + Process.myUserHandle() + "; isDesktopSupported" + DesktopFeature.isDesktopSupported() + "; currentProcessName: " + ActivityThread.currentProcessName() + "; displayId: " + i);
            }
            return null;
        }
        if (!z2) {
            Display display = ((DisplayManager) context.getSystemService("display")).getDisplay(i);
            if (display == null) {
                Log.w("DesktopSystemUIFactory", "return display is null displayId: " + i);
                return null;
            } else if (!DesktopFeature.isDesktopMode(display)) {
                if (DEBUG) {
                    Log.w("DesktopSystemUIFactory", "return display is no DesktopMode: " + i);
                }
                return null;
            } else {
                desktopDisplayContext = new DesktopDisplayContext(context, i);
            }
        } else {
            desktopDisplayContext = new FakeDesktopDisplayContext(context, i);
        }
        DesktopSystemUIFactory desktopSystemUIFactory = new DesktopSystemUIFactory();
        desktopSystemUIFactory.init(desktopDisplayContext);
        return desktopSystemUIFactory;
    }

    private DesktopSystemUIFactory() {
    }

    public void init(DesktopDisplayContext desktopDisplayContext) {
        this.mDesktopDisplayContext = desktopDisplayContext;
        DesktopGlobalRootComponent buildGlobalRootComponent = buildGlobalRootComponent((Context) desktopDisplayContext);
        this.mRootComponent = buildGlobalRootComponent;
        DesktopSysUIComponent.Builder sysUIComponent = buildGlobalRootComponent.getSysUIComponent();
        prepareSysUIComponentBuilder(sysUIComponent, (WMComponent) null).setPip(Optional.ofNullable((Object) null)).setLegacySplitScreen(Optional.ofNullable((Object) null)).setSplitScreen(Optional.ofNullable((Object) null)).setOneHanded(Optional.ofNullable((Object) null)).setBubbles(Optional.ofNullable((Object) null)).setHideDisplayCutout(Optional.ofNullable((Object) null)).setShellCommandHandler(Optional.ofNullable((Object) null)).setAppPairs(Optional.ofNullable((Object) null)).setTaskViewFactory(Optional.ofNullable((Object) null)).setTransitions(Transitions.createEmptyForTesting()).setStartingSurface(Optional.ofNullable((Object) null)).setTaskSurfaceHelper(Optional.ofNullable((Object) null));
        DesktopSysUIComponent build = sysUIComponent.build();
        this.mSysUIComponent = build;
        build.init();
        this.mSysUIComponent.createDependency().start();
    }

    public DesktopSysUIComponent getSysUIComponent() {
        return this.mSysUIComponent;
    }

    /* access modifiers changed from: protected */
    public DesktopGlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerDesktopGlobalRootComponent.builder().context(context).build();
    }

    public WindowContext getWindowContext() {
        return this.mDesktopDisplayContext.getWindowContext();
    }

    public DesktopDisplayContext getDesktopDisplayContext() {
        return this.mDesktopDisplayContext;
    }
}
