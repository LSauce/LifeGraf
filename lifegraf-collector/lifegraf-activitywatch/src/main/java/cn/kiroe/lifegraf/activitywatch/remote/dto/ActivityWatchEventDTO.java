package cn.kiroe.lifegraf.activitywatch.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ActivityWatchEventDTO {

    private Long id;

    @JsonProperty("timestamp")
    private OffsetDateTime timestamp;

    private Double duration;

    private ActivityWatchEventData data;


    @Data
    public static class ActivityWatchEventData {
        private String app;
        private String title;
    }
}
