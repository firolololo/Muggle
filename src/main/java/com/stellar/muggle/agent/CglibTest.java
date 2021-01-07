package com.stellar.muggle.agent;

import com.stellar.muggle.Firo;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 * @author firo
 * @version 1.0
 * @date 2021/1/7 17:50
 */
public class CglibTest {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Firo.class);
//        enhancer.setCallback(new FixedValue() {
//            @Override
//            public Object loadObject() throws Exception {
//                return "Hello Cglib";
//            }
//        });
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println("before method run...");
                Object result = proxy.invokeSuper(obj, args);
                System.out.println("after method run...");
                return result;
            }
        });
        Firo firo = (Firo)enhancer.create();
        System.out.println(firo.getClass());
//        Firo firo = new Firo();
        firo.sayHello();
//        System.out.println(firo.toString());
    }
}
