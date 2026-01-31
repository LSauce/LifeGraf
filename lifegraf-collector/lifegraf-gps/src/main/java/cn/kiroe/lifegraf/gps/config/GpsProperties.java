package cn.kiroe.lifegraf.gps.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.gps")
public class GpsProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;
}
