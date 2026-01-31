package cn.kiroe.lifegraf.steam.scheduler;

import cn.kiroe.lifegraf.steam.config.SteamProperties;
import cn.kiroe.lifegraf.steam.service.SteamService;
import com.lukaspradel.steamapi.data.json.friendslist.Friend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SteamScheduler {
    private final SteamService steamService;

    public SteamScheduler(SteamService steamService) {
        this.steamService = steamService;
    }

    @Scheduled(cron = "${app.steam.cron:0 0/10 * * * ?}")
    public void saveNowGaming() {
        steamService.saveNowGaming();
    }
}
