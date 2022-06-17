package com.android.systemui.privacy;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PrivacyItem.kt */
public final class PrivacyItem {
    @NotNull
    private final PrivacyApplication application;
    @NotNull
    private final String log;
    private final boolean paused;
    @NotNull
    private final PrivacyType privacyType;
    private final long timeStampElapsed;

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PrivacyItem)) {
            return false;
        }
        PrivacyItem privacyItem = (PrivacyItem) obj;
        return this.privacyType == privacyItem.privacyType && Intrinsics.areEqual((Object) this.application, (Object) privacyItem.application) && this.timeStampElapsed == privacyItem.timeStampElapsed && this.paused == privacyItem.paused;
    }

    public int hashCode() {
        int hashCode = ((((this.privacyType.hashCode() * 31) + this.application.hashCode()) * 31) + Long.hashCode(this.timeStampElapsed)) * 31;
        boolean z = this.paused;
        if (z) {
            z = true;
        }
        return hashCode + (z ? 1 : 0);
    }

    @NotNull
    public String toString() {
        return "PrivacyItem(privacyType=" + this.privacyType + ", application=" + this.application + ", timeStampElapsed=" + this.timeStampElapsed + ", paused=" + this.paused + ')';
    }

    public PrivacyItem(@NotNull PrivacyType privacyType2, @NotNull PrivacyApplication privacyApplication, long j, boolean z) {
        Intrinsics.checkNotNullParameter(privacyType2, "privacyType");
        Intrinsics.checkNotNullParameter(privacyApplication, "application");
        this.privacyType = privacyType2;
        this.application = privacyApplication;
        this.timeStampElapsed = j;
        this.paused = z;
        this.log = '(' + privacyType2.getLogName() + ", " + privacyApplication.getPackageName() + '(' + privacyApplication.getUid() + "), " + j + ", paused=" + z + ')';
    }

    @NotNull
    public final PrivacyType getPrivacyType() {
        return this.privacyType;
    }

    @NotNull
    public final PrivacyApplication getApplication() {
        return this.application;
    }

    public final long getTimeStampElapsed() {
        return this.timeStampElapsed;
    }

    public final boolean getPaused() {
        return this.paused;
    }

    @NotNull
    public final String getLog() {
        return this.log;
    }
}
