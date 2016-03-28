package com.trojx.fangyan.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.trojx.fangyan.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Administrator on 2016/1/18.
 */
public class WordActivity extends AppCompatActivity {

    public static final int LANG_ALL=0;//所有语言
    public static final int LANG_YUE=1;//粵文
    public static final int LANG_GAN=2;//赣语
    public static final int LANG_HAK=3;//客家语
    public static final int LANG_CJY=4;//晋语
    public static final int LANG_ZH=5;//汉语
    public static final int LANG_CDO=6;//闽东语
    public static final int LANG_NAN=7;//闽南语
    public static final int LANG_WUU=8;//吳语
    public static final int LANG_HSN=9;//湘语
    private static final String[] langs={"全部","粤语","赣语","客家语","晋语","汉语","闽东语","闽南语","吴语","湘语"};

    private ButtonFlat spinner;
    private AutoCompleteTextView tv_search;
    private ImageButton ib_search;
    private LinearLayout ll_search_result;
    private TextView tv_word;
    private ImageButton ib_fav;
    private TextView tv_search_count;
    private CardView cv_word_yue;
    private CardView cv_word_gan;
    private CardView cv_word_hak;
    private CardView cv_word_cjy;
    private CardView cv_word_zh;
    private CardView cv_word_cdo;
    private CardView cv_word_nan;
    private CardView cv_word_wuu;
    private CardView cv_word_hsn;
    private ImageButton ib_new_yue;
    private ImageButton ib_new_gan;
    private ImageButton ib_new_hak;
    private ImageButton ib_new_cjy;
    private ImageButton ib_new_zh;
    private ImageButton ib_new_cdo;
    private ImageButton ib_new_nan;
    private ImageButton ib_new_wuu;
    private ImageButton ib_new_hsn;
    private ListView lv_word_yue;
    private ListView lv_word_gan;
    private ListView lv_word_hak;
    private ListView lv_word_cjy;
    private ListView lv_word_zh;
    private ListView lv_word_cdo;
    private ListView lv_word_nan;
    private ListView lv_word_wuu;
    private ListView lv_word_hsn;
    private List<String> langList;
    private int currentLang=0;
    private ArrayList<String> historyList=new ArrayList<>();//搜索历史
    private ArrayList<String> relativeWordList=new ArrayList<>();//相似结果
    private ArrayList<String> autoCompleteWord=new ArrayList<>();//以上两者合并
    private SharedPreferences sp;
    private ArrayAdapter searchAdapter;
    private ArrayList<AVObject> yueWords,ganWords,hakWords,cjyWords,zhWords,cdoWords,nanWords,wuuWords,hsnWords;//各语言搜索结果List
    private MySearchResultAdapter yueAdapter;
    private MySearchResultAdapter ganAdapter;
    private MySearchResultAdapter hakAdapter;
    private MySearchResultAdapter cjyAdapter;
    private MySearchResultAdapter zhAdapter;
    private MySearchResultAdapter cdoAdapter;
    private MySearchResultAdapter nanAdapter;
    private MySearchResultAdapter wuuAdapter;
    private MySearchResultAdapter hsnAdapter;
    private ArrayList<MySearchResultAdapter> adapters=new ArrayList<>();//所有语言的adapter
    private ArrayList<ArrayList> wordsList=new ArrayList<>();//所有语言的list
    private ArrayList<ListView> listViews=new ArrayList<>();//所有ListView
    private ArrayList<CardView> cardViews=new ArrayList<>();//所有CardView
    public static final int REQUEST_NEW_WORD=1;
    private MediaPlayer player;
    private RecyclerView rv_word_relative;//显示相关词条
    private MyAdapter myAdapter;
    private RelativeLayout rl_word_relative;
    private ArrayList<String> wordsRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        findViews();
        setupSpinner();
        setupSearch();

        Intent intent=getIntent();
        String key=intent.getStringExtra("word");
        int lang=intent.getIntExtra("lang",0);//从wordlistActivity中传来
        currentLang=lang;
        spinner.setText(langs[lang]);
        tv_search.setText(key);
        search(key);



    }
    private void findViews(){
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_word);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);

        final SharedPreferences sp1=getSharedPreferences("lang_pref",MODE_PRIVATE);
        currentLang=sp1.getInt("lang_pref",0);

        spinner = (ButtonFlat) findViewById(R.id.spinner_lang);
        spinner.setText(langs[currentLang]);
        tv_search = (AutoCompleteTextView) findViewById(R.id.tv_search);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
        ll_search_result = (LinearLayout) findViewById(R.id.ll_search_result);
        tv_word = (TextView) findViewById(R.id.tv_word);
        ib_fav = (ImageButton) findViewById(R.id.ib_fav);
        tv_search_count = (TextView) findViewById(R.id.tv_search_count);

        rl_word_relative = (RelativeLayout) findViewById(R.id.rl_more_words);
        rv_word_relative = (RecyclerView) findViewById(R.id.rv_word_relative);

        cv_word_yue = (CardView) findViewById(R.id.cv_word_yue);
        cardViews.add(cv_word_yue);
        cv_word_gan = (CardView) findViewById(R.id.cv_word_gan);
        cardViews.add(cv_word_gan);
        cv_word_hak = (CardView) findViewById(R.id.cv_word_hak);
        cardViews.add(cv_word_hak);
        cv_word_cjy = (CardView) findViewById(R.id.cv_word_cjy);
        cardViews.add(cv_word_cjy);
        cv_word_zh = (CardView) findViewById(R.id.cv_word_zh);
        cardViews.add(cv_word_zh);
        cv_word_cdo = (CardView) findViewById(R.id.cv_word_cdo);
        cardViews.add(cv_word_cdo);
        cv_word_nan = (CardView) findViewById(R.id.cv_word_nan);
        cardViews.add(cv_word_nan);
        cv_word_wuu = (CardView) findViewById(R.id.cv_word_wuu);
        cardViews.add(cv_word_wuu);
        cv_word_hsn = (CardView) findViewById(R.id.cv_word_hsn);
        cardViews.add(cv_word_hsn);


//        ib_new_yue = (ImageButton) findViewById(R.id.ib_new_yue);
//        ib_new_gan = (ImageButton) findViewById(R.id.ib_new_gan);
//        ib_new_hak = (ImageButton) findViewById(R.id.ib_new_hak);
//        ib_new_cjy = (ImageButton) findViewById(R.id.ib_new_cjy);
//        ib_new_zh = (ImageButton) findViewById(R.id.ib_new_zh);
//        ib_new_cdo = (ImageButton) findViewById(R.id.ib_new_cdo);
//        ib_new_nan = (ImageButton) findViewById(R.id.ib_new_nan);
//        ib_new_wuu = (ImageButton) findViewById(R.id.ib_new_wuu);
//        ib_new_hsn = (ImageButton) findViewById(R.id.ib_new_hsn);

        lv_word_yue = (ListView) findViewById(R.id.lv_word_yue);
        listViews.add(lv_word_yue);
        lv_word_gan = (ListView) findViewById(R.id.lv_word_gan);
        listViews.add(lv_word_gan);
        lv_word_hak = (ListView) findViewById(R.id.lv_word_hak);
        listViews.add(lv_word_hak);
        lv_word_cjy = (ListView) findViewById(R.id.lv_word_cjy);
        listViews.add(lv_word_cjy);
        lv_word_zh = (ListView) findViewById(R.id.lv_word_zh);
        listViews.add(lv_word_zh);
        lv_word_cdo = (ListView) findViewById(R.id.lv_word_cdo);
        listViews.add(lv_word_cdo);
        lv_word_nan = (ListView) findViewById(R.id.lv_word_nan);
        listViews.add(lv_word_nan);
        lv_word_wuu = (ListView) findViewById(R.id.lv_word_wuu);
        listViews.add(lv_word_wuu);
        lv_word_hsn = (ListView) findViewById(R.id.lv_word_hsn);
        listViews.add(lv_word_hsn);

        if (yueWords == null)
            yueWords = new ArrayList<>();
        wordsList.add(yueWords);
        if (ganWords == null)
            ganWords = new ArrayList<>();
        wordsList.add(ganWords);
        if (hakWords == null)
            hakWords = new ArrayList<>();
        wordsList.add(hakWords);
        if (cjyWords == null)
            cjyWords = new ArrayList<>();
        wordsList.add(cjyWords);
        if (zhWords == null)
            zhWords = new ArrayList<>();
        wordsList.add(zhWords);
        if (cdoWords == null)
            cdoWords = new ArrayList<>();
        wordsList.add(cdoWords);
        if (nanWords == null)
            nanWords = new ArrayList<>();
        wordsList.add(nanWords);
        if (wuuWords == null)
            wuuWords = new ArrayList<>();
        wordsList.add(wuuWords);
        if (hsnWords == null)
            hsnWords = new ArrayList<>();
        wordsList.add(hsnWords);

        yueAdapter = new MySearchResultAdapter(yueWords);
        adapters.add(yueAdapter);
        ganAdapter = new MySearchResultAdapter(ganWords);
        adapters.add(ganAdapter);
        hakAdapter = new MySearchResultAdapter(hakWords);
        adapters.add(hakAdapter);
        cjyAdapter = new MySearchResultAdapter(cjyWords);
        adapters.add(cjyAdapter);
        zhAdapter = new MySearchResultAdapter(zhWords);
        adapters.add(zhAdapter);
        cdoAdapter = new MySearchResultAdapter(cdoWords);
        adapters.add(cdoAdapter);
        nanAdapter = new MySearchResultAdapter(nanWords);
        adapters.add(nanAdapter);
        wuuAdapter = new MySearchResultAdapter(wuuWords);
        adapters.add(wuuAdapter);
        hsnAdapter = new MySearchResultAdapter(hsnWords);
        adapters.add(hsnAdapter);

        lv_word_yue.setAdapter(yueAdapter);
        lv_word_gan.setAdapter(ganAdapter);
        lv_word_hak.setAdapter(hakAdapter);
        lv_word_cjy.setAdapter(cjyAdapter);
        lv_word_zh.setAdapter(zhAdapter);
        lv_word_cdo.setAdapter(cdoAdapter);
        lv_word_nan.setAdapter(nanAdapter);
        lv_word_wuu.setAdapter(wuuAdapter);
        lv_word_hsn.setAdapter(hsnAdapter);

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(tv_search.getText().toString());
            }
        });

        ib_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordActivity.this, NewWordActivity.class);
                intent.putExtra("word", tv_search.getText().toString());
                startActivityForResult(intent, REQUEST_NEW_WORD);
            }
        });

        ib_fav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ib_fav.setBackground(getResources().getDrawable(R.drawable.iconfont_add_new_word2));
                        break;
                    case MotionEvent.ACTION_UP:
                        ib_fav.setBackground(getResources().getDrawable(R.drawable.iconfont_add_new_word));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ib_fav.setBackground(getResources().getDrawable(R.drawable.iconfont_add_new_word));
                        break;
                }
                return false;
            }
        });

        tv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = relativeWordList.get(position);
                search(item);
            }
        });

        rv_word_relative.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        rv_word_relative.setItemAnimator(new DefaultItemAnimator());
        myAdapter = new MyAdapter();
        rv_word_relative.setAdapter(myAdapter);

    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder=new MyViewHolder(LayoutInflater.from(WordActivity.this)
            .inflate(R.layout.word_relative_item,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, final int position) {
            holder.textView.setText(wordsRelative.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wordsRelative.get(position)!=null){
                        search(wordsRelative.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
//            Log.e("auto words",wordsRelative.size()+"");
            return Math.min(wordsRelative.size(),4);//最多显示四个相关词条
        }
        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView textView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView= (TextView) itemView.findViewById(R.id.tv_word_relative_item);
            }
        }
    }
    private void setupSpinner(){
//        langList = new ArrayList<>();
//        langList.add("全部");
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
                AlertDialog.Builder builder=new AlertDialog.Builder(WordActivity.this);
                builder.setSingleChoiceItems(langs, currentLang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        currentLang=which;
                        spinner.setText(langs[which]);
                    }
                });
                builder.show();
            }
        });
    }
    private void setupSearch(){
//        MySearchHintAdapter mAdapter=new MySearchHintAdapter();

        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,autoCompleteWord);
        tv_search.setAdapter(searchAdapter);
        tv_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getHistorySearch();
                getRelativeWord();
//                searchAdapter.notifyDataSetChanged();[]
            }
        });
    }

    /**
     * 合并两个List，设置输入提示内容
     */
    private void getAutoCompleteWord(){
        autoCompleteWord.clear();
        autoCompleteWord.addAll(relativeWordList);
        autoCompleteWord.addAll(historyList);
        searchAdapter = new ArrayAdapter<>(WordActivity.this, android.R.layout.simple_list_item_1,autoCompleteWord);
        tv_search.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    /**
     * 搜索词条
     * @param key
     */
    private void  search(String key){

        tv_search.clearFocus();//解决每次搜索后下拉框总是显示的问题

//        if(key.equals(""))
//            return;

        for(ArrayList list:wordsList){
            list.clear();
        }
        for(CardView cardView:cardViews){
            cardView.setVisibility(View.GONE);
        }

//        rl_word_relative.setVisibility(View.VISIBLE);
//        myAdapter.notifyDataSetChanged();


        if(key.length()>6){//当超过n个字则缩小字号
            tv_word.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);//调小字号防止越界
        }else {
            tv_word.setTextSize(TypedValue.COMPLEX_UNIT_DIP,45);//调回原来大小
        }
        tv_word.setText(key);

        tv_search.setText(key);
        tv_search.setSelection(key.length());

        getRelativeWords();

        AVQuery<AVObject> query=new AVQuery<>("word");
        query.whereEqualTo("name", key);
        if(currentLang!=LANG_ALL)
            query.whereEqualTo("lang",currentLang+"");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if(list.size()==0){
                        tv_search_count.setText("尚未收录该词条发音，您可以为该词条添加发音");
                    }else {
                        tv_search_count.setText("搜索到 " + list.size() + " 个方言发音");
                    }
                    for (AVObject item : list) {
                        switch (item.getString("lang")) {
                            case "1":
                                yueWords.add(item);
                                cv_word_yue.setVisibility(View.VISIBLE);
                                break;
                            case "2":
                                ganWords.add(item);
                                cv_word_gan.setVisibility(View.VISIBLE);
                                break;
                            case "3":
                                hakWords.add(item);
                                cv_word_hak.setVisibility(View.VISIBLE);
                                break;
                            case "4":
                                cjyWords.add(item);
                                cv_word_cjy.setVisibility(View.VISIBLE);
                                break;
                            case "5":
                                zhWords.add(item);
                                cv_word_zh.setVisibility(View.VISIBLE);
                                break;
                            case "6":
                                cdoWords.add(item);
                                cv_word_cdo.setVisibility(View.VISIBLE);
                                break;
                            case "7":
                                nanWords.add(item);
                                cv_word_nan.setVisibility(View.VISIBLE);
                                break;
                            case "8":
                                wuuWords.add(item);
                                cv_word_wuu.setVisibility(View.VISIBLE);
                                break;
                            case "9":
                                hsnWords.add(item);
                                cv_word_hsn.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;
                        }
                    }
                    for (MySearchResultAdapter adapter : adapters) {
                        adapter.notifyDataSetChanged();
                    }
                    for (int i = 0; i < 9; i++) {
                        adjustListViewHeight(listViews.get(i), adapters.get(i), wordsList.get(i));
                    }
                } else {
                    Log.e("find word error", e.toString());
                }
            }
        });
    }

    /**
     * search（）时获取当前词条的相关词条显示在底部
     */
    private void getRelativeWords(){
        wordsRelative = new ArrayList<>();
        AVQuery<AVObject> query=new AVQuery<>("word");
        String s=tv_search.getText().toString();
        int len=s.length();
        int index= (int) (Math.random()*len);
        query.whereContains("name", String.valueOf(s.charAt(index)));
        query.setLimit(20);
        if(currentLang!=0){//如果语言为全部，则不做限制
            query.whereEqualTo("lang",currentLang+"");
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    if(list.size()>0){
                        for(AVObject item:list){
                            if(!wordsRelative.contains(item.getString("name"))){
                                wordsRelative.add(item.getString("name"));
                            }
                        }
                        rl_word_relative.setVisibility(View.VISIBLE);
                        myAdapter.notifyDataSetChanged();
                    }else {
                        rl_word_relative.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 添加搜索历史
     * @param s 搜索关键词
     */
    private void addHistorySearch(String s){
        if(sp!=null){
            SharedPreferences.Editor editor=sp.edit();
            Set<String> history=sp.getStringSet("history", null);
            if(history==null)
                history=new TreeSet<>();
            history.add(s);
            editor.putStringSet("history",history);
            editor.apply();
        }

    }

    /**
     * 获得搜索历史
     */
    private void getHistorySearch(){
        sp = getSharedPreferences("searchHistory",MODE_PRIVATE);
        Set<String> history=sp.getStringSet("history", null);
        if(history==null){
            history=new TreeSet<>();
            historyList.addAll(history);
        }else {
            historyList.addAll(history);
        }
    }

    /**
     * 获取当前输入的关键词的相关关键词
     */
    private void getRelativeWord(){

        AVQuery<AVObject> query=new AVQuery<>("word");
        if(currentLang!=LANG_ALL)
            query.whereEqualTo("lang",currentLang+"");
        query.whereStartsWith("name", tv_search.getText().toString());
        query.setLimit(10);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    relativeWordList.clear();
                    for(AVObject item:list){
                        if(!relativeWordList.contains(item.getString("name")))//解决重复字问题
                            relativeWordList.add(item.getString("name"));
                    }
                    getAutoCompleteWord();

//                    Log.e("result length", list.size() + "");//5
//                    Log.e("relative length",relativeWordList.size()+"");//5
//                    Log.e("auto length",autoCompleteWord.size()+"");//5
//                    Log.e("auto list",autoCompleteWord.toString());
                }
                else {
                    Log.e("get relative word error",e.toString());
                }
            }
        });
    }

    /**
     * 动态设置 ListView 高度
     * @param listView
     * @param adapter
     * @param items
     */
    private void adjustListViewHeight(ListView listView,MySearchResultAdapter adapter,ArrayList items){
        View view=adapter.getView(0,null,listView);
        view.measure(0, 0);
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height = view.getMeasuredHeight() * items.size() + listView.getDividerHeight() * (items.size() - 1);
        listView.setLayoutParams(params);
    }

    private void play(AVObject item){
//        if(player!=null&&player.isPlaying()){
//            return;
//        }
        AVFile avFile=item.getAVFile("voiceFile");
        if(avFile!=null){
            avFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    if(e==null){
                        try {
                            FileOutputStream fos=openFileOutput("play.mp3", MODE_PRIVATE);
                            fos.write(bytes);
                            fos.close();
                            player = MediaPlayer.create(WordActivity.this, Uri.parse(getFilesDir().getAbsolutePath() + "/play.mp3"));
                            player.start();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    class MySpinnerAdapter extends BaseAdapter{

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
    class MySearchHintAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return autoCompleteWord.size();
        }

        @Override
        public Object getItem(int position) {
            return autoCompleteWord.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=getLayoutInflater().inflate(R.layout.search_item,null);

            TextView tv_search_item= (TextView) v.findViewById(R.id.tv_search_item_word);
            tv_search_item.setText(autoCompleteWord.get(position));

            return v;
        }
    }
    class MySearchResultAdapter extends BaseAdapter{
        private ArrayList<AVObject> list;

        public MySearchResultAdapter(ArrayList<AVObject> list){
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

//            final AVObject item=list.get(position);

            View v=getLayoutInflater().inflate(R.layout.search_result_item,null);
            final ImageButton ib_start= (ImageButton) v.findViewById(R.id.ib_start);
            final TextView tv_like= (TextView) v.findViewById(R.id.tv_like);
            final ImageButton ib_like= (ImageButton) v.findViewById(R.id.ib_like);
            final TextView tv_provider= (TextView) v.findViewById(R.id.tv_provider);
            TextView tv_size= (TextView) v.findViewById(R.id.tv_size);
            if(list.size()!=0) {
                if (list.get(position).getInt("like") > 0)
                    tv_like.setText(list.get(position).getInt("like") + "");
                AVFile avFile=list.get(position).getAVFile("voiceFile");
                if(avFile!=null){
                    tv_size.setText(avFile.getSize() / 1024 + "KB");
                }
//                list.get(position).fetchInBackground(new GetCallback<AVObject>() {
//                    @Override
//                    public void done(AVObject avObject, AVException e) {
//                        if (e == null) {
//                            AVUser avUser = list.get(position).getAVUser("provider");
//                            Log.e("fetched object",avObject.toString());
////                            tv_provider.setText(avUser.getUsername());
//                        }
//                    }
//                });           avu
//                AVUser user=list.get(position).getAVUser("provider");
//                Log.e("item",list.get(position).toString());
                AVObject provider = list.get(position).getAVObject("provider");
                if (provider != null) {
                    Log.e("provider", provider.toString());
                    provider.fetchInBackground(new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
//                            Log.e("provider name",avObject.getString("username"));
                            tv_provider.setText(avObject.getString("username"));
                        }
                    });
                }
            }

            ib_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //开始播放
                    play(list.get(position));
//                    Log.e("start play", "");
                }
            });
            ib_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.get(position).increment("like");
                    list.get(position).saveInBackground();
                    int i = Integer.parseInt(tv_like.getText().toString()) + 1;
                    tv_like.setText(i + "");
                }
            });
            ib_start.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ib_start.setBackground(getResources().getDrawable(R.drawable.iconfont_word_speaker2));
                            break;
                        case MotionEvent.ACTION_UP:
                            ib_start.setBackground(getResources().getDrawable(R.drawable.iconfont_word_speaker));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            ib_start.setBackground(getResources().getDrawable(R.drawable.iconfont_word_speaker));
                        default:
                            break;
                    }
                    return false;
                }
            });
            ib_like.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            ib_like.setBackground(getResources().getDrawable(R.drawable.iconfont_thumb));
                            break;
                        case MotionEvent.ACTION_UP:
                            ib_like.setBackground(getResources().getDrawable(R.drawable.iconfont_thumb2));
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            ib_like.setBackground(getResources().getDrawable(R.drawable.iconfont_thumb2));
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });


            return v;
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_NEW_WORD&&resultCode==RESULT_OK){  //若新增发音成功则刷新结果
            search(tv_search.getText().toString());
        }
    }
}
