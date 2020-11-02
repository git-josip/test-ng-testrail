package com.mozzer.testrail.reporterdto;

import com.google.common.collect.Lists;
import com.mozzer.testrail.TestRailCase;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ReporterDtoUtils {
    public static List<TestRunData> extractTestRunData(ISuite suite) {
        List<TestRunData> allTestRunData = Lists.newArrayList();
        suite.getResults().forEach((key, value) -> {
            ITestContext context = value.getTestContext();

            TestRunData trd = TestRunData.builder()
                    .name(key)
                    .testMethodData(Lists.newArrayList())
                    .build();

            trd.getTestMethodData().addAll(createTestMethodData(context.getPassedTests().getAllResults(), TestRailStatus.PASSED));
            trd.getTestMethodData().addAll(createTestMethodData(context.getFailedTests().getAllResults(), TestRailStatus.FAILED));
            trd.getTestMethodData().addAll(createTestMethodData(context.getSkippedTests().getAllResults(), TestRailStatus.UNTESTED));

            allTestRunData.add(trd);
        });

        return allTestRunData;
    }

    public static List<TestMethodData> createTestMethodData(Set<ITestResult> results, TestRailStatus status) {
        List<TestMethodData> resultBuilder = Lists.newArrayList();

        results.forEach(element -> {
            Method method = element.getMethod().getConstructorOrMethod().getMethod();
            TestRailCase trCase = method.getAnnotation(TestRailCase.class);
            int testRailId = trCase.id();

            TestMethodData.TestMethodDataBuilder tmd = TestMethodData.builder()
                    .testRailId(testRailId)
                    .methodName(element.getName())
                    .testClassName(element.getInstanceName())
                    .status(status);

            if(Objects.nonNull(element.getThrowable())) {
                tmd.stackTrace(ExceptionUtils.getStackTrace(element.getThrowable()));
            }

            resultBuilder.add(tmd.build());
        });

        return resultBuilder;
    }
}
