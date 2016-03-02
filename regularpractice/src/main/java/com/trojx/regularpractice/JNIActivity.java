package com.trojx.regularpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Administrator on 2016/2/21.
 */
public class JNIActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NdkJniUtils jni=new NdkJniUtils();
        Log.e("jni",jni.getCLanguageString());
    }
}
