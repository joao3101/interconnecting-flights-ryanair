package com.ryanair.ryanair.bridge;

import java.util.List;
import java.util.Objects;

import com.ryanair.ryanair.model.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RouteRequest {
  public static final String RYAN_AIR = "RYANAIR";

  @Value("${BASE_RYANAIR_API_URL}")
  private String BASE_RYANAIR_API_URL;

  public List<Route> getRoutes(String departure, String arrival) {
    var webClient = WebClient.create(BASE_RYANAIR_API_URL);

    return webClient
        .get()
        .uri("/views/locate/3/routes")
        .retrieve()
        .bodyToFlux(Route.class)
        .filter(
            route ->
                Objects.isNull(route.connectionAirport())
                    && RYAN_AIR.equals(route.operator())
                    && (route.airportFrom().equalsIgnoreCase(departure)
                        || route.airportTo().equalsIgnoreCase(arrival)))
        .collectList()
        .block();
  }
}
