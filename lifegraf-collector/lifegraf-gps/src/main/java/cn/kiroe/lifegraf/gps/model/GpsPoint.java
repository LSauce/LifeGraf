package cn.kiroe.lifegraf.gps.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("gps_points")
public class GpsPoint {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("time")
    private LocalDateTime time;

    @TableField("track_id")
    private Long trackId;

    @TableField("latitude")
    private Double latitude;

    @TableField("longitude")
    private Double longitude;

    @TableField("altitude")
    private Double altitude;

    @TableField("timestamp")
    private LocalDateTime timestamp;

    @TableField("speed")
    private Double speed;
}
