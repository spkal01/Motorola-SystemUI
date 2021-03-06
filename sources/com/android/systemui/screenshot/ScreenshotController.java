package com.android.systemui.screenshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ExitTransitionCoordinator;
import android.app.Notification;
import android.app.SharedElementCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.DisplayAddress;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.ScrollCaptureResponse;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import android.window.WindowContext;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.policy.PhoneWindow;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.screenshot.ScreenshotView;
import com.android.systemui.screenshot.ScrollCaptureController;
import com.android.systemui.screenshot.TakeScreenshotService;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScreenshotController {
    static final IRemoteAnimationRunner.Stub SCREENSHOT_REMOTE_RUNNER = new IRemoteAnimationRunner.Stub() {
        public void onAnimationCancelled() {
        }

        public void onAnimationStart(int i, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, RemoteAnimationTarget[] remoteAnimationTargetArr3, IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            try {
                iRemoteAnimationFinishedCallback.onAnimationFinished();
            } catch (RemoteException e) {
                Log.e(ScreenshotController.TAG, "Error finishing screenshot remote animation", e);
            }
        }
    };
    /* access modifiers changed from: private */
    public static final String TAG = LogConfig.logTag(ScreenshotController.class);
    private final AccessibilityManager mAccessibilityManager;
    private final ExecutorService mBgExecutor;
    private final MediaActionSound mCameraSound;
    private final InterestingConfigChanges mConfigChanges;
    private final WindowContext mContext;
    private TakeScreenshotService.RequestCallback mCurrentRequestCallback;
    private final DisplayManager mDisplayManager;
    private final ImageExporter mImageExporter;
    private final boolean mIsLowRamDevice;
    private ListenableFuture<ScrollCaptureResponse> mLastScrollCaptureRequest;
    private ScrollCaptureResponse mLastScrollCaptureResponse;
    private final LongScreenshotData mLongScreenshotHolder;
    private final Executor mMainExecutor;
    private final ScreenshotNotificationsController mNotificationsController;
    private SaveImageInBackgroundTask mSaveInBgTask;
    private Bitmap mScreenBitmap;
    private Animator mScreenshotAnimation;
    private final Handler mScreenshotHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            if (message.what == 2) {
                ScreenshotController.this.mUiEventLogger.log(ScreenshotEvent.SCREENSHOT_INTERACTION_TIMEOUT);
                ScreenshotController.this.dismissScreenshot(false);
            }
        }
    };
    private final ScreenshotSmartActions mScreenshotSmartActions;
    private boolean mScreenshotTakenInPortrait;
    /* access modifiers changed from: private */
    public ScreenshotView mScreenshotView;
    private final ScrollCaptureClient mScrollCaptureClient;
    private final ScrollCaptureController mScrollCaptureController;
    /* access modifiers changed from: private */
    public final UiEventLogger mUiEventLogger;
    private final PhoneWindow mWindow;
    private final WindowManager.LayoutParams mWindowLayoutParams;
    private final WindowManager mWindowManager;

    interface ActionsReadyListener {
        void onActionsReady(SavedImageData savedImageData);
    }

    interface QuickShareActionReadyListener {
        void onActionsReady(QuickShareData quickShareData);
    }

    interface TransitionDestination {
        void setTransitionDestination(Rect rect, Runnable runnable);
    }

    static class SaveImageInBackgroundData {
        public Consumer<Uri> finisher;
        public String focusedAppPackageName;
        public Bitmap image;
        public ActionsReadyListener mActionsReadyListener;
        public QuickShareActionReadyListener mQuickShareActionsReadyListener;

        SaveImageInBackgroundData() {
        }

        /* access modifiers changed from: package-private */
        public void clearImage() {
            this.image = null;
        }
    }

    static class SavedImageData {
        public Notification.Action deleteAction;
        public Supplier<ActionTransition> editTransition;
        public Notification.Action googleLensAction;
        public Notification.Action quickShareAction;
        public Supplier<ActionTransition> shareTransition;
        public List<Notification.Action> smartActions;
        public Uri uri;

        SavedImageData() {
        }

        static class ActionTransition {
            public Notification.Action action;
            public Bundle bundle;
            public Runnable onCancelRunnable;

            ActionTransition() {
            }
        }

        public void reset() {
            this.uri = null;
            this.shareTransition = null;
            this.editTransition = null;
            this.deleteAction = null;
            this.smartActions = null;
            this.quickShareAction = null;
            this.googleLensAction = null;
        }
    }

    static class QuickShareData {
        public Notification.Action quickShareAction;

        QuickShareData() {
        }

        public void reset() {
            this.quickShareAction = null;
        }
    }

    ScreenshotController(Context context, ScreenshotSmartActions screenshotSmartActions, ScreenshotNotificationsController screenshotNotificationsController, ScrollCaptureClient scrollCaptureClient, UiEventLogger uiEventLogger, ImageExporter imageExporter, Executor executor, ScrollCaptureController scrollCaptureController, LongScreenshotData longScreenshotData, ActivityManager activityManager) {
        Context context2 = context;
        InterestingConfigChanges interestingConfigChanges = new InterestingConfigChanges(-2147474556);
        this.mConfigChanges = interestingConfigChanges;
        this.mScreenshotSmartActions = screenshotSmartActions;
        this.mNotificationsController = screenshotNotificationsController;
        this.mScrollCaptureClient = scrollCaptureClient;
        this.mUiEventLogger = uiEventLogger;
        this.mImageExporter = imageExporter;
        this.mMainExecutor = executor;
        this.mScrollCaptureController = scrollCaptureController;
        this.mLongScreenshotHolder = longScreenshotData;
        this.mIsLowRamDevice = activityManager.isLowRamDevice();
        this.mBgExecutor = Executors.newSingleThreadExecutor();
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        Objects.requireNonNull(displayManager);
        this.mDisplayManager = displayManager;
        WindowContext createWindowContext = context.createDisplayContext(getDefaultDisplay()).createWindowContext(2036, (Bundle) null);
        this.mContext = createWindowContext;
        WindowManager windowManager = (WindowManager) createWindowContext.getSystemService(WindowManager.class);
        this.mWindowManager = windowManager;
        this.mAccessibilityManager = AccessibilityManager.getInstance(createWindowContext);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 0, 0, 2036, 918816, -3);
        this.mWindowLayoutParams = layoutParams;
        layoutParams.setTitle("ScreenshotAnimation");
        layoutParams.layoutInDisplayCutoutMode = 3;
        layoutParams.setFitInsetsTypes(0);
        layoutParams.privateFlags |= 536870912;
        PhoneWindow phoneWindow = new PhoneWindow(createWindowContext);
        this.mWindow = phoneWindow;
        phoneWindow.setWindowManager(windowManager, (IBinder) null, (String) null);
        phoneWindow.requestFeature(1);
        phoneWindow.requestFeature(13);
        phoneWindow.setBackgroundDrawableResource(17170445);
        interestingConfigChanges.applyNewConfig(context.getResources());
        reloadAssets();
        MediaActionSound mediaActionSound = new MediaActionSound();
        this.mCameraSound = mediaActionSound;
        mediaActionSound.load(0);
    }

    /* access modifiers changed from: package-private */
    public void takeScreenshotFullscreen(Consumer<Uri> consumer, TakeScreenshotService.RequestCallback requestCallback) {
        this.mCurrentRequestCallback = requestCallback;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDefaultDisplay().getRealMetrics(displayMetrics);
        lambda$takeScreenshotPartial$0(consumer, new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels));
    }

    /* access modifiers changed from: package-private */
    public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i, int i2, ComponentName componentName, Consumer<Uri> consumer, TakeScreenshotService.RequestCallback requestCallback) {
        boolean z;
        Insets insets2;
        if (bitmap == null) {
            Log.e(TAG, "Got null bitmap from screenshot message");
            this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
            requestCallback.reportError();
            return;
        }
        if (!aspectRatiosMatch(bitmap, insets, rect)) {
            Insets insets3 = Insets.NONE;
            rect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            z = true;
            insets2 = insets3;
        } else {
            insets2 = insets;
            z = false;
        }
        this.mCurrentRequestCallback = requestCallback;
        saveScreenshot(bitmap, consumer, rect, insets2, z);
    }

    /* access modifiers changed from: package-private */
    public void takeScreenshotPartial(Consumer<Uri> consumer, TakeScreenshotService.RequestCallback requestCallback) {
        this.mScreenshotView.reset();
        this.mCurrentRequestCallback = requestCallback;
        attachWindow();
        this.mWindow.setContentView(this.mScreenshotView);
        this.mScreenshotView.requestApplyInsets();
        this.mScreenshotView.takePartialScreenshot(new ScreenshotController$$ExternalSyntheticLambda18(this, consumer));
    }

    /* access modifiers changed from: package-private */
    public void dismissScreenshot(boolean z) {
        if (z || !this.mScreenshotView.isDismissing()) {
            cancelTimeout();
            if (z) {
                finishDismiss();
            } else {
                this.mScreenshotView.animateDismissal();
            }
            ScrollCaptureResponse scrollCaptureResponse = this.mLastScrollCaptureResponse;
            if (scrollCaptureResponse != null) {
                scrollCaptureResponse.close();
                this.mLastScrollCaptureResponse = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isPendingSharedTransition() {
        return this.mScreenshotView.isPendingSharedTransition();
    }

    /* access modifiers changed from: package-private */
    public void releaseContext() {
        this.mContext.release();
        this.mCameraSound.release();
        this.mBgExecutor.shutdownNow();
    }

    private void reloadAssets() {
        ScreenshotView screenshotView = (ScreenshotView) LayoutInflater.from(this.mContext).inflate(R$layout.global_screenshot, (ViewGroup) null);
        this.mScreenshotView = screenshotView;
        screenshotView.init(this.mUiEventLogger, new ScreenshotView.ScreenshotViewCallback() {
            public void onUserInteraction() {
                ScreenshotController.this.resetTimeout();
            }

            public void onDismiss() {
                ScreenshotController.this.finishDismiss();
            }

            public void onTouchOutside() {
                ScreenshotController.this.setWindowFocusable(false);
            }
        });
        this.mScreenshotView.setOnKeyListener(new ScreenshotController$$ExternalSyntheticLambda1(this));
        this.mScreenshotView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mScreenshotView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reloadAssets$1(View view, int i, KeyEvent keyEvent) {
        if (i != 4) {
            return false;
        }
        dismissScreenshot(false);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: takeScreenshotInternal */
    public void lambda$takeScreenshotPartial$0(Consumer<Uri> consumer, Rect rect) {
        boolean z = true;
        if (this.mContext.getResources().getConfiguration().orientation != 1) {
            z = false;
        }
        this.mScreenshotTakenInPortrait = z;
        Rect rect2 = new Rect(rect);
        Bitmap captureScreenshot = captureScreenshot(rect);
        if (captureScreenshot == null) {
            Log.e(TAG, "takeScreenshotInternal: Screenshot bitmap was null");
            this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_capture_text);
            TakeScreenshotService.RequestCallback requestCallback = this.mCurrentRequestCallback;
            if (requestCallback != null) {
                requestCallback.reportError();
                return;
            }
            return;
        }
        saveScreenshot(captureScreenshot, consumer, rect2, Insets.NONE, true);
    }

    private Bitmap captureScreenshot(Rect rect) {
        int width = rect.width();
        int height = rect.height();
        Display defaultDisplay = getDefaultDisplay();
        DisplayAddress.Physical address = defaultDisplay.getAddress();
        if (!(address instanceof DisplayAddress.Physical)) {
            String str = TAG;
            Log.e(str, "Skipping Screenshot - Default display does not have a physical address: " + defaultDisplay);
            return null;
        }
        SurfaceControl.ScreenshotHardwareBuffer captureDisplay = SurfaceControl.captureDisplay(new SurfaceControl.DisplayCaptureArgs.Builder(SurfaceControl.getPhysicalDisplayToken(address.getPhysicalDisplayId())).setSourceCrop(rect).setSize(width, height).build());
        if (captureDisplay == null) {
            return null;
        }
        return captureDisplay.asBitmap();
    }

    private void saveScreenshot(Bitmap bitmap, Consumer<Uri> consumer, final Rect rect, Insets insets, final boolean z) {
        if (this.mAccessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEvent = new AccessibilityEvent(32);
            accessibilityEvent.setContentDescription(this.mContext.getResources().getString(R$string.screenshot_saving_title));
            this.mAccessibilityManager.sendAccessibilityEvent(accessibilityEvent);
        }
        int rotation = getDefaultDisplay().getRotation();
        try {
            WindowManagerGlobal.getInstance();
            if (WindowManagerGlobal.getWindowManagerService().isFocusedWindowRestrict() && rotation == 0) {
                Rect waterfallRect = getWaterfallRect(this.mContext.getResources());
                rect.left += waterfallRect.left;
                rect.top += waterfallRect.top;
                rect.right -= waterfallRect.right;
                rect.bottom -= waterfallRect.bottom;
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to restrict the screenshot", e);
        }
        if (this.mScreenshotView.isAttachedToWindow()) {
            if (!this.mScreenshotView.isDismissing()) {
                this.mUiEventLogger.log(ScreenshotEvent.SCREENSHOT_REENTERED);
            }
            this.mScreenshotView.reset();
        }
        this.mScreenshotView.updateOrientation(this.mWindowManager.getCurrentWindowMetrics().getWindowInsets().getDisplayCutout());
        this.mScreenBitmap = bitmap;
        if (!isUserSetupComplete()) {
            Log.w(TAG, "User setup not complete, displaying toast only");
            saveScreenshotAndToast(consumer);
            return;
        }
        this.mScreenBitmap.setHasAlpha(false);
        this.mScreenBitmap.prepareToDraw();
        saveScreenshotInWorkerThread(consumer, new ScreenshotController$$ExternalSyntheticLambda3(this), new ScreenshotController$$ExternalSyntheticLambda6(this));
        setWindowFocusable(true);
        withWindowAttached(new ScreenshotController$$ExternalSyntheticLambda9(this));
        attachWindow();
        this.mScreenshotView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ScreenshotController.this.mScreenshotView.getViewTreeObserver().removeOnPreDrawListener(this);
                ScreenshotController.this.startAnimation(rect, z);
                return true;
            }
        });
        this.mScreenshotView.setScreenshot(this.mScreenBitmap, insets);
        setContentView(this.mScreenshotView);
        this.mWindow.getDecorView().setOnApplyWindowInsetsListener(ScreenshotController$$ExternalSyntheticLambda0.INSTANCE);
        cancelTimeout();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveScreenshot$3() {
        requestScrollCapture();
        this.mWindow.peekDecorView().getViewRootImpl().setActivityConfigCallback(new ScreenshotController$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveScreenshot$2(Configuration configuration, int i) {
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            this.mScreenshotView.hideScrollChip();
            this.mScreenshotHandler.postDelayed(new ScreenshotController$$ExternalSyntheticLambda12(this), 150);
            this.mScreenshotView.updateDisplayCutoutMargins(this.mWindowManager.getCurrentWindowMetrics().getWindowInsets().getDisplayCutout());
            Animator animator = this.mScreenshotAnimation;
            if (animator != null && animator.isRunning()) {
                this.mScreenshotAnimation.end();
            }
        }
    }

    /* access modifiers changed from: private */
    public void requestScrollCapture() {
        if (!allowLongScreenshots()) {
            Log.d(TAG, "Long screenshots not supported on this device");
            return;
        }
        this.mScrollCaptureClient.setHostWindowToken(this.mWindow.getDecorView().getWindowToken());
        ListenableFuture<ScrollCaptureResponse> listenableFuture = this.mLastScrollCaptureRequest;
        if (listenableFuture != null) {
            listenableFuture.cancel(true);
        }
        ListenableFuture<ScrollCaptureResponse> request = this.mScrollCaptureClient.request(0);
        this.mLastScrollCaptureRequest = request;
        request.addListener(new ScreenshotController$$ExternalSyntheticLambda10(this), this.mMainExecutor);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestScrollCapture$5() {
        onScrollCaptureResponseReady(this.mLastScrollCaptureRequest);
    }

    private void onScrollCaptureResponseReady(Future<ScrollCaptureResponse> future) {
        try {
            ScrollCaptureResponse scrollCaptureResponse = this.mLastScrollCaptureResponse;
            if (scrollCaptureResponse != null) {
                scrollCaptureResponse.close();
            }
            ScrollCaptureResponse scrollCaptureResponse2 = future.get();
            this.mLastScrollCaptureResponse = scrollCaptureResponse2;
            if (!scrollCaptureResponse2.isConnected()) {
                String str = TAG;
                Log.d(str, "ScrollCapture: " + this.mLastScrollCaptureResponse.getDescription() + " [" + this.mLastScrollCaptureResponse.getWindowTitle() + "]");
                return;
            }
            String str2 = TAG;
            Log.d(str2, "ScrollCapture: connected to window [" + this.mLastScrollCaptureResponse.getWindowTitle() + "]");
            this.mScreenshotView.showScrollChip(new ScreenshotController$$ExternalSyntheticLambda14(this, this.mLastScrollCaptureResponse));
        } catch (CancellationException unused) {
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "requestScrollCapture failed", e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onScrollCaptureResponseReady$9(ScrollCaptureResponse scrollCaptureResponse) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDefaultDisplay().getRealMetrics(displayMetrics);
        this.mScreenshotView.prepareScrollingTransition(scrollCaptureResponse, this.mScreenBitmap, captureScreenshot(new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)), this.mScreenshotTakenInPortrait);
        this.mScreenshotView.post(new ScreenshotController$$ExternalSyntheticLambda13(this, scrollCaptureResponse));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onScrollCaptureResponseReady$8(ScrollCaptureResponse scrollCaptureResponse) {
        this.mLastScrollCaptureResponse = null;
        ListenableFuture<ScrollCaptureController.LongScreenshot> run = this.mScrollCaptureController.run(scrollCaptureResponse);
        run.addListener(new ScreenshotController$$ExternalSyntheticLambda17(this, run), this.mMainExecutor);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onScrollCaptureResponseReady$7(ListenableFuture listenableFuture) {
        try {
            ScrollCaptureController.LongScreenshot longScreenshot = (ScrollCaptureController.LongScreenshot) listenableFuture.get();
            if (longScreenshot.getHeight() == 0) {
                this.mScreenshotView.restoreNonScrollingUi();
                return;
            }
            this.mLongScreenshotHolder.setLongScreenshot(longScreenshot);
            this.mLongScreenshotHolder.setTransitionDestinationCallback(new ScreenshotController$$ExternalSyntheticLambda7(this, longScreenshot));
            Intent intent = new Intent(this.mContext, LongScreenshotActivity.class);
            intent.setFlags(335544320);
            WindowContext windowContext = this.mContext;
            windowContext.startActivity(intent, ActivityOptions.makeCustomAnimation(windowContext, 0, 0).toBundle());
            try {
                WindowManagerGlobal.getWindowManagerService().overridePendingAppTransitionRemote(new RemoteAnimationAdapter(SCREENSHOT_REMOTE_RUNNER, 0, 0), 0);
            } catch (Exception e) {
                Log.e(TAG, "Error overriding screenshot app transition", e);
            }
        } catch (InterruptedException | CancellationException | ExecutionException e2) {
            Log.e(TAG, "Exception", e2);
            this.mScreenshotView.restoreNonScrollingUi();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onScrollCaptureResponseReady$6(ScrollCaptureController.LongScreenshot longScreenshot, Rect rect, Runnable runnable) {
        this.mScreenshotView.startLongScreenshotTransition(rect, runnable, longScreenshot);
    }

    private void withWindowAttached(final Runnable runnable) {
        final View decorView = this.mWindow.getDecorView();
        if (decorView.isAttachedToWindow()) {
            runnable.run();
        } else {
            decorView.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                public void onWindowDetached() {
                }

                public void onWindowAttached() {
                    decorView.getViewTreeObserver().removeOnWindowAttachListener(this);
                    runnable.run();
                }
            });
        }
    }

    private void setContentView(View view) {
        this.mWindow.setContentView(view);
    }

    private void attachWindow() {
        View decorView = this.mWindow.getDecorView();
        if (!decorView.isAttachedToWindow()) {
            this.mWindowManager.addView(decorView, this.mWindowLayoutParams);
            decorView.requestApplyInsets();
        }
    }

    /* access modifiers changed from: package-private */
    public void removeWindow() {
        View peekDecorView = this.mWindow.peekDecorView();
        if (peekDecorView != null && peekDecorView.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(peekDecorView);
        }
    }

    private void saveScreenshotAndToast(Consumer<Uri> consumer) {
        this.mCameraSound.play(0);
        saveScreenshotInWorkerThread(consumer, new ScreenshotController$$ExternalSyntheticLambda5(this, consumer), (QuickShareActionReadyListener) null);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveScreenshotAndToast$11(Consumer consumer, SavedImageData savedImageData) {
        consumer.accept(savedImageData.uri);
        if (savedImageData.uri == null) {
            this.mUiEventLogger.log(ScreenshotEvent.SCREENSHOT_NOT_SAVED);
            this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_save_text);
            return;
        }
        this.mUiEventLogger.log(ScreenshotEvent.SCREENSHOT_SAVED);
        this.mScreenshotHandler.post(new ScreenshotController$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveScreenshotAndToast$10() {
        Toast.makeText(this.mContext, R$string.screenshot_saved_title, 0).show();
    }

    /* access modifiers changed from: private */
    public void startAnimation(Rect rect, boolean z) {
        Animator animator = this.mScreenshotAnimation;
        if (animator != null && animator.isRunning()) {
            this.mScreenshotAnimation.cancel();
        }
        this.mScreenshotAnimation = this.mScreenshotView.createScreenshotDropInAnimation(rect, z);
        this.mCameraSound.play(0);
        this.mScreenshotAnimation.start();
    }

    /* access modifiers changed from: private */
    public void finishDismiss() {
        cancelTimeout();
        removeWindow();
        this.mScreenshotView.reset();
        TakeScreenshotService.RequestCallback requestCallback = this.mCurrentRequestCallback;
        if (requestCallback != null) {
            requestCallback.onFinish();
            this.mCurrentRequestCallback = null;
        }
    }

    private void saveScreenshotInWorkerThread(Consumer<Uri> consumer, ActionsReadyListener actionsReadyListener, QuickShareActionReadyListener quickShareActionReadyListener) {
        SaveImageInBackgroundData saveImageInBackgroundData = new SaveImageInBackgroundData();
        saveImageInBackgroundData.image = this.mScreenBitmap;
        saveImageInBackgroundData.finisher = consumer;
        saveImageInBackgroundData.mActionsReadyListener = actionsReadyListener;
        saveImageInBackgroundData.mQuickShareActionsReadyListener = quickShareActionReadyListener;
        SaveImageInBackgroundTask saveImageInBackgroundTask = this.mSaveInBgTask;
        if (saveImageInBackgroundTask != null) {
            saveImageInBackgroundTask.setActionsReadyListener(new ScreenshotController$$ExternalSyntheticLambda4(this));
        }
        SaveImageInBackgroundTask saveImageInBackgroundTask2 = new SaveImageInBackgroundTask(this.mContext, this.mImageExporter, this.mScreenshotSmartActions, saveImageInBackgroundData, getActionTransitionSupplier());
        this.mSaveInBgTask = saveImageInBackgroundTask2;
        saveImageInBackgroundTask2.execute(new Void[0]);
    }

    private void cancelTimeout() {
        this.mScreenshotHandler.removeMessages(2);
    }

    /* access modifiers changed from: private */
    public void resetTimeout() {
        cancelTimeout();
        Handler handler = this.mScreenshotHandler;
        handler.sendMessageDelayed(handler.obtainMessage(2), (long) ((AccessibilityManager) this.mContext.getSystemService("accessibility")).getRecommendedTimeoutMillis(6000, 4));
    }

    /* access modifiers changed from: private */
    public void showUiOnActionsReady(SavedImageData savedImageData) {
        logSuccessOnActionsReady(savedImageData);
        resetTimeout();
        if (savedImageData.uri != null) {
            this.mScreenshotHandler.post(new ScreenshotController$$ExternalSyntheticLambda16(this, savedImageData));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUiOnActionsReady$12(final SavedImageData savedImageData) {
        Animator animator = this.mScreenshotAnimation;
        if (animator == null || !animator.isRunning()) {
            this.mScreenshotView.setChipIntents(savedImageData);
        } else {
            this.mScreenshotAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    ScreenshotController.this.mScreenshotView.setChipIntents(savedImageData);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void showUiOnQuickShareActionReady(QuickShareData quickShareData) {
        if (quickShareData.quickShareAction != null) {
            this.mScreenshotHandler.post(new ScreenshotController$$ExternalSyntheticLambda15(this, quickShareData));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUiOnQuickShareActionReady$13(final QuickShareData quickShareData) {
        Animator animator = this.mScreenshotAnimation;
        if (animator == null || !animator.isRunning()) {
            this.mScreenshotView.addQuickShareChip(quickShareData.quickShareAction);
        } else {
            this.mScreenshotAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    ScreenshotController.this.mScreenshotView.addQuickShareChip(quickShareData.quickShareAction);
                }
            });
        }
    }

    private Supplier<SavedImageData.ActionTransition> getActionTransitionSupplier() {
        return new ScreenshotController$$ExternalSyntheticLambda19(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ SavedImageData.ActionTransition lambda$getActionTransitionSupplier$15() {
        View transitionView = this.mScreenshotView.getTransitionView();
        transitionView.setX(transitionView.getX() - ((float) this.mScreenshotView.getStaticLeftMargin()));
        Pair startSharedElementAnimation = ActivityOptions.startSharedElementAnimation(this.mWindow, new ScreenshotExitTransitionCallbacksSupplier(true).get(), (SharedElementCallback) null, new Pair[]{Pair.create(this.mScreenshotView.getTransitionView(), "screenshot_preview_image")});
        ((ExitTransitionCoordinator) startSharedElementAnimation.second).startExit();
        SavedImageData.ActionTransition actionTransition = new SavedImageData.ActionTransition();
        actionTransition.bundle = ((ActivityOptions) startSharedElementAnimation.first).toBundle();
        actionTransition.onCancelRunnable = new ScreenshotController$$ExternalSyntheticLambda11(this);
        return actionTransition;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getActionTransitionSupplier$14() {
        ActivityOptions.stopSharedElementAnimation(this.mWindow);
    }

    /* access modifiers changed from: private */
    public void logSuccessOnActionsReady(SavedImageData savedImageData) {
        if (savedImageData.uri == null) {
            this.mUiEventLogger.log(ScreenshotEvent.SCREENSHOT_NOT_SAVED);
            this.mNotificationsController.notifyScreenshotError(R$string.screenshot_failed_to_save_text);
            return;
        }
        this.mUiEventLogger.log(ScreenshotEvent.SCREENSHOT_SAVED);
    }

    private boolean isUserSetupComplete() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "user_setup_complete", 0) == 1;
    }

    /* access modifiers changed from: private */
    public void setWindowFocusable(boolean z) {
        View peekDecorView;
        WindowManager.LayoutParams layoutParams = this.mWindowLayoutParams;
        int i = layoutParams.flags;
        if (z) {
            layoutParams.flags = i & -9;
        } else {
            layoutParams.flags = i | 8;
        }
        if (layoutParams.flags != i && (peekDecorView = this.mWindow.peekDecorView()) != null && peekDecorView.isAttachedToWindow()) {
            this.mWindowManager.updateViewLayout(peekDecorView, this.mWindowLayoutParams);
        }
    }

    private Display getDefaultDisplay() {
        return this.mDisplayManager.getDisplay(0);
    }

    private boolean allowLongScreenshots() {
        return !this.mIsLowRamDevice;
    }

    private static boolean aspectRatiosMatch(Bitmap bitmap, Insets insets, Rect rect) {
        int width = (bitmap.getWidth() - insets.left) - insets.right;
        int height = (bitmap.getHeight() - insets.top) - insets.bottom;
        if (height == 0 || width == 0 || bitmap.getWidth() == 0 || bitmap.getHeight() == 0 || Math.abs((((float) width) / ((float) height)) - (((float) rect.width()) / ((float) rect.height()))) >= 0.1f) {
            return false;
        }
        return true;
    }

    private class ScreenshotExitTransitionCallbacksSupplier implements Supplier<ExitTransitionCoordinator.ExitTransitionCallbacks> {
        final boolean mDismissOnHideSharedElements;

        ScreenshotExitTransitionCallbacksSupplier(boolean z) {
            this.mDismissOnHideSharedElements = z;
        }

        public ExitTransitionCoordinator.ExitTransitionCallbacks get() {
            return new ExitTransitionCoordinator.ExitTransitionCallbacks() {
                public boolean isReturnTransitionAllowed() {
                    return false;
                }

                public void onFinish() {
                }

                public void hideSharedElements() {
                    ScreenshotExitTransitionCallbacksSupplier screenshotExitTransitionCallbacksSupplier = ScreenshotExitTransitionCallbacksSupplier.this;
                    if (screenshotExitTransitionCallbacksSupplier.mDismissOnHideSharedElements) {
                        ScreenshotController.this.finishDismiss();
                    }
                }
            };
        }
    }

    public static Rect getWaterfallRect(Resources resources) {
        return new Rect(resources.getDimensionPixelSize(17105603), resources.getDimensionPixelSize(17105605), resources.getDimensionPixelSize(17105604), resources.getDimensionPixelSize(17105602));
    }
}
