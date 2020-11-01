package com.mozzer.tests;

import com.mozzer.testrail.TestRailCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class LoginTest extends BaseTest {
    @TestRailCase(id = 280)
    @Test()
    public void test1(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 281)
    @Test()
    public void test2(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }

    @TestRailCase(id = 282)
    @Test()
    public void test3(Method method) {
        Assert.assertTrue(Math.random() >= 0.5);
    }
}
