package com.android.systemui.media;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.util.animation.TransitionLayout;
import java.util.Objects;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: PlayerViewHolder.kt */
public final class PlayerViewHolder {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public static final Set<Integer> controlsIds;
    /* access modifiers changed from: private */
    @NotNull
    public static final Set<Integer> gutsIds = SetsKt__SetsKt.setOf(Integer.valueOf(R$id.remove_text), Integer.valueOf(R$id.cancel), Integer.valueOf(R$id.dismiss), Integer.valueOf(R$id.settings));
    private final ImageButton action0;
    private final ImageButton action1;
    private final ImageButton action2;
    private final ImageButton action3;
    private final ImageButton action4;
    private final ImageView albumView;
    private final ImageView appIcon;
    private final TextView artistText;
    private final View cancel;
    private final ViewGroup dismiss;
    private final View dismissLabel;
    private final TextView elapsedTimeView;
    private final TextView longPressText;
    @NotNull
    private final TransitionLayout player;
    private final ViewGroup progressTimes;
    private final ViewGroup seamless;
    private final ImageView seamlessFallback;
    private final ImageView seamlessIcon;
    private final TextView seamlessText;
    private final SeekBar seekBar;
    private final View settings;
    private final TextView settingsText;
    private final TextView titleText;
    private final TextView totalTimeView;

    public /* synthetic */ PlayerViewHolder(View view, DefaultConstructorMarker defaultConstructorMarker) {
        this(view);
    }

    private PlayerViewHolder(View view) {
        TransitionLayout transitionLayout = (TransitionLayout) view;
        this.player = transitionLayout;
        this.appIcon = (ImageView) view.requireViewById(R$id.icon);
        this.albumView = (ImageView) view.requireViewById(R$id.album_art);
        this.titleText = (TextView) view.requireViewById(R$id.header_title);
        this.artistText = (TextView) view.requireViewById(R$id.header_artist);
        this.seamless = (ViewGroup) view.requireViewById(R$id.media_seamless);
        this.seamlessIcon = (ImageView) view.requireViewById(R$id.media_seamless_image);
        this.seamlessText = (TextView) view.requireViewById(R$id.media_seamless_text);
        this.seamlessFallback = (ImageView) view.requireViewById(R$id.media_seamless_fallback);
        this.seekBar = (SeekBar) view.requireViewById(R$id.media_progress_bar);
        this.progressTimes = (ViewGroup) view.requireViewById(R$id.notification_media_progress_time);
        this.elapsedTimeView = (TextView) view.requireViewById(R$id.media_elapsed_time);
        this.totalTimeView = (TextView) view.requireViewById(R$id.media_total_time);
        this.action0 = (ImageButton) view.requireViewById(R$id.action0);
        this.action1 = (ImageButton) view.requireViewById(R$id.action1);
        this.action2 = (ImageButton) view.requireViewById(R$id.action2);
        this.action3 = (ImageButton) view.requireViewById(R$id.action3);
        this.action4 = (ImageButton) view.requireViewById(R$id.action4);
        this.longPressText = (TextView) view.requireViewById(R$id.remove_text);
        this.cancel = view.requireViewById(R$id.cancel);
        ViewGroup viewGroup = (ViewGroup) view.requireViewById(R$id.dismiss);
        this.dismiss = viewGroup;
        this.dismissLabel = viewGroup.getChildAt(0);
        this.settings = view.requireViewById(R$id.settings);
        this.settingsText = (TextView) view.requireViewById(R$id.settings_text);
        Drawable background = transitionLayout.getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type com.android.systemui.media.IlluminationDrawable");
        IlluminationDrawable illuminationDrawable = (IlluminationDrawable) background;
        ViewGroup seamless2 = getSeamless();
        Intrinsics.checkNotNullExpressionValue(seamless2, "seamless");
        illuminationDrawable.registerLightSource((View) seamless2);
        ImageButton action02 = getAction0();
        Intrinsics.checkNotNullExpressionValue(action02, "action0");
        illuminationDrawable.registerLightSource((View) action02);
        ImageButton action12 = getAction1();
        Intrinsics.checkNotNullExpressionValue(action12, "action1");
        illuminationDrawable.registerLightSource((View) action12);
        ImageButton action22 = getAction2();
        Intrinsics.checkNotNullExpressionValue(action22, "action2");
        illuminationDrawable.registerLightSource((View) action22);
        ImageButton action32 = getAction3();
        Intrinsics.checkNotNullExpressionValue(action32, "action3");
        illuminationDrawable.registerLightSource((View) action32);
        ImageButton action42 = getAction4();
        Intrinsics.checkNotNullExpressionValue(action42, "action4");
        illuminationDrawable.registerLightSource((View) action42);
        View cancel2 = getCancel();
        Intrinsics.checkNotNullExpressionValue(cancel2, "cancel");
        illuminationDrawable.registerLightSource(cancel2);
        ViewGroup dismiss2 = getDismiss();
        Intrinsics.checkNotNullExpressionValue(dismiss2, "dismiss");
        illuminationDrawable.registerLightSource((View) dismiss2);
        View settings2 = getSettings();
        Intrinsics.checkNotNullExpressionValue(settings2, "settings");
        illuminationDrawable.registerLightSource(settings2);
    }

    @NotNull
    public final TransitionLayout getPlayer() {
        return this.player;
    }

    public final ImageView getAppIcon() {
        return this.appIcon;
    }

    public final ImageView getAlbumView() {
        return this.albumView;
    }

    public final TextView getTitleText() {
        return this.titleText;
    }

    public final TextView getArtistText() {
        return this.artistText;
    }

    public final ViewGroup getSeamless() {
        return this.seamless;
    }

    public final ImageView getSeamlessIcon() {
        return this.seamlessIcon;
    }

    public final TextView getSeamlessText() {
        return this.seamlessText;
    }

    public final ImageView getSeamlessFallback() {
        return this.seamlessFallback;
    }

    public final SeekBar getSeekBar() {
        return this.seekBar;
    }

    public final ViewGroup getProgressTimes() {
        return this.progressTimes;
    }

    public final TextView getElapsedTimeView() {
        return this.elapsedTimeView;
    }

    public final TextView getTotalTimeView() {
        return this.totalTimeView;
    }

    public final ImageButton getAction0() {
        return this.action0;
    }

    public final ImageButton getAction1() {
        return this.action1;
    }

    public final ImageButton getAction2() {
        return this.action2;
    }

    public final ImageButton getAction3() {
        return this.action3;
    }

    public final ImageButton getAction4() {
        return this.action4;
    }

    public final TextView getLongPressText() {
        return this.longPressText;
    }

    public final View getCancel() {
        return this.cancel;
    }

    public final ViewGroup getDismiss() {
        return this.dismiss;
    }

    public final View getDismissLabel() {
        return this.dismissLabel;
    }

    public final View getSettings() {
        return this.settings;
    }

    public final TextView getSettingsText() {
        return this.settingsText;
    }

    @NotNull
    public final ImageButton getAction(int i) {
        if (i == R$id.action0) {
            ImageButton imageButton = this.action0;
            Intrinsics.checkNotNullExpressionValue(imageButton, "action0");
            return imageButton;
        } else if (i == R$id.action1) {
            ImageButton imageButton2 = this.action1;
            Intrinsics.checkNotNullExpressionValue(imageButton2, "action1");
            return imageButton2;
        } else if (i == R$id.action2) {
            ImageButton imageButton3 = this.action2;
            Intrinsics.checkNotNullExpressionValue(imageButton3, "action2");
            return imageButton3;
        } else if (i == R$id.action3) {
            ImageButton imageButton4 = this.action3;
            Intrinsics.checkNotNullExpressionValue(imageButton4, "action3");
            return imageButton4;
        } else if (i == R$id.action4) {
            ImageButton imageButton5 = this.action4;
            Intrinsics.checkNotNullExpressionValue(imageButton5, "action4");
            return imageButton5;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public final void marquee(boolean z, long j) {
        this.longPressText.getHandler().postDelayed(new PlayerViewHolder$marquee$1(this, z), j);
    }

    /* compiled from: PlayerViewHolder.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final PlayerViewHolder create(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup viewGroup) {
            Intrinsics.checkNotNullParameter(layoutInflater, "inflater");
            Intrinsics.checkNotNullParameter(viewGroup, "parent");
            View inflate = layoutInflater.inflate(R$layout.media_view, viewGroup, false);
            inflate.setLayoutDirection(3);
            Intrinsics.checkNotNullExpressionValue(inflate, "mediaView");
            PlayerViewHolder playerViewHolder = new PlayerViewHolder(inflate, (DefaultConstructorMarker) null);
            playerViewHolder.getSeekBar().setLayoutDirection(0);
            playerViewHolder.getProgressTimes().setLayoutDirection(0);
            return playerViewHolder;
        }

        @NotNull
        public final Set<Integer> getControlsIds() {
            return PlayerViewHolder.controlsIds;
        }

        @NotNull
        public final Set<Integer> getGutsIds() {
            return PlayerViewHolder.gutsIds;
        }
    }

    static {
        int i = R$id.icon;
        controlsIds = SetsKt__SetsKt.setOf(Integer.valueOf(i), Integer.valueOf(R$id.app_name), Integer.valueOf(R$id.album_art), Integer.valueOf(R$id.header_title), Integer.valueOf(R$id.header_artist), Integer.valueOf(R$id.media_seamless), Integer.valueOf(R$id.media_seamless_fallback), Integer.valueOf(R$id.notification_media_progress_time), Integer.valueOf(R$id.media_progress_bar), Integer.valueOf(R$id.action0), Integer.valueOf(R$id.action1), Integer.valueOf(R$id.action2), Integer.valueOf(R$id.action3), Integer.valueOf(R$id.action4), Integer.valueOf(i));
    }
}
