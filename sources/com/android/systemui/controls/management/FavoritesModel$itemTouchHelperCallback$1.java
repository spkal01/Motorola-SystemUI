package com.android.systemui.controls.management;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: FavoritesModel.kt */
public final class FavoritesModel$itemTouchHelperCallback$1 extends ItemTouchHelper.SimpleCallback {
    private final int MOVEMENT = 15;
    final /* synthetic */ FavoritesModel this$0;

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        Intrinsics.checkNotNullParameter(viewHolder, "viewHolder");
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    FavoritesModel$itemTouchHelperCallback$1(FavoritesModel favoritesModel) {
        super(0, 0);
        this.this$0 = favoritesModel;
    }

    public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder viewHolder2) {
        Intrinsics.checkNotNullParameter(recyclerView, "recyclerView");
        Intrinsics.checkNotNullParameter(viewHolder, "viewHolder");
        Intrinsics.checkNotNullParameter(viewHolder2, "target");
        this.this$0.onMoveItem(viewHolder.getBindingAdapterPosition(), viewHolder2.getBindingAdapterPosition());
        return true;
    }

    public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        Intrinsics.checkNotNullParameter(recyclerView, "recyclerView");
        Intrinsics.checkNotNullParameter(viewHolder, "viewHolder");
        if (viewHolder.getBindingAdapterPosition() < this.this$0.dividerPosition) {
            return ItemTouchHelper.Callback.makeMovementFlags(this.MOVEMENT, 0);
        }
        return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
    }

    public boolean canDropOver(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder viewHolder2) {
        Intrinsics.checkNotNullParameter(recyclerView, "recyclerView");
        Intrinsics.checkNotNullParameter(viewHolder, "current");
        Intrinsics.checkNotNullParameter(viewHolder2, "target");
        return viewHolder2.getBindingAdapterPosition() < this.this$0.dividerPosition;
    }
}
