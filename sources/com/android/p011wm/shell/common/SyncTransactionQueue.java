package com.android.p011wm.shell.common;

import android.util.Slog;
import android.view.SurfaceControl;
import android.window.WindowContainerTransaction;
import android.window.WindowContainerTransactionCallback;
import android.window.WindowOrganizer;
import java.util.ArrayList;

/* renamed from: com.android.wm.shell.common.SyncTransactionQueue */
public final class SyncTransactionQueue {
    /* access modifiers changed from: private */
    public SyncCallback mInFlight = null;
    /* access modifiers changed from: private */
    public final ShellExecutor mMainExecutor;
    /* access modifiers changed from: private */
    public final Runnable mOnReplyTimeout = new SyncTransactionQueue$$ExternalSyntheticLambda0(this);
    /* access modifiers changed from: private */
    public final ArrayList<SyncCallback> mQueue = new ArrayList<>();
    private final ArrayList<TransactionRunnable> mRunnables = new ArrayList<>();
    private final TransactionPool mTransactionPool;

    /* renamed from: com.android.wm.shell.common.SyncTransactionQueue$TransactionRunnable */
    public interface TransactionRunnable {
        void runWithTransaction(SurfaceControl.Transaction transaction);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        synchronized (this.mQueue) {
            SyncCallback syncCallback = this.mInFlight;
            if (syncCallback != null && this.mQueue.contains(syncCallback)) {
                Slog.w("SyncTransactionQueue", "Sync Transaction timed-out: " + this.mInFlight.mWCT);
                SyncCallback syncCallback2 = this.mInFlight;
                syncCallback2.onTransactionReady(syncCallback2.mId, new SurfaceControl.Transaction());
            }
        }
    }

    public SyncTransactionQueue(TransactionPool transactionPool, ShellExecutor shellExecutor) {
        this.mTransactionPool = transactionPool;
        this.mMainExecutor = shellExecutor;
    }

    public void queue(WindowContainerTransaction windowContainerTransaction) {
        SyncCallback syncCallback = new SyncCallback(windowContainerTransaction);
        synchronized (this.mQueue) {
            this.mQueue.add(syncCallback);
            if (this.mQueue.size() == 1) {
                syncCallback.send();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0025, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean queueIfWaiting(android.window.WindowContainerTransaction r3) {
        /*
            r2 = this;
            java.util.ArrayList<com.android.wm.shell.common.SyncTransactionQueue$SyncCallback> r0 = r2.mQueue
            monitor-enter(r0)
            java.util.ArrayList<com.android.wm.shell.common.SyncTransactionQueue$SyncCallback> r1 = r2.mQueue     // Catch:{ all -> 0x0026 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0026 }
            if (r1 == 0) goto L_0x000e
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return r2
        L_0x000e:
            com.android.wm.shell.common.SyncTransactionQueue$SyncCallback r1 = new com.android.wm.shell.common.SyncTransactionQueue$SyncCallback     // Catch:{ all -> 0x0026 }
            r1.<init>(r3)     // Catch:{ all -> 0x0026 }
            java.util.ArrayList<com.android.wm.shell.common.SyncTransactionQueue$SyncCallback> r3 = r2.mQueue     // Catch:{ all -> 0x0026 }
            r3.add(r1)     // Catch:{ all -> 0x0026 }
            java.util.ArrayList<com.android.wm.shell.common.SyncTransactionQueue$SyncCallback> r2 = r2.mQueue     // Catch:{ all -> 0x0026 }
            int r2 = r2.size()     // Catch:{ all -> 0x0026 }
            r3 = 1
            if (r2 != r3) goto L_0x0024
            r1.send()     // Catch:{ all -> 0x0026 }
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return r3
        L_0x0026:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.common.SyncTransactionQueue.queueIfWaiting(android.window.WindowContainerTransaction):boolean");
    }

    public void runInSync(TransactionRunnable transactionRunnable) {
        synchronized (this.mQueue) {
            if (this.mInFlight != null) {
                this.mRunnables.add(transactionRunnable);
                return;
            }
            SurfaceControl.Transaction acquire = this.mTransactionPool.acquire();
            transactionRunnable.runWithTransaction(acquire);
            acquire.apply();
            this.mTransactionPool.release(acquire);
        }
    }

    /* access modifiers changed from: private */
    public void onTransactionReceived(SurfaceControl.Transaction transaction) {
        int size = this.mRunnables.size();
        for (int i = 0; i < size; i++) {
            this.mRunnables.get(i).runWithTransaction(transaction);
        }
        this.mRunnables.clear();
        transaction.apply();
        transaction.close();
    }

    /* renamed from: com.android.wm.shell.common.SyncTransactionQueue$SyncCallback */
    private class SyncCallback extends WindowContainerTransactionCallback {
        int mId = -1;
        final WindowContainerTransaction mWCT;

        SyncCallback(WindowContainerTransaction windowContainerTransaction) {
            this.mWCT = windowContainerTransaction;
        }

        /* access modifiers changed from: package-private */
        public void send() {
            if (SyncTransactionQueue.this.mInFlight == null) {
                SyncCallback unused = SyncTransactionQueue.this.mInFlight = this;
                this.mId = new WindowOrganizer().applySyncTransaction(this.mWCT, this);
                SyncTransactionQueue.this.mMainExecutor.executeDelayed(SyncTransactionQueue.this.mOnReplyTimeout, 5300);
                return;
            }
            throw new IllegalStateException("Sync Transactions must be serialized. In Flight: " + SyncTransactionQueue.this.mInFlight.mId + " - " + SyncTransactionQueue.this.mInFlight.mWCT);
        }

        public void onTransactionReady(int i, SurfaceControl.Transaction transaction) {
            SyncTransactionQueue.this.mMainExecutor.execute(new SyncTransactionQueue$SyncCallback$$ExternalSyntheticLambda0(this, i, transaction));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x006d, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onTransactionReady$0(int r4, android.view.SurfaceControl.Transaction r5) {
            /*
                r3 = this;
                com.android.wm.shell.common.SyncTransactionQueue r0 = com.android.p011wm.shell.common.SyncTransactionQueue.this
                java.util.ArrayList r0 = r0.mQueue
                monitor-enter(r0)
                int r1 = r3.mId     // Catch:{ all -> 0x006e }
                if (r1 == r4) goto L_0x002d
                java.lang.String r5 = "SyncTransactionQueue"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x006e }
                r1.<init>()     // Catch:{ all -> 0x006e }
                java.lang.String r2 = "Got an unexpected onTransactionReady. Expected "
                r1.append(r2)     // Catch:{ all -> 0x006e }
                int r3 = r3.mId     // Catch:{ all -> 0x006e }
                r1.append(r3)     // Catch:{ all -> 0x006e }
                java.lang.String r3 = " but got "
                r1.append(r3)     // Catch:{ all -> 0x006e }
                r1.append(r4)     // Catch:{ all -> 0x006e }
                java.lang.String r3 = r1.toString()     // Catch:{ all -> 0x006e }
                android.util.Slog.e(r5, r3)     // Catch:{ all -> 0x006e }
                monitor-exit(r0)     // Catch:{ all -> 0x006e }
                return
            L_0x002d:
                com.android.wm.shell.common.SyncTransactionQueue r4 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                r1 = 0
                com.android.p011wm.shell.common.SyncTransactionQueue.SyncCallback unused = r4.mInFlight = r1     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.SyncTransactionQueue r4 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.ShellExecutor r4 = r4.mMainExecutor     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.SyncTransactionQueue r1 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                java.lang.Runnable r1 = r1.mOnReplyTimeout     // Catch:{ all -> 0x006e }
                r4.removeCallbacks(r1)     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.SyncTransactionQueue r4 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                java.util.ArrayList r4 = r4.mQueue     // Catch:{ all -> 0x006e }
                r4.remove(r3)     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.SyncTransactionQueue r4 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                r4.onTransactionReceived(r5)     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.SyncTransactionQueue r4 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                java.util.ArrayList r4 = r4.mQueue     // Catch:{ all -> 0x006e }
                boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x006e }
                if (r4 != 0) goto L_0x006c
                com.android.wm.shell.common.SyncTransactionQueue r3 = com.android.p011wm.shell.common.SyncTransactionQueue.this     // Catch:{ all -> 0x006e }
                java.util.ArrayList r3 = r3.mQueue     // Catch:{ all -> 0x006e }
                r4 = 0
                java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x006e }
                com.android.wm.shell.common.SyncTransactionQueue$SyncCallback r3 = (com.android.p011wm.shell.common.SyncTransactionQueue.SyncCallback) r3     // Catch:{ all -> 0x006e }
                r3.send()     // Catch:{ all -> 0x006e }
            L_0x006c:
                monitor-exit(r0)     // Catch:{ all -> 0x006e }
                return
            L_0x006e:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x006e }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.p011wm.shell.common.SyncTransactionQueue.SyncCallback.lambda$onTransactionReady$0(int, android.view.SurfaceControl$Transaction):void");
        }
    }
}
