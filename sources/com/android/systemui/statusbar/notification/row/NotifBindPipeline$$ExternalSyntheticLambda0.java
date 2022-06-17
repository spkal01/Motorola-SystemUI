package com.android.systemui.statusbar.notification.row;

import androidx.core.p002os.CancellationSignal;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import java.util.Set;

public final /* synthetic */ class NotifBindPipeline$$ExternalSyntheticLambda0 implements CancellationSignal.OnCancelListener {
    public final /* synthetic */ Set f$0;
    public final /* synthetic */ NotifBindPipeline.BindCallback f$1;

    public /* synthetic */ NotifBindPipeline$$ExternalSyntheticLambda0(Set set, NotifBindPipeline.BindCallback bindCallback) {
        this.f$0 = set;
        this.f$1 = bindCallback;
    }

    public final void onCancel() {
        this.f$0.remove(this.f$1);
    }
}
