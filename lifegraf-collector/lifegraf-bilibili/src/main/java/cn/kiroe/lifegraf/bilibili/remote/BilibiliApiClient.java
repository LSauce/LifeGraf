package cn.kiroe.lifegraf.bilibili.remote;

import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliHistoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface BilibiliApiClient {

    @GetExchange("/x/web-interface/history/cursor")
    ResponseEntity<BilibiliHistoryResponse> getHistoryList(
            @RequestParam(value = "max", required = false) Long max,
            @RequestParam(value = "business", required = false) String business,
            @RequestParam(value = "view_at", required = false) Long viewAt,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "ps", required = false) Integer ps
    );
}
