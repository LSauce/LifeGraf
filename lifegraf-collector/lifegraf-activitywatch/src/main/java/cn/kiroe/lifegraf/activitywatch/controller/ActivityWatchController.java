package cn.kiroe.lifegraf.activitywatch.controller;

import cn.kiroe.lifegraf.activitywatch.service.ActivityWatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/activityWatch")
public class ActivityWatchController {

    private final ActivityWatchService activityWatchService;

    public ActivityWatchController(ActivityWatchService activityWatchService) {
        this.activityWatchService = activityWatchService;
    }


    /**
     * 手动触发ActivityWatch数据同步
     * @return
     */
    @PostMapping("/sync")
    public ResponseEntity<Boolean> syncActivityWatch() {
        log.info("开始执行ActivityWatch数据同步任务");
        try {
            activityWatchService.syncAllEvents();
            log.info("ActivityWatch数据同步任务完成");
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error("ActivityWatch数据同步任务执行失败", e);
            return ResponseEntity.ok(false);
        }
    }


}
