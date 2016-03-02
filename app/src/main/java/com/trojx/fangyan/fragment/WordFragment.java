package com.trojx.fangyan.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.trojx.fangyan.R;
import com.trojx.fangyan.activity.WordActivity;
import com.trojx.fangyan.activity.WordListActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
public class WordFragment extends Fragment {

    private int[] langCount;
    private ArrayList<AVObject> newWords;
    private LangCountAdapter langCountAdapter;
    private static String[] langName={"粤语","赣语","客家","晋语","汉语","闽东","闽南","吴语","湘语"};
    private NewWordAdapter newWordAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Typeface typeface;//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        langCount = new int[9];
        for(int lang=1;lang<10;lang++){
            getLangCount(lang);
        }
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "hwxk.ttf");
        getNewWords();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_word,container,false);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.srl);
        GridView gv_lang_count= (GridView) v.findViewById(R.id.gv_lang_count);
        GridView gv_new_word= (GridView) v.findViewById(R.id.gv_new_word);
        final EditText et_search= (EditText) v.findViewById(R.id.et_word_search);
        ImageButton ib_search= (ImageButton) v.findViewById(R.id.ib_word_search);
        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key=et_search.getText().toString();
                if(!key.equals("")){
                    Intent intent=new Intent(getActivity(), WordActivity.class);
                    intent.putExtra("word",key);
                    startActivity(intent);
                }
            }
        });

        langCountAdapter = new LangCountAdapter();
        gv_lang_count.setAdapter(langCountAdapter);
        setGridViewHeightBasedOnChildren(gv_lang_count);

        newWordAdapter = new NewWordAdapter();
        gv_new_word.setAdapter(newWordAdapter);
        setGridViewHeightBasedOnChildren(gv_new_word);

        gv_new_word.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = newWords.get(position).getString("name");
                Intent intent = new Intent(getActivity(), WordActivity.class);
                intent.putExtra("word", key);
                startActivity(intent);
            }
        });

        gv_lang_count.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int lang=position+1;
                Intent intent=new Intent(getActivity(), WordListActivity.class);
                intent.putExtra("lang",lang);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for (int lang = 1; lang < 10; lang++) {
                    getLangCount(lang);
                }
                getNewWords();
            }
        });
        return v;
    }

    /**
     * 获取各方言词条数
     * @param lang
     */
    private void getLangCount(final int lang){
        AVQuery query=new AVQuery("word");
        query.whereEqualTo("lang", lang+"");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if(e==null){
                    langCount[lang - 1] = i;
                    langCountAdapter.notifyDataSetChanged();
                }else {
                 Log.e("getLangCountError",e.toString());
                }
            }
        });
    }

    /**
     * 获取最新词条
     */
    private void getNewWords(){
        newWords = new ArrayList<>();
        AVQuery<AVObject> query=new AVQuery<>("word");
        query.setLimit(21);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    HashSet<String> set=new HashSet<>();//去重
                    for (AVObject item : list) {
                        if(!set.contains(item.getString("name"))){
                            newWords.add(item);
                            set.add(item.getString("name"));
                        }
                    }
                    newWordAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {
                    Log.e("get new words error",e.toString());
                }
            }
        });
    }
    class LangCountAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=getLayoutInflater(null).inflate(R.layout.lang_count_item,null);
            TextView tv_name= (TextView) v.findViewById(R.id.item_lang_count_name);
            TextView tv_count= (TextView) v.findViewById(R.id.item_lang_count);
            tv_name.setText(langName[position]);
            tv_count.setText(langCount[position] + "");
            tv_name.setTypeface(typeface);
            return v;
        }
    }
    class NewWordAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=getLayoutInflater(null).inflate(R.layout.new_word_item,null);
            TextView tv_newword= (TextView) v.findViewById(R.id.tv_item_new_word);
            if(newWords.size()!=0)
                tv_newword.setText(newWords.get(position).getString("name"));
            return v;
        }
    }
    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        // 鑾峰彇GridView瀵瑰簲鐨凙dapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns=0;
        int horizontalBorderHeight=0;
        Class<?> clazz=gridView.getClass();
        try {
            Field column=clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns=(Integer)column.get(gridView);
            Field horizontalSpacing=clazz.getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight=(Integer)horizontalSpacing.get(gridView);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        if(listAdapter.getCount()%columns>0){
            rows=listAdapter.getCount()/columns+1;
        }else {
            rows=listAdapter.getCount()/columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { //鍙绠楁瘡椤归珮搴?琛屾暟
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 璁＄畻瀛愰」View 鐨勫楂?
            totalHeight += listItem.getMeasuredHeight(); // 缁熻鎵€鏈夊瓙椤圭殑鎬婚珮搴?
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight+horizontalBorderHeight*(rows-1);//鏈€鍚庡姞涓婂垎鍓茬嚎鎬婚珮搴?
        gridView.setLayoutParams(params);
    }

}
