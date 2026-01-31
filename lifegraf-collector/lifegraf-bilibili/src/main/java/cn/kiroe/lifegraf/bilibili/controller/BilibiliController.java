package cn.kiroe.lifegraf.bilibili.controller;

import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliAuthResponse;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliQRCodeGenerateResponse;
import cn.kiroe.lifegraf.bilibili.service.BilibiliHistoryService;
import cn.kiroe.lifegraf.bilibili.service.BilibiliService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/bilibili")
public class BilibiliController {

    private final BilibiliService bilibiliService;

    private final BilibiliHistoryService bilibiliHistoryService;

    public BilibiliController(BilibiliService bilibiliService, BilibiliHistoryService bilibiliHistoryService) {
        this.bilibiliService = bilibiliService;
        this.bilibiliHistoryService = bilibiliHistoryService;
    }

    /**
     * 生成B站登录二维码
     */
    @GetMapping("/qrcode")
    public ResponseEntity<BilibiliQRCodeGenerateResponse> generateQRCode() {
        try {
            BilibiliQRCodeGenerateResponse response = bilibiliService.generateQRCode();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("生成B站二维码失败", e);
            BilibiliQRCodeGenerateResponse errorResponse = new BilibiliQRCodeGenerateResponse();
            errorResponse.setCode(500);
            errorResponse.setMessage("生成二维码失败: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 轮询二维码状态
     *
     * @param qrcodeKey 二维码key
     * @return 登录状态响应
     */
    @GetMapping("/qrcode/poll")
    public ResponseEntity<BilibiliAuthResponse> pollQRCodeStatus(
            @RequestParam("qrcode_key") String qrcodeKey) {
        BilibiliAuthResponse result = bilibiliService.pollQRCodeStatus(qrcodeKey);
        return ResponseEntity.ok(result);
    }

    /**
     * 手动触发同步B站历史记录
     * @return
     */
    @PostMapping("/sync")
    public ResponseEntity<Boolean> syncBilibiliHistory() {
        try {
            bilibiliHistoryService.syncBilibiliHistory();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error("同步B站历史记录失败", e);
            return ResponseEntity.ok(false);
        }
    }
}
