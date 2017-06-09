package com.damianmichalak.joystick;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import static com.damianmichalak.joystick.JoyStickView.Mode.CLOSED;
import static com.damianmichalak.joystick.JoyStickView.Mode.MIDDLE;
import static com.damianmichalak.joystick.JoyStickView.Mode.OPENED;

public class JoyStickView extends View {

    enum Mode {OPENED, MIDDLE, CLOSED}

    private static final int DEFAULT_WIDTH_OPENED_DP = 120;
    private static final int DEFAULT_HEIGHT_OPENED_DP = 120;

    private final float density = getResources().getDisplayMetrics().density;

    private final int defaultWidthOpenedPX = (int) (DEFAULT_WIDTH_OPENED_DP * density);
    private final int defaultHeightOpenedPX = (int) (DEFAULT_HEIGHT_OPENED_DP * density);

    private int actualWidthOpenedPX;
    private int actualHeightOpenedPX;
    private int actualHeightClosedPX;
    private int actualWidthClosedPX;

    private Mode mode = CLOSED;
    private Paint paintRed;
    private Paint paintGreen;

    private final RectF ovalModeOpened = new RectF();
    private final RectF ovalModeClosed = new RectF();
    private ObjectAnimator enlargeAnimation;
    private ObjectAnimator shrinkAnimation;

    private Drawable searchDrawable;
    private Drawable editDrawable;
    private Drawable deleteDrawable;
    private Drawable callDrawable;

    private float scaleAnimation = 0;

    public JoyStickView(Context context) {
        super(context);
        init(context);
    }

    public JoyStickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JoyStickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.d("LOGS", "constructor");
        searchDrawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_search);
        editDrawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit);
        deleteDrawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        callDrawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_call);
        paintRed = new Paint();
        paintRed.setColor(Color.RED);
        paintRed.setAntiAlias(true);
        paintGreen = new Paint();
        paintGreen.setColor(Color.GREEN);
        paintGreen.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        ovalModeClosed.set(
                (actualWidthOpenedPX / 4) - ((actualWidthOpenedPX / 4) * scaleAnimation),
                (actualHeightOpenedPX / 4) - ((actualHeightOpenedPX / 4) * scaleAnimation),
                (actualWidthOpenedPX * 3 / 4) + ((actualWidthOpenedPX / 4) * scaleAnimation),
                (actualHeightOpenedPX * 3 / 4) + ((actualHeightOpenedPX / 4) * scaleAnimation)
        );
        canvas.drawOval(ovalModeClosed, paintRed);

        if (mode == OPENED) {
            searchDrawable.setBounds(actualWidthOpenedPX / 8, actualHeightOpenedPX / 8, actualWidthOpenedPX * 3 / 8, actualHeightOpenedPX * 3 / 8);
            searchDrawable.draw(canvas);
            editDrawable.setBounds(actualWidthOpenedPX * 5 / 8, actualHeightOpenedPX / 8, actualWidthOpenedPX * 7 / 8, actualHeightOpenedPX * 3 / 8);
            editDrawable.draw(canvas);
            deleteDrawable.setBounds(actualWidthOpenedPX / 8, actualHeightOpenedPX * 5 / 8, actualWidthOpenedPX * 3 / 8, actualHeightOpenedPX * 7 / 8);
            deleteDrawable.draw(canvas);
            callDrawable.setBounds(actualWidthOpenedPX * 5 / 8, actualHeightOpenedPX * 5 / 8, actualWidthOpenedPX * 7 / 8, actualHeightOpenedPX * 7 / 8);
            callDrawable.draw(canvas);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("LOGS", "onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            actualWidthOpenedPX = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            actualWidthOpenedPX = Math.min(defaultWidthOpenedPX, widthSize);
        } else {
            actualWidthOpenedPX = defaultWidthOpenedPX;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            actualHeightOpenedPX = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            actualHeightOpenedPX = Math.min(defaultHeightOpenedPX, heightSize);
        } else {
            actualHeightOpenedPX = defaultHeightOpenedPX;
        }

        actualHeightClosedPX = actualHeightOpenedPX / 2;
        actualWidthClosedPX = actualWidthOpenedPX / 2;

        ovalModeClosed.set(actualWidthOpenedPX / 4, actualHeightOpenedPX / 4, actualWidthOpenedPX * 3 / 4, actualHeightOpenedPX * 3 / 4);
        ovalModeOpened.set(0, 0, actualWidthOpenedPX, actualHeightOpenedPX);

        Log.d("LOGS", "width:" + actualWidthOpenedPX + " | height:" + actualHeightOpenedPX);
        setMeasuredDimension(actualWidthOpenedPX, actualHeightOpenedPX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("LOGS", "down: " + scaleAnimation);
            mode = OPENED;
            enlargeAnimation = ObjectAnimator.ofFloat(this, "scaleAnimation", scaleAnimation, 1);
            enlargeAnimation.setDuration(100);
            enlargeAnimation.setInterpolator(new AccelerateInterpolator());
            enlargeAnimation.start();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("LOGS", "up: " + scaleAnimation);
            mode = CLOSED;
            shrinkAnimation = ObjectAnimator.ofFloat(this, "scaleAnimation", scaleAnimation, 0);
            shrinkAnimation.setDuration(100);
            shrinkAnimation.setInterpolator(new AccelerateInterpolator());
            shrinkAnimation.start();
        }

        if (mode.equals(OPENED)) {
            final float x = event.getX();
            final float y = event.getY();
            if (x < actualWidthOpenedPX / 2) {
                //LEFT
                if (y < actualHeightOpenedPX / 2) {
                    //TOP
                    searchDrawable.setAlpha(128);
                    deleteDrawable.setAlpha(255);
                    editDrawable.setAlpha(255);
                    callDrawable.setAlpha(255);
                } else {
                    //BOTTOM
                    searchDrawable.setAlpha(255);
                    deleteDrawable.setAlpha(128);
                    editDrawable.setAlpha(255);
                    callDrawable.setAlpha(255);
                }
            } else {
                //RIGHT
                if (y < actualHeightOpenedPX / 2) {
                    searchDrawable.setAlpha(255);
                    deleteDrawable.setAlpha(255);
                    editDrawable.setAlpha(128);
                    callDrawable.setAlpha(255);
                    //TOP
                } else {
                    //BOTTOM
                    searchDrawable.setAlpha(255);
                    deleteDrawable.setAlpha(255);
                    editDrawable.setAlpha(255);
                    callDrawable.setAlpha(128);
                }
            }
        }

        invalidate();
        return true;
    }

    public float getScaleAnimation() {
        return scaleAnimation;
    }

    public void setScaleAnimation(float scaleAnimation) {

        if (scaleAnimation == 1) {
            mode = OPENED;
        } else if (scaleAnimation == 0) {
            mode = CLOSED;
        } else {
            mode = MIDDLE;
        }

        this.scaleAnimation = scaleAnimation;
        invalidate();
    }

}
