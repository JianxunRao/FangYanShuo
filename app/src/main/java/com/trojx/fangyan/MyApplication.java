package com.trojx.fangyan;

import android.app.Application;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.trojx.fangyan.activity.UpdateActivity;

/**
 * Created by Administrator on 2016/1/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        AVOSCloud.useAVCloudUS();//
        AVOSCloud.initialize(this, "6vNrCi5ou4rw5sb0fx8b0J4w-gzGzoHsz", "zfuBYXx8X235VVi6O7acOM8G");
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });
        PushService.setDefaultPushCallback(this, UpdateActivity.class);
    }
}
