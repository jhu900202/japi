package com.japi.config;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 단위 테스트 어노테이션
 *
 * @author 정현욱
 * @since 2025.09.12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("unit")
@ExtendWith({MockitoExtension.class, TestLoggingExtension.class})
public @interface UnitTest {
}