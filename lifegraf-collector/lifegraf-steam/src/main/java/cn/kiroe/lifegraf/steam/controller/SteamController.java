package cn.kiroe.lifegraf.steam.controller;

import cn.kiroe.lifegraf.steam.service.SteamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/steam")
public class SteamController {
    private final SteamService steamService;

    public SteamController(SteamService steamService) {
        this.steamService = steamService;
    }


    @PostMapping("/sync")
    public ResponseEntity<Boolean> sync() {
        steamService.saveNowGaming();
        return ResponseEntity.ok(true);
    }
}
