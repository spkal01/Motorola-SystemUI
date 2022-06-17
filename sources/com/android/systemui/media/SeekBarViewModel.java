package com.android.systemui.media;

import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.SeekBar;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.systemui.util.concurrency.RepeatableExecutor;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: SeekBarViewModel.kt */
public final class SeekBarViewModel {
    /* access modifiers changed from: private */
    @NotNull
    public Progress _data = new Progress(false, false, (Integer) null, 0);
    @NotNull
    private final MutableLiveData<Progress> _progress;
    @NotNull
    private final RepeatableExecutor bgExecutor;
    @NotNull
    private SeekBarViewModel$callback$1 callback;
    /* access modifiers changed from: private */
    @Nullable
    public Runnable cancel;
    /* access modifiers changed from: private */
    @Nullable
    public MediaController controller;
    /* access modifiers changed from: private */
    public boolean isFalseSeek;
    /* access modifiers changed from: private */
    public boolean listening;
    public Function0<Unit> logSmartspaceClick;
    /* access modifiers changed from: private */
    @Nullable
    public PlaybackState playbackState;
    /* access modifiers changed from: private */
    public boolean scrubbing;

    public SeekBarViewModel(@NotNull RepeatableExecutor repeatableExecutor) {
        Intrinsics.checkNotNullParameter(repeatableExecutor, "bgExecutor");
        this.bgExecutor = repeatableExecutor;
        MutableLiveData<Progress> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.postValue(this._data);
        Unit unit = Unit.INSTANCE;
        this._progress = mutableLiveData;
        this.callback = new SeekBarViewModel$callback$1(this);
        this.listening = true;
    }

    /* access modifiers changed from: private */
    public final void set_data(Progress progress) {
        this._data = progress;
        this._progress.postValue(progress);
    }

    @NotNull
    public final LiveData<Progress> getProgress() {
        return this._progress;
    }

    /* access modifiers changed from: private */
    public final void setController(MediaController mediaController) {
        MediaController mediaController2 = this.controller;
        MediaSession.Token token = null;
        MediaSession.Token sessionToken = mediaController2 == null ? null : mediaController2.getSessionToken();
        if (mediaController != null) {
            token = mediaController.getSessionToken();
        }
        if (!Intrinsics.areEqual((Object) sessionToken, (Object) token)) {
            MediaController mediaController3 = this.controller;
            if (mediaController3 != null) {
                mediaController3.unregisterCallback(this.callback);
            }
            if (mediaController != null) {
                mediaController.registerCallback(this.callback);
            }
            this.controller = mediaController;
        }
    }

    public final void setListening(boolean z) {
        this.bgExecutor.execute(new SeekBarViewModel$listening$1(this, z));
    }

    /* access modifiers changed from: private */
    public final void setScrubbing(boolean z) {
        if (this.scrubbing != z) {
            this.scrubbing = z;
            checkIfPollingNeeded();
        }
    }

    @NotNull
    public final Function0<Unit> getLogSmartspaceClick() {
        Function0<Unit> function0 = this.logSmartspaceClick;
        if (function0 != null) {
            return function0;
        }
        Intrinsics.throwUninitializedPropertyAccessException("logSmartspaceClick");
        throw null;
    }

    public final void setLogSmartspaceClick(@NotNull Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(function0, "<set-?>");
        this.logSmartspaceClick = function0;
    }

    public final void onSeekStarting() {
        this.bgExecutor.execute(new SeekBarViewModel$onSeekStarting$1(this));
    }

    public final void onSeekProgress(long j) {
        this.bgExecutor.execute(new SeekBarViewModel$onSeekProgress$1(this, j));
    }

    public final void onSeekFalse() {
        this.bgExecutor.execute(new SeekBarViewModel$onSeekFalse$1(this));
    }

    public final void onSeek(long j) {
        this.bgExecutor.execute(new SeekBarViewModel$onSeek$1(this, j));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006e, code lost:
        if (r0.intValue() != 0) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0070, code lost:
        if (r9 > 0) goto L_0x0073;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void updateController(@org.jetbrains.annotations.Nullable android.media.session.MediaController r9) {
        /*
            r8 = this;
            r8.setController(r9)
            android.media.session.MediaController r9 = r8.controller
            r0 = 0
            if (r9 != 0) goto L_0x000a
            r9 = r0
            goto L_0x000e
        L_0x000a:
            android.media.session.PlaybackState r9 = r9.getPlaybackState()
        L_0x000e:
            r8.playbackState = r9
            android.media.session.MediaController r9 = r8.controller
            if (r9 != 0) goto L_0x0016
            r9 = r0
            goto L_0x001a
        L_0x0016:
            android.media.MediaMetadata r9 = r9.getMetadata()
        L_0x001a:
            android.media.session.PlaybackState r1 = r8.playbackState
            r2 = 0
            if (r1 != 0) goto L_0x0022
            r4 = r2
            goto L_0x0026
        L_0x0022:
            long r4 = r1.getActions()
        L_0x0026:
            r6 = 256(0x100, double:1.265E-321)
            long r4 = r4 & r6
            int r1 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0031
            r1 = r2
            goto L_0x0032
        L_0x0031:
            r1 = r3
        L_0x0032:
            android.media.session.PlaybackState r4 = r8.playbackState
            if (r4 != 0) goto L_0x0038
            r4 = r0
            goto L_0x0041
        L_0x0038:
            long r4 = r4.getPosition()
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
        L_0x0041:
            if (r9 != 0) goto L_0x0045
            r9 = r0
            goto L_0x004f
        L_0x0045:
            java.lang.String r5 = "android.media.metadata.DURATION"
            long r5 = r9.getLong(r5)
            java.lang.Long r9 = java.lang.Long.valueOf(r5)
        L_0x004f:
            if (r9 != 0) goto L_0x0053
            r9 = r3
            goto L_0x0058
        L_0x0053:
            long r5 = r9.longValue()
            int r9 = (int) r5
        L_0x0058:
            android.media.session.PlaybackState r5 = r8.playbackState
            if (r5 == 0) goto L_0x0072
            if (r5 != 0) goto L_0x005f
            goto L_0x0067
        L_0x005f:
            int r0 = r5.getState()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
        L_0x0067:
            if (r0 != 0) goto L_0x006a
            goto L_0x0070
        L_0x006a:
            int r0 = r0.intValue()
            if (r0 == 0) goto L_0x0072
        L_0x0070:
            if (r9 > 0) goto L_0x0073
        L_0x0072:
            r2 = r3
        L_0x0073:
            com.android.systemui.media.SeekBarViewModel$Progress r0 = new com.android.systemui.media.SeekBarViewModel$Progress
            r0.<init>(r2, r1, r4, r9)
            r8.set_data(r0)
            r8.checkIfPollingNeeded()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.media.SeekBarViewModel.updateController(android.media.session.MediaController):void");
    }

    public final void clearController() {
        this.bgExecutor.execute(new SeekBarViewModel$clearController$1(this));
    }

    public final void onDestroy() {
        this.bgExecutor.execute(new SeekBarViewModel$onDestroy$1(this));
    }

    /* access modifiers changed from: private */
    public final void checkPlaybackPosition() {
        int duration = this._data.getDuration();
        PlaybackState playbackState2 = this.playbackState;
        Integer valueOf = playbackState2 == null ? null : Integer.valueOf((int) SeekBarViewModelKt.computePosition(playbackState2, (long) duration));
        if (valueOf != null && !Intrinsics.areEqual((Object) this._data.getElapsedTime(), (Object) valueOf)) {
            set_data(Progress.copy$default(this._data, false, false, valueOf, 0, 11, (Object) null));
        }
    }

    /* access modifiers changed from: private */
    public final void checkIfPollingNeeded() {
        boolean z = false;
        if (this.listening && !this.scrubbing) {
            PlaybackState playbackState2 = this.playbackState;
            if (playbackState2 == null ? false : SeekBarViewModelKt.isInMotion(playbackState2)) {
                z = true;
            }
        }
        if (!z) {
            Runnable runnable = this.cancel;
            if (runnable != null) {
                runnable.run();
            }
            this.cancel = null;
        } else if (this.cancel == null) {
            this.cancel = this.bgExecutor.executeRepeatedly(new SeekBarViewModel$checkIfPollingNeeded$1(this), 0, 100);
        }
    }

    @NotNull
    public final SeekBar.OnSeekBarChangeListener getSeekBarListener() {
        return new SeekBarChangeListener(this);
    }

    public final void attachTouchHandlers(@NotNull SeekBar seekBar) {
        Intrinsics.checkNotNullParameter(seekBar, "bar");
        seekBar.setOnSeekBarChangeListener(getSeekBarListener());
        seekBar.setOnTouchListener(new SeekBarTouchListener(this, seekBar));
    }

    /* compiled from: SeekBarViewModel.kt */
    private static final class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @NotNull
        private final SeekBarViewModel viewModel;

        public SeekBarChangeListener(@NotNull SeekBarViewModel seekBarViewModel) {
            Intrinsics.checkNotNullParameter(seekBarViewModel, "viewModel");
            this.viewModel = seekBarViewModel;
        }

        public void onProgressChanged(@NotNull SeekBar seekBar, int i, boolean z) {
            Intrinsics.checkNotNullParameter(seekBar, "bar");
            if (z) {
                this.viewModel.onSeekProgress((long) i);
            }
        }

        public void onStartTrackingTouch(@NotNull SeekBar seekBar) {
            Intrinsics.checkNotNullParameter(seekBar, "bar");
            this.viewModel.onSeekStarting();
        }

        public void onStopTrackingTouch(@NotNull SeekBar seekBar) {
            Intrinsics.checkNotNullParameter(seekBar, "bar");
            this.viewModel.onSeek((long) seekBar.getProgress());
        }
    }

    /* compiled from: SeekBarViewModel.kt */
    private static final class SeekBarTouchListener implements View.OnTouchListener, GestureDetector.OnGestureListener {
        @NotNull
        private final SeekBar bar;
        @NotNull
        private final GestureDetectorCompat detector;
        private final int flingVelocity;
        private boolean shouldGoToSeekBar;
        @NotNull
        private final SeekBarViewModel viewModel;

        public void onLongPress(@NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(motionEvent, "event");
        }

        public void onShowPress(@NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(motionEvent, "event");
        }

        public SeekBarTouchListener(@NotNull SeekBarViewModel seekBarViewModel, @NotNull SeekBar seekBar) {
            Intrinsics.checkNotNullParameter(seekBarViewModel, "viewModel");
            Intrinsics.checkNotNullParameter(seekBar, "bar");
            this.viewModel = seekBarViewModel;
            this.bar = seekBar;
            this.detector = new GestureDetectorCompat(seekBar.getContext(), this);
            this.flingVelocity = ViewConfiguration.get(seekBar.getContext()).getScaledMinimumFlingVelocity() * 10;
        }

        public boolean onTouch(@NotNull View view, @NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(view, "view");
            Intrinsics.checkNotNullParameter(motionEvent, "event");
            if (!Intrinsics.areEqual((Object) view, (Object) this.bar)) {
                return false;
            }
            this.detector.onTouchEvent(motionEvent);
            return !this.shouldGoToSeekBar;
        }

        public boolean onDown(@NotNull MotionEvent motionEvent) {
            double d;
            double d2;
            ViewParent parent;
            Intrinsics.checkNotNullParameter(motionEvent, "event");
            int paddingLeft = this.bar.getPaddingLeft();
            int paddingRight = this.bar.getPaddingRight();
            int progress = this.bar.getProgress();
            int max = this.bar.getMax() - this.bar.getMin();
            double min = max > 0 ? ((double) (progress - this.bar.getMin())) / ((double) max) : 0.0d;
            int width = (this.bar.getWidth() - paddingLeft) - paddingRight;
            if (this.bar.isLayoutRtl()) {
                d2 = (double) paddingLeft;
                d = ((double) width) * (((double) 1) - min);
            } else {
                d2 = (double) paddingLeft;
                d = ((double) width) * min;
            }
            double d3 = d2 + d;
            long height = (long) (this.bar.getHeight() / 2);
            int round = (int) (Math.round(d3) - height);
            int round2 = (int) (Math.round(d3) + height);
            int round3 = Math.round(motionEvent.getX());
            boolean z = round3 >= round && round3 <= round2;
            this.shouldGoToSeekBar = z;
            if (z && (parent = this.bar.getParent()) != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            return this.shouldGoToSeekBar;
        }

        public boolean onSingleTapUp(@NotNull MotionEvent motionEvent) {
            Intrinsics.checkNotNullParameter(motionEvent, "event");
            this.shouldGoToSeekBar = true;
            return true;
        }

        public boolean onScroll(@NotNull MotionEvent motionEvent, @NotNull MotionEvent motionEvent2, float f, float f2) {
            Intrinsics.checkNotNullParameter(motionEvent, "eventStart");
            Intrinsics.checkNotNullParameter(motionEvent2, "event");
            return this.shouldGoToSeekBar;
        }

        public boolean onFling(@NotNull MotionEvent motionEvent, @NotNull MotionEvent motionEvent2, float f, float f2) {
            Intrinsics.checkNotNullParameter(motionEvent, "eventStart");
            Intrinsics.checkNotNullParameter(motionEvent2, "event");
            if (Math.abs(f) > ((float) this.flingVelocity) || Math.abs(f2) > ((float) this.flingVelocity)) {
                this.viewModel.onSeekFalse();
            }
            return this.shouldGoToSeekBar;
        }
    }

    /* compiled from: SeekBarViewModel.kt */
    public static final class Progress {
        private final int duration;
        @Nullable
        private final Integer elapsedTime;
        private final boolean enabled;
        private final boolean seekAvailable;

        public static /* synthetic */ Progress copy$default(Progress progress, boolean z, boolean z2, Integer num, int i, int i2, Object obj) {
            if ((i2 & 1) != 0) {
                z = progress.enabled;
            }
            if ((i2 & 2) != 0) {
                z2 = progress.seekAvailable;
            }
            if ((i2 & 4) != 0) {
                num = progress.elapsedTime;
            }
            if ((i2 & 8) != 0) {
                i = progress.duration;
            }
            return progress.copy(z, z2, num, i);
        }

        @NotNull
        public final Progress copy(boolean z, boolean z2, @Nullable Integer num, int i) {
            return new Progress(z, z2, num, i);
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Progress)) {
                return false;
            }
            Progress progress = (Progress) obj;
            return this.enabled == progress.enabled && this.seekAvailable == progress.seekAvailable && Intrinsics.areEqual((Object) this.elapsedTime, (Object) progress.elapsedTime) && this.duration == progress.duration;
        }

        public int hashCode() {
            boolean z = this.enabled;
            boolean z2 = true;
            if (z) {
                z = true;
            }
            int i = (z ? 1 : 0) * true;
            boolean z3 = this.seekAvailable;
            if (!z3) {
                z2 = z3;
            }
            int i2 = (i + (z2 ? 1 : 0)) * 31;
            Integer num = this.elapsedTime;
            return ((i2 + (num == null ? 0 : num.hashCode())) * 31) + Integer.hashCode(this.duration);
        }

        @NotNull
        public String toString() {
            return "Progress(enabled=" + this.enabled + ", seekAvailable=" + this.seekAvailable + ", elapsedTime=" + this.elapsedTime + ", duration=" + this.duration + ')';
        }

        public Progress(boolean z, boolean z2, @Nullable Integer num, int i) {
            this.enabled = z;
            this.seekAvailable = z2;
            this.elapsedTime = num;
            this.duration = i;
        }

        public final boolean getEnabled() {
            return this.enabled;
        }

        public final boolean getSeekAvailable() {
            return this.seekAvailable;
        }

        @Nullable
        public final Integer getElapsedTime() {
            return this.elapsedTime;
        }

        public final int getDuration() {
            return this.duration;
        }
    }
}
