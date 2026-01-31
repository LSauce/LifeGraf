package cn.kiroe.lifegraf.bilibili.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BilibiliQRCodePollResponse {
    private int code;
    private String message;
    private int ttl;
    private DataInfo data;

    @Data
    public static class DataInfo {
        private String url;
        @JsonProperty("refresh_token")
        private String refreshToken;
        private long timestamp;
        private int code;
        private String message;
    }
}
