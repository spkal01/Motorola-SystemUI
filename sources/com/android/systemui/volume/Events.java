package com.android.systemui.volume;

import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.systemui.plugins.VolumeDialogController;

public class Events {
    public static final String[] DISMISS_REASONS = {"unknown", "touch_outside", "volume_controller", "timeout", "screen_off", "settings_clicked", "done_clicked", "a11y_stream_changed", "output_chooser", "usb_temperature_below_threshold", "taskbar"};
    private static final String[] EVENT_TAGS = {"show_dialog", "dismiss_dialog", "active_stream_changed", "expand", "key", "collection_started", "collection_stopped", "icon_click", "settings_click", "touch_level_changed", "level_changed", "internal_ringer_mode_changed", "external_ringer_mode_changed", "zen_mode_changed", "suppressor_changed", "mute_changed", "touch_level_done", "zen_mode_config_changed", "ringer_toggle", "show_usb_overheat_alarm", "dismiss_usb_overheat_alarm", "odi_captions_click", "odi_captions_tooltip_click", "touch_multi_level_changed", "touch_multi_level_done"};
    public static final String[] SHOW_REASONS = {"unknown", "volume_changed", "remote_volume_changed", "usb_temperature_above_threshold", "taskbar"};
    private static final String TAG = Util.logTag(Events.class);
    public static Callback sCallback;
    @VisibleForTesting
    static MetricsLogger sLegacyLogger = new MetricsLogger();
    @VisibleForTesting
    static UiEventLogger sUiEventLogger = new UiEventLoggerImpl();

    public interface Callback {
        void writeEvent(long j, int i, Object[] objArr);

        void writeState(long j, VolumeDialogController.State state);
    }

    private static String ringerModeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "unknown" : "normal" : "vibrate" : "silent";
    }

    private static String zenModeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? "unknown" : "alarms" : "no_interruptions" : "important_interruptions" : "off";
    }

    @VisibleForTesting
    public enum VolumeDialogOpenEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        VOLUME_DIALOG_SHOW_VOLUME_CHANGED(128),
        VOLUME_DIALOG_SHOW_REMOTE_VOLUME_CHANGED(129),
        VOLUME_DIALOG_SHOW_USB_TEMP_ALARM_CHANGED(130);
        
        private final int mId;

        private VolumeDialogOpenEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        static VolumeDialogOpenEvent fromReasons(int i) {
            if (i == 1) {
                return VOLUME_DIALOG_SHOW_VOLUME_CHANGED;
            }
            if (i == 2) {
                return VOLUME_DIALOG_SHOW_REMOTE_VOLUME_CHANGED;
            }
            if (i != 3) {
                return INVALID;
            }
            return VOLUME_DIALOG_SHOW_USB_TEMP_ALARM_CHANGED;
        }
    }

    @VisibleForTesting
    public enum VolumeDialogCloseEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        VOLUME_DIALOG_DISMISS_TOUCH_OUTSIDE(134),
        VOLUME_DIALOG_DISMISS_SYSTEM(135),
        VOLUME_DIALOG_DISMISS_TIMEOUT(136),
        VOLUME_DIALOG_DISMISS_SCREEN_OFF(137),
        VOLUME_DIALOG_DISMISS_SETTINGS(138),
        VOLUME_DIALOG_DISMISS_STREAM_GONE(140),
        VOLUME_DIALOG_DISMISS_USB_TEMP_ALARM_CHANGED(142);
        
        private final int mId;

        private VolumeDialogCloseEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        static VolumeDialogCloseEvent fromReason(int i) {
            if (i == 1) {
                return VOLUME_DIALOG_DISMISS_TOUCH_OUTSIDE;
            }
            if (i == 2) {
                return VOLUME_DIALOG_DISMISS_SYSTEM;
            }
            if (i == 3) {
                return VOLUME_DIALOG_DISMISS_TIMEOUT;
            }
            if (i == 4) {
                return VOLUME_DIALOG_DISMISS_SCREEN_OFF;
            }
            if (i == 5) {
                return VOLUME_DIALOG_DISMISS_SETTINGS;
            }
            if (i == 7) {
                return VOLUME_DIALOG_DISMISS_STREAM_GONE;
            }
            if (i != 9) {
                return INVALID;
            }
            return VOLUME_DIALOG_DISMISS_USB_TEMP_ALARM_CHANGED;
        }
    }

    @VisibleForTesting
    public enum VolumeDialogEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        VOLUME_DIALOG_SETTINGS_CLICK(143),
        VOLUME_DIALOG_EXPAND_DETAILS(144),
        VOLUME_DIALOG_COLLAPSE_DETAILS(145),
        VOLUME_DIALOG_ACTIVE_STREAM_CHANGED(146),
        VOLUME_DIALOG_MUTE_STREAM(147),
        VOLUME_DIALOG_UNMUTE_STREAM(148),
        VOLUME_DIALOG_TO_VIBRATE_STREAM(149),
        VOLUME_DIALOG_SLIDER(150),
        VOLUME_DIALOG_SLIDER_TO_ZERO(151),
        VOLUME_KEY_TO_ZERO(152),
        VOLUME_KEY(153),
        RINGER_MODE_SILENT(154),
        RINGER_MODE_VIBRATE(155),
        RINGER_MODE_NORMAL(334),
        USB_OVERHEAT_ALARM(160),
        USB_OVERHEAT_ALARM_DISMISSED(161);
        
        private final int mId;

        private VolumeDialogEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        static VolumeDialogEvent fromIconState(int i) {
            if (i == 1) {
                return VOLUME_DIALOG_UNMUTE_STREAM;
            }
            if (i == 2) {
                return VOLUME_DIALOG_MUTE_STREAM;
            }
            if (i != 3) {
                return INVALID;
            }
            return VOLUME_DIALOG_TO_VIBRATE_STREAM;
        }

        static VolumeDialogEvent fromSliderLevel(int i) {
            return i == 0 ? VOLUME_DIALOG_SLIDER_TO_ZERO : VOLUME_DIALOG_SLIDER;
        }

        static VolumeDialogEvent fromKeyLevel(int i) {
            return i == 0 ? VOLUME_KEY_TO_ZERO : VOLUME_KEY;
        }

        static VolumeDialogEvent fromRingerMode(int i) {
            if (i == 0) {
                return RINGER_MODE_SILENT;
            }
            if (i == 1) {
                return RINGER_MODE_VIBRATE;
            }
            if (i != 2) {
                return INVALID;
            }
            return RINGER_MODE_NORMAL;
        }
    }

    @VisibleForTesting
    public enum ZenModeEvent implements UiEventLogger.UiEventEnum {
        INVALID(0),
        ZEN_MODE_OFF(335),
        ZEN_MODE_IMPORTANT_ONLY(157),
        ZEN_MODE_ALARMS_ONLY(158),
        ZEN_MODE_NO_INTERRUPTIONS(159);
        
        private final int mId;

        private ZenModeEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }

        static ZenModeEvent fromZenMode(int i) {
            if (i == 0) {
                return ZEN_MODE_OFF;
            }
            if (i == 1) {
                return ZEN_MODE_IMPORTANT_ONLY;
            }
            if (i == 2) {
                return ZEN_MODE_NO_INTERRUPTIONS;
            }
            if (i != 3) {
                return INVALID;
            }
            return ZEN_MODE_ALARMS_ONLY;
        }
    }

    public static void writeEvent(int i, Object... objArr) {
        long currentTimeMillis = System.currentTimeMillis();
        Log.i(TAG, logEvent(i, objArr));
        Callback callback = sCallback;
        if (callback != null) {
            callback.writeEvent(currentTimeMillis, i, objArr);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0128, code lost:
        r1.append(ringerModeToString(r8[0].intValue()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x013a, code lost:
        if (r8.length <= 1) goto L_0x026a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x013c, code lost:
        r1.append(android.media.AudioSystem.streamToString(r8[0].intValue()));
        r1.append(' ');
        r1.append(r8[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x026e, code lost:
        return r1.toString();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String logEvent(int r7, java.lang.Object... r8) {
        /*
            java.lang.String[] r0 = EVENT_TAGS
            int r1 = r0.length
            if (r7 < r1) goto L_0x0008
            java.lang.String r7 = ""
            return r7
        L_0x0008:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "writeEvent "
            r1.<init>(r2)
            r0 = r0[r7]
            r1.append(r0)
            if (r8 == 0) goto L_0x026f
            int r0 = r8.length
            if (r0 != 0) goto L_0x001b
            goto L_0x026f
        L_0x001b:
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 1457(0x5b1, float:2.042E-42)
            r2 = 207(0xcf, float:2.9E-43)
            java.lang.String r3 = " keyguard="
            r4 = 32
            r5 = 0
            r6 = 1
            switch(r7) {
                case 0: goto L_0x0231;
                case 1: goto L_0x020f;
                case 2: goto L_0x01ed;
                case 3: goto L_0x01c9;
                case 4: goto L_0x0193;
                case 5: goto L_0x002d;
                case 6: goto L_0x002d;
                case 7: goto L_0x0155;
                case 8: goto L_0x002d;
                case 9: goto L_0x0139;
                case 10: goto L_0x0139;
                case 11: goto L_0x0128;
                case 12: goto L_0x0119;
                case 13: goto L_0x00fb;
                case 14: goto L_0x00e9;
                case 15: goto L_0x0139;
                case 16: goto L_0x00c9;
                case 17: goto L_0x002d;
                case 18: goto L_0x00a0;
                case 19: goto L_0x006b;
                case 20: goto L_0x0036;
                case 21: goto L_0x002d;
                case 22: goto L_0x002d;
                case 23: goto L_0x00e9;
                case 24: goto L_0x00e9;
                default: goto L_0x002d;
            }
        L_0x002d:
            java.util.List r7 = java.util.Arrays.asList(r8)
            r1.append(r7)
            goto L_0x026a
        L_0x0036:
            com.android.internal.logging.MetricsLogger r7 = sLegacyLogger
            r7.hidden(r0)
            com.android.internal.logging.UiEventLogger r7 = sUiEventLogger
            com.android.systemui.volume.Events$VolumeDialogEvent r0 = com.android.systemui.volume.Events.VolumeDialogEvent.USB_OVERHEAT_ALARM_DISMISSED
            r7.log(r0)
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r6]
            java.lang.Boolean r7 = (java.lang.Boolean) r7
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            boolean r2 = r7.booleanValue()
            java.lang.String r4 = "dismiss_usb_overheat_alarm"
            r0.histogram(r4, r2)
            r8 = r8[r5]
            java.lang.Integer r8 = (java.lang.Integer) r8
            java.lang.String[] r0 = DISMISS_REASONS
            int r8 = r8.intValue()
            r8 = r0[r8]
            r1.append(r8)
            r1.append(r3)
            r1.append(r7)
            goto L_0x026a
        L_0x006b:
            com.android.internal.logging.MetricsLogger r7 = sLegacyLogger
            r7.visible(r0)
            com.android.internal.logging.UiEventLogger r7 = sUiEventLogger
            com.android.systemui.volume.Events$VolumeDialogEvent r0 = com.android.systemui.volume.Events.VolumeDialogEvent.USB_OVERHEAT_ALARM
            r7.log(r0)
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r6]
            java.lang.Boolean r7 = (java.lang.Boolean) r7
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            boolean r2 = r7.booleanValue()
            java.lang.String r4 = "show_usb_overheat_alarm"
            r0.histogram(r4, r2)
            r8 = r8[r5]
            java.lang.Integer r8 = (java.lang.Integer) r8
            java.lang.String[] r0 = SHOW_REASONS
            int r8 = r8.intValue()
            r8 = r0[r8]
            r1.append(r8)
            r1.append(r3)
            r1.append(r7)
            goto L_0x026a
        L_0x00a0:
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.MetricsLogger r8 = sLegacyLogger
            r0 = 1385(0x569, float:1.941E-42)
            int r2 = r7.intValue()
            r8.action(r0, r2)
            com.android.internal.logging.UiEventLogger r8 = sUiEventLogger
            int r0 = r7.intValue()
            com.android.systemui.volume.Events$VolumeDialogEvent r0 = com.android.systemui.volume.Events.VolumeDialogEvent.fromRingerMode(r0)
            r8.log(r0)
            int r7 = r7.intValue()
            java.lang.String r7 = ringerModeToString(r7)
            r1.append(r7)
            goto L_0x026a
        L_0x00c9:
            int r7 = r8.length
            if (r7 <= r6) goto L_0x0139
            r7 = r8[r6]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            r2 = 209(0xd1, float:2.93E-43)
            int r3 = r7.intValue()
            r0.action(r2, r3)
            com.android.internal.logging.UiEventLogger r0 = sUiEventLogger
            int r7 = r7.intValue()
            com.android.systemui.volume.Events$VolumeDialogEvent r7 = com.android.systemui.volume.Events.VolumeDialogEvent.fromSliderLevel(r7)
            r0.log(r7)
            goto L_0x0139
        L_0x00e9:
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r5]
            r1.append(r7)
            r1.append(r4)
            r7 = r8[r6]
            r1.append(r7)
            goto L_0x026a
        L_0x00fb:
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r8 = r7.intValue()
            java.lang.String r8 = zenModeToString(r8)
            r1.append(r8)
            com.android.internal.logging.UiEventLogger r8 = sUiEventLogger
            int r7 = r7.intValue()
            com.android.systemui.volume.Events$ZenModeEvent r7 = com.android.systemui.volume.Events.ZenModeEvent.fromZenMode(r7)
            r8.log(r7)
            goto L_0x026a
        L_0x0119:
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            r2 = 213(0xd5, float:2.98E-43)
            int r7 = r7.intValue()
            r0.action(r2, r7)
        L_0x0128:
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            java.lang.String r7 = ringerModeToString(r7)
            r1.append(r7)
            goto L_0x026a
        L_0x0139:
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            java.lang.String r7 = android.media.AudioSystem.streamToString(r7)
            r1.append(r7)
            r1.append(r4)
            r7 = r8[r6]
            r1.append(r7)
            goto L_0x026a
        L_0x0155:
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            r2 = 212(0xd4, float:2.97E-43)
            int r3 = r7.intValue()
            r0.action(r2, r3)
            r8 = r8[r6]
            java.lang.Integer r8 = (java.lang.Integer) r8
            com.android.internal.logging.UiEventLogger r0 = sUiEventLogger
            int r2 = r8.intValue()
            com.android.systemui.volume.Events$VolumeDialogEvent r2 = com.android.systemui.volume.Events.VolumeDialogEvent.fromIconState(r2)
            r0.log(r2)
            int r7 = r7.intValue()
            java.lang.String r7 = android.media.AudioSystem.streamToString(r7)
            r1.append(r7)
            r1.append(r4)
            int r7 = r8.intValue()
            java.lang.String r7 = iconStateToString(r7)
            r1.append(r7)
            goto L_0x026a
        L_0x0193:
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            r2 = 211(0xd3, float:2.96E-43)
            int r3 = r7.intValue()
            r0.action(r2, r3)
            r8 = r8[r6]
            java.lang.Integer r8 = (java.lang.Integer) r8
            com.android.internal.logging.UiEventLogger r0 = sUiEventLogger
            int r2 = r8.intValue()
            com.android.systemui.volume.Events$VolumeDialogEvent r2 = com.android.systemui.volume.Events.VolumeDialogEvent.fromKeyLevel(r2)
            r0.log(r2)
            int r7 = r7.intValue()
            java.lang.String r7 = android.media.AudioSystem.streamToString(r7)
            r1.append(r7)
            r1.append(r4)
            r1.append(r8)
            goto L_0x026a
        L_0x01c9:
            r7 = r8[r5]
            java.lang.Boolean r7 = (java.lang.Boolean) r7
            com.android.internal.logging.MetricsLogger r8 = sLegacyLogger
            r0 = 208(0xd0, float:2.91E-43)
            boolean r2 = r7.booleanValue()
            r8.visibility(r0, r2)
            com.android.internal.logging.UiEventLogger r8 = sUiEventLogger
            boolean r0 = r7.booleanValue()
            if (r0 == 0) goto L_0x01e3
            com.android.systemui.volume.Events$VolumeDialogEvent r0 = com.android.systemui.volume.Events.VolumeDialogEvent.VOLUME_DIALOG_EXPAND_DETAILS
            goto L_0x01e5
        L_0x01e3:
            com.android.systemui.volume.Events$VolumeDialogEvent r0 = com.android.systemui.volume.Events.VolumeDialogEvent.VOLUME_DIALOG_COLLAPSE_DETAILS
        L_0x01e5:
            r8.log(r0)
            r1.append(r7)
            goto L_0x026a
        L_0x01ed:
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.MetricsLogger r8 = sLegacyLogger
            r0 = 210(0xd2, float:2.94E-43)
            int r2 = r7.intValue()
            r8.action(r0, r2)
            com.android.internal.logging.UiEventLogger r8 = sUiEventLogger
            com.android.systemui.volume.Events$VolumeDialogEvent r0 = com.android.systemui.volume.Events.VolumeDialogEvent.VOLUME_DIALOG_ACTIVE_STREAM_CHANGED
            r8.log(r0)
            int r7 = r7.intValue()
            java.lang.String r7 = android.media.AudioSystem.streamToString(r7)
            r1.append(r7)
            goto L_0x026a
        L_0x020f:
            com.android.internal.logging.MetricsLogger r7 = sLegacyLogger
            r7.hidden(r2)
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            com.android.internal.logging.UiEventLogger r8 = sUiEventLogger
            int r0 = r7.intValue()
            com.android.systemui.volume.Events$VolumeDialogCloseEvent r0 = com.android.systemui.volume.Events.VolumeDialogCloseEvent.fromReason(r0)
            r8.log(r0)
            java.lang.String[] r8 = DISMISS_REASONS
            int r7 = r7.intValue()
            r7 = r8[r7]
            r1.append(r7)
            goto L_0x026a
        L_0x0231:
            com.android.internal.logging.MetricsLogger r7 = sLegacyLogger
            r7.visible(r2)
            int r7 = r8.length
            if (r7 <= r6) goto L_0x026a
            r7 = r8[r5]
            java.lang.Integer r7 = (java.lang.Integer) r7
            r8 = r8[r6]
            java.lang.Boolean r8 = (java.lang.Boolean) r8
            com.android.internal.logging.MetricsLogger r0 = sLegacyLogger
            boolean r2 = r8.booleanValue()
            java.lang.String r4 = "volume_from_keyguard"
            r0.histogram(r4, r2)
            com.android.internal.logging.UiEventLogger r0 = sUiEventLogger
            int r2 = r7.intValue()
            com.android.systemui.volume.Events$VolumeDialogOpenEvent r2 = com.android.systemui.volume.Events.VolumeDialogOpenEvent.fromReasons(r2)
            r0.log(r2)
            java.lang.String[] r0 = SHOW_REASONS
            int r7 = r7.intValue()
            r7 = r0[r7]
            r1.append(r7)
            r1.append(r3)
            r1.append(r8)
        L_0x026a:
            java.lang.String r7 = r1.toString()
            return r7
        L_0x026f:
            r8 = 8
            if (r7 != r8) goto L_0x0281
            com.android.internal.logging.MetricsLogger r7 = sLegacyLogger
            r8 = 1386(0x56a, float:1.942E-42)
            r7.action(r8)
            com.android.internal.logging.UiEventLogger r7 = sUiEventLogger
            com.android.systemui.volume.Events$VolumeDialogEvent r8 = com.android.systemui.volume.Events.VolumeDialogEvent.VOLUME_DIALOG_SETTINGS_CLICK
            r7.log(r8)
        L_0x0281:
            java.lang.String r7 = r1.toString()
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.volume.Events.logEvent(int, java.lang.Object[]):java.lang.String");
    }

    public static void writeState(long j, VolumeDialogController.State state) {
        Callback callback = sCallback;
        if (callback != null) {
            callback.writeState(j, state);
        }
    }

    private static String iconStateToString(int i) {
        if (i == 1) {
            return "unmute";
        }
        if (i == 2) {
            return "mute";
        }
        if (i == 3) {
            return "vibrate";
        }
        return "unknown_state_" + i;
    }
}
