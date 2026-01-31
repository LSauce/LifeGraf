package cn.kiroe.lifegraf.activitywatch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.activity-watch")
public class ActivityWatchProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;
    /**
     * ActivityWatch 服务器地址
     */
    private String baseUrl = "http://localhost:5600";

    /**
     * 数据同步 cron 表达式
     */
    private String cron = "0 0/10 * * * ?";
}
