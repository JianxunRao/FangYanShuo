package com.trojx.fangyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.fangyan.R;

/**
 * Created by Administrator on 2016/1/24.
 */
public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et_account;
    private EditText et_password;
    private ButtonFlat bt_toReg;
    private ButtonRectangle bt_login;
    private static final int REQUEST_REG=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        et_account = (EditText) findViewById(R.id.et_login_account);
        et_password = (EditText) findViewById(R.id.et_login_password);
        bt_toReg = (ButtonFlat) findViewById(R.id.bt_login2reg);
            bt_toReg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        bt_login = (ButtonRectangle) findViewById(R.id.bt_login);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setTitle("登录");



        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        bt_toReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REG);
            }
        });

    }

    /**
     * 登录
     */
    private void login() {
        String account=et_account.getText().toString();
        String password=et_password.getText().toString();
        if(account.isEmpty()||password.isEmpty()){
            final Dialog dialog=new Dialog(LoginActivity.this,"错误","请正确填写用户名与密码");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
            dialog.getButtonAccept().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButtonAccept().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_password.setText("");
                    dialog.dismiss();
                }
            });
        }else {
            AVUser.logInInBackground(account, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e!=null){
                        Log.e("login error", e.toString());
                        final Dialog dialog=new Dialog(LoginActivity.this,"错误","登录失败，请检查输入的账号与密码");
                        dialog.show();
                        dialog.getButtonAccept().setText("好的");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                    }else {
                        Log.e("login success","login success");
                        Intent intent=new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_REG){
            switch (resultCode){
                case RESULT_OK:
                    String account=data.getStringExtra("account");
                    String password=data.getStringExtra("password");
                    et_account.setText(account);
                    et_password.setText(password);
                    login();
                    break;
                default:
                    break;
            }
        }

    }
}
