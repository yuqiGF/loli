package com.yuqiqi.superagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring ai框架调用ai大模型
 */
@Component
public class SpringAiAiInvoke implements CommandLineRunner { //自动测试一次，在启动时自动调用run方法
    @Resource
    private ChatModel dashscopeChatModel;   //使用灵积大模型必须用这个名字


    @Override
    public void run(String... args) throws Exception {
        AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("你好 我是宇崎崎")) // 直接调用model的call方法调用
                .getResult()   //获取结果
                .getOutput();  //获取输出
        System.out.println(assistantMessage.getText());  //获取文本响应
    }
}
