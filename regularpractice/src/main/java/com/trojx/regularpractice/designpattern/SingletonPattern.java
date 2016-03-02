package com.trojx.regularpractice.designpattern;

/**单例模式
* Created by Administrator on 2016/2/28.
        */
public class SingletonPattern {
    public static void main(String[] args){
        Singleton singleton=Singleton.getInstance();
    }
}
class Singleton  {
    private static Singleton singleton;
    public synchronized static  Singleton getInstance(){
        if(singleton==null){
            return new Singleton();
        }
        return singleton;
    }
}

