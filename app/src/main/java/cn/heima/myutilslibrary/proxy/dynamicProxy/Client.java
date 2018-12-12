package cn.heima.myutilslibrary.proxy.dynamicProxy;

import java.lang.reflect.Proxy;

import cn.heima.myutilslibrary.proxy.Person;
import cn.heima.myutilslibrary.proxy.SendInterface;

public class Client {
    public static void main(String[] arg){

        //构造一个小明
        SendInterface person = new Person();

        //构造一个动态代理
        DynamicProxy proxy=new DynamicProxy(person);

//获取被代理的小明的ClassLoader
        ClassLoader classLoader = person.getClass().getClassLoader();

        //动态构造一个代理者
        SendInterface sendInterface = (SendInterface)Proxy.newProxyInstance(classLoader, new Class[]{SendInterface.class}, proxy);
        sendInterface.submit();
        sendInterface.sendMessage();
    }
}
