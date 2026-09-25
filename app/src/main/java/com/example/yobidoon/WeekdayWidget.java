package com.example.yobidoon;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class WeekdayWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String widgetText = getTodayWeekday();

        android.util.Log.d("YobiDoon", "ウィジェット更新処理を実行しました：" + widgetText);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weekday_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

// ホーム画面とロック画面で文字サイズを変更
        android.os.Bundle options =
                appWidgetManager.getAppWidgetOptions(appWidgetId);

        int hostCategory =
                options.getInt(
                        AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY,
                        -1
                );

        if (hostCategory == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD) {
            // ロック画面
            views.setTextViewTextSize(
                    R.id.appwidget_text,
                    android.util.TypedValue.COMPLEX_UNIT_SP,
                    300
            );
        } else {
            // ホーム画面
            views.setTextViewTextSize(
                    R.id.appwidget_text,
                    android.util.TypedValue.COMPLEX_UNIT_SP,
                    120
            );
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        android.util.Log.d("YobiDoon", "ウィジェット onUpdate が呼ばれました");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static String getTodayWeekday() {

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "日";
            case Calendar.MONDAY:
                return "月";
            case Calendar.TUESDAY:
                return "火";
            case Calendar.WEDNESDAY:
                return "水";
            case Calendar.THURSDAY:
                return "木";
            case Calendar.FRIDAY:
                return "金";
            case Calendar.SATURDAY:
                return "土";
            default:
                return "";
        }
    }
}