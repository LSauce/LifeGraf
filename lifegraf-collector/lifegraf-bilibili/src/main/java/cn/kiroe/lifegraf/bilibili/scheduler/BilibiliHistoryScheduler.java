package cn.kiroe.lifegraf.bilibili.scheduler;

import cn.kiroe.lifegraf.bilibili.service.BilibiliHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BilibiliHistoryScheduler {

    private final BilibiliHistoryService bilibiliHistoryService;

    public BilibiliHistoryScheduler(
            BilibiliHistoryService bilibiliHistoryService) {
        this.bilibiliHistoryService = bilibiliHistoryService;
    }

    /**
     * 定时同步B站历史记录（通过配置文件设置cron表达式）
     */
    @Scheduled(cron = "${app.bilibili.cron}")
    public void syncBilibiliHistory() {
        log.info("开始执行B站历史记录定时同步任务");
        try {
            bilibiliHistoryService.syncBilibiliHistory();
            log.info("B站历史记录定时同步任务完成");
        } catch (Exception e) {
            log.error("B站历史记录定时同步任务执行失败", e);
        }
    }
}
