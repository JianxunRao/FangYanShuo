package com.trojx.fangyan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2016/1/31.
 */
public class UpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.zhagame.com/fangyan"));
        startActivity(intent);
        finish();
    }
}
