package com.trojx.regularpractice.broadcastTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**广播接收者实例
 * Created by Administrator on 2016/2/27.
 */
public class BatteryReceiver extends BroadcastReceiver {
    /**
     * 弹出Toast显示电池三种状态广播
     * @param context 广播接收者运行的上下文
     * @param intent 传入的intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case Intent.ACTION_BATTERY_CHANGED:
                Toast.makeText(context,"电池电量改变",Toast.LENGTH_SHORT).show();
                break;
            case Intent.ACTION_BATTERY_LOW:
                Toast.makeText(context,"电池电量低",Toast.LENGTH_SHORT).show();
                break;
            case  Intent.ACTION_BATTERY_OKAY:
                Toast.makeText(context,"电池电量恢复",Toast.LENGTH_SHORT).show();
                break;
            case "com.trojx.regularpractice.testbroadcast":
                Toast.makeText(context,"接收到自定义广播",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
