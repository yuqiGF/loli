package com.yuqiqi.superagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AnimeMasterTest {

    @Resource
    private AnimeMaster animeMaster;
    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好 我叫宇崎崎，我刚看完幸运星，它超级好看";
        String answer = animeMaster.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第二轮
        String message2 = "我刚刚看过什么？";
        answer = animeMaster.doChat(message, chatId);
        Assertions.assertNotNull(answer);

    }
}