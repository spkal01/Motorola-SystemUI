package com.android.systemui.statusbar.notification;

import android.os.RemoteException;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.legacy.LegacyNotificationRanker;
import com.android.systemui.statusbar.notification.collection.legacy.LegacyNotificationRankerStub;
import com.android.systemui.statusbar.notification.collection.legacy.NotificationGroupManagerLegacy;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;
import com.android.systemui.util.Assert;
import com.android.systemui.util.leak.LeakDetector;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationEntryManager implements CommonNotifCollection, Dumpable, VisualStabilityManager.Callback {
    private static final boolean DEBUG = Log.isLoggable("NotificationEntryMgr", 3);
    /* access modifiers changed from: private */
    public final ArrayMap<String, NotificationEntry> mActiveNotifications = new ArrayMap<>();
    private final Set<NotificationEntry> mAllNotifications;
    private final FeatureFlags mFeatureFlags;
    private final ForegroundServiceDismissalFeatureController mFgsFeatureController;
    private final NotificationGroupManagerLegacy mGroupManager;
    private final NotificationRowContentBinder.InflationCallback mInflationCallback;
    private NotificationListenerService.RankingMap mLatestRankingMap;
    private final LeakDetector mLeakDetector;
    /* access modifiers changed from: private */
    public final NotificationEntryManagerLogger mLogger;
    /* access modifiers changed from: private */
    public final HashMap<String, NotificationEntry> mNewNotifications;
    private final List<NotifCollectionListener> mNotifCollectionListeners;
    private final NotificationListener.NotificationHandler mNotifListener;
    /* access modifiers changed from: private */
    public final List<NotificationEntryListener> mNotificationEntryListeners;
    /* access modifiers changed from: private */
    public NotificationFilter mNotificationFilter;
    @VisibleForTesting
    final ArrayList<NotificationLifetimeExtender> mNotificationLifetimeExtenders;
    private final Lazy<NotificationRowBinder> mNotificationRowBinderLazy;
    /* access modifiers changed from: private */
    public final Set<String> mOldNotifications;
    @VisibleForTesting
    protected final HashMap<String, NotificationEntry> mPendingNotifications = new HashMap<>();
    private NotificationPresenter mPresenter;
    private LegacyNotificationRanker mRanker;
    private final Set<NotificationEntry> mReadOnlyAllNotifications;
    private final List<NotificationEntry> mReadOnlyNotifications;
    private final Lazy<NotificationRemoteInputManager> mRemoteInputManagerLazy;
    private final List<NotificationRemoveInterceptor> mRemoveInterceptors;
    private final Map<NotificationEntry, NotificationLifetimeExtender> mRetainedNotifications;
    @VisibleForTesting
    protected final ArrayList<NotificationEntry> mSortedAndFiltered;
    private final IStatusBarService mStatusBarService;
    private UnReadNotificationListener mUnReadNotificationListener;

    public interface KeyguardEnvironment {
        boolean isDeviceProvisioned();

        boolean isNotificationForCurrentProfiles(StatusBarNotification statusBarNotification);
    }

    public interface UnReadNotificationListener {
        void onUnReadNotificationSizeChanged(int i);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationEntryManager state:");
        printWriter.println("  mAllNotifications=");
        if (this.mAllNotifications.size() == 0) {
            printWriter.println("null");
        } else {
            int i = 0;
            for (NotificationEntry dumpEntry : this.mAllNotifications) {
                dumpEntry(printWriter, "  ", i, dumpEntry);
                i++;
            }
        }
        printWriter.print("  mPendingNotifications=");
        if (this.mPendingNotifications.size() == 0) {
            printWriter.println("null");
        } else {
            for (NotificationEntry sbn : this.mPendingNotifications.values()) {
                printWriter.println(sbn.getSbn());
            }
        }
        printWriter.println("  Remove interceptors registered:");
        for (NotificationRemoveInterceptor notificationRemoveInterceptor : this.mRemoveInterceptors) {
            printWriter.println("    " + notificationRemoveInterceptor.getClass().getSimpleName());
        }
        printWriter.println("  Lifetime extenders registered:");
        Iterator<NotificationLifetimeExtender> it = this.mNotificationLifetimeExtenders.iterator();
        while (it.hasNext()) {
            printWriter.println("    " + it.next().getClass().getSimpleName());
        }
        printWriter.println("  Lifetime-extended notifications:");
        if (this.mRetainedNotifications.isEmpty()) {
            printWriter.println("    None");
            return;
        }
        for (Map.Entry next : this.mRetainedNotifications.entrySet()) {
            printWriter.println("    " + ((NotificationEntry) next.getKey()).getSbn() + " retained by " + ((NotificationLifetimeExtender) next.getValue()).getClass().getName());
        }
    }

    public NotificationEntryManager(NotificationEntryManagerLogger notificationEntryManagerLogger, NotificationGroupManagerLegacy notificationGroupManagerLegacy, FeatureFlags featureFlags, Lazy<NotificationRowBinder> lazy, Lazy<NotificationRemoteInputManager> lazy2, LeakDetector leakDetector, ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController, IStatusBarService iStatusBarService) {
        ArraySet arraySet = new ArraySet();
        this.mAllNotifications = arraySet;
        this.mReadOnlyAllNotifications = Collections.unmodifiableSet(arraySet);
        ArrayList<NotificationEntry> arrayList = new ArrayList<>();
        this.mSortedAndFiltered = arrayList;
        this.mReadOnlyNotifications = Collections.unmodifiableList(arrayList);
        this.mRetainedNotifications = new ArrayMap();
        this.mNotifCollectionListeners = new ArrayList();
        this.mRanker = new LegacyNotificationRankerStub();
        this.mNotificationLifetimeExtenders = new ArrayList<>();
        this.mNotificationEntryListeners = new ArrayList();
        this.mRemoveInterceptors = new ArrayList();
        this.mOldNotifications = new HashSet();
        this.mNewNotifications = new HashMap<>();
        this.mUnReadNotificationListener = null;
        this.mInflationCallback = new NotificationRowContentBinder.InflationCallback() {
            public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
                NotificationEntryManager.this.handleInflationException(notificationEntry.getSbn(), exc);
            }

            public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
                NotificationEntryManager.this.mPendingNotifications.remove(notificationEntry.getKey());
                if (!notificationEntry.isRowRemoved()) {
                    boolean z = NotificationEntryManager.this.getActiveNotificationUnfiltered(notificationEntry.getKey()) == null;
                    NotificationEntryManager.this.mLogger.logNotifInflated(notificationEntry.getKey(), z);
                    if (z) {
                        for (NotificationEntryListener onEntryInflated : NotificationEntryManager.this.mNotificationEntryListeners) {
                            onEntryInflated.onEntryInflated(notificationEntry);
                        }
                        NotificationEntryManager.this.addActiveNotification(notificationEntry);
                        NotificationEntryManager.this.updateNotifications("onAsyncInflationFinished");
                        for (NotificationEntryListener onNotificationAdded : NotificationEntryManager.this.mNotificationEntryListeners) {
                            onNotificationAdded.onNotificationAdded(notificationEntry);
                        }
                    } else {
                        for (NotificationEntryListener onEntryReinflated : NotificationEntryManager.this.mNotificationEntryListeners) {
                            onEntryReinflated.onEntryReinflated(notificationEntry);
                        }
                    }
                    if (!NotificationEntryManager.this.mNotificationFilter.shouldFilterOut(notificationEntry) && !NotificationEntryManager.this.mOldNotifications.remove(notificationEntry.getKey()) && !notificationEntry.getSbn().getNotification().isGroupSummary()) {
                        NotificationEntryManager.this.mNewNotifications.put(notificationEntry.getKey(), notificationEntry);
                        NotificationEntryManager.this.notifyUnReadNotificationSizeChanged();
                    }
                }
            }
        };
        this.mNotifListener = new NotificationListener.NotificationHandler() {
            public void onNotificationsInitialized() {
            }

            public void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
                if (NotificationEntryManager.this.mActiveNotifications.containsKey(statusBarNotification.getKey())) {
                    NotificationEntryManager.this.updateNotification(statusBarNotification, rankingMap);
                } else {
                    NotificationEntryManager.this.addNotification(statusBarNotification, rankingMap);
                }
            }

            public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
                NotificationEntryManager.this.removeNotification(statusBarNotification.getKey(), rankingMap, i);
            }

            public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
                NotificationEntryManager.this.updateNotificationRanking(rankingMap);
            }
        };
        this.mLogger = notificationEntryManagerLogger;
        this.mGroupManager = notificationGroupManagerLegacy;
        this.mFeatureFlags = featureFlags;
        this.mNotificationRowBinderLazy = lazy;
        this.mRemoteInputManagerLazy = lazy2;
        this.mLeakDetector = leakDetector;
        this.mFgsFeatureController = foregroundServiceDismissalFeatureController;
        this.mStatusBarService = iStatusBarService;
    }

    public void attach(NotificationListener notificationListener) {
        for (StatusBarNotification key : notificationListener.getActiveNotifications()) {
            this.mOldNotifications.add(key.getKey());
        }
        this.mNotificationFilter = (NotificationFilter) Dependency.get(NotificationFilter.class);
        notificationListener.addNotificationHandler(this.mNotifListener);
    }

    public void setRanker(LegacyNotificationRanker legacyNotificationRanker) {
        this.mRanker = legacyNotificationRanker;
    }

    public void addNotificationEntryListener(NotificationEntryListener notificationEntryListener) {
        this.mNotificationEntryListeners.add(notificationEntryListener);
    }

    public void removeNotificationEntryListener(NotificationEntryListener notificationEntryListener) {
        this.mNotificationEntryListeners.remove(notificationEntryListener);
    }

    public void addNotificationRemoveInterceptor(NotificationRemoveInterceptor notificationRemoveInterceptor) {
        this.mRemoveInterceptors.add(notificationRemoveInterceptor);
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter) {
        this.mPresenter = notificationPresenter;
    }

    public void addNotificationLifetimeExtenders(List<NotificationLifetimeExtender> list) {
        for (NotificationLifetimeExtender addNotificationLifetimeExtender : list) {
            addNotificationLifetimeExtender(addNotificationLifetimeExtender);
        }
    }

    public void addNotificationLifetimeExtender(NotificationLifetimeExtender notificationLifetimeExtender) {
        this.mNotificationLifetimeExtenders.add(notificationLifetimeExtender);
        notificationLifetimeExtender.setCallback(new NotificationEntryManager$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addNotificationLifetimeExtender$0(String str) {
        removeNotification(str, this.mLatestRankingMap, 0);
    }

    public void onChangeAllowed() {
        updateNotifications("reordering is now allowed");
    }

    public void performRemoveNotification(StatusBarNotification statusBarNotification, DismissedByUserStats dismissedByUserStats, int i) {
        removeNotificationInternal(statusBarNotification.getKey(), (NotificationListenerService.RankingMap) null, dismissedByUserStats.notificationVisibility, false, dismissedByUserStats, i);
    }

    private NotificationVisibility obtainVisibility(String str) {
        NotificationEntry notificationEntry = this.mActiveNotifications.get(str);
        return NotificationVisibility.obtain(str, notificationEntry != null ? notificationEntry.getRanking().getRank() : 0, this.mActiveNotifications.size(), true, NotificationLogger.getNotificationLocation(getActiveNotificationUnfiltered(str)));
    }

    private void abortExistingInflation(String str, String str2) {
        if (this.mPendingNotifications.containsKey(str)) {
            NotificationEntry notificationEntry = this.mPendingNotifications.get(str);
            notificationEntry.abortTask();
            this.mPendingNotifications.remove(str);
            for (NotifCollectionListener onEntryCleanUp : this.mNotifCollectionListeners) {
                onEntryCleanUp.onEntryCleanUp(notificationEntry);
            }
            this.mLogger.logInflationAborted(str, "pending", str2);
        }
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered != null) {
            activeNotificationUnfiltered.abortTask();
            this.mLogger.logInflationAborted(str, "active", str2);
        }
    }

    /* access modifiers changed from: private */
    public void handleInflationException(StatusBarNotification statusBarNotification, Exception exc) {
        removeNotificationInternal(statusBarNotification.getKey(), (NotificationListenerService.RankingMap) null, (NotificationVisibility) null, true, (DismissedByUserStats) null, 4);
        for (NotificationEntryListener onInflationError : this.mNotificationEntryListeners) {
            onInflationError.onInflationError(statusBarNotification, exc);
        }
    }

    /* access modifiers changed from: private */
    public void addActiveNotification(NotificationEntry notificationEntry) {
        Assert.isMainThread();
        this.mActiveNotifications.put(notificationEntry.getKey(), notificationEntry);
        this.mGroupManager.onEntryAdded(notificationEntry);
        updateRankingAndSort(this.mRanker.getRankingMap(), "addEntryInternalInternal");
    }

    @VisibleForTesting
    public void addActiveNotificationForTest(NotificationEntry notificationEntry) {
        this.mActiveNotifications.put(notificationEntry.getKey(), notificationEntry);
        this.mGroupManager.onEntryAdded(notificationEntry);
        reapplyFilterAndSort("addVisibleNotification");
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void removeNotification(String str, NotificationListenerService.RankingMap rankingMap, int i) {
        removeNotificationInternal(str, rankingMap, obtainVisibility(str), false, (DismissedByUserStats) null, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:71:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void removeNotificationInternal(java.lang.String r7, android.service.notification.NotificationListenerService.RankingMap r8, com.android.internal.statusbar.NotificationVisibility r9, boolean r10, com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats r11, int r12) {
        /*
            r6 = this;
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r6.getActiveNotificationUnfiltered(r7)
            java.util.List<com.android.systemui.statusbar.NotificationRemoveInterceptor> r1 = r6.mRemoveInterceptors
            java.util.Iterator r1 = r1.iterator()
        L_0x000a:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0022
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.NotificationRemoveInterceptor r2 = (com.android.systemui.statusbar.NotificationRemoveInterceptor) r2
            boolean r2 = r2.onNotificationRemoveRequested(r7, r0, r12)
            if (r2 == 0) goto L_0x000a
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r6 = r6.mLogger
            r6.logRemovalIntercepted(r7)
            return
        L_0x0022:
            java.lang.String r1 = "removeNotification"
            r2 = 1
            r3 = 0
            if (r0 != 0) goto L_0x006f
            java.util.HashMap<java.lang.String, com.android.systemui.statusbar.notification.collection.NotificationEntry> r8 = r6.mPendingNotifications
            java.lang.Object r8 = r8.get(r7)
            com.android.systemui.statusbar.notification.collection.NotificationEntry r8 = (com.android.systemui.statusbar.notification.collection.NotificationEntry) r8
            if (r8 == 0) goto L_0x0135
            java.util.ArrayList<com.android.systemui.statusbar.NotificationLifetimeExtender> r9 = r6.mNotificationLifetimeExtenders
            java.util.Iterator r9 = r9.iterator()
        L_0x0038:
            boolean r10 = r9.hasNext()
            if (r10 == 0) goto L_0x005e
            java.lang.Object r10 = r9.next()
            com.android.systemui.statusbar.NotificationLifetimeExtender r10 = (com.android.systemui.statusbar.NotificationLifetimeExtender) r10
            boolean r11 = r10.shouldExtendLifetimeForPendingNotification(r8)
            if (r11 == 0) goto L_0x0038
            r6.extendLifetime(r8, r10)
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r11 = r6.mLogger
            java.lang.Class r10 = r10.getClass()
            java.lang.String r10 = r10.getName()
            java.lang.String r12 = "pending"
            r11.logLifetimeExtended(r7, r10, r12)
            r3 = r2
            goto L_0x0038
        L_0x005e:
            if (r3 != 0) goto L_0x0135
            r6.abortExistingInflation(r7, r1)
            java.util.Set<com.android.systemui.statusbar.notification.collection.NotificationEntry> r7 = r6.mAllNotifications
            r7.remove(r8)
            com.android.systemui.util.leak.LeakDetector r6 = r6.mLeakDetector
            r6.trackGarbage(r8)
            goto L_0x0135
        L_0x006f:
            boolean r4 = r0.isRowDismissed()
            if (r10 != 0) goto L_0x00a5
            if (r4 != 0) goto L_0x00a5
            java.util.ArrayList<com.android.systemui.statusbar.NotificationLifetimeExtender> r10 = r6.mNotificationLifetimeExtenders
            java.util.Iterator r10 = r10.iterator()
        L_0x007d:
            boolean r4 = r10.hasNext()
            if (r4 == 0) goto L_0x00a5
            java.lang.Object r4 = r10.next()
            com.android.systemui.statusbar.NotificationLifetimeExtender r4 = (com.android.systemui.statusbar.NotificationLifetimeExtender) r4
            boolean r5 = r4.shouldExtendLifetime(r0)
            if (r5 == 0) goto L_0x007d
            r6.mLatestRankingMap = r8
            r6.extendLifetime(r0, r4)
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r8 = r6.mLogger
            java.lang.Class r10 = r4.getClass()
            java.lang.String r10 = r10.getName()
            java.lang.String r4 = "active"
            r8.logLifetimeExtended(r7, r10, r4)
            r8 = r2
            goto L_0x00a6
        L_0x00a5:
            r8 = r3
        L_0x00a6:
            if (r8 != 0) goto L_0x0135
            r6.abortExistingInflation(r7, r1)
            java.util.Set<com.android.systemui.statusbar.notification.collection.NotificationEntry> r8 = r6.mAllNotifications
            r8.remove(r0)
            r6.cancelLifetimeExtension(r0)
            boolean r8 = r0.rowExists()
            if (r8 == 0) goto L_0x00bc
            r0.removeRow()
        L_0x00bc:
            r6.handleGroupSummaryRemoved(r7)
            r6.removeVisibleNotification(r7)
            java.lang.String r7 = "removeNotificationInternal"
            r6.updateNotifications(r7)
            if (r11 == 0) goto L_0x00ca
            goto L_0x00cb
        L_0x00ca:
            r2 = r3
        L_0x00cb:
            com.android.systemui.statusbar.notification.NotificationEntryManagerLogger r7 = r6.mLogger
            java.lang.String r8 = r0.getKey()
            r7.logNotifRemoved(r8, r2)
            if (r2 == 0) goto L_0x00df
            if (r9 == 0) goto L_0x00df
            android.service.notification.StatusBarNotification r7 = r0.getSbn()
            r6.sendNotificationRemovalToServer(r7, r11)
        L_0x00df:
            java.util.List<com.android.systemui.statusbar.notification.NotificationEntryListener> r7 = r6.mNotificationEntryListeners
            java.util.Iterator r7 = r7.iterator()
        L_0x00e5:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x00f5
            java.lang.Object r8 = r7.next()
            com.android.systemui.statusbar.notification.NotificationEntryListener r8 = (com.android.systemui.statusbar.notification.NotificationEntryListener) r8
            r8.onEntryRemoved(r0, r9, r2, r12)
            goto L_0x00e5
        L_0x00f5:
            java.util.List<com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener> r7 = r6.mNotifCollectionListeners
            java.util.Iterator r7 = r7.iterator()
        L_0x00fb:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x010b
            java.lang.Object r8 = r7.next()
            com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener r8 = (com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener) r8
            r8.onEntryRemoved(r0, r3)
            goto L_0x00fb
        L_0x010b:
            java.util.List<com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener> r7 = r6.mNotifCollectionListeners
            java.util.Iterator r7 = r7.iterator()
        L_0x0111:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x0121
            java.lang.Object r8 = r7.next()
            com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener r8 = (com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener) r8
            r8.onEntryCleanUp(r0)
            goto L_0x0111
        L_0x0121:
            java.util.HashMap<java.lang.String, com.android.systemui.statusbar.notification.collection.NotificationEntry> r7 = r6.mNewNotifications
            java.lang.String r8 = r0.getKey()
            java.lang.Object r7 = r7.remove(r8)
            if (r7 == 0) goto L_0x0130
            r6.notifyUnReadNotificationSizeChanged()
        L_0x0130:
            com.android.systemui.util.leak.LeakDetector r6 = r6.mLeakDetector
            r6.trackGarbage(r0)
        L_0x0135:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.NotificationEntryManager.removeNotificationInternal(java.lang.String, android.service.notification.NotificationListenerService$RankingMap, com.android.internal.statusbar.NotificationVisibility, boolean, com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats, int):void");
    }

    private void sendNotificationRemovalToServer(StatusBarNotification statusBarNotification, DismissedByUserStats dismissedByUserStats) {
        try {
            this.mStatusBarService.onNotificationClear(statusBarNotification.getPackageName(), statusBarNotification.getUser().getIdentifier(), statusBarNotification.getKey(), dismissedByUserStats.dismissalSurface, dismissedByUserStats.dismissalSentiment, dismissedByUserStats.notificationVisibility);
        } catch (RemoteException unused) {
        }
    }

    private void handleGroupSummaryRemoved(String str) {
        List<NotificationEntry> attachedNotifChildren;
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(str);
        if (activeNotificationUnfiltered != null && activeNotificationUnfiltered.rowExists() && activeNotificationUnfiltered.isSummaryWithChildren()) {
            if ((activeNotificationUnfiltered.getSbn().getOverrideGroupKey() == null || activeNotificationUnfiltered.isRowDismissed()) && (attachedNotifChildren = activeNotificationUnfiltered.getAttachedNotifChildren()) != null) {
                for (int i = 0; i < attachedNotifChildren.size(); i++) {
                    NotificationEntry notificationEntry = attachedNotifChildren.get(i);
                    boolean z = (activeNotificationUnfiltered.getSbn().getNotification().flags & 64) != 0;
                    boolean z2 = this.mRemoteInputManagerLazy.get().shouldKeepForRemoteInputHistory(notificationEntry) || this.mRemoteInputManagerLazy.get().shouldKeepForSmartReplyHistory(notificationEntry);
                    if (!z && !z2) {
                        notificationEntry.setKeepInParent(true);
                        notificationEntry.removeRow();
                    }
                }
            }
        }
    }

    private void addNotificationInternal(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) throws InflationException {
        String key = statusBarNotification.getKey();
        if (DEBUG) {
            Log.d("NotificationEntryMgr", "addNotification key=" + key);
        }
        updateRankingAndSort(rankingMap, "addNotificationInternal");
        NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
        rankingMap.getRanking(key, ranking);
        NotificationEntry notificationEntry = this.mPendingNotifications.get(key);
        if (notificationEntry != null) {
            notificationEntry.setSbn(statusBarNotification);
            notificationEntry.setRanking(ranking);
        } else {
            notificationEntry = new NotificationEntry(statusBarNotification, ranking, this.mFgsFeatureController.isForegroundServiceDismissalEnabled(), SystemClock.uptimeMillis());
            this.mAllNotifications.add(notificationEntry);
            this.mLeakDetector.trackInstance(notificationEntry);
            for (NotifCollectionListener onEntryInit : this.mNotifCollectionListeners) {
                onEntryInit.onEntryInit(notificationEntry);
            }
        }
        for (NotifCollectionListener onEntryBind : this.mNotifCollectionListeners) {
            onEntryBind.onEntryBind(notificationEntry, statusBarNotification);
        }
        if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mNotificationRowBinderLazy.get().inflateViews(notificationEntry, this.mInflationCallback);
        }
        this.mPendingNotifications.put(key, notificationEntry);
        this.mLogger.logNotifAdded(notificationEntry.getKey());
        for (NotificationEntryListener onPendingEntryAdded : this.mNotificationEntryListeners) {
            onPendingEntryAdded.onPendingEntryAdded(notificationEntry);
        }
        for (NotifCollectionListener onEntryAdded : this.mNotifCollectionListeners) {
            onEntryAdded.onEntryAdded(notificationEntry);
        }
        for (NotifCollectionListener onRankingApplied : this.mNotifCollectionListeners) {
            onRankingApplied.onRankingApplied();
        }
    }

    public void addNotification(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        try {
            addNotificationInternal(statusBarNotification, rankingMap);
        } catch (InflationException e) {
            handleInflationException(statusBarNotification, e);
        }
    }

    private void updateNotificationInternal(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) throws InflationException {
        if (DEBUG) {
            Log.d("NotificationEntryMgr", "updateNotification(" + statusBarNotification + ")");
        }
        String key = statusBarNotification.getKey();
        abortExistingInflation(key, "updateNotification");
        NotificationEntry activeNotificationUnfiltered = getActiveNotificationUnfiltered(key);
        if (activeNotificationUnfiltered != null) {
            cancelLifetimeExtension(activeNotificationUnfiltered);
            updateRankingAndSort(rankingMap, "updateNotificationInternal");
            StatusBarNotification sbn = activeNotificationUnfiltered.getSbn();
            activeNotificationUnfiltered.setSbn(statusBarNotification);
            for (NotifCollectionListener onEntryBind : this.mNotifCollectionListeners) {
                onEntryBind.onEntryBind(activeNotificationUnfiltered, statusBarNotification);
            }
            this.mGroupManager.onEntryUpdated(activeNotificationUnfiltered, sbn);
            this.mLogger.logNotifUpdated(activeNotificationUnfiltered.getKey());
            for (NotificationEntryListener onPreEntryUpdated : this.mNotificationEntryListeners) {
                onPreEntryUpdated.onPreEntryUpdated(activeNotificationUnfiltered);
            }
            for (NotifCollectionListener onEntryUpdated : this.mNotifCollectionListeners) {
                onEntryUpdated.onEntryUpdated(activeNotificationUnfiltered);
            }
            if (!this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
                this.mNotificationRowBinderLazy.get().inflateViews(activeNotificationUnfiltered, this.mInflationCallback);
            }
            updateNotifications("updateNotificationInternal");
            for (NotificationEntryListener onPostEntryUpdated : this.mNotificationEntryListeners) {
                onPostEntryUpdated.onPostEntryUpdated(activeNotificationUnfiltered);
            }
            for (NotifCollectionListener onRankingApplied : this.mNotifCollectionListeners) {
                onRankingApplied.onRankingApplied();
            }
        }
    }

    public void updateNotification(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
        try {
            updateNotificationInternal(statusBarNotification, rankingMap);
        } catch (InflationException e) {
            handleInflationException(statusBarNotification, e);
        }
    }

    public void updateNotifications(String str) {
        reapplyFilterAndSort(str);
        if (this.mPresenter != null && !this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mPresenter.updateNotificationViews(str);
        }
    }

    public void updateNotificationRanking(NotificationListenerService.RankingMap rankingMap) {
        ArrayList<NotificationEntry> arrayList = new ArrayList<>();
        arrayList.addAll(getVisibleNotifications());
        arrayList.addAll(this.mPendingNotifications.values());
        ArrayMap arrayMap = new ArrayMap();
        ArrayMap arrayMap2 = new ArrayMap();
        for (NotificationEntry notificationEntry : arrayList) {
            arrayMap.put(notificationEntry.getKey(), NotificationUiAdjustment.extractFromNotificationEntry(notificationEntry));
            arrayMap2.put(notificationEntry.getKey(), Integer.valueOf(notificationEntry.getImportance()));
        }
        updateRankingAndSort(rankingMap, "updateNotificationRanking");
        updateRankingOfPendingNotifications(rankingMap);
        for (NotificationEntry notificationEntry2 : arrayList) {
            this.mNotificationRowBinderLazy.get().onNotificationRankingUpdated(notificationEntry2, (Integer) arrayMap2.get(notificationEntry2.getKey()), (NotificationUiAdjustment) arrayMap.get(notificationEntry2.getKey()), NotificationUiAdjustment.extractFromNotificationEntry(notificationEntry2), this.mInflationCallback);
        }
        updateNotifications("updateNotificationRanking");
        for (NotificationEntryListener onNotificationRankingUpdated : this.mNotificationEntryListeners) {
            onNotificationRankingUpdated.onNotificationRankingUpdated(rankingMap);
        }
        for (NotifCollectionListener onRankingUpdate : this.mNotifCollectionListeners) {
            onRankingUpdate.onRankingUpdate(rankingMap);
        }
        for (NotifCollectionListener onRankingApplied : this.mNotifCollectionListeners) {
            onRankingApplied.onRankingApplied();
        }
    }

    private void updateRankingOfPendingNotifications(NotificationListenerService.RankingMap rankingMap) {
        if (rankingMap != null) {
            for (NotificationEntry next : this.mPendingNotifications.values()) {
                NotificationListenerService.Ranking ranking = new NotificationListenerService.Ranking();
                if (rankingMap.getRanking(next.getKey(), ranking)) {
                    next.setRanking(ranking);
                }
            }
        }
    }

    public Iterable<NotificationEntry> getPendingNotificationsIterator() {
        return this.mPendingNotifications.values();
    }

    public NotificationEntry getActiveNotificationUnfiltered(String str) {
        return this.mActiveNotifications.get(str);
    }

    public NotificationEntry getPendingOrActiveNotif(String str) {
        if (this.mPendingNotifications.containsKey(str)) {
            return this.mPendingNotifications.get(str);
        }
        return this.mActiveNotifications.get(str);
    }

    private void extendLifetime(NotificationEntry notificationEntry, NotificationLifetimeExtender notificationLifetimeExtender) {
        NotificationLifetimeExtender notificationLifetimeExtender2 = this.mRetainedNotifications.get(notificationEntry);
        if (!(notificationLifetimeExtender2 == null || notificationLifetimeExtender2 == notificationLifetimeExtender)) {
            notificationLifetimeExtender2.setShouldManageLifetime(notificationEntry, false);
        }
        this.mRetainedNotifications.put(notificationEntry, notificationLifetimeExtender);
        notificationLifetimeExtender.setShouldManageLifetime(notificationEntry, true);
    }

    private void cancelLifetimeExtension(NotificationEntry notificationEntry) {
        NotificationLifetimeExtender remove = this.mRetainedNotifications.remove(notificationEntry);
        if (remove != null) {
            remove.setShouldManageLifetime(notificationEntry, false);
        }
    }

    private void removeVisibleNotification(String str) {
        Assert.isMainThread();
        NotificationEntry remove = this.mActiveNotifications.remove(str);
        if (remove != null) {
            this.mGroupManager.onEntryRemoved(remove);
        }
    }

    public List<NotificationEntry> getActiveNotificationsForCurrentUser() {
        Assert.isMainThread();
        ArrayList arrayList = new ArrayList();
        int size = this.mActiveNotifications.size();
        for (int i = 0; i < size; i++) {
            NotificationEntry valueAt = this.mActiveNotifications.valueAt(i);
            if (this.mRanker.isNotificationForCurrentProfiles(valueAt)) {
                arrayList.add(valueAt);
            }
        }
        return arrayList;
    }

    public void reapplyFilterAndSort(String str) {
        if ("user switched".equals(str)) {
            resetUnReadNotificationSize();
        }
        updateRankingAndSort(this.mRanker.getRankingMap(), str);
    }

    private void updateRankingAndSort(NotificationListenerService.RankingMap rankingMap, String str) {
        this.mSortedAndFiltered.clear();
        this.mSortedAndFiltered.addAll(this.mRanker.updateRanking(rankingMap, this.mActiveNotifications.values(), str));
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println("NotificationEntryManager");
        int size = this.mSortedAndFiltered.size();
        printWriter.print(str);
        printWriter.println("active notifications: " + size);
        int i = 0;
        while (i < size) {
            dumpEntry(printWriter, str, i, this.mSortedAndFiltered.get(i));
            i++;
        }
        synchronized (this.mActiveNotifications) {
            int size2 = this.mActiveNotifications.size();
            printWriter.print(str);
            printWriter.println("inactive notifications: " + (size2 - i));
            int i2 = 0;
            for (int i3 = 0; i3 < size2; i3++) {
                NotificationEntry valueAt = this.mActiveNotifications.valueAt(i3);
                if (!this.mSortedAndFiltered.contains(valueAt)) {
                    dumpEntry(printWriter, str, i2, valueAt);
                    i2++;
                }
            }
        }
    }

    private void dumpEntry(PrintWriter printWriter, String str, int i, NotificationEntry notificationEntry) {
        printWriter.print(str);
        printWriter.println("  [" + i + "] key=" + notificationEntry.getKey() + " icon=" + notificationEntry.getIcons().getStatusBarIcon());
        StatusBarNotification sbn = notificationEntry.getSbn();
        printWriter.print(str);
        printWriter.println("      pkg=" + sbn.getPackageName() + " id=" + sbn.getId() + " importance=" + notificationEntry.getRanking().getImportance());
        printWriter.print(str);
        StringBuilder sb = new StringBuilder();
        sb.append("      notification=");
        sb.append(sbn.getNotification());
        printWriter.println(sb.toString());
    }

    public List<NotificationEntry> getVisibleNotifications() {
        return this.mReadOnlyNotifications;
    }

    public Collection<NotificationEntry> getAllNotifs() {
        return this.mReadOnlyAllNotifications;
    }

    public int getActiveNotificationsCount() {
        return this.mReadOnlyNotifications.size();
    }

    public boolean hasActiveNotifications() {
        return this.mReadOnlyNotifications.size() != 0;
    }

    public void addCollectionListener(NotifCollectionListener notifCollectionListener) {
        this.mNotifCollectionListeners.add(notifCollectionListener);
    }

    public int getUnReadNotificationSize() {
        return this.mNewNotifications.size();
    }

    public void resetUnReadNotificationSize() {
        this.mNewNotifications.clear();
        notifyUnReadNotificationSizeChanged();
    }

    /* access modifiers changed from: private */
    public void notifyUnReadNotificationSizeChanged() {
        UnReadNotificationListener unReadNotificationListener = this.mUnReadNotificationListener;
        if (unReadNotificationListener != null) {
            unReadNotificationListener.onUnReadNotificationSizeChanged(this.mNewNotifications.size());
        }
    }

    public void setUnReadNotificationListener(UnReadNotificationListener unReadNotificationListener) {
        this.mUnReadNotificationListener = unReadNotificationListener;
    }
}
