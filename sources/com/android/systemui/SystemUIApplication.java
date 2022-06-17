package com.android.systemui;

import android.app.ActivityThread;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.util.TimingsTraceLog;
import android.view.SurfaceControl;
import com.android.internal.protolog.common.ProtoLog;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.dagger.ContextComponentHelper;
import com.android.systemui.dagger.GlobalRootComponent;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.screenrecord.RecordingUtils;
import com.android.systemui.shared.system.ThreadedRendererCompat;
import com.android.systemui.util.NotificationChannels;

public class SystemUIApplication extends Application implements SystemUIAppComponentFactory.ContextInitializer {
    /* access modifiers changed from: private */
    public BootCompleteCacheImpl mBootCompleteCache;
    private ContextComponentHelper mComponentHelper;
    private SystemUIAppComponentFactory.ContextAvailableCallback mContextAvailableCallback;
    private GlobalRootComponent mRootComponent;
    /* access modifiers changed from: private */
    public SystemUI[] mServices;
    /* access modifiers changed from: private */
    public boolean mServicesStarted;
    private SysUIComponent mSysUIComponent;

    public SystemUIApplication() {
        Log.v("SystemUIService", "SystemUIApplication constructed.");
        ProtoLog.REQUIRE_PROTOLOGTOOL = false;
    }

    public void onCreate() {
        super.onCreate();
        String currentProcessName = ActivityThread.currentProcessName();
        ApplicationInfo applicationInfo = getApplicationInfo();
        if (currentProcessName != null) {
            if (currentProcessName.startsWith(applicationInfo.processName + ":systemui_readyfor")) {
                return;
            }
        }
        if (Build.TYPE.equals("userdebug")) {
            Looper.getMainLooper().setSlowLogThresholdMs(500, 500);
        }
        Log.v("SystemUIService", "SystemUIApplication created.");
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SystemUIBootTiming", 4096);
        timingsTraceLog.traceBegin("DependencyInjection");
        this.mContextAvailableCallback.onContextAvailable(this);
        this.mRootComponent = SystemUIFactory.getInstance().getRootComponent();
        SysUIComponent sysUIComponent = SystemUIFactory.getInstance().getSysUIComponent();
        this.mSysUIComponent = sysUIComponent;
        this.mComponentHelper = sysUIComponent.getContextComponentHelper();
        this.mBootCompleteCache = this.mSysUIComponent.provideBootCacheImpl();
        timingsTraceLog.traceEnd();
        setTheme(R$style.Theme_SystemUI);
        if (Process.myUserHandle().equals(UserHandle.SYSTEM)) {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
            intentFilter.setPriority(1000);
            int gPUContextPriority = SurfaceControl.getGPUContextPriority();
            Log.i("SystemUIService", "Found SurfaceFlinger's GPU Priority: " + gPUContextPriority);
            if (gPUContextPriority == ThreadedRendererCompat.EGL_CONTEXT_PRIORITY_REALTIME_NV) {
                Log.i("SystemUIService", "Setting SysUI's GPU Context priority to: " + ThreadedRendererCompat.EGL_CONTEXT_PRIORITY_HIGH_IMG);
                ThreadedRendererCompat.setContextPriority(ThreadedRendererCompat.EGL_CONTEXT_PRIORITY_HIGH_IMG);
            }
            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (!SystemUIApplication.this.mBootCompleteCache.isBootComplete()) {
                        SystemUIApplication.this.unregisterReceiver(this);
                        SystemUIApplication.this.mBootCompleteCache.setBootComplete();
                        if (SystemUIApplication.this.mServicesStarted) {
                            for (SystemUI onBootCompleted : SystemUIApplication.this.mServices) {
                                onBootCompleted.onBootCompleted();
                            }
                        }
                    }
                }
            }, intentFilter);
            registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction()) && SystemUIApplication.this.mBootCompleteCache.isBootComplete()) {
                        NotificationChannels.createAll(context);
                    }
                }
            }, new IntentFilter("android.intent.action.LOCALE_CHANGED"));
            return;
        }
        if (currentProcessName != null) {
            if (currentProcessName.startsWith(applicationInfo.processName + ":")) {
                return;
            }
        }
        startSecondaryUserServicesIfNeeded();
    }

    public void startServicesIfNeeded() {
        String[] strArr;
        if (MotoFeature.getInstance(this).isSupportCli()) {
            strArr = SystemUIFactory.getInstance().getSystemUIServiceComponentsForCli(getResources());
        } else {
            strArr = SystemUIFactory.getInstance().getSystemUIServiceComponents(getResources());
        }
        startServicesIfNeeded("StartServices", strArr);
    }

    /* access modifiers changed from: package-private */
    public void startSecondaryUserServicesIfNeeded() {
        String[] strArr;
        if (MotoFeature.getInstance(this).isSupportCli()) {
            strArr = SystemUIFactory.getInstance().getSystemUIServiceComponentsPerUserForCli(getResources());
        } else {
            strArr = SystemUIFactory.getInstance().getSystemUIServiceComponentsPerUser(getResources());
        }
        startServicesIfNeeded("StartSecondaryServices", strArr);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: com.android.systemui.SystemUI} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startServicesIfNeeded(java.lang.String r14, java.lang.String[] r15) {
        /*
            r13 = this;
            boolean r0 = r13.mServicesStarted
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            int r0 = com.android.systemui.screenrecord.RecordingSettings.getScreenRecordingStatus(r13)
            r1 = 0
            if (r0 != 0) goto L_0x0027
            boolean r0 = com.android.systemui.screenrecord.RecordingUtils.isRecordingFileExist(r13)
            if (r0 == 0) goto L_0x001f
            java.lang.Thread r0 = new java.lang.Thread
            com.android.systemui.SystemUIApplication$$ExternalSyntheticLambda0 r2 = new com.android.systemui.SystemUIApplication$$ExternalSyntheticLambda0
            r2.<init>(r13)
            r0.<init>(r2)
            r0.start()
        L_0x001f:
            android.content.Context r0 = r13.getApplicationContext()
            com.android.systemui.screenrecord.RecordingUtils.updateAudioParameter(r0, r1)
            goto L_0x002a
        L_0x0027:
            com.android.systemui.screenrecord.RecordingSettings.setScreenRecordingStatus(r13, r1)
        L_0x002a:
            int r0 = r15.length
            com.android.systemui.SystemUI[] r0 = new com.android.systemui.SystemUI[r0]
            r13.mServices = r0
            com.android.systemui.BootCompleteCacheImpl r0 = r13.mBootCompleteCache
            boolean r0 = r0.isBootComplete()
            if (r0 != 0) goto L_0x004b
            java.lang.String r0 = "sys.boot_completed"
            java.lang.String r0 = android.os.SystemProperties.get(r0)
            java.lang.String r2 = "1"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004b
            com.android.systemui.BootCompleteCacheImpl r0 = r13.mBootCompleteCache
            r0.setBootComplete()
        L_0x004b:
            com.android.systemui.dagger.SysUIComponent r0 = r13.mSysUIComponent
            com.android.systemui.dump.DumpManager r0 = r0.createDumpManager()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Starting SystemUI services for user "
            r2.append(r3)
            android.os.UserHandle r3 = android.os.Process.myUserHandle()
            int r3 = r3.getIdentifier()
            r2.append(r3)
            java.lang.String r3 = "."
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "SystemUIService"
            android.util.Log.v(r3, r2)
            android.util.TimingsTraceLog r2 = new android.util.TimingsTraceLog
            r4 = 4096(0x1000, double:2.0237E-320)
            java.lang.String r6 = "SystemUIBootTiming"
            r2.<init>(r6, r4)
            r2.traceBegin(r14)
            int r4 = r15.length
            r5 = r1
        L_0x0082:
            r6 = 1
            if (r5 >= r4) goto L_0x0128
            r7 = r15[r5]
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r14)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            r2.traceBegin(r8)
            long r8 = java.lang.System.currentTimeMillis()
            com.android.systemui.dagger.ContextComponentHelper r10 = r13.mComponentHelper     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            com.android.systemui.SystemUI r10 = r10.resolveSystemUI(r7)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            if (r10 != 0) goto L_0x00be
            java.lang.Class r10 = java.lang.Class.forName(r7)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            java.lang.Class[] r11 = new java.lang.Class[r6]     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            java.lang.Class<android.content.Context> r12 = android.content.Context.class
            r11[r1] = r12     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            java.lang.reflect.Constructor r10 = r10.getConstructor(r11)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            r6[r1] = r13     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            java.lang.Object r6 = r10.newInstance(r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            r10 = r6
            com.android.systemui.SystemUI r10 = (com.android.systemui.SystemUI) r10     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
        L_0x00be:
            com.android.systemui.SystemUI[] r6 = r13.mServices     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            r6[r5] = r10     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException -> 0x0121 }
            r6 = r6[r5]
            r6.start()
            r2.traceEnd()
            java.lang.Class<com.android.systemui.colorextraction.SysuiColorExtractor> r6 = com.android.systemui.colorextraction.SysuiColorExtractor.class
            com.android.systemui.Dependency.get(r6)
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r8
            r8 = 1000(0x3e8, double:4.94E-321)
            int r6 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x00fb
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "Initialization of "
            r6.append(r8)
            r6.append(r7)
            java.lang.String r7 = " took "
            r6.append(r7)
            r6.append(r10)
            java.lang.String r7 = " ms"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Log.w(r3, r6)
        L_0x00fb:
            com.android.systemui.BootCompleteCacheImpl r6 = r13.mBootCompleteCache
            boolean r6 = r6.isBootComplete()
            if (r6 == 0) goto L_0x010a
            com.android.systemui.SystemUI[] r6 = r13.mServices
            r6 = r6[r5]
            r6.onBootCompleted()
        L_0x010a:
            com.android.systemui.SystemUI[] r6 = r13.mServices
            r6 = r6[r5]
            java.lang.Class r6 = r6.getClass()
            java.lang.String r6 = r6.getName()
            com.android.systemui.SystemUI[] r7 = r13.mServices
            r7 = r7[r5]
            r0.registerDumpable(r6, r7)
            int r5 = r5 + 1
            goto L_0x0082
        L_0x0121:
            r13 = move-exception
            java.lang.RuntimeException r14 = new java.lang.RuntimeException
            r14.<init>(r13)
            throw r14
        L_0x0128:
            com.android.systemui.dagger.SysUIComponent r14 = r13.mSysUIComponent
            com.android.systemui.InitController r14 = r14.getInitController()
            r14.executePostInitTasks()
            r2.traceEnd()
            r13.mServicesStarted = r6
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SystemUIApplication.startServicesIfNeeded(java.lang.String, java.lang.String[]):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startServicesIfNeeded$0() {
        Log.v("SystemUIService", "Clean the unnecessary files");
        RecordingUtils.deleteFile(RecordingUtils.getRecordingDir(this));
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mServicesStarted) {
            this.mSysUIComponent.getConfigurationController().onConfigurationChanged(configuration);
            int length = this.mServices.length;
            for (int i = 0; i < length; i++) {
                SystemUI[] systemUIArr = this.mServices;
                if (systemUIArr[i] != null) {
                    systemUIArr[i].onConfigurationChanged(configuration);
                }
            }
        }
    }

    public void setContextAvailableCallback(SystemUIAppComponentFactory.ContextAvailableCallback contextAvailableCallback) {
        this.mContextAvailableCallback = contextAvailableCallback;
    }
}
