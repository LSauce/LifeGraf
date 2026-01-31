package cn.kiroe.lifegraf.leetcode.mapper;

import cn.kiroe.lifegraf.leetcode.model.LeetCodeSubmission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

@Mapper
public interface LeetCodeSubmissionMapper extends BaseMapper<LeetCodeSubmission> {

    /**
     * Get the last submission time
     *
     * @return last submission time, or minimum time if none exists
     */
    Instant getLastSubmissionTime();

    /**
     * Get the last submission time for a specific user
     *
     * @param userSlug the user slug
     * @return last submission time for the user, or minimum time if none exists
     */
    Instant getLastSubmissionTimeByUser(@Param("userSlug") String userSlug);

    /**
     * Batch insert LeetCode submissions
     *
     * @param submissions list of submissions
     */
    void batchInsert(@Param("submissions") List<LeetCodeSubmission> submissions);
}
