package com.android.systemui.wallet.p010ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.cardview.widget.CardView;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.moto.MotoFeature;

/* renamed from: com.android.systemui.wallet.ui.WalletCardView */
public class WalletCardView extends CardView {
    private final Paint mBorderPaint;

    public WalletCardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WalletCardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Paint paint = new Paint();
        this.mBorderPaint = paint;
        paint.setColor(context.getColor(R$color.wallet_card_border));
        paint.setStrokeWidth(context.getResources().getDimension(R$dimen.wallet_card_border_width));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        float radius = getRadius();
        if (!MotoFeature.isPrcProduct()) {
            canvas.drawRoundRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), radius, radius, this.mBorderPaint);
        }
    }
}
