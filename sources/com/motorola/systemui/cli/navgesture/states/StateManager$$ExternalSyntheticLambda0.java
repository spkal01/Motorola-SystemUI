package com.motorola.systemui.cli.navgesture.states;

public final /* synthetic */ class StateManager$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StateManager f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ LauncherState f$2;
    public final /* synthetic */ LauncherState f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ StateManager$$ExternalSyntheticLambda0(StateManager stateManager, int i, LauncherState launcherState, LauncherState launcherState2, Runnable runnable) {
        this.f$0 = stateManager;
        this.f$1 = i;
        this.f$2 = launcherState;
        this.f$3 = launcherState2;
        this.f$4 = runnable;
    }

    public final void run() {
        this.f$0.lambda$goToState$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
