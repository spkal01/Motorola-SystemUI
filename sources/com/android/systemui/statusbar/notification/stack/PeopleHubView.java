package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R$id;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PeopleHubView.kt */
public final class PeopleHubView extends StackScrollerDecorView implements SwipeableView {
    private boolean canSwipe;
    /* access modifiers changed from: private */
    public ViewGroup contents;
    private TextView label;
    private Sequence<?> personViewAdapters;

    @Nullable
    public NotificationMenuRowPlugin createMenu() {
        return null;
    }

    /* access modifiers changed from: protected */
    @Nullable
    public View findSecondaryView() {
        return null;
    }

    public boolean hasFinishedInitialization() {
        return true;
    }

    public boolean needsClippingToShelf() {
        return true;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PeopleHubView(@NotNull Context context, @NotNull AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(attributeSet, "attrs");
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        View requireViewById = requireViewById(R$id.people_list);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "requireViewById(R.id.people_list)");
        this.contents = (ViewGroup) requireViewById;
        View requireViewById2 = requireViewById(R$id.header_label);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "requireViewById(R.id.header_label)");
        this.label = (TextView) requireViewById2;
        ViewGroup viewGroup = this.contents;
        if (viewGroup != null) {
            this.personViewAdapters = CollectionsKt___CollectionsKt.asSequence(SequencesKt___SequencesKt.toList(SequencesKt___SequencesKt.mapNotNull(CollectionsKt___CollectionsKt.asSequence(RangesKt___RangesKt.until(0, viewGroup.getChildCount())), new PeopleHubView$onFinishInflate$1(this))));
            super.onFinishInflate();
            setVisible(true, false);
            return;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }

    /* access modifiers changed from: protected */
    @NotNull
    public View findContentView() {
        ViewGroup viewGroup = this.contents;
        if (viewGroup != null) {
            return viewGroup;
        }
        Intrinsics.throwUninitializedPropertyAccessException("contents");
        throw null;
    }

    public void resetTranslation() {
        setTranslationX(0.0f);
    }

    public void setTranslation(float f) {
        if (this.canSwipe) {
            super.setTranslation(f);
        }
    }

    public final boolean getCanSwipe() {
        return this.canSwipe;
    }

    /* access modifiers changed from: protected */
    public void applyContentTransformation(float f, float f2) {
        super.applyContentTransformation(f, f2);
        ViewGroup viewGroup = this.contents;
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            if (childCount > 0) {
                int i = 0;
                while (true) {
                    int i2 = i + 1;
                    ViewGroup viewGroup2 = this.contents;
                    if (viewGroup2 != null) {
                        View childAt = viewGroup2.getChildAt(i);
                        childAt.setAlpha(f);
                        childAt.setTranslationY(f2);
                        if (i2 < childCount) {
                            i = i2;
                        } else {
                            return;
                        }
                    } else {
                        Intrinsics.throwUninitializedPropertyAccessException("contents");
                        throw null;
                    }
                }
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("contents");
            throw null;
        }
    }

    /* compiled from: PeopleHubView.kt */
    private final class PersonDataListenerImpl {
        @NotNull
        private final ImageView avatarView;
        final /* synthetic */ PeopleHubView this$0;

        public PersonDataListenerImpl(@NotNull PeopleHubView peopleHubView, ImageView imageView) {
            Intrinsics.checkNotNullParameter(peopleHubView, "this$0");
            Intrinsics.checkNotNullParameter(imageView, "avatarView");
            this.this$0 = peopleHubView;
            this.avatarView = imageView;
        }
    }
}
