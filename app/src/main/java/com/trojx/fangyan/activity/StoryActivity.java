package com.trojx.fangyan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFlat;
import com.trojx.fangyan.R;
import com.trojx.fangyan.Util.RelativeDateFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/1/23.
 */
public class StoryActivity extends AppCompatActivity {

    private AVObject story;
    private ImageView civ_user_logo,iv_voice,iv_story_comment,iv_story_like;
    private TextView tv_user_name,tv_story_time,tv_story_time_long,tv_story_content
            ,tv_story_location,tv_story_comment_count,tv_story_like_count;
    private RelativeLayout rl_voice;

    private Toolbar toolbar;
    private MediaPlayer player;
    private static final int STATUS_PLAYING=1,STATUS_IDLE=0;
    private int currentState=STATUS_IDLE;
    private EditText et_comment;
    private ButtonFlat bt_submit;
    private ArrayList<AVObject> comments=new ArrayList<>();
    private ListView lv_comments;
    private MyCommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story);


        Intent intent=getIntent();
        String objectString=intent.getStringExtra("AVObject");
        try {
            story = AVObject.parseAVObject(objectString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViews();

        getComments();







    }
    private void findViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar_story);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle("评论");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white));
        ab.setTitle("评论");

        civ_user_logo= (ImageView) findViewById(R.id.iv_user_logo);
        tv_user_name= (TextView) findViewById(R.id.tv_user_name);
        tv_story_time= (TextView) findViewById(R.id.tv_story_time);
        rl_voice= (RelativeLayout) findViewById(R.id.rl_voice);
        iv_voice= (ImageView) findViewById(R.id.iv_voice);
        tv_story_time_long= (TextView) findViewById(R.id.tv_story_time_long);
        tv_story_content= (TextView) findViewById(R.id.tv_story_content);
        tv_story_location= (TextView) findViewById(R.id.tv_story_location);
        iv_story_like= (ImageView) findViewById(R.id.iv_story_like);
        tv_story_like_count= (TextView) findViewById(R.id.tv_story_like_count);
        iv_story_comment= (ImageView) findViewById(R.id.iv_story_comment);
        tv_story_comment_count= (TextView) findViewById(R.id.tv_story_comment_count);

        lv_comments = (ListView) findViewById(R.id.lv_story_comment);
        et_comment = (EditText) findViewById(R.id.et_story_comment);
        bt_submit = (ButtonFlat) findViewById(R.id.bt_submit_comment);

        adapter = new MyCommentAdapter();
        lv_comments.setAdapter(adapter);

        final AVUser avUser=story.getAVUser("provider");

//        avUser.fetchInBackground("logo", new GetCallback<AVObject>() {
//            @Override
//            public void done(AVObject avObject, AVException e) {
//                if(avUser.getUsername().length()>6){
//                    tv_user_name.setText(avUser.getUsername().substring(0,avUser.getUsername().length()/2));
//                }else {
//                    tv_user_name.setText(avUser.getUsername());
//                }
//                AVFile logoFile = avUser.getAVFile("logo");
//                if(!StoryActivity.this.isDestroyed()){
////                    Glide.with(StoryActivity.this).load(logoFile.getUrl()).crossFade().into(civ_user_logo); //You cannot start a load for a destroyed activity
//                    if(logoFile!=null){
//                        Glide.with(StoryActivity.this).load(logoFile.getUrl()).crossFade().into(civ_user_logo);
//                    }else {
//                        Glide.with(StoryActivity.this).load(R.drawable.logo).crossFade().into(civ_user_logo);
//                    }
//                }
//            }
//        });
        AVFile logoFile=avUser.getAVFile("logo");
        if (logoFile!=null){
            Glide.with(this).load(logoFile.getUrl()).crossFade().into(civ_user_logo);
        }else {
            Glide.with(this).load(R.drawable.logo).crossFade().into(civ_user_logo);
        }
        String userName=avUser.getUsername();
        if(userName.length()>6){
            tv_user_name.setText(userName.substring(0, userName.length() / 2));
        }else {
            tv_user_name.setText(userName);
        }

        int timelong=story.getInt("timelong");
        if(timelong<60000){
            tv_story_time_long.setText(timelong/1000+"秒");
        }else {
            tv_story_time_long.setText(timelong/60000+"分钟");
        }
        tv_story_location.setText(story.getString("location"));
        tv_story_like_count.setText(story.getInt("like") + "");
        tv_story_comment_count.setText(story.getInt("commentcount") + "");
        tv_story_content.setText(story.getString("content"));
        Date date=story.getDate("createdAt");
//        Date now=new Date();
//        String s=DateUtils.getRelativeTimeSpanString(date.getTime(), now.getTime(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        String s=DateUtils.getRelativeTimeSpanString(StoryActivity.this,date.getTime()).toString();

        tv_story_time.setText(s);

        rl_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_voice.setBackgroundResource(R.drawable.speaker_animation);
                AnimationDrawable ad = (AnimationDrawable) iv_voice.getBackground();
                ad.start();
                playRecord(story, ad);
            }
        });
        iv_story_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                story.increment("like");
                story.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e!=null)
                            Log.e("save like error",e.toString());
                    }
                });
                if (tv_story_like_count.getText().toString().equals("赞")) {
                    tv_story_like_count.setText(1 + "");
                } else {
                    int count = Integer.parseInt(tv_story_like_count.getText().toString()) + 1;
                    tv_story_like_count.setText(count + "");
                }
            }
        });
        iv_story_like.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iv_story_like.setImageResource(R.drawable.iconfont_thumb);
                        break;
                    case MotionEvent.ACTION_UP:
                        iv_story_like.setImageResource(R.drawable.iconfont_thumb2);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        iv_story_like.setImageResource(R.drawable.iconfont_thumb2);
                        break;
                }
                return false;
            }
        });
        iv_story_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("action",event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iv_story_comment.setImageResource(R.drawable.iconfont_comment);
                        break;
                    case MotionEvent.ACTION_UP:
                        iv_story_comment.setImageResource(R.drawable.iconfont_comment2);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        iv_story_comment.setImageResource(R.drawable.iconfont_comment2);
                        break;
                }
                return false;
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=et_comment.getText().toString();
                if(content.isEmpty()){
                    Toast.makeText(StoryActivity.this,"评论不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    AVObject comment=new AVObject("storycomment");
                    if(AVUser.getCurrentUser()!=null)
                        comment.put("user",AVUser.getCurrentUser());
                    comment.put("story",story);
                    comment.put("content",content);
                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            et_comment.setText("");
                            getComments();

                        }
                    });
                }
            }
        });
    }
    private void playRecord(AVObject item, final AnimationDrawable ad){
        if(currentState==STATUS_IDLE){
            currentState=STATUS_PLAYING;
        }else {
            player.stop();
            player.release();
        }
        AVFile avFile=item.getAVFile("voiceFile");
        avFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if(e==null){
                    try{
                        FileOutputStream fos=openFileOutput("story.3gp", Context.MODE_PRIVATE);
                        fos.write(bytes);
                        fos.flush();
                        fos.close();
                        player = MediaPlayer.create(StoryActivity.this, Uri.parse(getFilesDir().getAbsolutePath() + "/story.3gp"));
                        player.start();
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                                currentState = STATUS_IDLE;
                                ad.stop();
                            }
                        });
                    }catch (IOException e1){
                        Log.e("open story file error", e.toString());
                    }
                }else {
                    Log.e("get storyFile error",e.toString());
                }
            }
        });
    }
    private void getComments(){
        if(comments==null){
            comments = new ArrayList<>();
        }else {
            comments.clear();
        }
        AVQuery<AVObject> query=new AVQuery<>("storycomment");
        query.whereEqualTo("story",story);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    for(AVObject item:list){
                        comments.add(item);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("get story comm err",e.toString());
                }
            }
        });
    }
    class  MyCommentAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            final ViewHolder viewHolder;
            final AVObject item=comments.get(position);
            if(convertView==null){
                v= LayoutInflater.from(StoryActivity.this).inflate(R.layout.story_comment_item,null);
                viewHolder=new ViewHolder();
                viewHolder.iv_user= (ImageView) v.findViewById(R.id.iv_story_comment_user);
                viewHolder.tv_user= (TextView) v.findViewById(R.id.tv_story_comment_user);
                viewHolder.tv_time= (TextView) v.findViewById(R.id.tv_story_comment_time);
                viewHolder.tv_content= (TextView) v.findViewById(R.id.tv_story_comment_content);

                v.setTag(viewHolder);

            }else {
                v=convertView;
                viewHolder= (ViewHolder) v.getTag();
            }


            final AVUser avUser=item.getAVUser("user");
            if(avUser!=null){
                avUser.fetchInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        AVFile avFile = avUser.getAVFile("logo");
                        Glide.with(StoryActivity.this).load(avFile.getUrl()).crossFade().into(viewHolder.iv_user);
                        viewHolder.tv_user.setText(avUser.getUsername());
                    }
                });
            }else {
                viewHolder.tv_user.setText("游客");
            }


            Date date=item.getDate("updatedAt");
//            String s=DateUtils.getRelativeTimeSpanString(StoryActivity.this, date.getTime()).toString();
            viewHolder.tv_time.setText(RelativeDateFormat.format(date));

            viewHolder.tv_content.setText(item.getString("content"));

            return v;
        }
        class ViewHolder{
            ImageView iv_user;
            TextView tv_user,tv_time,tv_content;
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(player!=null){
//            player.stop();
            player.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
