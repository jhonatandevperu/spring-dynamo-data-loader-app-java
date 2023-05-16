package io.github.jhonatandevperu.springdynamodataloader.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean("taskExecutor")
  protected Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(100);
    executor.setAllowCoreThreadTimeOut(true);
    executor.setKeepAliveSeconds(5);
    executor.setThreadNamePrefix("taskExecutor");
    executor.initialize();
    return executor;
  }
}
