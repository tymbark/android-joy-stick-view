package com.damianmichalak.joy_stick;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import static com.damianmichalak.joy_stick.JoyStickView.Mode.CLOSED;
import static com.damianmichalak.joy_stick.JoyStickView.Mode.MIDDLE;
import static com.damianmichalak.joy_stick.JoyStickView.Mode.OPENED;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class JoyStickView extends View {

    enum Mode {OPENED, MIDDLE, CLOSED}

    private static final int DEFAULT_WIDTH_OPENED_DP = 120;
    private static final int DEFAULT_HEIGHT_OPENED_DP = 120;

    private final float density = getResources().getDisplayMetrics().density;

    private final int defaultWidthOpenedPX = (int) (DEFAULT_WIDTH_OPENED_DP * density);
    private final int defaultHeightOpenedPX = (int) (DEFAULT_HEIGHT_OPENED_DP * density);

    private Mode mode = CLOSED;
    private Paint paintRed;
    private Paint paintGreen;
    private Paint paintWhite;
    private Paint paintBlue;

    private final RectF ovalModeOpened = new RectF();
    private final RectF ovalModeClosed = new RectF();

    private final List<PointF> drawableCenterPoints = new ArrayList<>();

    private Drawable searchDrawable;
    private Drawable editDrawable;
    private Drawable deleteDrawable;
    private Drawable callDrawable;

    private float scaleAnimation = 0;

    private int diameter;
    private int count = 4;
    private int angle = 0;
    private float spread = 0.5f;

    private PointF click = new PointF();

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
        paintGreen.setStrokeWidth(10);
        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setAntiAlias(true);
        paintBlue = new Paint();
        paintBlue.setColor(Color.BLUE);
        paintBlue.setAntiAlias(true);
        calculateDrawableCenterPoints();
    }

    @Override
    public void invalidate() {
        calculateDrawableCenterPoints();
        super.invalidate();
    }

    private void calculateDrawableCenterPoints() {
        if (count < 2) return;

        drawableCenterPoints.clear();
        final int width8 = diameter / 8;

        for (int i = 0; i < count; i++) {
            int r = (int) (width8 * 4 * spread);
            int centerX = width8 * 4;
            int centerY = width8 * 4;

            final int slice = ((i * (360 / count)) + angle) % 360;

            float drawableCenterX = (float) (centerX + r * cos(Math.toRadians(slice)));
            float drawableCenterY = (float) (centerY + r * sin(Math.toRadians(slice)));

            drawableCenterPoints.add(new PointF(drawableCenterX, drawableCenterY));

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final int height8 = diameter / 8;
        final int width8 = diameter / 8;

        ovalModeClosed.set(
                (diameter / 4) - ((diameter / 4) * scaleAnimation),
                (diameter / 4) - ((diameter / 4) * scaleAnimation),
                (diameter * 3 / 4) + ((diameter / 4) * scaleAnimation),
                (diameter * 3 / 4) + ((diameter / 4) * scaleAnimation)
        );
        canvas.drawOval(ovalModeClosed, paintRed);

        drawDebugLines(canvas);
        drawDebugClick(canvas);

        if (count < 2) return;

        if (mode == OPENED) {
            for (PointF point : drawableCenterPoints) {
                searchDrawable.setBounds((int) point.x - width8, (int) point.y - height8, (int) point.x + width8, (int) point.y + height8);
                searchDrawable.draw(canvas);
            }
        }
    }

    private void drawDebugClick(Canvas canvas) {

        final int width8 = diameter / 8;

        int r = width8 * 4;
        int x0 = width8 * 4;
        int y0 = width8 * 4;

        int px;
        int py;

        px = (int) (click.x);
        py = (int) (click.y);

        canvas.drawLine(x0, y0, px, py, paintWhite);

    }

    private void drawDebugLines(Canvas canvas) {
        for (int i = 0; i < count; i++) {
            final int width8 = diameter / 8;

            int r = width8 * 4;
            int x0 = width8 * 4;
            int y0 = width8 * 4;

            int px;
            int py;

            final int slice = (i * (360 / count)) + angle + (180 / count);

            px = (int) (x0 + r * cos(Math.toRadians(slice)));
            py = (int) (y0 + r * sin(Math.toRadians(slice)));

            canvas.drawLine(x0, y0, px, py, paintBlue);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int actualWidthOpenedPX;
        int actualHeightOpenedPX;

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

        // picking the smaller side of the view as diameter
        diameter = Math.min(actualHeightOpenedPX, actualWidthOpenedPX);

        ovalModeClosed.set(diameter / 4, diameter / 4, diameter * 3 / 4, diameter * 3 / 4);
        ovalModeOpened.set(0, 0, diameter, diameter);

        setMeasuredDimension(diameter, diameter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mode = OPENED;
            final ObjectAnimator enlargeAnimation = ObjectAnimator.ofFloat(this, "scaleAnimation", scaleAnimation, 1);
            enlargeAnimation.setDuration(100);
            enlargeAnimation.setInterpolator(new AccelerateInterpolator());
            enlargeAnimation.start();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mode = CLOSED;
            final ObjectAnimator shrinkAnimation = ObjectAnimator.ofFloat(this, "scaleAnimation", scaleAnimation, 0);
            shrinkAnimation.setDuration(100);
            shrinkAnimation.setInterpolator(new AccelerateInterpolator());
            shrinkAnimation.start();
        }

        if (mode.equals(OPENED)) {
            final float x = event.getX();
            final float y = event.getY();

            click.set(x, y);
//            Log.d("CHUJ", " click p" + click);
            Log.d("CHUJ", " click angle" + (180 + Math.toDegrees(Math.atan2(click.y - diameter / 2, click.x - diameter / 2))));
        }

        invalidate();
        return true;
    }

    public void setItemsCount(int count) {
        this.count = count;
        invalidate();
    }

    public void setAngle(int angle) {
        this.angle = angle;
        invalidate();
    }

    public void setOpenedFixedMode(boolean on) {
        mode = on ? OPENED : CLOSED;
        scaleAnimation = on ? 1 : 0;
        invalidate();
    }

    public boolean getMode() {
        return mode.equals(OPENED);
    }

    public float getSpread() {
        return spread;
    }

    public void setSpread(float spread) {
        this.spread = spread;
        invalidate();
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
