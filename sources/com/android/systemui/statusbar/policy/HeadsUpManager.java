package com.android.systemui.statusbar.policy;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.Dependency;
import com.android.systemui.EventLogTags;
import com.android.systemui.R$integer;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.motorola.internal.app.MotoDesktopManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class HeadsUpManager extends AlertingNotificationManager {
    /* access modifiers changed from: private */
    public final AccessibilityManagerWrapper mAccessibilityMgr;
    protected final Context mContext;
    protected boolean mHasPinnedNotification;
    protected final HashSet<OnHeadsUpChangedListener> mListeners = new HashSet<>();
    protected int mSnoozeLengthMs;
    private final ArrayMap<String, Long> mSnoozedPackages;
    protected int mTouchAcceptanceDelay;
    private final UiEventLogger mUiEventLogger;
    protected int mUser;

    public int getContentFlag() {
        return 4;
    }

    public boolean isEntryAutoHeadsUpped(String str) {
        return false;
    }

    public boolean isTrackingHeadsUp() {
        return false;
    }

    public void onDensityOrFontScaleChanged() {
    }

    enum NotificationPeekEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_PEEK(801);
        
        private final int mId;

        private NotificationPeekEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public HeadsUpManager(final Context context) {
        this.mContext = context;
        this.mAccessibilityMgr = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
        this.mUiEventLogger = (UiEventLogger) Dependency.get(UiEventLogger.class);
        Resources resources = context.getResources();
        this.mMinimumDisplayTime = resources.getInteger(R$integer.heads_up_notification_minimum_time);
        this.mAutoDismissNotificationDecay = resources.getInteger(R$integer.heads_up_notification_decay);
        this.mTouchAcceptanceDelay = resources.getInteger(R$integer.touch_acceptance_delay);
        this.mSnoozedPackages = new ArrayMap<>();
        this.mSnoozeLengthMs = Settings.Global.getInt(context.getContentResolver(), "heads_up_snooze_length_ms", resources.getInteger(R$integer.heads_up_default_snooze_length_ms));
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("heads_up_snooze_length_ms"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                int i = Settings.Global.getInt(context.getContentResolver(), "heads_up_snooze_length_ms", -1);
                if (i > -1) {
                    HeadsUpManager headsUpManager = HeadsUpManager.this;
                    if (i != headsUpManager.mSnoozeLengthMs) {
                        headsUpManager.mSnoozeLengthMs = i;
                        if (Log.isLoggable("HeadsUpManager", 2)) {
                            Log.v("HeadsUpManager", "mSnoozeLengthMs = " + HeadsUpManager.this.mSnoozeLengthMs);
                        }
                    }
                }
            }
        });
    }

    public void addListener(OnHeadsUpChangedListener onHeadsUpChangedListener) {
        this.mListeners.add(onHeadsUpChangedListener);
    }

    public void removeListener(OnHeadsUpChangedListener onHeadsUpChangedListener) {
        this.mListeners.remove(onHeadsUpChangedListener);
    }

    public void updateNotification(String str, boolean z) {
        super.updateNotification(str, z);
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(str);
        if (z && headsUpEntry != null) {
            setEntryPinned(headsUpEntry, shouldHeadsUpBecomePinned(headsUpEntry.mEntry));
        }
    }

    public void showNotification(NotificationEntry notificationEntry) {
        super.showNotification(notificationEntry);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        if (MotoDesktopManager.isDesktopConnected(this.mContext) && hasFullScreenIntent(notificationEntry) && Display.isOffState(windowManager.getDefaultDisplay().getState()) && powerManager != null && powerManager.isInteractive()) {
            powerManager.wakeUp(SystemClock.uptimeMillis(), "HeadsUpManager");
            powerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldHeadsUpBecomePinned(NotificationEntry notificationEntry) {
        return hasFullScreenIntent(notificationEntry);
    }

    /* access modifiers changed from: protected */
    public boolean hasFullScreenIntent(NotificationEntry notificationEntry) {
        return notificationEntry.getSbn().getNotification().fullScreenIntent != null;
    }

    /* access modifiers changed from: protected */
    public void setEntryPinned(HeadsUpEntry headsUpEntry, boolean z) {
        if (Log.isLoggable("HeadsUpManager", 2)) {
            Log.v("HeadsUpManager", "setEntryPinned: " + z);
        }
        NotificationEntry notificationEntry = headsUpEntry.mEntry;
        if (notificationEntry.isRowPinned() != z) {
            notificationEntry.setRowPinned(z);
            updatePinnedMode();
            if (z && notificationEntry.getSbn() != null) {
                this.mUiEventLogger.logWithInstanceId(NotificationPeekEvent.NOTIFICATION_PEEK, notificationEntry.getSbn().getUid(), notificationEntry.getSbn().getPackageName(), notificationEntry.getSbn().getInstanceId());
            }
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                OnHeadsUpChangedListener onHeadsUpChangedListener = (OnHeadsUpChangedListener) it.next();
                if (z) {
                    onHeadsUpChangedListener.onHeadsUpPinned(notificationEntry);
                } else {
                    onHeadsUpChangedListener.onHeadsUpUnPinned(notificationEntry);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAlertEntryAdded(AlertingNotificationManager.AlertEntry alertEntry) {
        NotificationEntry notificationEntry = alertEntry.mEntry;
        notificationEntry.setHeadsUp(true);
        setEntryPinned((HeadsUpEntry) alertEntry, shouldHeadsUpBecomePinned(notificationEntry));
        EventLogTags.writeSysuiHeadsUpStatus(notificationEntry.getKey(), 1);
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((OnHeadsUpChangedListener) it.next()).onHeadsUpStateChanged(notificationEntry, true);
        }
    }

    /* access modifiers changed from: protected */
    public void onAlertEntryRemoved(AlertingNotificationManager.AlertEntry alertEntry) {
        NotificationEntry notificationEntry = alertEntry.mEntry;
        notificationEntry.setHeadsUp(false);
        setEntryPinned((HeadsUpEntry) alertEntry, false);
        EventLogTags.writeSysuiHeadsUpStatus(notificationEntry.getKey(), 0);
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((OnHeadsUpChangedListener) it.next()).onHeadsUpStateChanged(notificationEntry, false);
        }
    }

    /* access modifiers changed from: protected */
    public void updatePinnedMode() {
        boolean hasPinnedNotificationInternal = hasPinnedNotificationInternal();
        if (hasPinnedNotificationInternal != this.mHasPinnedNotification) {
            if (Log.isLoggable("HeadsUpManager", 2)) {
                Log.v("HeadsUpManager", "Pinned mode changed: " + this.mHasPinnedNotification + " -> " + hasPinnedNotificationInternal);
            }
            this.mHasPinnedNotification = hasPinnedNotificationInternal;
            if (hasPinnedNotificationInternal) {
                MetricsLogger.count(this.mContext, "note_peek", 1);
            }
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                ((OnHeadsUpChangedListener) it.next()).onHeadsUpPinnedModeChanged(hasPinnedNotificationInternal);
            }
        }
    }

    public boolean isSnoozed(String str) {
        String snoozeKey = snoozeKey(str, this.mUser);
        Long l = this.mSnoozedPackages.get(snoozeKey);
        if (l == null) {
            return false;
        }
        if (l.longValue() <= this.mClock.currentTimeMillis()) {
            this.mSnoozedPackages.remove(str);
            return false;
        } else if (!Log.isLoggable("HeadsUpManager", 2)) {
            return true;
        } else {
            Log.v("HeadsUpManager", snoozeKey + " snoozed");
            return true;
        }
    }

    public void snooze() {
        for (String headsUpEntry : this.mAlertEntries.keySet()) {
            this.mSnoozedPackages.put(snoozeKey(getHeadsUpEntry(headsUpEntry).mEntry.getSbn().getPackageName(), this.mUser), Long.valueOf(this.mClock.currentTimeMillis() + ((long) this.mSnoozeLengthMs)));
        }
    }

    private static String snoozeKey(String str, int i) {
        return i + "," + str;
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry getHeadsUpEntry(String str) {
        return (HeadsUpEntry) this.mAlertEntries.get(str);
    }

    public NotificationEntry getTopEntry() {
        HeadsUpEntry topHeadsUpEntry = getTopHeadsUpEntry();
        if (topHeadsUpEntry != null) {
            return topHeadsUpEntry.mEntry;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry getTopHeadsUpEntry() {
        HeadsUpEntry headsUpEntry = null;
        if (this.mAlertEntries.isEmpty()) {
            return null;
        }
        for (AlertingNotificationManager.AlertEntry next : this.mAlertEntries.values()) {
            if (headsUpEntry == null || next.compareTo((AlertingNotificationManager.AlertEntry) headsUpEntry) < 0) {
                headsUpEntry = (HeadsUpEntry) next;
            }
        }
        return headsUpEntry;
    }

    public void setUser(int i) {
        this.mUser = i;
    }

    /* access modifiers changed from: protected */
    public void dumpInternal(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("  mTouchAcceptanceDelay=");
        printWriter.println(this.mTouchAcceptanceDelay);
        printWriter.print("  mSnoozeLengthMs=");
        printWriter.println(this.mSnoozeLengthMs);
        printWriter.print("  now=");
        printWriter.println(this.mClock.currentTimeMillis());
        printWriter.print("  mUser=");
        printWriter.println(this.mUser);
        for (AlertingNotificationManager.AlertEntry alertEntry : this.mAlertEntries.values()) {
            printWriter.print("  HeadsUpEntry=");
            printWriter.println(alertEntry.mEntry);
        }
        int size = this.mSnoozedPackages.size();
        printWriter.println("  snoozed packages: " + size);
        for (int i = 0; i < size; i++) {
            printWriter.print("    ");
            printWriter.print(this.mSnoozedPackages.valueAt(i));
            printWriter.print(", ");
            printWriter.println(this.mSnoozedPackages.keyAt(i));
        }
    }

    public boolean hasPinnedHeadsUp() {
        return this.mHasPinnedNotification;
    }

    private boolean hasPinnedNotificationInternal() {
        for (String headsUpEntry : this.mAlertEntries.keySet()) {
            if (getHeadsUpEntry(headsUpEntry).mEntry.isRowPinned()) {
                return true;
            }
        }
        return false;
    }

    public void unpinAll(boolean z) {
        NotificationEntry notificationEntry;
        for (String headsUpEntry : this.mAlertEntries.keySet()) {
            HeadsUpEntry headsUpEntry2 = getHeadsUpEntry(headsUpEntry);
            setEntryPinned(headsUpEntry2, false);
            headsUpEntry2.updateEntry(false);
            if (z && (notificationEntry = headsUpEntry2.mEntry) != null && notificationEntry.mustStayOnScreen()) {
                headsUpEntry2.mEntry.setHeadsUpIsVisible();
            }
        }
    }

    public int compare(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(notificationEntry.getKey());
        HeadsUpEntry headsUpEntry2 = getHeadsUpEntry(notificationEntry2.getKey());
        if (headsUpEntry == null || headsUpEntry2 == null) {
            return headsUpEntry == null ? 1 : -1;
        }
        return headsUpEntry.compareTo((AlertingNotificationManager.AlertEntry) headsUpEntry2);
    }

    public void setExpanded(NotificationEntry notificationEntry, boolean z) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(notificationEntry.getKey());
        if (headsUpEntry != null && notificationEntry.isRowPinned()) {
            headsUpEntry.setExpanded(z);
        }
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry createAlertEntry() {
        return new HeadsUpEntry();
    }

    /* access modifiers changed from: private */
    public static boolean isCriticalCallNotif(NotificationEntry notificationEntry) {
        Notification notification = notificationEntry.getSbn().getNotification();
        if ((notification.isStyle(Notification.CallStyle.class) && notification.extras.getInt("android.callType") == 1) || (notificationEntry.getSbn().isOngoing() && "call".equals(notification.category))) {
            return true;
        }
        return false;
    }

    protected class HeadsUpEntry extends AlertingNotificationManager.AlertEntry {
        protected boolean expanded;
        public boolean remoteInputActive;

        protected HeadsUpEntry() {
            super();
        }

        public boolean isSticky() {
            return (this.mEntry.isRowPinned() && this.expanded) || this.remoteInputActive || HeadsUpManager.this.hasFullScreenIntent(this.mEntry);
        }

        public int compareTo(AlertingNotificationManager.AlertEntry alertEntry) {
            HeadsUpEntry headsUpEntry = (HeadsUpEntry) alertEntry;
            boolean isRowPinned = this.mEntry.isRowPinned();
            boolean isRowPinned2 = headsUpEntry.mEntry.isRowPinned();
            if (isRowPinned && !isRowPinned2) {
                return -1;
            }
            if (!isRowPinned && isRowPinned2) {
                return 1;
            }
            boolean hasFullScreenIntent = HeadsUpManager.this.hasFullScreenIntent(this.mEntry);
            boolean hasFullScreenIntent2 = HeadsUpManager.this.hasFullScreenIntent(headsUpEntry.mEntry);
            if (hasFullScreenIntent && !hasFullScreenIntent2) {
                return -1;
            }
            if (!hasFullScreenIntent && hasFullScreenIntent2) {
                return 1;
            }
            boolean access$000 = HeadsUpManager.isCriticalCallNotif(this.mEntry);
            boolean access$0002 = HeadsUpManager.isCriticalCallNotif(headsUpEntry.mEntry);
            if (access$000 && !access$0002) {
                return -1;
            }
            if (!access$000 && access$0002) {
                return 1;
            }
            boolean z = this.remoteInputActive;
            if (z && !headsUpEntry.remoteInputActive) {
                return -1;
            }
            if (z || !headsUpEntry.remoteInputActive) {
                return super.compareTo((AlertingNotificationManager.AlertEntry) headsUpEntry);
            }
            return 1;
        }

        public void setExpanded(boolean z) {
            this.expanded = z;
        }

        public void reset() {
            super.reset();
            this.expanded = false;
            this.remoteInputActive = false;
        }

        /* access modifiers changed from: protected */
        public long calculatePostTime() {
            return super.calculatePostTime() + ((long) HeadsUpManager.this.mTouchAcceptanceDelay);
        }

        /* access modifiers changed from: protected */
        public long calculateFinishTime() {
            return this.mPostTime + ((long) getRecommendedHeadsUpTimeoutMs(HeadsUpManager.this.mAutoDismissNotificationDecay));
        }

        /* access modifiers changed from: protected */
        public int getRecommendedHeadsUpTimeoutMs(int i) {
            return HeadsUpManager.this.mAccessibilityMgr.getRecommendedTimeoutMillis(i, 7);
        }
    }

    public void releaseAllImmediately() {
        NotificationEntry notificationEntry;
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            super.releaseAllImmediately();
            return;
        }
        Iterator it = new ArraySet(this.mAlertEntries.keySet()).iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            AlertingNotificationManager.AlertEntry alertEntry = this.mAlertEntries.get(str);
            if (alertEntry == null || (notificationEntry = alertEntry.mEntry) == null || !HeadsUpUtil.shouldCliHeadsUpForLock(this.mContext, notificationEntry.getSbn())) {
                removeAlertEntry(str);
            } else {
                Log.v("HeadsUpManager", "CLI Ingore release immediately=" + notificationEntry.getSbn());
            }
        }
    }
}
