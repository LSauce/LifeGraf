package cn.kiroe.lifegraf.steam.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.steam")
public class SteamProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * Steam API 基础地址
     */
    private String baseUrl = "api.steampowered.com";

    /**
     * Steam Web API 密钥
     * 请通过环境变量 STEAM_API_KEY 设置
     */
    private String apiKey = "your_steam_api_key";

    /**
     * 我的 Steam ID
     * 请通过环境变量 STEAM_MY_ID 设置
     */
    private String mySteamId = "12345678901234567";

    /**
     * 数据同步 cron 表达式
     */
    private String cron = "0 0/5 * * * ?";
}
