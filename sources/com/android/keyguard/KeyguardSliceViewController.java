package com.android.keyguard;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Trace;
import android.util.Log;
import android.view.Display;
import android.view.View;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.slice.Slice;
import androidx.slice.SliceViewManager;
import androidx.slice.widget.ListContent;
import androidx.slice.widget.RowContent;
import androidx.slice.widget.SliceContent;
import androidx.slice.widget.SliceLiveData;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.ViewController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyguardSliceViewController extends ViewController<KeyguardSliceView> implements Dumpable {
    /* access modifiers changed from: private */
    public final ActivityStarter mActivityStarter;
    /* access modifiers changed from: private */
    public Map<View, PendingIntent> mClickActions;
    private final ConfigurationController mConfigurationController;
    ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() {
        public void onDensityOrFontScaleChanged() {
            KeyguardSliceViewController.this.mView.onDensityOrFontScaleChanged();
        }

        public void onOverlayChanged() {
            KeyguardSliceViewController.this.mView.onOverlayChanged();
        }
    };
    private int mDisplayId;
    private final DumpManager mDumpManager;
    private Uri mKeyguardSliceUri;
    private LiveData<Slice> mLiveData;
    private int mLockScreenMode = 0;
    Observer<Slice> mObserver = new Observer<Slice>() {
        public void onChanged(Slice slice) {
            Slice unused = KeyguardSliceViewController.this.mSlice = slice;
            KeyguardSliceViewController.this.showSlice(slice);
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            PendingIntent pendingIntent = (PendingIntent) KeyguardSliceViewController.this.mClickActions.get(view);
            if (pendingIntent != null && KeyguardSliceViewController.this.mActivityStarter != null) {
                KeyguardSliceViewController.this.mActivityStarter.startPendingIntentDismissingKeyguard(pendingIntent);
            }
        }
    };
    /* access modifiers changed from: private */
    public Slice mSlice;
    TunerService.Tunable mTunable = new KeyguardSliceViewController$$ExternalSyntheticLambda0(this);
    private final TunerService mTunerService;
    /* access modifiers changed from: private */
    public KeyguardSliceView mView;

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str, String str2) {
        setupUri(str2);
    }

    public KeyguardSliceViewController(KeyguardSliceView keyguardSliceView, ActivityStarter activityStarter, ConfigurationController configurationController, TunerService tunerService, DumpManager dumpManager) {
        super(keyguardSliceView);
        this.mView = keyguardSliceView;
        this.mActivityStarter = activityStarter;
        this.mConfigurationController = configurationController;
        this.mTunerService = tunerService;
        this.mDumpManager = dumpManager;
    }

    /* access modifiers changed from: protected */
    public void onViewAttached() {
        LiveData<Slice> liveData;
        Display display = this.mView.getDisplay();
        if (display != null) {
            this.mDisplayId = display.getDisplayId();
        }
        this.mTunerService.addTunable(this.mTunable, "keyguard_slice_uri");
        if (this.mDisplayId == 0 && (liveData = this.mLiveData) != null) {
            liveData.observeForever(this.mObserver);
        }
        this.mConfigurationController.addCallback(this.mConfigurationListener);
        DumpManager dumpManager = this.mDumpManager;
        dumpManager.registerDumpable("KeyguardSliceViewCtrl@" + Integer.toHexString(hashCode()), this);
        this.mView.updateLockScreenMode(this.mLockScreenMode);
        this.mView.setOnClickListener(this.mOnClickListener);
    }

    /* access modifiers changed from: protected */
    public void onViewDetached() {
        if (this.mDisplayId == 0) {
            this.mLiveData.removeObserver(this.mObserver);
        }
        this.mTunerService.removeTunable(this.mTunable);
        this.mConfigurationController.removeCallback(this.mConfigurationListener);
        DumpManager dumpManager = this.mDumpManager;
        dumpManager.unregisterDumpable("KeyguardSliceViewCtrl@" + Integer.toHexString(hashCode()));
    }

    public void updateLockScreenMode(int i) {
        this.mLockScreenMode = i;
        this.mView.updateLockScreenMode(i);
    }

    public void setupUri(String str) {
        if (str == null) {
            str = "content://com.android.systemui.keyguard/main";
        }
        boolean z = false;
        LiveData<Slice> liveData = this.mLiveData;
        if (liveData != null && liveData.hasActiveObservers()) {
            z = true;
            this.mLiveData.removeObserver(this.mObserver);
        }
        this.mKeyguardSliceUri = Uri.parse(str);
        LiveData<Slice> fromUri = SliceLiveData.fromUri(this.mView.getContext(), this.mKeyguardSliceUri);
        this.mLiveData = fromUri;
        if (z) {
            fromUri.observeForever(this.mObserver);
        }
    }

    public void refresh() {
        Slice slice;
        Trace.beginSection("KeyguardSliceViewController#refresh");
        if ("content://com.android.systemui.keyguard/main".equals(this.mKeyguardSliceUri.toString())) {
            KeyguardSliceProvider attachedInstance = KeyguardSliceProvider.getAttachedInstance();
            if (attachedInstance != null) {
                slice = attachedInstance.onBindSlice(this.mKeyguardSliceUri);
            } else {
                Log.w("KeyguardSliceViewCtrl", "Keyguard slice not bound yet?");
                slice = null;
            }
        } else {
            slice = SliceViewManager.getInstance(this.mView.getContext()).bindSlice(this.mKeyguardSliceUri);
        }
        this.mObserver.onChanged(slice);
        Trace.endSection();
    }

    /* access modifiers changed from: package-private */
    public void updatePosition(int i, AnimationProperties animationProperties, boolean z) {
        PropertyAnimator.setProperty(this.mView, AnimatableProperty.TRANSLATION_X, (float) i, animationProperties, z);
    }

    /* access modifiers changed from: package-private */
    public void showSlice(Slice slice) {
        Trace.beginSection("KeyguardSliceViewController#showSlice");
        if (slice == null) {
            this.mView.hideSlice();
            Trace.endSection();
            return;
        }
        ListContent listContent = new ListContent(slice);
        RowContent header = listContent.getHeader();
        boolean z = header != null && !header.getSliceItem().hasHint("list_item");
        List list = (List) listContent.getRowItems().stream().filter(KeyguardSliceViewController$$ExternalSyntheticLambda1.INSTANCE).collect(Collectors.toList());
        KeyguardSliceView keyguardSliceView = this.mView;
        if (!z) {
            header = null;
        }
        this.mClickActions = keyguardSliceView.showSlice(header, list);
        Trace.endSection();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showSlice$1(SliceContent sliceContent) {
        return !"content://com.android.systemui.keyguard/action".equals(sliceContent.getSliceItem().getSlice().getUri().toString());
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("  mSlice: " + this.mSlice);
        printWriter.println("  mClickActions: " + this.mClickActions);
        printWriter.println("  mLockScreenMode: " + this.mLockScreenMode);
    }
}
