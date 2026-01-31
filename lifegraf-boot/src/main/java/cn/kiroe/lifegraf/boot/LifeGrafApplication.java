package cn.kiroe.lifegraf.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point for LifeGraf.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@ConfigurationPropertiesScan
public class LifeGrafApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeGrafApplication.class, args);
    }
}
