package com.trojx.fangyan.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trojx.fangyan.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/22.
 */
public class NewWordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private MaterialEditText et_new_word;
    private ButtonFlat spinner;
    private CheckBox checkBox;
    private TextView tv_time;
    private TextView tv_hint;
    private ImageButton ib_delete;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private Handler handler;
    private List<String> langList;
    private int currentLang;
    private static final int REFRESH_TIME=0;
    private static final int STATUS_IDLE=1;
    private static final int STATUS_RECORDING=2;
    private static final int STATUS_RECORDED=3;
    private static final int STATUS_PLAYING=4;
    private Timer timer;
    private TimerTask task;
    private int currentTime;
    private int currentState;
    private ButtonFloatSmall bt_main;
    private static final String[] langs={"粤语","赣语","客家语","晋语","汉语","闽东语","闽南语","吴语","湘语"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);

        findViews();

        Intent intent=getIntent();
        et_new_word.setText(intent.getStringExtra("word"));
        et_new_word.setSelection(intent.getStringExtra("word").length());

        currentTime=0;
        currentState=STATUS_IDLE;
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case REFRESH_TIME:
                        currentTime=currentTime+1;
                        tv_time.setText(currentTime + "秒");
                }
                return true;
            }
        });
        bt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentState){
                    case STATUS_IDLE:
                        currentState=STATUS_RECORDING;
                        startRecord();
                        break;
                    case STATUS_RECORDING:
                        currentState=STATUS_RECORDED;
                        stopRecord();
                        break;
                    case STATUS_RECORDED:
                        currentState=STATUS_PLAYING;
                        startPlay();
                        break;
                    case STATUS_PLAYING:
                        currentState=STATUS_RECORDED;
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
    private void findViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_new_word);
        toolbar.setTitle("新词条");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        et_new_word = (MaterialEditText) findViewById(R.id.et_new_word);
        spinner = (ButtonFlat) findViewById(R.id.spinner_new_word_lang);
            setupSpinner();
        checkBox = (CheckBox) findViewById(R.id.cb_anonymous);
        tv_time = (TextView) findViewById(R.id.tv_time);
            tv_time.setVisibility(View.GONE);
        tv_hint = (TextView) findViewById(R.id.tv_record_hint);
        ib_delete = (ImageButton) findViewById(R.id.ib_delete);
            ib_delete.setVisibility(View.GONE);
        bt_main = (ButtonFloatSmall) findViewById(R.id.bt_record);
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_microphone));
        bt_main.setRippleSpeed(20f);

    }

    /**
     * 初始化语言选择栏
     */
    private void setupSpinner(){
//        langList = new ArrayList<>();
////        langList.add("全部方言");
//        langList.add("粤语");
//        langList.add("赣语");
//        langList.add("客家语");
//        langList.add("晋语");
//        langList.add("汉语");
//        langList.add("闽东语");
//        langList.add("闽南语");
//        langList.add("吴语");
//        langList.add("湘语");
//        MySpinnerAdapter myAdapter=new MySpinnerAdapter();
//        spinner.setAdapter(myAdapter);
//        spinner.setPrompt("promote");
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                currentLang = position;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(NewWordActivity.this);
                builder.setSingleChoiceItems(langs, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinner.setText(langs[which]);
                        currentLang=which;
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });





    }
    /**
     * 开始录音
     */
    private  void startRecord(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/newRecord.3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        }catch (Exception e){
            Log.e("mRecorder prepare error",e.toString());
        }
        mRecorder.start();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=REFRESH_TIME;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000);

        tv_time.setVisibility(View.VISIBLE);
        tv_hint.setText("正在录音，点击按钮停止");
    }

    /**
     * 停止录音
     */
    private void stopRecord(){
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_play));
        ib_delete.setVisibility(View.VISIBLE);
        if(mRecorder!=null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder=null;
        }
        timer.cancel();
        timer=null;
        currentTime=0;

        tv_hint.setText("录音完成，点击按钮试听录音");
    }

    /**
     * 播放录音
     */
    private  void startPlay(){
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_pause));
        mPlayer = MediaPlayer.create(this, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/newRecord.3gp"));
//            mPlayer.prepare();
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentState = STATUS_RECORDED;
                bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_play));
            }
        });

        tv_hint.setText("");
    }
    /**
     * 停止播放录音
     */
    private void stopPlay(){
        bt_main.setDrawableIcon(getResources().getDrawable(R.drawable.iconfont_play));
        if(mPlayer!=null){
            mPlayer.release();
            mPlayer=null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.it_send_new_word:
                sendNewWord();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 保存新添加词语至Cloud
     */
    private void sendNewWord(){
        AVUser user=AVUser.getCurrentUser();

        if(currentState==STATUS_IDLE||currentState==STATUS_RECORDING){
            final Dialog dialog=new Dialog(this,"错误","你好像好没有录制发音哦");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else if(user==null&&(!checkBox.isCheck())){
                final Dialog dialog=new Dialog(this,"未登录","  你还没有登录，只能匿名发布新的发音");
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

            AVObject newWord=new AVObject("word");
            newWord.put("name",et_new_word.getText().toString());
            newWord.put("lang", currentLang + 1 + "");
            if(AVUser.getCurrentUser()!=null){
                newWord.put("provider",AVUser.getCurrentUser());
            }

            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/newRecord.3gp");
            try {
                AVFile avFile=AVFile.withFile(et_new_word.getText().toString()+".3gp",file);
                avFile.saveInBackground();
                newWord.put("voiceFile", avFile);
                newWord.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
//                    返回WordActivity
                        Intent intent=new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                        progressDialog.dismiss();
                    }
                });
            } catch (IOException e) {
                Log.e("send newWord error",e.toString());
            }
        }
    }


    class MySpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return langList.size();
        }

        @Override
        public Object getItem(int position) {
            return langList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=getLayoutInflater().inflate(R.layout.spinner_item,null);
            TextView tv_spinner_item= (TextView) v.findViewById(R.id.tv_spinner_item);
            tv_spinner_item.setText(langList.get(position));
            return v;
        }
    }

}
