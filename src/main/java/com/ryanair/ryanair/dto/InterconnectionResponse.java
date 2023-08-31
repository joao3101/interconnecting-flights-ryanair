package com.ryanair.ryanair.dto;

import java.util.List;

import lombok.Builder;

public interface InterconnectionResponse {
  @Builder
  record Details(Integer stops, List<Leg> legs) {}

  @Builder
  record Leg(
      String departureAirport,
      String arrivalAirport,
      String departureDateTime,
      String arrivalDateTime) {}
}
