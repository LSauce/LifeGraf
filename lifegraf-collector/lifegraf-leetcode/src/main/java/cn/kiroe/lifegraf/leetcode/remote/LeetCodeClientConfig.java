package cn.kiroe.lifegraf.leetcode.remote;

import cn.kiroe.lifegraf.leetcode.config.LeetCodeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class LeetCodeClientConfig {

    @Bean
    LeetCodeClient leetCodeClient(RestClient.Builder builder, LeetCodeProperties leetCodeProperties) {
        RestClient restClient = builder
                .baseUrl(leetCodeProperties.getBaseUrl())
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Request: {} {}", request.getMethod(), request.getURI());
                    ClientHttpResponse response = execution.execute(request, body);
                    log.debug("Response Status: {}", response.getStatusCode());
                    return response;
                })
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(LeetCodeClient.class);
    }
}
