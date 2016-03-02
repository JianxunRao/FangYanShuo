package com.trojx.regularpractice.designpattern;

import android.util.Log;

import java.util.ArrayList;

/**观察者模式
 * Created by Administrator on 2016/2/28.
 */
public class ObserverPattern {
    public static void main(String[] args){
        Seller seller=new Seller();
        ChineseBuyer buyer0=new ChineseBuyer();
        seller.registerBuyers(buyer0);
        UsaBuyer buyer1=new UsaBuyer();
        seller.registerBuyers(buyer1);
        try {
            //模拟耗时的处理过程
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        seller.notifyBuyers();
    }
}

//观察者
class Seller{
    private ArrayList<Buyer> buyers=new ArrayList<>();
    public void registerBuyers(Buyer buyer){
        buyers.add(buyer);
        Log.e("又一台手机被预定啦","");
    }
    public void notifyBuyers(){
        for(Buyer buyer:buyers){
            buyer.receive();
        }
    }

}
class ChineseBuyer implements Buyer{

    @Override
    public void receive() {
        Log.e("中国买家买到手机啦！","");
    }
}
class UsaBuyer implements  Buyer{

    @Override
    public void receive() {
        Log.e("美国买家买到手机啦！","");
    }
}
interface Buyer{
    void receive();
}

