package com.yuqiqi.superagent.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 通用 Re2 重读 Advisor（兼容阿里版 spring-ai 1.0.0.2）
 * 增强推理能力   但是更消耗token，成本变高
 */
@Component
public class ReReadingAdvisor implements CallAdvisor {

	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public String getName() {
		return "Re2重读";
	}

	/**
	 * 重写adviseCall方法 实现所需功能
	 */
	@NotNull
	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		//核心实现原理就是将输入的提示词再输入一次
		String origion = chatClientRequest.prompt().getUserMessage().getText();
		//拓展提示词⭐augmentUserMessage方法
		Prompt prompt = chatClientRequest.prompt().augmentUserMessage(origion + "请重读一下用户的请求：" + origion);
		//⭐构建新的请求
		ChatClientRequest newRequest = chatClientRequest.mutate()
				.prompt(prompt)
				.build();
		//然后调用advisor连中的下一个
		ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(newRequest);
		return chatClientResponse;
	}
}
