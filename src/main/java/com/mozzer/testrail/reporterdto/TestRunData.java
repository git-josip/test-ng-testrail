package com.mozzer.testrail.reporterdto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TestRunData {
    private String name;
    private List<TestMethodData> testMethodData;
}
