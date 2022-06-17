package com.android.systemui.p006qs.tiles;

import android.app.AlarmManager;
import com.android.systemui.statusbar.policy.NextAlarmController;

/* renamed from: com.android.systemui.qs.tiles.AlarmTile$callback$1 */
/* compiled from: AlarmTile.kt */
final class AlarmTile$callback$1 implements NextAlarmController.NextAlarmChangeCallback {
    final /* synthetic */ AlarmTile this$0;

    AlarmTile$callback$1(AlarmTile alarmTile) {
        this.this$0 = alarmTile;
    }

    public final void onNextAlarmChanged(AlarmManager.AlarmClockInfo alarmClockInfo) {
        this.this$0.lastAlarmInfo = alarmClockInfo;
        this.this$0.refreshState();
    }
}
