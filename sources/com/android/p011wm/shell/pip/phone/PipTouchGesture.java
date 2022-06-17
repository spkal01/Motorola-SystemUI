package com.android.p011wm.shell.pip.phone;

/* renamed from: com.android.wm.shell.pip.phone.PipTouchGesture */
public abstract class PipTouchGesture {
    public abstract void onDown(PipTouchState pipTouchState);

    public abstract boolean onMove(PipTouchState pipTouchState);

    public abstract boolean onUp(PipTouchState pipTouchState);
}
