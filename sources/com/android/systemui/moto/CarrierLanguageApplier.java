package com.android.systemui.moto;

import android.annotation.SuppressLint;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.R$xml;
import java.io.IOException;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParserException;

/* compiled from: CarrierLanguageApplier.kt */
public final class CarrierLanguageApplier {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);

    /* compiled from: CarrierLanguageApplier.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @SuppressLint({"NewApi", "MissingPermission"})
        public final void updateLanguageIfNecessary(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            String roCarrier = MotoFeature.getInstance(context).getRoCarrier();
            int[] activeSubscriptionIdList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionIdList();
            Intrinsics.checkNotNullExpressionValue(activeSubscriptionIdList, "mSubsMan.getActiveSubscriptionIdList()");
            boolean isDeviceProvisionedInSettingsDb = isDeviceProvisionedInSettingsDb(context);
            boolean z = false;
            if (roCarrier != null && !roCarrier.equals("unknown")) {
                if (!(!(activeSubscriptionIdList.length == 0)) && !isDeviceProvisionedInSettingsDb) {
                    Log.i("CarrierLanguageApplier", Intrinsics.stringPlus("updating language with following carrierId: ", roCarrier));
                    Resources resources = context.getResources();
                    Intrinsics.checkNotNullExpressionValue(resources, "context.resources");
                    String parseLanguageTag = parseLanguageTag(resources, roCarrier);
                    if (!TextUtils.isEmpty(parseLanguageTag)) {
                        Log.i("CarrierLanguageApplier", Intrinsics.stringPlus("language tag: ", parseLanguageTag));
                        try {
                            IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                            Configuration configuration = iActivityManager.getConfiguration();
                            configuration.setLocale(Locale.forLanguageTag(parseLanguageTag));
                            configuration.userSetLocale = true;
                            iActivityManager.updatePersistentConfiguration(configuration);
                            Log.i("CarrierLanguageApplier", Intrinsics.stringPlus("language updated: ", configuration));
                            return;
                        } catch (Exception e) {
                            Log.w("CarrierLanguageApplier", Intrinsics.stringPlus("failed to apply lang: ", e));
                            return;
                        }
                    } else {
                        Log.w("CarrierLanguageApplier", "No language was found. Not updating.");
                        return;
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("fail to update language: roCarrier: ");
            sb.append(roCarrier);
            sb.append(" - SIMCard present: ");
            if (activeSubscriptionIdList.length == 0) {
                z = true;
            }
            sb.append(!z);
            sb.append(" - isDeviceProvisioned: ");
            sb.append(isDeviceProvisionedInSettingsDb);
            Log.w("CarrierLanguageApplier", sb.toString());
        }

        private final String parseLanguageTag(Resources resources, String str) {
            XmlResourceParser xml = resources.getXml(R$xml.carrier_lang_map);
            Intrinsics.checkNotNullExpressionValue(xml, "res.getXml(R.xml.carrier_lang_map)");
            String str2 = "";
            while (true) {
                try {
                    if (xml.getEventType() == 1) {
                        break;
                    } else if (xml.getEventType() != 2 || !xml.getName().equals("entry")) {
                        xml.next();
                    } else {
                        String attributeValue = xml.getAttributeValue((String) null, "key");
                        Intrinsics.checkNotNullExpressionValue(attributeValue, "parser.getAttributeValue(null, \"key\")");
                        if (str.equals(attributeValue)) {
                            String nextText = xml.nextText();
                            Intrinsics.checkNotNullExpressionValue(nextText, "parser.nextText()");
                            str2 = nextText;
                            break;
                        }
                        xml.next();
                    }
                } catch (XmlPullParserException e) {
                    Log.w("CarrierLanguageApplier", "failure parsing lang-tag", e);
                } catch (IOException e2) {
                    Log.w("CarrierLanguageApplier", "failure parsing lang-tag IOException", e2);
                } catch (Throwable th) {
                    xml.close();
                    throw th;
                }
            }
            xml.close();
            return str2;
        }

        @SuppressLint({"NewApi"})
        private final boolean isDeviceProvisionedInSettingsDb(Context context) {
            return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
        }
    }
}
