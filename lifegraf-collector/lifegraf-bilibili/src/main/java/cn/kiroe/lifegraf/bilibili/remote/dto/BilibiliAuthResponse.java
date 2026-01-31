package cn.kiroe.lifegraf.bilibili.remote.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * B站二维码登录响应DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilibiliAuthResponse {

    /**
     * 响应码，0表示成功
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 登录状态
     */
    private String status;

    /**
     * 响应数据（成功时为cookie）
     */
    private Object data;

    /**
     * 创建成功响应
     */
    public static BilibiliAuthResponse success(String cookie) {
        return new BilibiliAuthResponse(0, "登录成功", null, cookie);
    }

    /**
     * 创建状态响应
     */
    public static BilibiliAuthResponse status(Integer code, String message, String status) {
        return new BilibiliAuthResponse(code, message, status, null);
    }

    /**
     * 创建错误响应
     */
    public static BilibiliAuthResponse error(String message) {
        return new BilibiliAuthResponse(500, message, null, null);
    }

    /**
     * 创建错误响应（带详细错误信息）
     */
    public static BilibiliAuthResponse error(String message, Throwable cause) {
        return new BilibiliAuthResponse(500, message + ": " + cause.getMessage(), null, null);
    }
}
