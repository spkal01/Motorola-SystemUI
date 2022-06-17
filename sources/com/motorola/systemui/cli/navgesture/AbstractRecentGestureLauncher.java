package com.motorola.systemui.cli.navgesture;

import android.content.Intent;
import android.os.Bundle;
import com.motorola.systemui.cli.navgesture.states.StateManager;
import com.motorola.systemui.cli.navgesture.util.ActivityTracker;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class AbstractRecentGestureLauncher extends BaseGestureActivity {
    public static final ActivityTracker<AbstractRecentGestureLauncher> ACTIVITY_TRACKER = new ActivityTracker<>();
    private StateManager mStateManager;

    /* access modifiers changed from: protected */
    public void onCreateBeforeInflaterView(Bundle bundle) {
        super.onCreateBeforeInflaterView(bundle);
        this.mStateManager = new StateManager(this);
    }

    /* access modifiers changed from: protected */
    public void onCreateAfterSetupViews() {
        super.onCreateAfterSetupViews();
        ACTIVITY_TRACKER.handleCreate(this);
        this.mStateManager.reapplyState();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ACTIVITY_TRACKER.handleNewIntent(this, intent);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        getStateManager().moveToRestState();
    }

    public void onDestroy() {
        super.onDestroy();
        ACTIVITY_TRACKER.onActivityDestroyed(this);
    }

    public void onBackPressed() {
        getStateManager().getState().onBackPressed(this);
    }

    public StateManager getStateManager() {
        return this.mStateManager;
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        printWriter.println(str + "Misc:");
        dumpMisc(printWriter);
        this.mStateManager.dump(str, printWriter);
    }
}
