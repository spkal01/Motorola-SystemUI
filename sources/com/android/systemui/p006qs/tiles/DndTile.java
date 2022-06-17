package com.android.systemui.p006qs.tiles;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.notification.EnableZenModeDialog;
import com.android.systemui.Prefs;
import com.android.systemui.R$drawable;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.SysUIToast;
import com.android.systemui.moto.CliAlertDialog;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.DetailAdapter;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.volume.ZenModePanel;
import com.motorola.android.provider.MotorolaSettings;

/* renamed from: com.android.systemui.qs.tiles.DndTile */
public class DndTile extends QSTileImpl<QSTile.BooleanState> {
    /* access modifiers changed from: private */
    public static final Intent ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");
    /* access modifiers changed from: private */
    public static final Intent ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
    /* access modifiers changed from: private */
    public final ZenModeController mController;
    /* access modifiers changed from: private */
    public final DndDetailAdapter mDetailAdapter;
    private boolean mListening;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            if ("DndTileCombinedIcon".equals(str) || "DndTileVisible".equals(str)) {
                DndTile.this.refreshState();
            }
        }
    };
    private final SharedPreferences mSharedPreferences;
    /* access modifiers changed from: private */
    public boolean mShowingDetail;
    private final ZenModeController.Callback mZenCallback;
    /* access modifiers changed from: private */
    public final ZenModePanel.Callback mZenModePanelCallback;

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowActionBarOverlay;
    }

    public DndTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, ZenModeController zenModeController, SharedPreferences sharedPreferences) {
        super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
        C12543 r1 = new ZenModeController.Callback() {
            public void onZenChanged(int i) {
                DndTile.this.refreshState(Integer.valueOf(i));
                if (DndTile.this.isShowingDetail()) {
                    DndTile.this.mDetailAdapter.updatePanel();
                }
            }

            public void onConfigChanged(ZenModeConfig zenModeConfig) {
                if (DndTile.this.isShowingDetail()) {
                    DndTile.this.mDetailAdapter.updatePanel();
                }
            }
        };
        this.mZenCallback = r1;
        this.mZenModePanelCallback = new ZenModePanel.Callback() {
            public void onExpanded(boolean z) {
            }

            public void onInteraction() {
            }

            public void onPrioritySettings() {
                DndTile.this.mActivityStarter.postStartActivityDismissingKeyguard(DndTile.ZEN_PRIORITY_SETTINGS, 0);
            }
        };
        this.mController = zenModeController;
        this.mSharedPreferences = sharedPreferences;
        this.mDetailAdapter = new DndDetailAdapter();
        zenModeController.observe(getLifecycle(), r1);
    }

    public static void setVisible(Context context, boolean z) {
        Prefs.putBoolean(context, "DndTileVisible", z);
    }

    public static boolean isVisible(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("DndTileVisible", false);
    }

    public static void setCombinedIcon(Context context, boolean z) {
        Prefs.putBoolean(context, "DndTileCombinedIcon", z);
    }

    public static boolean isCombinedIcon(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("DndTileCombinedIcon", false);
    }

    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    public Intent getLongClickIntent() {
        return ZEN_SETTINGS;
    }

    /* access modifiers changed from: protected */
    public void handleClick(View view) {
        if (((QSTile.BooleanState) this.mState).value) {
            MotorolaSettings.Secure.putIntForUser(this.mContext.getContentResolver(), "ftm_fdn_dnd_turned_off", 1, -2);
            this.mController.setZen(0, (Uri) null, this.TAG);
            return;
        }
        showDetail(true);
    }

    public void showDetail(boolean z) {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_duration", 0);
        if ((Settings.Secure.getInt(this.mContext.getContentResolver(), "show_zen_upgrade_notification", 0) == 0 || Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_settings_updated", 0) == 1) ? false : true) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "show_zen_upgrade_notification", 0);
            this.mController.setZen(1, (Uri) null, this.TAG);
            Intent intent = new Intent("android.settings.ZEN_MODE_ONBOARDING");
            intent.addFlags(268468224);
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        } else if (i == -1) {
            this.mUiHandler.post(new DndTile$$ExternalSyntheticLambda1(this));
        } else if (i != 0) {
            this.mController.setZen(1, ZenModeConfig.toTimeCondition(this.mContext, i, this.mHost.getUserId(), true).id, this.TAG);
        } else {
            this.mController.setZen(1, (Uri) null, this.TAG);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showDetail$1() {
        Context context = this.mContext;
        if (CliAlertDialog.canShowOnCliDisplay(context)) {
            context = MotoFeature.getCliContext(context);
            context.setTheme(this.mContext.getThemeResId());
        }
        Dialog createDialog = new EnableZenModeDialog(context).createDialog();
        createDialog.getWindow().setType(2009);
        SystemUIDialog.setShowForAllUsers(createDialog, true);
        SystemUIDialog.registerDismissListener(createDialog);
        SystemUIDialog.setWindowOnTop(createDialog);
        this.mUiHandler.post(new DndTile$$ExternalSyntheticLambda0(createDialog));
        this.mHost.collapsePanels();
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick(View view) {
        if (this.mController.isVolumeRestricted()) {
            this.mHost.collapsePanels();
            Context context = this.mContext;
            SysUIToast.makeText(context, (CharSequence) context.getString(17040173), 1).show();
        } else if (!((QSTile.BooleanState) this.mState).value) {
            this.mController.addCallback(new ZenModeController.Callback() {
                public void onZenChanged(int i) {
                    DndTile.this.mController.removeCallback(this);
                    DndTile.this.showDetail(true);
                }
            });
            this.mController.setZen(1, (Uri) null, this.TAG);
        } else {
            showDetail(true);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(R$string.quick_settings_dnd_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(QSTile.BooleanState booleanState, Object obj) {
        ZenModeController zenModeController = this.mController;
        if (zenModeController != null) {
            int intValue = obj instanceof Integer ? ((Integer) obj).intValue() : zenModeController.getZen();
            boolean z = intValue != 0;
            boolean z2 = booleanState.value != z;
            if (booleanState.slash == null) {
                booleanState.slash = new QSTile.SlashState();
            }
            booleanState.dualTarget = true;
            booleanState.value = z;
            booleanState.state = z ? 2 : 1;
            booleanState.slash.isSlashed = !z;
            booleanState.label = getTileLabel();
            booleanState.secondaryLabel = TextUtils.emptyIfNull(ZenModeConfig.getDescription(this.mContext, intValue != 0, this.mController.getConfig(), false));
            booleanState.icon = QSTileImpl.ResourceIcon.get(17303697);
            checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_adjust_volume");
            if (intValue == 1) {
                booleanState.contentDescription = this.mContext.getString(R$string.accessibility_quick_settings_dnd) + ", " + booleanState.secondaryLabel;
            } else if (intValue == 2) {
                booleanState.contentDescription = this.mContext.getString(R$string.accessibility_quick_settings_dnd) + ", " + this.mContext.getString(R$string.accessibility_quick_settings_dnd_none_on) + ", " + booleanState.secondaryLabel;
            } else if (intValue != 3) {
                booleanState.contentDescription = this.mContext.getString(R$string.accessibility_quick_settings_dnd);
            } else {
                booleanState.contentDescription = this.mContext.getString(R$string.accessibility_quick_settings_dnd) + ", " + this.mContext.getString(R$string.accessibility_quick_settings_dnd_alarms_on) + ", " + booleanState.secondaryLabel;
            }
            if (z2) {
                fireToggleStateChanged(booleanState.value);
            }
            booleanState.dualLabelContentDescription = this.mContext.getResources().getString(R$string.accessibility_quick_settings_open_settings, new Object[]{getTileLabel()});
            booleanState.expandedAccessibilityClassName = Switch.class.getName();
        }
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R$string.accessibility_quick_settings_dnd_changed_on);
        }
        return this.mContext.getString(R$string.accessibility_quick_settings_dnd_changed_off);
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                refreshState();
                Prefs.registerListener(this.mContext, this.mPrefListener);
                return;
            }
            Prefs.unregisterListener(this.mContext, this.mPrefListener);
        }
    }

    public boolean isAvailable() {
        return isVisible(this.mSharedPreferences);
    }

    /* renamed from: com.android.systemui.qs.tiles.DndTile$DndDetailAdapter */
    private final class DndDetailAdapter implements DetailAdapter, View.OnAttachStateChangeListener {
        private boolean mAuto;
        private ZenModePanel mZenPanel;

        public int getMetricsCategory() {
            return 149;
        }

        private DndDetailAdapter() {
        }

        public CharSequence getTitle() {
            return DndTile.this.mContext.getString(R$string.quick_settings_dnd_label);
        }

        public Boolean getToggleState() {
            return Boolean.valueOf(((QSTile.BooleanState) DndTile.this.mState).value);
        }

        public Intent getSettingsIntent() {
            return DndTile.ZEN_SETTINGS;
        }

        public void setToggleState(boolean z) {
            MetricsLogger.action(DndTile.this.mContext, 166, z);
            if (!z) {
                MotorolaSettings.Secure.putIntForUser(DndTile.this.mContext.getContentResolver(), "ftm_fdn_dnd_turned_off", 1, -2);
                DndTile.this.mController.setZen(0, (Uri) null, DndTile.this.TAG);
                this.mAuto = false;
                return;
            }
            DndTile.this.mController.setZen(1, (Uri) null, DndTile.this.TAG);
        }

        public View createDetailView(Context context, View view, ViewGroup viewGroup) {
            ZenModePanel zenModePanel;
            if (view != null) {
                zenModePanel = (ZenModePanel) view;
            } else {
                zenModePanel = (ZenModePanel) LayoutInflater.from(context).inflate(R$layout.zen_mode_panel, viewGroup, false);
            }
            this.mZenPanel = zenModePanel;
            if (view == null) {
                zenModePanel.init(DndTile.this.mController);
                this.mZenPanel.addOnAttachStateChangeListener(this);
                this.mZenPanel.setCallback(DndTile.this.mZenModePanelCallback);
                this.mZenPanel.setEmptyState(R$drawable.ic_qs_dnd_detail_empty, R$string.dnd_is_off);
            }
            updatePanel();
            return this.mZenPanel;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x002a, code lost:
            r2 = r2.enabler;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updatePanel() {
            /*
                r6 = this;
                com.android.systemui.volume.ZenModePanel r0 = r6.mZenPanel
                if (r0 != 0) goto L_0x0005
                return
            L_0x0005:
                r0 = 0
                r6.mAuto = r0
                com.android.systemui.qs.tiles.DndTile r1 = com.android.systemui.p006qs.tiles.DndTile.this
                com.android.systemui.statusbar.policy.ZenModeController r1 = r1.mController
                int r1 = r1.getZen()
                if (r1 != 0) goto L_0x001c
                com.android.systemui.volume.ZenModePanel r6 = r6.mZenPanel
                r0 = 2
                r6.setState(r0)
                goto L_0x0090
            L_0x001c:
                com.android.systemui.qs.tiles.DndTile r1 = com.android.systemui.p006qs.tiles.DndTile.this
                com.android.systemui.statusbar.policy.ZenModeController r1 = r1.mController
                android.service.notification.ZenModeConfig r1 = r1.getConfig()
                android.service.notification.ZenModeConfig$ZenRule r2 = r1.manualRule
                if (r2 == 0) goto L_0x0033
                java.lang.String r2 = r2.enabler
                if (r2 == 0) goto L_0x0033
                java.lang.String r2 = r6.getOwnerCaption(r2)
                goto L_0x0035
            L_0x0033:
                java.lang.String r2 = ""
            L_0x0035:
                android.util.ArrayMap r1 = r1.automaticRules
                java.util.Collection r1 = r1.values()
                java.util.Iterator r1 = r1.iterator()
            L_0x003f:
                boolean r3 = r1.hasNext()
                r4 = 1
                if (r3 == 0) goto L_0x0078
                java.lang.Object r3 = r1.next()
                android.service.notification.ZenModeConfig$ZenRule r3 = (android.service.notification.ZenModeConfig.ZenRule) r3
                boolean r5 = r3.isAutomaticActive()
                if (r5 == 0) goto L_0x003f
                boolean r2 = r2.isEmpty()
                if (r2 == 0) goto L_0x006b
                com.android.systemui.qs.tiles.DndTile r2 = com.android.systemui.p006qs.tiles.DndTile.this
                android.content.Context r2 = r2.mContext
                int r5 = com.android.systemui.R$string.qs_dnd_prompt_auto_rule
                java.lang.Object[] r4 = new java.lang.Object[r4]
                java.lang.String r3 = r3.name
                r4[r0] = r3
                java.lang.String r2 = r2.getString(r5, r4)
                goto L_0x003f
            L_0x006b:
                com.android.systemui.qs.tiles.DndTile r2 = com.android.systemui.p006qs.tiles.DndTile.this
                android.content.Context r2 = r2.mContext
                int r3 = com.android.systemui.R$string.qs_dnd_prompt_auto_rule_app
                java.lang.String r2 = r2.getString(r3)
                goto L_0x003f
            L_0x0078:
                boolean r1 = r2.isEmpty()
                if (r1 == 0) goto L_0x0084
                com.android.systemui.volume.ZenModePanel r6 = r6.mZenPanel
                r6.setState(r0)
                goto L_0x0090
            L_0x0084:
                r6.mAuto = r4
                com.android.systemui.volume.ZenModePanel r0 = r6.mZenPanel
                r0.setState(r4)
                com.android.systemui.volume.ZenModePanel r6 = r6.mZenPanel
                r6.setAutoText(r2)
            L_0x0090:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p006qs.tiles.DndTile.DndDetailAdapter.updatePanel():void");
        }

        private String getOwnerCaption(String str) {
            CharSequence loadLabel;
            PackageManager packageManager = DndTile.this.mContext.getPackageManager();
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                if (applicationInfo == null || (loadLabel = applicationInfo.loadLabel(packageManager)) == null) {
                    return "";
                }
                String trim = loadLabel.toString().trim();
                return DndTile.this.mContext.getString(R$string.qs_dnd_prompt_app, new Object[]{trim});
            } catch (Throwable th) {
                Slog.w(DndTile.this.TAG, "Error loading owner caption", th);
                return "";
            }
        }

        public void onViewAttachedToWindow(View view) {
            boolean unused = DndTile.this.mShowingDetail = true;
        }

        public void onViewDetachedFromWindow(View view) {
            boolean unused = DndTile.this.mShowingDetail = false;
            this.mZenPanel = null;
        }
    }
}
