package com.ryanair.ryanair.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanair.ryanair.bridge.RouteRequest;
import com.ryanair.ryanair.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RouteControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RouteRequest routeRequest;

  static Route route = new Route("", "", "", false, false, "", "");
  static List<Route> routesCode3 = List.of(route);

  static List<Route> routesCode4 = Arrays.asList(route, route);

  @BeforeEach
  void setup() {
    Mockito.when(routeRequest.getRoutes(any(), any())).thenReturn(routesCode3);
    Mockito.when(routeRequest.getRoutes(any(), any())).thenReturn(routesCode4);
    RouteController routeController = new RouteController(routeRequest);
  }

  private static Stream<Arguments> getRoutes_when_locateIsPresent_shouldReturnOK_withOneRoute() {
    return Stream.of(Arguments.of(routesCode3), Arguments.of(routesCode4));
  }

  @ParameterizedTest
  @MethodSource()
  public void getRoutes_when_locateIsPresent_shouldReturnOK_withOneRoute(List<Route> routes)
      throws Exception {
    String expectedResponseBody = new ObjectMapper().writeValueAsString(routes);

    this.mockMvc.perform(get("/api/v1/routes")).andDo(print()).andExpect(status().isOk());
  }
}
