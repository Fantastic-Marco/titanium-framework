package com.titanium.util.proxy;

import java.lang.reflect.Proxy;

public class ProxyMain {
    public static void main(String[] args) {
        MyCalculator calculator = (MyCalculator) Proxy.newProxyInstance(ProxyMain.class.getClassLoader(), new Class[]{MyCalculator.class}, new MyInvocationHandler());
        int addRes = calculator.add(2, 2);
        System.out.println("add executed: " + addRes);

        int subRes = calculator.sub(2, 2);
        System.out.println("sub executed: " + subRes);

        int mulRes = calculator.mul(2, 2);
        System.out.println("mul executed: " + mulRes);
    }
}
