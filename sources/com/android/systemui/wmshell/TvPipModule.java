package com.android.systemui.wmshell;

import android.content.Context;
import android.os.Handler;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.WindowManagerShellWrapper;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.pip.PipAnimationController;
import com.android.p011wm.shell.pip.PipBoundsAlgorithm;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipSnapAlgorithm;
import com.android.p011wm.shell.pip.PipSurfaceTransactionHelper;
import com.android.p011wm.shell.pip.PipTaskOrganizer;
import com.android.p011wm.shell.pip.PipTransitionController;
import com.android.p011wm.shell.pip.PipUiEventLogger;
import com.android.p011wm.shell.pip.p012tv.TvPipController;
import com.android.p011wm.shell.pip.p012tv.TvPipMenuController;
import com.android.p011wm.shell.pip.p012tv.TvPipNotificationController;
import com.android.p011wm.shell.pip.p012tv.TvPipTransition;
import com.android.p011wm.shell.transition.Transitions;
import java.util.Optional;

public abstract class TvPipModule {
    static Optional<Pip> providePip(Context context, PipBoundsState pipBoundsState, PipBoundsAlgorithm pipBoundsAlgorithm, PipTaskOrganizer pipTaskOrganizer, TvPipMenuController tvPipMenuController, PipMediaController pipMediaController, PipTransitionController pipTransitionController, TvPipNotificationController tvPipNotificationController, TaskStackListenerImpl taskStackListenerImpl, WindowManagerShellWrapper windowManagerShellWrapper, ShellExecutor shellExecutor) {
        return Optional.of(TvPipController.create(context, pipBoundsState, pipBoundsAlgorithm, pipTaskOrganizer, pipTransitionController, tvPipMenuController, pipMediaController, tvPipNotificationController, taskStackListenerImpl, windowManagerShellWrapper, shellExecutor));
    }

    static PipSnapAlgorithm providePipSnapAlgorithm() {
        return new PipSnapAlgorithm();
    }

    static PipBoundsAlgorithm providePipBoundsAlgorithm(Context context, PipBoundsState pipBoundsState, PipSnapAlgorithm pipSnapAlgorithm) {
        return new PipBoundsAlgorithm(context, pipBoundsState, pipSnapAlgorithm);
    }

    static PipBoundsState providePipBoundsState(Context context) {
        return new PipBoundsState(context);
    }

    static PipTransitionController provideTvPipTransition(Transitions transitions, ShellTaskOrganizer shellTaskOrganizer, PipAnimationController pipAnimationController, PipBoundsAlgorithm pipBoundsAlgorithm, PipBoundsState pipBoundsState, TvPipMenuController tvPipMenuController) {
        return new TvPipTransition(pipBoundsState, tvPipMenuController, pipBoundsAlgorithm, pipAnimationController, transitions, shellTaskOrganizer);
    }

    static TvPipMenuController providesTvPipMenuController(Context context, PipBoundsState pipBoundsState, SystemWindows systemWindows, PipMediaController pipMediaController, Handler handler) {
        return new TvPipMenuController(context, pipBoundsState, systemWindows, pipMediaController, handler);
    }

    static TvPipNotificationController provideTvPipNotificationController(Context context, PipMediaController pipMediaController, Handler handler) {
        return new TvPipNotificationController(context, pipMediaController, handler);
    }

    static PipAnimationController providePipAnimationController(PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        return new PipAnimationController(pipSurfaceTransactionHelper);
    }

    static PipTaskOrganizer providePipTaskOrganizer(Context context, TvPipMenuController tvPipMenuController, SyncTransactionQueue syncTransactionQueue, PipBoundsState pipBoundsState, PipBoundsAlgorithm pipBoundsAlgorithm, PipAnimationController pipAnimationController, PipTransitionController pipTransitionController, PipSurfaceTransactionHelper pipSurfaceTransactionHelper, Optional<LegacySplitScreenController> optional, DisplayController displayController, PipUiEventLogger pipUiEventLogger, ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        return new PipTaskOrganizer(context, syncTransactionQueue, pipBoundsState, pipBoundsAlgorithm, tvPipMenuController, pipAnimationController, pipSurfaceTransactionHelper, pipTransitionController, optional, displayController, pipUiEventLogger, shellTaskOrganizer, shellExecutor);
    }
}
