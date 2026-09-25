package com.example.yobidoon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class DateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        android.util.Log.d("YobiDoon", "DateChangeReceiverが実行されました");

        // ウィジェットを更新する
        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(context);

        ComponentName componentName =
                new ComponentName(context, WeekdayWidget.class);

        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(componentName);

        for (int appWidgetId : appWidgetIds) {
            WeekdayWidget.updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId
            );
        }

        // 次の日の0時を予約
        scheduleNextUpdate(context);
    }

    public static void scheduleNextUpdate(Context context) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(context, DateChangeReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(
                    AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}