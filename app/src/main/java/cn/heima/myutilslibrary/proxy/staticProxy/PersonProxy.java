package cn.heima.myutilslibrary.proxy.staticProxy;

import cn.heima.myutilslibrary.proxy.SendInterface;

public class PersonProxy implements SendInterface {
    private SendInterface sendInterface;//持有一个具体被代理者的引用

    public PersonProxy(SendInterface sendInterface) {
        this.sendInterface = sendInterface;
    }

    @Override
    public void submit() {
        sendInterface.submit();
    }

    @Override
    public void sendMessage() {
        sendInterface.sendMessage();
    }
}
