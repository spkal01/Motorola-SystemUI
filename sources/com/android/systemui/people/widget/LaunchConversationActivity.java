package com.android.systemui.people.widget;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.p011wm.shell.bubbles.Bubble;
import com.android.systemui.people.PeopleSpaceUtils;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.wmshell.BubblesManager;
import java.util.Optional;

public class LaunchConversationActivity extends Activity {
    /* access modifiers changed from: private */
    public Bubble mBubble;
    /* access modifiers changed from: private */
    public final Optional<BubblesManager> mBubblesManagerOptional;
    /* access modifiers changed from: private */
    public CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public NotificationEntry mEntryToBubble;
    private IStatusBarService mIStatusBarService;
    private boolean mIsForTesting;
    private NotificationEntryManager mNotificationEntryManager;
    private UiEventLogger mUiEventLogger = new UiEventLoggerImpl();
    private final UserManager mUserManager;

    public LaunchConversationActivity(NotificationEntryManager notificationEntryManager, Optional<BubblesManager> optional, UserManager userManager, CommandQueue commandQueue) {
        this.mNotificationEntryManager = notificationEntryManager;
        this.mBubblesManagerOptional = optional;
        this.mUserManager = userManager;
        this.mCommandQueue = commandQueue;
        commandQueue.addCallback((CommandQueue.Callbacks) new CommandQueue.Callbacks() {
            public void appTransitionFinished(int i) {
                if (LaunchConversationActivity.this.mBubblesManagerOptional.isPresent()) {
                    if (LaunchConversationActivity.this.mBubble != null) {
                        ((BubblesManager) LaunchConversationActivity.this.mBubblesManagerOptional.get()).expandStackAndSelectBubble(LaunchConversationActivity.this.mBubble);
                    } else if (LaunchConversationActivity.this.mEntryToBubble != null) {
                        ((BubblesManager) LaunchConversationActivity.this.mBubblesManagerOptional.get()).expandStackAndSelectBubble(LaunchConversationActivity.this.mEntryToBubble);
                    }
                }
                LaunchConversationActivity.this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this);
            }
        });
    }

    public void onCreate(Bundle bundle) {
        if (!this.mIsForTesting) {
            super.onCreate(bundle);
        }
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("extra_tile_id");
        String stringExtra2 = intent.getStringExtra("extra_package_name");
        UserHandle userHandle = (UserHandle) intent.getParcelableExtra("extra_user_handle");
        String stringExtra3 = intent.getStringExtra("extra_notification_key");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mUiEventLogger.log(PeopleSpaceUtils.PeopleSpaceWidgetEvent.PEOPLE_SPACE_WIDGET_CLICKED);
            try {
                if (this.mUserManager.isQuietModeEnabled(userHandle)) {
                    getApplicationContext().startActivity(UnlaunchableAppActivity.createInQuietModeDialogIntent(userHandle.getIdentifier()));
                    finish();
                    return;
                }
                if (this.mBubblesManagerOptional.isPresent()) {
                    this.mBubble = this.mBubblesManagerOptional.get().getBubbleWithShortcutId(stringExtra);
                    NotificationEntry pendingOrActiveNotif = this.mNotificationEntryManager.getPendingOrActiveNotif(stringExtra3);
                    if (this.mBubble != null || (pendingOrActiveNotif != null && pendingOrActiveNotif.canBubble())) {
                        this.mEntryToBubble = pendingOrActiveNotif;
                        finish();
                        return;
                    }
                }
                if (this.mIStatusBarService == null) {
                    this.mIStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
                }
                clearNotificationIfPresent(stringExtra3, stringExtra2, userHandle);
                ((LauncherApps) getApplicationContext().getSystemService(LauncherApps.class)).startShortcut(stringExtra2, stringExtra, (Rect) null, (Bundle) null, userHandle);
            } catch (Exception e) {
                Log.e("PeopleSpaceLaunchConv", "Exception launching shortcut:" + e);
            }
        }
        finish();
    }

    /* access modifiers changed from: package-private */
    public void clearNotificationIfPresent(String str, String str2, UserHandle userHandle) {
        if (!TextUtils.isEmpty(str)) {
            try {
                if (this.mIStatusBarService != null) {
                    NotificationEntryManager notificationEntryManager = this.mNotificationEntryManager;
                    if (notificationEntryManager != null) {
                        NotificationEntry pendingOrActiveNotif = notificationEntryManager.getPendingOrActiveNotif(str);
                        if (pendingOrActiveNotif == null) {
                            return;
                        }
                        if (pendingOrActiveNotif.getRanking() != null) {
                            String str3 = str2;
                            this.mIStatusBarService.onNotificationClear(str3, userHandle.getIdentifier(), str, 0, 2, NotificationVisibility.obtain(str, pendingOrActiveNotif.getRanking().getRank(), this.mNotificationEntryManager.getActiveNotificationsCount(), true));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("PeopleSpaceLaunchConv", "Exception cancelling notification:" + e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setIsForTesting(boolean z, IStatusBarService iStatusBarService) {
        this.mIsForTesting = z;
        this.mIStatusBarService = iStatusBarService;
    }
}
