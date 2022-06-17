package com.android.systemui.p006qs.tiles;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import java.text.DecimalFormat;

/* renamed from: com.android.systemui.qs.tiles.DataUsageDetailView */
public class DataUsageDetailView extends LinearLayout {
    private final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public DataUsageDetailView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = R$dimen.qs_data_usage_text_size;
        FontSizeUtils.updateFontSize(this, 16908310, i);
        FontSizeUtils.updateFontSize(this, R$id.usage_text, R$dimen.qs_data_usage_usage_text_size);
        FontSizeUtils.updateFontSize(this, R$id.usage_carrier_text, i);
        FontSizeUtils.updateFontSize(this, R$id.usage_info_top_text, i);
        FontSizeUtils.updateFontSize(this, R$id.usage_period_text, i);
        FontSizeUtils.updateFontSize(this, R$id.usage_info_bottom_text, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0122  */
    /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bind(com.android.settingslib.net.DataUsageController.DataUsageInfo r24) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            android.content.Context r2 = r0.mContext
            android.content.res.Resources r2 = r2.getResources()
            long r3 = r1.usageLevel
            long r5 = r1.warningLevel
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            r9 = 0
            r11 = 1
            r12 = 0
            if (r7 < 0) goto L_0x0076
            long r13 = r1.limitLevel
            int r7 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r7 > 0) goto L_0x001d
            goto L_0x0076
        L_0x001d:
            int r5 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r5 > 0) goto L_0x0046
            int r5 = com.android.systemui.R$string.quick_settings_cellular_detail_remaining_data
            long r13 = r13 - r3
            int r6 = com.android.systemui.R$string.quick_settings_cellular_detail_data_used
            java.lang.Object[] r7 = new java.lang.Object[r11]
            java.lang.String r3 = r0.formatBytes(r3)
            r7[r12] = r3
            java.lang.String r3 = r2.getString(r6, r7)
            int r4 = com.android.systemui.R$string.quick_settings_cellular_detail_data_limit
            java.lang.Object[] r6 = new java.lang.Object[r11]
            long r8 = r1.limitLevel
            java.lang.String r8 = r0.formatBytes(r8)
            r6[r12] = r8
            java.lang.String r2 = r2.getString(r4, r6)
            r7 = r2
            r2 = r3
            r3 = r13
            goto L_0x0088
        L_0x0046:
            int r5 = com.android.systemui.R$string.quick_settings_cellular_detail_over_limit
            long r6 = r3 - r13
            int r8 = com.android.systemui.R$string.quick_settings_cellular_detail_data_used
            java.lang.Object[] r9 = new java.lang.Object[r11]
            java.lang.String r3 = r0.formatBytes(r3)
            r9[r12] = r3
            java.lang.String r3 = r2.getString(r8, r9)
            int r4 = com.android.systemui.R$string.quick_settings_cellular_detail_data_limit
            java.lang.Object[] r8 = new java.lang.Object[r11]
            long r9 = r1.limitLevel
            java.lang.String r9 = r0.formatBytes(r9)
            r8[r12] = r9
            java.lang.String r8 = r2.getString(r4, r8)
            android.content.Context r2 = r0.mContext
            android.content.res.ColorStateList r2 = com.android.settingslib.Utils.getColorError(r2)
            r22 = r8
            r8 = r2
            r2 = r3
            r3 = r6
            r7 = r22
            goto L_0x0089
        L_0x0076:
            int r8 = com.android.systemui.R$string.quick_settings_cellular_detail_data_usage
            int r9 = com.android.systemui.R$string.quick_settings_cellular_detail_data_warning
            java.lang.Object[] r10 = new java.lang.Object[r11]
            java.lang.String r5 = r0.formatBytes(r5)
            r10[r12] = r5
            java.lang.String r2 = r2.getString(r9, r10)
            r5 = r8
            r7 = 0
        L_0x0088:
            r8 = 0
        L_0x0089:
            if (r8 != 0) goto L_0x0091
            android.content.Context r6 = r0.mContext
            android.content.res.ColorStateList r8 = com.android.settingslib.Utils.getColorAccent(r6)
        L_0x0091:
            r6 = 16908310(0x1020016, float:2.387729E-38)
            android.view.View r6 = r0.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r6.setText(r5)
            int r5 = com.android.systemui.R$id.usage_text
            android.view.View r5 = r0.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            java.lang.String r3 = r0.formatBytes(r3)
            r5.setText(r3)
            r5.setTextColor(r8)
            int r3 = com.android.systemui.R$id.usage_graph
            android.view.View r3 = r0.findViewById(r3)
            com.android.systemui.qs.DataUsageGraph r3 = (com.android.systemui.p006qs.DataUsageGraph) r3
            long r4 = r1.limitLevel
            long r8 = r1.warningLevel
            long r13 = r1.usageLevel
            r15 = r3
            r16 = r4
            r18 = r8
            r20 = r13
            r15.setLevels(r16, r18, r20)
            int r4 = com.android.systemui.R$id.usage_carrier_text
            android.view.View r4 = r0.findViewById(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            java.lang.String r5 = r1.carrier
            r4.setText(r5)
            int r4 = com.android.systemui.R$id.usage_period_text
            android.view.View r4 = r0.findViewById(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            java.lang.String r5 = r1.period
            r4.setText(r5)
            int r4 = com.android.systemui.R$id.usage_info_top_text
            android.view.View r4 = r0.findViewById(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r5 = 8
            if (r2 == 0) goto L_0x00ef
            r6 = r12
            goto L_0x00f0
        L_0x00ef:
            r6 = r5
        L_0x00f0:
            r4.setVisibility(r6)
            r4.setText(r2)
            int r2 = com.android.systemui.R$id.usage_info_bottom_text
            android.view.View r0 = r0.findViewById(r2)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r7 == 0) goto L_0x0102
            r2 = r12
            goto L_0x0103
        L_0x0102:
            r2 = r5
        L_0x0103:
            r0.setVisibility(r2)
            r0.setText(r7)
            long r6 = r1.warningLevel
            r8 = 0
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 > 0) goto L_0x0119
            long r0 = r1.limitLevel
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x0118
            goto L_0x0119
        L_0x0118:
            r11 = r12
        L_0x0119:
            if (r11 == 0) goto L_0x011c
            goto L_0x011d
        L_0x011c:
            r12 = r5
        L_0x011d:
            r3.setVisibility(r12)
            if (r11 != 0) goto L_0x0125
            r4.setVisibility(r5)
        L_0x0125:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.tiles.DataUsageDetailView.bind(com.android.settingslib.net.DataUsageController$DataUsageInfo):void");
    }

    private String formatBytes(long j) {
        String str;
        double d;
        double abs = (double) Math.abs(j);
        if (abs > 1.048576E8d) {
            d = abs / 1.073741824E9d;
            str = "GB";
        } else if (abs > 102400.0d) {
            d = abs / 1048576.0d;
            str = "MB";
        } else {
            d = abs / 1024.0d;
            str = "KB";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.FORMAT.format(d * ((double) (j < 0 ? -1 : 1))));
        sb.append(" ");
        sb.append(str);
        return sb.toString();
    }
}
