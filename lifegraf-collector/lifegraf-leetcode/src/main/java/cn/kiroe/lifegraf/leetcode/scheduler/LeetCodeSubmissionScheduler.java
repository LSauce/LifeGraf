package cn.kiroe.lifegraf.leetcode.scheduler;

import cn.kiroe.lifegraf.leetcode.service.LeetCodeSubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeetCodeSubmissionScheduler {

    private final LeetCodeSubmissionService leetCodeSubmissionService;

    public LeetCodeSubmissionScheduler(LeetCodeSubmissionService leetCodeSubmissionService) {
        this.leetCodeSubmissionService = leetCodeSubmissionService;
    }

    /**
     * Sync LeetCode submission records periodically (every 30 minutes)
     */
    @Scheduled(cron = "${app.leetcode.cron:0 0/30 * * * ?}")
    public void syncLeetCodeSubmissions() {
        log.info("开始执行LeetCode提交记录定时同步任务");
        try {
            leetCodeSubmissionService.syncLeetCodeSubmissions();
            log.info("LeetCode提交记录定时同步任务完成");
        } catch (Exception e) {
            log.error("LeetCode提交记录定时同步任务执行失败", e);
        }
    }
}
