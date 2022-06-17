package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.widget.CachingIconView;
import com.android.internal.widget.ConversationLayout;
import com.android.internal.widget.MessagingLinearLayout;
import com.android.systemui.R$dimen;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: NotificationConversationTemplateViewWrapper.kt */
public final class NotificationConversationTemplateViewWrapper extends NotificationTemplateViewWrapper {
    private View appName;
    private View conversationBadgeBg;
    private CachingIconView conversationIconView;
    @NotNull
    private final ConversationLayout conversationLayout;
    private View conversationTitleView;
    private View expandBtn;
    private View expandBtnContainer;
    @Nullable
    private View facePileBottom;
    @Nullable
    private View facePileBottomBg;
    @Nullable
    private View facePileTop;
    private ViewGroup imageMessageContainer;
    private View importanceRing;
    private MessagingLinearLayout messagingLinearLayout;
    private final int minHeightWithActions;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NotificationConversationTemplateViewWrapper(@NotNull Context context, @NotNull View view, @NotNull ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
        Intrinsics.checkNotNullParameter(context, "ctx");
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(expandableNotificationRow, "row");
        this.minHeightWithActions = NotificationUtils.getFontScaledHeight(context, R$dimen.notification_messaging_actions_min_height);
        this.conversationLayout = (ConversationLayout) view;
    }

    private final void resolveViews() {
        MessagingLinearLayout messagingLinearLayout2 = this.conversationLayout.getMessagingLinearLayout();
        Intrinsics.checkNotNullExpressionValue(messagingLinearLayout2, "conversationLayout.messagingLinearLayout");
        this.messagingLinearLayout = messagingLinearLayout2;
        ViewGroup imageMessageContainer2 = this.conversationLayout.getImageMessageContainer();
        Intrinsics.checkNotNullExpressionValue(imageMessageContainer2, "conversationLayout.imageMessageContainer");
        this.imageMessageContainer = imageMessageContainer2;
        ConversationLayout conversationLayout2 = this.conversationLayout;
        CachingIconView requireViewById = conversationLayout2.requireViewById(16908895);
        Intrinsics.checkNotNullExpressionValue(requireViewById, "requireViewById(com.android.internal.R.id.conversation_icon)");
        this.conversationIconView = requireViewById;
        View requireViewById2 = conversationLayout2.requireViewById(16908897);
        Intrinsics.checkNotNullExpressionValue(requireViewById2, "requireViewById(com.android.internal.R.id.conversation_icon_badge_bg)");
        this.conversationBadgeBg = requireViewById2;
        View requireViewById3 = conversationLayout2.requireViewById(16908955);
        Intrinsics.checkNotNullExpressionValue(requireViewById3, "requireViewById(com.android.internal.R.id.expand_button)");
        this.expandBtn = requireViewById3;
        View requireViewById4 = conversationLayout2.requireViewById(16908957);
        Intrinsics.checkNotNullExpressionValue(requireViewById4, "requireViewById(com.android.internal.R.id.expand_button_container)");
        this.expandBtnContainer = requireViewById4;
        View requireViewById5 = conversationLayout2.requireViewById(16908898);
        Intrinsics.checkNotNullExpressionValue(requireViewById5, "requireViewById(com.android.internal.R.id.conversation_icon_badge_ring)");
        this.importanceRing = requireViewById5;
        View requireViewById6 = conversationLayout2.requireViewById(16908763);
        Intrinsics.checkNotNullExpressionValue(requireViewById6, "requireViewById(com.android.internal.R.id.app_name_text)");
        this.appName = requireViewById6;
        View requireViewById7 = conversationLayout2.requireViewById(16908901);
        Intrinsics.checkNotNullExpressionValue(requireViewById7, "requireViewById(com.android.internal.R.id.conversation_text)");
        this.conversationTitleView = requireViewById7;
        this.facePileTop = conversationLayout2.findViewById(16908893);
        this.facePileBottom = conversationLayout2.findViewById(16908891);
        this.facePileBottomBg = conversationLayout2.findViewById(16908892);
    }

    public void onContentUpdated(@NotNull ExpandableNotificationRow expandableNotificationRow) {
        Intrinsics.checkNotNullParameter(expandableNotificationRow, "row");
        resolveViews();
        super.onContentUpdated(expandableNotificationRow);
    }

    /* access modifiers changed from: protected */
    public void updateTransformedTypes() {
        super.updateTransformedTypes();
        ViewTransformationHelper viewTransformationHelper = this.mTransformationHelper;
        View view = this.conversationTitleView;
        if (view != null) {
            viewTransformationHelper.addTransformedView(1, view);
            View[] viewArr = new View[2];
            MessagingLinearLayout messagingLinearLayout2 = this.messagingLinearLayout;
            if (messagingLinearLayout2 != null) {
                viewArr[0] = messagingLinearLayout2;
                View view2 = this.appName;
                if (view2 != null) {
                    viewArr[1] = view2;
                    addTransformedViews(viewArr);
                    ViewTransformationHelper viewTransformationHelper2 = this.mTransformationHelper;
                    ViewGroup viewGroup = this.imageMessageContainer;
                    if (viewGroup != null) {
                        NotificationMessagingTemplateViewWrapper.setCustomImageMessageTransform(viewTransformationHelper2, viewGroup);
                        View[] viewArr2 = new View[7];
                        CachingIconView cachingIconView = this.conversationIconView;
                        if (cachingIconView != null) {
                            viewArr2[0] = cachingIconView;
                            View view3 = this.conversationBadgeBg;
                            if (view3 != null) {
                                viewArr2[1] = view3;
                                View view4 = this.expandBtn;
                                if (view4 != null) {
                                    viewArr2[2] = view4;
                                    View view5 = this.importanceRing;
                                    if (view5 != null) {
                                        viewArr2[3] = view5;
                                        viewArr2[4] = this.facePileTop;
                                        viewArr2[5] = this.facePileBottom;
                                        viewArr2[6] = this.facePileBottomBg;
                                        addViewsTransformingToSimilar(viewArr2);
                                        return;
                                    }
                                    Intrinsics.throwUninitializedPropertyAccessException("importanceRing");
                                    throw null;
                                }
                                Intrinsics.throwUninitializedPropertyAccessException("expandBtn");
                                throw null;
                            }
                            Intrinsics.throwUninitializedPropertyAccessException("conversationBadgeBg");
                            throw null;
                        }
                        Intrinsics.throwUninitializedPropertyAccessException("conversationIconView");
                        throw null;
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("imageMessageContainer");
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException("appName");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("messagingLinearLayout");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("conversationTitleView");
        throw null;
    }

    @Nullable
    public View getShelfTransformationTarget() {
        if (!this.conversationLayout.isImportantConversation()) {
            return super.getShelfTransformationTarget();
        }
        CachingIconView cachingIconView = this.conversationIconView;
        if (cachingIconView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("conversationIconView");
            throw null;
        } else if (cachingIconView.getVisibility() == 8) {
            return super.getShelfTransformationTarget();
        } else {
            CachingIconView cachingIconView2 = this.conversationIconView;
            if (cachingIconView2 != null) {
                return cachingIconView2;
            }
            Intrinsics.throwUninitializedPropertyAccessException("conversationIconView");
            throw null;
        }
    }

    public void setRemoteInputVisible(boolean z) {
        this.conversationLayout.showHistoricMessages(z);
    }

    public void updateExpandability(boolean z, @NotNull View.OnClickListener onClickListener, boolean z2) {
        Intrinsics.checkNotNullParameter(onClickListener, "onClickListener");
        this.conversationLayout.updateExpandability(z, onClickListener);
    }

    public int getMinLayoutHeight() {
        View view = this.mActionsContainer;
        if (view == null || view.getVisibility() == 8) {
            return super.getMinLayoutHeight();
        }
        return this.minHeightWithActions;
    }
}
