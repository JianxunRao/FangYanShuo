package com.trojx.fangyan.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.trojx.fangyan.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**展示家乡话内容 包括音/视频
 * Created by Trojx on 2016/3/20.
 */
public class JxhActivity extends AppCompatActivity {


        private WebView webView;
        private int widthPixels;
        private SeekBar seekBar;
        private MyPlayer myPlayer;
        private TextView tv_title;
        private TextView tv_lang;
        private TextView tv_provider;
        private TextView tv_description;
        private AVObject item;
        private ImageButton imageButton;
        private DownloadMusicTask task;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_jxh);



                final Intent intent=getIntent();
                item=intent.getParcelableExtra("item");
                item.increment("viewcount");
                item.saveInBackground();

                findViews();

                final int id=item.getInt("jxhid");
                if(item.getBoolean("isVoice")){
                        ImageView iv_cover= (ImageView) findViewById(R.id.iv_jxh_cover);
                        iv_cover.setVisibility(View.VISIBLE);
                        final AVFile cover=item.getAVFile("cover");
                        if(cover!=null){
                                Glide.with(this).load(cover.getUrl()).crossFade().into(iv_cover);
                        }
                        seekBar = (SeekBar) findViewById(R.id.seekbar);
                        myPlayer = new MyPlayer(seekBar);
                        myPlayer.playUrl("http://www.jiaxianghua.org/res/getaudiofile?res_id="+id);
//                        myPlayer.stop();//
                        seekBar.setVisibility(View.VISIBLE);
                        imageButton.setImageResource(R.drawable.download);
                        imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                        Intent intent1=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jiaxianghua.org/res/getaudiofile?res_id="+id));
//                                        startActivity(intent1);//下载音乐文件
                                        task = new DownloadMusicTask();
                                        task.execute(id);
                                        Toast.makeText(JxhActivity.this,"已开始下载此音乐",Toast.LENGTH_SHORT).show();
                                }
                        });
                }else {
                        WindowManager wm=getWindowManager();
                        DisplayMetrics dm=new DisplayMetrics();
                        wm.getDefaultDisplay().getMetrics(dm);
                        widthPixels = dm.widthPixels;//1080px~360  =>  widthPixels/1080*360
                        Log.e("widthPixels", widthPixels + "");
                        Log.e("heightPixels",dm.heightPixels+"");
                        Log.e("destiny", dm.density + "");
                        Log.e("destinyDPI", dm.densityDpi + "");

                        imageButton.setImageResource(R.drawable.open_new_window);
                        imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jiaxianghua.org/dialect/play/video/" + item.getInt("jxhid")));
                                        startActivity(intent1);
                                }
                        });

                        webView = (WebView) findViewById(R.id.web_view);
                        webView.setVisibility(View.VISIBLE);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebChromeClient(new WebChromeClient());

                        getWebData(id);

                }

//                player = new MediaPlayer();
//                timer = new Timer();
//                timer.schedule(task, 0, 1000);
//                try {
//                        player.reset();
//                        player.setDataSource("http://www.jiaxianghua.org/res/getaudiofile?res_id=790");
//                        player.prepareAsync();
//                        player.setLooping(true);
//                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                        mp.start();
//                                }
//                        });
//                        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                                @Override
//                                public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                                        seekBar.setSecondaryProgress(percent);
//                                }
//                        });
//                } catch (IOException e) {
//                        Log.e("jxh player error",e.toString());
//                        e.printStackTrace();
//                }
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                        private  float f;
//
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                                Log.e("progress",progress+"");
//                                if(fromUser){
//                                        float p=progress;
//                                        f=p/100;
//                                }
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//                                player.seekTo((int) (f * player.getDuration()));
//                                Log.e("seekTo",f*player.getDuration()+"");
//                        }
//                });
        }
//        TimerTask task =new TimerTask() {
//                @Override
//                public void run() {
//                        if(player==null)
//                                return;
//                        if(player.isPlaying()&& !seekBar.isPressed()){
//                                handler.sendEmptyMessage(0);
//                        }
//                }
//        };
//        Handler handler=new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                        super.handleMessage(msg);
//                        int position=player.getCurrentPosition();
//                        int duration=player.getDuration();
//                        if(duration>0){
//                                long pos=seekBar.getMax()*position/duration;
//                                seekBar.setProgress((int) pos);
//                        }
//                }
//        };

        private  void findViews(){
                Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_jxh);
                setSupportActionBar(toolbar);
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                getSupportActionBar().setTitle(item.getString("title"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

                tv_title = (TextView) findViewById(R.id.tv_jxh_title);
                tv_lang = (TextView) findViewById(R.id.tv_jxh_lang);
                tv_provider = (TextView) findViewById(R.id.tv_jxh_provider);
                tv_description = (TextView) findViewById(R.id.tv_jxh_description);
                tv_description.setMovementMethod(new ScrollingMovementMethod());
                imageButton = (ImageButton) findViewById(R.id.ib_jxh);

                tv_title.setText(item.getString("title"));
                tv_lang.setText(item.getString("lang"));
                tv_provider.setText(item.getString("provider"));
                tv_description.setText(item.getString("description").replace(" ","\r\n"));

        }

        class MyPlayer implements MediaPlayer.OnPreparedListener,MediaPlayer.OnBufferingUpdateListener,
                MediaPlayer.OnCompletionListener{

                public MediaPlayer player;
                private SeekBar seekBar;
                private Timer mTimer=new Timer();

                public MyPlayer(SeekBar seekBar){
                        super();
                        this.seekBar=seekBar;
                        player=new MediaPlayer();
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setOnBufferingUpdateListener(this);
                        player.setOnPreparedListener(this);
                        player.setLooping(true);

                        mTimer.schedule(task, 0, 1000);

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                private  float f;
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        if(fromUser){
                                                float p=progress;
                                                f=p/100;
                                        }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                        player.seekTo((int) (f * player.getDuration()));
                                        Log.e("seekTo",f*player.getDuration()+"");
                                }
                        });
                }

                public void play(){
                        player.start();
                }

                public void playUrl(String url){
                        try {
                                player.reset();
                                player.setDataSource(url);
                                player.prepare();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }

                public void stop(){
                        if(player!=null){
                                player.stop();
                                player.release();
                                player=null;
                        }
                }


                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        seekBar.setSecondaryProgress(percent);
                }

                @Override
                public void onCompletion(MediaPlayer mp) {

                }

                @Override
                public void onPrepared(MediaPlayer mp) {
                        mp.start();
                }

                TimerTask task =new TimerTask() {
                        @Override
                        public void run() {
                                if(player==null)
                                        return;
                                if(player.isPlaying()&& !seekBar.isPressed()){
                                        handler.sendEmptyMessage(0);
                                }
                        }
                };
                Handler handler=new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                int position=player.getCurrentPosition();
                                int duration=player.getDuration();
                                if(duration>0){
                                        long pos=seekBar.getMax()*position/duration;
                                        seekBar.setProgress((int) pos);
                                }
                        }
                };
        }


        /**
         * 获取视频网页的html内容
         * @param index 视频的id
         *
         */
        private void getWebData(int index){
                GetWebDataTask task=new GetWebDataTask();
                task.execute(index);
        }

        class GetWebDataTask extends AsyncTask<Integer,Integer,String>{

                @Override
                protected String doInBackground(Integer... params) {
                        StringBuffer sb=new StringBuffer();
                        try {
                                URL url=new URL("http://www.jiaxianghua.org/dialect/play/video/"+params[0]);
                                InputStream in=url.openStream();
                                InputStreamReader isr=new InputStreamReader(in);
                                BufferedReader br=new BufferedReader(isr);
                                String s;
                                while((s=br.readLine())!=null){
                                        if(s.contains("width=600")){
                                                s=s.replace("width=600","width="+360);//widthPixels/1080*360
                                                s=s.replace("height=415","height="+(360*0.66));//(widthPixels/1080*360*0.66)
                                                Log.e("600px","adjusted size.");
                                        }
                                        sb.append(s);
                                }
                                in.close();
                        } catch (java.io.IOException e) {
                                Log.e("GetWebDataTask",e.toString());
                        }
                        return sb.toString();
                }

                @Override
                protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Log.i("onPostExecute",s);
                        webView.loadData(s,"text/html","utf8");
                }
        }

        class DownloadMusicTask extends AsyncTask<Integer,Integer,String>{

                @Override
                protected String doInBackground(Integer... params) {
                        String path="";
                        try {
                                URL url=new URL("http://www.jiaxianghua.org/res/getaudiofile?res_id="+item.getInt("jxhid"));
                                InputStream in=url.openStream();
                                path="方言说_"+item.getString("title")+".mp3";
                                File file=new File(Environment.getExternalStorageDirectory(),path);
                                FileOutputStream fos=new FileOutputStream(file);
                                byte[] buff=new byte[1024];
                                int len;
                                while ((len=in.read(buff))!=-1){
                                        fos.write(buff,0,len);
                                        fos.flush();
                                }
                                in.close();
                                fos.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("download music",e.toString());
                        }
                        return path;
                }

                @Override
                protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Toast.makeText(JxhActivity.this,"音频已下载,"+ s,Toast.LENGTH_LONG).show();
                }
        }


        @Override
        protected void onDestroy() {
                super.onDestroy();
                if(myPlayer!=null){
                        myPlayer.stop();
                }
                if(webView!=null){
                        webView.destroy();
                }
                if(task!=null){
                        task.cancel(true);
                }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                if(item.getItemId()==android.R.id.home){
                        finish();
                }
                return true;
        }


}
