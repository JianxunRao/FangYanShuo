package com.trojx.regularpractice;

/**
 * Created by Administrator on 2016/2/21.
 */
public class NdkJniUtils {
    static {
        System.loadLibrary("JniLibName");//装载的库名，与build.gradle中指定的要相同
    }
    public native  String getCLanguageString();
}
