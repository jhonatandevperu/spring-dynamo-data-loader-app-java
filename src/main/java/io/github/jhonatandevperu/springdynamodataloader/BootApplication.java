package io.github.jhonatandevperu.springdynamodataloader;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class BootApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(BootApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Data Loader for Dynamo, initialized! d|o_o|b");
  }
}
