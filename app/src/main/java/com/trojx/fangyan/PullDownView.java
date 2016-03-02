package com.trojx.fangyan;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 下拉刷新控件
 * 真正实现下拉刷新的是它‘
 * *有bug*
 * Created by Trojx on 2016/2/29.
 */
public class PullDownView extends LinearLayout implements ScrollOverListView.OnScrollOverListener {

    private boolean mIsDown;            // 是否按下
    private boolean mIsRefreshing;      // 是否下拉刷新中
    private boolean mIsFetchMoreing;    // 是否获取更多中
    private boolean mIsPullUpDone;      // 是否回推完成
    private boolean mEnableAutoFetchMore;   // 是否允许自动获取更多

    // 头部文件的状态
    private static final int HEADER_VIEW_STATE_IDLE = 0;            // 空闲
    private static final int HEADER_VIEW_STATE_NOT_OVER_HEIGHT = 1; // 没有超过默认高度
    private static final int HEADER_VIEW_STATE_OVER_HEIGHT = 2;     // 超过默认高度
    private int mHeaderViewState = HEADER_VIEW_STATE_IDLE;

    private static final int START_PULL_DEVIATION = 50; // 移动误差
    private static final int AUTO_INCREMENTAL = 10;     // 自增量，用于回弹

    private static final int WHAT_DID_LOAD_DATA = 1;    // Handler what 数据加载完毕
    private static final int WHAT_ON_REFRESH = 2;       // Handler what 刷新中
    private static final int WHAT_DID_REFRESH = 3;      // Handler what 已经刷新完
    private static final int WHAT_SET_HEADER_HEIGHT = 4;// Handler what 设置高度
    private static final int WHAT_DID_MORE = 5;         // Handler what 已经获取完更多

    final int DEFAULT_HEADER_VIEW_HEIGHT= 105;          //头部文件原本的高度


    private TextView mHeaderTextView;
    private ImageView mHeaderArrowView;
    private ProgressBar mHeaderLoadingView;
    private boolean mIsFetchingMore;
    private ProgressBar mFooterLoadingView;
    private TextView mFooterTextView;
    private TextView mHeaderViewDateView;
    private View mFooterView;
    private OnPullDownListener mOnPullDownListener;
    private View mHeaderView;
    private ViewGroup.LayoutParams mHeaderViewParams;
    private RotateAnimation mRotate0To180Animation;
    private RotateAnimation mRotate180To0Animation;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd HH:mm");
    private int mHeaderIncremental;//增量
    private float mMotionDownLastY; // 按下时候的Y轴坐标
    private ScrollOverListView mListView;

    public PullDownView(Context context) {
        super(context);
        initHeaderViewAndFooterViewAndListView(context);
    }

    public PullDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderViewAndFooterViewAndListView(context);
    }

    /**
     * 刷新事件接口
     */
    public interface OnPullDownListener {
        void onRefresh();
        void onMore();
    }
    /**
     * 通知加载完了数据，要放在Adapter.notifyDataSetChanged后面
     * 当你加载完数据的时候，调用这个notifyDidLoad()
     * 才会隐藏头部，并初始化数据等
     */
    public void notifyDidLoad() {
        mUIHandler.sendEmptyMessage(WHAT_DID_LOAD_DATA);
    }

    /**
     * 通知已经刷新完了，要放在Adapter.notifyDataSetChanged后面
     * 当你执行完刷新任务之后，调用这个notifyDidRefresh()
     * 才会隐藏掉头部文件等操作
     */
    public void notifyDidRefresh() {
        mUIHandler.sendEmptyMessage(WHAT_DID_REFRESH);
    }

    /**
     * 通知已经获取完更多了，要放在Adapter.notifyDataSetChanged后面
     * 当你执行完更多任务之后，调用这个notyfyDidMore()
     * 才会隐藏加载圈等操作
     */
    public void notifyDidMore() {
        mUIHandler.sendEmptyMessage(WHAT_DID_MORE);
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void setOnPullDownListener(OnPullDownListener listener){
        mOnPullDownListener = listener;
    }

    /**
     * 获取内嵌的listview
     * @return ScrollOverListView
     */
    public ListView getListView(){
        return mListView;
    }

    /**
     * 是否开启自动获取更多
     * 自动获取更多，将会隐藏footer，并在到达底部的时候自动刷新
     * @param index 倒数第几个触发
     */
    public void enableAutoFetchMore(boolean enable, int index){
        if(enable){
            mListView.setBottomPosition(index);
            mFooterLoadingView.setVisibility(View.VISIBLE);
        }else{
            mFooterTextView.setText("更多");
            mFooterLoadingView.setVisibility(View.GONE);
        }
        mEnableAutoFetchMore = enable;
    }

    /**
     * 初始化界面
     * @param context 上下文
     */
    private void initHeaderViewAndFooterViewAndListView(Context context){
        setOrientation(LinearLayout.VERTICAL);

        /*
        自定义头部文件
         */
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.pulldown_header,null);
        mHeaderViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mHeaderView,0, mHeaderViewParams);

        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.tv_pulldown_loading);
        mHeaderArrowView = (ImageView) mHeaderView.findViewById(R.id.iv_pulldown_arrow);
        mHeaderLoadingView = (ProgressBar) findViewById(R.id.pb_pulldown_header);

        // 注意，图片旋转之后，再执行旋转，坐标会重新开始计算
        mRotate0To180Animation = new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mRotate0To180Animation.setDuration(200);
        mRotate0To180Animation.setFillAfter(true);

        mRotate180To0Animation = new RotateAnimation(180,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mRotate180To0Animation.setDuration(200);
        mRotate180To0Animation.setFillAfter(true);

        /*
        自定义脚部文件
         */
        mFooterView = LayoutInflater.from(context).inflate(R.layout.pulldown_footer,null);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.tv_pulldown_footer);
        mFooterLoadingView = (ProgressBar) mFooterView.findViewById(R.id.pb_pulldown_footer);
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsFetchingMore) {
                    mIsFetchingMore = true;
                    mFooterLoadingView.setVisibility(VISIBLE);
                    mOnPullDownListener.onMore();
                }
            }
        });
        mListView = new ScrollOverListView(context);
        mListView.setOnScrollOverListener(this);
        mListView.setCacheColorHint(0);
        addView(mListView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //空的Listener
        mOnPullDownListener = new OnPullDownListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onMore() {

            }
        };
    }

    /**
     * 在下拉和回退的时候检查头部的状态，
     * 如果超过了默认高度，就显示可以刷新，
     * 否则显示下拉刷新
     */
    private void checkHeaderViewState(){
        if(mHeaderViewParams.height>=DEFAULT_HEADER_VIEW_HEIGHT){
            if(mHeaderViewState==HEADER_VIEW_STATE_OVER_HEIGHT) return;
            mHeaderViewState=HEADER_VIEW_STATE_OVER_HEIGHT;
            mHeaderTextView.setText("松开可以刷新");
            mHeaderArrowView.startAnimation(mRotate0To180Animation);
        }else {
            if(mHeaderViewState==HEADER_VIEW_STATE_NOT_OVER_HEIGHT
                    ||mHeaderViewState==HEADER_VIEW_STATE_IDLE)return;
            mHeaderViewState=HEADER_VIEW_STATE_NOT_OVER_HEIGHT;
            mHeaderTextView.setText("下拉可以刷新");
            mHeaderArrowView.startAnimation(mRotate180To0Animation);
        }
    }

    private void setHeaderHeight(int height){
        mHeaderIncremental = height;
        mHeaderViewParams.height=height;
        mHeaderView.setLayoutParams(mHeaderViewParams);
    }
    /**
     * 自动隐藏动画
     */
    class HideHeaderViewTask extends TimerTask {
        @Override
        public void run() {
            if(mIsDown) {
                cancel();
                return;
            }
            mHeaderIncremental -= AUTO_INCREMENTAL;
            if(mHeaderIncremental > 0){
                mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
            }else{
                mHeaderIncremental = 0;
                mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
                cancel();
            }
        }
    }
    /**
     * 自动显示动画
     */
    class ShowHeaderViewTask extends TimerTask{

        @Override
        public void run() {
            if(mIsDown) {
                cancel();
                return;
            }
            mHeaderIncremental -= AUTO_INCREMENTAL;
            if(mHeaderIncremental > DEFAULT_HEADER_VIEW_HEIGHT){
                mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
            }else{
                mHeaderIncremental = DEFAULT_HEADER_VIEW_HEIGHT;
                mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
                if(!mIsRefreshing){
                    mIsRefreshing = true;
                    mUIHandler.sendEmptyMessage(WHAT_ON_REFRESH);
                }
                cancel();
            }
        }
    }
    /**
     * 显示脚步脚部文件
     */
    private void showFooterView(){
        if(mListView.getFooterViewsCount() == 0 && isFillScreenItem()){
            mListView.addFooterView(mFooterView);
            mListView.setAdapter(mListView.getAdapter());
        }
    }
    /**
     * 条目是否填满整个屏幕
     */
    private boolean isFillScreenItem(){
        final int firstVisiblePosition = mListView.getFirstVisiblePosition();
        final int lastVisiblePostion = mListView.getLastVisiblePosition() - mListView.getFooterViewsCount();
        final int visibleItemCount = lastVisiblePostion - firstVisiblePosition + 1;
        final int totalItemCount = mListView.getCount() - mListView.getFooterViewsCount();

        if(visibleItemCount < totalItemCount) return true;
        return false;
    }

    private Handler mUIHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DID_LOAD_DATA:{
                    mHeaderViewParams.height = 0;
                    mHeaderLoadingView.setVisibility(View.GONE);
                    mHeaderTextView.setText("下拉可以刷新");
                    mHeaderViewDateView = (TextView) mHeaderView.findViewById(R.id.tv_pulldown_last_update);
                    mHeaderViewDateView.setVisibility(View.VISIBLE);
                    mHeaderViewDateView.setText("更新于：" + dateFormat.format(new Date(System.currentTimeMillis())));
                    mHeaderArrowView.setVisibility(View.VISIBLE);
                    showFooterView();
                    return;
                }

                case WHAT_ON_REFRESH:{
                    // 要清除掉动画，否则无法隐藏
                    mHeaderArrowView.clearAnimation();
                    mHeaderArrowView.setVisibility(View.INVISIBLE);
                    mHeaderLoadingView.setVisibility(View.VISIBLE);
                    mOnPullDownListener.onRefresh();
                    return;
                }

                case WHAT_DID_REFRESH :{
                    mIsRefreshing = false;
                    mHeaderViewState = HEADER_VIEW_STATE_IDLE;
                    mHeaderArrowView.setVisibility(View.VISIBLE);
                    mHeaderLoadingView.setVisibility(View.GONE);
                    mHeaderViewDateView.setText("更新于：" + dateFormat.format(new Date(System.currentTimeMillis())));
                    setHeaderHeight(0);
                    showFooterView();
                    return;
                }

                case WHAT_SET_HEADER_HEIGHT :{
                    setHeaderHeight(mHeaderIncremental);
                    return;
                }

                case WHAT_DID_MORE :{
                    mIsFetchMoreing = false;
                    mFooterTextView.setText("更多");
                    mFooterLoadingView.setVisibility(View.GONE);
                }
            }
        }
    };


    /**
     * 实现OnScrollOverListener接口
     * @param delta 手指点击移动的偏移量
     * @return 是否自己处理
     */
    @Override
    public boolean onListViewTopAndPullDown(int delta) {
        if(mIsRefreshing || mListView.getCount() - mListView.getFooterViewsCount() == 0) return false;

        int absDelta = Math.abs(delta);
        final int i = (int) Math.ceil((double)absDelta / 2);

        mHeaderIncremental += i;
        if(mHeaderIncremental >= 0){ // && mIncremental <= mMaxHeight
            setHeaderHeight(mHeaderIncremental);
            checkHeaderViewState();
        }
        return true;
    }

    @Override
    public boolean onListViewBottomAndPullUp(int delta) {
        if(!mEnableAutoFetchMore || mIsFetchMoreing) return false;
        // 数量充满屏幕才触发
        if(isFillScreenItem()){
            mIsFetchMoreing = true;
            mFooterTextView.setText("加载更多中...");
            mFooterLoadingView.setVisibility(View.VISIBLE);
            mOnPullDownListener.onMore();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMotionDown(MotionEvent event) {
        mIsDown = true;
        mIsPullUpDone = false;
        mMotionDownLastY = event.getRawY();
        return false;
    }

    @Override
    public boolean onMotionMove(MotionEvent event, int delta) {
        //当头部文件回推消失的时候，不允许滚动
        if(mIsPullUpDone) return true;

        // 如果开始按下到滑动距离不超过误差值，则不滑动
        final int absMotionY = (int) Math.abs(event.getRawY() - mMotionDownLastY);
        if(absMotionY < START_PULL_DEVIATION) return true;

        final int absDelta = Math.abs(delta);
        final int i = (int) Math.ceil((double)absDelta / 2);

        // onTopDown在顶部，并上回推和onTopUp相对
        if(mHeaderViewParams.height > 0 && delta < 0){
            mHeaderIncremental -= i;
            if(mHeaderIncremental > 0){
                setHeaderHeight(mHeaderIncremental);
                checkHeaderViewState();
            }else{
                mHeaderViewState = HEADER_VIEW_STATE_IDLE;
                mHeaderIncremental = 0;
                setHeaderHeight(mHeaderIncremental);
                mIsPullUpDone = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onMotionUp(MotionEvent event) {
        mIsDown = false;
        // 避免和点击事件冲突
        if(mHeaderViewParams.height > 0){
            // 判断头文件拉动的距离与设定的高度，小了就隐藏，多了就固定高度
            int x = mHeaderIncremental - DEFAULT_HEADER_VIEW_HEIGHT;
            Timer timer = new Timer(true);
            if(x < 0){
                timer.scheduleAtFixedRate(new HideHeaderViewTask(), 0, 10);
            }else{
                timer.scheduleAtFixedRate(new ShowHeaderViewTask(), 0, 10);
            }
            return true;
        }
        return false;
    }
}
