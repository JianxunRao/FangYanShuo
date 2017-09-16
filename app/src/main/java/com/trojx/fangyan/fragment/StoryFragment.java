package com.trojx.fangyan.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.avos.avoscloud.GetDataCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFloat;
import com.trojx.fangyan.R;
import com.trojx.fangyan.Util.RelativeDateFormat;
import com.trojx.fangyan.activity.StoryActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/1/18.
 */
public class StoryFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private ListView lv_story;
    private ButtonFloat fab;
    public ArrayList<AVObject> storyList;
    private MediaPlayer player;
    private static final int STATUS_PLAYING=1,STATUS_IDLE=0;
    private int currentState=STATUS_IDLE;
    private MyStoryAdapter adapter;
    public static final int REQUEST_NEW_STORY=1;
    public int skip=0;//获取更多故事时的跳过条目数

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_story,container,false);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.srl_story);
        lv_story = (ListView) v.findViewById(R.id.lv_story);
//        fab = (ButtonFloat) v.findViewById(R.id.fab_new_story);
//        fab.setDrawableIcon(getResources().getDrawable(R.drawable.ic_add_white_48dp));
        adapter = new MyStoryAdapter();
        lv_story.setAdapter(adapter);
        lv_story.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (player != null) {
//                    player.stop();
                    player.release();
                    currentState = STATUS_IDLE;
                }
                Intent intent = new Intent(getActivity(), StoryActivity.class);
                String AVObject = storyList.get(position).toString();
                intent.putExtra("AVObject", AVObject);
                startActivity(intent);
            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (player != null && player.isPlaying()) {
//                    player.stop();
//                    player.release();
//                    currentState = STATUS_IDLE;
//                }
//                Intent intent = new Intent(getActivity(), NewStoryActivity.class);
//                startActivityForResult(intent, REQUEST_NEW_STORY);
//            }
//        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                skip = 0;
                storyList.clear();
                getStory();
            }
        });

        lv_story.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean hasAppend;
            private int state;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                state=scrollState;
                if(scrollState==SCROLL_STATE_TOUCH_SCROLL||scrollState==SCROLL_STATE_FLING)
                    hasAppend=false;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem==totalItemCount-visibleItemCount&&!hasAppend){
                    getStory();
                    hasAppend=true;
                }
            }
        });


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storyList = new ArrayList<>();
        getStory();

    }

    /**
     * 获取故事列表
     */
    public void getStory(){
        AVQuery<AVObject> query=new AVQuery<>("story");

        query.setSkip(skip);
        skip=skip+30;

        query.setLimit(30);
        query.orderByDescending("createdAt");
        query.include("provider");//包括provider 避免重复查询
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject item : list) {
                        storyList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {
                    Log.e("get story list error", e.toString());
                }
            }
        });


    }

    class MyStoryAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return storyList.size();
        }

        @Override
        public Object getItem(int position) {
            return storyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            final ViewHolder viewHolder;
            final AVObject item= (AVObject) getItem(position);
            if(convertView==null){
                v=LayoutInflater.from(getActivity()).inflate(R.layout.story_item,null);
                viewHolder=new ViewHolder();
                viewHolder.civ_user_logo= (CircleImageView) v.findViewById(R.id.iv_user_logo);
                viewHolder.tv_user_name= (TextView) v.findViewById(R.id.tv_user_name);
                viewHolder.tv_story_time= (TextView) v.findViewById(R.id.tv_story_time);
                viewHolder.rl_voice= (RelativeLayout) v.findViewById(R.id.rl_voice);
                viewHolder.iv_voice= (ImageView) v.findViewById(R.id.iv_voice);
                viewHolder.tv_story_time_long= (TextView) v.findViewById(R.id.tv_story_time_long);
                viewHolder.tv_story_content= (TextView) v.findViewById(R.id.tv_story_content);
                viewHolder.tv_story_location= (TextView) v.findViewById(R.id.tv_story_location);
                viewHolder.iv_story_like= (ImageView) v.findViewById(R.id.iv_story_like);
                viewHolder.tv_story_like_count= (TextView) v.findViewById(R.id.tv_story_like_count);
                viewHolder.iv_story_comment= (ImageView) v.findViewById(R.id.iv_story_comment);
                viewHolder.tv_story_comment_count= (TextView) v.findViewById(R.id.tv_story_comment_count);

                v.setTag(viewHolder);
            }else {
                v=convertView;
                viewHolder= (ViewHolder) v.getTag();
            }

            final AVUser avUser=item.getAVUser("provider");

//            avUser.fetchInBackground(new GetCallback<AVObject>() {
//                @Override
//                public void done(AVObject avObject, AVException e) {
//                    if(e==null){
//                        AVFile logoFile = avUser.getAVFile("logo");
//                        if(logoFile!=null){
//                            Glide.with(getActivity()).load(logoFile.getUrl()).crossFade().into(viewHolder.civ_user_logo);
//                        }else {
//                            Glide.with(getActivity()).load(R.drawable.logo).crossFade().into(viewHolder.civ_user_logo);
//                        }
//                        if(avUser.getUsername().length()>6){
//                            viewHolder.tv_user_name.setText(avUser.getUsername().substring(0,avUser.getUsername().length()/2));
//                        }else {
//                            viewHolder.tv_user_name.setText(avUser.getUsername());
//                        }
//                    }else {
//                        Log.e("get user logo error",e.toString());
//                    }
//                }
//            });
            AVFile logoFile=avUser.getAVFile("logo");
            if(logoFile!=null){
                Glide.with(getActivity()).load(logoFile.getUrl()).crossFade().into(viewHolder.civ_user_logo);
            }else {
                Glide.with(getActivity()).load(R.drawable.logo).crossFade().into(viewHolder.civ_user_logo);
            }
            String userName=avUser.getUsername();
            viewHolder.tv_user_name.setText(userName);
            int timelong=item.getInt("timelong");
            if(timelong<60000){
                viewHolder.tv_story_time_long.setText(timelong/1000+ "秒");
            }else {
                viewHolder.tv_story_time_long.setText(timelong/60000+ "分钟");
            }
            if (item.has("location")){
                viewHolder.tv_story_location.setText(item.getString("location"));
            }else {
                viewHolder.tv_story_location.setText("来自 世界的某个角落");
            }
            viewHolder.tv_story_like_count.setText(item.getInt("like") + "");
            viewHolder.tv_story_comment_count.setText(item.getInt("commentcount") + "");
            viewHolder.tv_story_content.setText(item.getString("content"));
            Date date=item.getDate("createdAt");
            viewHolder.tv_story_time.setText(RelativeDateFormat.format(date));
            viewHolder.rl_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.iv_voice.setBackgroundResource(R.drawable.speaker_animation);
                    AnimationDrawable ad= (AnimationDrawable) viewHolder.iv_voice.getBackground();
                    ad.start();
                    playRecord(item,ad);
                }
            });
            viewHolder.iv_story_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.increment("like");
                    item.saveInBackground();
                    if (viewHolder.tv_story_like_count.getText().toString().equals("赞")) {
                        viewHolder.tv_story_like_count.setText(1 + "");
                    } else {
                        int count = Integer.parseInt(viewHolder.tv_story_like_count.getText().toString()) + 1;
                        viewHolder.tv_story_like_count.setText(count + "");
                    }
                }
            });
            viewHolder.iv_story_like.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            viewHolder.iv_story_like.setImageResource(R.drawable.iconfont_thumb);
                            break;
                        case MotionEvent.ACTION_UP:
                            viewHolder.iv_story_like.setImageResource(R.drawable.iconfont_thumb2);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            viewHolder.iv_story_like.setImageResource(R.drawable.iconfont_thumb2);
                            break;
                    }
                    return false;
                }
            });
            viewHolder.iv_story_comment.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            viewHolder.iv_story_comment.setImageResource(R.drawable.iconfont_comment);
                            break;
                        case MotionEvent.ACTION_UP:
                            viewHolder.iv_story_comment.setImageResource(R.drawable.iconfont_comment2);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            viewHolder.iv_story_comment.setImageResource(R.drawable.iconfont_comment2);
                            break;
                    }
                    return false;
                }
            });
            viewHolder.iv_story_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //转到StoryActivity
                    //...
                    if(player!=null&&player.isPlaying()){
                        player.stop();
                        player.release();
                        currentState=STATUS_IDLE;
                    }
                    Intent intent=new Intent(getActivity(), StoryActivity.class);
                    String AVObject=item.toString();
                    intent.putExtra("AVObject",AVObject);
                    startActivity(intent);
                }
            });

            return v;
        }
        class ViewHolder{
            ImageView iv_voice,iv_story_comment,iv_story_like;
            TextView tv_user_name,tv_story_time,tv_story_time_long,tv_story_content
                    ,tv_story_location,tv_story_comment_count,tv_story_like_count;
            RelativeLayout rl_voice;
            CircleImageView civ_user_logo;

        }
    }

    private void playRecord(AVObject item, final AnimationDrawable ad){
        if(currentState==STATUS_IDLE){
            currentState=STATUS_PLAYING;
        }else {
            try{
                if(player!=null&&player.isPlaying())
                    player.stop();
            }catch (IllegalStateException e){
                Log.e("player error",e.toString());
            }
            if(player!=null){
                player.release();
            }
        }
        AVFile avFile=item.getAVFile("voiceFile");
        avFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if (e == null) {
                    try {
                        FileOutputStream fos = getActivity().openFileOutput("story.3gp", Context.MODE_PRIVATE);
                        fos.write(bytes);
                        fos.flush();
                        fos.close();
                        try{
                            if(player!=null&&player.isPlaying())
                                player.stop();
                        }catch (IllegalStateException e0){
                            Log.e("player error",e0.toString());
                        }
                        if(player!=null){
                            player.release();
                        }
                        player = MediaPlayer.create(getActivity(), Uri.parse(getActivity().getFilesDir().getAbsolutePath() + "/story.3gp"));
                        if (player!=null){
                            player.start();
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.stop();
                                    mp.reset();
                                    currentState = STATUS_IDLE;
                                    ad.stop();
                                }
                            });
                        }else {
                            Toast.makeText(getActivity(), "播放失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e1) {
                        Log.e("open story file error", "");
                    }
                } else {
                    Log.e("get storyFile error", e.toString());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player!=null){
            if (player.isPlaying()){
                player.stop();
            }
            player.release();
        }
    }
}
