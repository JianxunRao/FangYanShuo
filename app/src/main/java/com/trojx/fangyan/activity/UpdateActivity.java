package com.trojx.fangyan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

/**
 * Created by Administrator on 2016/1/31.
 */
public class UpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVQuery<AVObject> query=new AVQuery<>("Other");
        query.whereEqualTo("name", "UpdateActivity跳转至的Url");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    if (list.size()>0){
                        String url=list.get(0).getString("url");
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
//V1.0&V1.1:
//        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.zhagame.com/fangyan"));
//        startActivity(intent);
//        finish();
    }
}
