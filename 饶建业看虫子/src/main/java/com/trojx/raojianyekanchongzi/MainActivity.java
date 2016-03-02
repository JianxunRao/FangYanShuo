package com.trojx.raojianyekanchongzi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView kcz;
    private int i=0;
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int y=msg.what;
            StringBuffer sb=new StringBuffer();
            for(int x=0;x<y%10;x++){
//                sb.append(" ");
            }
            sb.append(y);
            sb.append("《饶建业看虫子》");
            kcz.setText(sb.toString());
            if(y%2==0){
                iv.setImageResource(R.drawable.rjy_kcz);
            }else {
                iv.setImageResource(R.drawable.rjx_kcz2);
            }
            return false;
        }
    });
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.text_view);
        iv = (ImageView) findViewById(R.id.imageView);
        kcz = (TextView) findViewById(R.id.kcz);
        StringBuilder sb=new StringBuilder();
        for (int x = 0; x < 100; x++) {
            for(int y=0;y<x%10;y++){
//                sb.append(" ");
            }
            sb.append(x+"饶建业，你真逗\n");
            tv.setText(sb.toString());
        }
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                i++;
                Message msg=new Message();
                msg.what=i;
                handler.sendMessage(msg);
            }
        };
        Timer timer=new Timer();
        timer.schedule(task,0,1000);
    }
}