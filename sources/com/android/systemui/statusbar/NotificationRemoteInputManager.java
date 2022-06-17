package com.android.systemui.statusbar;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.RemoteInputHistoryItem;
import android.content.Context;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dumpable;
import com.android.systemui.R$id;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.CliNotificationSettings;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.policy.RemoteInputView;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class NotificationRemoteInputManager implements Dumpable {
    public static final boolean ENABLE_REMOTE_INPUT = SystemProperties.getBoolean("debug.enable_remote_input", true);
    public static boolean FORCE_REMOTE_INPUT_HISTORY = SystemProperties.getBoolean("debug.force_remoteinput_history", true);
    protected IStatusBarService mBarService;
    protected Callback mCallback;
    /* access modifiers changed from: private */
    public final NotificationClickNotifier mClickNotifier;
    protected final Context mContext;
    protected final ArraySet<NotificationEntry> mEntriesKeptForRemoteInputActive = new ArraySet<>();
    /* access modifiers changed from: private */
    public final NotificationEntryManager mEntryManager;
    private final RemoteViews.InteractionHandler mInteractionHandler = new RemoteViews.InteractionHandler() {
        public boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
            boolean z;
            ((StatusBar) NotificationRemoteInputManager.this.mStatusBarLazy.get()).wakeUpIfDozing(SystemClock.uptimeMillis(), view, "NOTIFICATION_CLICK");
            NotificationEntry notificationForParent = getNotificationForParent(view.getParent());
            NotificationRemoteInputManager.this.mLogger.logInitialClick(notificationForParent, pendingIntent);
            if (handleRemoteInput(view, pendingIntent)) {
                NotificationRemoteInputManager.this.mLogger.logRemoteInputWasHandled(notificationForParent);
                return true;
            }
            logActionClick(view, notificationForParent, pendingIntent);
            try {
                ActivityManager.getService().resumeAppSwitches();
            } catch (RemoteException unused) {
            }
            Notification.Action actionFromView = getActionFromView(view, notificationForParent, pendingIntent);
            Callback callback = NotificationRemoteInputManager.this.mCallback;
            if (actionFromView == null) {
                z = false;
            } else {
                z = actionFromView.isAuthenticationRequired();
            }
            return callback.handleRemoteViewClick(view, pendingIntent, z, new NotificationRemoteInputManager$1$$ExternalSyntheticLambda0(this, remoteResponse, view, pendingIntent, notificationForParent));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onInteraction$0(RemoteViews.RemoteResponse remoteResponse, View view, PendingIntent pendingIntent, NotificationEntry notificationEntry) {
            Pair launchOptions = remoteResponse.getLaunchOptions(view);
            if (DesktopFeature.isDesktopDisplayContext(view.getContext())) {
                int displayId = view.getContext().getDisplayId();
                Log.d("NotifRemoteInputManager", "startPendingIntent for display: " + displayId + "; pendingIntent: " + pendingIntent);
                ((ActivityOptions) launchOptions.second).setLaunchDisplayId(displayId);
                ((ActivityOptions) launchOptions.second).setCallerDisplayId(displayId);
            }
            NotificationRemoteInputManager.this.mLogger.logStartingIntentWithDefaultHandler(notificationEntry, pendingIntent);
            boolean startPendingIntent = RemoteViews.startPendingIntent(view, pendingIntent, launchOptions);
            if (startPendingIntent) {
                NotificationRemoteInputManager.this.releaseNotificationIfKeptForRemoteInputHistory(notificationEntry);
            }
            return startPendingIntent;
        }

        private Notification.Action getActionFromView(View view, NotificationEntry notificationEntry, PendingIntent pendingIntent) {
            Integer num = (Integer) view.getTag(16909233);
            if (num == null) {
                return null;
            }
            if (notificationEntry == null) {
                Log.w("NotifRemoteInputManager", "Couldn't determine notification for click.");
                return null;
            }
            StatusBarNotification sbn = notificationEntry.getSbn();
            Notification.Action[] actionArr = sbn.getNotification().actions;
            if (actionArr == null || num.intValue() >= actionArr.length) {
                Log.w("NotifRemoteInputManager", "statusBarNotification.getNotification().actions is null or invalid");
                return null;
            }
            Notification.Action action = sbn.getNotification().actions[num.intValue()];
            if (Objects.equals(action.actionIntent, pendingIntent)) {
                return action;
            }
            Log.w("NotifRemoteInputManager", "actionIntent does not match");
            return null;
        }

        private void logActionClick(View view, NotificationEntry notificationEntry, PendingIntent pendingIntent) {
            Notification.Action actionFromView = getActionFromView(view, notificationEntry, pendingIntent);
            if (actionFromView != null) {
                ViewParent parent = view.getParent();
                String key = notificationEntry.getSbn().getKey();
                NotificationRemoteInputManager.this.mClickNotifier.onNotificationActionClick(key, (view.getId() != 16908697 || parent == null || !(parent instanceof ViewGroup)) ? -1 : ((ViewGroup) parent).indexOfChild(view), actionFromView, NotificationVisibility.obtain(key, notificationEntry.getRanking().getRank(), NotificationRemoteInputManager.this.mEntryManager.getActiveNotificationsCount(), true, NotificationLogger.getNotificationLocation(notificationEntry)), false);
            }
        }

        private NotificationEntry getNotificationForParent(ViewParent viewParent) {
            while (viewParent != null) {
                if (viewParent instanceof ExpandableNotificationRow) {
                    return ((ExpandableNotificationRow) viewParent).getEntry();
                }
                viewParent = viewParent.getParent();
            }
            return null;
        }

        private boolean handleRemoteInput(View view, PendingIntent pendingIntent) {
            if (NotificationRemoteInputManager.this.mCallback.shouldHandleRemoteInput(view, pendingIntent)) {
                return true;
            }
            Object tag = view.getTag(16909353);
            RemoteInput[] remoteInputArr = tag instanceof RemoteInput[] ? (RemoteInput[]) tag : null;
            if (remoteInputArr == null) {
                return false;
            }
            RemoteInput remoteInput = null;
            for (RemoteInput remoteInput2 : remoteInputArr) {
                if (remoteInput2.getAllowFreeFormInput()) {
                    remoteInput = remoteInput2;
                }
            }
            if (remoteInput == null) {
                return false;
            }
            return NotificationRemoteInputManager.this.activateRemoteInput(view, remoteInputArr, remoteInput, pendingIntent, (NotificationEntry.EditedSuggestionInfo) null);
        }
    };
    private final KeyguardManager mKeyguardManager;
    protected final ArraySet<String> mKeysKeptForRemoteInputHistory = new ArraySet<>();
    protected final ArrayList<NotificationLifetimeExtender> mLifetimeExtenders = new ArrayList<>();
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    /* access modifiers changed from: private */
    public final ActionClickLogger mLogger;
    /* access modifiers changed from: private */
    public final Handler mMainHandler;
    protected NotificationLifetimeExtender.NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    protected RemoteInputController mRemoteInputController;
    private final RemoteInputUriController mRemoteInputUriController;
    /* access modifiers changed from: private */
    public final SmartReplyController mSmartReplyController;
    /* access modifiers changed from: private */
    public final Lazy<StatusBar> mStatusBarLazy;
    private final StatusBarStateController mStatusBarStateController;
    private final UserManager mUserManager;

    public interface AuthBypassPredicate {
        boolean canSendRemoteInputWithoutBouncer();
    }

    public interface BouncerChecker {
        boolean showBouncerIfNecessary();
    }

    public interface Callback {
        boolean handleRemoteViewClick(View view, PendingIntent pendingIntent, boolean z, ClickHandler clickHandler);

        void onLockedRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view);

        void onLockedWorkRemoteInput(int i, ExpandableNotificationRow expandableNotificationRow, View view);

        void onMakeExpandedVisibleForRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view, boolean z, Runnable runnable);

        boolean shouldHandleRemoteInput(View view, PendingIntent pendingIntent);
    }

    public interface ClickHandler {
        boolean handleClick();
    }

    public NotificationRemoteInputManager(Context context, NotificationLockscreenUserManager notificationLockscreenUserManager, SmartReplyController smartReplyController, NotificationEntryManager notificationEntryManager, Lazy<StatusBar> lazy, StatusBarStateController statusBarStateController, Handler handler, RemoteInputUriController remoteInputUriController, NotificationClickNotifier notificationClickNotifier, ActionClickLogger actionClickLogger) {
        this.mContext = context;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mSmartReplyController = smartReplyController;
        this.mEntryManager = notificationEntryManager;
        this.mStatusBarLazy = lazy;
        this.mMainHandler = handler;
        this.mLogger = actionClickLogger;
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mUserManager = (UserManager) context.getSystemService("user");
        addLifetimeExtenders();
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mStatusBarStateController = statusBarStateController;
        this.mRemoteInputUriController = remoteInputUriController;
        this.mClickNotifier = notificationClickNotifier;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
                if (z && notificationEntry != null) {
                    NotificationRemoteInputManager.this.onPerformRemoveNotification(notificationEntry, notificationEntry.getKey());
                }
            }
        });
    }

    public void setUpWithCallback(Callback callback, RemoteInputController.Delegate delegate) {
        this.mCallback = callback;
        RemoteInputController remoteInputController = new RemoteInputController(delegate, this.mRemoteInputUriController);
        this.mRemoteInputController = remoteInputController;
        remoteInputController.addCallback(new RemoteInputController.Callback() {
            public void onRemoteInputSent(NotificationEntry notificationEntry) {
                if (NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY && NotificationRemoteInputManager.this.isNotificationKeptForRemoteInputHistory(notificationEntry.getKey())) {
                    NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(notificationEntry.getKey());
                } else if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.contains(notificationEntry)) {
                    NotificationRemoteInputManager.this.mMainHandler.postDelayed(new NotificationRemoteInputManager$3$$ExternalSyntheticLambda0(this, notificationEntry), 200);
                }
                try {
                    NotificationRemoteInputManager.this.mBarService.onNotificationDirectReplied(notificationEntry.getSbn().getKey());
                    NotificationEntry.EditedSuggestionInfo editedSuggestionInfo = notificationEntry.editedSuggestionInfo;
                    if (editedSuggestionInfo != null) {
                        boolean z = !TextUtils.equals(notificationEntry.remoteInputText, editedSuggestionInfo.originalText);
                        IStatusBarService iStatusBarService = NotificationRemoteInputManager.this.mBarService;
                        String key = notificationEntry.getSbn().getKey();
                        NotificationEntry.EditedSuggestionInfo editedSuggestionInfo2 = notificationEntry.editedSuggestionInfo;
                        iStatusBarService.onNotificationSmartReplySent(key, editedSuggestionInfo2.index, editedSuggestionInfo2.originalText, NotificationLogger.getNotificationLocation(notificationEntry).toMetricsEventEnum(), z);
                    }
                } catch (RemoteException unused) {
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onRemoteInputSent$0(NotificationEntry notificationEntry) {
                if (NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove(notificationEntry)) {
                    NotificationRemoteInputManager.this.mNotificationLifetimeFinishedCallback.onSafeToRemove(notificationEntry.getKey());
                }
            }
        });
        this.mSmartReplyController.setCallback(new NotificationRemoteInputManager$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setUpWithCallback$0(NotificationEntry notificationEntry, CharSequence charSequence) {
        this.mEntryManager.updateNotification(rebuildNotificationWithRemoteInputInserted(notificationEntry, charSequence, true, (String) null, (Uri) null), (NotificationListenerService.RankingMap) null);
    }

    public boolean activateRemoteInput(View view, RemoteInput[] remoteInputArr, RemoteInput remoteInput, PendingIntent pendingIntent, NotificationEntry.EditedSuggestionInfo editedSuggestionInfo) {
        return lambda$activateRemoteInput$1(view, remoteInputArr, remoteInput, pendingIntent, editedSuggestionInfo, (String) null, (AuthBypassPredicate) null);
    }

    public boolean shouldAllowLockscreenRemoteInput(ExpandableNotificationRow expandableNotificationRow) {
        if (expandableNotificationRow == null || !expandableNotificationRow.isCliRow()) {
            return this.mLockscreenUserManager.shouldAllowLockscreenRemoteInput();
        }
        return !CliNotificationSettings.getInstance(this.mContext).isSafeReplyEnabled();
    }

    /* renamed from: activateRemoteInput */
    public boolean lambda$activateRemoteInput$1(View view, RemoteInput[] remoteInputArr, RemoteInput remoteInput, PendingIntent pendingIntent, NotificationEntry.EditedSuggestionInfo editedSuggestionInfo, String str, AuthBypassPredicate authBypassPredicate) {
        RemoteInputView remoteInputView;
        RemoteInputView remoteInputView2;
        ExpandableNotificationRow expandableNotificationRow;
        View view2 = view;
        PendingIntent pendingIntent2 = pendingIntent;
        String str2 = str;
        ViewParent parent = view.getParent();
        while (true) {
            remoteInputView = null;
            if (parent == null) {
                remoteInputView2 = null;
                expandableNotificationRow = null;
                break;
            }
            if (parent instanceof View) {
                View view3 = (View) parent;
                if (view3.isRootNamespace()) {
                    remoteInputView2 = findRemoteInputView(view3);
                    expandableNotificationRow = (ExpandableNotificationRow) view3.getTag(R$id.row_tag_for_content_view);
                    break;
                }
            }
            parent = parent.getParent();
        }
        if (expandableNotificationRow == null) {
            return false;
        }
        expandableNotificationRow.setUserExpanded(true);
        boolean z = authBypassPredicate != null;
        if (!z && showBouncerForRemoteInput(view2, pendingIntent2, expandableNotificationRow)) {
            return true;
        }
        if (remoteInputView2 == null || remoteInputView2.isAttachedToWindow()) {
            remoteInputView = remoteInputView2;
        }
        if (remoteInputView == null && (remoteInputView = findRemoteInputView(expandableNotificationRow.getPrivateLayout().getExpandedChild())) == null) {
            return false;
        }
        RemoteInputView remoteInputView3 = remoteInputView;
        if (remoteInputView3 == expandableNotificationRow.getPrivateLayout().getExpandedRemoteInput() && !expandableNotificationRow.getPrivateLayout().getExpandedChild().isShown()) {
            this.mCallback.onMakeExpandedVisibleForRemoteInput(expandableNotificationRow, view2, z, new NotificationRemoteInputManager$$ExternalSyntheticLambda2(this, view, remoteInputArr, remoteInput, pendingIntent, editedSuggestionInfo, str, authBypassPredicate));
            return true;
        } else if (!remoteInputView3.isAttachedToWindow()) {
            return false;
        } else {
            int width = view.getWidth();
            if (view2 instanceof TextView) {
                TextView textView = (TextView) view2;
                if (textView.getLayout() != null) {
                    width = Math.min(width, ((int) textView.getLayout().getLineWidth(0)) + textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight());
                }
            }
            int left = view.getLeft() + (width / 2);
            int top = view.getTop() + (view.getHeight() / 2);
            int width2 = remoteInputView3.getWidth();
            int height = remoteInputView3.getHeight() - top;
            int i = width2 - left;
            remoteInputView3.setRevealParameters(left, top, Math.max(Math.max(left + top, left + height), Math.max(i + top, i + height)));
            remoteInputView3.setPendingIntent(pendingIntent2);
            remoteInputView3.setRemoteInput(remoteInputArr, remoteInput, editedSuggestionInfo);
            remoteInputView3.focusAnimated();
            if (str2 != null) {
                remoteInputView3.setEditTextContent(str2);
            }
            if (z) {
                remoteInputView3.setBouncerChecker(new NotificationRemoteInputManager$$ExternalSyntheticLambda0(this, authBypassPredicate, view, pendingIntent, expandableNotificationRow));
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$activateRemoteInput$2(AuthBypassPredicate authBypassPredicate, View view, PendingIntent pendingIntent, ExpandableNotificationRow expandableNotificationRow) {
        return !authBypassPredicate.canSendRemoteInputWithoutBouncer() && showBouncerForRemoteInput(view, pendingIntent, expandableNotificationRow);
    }

    private boolean showBouncerForRemoteInput(View view, PendingIntent pendingIntent, ExpandableNotificationRow expandableNotificationRow) {
        UserInfo profileParent;
        if (shouldAllowLockscreenRemoteInput(expandableNotificationRow)) {
            return false;
        }
        int identifier = pendingIntent.getCreatorUserHandle().getIdentifier();
        boolean z = this.mUserManager.getUserInfo(identifier).isManagedProfile() && this.mKeyguardManager.isDeviceLocked(identifier);
        boolean z2 = z && (profileParent = this.mUserManager.getProfileParent(identifier)) != null && this.mKeyguardManager.isDeviceLocked(profileParent.id);
        if (this.mLockscreenUserManager.isLockscreenPublicMode(identifier) || this.mStatusBarStateController.getState() == 1) {
            if (!z || z2) {
                this.mCallback.onLockedRemoteInput(expandableNotificationRow, view);
            } else {
                this.mCallback.onLockedWorkRemoteInput(identifier, expandableNotificationRow, view);
            }
            return true;
        } else if (!z) {
            return false;
        } else {
            this.mCallback.onLockedWorkRemoteInput(identifier, expandableNotificationRow, view);
            return true;
        }
    }

    private RemoteInputView findRemoteInputView(View view) {
        if (view == null) {
            return null;
        }
        return (RemoteInputView) view.findViewWithTag(RemoteInputView.VIEW_TAG);
    }

    /* access modifiers changed from: protected */
    public void addLifetimeExtenders() {
        this.mLifetimeExtenders.add(new RemoteInputHistoryExtender());
        this.mLifetimeExtenders.add(new SmartReplyHistoryExtender());
        this.mLifetimeExtenders.add(new RemoteInputActiveExtender());
    }

    public ArrayList<NotificationLifetimeExtender> getLifetimeExtenders() {
        return this.mLifetimeExtenders;
    }

    public RemoteInputController getController() {
        return this.mRemoteInputController;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void onPerformRemoveNotification(NotificationEntry notificationEntry, String str) {
        if (this.mKeysKeptForRemoteInputHistory.contains(str)) {
            this.mKeysKeptForRemoteInputHistory.remove(str);
        }
        if (this.mRemoteInputController.isRemoteInputActive(notificationEntry)) {
            notificationEntry.mRemoteEditImeVisible = false;
            this.mRemoteInputController.removeRemoteInput(notificationEntry, (Object) null);
        }
    }

    public void onPanelCollapsed() {
        for (int i = 0; i < this.mEntriesKeptForRemoteInputActive.size(); i++) {
            NotificationEntry valueAt = this.mEntriesKeptForRemoteInputActive.valueAt(i);
            this.mRemoteInputController.removeRemoteInput(valueAt, (Object) null);
            NotificationLifetimeExtender.NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationLifetimeFinishedCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(valueAt.getKey());
            }
        }
        this.mEntriesKeptForRemoteInputActive.clear();
    }

    public boolean isNotificationKeptForRemoteInputHistory(String str) {
        return this.mKeysKeptForRemoteInputHistory.contains(str);
    }

    public boolean shouldKeepForRemoteInputHistory(NotificationEntry notificationEntry) {
        if (!FORCE_REMOTE_INPUT_HISTORY) {
            return false;
        }
        if (this.mRemoteInputController.isSpinning(notificationEntry.getKey()) || notificationEntry.hasJustSentRemoteInput()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void releaseNotificationIfKeptForRemoteInputHistory(NotificationEntry notificationEntry) {
        if (notificationEntry != null) {
            String key = notificationEntry.getKey();
            if (isNotificationKeptForRemoteInputHistory(key)) {
                this.mMainHandler.postDelayed(new NotificationRemoteInputManager$$ExternalSyntheticLambda3(this, key), 200);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$releaseNotificationIfKeptForRemoteInputHistory$3(String str) {
        if (isNotificationKeptForRemoteInputHistory(str)) {
            this.mNotificationLifetimeFinishedCallback.onSafeToRemove(str);
        }
    }

    public boolean shouldKeepForSmartReplyHistory(NotificationEntry notificationEntry) {
        if (!FORCE_REMOTE_INPUT_HISTORY) {
            return false;
        }
        return this.mSmartReplyController.isSendingSmartReply(notificationEntry.getKey());
    }

    public void checkRemoteInputOutside(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4 && motionEvent.getX() == 0.0f && motionEvent.getY() == 0.0f && this.mRemoteInputController.isRemoteInputActive()) {
            this.mRemoteInputController.closeRemoteInputs();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public StatusBarNotification rebuildNotificationForCanceledSmartReplies(NotificationEntry notificationEntry) {
        return rebuildNotificationWithRemoteInputInserted(notificationEntry, (CharSequence) null, false, (String) null, (Uri) null);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public StatusBarNotification rebuildNotificationWithRemoteInputInserted(NotificationEntry notificationEntry, CharSequence charSequence, boolean z, String str, Uri uri) {
        RemoteInputHistoryItem remoteInputHistoryItem;
        CharSequence charSequence2 = charSequence;
        Uri uri2 = uri;
        StatusBarNotification sbn = notificationEntry.getSbn();
        Notification.Builder recoverBuilder = Notification.Builder.recoverBuilder(this.mContext, sbn.getNotification().clone());
        if (!(charSequence2 == null && uri2 == null)) {
            if (uri2 != null) {
                remoteInputHistoryItem = new RemoteInputHistoryItem(str, uri2, charSequence2);
            } else {
                remoteInputHistoryItem = new RemoteInputHistoryItem(charSequence2);
            }
            Parcelable[] parcelableArray = sbn.getNotification().extras.getParcelableArray("android.remoteInputHistoryItems");
            recoverBuilder.setRemoteInputHistory(parcelableArray != null ? (RemoteInputHistoryItem[]) Stream.concat(Stream.of(remoteInputHistoryItem), Arrays.stream(parcelableArray).map(NotificationRemoteInputManager$$ExternalSyntheticLambda4.INSTANCE)).toArray(NotificationRemoteInputManager$$ExternalSyntheticLambda5.INSTANCE) : new RemoteInputHistoryItem[]{remoteInputHistoryItem});
        }
        recoverBuilder.setShowRemoteInputSpinner(z);
        recoverBuilder.setHideSmartReplies(true);
        Notification build = recoverBuilder.build();
        build.contentView = sbn.getNotification().contentView;
        build.bigContentView = sbn.getNotification().bigContentView;
        build.headsUpContentView = sbn.getNotification().headsUpContentView;
        return new StatusBarNotification(sbn.getPackageName(), sbn.getOpPkg(), sbn.getId(), sbn.getTag(), sbn.getUid(), sbn.getInitialPid(), build, sbn.getUser(), sbn.getOverrideGroupKey(), sbn.getPostTime());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ RemoteInputHistoryItem lambda$rebuildNotificationWithRemoteInputInserted$4(Parcelable parcelable) {
        return (RemoteInputHistoryItem) parcelable;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ RemoteInputHistoryItem[] lambda$rebuildNotificationWithRemoteInputInserted$5(int i) {
        return new RemoteInputHistoryItem[i];
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationRemoteInputManager state:");
        printWriter.print("  mKeysKeptForRemoteInputHistory: ");
        printWriter.println(this.mKeysKeptForRemoteInputHistory);
        printWriter.print("  mEntriesKeptForRemoteInputActive: ");
        printWriter.println(this.mEntriesKeptForRemoteInputActive);
    }

    public void bindRow(ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setRemoteInputController(this.mRemoteInputController);
    }

    public RemoteViews.InteractionHandler getRemoteViewsOnClickHandler() {
        return this.mInteractionHandler;
    }

    @VisibleForTesting
    public Set<NotificationEntry> getEntriesKeptForRemoteInputActive() {
        return this.mEntriesKeptForRemoteInputActive;
    }

    protected abstract class RemoteInputExtender implements NotificationLifetimeExtender {
        protected RemoteInputExtender() {
        }

        public void setCallback(NotificationLifetimeExtender.NotificationSafeToRemoveCallback notificationSafeToRemoveCallback) {
            NotificationRemoteInputManager notificationRemoteInputManager = NotificationRemoteInputManager.this;
            if (notificationRemoteInputManager.mNotificationLifetimeFinishedCallback == null) {
                notificationRemoteInputManager.mNotificationLifetimeFinishedCallback = notificationSafeToRemoveCallback;
            }
        }
    }

    protected class RemoteInputHistoryExtender extends RemoteInputExtender {
        protected RemoteInputHistoryExtender() {
            super();
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.shouldKeepForRemoteInputHistory(notificationEntry);
        }

        public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                CharSequence charSequence = notificationEntry.remoteInputText;
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = notificationEntry.remoteInputTextWhenReset;
                }
                String str = notificationEntry.remoteInputMimeType;
                Uri uri = notificationEntry.remoteInputUri;
                StatusBarNotification rebuildNotificationWithRemoteInputInserted = NotificationRemoteInputManager.this.rebuildNotificationWithRemoteInputInserted(notificationEntry, charSequence, false, str, uri);
                notificationEntry.onRemoteInputInserted();
                if (rebuildNotificationWithRemoteInputInserted != null) {
                    NotificationRemoteInputManager.this.mEntryManager.updateNotification(rebuildNotificationWithRemoteInputInserted, (NotificationListenerService.RankingMap) null);
                    if (!notificationEntry.isRemoved()) {
                        if (Log.isLoggable("NotifRemoteInputManager", 3)) {
                            Log.d("NotifRemoteInputManager", "Keeping notification around after sending remote input " + notificationEntry.getKey());
                        }
                        NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add(notificationEntry.getKey());
                        return;
                    }
                    return;
                }
                return;
            }
            NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove(notificationEntry.getKey());
        }
    }

    protected class SmartReplyHistoryExtender extends RemoteInputExtender {
        protected SmartReplyHistoryExtender() {
            super();
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.shouldKeepForSmartReplyHistory(notificationEntry);
        }

        public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                StatusBarNotification rebuildNotificationForCanceledSmartReplies = NotificationRemoteInputManager.this.rebuildNotificationForCanceledSmartReplies(notificationEntry);
                if (rebuildNotificationForCanceledSmartReplies != null) {
                    NotificationRemoteInputManager.this.mEntryManager.updateNotification(rebuildNotificationForCanceledSmartReplies, (NotificationListenerService.RankingMap) null);
                    if (!notificationEntry.isRemoved()) {
                        if (Log.isLoggable("NotifRemoteInputManager", 3)) {
                            Log.d("NotifRemoteInputManager", "Keeping notification around after sending smart reply " + notificationEntry.getKey());
                        }
                        NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.add(notificationEntry.getKey());
                        return;
                    }
                    return;
                }
                return;
            }
            NotificationRemoteInputManager.this.mKeysKeptForRemoteInputHistory.remove(notificationEntry.getKey());
            NotificationRemoteInputManager.this.mSmartReplyController.stopSending(notificationEntry);
        }
    }

    protected class RemoteInputActiveExtender extends RemoteInputExtender {
        protected RemoteInputActiveExtender() {
            super();
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
            return NotificationRemoteInputManager.this.mRemoteInputController.isRemoteInputActive(notificationEntry);
        }

        public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
            if (z) {
                if (Log.isLoggable("NotifRemoteInputManager", 3)) {
                    Log.d("NotifRemoteInputManager", "Keeping notification around while remote input active " + notificationEntry.getKey());
                }
                NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.add(notificationEntry);
                return;
            }
            NotificationRemoteInputManager.this.mEntriesKeptForRemoteInputActive.remove(notificationEntry);
        }
    }
}
