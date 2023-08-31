package com.ryanair.ryanair.controller;

import java.util.List;

import com.ryanair.ryanair.bridge.RouteRequest;
import com.ryanair.ryanair.model.Route;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api")
@Cacheable("routes")
public class RouteController {

  private final RouteRequest routeRequest;

  public RouteController(RouteRequest routeRequest) {
    this.routeRequest = routeRequest;
  }

  @GetMapping("v1/routes")
  public List<Route> getRoutes(
      @RequestParam(required = false) String departure,
      @RequestParam(required = false) String arrival) {
    System.out.println("departure: " + departure);
    System.out.println("arrival: " + arrival);
    return routeRequest.getRoutes(departure, arrival);
  }
}
