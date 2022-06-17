package com.android.systemui.people.widget;

import android.content.SharedPreferences;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final /* synthetic */ class PeopleBackupHelper$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ PeopleBackupHelper f$0;
    public final /* synthetic */ SharedPreferences.Editor f$1;
    public final /* synthetic */ List f$2;

    public /* synthetic */ PeopleBackupHelper$$ExternalSyntheticLambda0(PeopleBackupHelper peopleBackupHelper, SharedPreferences.Editor editor, List list) {
        this.f$0 = peopleBackupHelper;
        this.f$1 = editor;
        this.f$2 = list;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$performBackup$0(this.f$1, this.f$2, (Map.Entry) obj);
    }
}
