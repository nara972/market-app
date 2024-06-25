package com.group.marketapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MarketAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(MarketAppApplication.class, args);
  }

}
