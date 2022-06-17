package com.android.systemui.statusbar.notification.row;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dependency;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.notification.AssistantFeedbackController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotificationGuts;

public class FeedbackInfo extends LinearLayout implements NotificationGuts.GutsContent {
    private static final boolean DEBUG = Log.isLoggable("FeedbackInfo", 3);
    private String mAppName;
    private NotificationEntry mEntry;
    private ExpandableNotificationRow mExpandableNotificationRow;
    private AssistantFeedbackController mFeedbackController;
    private NotificationGuts mGutsContainer;
    private NotificationMenuRowPlugin mMenuRowPlugin;
    private NotificationEntryManager mNotificationEntryManager;
    private NotificationGutsManager mNotificationGutsManager;
    private String mPkg;
    private PackageManager mPm;
    private NotificationListenerService.Ranking mRanking;
    private IStatusBarService mStatusBarService;

    public View getContentView() {
        return this;
    }

    public boolean handleCloseControls(boolean z, boolean z2) {
        return false;
    }

    public boolean needsFalsingProtection() {
        return false;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    public boolean willBeRemoved() {
        return false;
    }

    public FeedbackInfo(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void bindGuts(PackageManager packageManager, StatusBarNotification statusBarNotification, NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, AssistantFeedbackController assistantFeedbackController) {
        this.mPkg = statusBarNotification.getPackageName();
        this.mPm = packageManager;
        this.mEntry = notificationEntry;
        this.mExpandableNotificationRow = expandableNotificationRow;
        this.mRanking = notificationEntry.getRanking();
        this.mFeedbackController = assistantFeedbackController;
        this.mAppName = this.mPkg;
        this.mNotificationEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mStatusBarService = (IStatusBarService) Dependency.get(IStatusBarService.class);
        this.mNotificationGutsManager = (NotificationGutsManager) Dependency.get(NotificationGutsManager.class);
        bindHeader();
        bindPrompt();
    }

    private void bindHeader() {
        Drawable drawable;
        try {
            ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(this.mPkg, 795136);
            if (applicationInfo != null) {
                this.mAppName = String.valueOf(this.mPm.getApplicationLabel(applicationInfo));
                drawable = this.mPm.getApplicationIcon(applicationInfo);
            } else {
                drawable = null;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            drawable = this.mPm.getDefaultActivityIcon();
        }
        ((ImageView) findViewById(R$id.pkg_icon)).setImageDrawable(drawable);
        ((TextView) findViewById(R$id.pkg_name)).setText(this.mAppName);
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

    private void bindPrompt() {
        TextView textView = (TextView) findViewById(R$id.yes);
        TextView textView2 = (TextView) findViewById(R$id.f68no);
        textView.setVisibility(0);
        textView2.setVisibility(0);
        textView.setOnClickListener(new FeedbackInfo$$ExternalSyntheticLambda0(this));
        textView2.setOnClickListener(new FeedbackInfo$$ExternalSyntheticLambda1(this));
        ((TextView) findViewById(R$id.prompt)).setText(Html.fromHtml(getPrompt()));
    }

    @SuppressLint({"DefaultLocale"})
    private String getPrompt() {
        StringBuilder sb = new StringBuilder();
        int feedbackStatus = this.mFeedbackController.getFeedbackStatus(this.mEntry);
        if (DEBUG) {
            sb.append(String.format("[DEBUG]: oldImportance=%d, newImportance=%d, ranking=%f\n\n", new Object[]{Integer.valueOf(this.mRanking.getChannel().getImportance()), Integer.valueOf(this.mRanking.getImportance()), Float.valueOf(this.mRanking.getRankingScore())}));
        }
        if (feedbackStatus == 1) {
            sb.append(this.mContext.getText(R$string.feedback_alerted));
        } else if (feedbackStatus == 2) {
            sb.append(this.mContext.getText(R$string.feedback_silenced));
        } else if (feedbackStatus == 3) {
            sb.append(this.mContext.getText(R$string.feedback_promoted));
        } else if (feedbackStatus == 4) {
            sb.append(this.mContext.getText(R$string.feedback_demoted));
        }
        sb.append(" ");
        sb.append(this.mContext.getText(R$string.feedback_prompt));
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public void positiveFeedback(View view) {
        this.mGutsContainer.closeControls(view, false);
        handleFeedback(true);
    }

    /* access modifiers changed from: private */
    public void negativeFeedback(View view) {
        NotificationMenuRowPlugin provider = this.mExpandableNotificationRow.getProvider();
        this.mMenuRowPlugin = provider;
        NotificationMenuRowPlugin.MenuItem longpressMenuItem = provider != null ? provider.getLongpressMenuItem(this.mContext) : null;
        this.mGutsContainer.closeControls(view, false);
        this.mNotificationGutsManager.openGuts(this.mExpandableNotificationRow, 0, 0, longpressMenuItem);
        handleFeedback(false);
    }

    private void handleFeedback(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt("feedback.rating", z ? 1 : -1);
        sendFeedbackToAssistant(bundle);
    }

    private void sendFeedbackToAssistant(Bundle bundle) {
        if (this.mFeedbackController.isFeedbackEnabled()) {
            try {
                this.mStatusBarService.onNotificationFeedbackReceived(this.mRanking.getKey(), bundle);
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.e("FeedbackInfo", "Failed to send feedback to assistant", e);
                }
            }
        }
    }

    public void setGutsParent(NotificationGuts notificationGuts) {
        this.mGutsContainer = notificationGuts;
    }

    public int getActualHeight() {
        return getHeight();
    }
}
