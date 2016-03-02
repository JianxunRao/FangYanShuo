package com.trojx.regularpractice.toucheventtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.trojx.regularpractice.R;

/**
 * Created by Administrator on 2016/2/22.
 */
public class TouchTestActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_test);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            Log.e("Activity"+ev.getAction(),"分派给下一级");
        }
        return super.dispatchTouchEvent(ev);
    }
}
