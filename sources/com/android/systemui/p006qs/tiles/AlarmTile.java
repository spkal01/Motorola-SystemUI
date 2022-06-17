package com.android.systemui.p006qs.tiles;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import androidx.lifecycle.LifecycleOwner;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.settings.UserTracker;
import com.android.systemui.statusbar.policy.NextAlarmController;
import java.util.Locale;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* renamed from: com.android.systemui.qs.tiles.AlarmTile */
/* compiled from: AlarmTile.kt */
public final class AlarmTile extends QSTileImpl<QSTile.State> {
    @NotNull
    private final NextAlarmController.NextAlarmChangeCallback callback;
    @NotNull
    private final Intent defaultIntent = new Intent("android.intent.action.SET_ALARM");
    private final QSTile.Icon icon = QSTileImpl.ResourceIcon.get(R$drawable.ic_alarm);
    /* access modifiers changed from: private */
    @Nullable
    public AlarmManager.AlarmClockInfo lastAlarmInfo;
    @NotNull
    private final UserTracker userTracker;

    /* renamed from: getDefaultIntent$frameworks__base__packages__SystemUI__android_common__SystemUI_core$annotations */
    public static /* synthetic */ void m49x39713f4d() {
    }

    @Nullable
    public Intent getLongClickIntent() {
        return null;
    }

    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AlarmTile(@NotNull QSHost qSHost, @NotNull Looper looper, @NotNull Handler handler, @NotNull FalsingManager falsingManager, @NotNull MetricsLogger metricsLogger, @NotNull StatusBarStateController statusBarStateController, @NotNull ActivityStarter activityStarter, @NotNull QSLogger qSLogger, @NotNull UserTracker userTracker2, @NotNull NextAlarmController nextAlarmController) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        Intrinsics.checkNotNullParameter(qSHost, "host");
        Intrinsics.checkNotNullParameter(looper, "backgroundLooper");
        Intrinsics.checkNotNullParameter(handler, "mainHandler");
        Intrinsics.checkNotNullParameter(falsingManager, "falsingManager");
        Intrinsics.checkNotNullParameter(metricsLogger, "metricsLogger");
        Intrinsics.checkNotNullParameter(statusBarStateController, "statusBarStateController");
        Intrinsics.checkNotNullParameter(activityStarter, "activityStarter");
        Intrinsics.checkNotNullParameter(qSLogger, "qsLogger");
        Intrinsics.checkNotNullParameter(userTracker2, "userTracker");
        Intrinsics.checkNotNullParameter(nextAlarmController, "nextAlarmController");
        this.userTracker = userTracker2;
        AlarmTile$callback$1 alarmTile$callback$1 = new AlarmTile$callback$1(this);
        this.callback = alarmTile$callback$1;
        nextAlarmController.observe((LifecycleOwner) this, alarmTile$callback$1);
    }

    @NotNull
    public QSTile.State newTileState() {
        QSTile.State state = new QSTile.State();
        state.handlesLongClick = false;
        return state;
    }

    /* access modifiers changed from: protected */
    public void handleClick(@Nullable View view) {
        PendingIntent pendingIntent = null;
        ActivityLaunchAnimator.Controller fromView = view == null ? null : ActivityLaunchAnimator.Controller.Companion.fromView(view, 32);
        AlarmManager.AlarmClockInfo alarmClockInfo = this.lastAlarmInfo;
        if (alarmClockInfo != null) {
            pendingIntent = alarmClockInfo.getShowIntent();
        }
        if (pendingIntent != null) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(pendingIntent, fromView);
        } else {
            this.mActivityStarter.postStartActivityDismissingKeyguard(this.defaultIntent, 0, fromView);
        }
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(@NotNull QSTile.State state, @Nullable Object obj) {
        Unit unit;
        Intrinsics.checkNotNullParameter(state, "state");
        state.icon = this.icon;
        state.label = getTileLabel();
        AlarmManager.AlarmClockInfo alarmClockInfo = this.lastAlarmInfo;
        if (alarmClockInfo == null) {
            unit = null;
        } else {
            state.secondaryLabel = formatNextAlarm(alarmClockInfo);
            state.state = 2;
            unit = Unit.INSTANCE;
        }
        if (unit == null) {
            state.secondaryLabel = this.mContext.getString(R$string.qs_alarm_tile_no_alarm);
            state.state = 1;
        }
        state.contentDescription = TextUtils.concat(new CharSequence[]{state.label, ", ", state.secondaryLabel});
    }

    @NotNull
    public CharSequence getTileLabel() {
        String string = this.mContext.getString(R$string.status_bar_alarm);
        Intrinsics.checkNotNullExpressionValue(string, "mContext.getString(R.string.status_bar_alarm)");
        return string;
    }

    private final String formatNextAlarm(AlarmManager.AlarmClockInfo alarmClockInfo) {
        return DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), use24HourFormat() ? "EHm" : "Ehma"), alarmClockInfo.getTriggerTime()).toString();
    }

    private final boolean use24HourFormat() {
        return DateFormat.is24HourFormat(this.mContext, this.userTracker.getUserId());
    }
}
