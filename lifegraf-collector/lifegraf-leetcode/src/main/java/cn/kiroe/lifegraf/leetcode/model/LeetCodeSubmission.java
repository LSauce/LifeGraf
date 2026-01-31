package cn.kiroe.lifegraf.leetcode.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("leetcode_submission")
public class LeetCodeSubmission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("submission_id")
    private Long submissionId;

    @TableField("user_slug")
    private String userSlug;

    @TableField("submit_time")
    private Instant submitTime;

    @TableField("question_title")
    private String questionTitle;

    @TableField("question_translated_title")
    private String questionTranslatedTitle;

    @TableField("title_slug")
    private String titleSlug;

    @TableField("question_frontend_id")
    private String questionFrontendId;

    @TableField("original_json")
    private String originalJson;

    @TableField("create_time")
    private Instant createTime;

    @TableField("update_time")
    private Instant updateTime;
}
