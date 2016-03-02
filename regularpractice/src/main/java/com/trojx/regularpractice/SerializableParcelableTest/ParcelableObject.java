package com.trojx.regularpractice.SerializableParcelableTest;

import android.os.Parcel;
import android.os.Parcelable;

/**Parcelable 使用实例
 * Created by Administrator on 2016/2/27.
 */
public class ParcelableObject implements Parcelable {
    private  int key;
    private  String value;
    public ParcelableObject(int key,String value) {
        this.key=key;
        this.value=value;
    }
    //内容接口描述，默认返回零
    @Override
    public int describeContents() {
        return 0;
    }

    //将对象写入外部提供的Parcel中
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(value);
    }

    //其中public static final一个都不能少，内部对象CREATOR的名称也不能改变，必须全部大写
    public static final Creator<ParcelableObject> CREATOR = new Creator<ParcelableObject>() {
        //将Parcel反序列化为单个对象
        @Override
        public ParcelableObject createFromParcel(Parcel in) {
            return new ParcelableObject(in.readInt(),in.readString());
        }
        //供外部类反序列化本类的数组时使用，每一项初始值为null
        @Override
        public ParcelableObject[] newArray(int size) {
            return new ParcelableObject[size];
        }
    };
}
