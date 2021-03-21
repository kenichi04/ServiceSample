package com.example.servicesample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

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
        }
    }

    /* メディア再生が終了したときのリスナクラス */
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 自分自身を終了
            stopSelf();
        }
    }
}
