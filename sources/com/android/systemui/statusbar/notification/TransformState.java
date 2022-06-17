package com.android.systemui.statusbar.notification;

import android.util.Pools;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.widget.MessagingImageMessage;
import com.android.internal.widget.MessagingPropertyAnimator;
import com.android.internal.widget.ViewClippingUtil;
import com.android.systemui.R$id;
import com.android.systemui.animation.Interpolators;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class TransformState {
    public static final int ALIGN_END_TAG = R$id.align_transform_end_tag;
    private static ViewClippingUtil.ClippingParameters CLIPPING_PARAMETERS = new ViewClippingUtil.ClippingParameters() {
        public boolean shouldFinish(View view) {
            if (view instanceof ExpandableNotificationRow) {
                return !((ExpandableNotificationRow) view).isChildInGroup();
            }
            return false;
        }

        public void onClippingStateChanged(View view, boolean z) {
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
                if (z) {
                    expandableNotificationRow.setClipToActualHeight(true);
                } else if (expandableNotificationRow.isChildInGroup()) {
                    expandableNotificationRow.setClipToActualHeight(false);
                }
            }
        }
    };
    private static final int TRANSFORMATION_START_SCLALE_X = R$id.transformation_start_scale_x_tag;
    private static final int TRANSFORMATION_START_SCLALE_Y = R$id.transformation_start_scale_y_tag;
    private static final int TRANSFORMATION_START_X = R$id.transformation_start_x_tag;
    private static final int TRANSFORMATION_START_Y = R$id.transformation_start_y_tag;
    private static Pools.SimplePool<TransformState> sInstancePool = new Pools.SimplePool<>(40);
    private boolean mAlignEnd;
    protected Interpolator mDefaultInterpolator = Interpolators.FAST_OUT_SLOW_IN;
    private int[] mOwnPosition = new int[2];
    private boolean mSameAsAny;
    protected TransformInfo mTransformInfo;
    private float mTransformationEndX = -1.0f;
    private float mTransformationEndY = -1.0f;
    protected View mTransformedView;

    public interface TransformInfo {
        boolean isAnimating();
    }

    public void initFrom(View view, TransformInfo transformInfo) {
        this.mTransformedView = view;
        this.mTransformInfo = transformInfo;
        this.mAlignEnd = Boolean.TRUE.equals(view.getTag(ALIGN_END_TAG));
    }

    public void transformViewFrom(TransformState transformState, float f) {
        this.mTransformedView.animate().cancel();
        if (sameAs(transformState)) {
            ensureVisible();
        } else {
            CrossFadeHelper.fadeIn(this.mTransformedView, f, true);
        }
        transformViewFullyFrom(transformState, f);
    }

    public void ensureVisible() {
        if (this.mTransformedView.getVisibility() == 4 || this.mTransformedView.getAlpha() != 1.0f) {
            this.mTransformedView.setAlpha(1.0f);
            this.mTransformedView.setVisibility(0);
        }
    }

    public void transformViewFullyFrom(TransformState transformState, float f) {
        transformViewFrom(transformState, 17, (ViewTransformationHelper.CustomTransformation) null, f);
    }

    public void transformViewFullyFrom(TransformState transformState, ViewTransformationHelper.CustomTransformation customTransformation, float f) {
        transformViewFrom(transformState, 17, customTransformation, f);
    }

    public void transformViewVerticalFrom(TransformState transformState, ViewTransformationHelper.CustomTransformation customTransformation, float f) {
        transformViewFrom(transformState, 16, customTransformation, f);
    }

    public void transformViewVerticalFrom(TransformState transformState, float f) {
        transformViewFrom(transformState, 16, (ViewTransformationHelper.CustomTransformation) null, f);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0113  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0118  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x011d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void transformViewFrom(com.android.systemui.statusbar.notification.TransformState r23, int r24, com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation r25, float r26) {
        /*
            r22 = this;
            r0 = r22
            r1 = r25
            r2 = r26
            android.view.View r3 = r0.mTransformedView
            r4 = r24 & 1
            r5 = 0
            r6 = 1
            if (r4 == 0) goto L_0x0010
            r4 = r6
            goto L_0x0011
        L_0x0010:
            r4 = r5
        L_0x0011:
            r7 = 16
            r8 = r24 & 16
            if (r8 == 0) goto L_0x0019
            r8 = r6
            goto L_0x001a
        L_0x0019:
            r8 = r5
        L_0x001a:
            int r9 = r22.getContentHeight()
            int r10 = r23.getContentHeight()
            if (r10 == r9) goto L_0x002a
            if (r10 == 0) goto L_0x002a
            if (r9 == 0) goto L_0x002a
            r11 = r6
            goto L_0x002b
        L_0x002a:
            r11 = r5
        L_0x002b:
            int r12 = r22.getContentWidth()
            int r13 = r23.getContentWidth()
            if (r13 == r12) goto L_0x003b
            if (r13 == 0) goto L_0x003b
            if (r12 == 0) goto L_0x003b
            r14 = r6
            goto L_0x003c
        L_0x003b:
            r14 = r5
        L_0x003c:
            if (r11 != 0) goto L_0x0040
            if (r14 == 0) goto L_0x0048
        L_0x0040:
            boolean r15 = r22.transformScale(r23)
            if (r15 == 0) goto L_0x0048
            r15 = r6
            goto L_0x0049
        L_0x0048:
            r15 = r5
        L_0x0049:
            boolean r16 = r22.transformRightEdge(r23)
            r7 = 0
            int r17 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            r7 = -1082130432(0xffffffffbf800000, float:-1.0)
            if (r17 == 0) goto L_0x0080
            if (r4 == 0) goto L_0x005e
            float r18 = r22.getTransformationStartX()
            int r18 = (r18 > r7 ? 1 : (r18 == r7 ? 0 : -1))
            if (r18 == 0) goto L_0x0080
        L_0x005e:
            if (r8 == 0) goto L_0x0068
            float r18 = r22.getTransformationStartY()
            int r18 = (r18 > r7 ? 1 : (r18 == r7 ? 0 : -1))
            if (r18 == 0) goto L_0x0080
        L_0x0068:
            if (r15 == 0) goto L_0x0074
            float r18 = r22.getTransformationStartScaleX()
            int r18 = (r18 > r7 ? 1 : (r18 == r7 ? 0 : -1))
            if (r18 != 0) goto L_0x0074
            if (r14 != 0) goto L_0x0080
        L_0x0074:
            if (r15 == 0) goto L_0x0126
            float r18 = r22.getTransformationStartScaleY()
            int r18 = (r18 > r7 ? 1 : (r18 == r7 ? 0 : -1))
            if (r18 != 0) goto L_0x0126
            if (r11 == 0) goto L_0x0126
        L_0x0080:
            if (r17 == 0) goto L_0x0087
            int[] r17 = r23.getLaidOutLocationOnScreen()
            goto L_0x008b
        L_0x0087:
            int[] r17 = r23.getLocationOnScreen()
        L_0x008b:
            int[] r18 = r22.getLaidOutLocationOnScreen()
            r7 = r23
            if (r1 == 0) goto L_0x009e
            boolean r19 = r1.initTransformation(r0, r7)
            if (r19 != 0) goto L_0x009a
            goto L_0x009e
        L_0x009a:
            r5 = -1082130432(0xffffffffbf800000, float:-1.0)
            goto L_0x0111
        L_0x009e:
            if (r4 == 0) goto L_0x00c7
            if (r16 == 0) goto L_0x00bd
            android.view.View r19 = r23.getTransformedView()
            int r19 = r19.getWidth()
            int r20 = r3.getWidth()
            r21 = r17[r5]
            int r21 = r21 + r19
            r5 = r18[r5]
            int r5 = r5 + r20
            int r5 = r21 - r5
            float r5 = (float) r5
            r0.setTransformationStartX(r5)
            goto L_0x00c7
        L_0x00bd:
            r19 = r17[r5]
            r5 = r18[r5]
            int r5 = r19 - r5
            float r5 = (float) r5
            r0.setTransformationStartX(r5)
        L_0x00c7:
            if (r8 == 0) goto L_0x00d3
            r5 = r17[r6]
            r17 = r18[r6]
            int r5 = r5 - r17
            float r5 = (float) r5
            r0.setTransformationStartY(r5)
        L_0x00d3:
            android.view.View r5 = r23.getTransformedView()
            if (r15 == 0) goto L_0x00f3
            if (r14 == 0) goto L_0x00f3
            float r7 = (float) r13
            float r13 = r5.getScaleX()
            float r7 = r7 * r13
            float r12 = (float) r12
            float r7 = r7 / r12
            r0.setTransformationStartScaleX(r7)
            if (r16 == 0) goto L_0x00ee
            int r7 = r3.getWidth()
            float r7 = (float) r7
            goto L_0x00ef
        L_0x00ee:
            r7 = 0
        L_0x00ef:
            r3.setPivotX(r7)
            goto L_0x00f8
        L_0x00f3:
            r7 = -1082130432(0xffffffffbf800000, float:-1.0)
            r0.setTransformationStartScaleX(r7)
        L_0x00f8:
            if (r15 == 0) goto L_0x010c
            if (r11 == 0) goto L_0x010c
            float r7 = (float) r10
            float r5 = r5.getScaleY()
            float r7 = r7 * r5
            float r5 = (float) r9
            float r7 = r7 / r5
            r0.setTransformationStartScaleY(r7)
            r5 = 0
            r3.setPivotY(r5)
            goto L_0x009a
        L_0x010c:
            r5 = -1082130432(0xffffffffbf800000, float:-1.0)
            r0.setTransformationStartScaleY(r5)
        L_0x0111:
            if (r4 != 0) goto L_0x0116
            r0.setTransformationStartX(r5)
        L_0x0116:
            if (r8 != 0) goto L_0x011b
            r0.setTransformationStartY(r5)
        L_0x011b:
            if (r15 != 0) goto L_0x0123
            r0.setTransformationStartScaleX(r5)
            r0.setTransformationStartScaleY(r5)
        L_0x0123:
            r0.setClippingDeactivated(r3, r6)
        L_0x0126:
            android.view.animation.Interpolator r5 = r0.mDefaultInterpolator
            float r5 = r5.getInterpolation(r2)
            if (r4 == 0) goto L_0x0148
            if (r1 == 0) goto L_0x013b
            android.view.animation.Interpolator r4 = r1.getCustomInterpolator(r6, r6)
            if (r4 == 0) goto L_0x013b
            float r4 = r4.getInterpolation(r2)
            goto L_0x013c
        L_0x013b:
            r4 = r5
        L_0x013c:
            float r7 = r22.getTransformationStartX()
            r9 = 0
            float r4 = com.android.systemui.statusbar.notification.NotificationUtils.interpolate(r7, r9, r4)
            r3.setTranslationX(r4)
        L_0x0148:
            if (r8 == 0) goto L_0x0166
            if (r1 == 0) goto L_0x0159
            r4 = 16
            android.view.animation.Interpolator r1 = r1.getCustomInterpolator(r4, r6)
            if (r1 == 0) goto L_0x0159
            float r1 = r1.getInterpolation(r2)
            goto L_0x015a
        L_0x0159:
            r1 = r5
        L_0x015a:
            float r2 = r22.getTransformationStartY()
            r4 = 0
            float r1 = com.android.systemui.statusbar.notification.NotificationUtils.interpolate(r2, r4, r1)
            r3.setTranslationY(r1)
        L_0x0166:
            if (r15 == 0) goto L_0x018a
            float r1 = r22.getTransformationStartScaleX()
            r2 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r4 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            r6 = 1065353216(0x3f800000, float:1.0)
            if (r4 == 0) goto L_0x017b
            float r1 = com.android.systemui.statusbar.notification.NotificationUtils.interpolate(r1, r6, r5)
            r3.setScaleX(r1)
        L_0x017b:
            float r0 = r22.getTransformationStartScaleY()
            int r1 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x018a
            float r0 = com.android.systemui.statusbar.notification.NotificationUtils.interpolate(r0, r6, r5)
            r3.setScaleY(r0)
        L_0x018a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.TransformState.transformViewFrom(com.android.systemui.statusbar.notification.TransformState, int, com.android.systemui.statusbar.ViewTransformationHelper$CustomTransformation, float):void");
    }

    /* access modifiers changed from: protected */
    public int getContentWidth() {
        return this.mTransformedView.getWidth();
    }

    /* access modifiers changed from: protected */
    public int getContentHeight() {
        return this.mTransformedView.getHeight();
    }

    /* access modifiers changed from: protected */
    public boolean transformScale(TransformState transformState) {
        return sameAs(transformState);
    }

    /* access modifiers changed from: protected */
    public boolean transformRightEdge(TransformState transformState) {
        boolean z = true;
        boolean z2 = this.mAlignEnd && transformState.mAlignEnd;
        if (!this.mTransformedView.isLayoutRtl() || !transformState.mTransformedView.isLayoutRtl()) {
            z = false;
        }
        return z2 ^ z;
    }

    public boolean transformViewTo(TransformState transformState, float f) {
        this.mTransformedView.animate().cancel();
        if (!sameAs(transformState)) {
            CrossFadeHelper.fadeOut(this.mTransformedView, f);
            transformViewFullyTo(transformState, f);
            return true;
        } else if (this.mTransformedView.getVisibility() != 0) {
            return false;
        } else {
            this.mTransformedView.setAlpha(0.0f);
            this.mTransformedView.setVisibility(4);
            return false;
        }
    }

    public void transformViewFullyTo(TransformState transformState, float f) {
        transformViewTo(transformState, 17, (ViewTransformationHelper.CustomTransformation) null, f);
    }

    public void transformViewFullyTo(TransformState transformState, ViewTransformationHelper.CustomTransformation customTransformation, float f) {
        transformViewTo(transformState, 17, customTransformation, f);
    }

    public void transformViewVerticalTo(TransformState transformState, ViewTransformationHelper.CustomTransformation customTransformation, float f) {
        transformViewTo(transformState, 16, customTransformation, f);
    }

    public void transformViewVerticalTo(TransformState transformState, float f) {
        transformViewTo(transformState, 16, (ViewTransformationHelper.CustomTransformation) null, f);
    }

    private void transformViewTo(TransformState transformState, int i, ViewTransformationHelper.CustomTransformation customTransformation, float f) {
        float f2;
        boolean z;
        int i2;
        float f3;
        TransformState transformState2 = transformState;
        ViewTransformationHelper.CustomTransformation customTransformation2 = customTransformation;
        float f4 = f;
        View view = this.mTransformedView;
        boolean z2 = (i & 1) != 0;
        boolean z3 = (i & 16) != 0;
        boolean transformScale = transformScale(transformState);
        boolean transformRightEdge = transformRightEdge(transformState);
        int contentWidth = getContentWidth();
        int contentWidth2 = transformState.getContentWidth();
        if (f4 == 0.0f) {
            if (z2) {
                float transformationStartX = getTransformationStartX();
                if (transformationStartX == -1.0f) {
                    transformationStartX = view.getTranslationX();
                }
                setTransformationStartX(transformationStartX);
            }
            if (z3) {
                float transformationStartY = getTransformationStartY();
                if (transformationStartY == -1.0f) {
                    transformationStartY = view.getTranslationY();
                }
                setTransformationStartY(transformationStartY);
            }
            if (!transformScale || contentWidth2 == contentWidth) {
                setTransformationStartScaleX(-1.0f);
            } else {
                setTransformationStartScaleX(view.getScaleX());
                view.setPivotX(transformRightEdge ? (float) view.getWidth() : 0.0f);
            }
            if (!transformScale || transformState.getContentHeight() == getContentHeight()) {
                setTransformationStartScaleY(-1.0f);
            } else {
                setTransformationStartScaleY(view.getScaleY());
                view.setPivotY(0.0f);
            }
            setClippingDeactivated(view, true);
        }
        float interpolation = this.mDefaultInterpolator.getInterpolation(f4);
        int[] laidOutLocationOnScreen = transformState.getLaidOutLocationOnScreen();
        int[] laidOutLocationOnScreen2 = getLaidOutLocationOnScreen();
        if (z2) {
            int width = view.getWidth();
            int width2 = transformState.getTransformedView().getWidth();
            if (transformRightEdge) {
                z = false;
                i2 = (laidOutLocationOnScreen[0] + width2) - (laidOutLocationOnScreen2[0] + width);
            } else {
                z = false;
                i2 = laidOutLocationOnScreen[0] - laidOutLocationOnScreen2[0];
            }
            float f5 = (float) i2;
            if (customTransformation2 != null) {
                if (customTransformation2.customTransformTarget(this, transformState2)) {
                    f5 = this.mTransformationEndX;
                }
                Interpolator customInterpolator = customTransformation2.getCustomInterpolator(1, z);
                if (customInterpolator != null) {
                    f3 = customInterpolator.getInterpolation(f4);
                    view.setTranslationX(NotificationUtils.interpolate(getTransformationStartX(), f5, f3));
                }
            }
            f3 = interpolation;
            view.setTranslationX(NotificationUtils.interpolate(getTransformationStartX(), f5, f3));
        }
        if (z3) {
            float f6 = (float) (laidOutLocationOnScreen[1] - laidOutLocationOnScreen2[1]);
            if (customTransformation2 != null) {
                if (customTransformation2.customTransformTarget(this, transformState2)) {
                    f6 = this.mTransformationEndY;
                }
                Interpolator customInterpolator2 = customTransformation2.getCustomInterpolator(16, false);
                if (customInterpolator2 != null) {
                    f2 = customInterpolator2.getInterpolation(f4);
                    view.setTranslationY(NotificationUtils.interpolate(getTransformationStartY(), f6, f2));
                }
            }
            f2 = interpolation;
            view.setTranslationY(NotificationUtils.interpolate(getTransformationStartY(), f6, f2));
        }
        if (transformScale) {
            float transformationStartScaleX = getTransformationStartScaleX();
            if (transformationStartScaleX != -1.0f) {
                view.setScaleX(NotificationUtils.interpolate(transformationStartScaleX, ((float) contentWidth2) / ((float) contentWidth), interpolation));
            }
            float transformationStartScaleY = getTransformationStartScaleY();
            if (transformationStartScaleY != -1.0f) {
                view.setScaleY(NotificationUtils.interpolate(transformationStartScaleY, ((float) transformState.getContentHeight()) / ((float) getContentHeight()), interpolation));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setClippingDeactivated(View view, boolean z) {
        ViewClippingUtil.setClippingDeactivated(view, z, CLIPPING_PARAMETERS);
    }

    public int[] getLaidOutLocationOnScreen() {
        int[] locationOnScreen = getLocationOnScreen();
        locationOnScreen[0] = (int) (((float) locationOnScreen[0]) - this.mTransformedView.getTranslationX());
        locationOnScreen[1] = (int) (((float) locationOnScreen[1]) - this.mTransformedView.getTranslationY());
        return locationOnScreen;
    }

    public int[] getLocationOnScreen() {
        this.mTransformedView.getLocationOnScreen(this.mOwnPosition);
        int[] iArr = this.mOwnPosition;
        iArr[0] = (int) (((float) iArr[0]) - ((1.0f - this.mTransformedView.getScaleX()) * this.mTransformedView.getPivotX()));
        int[] iArr2 = this.mOwnPosition;
        iArr2[1] = (int) (((float) iArr2[1]) - ((1.0f - this.mTransformedView.getScaleY()) * this.mTransformedView.getPivotY()));
        int[] iArr3 = this.mOwnPosition;
        iArr3[1] = iArr3[1] - (MessagingPropertyAnimator.getTop(this.mTransformedView) - MessagingPropertyAnimator.getLayoutTop(this.mTransformedView));
        return this.mOwnPosition;
    }

    /* access modifiers changed from: protected */
    public boolean sameAs(TransformState transformState) {
        return this.mSameAsAny;
    }

    public void appear(float f, TransformableView transformableView) {
        if (f == 0.0f) {
            prepareFadeIn();
        }
        CrossFadeHelper.fadeIn(this.mTransformedView, f, true);
    }

    public void disappear(float f, TransformableView transformableView) {
        CrossFadeHelper.fadeOut(this.mTransformedView, f);
    }

    public static TransformState createFrom(View view, TransformInfo transformInfo) {
        if (view instanceof TextView) {
            TextViewTransformState obtain = TextViewTransformState.obtain();
            obtain.initFrom(view, transformInfo);
            return obtain;
        } else if (view.getId() == 16908723) {
            ActionListTransformState obtain2 = ActionListTransformState.obtain();
            obtain2.initFrom(view, transformInfo);
            return obtain2;
        } else if (view.getId() == 16909247) {
            MessagingLayoutTransformState obtain3 = MessagingLayoutTransformState.obtain();
            obtain3.initFrom(view, transformInfo);
            return obtain3;
        } else if (view instanceof MessagingImageMessage) {
            MessagingImageTransformState obtain4 = MessagingImageTransformState.obtain();
            obtain4.initFrom(view, transformInfo);
            return obtain4;
        } else if (view instanceof ImageView) {
            ImageTransformState obtain5 = ImageTransformState.obtain();
            obtain5.initFrom(view, transformInfo);
            return obtain5;
        } else if (view instanceof ProgressBar) {
            ProgressTransformState obtain6 = ProgressTransformState.obtain();
            obtain6.initFrom(view, transformInfo);
            return obtain6;
        } else {
            TransformState obtain7 = obtain();
            obtain7.initFrom(view, transformInfo);
            return obtain7;
        }
    }

    public void setIsSameAsAnyView(boolean z) {
        this.mSameAsAny = z;
    }

    public void recycle() {
        reset();
        if (getClass() == TransformState.class) {
            sInstancePool.release(this);
        }
    }

    public void setTransformationEndY(float f) {
        this.mTransformationEndY = f;
    }

    public float getTransformationStartX() {
        Object tag = this.mTransformedView.getTag(TRANSFORMATION_START_X);
        if (tag == null) {
            return -1.0f;
        }
        return ((Float) tag).floatValue();
    }

    public float getTransformationStartY() {
        Object tag = this.mTransformedView.getTag(TRANSFORMATION_START_Y);
        if (tag == null) {
            return -1.0f;
        }
        return ((Float) tag).floatValue();
    }

    public float getTransformationStartScaleX() {
        Object tag = this.mTransformedView.getTag(TRANSFORMATION_START_SCLALE_X);
        if (tag == null) {
            return -1.0f;
        }
        return ((Float) tag).floatValue();
    }

    public float getTransformationStartScaleY() {
        Object tag = this.mTransformedView.getTag(TRANSFORMATION_START_SCLALE_Y);
        if (tag == null) {
            return -1.0f;
        }
        return ((Float) tag).floatValue();
    }

    public void setTransformationStartX(float f) {
        this.mTransformedView.setTag(TRANSFORMATION_START_X, Float.valueOf(f));
    }

    public void setTransformationStartY(float f) {
        this.mTransformedView.setTag(TRANSFORMATION_START_Y, Float.valueOf(f));
    }

    private void setTransformationStartScaleX(float f) {
        this.mTransformedView.setTag(TRANSFORMATION_START_SCLALE_X, Float.valueOf(f));
    }

    private void setTransformationStartScaleY(float f) {
        this.mTransformedView.setTag(TRANSFORMATION_START_SCLALE_Y, Float.valueOf(f));
    }

    /* access modifiers changed from: protected */
    public void reset() {
        this.mTransformedView = null;
        this.mTransformInfo = null;
        this.mSameAsAny = false;
        this.mTransformationEndX = -1.0f;
        this.mTransformationEndY = -1.0f;
        this.mAlignEnd = false;
        this.mDefaultInterpolator = Interpolators.FAST_OUT_SLOW_IN;
    }

    public void setVisible(boolean z, boolean z2) {
        if (z2 || this.mTransformedView.getVisibility() != 8) {
            if (this.mTransformedView.getVisibility() != 8) {
                this.mTransformedView.setVisibility(z ? 0 : 4);
            }
            this.mTransformedView.animate().cancel();
            this.mTransformedView.setAlpha(z ? 1.0f : 0.0f);
            resetTransformedView();
        }
    }

    public void prepareFadeIn() {
        resetTransformedView();
    }

    /* access modifiers changed from: protected */
    public void resetTransformedView() {
        this.mTransformedView.setTranslationX(0.0f);
        this.mTransformedView.setTranslationY(0.0f);
        this.mTransformedView.setScaleX(1.0f);
        this.mTransformedView.setScaleY(1.0f);
        setClippingDeactivated(this.mTransformedView, false);
        abortTransformation();
    }

    public void abortTransformation() {
        View view = this.mTransformedView;
        int i = TRANSFORMATION_START_X;
        Float valueOf = Float.valueOf(-1.0f);
        view.setTag(i, valueOf);
        this.mTransformedView.setTag(TRANSFORMATION_START_Y, valueOf);
        this.mTransformedView.setTag(TRANSFORMATION_START_SCLALE_X, valueOf);
        this.mTransformedView.setTag(TRANSFORMATION_START_SCLALE_Y, valueOf);
    }

    public static TransformState obtain() {
        TransformState transformState = (TransformState) sInstancePool.acquire();
        if (transformState != null) {
            return transformState;
        }
        return new TransformState();
    }

    public View getTransformedView() {
        return this.mTransformedView;
    }

    public void setDefaultInterpolator(Interpolator interpolator) {
        this.mDefaultInterpolator = interpolator;
    }
}