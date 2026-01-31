package cn.kiroe.lifegraf.leetcode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.leetcode")
public class LeetCodeProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * LeetCode API 基础地址
     */
    private String baseUrl = "https://leetcode.cn";

    /**
     * LeetCode 用户名
     * 请通过环境变量 LEETCODE_USER_SLUG 设置
     */
    private String userSlug = "your_username";

    /**
     * 数据同步 cron 表达式
     */
    private String cron = "0 0/10 * * * ?";
}
