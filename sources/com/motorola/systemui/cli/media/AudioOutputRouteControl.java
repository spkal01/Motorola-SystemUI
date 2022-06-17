package com.motorola.systemui.cli.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioOutputRouteControl {
    /* access modifiers changed from: private */
    public static final String TAG = "AudioOutputRouteControl";
    private List<MediaDevice> mAudioDevices = new ArrayList();
    private final Object mAudioDevicesLock = new Object();
    private AudioManager mAudioManager;
    private List<Callback> mCallBack = new ArrayList();
    private final BroadcastReceiver mCallReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
            if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
                if (intExtra == 0 || intExtra == 6) {
                    AudioOutputRouteControl.this.refreshDevices();
                }
            } else if ("android.intent.action.HEADSET_PLUG".equals(intent.getAction())) {
                AudioOutputRouteControl.this.refreshDevices();
            }
        }
    };
    private int mConnectDeviceIndex = -1;
    private Context mContext;
    private int mCurrentMediaRouteIconId = -1;
    private String mCurrentMediaRouteName = "";
    private final Executor mExecutor;
    private boolean mIsNeedSplitHdmiAndPhoneSpeaker = false;
    private final RouterManagerCallback mMediaRouterCallback;
    /* access modifiers changed from: private */
    public final String mPackageName;
    private MediaRouter2Manager mRouterManager;
    private List<MediaRoute2Info> routes;

    public static class Callback {
        public void onMediaDevicesUpdate(List<MediaDevice> list, int i) {
        }
    }

    AudioOutputRouteControl(Context context) {
        RouterManagerCallback routerManagerCallback = new RouterManagerCallback();
        this.mMediaRouterCallback = routerManagerCallback;
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        this.mExecutor = newSingleThreadExecutor;
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        MediaRouter2Manager instance = MediaRouter2Manager.getInstance(context);
        this.mRouterManager = instance;
        instance.registerCallback(newSingleThreadExecutor, routerManagerCallback);
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
    }

    public void startScan() {
        refreshDevices();
    }

    public void release() {
        this.mRouterManager.unregisterCallback(this.mMediaRouterCallback);
    }

    public List<MediaDevice> getMediaDevices() {
        return this.mAudioDevices;
    }

    public int getConnectDeviceIndex() {
        return this.mConnectDeviceIndex;
    }

    /* access modifiers changed from: private */
    public void refreshDevices() {
        refreshDevicesForMedia();
    }

    private void refreshDevicesForMedia() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        this.mIsNeedSplitHdmiAndPhoneSpeaker = false;
        List<MediaRoute2Info> availableRoutes = this.mRouterManager.getAvailableRoutes(this.mPackageName);
        this.routes = availableRoutes;
        for (MediaRoute2Info next : availableRoutes) {
            String str = TAG;
            Log.d(str, "route = " + next.toString());
            if (next.getType() != 9) {
                arrayList.add(new MediaDevice(this.mContext, this.mRouterManager, next, this.mPackageName, this.mAudioManager));
            } else if (this.mAudioManager.isWiredHeadsetOn()) {
                arrayList.add(createWiredHeadsetMediaDevice(next));
            } else {
                arrayList.add(new MediaDevice(this.mContext, this.mRouterManager, next, this.mPackageName, this.mAudioManager));
                this.mIsNeedSplitHdmiAndPhoneSpeaker = true;
                i = arrayList.size() - 1;
            }
        }
        if (arrayList.size() == 0) {
            this.mConnectDeviceIndex = -1;
            return;
        }
        if (this.mIsNeedSplitHdmiAndPhoneSpeaker) {
            arrayList.add(createPhoneMediaDevice((MediaDevice) arrayList.get(i)));
        }
        synchronized (this.mAudioDevicesLock) {
            this.mConnectDeviceIndex = getConnectDeviceIndex(arrayList);
            this.mAudioDevices.clear();
            this.mAudioDevices.addAll(arrayList);
            this.mCurrentMediaRouteName = ((MediaDevice) arrayList.get(this.mConnectDeviceIndex)).getName();
            this.mCurrentMediaRouteIconId = ((MediaDevice) arrayList.get(this.mConnectDeviceIndex)).getIconId();
            String str2 = TAG;
            Log.d(str2, "mConnectDeviceIndex = " + this.mConnectDeviceIndex + " mCurrentMediaRouteName = " + this.mCurrentMediaRouteName + " mCurrentMediaRouteIconId = " + this.mCurrentMediaRouteIconId);
            for (Callback onMediaDevicesUpdate : this.mCallBack) {
                onMediaDevicesUpdate.onMediaDevicesUpdate(this.mAudioDevices, this.mConnectDeviceIndex);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0019, code lost:
        if (r4.mIsNeedSplitHdmiAndPhoneSpeaker == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0025, code lost:
        if (r1.getRouteInfo().getType() != 9) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0027, code lost:
        setProperty("persist.desktop.allow_hdmi_media", "true");
        r0 = r4.mAudioManager;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0030, code lost:
        if (r0 == null) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
        if (r0.isMusicActive() == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        if (checkIfNeedUpdateMediaRoute(r1.getRouteInfo().getType()) == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0046, code lost:
        updateMediaRoute();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0053, code lost:
        if (r1.getRouteInfo().getType() != 2) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0055, code lost:
        setProperty("persist.desktop.allow_hdmi_media", "false");
        r0 = r4.mAudioManager;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005e, code lost:
        if (r0 == null) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0064, code lost:
        if (r0.isMusicActive() == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0072, code lost:
        if (checkIfNeedUpdateMediaRoute(r1.getRouteInfo().getType()) == false) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0074, code lost:
        updateMediaRoute();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007b, code lost:
        if (r1.connect() == false) goto L_0x0081;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007d, code lost:
        r4.mConnectDeviceIndex = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0080, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0081, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean connectNewMediaDeviceByPositionForMedia(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mAudioDevicesLock
            monitor-enter(r0)
            java.util.List<com.motorola.systemui.cli.media.MediaDevice> r1 = r4.mAudioDevices     // Catch:{ all -> 0x0082 }
            int r1 = r1.size()     // Catch:{ all -> 0x0082 }
            r2 = 0
            if (r5 < r1) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            return r2
        L_0x000e:
            java.util.List<com.motorola.systemui.cli.media.MediaDevice> r1 = r4.mAudioDevices     // Catch:{ all -> 0x0082 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0082 }
            com.motorola.systemui.cli.media.MediaDevice r1 = (com.motorola.systemui.cli.media.MediaDevice) r1     // Catch:{ all -> 0x0082 }
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            boolean r0 = r4.mIsNeedSplitHdmiAndPhoneSpeaker
            if (r0 == 0) goto L_0x0077
            android.media.MediaRoute2Info r0 = r1.getRouteInfo()
            int r0 = r0.getType()
            r3 = 9
            if (r0 != r3) goto L_0x004a
            java.lang.String r0 = "persist.desktop.allow_hdmi_media"
            java.lang.String r3 = "true"
            setProperty(r0, r3)
            android.media.AudioManager r0 = r4.mAudioManager
            if (r0 == 0) goto L_0x0077
            boolean r0 = r0.isMusicActive()
            if (r0 == 0) goto L_0x0077
            android.media.MediaRoute2Info r0 = r1.getRouteInfo()
            int r0 = r0.getType()
            boolean r0 = r4.checkIfNeedUpdateMediaRoute(r0)
            if (r0 == 0) goto L_0x0077
            r4.updateMediaRoute()
            goto L_0x0077
        L_0x004a:
            android.media.MediaRoute2Info r0 = r1.getRouteInfo()
            int r0 = r0.getType()
            r3 = 2
            if (r0 != r3) goto L_0x0077
            java.lang.String r0 = "persist.desktop.allow_hdmi_media"
            java.lang.String r3 = "false"
            setProperty(r0, r3)
            android.media.AudioManager r0 = r4.mAudioManager
            if (r0 == 0) goto L_0x0077
            boolean r0 = r0.isMusicActive()
            if (r0 == 0) goto L_0x0077
            android.media.MediaRoute2Info r0 = r1.getRouteInfo()
            int r0 = r0.getType()
            boolean r0 = r4.checkIfNeedUpdateMediaRoute(r0)
            if (r0 == 0) goto L_0x0077
            r4.updateMediaRoute()
        L_0x0077:
            boolean r0 = r1.connect()
            if (r0 == 0) goto L_0x0081
            r4.mConnectDeviceIndex = r5
            r4 = 1
            return r4
        L_0x0081:
            return r2
        L_0x0082:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0082 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.AudioOutputRouteControl.connectNewMediaDeviceByPositionForMedia(int):boolean");
    }

    private MediaDevice createPhoneMediaDevice(MediaDevice mediaDevice) {
        MediaRoute2Info build = new MediaRoute2Info.Builder("DEVICE_ROUTE", "Phone").addFeature("android.media.route.feature.LIVE_AUDIO, android.media.route.feature.LIVE_VIDEO, android.media.route.feature.LOCAL_PLAYBACK").setProviderId(mediaDevice.getRouteInfo().getProviderId()).setVolume(mediaDevice.getRouteInfo().getVolume()).setVolumeMax(mediaDevice.getRouteInfo().getVolumeMax()).setClientPackageName(mediaDevice.getRouteInfo().getClientPackageName()).setType(2).setVolumeHandling(1).setConnectionState(2).build();
        String str = TAG;
        Log.d(str, "phoneSpeakerDeviceRoute = " + build);
        return new MediaDevice(this.mContext, this.mRouterManager, build, this.mPackageName, this.mAudioManager);
    }

    private MediaDevice createWiredHeadsetMediaDevice(MediaRoute2Info mediaRoute2Info) {
        MediaRoute2Info build = new MediaRoute2Info.Builder("DEVICE_ROUTE", "Headset").addFeature("android.media.route.feature.LIVE_AUDIO, android.media.route.feature.LIVE_VIDEO, android.media.route.feature.LOCAL_PLAYBACK").setProviderId(mediaRoute2Info.getProviderId()).setVolume(mediaRoute2Info.getVolume()).setVolumeMax(mediaRoute2Info.getVolumeMax()).setClientPackageName(mediaRoute2Info.getClientPackageName()).setType(22).setVolumeHandling(1).setConnectionState(2).build();
        String str = TAG;
        Log.d(str, "wiredHeadsetDeviceRoute = " + build);
        return new MediaDevice(this.mContext, this.mRouterManager, build, this.mPackageName, this.mAudioManager);
    }

    public int getConnectDeviceIndex(List<MediaDevice> list) {
        List selectedRoutes = getRoutingSessionInfo().getSelectedRoutes();
        String str = (String) selectedRoutes.get(selectedRoutes.size() - 1);
        boolean booleanProperty = getBooleanProperty("persist.desktop.allow_hdmi_media", true);
        for (MediaDevice next : list) {
            if (next.getRouteInfo().getId().equals(str)) {
                if (!this.mIsNeedSplitHdmiAndPhoneSpeaker) {
                    return list.indexOf(next);
                }
                if (!next.isPhoneDevice() || ((!booleanProperty || next.isHdmiDevice()) && (booleanProperty || !next.isHdmiDevice()))) {
                    return list.indexOf(next);
                }
            }
        }
        return 0;
    }

    private RoutingSessionInfo getRoutingSessionInfo() {
        List routingSessions = this.mRouterManager.getRoutingSessions(this.mPackageName);
        return (RoutingSessionInfo) routingSessions.get(routingSessions.size() - 1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        if (r5 != 9) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0028, code lost:
        if (r4 != 2) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002a, code lost:
        if (r5 != 2) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        if (r4 == 9) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002e, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkIfNeedUpdateMediaRoute(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mAudioDevicesLock
            monitor-enter(r0)
            int r1 = r4.mConnectDeviceIndex     // Catch:{ all -> 0x0031 }
            java.util.List<com.motorola.systemui.cli.media.MediaDevice> r2 = r4.mAudioDevices     // Catch:{ all -> 0x0031 }
            int r2 = r2.size()     // Catch:{ all -> 0x0031 }
            r3 = 0
            if (r1 < r2) goto L_0x0010
            monitor-exit(r0)     // Catch:{ all -> 0x0031 }
            return r3
        L_0x0010:
            java.util.List<com.motorola.systemui.cli.media.MediaDevice> r1 = r4.mAudioDevices     // Catch:{ all -> 0x0031 }
            int r4 = r4.mConnectDeviceIndex     // Catch:{ all -> 0x0031 }
            java.lang.Object r4 = r1.get(r4)     // Catch:{ all -> 0x0031 }
            com.motorola.systemui.cli.media.MediaDevice r4 = (com.motorola.systemui.cli.media.MediaDevice) r4     // Catch:{ all -> 0x0031 }
            android.media.MediaRoute2Info r4 = r4.getRouteInfo()     // Catch:{ all -> 0x0031 }
            int r4 = r4.getType()     // Catch:{ all -> 0x0031 }
            monitor-exit(r0)     // Catch:{ all -> 0x0031 }
            r0 = 2
            r1 = 9
            if (r5 != r1) goto L_0x002a
            if (r4 != r0) goto L_0x002e
        L_0x002a:
            if (r5 != r0) goto L_0x002f
            if (r4 == r1) goto L_0x002f
        L_0x002e:
            return r3
        L_0x002f:
            r4 = 1
            return r4
        L_0x0031:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0031 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.motorola.systemui.cli.media.AudioOutputRouteControl.checkIfNeedUpdateMediaRoute(int):boolean");
    }

    private void updateMediaRoute() {
        new Thread(new Runnable() {
            public void run() {
                int forceUse = AudioSystem.getForceUse(1);
                String access$000 = AudioOutputRouteControl.TAG;
                Log.d(access$000, "updateMediaRoute force = " + forceUse);
                if (forceUse == 0) {
                    AudioSystem.setForceUse(1, 1);
                } else {
                    AudioSystem.setForceUse(1, 0);
                }
                AudioSystem.setForceUse(1, forceUse);
            }
        }).start();
    }

    class RouterManagerCallback implements MediaRouter2Manager.Callback {
        RouterManagerCallback() {
        }

        public void onRoutesAdded(List<MediaRoute2Info> list) {
            String access$000 = AudioOutputRouteControl.TAG;
            Log.d(access$000, "MediaRouter2Manager.Callback onRoutesAdded routes = " + list.toString());
            AudioOutputRouteControl.this.refreshDevices();
        }

        public void onPreferredFeaturesChanged(String str, List<String> list) {
            Log.d(AudioOutputRouteControl.TAG, "MediaRouter2Manager.Callback onPreferredFeaturesChanged");
            if (TextUtils.equals(AudioOutputRouteControl.this.mPackageName, str)) {
                AudioOutputRouteControl.this.refreshDevices();
            }
        }

        public void onRoutesChanged(List<MediaRoute2Info> list) {
            String access$000 = AudioOutputRouteControl.TAG;
            Log.d(access$000, "MediaRouter2Manager.Callback onRoutesChanged routes = " + list);
            AudioOutputRouteControl.this.refreshDevices();
        }

        public void onRoutesRemoved(List<MediaRoute2Info> list) {
            String access$000 = AudioOutputRouteControl.TAG;
            Log.d(access$000, "MediaRouter2Manager.Callback onRoutesRemoved routes = " + list);
            AudioOutputRouteControl.this.refreshDevices();
        }

        public void onTransferred(RoutingSessionInfo routingSessionInfo, RoutingSessionInfo routingSessionInfo2) {
            Log.d(AudioOutputRouteControl.TAG, "MediaRouter2Manager.Callback onTransferred");
            AudioOutputRouteControl.this.refreshDevices();
        }

        public void onTransferFailed(RoutingSessionInfo routingSessionInfo, MediaRoute2Info mediaRoute2Info) {
            Log.d(AudioOutputRouteControl.TAG, "MediaRouter2Manager.Callback onTransferFailed");
            AudioOutputRouteControl.this.refreshDevices();
        }

        public void onRequestFailed(int i) {
            Log.d(AudioOutputRouteControl.TAG, "MediaRouter2Manager.Callback onRequestFailed");
            AudioOutputRouteControl.this.refreshDevices();
        }

        public void onSessionUpdated(RoutingSessionInfo routingSessionInfo) {
            Log.d(AudioOutputRouteControl.TAG, "MediaRouter2Manager.Callback onSessionUpdated");
        }
    }

    public void registerCallback(Callback callback) {
        Objects.requireNonNull(callback, "callback must not be null");
        this.mCallBack.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        Objects.requireNonNull(callback, "callback must not be null");
        this.mCallBack.remove(callback);
    }

    public static void setProperty(String str, String str2) {
        Class<String> cls = String.class;
        try {
            Class<?> cls2 = Class.forName("android.os.SystemProperties");
            cls2.getMethod("set", new Class[]{cls, cls}).invoke(cls2, new Object[]{str, str2});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getBooleanProperty(String str, boolean z) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return ((Boolean) cls.getMethod("getBoolean", new Class[]{String.class, Boolean.TYPE}).invoke(cls, new Object[]{str, Boolean.valueOf(z)})).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return z;
        }
    }
}
