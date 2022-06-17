package com.android.p011wm.shell.sizecompatui;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;
import com.android.p011wm.shell.ShellTaskOrganizer;
import com.android.p011wm.shell.common.DisplayController;
import com.android.p011wm.shell.common.DisplayImeController;
import com.android.p011wm.shell.common.SyncTransactionQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

/* renamed from: com.android.wm.shell.sizecompatui.SizeCompatUIController */
public class SizeCompatUIController implements DisplayController.OnDisplaysChangedListener, DisplayImeController.ImePositionProcessor {
    private final SparseArray<SizeCompatUILayout> mActiveLayouts = new SparseArray<>(0);
    private SizeCompatUICallback mCallback;
    private final Context mContext;
    private final SparseArray<WeakReference<Context>> mDisplayContextCache = new SparseArray<>(0);
    private final DisplayController mDisplayController;
    private final Set<Integer> mDisplaysWithIme = new ArraySet(1);
    private boolean mHasShownHint;
    private final DisplayImeController mImeController;
    private final SyncTransactionQueue mSyncQueue;

    /* renamed from: com.android.wm.shell.sizecompatui.SizeCompatUIController$SizeCompatUICallback */
    public interface SizeCompatUICallback {
        void onSizeCompatRestartButtonClicked(int i);
    }

    public SizeCompatUIController(Context context, DisplayController displayController, DisplayImeController displayImeController, SyncTransactionQueue syncTransactionQueue) {
        this.mContext = context;
        this.mDisplayController = displayController;
        this.mImeController = displayImeController;
        this.mSyncQueue = syncTransactionQueue;
        displayController.addDisplayWindowListener(this);
        displayImeController.addPositionProcessor(this);
    }

    public void setSizeCompatUICallback(SizeCompatUICallback sizeCompatUICallback) {
        this.mCallback = sizeCompatUICallback;
    }

    public void onSizeCompatInfoChanged(int i, int i2, Configuration configuration, ShellTaskOrganizer.TaskListener taskListener) {
        if (configuration == null || taskListener == null) {
            removeLayout(i2);
        } else if (this.mActiveLayouts.contains(i2)) {
            updateLayout(i2, configuration, taskListener);
        } else {
            createLayout(i, i2, configuration, taskListener);
        }
    }

    public void onDisplayRemoved(int i) {
        this.mDisplayContextCache.remove(i);
        ArrayList arrayList = new ArrayList();
        forAllLayoutsOnDisplay(i, new SizeCompatUIController$$ExternalSyntheticLambda1(arrayList));
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            removeLayout(((Integer) arrayList.get(size)).intValue());
        }
    }

    public void onDisplayConfigurationChanged(int i, Configuration configuration) {
        forAllLayoutsOnDisplay(i, new SizeCompatUIController$$ExternalSyntheticLambda0(this.mDisplayController.getDisplayLayout(i)));
    }

    public void onImeVisibilityChanged(int i, boolean z) {
        if (z) {
            this.mDisplaysWithIme.add(Integer.valueOf(i));
        } else {
            this.mDisplaysWithIme.remove(Integer.valueOf(i));
        }
        forAllLayoutsOnDisplay(i, new SizeCompatUIController$$ExternalSyntheticLambda2(z));
    }

    private boolean isImeShowingOnDisplay(int i) {
        return this.mDisplaysWithIme.contains(Integer.valueOf(i));
    }

    private void createLayout(int i, int i2, Configuration configuration, ShellTaskOrganizer.TaskListener taskListener) {
        Context orCreateDisplayContext = getOrCreateDisplayContext(i);
        if (orCreateDisplayContext == null) {
            Log.e("SizeCompatUIController", "Cannot get context for display " + i);
            return;
        }
        SizeCompatUILayout createLayout = createLayout(orCreateDisplayContext, i, i2, configuration, taskListener);
        this.mActiveLayouts.put(i2, createLayout);
        createLayout.createSizeCompatButton(isImeShowingOnDisplay(i));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public SizeCompatUILayout createLayout(Context context, int i, int i2, Configuration configuration, ShellTaskOrganizer.TaskListener taskListener) {
        SizeCompatUILayout sizeCompatUILayout = new SizeCompatUILayout(this.mSyncQueue, this.mCallback, context, configuration, i2, taskListener, this.mDisplayController.getDisplayLayout(i), this.mHasShownHint);
        this.mHasShownHint = true;
        return sizeCompatUILayout;
    }

    private void updateLayout(int i, Configuration configuration, ShellTaskOrganizer.TaskListener taskListener) {
        SizeCompatUILayout sizeCompatUILayout = this.mActiveLayouts.get(i);
        if (sizeCompatUILayout != null) {
            sizeCompatUILayout.updateSizeCompatInfo(configuration, taskListener, isImeShowingOnDisplay(sizeCompatUILayout.getDisplayId()));
        }
    }

    private void removeLayout(int i) {
        SizeCompatUILayout sizeCompatUILayout = this.mActiveLayouts.get(i);
        if (sizeCompatUILayout != null) {
            sizeCompatUILayout.release();
            this.mActiveLayouts.remove(i);
        }
    }

    private Context getOrCreateDisplayContext(int i) {
        Display display;
        if (i == 0) {
            return this.mContext;
        }
        Context context = null;
        WeakReference weakReference = this.mDisplayContextCache.get(i);
        if (weakReference != null) {
            context = (Context) weakReference.get();
        }
        if (context != null || (display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(i)) == null) {
            return context;
        }
        Context createDisplayContext = this.mContext.createDisplayContext(display);
        this.mDisplayContextCache.put(i, new WeakReference(createDisplayContext));
        return createDisplayContext;
    }

    private void forAllLayoutsOnDisplay(int i, Consumer<SizeCompatUILayout> consumer) {
        for (int i2 = 0; i2 < this.mActiveLayouts.size(); i2++) {
            SizeCompatUILayout sizeCompatUILayout = this.mActiveLayouts.get(this.mActiveLayouts.keyAt(i2));
            if (sizeCompatUILayout != null && sizeCompatUILayout.getDisplayId() == i) {
                consumer.accept(sizeCompatUILayout);
            }
        }
    }
}
