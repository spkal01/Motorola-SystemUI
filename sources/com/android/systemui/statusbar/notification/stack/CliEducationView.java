package com.android.systemui.statusbar.notification.stack;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.R$color;
import com.android.systemui.R$id;
import com.android.systemui.R$string;
import com.android.systemui.SwipeHelper;
import com.android.systemui.recents.TriangleShape;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.util.AlarmTimeout;

public class CliEducationView extends FrameLayout {
    private View mCardContainer;
    private TextView mCardContentTv;
    private boolean mCliNeedEducationCard;
    private boolean mCliNeedEducationCardAgain;
    private boolean mCliNeedEducationDoubleTap;
    private boolean mCliNeedEducationSwipe;
    private int mCurrentStep;
    private View mDoubleTapContainer;
    private AlarmTimeout mEducationCardAlarm;
    private View mEducationContainer;
    private TextView mEducationDoubleClickTv;
    private AlarmTimeout mEducationDoubleTapAlarm;
    private StatusBarIconView mEducationIcon;
    private ImageView mEducationNoteBottom;
    private TextView mEducationNoteTv;
    private TextView mEducationSwipDownTv;
    private AlarmTimeout mEducationSwipeAlarm;
    private int[] mLocation;
    private TextView mNextTv;
    private OnNextListener mOnNextListener;
    private View mSwipeDownContainer;
    private SwipeHelper mSwipeHelper;

    public interface OnNextListener {
        void onExpandClick(boolean z);
    }

    public CliEducationView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CliEducationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentStep = -1;
        this.mLocation = new int[2];
        this.mCliNeedEducationCard = Prefs.getBoolean(this.mContext, "CliEducationCard", true);
        this.mCliNeedEducationCardAgain = Prefs.getBoolean(this.mContext, "CliEducationCardAgain", true);
        this.mCliNeedEducationSwipe = Prefs.getBoolean(this.mContext, "CliEducationSwipe", false);
        this.mCliNeedEducationDoubleTap = Prefs.getBoolean(this.mContext, "CliEducationDoubleTap", false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0007, code lost:
        r0 = r2.mSwipeHelper;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r3) {
        /*
            r2 = this;
            boolean r0 = r2.shouldEducationCard()
            r1 = 0
            if (r0 == 0) goto L_0x0010
            com.android.systemui.SwipeHelper r0 = r2.mSwipeHelper
            if (r0 == 0) goto L_0x0010
            boolean r0 = r0.onInterceptTouchEvent(r3)
            goto L_0x0011
        L_0x0010:
            r0 = r1
        L_0x0011:
            if (r0 != 0) goto L_0x0019
            boolean r2 = super.onInterceptTouchEvent(r3)
            if (r2 == 0) goto L_0x001a
        L_0x0019:
            r1 = 1
        L_0x001a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.stack.CliEducationView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        SwipeHelper swipeHelper;
        if (shouldEducationCard() && (swipeHelper = this.mSwipeHelper) != null) {
            swipeHelper.onTouchEvent(motionEvent);
        }
        if (motionEvent.getActionMasked() == 0 && this.mCurrentStep == 0 && touchInView(this.mEducationIcon, motionEvent)) {
            setStep(1);
        }
        super.onTouchEvent(motionEvent);
        return true;
    }

    public boolean touchInView(View view, MotionEvent motionEvent) {
        view.getLocationOnScreen(this.mLocation);
        int[] iArr = this.mLocation;
        int i = iArr[0];
        int i2 = iArr[1];
        return new Rect(i, i2, view.getWidth() + i, view.getHeight() + i2).contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ImageView imageView = (ImageView) findViewById(R$id.education_note_bottom);
        this.mEducationNoteBottom = imageView;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) layoutParams.width, (float) layoutParams.height, false));
        Paint paint = shapeDrawable.getPaint();
        Context context = this.mContext;
        int i = R$color.cli_education_color;
        paint.setColor(context.getColor(i));
        this.mEducationNoteBottom.setBackground(shapeDrawable);
        ImageView imageView2 = (ImageView) findViewById(R$id.education_double_tap_top);
        ViewGroup.LayoutParams layoutParams2 = imageView2.getLayoutParams();
        ShapeDrawable shapeDrawable2 = new ShapeDrawable(TriangleShape.create((float) layoutParams2.width, (float) layoutParams2.height, true));
        shapeDrawable2.getPaint().setColor(this.mContext.getColor(i));
        imageView2.setBackground(shapeDrawable2);
        ((ImageView) findViewById(R$id.education_swipe_down_top)).setBackground(shapeDrawable2);
        View findViewById = findViewById(R$id.education_card);
        this.mCardContainer = findViewById;
        findViewById.setOnClickListener(new CliEducationView$$ExternalSyntheticLambda3(this));
        this.mCardContainer.setVisibility(8);
        this.mCardContentTv = (TextView) findViewById(R$id.education_card_content);
        this.mNextTv = (TextView) findViewById(R$id.education_card_next);
        this.mEducationContainer = findViewById(R$id.education_note_container);
        findViewById(R$id.education_note_close).setOnClickListener(new CliEducationView$$ExternalSyntheticLambda4(this));
        View findViewById2 = findViewById(R$id.education_swipe_down);
        this.mSwipeDownContainer = findViewById2;
        findViewById2.setVisibility(8);
        findViewById(R$id.education_swipe_down_close).setOnClickListener(new CliEducationView$$ExternalSyntheticLambda6(this));
        View findViewById3 = findViewById(R$id.education_double_tap);
        this.mDoubleTapContainer = findViewById3;
        findViewById3.setVisibility(8);
        findViewById(R$id.education_double_tap_close).setOnClickListener(new CliEducationView$$ExternalSyntheticLambda5(this));
        this.mEducationNoteTv = (TextView) findViewById(R$id.education_note_content);
        this.mEducationSwipDownTv = (TextView) findViewById(R$id.education_swipe_down_content);
        this.mEducationDoubleClickTv = (TextView) findViewById(R$id.education_double_tap_content);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$0(View view) {
        int i = this.mCurrentStep;
        if (i >= 0 && i < 4) {
            setStep(i + 1);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$1(View view) {
        this.mEducationContainer.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$2(View view) {
        setStep(8);
        setEducationSwipe(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishInflate$3(View view) {
        setStep(8);
        setEducationDoubleTap(false);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        TextView textView = this.mEducationNoteTv;
        if (textView != null) {
            textView.setText(R$string.cli_education_step_one);
        }
        TextView textView2 = this.mEducationSwipDownTv;
        if (textView2 != null) {
            textView2.setText(R$string.cli_education_swipe_down);
        }
        TextView textView3 = this.mEducationDoubleClickTv;
        if (textView3 != null) {
            textView3.setText(R$string.cli_education_double_tap);
        }
    }

    public void setStep(int i) {
        Log.d("Cli_Education", "step=" + i);
        this.mCurrentStep = i;
        switch (i) {
            case 0:
                setVisibility(0);
                this.mEducationContainer.setVisibility(0);
                return;
            case 1:
                this.mEducationIcon.setSelected(true);
                this.mNextTv.setText(R$string.cli_education_next);
                this.mCardContentTv.setText(R$string.cli_education_step_two);
                this.mCardContainer.setVisibility(0);
                this.mEducationContainer.setVisibility(8);
                return;
            case 2:
                this.mNextTv.setVisibility(8);
                this.mCardContentTv.setText(R$string.cli_education_step_three);
                return;
            case 3:
                this.mOnNextListener.onExpandClick(true);
                return;
            case 4:
                this.mOnNextListener.onExpandClick(false);
                return;
            case 5:
                this.mEducationContainer.setVisibility(8);
                this.mCardContainer.setVisibility(8);
                setVisibility(8);
                if (i != 4) {
                    setEducationCard(false, true, true);
                    return;
                } else {
                    setEducationCard(false, false, true);
                    return;
                }
            case 6:
                setVisibility(0);
                this.mSwipeDownContainer.setVisibility(0);
                return;
            case 7:
                setVisibility(0);
                this.mDoubleTapContainer.setVisibility(0);
                return;
            default:
                this.mSwipeDownContainer.setVisibility(8);
                this.mDoubleTapContainer.setVisibility(8);
                setVisibility(8);
                return;
        }
    }

    public void setSwipeHelper(SwipeHelper swipeHelper) {
        this.mSwipeHelper = swipeHelper;
    }

    public View getCardContainer() {
        return this.mCardContainer;
    }

    public void setEducationIcon(StatusBarIconView statusBarIconView) {
        this.mEducationIcon = statusBarIconView;
    }

    public void setExpanded(boolean z) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mCardContainer.getLayoutParams();
        if (z) {
            layoutParams.height += 80;
            this.mCardContentTv.setText(R$string.cli_education_step_four);
        } else {
            layoutParams.height -= 80;
            this.mCardContentTv.setText(R$string.cli_education_step_five);
        }
        this.mCardContainer.setLayoutParams(layoutParams);
    }

    public void setOnNextListener(OnNextListener onNextListener) {
        this.mOnNextListener = onNextListener;
    }

    public void updateEducationNotePadding() {
        if (this.mEducationIcon != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mEducationNoteBottom.getLayoutParams();
            layoutParams.setMarginStart(((((int) this.mEducationIcon.getTranslationX()) - this.mEducationContainer.getLeft()) + (this.mEducationIcon.getWidth() / 2)) - (this.mEducationNoteBottom.getWidth() / 2));
            this.mEducationNoteBottom.setLayoutParams(layoutParams);
        }
    }

    public boolean shouldEducationCard() {
        return this.mCliNeedEducationCard;
    }

    public boolean shouldEducationCardAgain() {
        return this.mCliNeedEducationCardAgain;
    }

    public boolean shouldEducationSwipe() {
        return this.mCliNeedEducationSwipe;
    }

    public boolean shouldEducationDoubleTap() {
        return this.mCliNeedEducationDoubleTap;
    }

    private void setEducationCard(boolean z, boolean z2, boolean z3) {
        this.mCliNeedEducationCard = z;
        Prefs.putBoolean(this.mContext, "CliEducationCard", z);
        if (z2) {
            startEducationCardIfNeed();
        } else {
            setEducationCardAgain(false);
        }
        if (z3) {
            startEducationSwipeIfNeed();
        }
    }

    private void setEducationCardAgain(boolean z) {
        this.mCliNeedEducationCardAgain = z;
        Prefs.putBoolean(this.mContext, "CliEducationCardAgain", z);
    }

    private void setEducationSwipe(boolean z) {
        this.mCliNeedEducationSwipe = z;
        Prefs.putBoolean(this.mContext, "CliEducationSwipe", z);
        startEducationDoubleTapIfNeed();
    }

    private void setEducationDoubleTap(boolean z) {
        this.mCliNeedEducationDoubleTap = z;
        Prefs.putBoolean(this.mContext, "CliEducationDoubleTap", z);
    }

    public void startEducationCardIfNeed() {
        if (shouldEducationCardAgain()) {
            Log.d("Cli_Education", "startEducationCardIfNeed");
            AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
            AlarmTimeout alarmTimeout = this.mEducationCardAlarm;
            if (alarmTimeout != null) {
                alarmTimeout.cancel();
            }
            AlarmTimeout alarmTimeout2 = new AlarmTimeout(alarmManager, new CliEducationView$$ExternalSyntheticLambda2(this), "EDUCATION_CARD", new Handler());
            this.mEducationCardAlarm = alarmTimeout2;
            alarmTimeout2.schedule(86400000, 1);
        }
    }

    /* access modifiers changed from: private */
    public void resetEducationCard() {
        Log.d("Cli_Education", "resetEducationCard");
        AlarmTimeout alarmTimeout = this.mEducationCardAlarm;
        if (alarmTimeout != null) {
            alarmTimeout.cancel();
            this.mEducationCardAlarm = null;
        }
        this.mCardContainer.setTranslationX(0.0f);
        setExpanded(false);
        setEducationCard(true, false, false);
    }

    public void startEducationSwipeIfNeed() {
        if (!this.mCliNeedEducationSwipe) {
            Log.d("Cli_Education", "startEducationSwipeIfNeed");
            AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
            AlarmTimeout alarmTimeout = this.mEducationSwipeAlarm;
            if (alarmTimeout != null) {
                alarmTimeout.cancel();
            }
            AlarmTimeout alarmTimeout2 = new AlarmTimeout(alarmManager, new CliEducationView$$ExternalSyntheticLambda1(this), "EDUCATION_SWIPE", new Handler());
            this.mEducationSwipeAlarm = alarmTimeout2;
            alarmTimeout2.schedule(86400000, 1);
        }
    }

    /* access modifiers changed from: private */
    public void resetEducationSwipe() {
        Log.d("Cli_Education", "resetEducationSwipe");
        AlarmTimeout alarmTimeout = this.mEducationSwipeAlarm;
        if (alarmTimeout != null) {
            alarmTimeout.cancel();
            this.mEducationSwipeAlarm = null;
        }
        setEducationSwipe(true);
    }

    public void startEducationDoubleTapIfNeed() {
        if (!this.mCliNeedEducationDoubleTap) {
            Log.d("Cli_Education", "startEducationDoubleTapIfNeed");
            AlarmManager alarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
            AlarmTimeout alarmTimeout = this.mEducationDoubleTapAlarm;
            if (alarmTimeout != null) {
                alarmTimeout.cancel();
            }
            AlarmTimeout alarmTimeout2 = new AlarmTimeout(alarmManager, new CliEducationView$$ExternalSyntheticLambda0(this), "EDUCATION_DOUBLE_TAP", new Handler());
            this.mEducationDoubleTapAlarm = alarmTimeout2;
            alarmTimeout2.schedule(345600000, 1);
        }
    }

    /* access modifiers changed from: private */
    public void resetEducationDoubleTap() {
        Log.d("Cli_Education", "resetEducationDoubleTap");
        AlarmTimeout alarmTimeout = this.mEducationDoubleTapAlarm;
        if (alarmTimeout != null) {
            alarmTimeout.cancel();
            this.mEducationDoubleTapAlarm = null;
        }
        setEducationDoubleTap(true);
    }
}
