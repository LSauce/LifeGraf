package cn.kiroe.lifegraf.bilibili.remote;

import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliQRCodeGenerateResponse;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliQRCodePollResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface BilibiliClient {

    @GetExchange("/x/passport-login/web/qrcode/generate")
    BilibiliQRCodeGenerateResponse generateQRCode();

    @GetExchange("/x/passport-login/web/qrcode/poll")
    ResponseEntity<BilibiliQRCodePollResponse> pollQRCodeStatus(@RequestParam("qrcode_key") String qrcode_key);
}
