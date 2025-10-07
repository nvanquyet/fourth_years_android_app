package com.example.android_exam.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android_exam.R;
import com.example.android_exam.activities.HomeActivity;

public class NutritionOverviewWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.example.android_exam.widget.NutritionOverviewWidget.REFRESH";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Hiển thị placeholder trước
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildPlaceholderViews(context));
        }
        // Gọi service tải dữ liệu thật
        NutritionOverviewWidgetService.enqueueWork(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            // Nhấn refresh -> chạy service
            NutritionOverviewWidgetService.enqueueWork(context);
        }
    }

    /** Dùng khi service muốn cập nhật toàn bộ instance của widget */
    static void updateAll(Context context, RemoteViews views) {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, NutritionOverviewWidget.class);
        int[] ids = awm.getAppWidgetIds(cn);
        for (int id : ids) awm.updateAppWidget(id, views);
    }

    /** RemoteViews placeholder khi đang tải hoặc lỗi */
    // ... giữ nguyên import & class ...

    static RemoteViews buildPlaceholderViews(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.nutrition_overview_widget);
        rv.setTextViewText(R.id.tvKcalValue, "🔥 đang tải...");
        rv.setProgressBar(R.id.pbKcal, 100, 0, false);
        rv.setTextViewText(R.id.tvProtein, "Protein\n–/–g");
        rv.setTextViewText(R.id.tvCarbs,   "Carbs\n–/–g");
        rv.setTextViewText(R.id.tvFat,     "Fat\n–/–g");
        rv.setTextViewText(R.id.tvFiber,   "Fiber\n–/–g");

        // Root: mở app
        Intent openApp = new Intent(context, com.example.android_exam.activities.HomeActivity.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent piOpen = PendingIntent.getActivity(
                context, 0, openApp, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.widget_root, piOpen);

        // Nút refresh → broadcast REFRESH
        Intent refresh = new Intent(context, NutritionOverviewWidget.class).setAction(ACTION_REFRESH);
        PendingIntent piRefresh = PendingIntent.getBroadcast(
                context, 1, refresh, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.btnRefresh, piRefresh);

        return rv;
    }

}
