package cn.kiroe.lifegraf.gps.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GpsPointInputDTO {
    private Long trackId;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Long timestamp;

    private Double speed;
}
