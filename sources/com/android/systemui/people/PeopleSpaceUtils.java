package com.android.systemui.people;

import android.app.Notification;
import android.app.backup.BackupManager;
import android.app.people.ConversationChannel;
import android.app.people.IPeopleManager;
import android.app.people.PeopleSpaceTile;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.UserManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.MessagingMessage;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.R$string;
import com.android.systemui.people.widget.PeopleSpaceWidgetManager;
import com.android.systemui.people.widget.PeopleTileKey;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PeopleSpaceUtils {
    public static final PeopleTileKey EMPTY_KEY = new PeopleTileKey("", -1, "");

    public enum NotificationAction {
        POSTED,
        REMOVED
    }

    public static void setSharedPreferencesStorageForTile(Context context, PeopleTileKey peopleTileKey, int i, Uri uri, BackupManager backupManager) {
        String str;
        if (!PeopleTileKey.isValid(peopleTileKey)) {
            Log.e("PeopleSpaceUtils", "Not storing for invalid key");
            return;
        }
        SharedPreferencesHelper.setPeopleTileKey(context.getSharedPreferences(String.valueOf(i), 0), peopleTileKey);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        if (uri == null) {
            str = "";
        } else {
            str = uri.toString();
        }
        edit.putString(String.valueOf(i), str);
        addAppWidgetIdForKey(defaultSharedPreferences, edit, i, peopleTileKey.toString());
        if (!TextUtils.isEmpty(str)) {
            addAppWidgetIdForKey(defaultSharedPreferences, edit, i, str);
        }
        edit.apply();
        backupManager.dataChanged();
    }

    public static void removeSharedPreferencesStorageForTile(Context context, PeopleTileKey peopleTileKey, int i, String str) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.remove(String.valueOf(i));
        removeAppWidgetIdForKey(defaultSharedPreferences, edit, i, peopleTileKey.toString());
        removeAppWidgetIdForKey(defaultSharedPreferences, edit, i, str);
        edit.apply();
        SharedPreferences.Editor edit2 = context.getSharedPreferences(String.valueOf(i), 0).edit();
        edit2.remove("package_name");
        edit2.remove("user_id");
        edit2.remove("shortcut_id");
        edit2.apply();
    }

    private static void addAppWidgetIdForKey(SharedPreferences sharedPreferences, SharedPreferences.Editor editor, int i, String str) {
        HashSet hashSet = new HashSet(sharedPreferences.getStringSet(str, new HashSet()));
        hashSet.add(String.valueOf(i));
        editor.putStringSet(str, hashSet);
    }

    private static void removeAppWidgetIdForKey(SharedPreferences sharedPreferences, SharedPreferences.Editor editor, int i, String str) {
        HashSet hashSet = new HashSet(sharedPreferences.getStringSet(str, new HashSet()));
        hashSet.remove(String.valueOf(i));
        editor.putStringSet(str, hashSet);
    }

    public static List<NotificationEntry> getNotificationsByUri(PackageManager packageManager, String str, Map<PeopleTileKey, Set<NotificationEntry>> map) {
        if (TextUtils.isEmpty(str)) {
            return new ArrayList();
        }
        return (List) map.entrySet().stream().flatMap(PeopleSpaceUtils$$ExternalSyntheticLambda4.INSTANCE).filter(new PeopleSpaceUtils$$ExternalSyntheticLambda5(packageManager, str)).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getNotificationsByUri$1(PackageManager packageManager, String str, NotificationEntry notificationEntry) {
        return NotificationHelper.hasReadContactsPermission(packageManager, notificationEntry.getSbn()) && NotificationHelper.shouldMatchNotificationByUri(notificationEntry.getSbn()) && Objects.equals(str, NotificationHelper.getContactUri(notificationEntry.getSbn()));
    }

    public static int getMessagesCount(Set<NotificationEntry> set) {
        List<Notification.MessagingStyle.Message> messagingStyleMessages;
        int i = 0;
        for (NotificationEntry sbn : set) {
            Notification notification = sbn.getSbn().getNotification();
            if (!NotificationHelper.isMissedCall(notification) && (messagingStyleMessages = NotificationHelper.getMessagingStyleMessages(notification)) != null) {
                i += messagingStyleMessages.size();
            }
        }
        return i;
    }

    public static PeopleSpaceTile removeNotificationFields(PeopleSpaceTile peopleSpaceTile) {
        PeopleSpaceTile.Builder notificationCategory = peopleSpaceTile.toBuilder().setNotificationKey((String) null).setNotificationContent((CharSequence) null).setNotificationSender((CharSequence) null).setNotificationDataUri((Uri) null).setMessagesCount(0).setNotificationCategory((String) null);
        if (!TextUtils.isEmpty(peopleSpaceTile.getNotificationKey())) {
            notificationCategory.setLastInteractionTimestamp(System.currentTimeMillis());
        }
        return notificationCategory.build();
    }

    public static PeopleSpaceTile augmentTileFromNotification(Context context, PeopleSpaceTile peopleSpaceTile, PeopleTileKey peopleTileKey, NotificationEntry notificationEntry, int i, Optional<Integer> optional, BackupManager backupManager) {
        if (notificationEntry == null || notificationEntry.getSbn().getNotification() == null) {
            return removeNotificationFields(peopleSpaceTile);
        }
        StatusBarNotification sbn = notificationEntry.getSbn();
        Notification notification = sbn.getNotification();
        PeopleSpaceTile.Builder builder = peopleSpaceTile.toBuilder();
        String contactUri = NotificationHelper.getContactUri(sbn);
        if (optional.isPresent() && peopleSpaceTile.getContactUri() == null && !TextUtils.isEmpty(contactUri)) {
            Uri parse = Uri.parse(contactUri);
            setSharedPreferencesStorageForTile(context, new PeopleTileKey(peopleSpaceTile), optional.get().intValue(), parse, backupManager);
            builder.setContactUri(parse);
        }
        boolean isMissedCall = NotificationHelper.isMissedCall(notification);
        List<Notification.MessagingStyle.Message> messagingStyleMessages = NotificationHelper.getMessagingStyleMessages(notification);
        if (!isMissedCall && ArrayUtils.isEmpty(messagingStyleMessages)) {
            return removeNotificationFields(builder.build());
        }
        boolean z = false;
        Uri uri = null;
        Notification.MessagingStyle.Message message = messagingStyleMessages != null ? messagingStyleMessages.get(0) : null;
        if (message != null && !TextUtils.isEmpty(message.getText())) {
            z = true;
        }
        CharSequence text = (!isMissedCall || z) ? message.getText() : context.getString(R$string.missed_call);
        if (message != null && MessagingMessage.hasImage(message)) {
            uri = message.getDataUri();
        }
        return builder.setLastInteractionTimestamp(sbn.getPostTime()).setNotificationKey(sbn.getKey()).setNotificationCategory(notification.category).setNotificationContent(text).setNotificationSender(NotificationHelper.getSenderIfGroupConversation(notification, message)).setNotificationDataUri(uri).setMessagesCount(i).build();
    }

    public static List<PeopleSpaceTile> getSortedTiles(IPeopleManager iPeopleManager, LauncherApps launcherApps, UserManager userManager, Stream<ShortcutInfo> stream) {
        return (List) stream.filter(PeopleSpaceUtils$$ExternalSyntheticLambda8.INSTANCE).filter(new PeopleSpaceUtils$$ExternalSyntheticLambda6(userManager)).map(new PeopleSpaceUtils$$ExternalSyntheticLambda3(launcherApps)).filter(PeopleSpaceUtils$$ExternalSyntheticLambda7.INSTANCE).map(new PeopleSpaceUtils$$ExternalSyntheticLambda2(iPeopleManager)).sorted(PeopleSpaceUtils$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSortedTiles$2(UserManager userManager, ShortcutInfo shortcutInfo) {
        return !userManager.isQuietModeEnabled(shortcutInfo.getUserHandle());
    }

    public static PeopleSpaceTile getTile(ConversationChannel conversationChannel, LauncherApps launcherApps) {
        if (conversationChannel == null) {
            Log.i("PeopleSpaceUtils", "ConversationChannel is null");
            return null;
        }
        PeopleSpaceTile build = new PeopleSpaceTile.Builder(conversationChannel, launcherApps).build();
        if (shouldKeepConversation(build)) {
            return build;
        }
        Log.i("PeopleSpaceUtils", "PeopleSpaceTile is not valid");
        return null;
    }

    private static Long getLastInteraction(IPeopleManager iPeopleManager, PeopleSpaceTile peopleSpaceTile) {
        try {
            return Long.valueOf(iPeopleManager.getLastInteraction(peopleSpaceTile.getPackageName(), getUserId(peopleSpaceTile), peopleSpaceTile.getId()));
        } catch (Exception e) {
            Log.e("PeopleSpaceUtils", "Couldn't retrieve last interaction time", e);
            return 0L;
        }
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean shouldKeepConversation(PeopleSpaceTile peopleSpaceTile) {
        return peopleSpaceTile != null && !TextUtils.isEmpty(peopleSpaceTile.getUserName());
    }

    private static boolean hasBirthdayStatus(PeopleSpaceTile peopleSpaceTile, Context context) {
        return peopleSpaceTile.getBirthdayText() != null && peopleSpaceTile.getBirthdayText().equals(context.getString(R$string.birthday_status));
    }

    public static void getDataFromContactsOnBackgroundThread(Context context, PeopleSpaceWidgetManager peopleSpaceWidgetManager, Map<Integer, PeopleSpaceTile> map, int[] iArr) {
        ThreadUtils.postOnBackgroundThread(new PeopleSpaceUtils$$ExternalSyntheticLambda0(context, peopleSpaceWidgetManager, map, iArr));
    }

    @VisibleForTesting
    public static void getDataFromContacts(Context context, PeopleSpaceWidgetManager peopleSpaceWidgetManager, Map<Integer, PeopleSpaceTile> map, int[] iArr) {
        if (iArr.length != 0) {
            List<String> contactLookupKeysWithBirthdaysToday = getContactLookupKeysWithBirthdaysToday(context);
            for (int i : iArr) {
                PeopleSpaceTile peopleSpaceTile = map.get(Integer.valueOf(i));
                if (peopleSpaceTile == null || peopleSpaceTile.getContactUri() == null) {
                    updateTileContactFields(peopleSpaceWidgetManager, context, peopleSpaceTile, i, 0.0f, (String) null);
                } else {
                    updateTileWithBirthdayAndUpdateAffinity(context, peopleSpaceWidgetManager, contactLookupKeysWithBirthdaysToday, peopleSpaceTile, i);
                }
            }
        }
    }

    private static void updateTileContactFields(PeopleSpaceWidgetManager peopleSpaceWidgetManager, Context context, PeopleSpaceTile peopleSpaceTile, int i, float f, String str) {
        boolean z = true;
        boolean z2 = hasBirthdayStatus(peopleSpaceTile, context) && str == null;
        boolean z3 = !hasBirthdayStatus(peopleSpaceTile, context) && str != null;
        if (peopleSpaceTile.getContactAffinity() == f && !z2 && !z3) {
            z = false;
        }
        if (z) {
            peopleSpaceWidgetManager.lambda$addNewWidget$5(i, peopleSpaceTile.toBuilder().setBirthdayText(str).setContactAffinity(f).build());
        }
    }

    private static void updateTileWithBirthdayAndUpdateAffinity(Context context, PeopleSpaceWidgetManager peopleSpaceWidgetManager, List<String> list, PeopleSpaceTile peopleSpaceTile, int i) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(peopleSpaceTile.getContactUri(), (String[]) null, (String) null, (String[]) null, (String) null);
            while (cursor != null && cursor.moveToNext()) {
                String string = cursor.getString(cursor.getColumnIndex("lookup"));
                float contactAffinity = getContactAffinity(cursor);
                if (string.isEmpty() || !list.contains(string)) {
                    updateTileContactFields(peopleSpaceWidgetManager, context, peopleSpaceTile, i, contactAffinity, (String) null);
                } else {
                    updateTileContactFields(peopleSpaceWidgetManager, context, peopleSpaceTile, i, contactAffinity, context.getString(R$string.birthday_status));
                }
            }
            if (cursor == null) {
                return;
            }
        } catch (SQLException e) {
            Log.e("PeopleSpaceUtils", "Failed to query contact: " + e);
            if (cursor == null) {
                return;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        cursor.close();
    }

    private static float getContactAffinity(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex("starred");
        if (columnIndex < 0) {
            return 0.5f;
        }
        if (cursor.getInt(columnIndex) != 0) {
            return Math.max(0.5f, 1.0f);
        }
        return 0.5f;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x006d, code lost:
        if (r1 == null) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0070, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x004e, code lost:
        if (r1 != null) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0050, code lost:
        r1.close();
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.String> getContactLookupKeysWithBirthdaysToday(android.content.Context r11) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 1
            r0.<init>(r1)
            java.text.SimpleDateFormat r2 = new java.text.SimpleDateFormat
            java.lang.String r3 = "MM-dd"
            r2.<init>(r3)
            java.util.Date r3 = new java.util.Date
            r3.<init>()
            java.lang.String r2 = r2.format(r3)
            java.lang.String r3 = "lookup"
            java.lang.String r4 = "data1"
            java.lang.String[] r7 = new java.lang.String[]{r3, r4}
            java.lang.String r8 = "mimetype= ? AND data2=3 AND (substr(data1,6) = ? OR substr(data1,3) = ? )"
            r4 = 3
            java.lang.String[] r9 = new java.lang.String[r4]
            r4 = 0
            java.lang.String r5 = "vnd.android.cursor.item/contact_event"
            r9[r4] = r5
            r9[r1] = r2
            r1 = 2
            r9[r1] = r2
            r1 = 0
            android.content.ContentResolver r5 = r11.getContentResolver()     // Catch:{ SQLException -> 0x0056 }
            android.net.Uri r6 = android.provider.ContactsContract.Data.CONTENT_URI     // Catch:{ SQLException -> 0x0056 }
            r10 = 0
            android.database.Cursor r1 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ SQLException -> 0x0056 }
        L_0x003a:
            if (r1 == 0) goto L_0x004e
            boolean r11 = r1.moveToNext()     // Catch:{ SQLException -> 0x0056 }
            if (r11 == 0) goto L_0x004e
            int r11 = r1.getColumnIndex(r3)     // Catch:{ SQLException -> 0x0056 }
            java.lang.String r11 = r1.getString(r11)     // Catch:{ SQLException -> 0x0056 }
            r0.add(r11)     // Catch:{ SQLException -> 0x0056 }
            goto L_0x003a
        L_0x004e:
            if (r1 == 0) goto L_0x0070
        L_0x0050:
            r1.close()
            goto L_0x0070
        L_0x0054:
            r11 = move-exception
            goto L_0x0071
        L_0x0056:
            r11 = move-exception
            java.lang.String r2 = "PeopleSpaceUtils"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0054 }
            r3.<init>()     // Catch:{ all -> 0x0054 }
            java.lang.String r4 = "Failed to query birthdays: "
            r3.append(r4)     // Catch:{ all -> 0x0054 }
            r3.append(r11)     // Catch:{ all -> 0x0054 }
            java.lang.String r11 = r3.toString()     // Catch:{ all -> 0x0054 }
            android.util.Log.e(r2, r11)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0070
            goto L_0x0050
        L_0x0070:
            return r0
        L_0x0071:
            if (r1 == 0) goto L_0x0076
            r1.close()
        L_0x0076:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.people.PeopleSpaceUtils.getContactLookupKeysWithBirthdaysToday(android.content.Context):java.util.List");
    }

    public static int getUserId(PeopleSpaceTile peopleSpaceTile) {
        return peopleSpaceTile.getUserHandle().getIdentifier();
    }

    public enum PeopleSpaceWidgetEvent implements UiEventLogger.UiEventEnum {
        PEOPLE_SPACE_WIDGET_DELETED(666),
        PEOPLE_SPACE_WIDGET_ADDED(667),
        PEOPLE_SPACE_WIDGET_CLICKED(668);
        
        private final int mId;

        private PeopleSpaceWidgetEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }
}
