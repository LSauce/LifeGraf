package cn.kiroe.lifegraf.activitywatch.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("activity_watch_event")
public class ActivityWatchEvent {

    Long id;
    Long bucketId;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    Double duration;
    String app;
    String title;
}
