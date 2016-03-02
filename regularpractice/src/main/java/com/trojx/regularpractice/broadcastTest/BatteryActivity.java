package com.trojx.regularpractice.broadcastTest;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**发送一条自定义广播
 * Created by Administrator on 2016/2/27.
 */
public class BatteryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent("com.trojx.regularpractice.testbroadcast");
        sendBroadcast(intent);

        //动态注册广播接收者
        registerReceiver(DynamicReceiver.getInstance(), new IntentFilter("com.trojx.regularpractice.dynamicbroadcast"));
        Intent intent1=new Intent("com.trojx.regularpractice.dynamicbroadcast");
        sendBroadcast(intent1);
    }
}
