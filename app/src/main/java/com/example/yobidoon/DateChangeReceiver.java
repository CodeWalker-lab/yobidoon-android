
package com.example.yobidoon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class DateChangeReceiver extends BroadcastReceiver {

    private static final String ACTION_MIDNIGHT_UPDATE =
            "com.example.yobidoon.ACTION_MIDNIGHT_UPDATE";

    private static final int REQUEST_CODE = 100;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        // 受信を許可するアクションだけを処理する
        if (!ACTION_MIDNIGHT_UPDATE.equals(action)
                && !Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_TIME_CHANGED.equals(action)
                && !Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                && !Intent.ACTION_DATE_CHANGED.equals(action)) {
            return;
        }

// ウィジェットが配置されているか確認
        android.appwidget.AppWidgetManager manager =
                android.appwidget.AppWidgetManager.getInstance(context);

        android.content.ComponentName provider =
                new android.content.ComponentName(context, WeekdayWidget.class);

        int[] widgetIds = manager.getAppWidgetIds(provider);

// ウィジェットがある場合だけ更新・再予約
        if (widgetIds.length > 0) {
            WeekdayWidget.updateAllWidgets(context);
            scheduleNextUpdate(context);
        }
    }


    public static void scheduleNextUpdate(Context context) {

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        Calendar next = Calendar.getInstance();
        next.add(Calendar.DAY_OF_MONTH, 1);
        next.set(Calendar.HOUR_OF_DAY, 0);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(context, DateChangeReceiver.class);
        intent.setAction(ACTION_MIDNIGHT_UPDATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );

        // Doze中も実行できるようにし、必要なら端末を起動
        alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                next.getTimeInMillis(),
                pendingIntent
        );
    }

    public static void cancelNextUpdate(Context context) {

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        Intent intent = new Intent(context, DateChangeReceiver.class);
        intent.setAction(ACTION_MIDNIGHT_UPDATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE
                        | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
