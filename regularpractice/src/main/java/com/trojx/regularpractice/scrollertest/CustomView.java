package com.trojx.regularpractice.scrollertest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/2/22.
 */
public class CustomView extends LinearLayout {


    private final Scroller mScroller;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }
    public void smoothScrollTo(int fx,int fy){
        int dx=fx-mScroller.getFinalX();
        int dy=fy-mScroller.getFinalY();
        smoothScrollBy(dx,dy);
    }
    public void smoothScrollBy(int dx,int dy){
        mScroller.startScroll(mScroller.getFinalX(),mScroller.getFinalY(),dx,dy);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }
}
