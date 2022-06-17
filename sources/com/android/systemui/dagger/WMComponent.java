package com.android.systemui.dagger;

import com.android.p011wm.shell.ShellCommandHandler;
import com.android.p011wm.shell.ShellInit;
import com.android.p011wm.shell.TaskViewFactory;
import com.android.p011wm.shell.apppairs.AppPairs;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.onehanded.OneHanded;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.splitscreen.SplitScreen;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelper;
import com.android.p011wm.shell.transition.ShellTransitions;
import java.util.Optional;

public interface WMComponent {

    public interface Builder {
        WMComponent build();
    }

    Optional<AppPairs> getAppPairs();

    Optional<Bubbles> getBubbles();

    Optional<HideDisplayCutout> getHideDisplayCutout();

    Optional<LegacySplitScreen> getLegacySplitScreen();

    Optional<OneHanded> getOneHanded();

    Optional<Pip> getPip();

    Optional<ShellCommandHandler> getShellCommandHandler();

    ShellInit getShellInit();

    Optional<SplitScreen> getSplitScreen();

    Optional<StartingSurface> getStartingSurface();

    Optional<TaskSurfaceHelper> getTaskSurfaceHelper();

    Optional<TaskViewFactory> getTaskViewFactory();

    ShellTransitions getTransitions();

    void init() {
        getShellInit().init();
    }
}
