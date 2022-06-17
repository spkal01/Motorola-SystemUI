package com.android.systemui.moto;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.android.systemui.R$styleable;
import com.android.systemui.R$xml;
import com.android.systemui.moto.CarrierIcons;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

public class CarrierNetworkType {
    static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("CarrierNetworkType", 3));
    /* access modifiers changed from: private */
    public static final HashMap<String, CarrierIcons.DataTypeGroup> sDataTypeHashMap = new HashMap<>();

    private static void loadDataHashMap() {
        HashMap<String, CarrierIcons.DataTypeGroup> hashMap = sDataTypeHashMap;
        if (hashMap.isEmpty()) {
            hashMap.put("1X_W", CarrierIcons.WIDE_1X);
            hashMap.put("2G_W", CarrierIcons.WIDE_2G);
            hashMap.put("3G_W", CarrierIcons.WIDE_3G);
            hashMap.put("3G+_W", CarrierIcons.WIDE_3G_PLUS);
            hashMap.put("4G_W", CarrierIcons.WIDE_4G);
            hashMap.put("4G_A_W", CarrierIcons.WIDE_4G_ATT);
            hashMap.put("4G_LTE_W", CarrierIcons.WIDE_4G_LTE);
            hashMap.put("4G_LTE_A_W", CarrierIcons.WIDE_4G_LTE_ATT);
            hashMap.put("4G+_W", CarrierIcons.WIDE_4G_PLUS);
            CarrierIcons.DataTypeGroup dataTypeGroup = CarrierIcons.WIDE_E;
            hashMap.put("E_W", dataTypeGroup);
            hashMap.put("E_A_W", dataTypeGroup);
            CarrierIcons.DataTypeGroup dataTypeGroup2 = CarrierIcons.WIDE_G;
            hashMap.put("G_W", dataTypeGroup2);
            hashMap.put("G_A_W", dataTypeGroup2);
            hashMap.put("H_W", CarrierIcons.WIDE_H);
            hashMap.put("H+_W", CarrierIcons.WIDE_H_PLUS);
            hashMap.put("LTE_W", CarrierIcons.WIDE_LTE);
            hashMap.put("LTE+_W", CarrierIcons.WIDE_LTE_PLUS);
            hashMap.put("LTE_5GE", CarrierIcons.LTE_CA_5G_E);
            CarrierIcons.DataTypeGroup dataTypeGroup3 = CarrierIcons.NR_5G;
            hashMap.put("NA_5G", dataTypeGroup3);
            hashMap.put("NA_5G_PLUS", CarrierIcons.NR_5G_PLUS);
            hashMap.put("SA_5G", dataTypeGroup3);
            hashMap.put("OFF", CarrierIcons.WIDE_OFF);
        }
    }

    public static void loadCarrierNetworkMap(Context context, NetworkConfig networkConfig, SparseArray<CarrierIcons.DataTypeGroup> sparseArray, int i) {
        loadDataHashMap();
        XmlResourceParser xml = context.getResources().getXml(R$xml.carrier_network_map);
        try {
            parseNetwork(context, xml, networkConfig, sparseArray, i);
        } catch (XmlPullParserException e) {
            Log.w("CarrierNetworkType", "network XML parse error", e);
        } catch (IOException e2) {
            Log.w("CarrierNetworkType", "network XML parse IOException", e2);
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
        xml.close();
    }

    private static void parseNetwork(Context context, XmlResourceParser xmlResourceParser, NetworkConfig networkConfig, SparseArray<CarrierIcons.DataTypeGroup> sparseArray, int i) throws XmlPullParserException, IOException {
        boolean z;
        if (i != 0) {
            CarrierNetwork.getGenericLookup(networkConfig, sparseArray);
            StringBuilder sb = new StringBuilder();
            while (true) {
                z = true;
                if (xmlResourceParser.getEventType() == 1) {
                    z = false;
                    break;
                } else if (xmlResourceParser.next() == 2 && "item".equals(xmlResourceParser.getName())) {
                    TypedArray obtainAttributes = context.getResources().obtainAttributes(Xml.asAttributeSet(xmlResourceParser), R$styleable.MobileNetwork);
                    if (i == obtainAttributes.getInt(R$styleable.MobileNetwork_carrier_id, 0)) {
                        applyMap(sparseArray, obtainAttributes, sb);
                        if (DEBUG) {
                            Log.d("CarrierNetworkType", "Carrier Network Map: " + sb.toString());
                        }
                    } else {
                        obtainAttributes.recycle();
                    }
                }
            }
            if (!z && DEBUG) {
                Log.d("CarrierNetworkType", "Default Network Map loaded for " + i);
            }
        }
    }

    private static void applyMap(SparseArray<CarrierIcons.DataTypeGroup> sparseArray, TypedArray typedArray, StringBuilder sb) {
        String str;
        SparseArray<CarrierIcons.DataTypeGroup> sparseArray2 = sparseArray;
        TypedArray typedArray2 = typedArray;
        StringBuilder sb2 = sb;
        String valueOf = String.valueOf(typedArray2.getInt(R$styleable.MobileNetwork_carrier_id, 0));
        String string = typedArray2.getString(R$styleable.MobileNetwork_cdma_1x);
        String string2 = typedArray2.getString(R$styleable.MobileNetwork_g);
        String string3 = typedArray2.getString(R$styleable.MobileNetwork_e);
        String string4 = typedArray2.getString(R$styleable.MobileNetwork_evdo);
        String string5 = typedArray2.getString(R$styleable.MobileNetwork_umts);
        String string6 = typedArray2.getString(R$styleable.MobileNetwork_h);
        String string7 = typedArray2.getString(R$styleable.MobileNetwork_hspap);
        String string8 = typedArray2.getString(R$styleable.MobileNetwork_lte);
        String string9 = typedArray2.getString(R$styleable.MobileNetwork_off);
        String string10 = typedArray2.getString(R$styleable.MobileNetwork_td_scdma);
        String string11 = typedArray2.getString(R$styleable.MobileNetwork_lte_ca);
        String string12 = typedArray2.getString(R$styleable.MobileNetwork_na_5g);
        String string13 = typedArray2.getString(R$styleable.MobileNetwork_na_5g_plus);
        String string14 = typedArray2.getString(R$styleable.MobileNetwork_lte_5ge);
        String string15 = typedArray2.getString(R$styleable.MobileNetwork_sa_5g);
        sb2.append("carrierId:");
        sb2.append(valueOf);
        if (!TextUtils.isEmpty(string)) {
            HashMap<String, CarrierIcons.DataTypeGroup> hashMap = sDataTypeHashMap;
            str = string15;
            sparseArray2.put(7, hashMap.get(string));
            sparseArray2.put(4, hashMap.get(string));
            sb2.append(',');
            sb2.append("cdma_1x:");
            sb2.append(string);
        } else {
            str = string15;
        }
        if (!TextUtils.isEmpty(string2)) {
            HashMap<String, CarrierIcons.DataTypeGroup> hashMap2 = sDataTypeHashMap;
            sparseArray2.put(1, hashMap2.get(string2));
            sparseArray2.put(16, hashMap2.get(string2));
            sb2.append(',');
            sb2.append("g:");
            sb2.append(string2);
        }
        if (!TextUtils.isEmpty(string3)) {
            sparseArray2.put(2, sDataTypeHashMap.get(string3));
            sb2.append(',');
            sb2.append("e:");
            sb2.append(string3);
        }
        if (!TextUtils.isEmpty(string4)) {
            HashMap<String, CarrierIcons.DataTypeGroup> hashMap3 = sDataTypeHashMap;
            sparseArray2.put(5, hashMap3.get(string4));
            sparseArray2.put(6, hashMap3.get(string4));
            sparseArray2.put(12, hashMap3.get(string4));
            sparseArray2.put(14, hashMap3.get(string4));
            sb2.append(',');
            sb2.append("evdo:");
            sb2.append(string4);
        }
        if (!TextUtils.isEmpty(string5)) {
            sparseArray2.put(3, sDataTypeHashMap.get(string5));
            sb2.append(',');
            sb2.append("umts:");
            sb2.append(string5);
        }
        if (!TextUtils.isEmpty(string6)) {
            HashMap<String, CarrierIcons.DataTypeGroup> hashMap4 = sDataTypeHashMap;
            sparseArray2.put(8, hashMap4.get(string6));
            sparseArray2.put(10, hashMap4.get(string6));
            sparseArray2.put(9, hashMap4.get(string6));
            sb2.append(',');
            sb2.append("h:");
            sb2.append(string6);
        }
        if (!TextUtils.isEmpty(string7)) {
            sparseArray2.put(15, sDataTypeHashMap.get(string7));
            sb2.append(',');
            sb2.append("hspap:");
            sb2.append(string7);
        }
        if (!TextUtils.isEmpty(string8)) {
            sparseArray2.put(13, sDataTypeHashMap.get(string8));
            sb2.append(',');
            sb2.append("lte:");
            sb2.append(string8);
        }
        if (!TextUtils.isEmpty(string9)) {
            HashMap<String, CarrierIcons.DataTypeGroup> hashMap5 = sDataTypeHashMap;
            sparseArray2.put(11, hashMap5.get(string9));
            sparseArray2.put(0, hashMap5.get(string9));
            sparseArray2.put(18, hashMap5.get(string9));
            sb2.append(',');
            sb2.append("off:");
            sb2.append(string9);
        }
        if (!TextUtils.isEmpty(string10)) {
            sparseArray2.put(17, sDataTypeHashMap.get(string10));
            sb2.append(',');
            sb2.append("td_scdma:");
            sb2.append(string10);
        }
        if (!TextUtils.isEmpty(string11)) {
            sparseArray2.put(19, sDataTypeHashMap.get(string11));
            sb2.append(',');
            sb2.append("lte_ca:");
            sb2.append(string11);
        }
        if (!TextUtils.isEmpty(string12)) {
            String str2 = string12;
            sparseArray2.put(androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginStart, sDataTypeHashMap.get(str2));
            sb2.append(',');
            sb2.append("na_5g:");
            sb2.append(str2);
        }
        if (!TextUtils.isEmpty(string13)) {
            String str3 = string13;
            sparseArray2.put(androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginTop, sDataTypeHashMap.get(str3));
            sb2.append(',');
            sb2.append("na_5g_plus:");
            sb2.append(str3);
        }
        if (!TextUtils.isEmpty(string14)) {
            String str4 = string14;
            sparseArray2.put(androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginRight, sDataTypeHashMap.get(str4));
            sb2.append(',');
            sb2.append("lte_5g:");
            sb2.append(str4);
        }
        if (!TextUtils.isEmpty(str)) {
            String str5 = str;
            sparseArray2.put(20, sDataTypeHashMap.get(str5));
            sb2.append(',');
            sb2.append("sa_5g:");
            sb2.append(str5);
        }
    }

    static class CarrierNetwork {
        static final CarrierNetwork GENERIC = new CarrierNetwork(0, "both", "OFF", "G_W", "E_W", "3G_W", "3G_W", "3G_W", "LTE_W", "1X_W", "3G_W", "3G_W", "LTE+_W", "LTE_5GE", "NA_5G", "NA_5G_PLUS", "SA_5G");
        private int carrierId;
        private String cdma_1x;

        /* renamed from: e */
        private String f107e;
        private String evdo;

        /* renamed from: g */
        private String f108g;

        /* renamed from: h */
        private String f109h;
        private String home;
        private String hspap;
        private String lte;
        private String lte_5ge;
        private String lte_ca;
        private String na_5g;
        private String na_5g_plus;
        private String off;
        private String sa_5g;
        private String td_scdma;
        private String umts;

        public CarrierNetwork(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16) {
            this.carrierId = i;
            this.home = str;
            this.off = str2;
            this.f108g = str3;
            this.f107e = str4;
            this.umts = str5;
            this.f109h = str6;
            this.hspap = str7;
            this.lte = str8;
            this.cdma_1x = str9;
            this.evdo = str10;
            this.td_scdma = str11;
            this.lte_ca = str12;
            this.na_5g = str14;
            this.na_5g_plus = str15;
            this.lte_5ge = str13;
            this.sa_5g = str16;
        }

        public void getDataLookupArray(SparseArray<CarrierIcons.DataTypeGroup> sparseArray) {
            sparseArray.put(7, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.cdma_1x));
            sparseArray.put(4, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.cdma_1x));
            sparseArray.put(1, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.f108g));
            sparseArray.put(16, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.f108g));
            sparseArray.put(2, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.f107e));
            sparseArray.put(5, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.evdo));
            sparseArray.put(6, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.evdo));
            sparseArray.put(12, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.evdo));
            sparseArray.put(14, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.evdo));
            sparseArray.put(3, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.umts));
            sparseArray.put(8, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.f109h));
            sparseArray.put(10, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.f109h));
            sparseArray.put(9, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.f109h));
            sparseArray.put(15, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.hspap));
            sparseArray.put(13, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.lte));
            sparseArray.put(11, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.off));
            sparseArray.put(0, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.off));
            sparseArray.put(18, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.off));
            sparseArray.put(17, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.td_scdma));
            sparseArray.put(19, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.lte_ca));
            sparseArray.put(androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginStart, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.na_5g));
            sparseArray.put(androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginTop, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.na_5g_plus));
            sparseArray.put(androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginRight, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.lte_5ge));
            sparseArray.put(20, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get(this.sa_5g));
        }

        public static void getGenericLookup(NetworkConfig networkConfig, SparseArray<CarrierIcons.DataTypeGroup> sparseArray) {
            GENERIC.getDataLookupArray(sparseArray);
            updateDataLookupWithConfig(networkConfig, sparseArray);
        }

        private static void updateDataLookupWithConfig(NetworkConfig networkConfig, SparseArray<CarrierIcons.DataTypeGroup> sparseArray) {
            if (networkConfig.showAtLeast3G) {
                sparseArray.put(7, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(4, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(1, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(16, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(2, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(0, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
            }
            if (networkConfig.hspaDataDistinguishable) {
                sparseArray.put(8, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("H_W"));
                sparseArray.put(10, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("H_W"));
                sparseArray.put(9, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("H_W"));
                if (networkConfig.hspapDataDistinguishable) {
                    sparseArray.put(15, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("H+_W"));
                } else {
                    sparseArray.put(15, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("H_W"));
                }
            } else {
                sparseArray.put(8, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(10, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(9, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
                sparseArray.put(15, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("3G_W"));
            }
            if (networkConfig.show4GForHspap) {
                sparseArray.put(15, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("4G_W"));
            }
            if (networkConfig.show4gForLte) {
                sparseArray.put(13, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("4G_W"));
                if (networkConfig.hideLtePlus) {
                    sparseArray.put(19, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("4G_W"));
                } else {
                    sparseArray.put(19, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("4G+_W"));
                }
            } else if (networkConfig.hideLtePlus) {
                sparseArray.put(19, (CarrierIcons.DataTypeGroup) CarrierNetworkType.sDataTypeHashMap.get("LTE_W"));
            }
        }
    }
}
