package com.trojx.regularpractice.designpattern;

import android.util.Log;

/**工厂模式
 * Created by Administrator on 2016/2/28.
 */
public class FatcoryPattern {
    public static  void main(String[]args){
        Location location=LocationFactory.getInstance("谷歌");
        location.getPosition();
        location.getCityName(121,30);
    }
}
class LocationFactory{
    public static Location getInstance(String type){
        if(type.equals("百度")){
            return new BaiduLocation();
        }else {
            return new GoogleLocation();
        }
    }
}
class BaiduLocation implements Location{

    @Override
    public void getPosition() {
        Log.e("","根据百度地图获取位置");
    }

    @Override
    public void getCityName(int lat,int lng) {
        Log.e("","根据百度地图获取城市");
    }
}
class GoogleLocation implements  Location{

    @Override
    public void getPosition() {
        Log.e("","根据谷歌地图获取位置");
    }

    @Override
    public void getCityName(int lat, int lng) {
        Log.e("","根据谷歌地图获取城市");
    }
}
interface Location{
    void getPosition();
    void getCityName(int lat,int lng);
}