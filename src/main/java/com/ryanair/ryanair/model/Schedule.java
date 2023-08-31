package com.ryanair.ryanair.model;

import java.util.ArrayList;

public record Schedule(String month, ArrayList<Day> days) {}
