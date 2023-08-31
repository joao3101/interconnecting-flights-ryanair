package com.ryanair.ryanair.model;

public record Route(
    String airportFrom,
    String airportTo,
    String connectionAirport,
    boolean newRoute,
    boolean seasonalRoute,
    String operator,
    String groups) {}
