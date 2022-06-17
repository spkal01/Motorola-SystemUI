package com.android.systemui.moto;

import android.util.SparseArray;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;

public class CarrierIcons {
    public static final DataTypeGroup LTE_CA_5G_E = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_5ge_wide, R$drawable.zz_moto_stat_sys_data_suspended_5ge_wide, R$drawable.zz_moto_stat_sys_data_disabled_5ge_wide, R$drawable.zz_moto_stat_sys_data_connected_5ge_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_5ge_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_5ge_wide_dual, R$string.data_connection_5ge_html));
    public static final DataTypeGroup NR_5G = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_5g_wide, R$drawable.zz_moto_stat_sys_data_suspended_5g_wide, R$drawable.zz_moto_stat_sys_data_disabled_5g_wide, R$drawable.zz_moto_stat_sys_data_connected_5g_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_5g_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_5g_wide_dual, R$string.data_connection_5g));
    public static final DataTypeGroup NR_5G_PLUS = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_5g_plus_wide, R$drawable.zz_moto_stat_sys_data_suspended_5g_plus_wide, R$drawable.zz_moto_stat_sys_data_disabled_5g_plus_wide, R$drawable.zz_moto_stat_sys_data_connected_5g_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_5g_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_5g_plus_wide_dual, R$string.data_connection_5g_plus));
    public static final DataTypeGroup WIDE_1X = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_1x_wide, R$drawable.zz_moto_stat_sys_data_suspended_1x_wide, R$drawable.zz_moto_stat_sys_data_disabled_1x_wide, R$drawable.zz_moto_stat_sys_data_connected_1x_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_1x_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_1x_wide_dual, R$string.data_connection_cdma));
    public static final DataTypeGroup WIDE_2G = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_2g_wide, R$drawable.zz_moto_stat_sys_data_suspended_2g_wide, R$drawable.zz_moto_stat_sys_data_disabled_2g_wide, R$drawable.zz_moto_stat_sys_data_connected_2g_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_2g_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_2g_wide_dual, R$string.zz_moto_data_type_2g));
    public static final DataTypeGroup WIDE_3G = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_3g_wide, R$drawable.zz_moto_stat_sys_data_suspended_3g_wide, R$drawable.zz_moto_stat_sys_data_disabled_3g_wide, R$drawable.zz_moto_stat_sys_data_connected_3g_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_3g_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_3g_wide_dual, R$string.data_connection_3g));
    public static final DataTypeGroup WIDE_3G_PLUS = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_3g_plus_wide, R$drawable.zz_moto_stat_sys_data_suspended_3g_plus_wide, R$drawable.zz_moto_stat_sys_data_disabled_3g_plus_wide, R$drawable.zz_moto_stat_sys_data_connected_3g_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_3g_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_3g_plus_wide_dual, R$string.zz_moto_data_type_3g_plug));
    public static final DataTypeGroup WIDE_4G = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_4g_wide, R$drawable.zz_moto_stat_sys_data_suspended_4g_wide, R$drawable.zz_moto_stat_sys_data_disabled_4g_wide, R$drawable.zz_moto_stat_sys_data_connected_4g_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_4g_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_4g_wide_dual, R$string.data_connection_4g));
    public static final DataTypeGroup WIDE_4G_ATT = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_4g_wide_att, R$drawable.zz_moto_stat_sys_data_suspended_4g_wide_att, R$drawable.zz_moto_stat_sys_data_disabled_4g_wide_att, R$drawable.zz_moto_stat_sys_data_connected_4g_wide_att_dual, R$drawable.zz_moto_stat_sys_data_suspended_4g_wide_att_dual, R$drawable.zz_moto_stat_sys_data_disabled_4g_wide_att_dual, R$string.zz_moto_data_type_4g_ATT));
    public static final DataTypeGroup WIDE_4G_LTE;
    public static final DataTypeGroup WIDE_4G_LTE_ATT;
    public static final DataTypeGroup WIDE_4G_PLUS = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_4g_plus_wide, R$drawable.zz_moto_stat_sys_data_suspended_4g_plus_wide, R$drawable.zz_moto_stat_sys_data_disabled_4g_plus_wide, R$drawable.zz_moto_stat_sys_data_connected_4g_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_4g_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_4g_plus_wide_dual, R$string.data_connection_4g_plus));
    public static final DataTypeGroup WIDE_E = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_e_wide, R$drawable.zz_moto_stat_sys_data_suspended_e_wide, R$drawable.zz_moto_stat_sys_data_disabled_e_wide, R$drawable.zz_moto_stat_sys_data_connected_e_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_e_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_e_wide_dual, R$string.data_connection_edge));
    public static final DataTypeGroup WIDE_G = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_g_wide, R$drawable.zz_moto_stat_sys_data_suspended_g_wide, R$drawable.zz_moto_stat_sys_data_disabled_g_wide, R$drawable.zz_moto_stat_sys_data_connected_g_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_g_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_g_wide_dual, R$string.data_connection_gprs));
    public static final DataTypeGroup WIDE_H = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_h_wide, R$drawable.zz_moto_stat_sys_data_suspended_h_wide, R$drawable.zz_moto_stat_sys_data_disabled_h_wide, R$drawable.zz_moto_stat_sys_data_connected_h_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_h_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_h_wide_dual, R$string.data_connection_3_5g));
    public static final DataTypeGroup WIDE_H_PLUS = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_hplus_wide, R$drawable.zz_moto_stat_sys_data_suspended_hplus_wide, R$drawable.zz_moto_stat_sys_data_disabled_hplus_wide, R$drawable.zz_moto_stat_sys_data_connected_hplus_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_hplus_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_hplus_wide_dual, R$string.data_connection_3_5g_plus));
    public static final DataTypeGroup WIDE_LTE = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_lte_wide, R$drawable.zz_moto_stat_sys_data_suspended_lte_wide, R$drawable.zz_moto_stat_sys_data_disabled_lte_wide, R$drawable.zz_moto_stat_sys_data_connected_lte_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_lte_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_lte_wide_dual, R$string.data_connection_lte));
    public static final DataTypeGroup WIDE_LTE_PLUS = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_lte_plus_wide, R$drawable.zz_moto_stat_sys_data_suspended_lte_plus_wide, R$drawable.zz_moto_stat_sys_data_disabled_lte_plus_wide, R$drawable.zz_moto_stat_sys_data_connected_lte_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_suspended_lte_plus_wide_dual, R$drawable.zz_moto_stat_sys_data_disabled_lte_plus_wide_dual, R$string.data_connection_lte_plus));
    public static final DataTypeGroup WIDE_OFF = new DataTypeGroup(new DataTypeGroup.Icon(0, 0, 0, 0));

    static {
        int i = R$drawable.zz_moto_stat_sys_data_connected_4g_lte_wide;
        int i2 = R$drawable.zz_moto_stat_sys_data_suspended_4g_lte_wide;
        int i3 = R$drawable.zz_moto_stat_sys_data_disabled_4g_lte_wide;
        int i4 = R$drawable.zz_moto_stat_sys_data_connected_4g_lte_wide_dual;
        int i5 = R$drawable.zz_moto_stat_sys_data_suspended_4g_lte_wide_dual;
        int i6 = R$drawable.zz_moto_stat_sys_data_disabled_4g_lte_wide_dual;
        int i7 = R$string.zz_moto_data_type_4g_lte;
        WIDE_4G_LTE = new DataTypeGroup(new DataTypeGroup.Icon(i, i2, i3, i4, i5, i6, i7));
        WIDE_4G_LTE_ATT = new DataTypeGroup(new DataTypeGroup.Icon(R$drawable.zz_moto_stat_sys_data_connected_4g_lte_wide_att, R$drawable.zz_moto_stat_sys_data_suspended_4g_lte_wide_att, R$drawable.zz_moto_stat_sys_data_disabled_4g_lte_wide_att, R$drawable.zz_moto_stat_sys_data_connected_4g_lte_wide_att_dual, R$drawable.zz_moto_stat_sys_data_suspended_4g_lte_wide_att_dual, R$drawable.zz_moto_stat_sys_data_disabled_4g_lte_wide_att_dual, i7));
    }

    public static class DataTypeGroup {

        /* renamed from: sb */
        private Icon f106sb;

        private static class Icon {
            /* access modifiers changed from: private */
            public int active;
            /* access modifiers changed from: private */
            public int disable;
            /* access modifiers changed from: private */
            public int doubleActive;
            /* access modifiers changed from: private */
            public int doubleDisable;
            /* access modifiers changed from: private */
            public int doubleSuspend;
            /* access modifiers changed from: private */
            public int suspend;
            /* access modifiers changed from: private */
            public int typeDescription;

            private Icon() {
                this.active = -1;
                this.suspend = -1;
                this.disable = -1;
                this.doubleActive = -1;
                this.doubleSuspend = -1;
                this.doubleDisable = -1;
            }

            public Icon(int i, int i2, int i3, int i4) {
                this.active = i;
                this.suspend = i2;
                this.disable = i3;
                this.typeDescription = i4;
            }

            public Icon(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
                this.active = i;
                this.suspend = i2;
                this.disable = i3;
                this.doubleActive = i4;
                this.doubleSuspend = i5;
                this.doubleDisable = i6;
                this.typeDescription = i7;
            }
        }

        public DataTypeGroup() {
            this.f106sb = new Icon();
        }

        public DataTypeGroup(Icon icon) {
            this.f106sb = icon;
        }

        public int sbData(boolean z, int i) {
            if (z && i != -1) {
                i += 3;
            }
            if (i == 0) {
                return this.f106sb.active;
            }
            if (i == 1) {
                return this.f106sb.suspend;
            }
            if (i == 2) {
                return this.f106sb.disable;
            }
            if (i == 3) {
                return this.f106sb.doubleActive;
            }
            if (i == 4) {
                return this.f106sb.doubleSuspend;
            }
            if (i != 5) {
                return 0;
            }
            return this.f106sb.doubleDisable;
        }

        public int sbDataDescription() {
            return this.f106sb.typeDescription;
        }
    }

    public static class RoamingIcon {
        private static final int[] DOUBLE_ROAMING_ICONS = {R$drawable.zz_moto_stat_sys_data_connected_roam_wide_dual, R$drawable.zz_moto_stat_sys_roaming_cdma_0_wide_dual, R$drawable.zz_moto_stat_sys_roaming_cdma_flash_wide_dual, R$drawable.zz_moto_stat_sys_roaming_cdma_femtocell_wide_dual, R$drawable.zz_moto_stat_sys_roaming_cdma_femtocell_flash_wide_dual};
        private static final int[] ROAMING_ICONS = {R$drawable.zz_moto_stat_sys_data_connected_roam_wide, R$drawable.zz_moto_stat_sys_roaming_cdma_0_wide, R$drawable.zz_moto_stat_sys_roaming_cdma_flash_wide, R$drawable.zz_moto_stat_sys_roaming_cdma_femtocell_wide, R$drawable.zz_moto_stat_sys_roaming_cdma_femtocell_flash_wide};

        public static int sbRoaming(boolean z, int i) {
            if (z) {
                return DOUBLE_ROAMING_ICONS[i];
            }
            return ROAMING_ICONS[i];
        }
    }

    public static class ActivityIcon {
        private static final int[] ACTIVITY_ICONS = {R$drawable.zz_moto_stat_sys_data_activity_empty_wide, R$drawable.zz_moto_stat_sys_data_connected_dormant_wide, R$drawable.zz_moto_stat_sys_data_connected_in_wide, R$drawable.zz_moto_stat_sys_data_connected_out_wide, R$drawable.zz_moto_stat_sys_data_connected_inout_wide};
        private static final int[] ACTIVITY_ICONS_ATT = {R$drawable.zz_moto_stat_sys_data_activity_empty_wide_att, R$drawable.zz_moto_stat_sys_data_connected_dormant_wide_att, R$drawable.zz_moto_stat_sys_data_connected_in_wide_att, R$drawable.zz_moto_stat_sys_data_connected_out_wide_att, R$drawable.zz_moto_stat_sys_data_connected_inout_wide_att};
        private static final int[] ACTIVITY_ICONS_DUAL = {R$drawable.zz_moto_stat_sys_data_activity_empty_wide_dual, R$drawable.zz_moto_stat_sys_data_connected_dormant_wide_dual, R$drawable.zz_moto_stat_sys_data_connected_in_wide_dual, R$drawable.zz_moto_stat_sys_data_connected_out_wide_dual, R$drawable.zz_moto_stat_sys_data_connected_inout_wide_dual};
        static final int[] WIFI_ACTIVITY_ICONS = {R$drawable.zz_moto_stat_sys_wifi_idle_wide, R$drawable.zz_moto_stat_sys_wifi_in_wide, R$drawable.zz_moto_stat_sys_wifi_out_wide, R$drawable.zz_moto_stat_sys_wifi_inout_wide};

        public static int sbActivity(int i, boolean z, boolean z2) {
            if (z2) {
                return ACTIVITY_ICONS_DUAL[i];
            }
            return z ? ACTIVITY_ICONS_ATT[i] : ACTIVITY_ICONS[i];
        }

        public static int sbWifiActivity(int i) {
            return WIFI_ACTIVITY_ICONS[i];
        }
    }

    public static class SignalStrengthIcon {
        public static final int EMERGENCY = R$drawable.zz_moto_stat_sys_signal_emergency_only_wide;
        private static SparseArray<Integer> SB_NO_SIGNAL_BARS_SEPARATED;
        private static SparseArray<Integer> SB_NO_SIGNAL_BARS_SEPARATED_VZW;
        private static SparseArray<Integer> SB_NO_SIGNAL_BARS_SEPARATED_VZW_DUAL;
        public static final int[][] STRENGTH_4_BARS_SEPARATED = {new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_4_separated_wide, R$drawable.zz_moto_stat_sys_signal_1_of_4_separated_wide, R$drawable.zz_moto_stat_sys_signal_2_of_4_separated_wide, R$drawable.zz_moto_stat_sys_signal_3_of_4_separated_wide, R$drawable.zz_moto_stat_sys_signal_4_of_4_separated_wide}, new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_4_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_1_of_4_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_2_of_4_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_3_of_4_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_4_of_4_fully_separated_wide}};
        public static final int[][] STRENGTH_5_BARS_SEPARATED = {new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_5_separated_wide, R$drawable.zz_moto_stat_sys_signal_1_of_5_separated_wide, R$drawable.zz_moto_stat_sys_signal_2_of_5_separated_wide, R$drawable.zz_moto_stat_sys_signal_3_of_5_separated_wide, R$drawable.zz_moto_stat_sys_signal_4_of_5_separated_wide, R$drawable.zz_moto_stat_sys_signal_5_of_5_separated_wide}, new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_5_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_1_of_5_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_2_of_5_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_3_of_5_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_4_of_5_fully_separated_wide, R$drawable.zz_moto_stat_sys_signal_5_of_5_fully_separated_wide}};
        public static final int[][] STRENGTH_5_BARS_SEPARATED_VZW = {new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_5_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_1_of_5_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_2_of_5_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_3_of_5_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_4_of_5_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_5_of_5_separated_wide_vzw}, new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_5_fully_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_1_of_5_fully_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_2_of_5_fully_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_3_of_5_fully_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_4_of_5_fully_separated_wide_vzw, R$drawable.zz_moto_stat_sys_signal_5_of_5_fully_separated_wide_vzw}};
        public static final int[][] STRENGTH_5_BARS_SEPARATED_VZW_DUAL = {new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_5_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_1_of_5_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_2_of_5_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_3_of_5_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_4_of_5_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_5_of_5_separated_wide_vzw_dual}, new int[]{R$drawable.zz_moto_stat_sys_signal_0_of_5_fully_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_1_of_5_fully_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_2_of_5_fully_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_3_of_5_fully_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_4_of_5_fully_separated_wide_vzw_dual, R$drawable.zz_moto_stat_sys_signal_5_of_5_fully_separated_wide_vzw_dual}};

        private static int verifyMaxLevel(int i) {
            if (i > 5) {
                return 5;
            }
            if (i < 4) {
                return 4;
            }
            return i;
        }

        static {
            SparseArray<Integer> sparseArray = new SparseArray<>(2);
            SB_NO_SIGNAL_BARS_SEPARATED = sparseArray;
            sparseArray.append(4, Integer.valueOf(R$drawable.zz_moto_stat_sys_signal_no_signal_of_4_separated_wide));
            SparseArray<Integer> sparseArray2 = SB_NO_SIGNAL_BARS_SEPARATED;
            int i = R$drawable.zz_moto_stat_sys_signal_no_signal_of_5_separated_wide;
            sparseArray2.append(5, Integer.valueOf(i));
            SparseArray<Integer> sparseArray3 = new SparseArray<>(2);
            SB_NO_SIGNAL_BARS_SEPARATED_VZW = sparseArray3;
            sparseArray3.append(4, Integer.valueOf(i));
            SB_NO_SIGNAL_BARS_SEPARATED_VZW.append(5, Integer.valueOf(R$drawable.zz_moto_stat_sys_signal_no_signal_of_5_separated_wide_vzw));
            SparseArray<Integer> sparseArray4 = new SparseArray<>(2);
            SB_NO_SIGNAL_BARS_SEPARATED_VZW_DUAL = sparseArray4;
            int i2 = R$drawable.zz_moto_stat_sys_signal_no_signal_of_5_separated_wide_vzw_dual;
            sparseArray4.append(4, Integer.valueOf(i2));
            SB_NO_SIGNAL_BARS_SEPARATED_VZW_DUAL.append(5, Integer.valueOf(i2));
        }

        /* JADX WARNING: type inference failed for: r3v0, types: [boolean] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static int sbSignalStrength(int r1, int r2, boolean r3, boolean r4, boolean r5) {
            /*
                int r1 = verifyMaxLevel(r1)
                if (r2 <= r1) goto L_0x0007
                r2 = r1
            L_0x0007:
                r0 = 5
                if (r1 != r0) goto L_0x0023
                if (r4 == 0) goto L_0x001c
                if (r5 == 0) goto L_0x0015
                int[][] r1 = STRENGTH_5_BARS_SEPARATED_VZW_DUAL
                r1 = r1[r3]
                r1 = r1[r2]
                goto L_0x0029
            L_0x0015:
                int[][] r1 = STRENGTH_5_BARS_SEPARATED_VZW
                r1 = r1[r3]
                r1 = r1[r2]
                goto L_0x0029
            L_0x001c:
                int[][] r1 = STRENGTH_5_BARS_SEPARATED
                r1 = r1[r3]
                r1 = r1[r2]
                goto L_0x0029
            L_0x0023:
                int[][] r1 = STRENGTH_4_BARS_SEPARATED
                r1 = r1[r3]
                r1 = r1[r2]
            L_0x0029:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.moto.CarrierIcons.SignalStrengthIcon.sbSignalStrength(int, int, boolean, boolean, boolean):int");
        }

        public static int sbNoSignal(int i, boolean z, boolean z2) {
            Integer num;
            int verifyMaxLevel = verifyMaxLevel(i);
            if (!z) {
                num = SB_NO_SIGNAL_BARS_SEPARATED.get(verifyMaxLevel);
            } else if (z2) {
                num = SB_NO_SIGNAL_BARS_SEPARATED_VZW_DUAL.get(verifyMaxLevel);
            } else {
                num = SB_NO_SIGNAL_BARS_SEPARATED_VZW.get(verifyMaxLevel);
            }
            return num.intValue();
        }

        public static int getCurrentIconId(boolean z, boolean z2, int i, int i2, boolean z3, boolean z4, boolean z5, boolean z6) {
            if (!z5) {
                z3 = true;
            }
            if (z2) {
                return sbSignalStrength(i, i2, z3, z6, z4);
            }
            return sbNoSignal(i, z6, z4);
        }
    }
}
