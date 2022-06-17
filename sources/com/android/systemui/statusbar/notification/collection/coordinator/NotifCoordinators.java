package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSectioner;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NotifCoordinators implements Dumpable {
    private final List<Coordinator> mCoordinators;
    private final List<NotifSectioner> mOrderedSections;

    public NotifCoordinators(DumpManager dumpManager, FeatureFlags featureFlags, HideNotifsForOtherUsersCoordinator hideNotifsForOtherUsersCoordinator, KeyguardCoordinator keyguardCoordinator, RankingCoordinator rankingCoordinator, AppOpsCoordinator appOpsCoordinator, DeviceProvisionedCoordinator deviceProvisionedCoordinator, BubbleCoordinator bubbleCoordinator, HeadsUpCoordinator headsUpCoordinator, ConversationCoordinator conversationCoordinator, PreparationCoordinator preparationCoordinator, MediaCoordinator mediaCoordinator, SmartspaceDedupingCoordinator smartspaceDedupingCoordinator, VisualStabilityCoordinator visualStabilityCoordinator) {
        ArrayList arrayList = new ArrayList();
        this.mCoordinators = arrayList;
        ArrayList arrayList2 = new ArrayList();
        this.mOrderedSections = arrayList2;
        DumpManager dumpManager2 = dumpManager;
        dumpManager.registerDumpable("NotifCoordinators", this);
        arrayList.add(new HideLocallyDismissedNotifsCoordinator());
        HideNotifsForOtherUsersCoordinator hideNotifsForOtherUsersCoordinator2 = hideNotifsForOtherUsersCoordinator;
        arrayList.add(hideNotifsForOtherUsersCoordinator);
        KeyguardCoordinator keyguardCoordinator2 = keyguardCoordinator;
        arrayList.add(keyguardCoordinator);
        RankingCoordinator rankingCoordinator2 = rankingCoordinator;
        arrayList.add(rankingCoordinator);
        AppOpsCoordinator appOpsCoordinator2 = appOpsCoordinator;
        arrayList.add(appOpsCoordinator);
        DeviceProvisionedCoordinator deviceProvisionedCoordinator2 = deviceProvisionedCoordinator;
        arrayList.add(deviceProvisionedCoordinator);
        BubbleCoordinator bubbleCoordinator2 = bubbleCoordinator;
        arrayList.add(bubbleCoordinator);
        arrayList.add(conversationCoordinator);
        arrayList.add(mediaCoordinator);
        arrayList.add(visualStabilityCoordinator);
        if (featureFlags.isSmartspaceDedupingEnabled()) {
            arrayList.add(smartspaceDedupingCoordinator);
        }
        if (featureFlags.isNewNotifPipelineRenderingEnabled()) {
            arrayList.add(headsUpCoordinator);
            arrayList.add(preparationCoordinator);
        } else {
            HeadsUpCoordinator headsUpCoordinator2 = headsUpCoordinator;
        }
        if (featureFlags.isNewNotifPipelineRenderingEnabled()) {
            arrayList2.add(headsUpCoordinator.getSectioner());
        }
        arrayList2.add(appOpsCoordinator.getSectioner());
        arrayList2.add(conversationCoordinator.getSectioner());
        arrayList2.add(rankingCoordinator.getAlertingSectioner());
        arrayList2.add(rankingCoordinator.getSilentSectioner());
    }

    public void attach(NotifPipeline notifPipeline) {
        for (Coordinator attach : this.mCoordinators) {
            attach.attach(notifPipeline);
        }
        notifPipeline.setSections(this.mOrderedSections);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println();
        printWriter.println("NotifCoordinators:");
        for (Coordinator coordinator : this.mCoordinators) {
            printWriter.println("\t" + coordinator.getClass());
        }
        for (NotifSectioner name : this.mOrderedSections) {
            printWriter.println("\t" + name.getName());
        }
    }
}
