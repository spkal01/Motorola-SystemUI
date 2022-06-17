package com.android.systemui.moto;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.settingslib.SignalIcon$MobileState;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$bool;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.moto.NetworkStateTracker;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.MobileSignalController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.systemui.statusbar.onsview.MarqueeTextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class CarrierLabelUpdateMonitor implements MarqueeTextView.OnMarqueeListener {
    static final boolean DEBUG = (Build.IS_DEBUGGABLE || Log.isLoggable("CarrierLabel", 3));
    private static CarrierLabelUpdateMonitor sInstance;
    /* access modifiers changed from: private */
    public StatusBar mBar;
    /* access modifiers changed from: private */
    public TextView mCarrierLabel;
    /* access modifiers changed from: private */
    public View mCarrierLabelGroup;
    private boolean mCarrierLabelVisible = false;
    /* access modifiers changed from: private */
    public TextView mCellBroadcastMessage;
    /* access modifiers changed from: private */
    public boolean mDeviceProvisioned = false;
    /* access modifiers changed from: private */
    public DeviceProvisionedController mDeviceProvisionedController;
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedListener = new DeviceProvisionedController.DeviceProvisionedListener() {
        public void onDeviceProvisionedChanged() {
            CarrierLabelUpdateMonitor carrierLabelUpdateMonitor = CarrierLabelUpdateMonitor.this;
            boolean unused = carrierLabelUpdateMonitor.mDeviceProvisioned = carrierLabelUpdateMonitor.mDeviceProvisionedController.isDeviceProvisioned();
            CarrierLabelUpdateMonitor.this.mShortFormLabelListener.updateLabel();
        }
    };
    private CarrierLabelHandler mHandler = new CarrierLabelHandler();
    /* access modifiers changed from: private */
    public int mLastOrientation;
    /* access modifiers changed from: private */
    public NetworkController mNc;
    private NotificationShadeWindowView mNotificationShadeWindow;
    private NetworkStateTracker.PanelCarrierLabelListener mPanelCarrierLabelListener = new NetworkStateTracker.PanelCarrierLabelListener() {
        public void updateLabel() {
            if (CarrierLabelUpdateMonitor.this.mCarrierLabel != null) {
                boolean hasMobileDataFeature = CarrierLabelUpdateMonitor.this.mNc.hasMobileDataFeature();
                String networkSeparator = CarrierLabelUpdateMonitor.this.mNc.getNetworkSeparator();
                Map access$1100 = CarrierLabelUpdateMonitor.sortByDirection(CarrierLabelUpdateMonitor.this.mNc.getMobileSignalControllers(), CarrierLabelUpdateMonitor.this.mNc.isRtl());
                String str = "";
                if (access$1100 == null || access$1100.size() == 0) {
                    CarrierLabelUpdateMonitor.this.mCarrierLabel.setText(str);
                    if (CarrierLabelUpdateMonitor.this.mCellBroadcastMessage != null) {
                        CarrierLabelUpdateMonitor.this.mCellBroadcastMessage.setText(str);
                        return;
                    }
                    return;
                }
                ArrayList arrayList = new ArrayList(access$1100.keySet());
                int i = 0;
                String str2 = str;
                boolean z = false;
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    MobileSignalController mobileSignalController = (MobileSignalController) access$1100.get(arrayList.get(i2));
                    if (mobileSignalController != null) {
                        SignalIcon$MobileState mobileState = mobileSignalController.getMobileState();
                        str = mobileSignalController.getLabel(str, hasMobileDataFeature);
                        str2 = CarrierLabelUpdateMonitor.this.getCellBroadcastMessage(str2, hasMobileDataFeature, mobileState.cellBroadcastMessage, networkSeparator);
                        z = CarrierLabelUpdateMonitor.this.getCellBroadcastEnabled(z, hasMobileDataFeature, mobileState.isCellBroadcastEnabled);
                    }
                }
                CarrierLabelUpdateMonitor.this.mCarrierLabel.setText(str);
                if (CarrierLabelUpdateMonitor.this.mCellBroadcastMessage != null) {
                    CarrierLabelUpdateMonitor.this.mCellBroadcastMessage.setText(str2);
                }
                if (!CarrierLabelUpdateMonitor.this.mNc.hasMobileDataFeature()) {
                    return;
                }
                if (!TextUtils.isEmpty(str)) {
                    boolean access$200 = CarrierLabelUpdateMonitor.this.carrierLabelAllowedToShow();
                    if (CarrierLabelUpdateMonitor.this.mCarrierLabel != null) {
                        CarrierLabelUpdateMonitor.this.mCarrierLabel.setVisibility(access$200 ? 0 : 4);
                    }
                    if (!z || TextUtils.isEmpty(str2)) {
                        if (CarrierLabelUpdateMonitor.this.mCellBroadcastMessage != null) {
                            CarrierLabelUpdateMonitor.this.mCellBroadcastMessage.setVisibility(8);
                        }
                    } else if (CarrierLabelUpdateMonitor.this.mCellBroadcastMessage != null) {
                        TextView access$400 = CarrierLabelUpdateMonitor.this.mCellBroadcastMessage;
                        if (!CarrierLabelUpdateMonitor.this.carrierLabelAllowedToShow()) {
                            i = 4;
                        }
                        access$400.setVisibility(i);
                    }
                } else if (CarrierLabelUpdateMonitor.this.mCarrierLabel != null) {
                    CarrierLabelUpdateMonitor.this.mCarrierLabel.setVisibility(4);
                }
            }
        }
    };
    private boolean mPanelExpanded = false;
    private NotificationPanelViewController mPanelViewController;
    /* access modifiers changed from: private */
    public Runnable mRecoverRunnable;
    /* access modifiers changed from: private */
    public NetworkStateTracker.ShortFormLabelListener mShortFormLabelListener = new NetworkStateTracker.ShortFormLabelListener() {
        public void updateLabel() {
            if (CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText != null && CarrierLabelUpdateMonitor.this.shouldShowShortFormLabel()) {
                String access$800 = CarrierLabelUpdateMonitor.this.getShortFormLabel();
                if (!CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText.getText().equals(access$800)) {
                    if (CarrierLabelUpdateMonitor.DEBUG) {
                        Log.i("CarrierLabel", "updateLabel label = " + access$800);
                    }
                    CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText.setText(access$800);
                    if (!TextUtils.isEmpty(access$800)) {
                        CarrierLabelUpdateMonitor.this.updateShortFormLabel(true);
                    } else if (CarrierLabelUpdateMonitor.this.isOnsShown()) {
                        CarrierLabelUpdateMonitor.this.setOnsShown(false);
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mShortNameAllowedByUser = false;
    private ShortNameSettingChangedObserver mShortNameSettingChangedObserver;
    private boolean mShown;
    /* access modifiers changed from: private */
    public MarqueeTextView mStatusBarLabelShortFormViewText;
    private View mStatusBarLeftSide;
    private StatusBarWindowView mStatusBarWindow;
    private int mTextColor = -1;
    private StatusBarKeyguardViewManager mViewManager;

    public boolean getCellBroadcastEnabled(boolean z, boolean z2, boolean z3) {
        return !z2 ? z : z || z3;
    }

    private CarrierLabelUpdateMonitor() {
    }

    public static CarrierLabelUpdateMonitor getInstance() {
        if (sInstance == null) {
            sInstance = new CarrierLabelUpdateMonitor();
        }
        return sInstance;
    }

    public void setStatusBar(StatusBar statusBar, StatusBarWindowView statusBarWindowView, NotificationShadeWindowView notificationShadeWindowView, StatusBarKeyguardViewManager statusBarKeyguardViewManager, NotificationPanelViewController notificationPanelViewController, NetworkController networkController) {
        this.mBar = statusBar;
        this.mStatusBarWindow = statusBarWindowView;
        this.mNotificationShadeWindow = notificationShadeWindowView;
        this.mPanelViewController = notificationPanelViewController;
        this.mNc = networkController;
        this.mViewManager = statusBarKeyguardViewManager;
        initialize();
    }

    private void initialize() {
        this.mCarrierLabel = (TextView) this.mNotificationShadeWindow.findViewById(R$id.carrier_label);
        this.mCarrierLabelGroup = this.mNotificationShadeWindow.findViewById(R$id.carrier_label_groups);
        this.mStatusBarLabelShortFormViewText = (MarqueeTextView) this.mStatusBarWindow.findViewById(R$id.onsText_att);
        this.mStatusBarLeftSide = this.mStatusBarWindow.findViewById(R$id.status_bar_left_side);
        if (this.mStatusBarLabelShortFormViewText != null) {
            this.mRecoverRunnable = new Runnable() {
                public void run() {
                    CarrierLabelUpdateMonitor.this.setOnsShown(false);
                }
            };
        }
        TextView textView = (TextView) this.mNotificationShadeWindow.findViewById(R$id.cell_broadcast_message);
        this.mCellBroadcastMessage = textView;
        if (textView != null) {
            textView.setVisibility(8);
            int i = this.mTextColor;
            if (i != -1) {
                this.mCellBroadcastMessage.setTextColor(i);
            }
        }
        TextView textView2 = this.mCarrierLabel;
        if (textView2 != null) {
            textView2.setVisibility((!this.mCarrierLabelVisible || !carrierLabelAllowedToShow()) ? 4 : 0);
            int i2 = this.mTextColor;
            if (i2 != -1) {
                this.mCarrierLabel.setTextColor(i2);
            }
            this.mNc.addCarrierLabel(this.mPanelCarrierLabelListener);
        }
        this.mNc.addLabelShortFormView(this.mShortFormLabelListener);
        MarqueeTextView marqueeTextView = this.mStatusBarLabelShortFormViewText;
        if (marqueeTextView != null) {
            marqueeTextView.setVisibility(8);
        }
        ShortNameSettingChangedObserver shortNameSettingChangedObserver = new ShortNameSettingChangedObserver(this.mHandler, this.mBar.mContext);
        this.mShortNameSettingChangedObserver = shortNameSettingChangedObserver;
        shortNameSettingChangedObserver.register();
        this.mShortNameSettingChangedObserver.onChange(true);
        this.mPanelViewController.setPanelExpansionListener(new NetworkStateTracker.PanelViewExpansionListener() {
            public void updateExpansion(float f) {
                if (CarrierLabelUpdateMonitor.this.mCarrierLabelGroup != null) {
                    int[] iArr = new int[2];
                    CarrierLabelUpdateMonitor.this.mCarrierLabelGroup.getLocationInWindow(iArr);
                    boolean z = true;
                    int height = (iArr[1] + (CarrierLabelUpdateMonitor.this.mCarrierLabelGroup.getHeight() / 2)) - ((int) f);
                    int i = 0;
                    if (height <= 0 || !CarrierLabelUpdateMonitor.this.carrierLabelAllowedToShow()) {
                        z = false;
                    }
                    float f2 = 0.0f;
                    float height2 = CarrierLabelUpdateMonitor.this.mCarrierLabelGroup.getHeight() != 0 ? ((float) height) / ((float) CarrierLabelUpdateMonitor.this.mCarrierLabelGroup.getHeight()) : 0.0f;
                    if (height2 >= 0.0f) {
                        f2 = height2 > 1.0f ? 1.0f : height2;
                    }
                    if (CarrierLabelUpdateMonitor.this.mCarrierLabel != null) {
                        CarrierLabelUpdateMonitor.this.mCarrierLabel.setAlpha(f2);
                        CarrierLabelUpdateMonitor.this.mCarrierLabel.setVisibility(z ? 0 : 4);
                    }
                    if (CarrierLabelUpdateMonitor.this.mCellBroadcastMessage != null && CarrierLabelUpdateMonitor.this.mCellBroadcastMessage.getVisibility() != 8) {
                        CarrierLabelUpdateMonitor.this.mCellBroadcastMessage.setAlpha(f2);
                        TextView access$400 = CarrierLabelUpdateMonitor.this.mCellBroadcastMessage;
                        if (!z) {
                            i = 4;
                        }
                        access$400.setVisibility(i);
                    }
                }
            }
        });
        DeviceProvisionedController deviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
        this.mDeviceProvisionedController = deviceProvisionedController;
        deviceProvisionedController.addCallback(this.mDeviceProvisionedListener);
        this.mDeviceProvisioned = this.mDeviceProvisionedController.isDeviceProvisioned();
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).addDarkReceiver((DarkIconDispatcher.DarkReceiver) new DarkIconDispatcher.DarkReceiver() {
            public void onDarkChanged(Rect rect, float f, int i) {
                if (CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText != null) {
                    CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText.setTextColor(DarkIconDispatcher.getTint(rect, CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText, i));
                }
            }
        });
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(new ConfigurationController.ConfigurationListener() {
            public void onDensityOrFontScaleChanged() {
                CarrierLabelUpdateMonitor.this.updateCarrierGroupLayout();
                if (CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText != null) {
                    FontSizeUtils.updateFontSize(CarrierLabelUpdateMonitor.this.mStatusBarLabelShortFormViewText, R$dimen.status_bar_clock_size);
                }
                if (CarrierLabelUpdateMonitor.this.mCarrierLabel != null) {
                    FontSizeUtils.updateFontSize(CarrierLabelUpdateMonitor.this.mCarrierLabel, R$dimen.qs_time_expanded_size);
                }
                if (CarrierLabelUpdateMonitor.this.mCellBroadcastMessage != null) {
                    FontSizeUtils.updateFontSize(CarrierLabelUpdateMonitor.this.mCellBroadcastMessage, R$dimen.qs_time_expanded_size);
                }
            }

            public void onConfigChanged(Configuration configuration) {
                int access$700 = CarrierLabelUpdateMonitor.this.mLastOrientation;
                int i = configuration.orientation;
                if (access$700 != i) {
                    int unused = CarrierLabelUpdateMonitor.this.mLastOrientation = i;
                    CarrierLabelUpdateMonitor.this.updateCarrierGroupLayout();
                }
            }
        });
        ((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(new CarrierLabelUpdateMonitor$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initialize$0(int i) {
        updateCarrierGroupLayout();
    }

    /* access modifiers changed from: private */
    public void updateCarrierGroupLayout() {
        View view = this.mCarrierLabelGroup;
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            int dimensionPixelSize = ((NavigationModeController) Dependency.get(NavigationModeController.class)).getCurrentUserContext().getResources().getDimensionPixelSize(R$dimen.zz_moto_carrier_label_bottom_margin);
            if (this.mLastOrientation != 2) {
                dimensionPixelSize += MotoFeature.getBottomNotchHeight(this.mBar.mContext);
            }
            if (layoutParams.bottomMargin != dimensionPixelSize) {
                layoutParams.bottomMargin = dimensionPixelSize;
                this.mCarrierLabelGroup.setLayoutParams(layoutParams);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean carrierLabelAllowedToShow() {
        if (MotoFeature.getInstance(this.mBar.mContext).isCustomPanelView() && this.mPanelViewController.isOpenQSState()) {
            return false;
        }
        int barState = this.mBar.getBarState();
        boolean z = this.mBar.mContext.getResources().getBoolean(R$bool.zz_moto_hide_bottom_carrier_label);
        if (barState == 1 || barState == 2 || !this.mPanelExpanded || z) {
            return false;
        }
        return true;
    }

    public void panelExpansionChanged(boolean z) {
        if (this.mPanelExpanded != z) {
            this.mPanelExpanded = z;
            updateCarrierLabelVisibility(true);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r0 = r3.mViewManager;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateCarrierLabelVisibility(boolean r4) {
        /*
            r3 = this;
            android.widget.TextView r0 = r3.mCarrierLabel
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            com.android.systemui.statusbar.policy.NetworkController r0 = r3.mNc
            boolean r0 = r0.isEmergencyOnly()
            r1 = 0
            if (r0 != 0) goto L_0x0020
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager r0 = r3.mViewManager
            if (r0 == 0) goto L_0x0018
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x001e
        L_0x0018:
            boolean r0 = r3.carrierLabelAllowedToShow()
            if (r0 == 0) goto L_0x0020
        L_0x001e:
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = r1
        L_0x0021:
            if (r4 != 0) goto L_0x0027
            boolean r4 = r3.mCarrierLabelVisible
            if (r4 == r0) goto L_0x0059
        L_0x0027:
            r3.mCarrierLabelVisible = r0
            r4 = 4
            if (r0 == 0) goto L_0x0054
            android.widget.TextView r0 = r3.mCarrierLabel
            boolean r2 = r3.carrierLabelAllowedToShow()
            if (r2 == 0) goto L_0x0036
            r2 = r1
            goto L_0x0037
        L_0x0036:
            r2 = r4
        L_0x0037:
            r0.setVisibility(r2)
            android.widget.TextView r0 = r3.mCellBroadcastMessage
            if (r0 == 0) goto L_0x0059
            int r0 = r0.getVisibility()
            r2 = 8
            if (r0 == r2) goto L_0x0059
            android.widget.TextView r0 = r3.mCellBroadcastMessage
            boolean r3 = r3.carrierLabelAllowedToShow()
            if (r3 == 0) goto L_0x004f
            goto L_0x0050
        L_0x004f:
            r1 = r4
        L_0x0050:
            r0.setVisibility(r1)
            goto L_0x0059
        L_0x0054:
            android.widget.TextView r3 = r3.mCarrierLabel
            r3.setVisibility(r4)
        L_0x0059:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.moto.CarrierLabelUpdateMonitor.updateCarrierLabelVisibility(boolean):void");
    }

    public void updateBarState(boolean z) {
        updateShortFormLabel(z);
        updateCarrierLabelVisibility(z);
    }

    public static String getShortFormNetworkName(NetworkConfig networkConfig, boolean z, boolean z2, boolean z3, String str, boolean z4, String str2) {
        boolean z5 = true;
        boolean z6 = z4 && !TextUtils.isEmpty(str2);
        if (!z3 || TextUtils.isEmpty(str)) {
            z5 = false;
        }
        String charSequence = Resources.getSystem().getText(17040169).toString();
        if (!networkConfig.networkNameShortFormSupported || !z2) {
            return "";
        }
        boolean equals = charSequence.equals(str2);
        boolean equals2 = networkConfig.networkNameDefault.equals(str2);
        if (!networkConfig.networkNameSpnHasPriority || !z5 || z || equals) {
            if (z6 && !equals2 && !equals) {
                str = str2;
            } else if (!z5) {
                str = "";
            }
        }
        return str;
    }

    /* access modifiers changed from: private */
    public void updateShortFormLabel(boolean z) {
        if (shouldShowShortFormLabel() && this.mStatusBarLabelShortFormViewText != null && z) {
            if (DEBUG) {
                Log.i("CarrierLabel", "updateShortFormLabel bar state = " + this.mBar.getBarState());
            }
            if (this.mBar.getBarState() == 0) {
                setOnsShown(true);
            } else {
                setOnsShown(false);
            }
        }
    }

    public boolean shouldShowShortFormLabel() {
        return this.mShortNameAllowedByUser && this.mNc.isShortFormLabelEnabled() && this.mDeviceProvisioned;
    }

    public void onMarqueeRepeateChanged(int i) {
        if (DEBUG) {
            Log.i("CarrierLabel", "onMarqueeRepeateChanged repeatLimit = " + i);
        }
        this.mHandler.removeCallbacks(this.mRecoverRunnable);
        this.mHandler.obtainMessage(21).sendToTarget();
    }

    public void setOnsShown(boolean z) {
        if (DEBUG) {
            Log.i("CarrierLabel", "setOnsShown isShown = " + z + " mShown = " + this.mShown);
        }
        if (this.mShown != z) {
            this.mHandler.removeCallbacks(this.mRecoverRunnable);
            this.mShown = z;
            if (!z) {
                this.mStatusBarLabelShortFormViewText.setSelected(false);
                CrossFadeHelper.fadeIn(this.mStatusBarLeftSide, 100, 0);
                CrossFadeHelper.fadeOut(this.mStatusBarLabelShortFormViewText, 100, 0, new CarrierLabelUpdateMonitor$$ExternalSyntheticLambda1(this));
            } else if (!TextUtils.isEmpty(this.mStatusBarLabelShortFormViewText.getText())) {
                this.mStatusBarLabelShortFormViewText.setVisibility(0);
                CrossFadeHelper.fadeIn((View) this.mStatusBarLabelShortFormViewText, 100, 0);
                this.mStatusBarLeftSide.setVisibility(4);
                if (((int) this.mStatusBarLabelShortFormViewText.getPaint().measureText(this.mStatusBarLabelShortFormViewText.getText().toString())) > this.mStatusBarLeftSide.getWidth()) {
                    this.mStatusBarLabelShortFormViewText.setSelected(true);
                    this.mStatusBarLabelShortFormViewText.setMarqueeListener(this);
                    return;
                }
                this.mHandler.obtainMessage(20).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setOnsShown$1() {
        this.mStatusBarLabelShortFormViewText.setVisibility(8);
    }

    public boolean isOnsShown() {
        return this.mShown;
    }

    class ShortNameSettingChangedObserver extends ContentObserver {
        Handler mHandler;
        ContentResolver mResolver;

        public ShortNameSettingChangedObserver(Handler handler, Context context) {
            super(handler);
            this.mResolver = context.getContentResolver();
            this.mHandler = handler;
        }

        public void register() {
            this.mResolver.registerContentObserver(MotorolaSettings.Global.getUriFor("display_network_name"), false, this);
        }

        public void onChange(boolean z) {
            this.mHandler.sendEmptyMessage(10);
        }
    }

    /* access modifiers changed from: private */
    public String getShortFormLabel() {
        Map<Integer, MobileSignalController> sortByDirection = sortByDirection(this.mNc.getMobileSignalControllers(), this.mNc.isRtl());
        String str = "";
        if (!(sortByDirection == null || sortByDirection.size() == 0)) {
            String networkSeparator = this.mNc.getNetworkSeparator();
            ArrayList arrayList = new ArrayList(sortByDirection.keySet());
            for (int i = 0; i < arrayList.size(); i++) {
                MobileSignalController mobileSignalController = sortByDirection.get(arrayList.get(i));
                if (mobileSignalController != null) {
                    SignalIcon$MobileState mobileState = mobileSignalController.getMobileState();
                    if (!TextUtils.isEmpty(mobileState.shortFormLabel)) {
                        if (TextUtils.isEmpty(str)) {
                            str = mobileState.shortFormLabel;
                        } else {
                            str = str + networkSeparator + mobileState.shortFormLabel;
                        }
                    }
                }
            }
        }
        return str;
    }

    /* access modifiers changed from: private */
    public static Map<Integer, MobileSignalController> sortByDirection(SparseArray<MobileSignalController> sparseArray, final boolean z) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < sparseArray.size(); i++) {
            linkedHashMap.put(Integer.valueOf(sparseArray.keyAt(i)), sparseArray.valueAt(i));
        }
        LinkedList<Map.Entry> linkedList = new LinkedList<>(linkedHashMap.entrySet());
        Collections.sort(linkedList, new Comparator<Map.Entry<Integer, MobileSignalController>>() {
            public int compare(Map.Entry<Integer, MobileSignalController> entry, Map.Entry<Integer, MobileSignalController> entry2) {
                MobileSignalController value = entry.getValue();
                MobileSignalController value2 = entry2.getValue();
                int i = -1;
                int simSlotIndex = value != null ? value.getSubscriptionInfo().getSimSlotIndex() : -1;
                if (value2 != null) {
                    i = value2.getSubscriptionInfo().getSimSlotIndex();
                }
                return z ? i - simSlotIndex : simSlotIndex - i;
            }
        });
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        for (Map.Entry entry : linkedList) {
            linkedHashMap2.put((Integer) entry.getKey(), (MobileSignalController) entry.getValue());
        }
        return linkedHashMap2;
    }

    public String getCellBroadcastMessage(String str, boolean z, String str2, String str3) {
        if (!z) {
            return str;
        }
        if (!(str.length() == 0 || str2.length() == 0)) {
            str = str + str3;
        }
        return str + str2;
    }

    private class CarrierLabelHandler extends Handler {
        private CarrierLabelHandler() {
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 10) {
                CarrierLabelUpdateMonitor carrierLabelUpdateMonitor = CarrierLabelUpdateMonitor.this;
                boolean z = true;
                if (MotorolaSettings.Global.getInt(carrierLabelUpdateMonitor.mBar.mContext.getContentResolver(), "display_network_name", 1) != 1) {
                    z = false;
                }
                boolean unused = carrierLabelUpdateMonitor.mShortNameAllowedByUser = z;
                CarrierLabelUpdateMonitor.this.mShortFormLabelListener.updateLabel();
            } else if (i == 20) {
                postDelayed(CarrierLabelUpdateMonitor.this.mRecoverRunnable, 3000);
            } else if (i == 21) {
                postDelayed(CarrierLabelUpdateMonitor.this.mRecoverRunnable, 1800);
            }
        }
    }

    public void updateTextColor(int i) {
        this.mTextColor = i;
        TextView textView = this.mCarrierLabel;
        if (textView != null) {
            textView.setTextColor(i);
        }
        TextView textView2 = this.mCellBroadcastMessage;
        if (textView2 != null) {
            textView2.setTextColor(i);
        }
    }
}
