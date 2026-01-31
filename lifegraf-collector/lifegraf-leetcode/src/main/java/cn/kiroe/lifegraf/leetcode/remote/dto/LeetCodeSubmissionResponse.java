package cn.kiroe.lifegraf.leetcode.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LeetCodeSubmissionResponse {

    @JsonProperty("data")
    private ResponseData responseData;

    @Data
    public static class ResponseData {
        @JsonProperty("recentACSubmissions")
        private List<RecentACSubmission> recentACSubmissions;
    }

    @Data
    public static class RecentACSubmission {
        @JsonProperty("submissionId")
        private Long submissionId;

        @JsonProperty("submitTime")
        private Long submitTime;

        private Question question;
    }

    @Data
    public static class Question {
        private String title;

        @JsonProperty("translatedTitle")
        private String translatedTitle;

        @JsonProperty("titleSlug")
        private String titleSlug;

        @JsonProperty("questionFrontendId")
        private String questionFrontendId;
    }
}
