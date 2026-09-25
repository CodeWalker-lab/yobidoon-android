package com.example.yobidoon;

import android.os.Bundle;
import android.widget.TextView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification;
import android.os.Build;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 設定ボタン
        findViewById(R.id.settingsButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // 通知チャンネルを作成
        createNotificationChannel();

        // 通知の許可を確認
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{"android.permission.POST_NOTIFICATIONS"},
                        100
                );

            } else {
                showWeekdayNotification();
            }
        } else {
            showWeekdayNotification();
        }

        // 今日の曜日を表示
        updateWeekday();

        // 次の日の0時にウィジェットを更新するよう予約
        DateChangeReceiver.scheduleNextUpdate(this);
        android.util.Log.d("YobiDoon", "次回の0時更新を予約しました");

        // 画面端の余白を調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 今日の曜日を取得して画面に表示する
    private void updateWeekday() {

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String weekday;
        int backgroundColor;

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                weekday = "月";
                backgroundColor = 0xFFE3F2FD; // 薄い青
                break;

            case Calendar.TUESDAY:
                weekday = "火";
                backgroundColor = 0xFFFFEBEE; // 薄い赤
                break;

            case Calendar.WEDNESDAY:
                weekday = "水";
                backgroundColor = 0xFFE8F5E9; // 薄い緑
                break;

            case Calendar.THURSDAY:
                weekday = "木";
                backgroundColor = 0xFFFFF3E0; // 薄いオレンジ
                break;

            case Calendar.FRIDAY:
                weekday = "金";
                backgroundColor = 0xFFFFF8E1; // 薄い黄色
                break;

            case Calendar.SATURDAY:
                weekday = "土";
                backgroundColor = 0xFFE3F2FD; // 薄い青
                break;

            case Calendar.SUNDAY:
                weekday = "日";
                backgroundColor = 0xFFFFEBEE; // 薄い赤
                break;

            default:
                weekday = "";
                backgroundColor = 0xFFFFFFFF; // 白
                break;
        }

        TextView weekdayText = findViewById(R.id.weekdayText);
        weekdayText.setText(weekday);

// 保存されている文字サイズを読み込む
        SharedPreferences preferences =
                getSharedPreferences("YobiDoonSettings", MODE_PRIVATE);

        String fontSize = preferences.getString("fontSize", "normal");

// 選択された文字サイズを設定
        float textSize;

        switch (fontSize) {
            case "small":
                textSize = 120;
                break;

            case "large":
                textSize = 240;
                break;

            case "extra_large":
                textSize = 300;
                break;

            default:
                textSize = 180;
                break;
        }

        weekdayText.setTextSize(textSize);

    // 曜日に応じて背景色を変更
        findViewById(R.id.main).setBackgroundColor(backgroundColor);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // アプリ画面に戻ってきたら曜日を再確認
        updateWeekday();
    }

    // 通知の許可結果を受け取る
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                showWeekdayNotification();
            }
        }
    }

    private void createNotificationChannel() {

        // Android 8.0以上で通知チャンネルを作成
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "曜日通知";
            String description = "今日の曜日を通知します";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel =
                    new NotificationChannel("weekday_channel", name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }
    private void showWeekdayNotification() {

        Notification notification = new NotificationCompat.Builder(this, "weekday_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ようびドーン")
                .setContentText("今日は" + getTodayWeekday() + "曜日です")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);

        notificationManager.notify(1, notification);
    }
    private String getTodayWeekday() {

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