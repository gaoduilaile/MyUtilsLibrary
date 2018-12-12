package cn.heima.myutilslibrary.proxy.staticProxy;

import cn.heima.myutilslibrary.proxy.Person;
import cn.heima.myutilslibrary.proxy.SendInterface;

public class Client {

    public static void main(String[] arg){
        SendInterface person = new Person();//构造一个小明
        SendInterface sendInterface = new PersonProxy(person);//构造一个代理并将小明作为构造参数传递进去
        sendInterface.submit();
        sendInterface.sendMessage();
    }

}
