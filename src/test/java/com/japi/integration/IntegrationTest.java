package com.japi.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeTags("integration")
@SelectPackages("com.japi.integration")
@DisplayName("모든 통합 테스트 실행")
public class IntegrationTest {
}
