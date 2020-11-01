package com.mozzer.testrail.reporterdto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestMethodData {
    private TestRailStatus status;
    private int testRailId;
    private String methodName;
    private String testClassName;
    private String stackTrace;
}
