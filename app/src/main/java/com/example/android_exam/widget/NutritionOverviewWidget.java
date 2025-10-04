package com.example.android_exam.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android_exam.R;

public class NutritionOverviewWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "your.package.widget.ACTION_REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            startUpdate(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        super.onUpdate(context, manager, appWidgetIds);
        startUpdate(context);
    }

    private void startUpdate(Context context) {
        Intent svc = new Intent(context, NutritionOverviewWidgetService.class);
        context.startService(svc);
    }

    static void renderLoading(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_nutrition_overview);
        rv.setTextViewText(R.id.tvKcalValue, "Đang tải…");
        setRefreshIntent(context, rv);
        AppWidgetManager.getInstance(context)
                .updateAppWidget(new ComponentName(context, NutritionOverviewWidget.class), rv);
    }

    static void setRefreshIntent(Context context, RemoteViews rv) {
        Intent refresh = new Intent(context, NutritionOverviewWidget.class).setAction(ACTION_REFRESH);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, refresh, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        rv.setOnClickPendingIntent(R.id.root, pi);
    }
}

