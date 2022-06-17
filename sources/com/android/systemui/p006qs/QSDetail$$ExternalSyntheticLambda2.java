package com.android.systemui.p006qs;

import android.content.Intent;
import android.view.View;
import com.android.systemui.plugins.p005qs.DetailAdapter;

/* renamed from: com.android.systemui.qs.QSDetail$$ExternalSyntheticLambda2 */
public final /* synthetic */ class QSDetail$$ExternalSyntheticLambda2 implements View.OnClickListener {
    public final /* synthetic */ QSDetail f$0;
    public final /* synthetic */ DetailAdapter f$1;
    public final /* synthetic */ Intent f$2;

    public /* synthetic */ QSDetail$$ExternalSyntheticLambda2(QSDetail qSDetail, DetailAdapter detailAdapter, Intent intent) {
        this.f$0 = qSDetail;
        this.f$1 = detailAdapter;
        this.f$2 = intent;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setupDetailFooter$0(this.f$1, this.f$2, view);
    }
}
