package com.livable.server.reservation.controller;

import com.livable.server.reservation.dto.ReservationResponse;
import com.livable.server.reservation.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ReservationService reservationService;

    @DisplayName(
            "[GET][/api/reservation/places/{commonPlaceId}?date={yyyy-MM-dd}] - 특정 회의실의 사용 가능 시간 응답(예약해둔 시간)"
    )
    @Test
    void findAvailableTimesSuccessTest() throws Exception {
        // given
        List<ReservationResponse.AvailableReservationTimePerDateDto> result = IntStream.range(1, 10)
                .mapToObj(idx -> ReservationResponse.AvailableReservationTimePerDateDto.builder()
                        .date(LocalDate.now())
                        .availableTimes(new ArrayList<>(List.of(LocalTime.now())))
                        .build()
                )
                .collect(Collectors.toList());

        given(reservationService.findAvailableReservationTimes(anyLong(), anyLong(), any(LocalDate.class)))
                .willReturn(result);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/reservation/places/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("date", "2023-09-22")
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].date").isString())
                .andExpect(jsonPath("$.data[0].availableTimes").isArray());
    }
}