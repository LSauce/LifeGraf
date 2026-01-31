package cn.kiroe.lifegraf.activitywatch.scheduler;

import cn.kiroe.lifegraf.activitywatch.service.ActivityWatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityWatchScheduler {

    private final ActivityWatchService activityWatchService;

    public ActivityWatchScheduler(ActivityWatchService activityWatchService) {
        this.activityWatchService = activityWatchService;
    }

    @Scheduled(cron = "${app.activity-watch.cron:0 0/10 * * * ?}") // 每10分钟执行一次
    public void syncAllActivityWatchData() {
        log.info("开始执行ActivityWatch数据同步任务");
        try {
            activityWatchService.syncAllEvents();
            log.info("ActivityWatch数据同步任务完成");
        } catch (Exception e) {
            log.error("ActivityWatch数据同步任务执行失败", e);
        }
    }
}
