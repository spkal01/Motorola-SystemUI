package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.statusbar.notification.AssistantFeedbackController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.motorola.settingslib.InstalledAppUtils;
import java.util.List;
import java.util.Set;

public class NotificationInfo extends LinearLayout implements NotificationGuts.GutsContent {
    private int mActualHeight;
    private String mAppName;
    private OnAppSettingsClickListener mAppSettingsClickListener;
    private int mAppUid;
    private AssistantFeedbackController mAssistantFeedbackController;
    private TextView mAutomaticDescriptionView;
    private ChannelEditorDialogController mChannelEditorDialogController;
    private Integer mChosenImportance;
    private String mDelegatePkg;
    private NotificationEntry mEntry;
    private NotificationGuts mGutsContainer;
    private INotificationManager mINotificationManager;
    private boolean mIsAutomaticChosen;
    private boolean mIsDeviceProvisioned;
    private boolean mIsNonblockable;
    private boolean mIsSingleDefaultChannel;
    private boolean mIsSystemApp;
    private MetricsLogger mMetricsLogger;
    private int mNumUniqueChannelsInRow;
    private View.OnClickListener mOnAlert = new NotificationInfo$$ExternalSyntheticLambda0(this);
    private View.OnClickListener mOnAutomatic = new NotificationInfo$$ExternalSyntheticLambda3(this);
    private View.OnClickListener mOnDismissSettings = new NotificationInfo$$ExternalSyntheticLambda4(this);
    private OnSettingsClickListener mOnSettingsClickListener;
    private View.OnClickListener mOnSilent = new NotificationInfo$$ExternalSyntheticLambda2(this);
    private OnUserInteractionCallback mOnUserInteractionCallback;
    private String mPackageName;
    private Drawable mPkgIcon;
    private PackageManager mPm;
    private boolean mPresentingChannelEditorDialog = false;
    private boolean mPressedApply;
    private TextView mPriorityDescriptionView;
    private StatusBarNotification mSbn;
    private boolean mShowAutomaticSetting;
    private TextView mSilentDescriptionView;
    private NotificationChannel mSingleNotificationChannel;
    @VisibleForTesting
    boolean mSkipPost = false;
    private int mStartingChannelImportance;
    private RestrictedLockUtils.EnforcedAdmin mSuspendedAppsAdmin;
    private UiEventLogger mUiEventLogger;
    private Set<NotificationChannel> mUniqueChannelsInRow;
    private boolean mWasShownHighPriority;

    public interface CheckSaveListener {
    }

    public interface OnAppSettingsClickListener {
        void onClick(View view, Intent intent);
    }

    public interface OnSettingsClickListener {
        void onClick(View view, NotificationChannel notificationChannel, int i);
    }

    public View getContentView() {
        return this;
    }

    @VisibleForTesting
    public boolean isAnimating() {
        return false;
    }

    public boolean needsFalsingProtection() {
        return true;
    }

    public boolean willBeRemoved() {
        return false;
    }

    private boolean isSystemApp(String str, PackageInfo packageInfo) {
        if (packageInfo == null) {
            return false;
        }
        Context context = this.mContext;
        boolean isSystemPackage = Utils.isSystemPackage(context.getResources(), context.getPackageManager(), packageInfo);
        if (!isSystemPackage) {
            List heldRolesFromController = ((RoleManager) context.getSystemService(RoleManager.class)).getHeldRolesFromController(str);
            if (heldRolesFromController.contains("android.app.role.DIALER") || heldRolesFromController.contains("android.app.role.EMERGENCY")) {
                isSystemPackage = true;
            }
        }
        if (isSystemPackage || !InstalledAppUtils.get(context).isPackageRemoveBlockAll(str)) {
            return isSystemPackage;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        this.mIsAutomaticChosen = true;
        applyAlertingBehavior(2, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        this.mChosenImportance = 3;
        this.mIsAutomaticChosen = false;
        applyAlertingBehavior(0, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(View view) {
        this.mChosenImportance = 2;
        this.mIsAutomaticChosen = false;
        applyAlertingBehavior(1, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        this.mPressedApply = true;
        this.mGutsContainer.closeControls(view, true);
    }

    public NotificationInfo(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPriorityDescriptionView = (TextView) findViewById(R$id.alert_summary);
        this.mSilentDescriptionView = (TextView) findViewById(R$id.silence_summary);
        this.mAutomaticDescriptionView = (TextView) findViewById(R$id.automatic_summary);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00d4, code lost:
        r2 = r0.mSingleNotificationChannel;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bindNotification(android.content.pm.PackageManager r1, android.app.INotificationManager r2, com.android.systemui.statusbar.notification.row.OnUserInteractionCallback r3, com.android.systemui.statusbar.notification.row.ChannelEditorDialogController r4, java.lang.String r5, android.app.NotificationChannel r6, java.util.Set<android.app.NotificationChannel> r7, com.android.systemui.statusbar.notification.collection.NotificationEntry r8, com.android.systemui.statusbar.notification.row.NotificationInfo.OnSettingsClickListener r9, com.android.systemui.statusbar.notification.row.NotificationInfo.OnAppSettingsClickListener r10, com.android.internal.logging.UiEventLogger r11, boolean r12, boolean r13, boolean r14, com.android.systemui.statusbar.notification.AssistantFeedbackController r15) throws android.os.RemoteException {
        /*
            r0 = this;
            r0.mINotificationManager = r2
            java.lang.Class<com.android.internal.logging.MetricsLogger> r2 = com.android.internal.logging.MetricsLogger.class
            java.lang.Object r2 = com.android.systemui.Dependency.get(r2)
            com.android.internal.logging.MetricsLogger r2 = (com.android.internal.logging.MetricsLogger) r2
            r0.mMetricsLogger = r2
            r0.mOnUserInteractionCallback = r3
            r0.mChannelEditorDialogController = r4
            r0.mAssistantFeedbackController = r15
            r0.mPackageName = r5
            r0.mUniqueChannelsInRow = r7
            int r2 = r7.size()
            r0.mNumUniqueChannelsInRow = r2
            r0.mEntry = r8
            android.service.notification.StatusBarNotification r2 = r8.getSbn()
            r0.mSbn = r2
            r0.mPm = r1
            r0.mAppSettingsClickListener = r10
            java.lang.String r1 = r0.mPackageName
            r0.mAppName = r1
            r0.mOnSettingsClickListener = r9
            r0.mSingleNotificationChannel = r6
            if (r6 == 0) goto L_0x0038
            int r1 = r6.getImportance()
            r0.mStartingChannelImportance = r1
        L_0x0038:
            r0.mWasShownHighPriority = r14
            if (r13 != 0) goto L_0x0049
            android.content.Context r1 = r0.getContext()
            com.motorola.settingslib.InstalledAppUtils r1 = com.motorola.settingslib.InstalledAppUtils.get(r1)
            boolean r1 = r1.isPackageRemoveBlockAll(r5)
            r13 = r13 | r1
        L_0x0049:
            r0.mIsNonblockable = r13
            android.service.notification.StatusBarNotification r1 = r0.mSbn
            int r1 = r1.getUid()
            r0.mAppUid = r1
            android.service.notification.StatusBarNotification r1 = r0.mSbn
            java.lang.String r1 = r1.getOpPkg()
            r0.mDelegatePkg = r1
            r0.mIsDeviceProvisioned = r12
            com.android.systemui.statusbar.notification.AssistantFeedbackController r1 = r0.mAssistantFeedbackController
            boolean r1 = r1.isFeedbackEnabled()
            r0.mShowAutomaticSetting = r1
            r0.mUiEventLogger = r11
            android.content.Context r1 = r0.mContext     // Catch:{ NameNotFoundException -> 0x008e }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ NameNotFoundException -> 0x008e }
            java.lang.String r2 = r0.mPackageName     // Catch:{ NameNotFoundException -> 0x008e }
            r3 = 64
            android.content.pm.PackageInfo r1 = r1.getPackageInfo(r2, r3)     // Catch:{ NameNotFoundException -> 0x008e }
            java.lang.String r2 = r0.mPackageName     // Catch:{ NameNotFoundException -> 0x008e }
            boolean r1 = r0.isSystemApp(r2, r1)     // Catch:{ NameNotFoundException -> 0x008e }
            r0.mIsSystemApp = r1     // Catch:{ NameNotFoundException -> 0x008e }
            int r1 = r0.mAppUid     // Catch:{ NameNotFoundException -> 0x008e }
            int r1 = android.os.UserHandle.getUserId(r1)     // Catch:{ NameNotFoundException -> 0x008e }
            android.content.Context r2 = r0.mContext     // Catch:{ NameNotFoundException -> 0x008e }
            java.lang.String r3 = r0.mPackageName     // Catch:{ NameNotFoundException -> 0x008e }
            com.android.settingslib.RestrictedLockUtils$EnforcedAdmin r1 = com.android.settingslib.RestrictedLockUtilsInternal.checkIfApplicationIsSuspended(r2, r3, r1)     // Catch:{ NameNotFoundException -> 0x008e }
            r0.mSuspendedAppsAdmin = r1     // Catch:{ NameNotFoundException -> 0x008e }
            goto L_0x0092
        L_0x008e:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0092:
            boolean r1 = android.os.Build.IS_USER
            if (r1 != 0) goto L_0x00c4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "bindNotification notificationChannel = "
            r1.append(r2)
            java.lang.String r2 = r6.getId()
            r1.append(r2)
            java.lang.String r2 = "; mIsSystemApp = "
            r1.append(r2)
            boolean r2 = r0.mIsSystemApp
            r1.append(r2)
            java.lang.String r2 = "; mSuspendedAppsAdmin = "
            r1.append(r2)
            com.android.settingslib.RestrictedLockUtils$EnforcedAdmin r2 = r0.mSuspendedAppsAdmin
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "InfoGuts"
            android.util.Log.d(r2, r1)
        L_0x00c4:
            android.app.INotificationManager r1 = r0.mINotificationManager
            int r2 = r0.mAppUid
            r3 = 0
            int r1 = r1.getNumNotificationChannelsForPackage(r5, r2, r3)
            int r2 = r0.mNumUniqueChannelsInRow
            if (r2 == 0) goto L_0x010d
            r4 = 1
            if (r2 != r4) goto L_0x00e8
            android.app.NotificationChannel r2 = r0.mSingleNotificationChannel
            if (r2 == 0) goto L_0x00e8
            java.lang.String r2 = r2.getId()
            java.lang.String r5 = "miscellaneous"
            boolean r2 = r2.equals(r5)
            if (r2 == 0) goto L_0x00e8
            if (r1 != r4) goto L_0x00e8
            r1 = r4
            goto L_0x00e9
        L_0x00e8:
            r1 = r3
        L_0x00e9:
            r0.mIsSingleDefaultChannel = r1
            int r1 = r0.getAlertingBehavior()
            r2 = 2
            if (r1 != r2) goto L_0x00f3
            r3 = r4
        L_0x00f3:
            r0.mIsAutomaticChosen = r3
            r0.bindHeader()
            r0.bindChannelDetails()
            r0.bindInlineControls()
            com.android.systemui.statusbar.notification.row.NotificationControlsEvent r1 = com.android.systemui.statusbar.notification.row.NotificationControlsEvent.NOTIFICATION_CONTROLS_OPEN
            r0.logUiEvent(r1)
            com.android.internal.logging.MetricsLogger r1 = r0.mMetricsLogger
            android.metrics.LogMaker r0 = r0.notificationControlsLogMaker()
            r1.write(r0)
            return
        L_0x010d:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "bindNotification requires at least one channel"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.NotificationInfo.bindNotification(android.content.pm.PackageManager, android.app.INotificationManager, com.android.systemui.statusbar.notification.row.OnUserInteractionCallback, com.android.systemui.statusbar.notification.row.ChannelEditorDialogController, java.lang.String, android.app.NotificationChannel, java.util.Set, com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.notification.row.NotificationInfo$OnSettingsClickListener, com.android.systemui.statusbar.notification.row.NotificationInfo$OnAppSettingsClickListener, com.android.internal.logging.UiEventLogger, boolean, boolean, boolean, com.android.systemui.statusbar.notification.AssistantFeedbackController):void");
    }

    private void bindInlineControls() {
        if (this.mIsNonblockable) {
            findViewById(R$id.non_configurable_text).setVisibility(0);
            findViewById(R$id.non_configurable_multichannel_text).setVisibility(8);
            findViewById(R$id.interruptiveness_settings).setVisibility(8);
            ((TextView) findViewById(R$id.done)).setText(R$string.inline_done_button);
            findViewById(R$id.turn_off_notifications).setVisibility(8);
        } else if (this.mNumUniqueChannelsInRow > 1) {
            findViewById(R$id.non_configurable_text).setVisibility(8);
            findViewById(R$id.interruptiveness_settings).setVisibility(8);
            findViewById(R$id.non_configurable_multichannel_text).setVisibility(0);
        } else {
            findViewById(R$id.non_configurable_text).setVisibility(8);
            findViewById(R$id.non_configurable_multichannel_text).setVisibility(8);
            findViewById(R$id.interruptiveness_settings).setVisibility(0);
        }
        View findViewById = findViewById(R$id.turn_off_notifications);
        findViewById.setOnClickListener(getTurnOffNotificationsClickListener());
        findViewById.setVisibility((!findViewById.hasOnClickListeners() || this.mIsNonblockable) ? 8 : 0);
        View findViewById2 = findViewById(R$id.done);
        findViewById2.setOnClickListener(this.mOnDismissSettings);
        findViewById2.setAccessibilityDelegate(this.mGutsContainer.getAccessibilityDelegate());
        View findViewById3 = findViewById(R$id.silence);
        View findViewById4 = findViewById(R$id.alert);
        findViewById3.setOnClickListener(this.mOnSilent);
        findViewById4.setOnClickListener(this.mOnAlert);
        View findViewById5 = findViewById(R$id.automatic);
        if (this.mShowAutomaticSetting) {
            this.mAutomaticDescriptionView.setText(Html.fromHtml(this.mContext.getText(this.mAssistantFeedbackController.getInlineDescriptionResource(this.mEntry)).toString()));
            findViewById5.setVisibility(0);
            findViewById5.setOnClickListener(this.mOnAutomatic);
        } else {
            findViewById5.setVisibility(8);
        }
        applyAlertingBehavior(getAlertingBehavior(), false);
    }

    private void bindHeader() {
        this.mPkgIcon = null;
        try {
            ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPackageName, 795136);
            if (applicationInfo != null) {
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
                this.mPkgIcon = this.mPm.getApplicationIcon(applicationInfo);
            }
        } catch (PackageManager.NameNotFoundException unused) {
            this.mPkgIcon = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView) findViewById(R$id.pkg_icon)).setImageDrawable(this.mPkgIcon);
        ((TextView) findViewById(R$id.pkg_name)).setText(this.mAppName);
        bindDelegate();
        View findViewById = findViewById(R$id.app_settings);
        Intent appSettingsIntent = getAppSettingsIntent(this.mPm, this.mPackageName, this.mSingleNotificationChannel, this.mSbn.getId(), this.mSbn.getTag());
        int i = 0;
        if (appSettingsIntent == null || TextUtils.isEmpty(this.mSbn.getNotification().getSettingsText())) {
            findViewById.setVisibility(8);
        } else {
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(new NotificationInfo$$ExternalSyntheticLambda6(this, appSettingsIntent));
        }
        View findViewById2 = findViewById(R$id.info);
        findViewById2.setOnClickListener(getSettingsOnClickListener());
        if (!findViewById2.hasOnClickListeners()) {
            i = 8;
        }
        findViewById2.setVisibility(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$bindHeader$4(Intent intent, View view) {
        this.mAppSettingsClickListener.onClick(view, intent);
    }

    private View.OnClickListener getSettingsOnClickListener() {
        int i = this.mAppUid;
        if (i < 0 || this.mOnSettingsClickListener == null || !this.mIsDeviceProvisioned) {
            return null;
        }
        return new NotificationInfo$$ExternalSyntheticLambda5(this, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getSettingsOnClickListener$5(int i, View view) {
        this.mOnSettingsClickListener.onClick(view, this.mNumUniqueChannelsInRow > 1 ? null : this.mSingleNotificationChannel, i);
    }

    private View.OnClickListener getTurnOffNotificationsClickListener() {
        return new NotificationInfo$$ExternalSyntheticLambda1(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getTurnOffNotificationsClickListener$7(View view) {
        ChannelEditorDialogController channelEditorDialogController;
        if (!this.mPresentingChannelEditorDialog && (channelEditorDialogController = this.mChannelEditorDialogController) != null) {
            this.mPresentingChannelEditorDialog = true;
            channelEditorDialogController.prepareDialogForApp(this.mAppName, this.mPackageName, this.mAppUid, this.mUniqueChannelsInRow, this.mPkgIcon, this.mIsSystemApp, this.mSuspendedAppsAdmin, this.mOnSettingsClickListener);
            this.mChannelEditorDialogController.setOnFinishListener(new NotificationInfo$$ExternalSyntheticLambda7(this));
            this.mChannelEditorDialogController.show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getTurnOffNotificationsClickListener$6() {
        this.mPresentingChannelEditorDialog = false;
        this.mGutsContainer.closeControls(this, false);
    }

    private void bindChannelDetails() throws RemoteException {
        bindName();
        bindGroup();
    }

    private void bindName() {
        NotificationChannel notificationChannel;
        TextView textView = (TextView) findViewById(R$id.channel_name);
        if (this.mIsSingleDefaultChannel || this.mNumUniqueChannelsInRow > 1 || (notificationChannel = this.mSingleNotificationChannel) == null) {
            textView.setVisibility(8);
        } else {
            textView.setText(notificationChannel.getName());
        }
    }

    private void bindDelegate() {
        TextView textView = (TextView) findViewById(R$id.delegate_name);
        if (!TextUtils.equals(this.mPackageName, this.mDelegatePkg)) {
            textView.setVisibility(0);
        } else {
            textView.setVisibility(8);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r4.mINotificationManager.getNotificationChannelGroupForPackage(r4.mSingleNotificationChannel.getGroup(), r4.mPackageName, r4.mAppUid);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void bindGroup() throws android.os.RemoteException {
        /*
            r4 = this;
            android.app.NotificationChannel r0 = r4.mSingleNotificationChannel
            if (r0 == 0) goto L_0x0021
            java.lang.String r0 = r0.getGroup()
            if (r0 == 0) goto L_0x0021
            android.app.INotificationManager r0 = r4.mINotificationManager
            android.app.NotificationChannel r1 = r4.mSingleNotificationChannel
            java.lang.String r1 = r1.getGroup()
            java.lang.String r2 = r4.mPackageName
            int r3 = r4.mAppUid
            android.app.NotificationChannelGroup r0 = r0.getNotificationChannelGroupForPackage(r1, r2, r3)
            if (r0 == 0) goto L_0x0021
            java.lang.CharSequence r0 = r0.getName()
            goto L_0x0022
        L_0x0021:
            r0 = 0
        L_0x0022:
            int r1 = com.android.systemui.R$id.group_name
            android.view.View r4 = r4.findViewById(r1)
            android.widget.TextView r4 = (android.widget.TextView) r4
            if (r0 == 0) goto L_0x0034
            r4.setText(r0)
            r0 = 0
            r4.setVisibility(r0)
            goto L_0x0039
        L_0x0034:
            r0 = 8
            r4.setVisibility(r0)
        L_0x0039:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.NotificationInfo.bindGroup():void");
    }

    private void saveImportance() {
        if (!this.mIsNonblockable) {
            if (this.mChosenImportance == null) {
                this.mChosenImportance = Integer.valueOf(this.mStartingChannelImportance);
            }
            updateImportance();
        }
    }

    private void updateImportance() {
        if (this.mChosenImportance != null) {
            logUiEvent(NotificationControlsEvent.NOTIFICATION_CONTROLS_SAVE_IMPORTANCE);
            this.mMetricsLogger.write(importanceChangeLogMaker());
            int intValue = this.mChosenImportance.intValue();
            if (this.mStartingChannelImportance != -1000 && ((this.mWasShownHighPriority && this.mChosenImportance.intValue() >= 3) || (!this.mWasShownHighPriority && this.mChosenImportance.intValue() < 3))) {
                intValue = this.mStartingChannelImportance;
            }
            new Handler((Looper) Dependency.get(Dependency.BG_LOOPER)).post(new UpdateImportanceRunnable(this.mINotificationManager, this.mPackageName, this.mAppUid, this.mNumUniqueChannelsInRow == 1 ? this.mSingleNotificationChannel : null, this.mStartingChannelImportance, intValue, this.mIsAutomaticChosen));
            this.mOnUserInteractionCallback.onImportanceChanged(this.mEntry);
        }
    }

    public boolean post(Runnable runnable) {
        if (!this.mSkipPost) {
            return super.post(runnable);
        }
        runnable.run();
        return true;
    }

    private void applyAlertingBehavior(int i, boolean z) {
        int i2;
        boolean z2 = true;
        if (z) {
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.setOrdering(0);
            TransitionSet addTransition = transitionSet.addTransition(new Fade(2)).addTransition(new ChangeBounds());
            Transition duration = new Fade(1).setStartDelay(150).setDuration(200);
            Interpolator interpolator = Interpolators.FAST_OUT_SLOW_IN;
            addTransition.addTransition(duration.setInterpolator(interpolator));
            transitionSet.setDuration(350);
            transitionSet.setInterpolator(interpolator);
            TransitionManager.endTransitions(this);
            TransitionManager.beginDelayedTransition(this, transitionSet);
        }
        View findViewById = findViewById(R$id.alert);
        View findViewById2 = findViewById(R$id.silence);
        View findViewById3 = findViewById(R$id.automatic);
        if (i == 0) {
            this.mPriorityDescriptionView.setVisibility(0);
            this.mSilentDescriptionView.setVisibility(8);
            this.mAutomaticDescriptionView.setVisibility(8);
            post(new NotificationInfo$$ExternalSyntheticLambda8(findViewById, findViewById2, findViewById3));
        } else if (i == 1) {
            this.mSilentDescriptionView.setVisibility(0);
            this.mPriorityDescriptionView.setVisibility(8);
            this.mAutomaticDescriptionView.setVisibility(8);
            post(new NotificationInfo$$ExternalSyntheticLambda9(findViewById, findViewById2, findViewById3));
        } else if (i == 2) {
            this.mAutomaticDescriptionView.setVisibility(0);
            this.mPriorityDescriptionView.setVisibility(8);
            this.mSilentDescriptionView.setVisibility(8);
            post(new NotificationInfo$$ExternalSyntheticLambda10(findViewById3, findViewById, findViewById2));
        } else {
            throw new IllegalArgumentException("Unrecognized alerting behavior: " + i);
        }
        if (getAlertingBehavior() == i) {
            z2 = false;
        }
        TextView textView = (TextView) findViewById(R$id.done);
        if (z2) {
            i2 = R$string.inline_ok_button;
        } else {
            i2 = R$string.inline_done_button;
        }
        textView.setText(i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$applyAlertingBehavior$8(View view, View view2, View view3) {
        view.setSelected(true);
        view2.setSelected(false);
        view3.setSelected(false);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$applyAlertingBehavior$9(View view, View view2, View view3) {
        view.setSelected(false);
        view2.setSelected(true);
        view3.setSelected(false);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$applyAlertingBehavior$10(View view, View view2, View view3) {
        view.setSelected(true);
        view2.setSelected(false);
        view3.setSelected(false);
    }

    public void onFinishedClosing() {
        Integer num = this.mChosenImportance;
        if (num != null) {
            this.mStartingChannelImportance = num.intValue();
        }
        bindInlineControls();
        logUiEvent(NotificationControlsEvent.NOTIFICATION_CONTROLS_CLOSE);
        this.mMetricsLogger.write(notificationControlsLogMaker().setType(2));
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (this.mGutsContainer != null && accessibilityEvent.getEventType() == 32) {
            if (this.mGutsContainer.isExposed()) {
                accessibilityEvent.getText().add(this.mContext.getString(R$string.notification_channel_controls_opened_accessibility, new Object[]{this.mAppName}));
                return;
            }
            accessibilityEvent.getText().add(this.mContext.getString(R$string.notification_channel_controls_closed_accessibility, new Object[]{this.mAppName}));
        }
    }

    private Intent getAppSettingsIntent(PackageManager packageManager, String str, NotificationChannel notificationChannel, int i, String str2) {
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.NOTIFICATION_PREFERENCES").setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
        if (queryIntentActivities == null || queryIntentActivities.size() == 0 || queryIntentActivities.get(0) == null) {
            return null;
        }
        ActivityInfo activityInfo = queryIntentActivities.get(0).activityInfo;
        intent.setClassName(activityInfo.packageName, activityInfo.name);
        if (notificationChannel != null) {
            intent.putExtra("android.intent.extra.CHANNEL_ID", notificationChannel.getId());
        }
        intent.putExtra("android.intent.extra.NOTIFICATION_ID", i);
        intent.putExtra("android.intent.extra.NOTIFICATION_TAG", str2);
        return intent;
    }

    public void setGutsParent(NotificationGuts notificationGuts) {
        this.mGutsContainer = notificationGuts;
    }

    public boolean shouldBeSaved() {
        return this.mPressedApply;
    }

    public boolean handleCloseControls(boolean z, boolean z2) {
        ChannelEditorDialogController channelEditorDialogController;
        if (this.mPresentingChannelEditorDialog && (channelEditorDialogController = this.mChannelEditorDialogController) != null) {
            this.mPresentingChannelEditorDialog = false;
            channelEditorDialogController.setOnFinishListener((OnChannelEditorDialogFinishedListener) null);
            this.mChannelEditorDialogController.close();
        }
        if (z) {
            saveImportance();
        }
        return false;
    }

    public int getActualHeight() {
        return this.mActualHeight;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mActualHeight = getHeight();
    }

    private static class UpdateImportanceRunnable implements Runnable {
        private final int mAppUid;
        private final NotificationChannel mChannelToUpdate;
        private final int mCurrentImportance;
        private final INotificationManager mINotificationManager;
        private final int mNewImportance;
        private final String mPackageName;
        private final boolean mUnlockImportance;

        public UpdateImportanceRunnable(INotificationManager iNotificationManager, String str, int i, NotificationChannel notificationChannel, int i2, int i3, boolean z) {
            this.mINotificationManager = iNotificationManager;
            this.mPackageName = str;
            this.mAppUid = i;
            this.mChannelToUpdate = notificationChannel;
            this.mCurrentImportance = i2;
            this.mNewImportance = i3;
            this.mUnlockImportance = z;
        }

        public void run() {
            try {
                NotificationChannel notificationChannel = this.mChannelToUpdate;
                if (notificationChannel == null) {
                    this.mINotificationManager.setNotificationsEnabledWithImportanceLockForPackage(this.mPackageName, this.mAppUid, this.mNewImportance >= this.mCurrentImportance);
                } else if (this.mUnlockImportance) {
                    this.mINotificationManager.unlockNotificationChannel(this.mPackageName, this.mAppUid, notificationChannel.getId());
                } else {
                    notificationChannel.setImportance(this.mNewImportance);
                    this.mChannelToUpdate.lockFields(4);
                    this.mINotificationManager.updateNotificationChannelForPackage(this.mPackageName, this.mAppUid, this.mChannelToUpdate);
                }
            } catch (RemoteException e) {
                Log.e("InfoGuts", "Unable to update notification importance", e);
            }
        }
    }

    private void logUiEvent(NotificationControlsEvent notificationControlsEvent) {
        StatusBarNotification statusBarNotification = this.mSbn;
        if (statusBarNotification != null) {
            this.mUiEventLogger.logWithInstanceId(notificationControlsEvent, statusBarNotification.getUid(), this.mSbn.getPackageName(), this.mSbn.getInstanceId());
        }
    }

    private LogMaker getLogMaker() {
        StatusBarNotification statusBarNotification = this.mSbn;
        if (statusBarNotification == null) {
            return new LogMaker(1621);
        }
        return statusBarNotification.getLogMaker().setCategory(1621);
    }

    private LogMaker importanceChangeLogMaker() {
        Integer num = this.mChosenImportance;
        return getLogMaker().setCategory(291).setType(4).setSubtype(Integer.valueOf(num != null ? num.intValue() : this.mStartingChannelImportance).intValue() - this.mStartingChannelImportance);
    }

    private LogMaker notificationControlsLogMaker() {
        return getLogMaker().setCategory(204).setType(1).setSubtype(0);
    }

    private int getAlertingBehavior() {
        if (!this.mShowAutomaticSetting || this.mSingleNotificationChannel.hasUserSetImportance()) {
            return this.mWasShownHighPriority ^ true ? 1 : 0;
        }
        return 2;
    }
}
