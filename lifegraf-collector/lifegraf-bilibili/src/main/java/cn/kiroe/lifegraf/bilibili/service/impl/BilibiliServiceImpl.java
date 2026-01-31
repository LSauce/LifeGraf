package cn.kiroe.lifegraf.bilibili.service.impl;

import cn.kiroe.lifegraf.bilibili.BilibiliConstant;
import cn.kiroe.lifegraf.bilibili.mapper.TempStorageMapper;
import cn.kiroe.lifegraf.bilibili.model.TempStorage;
import cn.kiroe.lifegraf.bilibili.remote.BilibiliClient;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliAuthResponse;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliQRCodeGenerateResponse;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliQRCodePollResponse;
import cn.kiroe.lifegraf.bilibili.service.BilibiliService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class BilibiliServiceImpl implements BilibiliService {

    private final BilibiliClient bilibiliClient;
    private final TempStorageMapper tempStorageMapper;

    public BilibiliServiceImpl(BilibiliClient bilibiliClient, TempStorageMapper tempStorageMapper) {
        this.bilibiliClient = bilibiliClient;
        this.tempStorageMapper = tempStorageMapper;
    }

    @Override
    public BilibiliQRCodeGenerateResponse generateQRCode() {
        return bilibiliClient.generateQRCode();
    }

    @Override
    public BilibiliAuthResponse pollQRCodeStatus(String qrcodeKey) {
        try {
            ResponseEntity<BilibiliQRCodePollResponse> responseEntity = bilibiliClient.pollQRCodeStatus(qrcodeKey);
            BilibiliQRCodePollResponse response = responseEntity.getBody();

            if (response != null && response.getCode() == 0 && response.getData() != null) {
                int code = response.getData().getCode();

                return switch (code) {
                    case BilibiliConstant.QR_CODE_SUCCESS -> {
                        String cookie = extractCookieFromUrl(response.getData().getUrl());
                        saveCookie(cookie);
                        yield BilibiliAuthResponse.success(cookie);
                    }
                    case BilibiliConstant.QR_CODE_NOT_SCANNED ->
                        BilibiliAuthResponse.status(code, "二维码未扫描", "未扫描");
                    case BilibiliConstant.QR_CODE_SCANNED_NOT_CONFIRMED ->
                        BilibiliAuthResponse.status(code, "二维码已扫描，等待确认", "已扫描未确认");
                    case BilibiliConstant.QR_CODE_EXPIRED, BilibiliConstant.QR_CODE_INVALID ->
                        BilibiliAuthResponse.status(code, "二维码已失效", "二维码失效");
                    default -> BilibiliAuthResponse.error("未知状态码: " + code);
                };
            }

            return BilibiliAuthResponse.error("获取状态失败");
        } catch (Exception e) {
            log.error("轮询二维码状态时发生错误", e);
            return BilibiliAuthResponse.error("请求失败", e);
        }
    }

    @Override
    public void saveCookie(String cookie) {
        TempStorage storage = new TempStorage();
        storage.setKeyName(BilibiliConstant.BILIBILI_COOKIE_KEY);
        storage.setValue(cookie);
        storage.setExpireTime(Instant.now().plus(BilibiliConstant.COOKIE_EXPIRE_DAYS, ChronoUnit.DAYS));
        tempStorageMapper.insert(storage);

        log.info("B站登录Cookie已保存");
    }

    @Override
    public String getBilibiliCookie() {
        List<TempStorage> cookies = tempStorageMapper.selectList(
            Wrappers.<TempStorage>lambdaQuery()
                .eq(TempStorage::getKeyName, BilibiliConstant.BILIBILI_COOKIE_KEY)
                .gt(TempStorage::getExpireTime, Instant.now())
                .orderByDesc(TempStorage::getCreateTime)
        );

        return cookies.isEmpty() ? null : cookies.get(0).getValue();
    }

    /**
     * 从URL中提取Cookie信息
     */
    private String extractCookieFromUrl(String url) {
        if (url == null) {
            return null;
        }

        int queryIndex = url.indexOf("?");
        if (queryIndex == -1) {
            return null;
        }

        String[] parts = url.substring(queryIndex + 1).split("&");
        return String.join("; ", parts);
    }
}
