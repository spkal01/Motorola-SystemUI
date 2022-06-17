package com.android.systemui.screenrecord;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;
import com.motorola.android.provider.MotorolaSettings;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ScreenMediaRecorder {
    private static final ArrayList<String> EXCEPTED_CODEC = new ArrayList<>(Arrays.asList(new String[]{"c2.android", "omx.google"}));
    /* access modifiers changed from: private */
    public ScreenInternalAudioRecorder mAudio;
    /* access modifiers changed from: private */
    public ScreenRecordingAudioSource mAudioSource;
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z) {
            ScreenRecordingAudioSource screenRecordingAudioSource = ScreenRecordingAudioSource.values()[RecordingSettings.getAudioResource(ScreenMediaRecorder.this.mContext)];
            if (ScreenMediaRecorder.this.mAudioSource != screenRecordingAudioSource) {
                ScreenRecordingAudioSource unused = ScreenMediaRecorder.this.mAudioSource = screenRecordingAudioSource;
                Log.d("Recording_MediaRecorder", "change mAudioSource=" + ScreenMediaRecorder.this.mAudioSource);
                if (ScreenMediaRecorder.this.mAudio != null) {
                    ScreenMediaRecorder.this.mAudio.setAudioResource(ScreenMediaRecorder.this.mAudioSource);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    MediaRecorder.OnErrorListener mErrorListener;
    private Surface mInputSurface;
    MediaRecorder.OnInfoListener mListener;
    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private ScreenRecordingMuxer mMuxer;
    private File mTempAudioFile;
    private File mTempVideoFile;
    private int mUser;
    private VirtualDisplay mVirtualDisplay;

    public ScreenMediaRecorder(Context context, int i, MediaRecorder.OnInfoListener onInfoListener, MediaRecorder.OnErrorListener onErrorListener) {
        this.mContext = context;
        this.mUser = i;
        this.mListener = onInfoListener;
        this.mErrorListener = onErrorListener;
        this.mAudioSource = ScreenRecordingAudioSource.values()[RecordingSettings.getAudioResource(context)];
        Log.d("Recording_MediaRecorder", "init mAudioSource=" + this.mAudioSource);
    }

    private void prepare() throws IOException, RemoteException, RuntimeException {
        this.mMediaProjection = new MediaProjection(this.mContext, IMediaProjection.Stub.asInterface(IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection")).createProjection(this.mUser, this.mContext.getPackageName(), 0, false).asBinder()));
        this.mContext.getCacheDir().mkdirs();
        File recordingDir = RecordingUtils.getRecordingDir(this.mContext);
        if (!recordingDir.exists()) {
            recordingDir.mkdirs();
        }
        this.mTempVideoFile = File.createTempFile("temp", ".mp4", recordingDir);
        MediaRecorder mediaRecorder = new MediaRecorder();
        this.mMediaRecorder = mediaRecorder;
        mediaRecorder.setOnErrorListener(this.mErrorListener);
        this.mMediaRecorder.setVideoSource(2);
        this.mMediaRecorder.setOutputFormat(2);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(displayMetrics);
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        int resolution = RecordingSettings.getResolution(this.mContext);
        StringBuilder sb = new StringBuilder();
        sb.append("init screenWidth=");
        sb.append(i);
        sb.append(";screenHeight=");
        sb.append(i2);
        sb.append(";setting solution=");
        sb.append(resolution);
        sb.append(";default rate=");
        int i3 = 60;
        sb.append(60);
        Log.d("Recording_MediaRecorder", sb.toString());
        if (i2 > i) {
            if (i > resolution) {
                i2 = (i2 * resolution) / i;
                i = resolution;
            }
        } else if (i2 > resolution) {
            i = (i * resolution) / i2;
            i2 = resolution;
        }
        if (i % 2 != 0) {
            i--;
        }
        if (i2 % 2 != 0) {
            i2--;
        }
        int[] supportedSize = getSupportedSize(i, i2, 60);
        Log.d("Recording_MediaRecorder", "support screenWidth=" + i + ";dimens[0]=" + supportedSize[0] + ";screenHeight=" + i2 + ";dimens[1]=" + supportedSize[1] + ";dimens[2]=" + supportedSize[2]);
        int i4 = supportedSize[0];
        int i5 = supportedSize[1];
        int i6 = supportedSize[2];
        int i7 = i5 * i4;
        if (i6 >= 60) {
            i3 = i6;
        }
        int i8 = ((i7 * i3) / 30) * 6;
        this.mMediaRecorder.setVideoEncoder(2);
        this.mMediaRecorder.setVideoEncodingProfileLevel(8, 256);
        this.mMediaRecorder.setVideoSize(i4, i5);
        this.mMediaRecorder.setVideoFrameRate(i6);
        this.mMediaRecorder.setVideoEncodingBitRate(i8);
        int timeLimit = RecordingSettings.getTimeLimit(this.mContext);
        if (timeLimit != 0) {
            this.mMediaRecorder.setMaxDuration(60000 * timeLimit);
        }
        int fileLimit = RecordingSettings.getFileLimit(this.mContext);
        if (fileLimit != 0) {
            this.mMediaRecorder.setMaxFileSize(((long) fileLimit) * 1048576);
        }
        Log.d("Recording_MediaRecorder", "prepare actual rate=" + i6 + ";vidBitRate=" + i8 + ";timeLimit=" + timeLimit + ";sizeLimit=" + fileLimit);
        this.mMediaRecorder.setOutputFile(this.mTempVideoFile);
        this.mMediaRecorder.prepare();
        Surface surface = this.mMediaRecorder.getSurface();
        this.mInputSurface = surface;
        this.mVirtualDisplay = this.mMediaProjection.createVirtualDisplay("Recording Display", i4, i5, displayMetrics.densityDpi, 16, surface, (VirtualDisplay.Callback) null, (Handler) null);
        this.mMediaRecorder.setOnInfoListener(this.mListener);
        this.mTempAudioFile = File.createTempFile("temp", ".aac", recordingDir);
        this.mAudio = new ScreenInternalAudioRecorder(this.mTempAudioFile.getAbsolutePath(), this.mContext, this.mMediaProjection, this.mAudioSource);
    }

    private int[] getSupportedSize(int i, int i2, int i3) {
        int i4;
        MediaCodecInfo[] mediaCodecInfoArr;
        MediaCodecInfo mediaCodecInfo;
        String[] strArr;
        int i5;
        String str;
        MediaCodecInfo.VideoCapabilities videoCapabilities;
        boolean z;
        MediaCodecInfo.CodecCapabilities capabilitiesForType;
        int i6;
        int i7;
        int i8 = i;
        int i9 = i2;
        MediaCodecInfo[] codecInfos = new MediaCodecList(0).getCodecInfos();
        int length = codecInfos.length;
        int i10 = 0;
        boolean z2 = false;
        double d = 0.0d;
        int[] iArr = null;
        MediaCodecInfo.VideoCapabilities videoCapabilities2 = null;
        int i11 = i3;
        loop0:
        while (true) {
            if (i10 >= length) {
                break;
            }
            MediaCodecInfo mediaCodecInfo2 = codecInfos[i10];
            if (!EXCEPTED_CODEC.stream().anyMatch(new ScreenMediaRecorder$$ExternalSyntheticLambda0(mediaCodecInfo2))) {
                String str2 = "video/avc";
                String[] supportedTypes = mediaCodecInfo2.getSupportedTypes();
                int length2 = supportedTypes.length;
                mediaCodecInfoArr = codecInfos;
                int i12 = 0;
                while (true) {
                    i4 = length;
                    if (i12 >= length2) {
                        MediaCodecInfo.VideoCapabilities videoCapabilities3 = videoCapabilities2;
                        boolean z3 = z2;
                        break;
                    }
                    if (!supportedTypes[i12].equalsIgnoreCase(str2) || (capabilitiesForType = mediaCodecInfo2.getCapabilitiesForType(str2)) == null || capabilitiesForType.getVideoCapabilities() == null) {
                        str = str2;
                        videoCapabilities = videoCapabilities2;
                        z = z2;
                        i5 = length2;
                        strArr = supportedTypes;
                        mediaCodecInfo = mediaCodecInfo2;
                    } else {
                        MediaCodecInfo.VideoCapabilities videoCapabilities4 = capabilitiesForType.getVideoCapabilities();
                        str = str2;
                        int intValue = videoCapabilities4.getSupportedWidths().getUpper().intValue();
                        videoCapabilities = videoCapabilities2;
                        int intValue2 = videoCapabilities4.getSupportedHeights().getUpper().intValue();
                        if (i8 % videoCapabilities4.getWidthAlignment() != 0) {
                            z = z2;
                            i6 = i8 - (i8 % videoCapabilities4.getWidthAlignment());
                        } else {
                            z = z2;
                            i6 = i8;
                        }
                        if (i9 % videoCapabilities4.getHeightAlignment() != 0) {
                            strArr = supportedTypes;
                            i5 = length2;
                            i7 = i9 - (i9 % videoCapabilities4.getHeightAlignment());
                        } else {
                            i5 = length2;
                            strArr = supportedTypes;
                            i7 = i9;
                        }
                        StringBuilder sb = new StringBuilder();
                        mediaCodecInfo = mediaCodecInfo2;
                        sb.append("Screen size supported at width=");
                        sb.append(i6);
                        sb.append(";height=");
                        sb.append(i7);
                        Log.d("Recording_MediaRecorder", sb.toString());
                        if (intValue * intValue2 >= 33177600) {
                            z = true;
                        }
                        if (iArr != null) {
                            if (z) {
                                z2 = z;
                                videoCapabilities2 = videoCapabilities;
                                break loop0;
                            }
                        } else if (intValue < i6 || intValue2 < i7 || !videoCapabilities4.isSizeSupported(i6, i7)) {
                            double d2 = (double) intValue2;
                            videoCapabilities2 = videoCapabilities4;
                            double min = Math.min(((double) intValue) / ((double) i8), d2 / ((double) i9));
                            if (min > d) {
                                d = Math.min(1.0d, min);
                                z2 = z;
                                i12++;
                                length = i4;
                                str2 = str;
                                length2 = i5;
                                supportedTypes = strArr;
                                mediaCodecInfo2 = mediaCodecInfo;
                            }
                        } else {
                            int intValue3 = videoCapabilities4.getSupportedFrameRatesFor(i6, i7).getUpper().intValue();
                            if (intValue3 < i11) {
                                i11 = intValue3;
                            }
                            Log.d("Recording_MediaRecorder", "Screen size supported at rate " + i11 + ";maxRate=" + intValue3);
                            iArr = new int[]{i6, i7, i11};
                        }
                    }
                    z2 = z;
                    videoCapabilities2 = videoCapabilities;
                    i12++;
                    length = i4;
                    str2 = str;
                    length2 = i5;
                    supportedTypes = strArr;
                    mediaCodecInfo2 = mediaCodecInfo;
                }
            } else {
                mediaCodecInfoArr = codecInfos;
                i4 = length;
            }
            i10++;
            codecInfos = mediaCodecInfoArr;
            length = i4;
        }
        int i13 = 30;
        if (iArr != null) {
            if (z2 && iArr[2] > 60) {
                iArr[2] = 60;
            }
            if (!z2 && iArr[2] > 30) {
                iArr[2] = 30;
            }
            return iArr;
        }
        if (z2 && i11 > 60) {
            i11 = 60;
        }
        if (z2 || i11 <= 30) {
            i13 = i11;
        }
        int i14 = (int) (((double) i8) * d);
        int i15 = (int) (((double) i9) * d);
        if (i14 % videoCapabilities2.getWidthAlignment() != 0) {
            i14 -= i14 % videoCapabilities2.getWidthAlignment();
        }
        if (i15 % videoCapabilities2.getHeightAlignment() != 0) {
            i15 -= i15 % videoCapabilities2.getHeightAlignment();
        }
        int intValue4 = videoCapabilities2.getSupportedFrameRatesFor(i14, i15).getUpper().intValue();
        if (intValue4 < i13) {
            i13 = intValue4;
        }
        Log.d("Recording_MediaRecorder", "Resized by " + d + ": " + i14 + ", " + i15 + ", " + i13);
        return new int[]{i14, i15, i13};
    }

    /* access modifiers changed from: package-private */
    public void start() throws IOException, RemoteException, RuntimeException {
        Log.d("Recording_MediaRecorder", "start recording");
        prepare();
        this.mMediaRecorder.start();
        recordInternalAudio();
        this.mContext.getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("record_audio_resource"), false, this.mContentObserver);
    }

    /* access modifiers changed from: package-private */
    public void end() {
        try {
            this.mMediaRecorder.stop();
        } catch (Exception e) {
            Log.d("Recording_MediaRecorder", "mediaRecorder.stop " + e);
        }
        this.mMediaRecorder.release();
        this.mInputSurface.release();
        this.mVirtualDisplay.release();
        this.mMediaProjection.stop();
        this.mMediaRecorder = null;
        this.mMediaProjection = null;
        stopInternalAudioRecording();
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        Log.d("Recording_MediaRecorder", "end recording");
    }

    public boolean pause() {
        try {
            this.mMediaRecorder.pause();
            this.mAudio.pause();
            return true;
        } catch (Exception e) {
            Log.d("Recording_MediaRecorder", "mediaRecorder.pause " + e);
            return false;
        }
    }

    public boolean resume() {
        try {
            this.mMediaRecorder.resume();
            this.mAudio.resume();
            return true;
        } catch (Exception e) {
            Log.d("Recording_MediaRecorder", "mediaRecorder.resume " + e);
            return false;
        }
    }

    private void stopInternalAudioRecording() {
        this.mAudio.end();
        this.mAudio = null;
    }

    private void recordInternalAudio() throws IllegalStateException {
        this.mAudio.start();
    }

    /* access modifiers changed from: protected */
    public SavedRecording save() throws IOException {
        try {
            Log.d("Recording_MediaRecorder", "muxing recording");
            File createTempFile = File.createTempFile("temp", ".mp4", RecordingUtils.getRecordingDir(this.mContext));
            ScreenRecordingMuxer screenRecordingMuxer = new ScreenRecordingMuxer(0, createTempFile.getAbsolutePath(), this.mTempVideoFile.getAbsolutePath(), this.mTempAudioFile.getAbsolutePath());
            this.mMuxer = screenRecordingMuxer;
            screenRecordingMuxer.mux();
            this.mTempVideoFile.delete();
            this.mTempAudioFile.delete();
            this.mTempVideoFile = createTempFile;
        } catch (IOException | IllegalStateException e) {
            Log.e("Recording_MediaRecorder", "muxing recording " + e.getMessage());
            Log.e("Recording_MediaRecorder", "muxing recording Video existed " + this.mTempVideoFile.exists() + ";Audio existed " + this.mTempAudioFile.exists());
            e.printStackTrace();
        }
        String format = new SimpleDateFormat("'screen-'yyyyMMdd-HHmmss'.mp4'").format(new Date());
        ContentValues contentValues = new ContentValues();
        contentValues.put("relative_path", Environment.DIRECTORY_MOVIES + File.separator + "Screenrecord");
        contentValues.put("_display_name", format);
        contentValues.put("mime_type", "video/mp4");
        contentValues.put("date_added", Long.valueOf(System.currentTimeMillis()));
        contentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri insert = contentResolver.insert(MediaStore.Video.Media.getContentUri("external_primary"), contentValues);
        Log.d("Recording_MediaRecorder", insert.toString());
        OutputStream openOutputStream = contentResolver.openOutputStream(insert, "w");
        Files.copy(this.mTempVideoFile.toPath(), openOutputStream);
        openOutputStream.close();
        File file = this.mTempAudioFile;
        if (file != null) {
            file.delete();
        }
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        SavedRecording savedRecording = new SavedRecording(insert, this.mTempVideoFile, new Size(displayMetrics.widthPixels, displayMetrics.heightPixels));
        this.mTempVideoFile.delete();
        return savedRecording;
    }

    public class SavedRecording {
        private Bitmap mThumbnailBitmap;
        private Uri mUri;

        protected SavedRecording(Uri uri, File file, Size size) {
            this.mUri = uri;
            try {
                this.mThumbnailBitmap = ThumbnailUtils.createVideoThumbnail(file, size, (CancellationSignal) null);
            } catch (IOException e) {
                Log.e("Recording_MediaRecorder", "Error creating thumbnail", e);
            }
        }

        public Uri getUri() {
            return this.mUri;
        }

        public Bitmap getThumbnail() {
            return this.mThumbnailBitmap;
        }
    }
}
