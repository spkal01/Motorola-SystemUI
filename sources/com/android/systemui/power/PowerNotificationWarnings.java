package com.android.systemui.power;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Slog;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.CliToast;
import com.android.systemui.Dependency;
import com.android.systemui.Prefs;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$raw;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.SystemUI;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.power.PowerUI;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.util.NotificationChannels;
import com.android.systemui.volume.Events;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class PowerNotificationWarnings implements PowerUI.WarningsUI {
    private static final AudioAttributes AUDIO_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private static final boolean DEBUG = PowerUI.DEBUG;
    private static final String[] SHOWING_STRINGS = {"SHOWING_NOTHING", "SHOWING_WARNING", "SHOWING_SAVER", "SHOWING_INVALID_CHARGER", "SHOWING_AUTO_SAVER_SUGGESTION", "SHOWING_FULLY_CHARGED_AND_PLUGGED"};
    /* access modifiers changed from: private */
    public ActivityManagerWrapper mActivityManagerWrapper;
    private ActivityStarter mActivityStarter;
    private int mBatteryLevel;
    private int mBucket;
    private ChargerThermalWarningDialog mChargerThermalWarningDialog;
    /* access modifiers changed from: private */
    public final Context mContext;
    private BatteryStateSnapshot mCurrentBatterySnapshot;
    /* access modifiers changed from: private */
    public DeviceProvisionedController mDeviceProvisionedController;
    /* access modifiers changed from: private */
    public DisplayManager mDisplayManager;
    private boolean mFullyChargedPlugged;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public SystemUIDialog mHighTempDialog;
    private boolean mHighTempWarning;
    private boolean mInvalidCharger;
    private final KeyguardManager mKeyguard;
    private Ringtone mMotoUsbLpdAlarmRingtone;
    private SystemUIDialog mMotoUsbLpdDialog;
    private final NotificationManager mNoMan;
    /* access modifiers changed from: private */
    public final Intent mOpenBatterySettings;
    private boolean mPlaySound;
    private final PowerManager mPowerMan;
    private final Receiver mReceiver;
    private int mSavedVolumeLevel;
    /* access modifiers changed from: private */
    public SystemUIDialog mSaverConfirmation;
    private long mScreenOffTime;
    private boolean mShowAutoSaverSuggestion;
    private int mShowing;
    /* access modifiers changed from: private */
    public SystemUIDialog mThermalShutdownDialog;
    SystemUIDialog mUsbHighTempDialog;
    private boolean mWarning;
    private long mWarningTriggerTimeMs;
    private boolean mWeakCharger;

    public PowerNotificationWarnings(Context context, ActivityStarter activityStarter) {
        Receiver receiver = new Receiver();
        this.mReceiver = receiver;
        this.mOpenBatterySettings = settings("android.intent.action.POWER_USAGE_SUMMARY");
        this.mFullyChargedPlugged = false;
        this.mContext = context;
        this.mNoMan = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mPowerMan = (PowerManager) context.getSystemService("power");
        this.mKeyguard = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        receiver.init();
        this.mActivityStarter = activityStarter;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print("mWarning=");
        printWriter.println(this.mWarning);
        printWriter.print("mPlaySound=");
        printWriter.println(this.mPlaySound);
        printWriter.print("mInvalidCharger=");
        printWriter.println(this.mInvalidCharger);
        printWriter.print("mShowing=");
        printWriter.println(SHOWING_STRINGS[this.mShowing]);
        printWriter.print("mSaverConfirmation=");
        String str = "not null";
        printWriter.println(this.mSaverConfirmation != null ? str : null);
        printWriter.print("mSaverEnabledConfirmation=");
        printWriter.print("mHighTempWarning=");
        printWriter.println(this.mHighTempWarning);
        printWriter.print("mHighTempDialog=");
        printWriter.println(this.mHighTempDialog != null ? str : null);
        printWriter.print("mThermalShutdownDialog=");
        printWriter.println(this.mThermalShutdownDialog != null ? str : null);
        printWriter.print("mUsbHighTempDialog=");
        if (this.mUsbHighTempDialog == null) {
            str = null;
        }
        printWriter.println(str);
        printWriter.print("mWeakCharger=");
        printWriter.println(this.mWeakCharger);
        printWriter.print("mFullyChargedPlugged=");
        printWriter.println(this.mFullyChargedPlugged);
    }

    public void update(int i, int i2, long j) {
        this.mBatteryLevel = i;
        if (i2 >= 0) {
            this.mWarningTriggerTimeMs = 0;
        } else if (i2 < this.mBucket) {
            this.mWarningTriggerTimeMs = System.currentTimeMillis();
        }
        this.mBucket = i2;
        this.mScreenOffTime = j;
    }

    public void updateSnapshot(BatteryStateSnapshot batteryStateSnapshot) {
        this.mCurrentBatterySnapshot = batteryStateSnapshot;
    }

    private void updateNotification() {
        if (DEBUG) {
            Slog.d("PowerUI.Notification", "updateNotification mWarning=" + this.mWarning + " mPlaySound=" + this.mPlaySound + " mInvalidCharger=" + this.mInvalidCharger + " mFullyChargedPlugged=" + this.mFullyChargedPlugged);
        }
        if (this.mInvalidCharger) {
            showInvalidChargerNotification();
            this.mShowing = 3;
        } else if (this.mFullyChargedPlugged) {
            showFullyChargedPluggedNotification();
            this.mShowing = 5;
        } else if (this.mWarning) {
            showWarningNotification();
            this.mShowing = 1;
        } else if (this.mShowAutoSaverSuggestion) {
            if (this.mShowing != 4) {
                showAutoSaverSuggestionNotification();
            }
            this.mShowing = 4;
            this.mNoMan.cancelAsUser("low_battery", 2, UserHandle.ALL);
            this.mNoMan.cancelAsUser("low_battery", 3, UserHandle.ALL);
        } else {
            this.mNoMan.cancelAsUser("low_battery", 2, UserHandle.ALL);
            this.mNoMan.cancelAsUser("low_battery", 3, UserHandle.ALL);
            this.mNoMan.cancelAsUser("auto_saver", 49, UserHandle.ALL);
            this.mNoMan.cancelAsUser("fully_charged_and_plugged", 1201, UserHandle.ALL);
            this.mShowing = 0;
        }
    }

    private void showInvalidChargerNotification() {
        Notification.Builder color = new Notification.Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R$drawable.ic_power_low).setWhen(0).setShowWhen(false).setOngoing(true).setContentTitle(this.mContext.getString(R$string.invalid_charger_title)).setContentText(this.mContext.getString(R$string.invalid_charger_text)).setColor(this.mContext.getColor(17170460));
        SystemUI.overrideNotificationAppName(this.mContext, color, false);
        Notification build = color.build();
        this.mNoMan.cancelAsUser("low_battery", 3, UserHandle.ALL);
        this.mNoMan.notifyAsUser("low_battery", 2, build, UserHandle.ALL);
    }

    private void showFullyChargedPluggedNotification() {
        Notification.Builder color = new Notification.Builder(this.mContext, NotificationChannels.GENERAL).setSmallIcon(R$drawable.zz_moto_ic_power_fully_charged_and_plugged).setOngoing(true).setContentTitle(this.mContext.getString(R$string.zz_moto_fully_charged_and_plugged_notification_title)).setContentText(this.mContext.getString(R$string.zz_moto_fully_charged_and_plugged_notification_text)).setColor(this.mContext.getColor(17170460));
        SystemUI.overrideNotificationAppName(this.mContext, color, false);
        this.mNoMan.notifyAsUser("fully_charged_and_plugged", 1201, color.build(), UserHandle.ALL);
    }

    public void showFullyChargedPluggedWarning() {
        if (DEBUG) {
            Slog.d("PowerUI.Notification", "showing fully charged plugged warning");
        }
        this.mFullyChargedPlugged = true;
        updateNotification();
    }

    public void dismissFullyChargedPluggedWarning() {
        if (DEBUG) {
            Slog.d("PowerUI.Notification", "dismissing fully charged plugged warning");
        }
        this.mFullyChargedPlugged = false;
        updateNotification();
    }

    /* access modifiers changed from: protected */
    public void showWarningNotification() {
        String str;
        String format = NumberFormat.getPercentInstance().format(((double) this.mCurrentBatterySnapshot.getBatteryLevel()) / 100.0d);
        String string = this.mContext.getString(R$string.battery_low_title);
        if (this.mCurrentBatterySnapshot.isHybrid()) {
            str = getHybridContentString(format);
        } else {
            str = this.mContext.getString(R$string.battery_low_percent_format, new Object[]{format});
        }
        Notification.Builder visibility = new Notification.Builder(this.mContext, NotificationChannels.BATTERY).setSmallIcon(R$drawable.ic_power_low).setWhen(this.mWarningTriggerTimeMs).setShowWhen(false).setContentText(str).setContentTitle(string).setOnlyAlertOnce(true).setDeleteIntent(pendingBroadcast("PNW.dismissedWarning")).setStyle(new Notification.BigTextStyle().bigText(str)).setVisibility(1);
        if (hasBatterySettings()) {
            visibility.setContentIntent(pendingBroadcast("PNW.batterySettings"));
        }
        if (!this.mCurrentBatterySnapshot.isHybrid() || this.mBucket < 0 || this.mCurrentBatterySnapshot.getTimeRemainingMillis() < this.mCurrentBatterySnapshot.getSevereThresholdMillis()) {
            visibility.setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        }
        if (!this.mPowerMan.isPowerSaveMode()) {
            visibility.addAction(0, this.mContext.getString(R$string.battery_saver_start_action), pendingBroadcast("PNW.startSaver"));
        }
        visibility.setOnlyAlertOnce(!this.mPlaySound);
        this.mPlaySound = false;
        SystemUI.overrideNotificationAppName(this.mContext, visibility, false);
        Notification build = visibility.build();
        this.mNoMan.cancelAsUser("low_battery", 2, UserHandle.ALL);
        this.mNoMan.notifyAsUser("low_battery", 3, build, UserHandle.ALL);
    }

    private void showAutoSaverSuggestionNotification() {
        String string = this.mContext.getString(R$string.auto_saver_text);
        Notification.Builder contentText = new Notification.Builder(this.mContext, NotificationChannels.HINTS).setSmallIcon(R$drawable.ic_power_saver).setWhen(0).setShowWhen(false).setContentTitle(this.mContext.getString(R$string.auto_saver_title)).setStyle(new Notification.BigTextStyle().bigText(string)).setContentText(string);
        contentText.setContentIntent(pendingBroadcast("PNW.enableAutoSaver"));
        contentText.setDeleteIntent(pendingBroadcast("PNW.dismissAutoSaverSuggestion"));
        contentText.addAction(0, this.mContext.getString(R$string.no_auto_saver_action), pendingBroadcast("PNW.autoSaverNoThanks"));
        SystemUI.overrideNotificationAppName(this.mContext, contentText, false);
        this.mNoMan.notifyAsUser("auto_saver", 49, contentText.build(), UserHandle.ALL);
    }

    private String getHybridContentString(String str) {
        return PowerUtil.getBatteryRemainingStringFormatted(this.mContext, this.mCurrentBatterySnapshot.getTimeRemainingMillis(), str, this.mCurrentBatterySnapshot.isBasedOnUsage());
    }

    private PendingIntent pendingBroadcast(String str) {
        return PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(str).setPackage(this.mContext.getPackageName()).setFlags(268435456), 67108864, UserHandle.CURRENT);
    }

    private static Intent settings(String str) {
        return new Intent(str).setFlags(1551892480);
    }

    public boolean isInvalidChargerWarningShowing() {
        return this.mInvalidCharger;
    }

    public void dismissHighTemperatureWarning() {
        if (this.mHighTempWarning) {
            dismissHighTemperatureWarningInternal();
        }
    }

    /* access modifiers changed from: private */
    public void dismissHighTemperatureWarningInternal() {
        this.mNoMan.cancelAsUser("high_temp", 4, UserHandle.ALL);
        this.mHighTempWarning = false;
    }

    public void showHighTemperatureWarning() {
        if (!this.mHighTempWarning) {
            this.mHighTempWarning = true;
            String string = this.mContext.getString(R$string.high_temp_notif_message);
            Notification.Builder color = new Notification.Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R$drawable.ic_device_thermostat_24).setWhen(0).setShowWhen(false).setContentTitle(this.mContext.getString(R$string.high_temp_title)).setContentText(string).setStyle(new Notification.BigTextStyle().bigText(string)).setVisibility(1).setContentIntent(pendingBroadcast("PNW.clickedTempWarning")).setDeleteIntent(pendingBroadcast("PNW.dismissedTempWarning")).setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
            SystemUI.overrideNotificationAppName(this.mContext, color, false);
            this.mNoMan.notifyAsUser("high_temp", 4, color.build(), UserHandle.ALL);
        }
    }

    /* access modifiers changed from: private */
    public void showHighTemperatureDialog() {
        if (this.mHighTempDialog == null) {
            SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
            systemUIDialog.setIconAttribute(16843605);
            systemUIDialog.setTitle(R$string.high_temp_title);
            systemUIDialog.setMessage(R$string.high_temp_dialog_message);
            systemUIDialog.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.setOnDismissListener(new PowerNotificationWarnings$$ExternalSyntheticLambda6(this));
            final String string = this.mContext.getString(R$string.high_temp_dialog_help_url);
            if (!string.isEmpty()) {
                systemUIDialog.setNeutralButton(R$string.high_temp_dialog_help_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(string)).setFlags(268435456), true, (ActivityStarter.Callback) new PowerNotificationWarnings$1$$ExternalSyntheticLambda0(this));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onClick$0(int i) {
                        SystemUIDialog unused = PowerNotificationWarnings.this.mHighTempDialog = null;
                    }
                });
            }
            systemUIDialog.show();
            this.mHighTempDialog = systemUIDialog;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showHighTemperatureDialog$0(DialogInterface dialogInterface) {
        this.mHighTempDialog = null;
    }

    /* access modifiers changed from: package-private */
    public void dismissThermalShutdownWarning() {
        this.mNoMan.cancelAsUser("high_temp", 39, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public void showThermalShutdownDialog() {
        if (this.mThermalShutdownDialog == null) {
            SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
            systemUIDialog.setIconAttribute(16843605);
            systemUIDialog.setTitle(R$string.thermal_shutdown_title);
            systemUIDialog.setMessage(R$string.thermal_shutdown_dialog_message);
            systemUIDialog.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.setOnDismissListener(new PowerNotificationWarnings$$ExternalSyntheticLambda9(this));
            final String string = this.mContext.getString(R$string.thermal_shutdown_dialog_help_url);
            if (!string.isEmpty()) {
                systemUIDialog.setNeutralButton(R$string.thermal_shutdown_dialog_help_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(string)).setFlags(268435456), true, (ActivityStarter.Callback) new PowerNotificationWarnings$2$$ExternalSyntheticLambda0(this));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onClick$0(int i) {
                        SystemUIDialog unused = PowerNotificationWarnings.this.mThermalShutdownDialog = null;
                    }
                });
            }
            systemUIDialog.show();
            this.mThermalShutdownDialog = systemUIDialog;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showThermalShutdownDialog$1(DialogInterface dialogInterface) {
        this.mThermalShutdownDialog = null;
    }

    public void showThermalShutdownWarning() {
        String string = this.mContext.getString(R$string.thermal_shutdown_message);
        Notification.Builder color = new Notification.Builder(this.mContext, NotificationChannels.ALERTS).setSmallIcon(R$drawable.ic_device_thermostat_24).setWhen(0).setShowWhen(false).setContentTitle(this.mContext.getString(R$string.thermal_shutdown_title)).setContentText(string).setStyle(new Notification.BigTextStyle().bigText(string)).setVisibility(1).setContentIntent(pendingBroadcast("PNW.clickedThermalShutdownWarning")).setDeleteIntent(pendingBroadcast("PNW.dismissedThermalShutdownWarning")).setColor(Utils.getColorAttrDefaultColor(this.mContext, 16844099));
        SystemUI.overrideNotificationAppName(this.mContext, color, false);
        this.mNoMan.notifyAsUser("high_temp", 39, color.build(), UserHandle.ALL);
    }

    public void showUsbHighTemperatureAlarm() {
        this.mHandler.post(new PowerNotificationWarnings$$ExternalSyntheticLambda14(this));
    }

    /* access modifiers changed from: private */
    /* renamed from: showUsbHighTemperatureAlarmInternal */
    public void lambda$showUsbHighTemperatureAlarm$2() {
        if (this.mUsbHighTempDialog == null) {
            SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext, R$style.Theme_SystemUI_Dialog_Alert);
            systemUIDialog.setCancelable(false);
            systemUIDialog.setIconAttribute(16843605);
            systemUIDialog.setTitle(R$string.high_temp_alarm_title);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.setMessage(this.mContext.getString(R$string.high_temp_alarm_notify_message, new Object[]{""}));
            systemUIDialog.setPositiveButton(17039370, new PowerNotificationWarnings$$ExternalSyntheticLambda3(this));
            systemUIDialog.setNegativeButton(R$string.high_temp_alarm_help_care_steps, new PowerNotificationWarnings$$ExternalSyntheticLambda0(this));
            systemUIDialog.setOnDismissListener(new PowerNotificationWarnings$$ExternalSyntheticLambda8(this));
            systemUIDialog.getWindow().addFlags(2097280);
            systemUIDialog.show();
            this.mUsbHighTempDialog = systemUIDialog;
            Events.writeEvent(19, 3, Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$3(DialogInterface dialogInterface, int i) {
        this.mUsbHighTempDialog = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$5(DialogInterface dialogInterface, int i) {
        String string = this.mContext.getString(R$string.high_temp_alarm_help_url);
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.HelpTrampoline");
        intent.putExtra("android.intent.extra.TEXT", string);
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(intent, true, (ActivityStarter.Callback) new PowerNotificationWarnings$$ExternalSyntheticLambda12(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$4(int i) {
        this.mUsbHighTempDialog = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUsbHighTemperatureAlarmInternal$6(DialogInterface dialogInterface) {
        this.mUsbHighTempDialog = null;
        Events.writeEvent(20, 9, Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
    }

    public void showMotoUsbHighTemperatureAlarm(float f) {
        this.mHandler.post(new PowerNotificationWarnings$$ExternalSyntheticLambda15(this, f));
    }

    /* access modifiers changed from: private */
    /* renamed from: showMotoUsbHighTemperatureAlarmInternal */
    public void lambda$showMotoUsbHighTemperatureAlarm$7(float f) {
        if (this.mUsbHighTempDialog == null && f >= 75.0f) {
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
            int streamVolume = audioManager.getStreamVolume(4);
            setHighTemperatureAlarmVolume(audioManager, f);
            Ringtone highTemperatureAlarmRingTone = getHighTemperatureAlarmRingTone();
            SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext, R$style.Theme_SystemUI_Dialog_Alert);
            systemUIDialog.setCancelable(false);
            systemUIDialog.setTitle(R$string.zz_moto_high_temp_alarm_title);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.setMessage(this.mContext.getString(R$string.zz_moto_high_temp_alarm_notify_message));
            systemUIDialog.setPositiveButton(R$string.got_it, new PowerNotificationWarnings$$ExternalSyntheticLambda2(this));
            systemUIDialog.setOnDismissListener(new PowerNotificationWarnings$$ExternalSyntheticLambda11(this, highTemperatureAlarmRingTone, audioManager, streamVolume));
            systemUIDialog.getWindow().addFlags(2097280);
            systemUIDialog.show();
            highTemperatureAlarmRingTone.play();
            this.mUsbHighTempDialog = systemUIDialog;
            Events.writeEvent(19, 3, Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMotoUsbHighTemperatureAlarmInternal$8(DialogInterface dialogInterface, int i) {
        this.mUsbHighTempDialog = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMotoUsbHighTemperatureAlarmInternal$9(Ringtone ringtone, AudioManager audioManager, int i, DialogInterface dialogInterface) {
        if (ringtone != null) {
            ringtone.stop();
            audioManager.setStreamVolume(4, i, 0);
        }
        this.mUsbHighTempDialog = null;
        Events.writeEvent(20, 9, Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
    }

    private Ringtone getHighTemperatureAlarmRingTone() {
        Uri uri;
        Uri actualDefaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this.mContext, 4);
        if ("Osmium".equals(RingtoneManager.getRingtone(this.mContext, actualDefaultRingtoneUri).getTitle(this.mContext))) {
            uri = Uri.parse("android.resource://" + this.mContext.getPackageName() + "/" + R$raw.Krypton);
        } else {
            uri = Uri.parse("android.resource://" + this.mContext.getPackageName() + "/" + R$raw.Osmium);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(this.mContext, uri);
        ringtone.setStreamType(4);
        if (DEBUG) {
            Slog.d("PowerUI.Notification", "defaultAlarmUri:  : " + actualDefaultRingtoneUri + "defaultRingtone title: " + ringtone.getTitle(this.mContext));
        }
        return ringtone;
    }

    private void setHighTemperatureAlarmVolume(AudioManager audioManager, float f) {
        int i;
        int streamMaxVolume = audioManager.getStreamMaxVolume(4);
        int streamVolume = audioManager.getStreamVolume(4);
        if (f >= 95.0f) {
            i = streamMaxVolume;
        } else if (f >= 85.0f) {
            i = Math.max(streamMaxVolume - 1, streamVolume);
        } else {
            i = f >= 75.0f ? Math.max(streamMaxVolume - 2, streamVolume) : streamVolume;
        }
        if (DEBUG) {
            Slog.d("PowerUI.Notification", " maxVol: " + streamMaxVolume + " currentVol: " + streamVolume + " temperature: " + f + " vol:" + i);
        }
        audioManager.setStreamVolume(4, i, 0);
    }

    public boolean showMotoUsbLpdAlarm(boolean z, boolean z2) {
        if (!z) {
            if (this.mMotoUsbLpdDialog == null) {
                return false;
            }
            stopLpdAlarmRingtone();
            return true;
        } else if (z2) {
            this.mHandler.post(new PowerNotificationWarnings$$ExternalSyntheticLambda13(this));
            playLpdAlarmRingtone();
            return true;
        } else if (this.mMotoUsbLpdDialog == null) {
            return false;
        } else {
            stopLpdAlarmRingtone();
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: showMotoUsbLPDAlarmInternal */
    public void lambda$showMotoUsbLpdAlarm$10() {
        if (this.mMotoUsbLpdDialog == null) {
            SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext, R$style.Theme_SystemUI_Dialog_Alert);
            systemUIDialog.setCancelable(false);
            systemUIDialog.setTitle(R$string.zz_moto_usb_lpd_alarm_title);
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.setMessage(this.mContext.getString(R$string.zz_moto_usb_lpd_alarm_notify_message));
            systemUIDialog.setPositiveButton(R$string.got_it, new PowerNotificationWarnings$$ExternalSyntheticLambda4(this));
            systemUIDialog.setOnDismissListener(new PowerNotificationWarnings$$ExternalSyntheticLambda7(this));
            systemUIDialog.getWindow().addFlags(2097152);
            systemUIDialog.show();
            this.mMotoUsbLpdDialog = systemUIDialog;
            Slog.d("PowerUI.Notification", "show MotoUsbLpdAlarm, isKeyguardLocked: " + this.mKeyguard.isKeyguardLocked());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMotoUsbLPDAlarmInternal$11(DialogInterface dialogInterface, int i) {
        this.mMotoUsbLpdDialog = null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showMotoUsbLPDAlarmInternal$12(DialogInterface dialogInterface) {
        stopLpdAlarmRingtone();
        this.mMotoUsbLpdDialog = null;
        Slog.d("PowerUI.Notification", "dismiss MotoUsbLpdAlarm, isKeyguardLocked: " + this.mKeyguard.isKeyguardLocked());
    }

    private void setLpdAlarmVolume(AudioManager audioManager) {
        int streamMaxVolume = audioManager.getStreamMaxVolume(4);
        int streamVolume = audioManager.getStreamVolume(4);
        int max = Math.max(streamMaxVolume - 3, streamVolume);
        if (DEBUG) {
            Slog.d("PowerUI.Notification", "setLpdAlarmVolume maxVol: " + streamMaxVolume + " currentVol: " + streamVolume + " vol:" + max);
        }
        audioManager.setStreamVolume(4, max, 0);
    }

    private void playLpdAlarmRingtone() {
        if (this.mMotoUsbLpdAlarmRingtone == null) {
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
            this.mSavedVolumeLevel = audioManager.getStreamVolume(4);
            setLpdAlarmVolume(audioManager);
            Ringtone highTemperatureAlarmRingTone = getHighTemperatureAlarmRingTone();
            this.mMotoUsbLpdAlarmRingtone = highTemperatureAlarmRingTone;
            highTemperatureAlarmRingTone.play();
            Slog.d("PowerUI.Notification", "playLpdAlarmRingtone, isKeyguardLocked: " + this.mKeyguard.isKeyguardLocked());
        }
    }

    private void stopLpdAlarmRingtone() {
        if (this.mMotoUsbLpdAlarmRingtone != null) {
            this.mMotoUsbLpdAlarmRingtone.stop();
            ((AudioManager) this.mContext.getSystemService("audio")).setStreamVolume(4, this.mSavedVolumeLevel, 0);
            this.mMotoUsbLpdAlarmRingtone = null;
            Slog.d("PowerUI.Notification", "stopLpdAlarmRingtone, isKeyguardLocked: " + this.mKeyguard.isKeyguardLocked());
        }
    }

    public void updateLowBatteryWarning() {
        updateNotification();
    }

    public void dismissLowBatteryWarning() {
        if (DEBUG) {
            Slog.d("PowerUI.Notification", "dismissing low battery warning: level=" + this.mBatteryLevel);
        }
        dismissLowBatteryNotification();
    }

    /* access modifiers changed from: private */
    public void dismissLowBatteryNotification() {
        if (this.mWarning) {
            Slog.i("PowerUI.Notification", "dismissing low battery notification");
        }
        this.mWarning = false;
        updateNotification();
    }

    private boolean hasBatterySettings() {
        return this.mOpenBatterySettings.resolveActivity(this.mContext.getPackageManager()) != null;
    }

    public void showLowBatteryWarning(boolean z) {
        Slog.i("PowerUI.Notification", "show low battery warning: level=" + this.mBatteryLevel + " [" + this.mBucket + "] playSound=" + z);
        this.mPlaySound = z;
        this.mWarning = true;
        updateNotification();
    }

    public void dismissInvalidChargerWarning() {
        dismissInvalidChargerNotification();
    }

    private void dismissInvalidChargerNotification() {
        if (this.mInvalidCharger) {
            Slog.i("PowerUI.Notification", "dismissing invalid charger notification");
        }
        this.mInvalidCharger = false;
        updateNotification();
    }

    public void showInvalidChargerWarning() {
        this.mInvalidCharger = true;
        updateNotification();
    }

    /* access modifiers changed from: private */
    public void showAutoSaverSuggestion() {
        this.mShowAutoSaverSuggestion = true;
        updateNotification();
    }

    /* access modifiers changed from: private */
    public void dismissAutoSaverSuggestion() {
        this.mShowAutoSaverSuggestion = false;
        updateNotification();
    }

    public void userSwitched() {
        updateNotification();
    }

    /* access modifiers changed from: private */
    public void showStartSaverConfirmation(Bundle bundle) {
        if (this.mSaverConfirmation == null) {
            int i = bundle.getInt("extra_display_id", 0);
            boolean z = DEBUG;
            if (z) {
                Log.d("PowerUI.Notification", "showStartSaverConfirmation displayId :" + i);
            }
            Context context = this.mContext;
            if (i != 0) {
                Display display = this.mDisplayManager.getDisplay(i);
                if (display != null) {
                    context = this.mContext.createDisplayContext(display);
                } else if (z) {
                    Log.w("PowerUI.Notification", "showStartSaverConfirmation display is null :");
                    return;
                } else {
                    return;
                }
            }
            SystemUIDialog systemUIDialog = new SystemUIDialog(context);
            boolean z2 = bundle.getBoolean("extra_confirm_only");
            int i2 = bundle.getInt("extra_power_save_mode_trigger", 0);
            int i3 = bundle.getInt("extra_power_save_mode_trigger_level", 0);
            systemUIDialog.setMessage(getBatterySaverDescription());
            if (isEnglishLocale()) {
                systemUIDialog.setMessageHyphenationFrequency(0);
            }
            systemUIDialog.setMessageMovementMethod(LinkMovementMethod.getInstance());
            if (z2) {
                systemUIDialog.setTitle(R$string.battery_saver_confirmation_title_generic);
                systemUIDialog.setPositiveButton(17040057, new PowerNotificationWarnings$$ExternalSyntheticLambda5(this, i2, i3));
            } else {
                systemUIDialog.setTitle(R$string.battery_saver_confirmation_title);
                systemUIDialog.setPositiveButton(R$string.battery_saver_confirmation_ok, new PowerNotificationWarnings$$ExternalSyntheticLambda1(this));
                systemUIDialog.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            }
            systemUIDialog.setShowForAllUsers(true);
            systemUIDialog.setOnDismissListener(new PowerNotificationWarnings$$ExternalSyntheticLambda10(this));
            systemUIDialog.show();
            this.mSaverConfirmation = systemUIDialog;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showStartSaverConfirmation$13(int i, int i2, DialogInterface dialogInterface, int i3) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Settings.Global.putInt(contentResolver, "automatic_power_save_mode", i);
        Settings.Global.putInt(contentResolver, "low_power_trigger_level", i2);
        Settings.Secure.putIntForUser(contentResolver, "low_power_warning_acknowledged", 1, -2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showStartSaverConfirmation$14(DialogInterface dialogInterface, int i) {
        setSaverMode(true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showStartSaverConfirmation$15(DialogInterface dialogInterface) {
        this.mSaverConfirmation = null;
    }

    private boolean isEnglishLocale() {
        return Objects.equals(Locale.getDefault().getLanguage(), Locale.ENGLISH.getLanguage());
    }

    private CharSequence getBatterySaverDescription() {
        String charSequence = this.mContext.getText(R$string.help_uri_battery_saver_learn_more_link_target).toString();
        if (TextUtils.isEmpty(charSequence)) {
            return this.mContext.getText(17039781);
        }
        SpannableString spannableString = new SpannableString(this.mContext.getText(17039782));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannableString);
        for (Annotation annotation : (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class)) {
            if ("url".equals(annotation.getValue())) {
                int spanStart = spannableString.getSpanStart(annotation);
                int spanEnd = spannableString.getSpanEnd(annotation);
                C11333 r8 = new URLSpan(charSequence) {
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setUnderlineText(false);
                    }

                    public void onClick(View view) {
                        if (PowerNotificationWarnings.this.mSaverConfirmation != null) {
                            PowerNotificationWarnings.this.mSaverConfirmation.dismiss();
                        }
                        PowerNotificationWarnings.this.mContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS").setFlags(268435456));
                        Uri parse = Uri.parse(getURL());
                        Context context = view.getContext();
                        Intent flags = new Intent("android.intent.action.VIEW", parse).setFlags(268435456);
                        try {
                            context.startActivity(flags);
                        } catch (ActivityNotFoundException unused) {
                            Log.w("PowerUI.Notification", "Activity was not found for intent, " + flags.toString());
                        }
                    }
                };
                spannableStringBuilder.setSpan(r8, spanStart, spanEnd, spannableString.getSpanFlags(r8));
            }
        }
        return spannableStringBuilder;
    }

    /* access modifiers changed from: private */
    public void setSaverMode(boolean z, boolean z2) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, z, z2);
    }

    /* access modifiers changed from: private */
    public void startBatterySaverSchedulePage() {
        Intent intent = new Intent("com.android.settings.BATTERY_SAVER_SCHEDULE_SETTINGS");
        intent.setFlags(268468224);
        this.mActivityStarter.startActivity(intent, true);
    }

    private final class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void init() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("PNW.batterySettings");
            intentFilter.addAction("PNW.startSaver");
            intentFilter.addAction("PNW.dismissedWarning");
            intentFilter.addAction("PNW.clickedTempWarning");
            intentFilter.addAction("PNW.dismissedTempWarning");
            intentFilter.addAction("PNW.clickedThermalShutdownWarning");
            intentFilter.addAction("PNW.dismissedThermalShutdownWarning");
            intentFilter.addAction("PNW.startSaverConfirmation");
            intentFilter.addAction("PNW.autoSaverSuggestion");
            intentFilter.addAction("PNW.enableAutoSaver");
            intentFilter.addAction("PNW.autoSaverNoThanks");
            intentFilter.addAction("PNW.dismissAutoSaverSuggestion");
            PowerNotificationWarnings.this.mContext.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, "android.permission.DEVICE_POWER", PowerNotificationWarnings.this.mHandler);
            ActivityManagerWrapper unused = PowerNotificationWarnings.this.mActivityManagerWrapper = ActivityManagerWrapper.getInstance();
            DeviceProvisionedController unused2 = PowerNotificationWarnings.this.mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
            PowerNotificationWarnings powerNotificationWarnings = PowerNotificationWarnings.this;
            DisplayManager unused3 = powerNotificationWarnings.mDisplayManager = (DisplayManager) powerNotificationWarnings.mContext.getSystemService("display");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Slog.i("PowerUI.Notification", "Received " + action);
            if (action.equals("PNW.batterySettings")) {
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
                PowerNotificationWarnings.this.mContext.startActivityAsUser(PowerNotificationWarnings.this.mOpenBatterySettings, UserHandle.CURRENT);
            } else if (action.equals("PNW.startSaver")) {
                PowerNotificationWarnings.this.setSaverMode(true, true);
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
            } else if (action.equals("PNW.startSaverConfirmation")) {
                PowerNotificationWarnings.this.dismissLowBatteryNotification();
                PowerNotificationWarnings.this.showStartSaverConfirmation(intent.getExtras());
            } else if (action.equals("PNW.dismissedWarning")) {
                PowerNotificationWarnings.this.dismissLowBatteryWarning();
            } else if ("PNW.clickedTempWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissHighTemperatureWarningInternal();
                PowerNotificationWarnings.this.showHighTemperatureDialog();
            } else if ("PNW.dismissedTempWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissHighTemperatureWarningInternal();
            } else if ("PNW.clickedThermalShutdownWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissThermalShutdownWarning();
                PowerNotificationWarnings.this.showThermalShutdownDialog();
            } else if ("PNW.dismissedThermalShutdownWarning".equals(action)) {
                PowerNotificationWarnings.this.dismissThermalShutdownWarning();
            } else if ("PNW.autoSaverSuggestion".equals(action)) {
                PowerNotificationWarnings.this.showAutoSaverSuggestion();
            } else if ("PNW.dismissAutoSaverSuggestion".equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
            } else if ("PNW.enableAutoSaver".equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
                PowerNotificationWarnings.this.startBatterySaverSchedulePage();
            } else if ("PNW.autoSaverNoThanks".equals(action)) {
                PowerNotificationWarnings.this.dismissAutoSaverSuggestion();
                BatterySaverUtils.suppressAutoBatterySaver(context);
            }
        }
    }

    public void localeChanged() {
        updateNotification();
    }

    public void showTurboPowerToast(boolean z) {
        String str;
        if (!isUserSetupComplete()) {
            Slog.i("PowerUI.Notification", "TurChargerThermalWarning:  user setup not complete!");
            return;
        }
        if (z) {
            str = this.mContext.getResources().getString(R$string.zz_moto_turbo_charger_plugged_toast_text);
        } else {
            str = String.format(this.mContext.getResources().getString(R$string.zz_moto_turbo_charger_unplugged_toast_text), new Object[]{NumberFormat.getPercentInstance().format((double) (((float) this.mBatteryLevel) / 100.0f))});
        }
        if (!MotoFeature.getInstance(this.mContext).isSupportCli() || !MotoFeature.isLidClosed(this.mContext)) {
            View inflate = View.inflate(this.mContext, R$layout.zz_moto_toast_plus_icon, (ViewGroup) null);
            ((TextView) inflate.findViewById(R$id.message)).setText(str);
            Toast toast = new Toast(this.mContext);
            toast.setDuration(1);
            toast.setView(inflate);
            toast.getWindowParams().privateFlags |= 536870928;
            toast.show();
            return;
        }
        CliToast makeTextWithDrawable = CliToast.getInstance(this.mContext).makeTextWithDrawable((CharSequence) str, R$drawable.zz_moto_ic_turbopower, 1);
        makeTextWithDrawable.getWindowParams().privateFlags |= 536870928;
        makeTextWithDrawable.show();
    }

    public void showChargerThermalWarning(boolean z) {
        ChargerThermalWarningDialog chargerThermalWarningDialog = this.mChargerThermalWarningDialog;
        if (chargerThermalWarningDialog != null) {
            chargerThermalWarningDialog.dismiss();
            this.mChargerThermalWarningDialog = null;
        }
        if (!z) {
            return;
        }
        if (!isUserSetupComplete()) {
            Slog.i("PowerUI.Notification", "TurChargerThermalWarning:  user setup not complete!");
        } else if (isInCall()) {
            Slog.i("PowerUI.Notification", "TurChargerThermalWarning:  user in call!");
        } else {
            ChargerThermalWarningDialog chargerThermalWarningDialog2 = new ChargerThermalWarningDialog(R$string.zz_moto_turbo_charger_plugged_title, R$string.zz_moto_turbo_charger_plugged_text);
            this.mChargerThermalWarningDialog = chargerThermalWarningDialog2;
            chargerThermalWarningDialog2.show();
        }
    }

    private boolean isUserSetupComplete() {
        return this.mDeviceProvisionedController.isCurrentUserSetup();
    }

    private boolean isInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }

    private final class DismissReceiver extends BroadcastReceiver {
        private boolean mRegistered;
        private final ChargerThermalWarningDialog mWarningDialog;

        public DismissReceiver(ChargerThermalWarningDialog chargerThermalWarningDialog) {
            this.mWarningDialog = chargerThermalWarningDialog;
        }

        /* access modifiers changed from: package-private */
        public void register() {
            if (!this.mRegistered) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
                PowerNotificationWarnings.this.mContext.registerReceiverAsUser(this, UserHandle.CURRENT, intentFilter, (String) null, (Handler) null);
                this.mRegistered = true;
            }
        }

        /* access modifiers changed from: package-private */
        public void unregister() {
            if (this.mRegistered) {
                PowerNotificationWarnings.this.mContext.unregisterReceiver(this);
                this.mRegistered = false;
            }
        }

        public void onReceive(Context context, Intent intent) {
            ChargerThermalWarningDialog chargerThermalWarningDialog = this.mWarningDialog;
            if (chargerThermalWarningDialog != null) {
                chargerThermalWarningDialog.dismiss();
            }
        }
    }

    public void showWirelessChargerToast() {
        View inflate = View.inflate(this.mContext, R$layout.zz_moto_toast_wireless_charging, (ViewGroup) null);
        ((TextView) inflate.findViewById(R$id.message)).setText(R$string.wireless_toast_message);
        Toast toast = new Toast(this.mContext);
        toast.setDuration(1);
        toast.setGravity(16, 0, 0);
        toast.setView(inflate);
        WindowManager.LayoutParams windowParams = toast.getWindowParams();
        windowParams.privateFlags = 16 | windowParams.privateFlags;
        toast.show();
    }

    private class ChargerThermalWarningDialog {
        private Dialog mDialog;
        /* access modifiers changed from: private */
        public final DismissReceiver mDismissReceiver;
        private PhoneStateListener mPhoneStateListener;

        public ChargerThermalWarningDialog(int i, int i2) {
            this.mDismissReceiver = new DismissReceiver(this);
            AlertDialog create = new AlertDialog.Builder(PowerNotificationWarnings.this.mContext).setTitle(i).setMessage(i2).setPositiveButton(R$string.got_it, new DialogInterface.OnClickListener(PowerNotificationWarnings.this) {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Context access$400 = PowerNotificationWarnings.this.mContext;
                    Prefs.putBoolean(access$400, "TurboChargerAlertShowed" + String.valueOf(PowerNotificationWarnings.this.mActivityManagerWrapper.getCurrentUserId()), true);
                    ChargerThermalWarningDialog.this.listenForCallState(false);
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener(PowerNotificationWarnings.this) {
                public void onDismiss(DialogInterface dialogInterface) {
                    if (ChargerThermalWarningDialog.this.mDismissReceiver != null) {
                        ChargerThermalWarningDialog.this.mDismissReceiver.unregister();
                    } else {
                        Slog.e("PowerUI.Notification", "Receiver control should not be null");
                    }
                    ChargerThermalWarningDialog.this.listenForCallState(false);
                }
            }).create();
            this.mDialog = create;
            create.setCanceledOnTouchOutside(false);
            this.mDialog.getWindow().setGravity(80);
            this.mDialog.getWindow().setType(2003);
            WindowManager.LayoutParams attributes = this.mDialog.getWindow().getAttributes();
            attributes.layoutInDisplayCutoutMode = 3;
            attributes.setTitle("ChargerThermalWarningDialog");
            this.mDialog.getWindow().setAttributes(attributes);
        }

        public void show() {
            this.mDialog.show();
            listenForCallState(true);
            this.mDismissReceiver.register();
        }

        public void dismiss() {
            this.mDialog.dismiss();
        }

        /* access modifiers changed from: private */
        public void listenForCallState(boolean z) {
            if (z) {
                if (this.mPhoneStateListener == null) {
                    this.mPhoneStateListener = new PhoneStateListener() {
                        private int mCallState;

                        public void onCallStateChanged(int i, String str) {
                            Slog.i("PowerUI.Notification", "TurChargerThermalWarning: Call state changed: " + i);
                            if (this.mCallState != i) {
                                this.mCallState = i;
                                ChargerThermalWarningDialog.this.dismiss();
                            }
                        }
                    };
                    TelephonyManager.from(PowerNotificationWarnings.this.mContext).listen(this.mPhoneStateListener, 32);
                }
            } else if (this.mPhoneStateListener != null) {
                TelephonyManager.from(PowerNotificationWarnings.this.mContext).listen(this.mPhoneStateListener, 0);
                this.mPhoneStateListener = null;
            }
        }
    }
}
