package com.maxwaterfall.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class TictactoeApplication {

  public static void main(String[] args) {
    ReactorDebugAgent.init();
    SpringApplication.run(TictactoeApplication.class, args);
  }
}
