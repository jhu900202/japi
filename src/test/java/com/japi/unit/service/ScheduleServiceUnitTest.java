package com.japi.unit.service;

import com.caloriebar.caloriebar.config.CaloriebarUnitTest;
import com.caloriebar.caloriebar.dto.db.TblLoginMstDto;
import com.caloriebar.caloriebar.dto.response.CareScheduleHolidayDto;
import com.caloriebar.caloriebar.exception.ErrorCode;
import com.caloriebar.caloriebar.exception.UserException;
import com.caloriebar.caloriebar.fixtures.ScheduleFixtures;
import com.caloriebar.caloriebar.fixtures.UserFixtures;
import com.caloriebar.caloriebar.mapper.ScheduleMapper;
import com.caloriebar.caloriebar.service.ScheduleService;
import com.caloriebar.caloriebar.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@CaloriebarUnitTest
@DisplayName("스케줄 서비스 단위 테스트")
class ScheduleServiceUnitTest {

  @Mock
  private ScheduleMapper scheduleMapper;

  @InjectMocks
  private ScheduleService scheduleService;

  @Test
  @DisplayName("휴무일 수정 - 관리자 정상 플로우")
  void modifyHolidayList_AdminUser_Success() {

    // TODO: 정현욱 아래 오류 디버깅, 여기부터

    /*
    17:58:00.378 [main] DEBUG c.c.c.config.TestLoggingExtension - [modifyHolidayList_AdminUser_Success] 테스트 시작: modifyHolidayList_AdminUser_Success
17:58:00.565 [main] INFO  c.c.c.service.ScheduleService - [modifyHolidayList_AdminUser_Success] 휴무일 수정 시작 - 사용자: 1
17:58:00.575 [main] WARN  c.c.c.service.ScheduleService - [modifyHolidayList_AdminUser_Success] 휴무일 리스트에 중복 데이터 존재
17:58:00.583 [main] DEBUG c.c.c.config.TestLoggingExtension - [modifyHolidayList_AdminUser_Success] 테스트 완료: modifyHolidayList_AdminUser_Success

org.opentest4j.AssertionFailedError: Unexpected exception thrown: com.caloriebar.caloriebar.exception.UserException: 잘못된 요청입니다.

	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:152)
	at org.junit.jupiter.api.AssertDoesNotThrow.createAssertionFailedError(AssertDoesNotThrow.java:84)
	at org.junit.jupiter.api.AssertDoesNotThrow.assertDoesNotThrow(AssertDoesNotThrow.java:53)
	at org.junit.jupiter.api.AssertDoesNotThrow.assertDoesNotThrow(AssertDoesNotThrow.java:36)
	at org.junit.jupiter.api.Assertions.assertDoesNotThrow(Assertions.java:3199)
	at com.caloriebar.caloriebar.unit.service.ScheduleServiceUnitTest.lambda$modifyHolidayList_AdminUser_Success$1(ScheduleServiceUnitTest.java:54)
	at com.caloriebar.caloriebar.fixtures.UserFixtures.withUserLogin(UserFixtures.java:117)
	at com.caloriebar.caloriebar.unit.service.ScheduleServiceUnitTest.modifyHolidayList_AdminUser_Success(ScheduleServiceUnitTest.java:42)
	at java.base/java.lang.reflect.Method.invoke(Method.java:569)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
Caused by: com.caloriebar.caloriebar.exception.UserException: 잘못된 요청입니다.
	at com.caloriebar.caloriebar.service.ScheduleService.validateHolidayData(ScheduleService.java:340)
	at com.caloriebar.caloriebar.service.ScheduleService.modifyHolidayList(ScheduleService.java:211)
	at com.caloriebar.caloriebar.unit.service.ScheduleServiceUnitTest.lambda$modifyHolidayList_AdminUser_Success$0(ScheduleServiceUnitTest.java:54)
	at org.junit.jupiter.api.AssertDoesNotThrow.assertDoesNotThrow(AssertDoesNotThrow.java:49)
	... 8 more


     */

    UserFixtures.withUserLogin(UserFixtures.adminUser(), () -> {
      // Given
      List<CareScheduleHolidayDto> updateParamList = ScheduleFixtures.createUpdateParamList(LocalDate.now());
      TblLoginMstDto loginInfo = UserUtils.getCurrentUserDetails().getLoginInfo();

      String startDate = updateParamList.get(0).getHolidayDate();
      String endDate = updateParamList.get(updateParamList.size() - 1).getHolidayDate();

      doNothing().when(scheduleMapper).deleteHolidayList(loginInfo.getBranchCd(), startDate, endDate);
      doNothing().when(scheduleMapper).insertHolidayList(updateParamList);

      // When
      assertDoesNotThrow(() -> scheduleService.modifyHolidayList(updateParamList));

      // Then
      InOrder inOrder = inOrder(scheduleMapper);
      inOrder.verify(scheduleMapper, times(1)).deleteHolidayList(loginInfo.getBranchCd(), startDate, endDate);
      inOrder.verify(scheduleMapper, times(1)).insertHolidayList(updateParamList);
    });
  }

  @Test
  @DisplayName("휴무일 수정 - 권한 검증 실패 (빈 리스트)")
  void modifyHolidayList_EmptyList_ThrowsException() {
    UserFixtures.withUserLogin(UserFixtures.adminUser(), () -> {
      // Given
      List<CareScheduleHolidayDto> emptyList = Collections.emptyList();

      // When
      UserException exception = assertThrows(UserException.class,
          () -> scheduleService.modifyHolidayList(emptyList));

      // Then
      assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

      verify(scheduleMapper, never()).deleteHolidayList(any(), any(), any());
      verify(scheduleMapper, never()).insertHolidayList(any());
    });
  }

  @Test
  @DisplayName("휴무일 수정 - 권한 검증 실패 (다른 지점 데이터)")
  void modifyHolidayList_DifferentBranch_ThrowsException() {
    UserFixtures.withUserLogin(UserFixtures.managerUser(), () -> { // 일반 사용자
      // Given
      List<CareScheduleHolidayDto> holidays = ScheduleFixtures.createUpdateParamList(LocalDate.now());
      holidays.forEach(holiday -> holiday.setBranchCd("DIFFERENT_BRANCH")); // 다른 지점

      // When
      UserException exception = assertThrows(UserException.class,
          () -> scheduleService.modifyHolidayList(holidays));

      // Then
      assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

      verify(scheduleMapper, never()).deleteHolidayList(any(), any(), any());
      verify(scheduleMapper, never()).insertHolidayList(any());
    });
  }

  @Test
  @DisplayName("휴무일 수정 - 데이터 검증 실패 (잘못된 일수)")
  void modifyHolidayList_InvalidData_ThrowsException() {
    UserFixtures.withUserLogin(UserFixtures.adminUser(), () -> {
      // Given
      List<CareScheduleHolidayDto> invalidHolidays = new ArrayList<>();
      for (int i = 1; i <= 31; i++) { // 9월은 30일까지
        invalidHolidays.add(CareScheduleHolidayDto.builder()
            .branchCd("tw004")
            .holidayDate(String.format("2025-09-%02d", i))
            .holidayTime("1000")
            .isHoliday("false")
            .build());
      }

      // When
      UserException exception = assertThrows(UserException.class,
          () -> scheduleService.modifyHolidayList(invalidHolidays));

      // Then
      assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());

      verify(scheduleMapper, never()).deleteHolidayList(any(), any(), any());
      verify(scheduleMapper, never()).insertHolidayList(any());
    });
  }

}
