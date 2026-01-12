package com.yuqiqi.superagent.controller;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.yuqiqi.superagent.app.AnimeMaster;
import com.yuqiqi.superagent.service.AiService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
//加上跨域注解，防止前端 fetch 调用时报 CORS 错误
@CrossOrigin(origins = "*")
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

    // --- 新的流式接口 ---
    // 请求示例: http://localhost:8123/api/ai/AnimeMaster/stream?message=你好
    @GetMapping(value = "/AnimeMaster/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamAnime(@RequestParam String message) {
        // 这里假设 Service 层也已经实现了 streamChat 方法
        return aiService.streamChat(message);
    }
}
