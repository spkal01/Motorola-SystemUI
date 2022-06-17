package com.android.systemui.media;

import android.app.PendingIntent;
import android.app.smartspace.SmartspaceAction;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.util.Log;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.systemui.Dumpable;
import com.android.systemui.R$dimen;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.classifier.FalsingCollector;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.media.MediaDataManager;
import com.android.systemui.media.MediaHostStatesManager;
import com.android.systemui.media.MediaPlayerData;
import com.android.systemui.media.PlayerViewHolder;
import com.android.systemui.media.RecommendationViewHolder;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.PageIndicator;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.notification.collection.legacy.VisualStabilityManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.Utils;
import com.android.systemui.util.animation.MeasurementInput;
import com.android.systemui.util.animation.TransitionLayout;
import com.android.systemui.util.animation.UniqueObjectHostView;
import com.android.systemui.util.animation.UniqueObjectHostViewKt;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.time.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Provider;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: MediaCarouselController.kt */
public final class MediaCarouselController implements Dumpable {
    @NotNull
    private MediaPlayerData MediaPlayerData;
    /* access modifiers changed from: private */
    @NotNull
    public final ActivityStarter activityStarter;
    private int bgColor = getBackgroundColor();
    private int carouselMeasureHeight;
    private int carouselMeasureWidth;
    @NotNull
    private final MediaCarouselController$configListener$1 configListener;
    /* access modifiers changed from: private */
    @NotNull
    public final Context context;
    private int currentCarouselHeight;
    private int currentCarouselWidth;
    private int currentEndLocation = -1;
    private int currentStartLocation = -1;
    private float currentTransitionProgress = 1.0f;
    private boolean currentlyExpanded = true;
    private boolean currentlyShowingOnlyActive;
    @Nullable
    private MediaHostState desiredHostState;
    /* access modifiers changed from: private */
    public int desiredLocation = -1;
    private boolean isRtl;
    /* access modifiers changed from: private */
    @NotNull
    public Set<String> keysNeedRemoval = new LinkedHashSet();
    @NotNull
    private List<RemovedCallback> mRemovedCallbackList;
    @NotNull
    private final MediaScrollView mediaCarousel;
    @NotNull
    private final MediaCarouselScrollHandler mediaCarouselScrollHandler;
    @NotNull
    private final ViewGroup mediaContent;
    @NotNull
    private final Provider<MediaControlPanel> mediaControlPanelFactory;
    @NotNull
    private final ViewGroup mediaFrame;
    @NotNull
    private final MediaHostStatesManager mediaHostStatesManager;
    @NotNull
    private final MediaDataManager mediaManager;
    /* access modifiers changed from: private */
    public boolean needsReordering;
    @NotNull
    private final PageIndicator pageIndicator;
    private boolean playersVisible;
    private View settingsButton;
    private boolean shouldScrollToActivePlayer;
    @NotNull
    private final SystemClock systemClock;
    public Function0<Unit> updateUserVisibility;
    @NotNull
    private final VisualStabilityManager.Callback visualStabilityCallback;
    /* access modifiers changed from: private */
    @NotNull
    public final VisualStabilityManager visualStabilityManager;

    /* compiled from: MediaCarouselController.kt */
    public interface RemovedCallback {
        void onMediaRemoved(@NotNull String str);

        void syncMediaActiveState(@NotNull String str, boolean z);
    }

    public final void logSmartspaceCardReported(int i, int i2, boolean z, int i3) {
        logSmartspaceCardReported$default(this, i, i2, z, i3, 0, 16, (Object) null);
    }

    public MediaCarouselController(@NotNull Context context2, @NotNull Provider<MediaControlPanel> provider, @NotNull VisualStabilityManager visualStabilityManager2, @NotNull MediaHostStatesManager mediaHostStatesManager2, @NotNull ActivityStarter activityStarter2, @NotNull SystemClock systemClock2, @NotNull DelayableExecutor delayableExecutor, @NotNull MediaDataManager mediaDataManager, @NotNull ConfigurationController configurationController, @NotNull FalsingCollector falsingCollector, @NotNull FalsingManager falsingManager, @NotNull DumpManager dumpManager) {
        Context context3 = context2;
        Provider<MediaControlPanel> provider2 = provider;
        VisualStabilityManager visualStabilityManager3 = visualStabilityManager2;
        MediaHostStatesManager mediaHostStatesManager3 = mediaHostStatesManager2;
        ActivityStarter activityStarter3 = activityStarter2;
        SystemClock systemClock3 = systemClock2;
        MediaDataManager mediaDataManager2 = mediaDataManager;
        ConfigurationController configurationController2 = configurationController;
        DumpManager dumpManager2 = dumpManager;
        Intrinsics.checkNotNullParameter(context3, "context");
        Intrinsics.checkNotNullParameter(provider2, "mediaControlPanelFactory");
        Intrinsics.checkNotNullParameter(visualStabilityManager3, "visualStabilityManager");
        Intrinsics.checkNotNullParameter(mediaHostStatesManager3, "mediaHostStatesManager");
        Intrinsics.checkNotNullParameter(activityStarter3, "activityStarter");
        Intrinsics.checkNotNullParameter(systemClock3, "systemClock");
        DelayableExecutor delayableExecutor2 = delayableExecutor;
        Intrinsics.checkNotNullParameter(delayableExecutor2, "executor");
        Intrinsics.checkNotNullParameter(mediaDataManager2, "mediaManager");
        Intrinsics.checkNotNullParameter(configurationController2, "configurationController");
        Intrinsics.checkNotNullParameter(falsingCollector, "falsingCollector");
        Intrinsics.checkNotNullParameter(falsingManager, "falsingManager");
        Intrinsics.checkNotNullParameter(dumpManager2, "dumpManager");
        this.context = context3;
        this.mediaControlPanelFactory = provider2;
        this.visualStabilityManager = visualStabilityManager3;
        this.mediaHostStatesManager = mediaHostStatesManager3;
        this.activityStarter = activityStarter3;
        this.systemClock = systemClock3;
        this.mediaManager = mediaDataManager2;
        MediaCarouselController$configListener$1 mediaCarouselController$configListener$1 = new MediaCarouselController$configListener$1(this);
        this.configListener = mediaCarouselController$configListener$1;
        this.mRemovedCallbackList = new ArrayList();
        this.MediaPlayerData = new MediaPlayerData();
        dumpManager2.registerDumpable("MediaCarouselController", this);
        ViewGroup inflateMediaCarousel = inflateMediaCarousel();
        this.mediaFrame = inflateMediaCarousel;
        View requireViewById = inflateMediaCarousel.requireViewById(R$id.media_carousel_scroller);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "mediaFrame.requireViewById(R.id.media_carousel_scroller)");
        MediaScrollView mediaScrollView = (MediaScrollView) requireViewById;
        this.mediaCarousel = mediaScrollView;
        View requireViewById2 = inflateMediaCarousel.requireViewById(R$id.media_page_indicator);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "mediaFrame.requireViewById(R.id.media_page_indicator)");
        PageIndicator pageIndicator2 = (PageIndicator) requireViewById2;
        this.pageIndicator = pageIndicator2;
        ViewGroup viewGroup = inflateMediaCarousel;
        MediaCarouselScrollHandler mediaCarouselScrollHandler2 = r11;
        MediaCarouselScrollHandler mediaCarouselScrollHandler3 = new MediaCarouselScrollHandler(mediaScrollView, pageIndicator2, delayableExecutor2, new Function0<Unit>(this) {
            {
                Class<MediaCarouselController> cls = MediaCarouselController.class;
                MediaCarouselController mediaCarouselController = r8;
            }

            public final void invoke() {
                ((MediaCarouselController) this.receiver).onSwipeToDismiss();
            }
        }, new Function0<Unit>(this) {
            {
                Class<MediaCarouselController> cls = MediaCarouselController.class;
                MediaCarouselController mediaCarouselController = r8;
            }

            public final void invoke() {
                ((MediaCarouselController) this.receiver).updatePageIndicatorLocation();
            }
        }, new Function1<Boolean, Unit>(this) {
            {
                Class<MediaCarouselController> cls = MediaCarouselController.class;
                MediaCarouselController mediaCarouselController = r8;
            }

            public /* bridge */ /* synthetic */ Object invoke(Object obj) {
                invoke(((Boolean) obj).booleanValue());
                return Unit.INSTANCE;
            }

            public final void invoke(boolean z) {
                ((MediaCarouselController) this.receiver).closeGuts(z);
            }
        }, falsingCollector, falsingManager, new Function1<Boolean, Unit>(this) {
            {
                Class<MediaCarouselController> cls = MediaCarouselController.class;
                MediaCarouselController mediaCarouselController = r8;
            }

            public /* bridge */ /* synthetic */ Object invoke(Object obj) {
                invoke(((Boolean) obj).booleanValue());
                return Unit.INSTANCE;
            }

            public final void invoke(boolean z) {
                ((MediaCarouselController) this.receiver).logSmartspaceImpression(z);
            }
        });
        this.mediaCarouselScrollHandler = mediaCarouselScrollHandler2;
        setRtl(context2.getResources().getConfiguration().getLayoutDirection() == 1);
        inflateSettingsButton();
        View requireViewById3 = mediaScrollView.requireViewById(R$id.media_carousel);
        Intrinsics.checkNotNullExpressionValue(requireViewById3, "mediaCarousel.requireViewById(R.id.media_carousel)");
        this.mediaContent = (ViewGroup) requireViewById3;
        configurationController2.addCallback(mediaCarouselController$configListener$1);
        C10105 r1 = new VisualStabilityManager.Callback(this) {
            final /* synthetic */ MediaCarouselController this$0;

            {
                this.this$0 = r1;
            }

            public final void onChangeAllowed() {
                if (this.this$0.needsReordering) {
                    this.this$0.needsReordering = false;
                    this.this$0.reorderAllPlayers((MediaPlayerData.MediaSortKey) null);
                }
                Set<String> access$getKeysNeedRemoval$p = this.this$0.keysNeedRemoval;
                MediaCarouselController mediaCarouselController = this.this$0;
                for (String removePlayer$default : access$getKeysNeedRemoval$p) {
                    MediaCarouselController.removePlayer$default(mediaCarouselController, removePlayer$default, false, false, 6, (Object) null);
                }
                this.this$0.keysNeedRemoval.clear();
                MediaCarouselController mediaCarouselController2 = this.this$0;
                if (mediaCarouselController2.updateUserVisibility != null) {
                    mediaCarouselController2.getUpdateUserVisibility().invoke();
                }
                this.this$0.getMediaCarouselScrollHandler().scrollToStart();
            }
        };
        this.visualStabilityCallback = r1;
        visualStabilityManager3.addReorderingAllowedCallback(r1, true);
        mediaDataManager2.addListener(new MediaDataManager.Listener(this) {
            final /* synthetic */ MediaCarouselController this$0;

            {
                this.this$0 = r1;
            }

            public void onMediaDataLoaded(@NotNull String str, @Nullable String str2, @NotNull MediaData mediaData, boolean z, boolean z2) {
                MediaControlPanel mediaPlayer;
                Intrinsics.checkNotNullParameter(str, "key");
                Intrinsics.checkNotNullParameter(mediaData, "data");
                if (this.this$0.addOrUpdatePlayer(str, str2, mediaData) && (mediaPlayer = this.this$0.getMediaPlayerData().getMediaPlayer(str)) != null) {
                    MediaCarouselController mediaCarouselController = this.this$0;
                    mediaCarouselController.logSmartspaceCardReported(759, mediaPlayer.mInstanceId, false, mediaPlayer.getSurfaceForSmartspaceLogging(), mediaCarouselController.getMediaPlayerData().getMediaPlayerIndex(str));
                }
                if (this.this$0.getMediaCarouselScrollHandler().getVisibleToUser() && z2 && !this.this$0.getMediaCarouselScrollHandler().getQsExpanded()) {
                    MediaCarouselController mediaCarouselController2 = this.this$0;
                    mediaCarouselController2.logSmartspaceImpression(mediaCarouselController2.getMediaCarouselScrollHandler().getQsExpanded());
                }
                Boolean isPlaying = mediaData.isPlaying();
                Boolean valueOf = isPlaying == null ? null : Boolean.valueOf(!isPlaying.booleanValue());
                boolean z3 = (valueOf == null ? mediaData.isClearable() : valueOf.booleanValue()) && !mediaData.getActive();
                if (MotoFeature.getExistedInstance().isSupportCli()) {
                    z3 = !mediaData.getActive();
                    this.this$0.visualStabilityManager.temporarilyAllowReordering();
                }
                if (!z3 || Utils.useMediaResumption(this.this$0.context)) {
                    this.this$0.keysNeedRemoval.remove(str);
                    this.this$0.dispatchMediaActiveState(str, true);
                    return;
                }
                if (this.this$0.visualStabilityManager.isReorderingAllowed()) {
                    onMediaDataRemoved(str);
                } else {
                    this.this$0.keysNeedRemoval.add(str);
                }
                this.this$0.dispatchMediaActiveState(str, false);
            }

            public void onSmartspaceMediaDataLoaded(@NotNull String str, @NotNull SmartspaceMediaData smartspaceMediaData, boolean z) {
                Intrinsics.checkNotNullParameter(str, "key");
                Intrinsics.checkNotNullParameter(smartspaceMediaData, "data");
                if (smartspaceMediaData.isActive()) {
                    this.this$0.addSmartspaceMediaRecommendations(str, smartspaceMediaData, z);
                    MediaControlPanel mediaPlayer = this.this$0.getMediaPlayerData().getMediaPlayer(str);
                    if (mediaPlayer != null) {
                        MediaCarouselController mediaCarouselController = this.this$0;
                        mediaCarouselController.logSmartspaceCardReported(759, mediaPlayer.mInstanceId, true, mediaPlayer.getSurfaceForSmartspaceLogging(), mediaCarouselController.getMediaPlayerData().getMediaPlayerIndex(str));
                        if (mediaCarouselController.getMediaCarouselScrollHandler().getVisibleToUser() && mediaCarouselController.getMediaCarouselScrollHandler().getVisibleMediaIndex() == mediaCarouselController.getMediaPlayerData().getMediaPlayerIndex(str)) {
                            MediaCarouselController.logSmartspaceCardReported$default(mediaCarouselController, 800, mediaPlayer.mInstanceId, true, mediaPlayer.getSurfaceForSmartspaceLogging(), 0, 16, (Object) null);
                            return;
                        }
                        return;
                    }
                    return;
                }
                onSmartspaceMediaDataRemoved(smartspaceMediaData.getTargetId(), true);
            }

            public void onMediaDataRemoved(@NotNull String str) {
                Intrinsics.checkNotNullParameter(str, "key");
                MediaCarouselController.removePlayer$default(this.this$0, str, false, false, 6, (Object) null);
            }

            public void onSmartspaceMediaDataRemoved(@NotNull String str, boolean z) {
                Intrinsics.checkNotNullParameter(str, "key");
                if (z || this.this$0.visualStabilityManager.isReorderingAllowed()) {
                    onMediaDataRemoved(str);
                } else {
                    this.this$0.keysNeedRemoval.add(str);
                }
            }
        });
        viewGroup.addOnLayoutChangeListener(new View.OnLayoutChangeListener(this) {
            final /* synthetic */ MediaCarouselController this$0;

            {
                this.this$0 = r1;
            }

            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                this.this$0.updatePageIndicatorLocation();
            }
        });
        mediaHostStatesManager2.addCallback(new MediaHostStatesManager.Callback(this) {
            final /* synthetic */ MediaCarouselController this$0;

            {
                this.this$0 = r1;
            }

            public void onHostStateChanged(int i, @NotNull MediaHostState mediaHostState) {
                Intrinsics.checkNotNullParameter(mediaHostState, "mediaHostState");
                if (i == this.this$0.desiredLocation) {
                    MediaCarouselController mediaCarouselController = this.this$0;
                    MediaCarouselController.onDesiredLocationChanged$default(mediaCarouselController, mediaCarouselController.desiredLocation, mediaHostState, false, 0, 0, 24, (Object) null);
                }
            }
        });
    }

    @NotNull
    public final MediaCarouselScrollHandler getMediaCarouselScrollHandler() {
        return this.mediaCarouselScrollHandler;
    }

    @NotNull
    public final ViewGroup getMediaFrame() {
        return this.mediaFrame;
    }

    @NotNull
    public final MediaPlayerData getMediaPlayerData() {
        return this.MediaPlayerData;
    }

    /* access modifiers changed from: protected */
    public final void setShouldScrollToActivePlayer(boolean z) {
        this.shouldScrollToActivePlayer = z;
    }

    /* access modifiers changed from: private */
    public final void setRtl(boolean z) {
        if (z != this.isRtl) {
            this.isRtl = z;
            this.mediaFrame.setLayoutDirection(z ? 1 : 0);
            this.mediaCarouselScrollHandler.scrollToStart();
        }
    }

    private final void setCurrentlyExpanded(boolean z) {
        if (this.currentlyExpanded != z) {
            this.currentlyExpanded = z;
            for (MediaControlPanel listening : this.MediaPlayerData.players()) {
                listening.setListening(this.currentlyExpanded);
            }
        }
    }

    @NotNull
    public final Function0<Unit> getUpdateUserVisibility() {
        Function0<Unit> function0 = this.updateUserVisibility;
        if (function0 != null) {
            return function0;
        }
        Intrinsics.throwUninitializedPropertyAccessException("updateUserVisibility");
        throw null;
    }

    public final void setUpdateUserVisibility(@NotNull Function0<Unit> function0) {
        Intrinsics.checkNotNullParameter(function0, "<set-?>");
        this.updateUserVisibility = function0;
    }

    /* access modifiers changed from: private */
    public final void inflateSettingsButton() {
        View inflate = LayoutInflater.from(this.context).inflate(R$layout.media_carousel_settings_button, this.mediaFrame, false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.View");
        View view = this.settingsButton;
        if (view != null) {
            ViewGroup viewGroup = this.mediaFrame;
            if (view != null) {
                viewGroup.removeView(view);
            } else {
                Intrinsics.throwUninitializedPropertyAccessException("settingsButton");
                throw null;
            }
        }
        this.settingsButton = inflate;
        this.mediaFrame.addView(inflate);
        this.mediaCarouselScrollHandler.onSettingsButtonUpdated(inflate);
        View view2 = this.settingsButton;
        if (view2 != null) {
            view2.setOnClickListener(new MediaCarouselController$inflateSettingsButton$2(this));
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("settingsButton");
            throw null;
        }
    }

    private final ViewGroup inflateMediaCarousel() {
        View inflate = LayoutInflater.from(this.context).inflate(R$layout.media_carousel, new UniqueObjectHostView(this.context), false);
        Objects.requireNonNull(inflate, "null cannot be cast to non-null type android.view.ViewGroup");
        ViewGroup viewGroup = (ViewGroup) inflate;
        viewGroup.setLayoutDirection(3);
        return viewGroup;
    }

    /* access modifiers changed from: private */
    public final void reorderAllPlayers(MediaPlayerData.MediaSortKey mediaSortKey) {
        Unit unit;
        RecommendationViewHolder recommendationViewHolder;
        this.mediaContent.removeAllViews();
        Iterator<MediaControlPanel> it = this.MediaPlayerData.players().iterator();
        while (true) {
            unit = null;
            if (!it.hasNext()) {
                break;
            }
            MediaControlPanel next = it.next();
            PlayerViewHolder playerViewHolder = next.getPlayerViewHolder();
            if (playerViewHolder != null) {
                this.mediaContent.addView(playerViewHolder.getPlayer());
                unit = Unit.INSTANCE;
            }
            if (unit == null && (recommendationViewHolder = next.getRecommendationViewHolder()) != null) {
                this.mediaContent.addView(recommendationViewHolder.getRecommendations());
            }
        }
        this.mediaCarouselScrollHandler.onPlayersChanged();
        if (this.shouldScrollToActivePlayer) {
            int i = 0;
            this.shouldScrollToActivePlayer = false;
            int firstActiveMediaIndex = this.MediaPlayerData.firstActiveMediaIndex();
            int i2 = -1;
            if (firstActiveMediaIndex != -1) {
                if (mediaSortKey != null) {
                    Iterator<T> it2 = getMediaPlayerData().playerKeys().iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        T next2 = it2.next();
                        if (i < 0) {
                            CollectionsKt__CollectionsKt.throwIndexOverflow();
                        }
                        if (Intrinsics.areEqual((Object) mediaSortKey, (Object) (MediaPlayerData.MediaSortKey) next2)) {
                            i2 = i;
                            break;
                        }
                        i++;
                    }
                    getMediaCarouselScrollHandler().scrollToPlayer(i2, firstActiveMediaIndex);
                    unit = Unit.INSTANCE;
                }
                if (unit == null) {
                    new MediaCarouselController$reorderAllPlayers$4(this, firstActiveMediaIndex);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final boolean addOrUpdatePlayer(String str, String str2, MediaData mediaData) {
        TransitionLayout player;
        String str3 = str;
        MediaData copy$default = MediaData.copy$default(mediaData, 0, false, this.bgColor, (String) null, (Icon) null, (CharSequence) null, (CharSequence) null, (Icon) null, (List) null, (List) null, (String) null, (MediaSession.Token) null, (PendingIntent) null, (MediaDeviceData) null, false, (Runnable) null, false, false, (String) null, false, (Boolean) null, false, 0, 8388603, (Object) null);
        this.MediaPlayerData.moveIfExists(str2, str3);
        MediaControlPanel mediaPlayer = this.MediaPlayerData.getMediaPlayer(str3);
        MediaPlayerData.MediaSortKey mediaSortKey = (MediaPlayerData.MediaSortKey) CollectionsKt___CollectionsKt.elementAtOrNull(this.MediaPlayerData.playerKeys(), this.mediaCarouselScrollHandler.getVisibleMediaIndex());
        if (mediaPlayer == null) {
            MediaControlPanel mediaControlPanel = this.mediaControlPanelFactory.get();
            PlayerViewHolder.Companion companion = PlayerViewHolder.Companion;
            LayoutInflater from = LayoutInflater.from(this.context);
            Intrinsics.checkNotNullExpressionValue(from, "from(context)");
            mediaControlPanel.attachPlayer(companion.create(from, this.mediaContent));
            mediaControlPanel.getMediaViewController().setSizeChangedListener(new MediaCarouselController$addOrUpdatePlayer$1(this));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
            PlayerViewHolder playerViewHolder = mediaControlPanel.getPlayerViewHolder();
            if (!(playerViewHolder == null || (player = playerViewHolder.getPlayer()) == null)) {
                player.setLayoutParams(layoutParams);
            }
            mediaControlPanel.bindPlayer(copy$default, str3);
            mediaControlPanel.setListening(this.currentlyExpanded);
            MediaPlayerData mediaPlayerData = this.MediaPlayerData;
            Intrinsics.checkNotNullExpressionValue(mediaControlPanel, "newPlayer");
            mediaPlayerData.addMediaPlayer(str3, copy$default, mediaControlPanel, this.systemClock);
            Intrinsics.checkNotNullExpressionValue(mediaControlPanel, "newPlayer");
            updatePlayerToState(mediaControlPanel, true);
            reorderAllPlayers(mediaSortKey);
        } else {
            mediaPlayer.bindPlayer(copy$default, str3);
            this.MediaPlayerData.addMediaPlayer(str3, copy$default, mediaPlayer, this.systemClock);
            if (this.visualStabilityManager.isReorderingAllowed() || this.shouldScrollToActivePlayer) {
                reorderAllPlayers(mediaSortKey);
            } else {
                this.needsReordering = true;
            }
        }
        updatePageIndicator();
        this.mediaCarouselScrollHandler.onPlayersChanged();
        UniqueObjectHostViewKt.setRequiresRemeasuring(this.mediaCarousel, true);
        if (this.MediaPlayerData.players().size() != this.mediaContent.getChildCount()) {
            Log.wtf("MediaCarouselController", "Size of players list and number of views in carousel are out of sync");
        }
        if (mediaPlayer == null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public final void addSmartspaceMediaRecommendations(String str, SmartspaceMediaData smartspaceMediaData, boolean z) {
        TransitionLayout recommendations;
        if (this.MediaPlayerData.getMediaPlayer(str) != null) {
            Log.w("MediaCarouselController", "Skip adding smartspace target in carousel");
            return;
        }
        String smartspaceMediaKey = this.MediaPlayerData.smartspaceMediaKey();
        if (smartspaceMediaKey != null) {
            getMediaPlayerData().removeMediaPlayer(smartspaceMediaKey);
        }
        MediaControlPanel mediaControlPanel = this.mediaControlPanelFactory.get();
        RecommendationViewHolder.Companion companion = RecommendationViewHolder.Companion;
        LayoutInflater from = LayoutInflater.from(this.context);
        Intrinsics.checkNotNullExpressionValue(from, "from(context)");
        mediaControlPanel.attachRecommendation(companion.create(from, this.mediaContent));
        mediaControlPanel.getMediaViewController().setSizeChangedListener(new MediaCarouselController$addSmartspaceMediaRecommendations$2(this));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        RecommendationViewHolder recommendationViewHolder = mediaControlPanel.getRecommendationViewHolder();
        if (!(recommendationViewHolder == null || (recommendations = recommendationViewHolder.getRecommendations()) == null)) {
            recommendations.setLayoutParams(layoutParams);
        }
        mediaControlPanel.bindRecommendation(SmartspaceMediaData.copy$default(smartspaceMediaData, (String) null, false, false, (String) null, (SmartspaceAction) null, (List) null, this.bgColor, 63, (Object) null));
        MediaPlayerData mediaPlayerData = this.MediaPlayerData;
        Intrinsics.checkNotNullExpressionValue(mediaControlPanel, "newRecs");
        mediaPlayerData.addMediaRecommendation(str, smartspaceMediaData, mediaControlPanel, z, this.systemClock);
        Intrinsics.checkNotNullExpressionValue(mediaControlPanel, "newRecs");
        updatePlayerToState(mediaControlPanel, true);
        reorderAllPlayers((MediaPlayerData.MediaSortKey) CollectionsKt___CollectionsKt.elementAtOrNull(this.MediaPlayerData.playerKeys(), this.mediaCarouselScrollHandler.getVisibleMediaIndex()));
        updatePageIndicator();
        UniqueObjectHostViewKt.setRequiresRemeasuring(this.mediaCarousel, true);
        if (this.MediaPlayerData.players().size() != this.mediaContent.getChildCount()) {
            Log.wtf("MediaCarouselController", "Size of players list and number of views in carousel are out of sync");
        }
    }

    public static /* synthetic */ void removePlayer$default(MediaCarouselController mediaCarouselController, String str, boolean z, boolean z2, int i, Object obj) {
        if ((i & 2) != 0) {
            z = true;
        }
        if ((i & 4) != 0) {
            z2 = true;
        }
        mediaCarouselController.removePlayer(str, z, z2);
    }

    public final void removePlayer(@NotNull String str, boolean z, boolean z2) {
        List<RemovedCallback> list;
        Intrinsics.checkNotNullParameter(str, "key");
        MediaControlPanel removeMediaPlayer = this.MediaPlayerData.removeMediaPlayer(str);
        if (!(removeMediaPlayer == null || (list = this.mRemovedCallbackList) == null)) {
            for (RemovedCallback onMediaRemoved : list) {
                onMediaRemoved.onMediaRemoved(str);
            }
        }
        if (removeMediaPlayer != null) {
            getMediaCarouselScrollHandler().onPrePlayerRemoved(removeMediaPlayer);
            ViewGroup viewGroup = this.mediaContent;
            PlayerViewHolder playerViewHolder = removeMediaPlayer.getPlayerViewHolder();
            TransitionLayout transitionLayout = null;
            viewGroup.removeView(playerViewHolder == null ? null : playerViewHolder.getPlayer());
            ViewGroup viewGroup2 = this.mediaContent;
            RecommendationViewHolder recommendationViewHolder = removeMediaPlayer.getRecommendationViewHolder();
            if (recommendationViewHolder != null) {
                transitionLayout = recommendationViewHolder.getRecommendations();
            }
            viewGroup2.removeView(transitionLayout);
            removeMediaPlayer.onDestroy();
            getMediaCarouselScrollHandler().onPlayersChanged();
            updatePageIndicator();
            if (z) {
                this.mediaManager.dismissMediaData(str, 0);
            }
            if (z2) {
                this.mediaManager.dismissSmartspaceRecommendation(str, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void recreatePlayers() {
        this.bgColor = getBackgroundColor();
        this.pageIndicator.setTintList(ColorStateList.valueOf(getForegroundColor()));
        for (Triple triple : this.MediaPlayerData.mediaData()) {
            String str = (String) triple.component1();
            MediaData mediaData = (MediaData) triple.component2();
            if (((Boolean) triple.component3()).booleanValue()) {
                SmartspaceMediaData smartspaceMediaData$frameworks__base__packages__SystemUI__android_common__SystemUI_core = getMediaPlayerData().mo14306xc76e19e1();
                removePlayer(str, false, false);
                if (smartspaceMediaData$frameworks__base__packages__SystemUI__android_common__SystemUI_core != null) {
                    addSmartspaceMediaRecommendations(smartspaceMediaData$frameworks__base__packages__SystemUI__android_common__SystemUI_core.getTargetId(), smartspaceMediaData$frameworks__base__packages__SystemUI__android_common__SystemUI_core, getMediaPlayerData().mo14305x1d6e9f0e());
                }
            } else {
                removePlayer(str, false, false);
                addOrUpdatePlayer(str, (String) null, mediaData);
            }
        }
    }

    private final int getBackgroundColor() {
        return this.context.getColor(17170502);
    }

    private final int getForegroundColor() {
        return this.context.getColor(17170511);
    }

    private final void updatePageIndicator() {
        int childCount = this.mediaContent.getChildCount();
        this.pageIndicator.setNumPages(childCount);
        if (childCount == 1) {
            this.pageIndicator.setLocation(0.0f);
        }
        updatePageIndicatorAlpha();
    }

    public final void setCurrentState(int i, int i2, float f, boolean z) {
        if (i == this.currentStartLocation && i2 == this.currentEndLocation) {
            if ((f == this.currentTransitionProgress) && !z) {
                return;
            }
        }
        this.currentStartLocation = i;
        this.currentEndLocation = i2;
        this.currentTransitionProgress = f;
        for (MediaControlPanel next : this.MediaPlayerData.players()) {
            Intrinsics.checkNotNullExpressionValue(next, "mediaPlayer");
            updatePlayerToState(next, z);
        }
        maybeResetSettingsCog();
        updatePageIndicatorAlpha();
    }

    private final void updatePageIndicatorAlpha() {
        Map<Integer, MediaHostState> mediaHostStates = this.mediaHostStatesManager.getMediaHostStates();
        MediaHostState mediaHostState = mediaHostStates.get(Integer.valueOf(this.currentEndLocation));
        boolean z = false;
        boolean visible = mediaHostState == null ? false : mediaHostState.getVisible();
        MediaHostState mediaHostState2 = mediaHostStates.get(Integer.valueOf(this.currentStartLocation));
        if (mediaHostState2 != null) {
            z = mediaHostState2.getVisible();
        }
        float f = 1.0f;
        float f2 = z ? 1.0f : 0.0f;
        float f3 = visible ? 1.0f : 0.0f;
        if (!visible || !z) {
            float f4 = this.currentTransitionProgress;
            if (!visible) {
                f4 = 1.0f - f4;
            }
            f = MathUtils.lerp(f2, f3, MathUtils.constrain(MathUtils.map(0.95f, 1.0f, 0.0f, 1.0f, f4), 0.0f, 1.0f));
        }
        this.pageIndicator.setAlpha(f);
    }

    /* access modifiers changed from: private */
    public final void updatePageIndicatorLocation() {
        int i;
        int i2;
        if (this.isRtl) {
            i2 = this.pageIndicator.getWidth();
            i = this.currentCarouselWidth;
        } else {
            i2 = this.currentCarouselWidth;
            i = this.pageIndicator.getWidth();
        }
        this.pageIndicator.setTranslationX((((float) (i2 - i)) / 2.0f) + this.mediaCarouselScrollHandler.getContentTranslation());
        ViewGroup.LayoutParams layoutParams = this.pageIndicator.getLayoutParams();
        Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
        PageIndicator pageIndicator2 = this.pageIndicator;
        pageIndicator2.setTranslationY((float) ((this.currentCarouselHeight - pageIndicator2.getHeight()) - ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin));
    }

    /* access modifiers changed from: private */
    public final void updateCarouselDimensions() {
        int i = 0;
        int i2 = 0;
        for (MediaControlPanel mediaViewController : this.MediaPlayerData.players()) {
            MediaViewController mediaViewController2 = mediaViewController.getMediaViewController();
            Intrinsics.checkNotNullExpressionValue(mediaViewController2, "mediaPlayer.mediaViewController");
            i = Math.max(i, mediaViewController2.getCurrentWidth() + ((int) mediaViewController2.getTranslationX()));
            i2 = Math.max(i2, mediaViewController2.getCurrentHeight() + ((int) mediaViewController2.getTranslationY()));
        }
        if (i != this.currentCarouselWidth || i2 != this.currentCarouselHeight) {
            this.currentCarouselWidth = i;
            this.currentCarouselHeight = i2;
            this.mediaCarouselScrollHandler.setCarouselBounds(i, i2);
            updatePageIndicatorLocation();
        }
    }

    private final void maybeResetSettingsCog() {
        Map<Integer, MediaHostState> mediaHostStates = this.mediaHostStatesManager.getMediaHostStates();
        MediaHostState mediaHostState = mediaHostStates.get(Integer.valueOf(this.currentEndLocation));
        boolean showsOnlyActiveMedia = mediaHostState == null ? true : mediaHostState.getShowsOnlyActiveMedia();
        MediaHostState mediaHostState2 = mediaHostStates.get(Integer.valueOf(this.currentStartLocation));
        boolean showsOnlyActiveMedia2 = mediaHostState2 == null ? showsOnlyActiveMedia : mediaHostState2.getShowsOnlyActiveMedia();
        if (this.currentlyShowingOnlyActive == showsOnlyActiveMedia) {
            float f = this.currentTransitionProgress;
            boolean z = false;
            if (!(f == 1.0f)) {
                if (f == 0.0f) {
                    z = true;
                }
                if (z || showsOnlyActiveMedia2 == showsOnlyActiveMedia) {
                    return;
                }
            } else {
                return;
            }
        }
        this.currentlyShowingOnlyActive = showsOnlyActiveMedia;
        this.mediaCarouselScrollHandler.resetTranslation(true);
    }

    private final void updatePlayerToState(MediaControlPanel mediaControlPanel, boolean z) {
        mediaControlPanel.getMediaViewController().setCurrentState(this.currentStartLocation, this.currentEndLocation, this.currentTransitionProgress, z);
    }

    public static /* synthetic */ void onDesiredLocationChanged$default(MediaCarouselController mediaCarouselController, int i, MediaHostState mediaHostState, boolean z, long j, long j2, int i2, Object obj) {
        mediaCarouselController.onDesiredLocationChanged(i, mediaHostState, z, (i2 & 8) != 0 ? 200 : j, (i2 & 16) != 0 ? 0 : j2);
    }

    public final void onDesiredLocationChanged(int i, @Nullable MediaHostState mediaHostState, boolean z, long j, long j2) {
        if (mediaHostState != null) {
            this.desiredLocation = i;
            this.desiredHostState = mediaHostState;
            setCurrentlyExpanded(mediaHostState.getExpansion() > 0.0f);
            boolean z2 = !this.currentlyExpanded && !this.mediaManager.hasActiveMedia() && mediaHostState.getShowsOnlyActiveMedia();
            for (MediaControlPanel next : getMediaPlayerData().players()) {
                if (z) {
                    next.getMediaViewController().animatePendingStateChange(j, j2);
                }
                if (z2 && next.getMediaViewController().isGutsVisible()) {
                    next.closeGuts(!z);
                }
                next.getMediaViewController().onLocationPreChange(i);
            }
            getMediaCarouselScrollHandler().setShowsSettingsButton(!mediaHostState.getShowsOnlyActiveMedia());
            getMediaCarouselScrollHandler().setFalsingProtectionNeeded(mediaHostState.getFalsingProtectionNeeded());
            boolean visible = mediaHostState.getVisible();
            if (visible != this.playersVisible) {
                this.playersVisible = visible;
                if (visible) {
                    MediaCarouselScrollHandler.resetTranslation$default(getMediaCarouselScrollHandler(), false, 1, (Object) null);
                }
            }
            updateCarouselSize();
        }
    }

    public static /* synthetic */ void closeGuts$default(MediaCarouselController mediaCarouselController, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            z = true;
        }
        mediaCarouselController.closeGuts(z);
    }

    public final void closeGuts(boolean z) {
        for (MediaControlPanel closeGuts : this.MediaPlayerData.players()) {
            closeGuts.closeGuts(z);
        }
    }

    private final void updateCarouselSize() {
        MediaHostState mediaHostState = this.desiredHostState;
        MeasurementInput measurementInput = null;
        MeasurementInput measurementInput2 = mediaHostState == null ? null : mediaHostState.getMeasurementInput();
        int width = measurementInput2 == null ? 0 : measurementInput2.getWidth();
        MediaHostState mediaHostState2 = this.desiredHostState;
        MeasurementInput measurementInput3 = mediaHostState2 == null ? null : mediaHostState2.getMeasurementInput();
        int height = measurementInput3 == null ? 0 : measurementInput3.getHeight();
        if ((width != this.carouselMeasureWidth && width != 0) || (height != this.carouselMeasureHeight && height != 0)) {
            this.carouselMeasureWidth = width;
            this.carouselMeasureHeight = height;
            int dimensionPixelSize = this.context.getResources().getDimensionPixelSize(R$dimen.qs_media_padding) + width;
            MediaHostState mediaHostState3 = this.desiredHostState;
            MeasurementInput measurementInput4 = mediaHostState3 == null ? null : mediaHostState3.getMeasurementInput();
            int widthMeasureSpec = measurementInput4 == null ? 0 : measurementInput4.getWidthMeasureSpec();
            MediaHostState mediaHostState4 = this.desiredHostState;
            if (mediaHostState4 != null) {
                measurementInput = mediaHostState4.getMeasurementInput();
            }
            this.mediaCarousel.measure(widthMeasureSpec, measurementInput == null ? 0 : measurementInput.getHeightMeasureSpec());
            MediaScrollView mediaScrollView = this.mediaCarousel;
            mediaScrollView.layout(0, 0, width, mediaScrollView.getMeasuredHeight());
            this.mediaCarouselScrollHandler.setPlayerWidthPlusPadding(dimensionPixelSize);
        }
    }

    public final void logSmartspaceImpression(boolean z) {
        int visibleMediaIndex = this.mediaCarouselScrollHandler.getVisibleMediaIndex();
        if (this.MediaPlayerData.players().size() > visibleMediaIndex) {
            Object elementAt = CollectionsKt___CollectionsKt.elementAt(this.MediaPlayerData.players(), visibleMediaIndex);
            Intrinsics.checkNotNullExpressionValue(elementAt, "MediaPlayerData.players().elementAt(visibleMediaIndex)");
            MediaControlPanel mediaControlPanel = (MediaControlPanel) elementAt;
            boolean hasActiveMediaOrRecommendationCard = this.MediaPlayerData.hasActiveMediaOrRecommendationCard();
            boolean z2 = mediaControlPanel.getRecommendationViewHolder() != null;
            if (hasActiveMediaOrRecommendationCard || z) {
                logSmartspaceCardReported$default(this, 800, mediaControlPanel.mInstanceId, z2, mediaControlPanel.getSurfaceForSmartspaceLogging(), 0, 16, (Object) null);
            }
        }
    }

    public static /* synthetic */ void logSmartspaceCardReported$default(MediaCarouselController mediaCarouselController, int i, int i2, boolean z, int i3, int i4, int i5, Object obj) {
        if ((i5 & 16) != 0) {
            i4 = mediaCarouselController.mediaCarouselScrollHandler.getVisibleMediaIndex();
        }
        mediaCarouselController.logSmartspaceCardReported(i, i2, z, i3, i4);
    }

    public final void logSmartspaceCardReported(int i, int i2, boolean z, int i3, int i4) {
        if (z || this.mediaManager.getSmartspaceMediaData().isActive() || this.MediaPlayerData.mo14306xc76e19e1() != null) {
            SysUiStatsLog.write(352, i, i2, z ? 9 : 8, i3, i4, this.mediaContent.getChildCount());
        }
    }

    /* access modifiers changed from: private */
    public final void onSwipeToDismiss() {
        Collection<MediaControlPanel> players = this.MediaPlayerData.players();
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = players.iterator();
        while (true) {
            boolean z = false;
            if (!it.hasNext()) {
                break;
            }
            T next = it.next();
            if (((MediaControlPanel) next).getRecommendationViewHolder() != null) {
                z = true;
            }
            if (z) {
                arrayList.add(next);
            }
        }
        if (!arrayList.isEmpty()) {
            logSmartspaceCardReported(761, ((MediaControlPanel) arrayList.get(0)).mInstanceId, true, ((MediaControlPanel) arrayList.get(0)).getSurfaceForSmartspaceLogging(), -1);
        } else {
            int visibleMediaIndex = this.mediaCarouselScrollHandler.getVisibleMediaIndex();
            if (this.MediaPlayerData.players().size() > visibleMediaIndex) {
                Object elementAt = CollectionsKt___CollectionsKt.elementAt(this.MediaPlayerData.players(), visibleMediaIndex);
                Intrinsics.checkNotNullExpressionValue(elementAt, "MediaPlayerData.players().elementAt(visibleMediaIndex)");
                MediaControlPanel mediaControlPanel = (MediaControlPanel) elementAt;
                logSmartspaceCardReported(761, mediaControlPanel.mInstanceId, false, mediaControlPanel.getSurfaceForSmartspaceLogging(), -1);
            }
        }
        this.mediaManager.onSwipeToDismiss();
    }

    public void dump(@NotNull FileDescriptor fileDescriptor, @NotNull PrintWriter printWriter, @NotNull String[] strArr) {
        Intrinsics.checkNotNullParameter(fileDescriptor, "fd");
        Intrinsics.checkNotNullParameter(printWriter, "pw");
        Intrinsics.checkNotNullParameter(strArr, "args");
        printWriter.println(Intrinsics.stringPlus("keysNeedRemoval: ", this.keysNeedRemoval));
        printWriter.println(Intrinsics.stringPlus("playerKeys: ", getMediaPlayerData().playerKeys()));
        printWriter.println(Intrinsics.stringPlus("smartspaceMediaData: ", getMediaPlayerData().mo14306xc76e19e1()));
        printWriter.println(Intrinsics.stringPlus("shouldPrioritizeSs: ", Boolean.valueOf(getMediaPlayerData().mo14305x1d6e9f0e())));
    }

    public final void removeMainMediaPlayer(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "key");
        MediaDataManager.m39x7e6a6a7c(this.mediaManager, str, true, false, 4, (Object) null);
    }

    public final void addRemovedCallback(@NotNull RemovedCallback removedCallback) {
        Intrinsics.checkNotNullParameter(removedCallback, "callback");
        this.mRemovedCallbackList.add(removedCallback);
    }

    public final void dispatchMediaActiveState(@NotNull String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "key");
        for (RemovedCallback syncMediaActiveState : this.mRemovedCallbackList) {
            syncMediaActiveState.syncMediaActiveState(str, z);
        }
    }
}
