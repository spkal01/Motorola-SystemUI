package com.android.systemui.privacy.logging;

import android.permission.PermGroupUsage;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.privacy.PrivacyDialog;
import com.android.systemui.privacy.PrivacyItem;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PrivacyLogger.kt */
public final class PrivacyLogger {
    /* access modifiers changed from: private */
    @NotNull
    public final LogBuffer buffer;

    public PrivacyLogger(@NotNull LogBuffer logBuffer) {
        Intrinsics.checkNotNullParameter(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logUpdatedItemFromAppOps(int i, int i2, @NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "packageName");
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logUpdatedItemFromAppOps$2 privacyLogger$logUpdatedItemFromAppOps$2 = PrivacyLogger$logUpdatedItemFromAppOps$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logUpdatedItemFromAppOps$2);
            obtain.setInt1(i);
            obtain.setInt2(i2);
            obtain.setStr1(str);
            obtain.setBool1(z);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logRetrievedPrivacyItemsList(@NotNull List<PrivacyItem> list) {
        Intrinsics.checkNotNullParameter(list, "list");
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logRetrievedPrivacyItemsList$2 privacyLogger$logRetrievedPrivacyItemsList$2 = PrivacyLogger$logRetrievedPrivacyItemsList$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logRetrievedPrivacyItemsList$2);
            obtain.setStr1(listToString(list));
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logPrivacyItemsToHold(@NotNull List<PrivacyItem> list) {
        Intrinsics.checkNotNullParameter(list, "list");
        LogLevel logLevel = LogLevel.DEBUG;
        PrivacyLogger$logPrivacyItemsToHold$2 privacyLogger$logPrivacyItemsToHold$2 = PrivacyLogger$logPrivacyItemsToHold$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logPrivacyItemsToHold$2);
            obtain.setStr1(listToString(list));
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logPrivacyItemsUpdateScheduled(long j) {
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logPrivacyItemsUpdateScheduled$2 privacyLogger$logPrivacyItemsUpdateScheduled$2 = PrivacyLogger$logPrivacyItemsUpdateScheduled$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logPrivacyItemsUpdateScheduled$2);
            obtain.setStr1(PrivacyLoggerKt.DATE_FORMAT.format(Long.valueOf(System.currentTimeMillis() + j)));
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logCurrentProfilesChanged(@NotNull List<Integer> list) {
        Intrinsics.checkNotNullParameter(list, "profiles");
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logCurrentProfilesChanged$2 privacyLogger$logCurrentProfilesChanged$2 = PrivacyLogger$logCurrentProfilesChanged$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logCurrentProfilesChanged$2);
            obtain.setStr1(list.toString());
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logChipVisible(boolean z) {
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logChipVisible$2 privacyLogger$logChipVisible$2 = PrivacyLogger$logChipVisible$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logChipVisible$2);
            obtain.setBool1(z);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logStatusBarIconsVisible(boolean z, boolean z2, boolean z3) {
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logStatusBarIconsVisible$2 privacyLogger$logStatusBarIconsVisible$2 = PrivacyLogger$logStatusBarIconsVisible$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logStatusBarIconsVisible$2);
            obtain.setBool1(z);
            obtain.setBool2(z2);
            obtain.setBool3(z3);
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logUnfilteredPermGroupUsage(@NotNull List<PermGroupUsage> list) {
        Intrinsics.checkNotNullParameter(list, "contents");
        LogLevel logLevel = LogLevel.DEBUG;
        PrivacyLogger$logUnfilteredPermGroupUsage$2 privacyLogger$logUnfilteredPermGroupUsage$2 = PrivacyLogger$logUnfilteredPermGroupUsage$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logUnfilteredPermGroupUsage$2);
            obtain.setStr1(list.toString());
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logShowDialogContents(@NotNull List<PrivacyDialog.PrivacyElement> list) {
        Intrinsics.checkNotNullParameter(list, "contents");
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logShowDialogContents$2 privacyLogger$logShowDialogContents$2 = PrivacyLogger$logShowDialogContents$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logShowDialogContents$2);
            obtain.setStr1(list.toString());
            access$getBuffer$p.push(obtain);
        }
    }

    public final void logPrivacyDialogDismissed() {
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logPrivacyDialogDismissed$2 privacyLogger$logPrivacyDialogDismissed$2 = PrivacyLogger$logPrivacyDialogDismissed$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            access$getBuffer$p.push(access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logPrivacyDialogDismissed$2));
        }
    }

    public final void logStartSettingsActivityFromDialog(@NotNull String str, int i) {
        Intrinsics.checkNotNullParameter(str, "packageName");
        LogLevel logLevel = LogLevel.INFO;
        PrivacyLogger$logStartSettingsActivityFromDialog$2 privacyLogger$logStartSettingsActivityFromDialog$2 = PrivacyLogger$logStartSettingsActivityFromDialog$2.INSTANCE;
        LogBuffer access$getBuffer$p = this.buffer;
        if (!access$getBuffer$p.getFrozen()) {
            LogMessageImpl obtain = access$getBuffer$p.obtain("PrivacyLog", logLevel, privacyLogger$logStartSettingsActivityFromDialog$2);
            obtain.setStr1(str);
            obtain.setInt1(i);
            access$getBuffer$p.push(obtain);
        }
    }

    private final String listToString(List<PrivacyItem> list) {
        return CollectionsKt___CollectionsKt.joinToString$default(list, ", ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, PrivacyLogger$listToString$1.INSTANCE, 30, (Object) null);
    }
}
