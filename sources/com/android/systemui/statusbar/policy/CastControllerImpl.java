package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.media.projection.MediaProjectionInfo;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.R$string;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CastControllerImpl implements CastController {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("CastController", 3);
    private boolean mCallbackRegistered;
    @GuardedBy({"mCallbacks"})
    private final ArrayList<CastController.Callback> mCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Context mContext;
    private boolean mDiscovering;
    private final Object mDiscoveringLock = new Object();
    private final MediaRouter.SimpleCallback mMediaCallback;
    private final MediaRouter mMediaRouter;
    private MediaProjectionInfo mProjection;
    private final MediaProjectionManager.Callback mProjectionCallback;
    private final Object mProjectionLock = new Object();
    private final MediaProjectionManager mProjectionManager;
    private final BroadcastReceiver mReceiver;
    private final ArrayMap<String, MediaRouter.RouteInfo> mRoutes = new ArrayMap<>();

    public CastControllerImpl(Context context, DumpManager dumpManager) {
        C19881 r0 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED")) {
                    CastControllerImpl.this.updateRemoteDisplays();
                }
            }
        };
        this.mReceiver = r0;
        this.mMediaCallback = new MediaRouter.SimpleCallback() {
            public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                if (CastControllerImpl.DEBUG) {
                    Log.d("CastController", "onRouteAdded: " + CastControllerImpl.routeToString(routeInfo));
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }

            public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                if (CastControllerImpl.DEBUG) {
                    Log.d("CastController", "onRouteChanged: " + CastControllerImpl.routeToString(routeInfo));
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }

            public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                if (CastControllerImpl.DEBUG) {
                    Log.d("CastController", "onRouteRemoved: " + CastControllerImpl.routeToString(routeInfo));
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }

            public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                if (CastControllerImpl.DEBUG) {
                    Log.d("CastController", "onRouteSelected(" + i + "): " + CastControllerImpl.routeToString(routeInfo));
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }

            public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                if (CastControllerImpl.DEBUG) {
                    Log.d("CastController", "onRouteUnselected(" + i + "): " + CastControllerImpl.routeToString(routeInfo));
                }
                CastControllerImpl.this.updateRemoteDisplays();
            }
        };
        C19903 r1 = new MediaProjectionManager.Callback() {
            public void onStart(MediaProjectionInfo mediaProjectionInfo) {
                CastControllerImpl.this.setProjection(mediaProjectionInfo, true);
            }

            public void onStop(MediaProjectionInfo mediaProjectionInfo) {
                CastControllerImpl.this.setProjection(mediaProjectionInfo, false);
                if (CastControllerImpl.DEBUG) {
                    Log.d("CastController", "info.packageName=" + mediaProjectionInfo.getPackageName());
                }
                if (CastControllerImpl.this.mContext.getPackageName().equals(mediaProjectionInfo.getPackageName())) {
                    CastControllerImpl.this.mContext.startServiceAsUser(RecordingService.getStopIntent(CastControllerImpl.this.mContext), UserHandle.of(ActivityManager.getCurrentUser()));
                }
            }
        };
        this.mProjectionCallback = r1;
        this.mContext = context;
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService("media_router");
        this.mMediaRouter = mediaRouter;
        mediaRouter.setRouterGroupId("android.media.mirroring_group");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService("media_projection");
        this.mProjectionManager = mediaProjectionManager;
        this.mProjection = mediaProjectionManager.getActiveProjectionInfo();
        mediaProjectionManager.addCallback(r1, new Handler());
        dumpManager.registerDumpable("CastController", this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
        context.registerReceiver(r0, intentFilter);
        if (DEBUG) {
            Log.d("CastController", "new CastController()");
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("CastController state:");
        printWriter.print("  mDiscovering=");
        printWriter.println(this.mDiscovering);
        printWriter.print("  mCallbackRegistered=");
        printWriter.println(this.mCallbackRegistered);
        printWriter.print("  mCallbacks.size=");
        synchronized (this.mCallbacks) {
            printWriter.println(this.mCallbacks.size());
        }
        printWriter.print("  mRoutes.size=");
        printWriter.println(this.mRoutes.size());
        for (int i = 0; i < this.mRoutes.size(); i++) {
            printWriter.print("    ");
            printWriter.println(routeToString(this.mRoutes.valueAt(i)));
        }
        printWriter.print("  mProjection=");
        printWriter.println(this.mProjection);
    }

    public void addCallback(CastController.Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.add(callback);
        }
        fireOnCastDevicesChanged(callback);
        synchronized (this.mDiscoveringLock) {
            handleDiscoveryChangeLocked();
        }
    }

    public void removeCallback(CastController.Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
        }
        synchronized (this.mDiscoveringLock) {
            handleDiscoveryChangeLocked();
        }
    }

    public void setDiscovering(boolean z) {
        synchronized (this.mDiscoveringLock) {
            if (this.mDiscovering != z) {
                this.mDiscovering = z;
                if (DEBUG) {
                    Log.d("CastController", "setDiscovering: " + z);
                }
                handleDiscoveryChangeLocked();
            }
        }
    }

    private void handleDiscoveryChangeLocked() {
        boolean isEmpty;
        if (this.mCallbackRegistered) {
            this.mMediaRouter.removeCallback(this.mMediaCallback);
            this.mCallbackRegistered = false;
        }
        if (this.mDiscovering) {
            this.mMediaRouter.addCallback(4, this.mMediaCallback, 4);
            this.mCallbackRegistered = true;
            return;
        }
        synchronized (this.mCallbacks) {
            isEmpty = this.mCallbacks.isEmpty();
        }
        if (!isEmpty) {
            this.mMediaRouter.addCallback(4, this.mMediaCallback, 8);
            this.mCallbackRegistered = true;
        }
    }

    public void setCurrentUserId(int i) {
        this.mMediaRouter.rebindAsUser(i);
    }

    public List<CastController.CastDevice> getCastDevices() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mRoutes) {
            for (MediaRouter.RouteInfo next : this.mRoutes.values()) {
                CastController.CastDevice castDevice = new CastController.CastDevice();
                castDevice.f134id = next.getTag().toString();
                CharSequence name = next.getName(this.mContext);
                String str = null;
                castDevice.name = name != null ? name.toString() : null;
                CharSequence description = next.getDescription();
                if (description != null) {
                    str = description.toString();
                }
                castDevice.description = str;
                int statusCode = next.getStatusCode();
                if (statusCode == 2) {
                    castDevice.state = 1;
                } else {
                    if (!next.isSelected()) {
                        if (statusCode != 6) {
                            castDevice.state = 0;
                        }
                    }
                    castDevice.state = 2;
                }
                castDevice.tag = next;
                arrayList.add(castDevice);
            }
        }
        synchronized (this.mProjectionLock) {
            if (this.mProjection != null) {
                CastController.CastDevice castDevice2 = new CastController.CastDevice();
                castDevice2.f134id = this.mProjection.getPackageName();
                castDevice2.name = getAppName(this.mProjection.getPackageName());
                castDevice2.description = this.mContext.getString(R$string.quick_settings_casting);
                castDevice2.state = 2;
                castDevice2.tag = this.mProjection;
                arrayList.add(castDevice2);
            }
        }
        return arrayList;
    }

    public void startCasting(CastController.CastDevice castDevice) {
        Object obj;
        if (castDevice != null && (obj = castDevice.tag) != null) {
            boolean z = obj instanceof MediaProjectionInfo;
            boolean z2 = DEBUG;
            if (z2) {
                Log.d("CastController", "startCasting isProjection=" + z);
            }
            if (!z) {
                MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) castDevice.tag;
                if (z2) {
                    Log.d("CastController", "startCasting: " + routeToString(routeInfo));
                }
                this.mMediaRouter.selectRoute(4, routeInfo);
            }
        }
    }

    public void stopCasting(CastController.CastDevice castDevice) {
        boolean z = castDevice.tag instanceof MediaProjectionInfo;
        if (DEBUG) {
            Log.d("CastController", "stopCasting isProjection=" + z);
        }
        if (z) {
            MediaProjectionInfo mediaProjectionInfo = (MediaProjectionInfo) castDevice.tag;
            if (Objects.equals(this.mProjectionManager.getActiveProjectionInfo(), mediaProjectionInfo)) {
                this.mProjectionManager.stopActiveProjection();
                return;
            }
            Log.w("CastController", "Projection is no longer active: " + mediaProjectionInfo);
            return;
        }
        this.mMediaRouter.getFallbackRoute().select();
    }

    /* access modifiers changed from: private */
    public void setProjection(MediaProjectionInfo mediaProjectionInfo, boolean z) {
        boolean z2;
        MediaProjectionInfo mediaProjectionInfo2 = this.mProjection;
        synchronized (this.mProjectionLock) {
            boolean equals = Objects.equals(mediaProjectionInfo, this.mProjection);
            z2 = true;
            if (z && !equals) {
                this.mProjection = mediaProjectionInfo;
            } else if (z || !equals) {
                z2 = false;
            } else {
                this.mProjection = null;
            }
        }
        if (z2) {
            if (DEBUG) {
                Log.d("CastController", "setProjection: " + mediaProjectionInfo2 + " -> " + this.mProjection);
            }
            fireOnCastDevicesChanged();
        }
    }

    private String getAppName(String str) {
        PackageManager packageManager = this.mContext.getPackageManager();
        if (Utils.isHeadlessRemoteDisplayProvider(packageManager, str)) {
            return "";
        }
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                if (!TextUtils.isEmpty(loadLabel)) {
                    return loadLabel.toString();
                }
            }
            Log.w("CastController", "No label found for package: " + str);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("CastController", "Error getting appName for package: " + str, e);
        }
        return str;
    }

    /* access modifiers changed from: private */
    public void updateRemoteDisplays() {
        synchronized (this.mRoutes) {
            this.mRoutes.clear();
            int routeCount = this.mMediaRouter.getRouteCount();
            for (int i = 0; i < routeCount; i++) {
                MediaRouter.RouteInfo routeAt = this.mMediaRouter.getRouteAt(i);
                if (routeAt.isEnabled()) {
                    if (routeAt.matchesTypes(4)) {
                        ensureTagExists(routeAt);
                        this.mRoutes.put(routeAt.getTag().toString(), routeAt);
                    }
                }
            }
            MediaRouter.RouteInfo selectedRoute = this.mMediaRouter.getSelectedRoute(4);
            if (selectedRoute != null && !selectedRoute.isDefault()) {
                ensureTagExists(selectedRoute);
                this.mRoutes.put(selectedRoute.getTag().toString(), selectedRoute);
            }
        }
        fireOnCastDevicesChanged();
    }

    private void ensureTagExists(MediaRouter.RouteInfo routeInfo) {
        if (routeInfo.getTag() == null) {
            routeInfo.setTag(UUID.randomUUID().toString());
        }
    }

    /* access modifiers changed from: package-private */
    public void fireOnCastDevicesChanged() {
        synchronized (this.mCallbacks) {
            Iterator<CastController.Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                fireOnCastDevicesChanged(it.next());
            }
        }
    }

    private void fireOnCastDevicesChanged(CastController.Callback callback) {
        callback.onCastDevicesChanged();
    }

    /* access modifiers changed from: private */
    public static String routeToString(MediaRouter.RouteInfo routeInfo) {
        if (routeInfo == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(routeInfo.getName());
        sb.append('/');
        sb.append(routeInfo.getDescription());
        sb.append('@');
        sb.append(routeInfo.getDeviceAddress());
        sb.append(",status=");
        sb.append(routeInfo.getStatus());
        if (routeInfo.isDefault()) {
            sb.append(",default");
        }
        if (routeInfo.isEnabled()) {
            sb.append(",enabled");
        }
        if (routeInfo.isConnecting()) {
            sb.append(",connecting");
        }
        if (routeInfo.isSelected()) {
            sb.append(",selected");
        }
        sb.append(",id=");
        sb.append(routeInfo.getTag());
        return sb.toString();
    }
}
