package com.android.systemui.wmshell;

import android.animation.AnimationHandler;
import android.content.Context;
import android.view.IWindowManager;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.common.TaskStackListenerImpl;
import com.android.p011wm.shell.common.TransactionPool;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import com.android.p011wm.shell.startingsurface.StartingWindowTypeAlgorithm;
import com.android.p011wm.shell.startingsurface.p013tv.TvStartingWindowTypeAlgorithm;
import com.android.p011wm.shell.transition.Transitions;

public class TvWMShellModule {
    static DisplayImeController provideDisplayImeController(IWindowManager iWindowManager, DisplayController displayController, ShellExecutor shellExecutor, TransactionPool transactionPool) {
        return new DisplayImeController(iWindowManager, displayController, shellExecutor, transactionPool);
    }

    static LegacySplitScreenController provideSplitScreen(Context context, DisplayController displayController, SystemWindows systemWindows, DisplayImeController displayImeController, TransactionPool transactionPool, ShellTaskOrganizer shellTaskOrganizer, SyncTransactionQueue syncTransactionQueue, TaskStackListenerImpl taskStackListenerImpl, Transitions transitions, ShellExecutor shellExecutor, AnimationHandler animationHandler) {
        return new LegacySplitScreenController(context, displayController, systemWindows, displayImeController, transactionPool, shellTaskOrganizer, syncTransactionQueue, taskStackListenerImpl, transitions, shellExecutor, animationHandler);
    }

    static StartingWindowTypeAlgorithm provideStartingWindowTypeAlgorithm() {
        return new TvStartingWindowTypeAlgorithm();
    }
}
