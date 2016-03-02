package com.trojx.regularpractice.scrollertest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trojx.regularpractice.R;

/**
 * Created by Administrator on 2016/2/22.
 */
public class ScrollerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);

        final CustomView customView= (CustomView) findViewById(R.id.custom_view);

    }
}
