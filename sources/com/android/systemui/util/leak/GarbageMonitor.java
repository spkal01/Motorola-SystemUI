package com.android.systemui.util.leak;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dumpable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$integer;
import com.android.systemui.R$string;
import com.android.systemui.SystemUI;
import com.android.systemui.p006qs.QSHost;
import com.android.systemui.p006qs.logging.QSLogger;
import com.android.systemui.p006qs.tileimpl.QSIconViewImpl;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GarbageMonitor implements Dumpable {
    private static final boolean DEBUG = Log.isLoggable("GarbageMonitor", 3);
    private static final boolean ENABLE_AM_HEAP_LIMIT;
    public static final boolean HEAP_TRACKING_ENABLED;
    public static final boolean LEAK_REPORTING_ENABLED = (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.enable_leak_reporting", false));
    private final ActivityManager mAm;
    private final Context mContext;
    private final LongSparseArray<ProcessMemInfo> mData = new LongSparseArray<>();
    private DumpTruck mDumpTruck;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public long mHeapLimit;
    private final LeakReporter mLeakReporter;
    private final ArrayList<Long> mPids = new ArrayList<>();
    private MemoryTile mQSTile;
    private final TrackedGarbage mTrackedGarbage;

    static {
        boolean z = true;
        boolean z2 = Build.IS_DEBUGGABLE;
        HEAP_TRACKING_ENABLED = z2;
        if (!z2 || !SystemProperties.getBoolean("debug.enable_sysui_heap_limit", false)) {
            z = false;
        }
        ENABLE_AM_HEAP_LIMIT = z;
    }

    public GarbageMonitor(Context context, Looper looper, LeakDetector leakDetector, LeakReporter leakReporter) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mAm = (ActivityManager) context.getSystemService("activity");
        this.mHandler = new BackgroundHeapCheckHandler(looper);
        this.mTrackedGarbage = leakDetector.getTrackedGarbage();
        this.mLeakReporter = leakReporter;
        this.mDumpTruck = new DumpTruck(applicationContext);
        if (ENABLE_AM_HEAP_LIMIT) {
            this.mHeapLimit = (long) Settings.Global.getInt(context.getContentResolver(), "systemui_am_heap_limit", applicationContext.getResources().getInteger(R$integer.watch_heap_limit));
        }
    }

    public void startLeakMonitor() {
        if (this.mTrackedGarbage != null) {
            this.mHandler.sendEmptyMessage(1000);
        }
    }

    public void startHeapTracking() {
        startTrackingProcess((long) Process.myPid(), this.mContext.getPackageName(), System.currentTimeMillis());
        this.mHandler.sendEmptyMessage(3000);
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
        long j = this.mHeapLimit;
        if (j > 0) {
            activityManager.setWatchHeapLimit(j * 1024 * 2);
        }
    }

    /* access modifiers changed from: private */
    public boolean gcAndCheckGarbage() {
        if (this.mTrackedGarbage.countOldGarbage() <= 5) {
            return false;
        }
        Runtime.getRuntime().gc();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void reinspectGarbageAfterGc() {
        int countOldGarbage = this.mTrackedGarbage.countOldGarbage();
        if (countOldGarbage > 5) {
            this.mLeakReporter.dumpLeak(countOldGarbage);
        }
    }

    public ProcessMemInfo getMemInfo(int i) {
        return this.mData.get((long) i);
    }

    public List<Long> getTrackedProcesses() {
        return this.mPids;
    }

    public void startTrackingProcess(long j, String str, long j2) {
        synchronized (this.mPids) {
            if (!this.mPids.contains(Long.valueOf(j))) {
                this.mPids.add(Long.valueOf(j));
                logPids();
                this.mData.put(j, new ProcessMemInfo(j, str, j2));
            }
        }
    }

    private void logPids() {
        if (DEBUG) {
            StringBuffer stringBuffer = new StringBuffer("Now tracking processes: ");
            for (int i = 0; i < this.mPids.size(); i++) {
                this.mPids.get(i).intValue();
                stringBuffer.append(" ");
            }
            Log.v("GarbageMonitor", stringBuffer.toString());
        }
    }

    /* access modifiers changed from: private */
    public void update() {
        synchronized (this.mPids) {
            int i = 0;
            while (true) {
                if (i >= this.mPids.size()) {
                    break;
                }
                int intValue = this.mPids.get(i).intValue();
                long[] rss = Process.getRss(intValue);
                if (rss != null || rss.length != 0) {
                    long j = rss[0];
                    long j2 = (long) intValue;
                    ProcessMemInfo processMemInfo = this.mData.get(j2);
                    long[] jArr = processMemInfo.rss;
                    int i2 = processMemInfo.head;
                    processMemInfo.currentRss = j;
                    jArr[i2] = j;
                    processMemInfo.head = (i2 + 1) % jArr.length;
                    if (j > processMemInfo.max) {
                        processMemInfo.max = j;
                    }
                    if (j == 0) {
                        if (DEBUG) {
                            Log.v("GarbageMonitor", "update: pid " + intValue + " has rss=0, it probably died");
                        }
                        this.mData.remove(j2);
                    }
                    i++;
                } else if (DEBUG) {
                    Log.e("GarbageMonitor", "update: Process.getRss() didn't provide any values.");
                }
            }
            for (int size = this.mPids.size() - 1; size >= 0; size--) {
                if (this.mData.get((long) this.mPids.get(size).intValue()) == null) {
                    this.mPids.remove(size);
                    logPids();
                }
            }
        }
        MemoryTile memoryTile = this.mQSTile;
        if (memoryTile != null) {
            memoryTile.update();
        }
    }

    /* access modifiers changed from: private */
    public void setTile(MemoryTile memoryTile) {
        this.mQSTile = memoryTile;
        if (memoryTile != null) {
            memoryTile.update();
        }
    }

    /* access modifiers changed from: private */
    public static String formatBytes(long j) {
        String[] strArr = {"B", "K", "M", "G", "T"};
        int i = 0;
        while (i < 5 && j >= 1024) {
            j /= 1024;
            i++;
        }
        return j + strArr[i];
    }

    /* access modifiers changed from: private */
    public Intent dumpHprofAndGetShareIntent() {
        return this.mDumpTruck.captureHeaps(getTrackedProcesses()).createShareIntent();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("GarbageMonitor params:");
        printWriter.println(String.format("   mHeapLimit=%d KB", new Object[]{Long.valueOf(this.mHeapLimit)}));
        printWriter.println(String.format("   GARBAGE_INSPECTION_INTERVAL=%d (%.1f mins)", new Object[]{900000L, Float.valueOf(15.0f)}));
        printWriter.println(String.format("   HEAP_TRACK_INTERVAL=%d (%.1f mins)", new Object[]{60000L, Float.valueOf(1.0f)}));
        printWriter.println(String.format("   HEAP_TRACK_HISTORY_LEN=%d (%.1f hr total)", new Object[]{720, Float.valueOf(12.0f)}));
        printWriter.println("GarbageMonitor tracked processes:");
        Iterator<Long> it = this.mPids.iterator();
        while (it.hasNext()) {
            ProcessMemInfo processMemInfo = this.mData.get(it.next().longValue());
            if (processMemInfo != null) {
                processMemInfo.dump(fileDescriptor, printWriter, strArr);
            }
        }
    }

    private static class MemoryIconDrawable extends Drawable {
        final Drawable baseIcon;

        /* renamed from: dp */
        final float f142dp;
        long limit;
        final Paint paint;
        long rss;

        public int getOpacity() {
            return -3;
        }

        MemoryIconDrawable(Context context) {
            Paint paint2 = new Paint();
            this.paint = paint2;
            this.baseIcon = context.getDrawable(R$drawable.ic_memory).mutate();
            this.f142dp = context.getResources().getDisplayMetrics().density;
            paint2.setColor(QSIconViewImpl.getIconColorForState(context, 2));
        }

        public void setRss(long j) {
            if (j != this.rss) {
                this.rss = j;
                invalidateSelf();
            }
        }

        public void setLimit(long j) {
            if (j != this.limit) {
                this.limit = j;
                invalidateSelf();
            }
        }

        public void draw(Canvas canvas) {
            this.baseIcon.draw(canvas);
            long j = this.limit;
            if (j > 0) {
                long j2 = this.rss;
                if (j2 > 0) {
                    float min = Math.min(1.0f, ((float) j2) / ((float) j));
                    Rect bounds = getBounds();
                    float f = this.f142dp;
                    canvas.translate(((float) bounds.left) + (f * 8.0f), ((float) bounds.top) + (f * 5.0f));
                    float f2 = this.f142dp;
                    canvas.drawRect(0.0f, f2 * 14.0f * (1.0f - min), (8.0f * f2) + 1.0f, (f2 * 14.0f) + 1.0f, this.paint);
                }
            }
        }

        public void setBounds(int i, int i2, int i3, int i4) {
            super.setBounds(i, i2, i3, i4);
            this.baseIcon.setBounds(i, i2, i3, i4);
        }

        public int getIntrinsicHeight() {
            return this.baseIcon.getIntrinsicHeight();
        }

        public int getIntrinsicWidth() {
            return this.baseIcon.getIntrinsicWidth();
        }

        public void setAlpha(int i) {
            this.baseIcon.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.baseIcon.setColorFilter(colorFilter);
            this.paint.setColorFilter(colorFilter);
        }

        public void setTint(int i) {
            super.setTint(i);
            this.baseIcon.setTint(i);
        }

        public void setTintList(ColorStateList colorStateList) {
            super.setTintList(colorStateList);
            this.baseIcon.setTintList(colorStateList);
        }

        public void setTintMode(PorterDuff.Mode mode) {
            super.setTintMode(mode);
            this.baseIcon.setTintMode(mode);
        }
    }

    private static class MemoryGraphIcon extends QSTile.Icon {
        long limit;
        long rss;

        private MemoryGraphIcon() {
        }

        public void setRss(long j) {
            this.rss = j;
        }

        public void setHeapLimit(long j) {
            this.limit = j;
        }

        public Drawable getDrawable(Context context) {
            MemoryIconDrawable memoryIconDrawable = new MemoryIconDrawable(context);
            memoryIconDrawable.setRss(this.rss);
            memoryIconDrawable.setLimit(this.limit);
            return memoryIconDrawable;
        }
    }

    public static class MemoryTile extends QSTileImpl<QSTile.State> {
        /* access modifiers changed from: private */
        public boolean dumpInProgress;
        /* access modifiers changed from: private */

        /* renamed from: gm */
        public final GarbageMonitor f143gm;
        private ProcessMemInfo pmi;

        public int getMetricsCategory() {
            return 0;
        }

        public MemoryTile(QSHost qSHost, Looper looper, Handler handler, FalsingManager falsingManager, MetricsLogger metricsLogger, StatusBarStateController statusBarStateController, ActivityStarter activityStarter, QSLogger qSLogger, GarbageMonitor garbageMonitor) {
            super(qSHost, looper, handler, falsingManager, metricsLogger, statusBarStateController, activityStarter, qSLogger);
            this.f143gm = garbageMonitor;
        }

        public QSTile.State newTileState() {
            return new QSTile.State();
        }

        public Intent getLongClickIntent() {
            return new Intent();
        }

        /* access modifiers changed from: protected */
        public void handleClick(View view) {
            if (!this.dumpInProgress) {
                this.dumpInProgress = true;
                refreshState();
                new Thread("HeapDumpThread") {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException unused) {
                        }
                        MemoryTile.this.mHandler.post(new GarbageMonitor$MemoryTile$1$$ExternalSyntheticLambda0(this, MemoryTile.this.f143gm.dumpHprofAndGetShareIntent()));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$run$0(Intent intent) {
                        boolean unused = MemoryTile.this.dumpInProgress = false;
                        MemoryTile.this.refreshState();
                        MemoryTile.this.getHost().collapsePanels();
                        MemoryTile.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
                    }
                }.start();
            }
        }

        public void handleSetListening(boolean z) {
            super.handleSetListening(z);
            GarbageMonitor garbageMonitor = this.f143gm;
            if (garbageMonitor != null) {
                garbageMonitor.setTile(z ? this : null);
            }
            ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
            if (!z || this.f143gm.mHeapLimit <= 0) {
                activityManager.clearWatchHeapLimit();
            } else {
                activityManager.setWatchHeapLimit(this.f143gm.mHeapLimit * 1024);
            }
        }

        public CharSequence getTileLabel() {
            return getState().label;
        }

        /* access modifiers changed from: protected */
        public void handleUpdateState(QSTile.State state, Object obj) {
            String str;
            this.pmi = this.f143gm.getMemInfo(Process.myPid());
            MemoryGraphIcon memoryGraphIcon = new MemoryGraphIcon();
            memoryGraphIcon.setHeapLimit(this.f143gm.mHeapLimit);
            boolean z = this.dumpInProgress;
            state.state = z ? 0 : 2;
            if (z) {
                str = "Dumping...";
            } else {
                str = this.mContext.getString(R$string.heap_dump_tile_name);
            }
            state.label = str;
            ProcessMemInfo processMemInfo = this.pmi;
            if (processMemInfo != null) {
                memoryGraphIcon.setRss(processMemInfo.currentRss);
                state.secondaryLabel = String.format("rss: %s / %s", new Object[]{GarbageMonitor.formatBytes(this.pmi.currentRss * 1024), GarbageMonitor.formatBytes(this.f143gm.mHeapLimit * 1024)});
            } else {
                memoryGraphIcon.setRss(0);
                state.secondaryLabel = null;
            }
            state.icon = memoryGraphIcon;
        }

        public void update() {
            refreshState();
        }
    }

    public static class ProcessMemInfo implements Dumpable {
        public long currentRss;
        public int head = 0;
        public long max = 1;
        public String name;
        public long pid;
        public long[] rss = new long[720];
        public long startTime;

        public ProcessMemInfo(long j, String str, long j2) {
            this.pid = j;
            this.name = str;
            this.startTime = j2;
        }

        public long getUptime() {
            return System.currentTimeMillis() - this.startTime;
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print("{ \"pid\": ");
            printWriter.print(this.pid);
            printWriter.print(", \"name\": \"");
            printWriter.print(this.name.replace('\"', '-'));
            printWriter.print("\", \"start\": ");
            printWriter.print(this.startTime);
            printWriter.print(", \"rss\": [");
            for (int i = 0; i < this.rss.length; i++) {
                if (i > 0) {
                    printWriter.print(",");
                }
                long[] jArr = this.rss;
                printWriter.print(jArr[(this.head + i) % jArr.length]);
            }
            printWriter.println("] }");
        }
    }

    public static class Service extends SystemUI {
        private final GarbageMonitor mGarbageMonitor;

        public Service(Context context, GarbageMonitor garbageMonitor) {
            super(context);
            this.mGarbageMonitor = garbageMonitor;
        }

        public void start() {
            boolean z = false;
            if (Settings.Secure.getInt(this.mContext.getContentResolver(), "sysui_force_enable_leak_reporting", 0) != 0) {
                z = true;
            }
            if (GarbageMonitor.LEAK_REPORTING_ENABLED || z) {
                this.mGarbageMonitor.startLeakMonitor();
            }
            if (GarbageMonitor.HEAP_TRACKING_ENABLED || z) {
                this.mGarbageMonitor.startHeapTracking();
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            GarbageMonitor garbageMonitor = this.mGarbageMonitor;
            if (garbageMonitor != null) {
                garbageMonitor.dump(fileDescriptor, printWriter, strArr);
            }
        }
    }

    private class BackgroundHeapCheckHandler extends Handler {
        BackgroundHeapCheckHandler(Looper looper) {
            super(looper);
            if (Looper.getMainLooper().equals(looper)) {
                throw new RuntimeException("BackgroundHeapCheckHandler may not run on the ui thread");
            }
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1000) {
                if (GarbageMonitor.this.gcAndCheckGarbage()) {
                    postDelayed(new C2120xcff8442a(GarbageMonitor.this), 100);
                }
                removeMessages(1000);
                sendEmptyMessageDelayed(1000, 900000);
            } else if (i == 3000) {
                GarbageMonitor.this.update();
                removeMessages(3000);
                sendEmptyMessageDelayed(3000, 60000);
            }
        }
    }
}
