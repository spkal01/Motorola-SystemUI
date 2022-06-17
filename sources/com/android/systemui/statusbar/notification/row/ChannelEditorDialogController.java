package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.RestrictedLockUtils;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialog;
import com.android.systemui.statusbar.notification.row.NotificationInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ChannelEditorDialogController.kt */
public final class ChannelEditorDialogController {
    @Nullable
    private Drawable appIcon;
    @Nullable
    private String appName;
    @Nullable
    private Boolean appNotificationsCurrentlyEnabled;
    private boolean appNotificationsEnabled = true;
    @Nullable
    private Integer appUid;
    @Nullable
    private TextView applyButton;
    @NotNull
    private final List<NotificationChannelGroup> channelGroupList = new ArrayList();
    @NotNull
    private final Context context;
    private ChannelEditorDialog dialog;
    @NotNull
    private final ChannelEditorDialog.Builder dialogBuilder;
    @NotNull
    private final Map<NotificationChannel, Integer> edits = new LinkedHashMap();
    @NotNull
    private final HashMap<String, CharSequence> groupNameLookup = new HashMap<>();
    private boolean isSystemApp;
    @NotNull
    private final INotificationManager noMan;
    @Nullable
    private OnChannelEditorDialogFinishedListener onFinishListener;
    @Nullable
    private NotificationInfo.OnSettingsClickListener onSettingsClickListener;
    @Nullable
    private String packageName;
    @NotNull
    private final List<NotificationChannel> paddedChannels = new ArrayList();
    private boolean prepared;
    /* access modifiers changed from: private */
    @NotNull
    public final List<NotificationChannel> providedChannels = new ArrayList();
    @Nullable
    private RestrictedLockUtils.EnforcedAdmin suspendedAppsAdmin;
    private final int wmFlags = -2130444288;

    @VisibleForTesting
    /* renamed from: getGroupNameLookup$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m65xe31ae654() {
    }

    @VisibleForTesting
    /* renamed from: getPaddedChannels$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m66x59c1b43c() {
    }

    public ChannelEditorDialogController(@NotNull Context context2, @NotNull INotificationManager iNotificationManager, @NotNull ChannelEditorDialog.Builder builder) {
        Intrinsics.checkNotNullParameter(context2, "c");
        Intrinsics.checkNotNullParameter(iNotificationManager, "noMan");
        Intrinsics.checkNotNullParameter(builder, "dialogBuilder");
        this.noMan = iNotificationManager;
        this.dialogBuilder = builder;
        Context applicationContext = context2.getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "c.applicationContext");
        this.context = applicationContext;
    }

    @Nullable
    public final OnChannelEditorDialogFinishedListener getOnFinishListener() {
        return this.onFinishListener;
    }

    public final void setOnFinishListener(@Nullable OnChannelEditorDialogFinishedListener onChannelEditorDialogFinishedListener) {
        this.onFinishListener = onChannelEditorDialogFinishedListener;
    }

    @NotNull
    /* renamed from: getPaddedChannels$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final List<NotificationChannel> mo19961x87e1b1bc() {
        return this.paddedChannels;
    }

    @NotNull
    /* renamed from: getGroupNameLookup$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final HashMap<String, CharSequence> mo19959x8a347d4() {
        return this.groupNameLookup;
    }

    public final void prepareDialogForApp(@NotNull String str, @NotNull String str2, int i, @NotNull Set<NotificationChannel> set, @NotNull Drawable drawable, @Nullable NotificationInfo.OnSettingsClickListener onSettingsClickListener2) {
        Intrinsics.checkNotNullParameter(str, "appName");
        Intrinsics.checkNotNullParameter(str2, "packageName");
        Intrinsics.checkNotNullParameter(set, "channels");
        Intrinsics.checkNotNullParameter(drawable, "appIcon");
        this.appName = str;
        this.packageName = str2;
        this.appUid = Integer.valueOf(i);
        this.appIcon = drawable;
        boolean checkAreAppNotificationsOn = checkAreAppNotificationsOn();
        this.appNotificationsEnabled = checkAreAppNotificationsOn;
        this.onSettingsClickListener = onSettingsClickListener2;
        this.appNotificationsCurrentlyEnabled = Boolean.valueOf(checkAreAppNotificationsOn);
        this.channelGroupList.clear();
        this.channelGroupList.addAll(fetchNotificationChannelGroups());
        buildGroupNameLookup();
        this.providedChannels.clear();
        this.providedChannels.addAll(set);
        padToFourChannels(set);
        initDialog();
        this.prepared = true;
    }

    public final void prepareDialogForApp(@NotNull String str, @NotNull String str2, int i, @NotNull Set<NotificationChannel> set, @NotNull Drawable drawable, boolean z, @Nullable RestrictedLockUtils.EnforcedAdmin enforcedAdmin, @Nullable NotificationInfo.OnSettingsClickListener onSettingsClickListener2) {
        Intrinsics.checkNotNullParameter(str, "appName");
        Intrinsics.checkNotNullParameter(str2, "packageName");
        Intrinsics.checkNotNullParameter(set, "channels");
        Intrinsics.checkNotNullParameter(drawable, "appIcon");
        this.appName = str;
        this.packageName = str2;
        this.appUid = Integer.valueOf(i);
        this.appIcon = drawable;
        this.appNotificationsEnabled = checkAreAppNotificationsOn();
        this.onSettingsClickListener = onSettingsClickListener2;
        this.isSystemApp = z;
        this.suspendedAppsAdmin = enforcedAdmin;
        this.channelGroupList.clear();
        this.channelGroupList.addAll(fetchNotificationChannelGroups());
        buildGroupNameLookup();
        padToFourChannels(set);
        initDialog();
        this.prepared = true;
    }

    public final void setApplyVisible(boolean z) {
        TextView textView = this.applyButton;
        if (textView == null) {
            return;
        }
        if (z) {
            if (textView != null) {
                textView.setVisibility(0);
            }
        } else if (textView != null) {
            textView.setVisibility(8);
        }
    }

    private final void buildGroupNameLookup() {
        for (NotificationChannelGroup notificationChannelGroup : this.channelGroupList) {
            if (notificationChannelGroup.getId() != null) {
                HashMap<String, CharSequence> groupNameLookup$frameworks__base__packages__SystemUI__android_common__SystemUI_core = mo19959x8a347d4();
                String id = notificationChannelGroup.getId();
                Intrinsics.checkNotNullExpressionValue(id, "group.id");
                CharSequence name = notificationChannelGroup.getName();
                Intrinsics.checkNotNullExpressionValue(name, "group.name");
                groupNameLookup$frameworks__base__packages__SystemUI__android_common__SystemUI_core.put(id, name);
            }
        }
    }

    private final void padToFourChannels(Set<NotificationChannel> set) {
        this.paddedChannels.clear();
        boolean unused = CollectionsKt__MutableCollectionsKt.addAll(this.paddedChannels, SequencesKt___SequencesKt.take(CollectionsKt___CollectionsKt.asSequence(set), 4));
        boolean unused2 = CollectionsKt__MutableCollectionsKt.addAll(this.paddedChannels, SequencesKt___SequencesKt.take(SequencesKt___SequencesKt.distinct(SequencesKt___SequencesKt.filterNot(getDisplayableChannels(CollectionsKt___CollectionsKt.asSequence(this.channelGroupList)), new ChannelEditorDialogController$padToFourChannels$1(this))), 4 - this.paddedChannels.size()));
        if (this.paddedChannels.size() == 1 && Intrinsics.areEqual((Object) "miscellaneous", (Object) this.paddedChannels.get(0).getId())) {
            this.paddedChannels.clear();
        }
    }

    private final Sequence<NotificationChannel> getDisplayableChannels(Sequence<NotificationChannelGroup> sequence) {
        return SequencesKt___SequencesKt.sortedWith(SequencesKt___SequencesKt.flatMap(sequence, ChannelEditorDialogController$getDisplayableChannels$channels$1.INSTANCE), new C1603x78bbb58a());
    }

    public final void show() {
        if (this.prepared) {
            ChannelEditorDialog channelEditorDialog = this.dialog;
            if (channelEditorDialog != null) {
                channelEditorDialog.show();
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("dialog");
                throw null;
            }
        } else {
            throw new IllegalStateException("Must call prepareDialogForApp() before calling show()");
        }
    }

    public final void close() {
        done();
    }

    /* access modifiers changed from: private */
    public final void done() {
        resetState();
        ChannelEditorDialog channelEditorDialog = this.dialog;
        if (channelEditorDialog != null) {
            channelEditorDialog.dismiss();
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
            throw null;
        }
    }

    private final void resetState() {
        this.appIcon = null;
        this.appUid = null;
        this.packageName = null;
        this.appName = null;
        this.appNotificationsCurrentlyEnabled = null;
        this.edits.clear();
        this.paddedChannels.clear();
        this.providedChannels.clear();
        this.groupNameLookup.clear();
    }

    @NotNull
    public final CharSequence groupNameForId(@Nullable String str) {
        CharSequence charSequence = this.groupNameLookup.get(str);
        return charSequence == null ? "" : charSequence;
    }

    public final void proposeEditForChannel(@NotNull NotificationChannel notificationChannel, int i) {
        Intrinsics.checkNotNullParameter(notificationChannel, "channel");
        if (notificationChannel.getImportance() == i) {
            this.edits.remove(notificationChannel);
        } else {
            this.edits.put(notificationChannel, Integer.valueOf(i));
        }
        ChannelEditorDialog channelEditorDialog = this.dialog;
        if (channelEditorDialog != null) {
            channelEditorDialog.updateDoneButtonText(hasChanges());
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
            throw null;
        }
    }

    public final void proposeSetAppNotificationsEnabled(boolean z) {
        this.appNotificationsEnabled = z;
        ChannelEditorDialog channelEditorDialog = this.dialog;
        if (channelEditorDialog != null) {
            channelEditorDialog.updateDoneButtonText(hasChanges());
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
            throw null;
        }
    }

    public final boolean areAppNotificationsEnabled() {
        return this.appNotificationsEnabled;
    }

    private final boolean hasChanges() {
        return (this.edits.isEmpty() ^ true) || !Intrinsics.areEqual((Object) Boolean.valueOf(this.appNotificationsEnabled), (Object) this.appNotificationsCurrentlyEnabled);
    }

    private final List<NotificationChannelGroup> fetchNotificationChannelGroups() {
        try {
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            Intrinsics.checkNotNull(str);
            Integer num = this.appUid;
            Intrinsics.checkNotNull(num);
            List<NotificationChannelGroup> list = iNotificationManager.getNotificationChannelGroupsForPackage(str, num.intValue(), false).getList();
            if (!(list instanceof List)) {
                list = null;
            }
            if (list == null) {
                return CollectionsKt__CollectionsKt.emptyList();
            }
            return list;
        } catch (Exception e) {
            Log.e("ChannelDialogController", "Error fetching channel groups", e);
            return CollectionsKt__CollectionsKt.emptyList();
        }
    }

    private final boolean checkAreAppNotificationsOn() {
        try {
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            Intrinsics.checkNotNull(str);
            Integer num = this.appUid;
            Intrinsics.checkNotNull(num);
            return iNotificationManager.areNotificationsEnabledForPackage(str, num.intValue());
        } catch (Exception e) {
            Log.e("ChannelDialogController", "Error calling NoMan", e);
            return false;
        }
    }

    private final void applyAppNotificationsOn(boolean z) {
        try {
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            Intrinsics.checkNotNull(str);
            Integer num = this.appUid;
            Intrinsics.checkNotNull(num);
            iNotificationManager.setNotificationsEnabledForPackage(str, num.intValue(), z);
        } catch (Exception e) {
            Log.e("ChannelDialogController", "Error calling NoMan", e);
        }
    }

    private final void setChannelImportance(NotificationChannel notificationChannel, int i) {
        try {
            notificationChannel.setImportance(i);
            INotificationManager iNotificationManager = this.noMan;
            String str = this.packageName;
            Intrinsics.checkNotNull(str);
            Integer num = this.appUid;
            Intrinsics.checkNotNull(num);
            iNotificationManager.updateNotificationChannelForPackage(str, num.intValue(), notificationChannel);
        } catch (Exception e) {
            Log.e("ChannelDialogController", "Unable to update notification importance", e);
        }
    }

    @VisibleForTesting
    public final void apply() {
        for (Map.Entry next : this.edits.entrySet()) {
            NotificationChannel notificationChannel = (NotificationChannel) next.getKey();
            int intValue = ((Number) next.getValue()).intValue();
            if (notificationChannel.getImportance() != intValue) {
                setChannelImportance(notificationChannel, intValue);
            }
        }
        if (!Intrinsics.areEqual((Object) Boolean.valueOf(this.appNotificationsEnabled), (Object) this.appNotificationsCurrentlyEnabled)) {
            applyAppNotificationsOn(this.appNotificationsEnabled);
        }
    }

    @VisibleForTesting
    public final void launchSettings(@NotNull View view) {
        Intrinsics.checkNotNullParameter(view, "sender");
        NotificationInfo.OnSettingsClickListener onSettingsClickListener2 = this.onSettingsClickListener;
        if (onSettingsClickListener2 != null) {
            Integer num = this.appUid;
            Intrinsics.checkNotNull(num);
            onSettingsClickListener2.onClick(view, (NotificationChannel) null, num.intValue());
        }
    }

    private final void initDialog() {
        this.dialogBuilder.setContext(this.context);
        ChannelEditorDialog build = this.dialogBuilder.build();
        this.dialog = build;
        if (build != null) {
            Window window = build.getWindow();
            if (window != null) {
                window.requestFeature(1);
            }
            ChannelEditorDialog channelEditorDialog = this.dialog;
            if (channelEditorDialog != null) {
                channelEditorDialog.setTitle(" ");
                ChannelEditorDialog channelEditorDialog2 = this.dialog;
                if (channelEditorDialog2 != null) {
                    channelEditorDialog2.setContentView(R$layout.notif_half_shelf);
                    channelEditorDialog2.setCanceledOnTouchOutside(true);
                    channelEditorDialog2.setOnDismissListener(new ChannelEditorDialogController$initDialog$1$1(this));
                    this.applyButton = (TextView) channelEditorDialog2.findViewById(R$id.done_button);
                    ChannelEditorListView channelEditorListView = (ChannelEditorListView) channelEditorDialog2.findViewById(R$id.half_shelf_container);
                    if (channelEditorListView != null) {
                        channelEditorListView.setController(this);
                        channelEditorListView.setAppIcon(this.appIcon);
                        channelEditorListView.setAppName(this.appName);
                        channelEditorListView.setSystemApp(this.isSystemApp);
                        channelEditorListView.setSuspendedAppsAdmin(channelEditorListView.getSuspendedAppsAdmin());
                        channelEditorListView.setChannelGroups(this.channelGroupList);
                        channelEditorListView.setChannels(mo19961x87e1b1bc());
                    }
                    channelEditorDialog2.setOnShowListener(new ChannelEditorDialogController$initDialog$1$3(this, channelEditorListView));
                    TextView textView = this.applyButton;
                    if (textView != null) {
                        textView.setOnClickListener(new ChannelEditorDialogController$initDialog$1$4(this));
                    }
                    TextView textView2 = (TextView) channelEditorDialog2.findViewById(R$id.see_more_button);
                    if (textView2 != null) {
                        textView2.setOnClickListener(new ChannelEditorDialogController$initDialog$1$5(this));
                    }
                    Window window2 = channelEditorDialog2.getWindow();
                    if (window2 != null) {
                        window2.setBackgroundDrawable(new ColorDrawable(0));
                        window2.addFlags(this.wmFlags);
                        window2.setType(2017);
                        window2.setWindowAnimations(16973910);
                        WindowManager.LayoutParams attributes = window2.getAttributes();
                        attributes.format = -3;
                        attributes.setTitle(ChannelEditorDialogController.class.getSimpleName());
                        attributes.gravity = 81;
                        attributes.setFitInsetsTypes(window2.getAttributes().getFitInsetsTypes() & (~WindowInsets.Type.statusBars()));
                        attributes.width = -1;
                        attributes.height = -2;
                        Unit unit = Unit.INSTANCE;
                        window2.setAttributes(attributes);
                        return;
                    }
                    return;
                }
                Intrinsics.throwUninitializedPropertyAccessException("dialog");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("dialog");
        throw null;
    }

    public final int isInEditList(@NotNull NotificationChannel notificationChannel) {
        Intrinsics.checkNotNullParameter(notificationChannel, "channel");
        Integer num = this.edits.get(notificationChannel);
        if (num == null) {
            return -1000;
        }
        return num.intValue();
    }

    public final void updateDialog() {
        ChannelEditorDialog channelEditorDialog = this.dialog;
        if (channelEditorDialog != null) {
            channelEditorDialog.dismiss();
            initDialog();
            ChannelEditorDialog channelEditorDialog2 = this.dialog;
            if (channelEditorDialog2 != null) {
                channelEditorDialog2.show();
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("dialog");
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("dialog");
            throw null;
        }
    }
}
