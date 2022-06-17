package com.android.systemui.statusbar;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.view.View;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public final /* synthetic */ class NotificationRemoteInputManager$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ NotificationRemoteInputManager f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ RemoteInput[] f$2;
    public final /* synthetic */ RemoteInput f$3;
    public final /* synthetic */ PendingIntent f$4;
    public final /* synthetic */ NotificationEntry.EditedSuggestionInfo f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ NotificationRemoteInputManager.AuthBypassPredicate f$7;

    public /* synthetic */ NotificationRemoteInputManager$$ExternalSyntheticLambda2(NotificationRemoteInputManager notificationRemoteInputManager, View view, RemoteInput[] remoteInputArr, RemoteInput remoteInput, PendingIntent pendingIntent, NotificationEntry.EditedSuggestionInfo editedSuggestionInfo, String str, NotificationRemoteInputManager.AuthBypassPredicate authBypassPredicate) {
        this.f$0 = notificationRemoteInputManager;
        this.f$1 = view;
        this.f$2 = remoteInputArr;
        this.f$3 = remoteInput;
        this.f$4 = pendingIntent;
        this.f$5 = editedSuggestionInfo;
        this.f$6 = str;
        this.f$7 = authBypassPredicate;
    }

    public final void run() {
        this.f$0.lambda$activateRemoteInput$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
