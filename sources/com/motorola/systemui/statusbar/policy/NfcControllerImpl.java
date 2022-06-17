package com.motorola.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.util.Utils;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.systemui.statusbar.policy.NfcController;
import java.util.ArrayList;

public final class NfcControllerImpl extends BroadcastReceiver implements NfcController {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("NfcController", 3));
    private BroadcastDispatcher mBroadcastDispatcher;
    private final Context mContext;
    private final C2790H mHandler;
    private NfcAdapter mNfcAdapter;
    private boolean mNfcEnabled;
    private ContentObserver mNfcIconObserver;
    /* access modifiers changed from: private */
    public boolean mNfcIconVisible;

    public NfcControllerImpl(Context context, Looper looper, Looper looper2, BroadcastDispatcher broadcastDispatcher) {
        this.mContext = context;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mHandler = new C2790H(looper);
        if (isNfcAvailable()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
            intentFilter.addAction("android.nfc.action.ADAPTER_STATE_CHANGED");
            this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, new Handler(looper2), UserHandle.ALL);
            updateNfcIconVisible((Intent) null);
        }
    }

    public void addCallback(NfcController.NfcChangeCallback nfcChangeCallback) {
        if (isNfcAvailable()) {
            this.mHandler.obtainMessage(2, nfcChangeCallback).sendToTarget();
            this.mHandler.sendEmptyMessage(1);
        }
    }

    public void removeCallback(NfcController.NfcChangeCallback nfcChangeCallback) {
        this.mHandler.obtainMessage(3, nfcChangeCallback).sendToTarget();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            action.hashCode();
            if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                updateNfcIconVisible((Intent) null);
            } else if (action.equals("android.nfc.action.ADAPTER_STATE_CHANGED")) {
                updateNfcIconVisible(intent);
            }
        }
    }

    public boolean isNfcAvailable() {
        boolean hasSystemFeature = this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc");
        Log.v("NfcController", "isNfcAvailable?  : " + hasSystemFeature);
        return hasSystemFeature;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0039, code lost:
        if (r6 != 4) goto L_0x005b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateNfcIconVisible(android.content.Intent r6) {
        /*
            r5 = this;
            java.lang.String r0 = "NfcController"
            r1 = 0
            r2 = 1
            if (r6 == 0) goto L_0x0042
            java.lang.String r3 = r6.getAction()
            java.lang.String r4 = "android.nfc.action.ADAPTER_STATE_CHANGED"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x005b
            java.lang.String r3 = "android.nfc.extra.ADAPTER_STATE"
            int r6 = r6.getIntExtra(r3, r2)
            boolean r3 = DEBUG
            if (r3 == 0) goto L_0x0030
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "updateNFC state received : "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            android.util.Log.v(r0, r3)
        L_0x0030:
            if (r6 == r2) goto L_0x003f
            r3 = 2
            if (r6 == r3) goto L_0x003c
            r3 = 3
            if (r6 == r3) goto L_0x003c
            r3 = 4
            if (r6 == r3) goto L_0x003f
            goto L_0x005b
        L_0x003c:
            r5.mNfcEnabled = r2
            goto L_0x005b
        L_0x003f:
            r5.mNfcEnabled = r1
            goto L_0x005b
        L_0x0042:
            android.nfc.NfcAdapter r6 = r5.mNfcAdapter
            if (r6 != 0) goto L_0x004e
            android.content.Context r6 = r5.mContext     // Catch:{ UnsupportedOperationException -> 0x004e }
            android.nfc.NfcAdapter r6 = android.nfc.NfcAdapter.getNfcAdapter(r6)     // Catch:{ UnsupportedOperationException -> 0x004e }
            r5.mNfcAdapter = r6     // Catch:{ UnsupportedOperationException -> 0x004e }
        L_0x004e:
            android.nfc.NfcAdapter r6 = r5.mNfcAdapter
            if (r6 == 0) goto L_0x0059
            boolean r6 = r6.isEnabled()
            r5.mNfcEnabled = r6
            goto L_0x005b
        L_0x0059:
            r5.mNfcEnabled = r1
        L_0x005b:
            boolean r6 = r5.mNfcEnabled
            if (r6 == 0) goto L_0x0068
            android.content.Context r6 = r5.mContext
            boolean r6 = isNfcIconEnabled(r6)
            if (r6 == 0) goto L_0x0068
            r1 = r2
        L_0x0068:
            boolean r6 = r5.mNfcIconVisible
            if (r6 == r1) goto L_0x008b
            boolean r6 = DEBUG
            if (r6 == 0) goto L_0x0084
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r3 = "updateNFC icon visibility : "
            r6.append(r3)
            r6.append(r1)
            java.lang.String r6 = r6.toString()
            android.util.Log.v(r0, r6)
        L_0x0084:
            r5.mNfcIconVisible = r1
            com.motorola.systemui.statusbar.policy.NfcControllerImpl$H r5 = r5.mHandler
            r5.sendEmptyMessage(r2)
        L_0x008b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.statusbar.policy.NfcControllerImpl.updateNfcIconVisible(android.content.Intent):void");
    }

    public void registerNfcIconObserver(int i) {
        if (isNfcAvailable()) {
            if (DEBUG) {
                Log.v("NfcController", "Register NFC icon setting observer for user: " + i);
            }
            this.mNfcIconObserver = new ContentObserver(this.mHandler) {
                public void onChange(boolean z) {
                    if (NfcControllerImpl.DEBUG) {
                        Log.v("NfcController", "NFC icon setting observer changed");
                    }
                    NfcControllerImpl.this.updateNfcIconVisible((Intent) null);
                }
            };
            this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("show_nfc_icon_on_systemui"), false, this.mNfcIconObserver, i);
            updateNfcIconVisible((Intent) null);
        }
    }

    public void unregisterNfcIconObserver(int i) {
        if (DEBUG) {
            Log.v("NfcController", "Unregister NFC icon setting observer for user: " + i);
        }
        if (this.mNfcIconObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mNfcIconObserver);
        }
    }

    public static boolean isNfcIconEnabled(Context context) {
        int intForUser = MotorolaSettings.Secure.getIntForUser(context.getContentResolver(), "show_nfc_icon_on_systemui", -1, -2);
        if (intForUser < 0) {
            return getNfcConfig(context);
        }
        return intForUser == 1;
    }

    public static boolean getNfcConfig(Context context) {
        SubscriptionManager from = SubscriptionManager.from(context);
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = from.getActiveSubscriptionInfoForSimSlotIndex(0);
        if (activeSubscriptionInfoForSimSlotIndex != null) {
            if (DEBUG) {
                Log.i("NfcController", "get nfc config from slot 1");
            }
            return MotoFeature.getSubContext(context, activeSubscriptionInfoForSimSlotIndex).getResources().getBoolean(17891718);
        }
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex2 = from.getActiveSubscriptionInfoForSimSlotIndex(1);
        if (activeSubscriptionInfoForSimSlotIndex2 == null) {
            return context.getResources().getBoolean(17891718);
        }
        if (DEBUG) {
            Log.i("NfcController", "get nfc config from slot 2");
        }
        return MotoFeature.getSubContext(context, activeSubscriptionInfoForSimSlotIndex2).getResources().getBoolean(17891718);
    }

    /* renamed from: com.motorola.systemui.statusbar.policy.NfcControllerImpl$H */
    private final class C2790H extends Handler {
        private ArrayList<NfcController.NfcChangeCallback> mSettingsChangeCallbacks = new ArrayList<>();

        C2790H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                nfcIconVisibleChanged();
            } else if (i == 2) {
                this.mSettingsChangeCallbacks.add((NfcController.NfcChangeCallback) message.obj);
            } else if (i == 3) {
                this.mSettingsChangeCallbacks.remove((NfcController.NfcChangeCallback) message.obj);
            }
        }

        private void nfcIconVisibleChanged() {
            Utils.safeForeach(this.mSettingsChangeCallbacks, new NfcControllerImpl$H$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$nfcIconVisibleChanged$0(NfcController.NfcChangeCallback nfcChangeCallback) {
            nfcChangeCallback.onNfcIconVisibleChanged(NfcControllerImpl.this.mNfcIconVisible);
        }
    }
}
