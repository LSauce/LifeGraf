package cn.kiroe.lifegraf.activitywatch.remote;

import cn.kiroe.lifegraf.activitywatch.config.ActivityWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class ActivityWatchClientConfig {

    private final ActivityWatchProperties activityWatchProperties;

     public ActivityWatchClientConfig(ActivityWatchProperties activityWatchProperties) {
        this.activityWatchProperties = activityWatchProperties;
    }

    @Bean
    ActivityWatchClient activityWatchClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl(activityWatchProperties.getBaseUrl())
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ActivityWatchClient.class);
    }
}
