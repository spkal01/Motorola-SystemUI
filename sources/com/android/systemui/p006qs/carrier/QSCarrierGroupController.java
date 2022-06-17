package com.android.systemui.p006qs.carrier;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.keyguard.CarrierTextManager;
import com.android.settingslib.AccessibilityContentDescriptions;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.systemui.R$color;
import com.android.systemui.moto.ExtendedMobileDataInfo;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.util.CarrierConfigTracker;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController */
public class QSCarrierGroupController {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("QSCarrierGroup", 3);
    private final ActivityStarter mActivityStarter;
    private final Handler mBgHandler;
    private final Callback mCallback;
    private final CarrierConfigTracker mCarrierConfigTracker;
    private View[] mCarrierDividers;
    private QSCarrier[] mCarrierGroups;
    private final CarrierTextManager mCarrierTextManager;
    /* access modifiers changed from: private */
    public final StatusBarSignalPolicy.MobileIconState[] mInfos;
    private boolean mIsSingleCarrier;
    private int[] mLastSignalLevel;
    private String[] mLastSignalLevelDescription;
    private boolean mListening;
    /* access modifiers changed from: private */
    public C1214H mMainHandler;
    private final NetworkController mNetworkController;
    private final TextView mNoSimTextView;
    private OnSingleCarrierChangedListener mOnSingleCarrierChangedListener;
    /* access modifiers changed from: private */
    public final boolean mProviderModel;
    private final NetworkController.SignalCallback mSignalCallback;
    private final SlotIndexResolver mSlotIndexResolver;

    @FunctionalInterface
    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$OnSingleCarrierChangedListener */
    public interface OnSingleCarrierChangedListener {
    }

    @FunctionalInterface
    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$SlotIndexResolver */
    public interface SlotIndexResolver {
        int getSlotIndex(int i);
    }

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$Callback */
    private static class Callback implements CarrierTextManager.CarrierTextCallback {
        private C1214H mHandler;

        Callback(C1214H h) {
            this.mHandler = h;
        }

        public void updateCarrierInfo(CarrierTextManager.CarrierTextCallbackInfo carrierTextCallbackInfo) {
            this.mHandler.obtainMessage(0, carrierTextCallbackInfo).sendToTarget();
        }
    }

    public void onColorsChanged() {
        if (MotoFeature.getInstance(this.mNoSimTextView.getContext()).isCustomPanelView()) {
            TextView textView = this.mNoSimTextView;
            textView.setTextColor(textView.getResources().getColor(R$color.prcQSPanelCarrierGroup));
        }
    }

    private QSCarrierGroupController(QSCarrierGroup qSCarrierGroup, ActivityStarter activityStarter, Handler handler, Looper looper, NetworkController networkController, CarrierTextManager.Builder builder, Context context, CarrierConfigTracker carrierConfigTracker, FeatureFlags featureFlags, SlotIndexResolver slotIndexResolver) {
        this.mInfos = new StatusBarSignalPolicy.MobileIconState[3];
        this.mCarrierDividers = new View[2];
        this.mCarrierGroups = new QSCarrier[3];
        this.mLastSignalLevel = new int[3];
        this.mLastSignalLevelDescription = new String[3];
        this.mSignalCallback = new NetworkController.SignalCallback() {
            public void setMobileDataIndicators(NetworkController.MobileDataIndicators mobileDataIndicators) {
                if (!QSCarrierGroupController.this.mProviderModel) {
                    int slotIndex = QSCarrierGroupController.this.getSlotIndex(mobileDataIndicators.subId);
                    if (slotIndex >= 3) {
                        Log.w("QSCarrierGroup", "setMobileDataIndicators - slot: " + slotIndex);
                    } else if (slotIndex == -1) {
                        Log.e("QSCarrierGroup", "Invalid SIM slot index for subscription: " + mobileDataIndicators.subId);
                    } else {
                        QSCarrierGroupController.this.mInfos[slotIndex].visible = mobileDataIndicators.statusIcon.visible;
                        QSCarrierGroupController.this.mInfos[slotIndex].contentDescription = mobileDataIndicators.statusIcon.contentDescription;
                        QSCarrierGroupController.this.mInfos[slotIndex].typeContentDescription = mobileDataIndicators.typeContentDescription;
                        QSCarrierGroupController.this.mInfos[slotIndex].roaming = mobileDataIndicators.roaming;
                        QSCarrierGroupController.this.mInfos[slotIndex].subId = mobileDataIndicators.subId;
                        QSCarrierGroupController.this.mInfos[slotIndex].strengthId = mobileDataIndicators.statusIcon.icon;
                        if (QSCarrierGroupController.DEBUG) {
                            Log.i("QSCarrierGroup", "setMobileDataIndicators typeIconForQSCarrier = " + mobileDataIndicators.extendedInfo.typeIconForQSCarrier + " qsType = " + mobileDataIndicators.qsType + " description = " + mobileDataIndicators.description + " subId = " + mobileDataIndicators.subId + " roaming = " + mobileDataIndicators.roaming + " info = " + mobileDataIndicators.extendedInfo);
                        }
                        if (mobileDataIndicators.extendedInfo != null) {
                            QSCarrierGroupController.this.mInfos[slotIndex].isShowAttRat = mobileDataIndicators.extendedInfo.isShowAttRat;
                            QSCarrierGroupController.this.mInfos[slotIndex].isShowVzwRat = mobileDataIndicators.extendedInfo.isShowVzwRat;
                            QSCarrierGroupController.this.mInfos[slotIndex].typeId = mobileDataIndicators.extendedInfo.typeIconForQSCarrier;
                            StatusBarSignalPolicy.MobileIconState mobileIconState = QSCarrierGroupController.this.mInfos[slotIndex];
                            ExtendedMobileDataInfo extendedMobileDataInfo = mobileDataIndicators.extendedInfo;
                            mobileIconState.mMobileUseMotoUI = extendedMobileDataInfo.enableCustomize;
                            if (extendedMobileDataInfo.roamIcon != null) {
                                QSCarrierGroupController.this.mInfos[slotIndex].mMobileRoamingIconId = mobileDataIndicators.extendedInfo.roamIcon.icon;
                                QSCarrierGroupController.this.mInfos[slotIndex].mMobileRoamingIconContentDescription = mobileDataIndicators.extendedInfo.roamIcon.contentDescription;
                            }
                            if (mobileDataIndicators.extendedInfo.activityIconForQSCarrier != null) {
                                StatusBarSignalPolicy.MobileIconState mobileIconState2 = QSCarrierGroupController.this.mInfos[slotIndex];
                                NetworkController.IconState iconState = mobileDataIndicators.extendedInfo.activityIconForQSCarrier;
                                mobileIconState2.mMobileDataActivityIconId = iconState.icon;
                                if (!iconState.visible) {
                                    QSCarrierGroupController.this.mInfos[slotIndex].mMobileDataActivityIconId = 0;
                                }
                            } else {
                                QSCarrierGroupController.this.mInfos[slotIndex].mMobileDataActivityIconId = 0;
                            }
                            QSCarrierGroupController.this.mInfos[slotIndex].showSeparatedSignalBars = mobileDataIndicators.extendedInfo.showSeparatedSignalBars;
                        } else {
                            QSCarrierGroupController.this.mInfos[slotIndex].mMobileUseMotoUI = false;
                            QSCarrierGroupController.this.mInfos[slotIndex].mMobileRoamingIconId = 0;
                            QSCarrierGroupController.this.mInfos[slotIndex].mMobileRoamingIconContentDescription = null;
                            QSCarrierGroupController.this.mInfos[slotIndex].mMobileDataActivityIconId = 0;
                            QSCarrierGroupController.this.mInfos[slotIndex].mMobileIsBidiDirectionEnabled = true;
                            QSCarrierGroupController.this.mInfos[slotIndex].showSeparatedSignalBars = false;
                        }
                        QSCarrierGroupController.this.mMainHandler.obtainMessage(1).sendToTarget();
                    }
                }
            }

            public void setCallIndicator(NetworkController.IconState iconState, int i) {
                boolean unused = QSCarrierGroupController.this.mProviderModel;
            }

            public void setNoSims(boolean z, boolean z2) {
                if (z) {
                    for (int i = 0; i < 3; i++) {
                        QSCarrierGroupController.this.mInfos[i].visible = false;
                    }
                }
                QSCarrierGroupController.this.mMainHandler.obtainMessage(1).sendToTarget();
            }
        };
        if (featureFlags.isCombinedStatusBarSignalIconsEnabled()) {
            this.mProviderModel = true;
        } else {
            this.mProviderModel = false;
        }
        this.mActivityStarter = activityStarter;
        this.mBgHandler = handler;
        this.mNetworkController = networkController;
        this.mCarrierTextManager = builder.setShowAirplaneMode(false).setShowMissingSim(false).build();
        this.mCarrierConfigTracker = carrierConfigTracker;
        this.mSlotIndexResolver = slotIndexResolver;
        QSCarrierGroupController$$ExternalSyntheticLambda0 qSCarrierGroupController$$ExternalSyntheticLambda0 = new QSCarrierGroupController$$ExternalSyntheticLambda0(this);
        qSCarrierGroup.setOnClickListener(qSCarrierGroupController$$ExternalSyntheticLambda0);
        TextView noSimTextView = qSCarrierGroup.getNoSimTextView();
        this.mNoSimTextView = noSimTextView;
        noSimTextView.setOnClickListener(qSCarrierGroupController$$ExternalSyntheticLambda0);
        C1214H h = new C1214H(looper, new QSCarrierGroupController$$ExternalSyntheticLambda3(this), new QSCarrierGroupController$$ExternalSyntheticLambda2(this));
        this.mMainHandler = h;
        this.mCallback = new Callback(h);
        this.mCarrierGroups[0] = qSCarrierGroup.getCarrier1View();
        this.mCarrierGroups[1] = qSCarrierGroup.getCarrier2View();
        this.mCarrierGroups[2] = qSCarrierGroup.getCarrier3View();
        this.mCarrierDividers[0] = qSCarrierGroup.getCarrierDivider1();
        this.mCarrierDividers[1] = qSCarrierGroup.getCarrierDivider2();
        for (int i = 0; i < 3; i++) {
            this.mInfos[i] = new StatusBarSignalPolicy.MobileIconState();
            this.mLastSignalLevel[i] = TelephonyIcons.MOBILE_CALL_STRENGTH_ICONS[0];
            this.mLastSignalLevelDescription[i] = context.getText(AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0]).toString();
            this.mCarrierGroups[i].setOnClickListener(qSCarrierGroupController$$ExternalSyntheticLambda0);
        }
        qSCarrierGroup.setImportantForAccessibility(1);
        qSCarrierGroup.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
            }

            public void onViewDetachedFromWindow(View view) {
                QSCarrierGroupController.this.setListening(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (view.isVisibleToUser()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.WIRELESS_SETTINGS"), 0);
        }
    }

    /* access modifiers changed from: protected */
    public int getSlotIndex(int i) {
        return this.mSlotIndexResolver.getSlotIndex(i);
    }

    public void setOnSingleCarrierChangedListener(OnSingleCarrierChangedListener onSingleCarrierChangedListener) {
        this.mOnSingleCarrierChangedListener = onSingleCarrierChangedListener;
    }

    public boolean isSingleCarrier() {
        return this.mIsSingleCarrier;
    }

    public void setListening(boolean z) {
        if (z != this.mListening) {
            this.mListening = z;
            this.mBgHandler.post(new QSCarrierGroupController$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public void updateListeners() {
        if (this.mListening) {
            if (this.mNetworkController.hasVoiceCallingFeature()) {
                this.mNetworkController.addCallback(this.mSignalCallback);
            }
            this.mCarrierTextManager.setListening(this.mCallback);
            return;
        }
        this.mNetworkController.removeCallback(this.mSignalCallback);
        this.mCarrierTextManager.setListening((CarrierTextManager.CarrierTextCallback) null);
    }

    /* access modifiers changed from: private */
    public void handleUpdateState() {
        if (!this.mMainHandler.getLooper().isCurrentThread()) {
            this.mMainHandler.obtainMessage(1).sendToTarget();
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            this.mCarrierGroups[i2].updateState(this.mInfos[i2]);
        }
        boolean[] zArr = new boolean[3];
        for (int i3 = 0; i3 < 3; i3++) {
            zArr[i3] = this.mInfos[i3].visible || this.mCarrierGroups[i3].isCarrierTextVisible();
        }
        this.mCarrierDividers[0].setVisibility((!zArr[0] || !zArr[1]) ? 8 : 0);
        View view = this.mCarrierDividers[1];
        if ((!zArr[1] || !zArr[2]) && (!zArr[0] || !zArr[2])) {
            i = 8;
        }
        view.setVisibility(i);
    }

    /* access modifiers changed from: private */
    public void handleUpdateCarrierInfo(CarrierTextManager.CarrierTextCallbackInfo carrierTextCallbackInfo) {
        if (!this.mMainHandler.getLooper().isCurrentThread()) {
            this.mMainHandler.obtainMessage(0, carrierTextCallbackInfo).sendToTarget();
            return;
        }
        this.mNoSimTextView.setVisibility(8);
        if (carrierTextCallbackInfo.airplaneMode || !carrierTextCallbackInfo.anySimReady) {
            for (int i = 0; i < 3; i++) {
                this.mInfos[i].visible = false;
                this.mCarrierGroups[i].setCarrierText("");
                this.mCarrierGroups[i].setVisibility(8);
            }
            this.mNoSimTextView.setText(carrierTextCallbackInfo.carrierText);
            if (!TextUtils.isEmpty(carrierTextCallbackInfo.carrierText)) {
                this.mNoSimTextView.setVisibility(0);
            }
        } else {
            boolean[] zArr = new boolean[3];
            if (carrierTextCallbackInfo.listOfCarriers.length == carrierTextCallbackInfo.subscriptionIds.length) {
                int i2 = 0;
                while (i2 < 3 && i2 < carrierTextCallbackInfo.listOfCarriers.length) {
                    int slotIndex = getSlotIndex(carrierTextCallbackInfo.subscriptionIds[i2]);
                    if (slotIndex >= 3) {
                        Log.w("QSCarrierGroup", "updateInfoCarrier - slot: " + slotIndex);
                    } else if (slotIndex == -1) {
                        Log.e("QSCarrierGroup", "Invalid SIM slot index for subscription: " + carrierTextCallbackInfo.subscriptionIds[i2]);
                    } else {
                        zArr[slotIndex] = true;
                        this.mCarrierGroups[slotIndex].setCarrierText(carrierTextCallbackInfo.listOfCarriers[i2].toString().trim());
                        this.mCarrierGroups[slotIndex].setVisibility(0);
                    }
                    i2++;
                }
                for (int i3 = 0; i3 < 3; i3++) {
                    if (!zArr[i3]) {
                        this.mInfos[i3].visible = false;
                        this.mCarrierGroups[i3].setVisibility(8);
                    }
                }
            } else {
                Log.e("QSCarrierGroup", "Carrier information arrays not of same length");
            }
        }
        handleUpdateState();
    }

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$H */
    private static class C1214H extends Handler {
        private Consumer<CarrierTextManager.CarrierTextCallbackInfo> mUpdateCarrierInfo;
        private Runnable mUpdateState;

        C1214H(Looper looper, Consumer<CarrierTextManager.CarrierTextCallbackInfo> consumer, Runnable runnable) {
            super(looper);
            this.mUpdateCarrierInfo = consumer;
            this.mUpdateState = runnable;
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                this.mUpdateCarrierInfo.accept((CarrierTextManager.CarrierTextCallbackInfo) message.obj);
            } else if (i != 1) {
                super.handleMessage(message);
            } else {
                this.mUpdateState.run();
            }
        }
    }

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$Builder */
    public static class Builder {
        private final ActivityStarter mActivityStarter;
        private final CarrierConfigTracker mCarrierConfigTracker;
        private final CarrierTextManager.Builder mCarrierTextControllerBuilder;
        private final Context mContext;
        private final FeatureFlags mFeatureFlags;
        private final Handler mHandler;
        private final Looper mLooper;
        private final NetworkController mNetworkController;
        private final SlotIndexResolver mSlotIndexResolver;
        private QSCarrierGroup mView;

        public Builder(ActivityStarter activityStarter, Handler handler, Looper looper, NetworkController networkController, CarrierTextManager.Builder builder, Context context, CarrierConfigTracker carrierConfigTracker, FeatureFlags featureFlags, SlotIndexResolver slotIndexResolver) {
            this.mActivityStarter = activityStarter;
            this.mHandler = handler;
            this.mLooper = looper;
            this.mNetworkController = networkController;
            this.mCarrierTextControllerBuilder = builder;
            this.mContext = context;
            this.mCarrierConfigTracker = carrierConfigTracker;
            this.mFeatureFlags = featureFlags;
            this.mSlotIndexResolver = slotIndexResolver;
        }

        public Builder setQSCarrierGroup(QSCarrierGroup qSCarrierGroup) {
            this.mView = qSCarrierGroup;
            return this;
        }

        public QSCarrierGroupController build() {
            return new QSCarrierGroupController(this.mView, this.mActivityStarter, this.mHandler, this.mLooper, this.mNetworkController, this.mCarrierTextControllerBuilder, this.mContext, this.mCarrierConfigTracker, this.mFeatureFlags, this.mSlotIndexResolver);
        }
    }

    /* renamed from: com.android.systemui.qs.carrier.QSCarrierGroupController$SubscriptionManagerSlotIndexResolver */
    public static class SubscriptionManagerSlotIndexResolver implements SlotIndexResolver {
        public int getSlotIndex(int i) {
            return SubscriptionManager.getSlotIndex(i);
        }
    }
}
