package com.android.systemui.wmshell;

import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.IWindowManager;
import android.view.WindowManager;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.p011wm.shell.FullscreenTaskListener;
import com.android.p011wm.shell.RootTaskDisplayAreaOrganizer;
import com.android.p011wm.shell.ShellCommandHandler;
import com.android.p011wm.shell.ShellCommandHandlerImpl;
import com.android.p011wm.shell.ShellInit;
import com.android.p011wm.shell.ShellInitImpl;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.TaskViewFactory;
import com.android.p011wm.shell.TaskViewFactoryController;
import com.android.p011wm.shell.WindowManagerShellWrapper;
import com.android.p011wm.shell.apppairs.AppPairs;
import com.android.p011wm.shell.apppairs.AppPairsController;
import com.android.p011wm.shell.bubbles.BubbleController;
import com.android.p011wm.shell.bubbles.BubbleStackView;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.DisplayLayout;
import com.android.p011wm.shell.common.FloatingContentCoordinator;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.draganddrop.DragAndDropController;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutoutController;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import com.android.p011wm.shell.onehanded.OneHanded;
import com.android.p011wm.shell.onehanded.OneHandedController;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipSurfaceTransactionHelper;
import com.android.p011wm.shell.pip.PipUiEventLogger;
import com.android.p011wm.shell.pip.phone.PipAppOpsListener;
import com.android.p011wm.shell.pip.phone.PipTouchHandler;
import com.android.p011wm.shell.sizecompatui.SizeCompatUIController;
import com.android.p011wm.shell.splitscreen.SplitScreen;
import com.android.p011wm.shell.splitscreen.SplitScreenController;
import com.android.p011wm.shell.startingsurface.StartingSurface;
import com.android.p011wm.shell.startingsurface.StartingWindowController;
import com.android.p011wm.shell.startingsurface.StartingWindowTypeAlgorithm;
import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelper;
import com.android.p011wm.shell.tasksurfacehelper.TaskSurfaceHelperController;
import com.android.p011wm.shell.transition.ShellTransitions;
import com.android.p011wm.shell.transition.Transitions;
import java.util.Optional;

public abstract class WMShellBaseModule {
    static DisplayController provideDisplayController(Context context, IWindowManager iWindowManager, ShellExecutor shellExecutor) {
        return new DisplayController(context, iWindowManager, shellExecutor);
    }

    static DisplayLayout provideDisplayLayout() {
        return new DisplayLayout();
    }

    static DragAndDropController provideDragAndDropController(Context context, DisplayController displayController) {
        return new DragAndDropController(context, displayController);
    }

    static ShellTaskOrganizer provideShellTaskOrganizer(ShellExecutor shellExecutor, Context context, SizeCompatUIController sizeCompatUIController) {
        return new ShellTaskOrganizer(shellExecutor, context, sizeCompatUIController);
    }

    static SizeCompatUIController provideSizeCompatUIController(Context context, DisplayController displayController, DisplayImeController displayImeController, SyncTransactionQueue syncTransactionQueue) {
        return new SizeCompatUIController(context, displayController, displayImeController, syncTransactionQueue);
    }

    static SyncTransactionQueue provideSyncTransactionQueue(TransactionPool transactionPool, ShellExecutor shellExecutor) {
        return new SyncTransactionQueue(transactionPool, shellExecutor);
    }

    static SystemWindows provideSystemWindows(DisplayController displayController, IWindowManager iWindowManager) {
        return new SystemWindows(displayController, iWindowManager);
    }

    static TaskStackListenerImpl providerTaskStackListenerImpl(Handler handler) {
        return new TaskStackListenerImpl(handler);
    }

    static TransactionPool provideTransactionPool() {
        return new TransactionPool();
    }

    static WindowManagerShellWrapper provideWindowManagerShellWrapper(ShellExecutor shellExecutor) {
        return new WindowManagerShellWrapper(shellExecutor);
    }

    static Optional<Bubbles> provideBubbles(Optional<BubbleController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda1.INSTANCE);
    }

    static Optional<BubbleController> provideBubbleController(Context context, FloatingContentCoordinator floatingContentCoordinator, IStatusBarService iStatusBarService, WindowManager windowManager, WindowManagerShellWrapper windowManagerShellWrapper, LauncherApps launcherApps, TaskStackListenerImpl taskStackListenerImpl, UiEventLogger uiEventLogger, ShellTaskOrganizer shellTaskOrganizer, DisplayController displayController, ShellExecutor shellExecutor, Handler handler) {
        return Optional.of(BubbleController.create(context, (BubbleStackView.SurfaceSynchronizer) null, floatingContentCoordinator, iStatusBarService, windowManager, windowManagerShellWrapper, launcherApps, taskStackListenerImpl, uiEventLogger, shellTaskOrganizer, displayController, shellExecutor, handler));
    }

    static FullscreenTaskListener provideFullscreenTaskListener(SyncTransactionQueue syncTransactionQueue) {
        return new FullscreenTaskListener(syncTransactionQueue);
    }

    static Optional<HideDisplayCutout> provideHideDisplayCutout(Optional<HideDisplayCutoutController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda2.INSTANCE);
    }

    static Optional<HideDisplayCutoutController> provideHideDisplayCutoutController(Context context, DisplayController displayController, ShellExecutor shellExecutor) {
        return Optional.ofNullable(HideDisplayCutoutController.create(context, displayController, shellExecutor));
    }

    static Optional<OneHanded> provideOneHanded(Optional<OneHandedController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda4.INSTANCE);
    }

    static Optional<OneHandedController> provideOneHandedController(Context context, WindowManager windowManager, DisplayController displayController, DisplayLayout displayLayout, TaskStackListenerImpl taskStackListenerImpl, UiEventLogger uiEventLogger, ShellExecutor shellExecutor, Handler handler) {
        return Optional.ofNullable(OneHandedController.create(context, windowManager, displayController, displayLayout, taskStackListenerImpl, uiEventLogger, shellExecutor, handler));
    }

    static Optional<TaskSurfaceHelper> provideTaskSurfaceHelper(Optional<TaskSurfaceHelperController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda6.INSTANCE);
    }

    static Optional<TaskSurfaceHelperController> provideTaskSurfaceHelperController(ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        return Optional.ofNullable(new TaskSurfaceHelperController(shellTaskOrganizer, shellExecutor));
    }

    static FloatingContentCoordinator provideFloatingContentCoordinator() {
        return new FloatingContentCoordinator();
    }

    static PipAppOpsListener providePipAppOpsListener(Context context, PipTouchHandler pipTouchHandler, ShellExecutor shellExecutor) {
        return new PipAppOpsListener(context, pipTouchHandler.getMotionHelper(), shellExecutor);
    }

    static PipMediaController providePipMediaController(Context context, Handler handler) {
        return new PipMediaController(context, handler);
    }

    static PipSurfaceTransactionHelper providePipSurfaceTransactionHelper() {
        return new PipSurfaceTransactionHelper();
    }

    static PipUiEventLogger providePipUiEventLogger(UiEventLogger uiEventLogger, PackageManager packageManager) {
        return new PipUiEventLogger(uiEventLogger, packageManager);
    }

    static ShellTransitions provideRemoteTransitions(Transitions transitions) {
        return transitions.asRemoteTransitions();
    }

    static Transitions provideTransitions(ShellTaskOrganizer shellTaskOrganizer, TransactionPool transactionPool, Context context, ShellExecutor shellExecutor, ShellExecutor shellExecutor2) {
        return new Transitions(shellTaskOrganizer, transactionPool, context, shellExecutor, shellExecutor2);
    }

    static RootTaskDisplayAreaOrganizer provideRootTaskDisplayAreaOrganizer(ShellExecutor shellExecutor, Context context) {
        return new RootTaskDisplayAreaOrganizer(shellExecutor, context);
    }

    static Optional<SplitScreen> provideSplitScreen(Optional<SplitScreenController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda5.INSTANCE);
    }

    static Optional<SplitScreenController> provideSplitScreenController(ShellTaskOrganizer shellTaskOrganizer, SyncTransactionQueue syncTransactionQueue, Context context, RootTaskDisplayAreaOrganizer rootTaskDisplayAreaOrganizer, ShellExecutor shellExecutor, DisplayImeController displayImeController, Transitions transitions, TransactionPool transactionPool) {
        if (ActivityTaskManager.supportsSplitScreenMultiWindow(context)) {
            return Optional.of(new SplitScreenController(shellTaskOrganizer, syncTransactionQueue, context, rootTaskDisplayAreaOrganizer, shellExecutor, displayImeController, transitions, transactionPool));
        }
        return Optional.empty();
    }

    static Optional<LegacySplitScreen> provideLegacySplitScreen(Optional<LegacySplitScreenController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda3.INSTANCE);
    }

    static Optional<AppPairs> provideAppPairs(Optional<AppPairsController> optional) {
        return optional.map(WMShellBaseModule$$ExternalSyntheticLambda0.INSTANCE);
    }

    static Optional<StartingSurface> provideStartingSurface(StartingWindowController startingWindowController) {
        return Optional.of(startingWindowController.asStartingSurface());
    }

    static StartingWindowController provideStartingWindowController(Context context, ShellExecutor shellExecutor, StartingWindowTypeAlgorithm startingWindowTypeAlgorithm, TransactionPool transactionPool) {
        return new StartingWindowController(context, shellExecutor, startingWindowTypeAlgorithm, transactionPool);
    }

    static Optional<TaskViewFactory> provideTaskViewFactory(TaskViewFactoryController taskViewFactoryController) {
        return Optional.of(taskViewFactoryController.asTaskViewFactory());
    }

    static TaskViewFactoryController provideTaskViewFactoryController(ShellTaskOrganizer shellTaskOrganizer, ShellExecutor shellExecutor) {
        return new TaskViewFactoryController(shellTaskOrganizer, shellExecutor);
    }

    static ShellInit provideShellInit(ShellInitImpl shellInitImpl) {
        return shellInitImpl.asShellInit();
    }

    static ShellInitImpl provideShellInitImpl(DisplayImeController displayImeController, DragAndDropController dragAndDropController, ShellTaskOrganizer shellTaskOrganizer, Optional<BubbleController> optional, Optional<LegacySplitScreenController> optional2, Optional<SplitScreenController> optional3, Optional<AppPairsController> optional4, Optional<PipTouchHandler> optional5, FullscreenTaskListener fullscreenTaskListener, Transitions transitions, StartingWindowController startingWindowController, ShellExecutor shellExecutor) {
        return new ShellInitImpl(displayImeController, dragAndDropController, shellTaskOrganizer, optional, optional2, optional3, optional4, optional5, fullscreenTaskListener, transitions, startingWindowController, shellExecutor);
    }

    static Optional<ShellCommandHandler> provideShellCommandHandler(ShellCommandHandlerImpl shellCommandHandlerImpl) {
        return Optional.of(shellCommandHandlerImpl.asShellCommandHandler());
    }

    static ShellCommandHandlerImpl provideShellCommandHandlerImpl(ShellTaskOrganizer shellTaskOrganizer, Optional<LegacySplitScreenController> optional, Optional<SplitScreenController> optional2, Optional<Pip> optional3, Optional<OneHandedController> optional4, Optional<HideDisplayCutoutController> optional5, Optional<AppPairsController> optional6, ShellExecutor shellExecutor) {
        return new ShellCommandHandlerImpl(shellTaskOrganizer, optional, optional2, optional3, optional4, optional5, optional6, shellExecutor);
    }
}
