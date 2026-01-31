package cn.kiroe.lifegraf.steam.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnProperty(prefix = "app.steam", name = "enabled", havingValue = "true")
@ComponentScan(basePackages = {
    "cn.kiroe.lifegraf.steam.**",
})
public class SteamAutoConfiguration {
}
