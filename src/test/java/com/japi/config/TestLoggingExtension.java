package com.japi.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

import java.lang.reflect.Method;

/**
 * 테스트 로깅 공통 확장
 *
 * @author 정현욱
 * @since 2025.09.12
 */
@Slf4j
public class TestLoggingExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    String methodName = context.getTestMethod()
        .map(Method::getName)
        .orElse("UNKNOWN");

    MDC.put("testMethod", methodName);
    log.debug("테스트 시작: {}", methodName);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    String methodName = context.getTestMethod()
        .map(Method::getName)
        .orElse("UNKNOWN");

    log.debug("테스트 완료: {}", methodName);
    MDC.clear();
  }
}
