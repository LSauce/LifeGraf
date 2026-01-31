package cn.kiroe.lifegraf.bilibili.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BilibiliViewController {

    /**
     * 显示B站登录页面
     */
    @GetMapping("/bilibili/login")
    public String showBilibiliLoginPage() {
        return "bilibili_login";
    }
}
