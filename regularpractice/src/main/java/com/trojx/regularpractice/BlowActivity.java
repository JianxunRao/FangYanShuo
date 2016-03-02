package com.trojx.regularpractice;

import android.app.Service;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/2/17.
 */
public class BlowActivity extends AppCompatActivity {

    private static int[] samplingRates = {8000, 11025, 16000, 22050, 44100};
    private  boolean recording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blow);
    }
    private class AudioThread extends  Thread{
        private Handler handler;
        private  int minVolume;

        public AudioThread(int level,Handler handler){
            this.handler=handler;
            switch (level){
                case 0:
                    minVolume = 30000;//音量大小阈值，可以根据需求自行更改
                    break;
                case 1:
                    minVolume = 25000;
                    break;
                case 2:
                    minVolume = 20000;
                    break;
            }
        }
        @Override
        public void run() {
            recording=true;
            analyze();

        }
        private  void analyze(){
            for(int i=0;i<samplingRates.length;i++){
                int minSize= AudioRecord.getMinBufferSize(samplingRates[i],
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);//获取允许的最小缓冲区大小
                AudioRecord ar=new AudioRecord(MediaRecorder.AudioSource.MIC,
                        samplingRates[i],
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,minSize);
                if(ar.getState()==AudioRecord.STATE_INITIALIZED){
                    short[] buff=new short[minSize];
                    ar.startRecording();
                    while (recording){
                        ar.read(buff,0,minSize);//将音频数据从硬件读入缓冲区内
                        for(short s:buff){
                            if(Math.abs(s)>minVolume){//当该平率的音量超过阈值时，向handler发送一条message
                                handler.sendEmptyMessage(0);
                            }
                        }
                    }
                    ar.stop();
                    i=samplingRates.length;
                }
                ar.release();
                ar=null;
            }
        }
    }

    /**
     * 开启线程分析当前录音
     * @param v
     */
    public void blow(View v){
        Thread thread=new AudioThread(0,new Handler(new Handler.Callback() {
            private long last=0;
            @Override
            public boolean handleMessage(Message msg) {
                long now=System.currentTimeMillis();
                if (now-last < 1000) {//由于message是连续不断地接收的，设定响应时间间隔
                    return false;
                } else {
                    Log.e("Detected!","");
                    Toast.makeText(BlowActivity.this,"Detected!",Toast.LENGTH_SHORT).show();
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(100);//使手机震动0.1秒作为反馈
                    last = now;
                }
                return true;
            }
        }));
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recording=false;
    }
}
