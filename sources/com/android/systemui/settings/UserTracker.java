package com.android.systemui.settings;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: UserTracker.kt */
public interface UserTracker extends UserContextProvider {

    /* compiled from: UserTracker.kt */
    public interface Callback {
        void onProfilesChanged(@NotNull List<? extends UserInfo> list) {
            Intrinsics.checkNotNullParameter(list, "profiles");
        }

        void onUserChanged(int i, @NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "userContext");
        }
    }

    void addCallback(@NotNull Callback callback, @NotNull Executor executor);

    @NotNull
    UserHandle getUserHandle();

    int getUserId();

    @NotNull
    UserInfo getUserInfo();

    @NotNull
    List<UserInfo> getUserProfiles();

    void removeCallback(@NotNull Callback callback);
}
