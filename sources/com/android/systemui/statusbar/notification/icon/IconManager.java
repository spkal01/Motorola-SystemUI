package com.android.systemui.statusbar.notification.icon;

import android.app.Notification;
import android.app.Person;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.widget.ImageView;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.R$id;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import java.util.List;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: IconManager.kt */
public final class IconManager {
    @NotNull
    private final IconManager$entryListener$1 entryListener = new IconManager$entryListener$1(this);
    @NotNull
    private final IconBuilder iconBuilder;
    @NotNull
    private final LauncherApps launcherApps;
    /* access modifiers changed from: private */
    @NotNull
    public final CommonNotifCollection notifCollection;
    /* access modifiers changed from: private */
    @NotNull
    public final NotificationEntry.OnSensitivityChangedListener sensitivityListener = new IconManager$sensitivityListener$1(this);

    public IconManager(@NotNull CommonNotifCollection commonNotifCollection, @NotNull LauncherApps launcherApps2, @NotNull IconBuilder iconBuilder2) {
        Intrinsics.checkNotNullParameter(commonNotifCollection, "notifCollection");
        Intrinsics.checkNotNullParameter(launcherApps2, "launcherApps");
        Intrinsics.checkNotNullParameter(iconBuilder2, "iconBuilder");
        this.notifCollection = commonNotifCollection;
        this.launcherApps = launcherApps2;
        this.iconBuilder = iconBuilder2;
    }

    public final void attach() {
        this.notifCollection.addCollectionListener(this.entryListener);
    }

    public final void createIcons(@NotNull NotificationEntry notificationEntry) throws InflationException {
        StatusBarIconView statusBarIconView;
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        StatusBarIconView createIconView = this.iconBuilder.createIconView(notificationEntry);
        createIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        StatusBarIconView createIconView2 = this.iconBuilder.createIconView(notificationEntry);
        createIconView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        createIconView2.setVisibility(4);
        StatusBarIconView createIconView3 = this.iconBuilder.createIconView(notificationEntry);
        createIconView3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        createIconView3.setIncreasedSize(true);
        if (notificationEntry.getSbn().getNotification().isMediaNotification()) {
            statusBarIconView = this.iconBuilder.createIconView(notificationEntry);
            statusBarIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            statusBarIconView = null;
        }
        Pair<StatusBarIcon, StatusBarIcon> iconDescriptors = getIconDescriptors(notificationEntry);
        StatusBarIcon component1 = iconDescriptors.component1();
        StatusBarIcon component2 = iconDescriptors.component2();
        try {
            setIcon(notificationEntry, component1, createIconView);
            setIcon(notificationEntry, component2, createIconView2);
            setIcon(notificationEntry, component2, createIconView3);
            if (statusBarIconView != null) {
                setIcon(notificationEntry, component1, statusBarIconView);
            }
            notificationEntry.setIcons(IconPack.buildPack(createIconView, createIconView2, createIconView3, statusBarIconView, notificationEntry.getIcons()));
            if (MotoFeature.getExistedInstance().isSupportCli()) {
                StatusBarIconView createIconView4 = this.iconBuilder.createIconView(notificationEntry);
                createIconView4.setIsCarousel(true);
                createIconView4.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                setIcon(notificationEntry, component1, createIconView4);
                notificationEntry.getIcons().setCarouselIcon(createIconView4);
            }
        } catch (InflationException e) {
            notificationEntry.setIcons(IconPack.buildEmptyPack(notificationEntry.getIcons()));
            throw e;
        }
    }

    public final void updateIcons(@NotNull NotificationEntry notificationEntry) throws InflationException {
        StatusBarIconView carouselIcon;
        Intrinsics.checkNotNullParameter(notificationEntry, "entry");
        if (notificationEntry.getIcons().getAreIconsAvailable()) {
            notificationEntry.getIcons().setSmallIconDescriptor((StatusBarIcon) null);
            notificationEntry.getIcons().setPeopleAvatarDescriptor((StatusBarIcon) null);
            Pair<StatusBarIcon, StatusBarIcon> iconDescriptors = getIconDescriptors(notificationEntry);
            StatusBarIcon component1 = iconDescriptors.component1();
            StatusBarIcon component2 = iconDescriptors.component2();
            StatusBarIconView statusBarIcon = notificationEntry.getIcons().getStatusBarIcon();
            if (statusBarIcon != null) {
                statusBarIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, component1, statusBarIcon);
            }
            StatusBarIconView shelfIcon = notificationEntry.getIcons().getShelfIcon();
            if (shelfIcon != null) {
                shelfIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, component1, shelfIcon);
            }
            StatusBarIconView aodIcon = notificationEntry.getIcons().getAodIcon();
            if (aodIcon != null) {
                aodIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, component2, aodIcon);
            }
            StatusBarIconView centeredIcon = notificationEntry.getIcons().getCenteredIcon();
            if (centeredIcon != null) {
                centeredIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, component2, centeredIcon);
            }
            if (MotoFeature.getExistedInstance().isSupportCli() && (carouselIcon = notificationEntry.getIcons().getCarouselIcon()) != null) {
                carouselIcon.setNotification(notificationEntry.getSbn());
                setIcon(notificationEntry, component2, carouselIcon);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void updateIconsSafe(NotificationEntry notificationEntry) {
        try {
            updateIcons(notificationEntry);
        } catch (InflationException e) {
            Log.e("IconManager", "Unable to update icon", e);
        }
    }

    private final Pair<StatusBarIcon, StatusBarIcon> getIconDescriptors(NotificationEntry notificationEntry) throws InflationException {
        StatusBarIcon iconDescriptor = getIconDescriptor(notificationEntry, false);
        return new Pair<>(iconDescriptor, notificationEntry.isSensitive() ? getIconDescriptor(notificationEntry, true) : iconDescriptor);
    }

    private final StatusBarIcon getIconDescriptor(NotificationEntry notificationEntry, boolean z) throws InflationException {
        Icon icon;
        Notification notification = notificationEntry.getSbn().getNotification();
        boolean z2 = isImportantConversation(notificationEntry) && !z;
        StatusBarIcon peopleAvatarDescriptor = notificationEntry.getIcons().getPeopleAvatarDescriptor();
        StatusBarIcon smallIconDescriptor = notificationEntry.getIcons().getSmallIconDescriptor();
        if (z2 && peopleAvatarDescriptor != null) {
            return peopleAvatarDescriptor;
        }
        if (!z2 && smallIconDescriptor != null) {
            return smallIconDescriptor;
        }
        if (z2) {
            icon = createPeopleAvatar(notificationEntry);
        } else {
            icon = notification.getSmallIcon();
        }
        Icon icon2 = icon;
        if (icon2 != null) {
            UserHandle user = notificationEntry.getSbn().getUser();
            String packageName = notificationEntry.getSbn().getPackageName();
            int i = notification.iconLevel;
            int i2 = notification.number;
            IconBuilder iconBuilder2 = this.iconBuilder;
            Intrinsics.checkNotNullExpressionValue(notification, "n");
            StatusBarIcon statusBarIcon = new StatusBarIcon(user, packageName, icon2, i, i2, iconBuilder2.getIconContentDescription(notification));
            if (isImportantConversation(notificationEntry)) {
                if (z2) {
                    notificationEntry.getIcons().setPeopleAvatarDescriptor(statusBarIcon);
                } else {
                    notificationEntry.getIcons().setSmallIconDescriptor(statusBarIcon);
                }
            }
            return statusBarIcon;
        }
        throw new InflationException(Intrinsics.stringPlus("No icon in notification from ", notificationEntry.getSbn().getPackageName()));
    }

    private final void setIcon(NotificationEntry notificationEntry, StatusBarIcon statusBarIcon, StatusBarIconView statusBarIconView) throws InflationException {
        statusBarIconView.setShowsConversation(showsConversation(notificationEntry, statusBarIconView, statusBarIcon));
        statusBarIconView.setTag(R$id.icon_is_pre_L, Boolean.valueOf(notificationEntry.targetSdk < 21));
        if (!statusBarIconView.set(statusBarIcon)) {
            throw new InflationException(Intrinsics.stringPlus("Couldn't create icon ", statusBarIcon));
        }
    }

    private final Icon createPeopleAvatar(NotificationEntry notificationEntry) throws InflationException {
        ShortcutInfo conversationShortcutInfo = notificationEntry.getRanking().getConversationShortcutInfo();
        Icon shortcutIcon = conversationShortcutInfo != null ? this.launcherApps.getShortcutIcon(conversationShortcutInfo) : null;
        if (shortcutIcon == null) {
            Bundle bundle = notificationEntry.getSbn().getNotification().extras;
            Intrinsics.checkNotNullExpressionValue(bundle, "entry.sbn.notification.extras");
            List messagesFromBundleArray = Notification.MessagingStyle.Message.getMessagesFromBundleArray(bundle.getParcelableArray("android.messages"));
            Person person = (Person) bundle.getParcelable("android.messagingUser");
            int size = messagesFromBundleArray.size() - 1;
            if (size >= 0) {
                while (true) {
                    int i = size - 1;
                    Notification.MessagingStyle.Message message = (Notification.MessagingStyle.Message) messagesFromBundleArray.get(size);
                    Person senderPerson = message.getSenderPerson();
                    if (senderPerson != null && senderPerson != person) {
                        Person senderPerson2 = message.getSenderPerson();
                        Intrinsics.checkNotNull(senderPerson2);
                        shortcutIcon = senderPerson2.getIcon();
                        break;
                    } else if (i < 0) {
                        break;
                    } else {
                        size = i;
                    }
                }
            }
        }
        if (shortcutIcon == null) {
            shortcutIcon = notificationEntry.getSbn().getNotification().getLargeIcon();
        }
        if (shortcutIcon == null) {
            shortcutIcon = notificationEntry.getSbn().getNotification().getSmallIcon();
        }
        if (shortcutIcon != null) {
            return shortcutIcon;
        }
        throw new InflationException(Intrinsics.stringPlus("No icon in notification from ", notificationEntry.getSbn().getPackageName()));
    }

    private final boolean showsConversation(NotificationEntry notificationEntry, StatusBarIconView statusBarIconView, StatusBarIcon statusBarIcon) {
        boolean z = statusBarIconView == notificationEntry.getIcons().getShelfIcon() || statusBarIconView == notificationEntry.getIcons().getAodIcon();
        boolean equals = statusBarIcon.icon.equals(notificationEntry.getSbn().getNotification().getSmallIcon());
        if (!isImportantConversation(notificationEntry) || equals) {
            return false;
        }
        if (!z || !notificationEntry.isSensitive()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public final boolean isImportantConversation(NotificationEntry notificationEntry) {
        return notificationEntry.getRanking().getChannel() != null && notificationEntry.getRanking().getChannel().isImportantConversation();
    }
}
