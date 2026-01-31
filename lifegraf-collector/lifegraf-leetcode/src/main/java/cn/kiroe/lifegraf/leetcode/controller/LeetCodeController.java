package cn.kiroe.lifegraf.leetcode.controller;

import cn.kiroe.lifegraf.leetcode.model.LeetCodeSubmission;
import cn.kiroe.lifegraf.leetcode.service.LeetCodeSubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leetcode")
@Slf4j
public class LeetCodeController {

    private final LeetCodeSubmissionService leetCodeSubmissionService;

    public LeetCodeController(LeetCodeSubmissionService leetCodeSubmissionService) {
        this.leetCodeSubmissionService = leetCodeSubmissionService;
    }

    /**
     * Manually trigger LeetCode submission sync
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncSubmissions() {
        try {
            leetCodeSubmissionService.syncLeetCodeSubmissions();
            return ResponseEntity.ok("LeetCode submissions synced successfully");
        } catch (Exception e) {
            log.error("Error syncing LeetCode submissions", e);
            return ResponseEntity.status(500).body("Error syncing LeetCode submissions: " + e.getMessage());
        }
    }
}
