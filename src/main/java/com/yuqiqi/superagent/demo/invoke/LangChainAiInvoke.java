package com.yuqiqi.superagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

/**
 * langchain4j ai引入
 */
public class LangChainAiInvoke {
    public static void main(String[] args) {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-plus")
                .build();

        String answer = qwenChatModel.chat("我是宇崎崎");
        System.out.println(answer);
    }
}
