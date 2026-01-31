package cn.kiroe.lifegraf.activitywatch.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("activity_watch_bucket")
public class ActivityWatchBucket {
    Long id;
    String name;
    String type;
    String client;
    String hostname;
    String description;
    OffsetDateTime createTime;
    OffsetDateTime updateTime;
}
