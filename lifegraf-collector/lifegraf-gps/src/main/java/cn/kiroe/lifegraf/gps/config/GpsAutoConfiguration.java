package cn.kiroe.lifegraf.gps.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.gps", name = "enabled", havingValue = "true")
@ComponentScan(basePackages = {
    "cn.kiroe.lifegraf.gps.**"
})
public class GpsAutoConfiguration {
}
