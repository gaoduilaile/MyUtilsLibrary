package cn.heima.myutilslibrary.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 */
public class DynamicProxy implements InvocationHandler{
    private Object obj;//被代理的类的引用

    public DynamicProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        //调用被代理类的对象的方法
        Object invoke = method.invoke(obj, objects);
        return invoke;
    }
}
