package com.mozzer.testrail;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Plan;
import com.codepine.api.testrail.model.Result;
import com.codepine.api.testrail.model.ResultField;
import com.google.common.collect.Lists;
import com.mozzer.config.ConfigurationManager;
import com.mozzer.testrail.reporterdto.TestMethodData;
import com.mozzer.testrail.reporterdto.TestRailStatus;
import com.mozzer.testrail.reporterdto.TestRunData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.*;
import org.testng.xml.XmlSuite;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CustomTestRailReportListener implements IReporter{
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        suites.forEach(suite->{
            // One suite is mapped to 1 test PLAN on testRail.
            List<TestRunData> testRunData = extractTestRunData(suite);
            reportResults(testRunData);
        });
    }

    public List<TestRunData> extractTestRunData(ISuite suite) {
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

    public List<TestMethodData> createTestMethodData(Set<ITestResult> results, TestRailStatus status) {
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

    public static void reportResults( List<TestRunData> testRunData) {
        TestRail testRail = TestRail.builder(
                ConfigurationManager.getConfiguration().testRailUrl(),
                ConfigurationManager.getConfiguration().testRailUser(),
                ConfigurationManager.getConfiguration().testRailPassword()
        ).build();

        Plan plan = createTestRailPlan(testRail);

        handleTestRunReporting(testRunData, testRail, plan);

        testRail.plans().close(plan.getId()).execute();
    }

    private static void handleTestRunReporting(List<TestRunData> testRunData, TestRail testRail, Plan plan) {
        testRunData.forEach(trd -> {
            int testRailRun = createTestRailRun(testRail, plan, trd);
            List<ResultField> customResultFields = testRail.resultFields().list().execute();

            List<Result> testRailResultsMapped = trd.getTestMethodData().stream()
                    .map(md -> {
                        Result result = new Result()
                                .setTestId(md.getTestRailId())
                                .setStatusId(md.getStatus().getId())
                                .setCaseId(md.getTestRailId());

                        if(Objects.nonNull(md.getStackTrace())) {
                            result.addCustomField("stacktrace", md.getStackTrace());
                        }

                        return result;
                    }).collect(Collectors.toList());

            testRail.results().addForCases(testRailRun, testRailResultsMapped, customResultFields).execute();
        });
    }

    private static Plan createTestRailPlan(TestRail testRail) {
        Plan plan = new Plan();
        plan.setName(String.format("Test Plan - '%s'", SIMPLE_DATE_FORMAT.format(new Date())));
        plan.setProjectId(ConfigurationManager.getConfiguration().testRailProjectId());

        Plan createdPlan = testRail.plans().add(ConfigurationManager.getConfiguration().testRailProjectId(), plan).execute();
        createdPlan.setEntries(new ArrayList<>());

        return createdPlan;
    }

    private static int createTestRailRun(TestRail testRail, Plan plan, TestRunData testRunData) {
        Plan.Entry.Run run1 = new Plan.Entry.Run();
        run1.setIncludeAll(false)
                .setSuiteId(ConfigurationManager.getConfiguration().testRailSuiteId())
                .setCaseIds(
                        testRunData.getTestMethodData().stream()
                                .map(TestMethodData::getTestRailId)
                                .collect(Collectors.toList())
                );

        Plan.Entry runEntry = new Plan.Entry()
                .setName(String.format("Run - '%s' - '%s'", SIMPLE_DATE_FORMAT.format(new Date()), testRunData.getName()))
                .setSuiteId(ConfigurationManager.getConfiguration().testRailSuiteId())
                .setIncludeAll(false)
                .setCaseIds(
                        testRunData.getTestMethodData().stream()
                                .map(TestMethodData::getTestRailId)
                                .collect(Collectors.toList())
                ).setRuns(Lists.newArrayList(run1));

        Plan retrievedPlan = testRail.plans().get(plan.getId()).execute();
        Plan.Entry thisRunEntry = testRail.plans().addEntry(retrievedPlan.getId(), runEntry).execute();

        return thisRunEntry.getRuns().get(0).getId();
    }
}