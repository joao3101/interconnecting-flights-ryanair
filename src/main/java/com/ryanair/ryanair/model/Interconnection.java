package com.ryanair.ryanair.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record Interconnection(
    String departure,
    LocalDateTime departureDateTime,
    String arrival,
    LocalDateTime arrivalDateTime) {}
