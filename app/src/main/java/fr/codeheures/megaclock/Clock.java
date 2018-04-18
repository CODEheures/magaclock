package fr.codeheures.megaclock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class Clock extends View {

    private boolean isRunning = false;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float radius;
    private float centerX;
    private float centerY;



    public Clock(Context context) {
        super(context);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void onStart() {
        isRunning = true;
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        sleep(990);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }
            }
        }.start();
    }

    public void onResume() {
        isRunning = true;
    }

    public void onPause() {
        isRunning = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Recalc center & radius
        centerX = w/2;
        centerY = h/2;
        radius = Math.min(w/2, h/2)*0.9f;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Outer disk
        @SuppressLint("DrawAllocation") LinearGradient outerGradient = new LinearGradient(
          0, centerY-radius, 0, centerY+radius,
          new int[]{ 0xffe0e0e0, 0xff6e7774, 0xff0a0e0a, 0xff0a0809},
          new float[] {0, 0.49f, 0.5f, 1},
          Shader.TileMode.REPEAT
        );

        paint.setShader(outerGradient);
        canvas.drawCircle(centerX, centerY, radius, paint);


        //Inner disk
        paint.setShader(null);
        paint.setColor(0xff212121);
        canvas.drawCircle(centerX, centerY, radius*0.95f, paint);


        //Graduations
        paint.setColor(0xffffffff);
        paint.setStrokeWidth(3f);
        paint.setTextSize(radius/8f);

        int hour = 12;

        for (float angle = -90; angle < 270; angle+=6f) {

            double radianAngle = Math.toRadians(angle);

            if (angle%30 != 0) {
                //draw light lines
                float startX = (float) (centerX + Math.cos(radianAngle)*radius*0.85);
                float stopX = (float) (centerX + Math.cos(radianAngle)*radius*0.9);

                float startY = (float) (centerY + Math.sin(radianAngle)*radius*0.85);
                float stopY = (float) (centerY + Math.sin(radianAngle)*radius*0.9);

                canvas.drawLine(startX, startY, stopX, stopY, paint);
            } else {

                float p1X = (float) (centerX + Math.cos(radianAngle-0.01)*radius*0.8);
                float p1Y = (float) (centerY + Math.sin(radianAngle-0.01)*radius*0.8);

                float p2X = (float) (centerX + Math.cos(radianAngle-0.02)*radius*0.9);
                float p2Y = (float) (centerY + Math.sin(radianAngle-0.02)*radius*0.9);

                float p3X = (float) (centerX + Math.cos(radianAngle+0.02)*radius*0.9);
                float p3Y = (float) (centerY + Math.sin(radianAngle+0.02)*radius*0.9);

                float p4X = (float) (centerX + Math.cos(radianAngle+0.01)*radius*0.8);
                float p4Y = (float) (centerY + Math.sin(radianAngle+0.01)*radius*0.8);

                @SuppressLint("DrawAllocation") Path path = new Path();
                path.moveTo(p1X, p1Y);
                path.lineTo(p2X, p2Y);
                path.lineTo(p3X, p3Y);
                path.lineTo(p4X, p4Y);
                path.lineTo(p1X, p1Y);

                canvas.drawPath(path, paint);

                @SuppressLint("DrawAllocation") Rect textBound = new Rect();
                paint.getTextBounds(Integer.toString(hour),0, Integer.toString(hour).length(), textBound);

                float x = (float) (centerX + Math.cos(radianAngle)*radius*0.7 - textBound.width()/2);
                float y = (float) (centerY + Math.sin(radianAngle)*radius*0.7 + textBound.height()/2);

                canvas.drawText(Integer.toString(hour), x, y, paint);

                hour = ++hour%12;
            }
        }

        //Clockwise
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        //Hours
        float hourAngle = (float) Math.toRadians(-90 + (now.get(Calendar.HOUR)%12 + now.get(Calendar.MINUTE)/60f)*30);

        paint.setColor(0xffCE3B3B);
        float p1X = (float) (centerX + Math.cos(hourAngle)*radius*0.5);
        float p1Y = (float) (centerY + Math.sin(hourAngle)*radius*0.5);

        float p2X = (float) (centerX + Math.cos(hourAngle-0.34)*radius*0.1);
        float p2Y = (float) (centerY + Math.sin(hourAngle-0.34)*radius*0.1);

        float p3X = (float) (centerX + Math.cos(hourAngle+0.34)*radius*0.1);
        float p3Y = (float) (centerY + Math.sin(hourAngle+0.34)*radius*0.1);

        @SuppressLint("DrawAllocation") Path path = new Path();
        path.moveTo(p1X, p1Y);
        path.lineTo(p2X, p2Y);
        path.lineTo(p3X, p3Y);
        path.lineTo(p1X, p1Y);

        canvas.drawPath(path, paint);

        //Minutes
        float minuteAngle = (float) Math.toRadians(-90 + (now.get(Calendar.MINUTE) + now.get(Calendar.SECOND)/60f)*6);

        paint.setColor(0xffCE3B3B);
        p1X = (float) (centerX + Math.cos(minuteAngle)*radius*0.74);
        p1Y = (float) (centerY + Math.sin(minuteAngle)*radius*0.74);

        p2X = (float) (centerX + Math.cos(minuteAngle-0.2)*radius*0.1);
        p2Y = (float) (centerY + Math.sin(minuteAngle-0.2)*radius*0.1);

        p3X = (float) (centerX + Math.cos(minuteAngle+0.2)*radius*0.1);
        p3Y = (float) (centerY + Math.sin(minuteAngle+0.2)*radius*0.1);

        path.moveTo(p1X, p1Y);
        path.lineTo(p2X, p2Y);
        path.lineTo(p3X, p3Y);
        path.lineTo(p1X, p1Y);

        canvas.drawPath(path, paint);

        //Seconds
        float secondAngle = (float) Math.toRadians(-90 + (now.get(Calendar.SECOND))*6);

        paint.setColor(0xffCE3B3B);
        p1X = (float) (centerX + Math.cos(secondAngle)*radius*0.84);
        p1Y = (float) (centerY + Math.sin(secondAngle)*radius*0.84);

        p2X = (float) (centerX + Math.cos(secondAngle)*radius*0.1);
        p2Y = (float) (centerY + Math.sin(secondAngle)*radius*0.1);


        path.moveTo(p1X, p1Y);
        path.lineTo(p2X, p2Y);
        path.lineTo(p3X, p3Y);
        path.lineTo(p1X, p1Y);

        canvas.drawLine(p1X, p1Y, p2X, p2Y, paint);


        //Upper disk
        //Outer upper disk
        outerGradient = new LinearGradient(
                0, centerY-radius*0.12f, 0, centerY+radius*0.12f,
                new int[]{ 0xffe0e0e0, 0xff6e7774, 0xff0a0e0a, 0xff0a0809},
                new float[] {0, 0.49f, 0.5f, 1},
                Shader.TileMode.REPEAT
        );

        paint.setShader(outerGradient);
        canvas.drawCircle(centerX, centerY, radius*0.12f, paint);


        //Inner disk
        paint.setShader(null);
        paint.setColor(0xff212121);
        canvas.drawCircle(centerX, centerY, radius*0.1f, paint);


        super.onDraw(canvas);
    }
}
