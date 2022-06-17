package com.android.systemui.statusbar.notification.collection.legacy;

import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.Log;
import com.android.p011wm.shell.bubbles.Bubbles;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import com.android.systemui.statusbar.notification.collection.render.GroupMembershipManager;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

public class NotificationGroupManagerLegacy implements OnHeadsUpChangedListener, StatusBarStateController.StateListener, GroupMembershipManager, GroupExpansionManager, Dumpable {
    private int mBarState = -1;
    private final Optional<Bubbles> mBubblesOptional;
    private final EventBuffer mEventBuffer = new EventBuffer();
    private final ArraySet<GroupExpansionManager.OnGroupExpansionChangeListener> mExpansionChangeListeners = new ArraySet<>();
    /* access modifiers changed from: private */
    public final ArraySet<OnGroupChangeListener> mGroupChangeListeners = new ArraySet<>();
    /* access modifiers changed from: private */
    public final HashMap<String, NotificationGroup> mGroupMap = new HashMap<>();
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsUpdatingUnchangedGroup;
    private HashMap<String, StatusBarNotification> mIsolatedEntries = new HashMap<>();
    private final Lazy<PeopleNotificationIdentifier> mPeopleNotificationIdentifier;

    public interface OnGroupChangeListener {
        void onGroupAlertOverrideChanged(NotificationGroup notificationGroup, NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        }

        void onGroupCreated(NotificationGroup notificationGroup, String str) {
        }

        void onGroupCreatedFromChildren(NotificationGroup notificationGroup) {
        }

        void onGroupRemoved(NotificationGroup notificationGroup, String str) {
        }

        void onGroupSuppressionChanged(NotificationGroup notificationGroup, boolean z) {
        }

        void onGroupsChanged() {
        }
    }

    public NotificationGroupManagerLegacy(StatusBarStateController statusBarStateController, Lazy<PeopleNotificationIdentifier> lazy, Optional<Bubbles> optional) {
        statusBarStateController.addCallback(this);
        this.mPeopleNotificationIdentifier = lazy;
        this.mBubblesOptional = optional;
    }

    public void registerGroupChangeListener(OnGroupChangeListener onGroupChangeListener) {
        this.mGroupChangeListeners.add(onGroupChangeListener);
    }

    public void registerGroupExpansionChangeListener(GroupExpansionManager.OnGroupExpansionChangeListener onGroupExpansionChangeListener) {
        this.mExpansionChangeListeners.add(onGroupExpansionChangeListener);
    }

    public boolean isGroupExpanded(NotificationEntry notificationEntry) {
        NotificationGroup notificationGroup = this.mGroupMap.get(getGroupKey(notificationEntry.getSbn()));
        if (notificationGroup == null) {
            return false;
        }
        return notificationGroup.expanded;
    }

    public boolean isLogicalGroupExpanded(StatusBarNotification statusBarNotification) {
        NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        if (notificationGroup == null) {
            return false;
        }
        return notificationGroup.expanded;
    }

    public void setGroupExpanded(NotificationEntry notificationEntry, boolean z) {
        NotificationGroup notificationGroup = this.mGroupMap.get(getGroupKey(notificationEntry.getSbn()));
        if (notificationGroup != null) {
            setGroupExpanded(notificationGroup, z);
        }
    }

    private void setGroupExpanded(NotificationGroup notificationGroup, boolean z) {
        notificationGroup.expanded = z;
        if (notificationGroup.summary != null) {
            Iterator<GroupExpansionManager.OnGroupExpansionChangeListener> it = this.mExpansionChangeListeners.iterator();
            while (it.hasNext()) {
                it.next().onGroupExpansionChange(notificationGroup.summary.getRow(), z);
            }
        }
    }

    public void onEntryRemoved(NotificationEntry notificationEntry) {
        onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        StatusBarNotification remove = this.mIsolatedEntries.remove(notificationEntry.getKey());
        if (remove != null) {
            updateSuppression(this.mGroupMap.get(remove.getGroupKey()));
        }
    }

    private void onEntryRemovedInternal(NotificationEntry notificationEntry, StatusBarNotification statusBarNotification) {
        onEntryRemovedInternal(notificationEntry, statusBarNotification.getGroupKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }

    private void onEntryRemovedInternal(NotificationEntry notificationEntry, String str, boolean z, boolean z2) {
        String groupKey = getGroupKey(notificationEntry.getKey(), str);
        NotificationGroup notificationGroup = this.mGroupMap.get(groupKey);
        if (notificationGroup != null) {
            if (isGroupChild(notificationEntry.getKey(), z, z2)) {
                notificationGroup.children.remove(notificationEntry.getKey());
            } else {
                notificationGroup.summary = null;
            }
            updateSuppression(notificationGroup);
            if (notificationGroup.children.isEmpty() && notificationGroup.summary == null) {
                this.mGroupMap.remove(groupKey);
                Iterator<OnGroupChangeListener> it = this.mGroupChangeListeners.iterator();
                while (it.hasNext()) {
                    it.next().onGroupRemoved(notificationGroup, groupKey);
                }
            }
        }
    }

    public void onEntryAdded(NotificationEntry notificationEntry) {
        updateIsolation(notificationEntry);
        onEntryAddedInternal(notificationEntry);
    }

    private void onEntryAddedInternal(NotificationEntry notificationEntry) {
        String str;
        if (notificationEntry.isRowRemoved()) {
            notificationEntry.setDebugThrowable(new Throwable());
        }
        StatusBarNotification sbn = notificationEntry.getSbn();
        boolean isGroupChild = isGroupChild(sbn);
        String groupKey = getGroupKey(sbn);
        NotificationGroup notificationGroup = this.mGroupMap.get(groupKey);
        if (notificationGroup == null) {
            notificationGroup = new NotificationGroup(groupKey);
            this.mGroupMap.put(groupKey, notificationGroup);
            Iterator<OnGroupChangeListener> it = this.mGroupChangeListeners.iterator();
            while (it.hasNext()) {
                it.next().onGroupCreated(notificationGroup, groupKey);
            }
        }
        if (isGroupChild) {
            NotificationEntry notificationEntry2 = notificationGroup.children.get(notificationEntry.getKey());
            if (!(notificationEntry2 == null || notificationEntry2 == notificationEntry)) {
                Throwable debugThrowable = notificationEntry2.getDebugThrowable();
                StringBuilder sb = new StringBuilder();
                sb.append("Inconsistent entries found with the same key ");
                sb.append(notificationEntry.getKey());
                sb.append("existing removed: ");
                sb.append(notificationEntry2.isRowRemoved());
                if (debugThrowable != null) {
                    str = Log.getStackTraceString(debugThrowable) + "\n";
                } else {
                    str = "";
                }
                sb.append(str);
                sb.append(" added removed");
                sb.append(notificationEntry.isRowRemoved());
                Log.wtf("NotifGroupManager", sb.toString(), new Throwable());
            }
            notificationGroup.children.put(notificationEntry.getKey(), notificationEntry);
            addToPostBatchHistory(notificationGroup, notificationEntry);
            updateSuppression(notificationGroup);
            return;
        }
        notificationGroup.summary = notificationEntry;
        addToPostBatchHistory(notificationGroup, notificationEntry);
        notificationGroup.expanded = notificationEntry.areChildrenExpanded();
        updateSuppression(notificationGroup);
        if (!notificationGroup.children.isEmpty()) {
            Iterator it2 = new ArrayList(notificationGroup.children.values()).iterator();
            while (it2.hasNext()) {
                onEntryBecomingChild((NotificationEntry) it2.next());
            }
            Iterator<OnGroupChangeListener> it3 = this.mGroupChangeListeners.iterator();
            while (it3.hasNext()) {
                it3.next().onGroupCreatedFromChildren(notificationGroup);
            }
        }
    }

    private void addToPostBatchHistory(NotificationGroup notificationGroup, NotificationEntry notificationEntry) {
        if (notificationEntry != null && notificationGroup.postBatchHistory.add(new PostRecord(notificationEntry))) {
            trimPostBatchHistory(notificationGroup.postBatchHistory);
        }
    }

    private void trimPostBatchHistory(TreeSet<PostRecord> treeSet) {
        if (treeSet.size() > 1) {
            long j = treeSet.last().postTime - 5000;
            while (!treeSet.isEmpty() && treeSet.first().postTime < j) {
                treeSet.pollFirst();
            }
        }
    }

    private void onEntryBecomingChild(NotificationEntry notificationEntry) {
        updateIsolation(notificationEntry);
    }

    private void updateSuppression(NotificationGroup notificationGroup) {
        if (notificationGroup != null) {
            NotificationEntry notificationEntry = notificationGroup.alertOverride;
            notificationGroup.alertOverride = getPriorityConversationAlertOverride(notificationGroup);
            boolean z = false;
            int i = 0;
            boolean z2 = false;
            for (NotificationEntry next : notificationGroup.children.values()) {
                if (!this.mBubblesOptional.isPresent() || !this.mBubblesOptional.get().isBubbleNotificationSuppressedFromShade(next.getKey(), next.getSbn().getGroupKey())) {
                    i++;
                } else {
                    z2 = true;
                }
            }
            boolean z3 = notificationGroup.suppressed;
            NotificationEntry notificationEntry2 = notificationGroup.summary;
            boolean z4 = notificationEntry2 != null && !notificationGroup.expanded && (i == 1 || (i == 0 && notificationEntry2.getSbn().getNotification().isGroupSummary() && (hasIsolatedChildren(notificationGroup) || z2)));
            notificationGroup.suppressed = z4;
            boolean z5 = notificationEntry != notificationGroup.alertOverride;
            if (z3 != z4) {
                z = true;
            }
            if ((z5 || z) && !this.mIsUpdatingUnchangedGroup) {
                if (z5) {
                    this.mEventBuffer.notifyAlertOverrideChanged(notificationGroup, notificationEntry);
                }
                if (z) {
                    Iterator<OnGroupChangeListener> it = this.mGroupChangeListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onGroupSuppressionChanged(notificationGroup, notificationGroup.suppressed);
                    }
                }
                this.mEventBuffer.notifyGroupsChanged();
            }
        }
    }

    private NotificationEntry getPriorityConversationAlertOverride(NotificationGroup notificationGroup) {
        NotificationEntry notificationEntry;
        HashMap<String, NotificationEntry> importantConversations;
        if (notificationGroup != null && (notificationEntry = notificationGroup.summary) != null && !isIsolated(notificationEntry.getKey()) && notificationGroup.summary.getSbn().getNotification().getGroupAlertBehavior() == 1 && (importantConversations = getImportantConversations(notificationGroup)) != null && !importantConversations.isEmpty()) {
            HashSet hashSet = new HashSet(importantConversations.keySet());
            importantConversations.putAll(notificationGroup.children);
            for (NotificationEntry sbn : importantConversations.values()) {
                if (sbn.getSbn().getNotification().getGroupAlertBehavior() != 1) {
                    return null;
                }
            }
            TreeSet treeSet = new TreeSet(notificationGroup.postBatchHistory);
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                treeSet.addAll(this.mGroupMap.get((String) it.next()).postBatchHistory);
            }
            trimPostBatchHistory(treeSet);
            HashSet hashSet2 = new HashSet();
            long j = -1;
            NotificationEntry notificationEntry2 = null;
            for (PostRecord postRecord : treeSet.descendingSet()) {
                if (hashSet2.contains(postRecord.key)) {
                    break;
                }
                hashSet2.add(postRecord.key);
                NotificationEntry notificationEntry3 = importantConversations.get(postRecord.key);
                if (notificationEntry3 != null) {
                    long j2 = notificationEntry3.getSbn().getNotification().when;
                    if (notificationEntry2 == null || j2 > j) {
                        notificationEntry2 = notificationEntry3;
                        j = j2;
                    }
                }
            }
            if (notificationEntry2 == null || !hashSet.contains(notificationEntry2.getKey())) {
                return null;
            }
            return notificationEntry2;
        }
        return null;
    }

    private boolean hasIsolatedChildren(NotificationGroup notificationGroup) {
        return getNumberOfIsolatedChildren(notificationGroup.summary.getSbn().getGroupKey()) != 0;
    }

    private int getNumberOfIsolatedChildren(String str) {
        int i = 0;
        for (StatusBarNotification next : this.mIsolatedEntries.values()) {
            if (next.getGroupKey().equals(str) && isIsolated(next.getKey())) {
                i++;
            }
        }
        return i;
    }

    private HashMap<String, NotificationEntry> getImportantConversations(NotificationGroup notificationGroup) {
        String groupKey = notificationGroup.summary.getSbn().getGroupKey();
        HashMap<String, NotificationEntry> hashMap = null;
        for (StatusBarNotification next : this.mIsolatedEntries.values()) {
            if (next.getGroupKey().equals(groupKey)) {
                NotificationEntry notificationEntry = this.mGroupMap.get(next.getKey()).summary;
                if (isImportantConversation(notificationEntry)) {
                    if (hashMap == null) {
                        hashMap = new HashMap<>();
                    }
                    hashMap.put(next.getKey(), notificationEntry);
                }
            }
        }
        return hashMap;
    }

    public void onEntryUpdated(NotificationEntry notificationEntry, StatusBarNotification statusBarNotification) {
        onEntryUpdated(notificationEntry, statusBarNotification.getGroupKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }

    public void onEntryUpdated(NotificationEntry notificationEntry, String str, boolean z, boolean z2) {
        String groupKey = notificationEntry.getSbn().getGroupKey();
        boolean z3 = true;
        boolean z4 = !str.equals(groupKey);
        boolean isGroupChild = isGroupChild(notificationEntry.getKey(), z, z2);
        boolean isGroupChild2 = isGroupChild(notificationEntry.getSbn());
        if (z4 || isGroupChild != isGroupChild2) {
            z3 = false;
        }
        this.mIsUpdatingUnchangedGroup = z3;
        if (this.mGroupMap.get(getGroupKey(notificationEntry.getKey(), str)) != null) {
            onEntryRemovedInternal(notificationEntry, str, z, z2);
        }
        onEntryAddedInternal(notificationEntry);
        this.mIsUpdatingUnchangedGroup = false;
        if (isIsolated(notificationEntry.getSbn().getKey())) {
            this.mIsolatedEntries.put(notificationEntry.getKey(), notificationEntry.getSbn());
            if (z4) {
                updateSuppression(this.mGroupMap.get(str));
                updateSuppression(this.mGroupMap.get(groupKey));
            }
        } else if (!isGroupChild && isGroupChild2) {
            onEntryBecomingChild(notificationEntry);
        }
    }

    public boolean isSummaryOfSuppressedGroup(StatusBarNotification statusBarNotification) {
        return statusBarNotification.getNotification().isGroupSummary() && isGroupSuppressed(getGroupKey(statusBarNotification));
    }

    public NotificationGroup getGroupForSummary(StatusBarNotification statusBarNotification) {
        if (statusBarNotification.getNotification().isGroupSummary()) {
            return this.mGroupMap.get(getGroupKey(statusBarNotification));
        }
        return null;
    }

    private boolean isOnlyChild(StatusBarNotification statusBarNotification) {
        if (statusBarNotification.getNotification().isGroupSummary() || getTotalNumberOfChildren(statusBarNotification) != 1) {
            return false;
        }
        return true;
    }

    public boolean isOnlyChildInGroup(NotificationEntry notificationEntry) {
        NotificationEntry logicalGroupSummary;
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (isOnlyChild(sbn) && (logicalGroupSummary = getLogicalGroupSummary(notificationEntry)) != null && !logicalGroupSummary.getSbn().equals(sbn)) {
            return true;
        }
        return false;
    }

    private int getTotalNumberOfChildren(StatusBarNotification statusBarNotification) {
        int numberOfIsolatedChildren = getNumberOfIsolatedChildren(statusBarNotification.getGroupKey());
        NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        return numberOfIsolatedChildren + (notificationGroup != null ? notificationGroup.children.size() : 0);
    }

    private boolean isGroupSuppressed(String str) {
        NotificationGroup notificationGroup = this.mGroupMap.get(str);
        return notificationGroup != null && notificationGroup.suppressed;
    }

    private void setStatusBarState(int i) {
        this.mBarState = i;
        if (i == 1) {
            collapseGroups();
        }
    }

    public void collapseGroups() {
        ArrayList arrayList = new ArrayList(this.mGroupMap.values());
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            NotificationGroup notificationGroup = (NotificationGroup) arrayList.get(i);
            if (notificationGroup.expanded) {
                setGroupExpanded(notificationGroup, false);
            }
            updateSuppression(notificationGroup);
        }
    }

    public boolean isChildInGroup(NotificationEntry notificationEntry) {
        NotificationGroup notificationGroup;
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (isGroupChild(sbn) && (notificationGroup = this.mGroupMap.get(getGroupKey(sbn))) != null && notificationGroup.summary != null && !notificationGroup.suppressed && !notificationGroup.children.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isGroupSummary(NotificationEntry notificationEntry) {
        NotificationGroup notificationGroup;
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (isGroupSummary(sbn) && (notificationGroup = this.mGroupMap.get(getGroupKey(sbn))) != null && notificationGroup.summary != null && !notificationGroup.children.isEmpty() && Objects.equals(notificationGroup.summary.getSbn(), sbn)) {
            return true;
        }
        return false;
    }

    public NotificationEntry getGroupSummary(NotificationEntry notificationEntry) {
        return getGroupSummary(getGroupKey(notificationEntry.getSbn()));
    }

    public NotificationEntry getLogicalGroupSummary(NotificationEntry notificationEntry) {
        return getGroupSummary(notificationEntry.getSbn().getGroupKey());
    }

    private NotificationEntry getGroupSummary(String str) {
        NotificationGroup notificationGroup = this.mGroupMap.get(str);
        if (notificationGroup == null) {
            return null;
        }
        return notificationGroup.summary;
    }

    public ArrayList<NotificationEntry> getLogicalChildren(StatusBarNotification statusBarNotification) {
        NotificationGroup notificationGroup = this.mGroupMap.get(statusBarNotification.getGroupKey());
        if (notificationGroup == null) {
            return null;
        }
        ArrayList<NotificationEntry> arrayList = new ArrayList<>(notificationGroup.children.values());
        for (StatusBarNotification next : this.mIsolatedEntries.values()) {
            if (next.getGroupKey().equals(statusBarNotification.getGroupKey())) {
                arrayList.add(this.mGroupMap.get(next.getKey()).summary);
            }
        }
        return arrayList;
    }

    public List<NotificationEntry> getChildren(ListEntry listEntry) {
        NotificationGroup notificationGroup = this.mGroupMap.get(listEntry.getRepresentativeEntry().getSbn().getGroupKey());
        if (notificationGroup == null) {
            return null;
        }
        return new ArrayList(notificationGroup.children.values());
    }

    public void updateSuppression(NotificationEntry notificationEntry) {
        NotificationGroup notificationGroup = this.mGroupMap.get(getGroupKey(notificationEntry.getSbn()));
        if (notificationGroup != null) {
            updateSuppression(notificationGroup);
        }
    }

    public String getGroupKey(StatusBarNotification statusBarNotification) {
        return getGroupKey(statusBarNotification.getKey(), statusBarNotification.getGroupKey());
    }

    private String getGroupKey(String str, String str2) {
        return isIsolated(str) ? str : str2;
    }

    public boolean toggleGroupExpansion(NotificationEntry notificationEntry) {
        NotificationGroup notificationGroup = this.mGroupMap.get(getGroupKey(notificationEntry.getSbn()));
        if (notificationGroup == null) {
            return false;
        }
        setGroupExpanded(notificationGroup, !notificationGroup.expanded);
        return notificationGroup.expanded;
    }

    private boolean isIsolated(String str) {
        return this.mIsolatedEntries.containsKey(str);
    }

    public boolean isGroupSummary(StatusBarNotification statusBarNotification) {
        if (isIsolated(statusBarNotification.getKey())) {
            return true;
        }
        return statusBarNotification.getNotification().isGroupSummary();
    }

    public boolean isGroupChild(StatusBarNotification statusBarNotification) {
        return isGroupChild(statusBarNotification.getKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }

    private boolean isGroupChild(String str, boolean z, boolean z2) {
        return !isIsolated(str) && z && !z2;
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        updateIsolation(notificationEntry);
    }

    private boolean shouldIsolate(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (!sbn.isGroup() || sbn.getNotification().isGroupSummary()) {
            return false;
        }
        if (isImportantConversation(notificationEntry)) {
            return true;
        }
        HeadsUpManager headsUpManager = this.mHeadsUpManager;
        if (headsUpManager != null && !headsUpManager.isAlerting(notificationEntry.getKey())) {
            return false;
        }
        NotificationGroup notificationGroup = this.mGroupMap.get(sbn.getGroupKey());
        if (sbn.getNotification().fullScreenIntent != null || notificationGroup == null || !notificationGroup.expanded || isGroupNotFullyVisible(notificationGroup)) {
            return true;
        }
        return false;
    }

    private boolean isImportantConversation(NotificationEntry notificationEntry) {
        return this.mPeopleNotificationIdentifier.get().getPeopleNotificationType(notificationEntry) == 3;
    }

    private void isolateNotification(NotificationEntry notificationEntry) {
        onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        this.mIsolatedEntries.put(notificationEntry.getKey(), notificationEntry.getSbn());
        onEntryAddedInternal(notificationEntry);
        updateSuppression(this.mGroupMap.get(notificationEntry.getSbn().getGroupKey()));
        Iterator<OnGroupChangeListener> it = this.mGroupChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onGroupsChanged();
        }
    }

    public void updateIsolation(NotificationEntry notificationEntry) {
        this.mEventBuffer.startBuffering();
        boolean isIsolated = isIsolated(notificationEntry.getSbn().getKey());
        if (shouldIsolate(notificationEntry)) {
            if (!isIsolated) {
                isolateNotification(notificationEntry);
            }
        } else if (isIsolated) {
            stopIsolatingNotification(notificationEntry);
        }
        this.mEventBuffer.flushAndStopBuffering();
    }

    private void stopIsolatingNotification(NotificationEntry notificationEntry) {
        onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        this.mIsolatedEntries.remove(notificationEntry.getKey());
        onEntryAddedInternal(notificationEntry);
        Iterator<OnGroupChangeListener> it = this.mGroupChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onGroupsChanged();
        }
    }

    private boolean isGroupNotFullyVisible(NotificationGroup notificationGroup) {
        NotificationEntry notificationEntry = notificationGroup.summary;
        return notificationEntry == null || notificationEntry.isGroupNotFullyVisible();
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("GroupManagerLegacy state:");
        printWriter.println("  number of groups: " + this.mGroupMap.size());
        for (Map.Entry next : this.mGroupMap.entrySet()) {
            printWriter.println("\n    key: " + ((String) next.getKey()));
            printWriter.println(next.getValue());
        }
        printWriter.println("\n    isolated entries: " + this.mIsolatedEntries.size());
        for (Map.Entry next2 : this.mIsolatedEntries.entrySet()) {
            printWriter.print("      ");
            printWriter.print((String) next2.getKey());
            printWriter.print(", ");
            printWriter.println(next2.getValue());
        }
    }

    public void onStateChanged(int i) {
        setStatusBarState(i);
    }

    public static class PostRecord implements Comparable<PostRecord> {
        public final String key;
        public final long postTime;

        public PostRecord(NotificationEntry notificationEntry) {
            this.postTime = notificationEntry.getSbn().getPostTime();
            this.key = notificationEntry.getKey();
        }

        public int compareTo(PostRecord postRecord) {
            int compare = Long.compare(this.postTime, postRecord.postTime);
            return compare == 0 ? String.CASE_INSENSITIVE_ORDER.compare(this.key, postRecord.key) : compare;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || PostRecord.class != obj.getClass()) {
                return false;
            }
            PostRecord postRecord = (PostRecord) obj;
            if (this.postTime != postRecord.postTime || !this.key.equals(postRecord.key)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{Long.valueOf(this.postTime), this.key});
        }
    }

    public static class NotificationGroup {
        public NotificationEntry alertOverride;
        public final HashMap<String, NotificationEntry> children = new HashMap<>();
        public boolean expanded;
        public final String groupKey;
        public final TreeSet<PostRecord> postBatchHistory = new TreeSet<>();
        public NotificationEntry summary;
        public boolean suppressed;

        NotificationGroup(String str) {
            this.groupKey = str;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("    groupKey: ");
            sb.append(this.groupKey);
            sb.append("\n    summary:");
            appendEntry(sb, this.summary);
            sb.append("\n    children size: ");
            sb.append(this.children.size());
            for (NotificationEntry appendEntry : this.children.values()) {
                appendEntry(sb, appendEntry);
            }
            sb.append("\n    alertOverride:");
            appendEntry(sb, this.alertOverride);
            sb.append("\n    summary suppressed: ");
            sb.append(this.suppressed);
            return sb.toString();
        }

        private void appendEntry(StringBuilder sb, NotificationEntry notificationEntry) {
            sb.append("\n      ");
            sb.append(notificationEntry != null ? notificationEntry.getSbn() : "null");
            if (notificationEntry != null && notificationEntry.getDebugThrowable() != null) {
                sb.append(Log.getStackTraceString(notificationEntry.getDebugThrowable()));
            }
        }
    }

    private class EventBuffer {
        private boolean mDidGroupsChange;
        private boolean mIsBuffering;
        private final HashMap<String, NotificationEntry> mOldAlertOverrideByGroup;

        private EventBuffer() {
            this.mOldAlertOverrideByGroup = new HashMap<>();
            this.mIsBuffering = false;
            this.mDidGroupsChange = false;
        }

        /* access modifiers changed from: package-private */
        public void notifyAlertOverrideChanged(NotificationGroup notificationGroup, NotificationEntry notificationEntry) {
            if (this.mIsBuffering) {
                this.mOldAlertOverrideByGroup.putIfAbsent(notificationGroup.groupKey, notificationEntry);
                return;
            }
            Iterator it = NotificationGroupManagerLegacy.this.mGroupChangeListeners.iterator();
            while (it.hasNext()) {
                ((OnGroupChangeListener) it.next()).onGroupAlertOverrideChanged(notificationGroup, notificationEntry, notificationGroup.alertOverride);
            }
        }

        /* access modifiers changed from: package-private */
        public void notifyGroupsChanged() {
            if (this.mIsBuffering) {
                this.mDidGroupsChange = true;
                return;
            }
            Iterator it = NotificationGroupManagerLegacy.this.mGroupChangeListeners.iterator();
            while (it.hasNext()) {
                ((OnGroupChangeListener) it.next()).onGroupsChanged();
            }
        }

        /* access modifiers changed from: package-private */
        public void startBuffering() {
            this.mIsBuffering = true;
        }

        /* access modifiers changed from: package-private */
        public void flushAndStopBuffering() {
            NotificationEntry notificationEntry;
            this.mIsBuffering = false;
            for (Map.Entry next : this.mOldAlertOverrideByGroup.entrySet()) {
                NotificationGroup notificationGroup = (NotificationGroup) NotificationGroupManagerLegacy.this.mGroupMap.get(next.getKey());
                if (!(notificationGroup == null || notificationGroup.alertOverride == (notificationEntry = (NotificationEntry) next.getValue()))) {
                    notifyAlertOverrideChanged(notificationGroup, notificationEntry);
                }
            }
            this.mOldAlertOverrideByGroup.clear();
            if (this.mDidGroupsChange) {
                notifyGroupsChanged();
                this.mDidGroupsChange = false;
            }
        }
    }
}
