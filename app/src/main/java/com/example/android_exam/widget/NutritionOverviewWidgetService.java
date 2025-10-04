package com.example.android_exam.widget;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android_exam.R;

public class NutritionOverviewWidgetService extends IntentService {

    public NutritionOverviewWidgetService() {
        super("NutritionOverviewWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NutritionOverviewWidget.renderLoading(this);

        // TODO: Lấy dữ liệu thật từ Repository/API của bạn
        // Ví dụ dữ liệu mẫu:
        int kcalCurrent = 1850, kcalTarget = 2200;
        int kcalPercent = Math.min(100, (int) (100f * kcalCurrent / Math.max(1, kcalTarget)));

        Macro protein = new Macro("Protein", 68, 90, 0xFF1976D2); // blue
        Macro carbs   = new Macro("Carbs",   205,250, 0xFFF57C00); // orange
        Macro fat     = new Macro("Fat",     42,  65, 0xFF8E24AA); // purple
        Macro fiber   = new Macro("Fiber",   15,  26, 0xFF388E3C); // green

        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_nutrition_overview);

        // Kcal
        rv.setTextViewText(R.id.tvKcalValue, "🔥 " + kcalCurrent + " / " + kcalTarget);
        rv.setProgressBar(R.id.pbKcal, 100, kcalPercent, false);

        // Vẽ vòng tròn cho từng macro
        rv.setImageViewBitmap(R.id.imgProtein,
                RingBitmapUtil.drawRing(this, 72, protein.percent(), protein.color, 0x22000000));
        rv.setTextViewText(R.id.tvProtein, protein.label);

        rv.setImageViewBitmap(R.id.imgCarbs,
                RingBitmapUtil.drawRing(this, 72, carbs.percent(), carbs.color, 0x22000000));
        rv.setTextViewText(R.id.tvCarbs, carbs.label);

        rv.setImageViewBitmap(R.id.imgFat,
                RingBitmapUtil.drawRing(this, 72, fat.percent(), fat.color, 0x22000000));
        rv.setTextViewText(R.id.tvFat, fat.label);

        rv.setImageViewBitmap(R.id.imgFiber,
                RingBitmapUtil.drawRing(this, 72, fiber.percent(), fiber.color, 0x22000000));
        rv.setTextViewText(R.id.tvFiber, fiber.label);

        NutritionOverviewWidget.setRefreshIntent(this, rv);

        AppWidgetManager.getInstance(this)
                .updateAppWidget(new ComponentName(this, NutritionOverviewWidget.class), rv);
    }

    static class Macro {
        final String label; final int current; final int target; final int color;
        Macro(String l, int c, int t, int color){ this.label=l; this.current=c; this.target=t; this.color=color; }
        int percent(){ return Math.min(100, (int)(100f * current / Math.max(1, target))); }
    }
}

