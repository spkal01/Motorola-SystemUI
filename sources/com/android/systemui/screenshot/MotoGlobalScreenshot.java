package com.android.systemui.screenshot;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IProcessObserver;
import android.app.PendingIntent;
import android.app.TaskStackListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.DisplayAddress;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.util.ScreenshotHelper;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.moto.MomentsHelper;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.screenshot.ScreenshotController;
import com.android.systemui.shared.recents.utilities.BitmapUtil;
import com.motorola.screenshot.edit.IMotoScreenShotEditService;
import com.motorola.screenshot.edit.ScreenShotEditFileProvider;
import com.motorola.systemui.screenshot.BitmapOffsetCalculate;
import com.motorola.systemui.screenshot.LensRouterActivity;
import com.motorola.systemui.screenshot.LongScreenShotHelper;
import com.motorola.systemui.screenshot.LongScreenshotScrollView;
import com.motorola.systemui.screenshot.MotoScreenshotRootView;
import com.motorola.systemui.screenshot.ScreenshotHelperEx;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MotoGlobalScreenshot {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private Context mContext;
    private final DisplayManager.DisplayListener mDisplayListener;
    /* access modifiers changed from: private */
    public final ImageExporter mImageExporter;
    private ScreenshotNotificationsController mScreenshotNotificationsController;
    /* access modifiers changed from: private */
    public SparseArray<DisplayScreenshotSession> mScreenshotSessions = new SparseArray<>();
    /* access modifiers changed from: private */
    public final ScreenshotSmartActions mScreenshotSmartActions;

    public MotoGlobalScreenshot(Context context, ScreenshotNotificationsController screenshotNotificationsController, ImageExporter imageExporter, ScreenshotSmartActions screenshotSmartActions) {
        C13101 r0 = new DisplayManager.DisplayListener() {
            public void onDisplayAdded(int i) {
            }

            public void onDisplayChanged(int i) {
            }

            public void onDisplayRemoved(int i) {
                synchronized (MotoGlobalScreenshot.this.mScreenshotSessions) {
                    DisplayScreenshotSession displayScreenshotSession = (DisplayScreenshotSession) MotoGlobalScreenshot.this.mScreenshotSessions.get(i);
                    if (displayScreenshotSession != null) {
                        displayScreenshotSession.destory();
                        MotoGlobalScreenshot.this.mScreenshotSessions.remove(i);
                    }
                }
            }
        };
        this.mDisplayListener = r0;
        this.mContext = context;
        this.mScreenshotNotificationsController = screenshotNotificationsController;
        this.mImageExporter = imageExporter;
        this.mScreenshotSmartActions = screenshotSmartActions;
        ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(r0, (Handler) null);
    }

    public void takeScreenshot(int i, int i2) {
        DisplayScreenshotSession displayScreenshotSession;
        synchronized (this.mScreenshotSessions) {
            displayScreenshotSession = this.mScreenshotSessions.get(i);
            if (displayScreenshotSession == null) {
                displayScreenshotSession = new DisplayScreenshotSession(this.mContext, i, this.mScreenshotNotificationsController);
                this.mScreenshotSessions.put(i, displayScreenshotSession);
            }
        }
        displayScreenshotSession.takeScreenshot(i2);
    }

    public void takeScreenshot(int i, ScreenshotHelper.ScreenshotRequest screenshotRequest) {
        DisplayScreenshotSession displayScreenshotSession;
        synchronized (this.mScreenshotSessions) {
            displayScreenshotSession = this.mScreenshotSessions.get(i);
            if (displayScreenshotSession == null) {
                displayScreenshotSession = new DisplayScreenshotSession(this.mContext, i, this.mScreenshotNotificationsController);
                this.mScreenshotSessions.put(i, displayScreenshotSession);
            }
        }
        displayScreenshotSession.takeScreenshot(screenshotRequest);
    }

    private class DisplayScreenshotSession {
        /* access modifiers changed from: private */
        public Handler mBgHandler;
        private HandlerThread mBgHandlerThread;
        /* access modifiers changed from: private */
        public BitmapOffsetCalculate mBitmapOffsetCalculate;
        /* access modifiers changed from: private */
        public Bitmap mBottomBitmap;
        private final BroadcastReceiver mBroadcastReceiver;
        private MediaActionSound mCameraSound;
        /* access modifiers changed from: private */
        public boolean mCanPreviewCancelByTouch;
        private CliGlobalScreenshot mCliScreenshot;
        /* access modifiers changed from: private */
        public final Context mContext;
        /* access modifiers changed from: private */
        public View mCoverView;
        /* access modifiers changed from: private */
        public Dialog mDeleteDialog;
        private float mDensityScale;
        private final Display mDisplay;
        private final int mDisplayId;
        private final DisplayManager mDisplayManager;
        private final DisplayMetrics mDisplayMetrics;
        /* access modifiers changed from: private */
        public int mExpectCompareHeight;
        /* access modifiers changed from: private */
        public int mFirstScreenBitmapHeight;
        /* access modifiers changed from: private */
        public int mFirstScreenBitmapWidth;
        /* access modifiers changed from: private */
        public boolean mHandleSwipToDismiss;
        /* access modifiers changed from: private */
        public IMotoScreenShotEditService mIMotoScreenShotEditService;
        /* access modifiers changed from: private */
        public boolean mInSwipToDismissValidRect;
        /* access modifiers changed from: private */
        public boolean mIs3rdAppScrolledEnd;
        private boolean mIsCliDisplay;
        private boolean mIsFocusedWindowRestrict;
        /* access modifiers changed from: private */
        public int mLastCropBottom;
        /* access modifiers changed from: private */
        public float mLastDispatchScreenshotViewX;
        /* access modifiers changed from: private */
        public float mLastDispatchScreenshotViewY;
        /* access modifiers changed from: private */
        public int mLastExpectScrollDistance;
        /* access modifiers changed from: private */
        public Bitmap mLastFullBitmap;
        private ScreenshotController.SavedImageData mLastSavedImageData;
        private View mLensButton;
        /* access modifiers changed from: private */
        public LongScreenShotHelper mLongScreenShotHelper;
        private View mLongScreenshotProcessView;
        /* access modifiers changed from: private */
        public LongScreenshotScrollView mLongScreenshotScrollView;
        /* access modifiers changed from: private */
        public View mLongScreenshotScrollViewContainer;
        /* access modifiers changed from: private */
        public int mLongScreenshotScrollViewContainerTouchDownX;
        /* access modifiers changed from: private */
        public int mLongScreenshotScrollViewContainerTouchDownY;
        private int mLongScreenshotScrollViewHeight;
        /* access modifiers changed from: private */
        public View mLongScreenshotStartButton;
        /* access modifiers changed from: private */
        public Boolean mLongScreenshotStartedLock;
        private View mLongScreenshotStopButton;
        /* access modifiers changed from: private */
        public boolean mLongScreenshotSuccessfully;
        private int mMotoEditIndex;
        /* access modifiers changed from: private */
        public boolean mMotoEditOnLSShotEnded;
        private int mMotoEditSessionId;
        /* access modifiers changed from: private */
        public MotoScreenshotRootView mMotoScreenshotRootView;
        private MotoScreenshotRootView.OnInterceptTouchEventListener mMotoScreenshotRootViewInterceptTouchEventListener;
        private View.OnTouchListener mMotoScreenshotRootViewOnTouchListener;
        /* access modifiers changed from: private */
        public final ScreenshotNotificationsController mNotificationsController;
        private LongScreenShotHelper.OnLongScreenshotListener mOnLongScreenshotListener;
        private LongScreenshotScrollView.OnScrollListener mOnScrollListener;
        private LongScreenshotScrollView.OnTouchStatusListener mOnTouchStatusListener;
        /* access modifiers changed from: private */
        public final Handler mPermanentHandler;
        private SoftReference<int[]> mPixelsCache;
        /* access modifiers changed from: private */
        public boolean mPreviewWindowIsShowing;
        private ProcessObserver mProcessObserver;
        private List<SaveImageInBackgroundTask> mSaveInBgTasks;
        private final ServiceConnection mScreeenshotEditConnection;
        /* access modifiers changed from: private */
        public final Handler mScreenshotHandler;
        private View mScreenshotWindow;
        /* access modifiers changed from: private */
        public int mSeledPossibleDistanceIndex;
        private boolean mShoudStartEditorAfterSave;
        /* access modifiers changed from: private */
        public boolean mSmallScrollInvalidClick;
        /* access modifiers changed from: private */
        public int mSwipToDismissLastX;
        /* access modifiers changed from: private */
        public int mSwipToDismissLastXOffset;
        /* access modifiers changed from: private */
        public int mSwipToDismissTouchDownX;
        /* access modifiers changed from: private */
        public int mSwipToDismissTouchDownY;
        /* access modifiers changed from: private */
        public VelocityTracker mSwipToDismissVelocityTracker;
        private MyTaskStackListener mTaskListener;
        private Runnable mTimeoutQuitRunnable;
        /* access modifiers changed from: private */
        public View mToolbarLayout;
        /* access modifiers changed from: private */
        public boolean mTouchHandleByLongScreenshotScrollView;
        /* access modifiers changed from: private */
        public final int mTouchSlop;
        /* access modifiers changed from: private */
        public int mUpdatedCount;
        private PhoneWindow mWindow;
        private WindowManager.LayoutParams mWindowLayoutParams;
        private final WindowManager mWindowManager;

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$saveScreenshotInWorkerThread$3(Uri uri) {
        }

        static /* synthetic */ int access$6408(DisplayScreenshotSession displayScreenshotSession) {
            int i = displayScreenshotSession.mUpdatedCount;
            displayScreenshotSession.mUpdatedCount = i + 1;
            return i;
        }

        private DisplayScreenshotSession(Context context, int i, ScreenshotNotificationsController screenshotNotificationsController) {
            this.mSaveInBgTasks = new ArrayList();
            this.mLastSavedImageData = null;
            this.mIsFocusedWindowRestrict = false;
            this.mInSwipToDismissValidRect = false;
            this.mHandleSwipToDismiss = false;
            this.mSwipToDismissLastXOffset = 0;
            this.mTouchHandleByLongScreenshotScrollView = false;
            this.mSmallScrollInvalidClick = false;
            this.mSwipToDismissVelocityTracker = VelocityTracker.obtain();
            this.mPreviewWindowIsShowing = false;
            this.mPermanentHandler = new Handler(Looper.getMainLooper());
            this.mLongScreenshotSuccessfully = false;
            this.mScreenshotHandler = new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message message) {
                    int i = message.what;
                    boolean z = true;
                    if (i == 1) {
                        DisplayScreenshotSession.this.handleStopAutoScrolled();
                    } else if (i == 2) {
                        DisplayScreenshotSession displayScreenshotSession = DisplayScreenshotSession.this;
                        if (message.arg1 != 1) {
                            z = false;
                        }
                        displayScreenshotSession.handleSaveBitmapTips(z);
                    } else if (i == 3) {
                        if (message.arg1 != 1) {
                            z = false;
                        }
                        DisplayScreenshotSession.this.mLongScreenshotScrollView.addBitmaps((Bitmap[]) message.obj, z);
                    }
                }
            };
            this.mBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    action.hashCode();
                    if (!action.equals("com.com.android.systemui.ACTION_HEADS_UP_SHOWING")) {
                        if (!action.equals("com.android.systemui.ACTION_GLOBAL_ACTIONS_SHOW")) {
                            DisplayScreenshotSession.this.dismissScreenshotPreview("close system dialogs", true);
                            return;
                        }
                        if (DisplayScreenshotSession.this.mLongScreenshotStartButton != null) {
                            DisplayScreenshotSession.this.mLongScreenshotStartButton.setVisibility(8);
                        }
                        DisplayScreenshotSession.this.handleStopLongScreenshot();
                    } else if ("call".equals(intent.getStringExtra("category"))) {
                        DisplayScreenshotSession.this.dismissScreenshotPreview("call headsup showing", true);
                    }
                }
            };
            this.mTimeoutQuitRunnable = new Runnable() {
                public void run() {
                    DisplayScreenshotSession.this.dismissScreenshotPreview("time out", false);
                }
            };
            this.mOnTouchStatusListener = new C1356x6b1d743(this);
            this.mMotoScreenshotRootViewOnTouchListener = new View.OnTouchListener() {
                /* JADX WARNING: Code restructure failed: missing block: B:14:0x002f, code lost:
                    if (com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.access$2800(r9.this$1) == false) goto L_0x0039;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
                    r9.this$1.dismissScreenshotPreview("touch outside before saved", true);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:16:0x0039, code lost:
                    return false;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:49:0x0131, code lost:
                    if (r3 != 3) goto L_0x028f;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean onTouch(android.view.View r10, android.view.MotionEvent r11) {
                    /*
                        r9 = this;
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.MotoScreenshotRootView r10 = r10.mMotoScreenshotRootView
                        r0 = 0
                        if (r10 != 0) goto L_0x000a
                        return r0
                    L_0x000a:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r10 = r10.isToolbarEnabled()
                        r1 = 1
                        if (r10 != 0) goto L_0x003d
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        java.lang.Boolean r10 = r10.mLongScreenshotStartedLock
                        monitor-enter(r10)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this     // Catch:{ all -> 0x003a }
                        java.lang.Boolean r11 = r11.mLongScreenshotStartedLock     // Catch:{ all -> 0x003a }
                        boolean r11 = r11.booleanValue()     // Catch:{ all -> 0x003a }
                        if (r11 == 0) goto L_0x0028
                        monitor-exit(r10)     // Catch:{ all -> 0x003a }
                        return r0
                    L_0x0028:
                        monitor-exit(r10)     // Catch:{ all -> 0x003a }
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r10 = r10.mCanPreviewCancelByTouch
                        if (r10 == 0) goto L_0x0039
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        java.lang.String r10 = "touch outside before saved"
                        r9.dismissScreenshotPreview(r10, r1)
                    L_0x0039:
                        return r0
                    L_0x003a:
                        r9 = move-exception
                        monitor-exit(r10)     // Catch:{ all -> 0x003a }
                        throw r9
                    L_0x003d:
                        float r10 = r11.getX()
                        int r10 = (int) r10
                        float r2 = r11.getY()
                        int r2 = (int) r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r3 = r3.mHandleSwipToDismiss
                        r4 = 3
                        if (r3 == 0) goto L_0x0072
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r3 = r3.mTouchHandleByLongScreenshotScrollView
                        if (r3 == 0) goto L_0x0072
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r3.mTouchHandleByLongScreenshotScrollView = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r3 = r3.mLongScreenshotScrollView
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        float r5 = r5.mLastDispatchScreenshotViewX
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        float r6 = r6.mLastDispatchScreenshotViewY
                        r3.dispatchOutsideTouchEvent(r4, r5, r6)
                    L_0x0072:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r3 = r3.mHandleSwipToDismiss
                        if (r3 == 0) goto L_0x0080
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r9.handleSwipToDismissTouch(r11)
                        return r1
                    L_0x0080:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r3.mSwipToDismissLastX = r10
                        int r3 = r11.getAction()
                        if (r3 != 0) goto L_0x00c8
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.MotoScreenshotRootView r3 = r3.mMotoScreenshotRootView
                        int[] r3 = r3.getLocationOnScreen()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r5 = r5.mLongScreenshotScrollView
                        int[] r5 = r5.getLocationOnScreen()
                        r6 = r3[r0]
                        r7 = r5[r0]
                        int r6 = r6 - r7
                        int r6 = r6 + r10
                        float r6 = (float) r6
                        r3 = r3[r1]
                        r5 = r5[r1]
                        int r3 = r3 - r5
                        int r3 = r3 + r2
                        float r3 = (float) r3
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        float unused = r5.mLastDispatchScreenshotViewX = r6
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        float unused = r5.mLastDispatchScreenshotViewY = r3
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r7 = r5.mLongScreenshotScrollView
                        int r8 = r11.getAction()
                        boolean r3 = r7.dispatchOutsideTouchEvent(r8, r6, r3)
                        boolean unused = r5.mTouchHandleByLongScreenshotScrollView = r3
                    L_0x00c8:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r3 = r3.mTouchHandleByLongScreenshotScrollView
                        if (r3 == 0) goto L_0x0126
                        int r3 = r11.getAction()
                        if (r3 == 0) goto L_0x0120
                        if (r3 == r1) goto L_0x00db
                        if (r3 == r4) goto L_0x00db
                        goto L_0x00e5
                    L_0x00db:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        r3.startTimeoutQuitIfNeed()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r3.mTouchHandleByLongScreenshotScrollView = r0
                    L_0x00e5:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.MotoScreenshotRootView r3 = r3.mMotoScreenshotRootView
                        int[] r3 = r3.getLocationOnScreen()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r4 = r4.mLongScreenshotScrollView
                        int[] r4 = r4.getLocationOnScreen()
                        r5 = r3[r0]
                        r0 = r4[r0]
                        int r5 = r5 - r0
                        int r10 = r10 + r5
                        float r10 = (float) r10
                        r0 = r3[r1]
                        r1 = r4[r1]
                        int r0 = r0 - r1
                        int r2 = r2 + r0
                        float r0 = (float) r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        float unused = r1.mLastDispatchScreenshotViewX = r10
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        float unused = r1.mLastDispatchScreenshotViewY = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r9 = r9.mLongScreenshotScrollView
                        int r11 = r11.getAction()
                        boolean r9 = r9.dispatchOutsideTouchEvent(r11, r10, r0)
                        return r9
                    L_0x0120:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        r9.stopTimeoutQuit()
                        return r1
                    L_0x0126:
                        int r3 = r11.getAction()
                        if (r3 == 0) goto L_0x0280
                        if (r3 == r1) goto L_0x01b7
                        r5 = 2
                        if (r3 == r5) goto L_0x0135
                        if (r3 == r4) goto L_0x01b7
                        goto L_0x028f
                    L_0x0135:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r3 = r3.mInSwipToDismissValidRect
                        if (r3 == 0) goto L_0x0182
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.VelocityTracker r3 = r3.mSwipToDismissVelocityTracker
                        r3.addMovement(r11)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r11 = r11.mSwipToDismissTouchDownX
                        int r11 = r11 - r10
                        int r11 = java.lang.Math.abs(r11)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r3 = r3.mSwipToDismissTouchDownY
                        int r3 = r3 - r2
                        int r3 = java.lang.Math.abs(r3)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r4 = r4.mTouchSlop
                        if (r3 <= r4) goto L_0x016f
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r11.mInSwipToDismissValidRect = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r11.mSmallScrollInvalidClick = r1
                        goto L_0x0182
                    L_0x016f:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r0 = r0.mTouchSlop
                        if (r11 <= r0) goto L_0x0182
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r10.mHandleSwipToDismiss = r1
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r9.mSmallScrollInvalidClick = r1
                        return r1
                    L_0x0182:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r11 = r11.mSmallScrollInvalidClick
                        if (r11 != 0) goto L_0x028f
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r11 = r11.mLongScreenshotScrollViewContainerTouchDownX
                        int r11 = r11 - r10
                        int r10 = java.lang.Math.abs(r11)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r11 = r11.mLongScreenshotScrollViewContainerTouchDownY
                        int r11 = r11 - r2
                        int r11 = java.lang.Math.abs(r11)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r0 = r0.mTouchSlop
                        if (r10 > r0) goto L_0x01b0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r10 = r10.mTouchSlop
                        if (r11 <= r10) goto L_0x028f
                    L_0x01b0:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r9.mSmallScrollInvalidClick = r1
                        goto L_0x028f
                    L_0x01b7:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        r10.startTimeoutQuitIfNeed()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r10 = r10.mSmallScrollInvalidClick
                        if (r10 == 0) goto L_0x01c6
                        goto L_0x028f
                    L_0x01c6:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r10 = r10.mLongScreenshotScrollViewContainer
                        int r10 = r10.getLeft()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r11 = r11.mLongScreenshotScrollView
                        int r11 = r11.getLeft()
                        int r10 = r10 + r11
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r11 = r11.mLongScreenshotScrollView
                        int r11 = r11.getPaddingLeft()
                        int r10 = r10 + r11
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r11 = r11.mLongScreenshotScrollViewContainer
                        int r11 = r11.getLeft()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r2 = r2.mLongScreenshotScrollView
                        int r2 = r2.getRight()
                        int r11 = r11 + r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r2 = r2.mLongScreenshotScrollView
                        int r2 = r2.getPaddingRight()
                        int r11 = r11 - r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r2 = r2.mLongScreenshotScrollViewContainer
                        int r2 = r2.getTop()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r3 = r3.mLongScreenshotScrollView
                        int r3 = r3.getTop()
                        int r2 = r2 + r3
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r3 = r3.mLongScreenshotScrollView
                        int r3 = r3.getPaddingTop()
                        int r2 = r2 + r3
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r3 = r3.mLongScreenshotScrollView
                        float r3 = r3.getScrollerY()
                        int r3 = (int) r3
                        int r2 = r2 + r3
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r3 = r3.mLongScreenshotScrollViewContainer
                        int r3 = r3.getTop()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r4 = r4.mLongScreenshotScrollView
                        int r4 = r4.getBottom()
                        int r3 = r3 + r4
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r4 = r4.mLongScreenshotScrollView
                        int r4 = r4.getPaddingBottom()
                        int r3 = r3 - r4
                        android.graphics.Rect r4 = new android.graphics.Rect
                        r4.<init>(r10, r2, r11, r3)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r10 = r10.mLongScreenshotScrollViewContainerTouchDownX
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r11 = r11.mLongScreenshotScrollViewContainerTouchDownY
                        boolean r10 = r4.contains(r10, r11)
                        if (r10 == 0) goto L_0x026f
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        r9.startEditor()
                        goto L_0x028f
                    L_0x026f:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r10 = r10.mCanPreviewCancelByTouch
                        if (r10 == 0) goto L_0x028f
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        java.lang.String r10 = "touch outside"
                        r9.dismissScreenshotPreview(r10, r0)
                        goto L_0x028f
                    L_0x0280:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r11.mLongScreenshotScrollViewContainerTouchDownX = r10
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r10 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r10.mLongScreenshotScrollViewContainerTouchDownY = r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r9.mSmallScrollInvalidClick = r0
                    L_0x028f:
                        return r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C131513.onTouch(android.view.View, android.view.MotionEvent):boolean");
                }
            };
            this.mMotoScreenshotRootViewInterceptTouchEventListener = new MotoScreenshotRootView.OnInterceptTouchEventListener() {
                /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
                    if (r3 != 3) goto L_0x0084;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean onInterceptTouchEvent(android.view.MotionEvent r9) {
                    /*
                        r8 = this;
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r0 = r0.isToolbarEnabled()
                        r1 = 0
                        if (r0 == 0) goto L_0x0166
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.MotoScreenshotRootView r0 = r0.mMotoScreenshotRootView
                        if (r0 != 0) goto L_0x0013
                        goto L_0x0166
                    L_0x0013:
                        float r0 = r9.getX()
                        int r0 = (int) r0
                        float r2 = r9.getY()
                        int r2 = (int) r2
                        int r3 = r9.getAction()
                        r4 = 1
                        if (r3 == 0) goto L_0x0085
                        if (r3 == r4) goto L_0x0075
                        r5 = 2
                        if (r3 == r5) goto L_0x002d
                        r9 = 3
                        if (r3 == r9) goto L_0x0075
                        goto L_0x0084
                    L_0x002d:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean r3 = r3.mInSwipToDismissValidRect
                        if (r3 == 0) goto L_0x0084
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.VelocityTracker r3 = r3.mSwipToDismissVelocityTracker
                        r3.addMovement(r9)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r9.mSwipToDismissLastX = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r9 = r9.mSwipToDismissTouchDownX
                        int r9 = r9 - r0
                        int r9 = java.lang.Math.abs(r9)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r0 = r0.mSwipToDismissTouchDownY
                        int r0 = r0 - r2
                        int r0 = java.lang.Math.abs(r0)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r2 = r2.mTouchSlop
                        if (r0 <= r2) goto L_0x0067
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r8 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r8.mInSwipToDismissValidRect = r1
                        goto L_0x0084
                    L_0x0067:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int r0 = r0.mTouchSlop
                        if (r9 <= r0) goto L_0x0084
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r8 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r8.mHandleSwipToDismiss = r4
                        return r4
                    L_0x0075:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        r9.startTimeoutQuitIfNeed()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r9.mInSwipToDismissValidRect = r1
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r8 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r8.mHandleSwipToDismiss = r1
                    L_0x0084:
                        return r1
                    L_0x0085:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r3.mInSwipToDismissValidRect = r1
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r3.mHandleSwipToDismiss = r1
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        r3.stopTimeoutQuit()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r3.mSwipToDismissLastXOffset = r1
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r3 = r3.mLongScreenshotScrollViewContainer
                        int r3 = r3.getTop()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.LongScreenshotScrollView r5 = r5.mLongScreenshotScrollView
                        float r5 = r5.getVaildYStart()
                        int r5 = (int) r5
                        int r3 = r3 + r5
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r5 = r5.mToolbarLayout
                        int r5 = r5.getTop()
                        if (r3 <= r5) goto L_0x00c5
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r3 = r3.mToolbarLayout
                        int r3 = r3.getTop()
                    L_0x00c5:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r5 = r5.mLongScreenshotScrollViewContainer
                        int r5 = r5.getBottom()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r6 = r6.mToolbarLayout
                        int r6 = r6.getBottom()
                        if (r5 >= r6) goto L_0x00e5
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r5 = r5.mToolbarLayout
                        int r5 = r5.getBottom()
                    L_0x00e5:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        com.motorola.systemui.screenshot.MotoScreenshotRootView r6 = r6.mMotoScreenshotRootView
                        boolean r6 = r6.isLayoutRtl()
                        if (r6 == 0) goto L_0x0106
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r6 = r6.mToolbarLayout
                        int r6 = r6.getLeft()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r7 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r7 = r7.mLongScreenshotScrollViewContainer
                        int r7 = r7.getRight()
                        goto L_0x011a
                    L_0x0106:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r6 = r6.mLongScreenshotScrollViewContainer
                        int r6 = r6.getLeft()
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r7 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.View r7 = r7.mToolbarLayout
                        int r7 = r7.getRight()
                    L_0x011a:
                        if (r0 < r6) goto L_0x0166
                        if (r0 <= r7) goto L_0x011f
                        goto L_0x0166
                    L_0x011f:
                        if (r2 < r3) goto L_0x0166
                        if (r2 <= r5) goto L_0x0124
                        goto L_0x0166
                    L_0x0124:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.VelocityTracker r3 = r3.mSwipToDismissVelocityTracker
                        if (r3 != 0) goto L_0x0136
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.VelocityTracker r5 = android.view.VelocityTracker.obtain()
                        android.view.VelocityTracker unused = r3.mSwipToDismissVelocityTracker = r5
                        goto L_0x013f
                    L_0x0136:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.VelocityTracker r3 = r3.mSwipToDismissVelocityTracker
                        r3.clear()
                    L_0x013f:
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        android.view.VelocityTracker r3 = r3.mSwipToDismissVelocityTracker
                        r3.addMovement(r9)
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r9.mLongScreenshotScrollViewContainerTouchDownX = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r9.mLongScreenshotScrollViewContainerTouchDownY = r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r9.mSwipToDismissTouchDownX = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r9.mSwipToDismissTouchDownY = r2
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        int unused = r9.mSwipToDismissLastX = r0
                        com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r8 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                        boolean unused = r8.mInSwipToDismissValidRect = r4
                    L_0x0166:
                        return r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C131614.onInterceptTouchEvent(android.view.MotionEvent):boolean");
                }
            };
            this.mMotoEditIndex = 0;
            this.mMotoEditSessionId = -1;
            this.mMotoEditOnLSShotEnded = false;
            this.mLastExpectScrollDistance = -1;
            this.mUpdatedCount = 0;
            this.mLastCropBottom = 0;
            this.mSeledPossibleDistanceIndex = -1;
            this.mIs3rdAppScrolledEnd = false;
            this.mLongScreenshotStartedLock = Boolean.FALSE;
            this.mOnLongScreenshotListener = new LongScreenShotHelper.OnLongScreenshotListener() {
                public void onScreenShotUpdate(final int[] iArr, final Rect rect) {
                    if (MotoGlobalScreenshot.DEBUG) {
                        Log.d("MotoGlobalScreenshot", "onScreenShotUpdate possibleDistances = " + iArr + "; scrollViewBounds = " + rect);
                    }
                    DisplayScreenshotSession.this.mBgHandler.post(new Runnable() {
                        /* JADX WARNING: Removed duplicated region for block: B:102:0x042a  */
                        /* JADX WARNING: Removed duplicated region for block: B:109:0x046b  */
                        /* JADX WARNING: Removed duplicated region for block: B:82:0x0274  */
                        /* JADX WARNING: Removed duplicated region for block: B:83:0x0307  */
                        /* JADX WARNING: Removed duplicated region for block: B:89:0x0336  */
                        /* JADX WARNING: Removed duplicated region for block: B:94:0x03fd  */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            /*
                                r23 = this;
                                r0 = r23
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r1 = r1.takeScreenBitmap()
                                android.graphics.Rect r2 = r4
                                int r3 = r2.top
                                int r2 = r2.height()
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r4 = r4.mLastExpectScrollDistance
                                int r2 = r2 - r4
                                r14 = 2
                                int r2 = r2 / r14
                                int r15 = r3 + r2
                                android.graphics.Rect r2 = r4
                                int r2 = r2.width()
                                int r2 = r2 / 5
                                android.graphics.Rect r3 = r4
                                int r4 = r3.left
                                int r13 = r4 + r2
                                int r3 = r3.right
                                int r12 = r3 - r2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2.mExpectCompareHeight
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r3 = r3.mLastExpectScrollDistance
                                int r2 = java.lang.Math.min(r2, r3)
                                android.graphics.Rect r3 = r4
                                int r3 = r3.height()
                                r11 = 3
                                int r3 = r3 / r11
                                int r10 = java.lang.Math.min(r2, r3)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2.mLastExpectScrollDistance
                                int r9 = r15 + r2
                                int r2 = r12 - r13
                                int r2 = r2 / 100
                                if (r2 <= 0) goto L_0x0063
                                r7 = r2
                                goto L_0x0064
                            L_0x0063:
                                r7 = 1
                            L_0x0064:
                                r16 = 1
                                java.util.HashSet r2 = new java.util.HashSet
                                r2.<init>()
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r3 = r3.mLastExpectScrollDistance
                                int r3 = r3 + r15
                                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                                r2.add(r3)
                                boolean r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                java.lang.String r6 = "MotoGlobalScreenshot"
                                if (r3 == 0) goto L_0x009f
                                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                                r3.<init>()
                                java.lang.String r4 = "onScreenShotUpdate mLastExpectScrollDistance = "
                                r3.append(r4)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r4 = r4.mLastExpectScrollDistance
                                r3.append(r4)
                                java.lang.String r3 = r3.toString()
                                android.util.Log.d(r6, r3)
                            L_0x009f:
                                int[] r3 = r3
                                if (r3 == 0) goto L_0x00f2
                                boolean r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r3 == 0) goto L_0x00c0
                                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                                r3.<init>()
                                java.lang.String r4 = "onScreenShotUpdate possibleDistances size = "
                                r3.append(r4)
                                int[] r4 = r3
                                int r4 = r4.length
                                r3.append(r4)
                                java.lang.String r3 = r3.toString()
                                android.util.Log.d(r6, r3)
                            L_0x00c0:
                                int[] r3 = r3
                                int r4 = r3.length
                                r5 = 0
                            L_0x00c4:
                                if (r5 >= r4) goto L_0x00f2
                                r8 = r3[r5]
                                if (r8 < 0) goto L_0x00d3
                                int r19 = r15 + r8
                                java.lang.Integer r11 = java.lang.Integer.valueOf(r19)
                                r2.add(r11)
                            L_0x00d3:
                                boolean r11 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r11 == 0) goto L_0x00ed
                                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                                r11.<init>()
                                java.lang.String r14 = "onScreenShotUpdate possibleDistances = "
                                r11.append(r14)
                                r11.append(r8)
                                java.lang.String r8 = r11.toString()
                                android.util.Log.d(r6, r8)
                            L_0x00ed:
                                int r5 = r5 + 1
                                r11 = 3
                                r14 = 2
                                goto L_0x00c4
                            L_0x00f2:
                                java.lang.Object[] r2 = r2.toArray()
                                int r3 = r2.length
                                int[] r14 = new int[r3]
                                r3 = 0
                            L_0x00fa:
                                int r4 = r2.length
                                if (r3 >= r4) goto L_0x010a
                                r4 = r2[r3]
                                java.lang.Integer r4 = (java.lang.Integer) r4
                                int r4 = r4.intValue()
                                r14[r3] = r4
                                int r3 = r3 + 1
                                goto L_0x00fa
                            L_0x010a:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2.mUpdatedCount
                                if (r2 == 0) goto L_0x017d
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2.mSeledPossibleDistanceIndex
                                if (r2 >= 0) goto L_0x0120
                                goto L_0x017d
                            L_0x0120:
                                int[] r2 = r3
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r3 = r3.mSeledPossibleDistanceIndex
                                r2 = r2[r3]
                                if (r2 > 0) goto L_0x0170
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                com.motorola.systemui.screenshot.BitmapOffsetCalculate r2 = r2.mBitmapOffsetCalculate
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r3 = r3.mLastFullBitmap
                                r4 = r1
                                r11 = 0
                                r5 = r15
                                r8 = r6
                                r6 = r13
                                r17 = r7
                                r7 = r12
                                r21 = r8
                                r8 = r10
                                r18 = r9
                                r9 = r15
                                r22 = r10
                                r10 = r18
                                r11 = r17
                                r20 = r12
                                r12 = r16
                                r16 = r13
                                r13 = r14
                                int r2 = r2.calcY(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
                                if (r2 <= 0) goto L_0x0162
                                int r5 = r2 - r15
                                goto L_0x0163
                            L_0x0162:
                                r5 = 0
                            L_0x0163:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                r3 = -1
                                int unused = r2.mSeledPossibleDistanceIndex = r3
                                r2 = r5
                                r3 = r21
                                goto L_0x0248
                            L_0x0170:
                                r17 = r7
                                r18 = r9
                                r22 = r10
                                r20 = r12
                                r16 = r13
                                r3 = r6
                                goto L_0x0248
                            L_0x017d:
                                r21 = r6
                                r17 = r7
                                r18 = r9
                                r22 = r10
                                r20 = r12
                                r16 = r13
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                com.motorola.systemui.screenshot.BitmapOffsetCalculate r2 = r2.mBitmapOffsetCalculate
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r3 = r3.mLastFullBitmap
                                r12 = 1
                                r4 = r1
                                r5 = r15
                                r6 = r16
                                r7 = r20
                                r8 = r22
                                r9 = r15
                                r10 = r18
                                r11 = r17
                                r13 = r14
                                int r2 = r2.calcY(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
                                if (r2 <= 0) goto L_0x01b1
                                int r5 = r2 - r15
                                goto L_0x01b2
                            L_0x01b1:
                                r5 = 0
                            L_0x01b2:
                                boolean r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r3 == 0) goto L_0x01d7
                                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                                r3.<init>()
                                java.lang.String r4 = "onScreenShotUpdate calcY = "
                                r3.append(r4)
                                r3.append(r2)
                                java.lang.String r2 = "scrolledDistance = "
                                r3.append(r2)
                                r3.append(r5)
                                java.lang.String r2 = r3.toString()
                                r3 = r21
                                android.util.Log.d(r3, r2)
                                goto L_0x01d9
                            L_0x01d7:
                                r3 = r21
                            L_0x01d9:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2.mUpdatedCount
                                if (r2 != 0) goto L_0x0247
                                if (r5 <= 0) goto L_0x0214
                                r2 = 0
                            L_0x01e6:
                                int[] r4 = r3
                                int r6 = r4.length
                                if (r2 >= r6) goto L_0x0247
                                r4 = r4[r2]
                                if (r5 != r4) goto L_0x0211
                                boolean r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r4 == 0) goto L_0x0209
                                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                                r4.<init>()
                                java.lang.String r6 = "onScreenShotUpdate match possibleDistance i = "
                                r4.append(r6)
                                r4.append(r2)
                                java.lang.String r4 = r4.toString()
                                android.util.Log.d(r3, r4)
                            L_0x0209:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int unused = r4.mSeledPossibleDistanceIndex = r2
                                goto L_0x0247
                            L_0x0211:
                                int r2 = r2 + 1
                                goto L_0x01e6
                            L_0x0214:
                                r2 = 0
                            L_0x0215:
                                int[] r4 = r3
                                int r6 = r4.length
                                if (r2 >= r6) goto L_0x0247
                                r4 = r4[r2]
                                if (r4 <= 0) goto L_0x0244
                                boolean r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r4 == 0) goto L_0x0238
                                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                                r4.<init>()
                                java.lang.String r5 = "onScreenShotUpdate use possibleDistance i = "
                                r4.append(r5)
                                r4.append(r2)
                                java.lang.String r4 = r4.toString()
                                android.util.Log.d(r3, r4)
                            L_0x0238:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int unused = r4.mSeledPossibleDistanceIndex = r2
                                int[] r4 = r3
                                r2 = r4[r2]
                                goto L_0x0248
                            L_0x0244:
                                int r2 = r2 + 1
                                goto L_0x0215
                            L_0x0247:
                                r2 = r5
                            L_0x0248:
                                if (r2 <= 0) goto L_0x024c
                                r8 = 1
                                goto L_0x024d
                            L_0x024c:
                                r8 = 0
                            L_0x024d:
                                if (r8 == 0) goto L_0x026c
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r4 = r4.mUpdatedCount
                                r5 = 9
                                if (r4 > r5) goto L_0x026c
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r4 = r4.mLastExpectScrollDistance
                                r5 = 2
                                int r4 = r4 * r5
                                r5 = 3
                                int r4 = r4 / r5
                                if (r2 >= r4) goto L_0x026a
                                goto L_0x026d
                            L_0x026a:
                                r4 = 0
                                goto L_0x026e
                            L_0x026c:
                                r5 = 3
                            L_0x026d:
                                r4 = 1
                            L_0x026e:
                                boolean r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r6 == 0) goto L_0x0307
                                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                                r6.<init>()
                                java.lang.String r7 = "onScreenShotUpdate offsetBitmapY = "
                                r6.append(r7)
                                r6.append(r15)
                                java.lang.String r7 = "; startX = "
                                r6.append(r7)
                                r7 = r16
                                r6.append(r7)
                                java.lang.String r7 = "; endX = "
                                r6.append(r7)
                                r7 = r20
                                r6.append(r7)
                                java.lang.String r7 = "; compareHeight = "
                                r6.append(r7)
                                r7 = r22
                                r6.append(r7)
                                java.lang.String r9 = "; baseBitmapStartY = "
                                r6.append(r9)
                                r6.append(r15)
                                java.lang.String r9 = "; baseBitmapEndY = "
                                r6.append(r9)
                                r9 = r18
                                r6.append(r9)
                                java.lang.String r9 = "; intervalX = "
                                r6.append(r9)
                                r9 = r17
                                r6.append(r9)
                                java.lang.String r9 = "; probableYs = "
                                r6.append(r9)
                                r6.append(r14)
                                java.lang.String r9 = "; scrollViewBounds = "
                                r6.append(r9)
                                android.graphics.Rect r9 = r4
                                r6.append(r9)
                                java.lang.String r9 = "; possibleDistances = "
                                r6.append(r9)
                                int[] r9 = r3
                                r6.append(r9)
                                java.lang.String r9 = "; mLastExpectScrollDistance = "
                                r6.append(r9)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r9 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r9 = r9.mLastExpectScrollDistance
                                r6.append(r9)
                                java.lang.String r9 = "; scrolledDistance = "
                                r6.append(r9)
                                r6.append(r2)
                                java.lang.String r9 = "; scrolledSuccessful = "
                                r6.append(r9)
                                r6.append(r8)
                                java.lang.String r9 = "; end = "
                                r6.append(r9)
                                r6.append(r4)
                                java.lang.String r6 = r6.toString()
                                android.util.Log.d(r3, r6)
                                goto L_0x0309
                            L_0x0307:
                                r7 = r22
                            L_0x0309:
                                boolean r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                                if (r3 == 0) goto L_0x0334
                                if (r8 != 0) goto L_0x0334
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r6 = r3.mLastFullBitmap
                                java.lang.String r9 = "last_full.jpg"
                                java.io.File unused = r3.saveBitmapFile(r6, r9)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r10 = r15 + r7
                                android.graphics.Bitmap r3 = r3.cropBitmap(r1, r15, r10)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                java.lang.String r7 = "cur_crop.jpg"
                                java.io.File unused = r6.saveBitmapFile(r3, r7)
                                r3.recycle()
                            L_0x0334:
                                if (r8 == 0) goto L_0x03fd
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r3 = r3.mUpdatedCount
                                if (r3 != 0) goto L_0x03b2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2 + r15
                                int unused = r3.mLastCropBottom = r2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                r2.motoEditOnLongScreenShotStart()
                                android.graphics.Bitmap[] r2 = new android.graphics.Bitmap[r5]
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r5 = r3.mLastFullBitmap
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r6 = r6.mLastCropBottom
                                r7 = 0
                                android.graphics.Bitmap r3 = r3.cropBitmap(r5, r7, r6)
                                r2[r7] = r3
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r5 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                java.lang.String unused = r5.motoEditOnImageAdded(r3)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r5 = r3.mLastCropBottom
                                android.graphics.Bitmap r3 = r3.cropBitmap(r1, r15, r5)
                                r5 = 1
                                r2[r5] = r3
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                java.lang.String unused = r6.motoEditOnImageAdded(r3)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r6 = r3.mLastCropBottom
                                int r7 = r1.getHeight()
                                android.graphics.Bitmap r3 = r3.cropBitmap(r1, r6, r7)
                                r6 = 2
                                r2[r6] = r3
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap unused = r6.mBottomBitmap = r3
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r3 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                r3.addImages(r2, r5)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                com.motorola.systemui.screenshot.LongScreenshotScrollView r2 = r2.mLongScreenshotScrollView
                                r2.startAutoScrollAnim()
                                goto L_0x03f5
                            L_0x03b2:
                                r3 = 2
                                r5 = 1
                                r7 = 0
                                android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r3]
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r8 = r6.mLastCropBottom
                                int r8 = r8 - r2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r2 = r2.mLastCropBottom
                                android.graphics.Bitmap r2 = r6.cropBitmap(r1, r8, r2)
                                r3[r7] = r2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                java.lang.String unused = r6.motoEditOnImageAdded(r2)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r6 = r2.mLastCropBottom
                                int r7 = r1.getHeight()
                                android.graphics.Bitmap r2 = r2.cropBitmap(r1, r6, r7)
                                r3[r5] = r2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r6 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap unused = r6.mBottomBitmap = r2
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                r2.addImages(r3, r5)
                            L_0x03f5:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.access$6408(r2)
                                goto L_0x03fe
                            L_0x03fd:
                                r5 = 1
                            L_0x03fe:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r2 = r2.mLastFullBitmap
                                if (r2 == 0) goto L_0x0421
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r2 = r2.mLastFullBitmap
                                boolean r2 = r2.isRecycled()
                                if (r2 != 0) goto L_0x0421
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r2 = r2.mLastFullBitmap
                                r2.recycle()
                            L_0x0421:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r2 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap unused = r2.mLastFullBitmap = r1
                                if (r4 == 0) goto L_0x046b
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                int r1 = r1.mUpdatedCount
                                if (r1 <= 0) goto L_0x045c
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                android.graphics.Bitmap r2 = r1.mBottomBitmap
                                java.lang.String unused = r1.motoEditOnImageAdded(r2)
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                r1.motoEditOnLongScreenShotEnd()
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                com.motorola.systemui.screenshot.LongScreenShotHelper r1 = r1.mLongScreenShotHelper
                                if (r1 == 0) goto L_0x0463
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                com.motorola.systemui.screenshot.LongScreenShotHelper r1 = r1.mLongScreenShotHelper
                                r1.stopLongScreenShot()
                                goto L_0x0463
                            L_0x045c:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                r1.stopLongScreenshot()
                            L_0x0463:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession r0 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.this
                                boolean unused = r0.mIs3rdAppScrolledEnd = r5
                                goto L_0x0472
                            L_0x046b:
                                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$19 r1 = com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.this
                                android.graphics.Rect r0 = r4
                                r1.continueLongScreenShot(r0)
                            L_0x0472:
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.C132219.C13231.run():void");
                        }
                    });
                }

                public void onScreenShotFail() {
                    if (MotoGlobalScreenshot.DEBUG) {
                        Log.d("MotoGlobalScreenshot", "onScreenShotFail");
                    }
                    if (DisplayScreenshotSession.this.mLongScreenShotHelper != null) {
                        DisplayScreenshotSession.this.mLongScreenShotHelper.stopLongScreenShot();
                        DisplayScreenshotSession.this.mBgHandler.post(new Runnable() {
                            public void run() {
                                if (DisplayScreenshotSession.this.mUpdatedCount > 0) {
                                    DisplayScreenshotSession displayScreenshotSession = DisplayScreenshotSession.this;
                                    String unused = displayScreenshotSession.motoEditOnImageAdded(displayScreenshotSession.mBottomBitmap);
                                    DisplayScreenshotSession.this.motoEditOnLongScreenShotEnd();
                                    boolean unused2 = DisplayScreenshotSession.this.mIs3rdAppScrolledEnd = true;
                                    return;
                                }
                                DisplayScreenshotSession.this.stopLongScreenshot();
                            }
                        });
                    }
                }

                public void onLongScreenshotReady(boolean z, Rect rect) {
                    if (MotoGlobalScreenshot.DEBUG) {
                        Log.d("MotoGlobalScreenshot", "onLongScreenshotReady isReady = " + z + "; scrollViewBounds = " + rect);
                    }
                    if (!z) {
                        DisplayScreenshotSession.this.stopLongScreenshot();
                    } else {
                        continueLongScreenShot(rect);
                    }
                }

                /* access modifiers changed from: private */
                public void continueLongScreenShot(Rect rect) {
                    if (DisplayScreenshotSession.this.mLongScreenShotHelper != null) {
                        int unused = DisplayScreenshotSession.this.mLastExpectScrollDistance = rect.height() / 3;
                        if (DisplayScreenshotSession.this.mLastExpectScrollDistance <= 0) {
                            DisplayScreenshotSession.this.stopLongScreenshot();
                        } else {
                            DisplayScreenshotSession.this.mLongScreenShotHelper.continueLongScreenShot(DisplayScreenshotSession.this.mLastExpectScrollDistance);
                        }
                    } else if (MotoGlobalScreenshot.DEBUG) {
                        Log.d("MotoGlobalScreenshot", "continue, LongScreenShot has stopped");
                    }
                }
            };
            this.mOnScrollListener = new LongScreenshotScrollView.OnScrollListener() {
                public void onStopAutoScrolled() {
                }

                public void onScrollToEnd() {
                    if (DisplayScreenshotSession.this.mIs3rdAppScrolledEnd) {
                        DisplayScreenshotSession.this.stopLongScreenshot();
                    }
                }
            };
            this.mScreeenshotEditConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    IMotoScreenShotEditService unused = DisplayScreenshotSession.this.mIMotoScreenShotEditService = IMotoScreenShotEditService.Stub.asInterface(iBinder);
                    DisplayScreenshotSession.this.startTakeLongScreenshot();
                }

                public void onServiceDisconnected(ComponentName componentName) {
                    IMotoScreenShotEditService unused = DisplayScreenshotSession.this.mIMotoScreenShotEditService = null;
                    DisplayScreenshotSession.this.unbindMotoEditService();
                }

                public void onBindingDied(ComponentName componentName) {
                    IMotoScreenShotEditService unused = DisplayScreenshotSession.this.mIMotoScreenShotEditService = null;
                }

                public void onNullBinding(ComponentName componentName) {
                    IMotoScreenShotEditService unused = DisplayScreenshotSession.this.mIMotoScreenShotEditService = null;
                }
            };
            DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
            this.mDisplayManager = displayManager;
            Display display = displayManager.getDisplay(i);
            this.mDisplay = display;
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context.createDisplayContext(display).createWindowContext(2036, (Bundle) null), R$style.Theme_SystemUI);
            this.mContext = contextThemeWrapper;
            this.mWindowManager = (WindowManager) contextThemeWrapper.getSystemService("window");
            this.mDisplayId = i;
            boolean isSupportCli = MotoFeature.getInstance(context).isSupportCli();
            boolean z = true;
            z = (!isSupportCli || i != 1) ? false : z;
            this.mIsCliDisplay = z;
            if (z) {
                this.mCliScreenshot = new CliGlobalScreenshot(contextThemeWrapper, i);
            }
            DisplayMetrics displayMetrics = new DisplayMetrics();
            this.mDisplayMetrics = displayMetrics;
            display.getRealMetrics(displayMetrics);
            this.mDensityScale = displayMetrics.density;
            this.mTouchSlop = ViewConfiguration.get(contextThemeWrapper).getScaledTouchSlop();
            MediaActionSound mediaActionSound = new MediaActionSound();
            this.mCameraSound = mediaActionSound;
            mediaActionSound.load(0);
            this.mNotificationsController = screenshotNotificationsController;
        }

        public void destory() {
            stopLongScreenshot();
        }

        private void onPreviewWindowShow() {
            if (!this.mIsCliDisplay) {
                if (MotoGlobalScreenshot.DEBUG) {
                    Log.d("MotoGlobalScreenshot", "onPreviewWindowShow: initReceiver");
                }
                initReceiver();
            }
        }

        private void onPreviewWindowDismiss() {
            if (MotoGlobalScreenshot.DEBUG) {
                Log.d("MotoGlobalScreenshot", "onPreviewWindowDismiss: removeReceiver");
            }
            try {
                this.mContext.unregisterReceiver(this.mBroadcastReceiver);
            } catch (Exception unused) {
            }
        }

        private void resetNormalLayoutStatus() {
            this.mLastDispatchScreenshotViewX = 0.0f;
            this.mLastDispatchScreenshotViewY = 0.0f;
            this.mSwipToDismissTouchDownX = 0;
            this.mSwipToDismissTouchDownY = 0;
            this.mSwipToDismissLastX = 0;
            this.mInSwipToDismissValidRect = false;
            this.mHandleSwipToDismiss = false;
            this.mSwipToDismissLastXOffset = 0;
            this.mTouchHandleByLongScreenshotScrollView = false;
            this.mLongScreenshotScrollViewContainerTouchDownX = 0;
            this.mLongScreenshotScrollViewContainerTouchDownY = 0;
            this.mSmallScrollInvalidClick = false;
            this.mSwipToDismissVelocityTracker.clear();
            this.mShoudStartEditorAfterSave = false;
        }

        private void initReceiver() {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("com.com.android.systemui.ACTION_HEADS_UP_SHOWING");
            intentFilter.addAction("com.android.systemui.ACTION_GLOBAL_ACTIONS_SHOW");
            this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
        }

        private boolean aspectRatiosMatch(Bitmap bitmap, Insets insets, Rect rect) {
            int width = (bitmap.getWidth() - insets.left) - insets.right;
            int height = (bitmap.getHeight() - insets.top) - insets.bottom;
            if (height == 0 || width == 0 || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
                Log.e("MotoGlobalScreenshot", String.format("Provided bitmap and insets create degenerate region: %dx%d %s", new Object[]{Integer.valueOf(bitmap.getWidth()), Integer.valueOf(bitmap.getHeight()), insets}));
                return false;
            }
            float f = ((float) width) / ((float) height);
            float width2 = ((float) rect.width()) / ((float) rect.height());
            boolean z = Math.abs(f - width2) < 0.1f;
            if (!z) {
                Log.d("MotoGlobalScreenshot", String.format("aspectRatiosMatch: don't match bitmap: %f, bounds: %f", new Object[]{Float.valueOf(f), Float.valueOf(width2)}));
            }
            return z;
        }

        /* access modifiers changed from: package-private */
        public void takeScreenshot(ScreenshotHelper.ScreenshotRequest screenshotRequest) {
            Bitmap bundleToHardwareBitmap = BitmapUtil.bundleToHardwareBitmap(screenshotRequest.getBitmapBundle());
            Bitmap copy = bundleToHardwareBitmap.copy(Bitmap.Config.ARGB_8888, false);
            bundleToHardwareBitmap.recycle();
            Rect boundsInScreen = screenshotRequest.getBoundsInScreen();
            Insets insets = screenshotRequest.getInsets();
            screenshotRequest.getTaskId();
            screenshotRequest.getUserId();
            ComponentName topComponent = screenshotRequest.getTopComponent();
            if (aspectRatiosMatch(copy, insets, boundsInScreen)) {
                takeScreenshot(copy, cropBitmap(copy, insets), boundsInScreen, insets, topComponent != null ? topComponent.getPackageName() : null);
            } else {
                takeScreenshot(copy, (Bitmap) null, new Rect(0, 0, copy.getWidth(), copy.getHeight()), Insets.NONE, topComponent != null ? topComponent.getPackageName() : null);
            }
        }

        private void takeScreenshot(Bitmap bitmap, Bitmap bitmap2, Rect rect, Insets insets, String str) {
            this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
            this.mDisplay.getRealMetrics(this.mDisplayMetrics);
            if (this.mPreviewWindowIsShowing) {
                Log.v("MotoGlobalScreenshot", "ignore take screenshot because the preview is showing");
            } else {
                takeScreenshot(bitmap, bitmap2, 0, str, false);
            }
        }

        /* access modifiers changed from: package-private */
        public void takeScreenshot(int i) {
            boolean z;
            boolean z2 = true;
            this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
            this.mDisplay.getRealMetrics(this.mDisplayMetrics);
            if (!this.mPreviewWindowIsShowing || ScreenshotHelper.isSilentType(i)) {
                String focusedWindowApplicationPackageName = ((ActivityManager) this.mContext.getSystemService("activity")).getFocusedWindowApplicationPackageName();
                try {
                    if (this.mDisplayId == 0) {
                        WindowManagerGlobal.getInstance();
                        z = WindowManagerGlobal.getWindowManagerService().isFocusedWindowRestrict();
                    } else {
                        z = false;
                    }
                    this.mIsFocusedWindowRestrict = z;
                } catch (RemoteException e) {
                    Log.w("MotoGlobalScreenshot", "Unable to check if the focused window is restrict", e);
                }
                if (2 != i) {
                    z2 = false;
                }
                takeScreenshot(z2 ? getFocusedScreenBitmap() : takeScreenBitmap(), (Bitmap) null, i, focusedWindowApplicationPackageName, true);
                return;
            }
            Log.v("MotoGlobalScreenshot", "ignore take screenshot because the preview is showing");
        }

        /* access modifiers changed from: package-private */
        public void takeScreenshot(Bitmap bitmap, Bitmap bitmap2, final int i, String str, boolean z) {
            if (bitmap == null) {
                this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text, str, i);
                return;
            }
            ScreenshotHelperEx.onTakeScreenshotSuccess(this.mContext, str, i);
            if (!isUserSetupComplete() || ScreenshotHelper.isSilentType(i)) {
                saveScreenshotAndToast(bitmap, i, str);
                return;
            }
            onPreviewWindowShow();
            this.mCanPreviewCancelByTouch = i != 3;
            this.mPreviewWindowIsShowing = true;
            this.mLongScreenshotSuccessfully = false;
            this.mScreenshotHandler.post(new C1345xcf891079(this));
            if (this.mIsCliDisplay) {
                this.mCliScreenshot.startAnimation(bitmap);
                saveScreenshotInWorkerThread(bitmap, new ScreenshotController.ActionsReadyListener() {
                    public void onActionsReady(ScreenshotController.SavedImageData savedImageData) {
                        boolean unused = DisplayScreenshotSession.this.mPreviewWindowIsShowing = false;
                    }
                }, str);
                return;
            }
            if (bitmap2 == null) {
                bitmap2 = bitmap.copy(bitmap.getConfig(), false);
            }
            this.mFirstScreenBitmapWidth = bitmap2.getWidth();
            this.mFirstScreenBitmapHeight = bitmap2.getHeight();
            reloadLayout(bitmap2, z);
            saveScreenshotInWorkerThread(bitmap, new ScreenshotController.ActionsReadyListener() {
                public void onActionsReady(ScreenshotController.SavedImageData savedImageData) {
                    DisplayScreenshotSession.this.onFirstScreenshotSaved(savedImageData, i);
                }
            }, str);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$takeScreenshot$0() {
            this.mCameraSound.play(0);
        }

        /* access modifiers changed from: package-private */
        public void dismissScreenshotPreview(String str, boolean z) {
            View peekDecorView;
            int i;
            Log.v("MotoGlobalScreenshot", "clearing screenshot: " + str + "; is showing: " + this.mPreviewWindowIsShowing);
            if (this.mPreviewWindowIsShowing) {
                PhoneWindow phoneWindow = this.mWindow;
                if (phoneWindow == null || (peekDecorView = phoneWindow.peekDecorView()) == null || !peekDecorView.isAttachedToWindow()) {
                    Log.d("MotoGlobalScreenshot", "try to dismiss screenshot preview but the window is not attached");
                    return;
                }
                this.mUpdatedCount = 0;
                this.mLongScreenshotSuccessfully = false;
                this.mFirstScreenBitmapWidth = 0;
                this.mFirstScreenBitmapHeight = 0;
                this.mLastSavedImageData = null;
                SoftReference<int[]> softReference = this.mPixelsCache;
                if (softReference != null) {
                    softReference.clear();
                    this.mPixelsCache = null;
                }
                Dialog dialog = this.mDeleteDialog;
                if (dialog != null && dialog.isShowing()) {
                    this.mDeleteDialog.dismiss();
                    this.mDeleteDialog = null;
                }
                stopLongScreenshot();
                motoEditOnExitScreenShot();
                Handler handler = this.mBgHandler;
                if (handler != null) {
                    handler.getLooper().quitSafely();
                    if (this.mBgHandlerThread.isAlive()) {
                        this.mBgHandlerThread.interrupt();
                    }
                }
                this.mScreenshotHandler.removeCallbacksAndMessages((Object) null);
                onPreviewWindowDismiss();
                if (z) {
                    removeWindow();
                    this.mPreviewWindowIsShowing = false;
                    return;
                }
                if (this.mMotoScreenshotRootView.isLayoutRtl()) {
                    i = -(this.mMotoScreenshotRootView.getRight() - this.mToolbarLayout.getLeft());
                } else {
                    i = this.mMotoScreenshotRootView.getLeft() + this.mToolbarLayout.getWidth();
                }
                TranslateAnimation translateAnimation = new TranslateAnimation((float) this.mSwipToDismissLastXOffset, (float) (-i), 0.0f, 0.0f);
                translateAnimation.setDuration(200);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        DisplayScreenshotSession.this.removeWindow();
                        boolean unused = DisplayScreenshotSession.this.mPreviewWindowIsShowing = false;
                    }
                });
                this.mMotoScreenshotRootView.startAnimation(translateAnimation);
            }
        }

        private void saveScreenshotAndToast(Bitmap bitmap, final int i, final String str) {
            if (!ScreenshotHelper.isSilentType(i)) {
                this.mPermanentHandler.post(new C1340xcf891074(this));
            }
            saveScreenshotInWorkerThread(bitmap, new ScreenshotController.ActionsReadyListener() {
                public void onActionsReady(ScreenshotController.SavedImageData savedImageData) {
                    if (savedImageData.uri == null) {
                        DisplayScreenshotSession.this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text, str, i);
                    } else {
                        DisplayScreenshotSession.this.mPermanentHandler.post(new C1359x974e7e4d(this, i, savedImageData, str));
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onActionsReady$0(int i, ScreenshotController.SavedImageData savedImageData, String str) {
                    if (!ScreenshotHelper.isSilentType(i)) {
                        Toast.makeText(DisplayScreenshotSession.this.mContext, R$string.screenshot_saved_title, 0).show();
                    }
                    ScreenshotHelperEx.onScreenshotFinished(DisplayScreenshotSession.this.mContext, true, savedImageData.uri, str, i);
                }
            }, str);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$saveScreenshotAndToast$1() {
            this.mCameraSound.play(0);
        }

        private Supplier<ScreenshotController.SavedImageData.ActionTransition> getActionTransitionSupplier() {
            return C1349xcf89107d.INSTANCE;
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ ScreenshotController.SavedImageData.ActionTransition lambda$getActionTransitionSupplier$2() {
            return new ScreenshotController.SavedImageData.ActionTransition();
        }

        /* access modifiers changed from: private */
        public void saveScreenshotInWorkerThread(Bitmap bitmap, ScreenshotController.ActionsReadyListener actionsReadyListener, String str) {
            ScreenshotController.SaveImageInBackgroundData saveImageInBackgroundData = new ScreenshotController.SaveImageInBackgroundData();
            saveImageInBackgroundData.image = bitmap;
            saveImageInBackgroundData.focusedAppPackageName = str;
            saveImageInBackgroundData.finisher = C1348xcf89107c.INSTANCE;
            saveImageInBackgroundData.mActionsReadyListener = actionsReadyListener;
            synchronized (this.mSaveInBgTasks) {
                if (this.mSaveInBgTasks.size() >= 5) {
                    this.mSaveInBgTasks.remove(0).setActionsReadyListener(new ScreenshotController.ActionsReadyListener() {
                        public void onActionsReady(ScreenshotController.SavedImageData savedImageData) {
                        }
                    });
                }
                SaveImageInBackgroundTask saveImageInBackgroundTask = new SaveImageInBackgroundTask(this.mContext, MotoGlobalScreenshot.this.mImageExporter, MotoGlobalScreenshot.this.mScreenshotSmartActions, saveImageInBackgroundData, getActionTransitionSupplier());
                this.mSaveInBgTasks.add(saveImageInBackgroundTask);
                saveImageInBackgroundTask.execute(new Void[0]);
            }
        }

        private Bitmap getFocusedScreenBitmap() {
            this.mDisplay.getRotation();
            Rect focusedWindowRect = this.mWindowManager.getFocusedWindowRect(this.mDisplay.getDisplayId());
            if (focusedWindowRect != null && !focusedWindowRect.isEmpty()) {
                return takeScreenshotInternal(focusedWindowRect);
            }
            Log.w("MotoGlobalScreenshot", "getFocusedWindowRect failed.");
            return null;
        }

        /* access modifiers changed from: private */
        public Bitmap takeScreenBitmap() {
            DisplayMetrics displayMetrics = this.mDisplayMetrics;
            Rect rect = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            int rotation = this.mDisplay.getRotation();
            if (this.mIsFocusedWindowRestrict && rotation == 0) {
                Rect waterfallRect = ScreenshotController.getWaterfallRect(this.mContext.getResources());
                rect.left += waterfallRect.left;
                rect.top += waterfallRect.top;
                rect.right -= waterfallRect.right;
                rect.bottom -= waterfallRect.bottom;
            }
            rect.width();
            rect.height();
            return takeScreenshotInternal(rect);
        }

        private IBinder getVistualDisplayToken(int i) {
            return this.mDisplayManager.getDisplayToken(i);
        }

        private Bitmap takeScreenshotInternal(Rect rect) {
            IBinder iBinder;
            Bitmap bitmap;
            new Rect(rect);
            int width = rect.width();
            int height = rect.height();
            if (this.mDisplay.getAddress() instanceof DisplayAddress.Physical) {
                iBinder = SurfaceControl.getPhysicalDisplayToken(this.mDisplay.getAddress().getPhysicalDisplayId());
            } else {
                iBinder = getVistualDisplayToken(this.mDisplay.getDisplayId());
            }
            if (iBinder == null) {
                Log.w("MotoGlobalScreenshot", "Failed to take screenshot because the display is disconnected: " + this.mDisplayId);
                return null;
            }
            SurfaceControl.ScreenshotHardwareBuffer captureDisplay = SurfaceControl.captureDisplay(new SurfaceControl.DisplayCaptureArgs.Builder(iBinder).setSourceCrop(rect).setSize(width, height).build());
            if (captureDisplay == null) {
                bitmap = null;
            } else {
                bitmap = captureDisplay.asBitmap();
            }
            if (bitmap == null) {
                Log.e("MotoGlobalScreenshot", "takeScreenshotInternal: Screenshot bitmap was null");
                return null;
            }
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            bitmap.recycle();
            return copy;
        }

        private boolean isUserSetupComplete() {
            return Settings.Secure.getInt(this.mContext.getContentResolver(), "user_setup_complete", 0) == 1;
        }

        /* access modifiers changed from: private */
        public void updateContentLayout(int i, int i2) {
            View view = this.mLongScreenshotScrollViewContainer;
            if (view != null && view.getWidth() > 0) {
                ViewGroup.LayoutParams layoutParams = this.mLongScreenshotScrollView.getLayoutParams();
                int width = this.mLongScreenshotScrollView.getWidth();
                layoutParams.width = width;
                int i3 = (int) (((float) i2) * (((float) width) / ((float) i)));
                layoutParams.height = i3;
                this.mLongScreenshotScrollViewHeight = i3;
                this.mLongScreenshotScrollView.setLayoutParams(layoutParams);
                int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R$dimen.screenshot_scrollframe_max_height);
                if (dimensionPixelSize <= 0) {
                    dimensionPixelSize = (this.mLongScreenshotScrollView.getTop() + this.mLongScreenshotScrollView.getHeight()) - this.mContext.getResources().getDimensionPixelSize(17105533);
                }
                this.mLongScreenshotScrollView.setMaxShowingHeight(dimensionPixelSize);
            }
        }

        private void reloadLayout(Bitmap bitmap, boolean z) {
            resetNormalLayoutStatus();
            View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.zz_moto_global_screenshot, (ViewGroup) null);
            this.mScreenshotWindow = inflate;
            MotoScreenshotRootView motoScreenshotRootView = (MotoScreenshotRootView) inflate.findViewById(R$id.screenshot_root);
            this.mMotoScreenshotRootView = motoScreenshotRootView;
            this.mLongScreenshotScrollView = (LongScreenshotScrollView) motoScreenshotRootView.findViewById(R$id.long_screenshot_scroll);
            this.mToolbarLayout = this.mMotoScreenshotRootView.findViewById(R$id.toolbar);
            this.mLensButton = this.mMotoScreenshotRootView.findViewById(R$id.screenshot_lens_button);
            this.mCoverView = this.mMotoScreenshotRootView.findViewById(R$id.screenshot_cover_view);
            this.mLongScreenshotProcessView = this.mMotoScreenshotRootView.findViewById(R$id.long_screenshot_process);
            this.mLongScreenshotStartButton = this.mMotoScreenshotRootView.findViewById(R$id.long_screenshot_start_button);
            this.mLongScreenshotStopButton = this.mMotoScreenshotRootView.findViewById(R$id.long_screenshot_stop_button);
            this.mLongScreenshotScrollViewContainer = this.mMotoScreenshotRootView.findViewById(R$id.scroll_frame);
            this.mLensButton.setOnClickListener(new C1352x6b1d73f(this));
            this.mLongScreenshotStartButton.setOnClickListener(new C1354x6b1d741(this));
            this.mLongScreenshotStopButton.setOnClickListener(new C1350x6b1d73d(this));
            this.mLongScreenshotScrollView.setOnClickListener(new C1339x6b1d73c(this));
            this.mMotoScreenshotRootView.findViewById(R$id.screenshot_draw_button).setOnClickListener(new C1351x6b1d73e(this));
            this.mMotoScreenshotRootView.findViewById(R$id.screenshot_share_button).setOnClickListener(new C1355x6b1d742(this));
            this.mMotoScreenshotRootView.findViewById(R$id.screenshot_delete_button).setOnClickListener(new C1353x6b1d740(this));
            this.mLensButton.setVisibility(LensRouterActivity.isLensAvailable(this.mContext) ? 0 : 8);
            this.mToolbarLayout.setEnabled(false);
            this.mLongScreenshotScrollView.setTouchScrollEnable(false);
            this.mLongScreenshotScrollView.enableOutlineProvider();
            this.mLongScreenshotScrollView.setOnTouchStatusListener(this.mOnTouchStatusListener);
            this.mLongScreenshotScrollViewContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    if (i != i5 || i2 != i6 || i3 != i7 || i4 != i8) {
                        DisplayScreenshotSession.this.mLongScreenshotScrollViewContainer.removeOnLayoutChangeListener(this);
                        DisplayScreenshotSession.this.mScreenshotHandler.post(new Runnable() {
                            public void run() {
                                if (DisplayScreenshotSession.this.mFirstScreenBitmapWidth > 0) {
                                    DisplayScreenshotSession displayScreenshotSession = DisplayScreenshotSession.this;
                                    displayScreenshotSession.updateContentLayout(displayScreenshotSession.mFirstScreenBitmapWidth, DisplayScreenshotSession.this.mFirstScreenBitmapHeight);
                                    DisplayScreenshotSession.this.startEntryScreenAnim();
                                }
                            }
                        });
                    }
                }
            });
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 0, 0, 2036, 17302816, -3);
            this.mWindowLayoutParams = layoutParams;
            layoutParams.setTitle("Screenshot display: " + this.mDisplayId);
            WindowManager.LayoutParams layoutParams2 = this.mWindowLayoutParams;
            layoutParams2.privateFlags = layoutParams2.privateFlags | 1048576;
            layoutParams2.gravity = 48;
            layoutParams2.layoutInDisplayCutoutMode = 3;
            PhoneWindow phoneWindow = new PhoneWindow(this.mContext);
            this.mWindow = phoneWindow;
            phoneWindow.setWindowManager(this.mWindowManager, (IBinder) null, (String) null);
            this.mWindow.requestFeature(1);
            this.mWindow.requestFeature(13);
            this.mWindow.setBackgroundDrawableResource(17170445);
            int rotation = this.mDisplay.getRotation();
            if (rotation == 3 || rotation == 1) {
                this.mWindowLayoutParams.screenOrientation = 11;
                this.mMotoScreenshotRootView.setIsLandscape(true);
            } else {
                this.mWindowLayoutParams.screenOrientation = 12;
            }
            this.mLongScreenshotScrollView.addBitmap(bitmap);
            this.mLongScreenshotScrollView.setVisibility(0);
            enableButtons(false);
            this.mMotoScreenshotRootView.setOnTouchListener(this.mMotoScreenshotRootViewOnTouchListener);
            this.mMotoScreenshotRootView.setOnInterceptTouchEventListener(this.mMotoScreenshotRootViewInterceptTouchEventListener);
            if (this.mDisplayId == 0 && z) {
                LongScreenShotHelper longScreenShotHelper = new LongScreenShotHelper(this.mContext);
                this.mLongScreenShotHelper = longScreenShotHelper;
                longScreenShotHelper.startLongScreeenShot(new LongScreenShotHelper.OnLongScreenshotListener() {
                    public void onLongScreenshotReady(boolean z, Rect rect) {
                        boolean access$2100 = DisplayScreenshotSession.this.isInMultiWindow();
                        Log.d("MotoGlobalScreenshot", "onLongScreenshotReady ability = " + z + " isMultiWindow = " + access$2100);
                        if (z && !access$2100) {
                            DisplayScreenshotSession.this.initLongScreenshot();
                        }
                        DisplayScreenshotSession.this.mLongScreenshotStartButton.post(new C1360x4341b090(this, z, access$2100));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onLongScreenshotReady$0(boolean z, boolean z2) {
                        DisplayScreenshotSession.this.mLongScreenshotStartButton.setVisibility((!z || z2) ? 8 : 0);
                    }
                });
            }
            this.mWindow.setContentView(this.mScreenshotWindow);
            attachWindow();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$4(View view) {
            startLens();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$5(View view) {
            handleStartLongScreenshot();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$6(View view) {
            handleStopLongScreenshot();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$7(View view) {
            startEditorAfterSave();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$8(View view) {
            startEditor();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$9(View view) {
            startShare();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reloadLayout$10(View view) {
            showDeleteConfirmDialog();
        }

        private void attachWindow() {
            View decorView = this.mWindow.getDecorView();
            if (!decorView.isAttachedToWindow()) {
                if (MotoGlobalScreenshot.DEBUG) {
                    Log.d("MotoGlobalScreenshot", "attachWindow");
                }
                this.mWindowManager.addView(decorView, this.mWindowLayoutParams);
                decorView.requestApplyInsets();
            }
        }

        /* access modifiers changed from: private */
        public boolean isInMultiWindow() {
            try {
                ActivityTaskManager.RootTaskInfo focusedRootTaskInfo = ActivityTaskManager.getService().getFocusedRootTaskInfo();
                if (focusedRootTaskInfo == null) {
                    return false;
                }
                if (focusedRootTaskInfo.getWindowingMode() == 3 || focusedRootTaskInfo.getWindowingMode() == 4 || focusedRootTaskInfo.getWindowingMode() == 6) {
                    return true;
                }
                return false;
            } catch (Exception unused) {
                Log.d("MotoGlobalScreenshot", "failed to check for multi-window state");
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        public void removeWindow() {
            View peekDecorView = this.mWindow.peekDecorView();
            if (peekDecorView != null && peekDecorView.isAttachedToWindow()) {
                if (MotoGlobalScreenshot.DEBUG) {
                    Log.d("MotoGlobalScreenshot", "Removing screenshot window");
                }
                this.mWindowManager.removeViewImmediate(peekDecorView);
            }
        }

        /* access modifiers changed from: private */
        public void startEntryScreenAnim() {
            this.mCoverView.setVisibility(0);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 0.6f);
            alphaAnimation.setDuration(250);
            alphaAnimation.setRepeatCount(1);
            alphaAnimation.setRepeatMode(2);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    DisplayScreenshotSession.this.mCoverView.setVisibility(8);
                }
            });
            this.mCoverView.startAnimation(alphaAnimation);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.26f, 1.0f, 1.26f, 1.0f, (float) this.mLongScreenshotScrollViewContainer.getLeft(), (float) (this.mLongScreenshotScrollViewContainer.getTop() + this.mLongScreenshotScrollViewContainer.getHeight()));
            scaleAnimation.setDuration(250);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    DisplayScreenshotSession.this.mToolbarLayout.setVisibility(0);
                }
            });
            this.mMotoScreenshotRootView.startAnimation(scaleAnimation);
        }

        private void enableButtons(boolean z) {
            int[] iArr = {R$id.screenshot_lens_button, R$id.screenshot_draw_button, R$id.screenshot_share_button, R$id.screenshot_delete_button, R$id.long_screenshot_start_button};
            float f = z ? 1.0f : 0.5f;
            for (int i = 0; i < 5; i++) {
                View findViewById = this.mMotoScreenshotRootView.findViewById(iArr[i]);
                findViewById.setEnabled(z);
                findViewById.setAlpha(f);
            }
        }

        /* access modifiers changed from: private */
        public void delScreenshotFile() {
            ScreenshotController.SavedImageData savedImageData = this.mLastSavedImageData;
            if (savedImageData != null) {
                try {
                    savedImageData.deleteAction.actionIntent.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /* access modifiers changed from: private */
        public void onFirstScreenshotSaved(ScreenshotController.SavedImageData savedImageData, int i) {
            if (savedImageData != null && savedImageData.uri == null) {
                this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
                this.mScreenshotHandler.post(new C1343xcf891077(this));
            } else if (!this.mPreviewWindowIsShowing) {
                this.mPermanentHandler.post(new C1346xcf89107a(this));
            } else {
                this.mLastSavedImageData = savedImageData;
                this.mScreenshotHandler.post(new C1347xcf89107b(this, i));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onFirstScreenshotSaved$11() {
            dismissScreenshotPreview("error", true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onFirstScreenshotSaved$12() {
            Toast.makeText(this.mContext, R$string.screenshot_saved_title, 0).show();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onFirstScreenshotSaved$13(int i) {
            if (i == 3) {
                if (MotoGlobalScreenshot.DEBUG) {
                    Log.d("MotoGlobalScreenshot", "start edit because extra type = " + i);
                }
                startEditor();
            } else if (this.mShoudStartEditorAfterSave) {
                startEditor();
            } else {
                this.mToolbarLayout.setVisibility(0);
                this.mToolbarLayout.setEnabled(true);
                enableButtons(true);
                startTimeoutQuitIfNeed();
            }
        }

        /* access modifiers changed from: private */
        public void onLongScreenshotFinalBitmapSaved(ScreenshotController.SavedImageData savedImageData) {
            if (savedImageData != null && savedImageData.uri == null) {
                this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
                this.mScreenshotHandler.post(new C1344xcf891078(this));
            } else if (!this.mPreviewWindowIsShowing) {
                this.mPermanentHandler.post(new C1357x6b1d744(this));
            } else {
                this.mLastSavedImageData = savedImageData;
                this.mScreenshotHandler.post(new C1342xcf891076(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onLongScreenshotFinalBitmapSaved$14() {
            dismissScreenshotPreview("error long", true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onLongScreenshotFinalBitmapSaved$15() {
            Toast.makeText(this.mContext, R$string.screenshot_saved_title, 0).show();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onLongScreenshotFinalBitmapSaved$16() {
            this.mLongScreenshotProcessView.setVisibility(8);
            this.mToolbarLayout.setVisibility(0);
            this.mToolbarLayout.setEnabled(true);
            enableButtons(true);
            startTimeoutQuitIfNeed();
        }

        /* access modifiers changed from: private */
        public boolean isToolbarEnabled() {
            View view = this.mToolbarLayout;
            return view != null && view.getVisibility() == 0 && this.mToolbarLayout.isEnabled();
        }

        /* access modifiers changed from: private */
        public void startTimeoutQuitIfNeed() {
            stopTimeoutQuit();
            if (isToolbarEnabled()) {
                this.mScreenshotHandler.postDelayed(this.mTimeoutQuitRunnable, 4000);
            }
        }

        /* access modifiers changed from: private */
        public void stopTimeoutQuit() {
            this.mScreenshotHandler.removeCallbacks(this.mTimeoutQuitRunnable);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$17(int i) {
            if (i == 0) {
                stopTimeoutQuit();
            }
        }

        /* access modifiers changed from: protected */
        public float getEscapeVelocity() {
            return this.mDensityScale * 500.0f;
        }

        private float getMaxVelocity() {
            return this.mDensityScale * 4000.0f;
        }

        private boolean shouldSwipToQuitScreenshot() {
            float xVelocity = this.mSwipToDismissVelocityTracker.getXVelocity();
            if (this.mMotoScreenshotRootView.isLayoutRtl()) {
                int left = this.mToolbarLayout.getLeft();
                if (this.mSwipToDismissLastXOffset > (this.mLongScreenshotScrollViewContainer.getRight() - left) / 2 || xVelocity > getEscapeVelocity()) {
                    return true;
                }
                return false;
            }
            int left2 = this.mLongScreenshotScrollViewContainer.getLeft();
            if (this.mSwipToDismissLastXOffset < (-(this.mToolbarLayout.getRight() - left2)) / 2 || xVelocity < (-getEscapeVelocity())) {
                return true;
            }
            return false;
        }

        private void animToOriginPosition() {
            TranslateAnimation translateAnimation = new TranslateAnimation((float) this.mSwipToDismissLastXOffset, 0.0f, 0.0f, 0.0f);
            translateAnimation.setInterpolator(new AccelerateInterpolator());
            translateAnimation.setDuration(100);
            translateAnimation.setFillAfter(false);
            this.mMotoScreenshotRootView.startAnimation(translateAnimation);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0013, code lost:
            if (r1 != 3) goto L_0x006f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean handleSwipToDismissTouch(android.view.MotionEvent r8) {
            /*
                r7 = this;
                float r0 = r8.getX()
                int r0 = (int) r0
                int r1 = r8.getAction()
                r2 = 1
                r3 = 0
                if (r1 == 0) goto L_0x006d
                if (r1 == r2) goto L_0x004a
                r4 = 2
                if (r1 == r4) goto L_0x0016
                r8 = 3
                if (r1 == r8) goto L_0x004a
                goto L_0x006f
            L_0x0016:
                int r1 = r7.mSwipToDismissTouchDownX
                int r1 = r0 - r1
                com.motorola.systemui.screenshot.MotoScreenshotRootView r3 = r7.mMotoScreenshotRootView
                boolean r3 = r3.isLayoutRtl()
                if (r3 == 0) goto L_0x0027
                if (r1 >= 0) goto L_0x002b
                int r1 = r1 / 10
                goto L_0x002b
            L_0x0027:
                if (r1 <= 0) goto L_0x002b
                int r1 = r1 / 10
            L_0x002b:
                android.view.animation.TranslateAnimation r3 = new android.view.animation.TranslateAnimation
                int r4 = r7.mSwipToDismissLastXOffset
                float r4 = (float) r4
                float r5 = (float) r1
                r6 = 0
                r3.<init>(r4, r5, r6, r6)
                r4 = 0
                r3.setDuration(r4)
                r3.setFillAfter(r2)
                com.motorola.systemui.screenshot.MotoScreenshotRootView r4 = r7.mMotoScreenshotRootView
                r4.startAnimation(r3)
                r7.mSwipToDismissLastXOffset = r1
                android.view.VelocityTracker r1 = r7.mSwipToDismissVelocityTracker
                r1.addMovement(r8)
                goto L_0x006f
            L_0x004a:
                android.view.VelocityTracker r8 = r7.mSwipToDismissVelocityTracker
                r1 = 1000(0x3e8, float:1.401E-42)
                float r4 = r7.getMaxVelocity()
                r8.computeCurrentVelocity(r1, r4)
                boolean r8 = r7.shouldSwipToQuitScreenshot()
                if (r8 == 0) goto L_0x0062
                java.lang.String r8 = "user swip"
                r7.dismissScreenshotPreview(r8, r3)
                goto L_0x006a
            L_0x0062:
                r7.startTimeoutQuitIfNeed()
                r7.animToOriginPosition()
                r7.mSwipToDismissLastXOffset = r3
            L_0x006a:
                r7.mHandleSwipToDismiss = r3
                goto L_0x006f
            L_0x006d:
                r7.mSwipToDismissLastXOffset = r3
            L_0x006f:
                r7.mSwipToDismissLastX = r0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.handleSwipToDismissTouch(android.view.MotionEvent):boolean");
        }

        private void startActionTransition(ScreenshotController.SavedImageData.ActionTransition actionTransition) {
            try {
                actionTransition.action.actionIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Runnable runnable = actionTransition.onCancelRunnable;
                if (runnable != null) {
                    runnable.run();
                }
                Log.e("MotoGlobalScreenshot", "startActionTransition Intent cancelled", e);
            }
        }

        private void startEditorAfterSave() {
            if (isToolbarEnabled()) {
                startEditor();
            } else {
                this.mShoudStartEditorAfterSave = true;
            }
        }

        /* access modifiers changed from: private */
        public void startEditor() {
            if (this.mLastSavedImageData != null) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                if (this.mUpdatedCount <= 0 || !this.mLongScreenshotSuccessfully || !startMotoScreenshotEdit()) {
                    startActionTransition(this.mLastSavedImageData.editTransition.get());
                }
                this.mShoudStartEditorAfterSave = false;
                dismissScreenshotPreview("start editor", true);
            }
        }

        private void startShare() {
            if (this.mLastSavedImageData != null) {
                try {
                    ActivityManager.getService().resumeAppSwitches();
                } catch (RemoteException unused) {
                }
                startActionTransition(this.mLastSavedImageData.shareTransition.get());
                dismissScreenshotPreview("start share", true);
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(5:2|3|4|5|10) */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x001c, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x001d, code lost:
            r2.printStackTrace();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000b */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void startLens() {
            /*
                r2 = this;
                com.android.systemui.screenshot.ScreenshotController$SavedImageData r0 = r2.mLastSavedImageData
                if (r0 == 0) goto L_0x0020
                android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x000b }
                r0.resumeAppSwitches()     // Catch:{ RemoteException -> 0x000b }
            L_0x000b:
                com.android.systemui.screenshot.ScreenshotController$SavedImageData r0 = r2.mLastSavedImageData     // Catch:{ CanceledException -> 0x001c }
                android.app.Notification$Action r0 = r0.googleLensAction     // Catch:{ CanceledException -> 0x001c }
                android.app.PendingIntent r0 = r0.actionIntent     // Catch:{ CanceledException -> 0x001c }
                r0.send()     // Catch:{ CanceledException -> 0x001c }
                java.lang.String r0 = "start share"
                r1 = 1
                r2.dismissScreenshotPreview(r0, r1)     // Catch:{ CanceledException -> 0x001c }
                goto L_0x0020
            L_0x001c:
                r2 = move-exception
                r2.printStackTrace()
            L_0x0020:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.startLens():void");
        }

        private void showDeleteConfirmDialog() {
            stopTimeoutQuit();
            Dialog dialog = this.mDeleteDialog;
            if (dialog == null || !dialog.isShowing()) {
                AlertDialog create = new AlertDialog.Builder(this.mContext).setMessage(R$string.screenshot_delete_dialog_msg).setNegativeButton(17039369, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog unused = DisplayScreenshotSession.this.mDeleteDialog = null;
                        DisplayScreenshotSession.this.startTimeoutQuitIfNeed();
                    }
                }).setPositiveButton(17039379, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog unused = DisplayScreenshotSession.this.mDeleteDialog = null;
                        DisplayScreenshotSession.this.delScreenshotFile();
                        DisplayScreenshotSession.this.dismissScreenshotPreview("user del image", false);
                    }
                }).create();
                this.mDeleteDialog = create;
                create.setOnCancelListener(new C1338x6b1d73b(this));
                Window window = this.mDeleteDialog.getWindow();
                window.setType(2036);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 0, 0, 2036, 17302816, -3);
                layoutParams.setTitle("Screenshot delete dialog: " + this.mDisplayId);
                layoutParams.privateFlags = layoutParams.privateFlags | 1048576;
                layoutParams.gravity = 17;
                layoutParams.layoutInDisplayCutoutMode = 3;
                window.setAttributes(layoutParams);
                this.mDeleteDialog.show();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showDeleteConfirmDialog$18(DialogInterface dialogInterface) {
            this.mDeleteDialog = null;
            startTimeoutQuitIfNeed();
        }

        private void handleStartLongScreenshot() {
            synchronized (this.mLongScreenshotStartedLock) {
                this.mLongScreenshotStartedLock = Boolean.TRUE;
            }
            this.mToolbarLayout.setEnabled(false);
            enableButtons(false);
            stopTimeoutQuit();
            this.mLongScreenshotStartButton.setVisibility(8);
            this.mLongScreenshotStopButton.setVisibility(0);
            bindMotoEditService();
        }

        /* access modifiers changed from: private */
        public void handleStopLongScreenshot() {
            stopLongScreenshot();
        }

        /* access modifiers changed from: private */
        public void handleStopAutoScrolled() {
            this.mLongScreenshotScrollView.stopAutoScrollAnim();
            this.mLongScreenshotStopButton.setVisibility(8);
        }

        /* access modifiers changed from: private */
        public void handleSaveBitmapTips(boolean z) {
            int i = 8;
            this.mLongScreenshotStopButton.setVisibility(8);
            View view = this.mLongScreenshotProcessView;
            if (z) {
                i = 0;
            }
            view.setVisibility(i);
        }

        /* access modifiers changed from: private */
        public void initLongScreenshot() {
            this.mMotoEditIndex = 0;
            this.mMotoEditSessionId = -1;
            this.mMotoEditOnLSShotEnded = false;
            this.mLastExpectScrollDistance = -1;
            this.mUpdatedCount = 0;
            this.mLastCropBottom = 0;
            this.mSeledPossibleDistanceIndex = -1;
            this.mIs3rdAppScrolledEnd = false;
            synchronized (this.mLongScreenshotStartedLock) {
                this.mLongScreenshotStartedLock = Boolean.FALSE;
            }
            HandlerThread handlerThread = new HandlerThread("long_screenshot");
            this.mBgHandlerThread = handlerThread;
            handlerThread.start();
            this.mBgHandler = new Handler(this.mBgHandlerThread.getLooper());
            this.mLongScreenshotScrollView.setOnScrollListener(this.mOnScrollListener);
            this.mBitmapOffsetCalculate = new BitmapOffsetCalculate();
            this.mExpectCompareHeight = getExpectCompareHeight();
        }

        private int getExpectCompareHeight() {
            TypedValue typedValue = new TypedValue();
            this.mContext.getTheme().resolveAttribute(16842829, typedValue, true);
            return (int) typedValue.getDimension(this.mDisplayMetrics);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
            r0 = r4.mLongScreenShotHelper;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
            if (r0 == null) goto L_0x0085;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
            r0.stopLongScreenShot();
            r4.mLongScreenShotHelper = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0027, code lost:
            if (r4.mProcessObserver == null) goto L_0x0032;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
            android.app.ActivityManager.getService().unregisterProcessObserver(r4.mProcessObserver);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0034, code lost:
            if (r4.mTaskListener == null) goto L_0x003f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0036, code lost:
            android.app.ActivityTaskManager.getService().unregisterTaskStackListener(r4.mTaskListener);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x003f, code lost:
            r4.mProcessObserver = null;
            r4.mTaskListener = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0044, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x0045, code lost:
            android.util.Log.e("MotoGlobalScreenshot", "Can't unregister activity monitoring :" + r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0089, code lost:
            if (com.android.systemui.screenshot.MotoGlobalScreenshot.access$200() == false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x008b, code lost:
            android.util.Log.d("MotoGlobalScreenshot", "stopLongScreenshot has triggered.");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void stopLongScreenshot() {
            /*
                r4 = this;
                java.lang.Boolean r0 = r4.mLongScreenshotStartedLock
                monitor-enter(r0)
                java.lang.Boolean r1 = r4.mLongScreenshotStartedLock     // Catch:{ all -> 0x0094 }
                boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x0094 }
                r2 = 0
                if (r1 != 0) goto L_0x0017
                com.motorola.systemui.screenshot.LongScreenShotHelper r1 = r4.mLongScreenShotHelper     // Catch:{ all -> 0x0094 }
                if (r1 == 0) goto L_0x0015
                r1.stopLongScreenShot()     // Catch:{ all -> 0x0094 }
                r4.mLongScreenShotHelper = r2     // Catch:{ all -> 0x0094 }
            L_0x0015:
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                return
            L_0x0017:
                java.lang.Boolean r1 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x0094 }
                r4.mLongScreenshotStartedLock = r1     // Catch:{ all -> 0x0094 }
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                com.motorola.systemui.screenshot.LongScreenShotHelper r0 = r4.mLongScreenShotHelper
                if (r0 == 0) goto L_0x0085
                r0.stopLongScreenShot()
                r4.mLongScreenShotHelper = r2
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$ProcessObserver r0 = r4.mProcessObserver     // Catch:{ RemoteException -> 0x0044 }
                if (r0 == 0) goto L_0x0032
                android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x0044 }
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$ProcessObserver r1 = r4.mProcessObserver     // Catch:{ RemoteException -> 0x0044 }
                r0.unregisterProcessObserver(r1)     // Catch:{ RemoteException -> 0x0044 }
            L_0x0032:
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$MyTaskStackListener r0 = r4.mTaskListener     // Catch:{ RemoteException -> 0x0044 }
                if (r0 == 0) goto L_0x003f
                android.app.IActivityTaskManager r0 = android.app.ActivityTaskManager.getService()     // Catch:{ RemoteException -> 0x0044 }
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$MyTaskStackListener r1 = r4.mTaskListener     // Catch:{ RemoteException -> 0x0044 }
                r0.unregisterTaskStackListener(r1)     // Catch:{ RemoteException -> 0x0044 }
            L_0x003f:
                r4.mProcessObserver = r2     // Catch:{ RemoteException -> 0x0044 }
                r4.mTaskListener = r2     // Catch:{ RemoteException -> 0x0044 }
                goto L_0x005b
            L_0x0044:
                r0 = move-exception
                java.lang.String r1 = "MotoGlobalScreenshot"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Can't unregister activity monitoring :"
                r2.append(r3)
                r2.append(r0)
                java.lang.String r0 = r2.toString()
                android.util.Log.e(r1, r0)
            L_0x005b:
                android.os.Handler r0 = r4.mBgHandler
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$17 r1 = new com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$17
                r1.<init>()
                r0.post(r1)
                android.os.Handler r0 = r4.mScreenshotHandler
                r1 = 1
                r0.sendEmptyMessage(r1)
                int r0 = r4.mUpdatedCount
                if (r0 <= 0) goto L_0x007a
                android.os.Handler r0 = r4.mBgHandler
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$18 r1 = new com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$18
                r1.<init>()
                r0.post(r1)
                goto L_0x0084
            L_0x007a:
                android.os.Handler r0 = r4.mScreenshotHandler
                com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$$ExternalSyntheticLambda11 r1 = new com.android.systemui.screenshot.MotoGlobalScreenshot$DisplayScreenshotSession$$ExternalSyntheticLambda11
                r1.<init>(r4)
                r0.post(r1)
            L_0x0084:
                return
            L_0x0085:
                boolean r4 = com.android.systemui.screenshot.MotoGlobalScreenshot.DEBUG
                if (r4 == 0) goto L_0x0093
                java.lang.String r4 = "MotoGlobalScreenshot"
                java.lang.String r0 = "stopLongScreenshot has triggered."
                android.util.Log.d(r4, r0)
            L_0x0093:
                return
            L_0x0094:
                r4 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenshot.MotoGlobalScreenshot.DisplayScreenshotSession.stopLongScreenshot():void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$stopLongScreenshot$19() {
            onLongScreenshotFinalBitmapSaved(this.mLastSavedImageData);
        }

        /* access modifiers changed from: private */
        public void startTakeLongScreenshot() {
            Bitmap takeScreenBitmap = takeScreenBitmap();
            this.mLastFullBitmap = takeScreenBitmap;
            if (takeScreenBitmap != null && this.mLongScreenShotHelper != null) {
                this.mUpdatedCount = 0;
                this.mProcessObserver = new ProcessObserver();
                this.mTaskListener = new MyTaskStackListener();
                try {
                    ActivityManager.getService().registerProcessObserver(this.mProcessObserver);
                    ActivityTaskManager.getService().registerTaskStackListener(this.mTaskListener);
                } catch (RemoteException e) {
                    Log.e("MotoGlobalScreenshot", "Can't register activity monitoring :" + e);
                    this.mProcessObserver = null;
                    this.mTaskListener = null;
                }
                if (!this.mLongScreenShotHelper.startLongScreeenShot(this.mOnLongScreenshotListener)) {
                    stopLongScreenshot();
                }
            }
        }

        /* access modifiers changed from: private */
        public void showSaveBitmapTips(boolean z) {
            Message message = new Message();
            message.what = 2;
            message.arg1 = z ? 1 : 0;
            this.mScreenshotHandler.sendMessage(message);
        }

        /* access modifiers changed from: private */
        public void addImages(Bitmap[] bitmapArr, boolean z) {
            Message message = new Message();
            message.what = 3;
            message.obj = bitmapArr;
            message.arg1 = z ? 1 : 0;
            this.mScreenshotHandler.sendMessage(message);
        }

        private void bindMotoEditService() {
            Intent intent = new Intent();
            intent.setPackage("com.motorola.screenshoteditor");
            intent.setAction("com.motorola.screenshoteditor.action.BIND_SERVICE");
            if (!this.mContext.getApplicationContext().bindService(intent, this.mScreeenshotEditConnection, 1)) {
                Log.i("MotoGlobalScreenshot", "bind ScreenShot Edit Service error");
                this.mIMotoScreenShotEditService = null;
                startTakeLongScreenshot();
            }
        }

        /* access modifiers changed from: private */
        public void unbindMotoEditService() {
            try {
                this.mContext.getApplicationContext().unbindService(this.mScreeenshotEditConnection);
            } catch (Exception unused) {
            }
            this.mIMotoScreenShotEditService = null;
        }

        private File getEditBitmapSaveDir() {
            return new File(this.mContext.getFilesDir(), "longSh");
        }

        private String genMotoEditSaveNameSessionPrefix(int i) {
            return "session_" + i + "_";
        }

        private String genMotoEditSaveName(int i, int i2) {
            return genMotoEditSaveNameSessionPrefix(i) + "part_" + i2 + ".jpg";
        }

        /* access modifiers changed from: private */
        public void motoEditOnLongScreenShotStart() {
            IMotoScreenShotEditService iMotoScreenShotEditService = this.mIMotoScreenShotEditService;
            if (iMotoScreenShotEditService != null) {
                this.mMotoEditIndex = 0;
                try {
                    this.mMotoEditSessionId = iMotoScreenShotEditService.onLongScreenShotStart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /* access modifiers changed from: private */
        public void motoEditOnLongScreenShotEnd() {
            this.mMotoEditOnLSShotEnded = true;
            IMotoScreenShotEditService iMotoScreenShotEditService = this.mIMotoScreenShotEditService;
            if (iMotoScreenShotEditService != null) {
                try {
                    iMotoScreenShotEditService.onLongScreenShotEnd(this.mMotoEditSessionId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void motoEditOnExitScreenShot() {
            IMotoScreenShotEditService iMotoScreenShotEditService = this.mIMotoScreenShotEditService;
            if (iMotoScreenShotEditService != null) {
                try {
                    iMotoScreenShotEditService.onExitScreenShot(this.mMotoEditSessionId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                unbindMotoEditService();
            }
        }

        /* access modifiers changed from: private */
        public File saveBitmapFile(Bitmap bitmap, String str) {
            try {
                File file = new File(getEditBitmapSaveDir(), str);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* access modifiers changed from: private */
        public String motoEditOnImageAdded(Bitmap bitmap) {
            if (!(this.mIMotoScreenShotEditService == null || bitmap == null || this.mMotoEditOnLSShotEnded)) {
                String genMotoEditSaveName = genMotoEditSaveName(this.mMotoEditSessionId, this.mMotoEditIndex);
                this.mMotoEditIndex++;
                try {
                    long currentTimeMillis = System.currentTimeMillis();
                    File saveBitmapFile = saveBitmapFile(bitmap, genMotoEditSaveName);
                    if (MotoGlobalScreenshot.DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("motoEditOnImageAdded cost ");
                        sb.append(System.currentTimeMillis() - currentTimeMillis);
                        sb.append("msmMotoEditIndex = ");
                        sb.append(this.mMotoEditIndex - 1);
                        Log.d("MotoGlobalScreenshot", sb.toString());
                    }
                    if (saveBitmapFile != null) {
                        this.mIMotoScreenShotEditService.onImageAdded(this.mMotoEditSessionId, ScreenShotEditFileProvider.getUriForFile(this.mContext, "com.motorola.systemui.ScreenShotEditFileProvider", saveBitmapFile));
                    }
                    if (saveBitmapFile != null) {
                        return saveBitmapFile.getAbsolutePath();
                    }
                    return null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /* access modifiers changed from: private */
        public void motoEditSetMaxImageHeight(int i) {
            IMotoScreenShotEditService iMotoScreenShotEditService = this.mIMotoScreenShotEditService;
            if (iMotoScreenShotEditService != null) {
                try {
                    iMotoScreenShotEditService.setMaxImageHeight(this.mMotoEditSessionId, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean startMotoScreenshotEdit() {
            if (this.mIMotoScreenShotEditService == null) {
                return false;
            }
            try {
                String topPackage = MomentsHelper.getTopPackage();
                Log.d("MotoScreenshot", "setPackageName: " + topPackage);
                this.mIMotoScreenShotEditService.setScreenshotPackageName(topPackage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                this.mIMotoScreenShotEditService.startEditActivity(this.mMotoEditSessionId, -1);
                return true;
            } catch (Exception e2) {
                e2.printStackTrace();
                return false;
            }
        }

        /* access modifiers changed from: private */
        public Bitmap cropBitmap(Bitmap bitmap, int i, int i2) {
            int[] iArr = null;
            if (bitmap == null || i >= i2 || i < 0 || i2 <= 0 || i2 > bitmap.getHeight()) {
                Log.i("MotoGlobalScreenshot", "cropBitmap srcBitmap = " + bitmap + "; beginY = " + i + "; endY = " + i2 + "; return null");
                return null;
            }
            int width = bitmap.getWidth();
            int i3 = i2 - i;
            SoftReference<int[]> softReference = this.mPixelsCache;
            if (softReference != null) {
                iArr = softReference.get();
            }
            if (iArr == null || iArr.length != width * i3) {
                iArr = new int[(width * i3)];
                this.mPixelsCache = new SoftReference<>(iArr);
            }
            bitmap.getPixels(iArr, 0, width, 0, i, width, i3);
            return Bitmap.createBitmap(iArr, width, i3, Bitmap.Config.ARGB_8888);
        }

        private Bitmap cropBitmap(Bitmap bitmap, Insets insets) {
            int max = Math.max(insets.left, 0);
            int max2 = Math.max(insets.top, 0);
            int width = (bitmap.getWidth() - Math.max(insets.left, 0)) - Math.max(insets.right, 0);
            int height = (bitmap.getHeight() - Math.max(insets.top, 0)) - Math.max(insets.bottom, 0);
            int[] iArr = null;
            if (max < 0 || max2 < 0 || width <= 0 || height <= 0) {
                Log.i("MotoGlobalScreenshot", "cropBitmap srcBitmap = " + bitmap + "; insets = " + insets);
                return null;
            }
            SoftReference<int[]> softReference = this.mPixelsCache;
            if (softReference != null) {
                iArr = softReference.get();
            }
            if (iArr == null || iArr.length != width * height) {
                iArr = new int[(width * height)];
                this.mPixelsCache = new SoftReference<>(iArr);
            }
            int[] iArr2 = iArr;
            bitmap.getPixels(iArr2, 0, width, max, max2, width, height);
            return Bitmap.createBitmap(iArr2, width, height, Bitmap.Config.ARGB_8888);
        }

        private class ProcessObserver extends IProcessObserver.Stub {
            public void onForegroundServicesChanged(int i, int i2, int i3) {
            }

            public void onProcessDied(int i, int i2) {
            }

            private ProcessObserver() {
            }

            public void onForegroundActivitiesChanged(int i, int i2, boolean z) {
                if (z) {
                    Log.i("MotoGlobalScreenshot", "onForegroundActivitiesChanged pid = " + i + "; uid = " + i2);
                    DisplayScreenshotSession.this.mScreenshotHandler.post(new Runnable() {
                        public void run() {
                            DisplayScreenshotSession.this.stopLongScreenshot();
                        }
                    });
                }
            }
        }

        private class MyTaskStackListener extends TaskStackListener {
            private MyTaskStackListener() {
            }

            public void onTaskStackChanged() {
                Log.i("MotoGlobalScreenshot", "onTaskStackChanged.");
                DisplayScreenshotSession.this.mScreenshotHandler.post(new Runnable() {
                    public void run() {
                        DisplayScreenshotSession.this.stopLongScreenshot();
                    }
                });
            }
        }
    }
}
