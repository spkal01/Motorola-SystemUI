package com.android.systemui.p006qs;

import android.app.AlertDialog;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyEventLogger;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.SecurityController;

/* renamed from: com.android.systemui.qs.QSSecurityFooter */
class QSSecurityFooter implements View.OnClickListener, DialogInterface.OnClickListener {
    protected static final boolean DEBUG = Log.isLoggable("QSSecurityFooter", 3);
    /* access modifiers changed from: private */
    public final ActivityStarter mActivityStarter;
    private final Callback mCallback = new Callback();
    private final Context mContext;
    /* access modifiers changed from: private */
    public AlertDialog mDialog;
    /* access modifiers changed from: private */
    public int mFooterIconId;
    /* access modifiers changed from: private */
    public final TextView mFooterText;
    /* access modifiers changed from: private */
    public CharSequence mFooterTextContent = null;
    protected C1201H mHandler;
    /* access modifiers changed from: private */
    public QSTileHost mHost;
    /* access modifiers changed from: private */
    public boolean mIsVisible;
    private final Handler mMainHandler;
    /* access modifiers changed from: private */
    public final ImageView mPrimaryFooterIcon;
    /* access modifiers changed from: private */
    public Drawable mPrimaryFooterIconDrawable;
    /* access modifiers changed from: private */
    public final View mRootView;
    private final SecurityController mSecurityController;
    private final Runnable mUpdateDisplayState = new Runnable() {
        public void run() {
            if (QSSecurityFooter.this.mFooterTextContent != null) {
                QSSecurityFooter.this.mFooterText.setText(QSSecurityFooter.this.mFooterTextContent);
            }
            QSSecurityFooter.this.mRootView.setVisibility(!QSSecurityFooter.this.mIsVisible ? 8 : 0);
        }
    };
    private final Runnable mUpdatePrimaryIcon = new Runnable() {
        public void run() {
            if (QSSecurityFooter.this.mPrimaryFooterIconDrawable != null) {
                QSSecurityFooter.this.mPrimaryFooterIcon.setImageDrawable(QSSecurityFooter.this.mPrimaryFooterIconDrawable);
            } else {
                QSSecurityFooter.this.mPrimaryFooterIcon.setImageResource(QSSecurityFooter.this.mFooterIconId);
            }
        }
    };
    private final UserTracker mUserTracker;

    QSSecurityFooter(View view, UserTracker userTracker, Handler handler, ActivityStarter activityStarter, SecurityController securityController, Looper looper) {
        this.mRootView = view;
        view.setOnClickListener(this);
        this.mFooterText = (TextView) view.findViewById(R$id.footer_text);
        this.mPrimaryFooterIcon = (ImageView) view.findViewById(R$id.primary_footer_icon);
        this.mFooterIconId = R$drawable.ic_info_outline;
        this.mContext = view.getContext();
        this.mMainHandler = handler;
        this.mActivityStarter = activityStarter;
        this.mSecurityController = securityController;
        this.mHandler = new C1201H(looper);
        this.mUserTracker = userTracker;
    }

    public void setHostEnvironment(QSTileHost qSTileHost) {
        this.mHost = qSTileHost;
    }

    public void setListening(boolean z) {
        if (z) {
            this.mSecurityController.addCallback(this.mCallback);
            refreshState();
            return;
        }
        this.mSecurityController.removeCallback(this.mCallback);
    }

    public void onConfigurationChanged() {
        FontSizeUtils.updateFontSize(this.mFooterText, R$dimen.qs_tile_text_size);
        Resources resources = this.mContext.getResources();
        this.mFooterText.setMaxLines(resources.getInteger(R$integer.qs_security_footer_maxLines));
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.qs_footer_padding);
        this.mRootView.setPaddingRelative(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.qs_footers_margin_bottom);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mRootView.getLayoutParams();
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            marginLayoutParams.bottomMargin = 0;
        } else {
            marginLayoutParams.bottomMargin = dimensionPixelSize2;
        }
        marginLayoutParams.width = resources.getConfiguration().orientation == 1 ? -1 : -2;
        this.mRootView.setLayoutParams(marginLayoutParams);
        if (MotoFeature.getInstance(this.mContext).isCustomPanelView()) {
            this.mRootView.setBackground((Drawable) null);
            int color = resources.getColor(R$color.prc_qs_custom_toolbar_text_color);
            this.mFooterText.setTextColor(color);
            this.mPrimaryFooterIcon.setColorFilter(color);
            ((ImageView) this.mRootView.findViewById(R$id.footer_icon)).setColorFilter(color);
            return;
        }
        this.mRootView.setBackground(this.mContext.getDrawable(R$drawable.qs_security_footer_background));
    }

    public View getView() {
        return this.mRootView;
    }

    public boolean hasFooter() {
        return this.mRootView.getVisibility() != 8;
    }

    public void onClick(View view) {
        if (hasFooter()) {
            this.mHandler.sendEmptyMessage(0);
        }
    }

    /* access modifiers changed from: private */
    public void handleClick() {
        showDeviceMonitoringDialog();
        DevicePolicyEventLogger.createEvent(57).write();
    }

    public void showDeviceMonitoringDialog() {
        createDialog();
    }

    public void refreshState() {
        this.mHandler.sendEmptyMessage(1);
    }

    /* access modifiers changed from: private */
    public void handleRefreshState() {
        boolean isDeviceManaged = this.mSecurityController.isDeviceManaged();
        UserInfo userInfo = this.mUserTracker.getUserInfo();
        boolean z = UserManager.isDeviceInDemoMode(this.mContext) && userInfo != null && userInfo.isDemo();
        boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        boolean hasCACertInCurrentUser = this.mSecurityController.hasCACertInCurrentUser();
        boolean hasCACertInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        boolean isNetworkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        String primaryVpnName = this.mSecurityController.getPrimaryVpnName();
        String workProfileVpnName = this.mSecurityController.getWorkProfileVpnName();
        CharSequence deviceOwnerOrganizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        CharSequence workProfileOrganizationName = this.mSecurityController.getWorkProfileOrganizationName();
        boolean isProfileOwnerOfOrganizationOwnedDevice = this.mSecurityController.isProfileOwnerOfOrganizationOwnedDevice();
        boolean isParentalControlsEnabled = this.mSecurityController.isParentalControlsEnabled();
        boolean isWorkProfileOn = this.mSecurityController.isWorkProfileOn();
        boolean z2 = hasCACertInWorkProfile || workProfileVpnName != null || (hasWorkProfile && isNetworkLoggingEnabled);
        boolean z3 = (isDeviceManaged && !z) || hasCACertInCurrentUser || primaryVpnName != null || isProfileOwnerOfOrganizationOwnedDevice || isParentalControlsEnabled || (z2 && isWorkProfileOn);
        this.mIsVisible = z3;
        if (!z3 || !isProfileOwnerOfOrganizationOwnedDevice || (z2 && isWorkProfileOn)) {
            this.mRootView.setClickable(true);
            this.mRootView.findViewById(R$id.footer_icon).setVisibility(0);
        } else {
            this.mRootView.setClickable(false);
            this.mRootView.findViewById(R$id.footer_icon).setVisibility(8);
        }
        this.mFooterTextContent = getFooterText(isDeviceManaged, hasWorkProfile, hasCACertInCurrentUser, hasCACertInWorkProfile, isNetworkLoggingEnabled, primaryVpnName, workProfileVpnName, deviceOwnerOrganizationName, workProfileOrganizationName, isProfileOwnerOfOrganizationOwnedDevice, isParentalControlsEnabled, isWorkProfileOn);
        int i = R$drawable.ic_info_outline;
        if (!(primaryVpnName == null && workProfileVpnName == null)) {
            if (this.mSecurityController.isVpnBranded()) {
                i = R$drawable.stat_sys_branded_vpn;
            } else {
                i = R$drawable.stat_sys_vpn_ic;
            }
        }
        if (this.mFooterIconId != i) {
            this.mFooterIconId = i;
        }
        if (!isParentalControlsEnabled) {
            this.mPrimaryFooterIconDrawable = null;
        } else if (this.mPrimaryFooterIconDrawable == null) {
            this.mPrimaryFooterIconDrawable = this.mSecurityController.getIcon(this.mSecurityController.getDeviceAdminInfo());
        }
        this.mMainHandler.post(this.mUpdatePrimaryIcon);
        this.mMainHandler.post(this.mUpdateDisplayState);
    }

    /* access modifiers changed from: protected */
    public CharSequence getFooterText(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, String str, String str2, CharSequence charSequence, CharSequence charSequence2, boolean z6, boolean z7, boolean z8) {
        if (z7) {
            return this.mContext.getString(R$string.quick_settings_disclosure_parental_controls);
        }
        if (!z) {
            if (!z4 || !z8) {
                if (z3) {
                    return this.mContext.getString(R$string.quick_settings_disclosure_monitoring);
                }
                if (str != null && str2 != null) {
                    return this.mContext.getString(R$string.quick_settings_disclosure_vpns);
                }
                if (str2 != null && z8) {
                    return this.mContext.getString(R$string.quick_settings_disclosure_managed_profile_named_vpn, new Object[]{str2});
                } else if (str != null) {
                    if (z2) {
                        return this.mContext.getString(R$string.quick_settings_disclosure_personal_profile_named_vpn, new Object[]{str});
                    }
                    return this.mContext.getString(R$string.quick_settings_disclosure_named_vpn, new Object[]{str});
                } else if (z2 && z5 && z8) {
                    return this.mContext.getString(R$string.quick_settings_disclosure_managed_profile_network_activity);
                } else {
                    if (!z6) {
                        return null;
                    }
                    if (charSequence2 == null) {
                        return this.mContext.getString(R$string.quick_settings_disclosure_management);
                    }
                    return this.mContext.getString(R$string.quick_settings_disclosure_named_management, new Object[]{charSequence2});
                }
            } else if (charSequence2 == null) {
                return this.mContext.getString(R$string.quick_settings_disclosure_managed_profile_monitoring);
            } else {
                return this.mContext.getString(R$string.quick_settings_disclosure_named_managed_profile_monitoring, new Object[]{charSequence2});
            }
        } else if (z3 || z4 || z5) {
            if (charSequence == null) {
                return this.mContext.getString(R$string.quick_settings_disclosure_management_monitoring);
            }
            return this.mContext.getString(R$string.quick_settings_disclosure_named_management_monitoring, new Object[]{charSequence});
        } else if (str == null || str2 == null) {
            if (str == null && str2 == null) {
                if (charSequence == null) {
                    return this.mContext.getString(R$string.quick_settings_disclosure_management);
                }
                if (isFinancedDevice()) {
                    return this.mContext.getString(R$string.quick_settings_financed_disclosure_named_management, new Object[]{charSequence});
                }
                return this.mContext.getString(R$string.quick_settings_disclosure_named_management, new Object[]{charSequence});
            } else if (charSequence == null) {
                Context context = this.mContext;
                int i = R$string.quick_settings_disclosure_management_named_vpn;
                Object[] objArr = new Object[1];
                if (str == null) {
                    str = str2;
                }
                objArr[0] = str;
                return context.getString(i, objArr);
            } else {
                Context context2 = this.mContext;
                int i2 = R$string.quick_settings_disclosure_named_management_named_vpn;
                Object[] objArr2 = new Object[2];
                objArr2[0] = charSequence;
                if (str == null) {
                    str = str2;
                }
                objArr2[1] = str;
                return context2.getString(i2, objArr2);
            }
        } else if (charSequence == null) {
            return this.mContext.getString(R$string.quick_settings_disclosure_management_vpns);
        } else {
            return this.mContext.getString(R$string.quick_settings_disclosure_named_management_vpns, new Object[]{charSequence});
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            Intent intent = new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS");
            this.mDialog.dismiss();
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }
    }

    private void createDialog() {
        SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext, 0);
        this.mDialog = systemUIDialog;
        systemUIDialog.requestWindowFeature(1);
        this.mDialog.setButton(-1, getPositiveButton(), this);
        this.mDialog.setButton(-2, getNegativeButton(), this);
        this.mDialog.setView(createDialogView());
        this.mDialog.show();
        this.mDialog.getWindow().setLayout(-1, -2);
    }

    /* access modifiers changed from: package-private */
    public View createDialogView() {
        if (this.mSecurityController.isParentalControlsEnabled()) {
            return createParentalControlsDialogView();
        }
        return createOrganizationDialogView();
    }

    private View createOrganizationDialogView() {
        boolean isDeviceManaged = this.mSecurityController.isDeviceManaged();
        boolean hasWorkProfile = this.mSecurityController.hasWorkProfile();
        CharSequence deviceOwnerOrganizationName = this.mSecurityController.getDeviceOwnerOrganizationName();
        boolean hasCACertInCurrentUser = this.mSecurityController.hasCACertInCurrentUser();
        boolean hasCACertInWorkProfile = this.mSecurityController.hasCACertInWorkProfile();
        boolean isNetworkLoggingEnabled = this.mSecurityController.isNetworkLoggingEnabled();
        String primaryVpnName = this.mSecurityController.getPrimaryVpnName();
        String workProfileVpnName = this.mSecurityController.getWorkProfileVpnName();
        boolean z = false;
        View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.quick_settings_footer_dialog, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R$id.device_management_subtitle)).setText(getManagementTitle(deviceOwnerOrganizationName));
        CharSequence managementMessage = getManagementMessage(isDeviceManaged, deviceOwnerOrganizationName);
        if (managementMessage == null) {
            inflate.findViewById(R$id.device_management_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(R$id.device_management_disclosures).setVisibility(0);
            ((TextView) inflate.findViewById(R$id.device_management_warning)).setText(managementMessage);
            this.mDialog.setButton(-2, getSettingsButton(), this);
        }
        CharSequence caCertsMessage = getCaCertsMessage(isDeviceManaged, hasCACertInCurrentUser, hasCACertInWorkProfile);
        if (caCertsMessage == null) {
            inflate.findViewById(R$id.ca_certs_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(R$id.ca_certs_disclosures).setVisibility(0);
            TextView textView = (TextView) inflate.findViewById(R$id.ca_certs_warning);
            textView.setText(caCertsMessage);
            textView.setMovementMethod(new LinkMovementMethod());
        }
        CharSequence networkLoggingMessage = getNetworkLoggingMessage(isDeviceManaged, isNetworkLoggingEnabled);
        if (networkLoggingMessage == null) {
            inflate.findViewById(R$id.network_logging_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(R$id.network_logging_disclosures).setVisibility(0);
            ((TextView) inflate.findViewById(R$id.network_logging_warning)).setText(networkLoggingMessage);
        }
        CharSequence vpnMessage = getVpnMessage(isDeviceManaged, hasWorkProfile, primaryVpnName, workProfileVpnName);
        if (vpnMessage == null) {
            inflate.findViewById(R$id.vpn_disclosures).setVisibility(8);
        } else {
            inflate.findViewById(R$id.vpn_disclosures).setVisibility(0);
            TextView textView2 = (TextView) inflate.findViewById(R$id.vpn_warning);
            textView2.setText(vpnMessage);
            textView2.setMovementMethod(new LinkMovementMethod());
        }
        boolean z2 = managementMessage != null;
        boolean z3 = caCertsMessage != null;
        boolean z4 = networkLoggingMessage != null;
        if (vpnMessage != null) {
            z = true;
        }
        configSubtitleVisibility(z2, z3, z4, z, inflate);
        return inflate;
    }

    private View createParentalControlsDialogView() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R$layout.quick_settings_footer_dialog_parental_controls, (ViewGroup) null, false);
        DeviceAdminInfo deviceAdminInfo = this.mSecurityController.getDeviceAdminInfo();
        Drawable icon = this.mSecurityController.getIcon(deviceAdminInfo);
        if (icon != null) {
            ((ImageView) inflate.findViewById(R$id.parental_controls_icon)).setImageDrawable(icon);
        }
        ((TextView) inflate.findViewById(R$id.parental_controls_title)).setText(this.mSecurityController.getLabel(deviceAdminInfo));
        return inflate;
    }

    /* access modifiers changed from: protected */
    public void configSubtitleVisibility(boolean z, boolean z2, boolean z3, boolean z4, View view) {
        if (!z) {
            int i = z3 ? (z2 ? 1 : 0) + true : z2;
            if (z4) {
                i++;
            }
            if (i == 1) {
                if (z2) {
                    view.findViewById(R$id.ca_certs_subtitle).setVisibility(8);
                }
                if (z3) {
                    view.findViewById(R$id.network_logging_subtitle).setVisibility(8);
                }
                if (z4) {
                    view.findViewById(R$id.vpn_subtitle).setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public String getSettingsButton() {
        return this.mContext.getString(R$string.monitoring_button_view_policies);
    }

    private String getPositiveButton() {
        return this.mContext.getString(R$string.f72ok);
    }

    private String getNegativeButton() {
        if (this.mSecurityController.isParentalControlsEnabled()) {
            return this.mContext.getString(R$string.monitoring_button_view_controls);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public CharSequence getManagementMessage(boolean z, CharSequence charSequence) {
        if (!z) {
            return null;
        }
        if (charSequence == null) {
            return this.mContext.getString(R$string.monitoring_description_management);
        }
        if (isFinancedDevice()) {
            return this.mContext.getString(R$string.monitoring_financed_description_named_management, new Object[]{charSequence, charSequence});
        }
        return this.mContext.getString(R$string.monitoring_description_named_management, new Object[]{charSequence});
    }

    /* access modifiers changed from: protected */
    public CharSequence getCaCertsMessage(boolean z, boolean z2, boolean z3) {
        if (!z2 && !z3) {
            return null;
        }
        if (z) {
            return this.mContext.getString(R$string.monitoring_description_management_ca_certificate);
        }
        if (z3) {
            return this.mContext.getString(R$string.monitoring_description_managed_profile_ca_certificate);
        }
        return this.mContext.getString(R$string.monitoring_description_ca_certificate);
    }

    /* access modifiers changed from: protected */
    public CharSequence getNetworkLoggingMessage(boolean z, boolean z2) {
        if (!z2) {
            return null;
        }
        if (z) {
            return this.mContext.getString(R$string.monitoring_description_management_network_logging);
        }
        return this.mContext.getString(R$string.monitoring_description_managed_profile_network_logging);
    }

    /* access modifiers changed from: protected */
    public CharSequence getVpnMessage(boolean z, boolean z2, String str, String str2) {
        if (str == null && str2 == null) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (z) {
            if (str == null || str2 == null) {
                Context context = this.mContext;
                int i = R$string.monitoring_description_named_vpn;
                Object[] objArr = new Object[1];
                if (str == null) {
                    str = str2;
                }
                objArr[0] = str;
                spannableStringBuilder.append(context.getString(i, objArr));
            } else {
                spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_two_named_vpns, new Object[]{str, str2}));
            }
        } else if (str != null && str2 != null) {
            spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_two_named_vpns, new Object[]{str, str2}));
        } else if (str2 != null) {
            spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_managed_profile_named_vpn, new Object[]{str2}));
        } else if (z2) {
            spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_personal_profile_named_vpn, new Object[]{str}));
        } else {
            spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_named_vpn, new Object[]{str}));
        }
        spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_vpn_settings_separator));
        spannableStringBuilder.append(this.mContext.getString(R$string.monitoring_description_vpn_settings), new VpnSpan(), 0);
        return spannableStringBuilder;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getManagementTitle(CharSequence charSequence) {
        if (charSequence == null || !isFinancedDevice()) {
            return this.mContext.getString(R$string.monitoring_title_device_owned);
        }
        return this.mContext.getString(R$string.monitoring_title_financed_device, new Object[]{charSequence});
    }

    private boolean isFinancedDevice() {
        if (this.mSecurityController.isDeviceManaged()) {
            SecurityController securityController = this.mSecurityController;
            if (securityController.getDeviceOwnerType(securityController.getDeviceOwnerComponentOnAnyUser()) == 1) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: com.android.systemui.qs.QSSecurityFooter$Callback */
    private class Callback implements SecurityController.SecurityControllerCallback {
        private Callback() {
        }

        public void onStateChanged() {
            QSSecurityFooter.this.refreshState();
        }
    }

    /* renamed from: com.android.systemui.qs.QSSecurityFooter$H */
    private class C1201H extends Handler {
        private C1201H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            try {
                int i = message.what;
                if (i == 1) {
                    QSSecurityFooter.this.handleRefreshState();
                } else if (i == 0) {
                    QSSecurityFooter.this.handleClick();
                }
            } catch (Throwable th) {
                String str = "Error in " + null;
                Log.w("QSSecurityFooter", str, th);
                QSSecurityFooter.this.mHost.warn(str, th);
            }
        }
    }

    /* renamed from: com.android.systemui.qs.QSSecurityFooter$VpnSpan */
    protected class VpnSpan extends ClickableSpan {
        public int hashCode() {
            return 314159257;
        }

        protected VpnSpan() {
        }

        public void onClick(View view) {
            Intent intent = new Intent("android.settings.VPN_SETTINGS");
            QSSecurityFooter.this.mDialog.dismiss();
            QSSecurityFooter.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        }

        public boolean equals(Object obj) {
            return obj instanceof VpnSpan;
        }
    }
}
