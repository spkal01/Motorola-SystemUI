package com.android.systemui.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.android.internal.app.AssistUtils;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.InstanceIdSequence;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: AssistLogger.kt */
public class AssistLogger {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    @NotNull
    private static final Set<AssistantSessionEvent> SESSION_END_EVENTS = SetsKt__SetsKt.setOf(AssistantSessionEvent.ASSISTANT_SESSION_INVOCATION_CANCELLED, AssistantSessionEvent.ASSISTANT_SESSION_CLOSE);
    @NotNull
    private final AssistUtils assistUtils;
    @NotNull
    private final Context context;
    @Nullable
    private InstanceId currentInstanceId;
    @NotNull
    private final InstanceIdSequence instanceIdSequence = new InstanceIdSequence(1048576);
    @NotNull
    private final PhoneStateMonitor phoneStateMonitor;
    @NotNull
    private final UiEventLogger uiEventLogger;

    /* access modifiers changed from: protected */
    public void reportAssistantInvocationExtraData() {
    }

    public AssistLogger(@NotNull Context context2, @NotNull UiEventLogger uiEventLogger2, @NotNull AssistUtils assistUtils2, @NotNull PhoneStateMonitor phoneStateMonitor2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Intrinsics.checkNotNullParameter(uiEventLogger2, "uiEventLogger");
        Intrinsics.checkNotNullParameter(assistUtils2, "assistUtils");
        Intrinsics.checkNotNullParameter(phoneStateMonitor2, "phoneStateMonitor");
        this.context = context2;
        this.uiEventLogger = uiEventLogger2;
        this.assistUtils = assistUtils2;
        this.phoneStateMonitor = phoneStateMonitor2;
    }

    public final void reportAssistantInvocationEventFromLegacy(int i, boolean z, @Nullable ComponentName componentName, @Nullable Integer num) {
        reportAssistantInvocationEvent(AssistantInvocationEvent.Companion.eventFromLegacyInvocationType(i, z), componentName, num == null ? null : Integer.valueOf(AssistantInvocationEvent.Companion.deviceStateFromLegacyDeviceState(num.intValue())));
    }

    public final void reportAssistantInvocationEvent(@NotNull UiEventLogger.UiEventEnum uiEventEnum, @Nullable ComponentName componentName, @Nullable Integer num) {
        int i;
        Intrinsics.checkNotNullParameter(uiEventEnum, "invocationEvent");
        if (componentName == null) {
            componentName = getAssistantComponentForCurrentUser();
        }
        int assistantUid = getAssistantUid(componentName);
        if (num == null) {
            i = AssistantInvocationEvent.Companion.deviceStateFromLegacyDeviceState(this.phoneStateMonitor.getPhoneState());
        } else {
            i = num.intValue();
        }
        FrameworkStatsLog.write(281, uiEventEnum.getId(), assistantUid, componentName.flattenToString(), getOrCreateInstanceId().getId(), i, false);
        reportAssistantInvocationExtraData();
    }

    public final void reportAssistantSessionEvent(@NotNull UiEventLogger.UiEventEnum uiEventEnum) {
        Intrinsics.checkNotNullParameter(uiEventEnum, "sessionEvent");
        ComponentName assistantComponentForCurrentUser = getAssistantComponentForCurrentUser();
        this.uiEventLogger.logWithInstanceId(uiEventEnum, getAssistantUid(assistantComponentForCurrentUser), assistantComponentForCurrentUser.flattenToString(), getOrCreateInstanceId());
        if (CollectionsKt___CollectionsKt.contains(SESSION_END_EVENTS, uiEventEnum)) {
            clearInstanceId();
        }
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final InstanceId getOrCreateInstanceId() {
        InstanceId instanceId = this.currentInstanceId;
        if (instanceId == null) {
            instanceId = this.instanceIdSequence.newInstanceId();
        }
        this.currentInstanceId = instanceId;
        Intrinsics.checkNotNullExpressionValue(instanceId, "instanceId");
        return instanceId;
    }

    /* access modifiers changed from: protected */
    public final void clearInstanceId() {
        this.currentInstanceId = null;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public final ComponentName getAssistantComponentForCurrentUser() {
        ComponentName assistComponentForUser = this.assistUtils.getAssistComponentForUser(KeyguardUpdateMonitor.getCurrentUser());
        Intrinsics.checkNotNullExpressionValue(assistComponentForUser, "assistUtils.getAssistComponentForUser(KeyguardUpdateMonitor.getCurrentUser())");
        return assistComponentForUser;
    }

    /* access modifiers changed from: protected */
    public final int getAssistantUid(@NotNull ComponentName componentName) {
        Intrinsics.checkNotNullParameter(componentName, "assistantComponent");
        try {
            return this.context.getPackageManager().getApplicationInfo(componentName.getPackageName(), 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AssistLogger", "Unable to find Assistant UID", e);
            return 0;
        }
    }

    /* compiled from: AssistLogger.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
