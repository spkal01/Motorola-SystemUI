package com.android.systemui.p006qs.customize;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.systemui.Prefs;
import com.android.systemui.R$drawable;
import com.android.systemui.R$integer;
import com.android.systemui.R$layout;
import com.android.systemui.R$string;
import com.android.systemui.moto.MotoFeature;
import com.android.systemui.p006qs.QSEditEvent;
import com.android.systemui.p006qs.QSTileHost;
import com.android.systemui.p006qs.customize.TileQueryHelper;
import com.android.systemui.p006qs.external.CustomTile;
import com.android.systemui.p006qs.tileimpl.QSIconViewImpl;
import com.android.systemui.p006qs.tileimpl.QSTileViewImpl;
import com.android.systemui.plugins.p005qs.QSTile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* renamed from: com.android.systemui.qs.customize.TileAdapter */
public class TileAdapter extends RecyclerView.Adapter<Holder> implements TileQueryHelper.TileStateListener {
    private static final boolean DEBUG = (!Build.IS_USER);
    private static final int NUM_COLUMNS_ID = R$integer.quick_settings_num_columns;
    private static final int PRC_NUM_COLUMNS_ID = R$integer.zz_moto_prc_qs_num_columns;
    /* access modifiers changed from: private */
    public int mAccessibilityAction = 0;
    /* access modifiers changed from: private */
    public final AccessibilityDelegateCompat mAccessibilityDelegate;
    private int mAccessibilityFromIndex;
    private List<TileQueryHelper.TileInfo> mAllTiles;
    private final ItemTouchHelper.Callback mCallbacks;
    private final Context mContext;
    /* access modifiers changed from: private */
    public Holder mCurrentDrag;
    private List<String> mCurrentSpecs;
    private RecyclerView.ItemDecoration mDecoration;
    /* access modifiers changed from: private */
    public int mEditIndex;
    private int mFocusIndex;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private final QSTileHost mHost;
    /* access modifiers changed from: private */
    public boolean mIsPrcCustom;
    private final ItemTouchHelper mItemTouchHelper;
    private final MarginTileDecoration mMarginDecoration;
    private final int mMinNumTiles;
    private boolean mNeedsFocus;
    /* access modifiers changed from: private */
    public int mNumColumns;
    private List<TileQueryHelper.TileInfo> mOtherTiles;
    private RecyclerView mRecyclerView;
    private final GridLayoutManager.SpanSizeLookup mSizeLookup;
    private int mTileDividerIndex;
    /* access modifiers changed from: private */
    public final List<TileQueryHelper.TileInfo> mTiles = new ArrayList();
    private final UiEventLogger mUiEventLogger;

    public TileAdapter(Context context, QSTileHost qSTileHost, UiEventLogger uiEventLogger) {
        C12254 r0 = new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                int itemViewType = TileAdapter.this.getItemViewType(i);
                if (itemViewType == 1 || itemViewType == 4 || itemViewType == 3) {
                    return TileAdapter.this.mNumColumns;
                }
                return 1;
            }
        };
        this.mSizeLookup = r0;
        C12265 r1 = new ItemTouchHelper.Callback() {
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            public boolean isLongPressDragEnabled() {
                return true;
            }

            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            }

            public void animateViewHolder(Holder holder, boolean z) {
                if (z) {
                    holder.startDrag();
                    TileAdapter tileAdapter = TileAdapter.this;
                    tileAdapter.notifyItemChanged(tileAdapter.mEditIndex);
                    return;
                }
                holder.stopDrag();
            }

            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
                super.onSelectedChanged(viewHolder, i);
                if (i != 2) {
                    viewHolder = null;
                }
                if (viewHolder != TileAdapter.this.mCurrentDrag) {
                    if (TileAdapter.this.mCurrentDrag != null) {
                        int adapterPosition = TileAdapter.this.mCurrentDrag.getAdapterPosition();
                        if (adapterPosition != -1) {
                            TileQueryHelper.TileInfo tileInfo = (TileQueryHelper.TileInfo) TileAdapter.this.mTiles.get(adapterPosition);
                            if (TileAdapter.this.mIsPrcCustom) {
                                ((PrcCustomizeTileView) TileAdapter.this.mCurrentDrag.mTileView).setShowAppLabel(adapterPosition > TileAdapter.this.mEditIndex && !tileInfo.isSystem);
                            } else {
                                ((CustomizeTileView) TileAdapter.this.mCurrentDrag.mTileView).setShowAppLabel(adapterPosition > TileAdapter.this.mEditIndex && !tileInfo.isSystem);
                            }
                            animateViewHolder(TileAdapter.this.mCurrentDrag, false);
                            Holder unused = TileAdapter.this.mCurrentDrag = null;
                        } else {
                            return;
                        }
                    }
                    if (viewHolder != null) {
                        Holder unused2 = TileAdapter.this.mCurrentDrag = (Holder) viewHolder;
                        animateViewHolder(TileAdapter.this.mCurrentDrag, true);
                    }
                }
            }

            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                int adapterPosition = viewHolder2.getAdapterPosition();
                if (adapterPosition == 0 || adapterPosition == -1) {
                    return false;
                }
                if (TileAdapter.this.canRemoveTiles() || viewHolder.getAdapterPosition() >= TileAdapter.this.mEditIndex) {
                    if (adapterPosition <= TileAdapter.this.mEditIndex + 1) {
                        return true;
                    }
                    return false;
                } else if (adapterPosition < TileAdapter.this.mEditIndex) {
                    return true;
                } else {
                    return false;
                }
            }

            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType == 1 || itemViewType == 2 || itemViewType == 3 || itemViewType == 4) {
                    return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
                }
                return ItemTouchHelper.Callback.makeMovementFlags(15, 0);
            }

            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
                int adapterPosition = viewHolder.getAdapterPosition();
                int adapterPosition2 = viewHolder2.getAdapterPosition();
                if (adapterPosition == 0 || adapterPosition == -1 || adapterPosition2 == 0 || adapterPosition2 == -1) {
                    return false;
                }
                return TileAdapter.this.move(adapterPosition, adapterPosition2);
            }

            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                ((Holder) viewHolder).stopDrag();
                super.clearView(recyclerView, viewHolder);
                TileAdapter.this.mHandler.post(new Runnable() {
                    public void run() {
                        TileAdapter tileAdapter = TileAdapter.this;
                        tileAdapter.notifyItemChanged(tileAdapter.mEditIndex);
                    }
                });
            }
        };
        this.mCallbacks = r1;
        this.mContext = context;
        this.mHost = qSTileHost;
        this.mUiEventLogger = uiEventLogger;
        this.mItemTouchHelper = new ItemTouchHelper(r1);
        this.mDecoration = new TileItemDecoration(context);
        MarginTileDecoration marginTileDecoration = new MarginTileDecoration();
        this.mMarginDecoration = marginTileDecoration;
        boolean isCustomPanelView = MotoFeature.getInstance(context).isCustomPanelView();
        this.mIsPrcCustom = isCustomPanelView;
        marginTileDecoration.setPrcCustom(isCustomPanelView);
        this.mMinNumTiles = context.getResources().getInteger(R$integer.quick_settings_min_num_tiles);
        if (this.mIsPrcCustom) {
            this.mNumColumns = context.getResources().getInteger(PRC_NUM_COLUMNS_ID);
        } else {
            this.mNumColumns = context.getResources().getInteger(NUM_COLUMNS_ID);
        }
        this.mAccessibilityDelegate = new TileAdapterDelegate();
        r0.setSpanIndexCacheEnabled(true);
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = null;
    }

    public boolean updateNumColumns() {
        int i;
        if (this.mIsPrcCustom) {
            i = this.mContext.getResources().getInteger(PRC_NUM_COLUMNS_ID);
        } else {
            i = this.mContext.getResources().getInteger(NUM_COLUMNS_ID);
        }
        if (i == this.mNumColumns) {
            return false;
        }
        this.mNumColumns = i;
        return true;
    }

    public int getNumColumns() {
        return this.mNumColumns;
    }

    public ItemTouchHelper getItemTouchHelper() {
        return this.mItemTouchHelper;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return this.mDecoration;
    }

    public RecyclerView.ItemDecoration getMarginItemDecoration() {
        return this.mMarginDecoration;
    }

    public void changeHalfMargin(int i) {
        this.mMarginDecoration.setHalfMargin(i);
    }

    public void saveSpecs(QSTileHost qSTileHost) {
        ArrayList arrayList = new ArrayList();
        clearAccessibilityState();
        int i = 1;
        while (i < this.mTiles.size() && this.mTiles.get(i) != null) {
            arrayList.add(this.mTiles.get(i).spec);
            i++;
        }
        qSTileHost.changeTiles(this.mCurrentSpecs, arrayList);
        this.mCurrentSpecs = arrayList;
    }

    private void clearAccessibilityState() {
        if (this.mAccessibilityAction == 1) {
            List<TileQueryHelper.TileInfo> list = this.mTiles;
            int i = this.mEditIndex - 1;
            this.mEditIndex = i;
            list.remove(i);
            notifyDataSetChanged();
        }
        this.mAccessibilityAction = 0;
    }

    public void resetTileSpecs(List<String> list) {
        this.mHost.changeTiles(this.mCurrentSpecs, list);
        setTileSpecs(list);
        if (DEBUG) {
            Log.i("TileAdapter", "set QS_TILE_CUSTOMIZER_CHANGED false");
        }
        Prefs.putBoolean(this.mContext, "QsTileCustomizerChanged", false);
    }

    public void setTileSpecs(List<String> list) {
        if (this.mIsPrcCustom) {
            if (list.contains("internet")) {
                list.remove("internet");
            }
            List<String> prcFixedTiles = getPrcFixedTiles(this.mContext);
            for (int i = 0; i < prcFixedTiles.size(); i++) {
                String str = prcFixedTiles.get(i);
                if (list.contains(str)) {
                    list.remove(str);
                }
            }
        }
        if (!list.equals(this.mCurrentSpecs)) {
            this.mCurrentSpecs = list;
            recalcSpecs();
        }
    }

    private List<String> getPrcFixedTiles(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(Arrays.asList(context.getResources().getString(R$string.zz_moto_prc_fixed_tiles).split(",")));
        return arrayList;
    }

    private void removeFixedTiles(String str) {
        for (int i = 0; i < this.mAllTiles.size(); i++) {
            TileQueryHelper.TileInfo tileInfo = this.mAllTiles.get(i);
            if (tileInfo != null && tileInfo.spec.equals(str)) {
                this.mAllTiles.remove(i);
            }
        }
    }

    public void onTilesChanged(List<TileQueryHelper.TileInfo> list) {
        this.mAllTiles = list;
        if (this.mIsPrcCustom) {
            removeFixedTiles("internet");
            List<String> prcFixedTiles = getPrcFixedTiles(this.mContext);
            for (int i = 0; i < prcFixedTiles.size(); i++) {
                removeFixedTiles(prcFixedTiles.get(i));
            }
        }
        recalcSpecs();
    }

    private void recalcSpecs() {
        if (this.mCurrentSpecs != null && this.mAllTiles != null) {
            this.mOtherTiles = new ArrayList(this.mAllTiles);
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (int i2 = 0; i2 < this.mCurrentSpecs.size(); i2++) {
                TileQueryHelper.TileInfo andRemoveOther = getAndRemoveOther(this.mCurrentSpecs.get(i2));
                if (andRemoveOther != null) {
                    arrayList.add(andRemoveOther);
                }
            }
            arrayList.add((Object) null);
            while (i < this.mOtherTiles.size()) {
                TileQueryHelper.TileInfo tileInfo = this.mOtherTiles.get(i);
                if (tileInfo != null && tileInfo.isSystem) {
                    this.mOtherTiles.remove(i);
                    arrayList.add(tileInfo);
                    i--;
                }
                i++;
            }
            this.mTileDividerIndex = arrayList.size();
            arrayList.add((Object) null);
            arrayList.addAll(this.mOtherTiles);
            if (!arrayList.equals(this.mTiles)) {
                this.mTiles.clear();
                this.mTiles.add((Object) null);
                this.mTiles.addAll(arrayList);
                updateDividerLocations();
                notifyDataSetChanged();
            }
        }
    }

    private TileQueryHelper.TileInfo getAndRemoveOther(String str) {
        for (int i = 0; i < this.mOtherTiles.size(); i++) {
            TileQueryHelper.TileInfo tileInfo = this.mOtherTiles.get(i);
            if (tileInfo != null && tileInfo.spec.equals(str)) {
                return this.mOtherTiles.remove(i);
            }
        }
        return null;
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 3;
        }
        if (this.mAccessibilityAction == 1 && i == this.mEditIndex - 1) {
            return 2;
        }
        if (i == this.mTileDividerIndex) {
            return 4;
        }
        if (this.mTiles.get(i) == null) {
            return 1;
        }
        return 0;
    }

    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        FrameLayout frameLayout;
        View view;
        Context context = viewGroup.getContext();
        LayoutInflater from = LayoutInflater.from(context);
        if (i == 3) {
            if (this.mIsPrcCustom) {
                return new Holder(from.inflate(R$layout.prc_qs_customize_header, viewGroup, false));
            }
            return new Holder(from.inflate(R$layout.qs_customize_header, viewGroup, false));
        } else if (i == 4) {
            if (this.mIsPrcCustom) {
                return new Holder(from.inflate(R$layout.prc_qs_customize_tile_divider, viewGroup, false));
            }
            return new Holder(from.inflate(R$layout.qs_customize_tile_divider, viewGroup, false));
        } else if (i != 1) {
            if (this.mIsPrcCustom) {
                frameLayout = (FrameLayout) from.inflate(R$layout.prc_qs_customize_tile_frame, viewGroup, false);
            } else {
                frameLayout = (FrameLayout) from.inflate(R$layout.qs_customize_tile_frame, viewGroup, false);
            }
            if (this.mIsPrcCustom) {
                view = new PrcCustomizeTileView(context, new QSIconViewImpl(context));
                frameLayout.getLayoutParams().height = ((viewGroup.getWidth() - ((this.mNumColumns * 2) * this.mMarginDecoration.mHalfMargin)) - (viewGroup.getPaddingStart() + viewGroup.getPaddingEnd())) / this.mNumColumns;
            } else {
                view = new CustomizeTileView(context, new QSIconViewImpl(context));
            }
            frameLayout.addView(view);
            return new Holder(frameLayout);
        } else if (this.mIsPrcCustom) {
            return new Holder(from.inflate(R$layout.prc_qs_customize_divider, viewGroup, false));
        } else {
            return new Holder(from.inflate(R$layout.qs_customize_divider, viewGroup, false));
        }
    }

    public int getItemCount() {
        return this.mTiles.size();
    }

    public boolean onFailedToRecycleView(Holder holder) {
        holder.stopDrag();
        holder.clearDrag();
        return true;
    }

    private void setSelectableForHeaders(View view) {
        int i = 1;
        boolean z = this.mAccessibilityAction == 0;
        view.setFocusable(z);
        if (!z) {
            i = 4;
        }
        view.setImportantForAccessibility(i);
        view.setFocusableInTouchMode(z);
    }

    public void onBindViewHolder(final Holder holder, int i) {
        String str;
        if (holder.getItemViewType() == 3) {
            setSelectableForHeaders(holder.itemView);
            return;
        }
        int i2 = 4;
        boolean z = false;
        if (holder.getItemViewType() == 4) {
            View view = holder.itemView;
            if (this.mTileDividerIndex < this.mTiles.size() - 1) {
                i2 = 0;
            }
            view.setVisibility(i2);
        } else if (holder.getItemViewType() == 1) {
            Resources resources = this.mContext.getResources();
            if (this.mCurrentDrag == null) {
                str = resources.getString(R$string.drag_to_add_tiles);
            } else if (canRemoveTiles() || this.mCurrentDrag.getAdapterPosition() >= this.mEditIndex) {
                str = resources.getString(R$string.drag_to_remove_tiles);
            } else {
                str = resources.getString(R$string.drag_to_remove_disabled, new Object[]{Integer.valueOf(this.mMinNumTiles)});
            }
            ((TextView) holder.itemView.findViewById(16908310)).setText(str);
            setSelectableForHeaders(holder.itemView);
        } else if (holder.getItemViewType() == 2) {
            holder.mTileView.setClickable(true);
            holder.mTileView.setFocusable(true);
            holder.mTileView.setFocusableInTouchMode(true);
            holder.mTileView.setVisibility(0);
            holder.mTileView.setImportantForAccessibility(1);
            holder.mTileView.setContentDescription(this.mContext.getString(R$string.accessibility_qs_edit_tile_add_to_position, new Object[]{Integer.valueOf(i)}));
            holder.mTileView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    TileAdapter.this.selectPosition(holder.getLayoutPosition());
                }
            });
            focusOnHolder(holder);
        } else {
            TileQueryHelper.TileInfo tileInfo = this.mTiles.get(i);
            boolean z2 = i > 0 && i < this.mEditIndex;
            if (z2 && this.mAccessibilityAction == 1) {
                tileInfo.state.contentDescription = this.mContext.getString(R$string.accessibility_qs_edit_tile_add_to_position, new Object[]{Integer.valueOf(i)});
            } else if (!z2 || this.mAccessibilityAction != 2) {
                QSTile.State state = tileInfo.state;
                state.contentDescription = state.label;
            } else {
                tileInfo.state.contentDescription = this.mContext.getString(R$string.accessibility_qs_edit_tile_move_to_position, new Object[]{Integer.valueOf(i)});
            }
            tileInfo.state.expandedAccessibilityClassName = "";
            if (this.mIsPrcCustom) {
                holder.getTileAsPrcCustomizeView().changeState(tileInfo.state);
                holder.getTileAsPrcCustomizeView().setShowAppLabel(i > this.mEditIndex && !tileInfo.isSystem);
                PrcCustomizeTileView tileAsPrcCustomizeView = holder.getTileAsPrcCustomizeView();
                if (i < this.mEditIndex || tileInfo.isSystem) {
                    z = true;
                }
                tileAsPrcCustomizeView.setShowSideView(z);
            } else {
                holder.getTileAsCustomizeView().changeState(tileInfo.state);
                holder.getTileAsCustomizeView().setShowAppLabel(i > this.mEditIndex && !tileInfo.isSystem);
                CustomizeTileView tileAsCustomizeView = holder.getTileAsCustomizeView();
                if (i < this.mEditIndex || tileInfo.isSystem) {
                    z = true;
                }
                tileAsCustomizeView.setShowSideView(z);
            }
            holder.mTileView.setSelected(true);
            holder.mTileView.setImportantForAccessibility(1);
            holder.mTileView.setClickable(true);
            holder.mTileView.setOnClickListener((View.OnClickListener) null);
            holder.mTileView.setFocusable(true);
            holder.mTileView.setFocusableInTouchMode(true);
            if (this.mAccessibilityAction != 0) {
                holder.mTileView.setClickable(z2);
                holder.mTileView.setFocusable(z2);
                holder.mTileView.setFocusableInTouchMode(z2);
                QSTileViewImpl access$300 = holder.mTileView;
                if (z2) {
                    i2 = 1;
                }
                access$300.setImportantForAccessibility(i2);
                if (z2) {
                    holder.mTileView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            int layoutPosition = holder.getLayoutPosition();
                            if (layoutPosition != -1 && TileAdapter.this.mAccessibilityAction != 0) {
                                TileAdapter.this.selectPosition(layoutPosition);
                            }
                        }
                    });
                }
            }
            if (i == this.mFocusIndex) {
                focusOnHolder(holder);
            }
        }
    }

    private void focusOnHolder(final Holder holder) {
        if (this.mNeedsFocus) {
            holder.mTileView.requestLayout();
            holder.mTileView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    holder.mTileView.removeOnLayoutChangeListener(this);
                    holder.mTileView.requestFocus();
                    if (TileAdapter.this.mAccessibilityAction == 0) {
                        holder.mTileView.clearFocus();
                    }
                }
            });
            this.mNeedsFocus = false;
            this.mFocusIndex = -1;
        }
    }

    /* access modifiers changed from: private */
    public boolean canRemoveTiles() {
        return this.mCurrentSpecs.size() > this.mMinNumTiles;
    }

    /* access modifiers changed from: private */
    public void selectPosition(int i) {
        if (this.mAccessibilityAction == 1) {
            List<TileQueryHelper.TileInfo> list = this.mTiles;
            int i2 = this.mEditIndex;
            this.mEditIndex = i2 - 1;
            list.remove(i2);
        }
        this.mAccessibilityAction = 0;
        move(this.mAccessibilityFromIndex, i, false);
        this.mFocusIndex = i;
        this.mNeedsFocus = true;
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void startAccessibleAdd(int i) {
        this.mAccessibilityFromIndex = i;
        this.mAccessibilityAction = 1;
        List<TileQueryHelper.TileInfo> list = this.mTiles;
        int i2 = this.mEditIndex;
        this.mEditIndex = i2 + 1;
        list.add(i2, (Object) null);
        this.mTileDividerIndex++;
        this.mFocusIndex = this.mEditIndex - 1;
        this.mNeedsFocus = true;
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.post(new TileAdapter$$ExternalSyntheticLambda0(this));
        }
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startAccessibleAdd$0() {
        this.mRecyclerView.smoothScrollToPosition(this.mFocusIndex);
    }

    /* access modifiers changed from: private */
    public void startAccessibleMove(int i) {
        this.mAccessibilityFromIndex = i;
        this.mAccessibilityAction = 2;
        this.mFocusIndex = i;
        this.mNeedsFocus = true;
        notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public boolean canRemoveFromPosition(int i) {
        return canRemoveTiles() && isCurrentTile(i);
    }

    /* access modifiers changed from: private */
    public boolean isCurrentTile(int i) {
        return i < this.mEditIndex;
    }

    /* access modifiers changed from: private */
    public boolean canAddFromPosition(int i) {
        return i > this.mEditIndex;
    }

    /* access modifiers changed from: private */
    public boolean addFromPosition(int i) {
        if (!canAddFromPosition(i)) {
            return false;
        }
        move(i, this.mEditIndex);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean removeFromPosition(int i) {
        if (!canRemoveFromPosition(i)) {
            return false;
        }
        move(i, this.mTiles.get(i).isSystem ? this.mEditIndex : this.mTileDividerIndex);
        return true;
    }

    public GridLayoutManager.SpanSizeLookup getSizeLookup() {
        return this.mSizeLookup;
    }

    /* access modifiers changed from: private */
    public boolean move(int i, int i2) {
        return move(i, i2, true);
    }

    private boolean move(int i, int i2, boolean z) {
        if (i2 == i) {
            return true;
        }
        move(i, i2, this.mTiles, z);
        updateDividerLocations();
        int i3 = this.mEditIndex;
        if (i2 >= i3) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_REMOVE, 0, strip(this.mTiles.get(i2)));
        } else if (i >= i3) {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_ADD, 0, strip(this.mTiles.get(i2)));
        } else {
            this.mUiEventLogger.log(QSEditEvent.QS_EDIT_MOVE, 0, strip(this.mTiles.get(i2)));
        }
        if (DEBUG) {
            Log.i("TileAdapter", "set QS_TILE_CUSTOMIZER_CHANGED true");
        }
        Prefs.putBoolean(this.mContext, "QsTileCustomizerChanged", true);
        saveSpecs(this.mHost);
        return true;
    }

    private void updateDividerLocations() {
        this.mEditIndex = -1;
        this.mTileDividerIndex = this.mTiles.size();
        for (int i = 1; i < this.mTiles.size(); i++) {
            if (this.mTiles.get(i) == null) {
                if (this.mEditIndex == -1) {
                    this.mEditIndex = i;
                } else {
                    this.mTileDividerIndex = i;
                }
            }
        }
        int size = this.mTiles.size() - 1;
        int i2 = this.mTileDividerIndex;
        if (size == i2) {
            notifyItemChanged(i2);
        }
    }

    private static String strip(TileQueryHelper.TileInfo tileInfo) {
        String str = tileInfo.spec;
        return str.startsWith("custom(") ? CustomTile.getComponentFromSpec(str).getPackageName() : str;
    }

    private <T> void move(int i, int i2, List<T> list, boolean z) {
        list.add(i2, list.remove(i));
        if (z) {
            notifyItemMoved(i, i2);
        }
    }

    /* renamed from: com.android.systemui.qs.customize.TileAdapter$Holder */
    public class Holder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public QSTileViewImpl mTileView;

        public Holder(View view) {
            super(view);
            if (view instanceof FrameLayout) {
                QSTileViewImpl qSTileViewImpl = (QSTileViewImpl) ((FrameLayout) view).getChildAt(0);
                this.mTileView = qSTileViewImpl;
                qSTileViewImpl.getIcon().disableAnimation();
                this.mTileView.setTag(this);
                ViewCompat.setAccessibilityDelegate(this.mTileView, TileAdapter.this.mAccessibilityDelegate);
            }
        }

        public CustomizeTileView getTileAsCustomizeView() {
            return (CustomizeTileView) this.mTileView;
        }

        public PrcCustomizeTileView getTileAsPrcCustomizeView() {
            return (PrcCustomizeTileView) this.mTileView;
        }

        public void clearDrag() {
            this.itemView.clearAnimation();
            this.itemView.setScaleX(1.0f);
            this.itemView.setScaleY(1.0f);
        }

        public void startDrag() {
            this.itemView.animate().setDuration(100).scaleX(1.2f).scaleY(1.2f);
        }

        public void stopDrag() {
            this.itemView.animate().setDuration(100).scaleX(1.0f).scaleY(1.0f);
        }

        /* access modifiers changed from: package-private */
        public boolean canRemove() {
            return TileAdapter.this.canRemoveFromPosition(getLayoutPosition());
        }

        /* access modifiers changed from: package-private */
        public boolean canAdd() {
            return TileAdapter.this.canAddFromPosition(getLayoutPosition());
        }

        /* access modifiers changed from: package-private */
        public void toggleState() {
            if (canAdd()) {
                add();
            } else {
                remove();
            }
        }

        private void add() {
            if (TileAdapter.this.addFromPosition(getLayoutPosition())) {
                View view = this.itemView;
                view.announceForAccessibility(view.getContext().getText(R$string.accessibility_qs_edit_tile_added));
            }
        }

        private void remove() {
            if (TileAdapter.this.removeFromPosition(getLayoutPosition())) {
                View view = this.itemView;
                view.announceForAccessibility(view.getContext().getText(R$string.accessibility_qs_edit_tile_removed));
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isCurrentTile() {
            return TileAdapter.this.isCurrentTile(getLayoutPosition());
        }

        /* access modifiers changed from: package-private */
        public void startAccessibleAdd() {
            TileAdapter.this.startAccessibleAdd(getLayoutPosition());
        }

        /* access modifiers changed from: package-private */
        public void startAccessibleMove() {
            TileAdapter.this.startAccessibleMove(getLayoutPosition());
        }

        /* access modifiers changed from: package-private */
        public boolean canTakeAccessibleAction() {
            return TileAdapter.this.mAccessibilityAction == 0;
        }
    }

    /* renamed from: com.android.systemui.qs.customize.TileAdapter$TileItemDecoration */
    private class TileItemDecoration extends RecyclerView.ItemDecoration {
        private final Drawable mDrawable;

        private TileItemDecoration(Context context) {
            this.mDrawable = context.getDrawable(R$drawable.qs_customize_tile_decoration);
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            super.onDraw(canvas, recyclerView, state);
            int childCount = recyclerView.getChildCount();
            int width = recyclerView.getWidth();
            int bottom = recyclerView.getBottom();
            int i = 0;
            while (i < childCount) {
                View childAt = recyclerView.getChildAt(i);
                RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(childAt);
                if (childViewHolder == TileAdapter.this.mCurrentDrag || childViewHolder.getAdapterPosition() == 0 || (childViewHolder.getAdapterPosition() < TileAdapter.this.mEditIndex && !(childAt instanceof TextView))) {
                    i++;
                } else if (!TileAdapter.this.mIsPrcCustom) {
                    this.mDrawable.setBounds(0, childAt.getTop() + Math.round(ViewCompat.getTranslationY(childAt)), width, bottom);
                    this.mDrawable.draw(canvas);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: com.android.systemui.qs.customize.TileAdapter$MarginTileDecoration */
    private static class MarginTileDecoration extends RecyclerView.ItemDecoration {
        /* access modifiers changed from: private */
        public int mHalfMargin;
        private boolean mIsPrcCustom;

        private MarginTileDecoration() {
        }

        public void setPrcCustom(boolean z) {
            this.mIsPrcCustom = z;
        }

        public void setHalfMargin(int i) {
            this.mHalfMargin = i;
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            if (recyclerView.getLayoutManager() != null) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int spanIndex = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
                if (view instanceof TextView) {
                    super.getItemOffsets(rect, view, recyclerView, state);
                } else if (spanIndex != 0 && spanIndex != gridLayoutManager.getSpanCount() - 1) {
                    int i = this.mHalfMargin;
                    rect.left = i;
                    rect.right = i;
                } else if (recyclerView.isLayoutRtl()) {
                    if (spanIndex == 0) {
                        int i2 = this.mHalfMargin;
                        rect.left = i2;
                        if (this.mIsPrcCustom) {
                            rect.right = i2;
                        } else {
                            rect.right = 0;
                        }
                    } else {
                        int i3 = this.mHalfMargin;
                        rect.right = i3;
                        if (this.mIsPrcCustom) {
                            rect.left = i3;
                        } else {
                            rect.left = 0;
                        }
                    }
                } else if (spanIndex == 0) {
                    int i4 = this.mHalfMargin;
                    rect.right = i4;
                    if (this.mIsPrcCustom) {
                        rect.left = i4;
                    } else {
                        rect.left = 0;
                    }
                } else {
                    int i5 = this.mHalfMargin;
                    rect.left = i5;
                    if (this.mIsPrcCustom) {
                        rect.right = i5;
                    } else {
                        rect.right = 0;
                    }
                }
            }
        }
    }
}
