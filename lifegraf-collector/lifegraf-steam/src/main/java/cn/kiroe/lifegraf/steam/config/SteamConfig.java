package cn.kiroe.lifegraf.steam.config;

import cn.hutool.core.util.URLUtil;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SteamConfig {

    private final SteamProperties steamProperties;

    public SteamConfig(SteamProperties steamProperties) {
        this.steamProperties = steamProperties;
    }

    @Bean
    public SteamWebApiClient steamWebApiClient() {
        String baseUrl = steamProperties.getBaseUrl();
        if (baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) {
            baseUrl = baseUrl.substring(baseUrl.indexOf("://") + 3);
        }
        return new SteamWebApiClient
                .SteamWebApiClientBuilder(steamProperties.getApiKey())
                .baseUrl(baseUrl)
                .build();
    }
}
