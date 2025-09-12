package com.japi.integration.service;

import com.caloriebar.caloriebar.config.CaloriebarIntegrationTest;
import com.caloriebar.caloriebar.dto.response.CareScheduleHolidayDto;
import com.caloriebar.caloriebar.fixtures.ScheduleFixtures;
import com.caloriebar.caloriebar.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
@CaloriebarIntegrationTest
@DisplayName("스케줄 서비스 테스트")
class ScheduleServiceIntegrationTest {

  @Autowired
  private ScheduleService scheduleService;  // 실제 Bean
  // @Autowired
  // private ScheduleMapper scheduleMapper;    // 실제 Mapper
  // @Autowired
  // private ValidateUtils validateUtils;      // 실제 Utils

  @Test
  @DisplayName("휴무일 수정 - 전체 프로세스 통합 테스트")
  @WithMockUser(username = "admin", authorities = "ROLE_SUPERADMIN")
  void modifyHolidayList_Integration_Success() {
    // Given - 실제 데이터 준비
    List<CareScheduleHolidayDto> holidays = ScheduleFixtures.createUpdateParamList(LocalDate.now());

    // When - 전체 메서드 실행 (실제 DB 연동)
    assertDoesNotThrow(() -> scheduleService.modifyHolidayList(holidays));

    // Then - 실제 DB에서 결과 확인
    // List<CareScheduleHolidayDto> savedHolidays = scheduleMapper.getHolidayList("tw004");

    // assertEquals(holidays.size(), savedHolidays.size());
    // assertTrue(savedHolidays.stream().allMatch(h -> h.getBranchCd().equals("tw004")));
  }


}