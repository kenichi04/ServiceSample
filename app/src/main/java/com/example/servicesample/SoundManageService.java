package com.example.servicesample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class SoundManageService extends Service {

    private MediaPlayer _player;

    public SoundManageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        _player = new MediaPlayer();

        // 通知チャネルのID文字列を用意
        String id = "soundmanagerservice_notification_channel";
        // 通知チャネル名
        String name = getString(R.string.notification_channel_name);
        // 通知チャネルの重要度を標準に設定
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        // 通知チャネルを生成
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        // NotificationManagerオブジェクト取得
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知チャネルを設定
        manager.createNotificationChannel(channel);
    }

    // バックグラウンドで行う処理を記述
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String mediaFileUriStr =
                "android.resource://" + getPackageName() + "/" + R.raw.mountain_stream;
        Uri mediaFileUri = Uri.parse(mediaFileUriStr);
        try {
            // メディアプレーヤーに音声ファイルを指定
            _player.setDataSource(SoundManageService.this, mediaFileUri);
            // 非同期でメディア再生準備が完了した際のリスナを設定
            _player.setOnPreparedListener(new PlayerPreparedListener());
            // メディア再生が終了した際のリスナを設定
            _player.setOnCompletionListener(new PlayerCompletionListener());
            // 非同期でメディア再生を準備
            _player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 定数を返す. この値によってサービスが強制終了した場合の振る舞いが変わる
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(_player.isPlaying()) {
            _player.stop();
        }
        _player.release();
        _player = null;
    }

    /* メディア再生準備が完了時のリスナクラス */
    private class PlayerPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // メディアを再生
            mp.start();

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(SoundManageService.this, "soundmanagerservice_notification_channel");
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            builder.setContentTitle(getString(R.string.msg_notification_title_start));
            builder.setContentText(getString(R.string.msg_notification_text_start));

            // 起動先Activityクラスを指定したIntentオブジェクトを生成
            Intent intent = new Intent(SoundManageService.this, MainActivity.class);
            intent.putExtra("fromNotification", true);
            // PendingIntentオブジェクト取得/ PendingIntent: 指定されたタイミングで何かを起動するインテント
            PendingIntent stopServiceIntent =
                    PendingIntent.getActivity(SoundManageService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            // PendingIntentオブジェクトをbuilderに設定
            builder.setContentIntent(stopServiceIntent);
            // タップされた通知メッセージを自動的に消去する
            builder.setAutoCancel(true);

            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, notification);
        }
    }

    /* メディア再生が終了したときのリスナクラス */
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // Notificationを作成するBuilderクラス生成
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(SoundManageService.this, "soundmanagerservice_notification_channel");
            // 通知エリアに表示されるアイコンを設定
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            // 通知ドロワーでの表示タイトルを設定
            builder.setContentTitle(getString(R.string.msg_notification_title_finish));
            // 通知ドロワーでの表示メッセージを設定
            builder.setContentText(getString(R.string.msg_notification_text_finish));
            // builderからNotificationオブジェクト生成
            Notification notification = builder.build();
            // NotificationManagerオブジェクト取得
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知
            manager.notify(0, notification);

            // 自分自身でサービスを終了→onDestroy()が呼び出される
            stopSelf();
        }
    }
}
