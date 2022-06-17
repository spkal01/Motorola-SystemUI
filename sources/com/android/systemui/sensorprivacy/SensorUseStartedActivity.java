package com.android.systemui.sensorprivacy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import com.android.internal.app.AlertActivity;
import com.android.internal.util.FrameworkStatsLog;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.policy.IndividualSensorPrivacyController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SensorUseStartedActivity.kt */
public final class SensorUseStartedActivity extends AlertActivity implements DialogInterface.OnClickListener {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    private static final String LOG_TAG = SensorUseStartedActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    @NotNull
    public final Handler bgHandler;
    @NotNull
    private final KeyguardDismissUtil keyguardDismissUtil;
    @NotNull
    private final KeyguardStateController keyguardStateController;
    /* access modifiers changed from: private */
    public int sensor = -1;
    /* access modifiers changed from: private */
    @NotNull
    public final IndividualSensorPrivacyController sensorPrivacyController;
    private IndividualSensorPrivacyController.Callback sensorPrivacyListener;
    /* access modifiers changed from: private */
    public String sensorUsePackageName;
    private boolean unsuppressImmediately;

    public void onBackPressed() {
    }

    public SensorUseStartedActivity(@NotNull IndividualSensorPrivacyController individualSensorPrivacyController, @NotNull KeyguardStateController keyguardStateController2, @NotNull KeyguardDismissUtil keyguardDismissUtil2, @NotNull Handler handler) {
        Intrinsics.checkNotNullParameter(individualSensorPrivacyController, "sensorPrivacyController");
        Intrinsics.checkNotNullParameter(keyguardStateController2, "keyguardStateController");
        Intrinsics.checkNotNullParameter(keyguardDismissUtil2, "keyguardDismissUtil");
        Intrinsics.checkNotNullParameter(handler, "bgHandler");
        this.sensorPrivacyController = individualSensorPrivacyController;
        this.keyguardStateController = keyguardStateController2;
        this.keyguardDismissUtil = keyguardDismissUtil2;
        this.bgHandler = handler;
    }

    /* compiled from: SensorUseStartedActivity.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00de A[Catch:{ NameNotFoundException -> 0x012d }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ea A[Catch:{ NameNotFoundException -> 0x012d }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00f6 A[Catch:{ NameNotFoundException -> 0x012d }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0127  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(@org.jetbrains.annotations.Nullable android.os.Bundle r9) {
        /*
            r8 = this;
            com.android.systemui.sensorprivacy.SensorUseStartedActivity.super.onCreate(r9)
            r9 = 1
            r8.setShowWhenLocked(r9)
            r0 = 0
            r8.setFinishOnTouchOutside(r0)
            r8.setResult(r0)
            android.content.Intent r1 = r8.getIntent()
            java.lang.String r2 = "android.intent.extra.PACKAGE_NAME"
            java.lang.String r1 = r1.getStringExtra(r2)
            if (r1 != 0) goto L_0x001b
            return
        L_0x001b:
            r8.sensorUsePackageName = r1
            android.content.Intent r1 = r8.getIntent()
            java.lang.String r2 = android.hardware.SensorPrivacyManager.EXTRA_ALL_SENSORS
            boolean r1 = r1.getBooleanExtra(r2, r0)
            r2 = 2
            r3 = 2147483647(0x7fffffff, float:NaN)
            if (r1 == 0) goto L_0x004f
            r8.sensor = r3
            com.android.systemui.sensorprivacy.SensorUseStartedActivity$onCreate$1 r1 = new com.android.systemui.sensorprivacy.SensorUseStartedActivity$onCreate$1
            r1.<init>(r8)
            r8.sensorPrivacyListener = r1
            com.android.systemui.statusbar.policy.IndividualSensorPrivacyController r4 = r8.sensorPrivacyController
            r4.addCallback(r1)
            com.android.systemui.statusbar.policy.IndividualSensorPrivacyController r1 = r8.sensorPrivacyController
            boolean r1 = r1.isSensorBlocked(r9)
            if (r1 != 0) goto L_0x007a
            com.android.systemui.statusbar.policy.IndividualSensorPrivacyController r1 = r8.sensorPrivacyController
            boolean r1 = r1.isSensorBlocked(r2)
            if (r1 != 0) goto L_0x007a
            r8.finish()
            return
        L_0x004f:
            android.content.Intent r1 = r8.getIntent()
            java.lang.String r4 = android.hardware.SensorPrivacyManager.EXTRA_SENSOR
            r5 = -1
            int r1 = r1.getIntExtra(r4, r5)
            if (r1 != r5) goto L_0x0060
            r8.finish()
            return
        L_0x0060:
            kotlin.Unit r4 = kotlin.Unit.INSTANCE
            r8.sensor = r1
            com.android.systemui.sensorprivacy.SensorUseStartedActivity$onCreate$3 r1 = new com.android.systemui.sensorprivacy.SensorUseStartedActivity$onCreate$3
            r1.<init>(r8)
            r8.sensorPrivacyListener = r1
            com.android.systemui.statusbar.policy.IndividualSensorPrivacyController r4 = r8.sensorPrivacyController
            r4.addCallback(r1)
            com.android.systemui.statusbar.policy.IndividualSensorPrivacyController r1 = r8.sensorPrivacyController
            com.android.systemui.sensorprivacy.SensorUseStartedActivity$onCreate$4 r4 = new com.android.systemui.sensorprivacy.SensorUseStartedActivity$onCreate$4
            r4.<init>(r8)
            r1.addCallback(r4)
        L_0x007a:
            com.android.internal.app.AlertController$AlertParams r1 = r8.mAlertParams
            android.view.LayoutInflater r4 = r1.mInflater     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = com.android.systemui.R$layout.sensor_use_started_title     // Catch:{ NameNotFoundException -> 0x012d }
            r6 = 0
            android.view.View r4 = r4.inflate(r5, r6)     // Catch:{ NameNotFoundException -> 0x012d }
            r1.mCustomTitleView = r4     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = com.android.systemui.R$id.sensor_use_started_title_message     // Catch:{ NameNotFoundException -> 0x012d }
            android.view.View r4 = r4.findViewById(r5)     // Catch:{ NameNotFoundException -> 0x012d }
            com.android.internal.widget.DialogTitle r4 = (com.android.internal.widget.DialogTitle) r4     // Catch:{ NameNotFoundException -> 0x012d }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r4)     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = r8.sensor     // Catch:{ NameNotFoundException -> 0x012d }
            if (r5 == r9) goto L_0x00a2
            if (r5 == r2) goto L_0x009f
            if (r5 == r3) goto L_0x009c
            r5 = r0
            goto L_0x00a4
        L_0x009c:
            int r5 = com.android.systemui.R$string.sensor_privacy_start_use_mic_camera_dialog_title     // Catch:{ NameNotFoundException -> 0x012d }
            goto L_0x00a4
        L_0x009f:
            int r5 = com.android.systemui.R$string.sensor_privacy_start_use_camera_dialog_title     // Catch:{ NameNotFoundException -> 0x012d }
            goto L_0x00a4
        L_0x00a2:
            int r5 = com.android.systemui.R$string.sensor_privacy_start_use_mic_dialog_title     // Catch:{ NameNotFoundException -> 0x012d }
        L_0x00a4:
            r4.setText(r5)     // Catch:{ NameNotFoundException -> 0x012d }
            android.view.View r4 = r1.mCustomTitleView     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = com.android.systemui.R$id.sensor_use_microphone_icon     // Catch:{ NameNotFoundException -> 0x012d }
            android.view.View r4 = r4.findViewById(r5)     // Catch:{ NameNotFoundException -> 0x012d }
            android.widget.ImageView r4 = (android.widget.ImageView) r4     // Catch:{ NameNotFoundException -> 0x012d }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r4)     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = r8.sensor     // Catch:{ NameNotFoundException -> 0x012d }
            r7 = 8
            if (r5 == r9) goto L_0x00bf
            if (r5 != r3) goto L_0x00bd
            goto L_0x00bf
        L_0x00bd:
            r5 = r7
            goto L_0x00c0
        L_0x00bf:
            r5 = r0
        L_0x00c0:
            r4.setVisibility(r5)     // Catch:{ NameNotFoundException -> 0x012d }
            android.view.View r4 = r1.mCustomTitleView     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = com.android.systemui.R$id.sensor_use_camera_icon     // Catch:{ NameNotFoundException -> 0x012d }
            android.view.View r4 = r4.findViewById(r5)     // Catch:{ NameNotFoundException -> 0x012d }
            android.widget.ImageView r4 = (android.widget.ImageView) r4     // Catch:{ NameNotFoundException -> 0x012d }
            kotlin.jvm.internal.Intrinsics.checkNotNull(r4)     // Catch:{ NameNotFoundException -> 0x012d }
            int r5 = r8.sensor     // Catch:{ NameNotFoundException -> 0x012d }
            if (r5 == r2) goto L_0x00d6
            if (r5 != r3) goto L_0x00d7
        L_0x00d6:
            r7 = r0
        L_0x00d7:
            r4.setVisibility(r7)     // Catch:{ NameNotFoundException -> 0x012d }
            int r4 = r8.sensor     // Catch:{ NameNotFoundException -> 0x012d }
            if (r4 == r9) goto L_0x00ea
            if (r4 == r2) goto L_0x00e7
            if (r4 == r3) goto L_0x00e4
            r2 = r0
            goto L_0x00ec
        L_0x00e4:
            int r2 = com.android.systemui.R$string.sensor_privacy_start_use_mic_camera_dialog_content     // Catch:{ NameNotFoundException -> 0x012d }
            goto L_0x00ec
        L_0x00e7:
            int r2 = com.android.systemui.R$string.sensor_privacy_start_use_camera_dialog_content     // Catch:{ NameNotFoundException -> 0x012d }
            goto L_0x00ec
        L_0x00ea:
            int r2 = com.android.systemui.R$string.sensor_privacy_start_use_mic_dialog_content     // Catch:{ NameNotFoundException -> 0x012d }
        L_0x00ec:
            java.lang.Object[] r9 = new java.lang.Object[r9]     // Catch:{ NameNotFoundException -> 0x012d }
            android.content.pm.PackageManager r3 = r8.getPackageManager()     // Catch:{ NameNotFoundException -> 0x012d }
            java.lang.String r4 = r8.sensorUsePackageName     // Catch:{ NameNotFoundException -> 0x012d }
            if (r4 == 0) goto L_0x0127
            android.content.pm.ApplicationInfo r3 = r3.getApplicationInfo(r4, r0)     // Catch:{ NameNotFoundException -> 0x012d }
            android.content.pm.PackageManager r4 = r8.getPackageManager()     // Catch:{ NameNotFoundException -> 0x012d }
            java.lang.CharSequence r3 = r3.loadLabel(r4)     // Catch:{ NameNotFoundException -> 0x012d }
            r9[r0] = r3     // Catch:{ NameNotFoundException -> 0x012d }
            java.lang.String r9 = r8.getString(r2, r9)     // Catch:{ NameNotFoundException -> 0x012d }
            android.text.Spanned r9 = android.text.Html.fromHtml(r9, r0)     // Catch:{ NameNotFoundException -> 0x012d }
            r1.mMessage = r9     // Catch:{ NameNotFoundException -> 0x012d }
            r9 = 17041410(0x1040802, float:2.4250316E-38)
            java.lang.String r9 = r8.getString(r9)
            r1.mPositiveButtonText = r9
            r9 = 17039360(0x1040000, float:2.424457E-38)
            java.lang.String r9 = r8.getString(r9)
            r1.mNegativeButtonText = r9
            r1.mPositiveButtonListener = r8
            r1.mNegativeButtonListener = r8
            r8.setupAlert()
            return
        L_0x0127:
            java.lang.String r9 = "sensorUsePackageName"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r9)     // Catch:{ NameNotFoundException -> 0x012d }
            throw r6     // Catch:{ NameNotFoundException -> 0x012d }
        L_0x012d:
            r8.finish()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.sensorprivacy.SensorUseStartedActivity.onCreate(android.os.Bundle):void");
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        SensorUseStartedActivity.super.onStart();
        setSuppressed(true);
        this.unsuppressImmediately = false;
    }

    public void onClick(@Nullable DialogInterface dialogInterface, int i) {
        if (i == -2) {
            this.unsuppressImmediately = false;
            String str = this.sensorUsePackageName;
            if (str != null) {
                FrameworkStatsLog.write(382, 2, str);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("sensorUsePackageName");
                throw null;
            }
        } else if (i == -1) {
            if (!this.keyguardStateController.isMethodSecure() || !this.keyguardStateController.isShowing()) {
                disableSensorPrivacy();
                String str2 = this.sensorUsePackageName;
                if (str2 != null) {
                    FrameworkStatsLog.write(382, 1, str2);
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException("sensorUsePackageName");
                    throw null;
                }
            } else {
                this.keyguardDismissUtil.executeWhenUnlocked(new SensorUseStartedActivity$onClick$1(this), false, true);
            }
        }
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        SensorUseStartedActivity.super.onStop();
        if (this.unsuppressImmediately) {
            setSuppressed(false);
        } else {
            this.bgHandler.postDelayed(new SensorUseStartedActivity$onStop$1(this), 2000);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        SensorUseStartedActivity.super.onDestroy();
        IndividualSensorPrivacyController individualSensorPrivacyController = this.sensorPrivacyController;
        IndividualSensorPrivacyController.Callback callback = this.sensorPrivacyListener;
        if (callback != null) {
            individualSensorPrivacyController.removeCallback(callback);
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("sensorPrivacyListener");
            throw null;
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(@Nullable Intent intent) {
        setIntent(intent);
        recreate();
    }

    /* access modifiers changed from: private */
    public final void disableSensorPrivacy() {
        int i = this.sensor;
        if (i == Integer.MAX_VALUE) {
            this.sensorPrivacyController.setSensorBlocked(3, 1, false);
            this.sensorPrivacyController.setSensorBlocked(3, 2, false);
        } else {
            this.sensorPrivacyController.setSensorBlocked(3, i, false);
        }
        this.unsuppressImmediately = true;
        setResult(-1);
    }

    /* access modifiers changed from: private */
    public final void setSuppressed(boolean z) {
        int i = this.sensor;
        if (i == Integer.MAX_VALUE) {
            this.sensorPrivacyController.suppressSensorPrivacyReminders(1, z);
            this.sensorPrivacyController.suppressSensorPrivacyReminders(2, z);
            return;
        }
        this.sensorPrivacyController.suppressSensorPrivacyReminders(i, z);
    }
}
