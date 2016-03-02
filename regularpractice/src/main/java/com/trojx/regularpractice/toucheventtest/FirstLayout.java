package com.trojx.regularpractice.toucheventtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/2/22.
 */
public class FirstLayout extends FrameLayout {


    public FirstLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN)
        Log.e("FirstLayout" + ev.getAction(), "分派给下一级");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("FirstLayout" + ev.getAction(), "需要拦截吗？  false");
//        return super.onInterceptTouchEvent(ev);
        return false;
    }
}
