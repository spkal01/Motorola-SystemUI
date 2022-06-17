package com.android.systemui.controls.p004ui;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.util.concurrency.DelayableExecutor;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ThumbnailBehavior$bind$1 */
/* compiled from: ThumbnailBehavior.kt */
final class ThumbnailBehavior$bind$1 implements Runnable {
    final /* synthetic */ ClipDrawable $clipLayer;
    final /* synthetic */ int $colorOffset;
    final /* synthetic */ ThumbnailBehavior this$0;

    ThumbnailBehavior$bind$1(ThumbnailBehavior thumbnailBehavior, ClipDrawable clipDrawable, int i) {
        this.this$0 = thumbnailBehavior;
        this.$clipLayer = clipDrawable;
        this.$colorOffset = i;
    }

    public final void run() {
        final Drawable loadDrawable = this.this$0.getTemplate().getThumbnail().loadDrawable(this.this$0.getCvh().getContext());
        DelayableExecutor uiExecutor = this.this$0.getCvh().getUiExecutor();
        final ThumbnailBehavior thumbnailBehavior = this.this$0;
        final ClipDrawable clipDrawable = this.$clipLayer;
        final int i = this.$colorOffset;
        uiExecutor.execute(new Runnable() {
            public final void run() {
                ClipDrawable clipDrawable = clipDrawable;
                Drawable drawable = loadDrawable;
                Intrinsics.checkNotNullExpressionValue(drawable, "drawable");
                clipDrawable.setDrawable(new CornerDrawable(drawable, (float) thumbnailBehavior.getCvh().getContext().getResources().getDimensionPixelSize(R$dimen.control_corner_radius)));
                clipDrawable.setColorFilter(new BlendModeColorFilter(thumbnailBehavior.getCvh().getContext().getResources().getColor(R$color.control_thumbnail_tint), BlendMode.LUMINOSITY));
                ControlViewHolder.m28x1a61c355(thumbnailBehavior.getCvh(), thumbnailBehavior.getEnabled(), i, false, 4, (Object) null);
            }
        });
    }
}
