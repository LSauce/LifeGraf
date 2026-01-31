package cn.kiroe.lifegraf.bilibili.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.bilibili")
public class BilibiliProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 数据同步 cron 表达式
     */
    private String cron = "0 0/10 * * * ?";
}
