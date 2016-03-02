package com.trojx.regularpractice.recyclerviewtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.trojx.regularpractice.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/22.
 */
public class RecyclerViewActivity extends AppCompatActivity {

    private ArrayList<String> data;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_recycler);

        data = new ArrayList<>();
        for (int i='A';i<'z';i++){
            data.add("" + (char) i);
        }

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycle_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(RecyclerViewActivity.this,data.get(position)+"clicked",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(RecyclerViewActivity.this,data.get(position)+"long clicked",Toast.LENGTH_SHORT).show();
            }
        });
//        recyclerView.addItemDecoration(RecyclerView.ItemDecoration);

    }
    public interface OnItemClickListener
    {
        void onItemClick(View view,int position);
        void onItemLongClick(View view ,int position);
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private OnItemClickListener mOnItemClickListener;
        public  void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
            this.mOnItemClickListener=mOnItemClickListener;
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(RecyclerViewActivity.this).inflate(R.layout.recycler_item,parent,false);
            MyViewHolder holder=new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.tv.setHeight(150*(1+position%4));
            holder.tv.setWidth(150*(1+position%4));
            holder.tv.setText(data.get(position));
            if(mOnItemClickListener!=null){
                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=holder.getLayoutPosition();//此pos与position不同，由于添加、删除元素，pos不同于初始创建view时的位置
                        mOnItemClickListener.onItemClick(v,pos);
                    }
                });
                holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos=holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(v,pos);
                        return true;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView tv;
            public MyViewHolder(View itemView) {
                super(itemView);
                tv= (TextView) itemView.findViewById(R.id.tv_recycler_item);
            }
        }

        public void addData(int position){
            data.add(position,"InsertData");
            notifyItemInserted(position);
        }

        public void removeData(int position){
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recycler_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.it_add:
                myAdapter.addData(5);
                break;
            case R.id.it_remove:
                myAdapter.removeData(5);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
