package com.trojx.fangyan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**可以监听是否滚动到顶部或底部的自定义ListView
 * *有bug*
 * Created by Trojx on 2016/2/29.
 */
public class ScrollOverListView extends ListView{
    private int mLastY;
    private int mTopPosition;
    private  int mBottomPosition;

    public ScrollOverListView(Context context) {
        super(context);
        init();
    }

    public ScrollOverListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollOverListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mTopPosition=0;
        mBottomPosition=0;
    }

    private OnScrollOverListener mOnScrollOverListener=new OnScrollOverListener() {
        @Override
        public boolean onListViewTopAndPullDown(int delta) {
            return false;
        }

        @Override
        public boolean onListViewBottomAndPullUp(int delta) {
            return false;
        }

        @Override
        public boolean onMotionDown(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onMotionMove(MotionEvent event,int delta) {
            return false;
        }

        @Override
        public boolean onMotionUp(MotionEvent event) {
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action=ev.getAction();
        final int y= (int) ev.getRawY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mLastY=y;//这到底什么意思？
                final boolean isHandled=mOnScrollOverListener.onMotionDown(ev);
                if(isHandled){
                    mLastY=y;
                    return isHandled;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final int childCount=getChildCount();
                if(childCount==0)
                    return super.onTouchEvent(ev);
                final int  itemCount=getAdapter().getCount()-mBottomPosition;
                final int deltaY=y-mLastY;
                final int firstTop=getChildAt(0).getTop();
                final int listPadding=getListPaddingTop();

                final int lastBottom=getChildAt(childCount-1).getBottom();
                final int end=getHeight()-getPaddingBottom();

                final int firstVisiblePosition=getFirstVisiblePosition();

                final boolean isHandleMotionMove=mOnScrollOverListener.onMotionMove(ev,deltaY);

                if(isHandleMotionMove){
                    mLastY=y;
                    return true;
                }

                if(firstVisiblePosition<=mTopPosition&&firstTop>=listPadding&&deltaY>0){
                    final boolean isHandleOnListViewTopAndPullDown;
                    isHandleOnListViewTopAndPullDown=mOnScrollOverListener.onListViewTopAndPullDown(deltaY);
                    if(isHandleOnListViewTopAndPullDown){
                        mLastY=y;
                        return  true;
                    }
                }
                if(firstVisiblePosition+childCount>=itemCount&&lastBottom<=end&&deltaY<0){
                    final boolean isHandleOnListViewBottomAndPullDown;
                    isHandleOnListViewBottomAndPullDown = mOnScrollOverListener.onListViewBottomAndPullUp(deltaY);
                    if(isHandleOnListViewBottomAndPullDown){
                        mLastY = y;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                final boolean isHandleMotionUp=mOnScrollOverListener.onMotionUp(ev);
                if(isHandleMotionUp){
                    mLastY=y;
                    return true;
                }
                break;
        }
        mLastY=y;
        return super.onTouchEvent(ev);
    }

    /**
     * 自定义其中一个条目为头部，头部触发事件以此为准，默认为第一个
     * @param index 正数第几个
     */
    public void setTopPosition(int index){
        if(getAdapter()==null){
            throw new NullPointerException("没有指定Adapter！");
        }
        if(index<0){
            throw new IllegalArgumentException("条目数不能小于零");
        }
        mTopPosition=index;
    }

    /**
     * 自定义其中一个条目为底部，底部触发事件以此为准，默认为最后一个
     * @param index *倒数*第几个
     */
    public void setBottomPosition(int index){
        if(getAdapter()==null){
            throw new NullPointerException("没有指定Adapter！");
        }
        if(index<0){
            throw new IllegalArgumentException("条目数不能小于零");
        }
        mBottomPosition=index;
    }

    /**
     * 设置外部监听器
     * @param onScrollOverListener
     */
    public void setOnScrollOverListener(OnScrollOverListener onScrollOverListener){
        mOnScrollOverListener=onScrollOverListener;
    }

    public interface OnScrollOverListener{
        /**
         * 到达顶部时触发
         * @param delta 手指点击移动的偏移量
         * @return
         */
        boolean onListViewTopAndPullDown(int delta);

        /**
         *到达底部时触发
         * @param delta 手指点击移动的偏移量
         * @return
         */
        boolean onListViewBottomAndPullUp(int delta);

        /**
         * 手指触摸按下触发
         * @param event
         * @return 返回true表示自己处理
         */
        boolean onMotionDown(MotionEvent event);

        /**
         * 手指移动时触发
         * @param event
         * @return 返回true表示自己处理
         */
        boolean onMotionMove(MotionEvent event,int delta);

        /**
         * 手指提起后触发
         * @param event
         * @return 返回true表示自己处理
         */
        boolean onMotionUp(MotionEvent event);
    }
}
