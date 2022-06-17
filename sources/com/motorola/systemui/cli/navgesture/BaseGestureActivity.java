package com.motorola.systemui.cli.navgesture;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import com.motorola.systemui.cli.navgesture.recents.RecentsModel;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseGestureActivity extends CommonBasicActivity {
    private int mActivityFlags;
    private int mForceInvisible;
    private final ArrayList<MultiWindowModeChangedListener> mMultiWindowModeChangedListeners = new ArrayList<>();
    private List<Consumer<BaseGestureActivity>> mOnResumeCallbacks = new ArrayList();
    private Runnable mOnStartCallback;

    public interface MultiWindowModeChangedListener {
        void onMultiWindowModeChanged(boolean z);
    }

    public abstract IRecentsView getOverviewPanel();

    public abstract ViewGroup getRootView();

    public void runOnceOnStart(Runnable runnable) {
        this.mOnStartCallback = runnable;
    }

    public void clearRunOnceOnStartCallback() {
        this.mOnStartCallback = null;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        this.mActivityFlags |= 1;
        super.onStart();
        Runnable runnable = this.mOnStartCallback;
        if (runnable != null) {
            runnable.run();
            this.mOnStartCallback = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.mActivityFlags |= 6;
        super.onResume();
        if (!this.mOnResumeCallbacks.isEmpty()) {
            ArrayList arrayList = new ArrayList(this.mOnResumeCallbacks);
            this.mOnResumeCallbacks.clear();
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((Consumer) arrayList.get(size)).accept(this);
            }
            arrayList.clear();
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        this.mActivityFlags &= -5;
        super.onUserLeaveHint();
    }

    public void onMultiWindowModeChanged(boolean z, Configuration configuration) {
        super.onMultiWindowModeChanged(z, configuration);
        for (int size = this.mMultiWindowModeChangedListeners.size() - 1; size >= 0; size--) {
            this.mMultiWindowModeChangedListeners.get(size).onMultiWindowModeChanged(z);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mActivityFlags &= -6;
        this.mForceInvisible = 0;
        super.onStop();
        getSystemUiController().updateUiState(3, 0);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        this.mActivityFlags &= -3;
        super.onPause();
        getSystemUiController().updateUiState(3, 0);
    }

    public boolean isStarted() {
        return (this.mActivityFlags & 1) != 0;
    }

    public boolean hasBeenResumed() {
        return (this.mActivityFlags & 2) != 0;
    }

    public void addForceInvisibleFlag(int i) {
        this.mForceInvisible = i | this.mForceInvisible;
    }

    public void clearForceInvisibleFlag(int i) {
        this.mForceInvisible = (~i) & this.mForceInvisible;
    }

    public boolean isForceInvisible() {
        return hasSomeInvisibleFlag(7);
    }

    public boolean hasSomeInvisibleFlag(int i) {
        return (this.mForceInvisible & i) != 0;
    }

    /* access modifiers changed from: protected */
    public void dumpMisc(PrintWriter printWriter) {
        printWriter.println(" orientation=" + getResources().getConfiguration().orientation);
        printWriter.println(" mSystemUiController: " + getSystemUiController());
        printWriter.println(" mActivityFlags: " + this.mActivityFlags);
        printWriter.println(" mForceInvisible: " + this.mForceInvisible);
    }

    public static <T extends BaseGestureActivity> T fromContext(Context context) {
        if (context instanceof BaseGestureActivity) {
            return (BaseGestureActivity) context;
        }
        if (context instanceof ContextThemeWrapper) {
            return fromContext(((ContextWrapper) context).getBaseContext());
        }
        throw new IllegalArgumentException("Cannot find BaseGestureActivity in parent tree");
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        RecentsModel recentsModel = RecentsModel.INSTANCE.lambda$get$0(this);
        if (recentsModel != null) {
            recentsModel.onTrimMemory(i);
        }
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        RecentsModel recentsModel = RecentsModel.INSTANCE.lambda$get$0(this);
        if (recentsModel != null) {
            recentsModel.getThumbnailCache().getHighResLoadingState().setVisible(true);
        }
    }
}
