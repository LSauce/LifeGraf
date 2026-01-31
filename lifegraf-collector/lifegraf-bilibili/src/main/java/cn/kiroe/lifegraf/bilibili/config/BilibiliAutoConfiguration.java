package cn.kiroe.lifegraf.bilibili.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.bilibili", name = "enabled", havingValue = "true")
@ComponentScan(basePackages = {
    "cn.kiroe.lifegraf.bilibili.**",
})
public class BilibiliAutoConfiguration {
}
