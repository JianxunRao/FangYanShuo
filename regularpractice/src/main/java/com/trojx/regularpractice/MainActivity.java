package com.trojx.regularpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/2.
 */
public class MainActivity extends AppCompatActivity {

    private PublicBussiness zjr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView=getLayoutInflater().inflate(R.layout.activity_main,null);
        setContentView(rootView);
        TextView textView = (TextView) findViewById(R.id.text_view);
        ImageView imageView = (ImageView) findViewById(R.id.image_view);

        //一、将布局View存成图像保存
//        View view=getLayoutInflater().inflate(R.layout.activity_main,null);
//        view.setDrawingCacheEnabled(true);
//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        try{
//            Bitmap bitmap=view.getDrawingCache();
//            FileOutputStream fos=new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"viewCache.jpg"));
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
//            fos.close();
//        }catch (Exception e){
//            Log.e("error",e.toString());
//        }

        //二、设置渐变背景色
//        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{Color.BLUE,Color.RED});
//        getWindow().setBackgroundDrawable(gradientDrawable);


        //三、SpannableString文本类，包含不可变的文本但可以用已有对象替换和分离。
////可变文本类参考SpannableStringBuilder
//        SpannableString ss = new SpannableString("红色打电话斜体删除线绿色下划线图片:.");
////用颜色标记文本
//        ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 2,
////setSpan时需要指定的 flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括).
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////用超链接标记文本
//        ss.setSpan(new URLSpan("tel:4155551212"), 2, 5,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////用样式标记文本（斜体）
//        ss.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 5, 7,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////用删除线标记文本
//        ss.setSpan(new StrikethroughSpan(), 7, 10,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////用下划线标记文本
//        ss.setSpan(new UnderlineSpan(), 10, 16,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////用颜色标记
//        ss.setSpan(new ForegroundColorSpan(Color.GREEN), 10, 13,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////获取Drawable资源
//        Drawable d = getResources().getDrawable(R.drawable.ic_thumb_up_black_48dp);
//        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
////创建ImageSpan
//        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
////用ImageSpan替换文本
//        ss.setSpan(span, 18, 19, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        textView.setText(ss);
//        textView.setMovementMethod(LinkMovementMethod.getInstance()); //实现文本的滚动

        //四、状态Drawable资源 （见state_drawable）（还有淡入淡出资源、图像级别资源）

        //五、在ImageView中显示图像的一部分
//        Bitmap sourceBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.r101);
//        Bitmap smallBitmap=Bitmap.createBitmap(sourceBitmap,200,200,300,300);
//        imageView.setImageBitmap(smallBitmap);
        //六、服务
//        Intent intent=new Intent(this,PlayService.class);
//        intent.putExtra("music","sdcard/music.mp3");
//        startService(intent);
//        bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                zjr = (PublicBussiness) service;
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//        },BIND_AUTO_CREATE);
//    }
//
//    public void start(View v){
//
//        zjr.Play();
//    }
//    public void pause(View v){
//        zjr.Pause();
//    }
//    public void stop(View v){
//        zjr.Stop();
//    }
        //七、利用反射，创建一个永不关闭的Toast
//        Toast toast=Toast.makeText(this,"No dismiss toast",Toast.LENGTH_SHORT);
//        try{
//            Field field=toast.getClass().getDeclaredField("mTN");
//            field.setAccessible(true);
//            Object obj=field.get(toast);
//            Method method=obj.getClass().getDeclaredMethod("show",null);
//            method.invoke(obj,null);
//        }catch (Exception e){
//            Log.e("error",e.toString());
//        }
        //八、PopupWindow对象显示弹出框
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View contentView = getLayoutInflater().inflate(R.layout.popup_window, null);
//                PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//                popupWindow.setTouchable(true);
//                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.r101));
//                popupWindow.showAsDropDown(v);
//            }
//        });
        //九、Notification
//        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Bitmap icon= BitmapFactory.decodeResource(getResources(),R.drawable.r101);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,getIntent(),0);
//        Notification notification=new Notification.Builder(this).setContentTitle("title").setContentText("text")
//                .setSmallIcon(R.drawable.ic_thumb_up_black_48dp).setContentIntent(pendingIntent)
//                .setLargeIcon(icon).build();
//        notificationManager.notify(R.drawable.r101,notification);

        //十、WebView
//        WebView webView= (WebView) findViewById(R.id.web_view);
//        String html="\n" +
//                "<html lang='zh-CN' xml:lang='zh-CN' xmlns='http://www.w3.org/1999/xhtml'>\n" +
//                "<a href=\"http://ac-6vnrci5o.clouddn.com/HkeeNRziYUNCLP4vEiL27VLU7OP5482xeE4VeDXI.apk\">Download:FangYanShuo V1.1</a>\n" +
//                "</html>";
////        webView.loadUrl("http://www.zhagame.com");
//        webView.loadDataWithBaseURL("zhagame", html, "text/html", "utf-8", null);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
    }
}
