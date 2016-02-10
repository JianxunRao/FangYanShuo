package com.trojx.fangyan.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.fangyan.R;

/**
 * Created by Administrator on 2016/2/5.
 */
public class AboutActivity extends AppCompatActivity {

    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        AVObject version1=new AVObject("version");
//        version1.put("versionCode",1+"");
//        version1.put("versionFeature", "");
//        try {
//            AVFile file=AVFile.withAbsoluteLocalPath("FangYanShuo1.0.apk","sdcard/com.trojx.fangyan-1.apk");
//            version1.put("apkFile",file);
//            version1.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(AVException e) {
//                    if(e!=null){
//                        Log.e("apk",e.toString());
//                    }
//                }
//            });
//        } catch (IOException e) {
//            Log.e("e",e.toString());
//        }


        TextView tv_version= (TextView) findViewById(R.id.tv_about_version);
        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo info=packageManager.getPackageInfo(getPackageName(),0);
            version = info.versionName;
            tv_version.setText("版本号："+ version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("get version error",e.toString());
        }

        ButtonFlat bt_more= (ButtonFlat) findViewById(R.id.bt_about_more);
        bt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, MoreInfoActivity.class);
                startActivity(intent);
            }
        });
        ButtonFlat bt_update= (ButtonFlat) findViewById(R.id.bt_about_update);
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(AboutActivity.this, "获取更新");
                progressDialog.show();
                AVQuery<AVObject> query = new AVQuery<>("version");
                query.orderByDescending("versionCode");
                query.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        if (e == null) {
                            progressDialog.dismiss();
                            String versionCode = avObject.getString("versionCode");
                            final String apkUrl = avObject.getString("apkUrl");
                            String feature = avObject.getString("versionFeature");
                            if (!versionCode.equals(version)) {//
                                final Dialog dialog = new Dialog(AboutActivity.this, "新版本", feature);
                                dialog.show();
                                dialog.getButtonAccept().setText("更新");
                                dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                final Dialog dialog = new Dialog(AboutActivity.this, "暂无更新", "当前已是最新版本！");
                                dialog.show();
                                dialog.getButtonAccept().setText("好的");
                                dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            Log.e("get update error", e.toString());
                            progressDialog.dismiss();
                            final Dialog dialog=new Dialog(AboutActivity.this,"错误","获取更新失败，请检查网络设置然后再试。");
                            dialog.show();
                            dialog.getButtonAccept().setText("好的");
                            dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
