package com.trojx.fangyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.fangyan.R;

/**
 * Created by Administrator on 2016/1/25.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText et_account;
    private EditText et_password;
    private ButtonRectangle bt_register;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        et_account = (EditText) findViewById(R.id.et_register_account);
        et_password = (EditText) findViewById(R.id.et_register_password);
        bt_register = (ButtonRectangle) findViewById(R.id.bt_register);

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle("注册");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));


        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    private void register(){
        String account=et_account.getText().toString();
        String password=et_password.getText().toString();
        AVUser avUser=new AVUser();
        avUser.setUsername(account);
        avUser.setPassword(password);

        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    //返回activity
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    intent.putExtra("account",et_account.getText().toString());
                    intent.putExtra("password",et_password.getText().toString());
                    finish();
                }else {
                    final Dialog dialog=new Dialog(RegisterActivity.this,"错误","注册失败，请检查你的账号与密码");
                    dialog.show();
                    dialog.getButtonAccept().setText("好的");
                    dialog.getButtonAccept().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });





    }
}
