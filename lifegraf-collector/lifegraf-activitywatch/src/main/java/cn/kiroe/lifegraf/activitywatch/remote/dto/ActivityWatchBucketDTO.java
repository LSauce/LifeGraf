package cn.kiroe.lifegraf.activitywatch.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ActivityWatchBucketDTO {

    private String id;
    private String name;
    private String type;
    private String client;
    private String hostname;
    private Object data;

    @JsonProperty("created")
    private OffsetDateTime created;

    @JsonProperty("last_updated")
    private OffsetDateTime lastUpdated;
}
