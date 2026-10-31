package cn.kiroe.lifegraf.gps.controller;

import cn.kiroe.lifegraf.gps.dto.GpsPointInputDTO;
import cn.kiroe.lifegraf.gps.model.GpsPoint;
import cn.kiroe.lifegraf.gps.service.GpsDataService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/gps")
public class GpsPointController {

    private final GpsDataService gpsDataService;

    public GpsPointController(GpsDataService gpsDataService) {
        this.gpsDataService = gpsDataService;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadGpsData(@RequestBody(required = false) List<GpsPointInputDTO> gpsPoints) {
        // Validate the entire batch before invoking any persistence logic.
        if (gpsPoints == null || gpsPoints.isEmpty()) {
            return ResponseEntity.badRequest().body("GPS points must be a non-empty array");
        }
        for (int i = 0; i < gpsPoints.size(); i++) {
            GpsPointInputDTO point = gpsPoints.get(i);
            if (point == null) {
                return ResponseEntity.badRequest().body("GPS point at index " + i + " must not be null");
            }
            if (!isCoordinateInRange(point.getLatitude(), 90)) {
                return ResponseEntity.badRequest().body(
                        "GPS point at index " + i + ": latitude is required and must be between -90 and 90");
            }
            if (!isCoordinateInRange(point.getLongitude(), 180)) {
                return ResponseEntity.badRequest().body(
                        "GPS point at index " + i + ": longitude is required and must be between -180 and 180");
            }
        }
        try {
            gpsDataService.saveGpsPoints(gpsPoints);
            return ResponseEntity.ok("GPS data saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving GPS data: " + e.getMessage());
        }
    }

    private static boolean isCoordinateInRange(Double value, double limit) {
        return value != null && Double.isFinite(value) && value >= -limit && value <= limit;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleUnreadableUpload() {
        return ResponseEntity.badRequest().body("Invalid GPS upload JSON: expected an array of GPS points");
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<GpsPoint>> getTrack(@PathVariable Long trackId) {
        List<GpsPoint> points = gpsDataService.getTrackById(trackId);
        return ResponseEntity.ok(points);
    }
}
