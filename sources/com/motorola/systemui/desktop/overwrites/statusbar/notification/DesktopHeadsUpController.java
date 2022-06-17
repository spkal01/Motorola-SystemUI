package com.motorola.systemui.desktop.overwrites.statusbar.notification;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$style;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.util.Assert;
import com.motorola.android.provider.MotorolaSettings;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DesktopHeadsUpController implements CallbackController<NotifCollectionListener>, ConfigurationController.ConfigurationListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private final ArrayMap<String, HeadsupWindow> mActiveHeadsupWindows = new ArrayMap<>();
    private final ArrayMap<String, NotificationEntry> mActiveNotifications = new ArrayMap<>();
    /* access modifiers changed from: private */
    public boolean mAttached = false;
    private long mAttachedTime = 0;
    /* access modifiers changed from: private */
    public final int mAutoDismissNotificationDecay;
    private final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public ContentObserver mContentObserver;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public Context mCurrentUserContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    /* access modifiers changed from: private */
    public final int mDisplayId;
    private final DisplayManager mDisplayManager;
    private NotificationGroupManagerLegacy mGroupManagerLegacy;
    private final NotificationRowContentBinder.InflationCallback mInflationCallback = new NotificationRowContentBinder.InflationCallback() {
        public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
            Log.w("DesktopHeadsUpController", "inflation entry error: " + notificationEntry.getKey() + "; e:" + exc.getMessage());
        }

        public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
            if (DesktopHeadsUpController.this.mAttached) {
                DesktopHeadsUpController.this.mPendingNotifications.remove(notificationEntry.getKey());
                if (!notificationEntry.isRowRemoved()) {
                    boolean z = DesktopHeadsUpController.this.getActiveNotificationUnfiltered(notificationEntry.getKey()) == null;
                    if (DesktopHeadsUpController.DEBUG) {
                        Log.d("DesktopHeadsUpController", "onAsyncInflationFinished: " + notificationEntry.getKey());
                    }
                    if (z) {
                        DesktopHeadsUpController.this.addActiveNotification(notificationEntry);
                        DesktopHeadsUpController.this.showHeadsUp(notificationEntry);
                        return;
                    }
                    DesktopHeadsUpController.this.updateHeadsUp(notificationEntry);
                }
            }
        }
    };
    private final NotificationInterruptStateProvider mInterruptStateProvider;
    /* access modifiers changed from: private */
    public boolean mIsHeadUpDisable = false;
    /* access modifiers changed from: private */
    public int mLayoutDirection = 0;
    private final ArrayList<NotifCollectionListener> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Handler mMainHandler;
    /* access modifiers changed from: private */
    public final int mMinimumDisplayTime;
    private final NotifBindPipeline mNotifBindPipeline;
    private final NotificationListener.NotificationHandler mNotifListener = new NotificationListener.NotificationHandler() {
        public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        }

        public void onNotificationsInitialized() {
        }

        public void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
            if (!DesktopHeadsUpController.this.mIsHeadUpDisable) {
                DesktopHeadsUpController.this.handleNotificationPosted(statusBarNotification, rankingMap, false);
            }
        }

        public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
            DesktopHeadsUpController.this.handleRemoveNotification(statusBarNotification.getKey());
        }
    };
    private final NotificationListener mNotificationListener;
    private final Lazy<NotificationRowBinder> mNotificationRowBinderLazy;
    private Set<String> mOldNotificationKeys = new HashSet();
    private final NotificationGroupManagerLegacy.OnGroupChangeListener mOnGroupChangeListener = new NotificationGroupManagerLegacy.OnGroupChangeListener() {
        public void onGroupSuppressionChanged(NotificationGroupManagerLegacy.NotificationGroup notificationGroup, boolean z) {
            if (z) {
                if (DesktopHeadsUpController.this.isAlertingOrPending(notificationGroup.summary.getKey())) {
                    DesktopHeadsUpController.this.handleSuppressedSummaryAlerted(notificationGroup.summary.getSbn());
                }
            }
        }
    };
    @VisibleForTesting
    protected final HashMap<String, NotificationEntry> mPendingNotifications = new HashMap<>();
    private CurrentUserTracker mUserTracker;

    DesktopHeadsUpController(int i, Context context, Handler handler, NotificationInterruptStateProvider notificationInterruptStateProvider, DeviceProvisionedController deviceProvisionedController, Lazy<NotificationRowBinder> lazy, ConfigurationController configurationController, NotificationGroupManagerLegacy notificationGroupManagerLegacy, NotifBindPipeline notifBindPipeline, NotificationListener notificationListener) {
        this.mDisplayId = i;
        this.mContext = context;
        this.mMainHandler = handler;
        this.mInterruptStateProvider = notificationInterruptStateProvider;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mNotificationRowBinderLazy = lazy;
        this.mAutoDismissNotificationDecay = context.getResources().getInteger(R$integer.heads_up_notification_decay);
        this.mMinimumDisplayTime = context.getResources().getInteger(R$integer.heads_up_notification_minimum_time);
        this.mLayoutDirection = context.getResources().getConfiguration().getLayoutDirection();
        this.mConfigurationController = configurationController;
        this.mGroupManagerLegacy = notificationGroupManagerLegacy;
        this.mNotifBindPipeline = notifBindPipeline;
        this.mNotificationListener = notificationListener;
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
        this.mContentObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                super.onChange(z);
                DesktopHeadsUpController desktopHeadsUpController = DesktopHeadsUpController.this;
                boolean access$100 = desktopHeadsUpController.getDisablePopupNotification(desktopHeadsUpController.mCurrentUserContext.getContentResolver());
                if (DesktopHeadsUpController.this.mIsHeadUpDisable != access$100) {
                    boolean unused = DesktopHeadsUpController.this.mIsHeadUpDisable = access$100;
                    if (DesktopHeadsUpController.this.mIsHeadUpDisable) {
                        DesktopHeadsUpController.this.abortAllExistingInflation();
                        DesktopHeadsUpController.this.removeAllHeadsUpWindow();
                    }
                }
            }
        };
        this.mUserTracker = new CurrentUserTracker((BroadcastDispatcher) Dependency.get(BroadcastDispatcher.class)) {
            public void onUserSwitched(int i) {
                if (DesktopHeadsUpController.this.mAttached) {
                    DesktopHeadsUpController.this.mCurrentUserContext.getContentResolver().unregisterContentObserver(DesktopHeadsUpController.this.mContentObserver);
                }
                DesktopHeadsUpController.this.updateUserContext(i);
                if (DesktopHeadsUpController.this.mAttached) {
                    DesktopHeadsUpController.this.mCurrentUserContext.getContentResolver().registerContentObserver(MotorolaSettings.System.getUriFor("settings_disable_popup_notification"), true, DesktopHeadsUpController.this.mContentObserver);
                }
            }
        };
        Log.d("DesktopHeadsUpController", "DesktopHeadsUpController: " + context.getDisplayId());
    }

    /* access modifiers changed from: private */
    public void updateUserContext(int i) {
        this.mCurrentUserContext = this.mContext.createContextAsUser(UserHandle.of(i), 0);
    }

    public void attach() {
        Log.d("DesktopHeadsUpController", "attach: ");
        this.mAttached = true;
        this.mAttachedTime = SystemClock.elapsedRealtime();
        this.mUserTracker.startTracking();
        updateUserContext(this.mUserTracker.getCurrentUserId());
        this.mCurrentUserContext.getContentResolver().registerContentObserver(MotorolaSettings.System.getUriFor("settings_disable_popup_notification"), true, this.mContentObserver);
        this.mIsHeadUpDisable = getDisablePopupNotification(this.mCurrentUserContext.getContentResolver());
        this.mConfigurationController.addCallback(this);
        this.mNotificationListener.addNotificationHandler(this.mNotifListener);
        this.mGroupManagerLegacy.registerGroupChangeListener(this.mOnGroupChangeListener);
        this.mNotifBindPipeline.attach(this);
    }

    /* access modifiers changed from: private */
    public void abortAllExistingInflation() {
        Object[] array = this.mPendingNotifications.keySet().toArray();
        if (array != null && array.length > 0) {
            for (Object obj : array) {
                abortExistingInflation((String) obj, "detach");
            }
        }
        Object[] array2 = this.mActiveNotifications.keySet().toArray();
        if (array2 != null && array2.length > 0) {
            for (Object obj2 : array2) {
                abortExistingInflation((String) obj2, "detach");
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeAllHeadsUpWindow() {
        for (String handleRemoveNotification : new HashSet(this.mActiveHeadsupWindows.keySet())) {
            handleRemoveNotification(handleRemoveNotification);
        }
    }

    private void handleAddNotification(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, boolean z) {
        String key = statusBarNotification.getKey();
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        if (rankingMap.getRanking(key, ranking)) {
            NotificationEntry notificationEntry = this.mPendingNotifications.get(key);
            if (notificationEntry != null) {
                notificationEntry.setSbn(statusBarNotification);
            } else {
                notificationEntry = new NotificationEntry(statusBarNotification, ranking, false, SystemClock.uptimeMillis());
                Iterator<NotifCollectionListener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    it.next().onEntryInit(notificationEntry);
                }
            }
            boolean shouldHeadsUp = this.mInterruptStateProvider.shouldHeadsUp(notificationEntry);
            if (shouldHeadsUp && this.mGroupManagerLegacy.isSummaryOfSuppressedGroup(statusBarNotification)) {
                this.mPendingNotifications.put(key, notificationEntry);
                handleSuppressedSummaryAlerted(statusBarNotification);
            } else if (z || shouldHeadsUp) {
                try {
                    this.mNotificationRowBinderLazy.get().inflateViews(notificationEntry, this.mInflationCallback);
                    this.mPendingNotifications.put(key, notificationEntry);
                } catch (InflationException e) {
                    e.printStackTrace();
                }
            } else {
                handleRemoveNotification(key);
            }
        }
    }

    private void handleUpdateNotification(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, boolean z) {
        String key = statusBarNotification.getKey();
        abortExistingInflation(key, "updateNotification");
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(key);
        if (activeNotificationUnfiltered != null) {
            activeNotificationUnfiltered.setSbn(statusBarNotification);
            activeNotificationUnfiltered.targetSdk = resolveNotificationSdk(statusBarNotification);
            NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
            if (rankingMap.getRanking(activeNotificationUnfiltered.getKey(), ranking)) {
                activeNotificationUnfiltered.setRanking(ranking);
            }
            boolean shouldHeadsUp = this.mInterruptStateProvider.shouldHeadsUp(activeNotificationUnfiltered);
            if (shouldHeadsUp && this.mGroupManagerLegacy.isSummaryOfSuppressedGroup(statusBarNotification)) {
                handleSuppressedSummaryAlerted(statusBarNotification);
            } else if (z || shouldHeadsUp) {
                try {
                    this.mNotificationRowBinderLazy.get().inflateViews(activeNotificationUnfiltered, this.mInflationCallback);
                } catch (InflationException e) {
                    e.printStackTrace();
                }
            } else {
                handleRemoveNotification(key);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleRemoveNotification(String str) {
        this.mOldNotificationKeys.remove(str);
        abortExistingInflation(str, "removeNotification");
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered != null) {
            this.mActiveNotifications.remove(str);
            Iterator<NotifCollectionListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onEntryCleanUp(activeNotificationUnfiltered);
            }
            if (activeNotificationUnfiltered.rowExists()) {
                activeNotificationUnfiltered.removeRow();
            }
            removeHeadsUp(activeNotificationUnfiltered, false);
        }
    }

    private void abortExistingInflation(String str, String str2) {
        if (this.mPendingNotifications.containsKey(str)) {
            NotificationEntry notificationEntry = this.mPendingNotifications.get(str);
            notificationEntry.abortTask();
            this.mPendingNotifications.remove(str);
            Iterator<NotifCollectionListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onEntryCleanUp(notificationEntry);
            }
        }
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered != null) {
            activeNotificationUnfiltered.abortTask();
        }
    }

    private int resolveNotificationSdk(StatusBarNotification statusBarNotification) {
        try {
            return getPackageManagerForUser(this.mContext, statusBarNotification.getUser().getIdentifier()).getApplicationInfo(statusBarNotification.getPackageName(), 0).targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("DesktopHeadsUpController", "Failed looking up ApplicationInfo for " + statusBarNotification.getPackageName(), e);
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public NotificationEntry getActiveNotificationUnfiltered(String str) {
        return this.mActiveNotifications.get(str);
    }

    /* access modifiers changed from: private */
    public void addActiveNotification(NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.mActiveNotifications.put(notificationEntry.getKey(), notificationEntry);
    }

    public void addCallback(NotifCollectionListener notifCollectionListener) {
        this.mListeners.add(notifCollectionListener);
    }

    public void removeCallback(NotifCollectionListener notifCollectionListener) {
        this.mListeners.remove(notifCollectionListener);
    }

    public void setRemoteInputActive(NotificationEntry notificationEntry, boolean z) {
        HeadsupWindow headsupWindow = this.mActiveHeadsupWindows.get(notificationEntry.getKey());
        if (headsupWindow != null && headsupWindow.mNotificationEntry == notificationEntry && headsupWindow.remoteInputActive != z) {
            headsupWindow.remoteInputActive = z;
            if (z) {
                headsupWindow.removeAutoRemovalCallbacks();
            } else {
                headsupWindow.addAutoRemovalCallbacks((long) this.mMinimumDisplayTime);
            }
            headsupWindow.updateFocusableFlag();
        }
    }

    /* access modifiers changed from: private */
    public void showHeadsUp(NotificationEntry notificationEntry) {
        if (this.mDeviceProvisionedController.isDeviceProvisioned() && this.mDeviceProvisionedController.isCurrentUserSetup()) {
            notificationEntry.setInterruption();
            String key = notificationEntry.getKey();
            boolean contains = this.mOldNotificationKeys.contains(key);
            HeadsupWindow createHeadsupWindow = createHeadsupWindow(notificationEntry);
            if (createHeadsupWindow != null) {
                if (!contains) {
                    try {
                        createHeadsupWindow.show();
                    } catch (Exception e) {
                        Log.w("DesktopHeadsUpController", "HeadsupWindow show fail: " + e.getMessage());
                        return;
                    }
                } else {
                    Log.w("DesktopHeadsUpController", "HeadsupWindow ignore show for old: " + key);
                }
                this.mActiveHeadsupWindows.put(key, createHeadsupWindow);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateHeadsUp(NotificationEntry notificationEntry) {
        boolean alertAgain = alertAgain(notificationEntry, notificationEntry.getSbn().getNotification());
        HeadsupWindow headsupWindow = this.mActiveHeadsupWindows.get(notificationEntry.getKey());
        if (headsupWindow != null) {
            headsupWindow.update(notificationEntry, alertAgain);
        } else if (alertAgain) {
            showHeadsUp(notificationEntry);
        } else {
            Log.d("DesktopHeadsUpController", "updateHeadsUp ignore alertAgain is false");
        }
    }

    /* access modifiers changed from: private */
    public void removeHeadsUp(NotificationEntry notificationEntry, boolean z) {
        HeadsupWindow remove = this.mActiveHeadsupWindows.remove(notificationEntry.getKey());
        if (remove != null) {
            remove.hide(z);
        }
    }

    private static boolean alertAgain(NotificationEntry notificationEntry, Notification notification) {
        return notificationEntry == null || !notificationEntry.hasInterrupted() || (notification.flags & 8) == 0;
    }

    /* access modifiers changed from: private */
    public boolean isAlertingOrPending(String str) {
        return this.mPendingNotifications.containsKey(str) || this.mActiveNotifications.containsKey(str);
    }

    /* access modifiers changed from: private */
    public void handleSuppressedSummaryAlerted(StatusBarNotification statusBarNotification) {
        ArrayList<NotificationEntry> logicalChildren;
        NotificationEntry next;
        if (this.mGroupManagerLegacy.isSummaryOfSuppressedGroup(statusBarNotification) && isAlertingOrPending(statusBarNotification.getKey()) && (logicalChildren = this.mGroupManagerLegacy.getLogicalChildren(statusBarNotification)) != null && (next = logicalChildren.iterator().next()) != null && !next.getRow().keepInParent() && !next.isRowRemoved() && !next.isRowDismissed()) {
            transferAlertState(statusBarNotification, next.getSbn());
        }
    }

    private void transferAlertState(StatusBarNotification statusBarNotification, StatusBarNotification statusBarNotification2) {
        handleRemoveNotification(statusBarNotification.getKey());
        forceAlertNotification(statusBarNotification2);
    }

    public void onConfigChanged(Configuration configuration) {
        int layoutDirection = configuration.getLayoutDirection();
        if (layoutDirection != this.mLayoutDirection) {
            this.mLayoutDirection = layoutDirection;
        }
    }

    public HeadsupWindow createHeadsupWindow(NotificationEntry notificationEntry) {
        Display display = this.mDisplayManager.getDisplay(this.mDisplayId);
        if (display != null) {
            return new HeadsupWindow(notificationEntry, display);
        }
        Log.e("DesktopHeadsUpController", "create HeadsupWindow with expired display: " + this.mDisplayId);
        return null;
    }

    private final class HeadsupWindow {
        /* access modifiers changed from: private */
        public boolean mChildrenUpdateRequested;
        private ViewTreeObserver.OnPreDrawListener mChildrenUpdater = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ExpandableNotificationRow row = HeadsupWindow.this.mNotificationEntry.getRow();
                row.setActualHeight(row.getIntrinsicHeight());
                boolean unused = HeadsupWindow.this.mChildrenUpdateRequested = false;
                HeadsupWindow.this.mPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        /* access modifiers changed from: private */
        public final WindowManager mDisplyWindowManager;
        private boolean mIsShowing = false;
        /* access modifiers changed from: private */
        public WindowManager.LayoutParams mLayoutParams;
        /* access modifiers changed from: private */
        public NotificationEntry mNotificationEntry;
        /* access modifiers changed from: private */
        public ViewGroup mPanel;
        private View.OnAttachStateChangeListener mPanelOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
            }

            public void onViewDetachedFromWindow(View view) {
                HeadsupWindow.this.mNotificationEntry.getRow().setOnHeightChangedListener((ExpandableView.OnHeightChangedListener) null);
                HeadsupWindow.this.mPanel.removeOnLayoutChangeListener(HeadsupWindow.this.mPanelOnLayoutChangeListener);
                HeadsupWindow.this.mPanel.removeOnAttachStateChangeListener(this);
            }
        };
        /* access modifiers changed from: private */
        public View.OnLayoutChangeListener mPanelOnLayoutChangeListener = new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                HeadsupWindow.this.requestChildrenUpdate();
            }
        };
        private Runnable mRemoveAlertRunnable = new DesktopHeadsUpController$HeadsupWindow$$ExternalSyntheticLambda1(this);
        public boolean remoteInputActive = false;

        public HeadsupWindow(NotificationEntry notificationEntry, Display display) {
            this.mNotificationEntry = notificationEntry;
            this.mDisplyWindowManager = (WindowManager) DesktopHeadsUpController.this.mContext.createDisplayContext(display).createWindowContext(2038, (Bundle) null).getSystemService("window");
        }

        /* access modifiers changed from: private */
        public void requestChildrenUpdate() {
            if (!this.mChildrenUpdateRequested) {
                this.mPanel.getViewTreeObserver().addOnPreDrawListener(this.mChildrenUpdater);
                this.mChildrenUpdateRequested = true;
                this.mPanel.invalidate();
                addCloseBtn();
                hideOtherNotificationContentView();
            }
        }

        public void show() {
            if (!this.mIsShowing) {
                final ExpandableNotificationRow row = this.mNotificationEntry.getRow();
                row.setTopRoundness(1.0f, false);
                row.setBottomRoundness(1.0f, false);
                this.mNotificationEntry.setHeadsUp(true);
                row.setSystemExpanded(true);
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
                row.measure(makeMeasureSpec, makeMeasureSpec);
                addCloseBtn();
                hideOtherNotificationContentView();
                this.mLayoutParams = new WindowManager.LayoutParams(DesktopHeadsUpController.this.mContext.getResources().getDimensionPixelSize(R$dimen.desktop_heads_up_width), row.getMeasuredHeight() + DesktopHeadsUpController.this.mContext.getResources().getDimensionPixelSize(R$dimen.notification_headsup_margin), 2038, 545521768, -3);
                if (DesktopHeadsUpController.this.mLayoutDirection == 1) {
                    WindowManager.LayoutParams layoutParams = this.mLayoutParams;
                    layoutParams.gravity = 51;
                    layoutParams.windowAnimations = R$style.Animation_TaskbarNotificationPanelRTL;
                } else {
                    WindowManager.LayoutParams layoutParams2 = this.mLayoutParams;
                    layoutParams2.gravity = 53;
                    layoutParams2.windowAnimations = R$style.Animation_TaskbarNotificationPanel;
                }
                WindowManager.LayoutParams layoutParams3 = this.mLayoutParams;
                layoutParams3.privateFlags = 16;
                layoutParams3.setTitle("DesktopHeadsUp: " + DesktopHeadsUpController.this.mDisplayId + "; " + this.mNotificationEntry.getKey());
                ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(DesktopHeadsUpController.this.mContext).inflate(R$layout.notification_headsup_view, (ViewGroup) null);
                this.mPanel = viewGroup;
                viewGroup.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() != 4) {
                            return false;
                        }
                        HeadsupWindow headsupWindow = HeadsupWindow.this;
                        if (!headsupWindow.remoteInputActive || !headsupWindow.mNotificationEntry.rowExists()) {
                            return true;
                        }
                        HeadsupWindow.this.mNotificationEntry.closeRemoteInput();
                        return true;
                    }
                });
                ViewParent parent = row.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeAllViews();
                }
                this.mPanel.addView(row, new FrameLayout.LayoutParams(-1, -1));
                row.setOnHeightChangedListener(new ExpandableView.OnHeightChangedListener() {
                    public void onReset(ExpandableView expandableView) {
                    }

                    public void onHeightChanged(ExpandableView expandableView, boolean z) {
                        int intrinsicHeight = row.getIntrinsicHeight() + DesktopHeadsUpController.this.mContext.getResources().getDimensionPixelSize(R$dimen.notification_headsup_margin);
                        if (intrinsicHeight != HeadsupWindow.this.mLayoutParams.height) {
                            HeadsupWindow.this.mLayoutParams.height = intrinsicHeight;
                            HeadsupWindow.this.mDisplyWindowManager.updateViewLayout(HeadsupWindow.this.mPanel, HeadsupWindow.this.mLayoutParams);
                            HeadsupWindow.this.requestChildrenUpdate();
                        }
                    }
                });
                this.mPanel.addOnLayoutChangeListener(this.mPanelOnLayoutChangeListener);
                this.mPanel.addOnAttachStateChangeListener(this.mPanelOnAttachStateChangeListener);
                this.mDisplyWindowManager.addView(this.mPanel, this.mLayoutParams);
                addAutoRemovalCallbacks();
                this.mIsShowing = true;
            }
        }

        public void hide(boolean z) {
            if (this.mIsShowing) {
                if (z) {
                    this.mDisplyWindowManager.removeViewImmediate(this.mPanel);
                } else {
                    this.mDisplyWindowManager.removeView(this.mPanel);
                }
                removeAutoRemovalCallbacks();
            }
            this.mIsShowing = false;
        }

        private void addCloseBtn() {
            View expandButton;
            ImageView imageView;
            NotificationViewWrapper visibleNotificationViewWrapper = this.mNotificationEntry.getRow().getVisibleNotificationViewWrapper();
            if (visibleNotificationViewWrapper != null && (expandButton = visibleNotificationViewWrapper.getExpandButton()) != null && (imageView = (ImageView) expandButton.findViewById(16908958)) != null) {
                imageView.setImageDrawable(DesktopHeadsUpController.this.mContext.getDrawable(R$drawable.zz_moto_ic_heads_up_close));
                imageView.setOnClickListener(new DesktopHeadsUpController$HeadsupWindow$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$addCloseBtn$0(View view) {
            DesktopHeadsUpController.this.removeHeadsUp(this.mNotificationEntry, false);
        }

        private void hideOtherNotificationContentView() {
            NotificationContentView showingLayout;
            ExpandableNotificationRow row = this.mNotificationEntry.getRow();
            if (row != null && (showingLayout = row.getShowingLayout()) != null) {
                NotificationContentView privateLayout = row.getPrivateLayout();
                NotificationContentView publicLayout = row.getPublicLayout();
                if (privateLayout != null && publicLayout != null) {
                    int i = 0;
                    privateLayout.setVisibility(showingLayout == privateLayout ? 0 : 4);
                    if (showingLayout != publicLayout) {
                        i = 4;
                    }
                    publicLayout.setVisibility(i);
                }
            }
        }

        public void update(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                removeAutoRemovalCallbacks();
                addAutoRemovalCallbacks();
                show();
            }
        }

        private boolean isSticky() {
            return this.remoteInputActive || hasFullScreenIntent(this.mNotificationEntry);
        }

        private boolean hasFullScreenIntent(NotificationEntry notificationEntry) {
            return notificationEntry.getSbn().getNotification().fullScreenIntent != null;
        }

        private void addAutoRemovalCallbacks() {
            addAutoRemovalCallbacks(-1);
        }

        /* access modifiers changed from: private */
        public void addAutoRemovalCallbacks(long j) {
            removeAutoRemovalCallbacks();
            if (!isSticky()) {
                if (j <= 0) {
                    j = (long) Math.max(DesktopHeadsUpController.this.mAutoDismissNotificationDecay, DesktopHeadsUpController.this.mMinimumDisplayTime);
                }
                DesktopHeadsUpController.this.mMainHandler.postDelayed(this.mRemoveAlertRunnable, j);
            }
        }

        /* access modifiers changed from: private */
        public void removeAutoRemovalCallbacks() {
            DesktopHeadsUpController.this.mMainHandler.removeCallbacks(this.mRemoveAlertRunnable);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1() {
            hide(false);
        }

        public void updateFocusableFlag() {
            if (this.remoteInputActive) {
                this.mLayoutParams.flags &= -9;
            } else {
                this.mLayoutParams.flags |= 8;
            }
            if (this.mIsShowing) {
                this.mDisplyWindowManager.updateViewLayout(this.mPanel, this.mLayoutParams);
            }
        }
    }

    private void forceAlertNotification(StatusBarNotification statusBarNotification) {
        handleNotificationPosted(statusBarNotification, this.mNotificationListener.getCurrentRanking(), true);
    }

    /* access modifiers changed from: private */
    public void handleNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, boolean z) {
        boolean containsKey = this.mActiveNotifications.containsKey(statusBarNotification.getKey());
        String key = statusBarNotification.getKey();
        if (SystemClock.elapsedRealtime() - this.mAttachedTime < 3000) {
            this.mOldNotificationKeys.add(key);
        } else {
            this.mOldNotificationKeys.remove(key);
        }
        if (containsKey) {
            handleUpdateNotification(statusBarNotification, rankingMap, z);
        } else {
            handleAddNotification(statusBarNotification, rankingMap, z);
        }
    }

    private PackageManager getPackageManagerForUser(Context context, int i) {
        if (i >= 0) {
            try {
                context = context.createPackageContextAsUser("com.android.systemui", 4, new UserHandle(i));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return context.getPackageManager();
    }

    /* access modifiers changed from: private */
    public boolean getDisablePopupNotification(ContentResolver contentResolver) {
        try {
            return MotorolaSettings.System.getInt(this.mCurrentUserContext.getContentResolver(), "settings_disable_popup_notification") == 1;
        } catch (Exception unused) {
            return false;
        }
    }
}
