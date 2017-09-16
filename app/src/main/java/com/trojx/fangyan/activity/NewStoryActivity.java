package com.trojx.fangyan.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.trojx.fangyan.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/26.
 */
public class NewStoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et_story;
    private ImageView iv_location;
    private TextView tv_location;
    private CheckBox cb_location;
    private ButtonFloatSmall bt_main;
    private TextView tv_time;
    private ImageButton ib_delete;
    private TextView tv_hint;

    LocationManager locationManager;
    private Location location;
    private MediaRecorder mRecorder;
    private Timer timer;
    private TimerTask task;
    private static final int GET_LOCATION = -1;
    private static final int REFRESH_TIME = 0;
    private static final int STATUS_IDLE = 1;
    private static final int STATUS_RECORDING = 2;
    private static final int STATUS_RECORDED = 3;
    private static final int STATUS_PLAYING = 4;
    private Handler handler;
    private int currentTime;
    private MediaPlayer mPlayer;
    private int currentState;
    private int currentTimeLong = 0;//音频时长
    public static final String TAG="NewStoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_story);

        findViews();
        currentTimeLong = 0;
        currentTime = 0;
        currentState = STATUS_IDLE;
        getLatLng();



//        handler = new Handler(){ //leak???
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what){
//                    case 1:
//                        String location= (String) msg.obj;
//                        tv_location.setText(location);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_LOCATION:
                        String location = (String) msg.obj;
                        tv_location.setText(location);
                        break;
                    case REFRESH_TIME:
                        currentTime = currentTime + 1;
                        tv_time.setText(currentTime + "秒");
                        currentTimeLong = currentTimeLong + 1;
                    default:
                        break;
                }
                return true;
            }
        });
        bt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentState) {
                    case STATUS_IDLE:
                        currentState = STATUS_RECORDING;
                        startRecord();
                        break;
                    case STATUS_RECORDING:
                        currentState = STATUS_RECORDED;
                        stopRecord();
                        break;
                    case STATUS_RECORDED:
                        currentState = STATUS_PLAYING;
                        startPlay();
                        break;
                    case STATUS_PLAYING:
                        currentState = STATUS_RECORDED;
                        stopPlay();
                }
            }
        });
        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState == STATUS_RECORDED) {
//                    Dialog dialog=new Dialog(NewWordActivity.this,"确认","删除这个录音吗?\r\n你可以再次录音");
//                    dialog.addCancelButton("删除");
//                    dialog.show();
//                    dialog.getButtonAccept().setText("删除");
//                    dialog.getButtonAccept().setTextColor(getResources().getColor(R.color.firebrick));
//                    dialog.getButtonCancel().setTextColor(getResources().getColor(R.color.cadetblue));
//                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            currentState=STATUS_IDLE;
//                            currentTime=0;
//                            bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_microphone));
//
//                        }
//                    });
                    currentState = STATUS_IDLE;
                    currentTime = 0;
                    bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_microphone));
                    ib_delete.setVisibility(View.GONE);
                    tv_time.setVisibility(View.GONE);
                    tv_hint.setText("点击按钮，开始录制发音");
                }
            }
        });
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_new_story);
        setSupportActionBar(toolbar);
        toolbar.setTitle("新故事");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        ab.setTitle("新故事");

        et_story = (EditText) findViewById(R.id.et_new_story);
        iv_location = (ImageView) findViewById(R.id.iv_new_story_location);
        tv_location = (TextView) findViewById(R.id.tv_new_story_location);
        cb_location = (CheckBox) findViewById(R.id.cb_use_location);
        bt_main = (ButtonFloatSmall) findViewById(R.id.bt_record);
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_microphone));
        bt_main.setRippleSpeed(20f);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setVisibility(View.GONE);
        ib_delete = (ImageButton) findViewById(R.id.ib_delete);
        ib_delete.setVisibility(View.GONE);
        tv_hint = (TextView) findViewById(R.id.tv_record_hint);


    }

    /**
     * 开始录音
     */
    private void startRecord() {

        PackageManager pm = getPackageManager();
        boolean recordPermission = pm.checkPermission("android.permission.RECORD_AUDIO", "com.trojx.fangyan")
                == PackageManager.PERMISSION_GRANTED;
        if (!recordPermission) {
            Toast.makeText(this, "没有录音权限", Toast.LENGTH_SHORT).show();
        } else {
            currentTimeLong = 0;

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/newStoryRecord.3gp");
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (Exception e) {
                Log.e("mRecorder prepare error", e.toString());
            }
            mRecorder.start();
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = REFRESH_TIME;
                    handler.sendMessage(message);
                }
            };
            timer.schedule(task, 0, 1000);

            tv_time.setVisibility(View.VISIBLE);
            tv_hint.setText("正在录音，点击按钮停止");
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_play));
        ib_delete.setVisibility(View.VISIBLE);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        timer.cancel();
        timer = null;
        currentTime = 0;

        tv_hint.setText("录音完成，点击按钮试听录音");
    }

    /**
     * 播放录音
     */
    private void startPlay() {
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_pause));
        mPlayer = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/newStoryRecord.3gp"));
//            mPlayer.prepare();
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentState = STATUS_RECORDED;
                bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_play));
            }
        });
    }

    /**
     * 停止播放录音
     */
    private void stopPlay() {
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_play));
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 获取经纬度数值
     */
    private void getLatLng() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.NETWORK_PROVIDER;
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(grant -> {
            if (grant) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "getLatLng: grant="+grant );
                }
                Log.i(TAG, "getLatLng: Providers"+locationManager.getAllProviders().toString());
                location = locationManager.getLastKnownLocation(provider);
                getAddress();
            }else {
                Log.e(TAG, "getLatLng: grant="+grant );
            }
        });
    }

    /**
     * 由经纬度获取地理位置
     */
    private void getAddress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    StringBuilder url=new StringBuilder();
                    url.append("http://api.map.baidu.com/geocoder/v2/?ak=hRPA3mCvMq00f57wndGUVQ42" +
                            "&mcode=AF:91:42:AA:61:C1:95:22:E6:0C:14:93:0A:DA:FC:45:D6:DC:3B:27;com.trojx.fangyan&callback=renderReverse&location=");
                    url.append(location.getLatitude()+","+location.getLongitude());
                    url.append("&output=json&pois=0");
                    URL urls=new URL(url.toString());
                    InputStream ins=urls.openStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(ins));
                    String line;
                    StringBuilder response=new StringBuilder();
                    while ((line=br.readLine())!=null){
                        response.append(line);
                    }
                    Log.e("response",response.toString());
                    JSONObject jsonObject=new JSONObject(response.toString().substring(29));
                    JSONObject result=jsonObject.getJSONObject("result");
                    JSONObject addressComponent=result.getJSONObject("addressComponent");
                    String LocationMsg=new String(addressComponent.getString("province")+addressComponent.getString("city"));
                    Log.e("locationMsg",LocationMsg.toString());
                    Message message=new Message();
                    message.what=GET_LOCATION;
                    message.obj=LocationMsg;
                    handler.sendMessage(message);
//                    Log.e("city",addressComponent.getString("city"));
//                    Log.e("province",addressComponent.getString("province"));
                }catch (Exception e){
                    Log.e("get location error",e.toString());
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_word,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.it_send_new_word:
                sendNewStory();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 保存新添加故事至Cloud
     */
    private void sendNewStory(){

        if(currentState==STATUS_IDLE||currentState==STATUS_PLAYING){
            final Dialog dialog=new Dialog(this,"错误","你好像好没有录制发音哦");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else {
            final ProgressDialog progressDialog=new ProgressDialog(this,"发布中",R.color.colorPrimary);
            progressDialog.show();

            AVUser user=AVUser.getCurrentUser();
            final AVObject newStory=new AVObject("story");
            newStory.put("content",et_story.getText().toString());
            if(cb_location.isCheck()){
                String location=tv_location.getText().toString();
                if(!location.equals("正在定位")){
                    newStory.put("location",location);
                }
            }
            newStory.put("provider",user);
            AVGeoPoint geoPoint=new AVGeoPoint(location.getLatitude(),location.getLongitude());
            newStory.put("geoPoint",geoPoint);
            newStory.put("timelong",currentTimeLong*1000);

//        newStory.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if(e!=null){
//                    Log.e("save story error",e.toString());
//                }
//            }
//        });

            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/newStoryRecord.3gp");
            try{
                final AVFile avFile=AVFile.withFile(et_story.getText().toString().hashCode()+".3gp",file);
                final int[] flag=new int[1];
//                flag[0]=0;
                avFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e==null){
//                            flag[0]++;
//                            Log.e("file save",flag[0]+"");
//                            if(flag[0]==2){
//                                progressDialog.dismiss();
//                                setResult(RESULT_OK);
//                                finish();
//                            }
                        }else {
                            Log.e("upload voice error",e.toString());
                        }
                    }
                });
                newStory.put("voiceFile",avFile);
                newStory.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e!=null){
                            Log.e("save error",e.toString());
                        }else {
//                            flag[0]++;
//                            Log.e("story save",flag[0]+"");
//                            if(flag[0]==2){
                                progressDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
//                            }
                        }
                    }
                });
            }catch (IOException e){
                Log.e("send newStory error",e.toString());
            }
        }
    }
}
