package cn.kiroe.lifegraf.bilibili.remote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class BilibiliClientConfig {

    @Bean
    BilibiliClient bilibiliClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("https://passport.bilibili.com")
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Request: {}", new String(body));
                    ClientHttpResponse response = execution.execute(request, body);
                    log.debug("Response Status: {}", response.getStatusCode());
                    return response;
                })
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(BilibiliClient.class);
    }
}
