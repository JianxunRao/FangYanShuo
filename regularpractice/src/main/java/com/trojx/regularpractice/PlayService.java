package com.trojx.regularpractice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/2/3.
 */
public class PlayService extends Service {

    private MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String music=intent.getStringExtra("music");
        player = MediaPlayer.create(this, Uri.parse(music));
        ZhongJianRen zjr=new ZhongJianRen();
        return zjr;
    }
    class ZhongJianRen extends Binder implements PublicBussiness{

        @Override
        public void Play() {
            player.start();
        }

        @Override
        public void Pause() {
            player.pause();
        }

        @Override
        public void Stop() {
            player.stop();
        }
    }
}
