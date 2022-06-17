package com.android.systemui.media;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$id;
import com.android.systemui.R$layout;
import com.android.systemui.util.animation.TransitionLayout;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: RecommendationViewHolder.kt */
public final class RecommendationViewHolder {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    @NotNull
    public static final Set<Integer> controlsIds = SetsKt__SetsKt.setOf(Integer.valueOf(R$id.recommendation_card_icon), Integer.valueOf(R$id.recommendation_card_text), Integer.valueOf(R$id.media_cover1), Integer.valueOf(R$id.media_cover2), Integer.valueOf(R$id.media_cover3), Integer.valueOf(R$id.media_cover4), Integer.valueOf(R$id.media_cover5), Integer.valueOf(R$id.media_cover6), Integer.valueOf(R$id.media_cover1_container), Integer.valueOf(R$id.media_cover2_container), Integer.valueOf(R$id.media_cover3_container), Integer.valueOf(R$id.media_cover4_container), Integer.valueOf(R$id.media_cover5_container), Integer.valueOf(R$id.media_cover6_container));
    /* access modifiers changed from: private */
    @NotNull
    public static final Set<Integer> gutsIds = SetsKt__SetsKt.setOf(Integer.valueOf(R$id.remove_text), Integer.valueOf(R$id.cancel), Integer.valueOf(R$id.dismiss), Integer.valueOf(R$id.settings));
    private final View cancel;
    private final ImageView cardIcon;
    private final TextView cardText;
    private final ViewGroup dismiss;
    private final View dismissLabel;
    private final TextView longPressText;
    @NotNull
    private final List<ViewGroup> mediaCoverContainers;
    @NotNull
    private final List<Integer> mediaCoverContainersResIds;
    @NotNull
    private final List<ImageView> mediaCoverItems;
    @NotNull
    private final List<Integer> mediaCoverItemsResIds;
    @NotNull
    private final TransitionLayout recommendations;
    private final View settings;
    private final TextView settingsText;

    public /* synthetic */ RecommendationViewHolder(View view, DefaultConstructorMarker defaultConstructorMarker) {
        this(view);
    }

    private RecommendationViewHolder(View view) {
        View view2 = view;
        TransitionLayout transitionLayout = (TransitionLayout) view2;
        this.recommendations = transitionLayout;
        this.cardIcon = (ImageView) view2.requireViewById(R$id.recommendation_card_icon);
        this.cardText = (TextView) view2.requireViewById(R$id.recommendation_card_text);
        int i = R$id.media_cover1;
        View requireViewById = view2.requireViewById(i);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "itemView.requireViewById(R.id.media_cover1)");
        int i2 = R$id.media_cover2;
        View requireViewById2 = view2.requireViewById(i2);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "itemView.requireViewById(R.id.media_cover2)");
        int i3 = R$id.media_cover3;
        View requireViewById3 = view2.requireViewById(i3);
        Intrinsics.checkNotNullExpressionValue(requireViewById3, "itemView.requireViewById(R.id.media_cover3)");
        int i4 = R$id.media_cover4;
        View requireViewById4 = view2.requireViewById(i4);
        Intrinsics.checkNotNullExpressionValue(requireViewById4, "itemView.requireViewById(R.id.media_cover4)");
        int i5 = R$id.media_cover5;
        View requireViewById5 = view2.requireViewById(i5);
        Intrinsics.checkNotNullExpressionValue(requireViewById5, "itemView.requireViewById(R.id.media_cover5)");
        int i6 = R$id.media_cover6;
        View requireViewById6 = view2.requireViewById(i6);
        Intrinsics.checkNotNullExpressionValue(requireViewById6, "itemView.requireViewById(R.id.media_cover6)");
        this.mediaCoverItems = CollectionsKt__CollectionsKt.listOf((ImageView) requireViewById, (ImageView) requireViewById2, (ImageView) requireViewById3, (ImageView) requireViewById4, (ImageView) requireViewById5, (ImageView) requireViewById6);
        int i7 = R$id.media_cover1_container;
        View requireViewById7 = view2.requireViewById(i7);
        Intrinsics.checkNotNullExpressionValue(requireViewById7, "itemView.requireViewById(R.id.media_cover1_container)");
        int i8 = R$id.media_cover2_container;
        View requireViewById8 = view2.requireViewById(i8);
        Intrinsics.checkNotNullExpressionValue(requireViewById8, "itemView.requireViewById(R.id.media_cover2_container)");
        int i9 = R$id.media_cover3_container;
        View requireViewById9 = view2.requireViewById(i9);
        Intrinsics.checkNotNullExpressionValue(requireViewById9, "itemView.requireViewById(R.id.media_cover3_container)");
        int i10 = R$id.media_cover4_container;
        View requireViewById10 = view2.requireViewById(i10);
        Intrinsics.checkNotNullExpressionValue(requireViewById10, "itemView.requireViewById(R.id.media_cover4_container)");
        int i11 = R$id.media_cover5_container;
        View requireViewById11 = view2.requireViewById(i11);
        TransitionLayout transitionLayout2 = transitionLayout;
        Intrinsics.checkNotNullExpressionValue(requireViewById11, "itemView.requireViewById(R.id.media_cover5_container)");
        int i12 = R$id.media_cover6_container;
        View requireViewById12 = view2.requireViewById(i12);
        Intrinsics.checkNotNullExpressionValue(requireViewById12, "itemView.requireViewById(R.id.media_cover6_container)");
        this.mediaCoverContainers = CollectionsKt__CollectionsKt.listOf((ViewGroup) requireViewById7, (ViewGroup) requireViewById8, (ViewGroup) requireViewById9, (ViewGroup) requireViewById10, (ViewGroup) requireViewById11, (ViewGroup) requireViewById12);
        this.mediaCoverItemsResIds = CollectionsKt__CollectionsKt.listOf(Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6));
        this.mediaCoverContainersResIds = CollectionsKt__CollectionsKt.listOf(Integer.valueOf(i7), Integer.valueOf(i8), Integer.valueOf(i9), Integer.valueOf(i10), Integer.valueOf(i11), Integer.valueOf(i12));
        View view3 = view;
        this.longPressText = (TextView) view3.requireViewById(R$id.remove_text);
        this.cancel = view3.requireViewById(R$id.cancel);
        ViewGroup viewGroup = (ViewGroup) view3.requireViewById(R$id.dismiss);
        this.dismiss = viewGroup;
        this.dismissLabel = viewGroup.getChildAt(0);
        this.settings = view3.requireViewById(R$id.settings);
        this.settingsText = (TextView) view3.requireViewById(R$id.settings_text);
        Drawable background = transitionLayout2.getBackground();
        Objects.requireNonNull(background, "null cannot be cast to non-null type com.android.systemui.media.IlluminationDrawable");
        IlluminationDrawable illuminationDrawable = (IlluminationDrawable) background;
        for (ViewGroup registerLightSource : getMediaCoverContainers()) {
            illuminationDrawable.registerLightSource((View) registerLightSource);
        }
        View cancel2 = getCancel();
        Intrinsics.checkNotNullExpressionValue(cancel2, "cancel");
        illuminationDrawable.registerLightSource(cancel2);
        ViewGroup dismiss2 = getDismiss();
        Intrinsics.checkNotNullExpressionValue(dismiss2, "dismiss");
        illuminationDrawable.registerLightSource((View) dismiss2);
        View dismissLabel2 = getDismissLabel();
        Intrinsics.checkNotNullExpressionValue(dismissLabel2, "dismissLabel");
        illuminationDrawable.registerLightSource(dismissLabel2);
        View settings2 = getSettings();
        Intrinsics.checkNotNullExpressionValue(settings2, "settings");
        illuminationDrawable.registerLightSource(settings2);
    }

    @NotNull
    public final TransitionLayout getRecommendations() {
        return this.recommendations;
    }

    public final ImageView getCardIcon() {
        return this.cardIcon;
    }

    public final TextView getCardText() {
        return this.cardText;
    }

    @NotNull
    public final List<ImageView> getMediaCoverItems() {
        return this.mediaCoverItems;
    }

    @NotNull
    public final List<ViewGroup> getMediaCoverContainers() {
        return this.mediaCoverContainers;
    }

    @NotNull
    public final List<Integer> getMediaCoverItemsResIds() {
        return this.mediaCoverItemsResIds;
    }

    @NotNull
    public final List<Integer> getMediaCoverContainersResIds() {
        return this.mediaCoverContainersResIds;
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

    public final void marquee(boolean z, long j) {
        this.longPressText.getHandler().postDelayed(new RecommendationViewHolder$marquee$1(this, z), j);
    }

    /* compiled from: RecommendationViewHolder.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final RecommendationViewHolder create(@NotNull LayoutInflater layoutInflater, @NotNull ViewGroup viewGroup) {
            Intrinsics.checkNotNullParameter(layoutInflater, "inflater");
            Intrinsics.checkNotNullParameter(viewGroup, "parent");
            View inflate = layoutInflater.inflate(R$layout.media_smartspace_recommendations, viewGroup, false);
            inflate.setLayoutDirection(3);
            Intrinsics.checkNotNullExpressionValue(inflate, "itemView");
            return new RecommendationViewHolder(inflate, (DefaultConstructorMarker) null);
        }

        @NotNull
        public final Set<Integer> getControlsIds() {
            return RecommendationViewHolder.controlsIds;
        }

        @NotNull
        public final Set<Integer> getGutsIds() {
            return RecommendationViewHolder.gutsIds;
        }
    }
}
