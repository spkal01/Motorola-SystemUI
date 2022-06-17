package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.policy.CallbackController;
import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: StatusBarLocationPublisher.kt */
public final class StatusBarLocationPublisher implements CallbackController<StatusBarMarginUpdatedListener> {
    @NotNull
    private final Set<WeakReference<StatusBarMarginUpdatedListener>> listeners = new LinkedHashSet();
    private int marginLeft;
    private int marginRight;

    public final int getMarginLeft() {
        return this.marginLeft;
    }

    public final int getMarginRight() {
        return this.marginRight;
    }

    public void addCallback(@NotNull StatusBarMarginUpdatedListener statusBarMarginUpdatedListener) {
        Intrinsics.checkNotNullParameter(statusBarMarginUpdatedListener, "listener");
        this.listeners.add(new WeakReference(statusBarMarginUpdatedListener));
    }

    public void removeCallback(@NotNull StatusBarMarginUpdatedListener statusBarMarginUpdatedListener) {
        Intrinsics.checkNotNullParameter(statusBarMarginUpdatedListener, "listener");
        WeakReference weakReference = null;
        for (WeakReference next : this.listeners) {
            if (Intrinsics.areEqual(next.get(), (Object) statusBarMarginUpdatedListener)) {
                weakReference = next;
            }
        }
        if (weakReference != null) {
            this.listeners.remove(weakReference);
        }
    }

    public final void updateStatusBarMargin(int i, int i2) {
        this.marginLeft = i;
        this.marginRight = i2;
        notifyListeners();
    }

    private final void notifyListeners() {
        List<T> list;
        synchronized (this) {
            list = CollectionsKt___CollectionsKt.toList(this.listeners);
            Unit unit = Unit.INSTANCE;
        }
        for (T t : list) {
            if (t.get() == null) {
                this.listeners.remove(t);
            }
            StatusBarMarginUpdatedListener statusBarMarginUpdatedListener = (StatusBarMarginUpdatedListener) t.get();
            if (statusBarMarginUpdatedListener != null) {
                statusBarMarginUpdatedListener.onStatusBarMarginUpdated(getMarginLeft(), getMarginRight());
            }
        }
    }
}
