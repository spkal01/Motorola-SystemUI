package com.android.p011wm.shell.bubbles;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.util.ContrastColorUtil;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.bubbles.BubbleData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* renamed from: com.android.wm.shell.bubbles.BubbleOverflowContainerView */
public class BubbleOverflowContainerView extends LinearLayout {
    /* access modifiers changed from: private */
    public BubbleOverflowAdapter mAdapter;
    private BubbleController mController;
    private final BubbleData.Listener mDataListener;
    private LinearLayout mEmptyState;
    private ImageView mEmptyStateImage;
    private TextView mEmptyStateSubtitle;
    private TextView mEmptyStateTitle;
    private View.OnKeyListener mKeyListener;
    /* access modifiers changed from: private */
    public List<Bubble> mOverflowBubbles;
    private RecyclerView mRecyclerView;

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 4) {
            return false;
        }
        this.mController.collapseStack();
        return true;
    }

    /* renamed from: com.android.wm.shell.bubbles.BubbleOverflowContainerView$OverflowGridLayoutManager */
    private class OverflowGridLayoutManager extends GridLayoutManager {
        OverflowGridLayoutManager(Context context, int i) {
            super(context, i);
        }

        public int getColumnCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
            int itemCount = state.getItemCount();
            int columnCountForAccessibility = super.getColumnCountForAccessibility(recycler, state);
            return itemCount < columnCountForAccessibility ? itemCount : columnCountForAccessibility;
        }
    }

    public BubbleOverflowContainerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BubbleOverflowContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BubbleOverflowContainerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BubbleOverflowContainerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOverflowBubbles = new ArrayList();
        this.mKeyListener = new BubbleOverflowContainerView$$ExternalSyntheticLambda0(this);
        this.mDataListener = new BubbleData.Listener() {
            public void applyUpdate(BubbleData.Update update) {
                Bubble bubble = update.removedOverflowBubble;
                if (bubble != null) {
                    bubble.cleanupViews();
                    int indexOf = BubbleOverflowContainerView.this.mOverflowBubbles.indexOf(bubble);
                    BubbleOverflowContainerView.this.mOverflowBubbles.remove(bubble);
                    BubbleOverflowContainerView.this.mAdapter.notifyItemRemoved(indexOf);
                }
                Bubble bubble2 = update.addedOverflowBubble;
                if (bubble2 != null) {
                    int indexOf2 = BubbleOverflowContainerView.this.mOverflowBubbles.indexOf(bubble2);
                    if (indexOf2 > 0) {
                        BubbleOverflowContainerView.this.mOverflowBubbles.remove(bubble2);
                        BubbleOverflowContainerView.this.mOverflowBubbles.add(0, bubble2);
                        BubbleOverflowContainerView.this.mAdapter.notifyItemMoved(indexOf2, 0);
                    } else {
                        BubbleOverflowContainerView.this.mOverflowBubbles.add(0, bubble2);
                        BubbleOverflowContainerView.this.mAdapter.notifyItemInserted(0);
                    }
                }
                BubbleOverflowContainerView.this.updateEmptyStateVisibility();
            }
        };
        setFocusableInTouchMode(true);
    }

    public void setBubbleController(BubbleController bubbleController) {
        this.mController = bubbleController;
    }

    public void show() {
        requestFocus();
        updateOverflow();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mRecyclerView = (RecyclerView) findViewById(C2219R.C2222id.bubble_overflow_recycler);
        this.mEmptyState = (LinearLayout) findViewById(C2219R.C2222id.bubble_overflow_empty_state);
        this.mEmptyStateTitle = (TextView) findViewById(C2219R.C2222id.bubble_overflow_empty_title);
        this.mEmptyStateSubtitle = (TextView) findViewById(C2219R.C2222id.bubble_overflow_empty_subtitle);
        this.mEmptyStateImage = (ImageView) findViewById(C2219R.C2222id.bubble_overflow_empty_state_image);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        BubbleController bubbleController = this.mController;
        if (bubbleController != null) {
            bubbleController.updateWindowFlagsForOverflow(true);
        }
        setOnKeyListener(this.mKeyListener);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BubbleController bubbleController = this.mController;
        if (bubbleController != null) {
            bubbleController.updateWindowFlagsForOverflow(false);
        }
        setOnKeyListener((View.OnKeyListener) null);
    }

    /* access modifiers changed from: package-private */
    public void updateOverflow() {
        this.mRecyclerView.setLayoutManager(new OverflowGridLayoutManager(getContext(), getResources().getInteger(C2219R.integer.bubbles_overflow_columns)));
        Context context = getContext();
        List<Bubble> list = this.mOverflowBubbles;
        BubbleController bubbleController = this.mController;
        Objects.requireNonNull(bubbleController);
        BubbleOverflowAdapter bubbleOverflowAdapter = new BubbleOverflowAdapter(context, list, new BubbleOverflowContainerView$$ExternalSyntheticLambda1(bubbleController), this.mController.getPositioner());
        this.mAdapter = bubbleOverflowAdapter;
        this.mRecyclerView.setAdapter(bubbleOverflowAdapter);
        this.mOverflowBubbles.clear();
        this.mOverflowBubbles.addAll(this.mController.getOverflowBubbles());
        this.mAdapter.notifyDataSetChanged();
        this.mController.setOverflowListener(this.mDataListener);
        updateEmptyStateVisibility();
        updateTheme();
    }

    /* access modifiers changed from: package-private */
    public void updateEmptyStateVisibility() {
        int i = 0;
        this.mEmptyState.setVisibility(this.mOverflowBubbles.isEmpty() ? 0 : 8);
        RecyclerView recyclerView = this.mRecyclerView;
        if (this.mOverflowBubbles.isEmpty()) {
            i = 8;
        }
        recyclerView.setVisibility(i);
    }

    /* access modifiers changed from: package-private */
    public void updateTheme() {
        Drawable drawable;
        int i;
        Resources resources = getResources();
        boolean z = (resources.getConfiguration().uiMode & 48) == 32;
        ImageView imageView = this.mEmptyStateImage;
        if (z) {
            drawable = resources.getDrawable(C2219R.C2221drawable.bubble_ic_empty_overflow_dark);
        } else {
            drawable = resources.getDrawable(C2219R.C2221drawable.bubble_ic_empty_overflow_light);
        }
        imageView.setImageDrawable(drawable);
        View findViewById = findViewById(C2219R.C2222id.bubble_overflow_container);
        if (z) {
            i = resources.getColor(C2219R.C2220color.bubbles_dark);
        } else {
            i = resources.getColor(C2219R.C2220color.bubbles_light);
        }
        findViewById.setBackgroundColor(i);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(new int[]{16844002, 16842808});
        int i2 = -16777216;
        int color = obtainStyledAttributes.getColor(0, z ? -16777216 : -1);
        if (z) {
            i2 = -1;
        }
        int ensureTextContrast = ContrastColorUtil.ensureTextContrast(obtainStyledAttributes.getColor(1, i2), color, z);
        obtainStyledAttributes.recycle();
        setBackgroundColor(color);
        this.mEmptyStateTitle.setTextColor(ensureTextContrast);
        this.mEmptyStateSubtitle.setTextColor(ensureTextContrast);
    }

    public void updateFontSize() {
        float dimensionPixelSize = (float) this.mContext.getResources().getDimensionPixelSize(17105550);
        this.mEmptyStateTitle.setTextSize(0, dimensionPixelSize);
        this.mEmptyStateSubtitle.setTextSize(0, dimensionPixelSize);
    }
}
