package cn.kiroe.lifegraf.activitywatch.remote;

import cn.kiroe.lifegraf.activitywatch.remote.dto.ActivityWatchBucketDTO;
import cn.kiroe.lifegraf.activitywatch.remote.dto.ActivityWatchEventDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

@HttpExchange("/api/0")
public interface ActivityWatchClient {

    @GetExchange("/buckets/")
    Map<String, ActivityWatchBucketDTO> getBuckets();

    @GetExchange("/buckets/{bucketId}/events")
    ActivityWatchEventDTO[] getEvents(
            @org.springframework.web.bind.annotation.PathVariable("bucketId") String bucketId,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam("limit") String limit
    );
}
