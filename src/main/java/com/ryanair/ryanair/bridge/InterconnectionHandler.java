package com.ryanair.ryanair.bridge;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ryanair.ryanair.dto.InterconnectionResponse;
import com.ryanair.ryanair.model.Day;
import com.ryanair.ryanair.model.Flight;
import com.ryanair.ryanair.model.Interconnection;
import com.ryanair.ryanair.model.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InterconnectionHandler {
  private final RouteRequest routeRequest;
  private final ScheduleRequest scheduleRequest;

  public InterconnectionHandler(RouteRequest routeRequest, ScheduleRequest scheduleRequest) {
    this.routeRequest = routeRequest;
    this.scheduleRequest = scheduleRequest;
  }

  public List<InterconnectionResponse.Details> getInterconnections(
      Interconnection interconnection) {
    var routes = routeRequest.getRoutes(interconnection.departure(), interconnection.arrival());

    ArrayList<InterconnectionResponse.Details> rsp = new ArrayList<>();

    ArrayList<Route> goingRoutes = new ArrayList<>();
    ArrayList<Route> arrivingRoutes = new ArrayList<>();

    List<String> interconnectionRoutes = new ArrayList<>();
    Boolean hasDirectConnection = false;

    for (Route route : routes) {
      if (route.airportFrom().equalsIgnoreCase(interconnection.departure())) {
        goingRoutes.add(route);
      }
      if (route.airportTo().equalsIgnoreCase(interconnection.arrival())) {
        arrivingRoutes.add(route);
      }
      if (route.airportFrom().equalsIgnoreCase(interconnection.departure())
          && route.airportTo().equalsIgnoreCase(interconnection.arrival())) {
        hasDirectConnection = true;
      }
    }

    for (Route gr : goingRoutes) {
      for (Route ar : arrivingRoutes) {
        if (gr.airportTo().equalsIgnoreCase(ar.airportFrom())) {
          interconnectionRoutes.add(gr.airportTo());
        }
      }
    }

    for (String key : interconnectionRoutes) {
      log.info("Key: " + key);
    }

    if (hasDirectConnection) {
      ArrayList<InterconnectionResponse.Leg> legs = new ArrayList<>();
      var schedule =
          scheduleRequest.getSchedules(
              interconnection.departure(),
              interconnection.arrival(),
              interconnection.departureDateTime(),
              interconnection.arrivalDateTime());
      for (Day d : schedule.days()) {
        if (d.day() >= interconnection.departureDateTime().getDayOfMonth()
            && d.day() <= interconnection.arrivalDateTime().getDayOfMonth()) {
          for (Flight f : d.flights()) {
            String[] dt = f.departureTime().split(":");
            int dtHour = Integer.parseInt(dt[0]);
            int dtMinute = Integer.parseInt(dt[1]);

            String[] at = f.arrivalTime().split(":");
            int atHour = Integer.parseInt(at[0]);
            int atMinute = Integer.parseInt(at[1]);

            var flightDT =
                LocalDateTime.of(
                    interconnection.departureDateTime().getYear(),
                    interconnection.departureDateTime().getMonthValue(),
                    d.day(),
                    dtHour,
                    dtMinute);
            var flightAT =
                LocalDateTime.of(
                    interconnection.arrivalDateTime().getYear(),
                    interconnection.arrivalDateTime().getMonthValue(),
                    d.day(),
                    atHour,
                    atMinute);

            if (flightDT.isAfter(interconnection.departureDateTime())
                && flightAT.isBefore(interconnection.arrivalDateTime())) {
              var l =
                  InterconnectionResponse.Leg.builder()
                      .arrivalAirport(interconnection.arrival())
                      .arrivalDateTime(
                          String.valueOf(
                              interconnection.arrivalDateTime().getYear()
                                  + "-"
                                  + schedule.month()
                                  + "-"
                                  + d.day()
                                  + "T"
                                  + f.arrivalTime()))
                      .departureAirport(interconnection.departure())
                      .departureDateTime(
                          String.valueOf(
                              interconnection.departureDateTime().getYear()
                                  + "-"
                                  + schedule.month()
                                  + "-"
                                  + d.day()
                                  + "T"
                                  + f.departureTime()))
                      .build();
              legs.add(l);
              rsp.add(InterconnectionResponse.Details.builder().stops(0).legs(legs).build());
            }
          }
        }
      }
    }

    for (String ir : interconnectionRoutes) {
      ArrayList<InterconnectionResponse.Leg> connectionLegs = new ArrayList<>();
      // First leg
      var schedule =
          scheduleRequest.getSchedules(
              interconnection.departure(),
              ir,
              interconnection.departureDateTime(),
              interconnection.arrivalDateTime());
      for (Day d : schedule.days()) {
        if (d.day() >= interconnection.departureDateTime().getDayOfMonth()
            && d.day() <= interconnection.arrivalDateTime().getDayOfMonth()) {
          for (Flight f : d.flights()) {
            String[] dt = f.departureTime().split(":");
            int dtHour = Integer.parseInt(dt[0]);
            int dtMinute = Integer.parseInt(dt[1]);

            String[] at = f.arrivalTime().split(":");
            int atHour = Integer.parseInt(at[0]);
            int atMinute = Integer.parseInt(at[1]);

            var flightDT =
                LocalDateTime.of(
                    interconnection.departureDateTime().getYear(),
                    interconnection.departureDateTime().getMonthValue(),
                    d.day(),
                    dtHour,
                    dtMinute);
            var flightAT =
                LocalDateTime.of(
                    interconnection.arrivalDateTime().getYear(),
                    interconnection.arrivalDateTime().getMonthValue(),
                    d.day(),
                    atHour,
                    atMinute);

            if (flightDT.isAfter(interconnection.departureDateTime())
                && flightAT.isBefore(interconnection.arrivalDateTime())) {
              String day;
              if (d.day() < 10) {
                day = "0" + d.day();
              } else {
                day = d.day().toString();
              }

              String month = schedule.month();
              if (Integer.parseInt(schedule.month()) < 10) {
                month = "0" + schedule.month();
              }

              var l =
                  InterconnectionResponse.Leg.builder()
                      .arrivalAirport(ir)
                      .arrivalDateTime(
                          String.valueOf(
                              interconnection.arrivalDateTime().getYear()
                                  + "-"
                                  + month
                                  + "-"
                                  + day
                                  + "T"
                                  + f.arrivalTime()))
                      .departureAirport(interconnection.departure())
                      .departureDateTime(
                          String.valueOf(
                              interconnection.departureDateTime().getYear()
                                  + "-"
                                  + month
                                  + "-"
                                  + day
                                  + "T"
                                  + f.departureTime()))
                      .build();
              connectionLegs.add(l);
            }
          }
        }
      }

      if (connectionLegs.isEmpty()) {
        continue;
      }
      var firstLegArrivalTime = LocalDateTime.parse(connectionLegs.get(0).arrivalDateTime());

      // Adding two hours for the connection
      firstLegArrivalTime.plusHours(2);

      // Second leg
      var scheduleTwo =
          scheduleRequest.getSchedules(
              ir,
              interconnection.arrival(),
              firstLegArrivalTime,
              interconnection.arrivalDateTime());
      for (Day d : scheduleTwo.days()) {
        if (d.day() >= firstLegArrivalTime.getDayOfMonth()
            && d.day() <= interconnection.arrivalDateTime().getDayOfMonth()) {
          for (Flight f : d.flights()) {
            String[] dt = f.departureTime().split(":");
            int dtHour = Integer.parseInt(dt[0]);
            int dtMinute = Integer.parseInt(dt[1]);

            String[] at = f.arrivalTime().split(":");
            int atHour = Integer.parseInt(at[0]);
            int atMinute = Integer.parseInt(at[1]);

            var flightDT =
                LocalDateTime.of(
                    firstLegArrivalTime.getYear(),
                    firstLegArrivalTime.getMonthValue(),
                    d.day(),
                    dtHour,
                    dtMinute);
            var flightAT =
                LocalDateTime.of(
                    interconnection.arrivalDateTime().getYear(),
                    interconnection.arrivalDateTime().getMonthValue(),
                    d.day(),
                    atHour,
                    atMinute);

            if (flightDT.isAfter(firstLegArrivalTime)
                && flightAT.isBefore(interconnection.arrivalDateTime())) {
              var l =
                  InterconnectionResponse.Leg.builder()
                      .arrivalAirport(interconnection.arrival())
                      .arrivalDateTime(
                          String.valueOf(
                              interconnection.arrivalDateTime().getYear()
                                  + "-"
                                  + scheduleTwo.month()
                                  + "-"
                                  + d.day()
                                  + "T"
                                  + f.arrivalTime()))
                      .departureAirport(ir)
                      .departureDateTime(
                          String.valueOf(
                              firstLegArrivalTime.getYear()
                                  + "-"
                                  + scheduleTwo.month()
                                  + "-"
                                  + d.day()
                                  + "T"
                                  + f.departureTime()))
                      .build();
              connectionLegs.add(l);
            }
          }
        }
      }

      if (connectionLegs.size() < 2) {
        continue;
      }

      rsp.add(InterconnectionResponse.Details.builder().stops(1).legs(connectionLegs).build());
    }

    return rsp;
  }
}
