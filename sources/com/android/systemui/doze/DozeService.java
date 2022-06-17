package com.android.systemui.doze;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.service.dreams.DreamService;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.doze.dagger.DozeComponent;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.plugins.DozeServicePlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class DozeService extends DreamService implements DozeMachine.Service, DozeServicePlugin.RequestDoze, PluginListener<DozeServicePlugin> {
    static final boolean DEBUG = Log.isLoggable("DozeService", 3);
    private final DozeComponent.Builder mDozeComponentBuilder;
    private DozeMachine mDozeMachine;
    private DozeServicePlugin mDozePlugin;
    private PluginManager mPluginManager;

    public DozeService(DozeComponent.Builder builder, PluginManager pluginManager) {
        this.mDozeComponentBuilder = builder;
        setDebug(DEBUG);
        this.mPluginManager = pluginManager;
    }

    public void onCreate() {
        super.onCreate();
        setWindowless(true);
        this.mPluginManager.addPluginListener(this, (Class<?>) DozeServicePlugin.class, false);
        this.mDozeMachine = this.mDozeComponentBuilder.build(this).getDozeMachine();
        ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).setDozeMachine(this.mDozeMachine);
    }

    public void onDestroy() {
        PluginManager pluginManager = this.mPluginManager;
        if (pluginManager != null) {
            pluginManager.removePluginListener(this);
        }
        super.onDestroy();
        this.mDozeMachine.destroy();
        this.mDozeMachine = null;
        ((MotoDisplayManager) Dependency.get(MotoDisplayManager.class)).setDozeMachine((DozeMachine) null);
    }

    public void onPluginConnected(DozeServicePlugin dozeServicePlugin, Context context) {
        this.mDozePlugin = dozeServicePlugin;
        dozeServicePlugin.setDozeRequester(this);
    }

    public void onPluginDisconnected(DozeServicePlugin dozeServicePlugin) {
        DozeServicePlugin dozeServicePlugin2 = this.mDozePlugin;
        if (dozeServicePlugin2 != null) {
            dozeServicePlugin2.onDreamingStopped();
            this.mDozePlugin = null;
        }
    }

    public void onDreamingStarted() {
        super.onDreamingStarted();
        this.mDozeMachine.requestState(DozeMachine.State.INITIALIZED);
        startDozing();
        DozeServicePlugin dozeServicePlugin = this.mDozePlugin;
        if (dozeServicePlugin != null) {
            dozeServicePlugin.onDreamingStarted();
        }
        if (MotoFeature.getExistedInstance().isSupportCli()) {
            MotoDisplayManager motoDisplayManager = (MotoDisplayManager) Dependency.get(MotoDisplayManager.class);
            if (motoDisplayManager.shouldPulseForLidClosed()) {
                motoDisplayManager.restoreShouldPulse();
                this.mDozeMachine.requestPulseForCli(12);
            }
        }
    }

    public void onDreamingStopped() {
        super.onDreamingStopped();
        this.mDozeMachine.requestState(DozeMachine.State.FINISH);
        DozeServicePlugin dozeServicePlugin = this.mDozePlugin;
        if (dozeServicePlugin != null) {
            dozeServicePlugin.onDreamingStopped();
        }
    }

    /* access modifiers changed from: protected */
    public void dumpOnHandler(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dumpOnHandler(fileDescriptor, printWriter, strArr);
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.dump(printWriter);
        }
    }

    public void requestWakeUp() {
        ((PowerManager) getSystemService(PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:NODOZE");
    }

    public void onRequestShowDoze() {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.requestState(DozeMachine.State.DOZE_AOD);
        }
    }

    public void onRequestHideDoze() {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.requestState(DozeMachine.State.DOZE);
        }
    }

    public void setDozeScreenState(int i) {
        super.setDozeScreenState(i);
        this.mDozeMachine.onScreenState(i);
    }
}
