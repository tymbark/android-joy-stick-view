package com.damianmichalak.joystick;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


public class JoyStickViewGroup extends ViewGroup {

    public JoyStickViewGroup(Context context) {
        super(context);
    }

    public JoyStickViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JoyStickViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
