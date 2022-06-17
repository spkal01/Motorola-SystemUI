package com.android.systemui.wmshell;

import android.animation.AnimationHandler;
import android.content.Context;
import android.os.Handler;
import android.view.IWindowManager;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.WindowManagerShellWrapper;
import com.android.p011wm.shell.apppairs.AppPairsController;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.FloatingContentCoordinator;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import com.android.p011wm.shell.onehanded.OneHandedController;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.pip.PipAnimationController;
import com.android.p011wm.shell.pip.PipBoundsAlgorithm;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipSnapAlgorithm;
import com.android.p011wm.shell.pip.PipSurfaceTransactionHelper;
import com.android.p011wm.shell.pip.PipTaskOrganizer;
import com.android.p011wm.shell.pip.PipTransition;
import com.android.p011wm.shell.pip.PipTransitionController;
import com.android.p011wm.shell.pip.PipUiEventLogger;
import com.android.p011wm.shell.pip.phone.PhonePipMenuController;
import com.android.p011wm.shell.pip.phone.PipAppOpsListener;
import com.android.p011wm.shell.pip.phone.PipController;
import com.android.p011wm.shell.pip.phone.PipMotionHelper;
import com.android.p011wm.shell.pip.phone.PipTouchHandler;
import com.android.p011wm.shell.startingsurface.StartingWindowTypeAlgorithm;
import com.android.p011wm.shell.startingsurface.phone.PhoneStartingWindowTypeAlgorithm;
import com.android.p011wm.shell.transition.Transitions;
import java.util.Optional;

public class WMShellModule {
    static DisplayImeController provideDisplayImeController(IWindowManager iWindowManager, DisplayController displayController, ShellExecutor shellExecutor, TransactionPool transactionPool) {
        return new DisplayImeController(iWindowManager, displayController, shellExecutor, transactionPool);
    }

    static LegacySplitScreenController provideLegacySplitScreen(Context context, DisplayController displayController, SystemWindows systemWindows, DisplayImeController displayImeController, TransactionPool transactionPool, ShellTaskOrganizer shellTaskOrganizer, SyncTransactionQueue syncTransactionQueue, TaskStackListenerImpl taskStackListenerImpl, Transitions transitions, ShellExecutor shellExecutor, AnimationHandler animationHandler) {
        return new LegacySplitScreenController(context, displayController, systemWindows, displayImeController, transactionPool, shellTaskOrganizer, syncTransactionQueue, taskStackListenerImpl, transitions, shellExecutor, animationHandler);
    }

    static AppPairsController provideAppPairs(ShellTaskOrganizer shellTaskOrganizer, SyncTransactionQueue syncTransactionQueue, DisplayController displayController, ShellExecutor shellExecutor, DisplayImeController displayImeController) {
        return new AppPairsController(shellTaskOrganizer, syncTransactionQueue, displayController, shellExecutor, displayImeController);
    }

    static Optional<Pip> providePip(Context context, DisplayController displayController, PipAppOpsListener pipAppOpsListener, PipBoundsAlgorithm pipBoundsAlgorithm, PipBoundsState pipBoundsState, PipMediaController pipMediaController, PhonePipMenuController phonePipMenuController, PipTaskOrganizer pipTaskOrganizer, PipTouchHandler pipTouchHandler, PipTransitionController pipTransitionController, WindowManagerShellWrapper windowManagerShellWrapper, TaskStackListenerImpl taskStackListenerImpl, Optional<OneHandedController> optional, ShellExecutor shellExecutor) {
        return Optional.ofNullable(PipController.create(context, displayController, pipAppOpsListener, pipBoundsAlgorithm, pipBoundsState, pipMediaController, phonePipMenuController, pipTaskOrganizer, pipTouchHandler, pipTransitionController, windowManagerShellWrapper, taskStackListenerImpl, optional, shellExecutor));
    }

    static PipBoundsState providePipBoundsState(Context context) {
        return new PipBoundsState(context);
    }

    static PipSnapAlgorithm providePipSnapAlgorithm() {
        return new PipSnapAlgorithm();
    }

    static PipBoundsAlgorithm providesPipBoundsAlgorithm(Context context, PipBoundsState pipBoundsState, PipSnapAlgorithm pipSnapAlgorithm) {
        return new PipBoundsAlgorithm(context, pipBoundsState, pipSnapAlgorithm);
    }

    static PhonePipMenuController providesPipPhoneMenuController(Context context, PipBoundsState pipBoundsState, PipMediaController pipMediaController, SystemWindows systemWindows, ShellExecutor shellExecutor, Handler handler) {
        return new PhonePipMenuController(context, pipBoundsState, pipMediaController, systemWindows, shellExecutor, handler);
    }

    static PipTouchHandler providePipTouchHandler(Context context, PhonePipMenuController phonePipMenuController, PipBoundsAlgorithm pipBoundsAlgorithm, PipBoundsState pipBoundsState, PipTaskOrganizer pipTaskOrganizer, PipMotionHelper pipMotionHelper, FloatingContentCoordinator floatingContentCoordinator, PipUiEventLogger pipUiEventLogger, ShellExecutor shellExecutor) {
        return new PipTouchHandler(context, phonePipMenuController, pipBoundsAlgorithm, pipBoundsState, pipTaskOrganizer, pipMotionHelper, floatingContentCoordinator, pipUiEventLogger, shellExecutor);
    }

    static PipTaskOrganizer providePipTaskOrganizer(Context context, SyncTransactionQueue syncTransactionQueue, PipBoundsState pipBoundsState, PipBoundsAlgorithm pipBoundsAlgorithm, PhonePipMenuController phonePipMenuController, PipAnimationController pipAnimationController, PipSurfaceTransactionHelper pipSurfaceTransactionHelper, PipTransitionController pipTransitionController, Optional<LegacySplitScreenController> optional, DisplayController displayController, PipUiEventLogger pipUiEventLogger, ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        return new PipTaskOrganizer(context, syncTransactionQueue, pipBoundsState, pipBoundsAlgorithm, phonePipMenuController, pipAnimationController, pipSurfaceTransactionHelper, pipTransitionController, optional, displayController, pipUiEventLogger, shellTaskOrganizer, shellExecutor);
    }

    static PipAnimationController providePipAnimationController(PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        return new PipAnimationController(pipSurfaceTransactionHelper);
    }

    static PipTransitionController providePipTransitionController(Context context, Transitions transitions, ShellTaskOrganizer shellTaskOrganizer, PipAnimationController pipAnimationController, PipBoundsAlgorithm pipBoundsAlgorithm, PipBoundsState pipBoundsState, PhonePipMenuController phonePipMenuController) {
        return new PipTransition(context, pipBoundsState, phonePipMenuController, pipBoundsAlgorithm, pipAnimationController, transitions, shellTaskOrganizer);
    }

    static PipMotionHelper providePipMotionHelper(Context context, PipBoundsState pipBoundsState, PipTaskOrganizer pipTaskOrganizer, PhonePipMenuController phonePipMenuController, PipSnapAlgorithm pipSnapAlgorithm, PipTransitionController pipTransitionController, FloatingContentCoordinator floatingContentCoordinator) {
        return new PipMotionHelper(context, pipBoundsState, pipTaskOrganizer, phonePipMenuController, pipSnapAlgorithm, pipTransitionController, floatingContentCoordinator);
    }

    static StartingWindowTypeAlgorithm provideStartingWindowTypeAlgorithm() {
        return new PhoneStartingWindowTypeAlgorithm();
    }
}
