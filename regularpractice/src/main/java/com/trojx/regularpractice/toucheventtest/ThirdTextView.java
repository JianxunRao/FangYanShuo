package com.trojx.regularpractice.toucheventtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/22.
 */
public class ThirdTextView extends TextView {


    public ThirdTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
            Log.e("ThirdTextView" + event.getAction(), "下面没人了，并不能分派给下一级");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_DOWN)
            Log.e("ThirdTextView"+event.getAction(),"实习生处理完了该事件");
        return  true;
    }

}
