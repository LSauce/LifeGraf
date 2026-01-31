package cn.kiroe.lifegraf.gps.converter;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.kiroe.lifegraf.gps.dto.GpsPointInputDTO;
import cn.kiroe.lifegraf.gps.model.GpsPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GpsPointConverter {
    @Mapping(source = "timestamp", target = "timestamp")
    GpsPoint dto2po(GpsPointInputDTO dto);
    List<GpsPoint> dtos2pos(List<GpsPointInputDTO> dtos);

    default LocalDateTime fromTimestamp(Long timestamp) {
        return LocalDateTimeUtil.of(timestamp*1000);
    }
}
