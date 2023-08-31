package com.ryanair.ryanair.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.ryanair.ryanair.bridge.InterconnectionHandler;
import com.ryanair.ryanair.dto.InterconnectionResponse;
import com.ryanair.ryanair.model.Interconnection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api")
public class InterconnectionController {

  private final InterconnectionHandler interconnectionHandler;

  public InterconnectionController(InterconnectionHandler interconnectionHandler) {
    this.interconnectionHandler = interconnectionHandler;
  }

  @GetMapping("v1/interconnections")
  public List<InterconnectionResponse.Details> getRoutesWithParams(
      @RequestParam(required = false) String departure,
      @RequestParam(required = false) String arrival,
      @RequestParam(required = false) String departureDateTime,
      @RequestParam(required = false) String arrivalDateTime) {

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

    Interconnection interconnection =
        Interconnection.builder()
            .arrival(arrival)
            .arrivalDateTime(arrivalDate)
            .departure(departure)
            .departureDateTime(departureDate)
            .build();

    return interconnectionHandler.getInterconnections(interconnection);
  }
}
