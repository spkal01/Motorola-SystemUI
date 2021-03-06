package com.android.p011wm.shell;

import com.android.p011wm.shell.apppairs.AppPairsController;
import com.android.p011wm.shell.bubbles.BubbleController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.draganddrop.DragAndDropController;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreenController;
import com.android.p011wm.shell.pip.phone.PipTouchHandler;
import com.android.p011wm.shell.splitscreen.SplitScreenController;
import com.android.p011wm.shell.startingsurface.StartingWindowController;
import com.android.p011wm.shell.transition.Transitions;
import java.util.Optional;

/* renamed from: com.android.wm.shell.ShellInitImpl */
public class ShellInitImpl {
    private final Optional<AppPairsController> mAppPairsOptional;
    private final Optional<BubbleController> mBubblesOptional;
    private final DisplayImeController mDisplayImeController;
    private final DragAndDropController mDragAndDropController;
    private final FullscreenTaskListener mFullscreenTaskListener;
    private final InitImpl mImpl = new InitImpl();
    private final Optional<LegacySplitScreenController> mLegacySplitScreenOptional;
    /* access modifiers changed from: private */
    public final ShellExecutor mMainExecutor;
    private final Optional<PipTouchHandler> mPipTouchHandlerOptional;
    private final ShellTaskOrganizer mShellTaskOrganizer;
    private final Optional<SplitScreenController> mSplitScreenOptional;
    private final StartingWindowController mStartingWindow;
    private final Transitions mTransitions;

    public ShellInitImpl(DisplayImeController displayImeController, DragAndDropController dragAndDropController, ShellTaskOrganizer shellTaskOrganizer, Optional<BubbleController> optional, Optional<LegacySplitScreenController> optional2, Optional<SplitScreenController> optional3, Optional<AppPairsController> optional4, Optional<PipTouchHandler> optional5, FullscreenTaskListener fullscreenTaskListener, Transitions transitions, StartingWindowController startingWindowController, ShellExecutor shellExecutor) {
        this.mDisplayImeController = displayImeController;
        this.mDragAndDropController = dragAndDropController;
        this.mShellTaskOrganizer = shellTaskOrganizer;
        this.mBubblesOptional = optional;
        this.mLegacySplitScreenOptional = optional2;
        this.mSplitScreenOptional = optional3;
        this.mAppPairsOptional = optional4;
        this.mFullscreenTaskListener = fullscreenTaskListener;
        this.mPipTouchHandlerOptional = optional5;
        this.mTransitions = transitions;
        this.mMainExecutor = shellExecutor;
        this.mStartingWindow = startingWindowController;
    }

    public ShellInit asShellInit() {
        return this.mImpl;
    }

    /* access modifiers changed from: private */
    public void init() {
        this.mDisplayImeController.startMonitorDisplays();
        this.mShellTaskOrganizer.addListenerForType(this.mFullscreenTaskListener, -2);
        this.mShellTaskOrganizer.initStartingWindow(this.mStartingWindow);
        this.mShellTaskOrganizer.registerOrganizer();
        this.mAppPairsOptional.ifPresent(ShellInitImpl$$ExternalSyntheticLambda0.INSTANCE);
        this.mSplitScreenOptional.ifPresent(ShellInitImpl$$ExternalSyntheticLambda3.INSTANCE);
        this.mBubblesOptional.ifPresent(ShellInitImpl$$ExternalSyntheticLambda1.INSTANCE);
        this.mDragAndDropController.initialize(this.mSplitScreenOptional);
        if (Transitions.ENABLE_SHELL_TRANSITIONS) {
            this.mTransitions.register(this.mShellTaskOrganizer);
        }
        this.mPipTouchHandlerOptional.ifPresent(ShellInitImpl$$ExternalSyntheticLambda2.INSTANCE);
    }

    /* renamed from: com.android.wm.shell.ShellInitImpl$InitImpl */
    private class InitImpl implements ShellInit {
        private InitImpl() {
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$init$0() {
            ShellInitImpl.this.init();
        }

        public void init() {
            try {
                ShellInitImpl.this.mMainExecutor.executeBlocking(new ShellInitImpl$InitImpl$$ExternalSyntheticLambda0(this));
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to initialize the Shell in 2s", e);
            }
        }
    }
}
