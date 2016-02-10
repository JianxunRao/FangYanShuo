package com.trojx.fangyan.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.fangyan.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/28.
 */
public class SplashActivity extends AppCompatActivity {
    private boolean networkOk=false;
    private int timeCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        TextView tv_version= (TextView) findViewById(R.id.tv_splash_version);
        try {
            PackageManager packageManager=getPackageManager();
            PackageInfo info=packageManager.getPackageInfo(getPackageName(),0);
            String version=info.versionName;
            tv_version.setText("Version:"+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AVQuery<AVObject> query=new AVQuery<>("word");
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if(e!=null){
                    final Dialog dialog=new Dialog(SplashActivity.this,"错误","没有可用的网络连接，请检查网络设置后稍后再试");
                    dialog.show();
                    dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    dialog.getButtonAccept().setText("好的");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    Log.e("",e.toString());
                }else {
                    networkOk=true;
                }
            }
        });
        final Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                timeCount++;
                if(timeCount==3&&networkOk){
                    timer.cancel();
                    Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.schedule(task,0,1000);
    }

}
