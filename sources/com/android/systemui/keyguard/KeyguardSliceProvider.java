package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Trace;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.R$drawable;
import com.android.systemui.R$string;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import com.motorola.keyguard.WeatherSource;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class KeyguardSliceProvider extends SliceProvider implements NextAlarmController.NextAlarmChangeCallback, ZenModeController.Callback, NotificationMediaManager.MediaListener, StatusBarStateController.StateListener, SystemUIAppComponentFactory.ContextInitializer {
    @VisibleForTesting
    static final int ALARM_VISIBILITY_HOURS = 12;
    private static final StyleSpan BOLD_STYLE = new StyleSpan(1);
    private static KeyguardSliceProvider sInstance;
    private static final Object sInstanceLock = new Object();
    public AlarmManager mAlarmManager;
    protected final Uri mAlarmUri = Uri.parse("content://com.android.systemui.keyguard/alarm");
    private Uri mAppWeatherUri = Uri.parse("content://com.motorola.commandcenter.weather");
    public ContentResolver mContentResolver;
    private SystemUIAppComponentFactory.ContextAvailableCallback mContextAvailableCallback;
    private final Date mCurrentTime = new Date();
    private DateFormat mDateFormat;
    private String mDatePattern;
    protected final Uri mDateUri = Uri.parse("content://com.android.systemui.keyguard/date");
    protected final Uri mDndUri = Uri.parse("content://com.android.systemui.keyguard/dnd");
    public DozeParameters mDozeParameters;
    protected boolean mDozing;
    private final Handler mHandler = new Handler();
    protected final Uri mHeaderUri = Uri.parse("content://com.android.systemui.keyguard/header");
    private int mIconNumber = -999;
    @VisibleForTesting
    final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.DATE_CHANGED".equals(action)) {
                synchronized (this) {
                    KeyguardSliceProvider.this.updateClockLocked();
                }
            } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                synchronized (this) {
                    KeyguardSliceProvider.this.cleanDateFormatLocked();
                }
            }
        }
    };
    public KeyguardBypassController mKeyguardBypassController;
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onTimeChanged() {
            synchronized (this) {
                KeyguardSliceProvider.this.updateClockLocked();
            }
        }

        public void onTimeZoneChanged(TimeZone timeZone) {
            synchronized (this) {
                KeyguardSliceProvider.this.cleanDateFormatLocked();
            }
        }
    };
    private String mLastText;
    private CharSequence mMediaArtist;
    private final Handler mMediaHandler = new Handler();
    private boolean mMediaIsVisible;
    public NotificationMediaManager mMediaManager;
    private CharSequence mMediaTitle;
    protected final Uri mMediaUri = Uri.parse("content://com.android.systemui.keyguard/media");
    @VisibleForTesting
    protected SettableWakeLock mMediaWakeLock;
    private String mNextAlarm;
    public NextAlarmController mNextAlarmController;
    private AlarmManager.AlarmClockInfo mNextAlarmInfo;
    private PendingIntent mPendingIntent;
    private boolean mRegistered;
    protected final Uri mSliceUri = Uri.parse("content://com.android.systemui.keyguard/main");
    private int mStatusBarState;
    public StatusBarStateController mStatusBarStateController;
    private int mTempF = -999;
    private final AlarmManager.OnAlarmListener mUpdateNextAlarm = new KeyguardSliceProvider$$ExternalSyntheticLambda0(this);
    private ContentObserver mWeatherContentObserver = null;
    private HandlerThread mWeatherHandlerThread;
    private int mWeatherIcon;
    private String mWeatherText;
    /* access modifiers changed from: private */
    public Handler mWeatherUIHandler;
    protected final Uri mWeatherUri = Uri.parse("content://com.android.systemui.keyguard/weather");
    private Handler mWeatherWorkHandler;
    public ZenModeController mZenModeController;

    public static KeyguardSliceProvider getAttachedInstance() {
        return sInstance;
    }

    public Slice onBindSlice(Uri uri) {
        Slice build;
        Trace.beginSection("KeyguardSliceProvider#onBindSlice");
        synchronized (this) {
            ListBuilder listBuilder = new ListBuilder(getContext(), this.mSliceUri, -1);
            if (needsMediaLocked()) {
                addMediaLocked(listBuilder);
            } else {
                listBuilder.addRow(new ListBuilder.RowBuilder(this.mDateUri).setTitle(this.mLastText));
            }
            if (supportWeather()) {
                addWeatherLocked(getContext(), listBuilder);
            }
            addNextAlarmLocked(listBuilder);
            addZenModeLocked(listBuilder);
            addPrimaryActionLocked(listBuilder);
            build = listBuilder.build();
        }
        Trace.endSection();
        return build;
    }

    /* access modifiers changed from: protected */
    public boolean needsMediaLocked() {
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        boolean z = keyguardBypassController != null && keyguardBypassController.getBypassEnabled() && this.mDozeParameters.getAlwaysOn();
        boolean z2 = this.mStatusBarState == 0 && this.mMediaIsVisible;
        if (TextUtils.isEmpty(this.mMediaTitle) || !this.mMediaIsVisible || (!this.mDozing && !z && !z2)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void addMediaLocked(ListBuilder listBuilder) {
        if (!TextUtils.isEmpty(this.mMediaTitle)) {
            listBuilder.setHeader(new ListBuilder.HeaderBuilder(this.mHeaderUri).setTitle(this.mMediaTitle));
            if (!TextUtils.isEmpty(this.mMediaArtist)) {
                ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(this.mMediaUri);
                rowBuilder.setTitle(this.mMediaArtist);
                NotificationMediaManager notificationMediaManager = this.mMediaManager;
                IconCompat iconCompat = null;
                Icon mediaIcon = notificationMediaManager == null ? null : notificationMediaManager.getMediaIcon();
                if (mediaIcon != null) {
                    iconCompat = IconCompat.createFromIcon(getContext(), mediaIcon);
                }
                if (iconCompat != null) {
                    rowBuilder.addEndItem(iconCompat, 0);
                }
                listBuilder.addRow(rowBuilder);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void addPrimaryActionLocked(ListBuilder listBuilder) {
        listBuilder.addRow(new ListBuilder.RowBuilder(Uri.parse("content://com.android.systemui.keyguard/action")).setPrimaryAction(SliceAction.createDeeplink(this.mPendingIntent, IconCompat.createWithResource(getContext(), R$drawable.ic_access_alarms_big), 0, this.mLastText)));
    }

    /* access modifiers changed from: protected */
    public void addNextAlarmLocked(ListBuilder listBuilder) {
        if (!TextUtils.isEmpty(this.mNextAlarm) && !supportWeather()) {
            listBuilder.addRow(new ListBuilder.RowBuilder(this.mAlarmUri).setTitle(this.mNextAlarm).addEndItem(IconCompat.createWithResource(getContext(), R$drawable.ic_access_alarms_big), 0));
        }
    }

    /* access modifiers changed from: protected */
    public void addZenModeLocked(ListBuilder listBuilder) {
        if (isDndOn() && !supportWeather()) {
            listBuilder.addRow(new ListBuilder.RowBuilder(this.mDndUri).setContentDescription(getContext().getResources().getString(R$string.accessibility_quick_settings_dnd)).addEndItem(IconCompat.createWithResource(getContext(), R$drawable.stat_sys_dnd), 0));
        }
    }

    /* access modifiers changed from: protected */
    public boolean isDndOn() {
        return this.mZenModeController.getZen() != 0;
    }

    public boolean onCreateSliceProvider() {
        this.mContextAvailableCallback.onContextAvailable(getContext());
        this.mMediaWakeLock = new SettableWakeLock(WakeLock.createPartial(getContext(), "media"), "media");
        synchronized (sInstanceLock) {
            KeyguardSliceProvider keyguardSliceProvider = sInstance;
            if (keyguardSliceProvider != null) {
                keyguardSliceProvider.onDestroy();
            }
            this.mDatePattern = getContext().getString(R$string.system_ui_aod_date_pattern);
            this.mPendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), KeyguardSliceProvider.class), 67108864);
            this.mMediaManager.addCallback(this);
            this.mStatusBarStateController.addCallback(this);
            this.mNextAlarmController.addCallback(this);
            this.mZenModeController.addCallback(this);
            sInstance = this;
            registerClockUpdate();
            updateClockLocked();
            if (supportWeather()) {
                initWeatherHandlers();
                registerWeatherObserver();
                notifyUpdateWeather();
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onDestroy() {
        synchronized (sInstanceLock) {
            this.mNextAlarmController.removeCallback(this);
            this.mZenModeController.removeCallback(this);
            this.mMediaWakeLock.setAcquired(false);
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            if (this.mRegistered) {
                this.mRegistered = false;
                getKeyguardUpdateMonitor().removeCallback(this.mKeyguardUpdateMonitorCallback);
                getContext().unregisterReceiver(this.mIntentReceiver);
                unregisterWeatherObserver();
            }
            HandlerThread handlerThread = this.mWeatherHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
                this.mWeatherHandlerThread = null;
            }
            sInstance = null;
        }
    }

    public void onZenChanged(int i) {
        notifyChange();
    }

    public void onConfigChanged(ZenModeConfig zenModeConfig) {
        notifyChange();
    }

    /* access modifiers changed from: private */
    public void updateNextAlarm() {
        synchronized (this) {
            if (withinNHoursLocked(this.mNextAlarmInfo, 12)) {
                String string = getContext().getResources().getString(R$string.clock_12hr_format);
                String string2 = getContext().getResources().getString(R$string.clock_24hr_format);
                if (android.text.format.DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser())) {
                    string = string2;
                }
                this.mNextAlarm = android.text.format.DateFormat.format(android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), string).replaceAll("a", "").trim(), this.mNextAlarmInfo.getTriggerTime()).toString();
            } else {
                this.mNextAlarm = "";
            }
        }
        notifyChange();
    }

    private boolean withinNHoursLocked(AlarmManager.AlarmClockInfo alarmClockInfo, int i) {
        if (alarmClockInfo == null) {
            return false;
        }
        if (this.mNextAlarmInfo.getTriggerTime() <= System.currentTimeMillis() + TimeUnit.HOURS.toMillis((long) i)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void registerClockUpdate() {
        synchronized (this) {
            if (!this.mRegistered) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.DATE_CHANGED");
                intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
                getContext().registerReceiver(this.mIntentReceiver, intentFilter, (String) null, (Handler) null);
                getKeyguardUpdateMonitor().registerCallback(this.mKeyguardUpdateMonitorCallback);
                this.mRegistered = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isRegistered() {
        boolean z;
        synchronized (this) {
            z = this.mRegistered;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void updateClockLocked() {
        String formattedDateLocked = getFormattedDateLocked();
        if (!formattedDateLocked.equals(this.mLastText)) {
            this.mLastText = formattedDateLocked;
            notifyChange();
            updateNextAlarm();
        }
    }

    /* access modifiers changed from: protected */
    public String getFormattedDateLocked() {
        if (this.mDateFormat == null) {
            DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(this.mDatePattern, Locale.getDefault());
            instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = instanceForSkeleton;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        return this.mDateFormat.format(this.mCurrentTime);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void cleanDateFormatLocked() {
        this.mDateFormat = null;
    }

    public void onNextAlarmChanged(AlarmManager.AlarmClockInfo alarmClockInfo) {
        long triggerTime;
        synchronized (this) {
            this.mNextAlarmInfo = alarmClockInfo;
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            AlarmManager.AlarmClockInfo alarmClockInfo2 = this.mNextAlarmInfo;
            if (alarmClockInfo2 == null) {
                triggerTime = -1;
            } else {
                triggerTime = alarmClockInfo2.getTriggerTime() - TimeUnit.HOURS.toMillis(12);
            }
            long j = triggerTime;
            if (j > 0) {
                this.mAlarmManager.setExact(1, j, "lock_screen_next_alarm", this.mUpdateNextAlarm, this.mHandler);
            }
        }
        updateNextAlarm();
    }

    private KeyguardUpdateMonitor getKeyguardUpdateMonitor() {
        return (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
    }

    public void onPrimaryMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        synchronized (this) {
            boolean isPlayingState = NotificationMediaManager.isPlayingState(i);
            this.mMediaHandler.removeCallbacksAndMessages((Object) null);
            if (!this.mMediaIsVisible || isPlayingState || this.mStatusBarState == 0) {
                this.mMediaWakeLock.setAcquired(false);
                updateMediaStateLocked(mediaMetadata, i);
            } else {
                this.mMediaWakeLock.setAcquired(true);
                this.mMediaHandler.postDelayed(new KeyguardSliceProvider$$ExternalSyntheticLambda1(this, mediaMetadata, i), 2000);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPrimaryMetadataOrStateChanged$0(MediaMetadata mediaMetadata, int i) {
        synchronized (this) {
            updateMediaStateLocked(mediaMetadata, i);
            this.mMediaWakeLock.setAcquired(false);
        }
    }

    private void updateMediaStateLocked(MediaMetadata mediaMetadata, int i) {
        CharSequence charSequence;
        boolean isPlayingState = NotificationMediaManager.isPlayingState(i);
        CharSequence charSequence2 = null;
        if (mediaMetadata != null) {
            charSequence = mediaMetadata.getText("android.media.metadata.TITLE");
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = getContext().getResources().getString(R$string.music_controls_no_title);
            }
        } else {
            charSequence = null;
        }
        if (mediaMetadata != null) {
            charSequence2 = mediaMetadata.getText("android.media.metadata.ARTIST");
        }
        if (isPlayingState != this.mMediaIsVisible || !TextUtils.equals(charSequence, this.mMediaTitle) || !TextUtils.equals(charSequence2, this.mMediaArtist)) {
            this.mMediaTitle = charSequence;
            this.mMediaArtist = charSequence2;
            this.mMediaIsVisible = isPlayingState;
            notifyChange();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyChange() {
        this.mContentResolver.notifyChange(this.mSliceUri, (ContentObserver) null);
    }

    public void onDozingChanged(boolean z) {
        boolean z2;
        synchronized (this) {
            boolean needsMediaLocked = needsMediaLocked();
            this.mDozing = z;
            z2 = needsMediaLocked != needsMediaLocked();
        }
        if (z2) {
            notifyChange();
        }
    }

    public void onStateChanged(int i) {
        boolean z;
        synchronized (this) {
            boolean needsMediaLocked = needsMediaLocked();
            this.mStatusBarState = i;
            z = needsMediaLocked != needsMediaLocked();
        }
        if (z) {
            notifyChange();
        }
    }

    public void setContextAvailableCallback(SystemUIAppComponentFactory.ContextAvailableCallback contextAvailableCallback) {
        this.mContextAvailableCallback = contextAvailableCallback;
    }

    private void addWeatherLocked(Context context, ListBuilder listBuilder) {
        Context remoteContext;
        notifyUpdateWeather();
        if (!TextUtils.isEmpty(this.mWeatherText) && (remoteContext = WeatherSource.getRemoteContext(context)) != null) {
            IconCompat createWithResource = IconCompat.createWithResource(remoteContext, this.mWeatherIcon);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.motorola.timeweatherwidget", "com.motorola.commandcenter.weather.WeatherActivity"));
            listBuilder.addRow(new ListBuilder.RowBuilder(this.mWeatherUri).setTitle(this.mWeatherText).addEndItem(createWithResource, 0).setPrimaryAction(SliceAction.createDeeplink(PendingIntent.getActivity(context, 0, intent, 67108864), createWithResource, 0, this.mWeatherText)));
        }
    }

    /* access modifiers changed from: private */
    public void updateWeatherLocked(Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt("currentTempInt", -999);
            int i2 = bundle.getInt("mWeatherIcon", -999);
            if (i == this.mTempF && i2 == this.mIconNumber) {
                Log.i("KgdSliceProvider", "The weather info has not change, does not need to be updated. mTempF=" + this.mTempF + " iconNumber=" + i2);
                return;
            }
            this.mTempF = i;
            this.mIconNumber = i2;
            int weatherIconResource = WeatherSource.getWeatherIconResource(getContext(), i2);
            if (i == -999 || i2 == -999 || weatherIconResource == -999) {
                this.mWeatherText = "";
            } else {
                if (bundle.getBoolean("celsius", false)) {
                    i = WeatherSource.getCelsiusTemperature(i);
                }
                this.mWeatherText = String.format(getContext().getResources().getString(R$string.weather_temp), new Object[]{Integer.valueOf(i)});
                this.mWeatherIcon = weatherIconResource;
            }
            notifyChange();
        } else if (!TextUtils.isEmpty(this.mWeatherText)) {
            this.mWeatherText = "";
            notifyChange();
        }
    }

    private void registerWeatherObserver() {
        if (supportWeather()) {
            this.mWeatherContentObserver = new ContentObserver(this.mHandler) {
                public void onChange(boolean z) {
                    KeyguardSliceProvider.this.notifyUpdateWeather();
                }
            };
            try {
                getContext().getContentResolver().registerContentObserver(this.mAppWeatherUri, true, this.mWeatherContentObserver);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void unregisterWeatherObserver() {
        if (supportWeather() && this.mWeatherContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mWeatherContentObserver);
            this.mWeatherContentObserver = null;
        }
    }

    private boolean supportWeather() {
        return MotoFeature.getInstance(getContext()).supportLockscreenWeather();
    }

    /* access modifiers changed from: private */
    public void notifyUpdateWeather() {
        if (KeyguardConstants.DEBUG) {
            Log.d("KgdSliceProvider", "notify update weather info");
        }
        if (supportWeather()) {
            if (this.mWeatherWorkHandler.hasMessages(1)) {
                this.mWeatherWorkHandler.removeMessages(1);
            }
            this.mWeatherWorkHandler.sendEmptyMessage(1);
        }
    }

    /* access modifiers changed from: private */
    public Bundle getWeatherInfo() {
        try {
            return getContext().getContentResolver().call(this.mAppWeatherUri, "get_weather_data", (String) null, (Bundle) null);
        } catch (RuntimeException e) {
            Log.e("KgdSliceProvider", "Failed to load weather provider", e);
            return null;
        }
    }

    private void initWeatherHandlers() {
        HandlerThread handlerThread = new HandlerThread("updateWeather");
        this.mWeatherHandlerThread = handlerThread;
        handlerThread.start();
        this.mWeatherWorkHandler = new Handler(this.mWeatherHandlerThread.getLooper()) {
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    Message obtainMessage = KeyguardSliceProvider.this.mWeatherUIHandler.obtainMessage(2, KeyguardSliceProvider.this.getWeatherInfo());
                    if (KeyguardSliceProvider.this.mWeatherUIHandler.hasMessages(2)) {
                        KeyguardSliceProvider.this.mWeatherUIHandler.removeMessages(2);
                    }
                    KeyguardSliceProvider.this.mWeatherUIHandler.sendMessage(obtainMessage);
                }
            }
        };
        this.mWeatherUIHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                if (message.what == 2) {
                    KeyguardSliceProvider.this.updateWeatherLocked((Bundle) message.obj);
                }
            }
        };
    }
}
