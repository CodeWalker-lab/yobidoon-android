package com.example.yobidoon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup fontSizeGroup;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // 画面部品を取得
        fontSizeGroup = findViewById(R.id.fontSizeGroup);
        saveButton = findViewById(R.id.saveButton);

        // 保存されている文字サイズを読み込む
        SharedPreferences preferences =
                getSharedPreferences("YobiDoonSettings", MODE_PRIVATE);

        String fontSize = preferences.getString("fontSize", "normal");

        // 保存されているサイズを選択状態にする
        switch (fontSize) {
            case "small":
                ((RadioButton) findViewById(R.id.fontSizeSmall)).setChecked(true);
                break;

            case "large":
                ((RadioButton) findViewById(R.id.fontSizeLarge)).setChecked(true);
                break;

            case "extra_large":
                ((RadioButton) findViewById(R.id.fontSizeExtraLarge)).setChecked(true);
                break;

            default:
                ((RadioButton) findViewById(R.id.fontSizeNormal)).setChecked(true);
                break;
        }

        // 保存ボタン
        saveButton.setOnClickListener(v -> {

            int checkedId = fontSizeGroup.getCheckedRadioButtonId();

            String selectedSize;

            if (checkedId == R.id.fontSizeSmall) {
                selectedSize = "small";

            } else if (checkedId == R.id.fontSizeLarge) {
                selectedSize = "large";

            } else if (checkedId == R.id.fontSizeExtraLarge) {
                selectedSize = "extra_large";

            } else {
                selectedSize = "normal";
            }

            // 選択した文字サイズを保存
            preferences.edit()
                    .putString("fontSize", selectedSize)
                    .apply();

            // メイン画面へ戻る
            finish();
        });

        // 画面端の余白を調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}