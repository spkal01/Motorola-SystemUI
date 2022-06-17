package com.android.systemui.p006qs.tiles;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenrecord.RecordingSettings;
import com.android.systemui.settings.UserContextProvider;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.motorola.android.provider.MotorolaSettings;

/* renamed from: com.android.systemui.qs.tiles.ScreenRecordTile */
public class ScreenRecordTile extends QSTileImpl<QSTile.BooleanState> implements RecordingController.RecordingStateChangeCallback {
    private ActivityStarter mActivityStarter;
    private Callback mCallback = new Callback();
    private RecordingController mController;
    private boolean mListening = false;
    /* access modifiers changed from: private */
    public long mMillisUntilFinished = 0;
    private ContentObserver mRecordingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z) {
            int screenRecordingStatus = RecordingSettings.getScreenRecordingStatus(ScreenRecordTile.this.mUserContextTracker.getUserContext());
            Log.d("Recording_Tile", "status=" + screenRecordingStatus);
            if (ScreenRecordTile.this.mRecordingStatus != screenRecordingStatus) {
                int unused = ScreenRecordTile.this.mRecordingStatus = screenRecordingStatus;
                ScreenRecordTile.this.refreshState();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mRecordingStatus = 0;
    /* access modifiers changed from: private */
    public final UserContextProvider mUserContextTracker;
    private int mUserId = UserHandle.myUserId();

    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ScreenRecordTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, UserContextProvider userContextProvider, ActivityStarter activityStarter, QSLogger qSLogger, RecordingController recordingController, KeyguardDismissUtil keyguardDismissUtil) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        this.mController = recordingController;
        this.mUserContextTracker = userContextProvider;
        this.mActivityStarter = activityStarter;
    }

    public QSTile.BooleanState newTileState() {
        QSTile.BooleanState booleanState = new QSTile.BooleanState();
        booleanState.handlesLongClick = true;
        return booleanState;
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (this.mRecordingStatus == 0) {
            startCountdown();
        } else {
            stopRecording();
        }
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        CharSequence charSequence;
        int i = this.mRecordingStatus;
        booleanState.value = i != 0;
        booleanState.state = i != 0 ? 2 : 1;
        booleanState.label = this.mContext.getString(R$string.quick_settings_screen_record_label);
        booleanState.icon = QSTileImpl.ResourceIcon.get(R$drawable.ic_qs_screenrecord);
        booleanState.forceExpandIcon = booleanState.state == 1;
        if (this.mRecordingStatus == 0) {
            booleanState.secondaryLabel = this.mContext.getString(R$string.quick_settings_screen_record_start);
        } else {
            booleanState.secondaryLabel = this.mContext.getString(R$string.quick_settings_screen_record_stop);
        }
        if (TextUtils.isEmpty(booleanState.secondaryLabel)) {
            charSequence = booleanState.label;
        } else {
            charSequence = TextUtils.concat(new CharSequence[]{booleanState.label, ", ", booleanState.secondaryLabel});
        }
        booleanState.contentDescription = charSequence;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    public Intent getLongClickIntent() {
        Log.d("Recording_Tile", "Starting screenrecord settings");
        Intent intent = new Intent();
        intent.setClassName("com.motorola.coresettingsext", "com.motorola.coresettingsext.screenrecord.ScreenRecordActivity");
        return intent;
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_screen_record_label);
    }

    private void startCountdown() {
        Log.d("Recording_Tile", "Starting countdown");
        Intent promptIntent = this.mController.getPromptIntent();
        if (DesktopFeature.isDesktopDisplayContext(this.mContext)) {
            getHost().collapsePanels();
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            makeBasic.setLaunchWindowingMode(1);
            this.mContext.startActivityAsUser(promptIntent, makeBasic.toBundle(), UserHandle.CURRENT);
            return;
        }
        this.mActivityStarter.postStartActivityDismissingKeyguard(promptIntent, 0);
    }

    private void stopRecording() {
        Log.d("Recording_Tile", "Stopping recording from tile");
        Context context = this.mContext;
        context.startServiceAsUser(RecordingService.getStopIntent(context), UserHandle.of(ActivityManager.getCurrentUser()));
    }

    /* access modifiers changed from: protected */
    public void handleSetListening(boolean z) {
        if (z != this.mListening) {
            this.mListening = z;
            if (z) {
                this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("screen_recording_status"), false, this.mRecordingObserver, -1);
                this.mRecordingStatus = RecordingSettings.getScreenRecordingStatus(this.mUserContextTracker.getUserContext());
                refreshState();
                return;
            }
            this.mContext.getContentResolver().unregisterContentObserver(this.mRecordingObserver);
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.ScreenRecordTile$Callback */
    private final class Callback implements RecordingController.RecordingStateChangeCallback {
        private Callback() {
        }

        public void onCountdown(long j) {
            long unused = ScreenRecordTile.this.mMillisUntilFinished = j;
            ScreenRecordTile.this.refreshState();
        }

        public void onCountdownEnd() {
            ScreenRecordTile.this.refreshState();
        }

        public void onCountdownCancel(boolean z) {
            ScreenRecordTile.this.refreshState();
        }

        public void onRecordingStart() {
            ScreenRecordTile.this.refreshState();
        }

        public void onRecordingEnd() {
            ScreenRecordTile.this.refreshState();
        }
    }
}
