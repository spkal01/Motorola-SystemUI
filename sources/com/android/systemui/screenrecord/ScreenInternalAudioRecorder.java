package com.android.systemui.screenrecord;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenInternalAudioRecorder {
    /* access modifiers changed from: private */
    public static String TAG = "Recording_AudioRecorder";
    private float INTERNAL_VOLUME_SCALE_MAX = 0.35f;
    private final AudioManager mAudio;
    /* access modifiers changed from: private */
    public AudioRecord mAudioRecord;
    private AudioRecord mAudioRecordMic;
    private MediaCodec mCodec;
    private Config mConfig = new Config();
    private final Context mContext;
    private final BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (ScreenInternalAudioRecorder.this.mMic) {
                String action = intent.getAction();
                if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                    int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
                    if (intExtra == 2) {
                        Log.d(ScreenInternalAudioRecorder.TAG, "Bluetooth connected");
                        ScreenInternalAudioRecorder.this.changeMic();
                    } else if (intExtra == 0) {
                        Log.d(ScreenInternalAudioRecorder.TAG, "Bluetooth not connected");
                        ScreenInternalAudioRecorder.this.changeMic();
                    }
                } else if (action.equals("android.intent.action.HEADSET_PLUG")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        Log.d(ScreenInternalAudioRecorder.TAG, "headset not connected");
                        ScreenInternalAudioRecorder.this.changeMic();
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        Log.d(ScreenInternalAudioRecorder.TAG, "headset connected");
                        ScreenInternalAudioRecorder.this.changeMic();
                    }
                } else if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
                    ScreenInternalAudioRecorder.this.setInteralVolumeRate();
                }
            }
        }
    };
    private float mInternalVolumeScale = 0.35f;
    /* access modifiers changed from: private */
    public boolean mIsCalling = false;
    private boolean mIsHeadsetOn = false;
    private ScreenRecordingAudioSource mLastAudioSource = ScreenRecordingAudioSource.NONE;
    private Object mLockMic = new Object();
    private Object mLockRun = new Object();
    private MediaProjection mMediaProjection;
    /* access modifiers changed from: private */
    public boolean mMic = true;
    private int mMicSource = 1;
    private MediaMuxer mMuxer;
    private boolean mPaused = false;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        private int mCallState;

        public void onCallStateChanged(int i, String str) {
            if (this.mCallState != i && ScreenInternalAudioRecorder.this.mAudioRecord.getRecordingState() == 3) {
                if (i == 2) {
                    String access$100 = ScreenInternalAudioRecorder.TAG;
                    Log.d(access$100, "CALL_STATE_OFFHOOK change mIsCalling=" + ScreenInternalAudioRecorder.this.mIsCalling + " as true!");
                    boolean unused = ScreenInternalAudioRecorder.this.mIsCalling = true;
                } else if (this.mCallState == 2) {
                    String access$1002 = ScreenInternalAudioRecorder.TAG;
                    Log.d(access$1002, "CALL_STATE_OFFHOOK change mIsCalling=" + ScreenInternalAudioRecorder.this.mIsCalling + " as false!");
                    boolean unused2 = ScreenInternalAudioRecorder.this.mIsCalling = false;
                }
                this.mCallState = i;
            }
        }
    };
    private long mPresentationTime;
    private int mSize;
    private boolean mStarted;
    private Thread mThread;
    private long mTotalBytes;
    private int mTrackId = -1;

    public ScreenInternalAudioRecorder(String str, Context context, MediaProjection mediaProjection, ScreenRecordingAudioSource screenRecordingAudioSource) throws IOException {
        this.mLastAudioSource = screenRecordingAudioSource;
        this.mMicSource = getMicSource(screenRecordingAudioSource);
        this.mMuxer = new MediaMuxer(str, 0);
        this.mContext = context;
        this.mMediaProjection = mediaProjection;
        this.mAudio = (AudioManager) context.getSystemService("audio");
        String str2 = TAG;
        Log.d(str2, "creating audio file " + str);
        setupSimple();
    }

    public static class Config {
        public int bitRate = 196000;
        public int bufferSizeBytes = 131072;
        public int channelInMask = 16;
        public int channelOutMask = 4;
        public int encoding = 2;
        public boolean legacy_app_looback = false;
        public boolean privileged = true;
        public int sampleRate = 44100;

        public String toString() {
            return "channelMask=" + this.channelOutMask + "\n   encoding=" + this.encoding + "\n sampleRate=" + this.sampleRate + "\n bufferSize=" + this.bufferSizeBytes + "\n privileged=" + this.privileged + "\n legacy app looback=" + this.legacy_app_looback;
        }
    }

    private void setupSimple() throws IOException {
        Config config = this.mConfig;
        int minBufferSize = AudioRecord.getMinBufferSize(config.sampleRate, config.channelInMask, config.encoding) * 2;
        this.mSize = minBufferSize;
        String str = TAG;
        Log.d(str, "audio buffer size: " + minBufferSize + ";mLastAudioSource=" + this.mLastAudioSource);
        AudioFormat build = new AudioFormat.Builder().setEncoding(this.mConfig.encoding).setSampleRate(this.mConfig.sampleRate).setChannelMask(this.mConfig.channelOutMask).build();
        this.mAudioRecord = new AudioRecord.Builder().setAudioFormat(build).setAudioPlaybackCaptureConfig(new AudioPlaybackCaptureConfiguration.Builder(this.mMediaProjection).addMatchingUsage(1).addMatchingUsage(0).addMatchingUsage(14).build()).build();
        String str2 = TAG;
        Log.d(str2, "new mAudioRecordMic mMicSource=" + this.mMicSource);
        if (this.mMic && this.mMicSource != 0) {
            int i = this.mMicSource;
            Config config2 = this.mConfig;
            this.mAudioRecordMic = new AudioRecord(i, config2.sampleRate, 16, config2.encoding, minBufferSize);
            changeMic();
        }
        this.mCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", this.mConfig.sampleRate, 1);
        createAudioFormat.setInteger("aac-profile", 2);
        createAudioFormat.setInteger("bitrate", this.mConfig.bitRate);
        createAudioFormat.setInteger("pcm-encoding", this.mConfig.encoding);
        this.mCodec.configure(createAudioFormat, (Surface) null, (MediaCrypto) null, 1);
        this.mThread = new Thread(new ScreenInternalAudioRecorder$$ExternalSyntheticLambda0(this, minBufferSize));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setupSimple$0(int i) {
        byte[] bArr;
        short[] sArr;
        int i2;
        int i3;
        boolean z;
        short[] sArr2 = null;
        if (this.mMic) {
            int i4 = i / 2;
            bArr = new byte[i];
            sArr2 = new short[i4];
            sArr = new short[i4];
        } else {
            bArr = new byte[i];
            sArr = null;
        }
        while (true) {
            if (this.mPaused) {
                synchronized (this.mLockRun) {
                    try {
                        this.mLockRun.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            boolean z2 = true;
            int i5 = 0;
            if (this.mMic) {
                if (this.mAudioRecord.getState() != 1) {
                    break;
                }
                synchronized (this.mLockMic) {
                    AudioRecord audioRecord = this.mAudioRecordMic;
                    if (audioRecord == null) {
                        i2 = 0;
                        z = false;
                    } else {
                        i2 = audioRecord.read(sArr2, 0, sArr2.length);
                        if (this.mLastAudioSource == ScreenRecordingAudioSource.MIC_AND_INTERNAL) {
                            sArr2 = scaleValues(sArr2, i2, this.mIsHeadsetOn ? 1.5f : 1.2f);
                        }
                        z = true;
                    }
                }
                if (this.mAudio.getMode() != 3) {
                    z2 = false;
                }
                if (z) {
                    int read = this.mAudioRecord.read(sArr, 0, sArr.length);
                    ScreenRecordingAudioSource screenRecordingAudioSource = this.mLastAudioSource;
                    if (screenRecordingAudioSource == ScreenRecordingAudioSource.MIC || screenRecordingAudioSource == ScreenRecordingAudioSource.NONE || z2) {
                        for (int i6 = 0; i6 < sArr.length; i6++) {
                            sArr[i6] = 0;
                        }
                    } else {
                        sArr = scaleValues(sArr, read, this.mInternalVolumeScale);
                    }
                    ScreenRecordingAudioSource screenRecordingAudioSource2 = this.mLastAudioSource;
                    if (screenRecordingAudioSource2 == ScreenRecordingAudioSource.INTERNAL || screenRecordingAudioSource2 == ScreenRecordingAudioSource.NONE || this.mIsCalling || z2) {
                        for (int i7 = 0; i7 < sArr2.length; i7++) {
                            sArr2[i7] = 0;
                        }
                    }
                    i3 = Math.min(read, i2) * 2;
                    i5 = read;
                    bArr = addAndConvertBuffers(sArr, read, sArr2, i2);
                } else {
                    i3 = this.mAudioRecord.read(bArr, 0, bArr.length);
                    ScreenRecordingAudioSource screenRecordingAudioSource3 = this.mLastAudioSource;
                    if (screenRecordingAudioSource3 == ScreenRecordingAudioSource.MIC || screenRecordingAudioSource3 == ScreenRecordingAudioSource.NONE || z2) {
                        for (int i8 = 0; i8 < bArr.length; i8++) {
                            bArr[i8] = 0;
                        }
                    }
                }
            } else if (this.mAudioRecord.getState() != 1) {
                break;
            } else {
                i3 = this.mAudioRecord.read(bArr, 0, bArr.length);
                i2 = 0;
            }
            if (i3 < 0) {
                Log.e(TAG, "read error " + i3 + ", shorts internal: " + i5 + ", shorts mic: " + i2);
                break;
            }
            encode(bArr, i3);
        }
        endStream();
    }

    public void pause() {
        this.mPaused = true;
    }

    public void resume() {
        this.mPaused = false;
        synchronized (this.mLockRun) {
            this.mLockRun.notify();
        }
    }

    private short[] scaleValues(short[] sArr, int i, float f) {
        for (int i2 = 0; i2 < i; i2++) {
            short s = sArr[i2];
            int i3 = (int) (((float) sArr[i2]) * f);
            if (i3 > 32767) {
                i3 = 32767;
            } else if (i3 < -32768) {
                i3 = -32768;
            }
            sArr[i2] = (short) i3;
        }
        return sArr;
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [short[]] */
    /* JADX WARNING: type inference failed for: r8v0, types: [short[]] */
    /* JADX WARNING: type inference failed for: r2v8, types: [short, int] */
    /* JADX WARNING: type inference failed for: r3v4, types: [short, int] */
    /* JADX WARNING: type inference failed for: r2v10, types: [short] */
    /* JADX WARNING: type inference failed for: r2v11, types: [short] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=short, code=int, for r2v10, types: [short] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=short, code=int, for r2v11, types: [short] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] addAndConvertBuffers(short[] r6, int r7, short[] r8, int r9) {
        /*
            r5 = this;
            int r5 = java.lang.Math.max(r7, r9)
            r0 = 0
            if (r5 >= 0) goto L_0x000a
            byte[] r5 = new byte[r0]
            return r5
        L_0x000a:
            int r1 = r5 * 2
            byte[] r1 = new byte[r1]
        L_0x000e:
            if (r0 >= r5) goto L_0x003c
            if (r0 <= r7) goto L_0x0015
            short r2 = r8[r0]
            goto L_0x001f
        L_0x0015:
            if (r0 <= r9) goto L_0x001a
            short r2 = r6[r0]
            goto L_0x001f
        L_0x001a:
            short r2 = r6[r0]
            short r3 = r8[r0]
            int r2 = r2 + r3
        L_0x001f:
            r3 = 32767(0x7fff, float:4.5916E-41)
            if (r2 <= r3) goto L_0x0024
            r2 = r3
        L_0x0024:
            r3 = -32768(0xffffffffffff8000, float:NaN)
            if (r2 >= r3) goto L_0x0029
            r2 = r3
        L_0x0029:
            int r3 = r0 * 2
            r4 = r2 & 255(0xff, float:3.57E-43)
            byte r4 = (byte) r4
            r1[r3] = r4
            int r3 = r3 + 1
            int r2 = r2 >> 8
            r2 = r2 & 255(0xff, float:3.57E-43)
            byte r2 = (byte) r2
            r1[r3] = r2
            int r0 = r0 + 1
            goto L_0x000e
        L_0x003c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenrecord.ScreenInternalAudioRecorder.addAndConvertBuffers(short[], int, short[], int):byte[]");
    }

    private void encode(byte[] bArr, int i) {
        int i2 = 0;
        while (i > 0) {
            int dequeueInputBuffer = this.mCodec.dequeueInputBuffer(500);
            if (dequeueInputBuffer < 0) {
                writeOutput();
                return;
            }
            ByteBuffer inputBuffer = this.mCodec.getInputBuffer(dequeueInputBuffer);
            inputBuffer.clear();
            int capacity = inputBuffer.capacity();
            int i3 = i > capacity ? capacity : i;
            i -= i3;
            inputBuffer.put(bArr, i2, i3);
            i2 += i3;
            this.mCodec.queueInputBuffer(dequeueInputBuffer, 0, i3, this.mPresentationTime, 0);
            long j = this.mTotalBytes + ((long) (i3 + 0));
            this.mTotalBytes = j;
            this.mPresentationTime = ((j / 2) * 1000000) / ((long) this.mConfig.sampleRate);
            writeOutput();
        }
    }

    private void endStream() {
        this.mCodec.queueInputBuffer(this.mCodec.dequeueInputBuffer(500), 0, 0, this.mPresentationTime, 4);
        writeOutput();
    }

    private void writeOutput() {
        while (true) {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int dequeueOutputBuffer = this.mCodec.dequeueOutputBuffer(bufferInfo, 500);
            if (dequeueOutputBuffer == -2) {
                this.mTrackId = this.mMuxer.addTrack(this.mCodec.getOutputFormat());
                this.mMuxer.start();
            } else if (dequeueOutputBuffer != -1 && this.mTrackId >= 0) {
                ByteBuffer outputBuffer = this.mCodec.getOutputBuffer(dequeueOutputBuffer);
                if ((bufferInfo.flags & 2) == 0 || bufferInfo.size == 0) {
                    this.mMuxer.writeSampleData(this.mTrackId, outputBuffer, bufferInfo);
                }
                this.mCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
            } else {
                return;
            }
        }
    }

    public synchronized void start() throws IllegalStateException {
        AudioRecord audioRecord;
        if (!this.mStarted) {
            this.mStarted = true;
            listenForCallState(true);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            this.mContext.registerReceiver(this.mHeadsetPlugReceiver, intentFilter);
            setInteralVolumeRate();
            ScreenRecordingAudioSource screenRecordingAudioSource = this.mLastAudioSource;
            if (screenRecordingAudioSource == ScreenRecordingAudioSource.MIC_AND_INTERNAL || screenRecordingAudioSource == ScreenRecordingAudioSource.MIC) {
                RecordingUtils.updateAudioParameter(this.mContext, true);
            }
            this.mAudioRecord.startRecording();
            if (this.mMic && (audioRecord = this.mAudioRecordMic) != null) {
                audioRecord.startRecording();
            }
            String str = TAG;
            Log.d(str, "channel count " + this.mAudioRecord.getChannelCount());
            this.mCodec.start();
            if (this.mAudioRecord.getRecordingState() == 3) {
                this.mThread.start();
            } else {
                throw new IllegalStateException("Audio recording failed to start");
            }
        } else if (this.mThread == null) {
            throw new IllegalStateException("Recording stopped and can't restart (single use)");
        } else {
            throw new IllegalStateException("Recording already started");
        }
    }

    public void end() {
        AudioRecord audioRecord;
        AudioRecord audioRecord2;
        listenForCallState(false);
        this.mContext.unregisterReceiver(this.mHeadsetPlugReceiver);
        RecordingUtils.updateAudioParameter(this.mContext, false);
        this.mAudioRecord.stop();
        if (this.mMic && (audioRecord2 = this.mAudioRecordMic) != null) {
            audioRecord2.stop();
        }
        this.mAudioRecord.release();
        if (this.mMic && (audioRecord = this.mAudioRecordMic) != null) {
            audioRecord.release();
        }
        if (this.mPaused) {
            synchronized (this.mLockRun) {
                this.mLockRun.notify();
            }
        }
        try {
            this.mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mCodec.stop();
        this.mCodec.release();
        this.mMuxer.stop();
        this.mMuxer.release();
        this.mThread = null;
    }

    public synchronized void setAudioResource(ScreenRecordingAudioSource screenRecordingAudioSource) {
        synchronized (this.mLockMic) {
            if (this.mLastAudioSource != screenRecordingAudioSource) {
                updateMic(screenRecordingAudioSource);
                this.mLastAudioSource = screenRecordingAudioSource;
                if (screenRecordingAudioSource != ScreenRecordingAudioSource.MIC_AND_INTERNAL) {
                    if (screenRecordingAudioSource != ScreenRecordingAudioSource.MIC) {
                        RecordingUtils.updateAudioParameter(this.mContext, false);
                    }
                }
                RecordingUtils.updateAudioParameter(this.mContext, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setInteralVolumeRate() {
        float streamVolume = ((float) this.mAudio.getStreamVolume(3)) / ((float) this.mAudio.getStreamMaxVolume(3));
        this.mInternalVolumeScale = streamVolume;
        float f = this.INTERNAL_VOLUME_SCALE_MAX;
        if (streamVolume > f) {
            this.mInternalVolumeScale = f;
        }
        String str = TAG;
        Log.d(str, "mInternalVolumeScale " + this.mInternalVolumeScale);
    }

    private int getMicSource(ScreenRecordingAudioSource screenRecordingAudioSource) {
        if (screenRecordingAudioSource == ScreenRecordingAudioSource.MIC || screenRecordingAudioSource == ScreenRecordingAudioSource.MIC_AND_INTERNAL) {
            return 1;
        }
        return 0;
    }

    private void updateMic(ScreenRecordingAudioSource screenRecordingAudioSource) {
        int i = this.mMicSource;
        int micSource = getMicSource(screenRecordingAudioSource);
        this.mMicSource = micSource;
        if (i != micSource) {
            String str = TAG;
            Log.d(str, "updateMic source=" + this.mMicSource + ";oldMicSource=" + i);
            AudioRecord audioRecord = this.mAudioRecordMic;
            if (audioRecord != null) {
                audioRecord.stop();
                this.mAudioRecordMic.release();
                this.mAudioRecordMic = null;
            }
            if (this.mMicSource != 0) {
                int i2 = this.mMicSource;
                Config config = this.mConfig;
                this.mAudioRecordMic = new AudioRecord(i2, config.sampleRate, 16, config.encoding, this.mSize);
                changeMic();
                this.mAudioRecordMic.startRecording();
            }
        }
    }

    private void listenForCallState(boolean z) {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        if (telephonyManager == null) {
            return;
        }
        if (z) {
            telephonyManager.listen(this.mPhoneStateListener, 32);
        } else {
            telephonyManager.listen(this.mPhoneStateListener, 0);
        }
    }

    /* access modifiers changed from: private */
    public void changeMic() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean z = false;
        int profileConnectionState = defaultAdapter != null ? defaultAdapter.getProfileConnectionState(1) : 0;
        if (profileConnectionState == 2 || this.mAudio.isWiredHeadsetOn()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("recording with headset=");
            if (profileConnectionState == 2) {
                z = true;
            }
            sb.append(z);
            sb.append(":");
            sb.append(this.mAudio.isWiredHeadsetOn());
            Log.d(str, sb.toString());
            this.mIsHeadsetOn = true;
            return;
        }
        this.mIsHeadsetOn = false;
    }
}
