package cn.kiroe.lifegraf.bilibili.remote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BilibiliQRCodeGenerateResponse {
    private int code;
    private String message;
    private int ttl;
    private DataInfo data;

    @Data
    public static class DataInfo {
        private String url;
        @JsonProperty("qrcode_key")
        private String qrcodeKey;
    }
}
