package cn.krvision.toolmodule.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gaoqiong on 2019/1/7 16:53
 * Description:注解
 */

/*@interface就是注解类的标志性声明了（注意，类成员只能是以下几种：基本数据类型、Class类型、枚举类型
元注解：
@Retention：指定的注解作用范围
值：
RetentionPolicy.SOURCE  java源码范围可见
RetentionPolicy.CLASS .class字节码可见
RetentionPolicy.RUNTIME 运行的时候都可见

@Target：代表定义的注解修饰范围(属性，方法，类)
ElementType.TYPE：注解修饰类
ElementType.METHOD：注解修饰方法
ElementType.FILED：注解修饰属性*/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyInject {

    int value();

}
