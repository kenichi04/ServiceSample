package com.example.servicesample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通知タップによる起動時か判定
        Intent intent = getIntent();
        boolean fromNotification = intent.getBooleanExtra("fromNotification", false);
        if(fromNotification) {
            // 通知タップからの起動時
            Button btPlay = findViewById(R.id.btPlay);
            Button btStop = findViewById(R.id.btStop);
            btPlay.setEnabled(false);
            btStop.setEnabled(true);
        }
    }

    public void onPlayButtonClick(View view) {
        // サービスの起動処理
        Intent intent = new Intent(MainActivity.this, SoundManageService.class);
        startService(intent);

        Button btPlay = findViewById(R.id.btPlay);
        Button btStop = findViewById(R.id.btStop);
        btPlay.setEnabled(false);
        btStop.setEnabled(true);
    }

    public void onStopButtonClick(View view) {
        // サービスの停止処理
        Intent intent = new Intent(MainActivity.this, SoundManageService.class);
        stopService(intent);

        Button btPlay = findViewById(R.id.btPlay);
        Button btStop = findViewById(R.id.btStop);
        btPlay.setEnabled(true);
        btStop.setEnabled(false);
    }
}