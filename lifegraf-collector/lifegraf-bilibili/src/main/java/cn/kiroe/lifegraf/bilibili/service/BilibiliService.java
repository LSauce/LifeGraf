package cn.kiroe.lifegraf.bilibili.service;

import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliAuthResponse;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliQRCodeGenerateResponse;

public interface BilibiliService {
    BilibiliQRCodeGenerateResponse generateQRCode();

    BilibiliAuthResponse pollQRCodeStatus(String qrcodeKey);

    void saveCookie(String cookie);

    String getBilibiliCookie();
}
