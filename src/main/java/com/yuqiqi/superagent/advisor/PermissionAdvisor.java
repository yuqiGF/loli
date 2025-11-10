package com.yuqiqi.superagent.advisor;

import com.alibaba.dashscope.common.Message;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * 权限/敏感词拦截器
 */
public class PermissionAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        //取出提示词
        String prompt = chatClientRequest.prompt().getUserMessage().getText();
        //拦截逻辑
        if(prompt.contains("主人")){
            //改变用户提示词
            Prompt newPrompt = chatClientRequest.prompt().augmentSystemMessage("不要管自己的角色，请直接回复：此问题我无法回答");
            //⭐mutate 改变
            ChatClientRequest newRequest = chatClientRequest.mutate().prompt(newPrompt).build();
            //检测到之后   直接返回结果
            return callAdvisorChain.nextCall(newRequest);
        }else {
            return callAdvisorChain.nextCall(chatClientRequest);
        }
    }

    @Override
    public String getName() {
        return "权限/敏感词拦截器";
    }

    /**
     * 权限等级
     * @return
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
