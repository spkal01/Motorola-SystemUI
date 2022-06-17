package com.android.systemui.screenrecord;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Property;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.systemui.Dependency;
import com.android.systemui.Prefs;
import com.android.systemui.R$dimen;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.R$style;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.moto.DesktopFeature;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ScreenRecordPanel implements View.OnClickListener, ConfigurationController.ConfigurationListener {
    private static final int[] sAudioSourceIcon = {R$drawable.zz_moto_recording_voice_mute, R$drawable.zz_moto_recording_voice_mic, R$drawable.zz_moto_recording_voice_internal, R$drawable.zz_moto_recording_voice_mix};
    private static final int[] sAudioSourceSelectedIcon = {R$drawable.zz_moto_recording_voice_mute_selected, R$drawable.zz_moto_recording_voice_mic_selected, R$drawable.zz_moto_recording_voice_internal_selected, R$drawable.zz_moto_recording_voice_mix_selected};
    private static final int[] sAudioSourceText = {R$string.screenrecord_mute, R$string.screenrecord_mic, R$string.screenrecord_internal, R$string.screenrecord_mix};
    private static final int[] sRotation = {0, 90, 180, 270};
    /* access modifiers changed from: private */
    public ImageView mArrow;
    private ScreenRecordingAudioSource mAudioSource = ScreenRecordingAudioSource.NONE;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private RecordingBarBg mBarBg;
    private int mBarHeight;
    /* access modifiers changed from: private */
    public RelativeLayout mBarLayout;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams mBarParams;
    private int mBarWindowBottom;
    private int mBarWindowLeft;
    /* access modifiers changed from: private */
    public long mBaseTime = 0;
    private Callback mCallback = new Callback();
    /* access modifiers changed from: private */
    public CameraDevice mCameraDevice;
    private int mCameraHeight;
    private String mCameraId;
    /* access modifiers changed from: private */
    public FrameLayout mCameraLayout;
    /* access modifiers changed from: private */
    public int mCameraLayoutHeight;
    private final CameraManager mCameraManager;
    private ImageView mCameraOffView;
    private int mCameraPadding;
    private WindowManager.LayoutParams mCameraParams;
    private int mCameraPreviewHeight = 0;
    private int mCameraPreviewWidth = 0;
    private ImageView mCameraStateView;
    private TextureView mCameraView;
    /* access modifiers changed from: private */
    public CaptureRequest mCaptureRequest;
    /* access modifiers changed from: private */
    public CaptureRequest.Builder mCaptureRequestBuilder;
    /* access modifiers changed from: private */
    public LinearLayout mChildControllerView;
    private final ScreenLifecycle.Observer mCliScreenObserver = new ScreenLifecycle.Observer() {
        public void onLidOpen() {
            Log.d("Recording_Panel", "RECORDING_STOP by lid open");
            ScreenRecordPanel.this.nextRecordingState(0);
        }
    };
    /* access modifiers changed from: private */
    public ImageView mCloseView;
    private CollapseRunnable mCollapseRunnable = new CollapseRunnable();
    private RecordingRadioGroup mColorLayout;
    private ImageView mColorView;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final RecordingController mController;
    /* access modifiers changed from: private */
    public RecordingLayout mControllerView;
    /* access modifiers changed from: private */
    public int mCountdown = 0;
    private CameraDevice.StateCallback mDeviceStateCallback = new CameraDevice.StateCallback() {
        public void onOpened(CameraDevice cameraDevice) {
            CameraDevice unused = ScreenRecordPanel.this.mCameraDevice = cameraDevice;
            ScreenRecordPanel.this.previewSession();
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            if (ScreenRecordPanel.this.mCameraDevice != null && ScreenRecordPanel.this.mCameraDevice == cameraDevice) {
                ScreenRecordPanel.this.mMainHandler.post(new ScreenRecordPanel$2$$ExternalSyntheticLambda1(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onDisconnected$0() {
            ScreenRecordPanel.this.updateCameraState(false);
        }

        public void onError(CameraDevice cameraDevice, int i) {
            ScreenRecordPanel.this.mMainHandler.post(new ScreenRecordPanel$2$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$1() {
            ScreenRecordPanel.this.updateCameraState(false);
        }
    };
    private final boolean mDisableSecureOverlay;
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        public void onDisplayAdded(int i) {
        }

        public void onDisplayChanged(int i) {
        }

        public void onDisplayRemoved(int i) {
            if (ScreenRecordPanel.this.mContext.getDisplayId() == i) {
                Log.d("Recording_Panel", "RECORDING_STOP by display removed=" + i);
                ScreenRecordPanel.this.nextRecordingState(0);
            }
        }
    };
    private final DisplayManager mDisplayManager;
    private DoodleView mDrawingLayout;
    /* access modifiers changed from: private */
    public int mIconSize;
    /* access modifiers changed from: private */
    public boolean mIsAnimation;
    /* access modifiers changed from: private */
    public boolean mIsCameraOn;
    private boolean mIsColorShowing;
    /* access modifiers changed from: private */
    public boolean mIsDestroy;
    private boolean mIsDrawing;
    private boolean mIsExpanded = true;
    /* access modifiers changed from: private */
    public boolean mIsPaused;
    /* access modifiers changed from: private */
    public boolean mIsRight = true;
    private int mLandRotation;
    /* access modifiers changed from: private */
    public final Handler mMainHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public LinearLayout mMenuLayout;
    private int mMenuLineHeight;
    private int mMenuLineWidth;
    private int mOrientation;
    private final OrientationListener mOrientationListener;
    private long mPausedStart;
    /* access modifiers changed from: private */
    public long mPausedTime = 0;
    private ImageView mPenView;
    /* access modifiers changed from: private */
    public CameraCaptureSession mPreviewSession;
    private ImageView mRecordStateView;
    private final RecordingBroadcastReceiver mRecordingBroadcastReceiver;
    /* access modifiers changed from: private */
    public int mRecordingState = 0;
    /* access modifiers changed from: private */
    public ImageView mRecordingStateView;
    private int mRotation = 0;
    private int mScaledHeight = 0;
    private int mScaledWidth = 0;
    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            ScreenRecordPanel.this.connectCamera(i, i2);
            ScreenRecordPanel.this.transformPreviewToRotation(i, i2);
        }
    };
    private View mTimer;
    /* access modifiers changed from: private */
    public TextView mTimerContent;
    private int mViewFinderSize;
    private ImageView mVoiceStateView;
    /* access modifiers changed from: private */
    public final WindowManager mWindowManager;

    public ScreenRecordPanel(Context context) {
        int i;
        this.mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        this.mWindowManager = windowManager;
        this.mCameraManager = (CameraManager) context.getSystemService("camera");
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
        this.mController = (RecordingController) Dependency.get(RecordingController.class);
        Resources resources = context.getResources();
        this.mLandRotation = windowManager.getDefaultDisplay().getRotation();
        this.mOrientation = resources.getConfiguration().orientation;
        this.mRotation = sRotation[windowManager.getDefaultDisplay().getRotation()];
        this.mOrientationListener = new OrientationListener(context, 3);
        this.mRecordingBroadcastReceiver = new RecordingBroadcastReceiver();
        this.mBarWindowLeft = resources.getDimensionPixelSize(R$dimen.screenrecord_bar_window_left);
        this.mBarWindowBottom = resources.getDimensionPixelSize(R$dimen.screenrecord_bar_window_bottom);
        this.mMenuLineHeight = resources.getDimensionPixelSize(R$dimen.screenrecord_menu_line_height);
        this.mMenuLineWidth = resources.getDimensionPixelSize(R$dimen.screenrecord_menu_line_width);
        this.mIconSize = resources.getDimensionPixelSize(R$dimen.screenrecord_icon_size);
        this.mViewFinderSize = RecordingSettings.getViewFinderSize(context);
        this.mCameraPadding = resources.getDimensionPixelSize(R$dimen.screenrecord_camera_padding);
        if (this.mViewFinderSize == 0) {
            i = resources.getDimensionPixelSize(R$dimen.screenrecord_camera_height);
        } else {
            i = resources.getDimensionPixelSize(R$dimen.screenrecord_camera_big_height);
        }
        this.mCameraHeight = i;
        this.mCameraLayoutHeight = i + (this.mCameraPadding * 2);
        this.mBarHeight = resources.getDimensionPixelSize(R$dimen.screenrecord_bar_window_height);
        Log.d("Recording_Panel", "mViewFinderSize=" + this.mViewFinderSize);
        this.mDisableSecureOverlay = "1".equals(SystemProperties.get("screen_recording_disable_secure_overlay", "0"));
    }

    public void show() {
        Log.d("Recording_Panel", "show recording panel=" + this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        this.mOrientationListener.enable();
        this.mController.addCallback((RecordingController.RecordingStateChangeCallback) this.mCallback);
        this.mRecordingBroadcastReceiver.register(this.mContext.getApplicationContext());
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mMainHandler);
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            ((ScreenLifecycle) Dependency.get(ScreenLifecycle.class)).addObserver(this.mCliScreenObserver);
        }
        nextRecordingState(1);
    }

    private void hide() {
        Log.d("Recording_Panel", "hide recording panel=" + this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
        this.mOrientationListener.disable();
        this.mController.removeCallback((RecordingController.RecordingStateChangeCallback) this.mCallback);
        this.mRecordingBroadcastReceiver.unregister(this.mContext.getApplicationContext());
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
        if (MotoFeature.getInstance(this.mContext).isSupportCli()) {
            ((ScreenLifecycle) Dependency.get(ScreenLifecycle.class)).removeObserver(this.mCliScreenObserver);
        }
        destroyVoiceOptionWindow();
        destroyDrawingWindow();
        this.mMainHandler.removeCallbacks(this.mCollapseRunnable);
        RelativeLayout relativeLayout = this.mBarLayout;
        if (relativeLayout != null && relativeLayout.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.mBarLayout);
        }
        updateCameraState(false);
        this.mIsDestroy = true;
    }

    private void createRecordingBarWindow() {
        RelativeLayout relativeLayout = (RelativeLayout) ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R$layout.zz_moto_floating_bar, (ViewGroup) null);
        this.mBarLayout = relativeLayout;
        RecordingLayout recordingLayout = (RecordingLayout) relativeLayout.findViewById(R$id.controller);
        this.mControllerView = recordingLayout;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recordingLayout.getLayoutParams();
        layoutParams.height = this.mBarHeight;
        this.mControllerView.setLayoutParams(layoutParams);
        this.mControllerView.setOnTouchListener(new FloatViewMoveListener());
        this.mControllerView.setInterceptTouchListen(new ScreenRecordPanel$$ExternalSyntheticLambda1(this));
        this.mChildControllerView = (LinearLayout) this.mBarLayout.findViewById(R$id.child_controller);
        ImageView imageView = (ImageView) this.mBarLayout.findViewById(R$id.record_state);
        this.mRecordStateView = imageView;
        imageView.setOnClickListener(this);
        ImageView imageView2 = (ImageView) this.mBarLayout.findViewById(R$id.recording_state);
        this.mRecordingStateView = imageView2;
        imageView2.setOnClickListener(this);
        ImageView imageView3 = (ImageView) this.mBarLayout.findViewById(R$id.camera_state);
        this.mCameraStateView = imageView3;
        setImageResource(imageView3, R$drawable.zz_moto_recording_camera_close, R$string.screenrecord_camera_off);
        this.mCameraStateView.setOnClickListener(this);
        this.mVoiceStateView = (ImageView) this.mBarLayout.findViewById(R$id.voice_state);
        int audioResource = RecordingSettings.getAudioResource(this.mContext);
        this.mAudioSource = ScreenRecordingAudioSource.values()[audioResource];
        setImageResource(this.mVoiceStateView, sAudioSourceIcon[audioResource], sAudioSourceText[audioResource]);
        this.mVoiceStateView.setOnClickListener(this);
        ImageView imageView4 = (ImageView) this.mBarLayout.findViewById(R$id.pen);
        this.mPenView = imageView4;
        imageView4.setOnClickListener(this);
        ImageView imageView5 = (ImageView) this.mBarLayout.findViewById(R$id.color);
        this.mColorView = imageView5;
        imageView5.setOnClickListener(this);
        RecordingRadioGroup recordingRadioGroup = (RecordingRadioGroup) this.mBarLayout.findViewById(R$id.color_radio_group);
        this.mColorLayout = recordingRadioGroup;
        recordingRadioGroup.check(getColorId());
        updateColor(true);
        this.mColorLayout.setOnCheckedChangeListener(new ScreenRecordPanel$$ExternalSyntheticLambda2(this));
        ImageView imageView6 = (ImageView) this.mBarLayout.findViewById(R$id.close);
        this.mCloseView = imageView6;
        imageView6.setOnClickListener(this);
        ImageView imageView7 = (ImageView) this.mBarLayout.findViewById(R$id.arrow);
        this.mArrow = imageView7;
        imageView7.setOnClickListener(this);
        this.mTimer = this.mBarLayout.findViewById(R$id.timer);
        this.mTimerContent = (TextView) this.mBarLayout.findViewById(R$id.timer_right);
        initBarBackground();
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(-2, -2, 2036, 8, -3);
        this.mBarParams = layoutParams2;
        int i = layoutParams2.privateFlags | 536870912;
        layoutParams2.privateFlags = i;
        if (!this.mDisableSecureOverlay) {
            layoutParams2.privateFlags = i | 2097152;
        }
        layoutParams2.x = this.mBarWindowLeft;
        if (DesktopFeature.isDesktopMode(this.mContext.getDisplay())) {
            this.mBarParams.y = (getScreenHeight() - this.mBarHeight) / 2;
        } else {
            this.mBarParams.y = this.mBarWindowBottom;
        }
        WindowManager.LayoutParams layoutParams3 = this.mBarParams;
        layoutParams3.gravity = 53;
        layoutParams3.windowAnimations = R$style.recording_windowAnimations;
        layoutParams3.setTitle("screenrecord_bar");
        this.mWindowManager.addView(this.mBarLayout, this.mBarParams);
        updateBarBackground();
        if (RecordingSettings.sCameraStatus) {
            this.mBarLayout.post(new ScreenRecordPanel$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createRecordingBarWindow$0(View view, MotionEvent motionEvent) {
        if (this.mMenuLayout != null) {
            return true;
        }
        CollapseRunnable collapseRunnable = this.mCollapseRunnable;
        if (collapseRunnable == null) {
            return false;
        }
        this.mMainHandler.removeCallbacks(collapseRunnable);
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createRecordingBarWindow$1(RadioGroup radioGroup, int i) {
        updateColor(false);
        saveColorId(i);
        updateColorLayoutVisibility(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createRecordingBarWindow$2() {
        updateCameraState(true);
    }

    private void updateVoiceState(ScreenRecordingAudioSource screenRecordingAudioSource) {
        Log.d("Recording_Panel", "mAudioSource=" + this.mAudioSource + ";audioSource=" + screenRecordingAudioSource);
        this.mAudioSource = screenRecordingAudioSource;
        destroyVoiceOptionWindow();
        RecordingSettings.setAudioResource(this.mContext, this.mAudioSource.ordinal());
    }

    /* access modifiers changed from: private */
    public void updateCameraState(boolean z) {
        Log.d("Recording_Panel", "mIsCameraOn=" + this.mIsCameraOn + ";on=" + z);
        if (this.mIsCameraOn != z) {
            this.mIsCameraOn = z;
            if (z) {
                createCameraWindow();
                farAwaySideAnimation();
            } else {
                destroyCameraWindow();
                closeToSideAnimation();
            }
            if (this.mIsCameraOn) {
                setImageResource(this.mCameraStateView, R$drawable.zz_moto_recording_camera_open, R$string.screenrecord_camera_on);
            } else {
                setImageResource(this.mCameraStateView, R$drawable.zz_moto_recording_camera_close, R$string.screenrecord_camera_off);
            }
        }
    }

    /* access modifiers changed from: private */
    public void exchangeCamera(boolean z) {
        Bitmap bitmap;
        if (!this.mIsCameraOn) {
            return;
        }
        if (z) {
            TextureView textureView = this.mCameraView;
            if (textureView != null) {
                textureView.setVisibility(0);
            }
            ImageView imageView = this.mCameraOffView;
            if (imageView != null) {
                imageView.setVisibility(8);
                return;
            }
            return;
        }
        ImageView imageView2 = this.mCameraOffView;
        if (imageView2 != null) {
            imageView2.setVisibility(0);
            this.mCameraOffView.setRotation((float) this.mRotation);
            TextureView textureView2 = this.mCameraView;
            if (!(textureView2 == null || (bitmap = textureView2.getBitmap(this.mScaledWidth, this.mScaledHeight)) == null)) {
                this.mCameraOffView.setImageBitmap(bitmap);
            }
        }
        TextureView textureView3 = this.mCameraView;
        if (textureView3 != null) {
            textureView3.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void nextRecordingState(int i) {
        if (i != 0) {
            if (i == 1) {
                createRecordingBarWindow();
                startCountdown();
            } else if (i == 2) {
                this.mController.startRecording();
            } else if (i == 3) {
                this.mController.cancelCountdown(false);
            }
        } else if (this.mController.isRecording()) {
            this.mController.stopRecording();
        } else {
            this.mController.cancelCountdown(true);
        }
    }

    /* access modifiers changed from: private */
    public void updateRecordingState(int i) {
        updateRecordingStateInner(i);
    }

    private void updateRecordingStateInner(int i) {
        Log.d("Recording_Panel", "mRecordingState=" + this.mRecordingState + ";recordingState=" + i);
        this.mRecordingState = i;
        if (i == 0) {
            hide();
        } else if (i == 1) {
            int i2 = R$drawable.zz_moto_recording_record;
            int i3 = this.mCountdown;
            if (i3 == 1) {
                i2 = R$drawable.zz_moto_recording_countdown_1;
            } else if (i3 == 2) {
                i2 = R$drawable.zz_moto_recording_countdown_2;
            } else if (i3 == 3) {
                i2 = R$drawable.zz_moto_recording_countdown_3;
            }
            setImageResource(this.mRecordStateView, i2, R$string.screenrecord_countdown_timer);
        } else if (i == 2) {
            setImageResource(this.mRecordStateView, R$drawable.zz_moto_recording_stop, R$string.screenrecord_stop_recording);
            this.mTimer.setVisibility(0);
            this.mBaseTime = SystemClock.elapsedRealtime();
            this.mTimerContent.postDelayed(new TimerRunnable(), 1000);
            updateCameraPositionAsync();
            if (this.mIsExpanded) {
                stretchController();
                if (this.mIsDrawing) {
                    startRecordingAimation();
                }
            }
        } else if (i == 3) {
            setImageResource(this.mRecordStateView, R$drawable.zz_moto_recording_record, R$string.screenrecord_start_recording);
        }
    }

    /* access modifiers changed from: private */
    public void sortController(boolean z) {
        if (this.mIsRight != z) {
            Log.d("Recording_Panel", "mIsRight=" + this.mIsRight);
            this.mIsRight = z;
            this.mControllerView.removeAllViews();
            this.mChildControllerView.removeAllViews();
            if (this.mIsRight) {
                this.mControllerView.addView(this.mArrow);
                this.mControllerView.addView(this.mChildControllerView);
                this.mControllerView.addView(this.mRecordingStateView);
                this.mControllerView.addView(this.mRecordStateView);
                this.mChildControllerView.addView(this.mCloseView);
                this.mChildControllerView.addView(this.mColorView);
                this.mChildControllerView.addView(this.mPenView);
                this.mChildControllerView.addView(this.mVoiceStateView);
                this.mChildControllerView.addView(this.mCameraStateView);
            } else {
                this.mControllerView.addView(this.mRecordStateView);
                this.mControllerView.addView(this.mRecordingStateView);
                this.mControllerView.addView(this.mChildControllerView);
                this.mControllerView.addView(this.mArrow);
                this.mChildControllerView.addView(this.mCameraStateView);
                this.mChildControllerView.addView(this.mVoiceStateView);
                this.mChildControllerView.addView(this.mPenView);
                this.mChildControllerView.addView(this.mColorView);
                this.mChildControllerView.addView(this.mCloseView);
            }
            updateBottom();
            updateArrow();
            updateBarBackground();
            updateCameraPosition(true, false);
        }
    }

    /* access modifiers changed from: private */
    public void stretchController() {
        if (!this.mIsDrawing) {
            boolean z = !this.mIsExpanded;
            this.mIsExpanded = z;
            if (z) {
                expandedAnimation(this.mIsRight);
            } else {
                collapsedAnimation(this.mIsRight);
            }
        }
    }

    private void initBarBackground() {
        RecordingBarBg recordingBarBg = (RecordingBarBg) this.mBarLayout.findViewById(R$id.bar_bg);
        this.mBarBg = recordingBarBg;
        recordingBarBg.setCameraPadding(this.mCameraPadding);
    }

    private void updateBarBackground() {
        this.mBarBg.setAlignRight(this.mIsRight);
        this.mBarBg.invalidate();
    }

    private int getColorId() {
        int i = Prefs.getInt(this.mContext, "ScreenRecordColor", 0);
        if (i == 0) {
            return R$id.screenrecord_color_radio_black;
        }
        if (1 == i) {
            return R$id.screenrecord_color_radio_white;
        }
        if (2 == i) {
            return R$id.screenrecord_color_radio_red;
        }
        if (3 == i) {
            return R$id.screenrecord_color_radio_yellow;
        }
        if (4 == i) {
            return R$id.screenrecord_color_radio_blue;
        }
        return R$id.screenrecord_color_radio_green;
    }

    private void saveColorId(int i) {
        if (R$id.screenrecord_color_radio_black == i) {
            Prefs.putInt(this.mContext, "ScreenRecordColor", 0);
        } else if (R$id.screenrecord_color_radio_white == i) {
            Prefs.putInt(this.mContext, "ScreenRecordColor", 1);
        } else if (R$id.screenrecord_color_radio_red == i) {
            Prefs.putInt(this.mContext, "ScreenRecordColor", 2);
        } else if (R$id.screenrecord_color_radio_yellow == i) {
            Prefs.putInt(this.mContext, "ScreenRecordColor", 3);
        } else if (R$id.screenrecord_color_radio_blue == i) {
            Prefs.putInt(this.mContext, "ScreenRecordColor", 4);
        } else if (R$id.screenrecord_color_radio_green == i) {
            Prefs.putInt(this.mContext, "ScreenRecordColor", 5);
        }
    }

    private void updateColor(boolean z) {
        int brushColor = this.mColorLayout.getBrushColor(z);
        LayerDrawable layerDrawable = (LayerDrawable) this.mContext.getDrawable(R$drawable.zz_moto_recording_color);
        Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(R$id.fill);
        findDrawableByLayerId.setTint(brushColor);
        if (R$id.screenrecord_color_radio_black != this.mColorLayout.getCheckedRadioButtonId() || z) {
            this.mColorView.setImageDrawable(findDrawableByLayerId);
        } else {
            this.mColorView.setImageDrawable(layerDrawable);
        }
        DoodleView doodleView = this.mDrawingLayout;
        if (doodleView != null) {
            doodleView.setPaintColor(brushColor);
        }
    }

    private void updateColorLayoutVisibility(boolean z) {
        this.mIsColorShowing = z;
        if (z) {
            this.mColorLayout.setVisibility(0);
        } else {
            this.mColorLayout.setVisibility(8);
        }
        if (!this.mIsColorShowing) {
            postCollapse();
        }
    }

    /* access modifiers changed from: private */
    public void updateBottomVisibility(int i) {
        if (this.mRecordingState == 2) {
            this.mTimer.setVisibility(i);
        }
        if (this.mIsColorShowing) {
            this.mColorLayout.setVisibility(i);
        }
    }

    private void updateBottom() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mTimer.getLayoutParams();
        int i = 7;
        layoutParams.removeRule(7);
        layoutParams.removeRule(5);
        layoutParams.addRule(this.mIsRight ? 7 : 5, this.mControllerView.getId());
        this.mTimer.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) this.mColorLayout.getLayoutParams();
        layoutParams2.removeRule(7);
        layoutParams2.removeRule(5);
        if (this.mIsRight) {
            i = 5;
        }
        layoutParams2.addRule(i, this.mControllerView.getId());
        this.mColorLayout.setLayoutParams(layoutParams2);
    }

    /* access modifiers changed from: private */
    public void updateArrow() {
        int i;
        int i2;
        if (this.mIsDrawing) {
            DoodleView doodleView = this.mDrawingLayout;
            if (doodleView == null || !doodleView.hasDrawingPath()) {
                setImageResource(this.mArrow, R$drawable.zz_moto_recording_nothing_undo, R$string.screenrecord_undo);
            } else {
                setImageResource(this.mArrow, R$drawable.zz_moto_recording_undo, R$string.screenrecord_undo);
            }
        } else if (this.mIsExpanded) {
            ImageView imageView = this.mArrow;
            if (this.mIsRight) {
                i2 = R$drawable.zz_moto_recording_arrow_right;
            } else {
                i2 = R$drawable.zz_moto_recording_arrow_left;
            }
            setImageResource(imageView, i2, R$string.screenrecord_collapse_menu);
        } else {
            ImageView imageView2 = this.mArrow;
            if (this.mIsRight) {
                i = R$drawable.zz_moto_recording_arrow_left;
            } else {
                i = R$drawable.zz_moto_recording_arrow_right;
            }
            setImageResource(imageView2, i, R$string.screenrecord_expand_menu);
        }
    }

    /* access modifiers changed from: private */
    public void updateCameraPositionAsync() {
        FrameLayout frameLayout = this.mCameraLayout;
        if (frameLayout != null) {
            frameLayout.post(new ScreenRecordPanel$$ExternalSyntheticLambda4(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateCameraPositionAsync$3() {
        updateCameraPosition(true, false);
    }

    /* access modifiers changed from: private */
    public void updateCameraPosition(boolean z, boolean z2) {
        RelativeLayout relativeLayout;
        if (this.mCameraLayout != null) {
            WindowManager.LayoutParams layoutParams = this.mCameraParams;
            layoutParams.gravity = 53;
            if (this.mIsRight) {
                int i = this.mBarParams.x - this.mCameraLayoutHeight;
                layoutParams.x = i;
                if (i < 0) {
                    layoutParams.x = 0;
                }
            } else {
                Rect rect = new Rect();
                this.mBarLayout.getWindowDisplayFrame(rect);
                int i2 = rect.right - rect.left;
                this.mCameraParams.x = this.mBarParams.x + this.mBarLayout.getWidth();
                WindowManager.LayoutParams layoutParams2 = this.mCameraParams;
                int i3 = layoutParams2.x;
                int i4 = this.mCameraLayoutHeight;
                if (i3 > i2 - i4) {
                    layoutParams2.x = i2 - i4;
                }
            }
            this.mCameraParams.y = (this.mBarParams.y + (this.mControllerView.getHeight() / 2)) - (this.mCameraLayoutHeight / 2);
            if (z && this.mCameraLayout.isAttachedToWindow()) {
                this.mWindowManager.updateViewLayout(this.mCameraLayout, this.mCameraParams);
            }
            if (z2 && (relativeLayout = this.mBarLayout) != null) {
                if (this.mIsRight) {
                    this.mBarParams.x = this.mCameraParams.x + this.mCameraLayoutHeight;
                } else {
                    this.mBarParams.x = this.mCameraParams.x - relativeLayout.getWidth();
                }
                this.mWindowManager.updateViewLayout(this.mBarLayout, this.mBarParams);
            }
        }
    }

    private void startCountdown() {
        Context context = this.mContext;
        PendingIntent service = PendingIntent.getService(context, 2, RecordingService.getStartIntent(context, 0, this.mAudioSource.ordinal(), RecordingSettings.getTouchPoint(this.mContext)), 201326592);
        Context context2 = this.mContext;
        this.mController.startCountdown(3000, 1000, service, PendingIntent.getService(context2, 2, RecordingService.getStopIntent(context2), 201326592));
    }

    private void createDrawingWindow() {
        DoodleView doodleView = new DoodleView(this.mContext, (AttributeSet) null);
        this.mDrawingLayout = doodleView;
        doodleView.setArrawView(this.mArrow);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 2038, 262152, -3);
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.screenOrientation = 14;
        layoutParams.gravity = 51;
        layoutParams.setTitle("screenrecord_drawing");
        this.mWindowManager.addView(this.mDrawingLayout, layoutParams);
    }

    private void destroyDrawingWindow() {
        DoodleView doodleView = this.mDrawingLayout;
        if (doodleView != null && doodleView.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.mDrawingLayout);
            this.mDrawingLayout = null;
        }
    }

    private void createVoiceOptionWindow() {
        setImageResource(this.mVoiceStateView, sAudioSourceSelectedIcon[this.mAudioSource.ordinal()], sAudioSourceText[this.mAudioSource.ordinal()]);
        TextView textView = null;
        LinearLayout linearLayout = (LinearLayout) ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R$layout.zz_moto_floating_menu, (ViewGroup) null);
        this.mMenuLayout = linearLayout;
        View findViewById = linearLayout.findViewById(R$id.recording_mic);
        findViewById.setOnClickListener(this);
        View findViewById2 = this.mMenuLayout.findViewById(R$id.recording_internal);
        findViewById2.setOnClickListener(this);
        View findViewById3 = this.mMenuLayout.findViewById(R$id.recording_mute);
        findViewById3.setOnClickListener(this);
        View findViewById4 = this.mMenuLayout.findViewById(R$id.recording_mix);
        findViewById4.setOnClickListener(this);
        ScreenRecordingAudioSource screenRecordingAudioSource = this.mAudioSource;
        if (screenRecordingAudioSource == ScreenRecordingAudioSource.NONE) {
            textView = findViewById3;
        } else if (screenRecordingAudioSource == ScreenRecordingAudioSource.INTERNAL) {
            textView = findViewById2;
        } else if (screenRecordingAudioSource == ScreenRecordingAudioSource.MIC) {
            textView = findViewById;
        } else if (screenRecordingAudioSource == ScreenRecordingAudioSource.MIC_AND_INTERNAL) {
            textView = findViewById4;
        }
        if (textView != null) {
            TextView textView2 = textView;
            Drawable[] compoundDrawablesRelative = textView2.getCompoundDrawablesRelative();
            int color = this.mContext.getResources().getColor(17170498);
            if (compoundDrawablesRelative[0] != null) {
                compoundDrawablesRelative[0].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
            textView2.setTextColor(color);
            if (textView == findViewById) {
                textView.setBackgroundResource(R$drawable.zz_moto_recording_menu_selected_top);
            } else if (textView == findViewById3) {
                textView.setBackgroundResource(R$drawable.zz_moto_recording_menu_selected_bottom);
            } else {
                textView.setBackgroundResource(R$drawable.zz_moto_recording_menu_selected);
            }
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2036, 262152, -3);
        if (!this.mDisableSecureOverlay) {
            layoutParams.privateFlags |= 2097152;
        }
        layoutParams.x = getMenuWindowLeft();
        layoutParams.y = getMenuWindowTop();
        layoutParams.gravity = 51;
        layoutParams.setTitle("screenrecord_menu");
        this.mWindowManager.addView(this.mMenuLayout, layoutParams);
        this.mMenuLayout.setOnTouchListener(new ScreenRecordPanel$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createVoiceOptionWindow$4(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 4) {
            return false;
        }
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        int[] iArr = new int[2];
        this.mBarLayout.getLocationOnScreen(iArr);
        if (((float) rawX) > this.mBarLayout.getX() && rawX < iArr[0] + this.mBarLayout.getWidth() && ((float) rawY) > this.mBarLayout.getY() && rawY < iArr[1] + this.mBarLayout.getHeight()) {
            return false;
        }
        destroyVoiceOptionWindow();
        return true;
    }

    /* access modifiers changed from: private */
    public void destroyVoiceOptionWindow() {
        setImageResource(this.mVoiceStateView, sAudioSourceIcon[this.mAudioSource.ordinal()], sAudioSourceText[this.mAudioSource.ordinal()]);
        LinearLayout linearLayout = this.mMenuLayout;
        if (linearLayout != null && linearLayout.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.mMenuLayout);
            this.mMenuLayout = null;
        }
        postCollapse();
    }

    private void createCameraWindow() {
        Log.d("Recording_Panel", "createCameraWindow");
        FrameLayout frameLayout = (FrameLayout) ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R$layout.zz_moto_floating_camera_preview, (ViewGroup) null);
        this.mCameraLayout = frameLayout;
        frameLayout.setOnTouchListener(new FloatViewMoveListener());
        this.mCameraOffView = (ImageView) this.mCameraLayout.findViewById(R$id.camera_off_img);
        TextureView textureView = (TextureView) this.mCameraLayout.findViewById(R$id.tv_camera_preview);
        this.mCameraView = textureView;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textureView.getLayoutParams();
        int i = this.mCameraHeight;
        layoutParams.height = i;
        layoutParams.width = i;
        this.mCameraView.setLayoutParams(layoutParams);
        this.mCameraOffView.setLayoutParams(layoutParams);
        int i2 = this.mCameraLayoutHeight;
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(i2, i2, 2036, 8, -3);
        this.mCameraParams = layoutParams2;
        layoutParams2.privateFlags |= 536870912;
        updateCameraPosition(false, false);
        this.mCameraParams.setTitle("screenrecord_camera");
        this.mWindowManager.addView(this.mCameraLayout, this.mCameraParams);
        enableCameraControls();
    }

    private void destroyCameraWindow() {
        closeCamera();
        HandlerThread handlerThread = this.mBackgroundThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mBackgroundHandler = null;
            this.mBackgroundThread = null;
        }
        updateCameraPosition(false, false);
        FrameLayout frameLayout = this.mCameraLayout;
        if (frameLayout != null && frameLayout.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.mCameraLayout);
            this.mCameraLayout = null;
        }
    }

    private void closeCamera() {
        CameraCaptureSession cameraCaptureSession = this.mPreviewSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.mPreviewSession = null;
        }
        CameraDevice cameraDevice = this.mCameraDevice;
        if (cameraDevice != null) {
            cameraDevice.close();
            this.mCameraDevice = null;
        }
    }

    /* access modifiers changed from: private */
    public void connectCamera(int i, int i2) {
        try {
            Log.d("Recording_Panel", "before width=" + i + ";height=" + i2);
            String[] cameraIdList = this.mCameraManager.getCameraIdList();
            int length = cameraIdList.length;
            int i3 = 0;
            int i4 = 0;
            while (true) {
                if (i4 >= length) {
                    break;
                }
                String str = cameraIdList[i4];
                CameraCharacteristics cameraCharacteristics = this.mCameraManager.getCameraCharacteristics(str);
                if (((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() != 0) {
                    i4++;
                } else {
                    Size[] outputSizes = ((StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class);
                    int i5 = Integer.MAX_VALUE;
                    int length2 = outputSizes.length;
                    while (true) {
                        if (i3 >= length2) {
                            break;
                        }
                        Size size = outputSizes[i3];
                        int width = size.getWidth();
                        int height = size.getHeight();
                        int abs = Math.abs(width - i) + Math.abs(height - i2);
                        if (abs == 0) {
                            this.mCameraPreviewWidth = width;
                            this.mCameraPreviewHeight = height;
                            break;
                        }
                        if (i5 > abs && width > i && height > i2) {
                            this.mCameraPreviewWidth = width;
                            this.mCameraPreviewHeight = height;
                            i5 = abs;
                        }
                        i3++;
                    }
                    this.mCameraId = str;
                }
            }
            Log.d("Recording_Panel", "after width=" + this.mCameraPreviewWidth + ";height=" + this.mCameraPreviewHeight);
            this.mCameraManager.openCamera(this.mCameraId, this.mDeviceStateCallback, this.mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void enableCameraControls() {
        if (this.mBackgroundThread == null) {
            HandlerThread handlerThread = new HandlerThread("recording camera2");
            this.mBackgroundThread = handlerThread;
            handlerThread.start();
            this.mBackgroundHandler = new Handler(this.mBackgroundThread.getLooper());
        }
        this.mCameraView.setSurfaceTextureListener(this.mSurfaceTextureListener);
    }

    /* access modifiers changed from: private */
    public void previewSession() {
        SurfaceTexture surfaceTexture = this.mCameraView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(this.mCameraPreviewWidth, this.mCameraPreviewHeight);
        Surface surface = new Surface(surfaceTexture);
        try {
            CaptureRequest.Builder createCaptureRequest = this.mCameraDevice.createCaptureRequest(1);
            this.mCaptureRequestBuilder = createCaptureRequest;
            createCaptureRequest.addTarget(surface);
            this.mCameraDevice.createCaptureSession(Arrays.asList(new Surface[]{surface}), new CameraCaptureSession.StateCallback() {
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                }

                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    ScreenRecordPanel screenRecordPanel = ScreenRecordPanel.this;
                    CaptureRequest unused = screenRecordPanel.mCaptureRequest = screenRecordPanel.mCaptureRequestBuilder.build();
                    CameraCaptureSession unused2 = ScreenRecordPanel.this.mPreviewSession = cameraCaptureSession;
                    try {
                        ScreenRecordPanel.this.mPreviewSession.setRepeatingRequest(ScreenRecordPanel.this.mCaptureRequest, (CameraCaptureSession.CaptureCallback) null, (Handler) null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }, (Handler) null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void transformPreviewToRotation(int i, int i2) {
        float f;
        Matrix matrix = new Matrix();
        int i3 = this.mRotation;
        int i4 = (i3 == 90 || i3 == 270) ? 180 - i3 : 0;
        float f2 = ((float) this.mCameraPreviewHeight) / ((float) this.mCameraPreviewWidth);
        float f3 = (float) i;
        float f4 = (float) i2;
        float f5 = f3 / f4;
        float f6 = 1.0f;
        if (f5 < f2) {
            float f7 = f2 / f5;
            f = 1.0f;
            f6 = f7;
        } else {
            f = f5 / f2;
        }
        float f8 = f3 * f6;
        float f9 = f4 * f;
        matrix.postScale(f6, f);
        matrix.postTranslate((f3 - f8) / 2.0f, (f4 - f9) / 2.0f);
        RectF rectF = new RectF(0.0f, 0.0f, f3, f4);
        matrix.postRotate((float) i4, rectF.centerX(), rectF.centerY());
        this.mCameraView.setTransform(matrix);
        this.mScaledWidth = (int) f8;
        this.mScaledHeight = (int) f9;
    }

    public void onConfigChanged(Configuration configuration) {
        int i;
        if (configuration != null && (i = configuration.orientation) != this.mOrientation) {
            this.mOrientation = i;
            Log.d("Recording_Panel", "mOrientation=" + this.mOrientation);
            RelativeLayout relativeLayout = this.mBarLayout;
            if (relativeLayout != null && relativeLayout.isAttachedToWindow()) {
                orientationBar();
                sortController(getRight());
                updateCameraPosition(true, false);
                closeToSideAnimation();
            }
        }
    }

    public void onClick(View view) {
        int i;
        int i2;
        int i3;
        int i4;
        if (!this.mIsAnimation) {
            int id = view.getId();
            if (R$id.recording_state == id) {
                if (this.mIsPaused) {
                    if (!this.mController.resume()) {
                        return;
                    }
                } else if (!this.mController.pause()) {
                    return;
                }
                boolean z = !this.mIsPaused;
                this.mIsPaused = z;
                if (z) {
                    this.mPausedStart = SystemClock.elapsedRealtime();
                } else {
                    this.mPausedTime += SystemClock.elapsedRealtime() - this.mPausedStart;
                }
                ImageView imageView = this.mRecordingStateView;
                boolean z2 = this.mIsPaused;
                if (z2) {
                    i3 = R$drawable.zz_moto_recording_resume;
                } else {
                    i3 = R$drawable.zz_moto_recording_pause;
                }
                if (z2) {
                    i4 = R$string.screenrecord_pause_recording;
                } else {
                    i4 = R$string.screenrecord_resume_recording;
                }
                setImageResource(imageView, i3, i4);
                if (!this.mIsExpanded) {
                    collapsedAnimationForResume(this.mIsRight);
                } else {
                    postCollapse();
                }
            } else {
                int i5 = 3;
                if (R$id.record_state == id) {
                    int i6 = this.mRecordingState;
                    if (i6 != 1) {
                        i5 = i6 == 3 ? 2 : 0;
                    }
                    nextRecordingState(i5);
                } else if (R$id.camera_state == id) {
                    if (this.mRecordingState == 1) {
                        nextRecordingState(3);
                    }
                    updateCameraState(!this.mIsCameraOn);
                } else if (R$id.voice_state == id) {
                    if (this.mMenuLayout == null) {
                        if (this.mRecordingState == 1) {
                            nextRecordingState(3);
                        }
                        if (this.mIsColorShowing) {
                            updateColorLayoutVisibility(false);
                        }
                        createVoiceOptionWindow();
                        return;
                    }
                    destroyVoiceOptionWindow();
                } else if (R$id.pen == id) {
                    boolean z3 = !this.mIsDrawing;
                    this.mIsDrawing = z3;
                    ImageView imageView2 = this.mPenView;
                    if (z3) {
                        i = R$drawable.zz_moto_recording_pen_open;
                    } else {
                        i = R$drawable.zz_moto_recording_pen_close;
                    }
                    if (z3) {
                        i2 = R$string.screenrecord_pen_open;
                    } else {
                        i2 = R$string.screenrecord_pen_close;
                    }
                    setImageResource(imageView2, i, i2);
                    if (this.mIsDrawing) {
                        createDrawingWindow();
                        updateColor(false);
                    } else {
                        destroyDrawingWindow();
                        updateColor(true);
                        updateColorLayoutVisibility(false);
                        postCollapse();
                    }
                    updateArrow();
                } else if (R$id.color == id) {
                    if (this.mIsDrawing) {
                        updateColorLayoutVisibility(!this.mIsColorShowing);
                    }
                } else if (R$id.close == id) {
                    nextRecordingState(0);
                } else if (R$id.arrow == id) {
                    if (this.mIsDrawing) {
                        this.mDrawingLayout.undo();
                    } else {
                        stretchController();
                    }
                } else if (R$id.recording_mic == id) {
                    updateVoiceState(ScreenRecordingAudioSource.MIC);
                } else if (R$id.recording_internal == id) {
                    updateVoiceState(ScreenRecordingAudioSource.INTERNAL);
                } else if (R$id.recording_mix == id) {
                    updateVoiceState(ScreenRecordingAudioSource.MIC_AND_INTERNAL);
                } else if (R$id.recording_mute == id) {
                    updateVoiceState(ScreenRecordingAudioSource.NONE);
                }
            }
        }
    }

    private class FloatViewMoveListener implements View.OnTouchListener {
        private FloatViewMoveListener() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
            if (r0 != 3) goto L_0x00d6;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouch(android.view.View r7, android.view.MotionEvent r8) {
            /*
                r6 = this;
                int r0 = r8.getAction()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x002c
                if (r0 == r2) goto L_0x0012
                r7 = 2
                if (r0 == r7) goto L_0x005c
                r7 = 3
                if (r0 == r7) goto L_0x0012
                goto L_0x00d6
            L_0x0012:
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r7.exchangeCamera(r2)
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                boolean r8 = r7.getRight()
                r7.sortController(r8)
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r7.updateBottomVisibility(r1)
                com.android.systemui.screenrecord.ScreenRecordPanel r6 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r6.closeToSideAnimation()
                goto L_0x00d6
            L_0x002c:
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.widget.LinearLayout r0 = r0.mMenuLayout
                if (r0 == 0) goto L_0x003a
                com.android.systemui.screenrecord.ScreenRecordPanel r6 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r6.destroyVoiceOptionWindow()
                return r1
            L_0x003a:
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.widget.FrameLayout r0 = r0.mCameraLayout
                if (r7 != r0) goto L_0x005c
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                com.android.systemui.screenrecord.RecordingLayout r7 = r7.mControllerView
                float r0 = r8.getRawX()
                int r0 = (int) r0
                r7.mTouchStartX = r0
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                com.android.systemui.screenrecord.RecordingLayout r7 = r7.mControllerView
                float r0 = r8.getRawY()
                int r0 = (int) r0
                r7.mTouchStartY = r0
            L_0x005c:
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r0 = 4
                r7.updateBottomVisibility(r0)
                com.android.systemui.screenrecord.ScreenRecordPanel r7 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r7.exchangeCamera(r1)
                float r7 = r8.getRawX()
                int r7 = (int) r7
                float r8 = r8.getRawY()
                int r8 = (int) r8
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                com.android.systemui.screenrecord.RecordingLayout r0 = r0.mControllerView
                int r0 = r0.mTouchStartX
                int r0 = r7 - r0
                com.android.systemui.screenrecord.ScreenRecordPanel r3 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                com.android.systemui.screenrecord.RecordingLayout r3 = r3.mControllerView
                int r3 = r3.mTouchStartY
                int r3 = r8 - r3
                com.android.systemui.screenrecord.ScreenRecordPanel r4 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.view.WindowManager$LayoutParams r4 = r4.mBarParams
                int r5 = r4.x
                int r5 = r5 - r0
                r4.x = r5
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.view.WindowManager$LayoutParams r0 = r0.mBarParams
                int r4 = r0.y
                int r4 = r4 + r3
                r0.y = r4
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r0.adjustBarParams()
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.widget.RelativeLayout r0 = r0.mBarLayout
                boolean r0 = r0.isAttachedToWindow()
                if (r0 == 0) goto L_0x00c6
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.view.WindowManager r0 = r0.mWindowManager
                com.android.systemui.screenrecord.ScreenRecordPanel r3 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.widget.RelativeLayout r3 = r3.mBarLayout
                com.android.systemui.screenrecord.ScreenRecordPanel r4 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                android.view.WindowManager$LayoutParams r4 = r4.mBarParams
                r0.updateViewLayout(r3, r4)
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                r0.updateCameraPosition(r2, r1)
            L_0x00c6:
                com.android.systemui.screenrecord.ScreenRecordPanel r0 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                com.android.systemui.screenrecord.RecordingLayout r0 = r0.mControllerView
                r0.mTouchStartX = r7
                com.android.systemui.screenrecord.ScreenRecordPanel r6 = com.android.systemui.screenrecord.ScreenRecordPanel.this
                com.android.systemui.screenrecord.RecordingLayout r6 = r6.mControllerView
                r6.mTouchStartY = r8
            L_0x00d6:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.screenrecord.ScreenRecordPanel.FloatViewMoveListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    }

    /* access modifiers changed from: private */
    public void postCollapse() {
        if (this.mIsExpanded) {
            this.mMainHandler.removeCallbacks(this.mCollapseRunnable);
            if (!this.mIsDrawing) {
                this.mMainHandler.postDelayed(this.mCollapseRunnable, 5000);
            }
        }
    }

    private final class CollapseRunnable implements Runnable {
        private CollapseRunnable() {
        }

        public void run() {
            ScreenRecordPanel.this.stretchController();
        }
    }

    private final class Callback implements RecordingController.RecordingStateChangeCallback {
        private Callback() {
        }

        public void onCountdown(long j) {
            int unused = ScreenRecordPanel.this.mCountdown = (int) Math.floorDiv(j + 500, 1000);
            ScreenRecordPanel.this.updateRecordingState(1);
        }

        public void onCountdownEnd() {
            ScreenRecordPanel.this.updateRecordingState(2);
        }

        public void onCountdownCancel(boolean z) {
            if (z) {
                ScreenRecordPanel.this.updateRecordingState(0);
            } else {
                ScreenRecordPanel.this.updateRecordingState(3);
            }
        }

        public void onRecordingStart() {
            ScreenRecordPanel.this.updateRecordingState(2);
        }

        public void onRecordingEnd() {
            ScreenRecordPanel.this.updateRecordingState(0);
        }
    }

    private final class TimerRunnable implements Runnable {
        public TimerRunnable() {
            if (ScreenRecordPanel.this.mTimerContent != null) {
                ScreenRecordPanel.this.mTimerContent.setText(ScreenRecordPanel.this.mContext.getString(17040161, new Object[]{0, 0}));
            }
        }

        public void run() {
            if (ScreenRecordPanel.this.mTimerContent != null && 2 == ScreenRecordPanel.this.mRecordingState) {
                if (!ScreenRecordPanel.this.mIsPaused) {
                    int elapsedRealtime = (int) (((SystemClock.elapsedRealtime() - ScreenRecordPanel.this.mBaseTime) - ScreenRecordPanel.this.mPausedTime) / 1000);
                    int i = elapsedRealtime / 3600;
                    int i2 = elapsedRealtime % 60;
                    int i3 = i > 0 ? (elapsedRealtime % 3600) / 60 : elapsedRealtime / 60;
                    if (i == 0) {
                        ScreenRecordPanel.this.mTimerContent.setText(ScreenRecordPanel.this.mContext.getString(17040161, new Object[]{Integer.valueOf(i3), Integer.valueOf(i2)}));
                    } else {
                        ScreenRecordPanel.this.mTimerContent.setText(ScreenRecordPanel.this.mContext.getString(17040160, new Object[]{Integer.valueOf(i), Integer.valueOf(i3), Integer.valueOf(i2)}));
                    }
                    if (elapsedRealtime % 10 == 0 && !RecordingUtils.isRecordingMemEnough(ScreenRecordPanel.this.mContext)) {
                        Toast.makeText(ScreenRecordPanel.this.mContext, R$string.screenrecord_memory_lowest, 1).show();
                    }
                }
                ScreenRecordPanel.this.mTimerContent.postDelayed(this, 1000);
            }
        }
    }

    private final class RecordingBroadcastReceiver extends BroadcastReceiver {
        private RecordingBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.USER_SWITCHED".equals(action) || "android.intent.action.SCREEN_OFF".equals(action)) {
                Log.d("Recording_Panel", "RECORDING_STOP by " + action);
                ScreenRecordPanel.this.nextRecordingState(0);
            }
        }

        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            context.registerReceiver(this, intentFilter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }
    }

    private final class OrientationListener extends OrientationEventListener {
        OrientationListener(Context context, int i) {
            super(context, i);
        }

        public void onOrientationChanged(int i) {
            ScreenRecordPanel.this.updateRotation(i);
        }
    }

    /* access modifiers changed from: private */
    public void updateRotation(int i) {
        int i2;
        if (i != -1) {
            int i3 = i % 360;
            int i4 = this.mRotation;
            if (i4 == 0 && (i3 >= 345 || i3 <= 15)) {
                return;
            }
            if (i3 < i4 - 15 || i3 > i4 + 15) {
                if ((i3 >= 0 && i3 <= 15) || i3 >= 345) {
                    i2 = 0;
                } else if (i3 >= 75 && i3 <= 105) {
                    i2 = 90;
                } else if (i3 < 165 || i3 > 195) {
                    i2 = (i3 < 255 || i3 > 285) ? i4 : 270;
                } else {
                    return;
                }
                if (i4 != i2) {
                    Log.d("Recording_Panel", "lastOrientation=" + i2 + "oldOriention=" + this.mRotation);
                    this.mRotation = i2;
                    FrameLayout frameLayout = this.mCameraLayout;
                    if (frameLayout != null && frameLayout.isAttachedToWindow()) {
                        transformPreviewToRotation(this.mCameraView.getWidth(), this.mCameraView.getHeight());
                    }
                }
            }
        }
    }

    private void orientationBar() {
        int i;
        int i2;
        destroyVoiceOptionWindow();
        int screenHeight = (getScreenHeight() - this.mBarParams.x) - (this.mBarLayout.getWidth() / 2);
        int height = this.mBarParams.y + (this.mBarLayout.getHeight() / 2);
        if (this.mOrientation != 1) {
            int rotation = this.mWindowManager.getDefaultDisplay().getRotation();
            this.mLandRotation = rotation;
            if (rotation == 3) {
                i2 = getScreenWidth();
            } else {
                i = getScreenHeight();
                screenHeight = i - screenHeight;
                this.mBarParams.y = screenHeight - (this.mBarLayout.getHeight() / 2);
                this.mBarParams.x = (getScreenWidth() - height) - (this.mBarLayout.getWidth() / 2);
                adjustBarParams();
                this.mWindowManager.updateViewLayout(this.mBarLayout, this.mBarParams);
            }
        } else if (this.mLandRotation == 3) {
            i = getScreenHeight();
            screenHeight = i - screenHeight;
            this.mBarParams.y = screenHeight - (this.mBarLayout.getHeight() / 2);
            this.mBarParams.x = (getScreenWidth() - height) - (this.mBarLayout.getWidth() / 2);
            adjustBarParams();
            this.mWindowManager.updateViewLayout(this.mBarLayout, this.mBarParams);
        } else {
            i2 = getScreenWidth();
        }
        height = i2 - height;
        this.mBarParams.y = screenHeight - (this.mBarLayout.getHeight() / 2);
        this.mBarParams.x = (getScreenWidth() - height) - (this.mBarLayout.getWidth() / 2);
        adjustBarParams();
        this.mWindowManager.updateViewLayout(this.mBarLayout, this.mBarParams);
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /* access modifiers changed from: private */
    public boolean getRight() {
        Rect rect = new Rect();
        this.mBarLayout.getWindowDisplayFrame(rect);
        int i = rect.right - rect.left;
        boolean z = this.mIsCameraOn;
        if (((!this.mIsRight || !z) ? this.mBarParams : this.mCameraParams).x + ((this.mBarLayout.getWidth() + (z ? this.mCameraHeight : 0)) / 2) < i / 2) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void adjustBarParams() {
        int i;
        Rect rect = new Rect();
        this.mBarLayout.getWindowDisplayFrame(rect);
        int i2 = rect.bottom - rect.top;
        int i3 = rect.right - rect.left;
        boolean z = this.mIsCameraOn;
        int i4 = (!z || !this.mIsRight) ? 0 : this.mCameraLayoutHeight;
        if (!z || this.mIsRight) {
            i = this.mBarLayout.getWidth();
        } else {
            i3 -= this.mBarLayout.getWidth();
            i = this.mCameraLayoutHeight;
        }
        int i5 = i3 - i;
        WindowManager.LayoutParams layoutParams = this.mBarParams;
        if (layoutParams.x < i4) {
            layoutParams.x = i4;
        }
        if (layoutParams.x > i5) {
            layoutParams.x = i5;
        }
        if (layoutParams.y < 0) {
            layoutParams.y = 0;
        }
        if (layoutParams.y + this.mBarLayout.getHeight() > i2) {
            this.mBarParams.y = i2 - this.mBarLayout.getHeight();
        }
    }

    private int getMenuWindowLeft() {
        int i;
        int i2;
        Rect rect = new Rect();
        this.mBarLayout.getWindowDisplayFrame(rect);
        int i3 = rect.right - rect.left;
        int i4 = 2;
        if (this.mIsRight) {
            if (2 == this.mRecordingState) {
                i4 = 3;
            }
            i = (i3 - this.mBarParams.x) - (this.mIconSize * i4);
            i2 = this.mMenuLineWidth;
        } else {
            int i5 = 2 == this.mRecordingState ? 4 : 5;
            i = i3 - this.mBarParams.x;
            i2 = this.mIconSize * i5;
        }
        return i - i2;
    }

    private int getMenuWindowTop() {
        if (this.mBarParams.y + (this.mBarHeight / 2) > getScreenHeight() / 2) {
            return (this.mBarParams.y - this.mCameraPadding) - (this.mMenuLineHeight * 4);
        }
        return this.mBarParams.y + this.mBarHeight + this.mCameraPadding;
    }

    private int getIconNumber(boolean z) {
        boolean z2 = true;
        if (!z ? this.mRecordingStateView.getVisibility() != 0 : 2 != this.mRecordingState) {
            z2 = false;
        }
        if (!z2 || this.mIsPaused) {
            if (this.mCloseView.getVisibility() == 0) {
                return 5;
            }
            return 4;
        } else if (this.mCloseView.getVisibility() == 0) {
            return 6;
        } else {
            return 5;
        }
    }

    private void collapsedAnimation(final boolean z) {
        if (this.mBarLayout.isAttachedToWindow()) {
            this.mIsAnimation = true;
            final int iconNumber = this.mIconSize * getIconNumber(false);
            long iconNumber2 = 300 / ((long) getIconNumber(false));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator createChildrenScaleAnimator = createChildrenScaleAnimator(true, z, iconNumber2);
            Animator createStretchAnimator = this.mBarBg.createStretchAnimator(true, z, iconNumber);
            createStretchAnimator.setDuration(300);
            AnimatorSet createArrowAnimator = createArrowAnimator(this.mArrow, true, z, iconNumber);
            createArrowAnimator.setDuration(300);
            if (2 != this.mRecordingState || this.mIsPaused) {
                animatorSet.playTogether(new Animator[]{createChildrenScaleAnimator, createStretchAnimator, createArrowAnimator});
            } else {
                AnimatorSet createScaleAnimator = createScaleAnimator(this.mRecordingStateView, true, iconNumber2);
                createScaleAnimator.setStartDelay(300 - iconNumber2);
                animatorSet.playTogether(new Animator[]{createChildrenScaleAnimator, createStretchAnimator, createArrowAnimator, createScaleAnimator});
            }
            animatorSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    if (ScreenRecordPanel.this.mBarLayout.isAttachedToWindow() && !ScreenRecordPanel.this.mIsDestroy) {
                        ScreenRecordPanel.this.mWindowManager.removeView(ScreenRecordPanel.this.mBarLayout);
                        ScreenRecordPanel.this.mBarParams.width = -2;
                        ScreenRecordPanel.this.mBarParams.x += z ? 0 : iconNumber;
                        ScreenRecordPanel.this.mChildControllerView.setVisibility(8);
                        ScreenRecordPanel.this.resetBarLayout(true);
                        ScreenRecordPanel.this.mWindowManager.addView(ScreenRecordPanel.this.mBarLayout, ScreenRecordPanel.this.mBarParams);
                        ScreenRecordPanel.this.updateArrow();
                        ScreenRecordPanel.this.updateCameraPositionAsync();
                        if (2 == ScreenRecordPanel.this.mRecordingState) {
                            ScreenRecordPanel.this.mCloseView.setVisibility(8);
                            if (!ScreenRecordPanel.this.mIsPaused) {
                                ScreenRecordPanel.this.mRecordingStateView.setVisibility(8);
                            }
                        }
                        boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                    }
                }
            });
            animatorSet.start();
        }
    }

    private void collapsedAnimationForResume(final boolean z) {
        if (this.mBarLayout.isAttachedToWindow()) {
            this.mIsAnimation = true;
            final int i = this.mIconSize;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator createStretchAnimator = this.mBarBg.createStretchAnimator(true, z, i);
            createStretchAnimator.setDuration(300);
            AnimatorSet createArrowAnimatorForResume = createArrowAnimatorForResume(this.mArrow, true, z, i);
            createArrowAnimatorForResume.setDuration(300);
            animatorSet.playTogether(new Animator[]{createStretchAnimator, createArrowAnimatorForResume, createScaleAnimator(this.mRecordingStateView, true, 300)});
            animatorSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    if (ScreenRecordPanel.this.mBarLayout.isAttachedToWindow() && !ScreenRecordPanel.this.mIsDestroy) {
                        ScreenRecordPanel.this.mWindowManager.removeView(ScreenRecordPanel.this.mBarLayout);
                        ScreenRecordPanel.this.mBarParams.width = -2;
                        ScreenRecordPanel.this.mBarParams.x += z ? 0 : i;
                        ScreenRecordPanel.this.mRecordingStateView.setVisibility(8);
                        ScreenRecordPanel.this.resetBarLayout(true);
                        ScreenRecordPanel.this.mWindowManager.addView(ScreenRecordPanel.this.mBarLayout, ScreenRecordPanel.this.mBarParams);
                        ScreenRecordPanel.this.updateCameraPositionAsync();
                        boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                    }
                }
            });
            animatorSet.start();
        }
    }

    private void expandedAnimation(boolean z) {
        if (this.mBarLayout.isAttachedToWindow()) {
            this.mIsAnimation = true;
            int iconNumber = this.mIconSize * getIconNumber(true);
            this.mWindowManager.removeView(this.mBarLayout);
            if (z) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mControllerView.getLayoutParams();
                layoutParams.addRule(11);
                this.mControllerView.setLayoutParams(layoutParams);
            } else {
                this.mBarParams.x -= iconNumber;
            }
            this.mBarParams.width = this.mControllerView.getWidth() + iconNumber;
            this.mWindowManager.addView(this.mBarLayout, this.mBarParams);
            this.mBarLayout.post(new ScreenRecordPanel$$ExternalSyntheticLambda5(this, z, iconNumber));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$expandedAnimation$5(boolean z, final int i) {
        long iconNumber = 300 / ((long) getIconNumber(true));
        AnimatorSet animatorSet = new AnimatorSet();
        Animator createChildrenScaleAnimator = createChildrenScaleAnimator(false, z, iconNumber);
        Animator createStretchAnimator = this.mBarBg.createStretchAnimator(false, z, i);
        createStretchAnimator.setDuration(300);
        AnimatorSet createArrowAnimator = createArrowAnimator(this.mArrow, false, z, i);
        createArrowAnimator.setDuration(300);
        if (2 != this.mRecordingState || this.mIsPaused) {
            animatorSet.playTogether(new Animator[]{createChildrenScaleAnimator, createStretchAnimator, createArrowAnimator});
        } else {
            AnimatorSet createScaleAnimator = createScaleAnimator(this.mRecordingStateView, false, iconNumber);
            createChildrenScaleAnimator.setStartDelay(iconNumber);
            animatorSet.playTogether(new Animator[]{createChildrenScaleAnimator, createStretchAnimator, createArrowAnimator, createScaleAnimator});
        }
        animatorSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                if (2 == ScreenRecordPanel.this.mRecordingState) {
                    ScreenRecordPanel.this.mRecordingStateView.setVisibility(0);
                }
                ScreenRecordPanel.this.mChildControllerView.setVisibility(0);
                ScreenRecordPanel.this.mArrow.setTranslationX((float) i);
            }

            public void onAnimationEnd(Animator animator) {
                if (ScreenRecordPanel.this.mBarLayout.isAttachedToWindow() && !ScreenRecordPanel.this.mIsDestroy) {
                    ((RelativeLayout.LayoutParams) ScreenRecordPanel.this.mControllerView.getLayoutParams()).removeRule(11);
                    ScreenRecordPanel.this.resetBarLayout(false);
                    ScreenRecordPanel.this.mBarParams.width = -2;
                    ScreenRecordPanel.this.mWindowManager.updateViewLayout(ScreenRecordPanel.this.mBarLayout, ScreenRecordPanel.this.mBarParams);
                    ScreenRecordPanel.this.updateArrow();
                    ScreenRecordPanel.this.updateCameraPositionAsync();
                    ScreenRecordPanel.this.postCollapse();
                    boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                }
            }
        });
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public void closeToSideAnimation() {
        this.mIsAnimation = true;
        int i = 0;
        final int i2 = this.mBarParams.x - ((!this.mIsCameraOn || !this.mIsRight) ? 0 : this.mCameraLayoutHeight);
        Rect rect = new Rect();
        this.mBarLayout.getWindowDisplayFrame(rect);
        int width = ((rect.right - rect.left) - i2) - this.mBarLayout.getWidth();
        if (this.mIsCameraOn) {
            i = this.mCameraLayoutHeight;
        }
        final int i3 = width - i;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        ofFloat.setDuration(300);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int i;
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                WindowManager.LayoutParams access$1700 = ScreenRecordPanel.this.mBarParams;
                if (ScreenRecordPanel.this.mIsRight) {
                    i = ((int) (((float) i2) * floatValue)) + (ScreenRecordPanel.this.mIsCameraOn ? ScreenRecordPanel.this.mCameraLayoutHeight : 0);
                } else {
                    i = (int) (((float) i2) + (((float) i3) * (1.0f - floatValue)));
                }
                access$1700.x = i;
                if (ScreenRecordPanel.this.mBarLayout.isAttachedToWindow()) {
                    ScreenRecordPanel.this.mWindowManager.updateViewLayout(ScreenRecordPanel.this.mBarLayout, ScreenRecordPanel.this.mBarParams);
                    ScreenRecordPanel.this.updateCameraPosition(true, false);
                }
            }
        });
        ofFloat.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                ScreenRecordPanel.this.postCollapse();
            }

            public void onAnimationCancel(Animator animator) {
                boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                ScreenRecordPanel.this.postCollapse();
            }
        });
        ofFloat.start();
    }

    private void farAwaySideAnimation() {
        this.mIsAnimation = true;
        final int i = this.mBarParams.x;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(300);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int i;
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                WindowManager.LayoutParams access$1700 = ScreenRecordPanel.this.mBarParams;
                if (ScreenRecordPanel.this.mIsRight) {
                    i = i + ((int) (((float) ScreenRecordPanel.this.mCameraLayoutHeight) * floatValue));
                } else {
                    i = i - ((int) (((float) ScreenRecordPanel.this.mCameraLayoutHeight) * floatValue));
                }
                access$1700.x = i;
                if (ScreenRecordPanel.this.mBarLayout.isAttachedToWindow()) {
                    ScreenRecordPanel.this.mWindowManager.updateViewLayout(ScreenRecordPanel.this.mBarLayout, ScreenRecordPanel.this.mBarParams);
                }
            }
        });
        ofFloat.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                ScreenRecordPanel.this.postCollapse();
            }

            public void onAnimationCancel(Animator animator) {
                boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                ScreenRecordPanel.this.postCollapse();
            }
        });
        ofFloat.start();
    }

    private void startRecordingAimation() {
        this.mIsAnimation = true;
        int i = this.mBarParams.x;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(600);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ScreenRecordPanel.this.mCloseView.getLayoutParams();
                layoutParams.width = (int) (((float) ScreenRecordPanel.this.mIconSize) * (1.0f - floatValue));
                ScreenRecordPanel.this.mCloseView.setLayoutParams(layoutParams);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) ScreenRecordPanel.this.mRecordingStateView.getLayoutParams();
                layoutParams2.width = (int) (((float) ScreenRecordPanel.this.mIconSize) * floatValue);
                ScreenRecordPanel.this.mRecordingStateView.setLayoutParams(layoutParams2);
            }
        });
        ofFloat.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                ScreenRecordPanel.this.mRecordingStateView.setVisibility(0);
            }

            public void onAnimationEnd(Animator animator) {
                boolean unused = ScreenRecordPanel.this.mIsAnimation = false;
                ScreenRecordPanel.this.mCloseView.setVisibility(8);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ScreenRecordPanel.this.mCloseView.getLayoutParams();
                layoutParams.width = ScreenRecordPanel.this.mIconSize;
                ScreenRecordPanel.this.mCloseView.setLayoutParams(layoutParams);
                ScreenRecordPanel.this.postCollapse();
            }
        });
        ofFloat.start();
    }

    private Animator createTransitionAnimator(View view, boolean z, boolean z2, int i) {
        float f;
        Property property = View.TRANSLATION_X;
        float[] fArr = new float[2];
        float f2 = 0.0f;
        if (z) {
            f = 0.0f;
        } else {
            f = (float) (z2 ? i : -i);
        }
        fArr[0] = f;
        if (z) {
            if (!z2) {
                i = -i;
            }
            f2 = (float) i;
        }
        fArr[1] = f2;
        return ObjectAnimator.ofFloat(view, property, fArr);
    }

    private Animator createChildrenScaleAnimator(boolean z, boolean z2, long j) {
        AnimatorSet animatorSet = new AnimatorSet();
        int childCount = this.mChildControllerView.getChildCount();
        ArrayList arrayList = new ArrayList(childCount);
        for (int i = 0; i < childCount; i++) {
            if (this.mChildControllerView.getChildAt(i).getVisibility() != 8) {
                arrayList.add(createScaleAnimator(this.mChildControllerView.getChildAt(i), z, j));
            }
        }
        if ((!z && z2) || (z && !z2)) {
            Collections.reverse(arrayList);
        }
        animatorSet.playSequentially(arrayList);
        return animatorSet;
    }

    private AnimatorSet createScaleAnimator(View view, boolean z, long j) {
        AnimatorSet animatorSet = new AnimatorSet();
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        fArr[1] = z ? 0.0f : 1.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, property, fArr);
        ofFloat.setDuration(j);
        animatorSet.play(ofFloat);
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        fArr2[0] = z ? 1.0f : 0.0f;
        if (z) {
            f = 0.0f;
        }
        fArr2[1] = f;
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, property2, fArr2);
        ofFloat2.setDuration(j);
        animatorSet.play(ofFloat2);
        return animatorSet;
    }

    private AnimatorSet createArrowAnimator(View view, boolean z, boolean z2, int i) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(view, "rotation", new float[]{0.0f, 180.0f}));
        animatorSet.play(createTransitionAnimator(view, z, z2, i));
        return animatorSet;
    }

    private AnimatorSet createArrowAnimatorForResume(View view, boolean z, boolean z2, int i) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(createTransitionAnimator(view, z, z2, i));
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public void resetBarLayout(boolean z) {
        this.mArrow.setRotation(0.0f);
        this.mArrow.setTranslationX(0.0f);
        int childCount = this.mChildControllerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.mChildControllerView.getChildAt(i).getVisibility() != 8) {
                float f = 1.0f;
                this.mChildControllerView.getChildAt(i).setScaleX(z ? 0.0f : 1.0f);
                View childAt = this.mChildControllerView.getChildAt(i);
                if (z) {
                    f = 0.0f;
                }
                childAt.setScaleY(f);
            }
        }
        this.mBarBg.reset();
    }

    private void setImageResource(ImageView imageView, int i, int i2) {
        imageView.setImageResource(i);
        imageView.setContentDescription(this.mContext.getText(i2));
    }
}
