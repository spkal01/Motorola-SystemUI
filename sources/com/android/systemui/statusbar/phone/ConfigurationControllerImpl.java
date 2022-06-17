package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: ConfigurationControllerImpl.kt */
public final class ConfigurationControllerImpl implements ConfigurationController {
    @NotNull
    private final Context context;
    private int density;
    private float fontScale;
    private final boolean inCarMode;
    @NotNull
    private final Configuration lastConfig = new Configuration();
    private int layoutDirection;
    @NotNull
    private final List<ConfigurationController.ConfigurationListener> listeners = new ArrayList();
    @Nullable
    private LocaleList localeList;
    private int smallestScreenWidth;
    private int uiMode;

    public ConfigurationControllerImpl(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        Configuration configuration = context2.getResources().getConfiguration();
        this.context = context2;
        this.fontScale = configuration.fontScale;
        this.density = configuration.densityDpi;
        this.smallestScreenWidth = configuration.smallestScreenWidthDp;
        int i = configuration.uiMode;
        this.inCarMode = (i & 15) == 3;
        this.uiMode = i & 48;
        this.localeList = configuration.getLocales();
        this.layoutDirection = configuration.getLayoutDirection();
    }

    public void notifyThemeChanged() {
        for (ConfigurationController.ConfigurationListener configurationListener : new ArrayList(this.listeners)) {
            if (this.listeners.contains(configurationListener)) {
                configurationListener.onThemeChanged();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004c, code lost:
        if (r4 == false) goto L_0x006e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onConfigurationChanged(@org.jetbrains.annotations.NotNull android.content.res.Configuration r11) {
        /*
            r10 = this;
            java.lang.String r0 = "newConfig"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r11, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r1 = r10.listeners
            r0.<init>(r1)
            java.util.Iterator r1 = r0.iterator()
        L_0x0010:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0028
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r2 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r2
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r3 = r10.listeners
            boolean r3 = r3.contains(r2)
            if (r3 == 0) goto L_0x0010
            r2.onConfigChanged(r11)
            goto L_0x0010
        L_0x0028:
            float r1 = r11.fontScale
            int r2 = r11.densityDpi
            int r3 = r11.uiMode
            r3 = r3 & 48
            int r4 = r10.uiMode
            r5 = 0
            r6 = 1
            if (r3 == r4) goto L_0x0038
            r4 = r6
            goto L_0x0039
        L_0x0038:
            r4 = r5
        L_0x0039:
            int r7 = r10.density
            if (r2 != r7) goto L_0x004e
            float r7 = r10.fontScale
            int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r7 != 0) goto L_0x0045
            r7 = r6
            goto L_0x0046
        L_0x0045:
            r7 = r5
        L_0x0046:
            if (r7 == 0) goto L_0x004e
            boolean r7 = r10.inCarMode
            if (r7 == 0) goto L_0x006e
            if (r4 == 0) goto L_0x006e
        L_0x004e:
            java.util.Iterator r7 = r0.iterator()
        L_0x0052:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x006a
            java.lang.Object r8 = r7.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r8 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r8
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r9 = r10.listeners
            boolean r9 = r9.contains(r8)
            if (r9 == 0) goto L_0x0052
            r8.onDensityOrFontScaleChanged()
            goto L_0x0052
        L_0x006a:
            r10.density = r2
            r10.fontScale = r1
        L_0x006e:
            int r1 = r11.smallestScreenWidthDp
            int r2 = r10.smallestScreenWidth
            if (r1 == r2) goto L_0x0092
            r10.smallestScreenWidth = r1
            java.util.Iterator r1 = r0.iterator()
        L_0x007a:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0092
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r2 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r2
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r7 = r10.listeners
            boolean r7 = r7.contains(r2)
            if (r7 == 0) goto L_0x007a
            r2.onSmallestScreenWidthChanged()
            goto L_0x007a
        L_0x0092:
            android.os.LocaleList r1 = r11.getLocales()
            android.os.LocaleList r2 = r10.localeList
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1, (java.lang.Object) r2)
            if (r2 != 0) goto L_0x00bc
            r10.localeList = r1
            java.util.Iterator r1 = r0.iterator()
        L_0x00a4:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00bc
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r2 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r2
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r7 = r10.listeners
            boolean r7 = r7.contains(r2)
            if (r7 == 0) goto L_0x00a4
            r2.onLocaleListChanged()
            goto L_0x00a4
        L_0x00bc:
            if (r4 == 0) goto L_0x00eb
            android.content.Context r1 = r10.context
            android.content.res.Resources$Theme r1 = r1.getTheme()
            android.content.Context r2 = r10.context
            int r2 = r2.getThemeResId()
            r1.applyStyle(r2, r6)
            r10.uiMode = r3
            java.util.Iterator r1 = r0.iterator()
        L_0x00d3:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x00eb
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r2 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r2
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r3 = r10.listeners
            boolean r3 = r3.contains(r2)
            if (r3 == 0) goto L_0x00d3
            r2.onUiModeChanged()
            goto L_0x00d3
        L_0x00eb:
            int r1 = r10.layoutDirection
            int r2 = r11.getLayoutDirection()
            if (r1 == r2) goto L_0x011c
            int r1 = r11.getLayoutDirection()
            r10.layoutDirection = r1
            java.util.Iterator r1 = r0.iterator()
        L_0x00fd:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x011c
            java.lang.Object r2 = r1.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r2 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r2
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r3 = r10.listeners
            boolean r3 = r3.contains(r2)
            if (r3 == 0) goto L_0x00fd
            int r3 = r10.layoutDirection
            if (r3 != r6) goto L_0x0117
            r3 = r6
            goto L_0x0118
        L_0x0117:
            r3 = r5
        L_0x0118:
            r2.onLayoutDirectionChanged(r3)
            goto L_0x00fd
        L_0x011c:
            android.content.res.Configuration r1 = r10.lastConfig
            int r11 = r1.updateFrom(r11)
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r11 = r11 & r1
            if (r11 == 0) goto L_0x0143
            java.util.Iterator r11 = r0.iterator()
        L_0x012b:
            boolean r0 = r11.hasNext()
            if (r0 == 0) goto L_0x0143
            java.lang.Object r0 = r11.next()
            com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener r0 = (com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener) r0
            java.util.List<com.android.systemui.statusbar.policy.ConfigurationController$ConfigurationListener> r1 = r10.listeners
            boolean r1 = r1.contains(r0)
            if (r1 == 0) goto L_0x012b
            r0.onOverlayChanged()
            goto L_0x012b
        L_0x0143:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.ConfigurationControllerImpl.onConfigurationChanged(android.content.res.Configuration):void");
    }

    public void addCallback(@NotNull ConfigurationController.ConfigurationListener configurationListener) {
        Intrinsics.checkNotNullParameter(configurationListener, "listener");
        this.listeners.add(configurationListener);
        configurationListener.onDensityOrFontScaleChanged();
    }

    public void removeCallback(@NotNull ConfigurationController.ConfigurationListener configurationListener) {
        Intrinsics.checkNotNullParameter(configurationListener, "listener");
        this.listeners.remove(configurationListener);
    }

    public boolean isLayoutRtl() {
        return this.layoutDirection == 1;
    }
}
