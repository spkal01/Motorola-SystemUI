package com.android.systemui.p006qs.customize;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Button;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.p005qs.QSTile;
import com.android.systemui.settings.UserTracker;
import com.motorola.systemui.p014qs.DynamicTileManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/* renamed from: com.android.systemui.qs.customize.TileQueryHelper */
public class TileQueryHelper {
    /* access modifiers changed from: private */
    public final Executor mBgExecutor;
    private final Context mContext;
    private boolean mFinished;
    private TileStateListener mListener;
    private final Executor mMainExecutor;
    private final ArraySet<String> mSpecs = new ArraySet<>();
    private final ArrayList<TileInfo> mTiles = new ArrayList<>();
    private final UserTracker mUserTracker;

    /* renamed from: com.android.systemui.qs.customize.TileQueryHelper$TileInfo */
    public static class TileInfo {
        public boolean isSystem;
        public String spec;
        public QSTile.State state;
    }

    /* renamed from: com.android.systemui.qs.customize.TileQueryHelper$TileStateListener */
    public interface TileStateListener {
        void onTilesChanged(List<TileInfo> list);
    }

    public TileQueryHelper(Context context, UserTracker userTracker, Executor executor, Executor executor2) {
        this.mContext = context;
        this.mMainExecutor = executor;
        this.mBgExecutor = executor2;
        this.mUserTracker = userTracker;
    }

    public void setListener(TileStateListener tileStateListener) {
        this.mListener = tileStateListener;
    }

    public void queryTiles(QSTileHost qSTileHost) {
        this.mTiles.clear();
        this.mSpecs.clear();
        this.mFinished = false;
        addCurrentAndStockTiles(qSTileHost);
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    private void addCurrentAndStockTiles(QSTileHost qSTileHost) {
        QSTile createTile;
        String string = this.mContext.getString(R$string.quick_settings_tiles_stock);
        String string2 = Settings.Secure.getString(this.mContext.getContentResolver(), "sysui_qs_tiles");
        ArrayList arrayList = new ArrayList();
        if (MotoFeature.isPrcProduct()) {
            arrayList.add("screenshot");
        }
        if (string2 != null) {
            arrayList.addAll(Arrays.asList(string2.split(",")));
        } else {
            string2 = "";
        }
        for (String str : string.split(",")) {
            if (!string2.contains(str)) {
                arrayList.add(str);
            }
        }
        if (Build.IS_DEBUGGABLE && !string2.contains("dbg:mem")) {
            arrayList.add("dbg:mem");
        }
        DynamicTileManager.getInstance(qSTileHost).addStockTiles(arrayList);
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (!str2.startsWith("custom(") && (createTile = qSTileHost.createTile(str2)) != null) {
                if (!createTile.isAvailable()) {
                    Log.d("TileQueryHelper", "Destroying not available tile: " + str2);
                    createTile.setTileSpec(str2);
                    createTile.destroy();
                } else {
                    createTile.setTileSpec(str2);
                    arrayList2.add(createTile);
                }
            }
        }
        new TileCollector(arrayList2, qSTileHost).startListening();
    }

    /* renamed from: com.android.systemui.qs.customize.TileQueryHelper$TilePair */
    private static class TilePair {
        boolean mReady;
        QSTile mTile;

        private TilePair() {
            this.mReady = false;
        }
    }

    /* renamed from: com.android.systemui.qs.customize.TileQueryHelper$TileCollector */
    private class TileCollector implements QSTile.Callback {
        private final QSTileHost mQSTileHost;
        private final List<TilePair> mQSTileList = new ArrayList();

        public void onAnnouncementRequested(CharSequence charSequence) {
        }

        public void onScanStateChanged(boolean z) {
        }

        public void onShowDetail(boolean z) {
        }

        public void onToggleStateChanged(boolean z) {
        }

        TileCollector(List<QSTile> list, QSTileHost qSTileHost) {
            for (QSTile qSTile : list) {
                TilePair tilePair = new TilePair();
                tilePair.mTile = qSTile;
                this.mQSTileList.add(tilePair);
            }
            this.mQSTileHost = qSTileHost;
            if (list.isEmpty()) {
                TileQueryHelper.this.mBgExecutor.execute(new TileQueryHelper$TileCollector$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public void finished() {
            TileQueryHelper.this.notifyTilesChanged(false);
            TileQueryHelper.this.addPackageTiles(this.mQSTileHost);
        }

        /* access modifiers changed from: private */
        public void startListening() {
            for (TilePair next : this.mQSTileList) {
                next.mTile.addCallback(this);
                next.mTile.setListening(this, true);
                next.mTile.refreshState();
            }
        }

        public void onStateChanged(QSTile.State state) {
            boolean z = true;
            for (TilePair next : this.mQSTileList) {
                if (!next.mReady && next.mTile.isTileReady()) {
                    next.mTile.removeCallback(this);
                    next.mTile.setListening(this, false);
                    next.mReady = true;
                } else if (!next.mReady) {
                    z = false;
                }
            }
            if (z) {
                for (TilePair tilePair : this.mQSTileList) {
                    QSTile qSTile = tilePair.mTile;
                    QSTile.State copy = qSTile.getState().copy();
                    copy.label = qSTile.getTileLabel();
                    qSTile.destroy();
                    TileQueryHelper.this.addTile(qSTile.getTileSpec(), (CharSequence) null, copy, true);
                }
                finished();
            }
        }
    }

    /* access modifiers changed from: private */
    public void addPackageTiles(QSTileHost qSTileHost) {
        this.mBgExecutor.execute(new TileQueryHelper$$ExternalSyntheticLambda0(this, qSTileHost));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addPackageTiles$0(QSTileHost qSTileHost) {
        Collection<QSTile> tiles = qSTileHost.getTiles();
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(new Intent("android.service.quicksettings.action.QS_TILE"), 0, this.mUserTracker.getUserId());
        String string = this.mContext.getString(R$string.quick_settings_tiles_stock);
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
            if (!string.contains(componentName.flattenToString())) {
                CharSequence loadLabel = resolveInfo.serviceInfo.applicationInfo.loadLabel(packageManager);
                String spec = CustomTile.toSpec(componentName);
                if (DynamicTileManager.getInstance(qSTileHost).isTileAvailable(spec)) {
                    QSTile.State state = getState(tiles, spec);
                    if (state != null) {
                        addTile(spec, loadLabel, state, false);
                    } else {
                        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                        if (serviceInfo.icon != 0 || serviceInfo.applicationInfo.icon != 0) {
                            Drawable loadIcon = serviceInfo.loadIcon(packageManager);
                            if ("android.permission.BIND_QUICK_SETTINGS_TILE".equals(resolveInfo.serviceInfo.permission) && loadIcon != null) {
                                loadIcon.mutate();
                                loadIcon.setTint(this.mContext.getColor(17170443));
                                CharSequence loadLabel2 = resolveInfo.serviceInfo.loadLabel(packageManager);
                                createStateAndAddTile(spec, loadIcon, loadLabel2 != null ? loadLabel2.toString() : "null", loadLabel);
                            }
                        }
                    }
                }
            }
        }
        notifyTilesChanged(true);
    }

    /* access modifiers changed from: private */
    public void notifyTilesChanged(boolean z) {
        this.mMainExecutor.execute(new TileQueryHelper$$ExternalSyntheticLambda1(this, new ArrayList(this.mTiles), z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyTilesChanged$1(ArrayList arrayList, boolean z) {
        TileStateListener tileStateListener = this.mListener;
        if (tileStateListener != null) {
            tileStateListener.onTilesChanged(arrayList);
        }
        this.mFinished = z;
    }

    private QSTile.State getState(Collection<QSTile> collection, String str) {
        for (QSTile next : collection) {
            if (str.equals(next.getTileSpec())) {
                return next.getState().copy();
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void addTile(String str, CharSequence charSequence, QSTile.State state, boolean z) {
        if (this.mSpecs.contains(str)) {
            Iterator<TileInfo> it = this.mTiles.iterator();
            while (it.hasNext()) {
                TileInfo next = it.next();
                if (str.equals(next.spec)) {
                    next.state.state = state.state;
                }
            }
            return;
        }
        TileInfo tileInfo = new TileInfo();
        tileInfo.state = state;
        state.dualTarget = false;
        state.expandedAccessibilityClassName = Button.class.getName();
        tileInfo.spec = str;
        QSTile.State state2 = tileInfo.state;
        if (z || TextUtils.equals(state.label, charSequence)) {
            charSequence = null;
        }
        state2.secondaryLabel = charSequence;
        tileInfo.isSystem = z;
        this.mTiles.add(tileInfo);
        this.mSpecs.add(str);
    }

    private void createStateAndAddTile(String str, Drawable drawable, CharSequence charSequence, CharSequence charSequence2) {
        QSTile.State state = new QSTile.State();
        state.state = 1;
        state.label = charSequence;
        state.contentDescription = charSequence;
        state.icon = new QSTileImpl.DrawableIcon(drawable);
        addTile(str, charSequence2, state, false);
    }
}
