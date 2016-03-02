package com.trojx.regularpractice.broadcastTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**动态广播接收者
 * Created by Administrator on 2016/2/27.
 */
public class DynamicReceiver extends BroadcastReceiver {
    private static DynamicReceiver instance;

    public synchronized static DynamicReceiver getInstance(){
        if(instance==null){
            return new DynamicReceiver();
        }
        return instance;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"动态接收者接收到广播",Toast.LENGTH_SHORT).show();
    }
}
