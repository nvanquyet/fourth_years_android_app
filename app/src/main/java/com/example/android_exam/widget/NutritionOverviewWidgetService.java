package com.example.android_exam.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.android_exam.R;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.nutrition.OverviewNutritionSummaryDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.UserInformationDto;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.data.repository.HomeRepository;
import com.example.android_exam.utils.SessionManager;

import java.math.BigDecimal;

public class NutritionOverviewWidgetService extends JobIntentService {

    private static final String TAG = "WidgetService";
    private static final int JOB_ID = 0xF00D; // any unique int

    /** Public helper để Provider gọi */
    public static void enqueueWork(Context context) {
        Intent i = new Intent(context, NutritionOverviewWidgetService.class);
        JobIntentService.enqueueWork(context, NutritionOverviewWidgetService.class, JOB_ID, i);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Đảm bảo token đã set vào ApiManager (tránh 401 khi app bị kill)
        String token = SessionManager.getToken();                       // :contentReference[oaicite:1]{index=1}
        if (token != null && !token.isEmpty()) {
            ApiManager.getInstance().setAuthToken(token);
        }

        // Hiển thị 'đang tải'
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        ComponentName cn = new ComponentName(this, NutritionOverviewWidget.class);
        int[] ids = awm.getAppWidgetIds(cn);
        for (int id : ids) {
            awm.updateAppWidget(id, NutritionOverviewWidget.buildPlaceholderViews(this));
        }

        // Lấy user & call API lấy overview qua HomeRepository
        HomeRepository repo = new HomeRepository();                    // repo tự dùng SessionManager.getUser()
        repo.getNutritionOverview(new DataCallback<ApiResponse<OverviewNutritionSummaryDto>>() {
            @Override
            public void onLoading(boolean isLoading) { /* no-op for widget */ }

            @Override
            public void onSuccess(ApiResponse<OverviewNutritionSummaryDto> result) {
                if (result != null && result.isSuccess() && result.getData() != null) {
                    RemoteViews views = buildDataViews(NutritionOverviewWidgetService.this, result.getData());
                    NutritionOverviewWidget.updateAll(NutritionOverviewWidgetService.this, views);
                } else {
                    Log.w(TAG, "Empty/failed result -> show error");
                    showError("Không có dữ liệu");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error);
                showError(error);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
                showError("Lỗi kết nối");
            }
        });
    }

    private void showError(String msg) {
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.nutrition_overview_widget);
        rv.setTextViewText(R.id.tvKcalValue, "🔥 " + msg);
        rv.setProgressBar(R.id.pbKcal, 100, 0, false);
        NutritionOverviewWidget.updateAll(this, rv);
    }

    /** Chuyển DTO → RemoteViews */
    private RemoteViews buildDataViews(Context context, OverviewNutritionSummaryDto dto) {
        // Dựa trên các getter có thật trong DTO: averageX / targetX  :contentReference[oaicite:2]{index=2}
        BigDecimal avgCal   = BigDecimal.valueOf(safe(dto.getAverageCalories()));
        BigDecimal trgCal   = BigDecimal.valueOf(safe(dto.getTargetCalories()));
        int kcalPercent = percent(avgCal, trgCal);

        BigDecimal avgPro   = BigDecimal.valueOf(safe(dto.getAverageProtein()));
        BigDecimal trgPro   = BigDecimal.valueOf(safe(dto.getTargetProtein()));
        BigDecimal avgCarb  = BigDecimal.valueOf(safe(dto.getAverageCarbs()));
        BigDecimal trgCarb  = BigDecimal.valueOf(safe(dto.getTargetCarbs()));
        BigDecimal avgFat   = BigDecimal.valueOf(safe(dto.getAverageFat()));
        BigDecimal trgFat   = BigDecimal.valueOf(safe(dto.getTargetFat()));
        BigDecimal avgFiber = BigDecimal.valueOf(safe(dto.getAverageFiber()));
        BigDecimal trgFiber = BigDecimal.valueOf(safe(dto.getTargetFiber()));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.nutrition_overview_widget);

        rv.setTextViewText(R.id.tvKcalValue,
                "🔥 " + avgCal.intValue() + " / " + trgCal.intValue());
        rv.setProgressBar(R.id.pbKcal, 100, Math.min(100, Math.max(0, kcalPercent)), false);

        rv.setTextViewText(R.id.tvProtein, "Protein\n" + avgPro.intValue() + "/" + trgPro.intValue() + "g");
        rv.setTextViewText(R.id.tvCarbs,   "Carbs\n"   + avgCarb.intValue() + "/" + trgCarb.intValue() + "g");
        rv.setTextViewText(R.id.tvFat,     "Fat\n"     + avgFat.intValue() + "/" + trgFat.intValue() + "g");
        rv.setTextViewText(R.id.tvFiber,   "Fiber\n"   + avgFiber.intValue() + "/" + trgFiber.intValue() + "g");

        // Re-add click actions giống Provider
        rv = NutritionOverviewWidget.buildPlaceholderViews(context);
        rv.setTextViewText(R.id.tvKcalValue,
                "🔥 " + avgCal.intValue() + " / " + trgCal.intValue());
        rv.setProgressBar(R.id.pbKcal, 100, Math.min(100, Math.max(0, kcalPercent)), false);
        rv.setTextViewText(R.id.tvProtein, "Protein\n" + avgPro.intValue() + "/" + trgPro.intValue() + "g");
        rv.setTextViewText(R.id.tvCarbs,   "Carbs\n"   + avgCarb.intValue() + "/" + trgCarb.intValue() + "g");
        rv.setTextViewText(R.id.tvFat,     "Fat\n"     + avgFat.intValue() + "/" + trgFat.intValue() + "g");
        rv.setTextViewText(R.id.tvFiber,   "Fiber\n"   + avgFiber.intValue() + "/" + trgFiber.intValue() + "g");

        return rv;
    }

    private static double safe(Double v) { return v == null ? 0.0 : v; }
    private static double safe(double v) { return v; }

    private static int percent(BigDecimal current, BigDecimal target) {
        if (target == null || target.compareTo(BigDecimal.ZERO) <= 0) return 0;
        return current.multiply(BigDecimal.valueOf(100))
                .divide(target, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
    }
}
