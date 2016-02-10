package com.trojx.fangyan.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.trojx.fangyan.R;

/**
 * Created by Administrator on 2016/2/8.
 */
public class SettingActivity extends AppCompatActivity {

    private AVUser avUser;
//    private ButtonRectangle bt_login;
    private RelativeLayout rl_name;
    private RelativeLayout rl_about;
    private TextView tv_name;
    private Toolbar toolbar;
    public static final int REQUEST_LOGIN=1;
    private TextView tv_version;
    private RelativeLayout rl_lang_pref;
    private static final String[] langs={"全部方言","粤语","赣语","客家语","晋语","汉语","闽东语","闽南语","吴语","湘语"};
    private TextView tv_lang_pref;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("lang_pref",MODE_PRIVATE);
        final SharedPreferences.Editor editor= sp.edit();
        int lang_pref= sp.getInt("lang_pref", -1);

        toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
        rl_name = (RelativeLayout) findViewById(R.id.rl_setting_name);
        rl_about = (RelativeLayout) findViewById(R.id.rl_setting_about);
        rl_lang_pref = (RelativeLayout) findViewById(R.id.rl_setting_lang_pref);
        tv_name = (TextView) findViewById(R.id.tv_setting_user_name);
//        bt_login = (ButtonRectangle) findViewById(R.id.bt_setting_login);
        tv_version = (TextView) findViewById(R.id.tv_setting_version);
        tv_lang_pref = (TextView) findViewById(R.id.tv_setting_lang_pref);
        if(lang_pref!=-1){
            tv_lang_pref.setText(langs[lang_pref]);
        }

        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        avUser = AVUser.getCurrentUser();
        if(avUser !=null){
            tv_name.setText(avUser.getUsername());
//            bt_login.setText("注销");
        }

        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            tv_version.setText("方言说 "+packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        rl_lang_pref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
                int i=sp.getInt("lang_pref",-1);
                builder.setSingleChoiceItems(langs,i, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_lang_pref.setText(langs[which]);
                        dialog.dismiss();
                        editor.putInt("lang_pref",which);
                        editor.commit();
                    }
                });
                builder.show();
            }
        });

//        bt_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                avUser=AVUser.getCurrentUser();
//                if (avUser != null) {
//                    avUser.logOut();
//                    tv_name.setText("未登录");
//                    bt_login.setText("登录");
//                }else {
//                    Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
//                    startActivityForResult(intent,REQUEST_LOGIN);
//                }
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_LOGIN&&resultCode==RESULT_OK){
//            bt_login.setText("注销");
            tv_name.setText(AVUser.getCurrentUser().getUsername());
        }
    }

    @Override
    public void finish() {
        super.finish();
        setResult(RESULT_OK);
    }
}
