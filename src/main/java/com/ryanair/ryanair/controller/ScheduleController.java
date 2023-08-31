package com.ryanair.ryanair.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.ryanair.ryanair.bridge.ScheduleRequest;
import com.ryanair.ryanair.model.Schedule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api")
public class ScheduleController {

  private final ScheduleRequest scheduleRequest;

  public ScheduleController(ScheduleRequest scheduleRequest) {
    this.scheduleRequest = scheduleRequest;
  }

  @GetMapping("v1/schedule")
  public Schedule getRoutesWithParams(
      @RequestParam(required = false) String departure,
      @RequestParam(required = false) String arrival,
      @RequestParam(required = false) String departureDateTime,
      @RequestParam(required = false) String arrivalDateTime) {

    System.out.println("departure: " + departure);
    System.out.println("arrival: " + arrival);
    System.out.println("departureDateTime: " + departureDateTime);
    System.out.println("arrivalDateTime: " + arrivalDateTime);

    LocalDateTime departureDate;
    LocalDateTime arrivalDate;

    try {
      departureDate = LocalDateTime.parse(departureDateTime);
    } catch (DateTimeParseException error) {
      throw error;
    }

    try {
      arrivalDate = LocalDateTime.parse(arrivalDateTime);
    } catch (DateTimeParseException error) {
      throw error;
    }

    LocalDateTime currentDateTime = LocalDateTime.now();

    if (departureDate.isBefore(currentDateTime) || arrivalDate.isBefore(currentDateTime)) {
      throw new IllegalArgumentException("Date should be in the future.");
    }

    return scheduleRequest.getSchedules(departure, arrival, departureDate, arrivalDate);
  }
}
