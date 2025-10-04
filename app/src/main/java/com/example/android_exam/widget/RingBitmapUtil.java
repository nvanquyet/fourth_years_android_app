package com.example.android_exam.widget;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RingBitmapUtil {

    public static Bitmap drawRing(Context ctx, int sizeDp, int percent, int strokeColor, int trackColor) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int sizePx = (int) (sizeDp * density);
        Bitmap bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        float stroke = Math.max(6f, sizePx * 0.10f);
        float pad = stroke / 2f + 2f;
        RectF oval = new RectF(pad, pad, sizePx - pad, sizePx - pad);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(stroke);

        // Track
        p.setColor(trackColor);
        c.drawArc(oval, -90, 360, false, p);

        // Progress
        p.setColor(strokeColor);
        float sweep = 360f * Math.min(100, Math.max(0, percent)) / 100f;
        c.drawArc(oval, -90, sweep, false, p);

        return bmp;
    }
}
