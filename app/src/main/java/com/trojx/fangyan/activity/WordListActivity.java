package com.trojx.fangyan.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.fangyan.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**显示某一语种的全部词语，实现listview刷新与加载更多
 * Created by Trojx on 2016/2/29.
 */
public class WordListActivity extends AppCompatActivity {

    private int currentLang;//当前语言
    private int currentSkip=0;//当前的skip
    private ArrayList<AVObject> wordList=new ArrayList<>();
    private MyAdapter adapter;
    private static final String[] langs={"全部","粤语","赣语","客家语","晋语","汉语","闽东语","闽南语","吴语","湘语"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        Intent intent=getIntent();
        currentLang=intent.getIntExtra("lang",1);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_word_list);
        toolbar.setTitle(langs[currentLang]);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("navi", "press");
                onBackPressed();//没有用
            }
        });
        setSupportActionBar(toolbar);
        Toast.makeText(this,"长按词语直接发音哦~",Toast.LENGTH_LONG).show();



        RecyclerView rv_word= (RecyclerView) findViewById(R.id.rv_word_list);
        rv_word.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        adapter = new MyAdapter();
        rv_word.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AVObject item=wordList.get(position);
                String name=item.getString("name");
                Intent intent=new Intent(WordListActivity.this,WordActivity.class);
                intent.putExtra("word",name);
                intent.putExtra("lang",currentLang);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                downloadVoice(wordList.get(position).getAVFile("voiceFile"));
            }
        });
        rv_word.setItemAnimator(new DefaultItemAnimator());
        getSomeWord();

    }

    /**
     * 获取随机的word
     */
    private void getSomeWord(){
        if(wordList!=null){
            wordList.clear();
        }
        AVQuery<AVObject> query=new AVQuery<>("word");
        query.setLimit(51);
        query.setSkip(currentSkip);
        currentSkip+=51;
        query.whereEqualTo("lang", currentLang+"");
        query.include("voiceFile");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    if(list.size()>0){
                        for(AVObject item:list){
                            wordList.add(item);
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        currentSkip=0;//如果达到了最后一组之后，则重新回到第一组
                        getSomeWord();
                    }
                }else {
                    Log.e("获取词条失败",e.toString());
                }
            }
        });
    }
    public  interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            mOnItemClickListener=onItemClickListener;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder=new MyViewHolder(LayoutInflater.from(WordListActivity.this).inflate(R.layout.word_list_item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.MyViewHolder holder, int position) {
            holder.textView.setText(wordList.get(position).getString("name"));
            if(mOnItemClickListener!=null){
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(v,pos);
                    }
                });
                holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //放大？
                        int pos=holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(v,pos);
                        return true;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return wordList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView textView;
            public MyViewHolder(View itemView) {
                super(itemView);
                textView= (TextView) itemView.findViewById(R.id.tv_word_list_item_name);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.it_get_some_words:
                getSomeWord();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 下载指定word的音频
     * @param file 音频文件
     */
    private void downloadVoice(AVFile file){
        final ProgressDialog progressDialog=new ProgressDialog(this,"获取音频中");
        progressDialog.show();
        if(file!=null){
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    if(e==null){
                        progressDialog.dismiss();
                        play(bytes);
                    }else {
                        Log.e("get voice file error",e.toString());
                    }
                }
            });
        }
    }

    /**
     * 播放音频
     * @param bytes 音频文件的字节数组
     */
    private void play(byte[] bytes){
        File file=new File("sdcard/word.fys");
        try {
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length - 1);
            fos.close();
            MediaPlayer mediaPlayer=MediaPlayer.create(this, Uri.parse("sdcard/word.fys"));
//            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
