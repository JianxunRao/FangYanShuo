package com.trojx.fangyan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.trojx.fangyan.R;
import com.trojx.fangyan.activity.JxhActivity;

import java.util.LinkedList;
import java.util.List;

/**家乡话Fragment
 * Created by Trojx on 2016/3/20.
 */
public class JxhFragment extends Fragment{

    private LinkedList<AVObject> mList;
    private JxhAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new LinkedList<>();
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_jxh,container,false);//false很关键！！！！
        RecyclerView recyclerView= (RecyclerView) v.findViewById(R.id.rv_jxh);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        adapter = new JxhAdapter();
        adapter.setOnclickListener(new MyOnclickListener() {
            @Override
            public void onClick(int index) {
//                Toast.makeText(getActivity(), index+"clicked",Toast.LENGTH_SHORT).show();
//                Intent
                Intent intent=new Intent(getActivity(), JxhActivity.class);
                intent.putExtra("item",mList.get(index));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);



        return v;
    }
    interface MyOnclickListener{
        void onClick(int index);
    }
    class JxhAdapter extends RecyclerView.Adapter<JxhAdapter.MyViewHolder>{
        private MyOnclickListener listener;

        public void setOnclickListener(MyOnclickListener listener){
            this.listener=listener;
        }

        @Override
        public JxhAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            MyViewHolder holder=new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.jxh_item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final JxhAdapter.MyViewHolder holder, int position) {
            AVObject item=mList.get(position);

            if(listener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=holder.getLayoutPosition();
                        listener.onClick(pos);
                    }
                });
            }

            String title=item.getString("title");
            holder.tv.setText(title);

            AVFile coverPic=item.getAVFile("cover");


            if (coverPic!=null){
                Glide.with(JxhFragment.this).load(coverPic.getUrl()).crossFade().into(holder.iv);
            }

            if(item.getBoolean("isVoice")){
                Glide.with(JxhFragment.this).load(R.drawable.iconfont_audio).into(holder.iv_tag);
            }else {
                Glide.with(JxhFragment.this).load(R.drawable.iconfont_video).into(holder.iv_tag);
            }

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder{


            ImageView iv;
            TextView tv;
            ImageView iv_tag;
            public MyViewHolder(View itemView) {
                super(itemView);
                iv= (ImageView) itemView.findViewById(R.id.iv_jxh_item);
                tv= (TextView) itemView.findViewById(R.id.tv_jxh_item);
                iv_tag= (ImageView) itemView.findViewById(R.id.iv_jxh_item_isVoice);
            }
        }
    }

//    /**
//     * 获取音频资源
//     */
//    private void getVoice(){
//        AVQuery<AVObject> query=new AVQuery<>("jxh");
//        query.whereEqualTo("isVoice",true);
//        query.setLimit(1000);
//        query.include("coverPic");
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if(e==null){
//                    voiceList= (ArrayList<AVObject>) list;
//                }else {
//                    Log.e("getVoice",e.toString());
//                }
//            }
//        });
//    }

    /**
     * 获取音/视频资源
     */
    private void getData(){
        AVQuery<AVObject> query=new AVQuery<>("jxh");
//        query.whereEqualTo("isVoice",false);
        query.setLimit(1000);
        query.orderByDescending("viewcount");
        query.include("cover");
        query.whereExists("cover");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    mList= (LinkedList<AVObject>) list;
                    if(adapter!=null)
                        adapter.notifyDataSetChanged();
                }else {
                    Log.e("getVoice",e.toString());
                }
            }
        });
    }
}
