package com.yuqiqi.superagent.controller;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.yuqiqi.superagent.app.AnimeMaster;
import com.yuqiqi.superagent.service.AiService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private AiService aiService;

    @PostMapping("/AnimeMaster")
    public String anime(@RequestParam String message){
        return aiService.chat(message);
    }
}
