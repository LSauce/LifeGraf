package cn.kiroe.lifegraf.gps.service;

import cn.kiroe.lifegraf.gps.converter.GpsPointConverter;
import cn.kiroe.lifegraf.gps.dto.GpsPointInputDTO;
import cn.kiroe.lifegraf.gps.model.GpsPoint;
import cn.kiroe.lifegraf.gps.mapper.GpsPointMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GpsDataService extends ServiceImpl<GpsPointMapper, GpsPoint> implements IService<GpsPoint> {
    private final GpsPointConverter converter;

    public GpsDataService(GpsPointConverter converter) {
        this.converter = converter;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveGpsPoints(List<GpsPointInputDTO> gpsPoints) {
        List<GpsPoint> gpsPointList = converter.dtos2pos(gpsPoints);
        LocalDateTime now = LocalDateTime.now();
        gpsPointList.forEach(gpsPoint -> gpsPoint.setTime(now));
        saveBatch(gpsPointList);
    }

    public List<GpsPoint> getTrackById(Long trackId) {
        return list(Wrappers.<GpsPoint>lambdaQuery().eq(GpsPoint::getTrackId, trackId));
    }
}
