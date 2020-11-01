package com.mozzer.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;

@LoadPolicy(LoadType.MERGE)
@Config.Sources({ "classpath:config.properties" })
public interface Configuration extends Config {
    @Key("testRailUrl")
    String testRailUrl();

    @Key("testRailUser")
    String testRailUser();

    @Key("testRailPassword")
    String testRailPassword();

    @Key("testRailProjectId")
    int testRailProjectId();

    @Key("testRailSuiteId")
    int testRailSuiteId();
}
