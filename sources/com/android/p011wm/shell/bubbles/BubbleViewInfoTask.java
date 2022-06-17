package com.android.p011wm.shell.bubbles;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.util.Log;
import android.util.PathParser;
import android.view.LayoutInflater;
import com.android.internal.graphics.ColorUtils;
import com.android.launcher3.icons.BitmapInfo;
import com.android.p011wm.shell.C2219R;
import com.android.p011wm.shell.bubbles.Bubble;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Executor;

/* renamed from: com.android.wm.shell.bubbles.BubbleViewInfoTask */
public class BubbleViewInfoTask extends AsyncTask<Void, Void, BubbleViewInfo> {
    private Bubble mBubble;
    private Callback mCallback;
    private WeakReference<Context> mContext;
    private WeakReference<BubbleController> mController;
    private BubbleIconFactory mIconFactory;
    private Executor mMainExecutor;
    private boolean mSkipInflation;
    private WeakReference<BubbleStackView> mStackView;

    /* renamed from: com.android.wm.shell.bubbles.BubbleViewInfoTask$Callback */
    public interface Callback {
        void onBubbleViewsReady(Bubble bubble);
    }

    BubbleViewInfoTask(Bubble bubble, Context context, BubbleController bubbleController, BubbleStackView bubbleStackView, BubbleIconFactory bubbleIconFactory, boolean z, Callback callback, Executor executor) {
        this.mBubble = bubble;
        this.mContext = new WeakReference<>(context);
        this.mController = new WeakReference<>(bubbleController);
        this.mStackView = new WeakReference<>(bubbleStackView);
        this.mIconFactory = bubbleIconFactory;
        this.mSkipInflation = z;
        this.mCallback = callback;
        this.mMainExecutor = executor;
    }

    /* access modifiers changed from: protected */
    public BubbleViewInfo doInBackground(Void... voidArr) {
        return BubbleViewInfo.populate((Context) this.mContext.get(), (BubbleController) this.mController.get(), (BubbleStackView) this.mStackView.get(), this.mIconFactory, this.mBubble, this.mSkipInflation);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(BubbleViewInfo bubbleViewInfo) {
        if (!isCancelled() && bubbleViewInfo != null) {
            this.mMainExecutor.execute(new BubbleViewInfoTask$$ExternalSyntheticLambda0(this, bubbleViewInfo));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPostExecute$0(BubbleViewInfo bubbleViewInfo) {
        this.mBubble.setViewInfo(bubbleViewInfo);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onBubbleViewsReady(this.mBubble);
        }
    }

    /* renamed from: com.android.wm.shell.bubbles.BubbleViewInfoTask$BubbleViewInfo */
    public static class BubbleViewInfo {
        String appName;
        Bitmap badgeBitmap;
        Bitmap bubbleBitmap;
        int dotColor;
        Path dotPath;
        BubbleExpandedView expandedView;
        Bubble.FlyoutMessage flyoutMessage;
        BadgedImageView imageView;
        ShortcutInfo shortcutInfo;

        public static BubbleViewInfo populate(Context context, BubbleController bubbleController, BubbleStackView bubbleStackView, BubbleIconFactory bubbleIconFactory, Bubble bubble, boolean z) {
            BubbleViewInfo bubbleViewInfo = new BubbleViewInfo();
            if (!z && !bubble.isInflated()) {
                LayoutInflater from = LayoutInflater.from(context);
                BadgedImageView badgedImageView = (BadgedImageView) from.inflate(C2219R.layout.bubble_view, bubbleStackView, false);
                bubbleViewInfo.imageView = badgedImageView;
                badgedImageView.initialize(bubbleController.getPositioner());
                BubbleExpandedView bubbleExpandedView = (BubbleExpandedView) from.inflate(C2219R.layout.bubble_expanded_view, bubbleStackView, false);
                bubbleViewInfo.expandedView = bubbleExpandedView;
                bubbleExpandedView.initialize(bubbleController, bubbleStackView, false);
            }
            if (bubble.getShortcutInfo() != null) {
                bubbleViewInfo.shortcutInfo = bubble.getShortcutInfo();
            }
            PackageManager packageManagerForUser = BubbleController.getPackageManagerForUser(context, bubble.getUser().getIdentifier());
            try {
                ApplicationInfo applicationInfo = packageManagerForUser.getApplicationInfo(bubble.getPackageName(), 795136);
                if (applicationInfo != null) {
                    bubbleViewInfo.appName = String.valueOf(packageManagerForUser.getApplicationLabel(applicationInfo));
                }
                Drawable applicationIcon = packageManagerForUser.getApplicationIcon(bubble.getPackageName());
                Drawable userBadgedIcon = packageManagerForUser.getUserBadgedIcon(applicationIcon, bubble.getUser());
                Drawable bubbleDrawable = bubbleIconFactory.getBubbleDrawable(context, bubbleViewInfo.shortcutInfo, bubble.getIcon());
                if (bubbleDrawable != null) {
                    applicationIcon = bubbleDrawable;
                }
                BitmapInfo badgeBitmap2 = bubbleIconFactory.getBadgeBitmap(userBadgedIcon, bubble.isImportantConversation());
                bubbleViewInfo.badgeBitmap = badgeBitmap2.icon;
                bubbleViewInfo.bubbleBitmap = bubbleIconFactory.createBadgedIconBitmap(applicationIcon, (UserHandle) null, true).icon;
                Path createPathFromPathData = PathParser.createPathFromPathData(context.getResources().getString(17039967));
                Matrix matrix = new Matrix();
                float scale = bubbleIconFactory.getNormalizer().getScale(applicationIcon, (RectF) null, (Path) null, (boolean[]) null);
                matrix.setScale(scale, scale, 50.0f, 50.0f);
                createPathFromPathData.transform(matrix);
                bubbleViewInfo.dotPath = createPathFromPathData;
                bubbleViewInfo.dotColor = ColorUtils.blendARGB(badgeBitmap2.color, -1, 0.54f);
                Bubble.FlyoutMessage flyoutMessage2 = bubble.getFlyoutMessage();
                bubbleViewInfo.flyoutMessage = flyoutMessage2;
                if (flyoutMessage2 != null) {
                    flyoutMessage2.senderAvatar = BubbleViewInfoTask.loadSenderAvatar(context, flyoutMessage2.senderIcon);
                }
                return bubbleViewInfo;
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("Bubbles", "Unable to find package: " + bubble.getPackageName());
                return null;
            }
        }
    }

    static Drawable loadSenderAvatar(Context context, Icon icon) {
        Objects.requireNonNull(context);
        if (icon == null) {
            return null;
        }
        try {
            if (icon.getType() == 4 || icon.getType() == 6) {
                context.grantUriPermission(context.getPackageName(), icon.getUri(), 1);
            }
            return icon.loadDrawable(context);
        } catch (Exception e) {
            Log.w("Bubbles", "loadSenderAvatar failed: " + e.getMessage());
            return null;
        }
    }
}
