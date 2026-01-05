package com.yuqiqi.superagent.controller;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.yuqiqi.superagent.app.AnimeMaster;
import com.yuqiqi.superagent.service.AiService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private AiService aiService;

    // 请求路径示例 http://localhost:8123/api/ai/AnimeMaster?message=
    @PostMapping("/AnimeMaster")
    public String anime(@RequestParam String message){
        return aiService.chat(message);
    }

    @GetMapping("/AnimeMaster")
    public String test(String message){
        return aiService.chat(message);
    }
}
