package com.ryanair.ryanair.model;

import java.util.ArrayList;

public record Day(Integer day, ArrayList<Flight> flights) {}
