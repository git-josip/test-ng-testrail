package com.mozzer.tests;

import com.mozzer.testrail.TestRailCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.lang.reflect.Method;

public class DashboardTest extends BaseTest {

    @TestRailCase(id = 2885)
    @Test()
    public void test1(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 2886)
    @Test()
    public void test2(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 2887)
    @Test()
    public void test3(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 2888)
    @Test()
    public void test4(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 2889)
    @Test()
    public void test5(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 2890)
    @Test()
    public void test6(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 2891)
    @Test()
    public void test7(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }
}
