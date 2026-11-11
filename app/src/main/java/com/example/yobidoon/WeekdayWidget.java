package com.example.yobidoon;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;
import java.util.Calendar;

public class WeekdayWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        updateAllWidgets(context);
        DateChangeReceiver.scheduleNextUpdate(context);
    }

    @Override
    public void onEnabled(Context context) {
        DateChangeReceiver.scheduleNextUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {
        DateChangeReceiver.cancelNextUpdate(context);
    }

    static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, WeekdayWidget.class);
        int[] ids = manager.getAppWidgetIds(provider);
        for (int id : ids) {
            RemoteViews views = new RemoteViews(
                    context.getPackageName(), R.layout.weekday_widget);
            views.setTextViewText(R.id.appwidget_text, getTodayWeekday());
            manager.updateAppWidget(id, views);
        }
    }

    private static String getTodayWeekday() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY: return "日";
            case Calendar.MONDAY: return "月";
            case Calendar.TUESDAY: return "火";
            case Calendar.WEDNESDAY: return "水";
            case Calendar.THURSDAY: return "木";
            case Calendar.FRIDAY: return "金";
            case Calendar.SATURDAY: return "土";
            default: return "";
        }
    }
}
