package com.yuqiqi.superagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AnimeMasterTest {
    //引入测试类
    @Resource
    private AnimeMaster animeMaster;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好 我的主人叫宇崎崎，我刚看完幸运星，它超级好看";
        String answer = animeMaster.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        //第二轮
        String message2 = "我刚刚看过什么？";
        answer = animeMaster.doChat(message2, chatId);
        Assertions.assertNotNull(answer);

    }

    @Test
    void test(){
        int[] nums = new int[]{10,10,20,20,20,30,30,30,35};
        int sum = 0;
        int flag = 0;
        int count = 0;
        Arrays.sort(nums);
        for (int i = 0 ; i < nums.length ; i ++){
            if(nums[i] == 0){
                sum += 1;
                continue;
            }
            // 1 1 1 1 1
            if (flag == nums[i]){
                if(count > nums[i]){  //代表这就是这一组的最后一个了
                    count = 0;
                    sum += nums[i] + 1;
                }
                count ++;
            }else {
                sum += nums[i] + 1;
                flag = nums[i];
                count ++;
            }
        }
    }

    /**
     * 测试结构化输出
     */
    @Test
    void doChatWithReport() {
        String chatId = "1";
        String message = "你好，我叫宇崎崎，我喜欢看一些可爱的动画";
        AnimeMaster.AnimeReport animeReport = animeMaster.doChatWithReport(message, chatId);
        Assertions.assertNotNull(animeReport); //非空断言
    }

    @Test
    void doChatWithRAG() {
        String id = UUID.randomUUID().toString();
        String message = "你好，我叫宇崎崎，我喜欢看一些可爱的动画";
        String RAGResult = animeMaster.doChatWithRAG(message, id);
        Assertions.assertNotNull(RAGResult);
    }
}