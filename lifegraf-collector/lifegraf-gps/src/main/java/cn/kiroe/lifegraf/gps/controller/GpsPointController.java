package cn.kiroe.lifegraf.gps.controller;

import cn.kiroe.lifegraf.gps.dto.GpsPointInputDTO;
import cn.kiroe.lifegraf.gps.model.GpsPoint;
import cn.kiroe.lifegraf.gps.service.GpsDataService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/gps")
public class GpsPointController {

    private final GpsDataService gpsDataService;

    public GpsPointController(GpsDataService gpsDataService) {
        this.gpsDataService = gpsDataService;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadGpsData(@RequestBody List<GpsPointInputDTO> gpsPoints) {
        try {
            gpsDataService.saveGpsPoints(gpsPoints);
            return ResponseEntity.ok("GPS data saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving GPS data: " + e.getMessage());
        }
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<GpsPoint>> getTrack(@PathVariable Long trackId) {
        List<GpsPoint> points = gpsDataService.getTrackById(trackId);
        return ResponseEntity.ok(points);
    }
}
