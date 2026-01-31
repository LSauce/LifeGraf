package cn.kiroe.lifegraf.leetcode.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LeetCodeFollowerResponse {

    @JsonProperty("data")
    private ResponseData responseData;

    @Data
    public static class ResponseData {
        @JsonProperty("followers")
        private Followers followers;
    }

    @Data
    public static class Followers {
        private List<User> users;
    }

    @Data
    public static class User {
        @JsonProperty("realName")
        private String realName;

        @JsonProperty("userAvatar")
        private String userAvatar;

        @JsonProperty("userSlug")
        private String userSlug;

        @JsonProperty("aboutMe")
        private String aboutMe;

        @JsonProperty("isMutualFollowing")
        private Boolean isMutualFollowing;

        @JsonProperty("isFollowingMe")
        private Boolean isFollowingMe;

        @JsonProperty("isFollowedByMe")
        private Boolean isFollowedByMe;
    }
}
