package com.mozzer.tests;

import com.mozzer.testrail.CustomTestRailReportListener;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

@Listeners(CustomTestRailReportListener.class)
public class BaseTest {
    @BeforeTest
    public void beforeMethod() {
        System.out.println("Starting Tests");
    }

    @AfterTest
    public void afterMethod() {
        System.out.println("Finished Tests");
    }
}
