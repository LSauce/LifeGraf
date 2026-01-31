package cn.kiroe.lifegraf.bilibili;

import java.time.Instant;

/**
 * B站模块常量定义
 */
public class BilibiliConstant {

    /**
     * Cookie存储Key前缀
     */
    public static final String BILIBILI_COOKIE_KEY = "bilibili_cookie_";

    /**
     * 历史记录分页大小
     */
    public static final int HISTORY_PAGE_SIZE = 20;

    /**
     * API请求延迟范围（毫秒）
     */
    public static final long MIN_DELAY_MS = 2000;
    public static final long MAX_DELAY_MS = 4000;

    /**
     * Cookie过期时间（天）
     */
    public static final int COOKIE_EXPIRE_DAYS = 180;

    /**
     * 最小时间戳（Unix epoch）
     */
    public static final Instant MIN_TIMESTAMP = Instant.EPOCH;

    /**
     * 时间戳转换常量：秒转毫秒
     */
    public static final long MILLIS_PER_SECOND = 1000L;

    /**
     * 二维码状态码
     */
    public static final int QR_CODE_SUCCESS = 0; // 登录成功
    public static final int QR_CODE_NOT_SCANNED = 8404402; // 未扫描
    public static final int QR_CODE_SCANNED_NOT_CONFIRMED = 8404403; // 已扫描未确认
    public static final int QR_CODE_EXPIRED = 8404401; // 二维码失效
    public static final int QR_CODE_INVALID = 8404101; // 二维码无效
}
