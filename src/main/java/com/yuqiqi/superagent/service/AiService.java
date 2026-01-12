package com.yuqiqi.superagent.service;

import com.yuqiqi.superagent.app.AnimeMaster;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


public interface AiService {
    /**
     * 完整文本阻塞输出
     * @param message
     * @return
     */
    String chat(String message);

    /**
     * 流式输出接口
     * @param message
     * @return
     */
    Flux<String> streamChat(String message);
}
