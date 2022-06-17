package com.android.p011wm.shell.pip.phone;

import android.app.RemoteAction;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Size;
import android.view.IWindow;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.View;
import android.view.WindowManagerGlobal;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.common.SystemWindows;
import com.android.p011wm.shell.pip.PipBoundsState;
import com.android.p011wm.shell.pip.PipMediaController;
import com.android.p011wm.shell.pip.PipMenuController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.android.wm.shell.pip.phone.PhonePipMenuController */
public class PhonePipMenuController implements PipMenuController {
    private ParceledListSlice<RemoteAction> mAppActions;
    private SyncRtSurfaceTransactionApplier mApplier;
    private final Context mContext;
    private final ArrayList<Listener> mListeners = new ArrayList<>();
    private final ShellExecutor mMainExecutor;
    private final Handler mMainHandler;
    private PipMediaController.ActionListener mMediaActionListener = new PipMediaController.ActionListener() {
        public void onMediaActionsChanged(List<RemoteAction> list) {
            ParceledListSlice unused = PhonePipMenuController.this.mMediaActions = new ParceledListSlice(list);
            PhonePipMenuController.this.updateMenuActions();
        }
    };
    /* access modifiers changed from: private */
    public ParceledListSlice<RemoteAction> mMediaActions;
    private final PipMediaController mMediaController;
    private int mMenuState;
    private final Matrix mMoveTransform = new Matrix();
    private final PipBoundsState mPipBoundsState;
    private IBinder mPipMenuInputToken;
    private PipMenuView mPipMenuView;
    private final SystemWindows mSystemWindows;
    private final RectF mTmpDestinationRectF = new RectF();
    private final Rect mTmpSourceBounds = new Rect();
    private final RectF mTmpSourceRectF = new RectF();
    private final float[] mTmpValues = new float[9];
    private final Runnable mUpdateEmbeddedMatrix = new PhonePipMenuController$$ExternalSyntheticLambda0(this);
    private boolean mXrvdFeatureEnabled = false;

    /* renamed from: com.android.wm.shell.pip.phone.PhonePipMenuController$Listener */
    public interface Listener {
        void onPipDismiss();

        void onPipExpand();

        void onPipMenuStateChangeFinish(int i);

        void onPipMenuStateChangeStart(int i, boolean z, Runnable runnable);

        void onPipShowMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        PipMenuView pipMenuView = this.mPipMenuView;
        if (pipMenuView != null && pipMenuView.getViewRootImpl() != null) {
            this.mMoveTransform.getValues(this.mTmpValues);
            try {
                this.mPipMenuView.getViewRootImpl().getAccessibilityEmbeddedConnection().setScreenMatrix(this.mTmpValues);
            } catch (RemoteException unused) {
            }
        }
    }

    public PhonePipMenuController(Context context, PipBoundsState pipBoundsState, PipMediaController pipMediaController, SystemWindows systemWindows, ShellExecutor shellExecutor, Handler handler) {
        this.mContext = context;
        this.mPipBoundsState = pipBoundsState;
        this.mMediaController = pipMediaController;
        this.mSystemWindows = systemWindows;
        this.mMainExecutor = shellExecutor;
        this.mMainHandler = handler;
        this.mXrvdFeatureEnabled = context.getResources().getBoolean(17891589);
    }

    public boolean isMenuVisible() {
        return (this.mPipMenuView == null || this.mMenuState == 0) ? false : true;
    }

    public void attach(SurfaceControl surfaceControl) {
        attachPipMenuView();
    }

    public void detach() {
        hideMenu();
        detachPipMenuView();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0053  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void attachPipMenuView() {
        /*
            r6 = this;
            java.lang.String r0 = "PhonePipMenuController"
            boolean r1 = r6.mXrvdFeatureEnabled
            r2 = 0
            if (r1 == 0) goto L_0x004e
            android.view.IWindowManager r1 = android.view.WindowManagerGlobal.getWindowManagerService()     // Catch:{ RemoteException -> 0x0032 }
            int r1 = r1.getXrvdFocusedDisplayId()     // Catch:{ RemoteException -> 0x0032 }
            android.hardware.display.DisplayManagerGlobal r3 = android.hardware.display.DisplayManagerGlobal.getInstance()     // Catch:{ RemoteException -> 0x0030 }
            android.view.DisplayInfo r3 = r3.getDisplayInfo(r1)     // Catch:{ RemoteException -> 0x0030 }
            if (r3 == 0) goto L_0x002e
            int r4 = r3.type     // Catch:{ RemoteException -> 0x0030 }
            r5 = 5
            if (r4 != r5) goto L_0x002e
            int r4 = r3.ownerUid     // Catch:{ RemoteException -> 0x0030 }
            r5 = 1000(0x3e8, float:1.401E-42)
            if (r4 != r5) goto L_0x002e
            java.lang.String r3 = r3.ownerPackageName     // Catch:{ RemoteException -> 0x0030 }
            java.lang.String r4 = "com.qualcomm.qti.xrvd.service"
            boolean r3 = r3.equals(r4)     // Catch:{ RemoteException -> 0x0030 }
            if (r3 != 0) goto L_0x0039
        L_0x002e:
            r1 = r2
            goto L_0x0039
        L_0x0030:
            r3 = move-exception
            goto L_0x0034
        L_0x0032:
            r3 = move-exception
            r1 = r2
        L_0x0034:
            java.lang.String r4 = "Unable to get display"
            android.util.Log.e(r0, r4, r3)
        L_0x0039:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "attachPipMenuView() display:"
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r0, r3)
            goto L_0x004f
        L_0x004e:
            r1 = r2
        L_0x004f:
            com.android.wm.shell.pip.phone.PipMenuView r0 = r6.mPipMenuView
            if (r0 == 0) goto L_0x0056
            r6.detachPipMenuView()
        L_0x0056:
            com.android.wm.shell.pip.phone.PipMenuView r0 = new com.android.wm.shell.pip.phone.PipMenuView
            android.content.Context r3 = r6.mContext
            com.android.wm.shell.common.ShellExecutor r4 = r6.mMainExecutor
            android.os.Handler r5 = r6.mMainHandler
            r0.<init>(r3, r6, r4, r5)
            r6.mPipMenuView = r0
            com.android.wm.shell.common.SystemWindows r3 = r6.mSystemWindows
            java.lang.String r4 = "PipMenuView"
            android.view.WindowManager$LayoutParams r6 = r6.getPipMenuLayoutParams(r4, r2, r2)
            r2 = 1
            r3.addView(r0, r6, r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.pip.phone.PhonePipMenuController.attachPipMenuView():void");
    }

    private void detachPipMenuView() {
        if (this.mXrvdFeatureEnabled) {
            Log.d("PhonePipMenuController", "detachPipMenuView() display:" + this.mContext.getDisplayId());
        }
        PipMenuView pipMenuView = this.mPipMenuView;
        if (pipMenuView != null) {
            this.mApplier = null;
            this.mSystemWindows.removeView(pipMenuView);
            this.mPipMenuView = null;
            this.mPipMenuInputToken = null;
        }
    }

    public void updateMenuBounds(Rect rect) {
        this.mSystemWindows.updateViewLayout(this.mPipMenuView, getPipMenuLayoutParams("PipMenuView", rect.width(), rect.height()));
        updateMenuLayout(rect);
    }

    public SurfaceControl getSurfaceControl() {
        return this.mSystemWindows.getViewSurface(this.mPipMenuView);
    }

    public void addListener(Listener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    /* access modifiers changed from: package-private */
    public Size getEstimatedMinMenuSize() {
        PipMenuView pipMenuView = this.mPipMenuView;
        if (pipMenuView == null) {
            return null;
        }
        return pipMenuView.getEstimatedMinMenuSize();
    }

    public void showMenu() {
        this.mListeners.forEach(PhonePipMenuController$$ExternalSyntheticLambda5.INSTANCE);
    }

    public void showMenuWithPossibleDelay(int i, Rect rect, boolean z, boolean z2, boolean z3) {
        if (z2) {
            fadeOutMenu();
        }
        showMenuInternal(i, rect, z, z2, z2, z3);
    }

    public void showMenu(int i, Rect rect, boolean z, boolean z2, boolean z3) {
        showMenuInternal(i, rect, z, z2, false, z3);
    }

    private void showMenuInternal(int i, Rect rect, boolean z, boolean z2, boolean z3, boolean z4) {
        if (maybeCreateSyncApplier()) {
            movePipMenu((SurfaceControl) null, (SurfaceControl.Transaction) null, rect);
            updateMenuBounds(rect);
            this.mPipMenuView.showMenu(i, rect, z, z2, z3, z4);
        }
    }

    public void movePipMenu(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, Rect rect) {
        if (!rect.isEmpty() && maybeCreateSyncApplier()) {
            if (Build.IS_DEBUGGABLE) {
                Log.d("PipDebug", "movePipMenu to " + rect + " callers=\n" + Debug.getCallers(7, "    "));
            }
            if (surfaceControl == null || transaction == null) {
                this.mTmpSourceBounds.set(0, 0, rect.width(), rect.height());
            } else {
                this.mPipMenuView.getBoundsOnScreen(this.mTmpSourceBounds);
            }
            this.mTmpSourceRectF.set(this.mTmpSourceBounds);
            this.mTmpDestinationRectF.set(rect);
            this.mMoveTransform.setRectToRect(this.mTmpSourceRectF, this.mTmpDestinationRectF, Matrix.ScaleToFit.FILL);
            SyncRtSurfaceTransactionApplier.SurfaceParams build = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(getSurfaceControl()).withMatrix(this.mMoveTransform).build();
            if (surfaceControl == null || transaction == null) {
                this.mApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{build});
            } else {
                SyncRtSurfaceTransactionApplier.SurfaceParams build2 = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(surfaceControl).withMergeTransaction(transaction).build();
                this.mApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{build, build2});
            }
            if (this.mPipMenuView.getViewRootImpl() != null) {
                this.mPipMenuView.getHandler().removeCallbacks(this.mUpdateEmbeddedMatrix);
                this.mPipMenuView.getHandler().post(this.mUpdateEmbeddedMatrix);
            }
        }
    }

    public void resizePipMenu(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, Rect rect) {
        if (!rect.isEmpty() && maybeCreateSyncApplier()) {
            SyncRtSurfaceTransactionApplier.SurfaceParams build = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(getSurfaceControl()).withWindowCrop(rect).build();
            if (surfaceControl == null || transaction == null) {
                this.mApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{build});
                return;
            }
            SyncRtSurfaceTransactionApplier.SurfaceParams build2 = new SyncRtSurfaceTransactionApplier.SurfaceParams.Builder(surfaceControl).withMergeTransaction(transaction).build();
            this.mApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{build, build2});
        }
    }

    private boolean maybeCreateSyncApplier() {
        PipMenuView pipMenuView = this.mPipMenuView;
        if (pipMenuView == null || pipMenuView.getViewRootImpl() == null) {
            Log.v("PhonePipMenuController", "Not going to move PiP, either menu or its parent is not created.");
            return false;
        }
        if (this.mApplier == null) {
            this.mApplier = new SyncRtSurfaceTransactionApplier(this.mPipMenuView);
            this.mPipMenuInputToken = this.mPipMenuView.getViewRootImpl().getInputToken();
        }
        if (this.mApplier != null) {
            return true;
        }
        return false;
    }

    public void pokeMenu() {
        if (isMenuVisible()) {
            this.mPipMenuView.pokeMenu();
        }
    }

    private void fadeOutMenu() {
        if (isMenuVisible()) {
            this.mPipMenuView.fadeOutMenu();
        }
    }

    public void hideMenu() {
        if (isMenuVisible()) {
            this.mPipMenuView.hideMenu();
        }
    }

    public void hideMenu(int i, boolean z) {
        if (isMenuVisible()) {
            this.mPipMenuView.hideMenu(z, i);
        }
    }

    public void hideMenu(Runnable runnable, Runnable runnable2) {
        if (isMenuVisible()) {
            if (runnable != null) {
                runnable.run();
            }
            this.mPipMenuView.hideMenu(runnable2);
        }
    }

    public void setAppActions(ParceledListSlice<RemoteAction> parceledListSlice) {
        this.mAppActions = parceledListSlice;
        updateMenuActions();
    }

    /* access modifiers changed from: package-private */
    public void onPipExpand() {
        this.mListeners.forEach(PhonePipMenuController$$ExternalSyntheticLambda4.INSTANCE);
    }

    /* access modifiers changed from: package-private */
    public void onPipDismiss() {
        this.mListeners.forEach(PhonePipMenuController$$ExternalSyntheticLambda3.INSTANCE);
    }

    private ParceledListSlice<RemoteAction> resolveMenuActions() {
        if (isValidActions(this.mAppActions)) {
            return this.mAppActions;
        }
        return this.mMediaActions;
    }

    /* access modifiers changed from: private */
    public void updateMenuActions() {
        ParceledListSlice<RemoteAction> resolveMenuActions;
        if (this.mPipMenuView != null && (resolveMenuActions = resolveMenuActions()) != null) {
            this.mPipMenuView.setActions(this.mPipBoundsState.getBounds(), resolveMenuActions.getList());
        }
    }

    private static boolean isValidActions(ParceledListSlice<?> parceledListSlice) {
        return parceledListSlice != null && parceledListSlice.getList().size() > 0;
    }

    /* access modifiers changed from: package-private */
    public void onMenuStateChangeStart(int i, boolean z, Runnable runnable) {
        if (i != this.mMenuState) {
            this.mListeners.forEach(new PhonePipMenuController$$ExternalSyntheticLambda2(i, z, runnable));
            if (i == 2) {
                this.mMediaController.addActionListener(this.mMediaActionListener);
            } else {
                this.mMediaController.removeActionListener(this.mMediaActionListener);
            }
            try {
                WindowManagerGlobal.getWindowSession().grantEmbeddedWindowFocus((IWindow) null, this.mPipMenuInputToken, i != 0);
            } catch (RemoteException e) {
                Log.e("PhonePipMenuController", "Unable to update focus as menu appears/disappears", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onMenuStateChangeFinish(int i) {
        if (i != this.mMenuState) {
            this.mListeners.forEach(new PhonePipMenuController$$ExternalSyntheticLambda1(i));
        }
        this.mMenuState = i;
        if (i != 0) {
            this.mSystemWindows.setShellRootAccessibilityWindow(0, 1, this.mPipMenuView);
        } else {
            this.mSystemWindows.setShellRootAccessibilityWindow(0, 1, (View) null);
        }
    }

    /* access modifiers changed from: package-private */
    public void handlePointerEvent(MotionEvent motionEvent) {
        if (this.mPipMenuView != null) {
            if (motionEvent.isTouchEvent()) {
                this.mPipMenuView.dispatchTouchEvent(motionEvent);
            } else {
                this.mPipMenuView.dispatchGenericMotionEvent(motionEvent);
            }
        }
    }

    public void updateMenuLayout(Rect rect) {
        if (isMenuVisible()) {
            this.mPipMenuView.updateMenuLayout(rect);
        }
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter printWriter, String str) {
        String str2 = str + "  ";
        printWriter.println(str + "PhonePipMenuController");
        printWriter.println(str2 + "mMenuState=" + this.mMenuState);
        printWriter.println(str2 + "mPipMenuView=" + this.mPipMenuView);
        printWriter.println(str2 + "mListeners=" + this.mListeners.size());
    }
}
