package com.trojx.fangyan.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.fangyan.R;
import com.trojx.fangyan.Util.RelativeDateFormat;
import com.trojx.fangyan.activity.LoginActivity;
import com.trojx.fangyan.activity.StoryActivity;
import com.trojx.fangyan.activity.WordActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/1/18.
 */
public class MeFragment extends Fragment {

    public CircleImageView civ_user;
    private TextView tv_name;
    private TextView tv_setting;
    private ImageView iv_setting;
    private RelativeLayout rl_word_head;
    private ListView lv_word;
    private ListView lv_story;
    private RelativeLayout rl_story_head;
    private AVUser user;
    private ArrayList<AVObject> words=null;
    private ArrayList<AVObject> storys=null;
    private static final String[] langs={"粤语","赣语","客家语","晋语","汉语","闽东语","闽南语","吴语","湘语"};
    private MyWordAdapter adapterWord;
    private MyStoryAdapter adapterStory;
    private ImageView iv_word_arrow;
    private ImageView iv_story_arrow;
    private ImageView iv_back_blur;
    public static final int REQUEST_LOGIN=1;
    public static final int REQUEST_NEW_LOGO=2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View v=inflater.inflate(R.layout.fragment_me,container,false);
        civ_user = (CircleImageView) v.findViewById(R.id.iv_me_user);
        tv_name = (TextView) v.findViewById(R.id.tv_me_name);
        tv_setting = (TextView) v.findViewById(R.id.tv_me_setting);
        iv_setting = (ImageView) v.findViewById(R.id.iv_me_setting);
        rl_word_head = (RelativeLayout) v.findViewById(R.id.rl_me_word_head);
        lv_word = (ListView) v.findViewById(R.id.lv_me_word);
        rl_story_head = (RelativeLayout) v.findViewById(R.id.rl_me_story_head);
        lv_story = (ListView) v.findViewById(R.id.lv_me_story);
        iv_word_arrow = (ImageView) v.findViewById(R.id.iv_me_word_arrow);
        iv_story_arrow = (ImageView) v.findViewById(R.id.iv_me_story_arrow);
        iv_back_blur = (ImageView) v.findViewById(R.id.iv_me_blur_background);

        iv_setting.setVisibility(View.GONE);//setting被改为注销，命名不变
        tv_setting.setVisibility(View.GONE);

        refresh();///////

        adapterWord = new MyWordAdapter();
        lv_word.setAdapter(adapterWord);

        adapterStory = new MyStoryAdapter();
        lv_story.setAdapter(adapterStory);

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                } else {
//                    user.logOut();
//                    refresh();
                }
            }
        });

        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.logOut();
                refresh();
            }
        });

        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.logOut();
                refresh();
            }
        });

        rl_word_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (lv_word.getVisibility()) {
                    case View.GONE:
                        lv_word.setVisibility(View.VISIBLE);
                        iv_word_arrow.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_arrow_up));
//                        adjustListViewHeight(); ??
                        break;
                    case View.VISIBLE:
                        lv_word.setVisibility(View.GONE);
                        iv_word_arrow.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_arrow_down));
                        break;
                    default:
                        break;
                }
            }
        });

        rl_story_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (lv_story.getVisibility()) {
                    case View.GONE:
                        lv_story.setVisibility(View.VISIBLE);
                        iv_story_arrow.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_arrow_up));
                        break;
                    case View.VISIBLE:
                        lv_story.setVisibility(View.GONE);
                        iv_story_arrow.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_arrow_down));
                        break;
                    default:
                        break;
                }
            }
        });

        lv_story.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String objectString = storys.get(position).toString();
                Intent intent = new Intent(getActivity(), StoryActivity.class);
                intent.putExtra("AVObject", objectString);
                startActivity(intent);
            }
        });

        lv_word.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = words.get(position).getString("name");
                Intent intent = new Intent(getActivity(), WordActivity.class);
                intent.putExtra("word", key);
                startActivity(intent);
            }
        });

        civ_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AVUser.getCurrentUser() != null) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_NEW_LOGO);
                }
            }
        });


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    /**
     * 获取单词收藏、发布的故事数据
     */
    private void getListViewData(){
        if(words!=null){
            words.clear();
        }else {
            words = new ArrayList<>();
        }
        if(storys!=null){
            storys.clear();
        }else {
            storys = new ArrayList<>();
        }
        if(user!=null){
            AVQuery<AVObject> query_word=new AVQuery<>("word");
            query_word.whereEqualTo("provider", user);
            query_word.include("provider");//防止转到详情界面时用户空指针
            query_word.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject item : list) {
                            words.add(item);
                        }
                        adapterWord.notifyDataSetChanged();
                        adjustListViewHeight(lv_word, adapterWord, words);
                    } else {
                        Log.e("get my words error", e.toString());
                    }
                }
            });

            AVQuery<AVObject> query_story=new AVQuery<>("story");
            query_story.whereEqualTo("provider",user);
            query_story.include("provider");//防止转到详情界面时用户空指针
            query_story.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(e==null){
                        for(AVObject item:list){
                            storys.add(item);
                        }
                        adapterStory.notifyDataSetChanged();
                        adjustListViewHeight(lv_story,adapterStory,storys);
                    }else {
                        Log.e("get my storys error",e.toString());
                    }
                }
            });
        }

    }
    class MyWordAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return words.size();
        }

        @Override
        public Object getItem(int position) {
            return words.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            v=LayoutInflater.from(getActivity()).inflate(R.layout.my_word_item,null);
            TextView tv_name= (TextView) v.findViewById(R.id.tv_my_word_name);
            TextView tv_lang= (TextView) v.findViewById(R.id.tv_my_word_lang);
            TextView tv_like= (TextView) v.findViewById(R.id.tv_my_word_like);
            ImageView iv_delete= (ImageView) v.findViewById(R.id.iv_my_word_delete);
            if(words.size()>0){
                final AVObject item=words.get(position);//越界？
                tv_name.setText(item.getString("name"));
                String lang=item.getString("lang");
                Integer langInt=Integer.parseInt(lang);
                tv_lang.setText(langs[langInt-1]);
                tv_like.setText(item.getInt("like") + "人赞过");

                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(getActivity(), "删除", "确认删除这个词条吗？");
                        dialog.show();
                        dialog.getButtonAccept().setText("是的");
                        dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                item.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        words.remove(item);
                                        adapterWord.notifyDataSetChanged();
                                        adjustListViewHeight(lv_word,adapterWord,words);
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
            return v;
        }
    }
    class MyStoryAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return storys.size();
        }

        @Override
        public Object getItem(int position) {
            return storys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            v=LayoutInflater.from(getActivity()).inflate(R.layout.my_story_item,null);
            TextView tv_name= (TextView) v.findViewById(R.id.tv_my_story_name);
            TextView tv_time= (TextView) v.findViewById(R.id.tv_my_story_time);
            TextView tv_comment= (TextView) v.findViewById(R.id.tv_my_story_comment_count);
            TextView tv_like= (TextView) v.findViewById(R.id.tv_my_story_like_count);
            ImageView iv_delete= (ImageView) v.findViewById(R.id.iv_my_story_delete);

            if(storys.size()>0){
                final AVObject item=storys.get(position);

                tv_name.setText(item.getString("content"));
                tv_like.setText(item.getInt("like")+"");
                tv_comment.setText(item.getInt("commentcount")+"");
                tv_time.setText(RelativeDateFormat.format(item.getDate("createdAt")));


                iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(getActivity(), "删除", "确认删除这个故事吗？");
                        dialog.show();
                        dialog.getButtonAccept().setText("是的");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                item.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        storys.remove(item);
                                        adapterStory.notifyDataSetChanged();
                                        adjustListViewHeight(lv_story,adapterStory,storys);
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }

            return v;
        }
    }

    /**
     * 刷新显示，如从登录activity返回后
     */
    public  void refresh(){
        user=AVUser.getCurrentUser();

        getListViewData();

        if(user!=null){
            user.fetchInBackground("logo", new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if(e==null){
                        AVFile file=avObject.getAVFile("logo");
                        if(file!=null&&civ_user!=null){
                            Glide.with(getActivity()).load(file.getThumbnailUrl(true,80,80)).crossFade().into(civ_user);//改用缩略图
                        }
                       AVFile file1=avObject.getAVFile("blurBack");
                        if(file1!=null&&iv_back_blur!=null){
                            Glide.with(getActivity()).load(file1.getThumbnailUrl(true,360,240)).crossFade().into(iv_back_blur);
                        }

                    }
                }
            });
            tv_name.setText(user.getUsername());
            tv_setting.setVisibility(View.VISIBLE);
            iv_setting.setVisibility(View.VISIBLE);

        }else {
            tv_name.setText("注册/登录");
            civ_user.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_no_avatar2));
            tv_setting.setVisibility(View.GONE);
            iv_setting.setVisibility(View.GONE);
        }
    }

    /**
     * 动态设置ListView高度
     * @param listView
     * @param adapter
     * @param items
     */
    private void adjustListViewHeight(ListView listView,BaseAdapter adapter,ArrayList items){
        View view=adapter.getView(0, null, listView);
        view.measure(0, 0);
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height = view.getMeasuredHeight() * items.size() + listView.getDividerHeight() * (items.size() - 1);
        listView.setLayoutParams(params);
    }

    /**
     * 将bitmap高斯模糊化
     * @param bitmap
     * @return
     */
    public Bitmap blurBitmap(Bitmap bitmap){

        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(getActivity().getApplicationContext());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(25.f);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        bitmap.recycle();
        rs.destroy();

        return  outBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_LOGIN:
                if(resultCode== Activity.RESULT_OK)
                    refresh();
                break;
            case REQUEST_NEW_LOGO:
                if(resultCode==Activity.RESULT_OK){
                    if(data!=null&&data.getData()!=null){
                        Uri uri=data.getData();
                        Log.e("data",data.getData().toString());
                        ContentResolver cr=getActivity().getContentResolver();
                        try {
                            InputStream in=cr.openInputStream(uri);
                            final File file=new File(getActivity().getFilesDir().getAbsolutePath()+uri.hashCode());
                            if(in==null)
                                break;
                            BufferedInputStream bis=new BufferedInputStream(in);
                            FileOutputStream fos=new FileOutputStream(file);
                            byte[] buff=new byte[1024];
                            while (bis.read(buff)!=-1){
                                fos.write(buff);
                                fos.flush();
                            }
                            fos.close();

                            Glide.with(this).load(file).into(civ_user);//先加载本地的头像文件

                            AVUser avUser=AVUser.getCurrentUser();
                            AVFile avFile=AVFile.withAbsoluteLocalPath(avUser.getUsername(),file.getAbsolutePath());
                            avUser.put("logo", avFile);
                            avUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {

                                    generateBlurImg(file);//在这里调用，否则同时上传两张图片报错

                                    if (e != null) {
                                        Log.e("save new logo error", e.toString());
                                    }
                                }
                            });

                        } catch (IOException e) {
                            Log.e("upload user logo error",e.toString());
                        }

                    }
                }
                break;
        }
    }

    /**
     * 生成高斯模糊照片并保存到cloud并刷新显示
     */
    public void generateBlurImg(File file){
        if(file.exists()){
            Display dp=getActivity().getWindowManager().getDefaultDisplay();
            int screenWidth=dp.getWidth();
            int screenHeight=dp.getHeight();
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            int imgWidth=options.outWidth;
            int imgHeight=options.outHeight;
            int scale=1;
            int scaleX=imgWidth/screenWidth;
            int scaleY=imgHeight/screenHeight;
            if(scaleX >= scaleY && scaleX > 1){
                scale = scaleX;
            }
            else if(scaleY > scaleX && scaleY > 1){
                scale = scaleY;
            }
            options.inSampleSize=scale;
            options.inJustDecodeBounds=false;
            Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            Bitmap blurBitmap=blurBitmap(bitmap);
            try{
                File file1=new File(getActivity().getFilesDir().getAbsolutePath()+"//blurBack.png");
                FileOutputStream fos=new FileOutputStream(file1);
                blurBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                AVFile avFile=AVFile.withFile(AVUser.getCurrentUser().getUsername(),file1);
                user.put("blurBack",avFile);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e==null){
                            AVFile blurBack=user.getAVFile("blurBack");
                            Glide.with(MeFragment.this).load(blurBack.getUrl()).crossFade().into(iv_back_blur);
                        }else {
                            Log.e("save blurBack error",e.toString());
                        }
                    }
                });
            }catch (IOException e){
                Log.e("generate blur error",e.toString());
            }
        }
    }
}
