package com.android.systemui.controls.dagger;

import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.controls.p004ui.ControlsUiController;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.settings.SecureSettings;
import dagger.Lazy;
import java.util.Optional;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsComponent.kt */
public final class ControlsComponent {
    private boolean canShowWhileLockedSetting;
    @NotNull
    private final Context context;
    private final boolean featureEnabled;
    @NotNull
    private final KeyguardStateController keyguardStateController;
    @NotNull
    private final Lazy<ControlsController> lazyControlsController;
    @NotNull
    private final Lazy<ControlsListingController> lazyControlsListingController;
    @NotNull
    private final Lazy<ControlsUiController> lazyControlsUiController;
    @NotNull
    private final LockPatternUtils lockPatternUtils;
    @NotNull
    private final SecureSettings secureSettings;
    @NotNull
    private final ContentObserver showWhileLockedObserver;
    @NotNull
    private final UserTracker userTracker;

    /* compiled from: ControlsComponent.kt */
    public enum Visibility {
        AVAILABLE,
        AVAILABLE_AFTER_UNLOCK,
        UNAVAILABLE
    }

    public ControlsComponent(boolean z, @NotNull Context context2, @NotNull Lazy<ControlsController> lazy, @NotNull Lazy<ControlsUiController> lazy2, @NotNull Lazy<ControlsListingController> lazy3, @NotNull LockPatternUtils lockPatternUtils2, @NotNull KeyguardStateController keyguardStateController2, @NotNull UserTracker userTracker2, @NotNull SecureSettings secureSettings2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(lazy, "lazyControlsController");
        Intrinsics.checkNotNullParameter(lazy2, "lazyControlsUiController");
        Intrinsics.checkNotNullParameter(lazy3, "lazyControlsListingController");
        Intrinsics.checkNotNullParameter(lockPatternUtils2, "lockPatternUtils");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(userTracker2, "userTracker");
        Intrinsics.checkNotNullParameter(secureSettings2, "secureSettings");
        this.featureEnabled = z;
        this.context = context2;
        this.lazyControlsController = lazy;
        this.lazyControlsUiController = lazy2;
        this.lazyControlsListingController = lazy3;
        this.lockPatternUtils = lockPatternUtils2;
        this.keyguardStateController = keyguardStateController2;
        this.userTracker = userTracker2;
        this.secureSettings = secureSettings2;
        ControlsComponent$showWhileLockedObserver$1 controlsComponent$showWhileLockedObserver$1 = new ControlsComponent$showWhileLockedObserver$1(this);
        this.showWhileLockedObserver = controlsComponent$showWhileLockedObserver$1;
        if (z) {
            secureSettings2.registerContentObserver(Settings.Secure.getUriFor("lockscreen_show_controls"), false, controlsComponent$showWhileLockedObserver$1);
            updateShowWhileLocked();
        }
    }

    @NotNull
    public final Optional<ControlsController> getControlsController() {
        Optional<ControlsController> optional;
        String str;
        if (this.featureEnabled) {
            optional = Optional.of(this.lazyControlsController.get());
            str = "of(lazyControlsController.get())";
        } else {
            optional = Optional.empty();
            str = "empty()";
        }
        Intrinsics.checkNotNullExpressionValue(optional, str);
        return optional;
    }

    @NotNull
    public final Optional<ControlsListingController> getControlsListingController() {
        if (this.featureEnabled) {
            Optional<ControlsListingController> of = Optional.of(this.lazyControlsListingController.get());
            Intrinsics.checkNotNullExpressionValue(of, "{\n            Optional.of(lazyControlsListingController.get())\n        }");
            return of;
        }
        Optional<ControlsListingController> empty = Optional.empty();
        Intrinsics.checkNotNullExpressionValue(empty, "{\n            Optional.empty()\n        }");
        return empty;
    }

    public final boolean isEnabled() {
        return this.featureEnabled;
    }

    @NotNull
    public final Visibility getVisibility() {
        if (!isEnabled()) {
            return Visibility.UNAVAILABLE;
        }
        if (this.lockPatternUtils.getStrongAuthForUser(this.userTracker.getUserHandle().getIdentifier()) == 1) {
            return Visibility.AVAILABLE_AFTER_UNLOCK;
        }
        if (this.canShowWhileLockedSetting || this.keyguardStateController.isUnlocked()) {
            return Visibility.AVAILABLE;
        }
        return Visibility.AVAILABLE_AFTER_UNLOCK;
    }

    /* access modifiers changed from: private */
    public final void updateShowWhileLocked() {
        boolean z = false;
        if (this.secureSettings.getInt("lockscreen_show_controls", 0) != 0) {
            z = true;
        }
        this.canShowWhileLockedSetting = z;
    }
}
