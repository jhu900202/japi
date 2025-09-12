package com.japi.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeTags("unit")
@SelectPackages("com.caloriebar.caloriebar.unit")
@DisplayName("모든 단위 테스트 실행")
public class UnitTest {
}
