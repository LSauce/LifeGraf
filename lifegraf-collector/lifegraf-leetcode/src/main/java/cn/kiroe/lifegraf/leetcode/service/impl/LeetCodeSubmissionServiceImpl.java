package cn.kiroe.lifegraf.leetcode.service.impl;

import cn.kiroe.lifegraf.leetcode.config.LeetCodeProperties;
import cn.kiroe.lifegraf.leetcode.converter.LeetCodeConverter;
import cn.kiroe.lifegraf.leetcode.mapper.LeetCodeSubmissionMapper;
import cn.kiroe.lifegraf.leetcode.model.LeetCodeSubmission;
import cn.kiroe.lifegraf.leetcode.remote.LeetCodeClient;
import cn.kiroe.lifegraf.leetcode.remote.dto.LeetCodeFollowerResponse;
import cn.kiroe.lifegraf.leetcode.remote.dto.LeetCodeSubmissionResponse;
import cn.kiroe.lifegraf.leetcode.service.LeetCodeSubmissionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class LeetCodeSubmissionServiceImpl extends ServiceImpl<LeetCodeSubmissionMapper, LeetCodeSubmission> implements LeetCodeSubmissionService {

    private final LeetCodeClient leetCodeClient;
    private final LeetCodeSubmissionMapper leetCodeSubmissionMapper;
    private final LeetCodeConverter leetCodeConverter;
    private final LeetCodeProperties leetCodeProperties;

    public LeetCodeSubmissionServiceImpl(
            LeetCodeClient leetCodeClient,
            LeetCodeSubmissionMapper leetCodeSubmissionMapper,
            LeetCodeConverter leetCodeConverter,
            LeetCodeProperties leetCodeProperties) {
        this.leetCodeClient = leetCodeClient;
        this.leetCodeSubmissionMapper = leetCodeSubmissionMapper;
        this.leetCodeConverter = leetCodeConverter;
        this.leetCodeProperties = leetCodeProperties;
    }

    @Override
    public void syncLeetCodeSubmissions() {
        log.info("开始同步LeetCode提交记录");

        try {
            String userSlug = leetCodeProperties.getUserSlug();
            if (userSlug == null || userSlug.isEmpty()) {
                log.warn("未配置LeetCode用户名，无法同步提交记录");
                return;
            }

            // 1. 获取关注列表
            List<String> userSlugs = getFollowers(userSlug);
            userSlugs.add(userSlug); // 添加自己
            log.info("需要同步的用户列表: {}", userSlugs);

            // 2. 获取每个用户的提交记录
            List<LeetCodeSubmission> allNewSubmissions = new ArrayList<>();
            String query = """
                query recentAcSubmissions($userSlug: String!) {
                  recentACSubmissions(userSlug: $userSlug) {
                    submissionId
                    submitTime
                    question {
                      title
                      translatedTitle
                      titleSlug
                      questionFrontendId
                    }
                  }
                }
                """;

            for (String currentUserSlug : userSlugs) {
                try {
                    // 添加随机延迟 (500-2000ms)
                    addRandomDelay();
                    LeetCodeSubmission lastSubmission = getOne(Wrappers.<LeetCodeSubmission>lambdaQuery()
                            .select(LeetCodeSubmission::getSubmitTime)
                            .eq(LeetCodeSubmission::getUserSlug, currentUserSlug)
                            .orderByDesc(true, LeetCodeSubmission::getSubmitTime).last("LIMIT 1"));
                    Instant lastSubmissionTime = lastSubmission == null ? Instant.now().minus(365*20, ChronoUnit.DAYS) : lastSubmission.getSubmitTime();

                    log.info("用户 {} 的最后提交时间: {}", currentUserSlug, lastSubmissionTime);

                    // 调用GraphQL API获取最近的提交记录
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("userSlug", currentUserSlug);

                    LeetCodeClient.GraphQLRequest request = new LeetCodeClient.GraphQLRequest(query, variables);
                    LeetCodeSubmissionResponse response = leetCodeClient.getRecentACSubmissions(request);

                    if (response == null || response.getResponseData() == null ||
                        response.getResponseData().getRecentACSubmissions() == null) {
                        log.warn("用户 {} 未获取到LeetCode提交记录", currentUserSlug);
                        continue;
                    }

                    List<LeetCodeSubmissionResponse.RecentACSubmission> submissions =
                        response.getResponseData().getRecentACSubmissions();

                    log.info("用户 {} 获取到 {} 条提交记录", currentUserSlug, submissions.size());

                    // 过滤出新增的提交记录
                    List<LeetCodeSubmissionResponse.RecentACSubmission> newSubmissions = submissions.stream()
                        .filter(submission -> {
                            Instant submitTime = Instant.ofEpochSecond(submission.getSubmitTime());
                            return submitTime.isAfter(lastSubmissionTime);
                        })
                        .toList();

                    log.info("用户 {} 过滤后有 {} 条新提交记录", currentUserSlug, newSubmissions.size());

                    // 转换并设置userSlug
                    if (!newSubmissions.isEmpty()) {
                        List<LeetCodeSubmission> leetCodeSubmissions = leetCodeConverter.dtosToPos(newSubmissions);

                        // 设置userSlug
                        for (LeetCodeSubmission submission : leetCodeSubmissions) {
                            submission.setUserSlug(currentUserSlug);
                            submission.setCreateTime(Instant.now());
                            submission.setUpdateTime(Instant.now());
                        }

                        allNewSubmissions.addAll(leetCodeSubmissions);
                    }
                } catch (Exception e) {
                    log.error("同步用户 {} 的提交记录时发生错误", currentUserSlug, e);
                }
            }

            // 3. 批量保存到数据库
            if (!allNewSubmissions.isEmpty()) {
                leetCodeSubmissionMapper.batchInsert(allNewSubmissions);
                log.info("成功同步 {} 条LeetCode提交记录", allNewSubmissions.size());
            } else {
                log.info("没有新的LeetCode提交记录需要同步");
            }

        } catch (Exception e) {
            log.error("同步LeetCode提交记录时发生错误", e);
        }

        log.info("LeetCode提交记录同步完成");
    }

    /**
     * 获取关注列表
     */
    private List<String> getFollowers(String userSlug) {
        List<String> followers = new ArrayList<>();

        try {
            String query = """
                query followerUsers($userSlug: String!, $pageNumber: Int, $resultPerPage: Int) {
                  followers(
                    userSlug: $userSlug
                    pageNumber: $pageNumber
                    resultPerPage: $resultPerPage
                  ) {
                    users {
                      realName
                      userAvatar
                      userSlug
                      aboutMe
                      isMutualFollowing
                      isFollowingMe
                      isFollowedByMe
                    }
                  }
                }
                """;

            Map<String, Object> variables = new HashMap<>();
            variables.put("userSlug", userSlug);
            variables.put("pageNumber", 0);
            variables.put("resultPerPage", 100); // 增加每页数量以获取更多关注者

            LeetCodeClient.GraphQLRequest request = new LeetCodeClient.GraphQLRequest(query, variables);
            LeetCodeFollowerResponse response = leetCodeClient.getFollowers(request);

            if (response != null && response.getResponseData() != null &&
                response.getResponseData().getFollowers() != null &&
                response.getResponseData().getFollowers().getUsers() != null) {
                List<LeetCodeFollowerResponse.User> users = response.getResponseData().getFollowers().getUsers();
                for (LeetCodeFollowerResponse.User user : users) {
                    followers.add(user.getUserSlug());
                }
                log.info("获取到 {} 个关注用户", followers.size());
            } else {
                log.warn("未获取到关注列表");
            }
        } catch (Exception e) {
            log.error("获取关注列表时发生错误", e);
        }

        return followers;
    }

    /**
     * 添加随机延迟 (500-2000ms)
     */
    private void addRandomDelay() {
        try {
            int delay = ThreadLocalRandom.current().nextInt(500, 2001);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("随机延迟被中断", e);
        }
    }
}
