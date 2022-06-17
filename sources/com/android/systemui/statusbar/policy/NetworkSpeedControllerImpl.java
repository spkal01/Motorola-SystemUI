package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R$dimen;
import com.android.systemui.R$layout;
import com.android.systemui.moto.DualSimIconController;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.motorola.android.provider.MotorolaSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class NetworkSpeedControllerImpl implements CallbackController, Dumpable, ConfigurationController.ConfigurationListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (!Build.IS_USER);
    private final int NET_COUNT = 3;
    private final ArrayList<NetworkSpeedController$Callback> mCallbacks = new ArrayList<>();
    private String mContentDescription = "0\nB/s";
    private final Context mContext;
    /* access modifiers changed from: private */
    public boolean mHidden = false;
    private final StatusBarIconController mIconController;
    private int mIconWidth;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (NetworkSpeedControllerImpl.DEBUG) {
                Log.d("NetworkSpeedController", " onReceive: " + action);
            }
            if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                boolean unused = NetworkSpeedControllerImpl.this.mIsWifiConnected = ((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected();
                if (NetworkSpeedControllerImpl.this.mIsWifiConnected && NetworkSpeedControllerImpl.this.mPaused) {
                    NetworkSpeedControllerImpl.this.startNetworkSpeedUpdate();
                }
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                try {
                    boolean unused2 = NetworkSpeedControllerImpl.this.mIsNetworkConnected = ((NetworkInfo) intent.getExtras().get("networkInfo")).isConnected();
                    if (NetworkSpeedControllerImpl.this.mIsNetworkConnected && NetworkSpeedControllerImpl.this.mPaused) {
                        NetworkSpeedControllerImpl.this.startNetworkSpeedUpdate();
                    }
                } catch (Exception unused3) {
                }
            } else if ("android.intent.action.AIRPLANE_MODE".equals(action)) {
                if (NetworkSpeedControllerImpl.this.isAirplaneMode() && !NetworkSpeedControllerImpl.this.mPaused) {
                    NetworkSpeedControllerImpl.this.mNHandler.removeCallbacks(NetworkSpeedControllerImpl.this.mNRunnable);
                    boolean unused4 = NetworkSpeedControllerImpl.this.mPaused = true;
                    boolean unused5 = NetworkSpeedControllerImpl.this.mHidden = true;
                    NetworkSpeedControllerImpl.this.resetNetworkSpeed();
                }
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                boolean unused6 = NetworkSpeedControllerImpl.this.mIsScreenOff = false;
                NetworkSpeedControllerImpl.this.startNetworkSpeedUpdate();
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                boolean unused7 = NetworkSpeedControllerImpl.this.mIsScreenOff = true;
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsNetworkConnected;
    private boolean mIsNetworkSpeedEnabled;
    /* access modifiers changed from: private */
    public boolean mIsScreenOff = false;
    /* access modifiers changed from: private */
    public boolean mIsWifiConnected;
    /* access modifiers changed from: private */
    public Handler mMainHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 1) {
                if (NetworkSpeedControllerImpl.this.mNetworkSpeedTextView != null) {
                    NetworkSpeedControllerImpl.this.mNetworkSpeedTextView.setText(NetworkSpeedControllerImpl.this.calculateSpeedText(message.arg1));
                    NetworkSpeedControllerImpl.this.updateNetworkSpeed();
                }
            } else if (i == 2 && NetworkSpeedControllerImpl.this.mNetworkSpeedTextView != null) {
                NetworkSpeedControllerImpl.this.mNetworkSpeedTextView.setText(NetworkSpeedControllerImpl.this.getSpanString("0", "\nB/s"));
                NetworkSpeedControllerImpl.this.updateNetworkSpeed();
            }
        }
    };
    /* access modifiers changed from: private */
    public final Handler mNHandler;
    /* access modifiers changed from: private */
    public Runnable mNRunnable = new Runnable() {
        public void run() {
            NetworkSpeedControllerImpl.this.mNHandler.removeCallbacks(NetworkSpeedControllerImpl.this.mNRunnable);
            if (NetworkSpeedControllerImpl.this.isPauseRefresh()) {
                NetworkSpeedControllerImpl.this.resetNetworkSpeed();
                return;
            }
            Message obtainMessage = NetworkSpeedControllerImpl.this.mMainHandler.obtainMessage();
            obtainMessage.what = 1;
            obtainMessage.arg1 = NetworkSpeedControllerImpl.this.getAverageSpeed();
            NetworkSpeedControllerImpl.this.mMainHandler.sendMessage(obtainMessage);
            NetworkSpeedControllerImpl.this.mNHandler.postDelayed(NetworkSpeedControllerImpl.this.mNRunnable, 3000);
        }
    };
    /* access modifiers changed from: private */
    public TextView mNetworkSpeedTextView;
    /* access modifiers changed from: private */
    public boolean mPaused = true;
    private RelativeSizeSpan mRelativeSizeSpan = new RelativeSizeSpan(1.3f);
    private final String mSlotNetworkSpeed;
    private long mTotalBytes = getCurrentTotalBytes();

    public NetworkSpeedControllerImpl(Context context, StatusBarIconController statusBarIconController, Handler handler) {
        this.mContext = context;
        this.mIconController = statusBarIconController;
        this.mNHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
        this.mSlotNetworkSpeed = context.getString(17041512);
        init();
    }

    private void init() {
        registerContentSwitchObserver(MotorolaSettings.Global.getUriFor("internet_speed_switch"));
        if (!isNetworkSpeedEnabled()) {
            Log.d("NetworkSpeedController", "Network speed icon not enabled.");
            this.mIsNetworkSpeedEnabled = false;
            return;
        }
        updateNetworkSpeedState(true);
    }

    /* access modifiers changed from: private */
    public void updateNetworkSpeedState(boolean z) {
        Class cls = ConfigurationController.class;
        if (this.mIsNetworkSpeedEnabled != z) {
            this.mIsNetworkSpeedEnabled = z;
            fireNetworkSpeedAvailabilityChanged();
        }
        if (z) {
            initTextView();
            registerReceiver();
            startNetworkSpeedUpdate();
            ((ConfigurationController) Dependency.get(cls)).addCallback(this);
            return;
        }
        this.mNetworkSpeedTextView = null;
        unRegisterReceiver();
        stopNetworkSpeedUpdate();
        ((ConfigurationController) Dependency.get(cls)).removeCallback(this);
    }

    /* access modifiers changed from: private */
    public void startNetworkSpeedUpdate() {
        this.mTotalBytes = getCurrentTotalBytes();
        updateNetworkSpeed();
        this.mNHandler.postDelayed(this.mNRunnable, 500);
        this.mPaused = false;
        this.mHidden = false;
    }

    private void stopNetworkSpeedUpdate() {
        updateNetworkSpeed();
        this.mNHandler.removeCallbacks(this.mNRunnable);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        try {
            this.mContext.registerReceiver(this.mIntentReceiver, intentFilter);
        } catch (Exception e) {
            Log.e("NetworkSpeedController", "registerReceiver exception, e = " + e);
        }
    }

    private void unRegisterReceiver() {
        try {
            this.mContext.unregisterReceiver(this.mIntentReceiver);
        } catch (Exception e) {
            Log.e("NetworkSpeedController", "unRegisterReceiver exception, e = " + e);
        }
    }

    public boolean isNetworkSpeedEnabled() {
        int i = MotorolaSettings.Global.getInt(this.mContext.getContentResolver(), "internet_speed_switch", 0);
        if (DEBUG) {
            Log.d("NetworkSpeedController", "isNetworkSpeedEnabled: " + i);
        }
        if (i == 1) {
            return true;
        }
        return false;
    }

    private void initTextView() {
        this.mIconWidth = this.mContext.getResources().getDimensionPixelSize(R$dimen.network_speed_icon_size);
        TextView textView = (TextView) LayoutInflater.from(this.mContext).inflate(R$layout.network_speed_view, (ViewGroup) null);
        this.mNetworkSpeedTextView = textView;
        textView.setText(getSpanString("0", "\nB/s"));
        this.mNetworkSpeedTextView.setWidth(this.mIconWidth);
    }

    public void updateNetworkSpeed() {
        if (!this.mIsNetworkSpeedEnabled || this.mHidden) {
            this.mIconController.setIcon(this.mSlotNetworkSpeed, (StatusBarIcon) null);
            return;
        }
        this.mIconController.setIcon(this.mSlotNetworkSpeed, getIcon());
        this.mIconController.setIconVisibility(this.mSlotNetworkSpeed, true);
    }

    /* access modifiers changed from: private */
    public SpannableString calculateSpeedText(int i) {
        String str;
        String str2;
        DecimalFormat decimalFormat;
        DecimalFormat decimalFormat2;
        DecimalFormat decimalFormat3;
        if (i < 1000) {
            if (i < 0) {
                i = 0;
            }
            str = String.valueOf(i);
            str2 = "\nB/s";
        } else if (i >= 1048576000) {
            double d = ((double) i) / 1.073741824E9d;
            if (d >= 100.0d) {
                decimalFormat3 = new DecimalFormat("0");
            } else if (d >= 10.0d) {
                decimalFormat3 = new DecimalFormat("0.0");
            } else {
                decimalFormat3 = new DecimalFormat("0.00");
            }
            str = decimalFormat3.format(d);
            str2 = "\nGB/s";
        } else if (i >= 1024000) {
            double d2 = ((double) i) / 1048576.0d;
            if (d2 >= 100.0d) {
                decimalFormat2 = new DecimalFormat("0");
            } else if (d2 >= 10.0d) {
                decimalFormat2 = new DecimalFormat("0.0");
            } else {
                decimalFormat2 = new DecimalFormat("0.00");
            }
            str = decimalFormat2.format(d2);
            str2 = "\nMB/s";
        } else {
            double d3 = ((double) i) / 1024.0d;
            if (d3 >= 100.0d) {
                decimalFormat = new DecimalFormat("0");
            } else if (d3 >= 10.0d) {
                decimalFormat = new DecimalFormat("0.0");
            } else {
                decimalFormat = new DecimalFormat("0.00");
            }
            str = decimalFormat.format(d3);
            str2 = "\nKB/s";
        }
        String str3 = str + str2;
        if (DEBUG) {
            Log.d("NetworkSpeedController", "calculateSpeedText:  realSpeedStr: " + str3);
        }
        this.mContentDescription = str3;
        getSpanString(str, str2);
        return getSpanString(str, str2);
    }

    /* access modifiers changed from: private */
    public SpannableString getSpanString(String str, String str2) {
        SpannableString spannableString = new SpannableString(str + str2);
        spannableString.setSpan(this.mRelativeSizeSpan, 0, str.length(), 0);
        return spannableString;
    }

    public Bitmap getIconBitmap() {
        TextView textView = this.mNetworkSpeedTextView;
        if (textView == null) {
            return null;
        }
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        TextView textView2 = this.mNetworkSpeedTextView;
        textView2.layout(0, 0, this.mIconWidth, textView2.getMeasuredHeight());
        this.mNetworkSpeedTextView.setDrawingCacheEnabled(true);
        if (DEBUG) {
            Log.d("TAG", "mNetworkSpeedTextView: height: " + this.mNetworkSpeedTextView.getHeight());
        }
        Bitmap createBitmap = Bitmap.createBitmap(this.mNetworkSpeedTextView.getDrawingCache());
        this.mNetworkSpeedTextView.nullLayouts();
        this.mNetworkSpeedTextView.destroyDrawingCache();
        return createBitmap;
    }

    public StatusBarIcon getIcon() {
        return new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithBitmap(getIconBitmap()), 0, 0, this.mContentDescription);
    }

    /* access modifiers changed from: private */
    public boolean isAirplaneMode() {
        try {
            return Settings.System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0;
        } catch (Exception unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void resetNetworkSpeed() {
        Message obtainMessage = this.mMainHandler.obtainMessage();
        obtainMessage.what = 2;
        this.mMainHandler.sendMessage(obtainMessage);
    }

    /* access modifiers changed from: private */
    public boolean isPauseRefresh() {
        if (this.mIsScreenOff) {
            this.mPaused = true;
            return true;
        } else if (this.mIsWifiConnected || this.mIsNetworkConnected) {
            return false;
        } else {
            this.mPaused = true;
            if (!isNetworkConnected()) {
                this.mHidden = true;
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public int getAverageSpeed() {
        long currentTotalBytes = getCurrentTotalBytes() - this.mTotalBytes;
        this.mTotalBytes = getCurrentTotalBytes();
        return ((int) currentTotalBytes) / 3;
    }

    private long getCurrentTotalBytes() {
        long currentLocalRxBytes = getCurrentLocalRxBytes() + getCurrentLocalTxBytes();
        long totalRxBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        long j = totalRxBytes - currentLocalRxBytes;
        if (DEBUG) {
            Log.v("NetworkSpeedController", " getCurrentTotalBytes: t1 = " + totalRxBytes + "t2 = " + currentLocalRxBytes + "t = " + j);
        }
        return j;
    }

    private long getCurrentLocalRxBytes() {
        try {
            return ((Long) TrafficStats.class.getDeclaredMethod("getRxBytes", new Class[]{String.class}).invoke((Object) null, new Object[]{"lo"})).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long getCurrentLocalTxBytes() {
        try {
            return ((Long) TrafficStats.class.getDeclaredMethod("getTxBytes", new Class[]{String.class}).invoke((Object) null, new Object[]{"lo"})).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean isNetworkConnected() {
        if (this.mIsNetworkConnected || this.mIsWifiConnected) {
            return true;
        }
        if (((TelephonyManager) this.mContext.getSystemService("phone")).getDataEnabled() && this.mIsNetworkConnected) {
            return true;
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
            if (connectivityManager != null) {
                NetworkInfo.State state = connectivityManager.getNetworkInfo(0).getState();
                NetworkInfo.State state2 = connectivityManager.getNetworkInfo(1).getState();
                if (state != NetworkInfo.State.CONNECTED) {
                    if (state != NetworkInfo.State.CONNECTING) {
                        if (state2 != NetworkInfo.State.CONNECTED) {
                            if (state2 != NetworkInfo.State.CONNECTING) {
                                NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
                                if (allNetworkInfo != null) {
                                    for (int i = 0; i < allNetworkInfo.length; i++) {
                                        if (allNetworkInfo[i] != null && allNetworkInfo[i].isConnected()) {
                                            return true;
                                        }
                                        if (allNetworkInfo[i] != null && allNetworkInfo[i].getState() != null && allNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("NetworkSpeedController", "isNetworkConnected exception, e = " + e);
            return false;
        }
    }

    public void registerContentSwitchObserver(Uri uri) {
        this.mContext.getContentResolver().registerContentObserver(uri, false, new ContentObserver(new Handler()) {
            public void onChange(boolean z) {
                boolean isNetworkSpeedEnabled = NetworkSpeedControllerImpl.this.isNetworkSpeedEnabled();
                if (NetworkSpeedControllerImpl.DEBUG) {
                    Log.d("NetworkSpeedController", "onChange: selfChange = " + z + " enable = " + isNetworkSpeedEnabled);
                }
                NetworkSpeedControllerImpl.this.updateNetworkSpeedState(isNetworkSpeedEnabled);
                ((DualSimIconController) Dependency.get(DualSimIconController.class)).updateShowNetworkSpeed(isNetworkSpeedEnabled);
            }
        });
    }

    public void onConfigChanged(Configuration configuration) {
        if (this.mIsNetworkSpeedEnabled) {
            initTextView();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addCallback(com.android.systemui.statusbar.policy.NetworkSpeedController$Callback r5) {
        /*
            r4 = this;
            java.util.ArrayList<com.android.systemui.statusbar.policy.NetworkSpeedController$Callback> r0 = r4.mCallbacks
            monitor-enter(r0)
            if (r5 == 0) goto L_0x0039
            java.util.ArrayList<com.android.systemui.statusbar.policy.NetworkSpeedController$Callback> r1 = r4.mCallbacks     // Catch:{ all -> 0x003b }
            boolean r1 = r1.contains(r5)     // Catch:{ all -> 0x003b }
            if (r1 == 0) goto L_0x000e
            goto L_0x0039
        L_0x000e:
            boolean r1 = DEBUG     // Catch:{ all -> 0x003b }
            if (r1 == 0) goto L_0x0028
            java.lang.String r1 = "NetworkSpeedController"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x003b }
            r2.<init>()     // Catch:{ all -> 0x003b }
            java.lang.String r3 = "addCallback "
            r2.append(r3)     // Catch:{ all -> 0x003b }
            r2.append(r5)     // Catch:{ all -> 0x003b }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x003b }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x003b }
        L_0x0028:
            java.util.ArrayList<com.android.systemui.statusbar.policy.NetworkSpeedController$Callback> r1 = r4.mCallbacks     // Catch:{ all -> 0x003b }
            r1.add(r5)     // Catch:{ all -> 0x003b }
            android.os.Handler r1 = r4.mMainHandler     // Catch:{ all -> 0x003b }
            com.android.systemui.statusbar.policy.NetworkSpeedControllerImpl$$ExternalSyntheticLambda0 r2 = new com.android.systemui.statusbar.policy.NetworkSpeedControllerImpl$$ExternalSyntheticLambda0     // Catch:{ all -> 0x003b }
            r2.<init>(r4, r5)     // Catch:{ all -> 0x003b }
            r1.post(r2)     // Catch:{ all -> 0x003b }
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return
        L_0x0039:
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            return
        L_0x003b:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003b }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkSpeedControllerImpl.addCallback(com.android.systemui.statusbar.policy.NetworkSpeedController$Callback):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addCallback$0(NetworkSpeedController$Callback networkSpeedController$Callback) {
        networkSpeedController$Callback.onNetworkSpeedAvailabilityChanged(isNetworkSpeedEnabled());
    }

    public void removeCallback(NetworkSpeedController$Callback networkSpeedController$Callback) {
        if (networkSpeedController$Callback != null) {
            if (DEBUG) {
                Log.d("NetworkSpeedController", "removeCallback " + networkSpeedController$Callback);
            }
            synchronized (this.mCallbacks) {
                this.mCallbacks.remove(networkSpeedController$Callback);
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NetworkSpeedController state:");
        printWriter.print("  available= ");
        printWriter.println(isNetworkSpeedEnabled());
        printWriter.print("  mIsNetworkSpeedEnabled= ");
        printWriter.println(this.mIsNetworkSpeedEnabled);
    }

    private void fireNetworkSpeedAvailabilityChanged() {
        ArrayList<NetworkSpeedController$Callback> arrayList;
        synchronized (this.mCallbacks) {
            arrayList = new ArrayList<>(this.mCallbacks);
        }
        for (NetworkSpeedController$Callback onNetworkSpeedAvailabilityChanged : arrayList) {
            onNetworkSpeedAvailabilityChanged.onNetworkSpeedAvailabilityChanged(this.mIsNetworkSpeedEnabled);
        }
    }
}
