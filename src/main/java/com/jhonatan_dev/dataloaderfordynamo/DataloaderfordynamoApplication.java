package com.jhonatan_dev.dataloaderfordynamo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataloaderfordynamoApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(DataloaderfordynamoApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("Data Loader for Dynamo, initialized! d|o_o|b");
  }
}
