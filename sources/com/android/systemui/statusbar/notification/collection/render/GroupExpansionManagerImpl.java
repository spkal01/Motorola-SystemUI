package com.android.systemui.statusbar.notification.collection.render;

import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.coordinator.Coordinator;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeRenderListListener;
import com.android.systemui.statusbar.notification.collection.render.GroupExpansionManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupExpansionManagerImpl implements GroupExpansionManager, Coordinator {
    private final Set<NotificationEntry> mExpandedGroups = new HashSet();
    private final GroupMembershipManager mGroupMembershipManager;
    private final OnBeforeRenderListListener mNotifTracker = new GroupExpansionManagerImpl$$ExternalSyntheticLambda0(this);
    private final Set<GroupExpansionManager.OnGroupExpansionChangeListener> mOnGroupChangeListeners = new HashSet();

    public GroupExpansionManagerImpl(GroupMembershipManager groupMembershipManager) {
        this.mGroupMembershipManager = groupMembershipManager;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(List list) {
        HashSet hashSet = new HashSet();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            ListEntry listEntry = (ListEntry) it.next();
            if (listEntry instanceof GroupEntry) {
                hashSet.add(listEntry.getRepresentativeEntry());
            }
        }
        this.mExpandedGroups.removeIf(new GroupExpansionManagerImpl$$ExternalSyntheticLambda1(hashSet));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0(Set set, NotificationEntry notificationEntry) {
        return !set.contains(notificationEntry);
    }

    public void attach(NotifPipeline notifPipeline) {
        notifPipeline.addOnBeforeRenderListListener(this.mNotifTracker);
    }

    public void registerGroupExpansionChangeListener(GroupExpansionManager.OnGroupExpansionChangeListener onGroupExpansionChangeListener) {
        this.mOnGroupChangeListeners.add(onGroupExpansionChangeListener);
    }

    public boolean isGroupExpanded(NotificationEntry notificationEntry) {
        return this.mExpandedGroups.contains(this.mGroupMembershipManager.getGroupSummary(notificationEntry));
    }

    public void setGroupExpanded(NotificationEntry notificationEntry, boolean z) {
        NotificationEntry groupSummary = this.mGroupMembershipManager.getGroupSummary(notificationEntry);
        if (z) {
            this.mExpandedGroups.add(groupSummary);
        } else {
            this.mExpandedGroups.remove(groupSummary);
        }
        sendOnGroupExpandedChange(notificationEntry, z);
    }

    public boolean toggleGroupExpansion(NotificationEntry notificationEntry) {
        setGroupExpanded(notificationEntry, !isGroupExpanded(notificationEntry));
        return isGroupExpanded(notificationEntry);
    }

    public void collapseGroups() {
        for (NotificationEntry groupExpanded : this.mExpandedGroups) {
            setGroupExpanded(groupExpanded, false);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationEntryExpansion state:");
        printWriter.println("  # expanded groups: " + this.mExpandedGroups.size());
        for (NotificationEntry key : this.mExpandedGroups) {
            printWriter.println("    summary key of expanded group: " + key.getKey());
        }
    }

    private void sendOnGroupExpandedChange(NotificationEntry notificationEntry, boolean z) {
        for (GroupExpansionManager.OnGroupExpansionChangeListener onGroupExpansionChange : this.mOnGroupChangeListeners) {
            onGroupExpansionChange.onGroupExpansionChange(notificationEntry.getRow(), z);
        }
    }
}
