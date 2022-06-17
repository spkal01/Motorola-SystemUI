package com.android.p011wm.shell.transition;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.view.SurfaceControl;
import android.window.IRemoteTransition;
import android.window.IRemoteTransitionFinishedCallback;
import android.window.TransitionFilter;
import android.window.TransitionInfo;
import android.window.TransitionRequestInfo;
import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import com.android.p011wm.shell.common.ShellExecutor;
import com.android.p011wm.shell.protolog.ShellProtoLogCache;
import com.android.p011wm.shell.protolog.ShellProtoLogGroup;
import com.android.p011wm.shell.protolog.ShellProtoLogImpl;
import com.android.p011wm.shell.transition.Transitions;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.transition.RemoteTransitionHandler */
public class RemoteTransitionHandler implements Transitions.TransitionHandler {
    /* access modifiers changed from: private */
    public final ArrayList<Pair<TransitionFilter, IRemoteTransition>> mFilters = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ShellExecutor mMainExecutor;
    /* access modifiers changed from: private */
    public final ArrayMap<IBinder, IRemoteTransition> mRequestedRemotes = new ArrayMap<>();
    private final IBinder.DeathRecipient mTransitionDeathRecipient = new IBinder.DeathRecipient() {
        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$0() {
            RemoteTransitionHandler.this.mFilters.clear();
        }

        public void binderDied() {
            RemoteTransitionHandler.this.mMainExecutor.execute(new RemoteTransitionHandler$1$$ExternalSyntheticLambda0(this));
        }
    };

    RemoteTransitionHandler(ShellExecutor shellExecutor) {
        this.mMainExecutor = shellExecutor;
    }

    /* access modifiers changed from: package-private */
    public void addFiltered(TransitionFilter transitionFilter, IRemoteTransition iRemoteTransition) {
        try {
            iRemoteTransition.asBinder().linkToDeath(this.mTransitionDeathRecipient, 0);
            this.mFilters.add(new Pair(transitionFilter, iRemoteTransition));
        } catch (RemoteException unused) {
            Slog.e("RemoteTransitionHandler", "Failed to link to death");
        }
    }

    /* access modifiers changed from: package-private */
    public void removeFiltered(IRemoteTransition iRemoteTransition) {
        boolean z = false;
        for (int size = this.mFilters.size() - 1; size >= 0; size--) {
            if (this.mFilters.get(size).second == iRemoteTransition) {
                this.mFilters.remove(size);
                z = true;
            }
        }
        if (z) {
            iRemoteTransition.asBinder().unlinkToDeath(this.mTransitionDeathRecipient, 0);
        }
    }

    public void onTransitionMerged(IBinder iBinder) {
        this.mRequestedRemotes.remove(iBinder);
    }

    public boolean startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Transitions.TransitionFinishCallback transitionFinishCallback) {
        IRemoteTransition iRemoteTransition = this.mRequestedRemotes.get(iBinder);
        if (iRemoteTransition == null) {
            if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
                ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, -1269886472, 0, "Transition %s doesn't have explicit remote, search filters for match for %s", String.valueOf(iBinder), String.valueOf(transitionInfo));
            }
            int size = this.mFilters.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
                    ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, 990371881, 0, " Checking filter %s", String.valueOf(this.mFilters.get(size)));
                }
                if (((TransitionFilter) this.mFilters.get(size).first).matches(transitionInfo)) {
                    iRemoteTransition = (IRemoteTransition) this.mFilters.get(size).second;
                    this.mRequestedRemotes.put(iBinder, iRemoteTransition);
                    break;
                }
                size--;
            }
        }
        if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, -1671119352, 0, " Delegate animation for %s to %s", String.valueOf(iBinder), String.valueOf(iRemoteTransition));
        }
        if (iRemoteTransition == null) {
            return false;
        }
        RemoteTransitionHandler$$ExternalSyntheticLambda0 remoteTransitionHandler$$ExternalSyntheticLambda0 = new RemoteTransitionHandler$$ExternalSyntheticLambda0(this, iBinder, transitionFinishCallback);
        final IRemoteTransition iRemoteTransition2 = iRemoteTransition;
        final RemoteTransitionHandler$$ExternalSyntheticLambda0 remoteTransitionHandler$$ExternalSyntheticLambda02 = remoteTransitionHandler$$ExternalSyntheticLambda0;
        final IBinder iBinder2 = iBinder;
        final Transitions.TransitionFinishCallback transitionFinishCallback2 = transitionFinishCallback;
        C24142 r5 = new IRemoteTransitionFinishedCallback.Stub() {
            public void onTransitionFinished(WindowContainerTransaction windowContainerTransaction) {
                if (iRemoteTransition2.asBinder() != null) {
                    iRemoteTransition2.asBinder().unlinkToDeath(remoteTransitionHandler$$ExternalSyntheticLambda02, 0);
                }
                RemoteTransitionHandler.this.mMainExecutor.execute(new RemoteTransitionHandler$2$$ExternalSyntheticLambda0(this, iBinder2, transitionFinishCallback2, windowContainerTransaction));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onTransitionFinished$0(IBinder iBinder, Transitions.TransitionFinishCallback transitionFinishCallback, WindowContainerTransaction windowContainerTransaction) {
                RemoteTransitionHandler.this.mRequestedRemotes.remove(iBinder);
                transitionFinishCallback.onTransitionFinished(windowContainerTransaction, (WindowContainerTransactionCallback) null);
            }
        };
        try {
            if (iRemoteTransition.asBinder() != null) {
                iRemoteTransition.asBinder().linkToDeath(remoteTransitionHandler$$ExternalSyntheticLambda0, 0);
            }
            iRemoteTransition.startAnimation(iBinder, transitionInfo, transaction, r5);
        } catch (RemoteException e) {
            Log.e("ShellTransitions", "Error running remote transition.", e);
            if (iRemoteTransition.asBinder() != null) {
                iRemoteTransition.asBinder().unlinkToDeath(remoteTransitionHandler$$ExternalSyntheticLambda0, 0);
            }
            this.mRequestedRemotes.remove(iBinder);
            this.mMainExecutor.execute(new RemoteTransitionHandler$$ExternalSyntheticLambda2(transitionFinishCallback));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startAnimation$1(IBinder iBinder, Transitions.TransitionFinishCallback transitionFinishCallback) {
        Log.e("ShellTransitions", "Remote transition died, finishing");
        this.mMainExecutor.execute(new RemoteTransitionHandler$$ExternalSyntheticLambda1(this, iBinder, transitionFinishCallback));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startAnimation$0(IBinder iBinder, Transitions.TransitionFinishCallback transitionFinishCallback) {
        this.mRequestedRemotes.remove(iBinder);
        transitionFinishCallback.onTransitionFinished((WindowContainerTransaction) null, (WindowContainerTransactionCallback) null);
    }

    public void mergeAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, final IBinder iBinder2, final Transitions.TransitionFinishCallback transitionFinishCallback) {
        IRemoteTransition iRemoteTransition = this.mRequestedRemotes.get(iBinder2);
        if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
            String valueOf = String.valueOf(iBinder);
            String valueOf2 = String.valueOf(iRemoteTransition);
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, -114556030, 0, " Attempt merge %s into %s", valueOf, valueOf2);
        }
        if (iRemoteTransition != null) {
            try {
                iRemoteTransition.mergeAnimation(iBinder, transitionInfo, transaction, iBinder2, new IRemoteTransitionFinishedCallback.Stub() {
                    public void onTransitionFinished(WindowContainerTransaction windowContainerTransaction) {
                        RemoteTransitionHandler.this.mMainExecutor.execute(new RemoteTransitionHandler$3$$ExternalSyntheticLambda0(this, iBinder2, transitionFinishCallback, windowContainerTransaction));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onTransitionFinished$0(IBinder iBinder, Transitions.TransitionFinishCallback transitionFinishCallback, WindowContainerTransaction windowContainerTransaction) {
                        if (!RemoteTransitionHandler.this.mRequestedRemotes.containsKey(iBinder)) {
                            Log.e("RemoteTransitionHandler", "Merged transition finished after it's mergeTarget (the transition it was supposed to merge into). This usually means that the mergeTarget's RemoteTransition impl erroneously accepted/ran the merge request after finishing the mergeTarget");
                        }
                        transitionFinishCallback.onTransitionFinished(windowContainerTransaction, (WindowContainerTransactionCallback) null);
                    }
                });
            } catch (RemoteException e) {
                Log.e("ShellTransitions", "Error attempting to merge remote transition.", e);
            }
        }
    }

    public WindowContainerTransaction handleRequest(IBinder iBinder, TransitionRequestInfo transitionRequestInfo) {
        IRemoteTransition remoteTransition = transitionRequestInfo.getRemoteTransition();
        if (remoteTransition == null) {
            return null;
        }
        this.mRequestedRemotes.put(iBinder, remoteTransition);
        if (ShellProtoLogCache.WM_SHELL_TRANSITIONS_enabled) {
            String valueOf = String.valueOf(iBinder);
            String valueOf2 = String.valueOf(remoteTransition);
            ShellProtoLogImpl.m93v(ShellProtoLogGroup.WM_SHELL_TRANSITIONS, 214412327, 0, "RemoteTransition directly requested for %s: %s", valueOf, valueOf2);
        }
        return new WindowContainerTransaction();
    }
}
