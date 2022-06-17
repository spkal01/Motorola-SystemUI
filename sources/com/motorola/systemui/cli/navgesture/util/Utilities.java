package com.motorola.systemui.cli.navgesture.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

public final class Utilities {
    public static final IntProperty<Drawable> DRAWABLE_ALPHA_PROPERTY = new IntProperty<Drawable>("drawableAlpha") {
        public void setValue(Drawable drawable, int i) {
            drawable.setAlpha(i);
        }

        public Integer get(Drawable drawable) {
            return Integer.valueOf(drawable.getAlpha());
        }
    };
    public static final Property<Drawable, Rect> DRAWABLE_BOUNDS_PROPERTY = new Property<Drawable, Rect>(Rect.class, "drawableBounds") {
        public Rect get(Drawable drawable) {
            return drawable.getBounds();
        }

        public void set(Drawable drawable, Rect rect) {
            drawable.setBounds(rect);
        }
    };
    public static final Intent MANAGE_APP_CLI_ACCESSES_INTENT = new Intent().setPackage("com.motorola.cli.settings").setAction("com.motorola.intent.action.MANAGE_APP_CLI_ACCESSES").setFlags(268435456);
    public static final FloatProperty<View> SCALE_PROPERTY = new FloatProperty<View>("scale") {
        public Float get(View view) {
            return Float.valueOf(view.getScaleX());
        }

        public void setValue(View view, float f) {
            view.setScaleX(f);
            view.setScaleY(f);
        }
    };
    private static final Matrix sInverseMatrix = new Matrix();
    private static final Matrix sMatrix = new Matrix();
    private static final float[] sTmpRectPoints = new float[4];
    private static final float[] sTmpXY = new float[2];

    public static float mapRange(float f, float f2, float f3) {
        return f2 + (f * (f3 - f2));
    }

    public static float squaredHypot(float f, float f2) {
        return (f * f) + (f2 * f2);
    }

    public static boolean shouldDisableGestures(MotionEvent motionEvent) {
        return (motionEvent.getEdgeFlags() & 512) == 512;
    }

    public static float getDescendantCoordRelativeToAncestor(View view, View view2, Rect rect) {
        float[] fArr = sTmpRectPoints;
        fArr[0] = 0.0f;
        fArr[1] = 0.0f;
        fArr[2] = (float) view.getWidth();
        fArr[3] = (float) view.getHeight();
        float descendantCoordRelativeToAncestor = getDescendantCoordRelativeToAncestor(view, view2, fArr, false, false);
        rect.left = Math.round(Math.min(fArr[0], fArr[2]));
        rect.top = Math.round(Math.min(fArr[1], fArr[3]));
        rect.right = Math.round(Math.max(fArr[0], fArr[2]));
        rect.bottom = Math.round(Math.max(fArr[1], fArr[3]));
        return descendantCoordRelativeToAncestor;
    }

    public static float getDescendantCoordRelativeToAncestor(View view, View view2, float[] fArr, boolean z, boolean z2) {
        float f = 1.0f;
        View view3 = view;
        while (view3 != view2 && view3 != null) {
            if (view3 != view || z) {
                offsetPoints(fArr, (float) (-view3.getScrollX()), (float) (-view3.getScrollY()));
            }
            if (!z2) {
                view3.getMatrix().mapPoints(fArr);
            }
            offsetPoints(fArr, (float) view3.getLeft(), (float) view3.getTop());
            f *= view3.getScaleX();
            ViewParent parent = view3.getParent();
            view3 = parent instanceof View ? (View) parent : null;
        }
        return f;
    }

    public static void offsetPoints(float[] fArr, float f, float f2) {
        for (int i = 0; i < fArr.length; i += 2) {
            fArr[i] = fArr[i] + f;
            int i2 = i + 1;
            fArr[i2] = fArr[i2] + f2;
        }
    }

    public static boolean pointInView(View view, float f, float f2, float f3) {
        float f4 = -f3;
        return f >= f4 && f2 >= f4 && f < ((float) view.getWidth()) + f3 && f2 < ((float) view.getHeight()) + f3;
    }

    public static void scaleRectFAboutCenter(RectF rectF, float f) {
        if (f != 1.0f) {
            float centerX = rectF.centerX();
            float centerY = rectF.centerY();
            rectF.offset(-centerX, -centerY);
            rectF.left *= f;
            rectF.top *= f;
            rectF.right *= f;
            rectF.bottom *= f;
            rectF.offset(centerX, centerY);
        }
    }

    public static void scaleRectAboutCenter(Rect rect, float f) {
        if (f != 1.0f) {
            int centerX = rect.centerX();
            int centerY = rect.centerY();
            rect.offset(-centerX, -centerY);
            scaleRect(rect, f);
            rect.offset(centerX, centerY);
        }
    }

    public static void scaleRect(Rect rect, float f) {
        if (f != 1.0f) {
            rect.left = Math.round((((float) rect.left) * f) + 0.5f);
            rect.top = Math.round((((float) rect.top) * f) + 0.5f);
            rect.right = Math.round((((float) rect.right) * f) + 0.5f);
            rect.bottom = Math.round((((float) rect.bottom) * f) + 0.5f);
        }
    }

    public static float getProgress(float f, float f2, float f3) {
        return Math.abs(f - f2) / Math.abs(f3 - f2);
    }

    public static int boundToRange(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, i3));
    }

    public static float boundToRange(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f, f3));
    }

    public static void postAsyncCallback(Handler handler, Runnable runnable) {
        Message obtain = Message.obtain(handler, runnable);
        obtain.setAsynchronous(true);
        handler.sendMessage(obtain);
    }

    public static float squaredTouchSlop(Context context) {
        float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        return scaledTouchSlop * scaledTouchSlop;
    }

    public static void unregisterReceiverSafely(Context context, BroadcastReceiver broadcastReceiver) {
        try {
            context.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException unused) {
        }
    }
}
