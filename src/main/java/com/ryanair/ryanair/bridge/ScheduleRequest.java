package com.ryanair.ryanair.bridge;

import java.time.LocalDateTime;

import com.ryanair.ryanair.model.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ScheduleRequest {

  public static final String RYAN_AIR = "RYANAIR";

  @Value("${BASE_RYANAIR_API_URL}")
  private String BASE_RYANAIR_API_URL;

  public Schedule getSchedules(
      String departure, String arrival, LocalDateTime departureDate, LocalDateTime arrivalDate) {
    var webClient = WebClient.create(BASE_RYANAIR_API_URL);

    var schedule =
        webClient
            .get()
            .uri(
                "/timtbl/3/schedules/"
                    .concat(departure)
                    .concat("/")
                    .concat(arrival)
                    .concat("/years/")
                    .concat(String.valueOf(departureDate.getYear()))
                    .concat("/months/")
                    .concat(String.valueOf(departureDate.getMonthValue())))
            .retrieve()
            .toEntity(Schedule.class)
            .block();

    schedule.getBody();

    return schedule.getBody();
  }
}
