package cn.kiroe.lifegraf.activitywatch.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.activity-watch", name = "enabled", havingValue = "true")
@ComponentScan(basePackages = {
    "cn.kiroe.lifegraf.activitywatch.*"
})
public class ActivityWatchAutoConfiguration {
}
