package com.android.systemui.wmshell;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.p011wm.shell.ShellCommandHandler;
import com.android.p011wm.shell.hidedisplaycutout.HideDisplayCutout;
import com.android.p011wm.shell.legacysplitscreen.LegacySplitScreen;
import com.android.p011wm.shell.nano.WmShellTraceProto;
import com.android.p011wm.shell.onehanded.OneHanded;
import com.android.p011wm.shell.onehanded.OneHandedEventCallback;
import com.android.p011wm.shell.onehanded.OneHandedTransitionCallback;
import com.android.p011wm.shell.pip.Pip;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.model.SysUiState;
import com.android.systemui.navigationbar.NavigationModeController;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.tracing.nano.SystemUiTraceProto;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;

public final class WMShell extends SystemUI implements CommandQueue.Callbacks, ProtoTraceable<SystemUiTraceProto> {
    /* access modifiers changed from: private */
    public final CommandQueue mCommandQueue;
    private final ConfigurationController mConfigurationController;
    private final Optional<HideDisplayCutout> mHideDisplayCutoutOptional;
    private boolean mIsSysUiStateValid;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final NavigationModeController mNavigationModeController;
    private KeyguardUpdateMonitorCallback mOneHandedKeyguardCallback;
    private final Optional<OneHanded> mOneHandedOptional;
    private KeyguardUpdateMonitorCallback mPipKeyguardCallback;
    private final Optional<Pip> mPipOptional;
    private final ProtoTracer mProtoTracer;
    private final ScreenLifecycle mScreenLifecycle;
    private final Optional<ShellCommandHandler> mShellCommandHandler;
    private KeyguardUpdateMonitorCallback mSplitScreenKeyguardCallback;
    private final Optional<LegacySplitScreen> mSplitScreenOptional;
    /* access modifiers changed from: private */
    public final Executor mSysUiMainExecutor;
    /* access modifiers changed from: private */
    public final SysUiState mSysUiState;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    private WakefulnessLifecycle.Observer mWakefulnessObserver;

    public WMShell(Context context, Optional<Pip> optional, Optional<LegacySplitScreen> optional2, Optional<OneHanded> optional3, Optional<HideDisplayCutout> optional4, Optional<ShellCommandHandler> optional5, CommandQueue commandQueue, ConfigurationController configurationController, KeyguardUpdateMonitor keyguardUpdateMonitor, NavigationModeController navigationModeController, ScreenLifecycle screenLifecycle, SysUiState sysUiState, ProtoTracer protoTracer, WakefulnessLifecycle wakefulnessLifecycle, Executor executor) {
        super(context);
        this.mCommandQueue = commandQueue;
        this.mConfigurationController = configurationController;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mNavigationModeController = navigationModeController;
        this.mScreenLifecycle = screenLifecycle;
        this.mSysUiState = sysUiState;
        this.mPipOptional = optional;
        this.mSplitScreenOptional = optional2;
        this.mOneHandedOptional = optional3;
        this.mHideDisplayCutoutOptional = optional4;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mProtoTracer = protoTracer;
        this.mShellCommandHandler = optional5;
        this.mSysUiMainExecutor = executor;
    }

    public void start() {
        this.mProtoTracer.add(this);
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mPipOptional.ifPresent(new WMShell$$ExternalSyntheticLambda5(this));
        this.mSplitScreenOptional.ifPresent(new WMShell$$ExternalSyntheticLambda3(this));
        this.mOneHandedOptional.ifPresent(new WMShell$$ExternalSyntheticLambda4(this));
        this.mHideDisplayCutoutOptional.ifPresent(new WMShell$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void initPip(final Pip pip) {
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) new CommandQueue.Callbacks() {
            public void showPictureInPictureMenu() {
                pip.showPictureInPictureMenu();
            }
        });
        C22052 r0 = new KeyguardUpdateMonitorCallback() {
            public void onKeyguardVisibilityChanged(boolean z) {
                if (z) {
                    pip.hidePipMenu((Runnable) null, (Runnable) null);
                }
            }
        };
        this.mPipKeyguardCallback = r0;
        this.mKeyguardUpdateMonitor.registerCallback(r0);
        this.mSysUiState.addCallback(new WMShell$$ExternalSyntheticLambda0(this, pip));
        this.mConfigurationController.addCallback(new ConfigurationController.ConfigurationListener() {
            public void onConfigChanged(Configuration configuration) {
                pip.onConfigurationChanged(configuration);
            }

            public void onDensityOrFontScaleChanged() {
                pip.onDensityOrFontScaleChanged();
            }

            public void onOverlayChanged() {
                pip.onOverlayChanged();
            }
        });
        ((UserInfoController) Dependency.get(UserInfoController.class)).addCallback(new WMShell$$ExternalSyntheticLambda1(pip));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initPip$0(Pip pip, int i) {
        boolean z = (51788 & i) == 0;
        this.mIsSysUiStateValid = z;
        pip.onSystemUiStateChanged(z, i);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void initSplitScreen(final LegacySplitScreen legacySplitScreen) {
        C22074 r0 = new KeyguardUpdateMonitorCallback() {
            public void onKeyguardVisibilityChanged(boolean z) {
                legacySplitScreen.onKeyguardVisibilityChanged(z);
            }
        };
        this.mSplitScreenKeyguardCallback = r0;
        this.mKeyguardUpdateMonitor.registerCallback(r0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void initOneHanded(final OneHanded oneHanded) {
        oneHanded.registerTransitionCallback(new OneHandedTransitionCallback() {
            public void onStartTransition(boolean z) {
                WMShell.this.mSysUiMainExecutor.execute(new WMShell$5$$ExternalSyntheticLambda1(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onStartTransition$0() {
                WMShell.this.mSysUiState.setFlag(65536, true).commitUpdate(0);
            }

            public void onStartFinished(Rect rect) {
                WMShell.this.mSysUiMainExecutor.execute(new WMShell$5$$ExternalSyntheticLambda2(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onStartFinished$1() {
                WMShell.this.mSysUiState.setFlag(65536, true).commitUpdate(0);
            }

            public void onStopFinished(Rect rect) {
                WMShell.this.mSysUiMainExecutor.execute(new WMShell$5$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onStopFinished$2() {
                WMShell.this.mSysUiState.setFlag(65536, false).commitUpdate(0);
            }
        });
        oneHanded.registerEventCallback(new OneHandedEventCallback() {
            public void notifyExpandNotification() {
                WMShell.this.mSysUiMainExecutor.execute(new WMShell$6$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$notifyExpandNotification$0() {
                WMShell.this.mCommandQueue.handleSystemKey(281);
            }
        });
        C22107 r0 = new KeyguardUpdateMonitorCallback() {
            public void onKeyguardVisibilityChanged(boolean z) {
                oneHanded.onKeyguardVisibilityChanged(z);
                oneHanded.stopOneHanded();
            }

            public void onUserSwitchComplete(int i) {
                oneHanded.onUserSwitch(i);
            }
        };
        this.mOneHandedKeyguardCallback = r0;
        this.mKeyguardUpdateMonitor.registerCallback(r0);
        C22118 r02 = new WakefulnessLifecycle.Observer() {
            public void onFinishedWakingUp() {
                oneHanded.setLockedDisabled(false, false);
            }

            public void onStartedGoingToSleep() {
                oneHanded.stopOneHanded();
                oneHanded.setLockedDisabled(true, false);
            }
        };
        this.mWakefulnessObserver = r02;
        this.mWakefulnessLifecycle.addObserver(r02);
        this.mScreenLifecycle.addObserver(new ScreenLifecycle.Observer() {
            public void onScreenTurningOff() {
                oneHanded.stopOneHanded(7);
            }
        });
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) new CommandQueue.Callbacks() {
            public void onCameraLaunchGestureDetected(int i) {
                oneHanded.stopOneHanded();
            }

            public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
                if (i == 0 && (i2 & 2) != 0) {
                    oneHanded.stopOneHanded(3);
                }
            }
        });
        this.mConfigurationController.addCallback(new ConfigurationController.ConfigurationListener() {
            public void onConfigChanged(Configuration configuration) {
                oneHanded.onConfigChanged(configuration);
            }
        });
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void initHideDisplayCutout(final HideDisplayCutout hideDisplayCutout) {
        this.mConfigurationController.addCallback(new ConfigurationController.ConfigurationListener() {
            public void onConfigChanged(Configuration configuration) {
                hideDisplayCutout.onConfigurationChanged(configuration);
            }
        });
    }

    public void writeToProto(SystemUiTraceProto systemUiTraceProto) {
        if (systemUiTraceProto.wmShell == null) {
            systemUiTraceProto.wmShell = new WmShellTraceProto();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if ((!this.mShellCommandHandler.isPresent() || !this.mShellCommandHandler.get().handleCommand(strArr, printWriter)) && !handleLoggingCommand(strArr, printWriter)) {
            this.mShellCommandHandler.ifPresent(new WMShell$$ExternalSyntheticLambda6(printWriter));
        }
    }

    public void handleWindowManagerLoggingCommand(String[] strArr, ParcelFileDescriptor parcelFileDescriptor) {
        PrintWriter printWriter = new PrintWriter(new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor));
        handleLoggingCommand(strArr, printWriter);
        printWriter.flush();
        printWriter.close();
    }

    private boolean handleLoggingCommand(String[] strArr, PrintWriter printWriter) {
        ShellProtoLogImpl singleInstance = ShellProtoLogImpl.getSingleInstance();
        int i = 0;
        while (i < strArr.length) {
            String str = strArr[i];
            str.hashCode();
            if (str.equals("enable-text")) {
                String[] strArr2 = (String[]) Arrays.copyOfRange(strArr, i + 1, strArr.length);
                if (singleInstance.startTextLogging(strArr2, printWriter) == 0) {
                    printWriter.println("Starting logging on groups: " + Arrays.toString(strArr2));
                }
                return true;
            } else if (!str.equals("disable-text")) {
                i++;
            } else {
                String[] strArr3 = (String[]) Arrays.copyOfRange(strArr, i + 1, strArr.length);
                if (singleInstance.stopTextLogging(strArr3, printWriter) == 0) {
                    printWriter.println("Stopping logging on groups: " + Arrays.toString(strArr3));
                }
                return true;
            }
        }
        return false;
    }
}
