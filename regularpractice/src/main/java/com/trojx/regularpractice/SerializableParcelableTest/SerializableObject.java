package com.trojx.regularpractice.SerializableParcelableTest;

import java.io.Serializable;

/**Serializable 使用实例
 * Created by Administrator on 2016/2/27.
 */
public class SerializableObject implements Serializable {
    private  int key;
    private String value;
    public SerializableObject(int key,String value){
        this.key=key;
        this.value=value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
