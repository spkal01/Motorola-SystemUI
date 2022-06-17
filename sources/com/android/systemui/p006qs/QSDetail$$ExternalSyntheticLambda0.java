package com.android.systemui.p006qs;

import android.view.View;
import com.android.systemui.plugins.p005qs.DetailAdapter;

/* renamed from: com.android.systemui.qs.QSDetail$$ExternalSyntheticLambda0 */
public final /* synthetic */ class QSDetail$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ QSDetail f$0;
    public final /* synthetic */ DetailAdapter f$1;

    public /* synthetic */ QSDetail$$ExternalSyntheticLambda0(QSDetail qSDetail, DetailAdapter detailAdapter) {
        this.f$0 = qSDetail;
        this.f$1 = detailAdapter;
    }

    public final void onClick(View view) {
        this.f$0.lambda$setupDetailHeader$2(this.f$1, view);
    }
}
