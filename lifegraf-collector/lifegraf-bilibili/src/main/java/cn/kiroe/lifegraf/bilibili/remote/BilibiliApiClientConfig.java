package cn.kiroe.lifegraf.bilibili.remote;

import cn.kiroe.lifegraf.bilibili.service.BilibiliService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

@Slf4j
@Configuration
public class BilibiliApiClientConfig {

    private static final String BASE_URL = "https://api.bilibili.com";

    @Bean
    BilibiliApiClient bilibiliApiClient(RestClient.Builder builder, BilibiliService bilibiliService) {
        RestClient restClient = builder
                .baseUrl(BASE_URL)
                .requestInterceptor((request, body, execution) -> {
                    Optional<String> cookieOpt = Optional.ofNullable(bilibiliService.getBilibiliCookie());
                    cookieOpt.ifPresentOrElse(
                            cookie -> {
                                request.getHeaders().add(HttpHeaders.COOKIE, cookie);
                                log.debug("添加B站Cookie到请求头");
                            },
                            () -> log.warn("未找到B站Cookie，可能无法获取需要登录的数据")
                    );

                    ClientHttpResponse response = execution.execute(request, body);
                    log.debug("Response Status: {}", response.getStatusCode());
                    return response;
                })
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(BilibiliApiClient.class);
    }
}
