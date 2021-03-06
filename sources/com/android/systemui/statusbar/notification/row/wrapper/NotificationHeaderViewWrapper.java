package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.util.ArraySet;
import android.util.Pair;
import android.view.NotificationHeaderView;
import android.view.NotificationTopLineView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.widget.CachingIconView;
import com.android.internal.widget.NotificationExpandButton;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.CustomInterpolatorTransformation;
import com.android.systemui.statusbar.notification.ImageTransformState;
import com.android.systemui.statusbar.notification.TransformState;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.Stack;

public class NotificationHeaderViewWrapper extends NotificationViewWrapper {
    /* access modifiers changed from: private */
    public static final Interpolator LOW_PRIORITY_HEADER_CLOSE = new PathInterpolator(0.4f, 0.0f, 0.7f, 1.0f);
    private View mAltExpandTarget;
    private TextView mAppNameText;
    private View mAudiblyAlertedIcon;
    private NotificationExpandButton mExpandButton;
    private View mFeedbackIcon;
    private TextView mHeaderText;
    private CachingIconView mIcon;
    private View mIconContainer;
    /* access modifiers changed from: private */
    public boolean mIsLowPriority;
    protected NotificationHeaderView mNotificationHeader;
    protected NotificationTopLineView mNotificationTopLine;
    /* access modifiers changed from: private */
    public boolean mTransformLowPriorityTitle;
    protected final ViewTransformationHelper mTransformationHelper;
    private ImageView mWorkProfileImage;

    protected NotificationHeaderViewWrapper(Context context, View view, ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        ViewTransformationHelper viewTransformationHelper = new ViewTransformationHelper();
        this.mTransformationHelper = viewTransformationHelper;
        viewTransformationHelper.setCustomTransformation(new CustomInterpolatorTransformation(1) {
            public Interpolator getCustomInterpolator(int i, boolean z) {
                boolean z2 = NotificationHeaderViewWrapper.this.mView instanceof NotificationHeaderView;
                if (i != 16) {
                    return null;
                }
                if ((!z2 || z) && (z2 || !z)) {
                    return NotificationHeaderViewWrapper.LOW_PRIORITY_HEADER_CLOSE;
                }
                return Interpolators.LINEAR_OUT_SLOW_IN;
            }

            /* access modifiers changed from: protected */
            public boolean hasCustomTransformation() {
                return NotificationHeaderViewWrapper.this.mIsLowPriority && NotificationHeaderViewWrapper.this.mTransformLowPriorityTitle;
            }
        }, 1);
        resolveHeaderViews();
        addFeedbackOnClickListener(expandableNotificationRow);
    }

    /* access modifiers changed from: protected */
    public void resolveHeaderViews() {
        this.mIcon = this.mView.findViewById(16908294);
        this.mHeaderText = (TextView) this.mView.findViewById(16909045);
        this.mAppNameText = (TextView) this.mView.findViewById(16908763);
        this.mExpandButton = this.mView.findViewById(16908955);
        this.mAltExpandTarget = this.mView.findViewById(16908749);
        this.mIconContainer = this.mView.findViewById(16908899);
        this.mWorkProfileImage = (ImageView) this.mView.findViewById(16909327);
        this.mNotificationHeader = this.mView.findViewById(16909236);
        this.mNotificationTopLine = this.mView.findViewById(16909248);
        this.mAudiblyAlertedIcon = this.mView.findViewById(16908741);
        this.mFeedbackIcon = this.mView.findViewById(16908968);
    }

    private void addFeedbackOnClickListener(ExpandableNotificationRow expandableNotificationRow) {
        View.OnClickListener feedbackOnClickListener = expandableNotificationRow.getFeedbackOnClickListener();
        NotificationTopLineView notificationTopLineView = this.mNotificationTopLine;
        if (notificationTopLineView != null) {
            notificationTopLineView.setFeedbackOnClickListener(feedbackOnClickListener);
        }
        View view = this.mFeedbackIcon;
        if (view != null) {
            view.setOnClickListener(feedbackOnClickListener);
        }
    }

    public void showFeedbackIcon(boolean z, Pair<Integer, Integer> pair) {
        View view = this.mFeedbackIcon;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            if (z) {
                View view2 = this.mFeedbackIcon;
                if (view2 instanceof ImageButton) {
                    ((ImageButton) view2).setImageResource(((Integer) pair.first).intValue());
                }
                this.mFeedbackIcon.setContentDescription(this.mView.getContext().getString(((Integer) pair.second).intValue()));
            }
        }
    }

    public void onContentUpdated(ExpandableNotificationRow expandableNotificationRow) {
        super.onContentUpdated(expandableNotificationRow);
        this.mIsLowPriority = expandableNotificationRow.getEntry().isAmbient();
        this.mTransformLowPriorityTitle = !expandableNotificationRow.isChildInGroup() && !expandableNotificationRow.isSummaryWithChildren();
        ArraySet<View> allTransformingViews = this.mTransformationHelper.getAllTransformingViews();
        resolveHeaderViews();
        updateTransformedTypes();
        addRemainingTransformTypes();
        updateCropToPaddingForImageViews();
        this.mIcon.setTag(ImageTransformState.ICON_TAG, expandableNotificationRow.getEntry().getSbn().getNotification().getSmallIcon());
        ArraySet<View> allTransformingViews2 = this.mTransformationHelper.getAllTransformingViews();
        for (int i = 0; i < allTransformingViews.size(); i++) {
            View valueAt = allTransformingViews.valueAt(i);
            if (!allTransformingViews2.contains(valueAt)) {
                this.mTransformationHelper.resetTransformedView(valueAt);
            }
        }
    }

    private void addRemainingTransformTypes() {
        this.mTransformationHelper.addRemainingTransformTypes(this.mView);
    }

    private void updateCropToPaddingForImageViews() {
        Stack stack = new Stack();
        stack.push(this.mView);
        while (!stack.isEmpty()) {
            View view = (View) stack.pop();
            if ((view instanceof ImageView) && view.getId() != 16908898) {
                ((ImageView) view).setCropToPadding(true);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    stack.push(viewGroup.getChildAt(i));
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateTransformedTypes() {
        TextView textView;
        this.mTransformationHelper.reset();
        this.mTransformationHelper.addTransformedView(0, this.mIcon);
        this.mTransformationHelper.addTransformedView(6, this.mExpandButton);
        if (this.mIsLowPriority && (textView = this.mHeaderText) != null) {
            this.mTransformationHelper.addTransformedView(1, textView);
        }
        addViewsTransformingToSimilar(this.mWorkProfileImage, this.mAudiblyAlertedIcon, this.mFeedbackIcon);
    }

    public void updateExpandability(boolean z, View.OnClickListener onClickListener, boolean z2) {
        this.mExpandButton.setVisibility(z ? 0 : 8);
        this.mExpandButton.setOnClickListener(z ? onClickListener : null);
        View view = this.mAltExpandTarget;
        if (view != null) {
            view.setOnClickListener(z ? onClickListener : null);
        }
        View view2 = this.mIconContainer;
        if (view2 != null) {
            view2.setOnClickListener(z ? onClickListener : null);
        }
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            if (!z) {
                onClickListener = null;
            }
            notificationHeaderView.setOnClickListener(onClickListener);
        }
        if (z2) {
            this.mExpandButton.getParent().requestLayout();
        }
    }

    public void setExpanded(boolean z) {
        this.mExpandButton.setExpanded(z);
    }

    public void setRecentlyAudiblyAlerted(boolean z) {
        View view = this.mAudiblyAlertedIcon;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    public NotificationHeaderView getNotificationHeader() {
        return this.mNotificationHeader;
    }

    public View getExpandButton() {
        return this.mExpandButton;
    }

    public CachingIconView getIcon() {
        return this.mIcon;
    }

    public int getOriginalIconColor() {
        return this.mIcon.getOriginalIconColor();
    }

    public View getShelfTransformationTarget() {
        return this.mIcon;
    }

    public TransformState getCurrentState(int i) {
        return this.mTransformationHelper.getCurrentState(i);
    }

    public void transformTo(TransformableView transformableView, Runnable runnable) {
        this.mTransformationHelper.transformTo(transformableView, runnable);
    }

    public void transformTo(TransformableView transformableView, float f) {
        this.mTransformationHelper.transformTo(transformableView, f);
    }

    public void transformFrom(TransformableView transformableView) {
        this.mTransformationHelper.transformFrom(transformableView);
    }

    public void transformFrom(TransformableView transformableView, float f) {
        this.mTransformationHelper.transformFrom(transformableView, f);
    }

    public void setIsChildInGroup(boolean z) {
        super.setIsChildInGroup(z);
        this.mTransformLowPriorityTitle = !z;
    }

    public void setVisible(boolean z) {
        super.setVisible(z);
        this.mTransformationHelper.setVisible(z);
    }

    /* access modifiers changed from: protected */
    public void addTransformedViews(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                this.mTransformationHelper.addTransformedView(view);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void addViewsTransformingToSimilar(View... viewArr) {
        for (View view : viewArr) {
            if (view != null) {
                this.mTransformationHelper.addViewTransformingToSimilar(view);
            }
        }
    }
}
