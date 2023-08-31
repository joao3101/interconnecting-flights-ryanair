package com.ryanair.ryanair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RyanairApplication {

  public static void main(String[] args) {
    SpringApplication.run(RyanairApplication.class, args);
  }
}
