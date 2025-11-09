//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.yuqiqi.superagent.advisor;
/**
 * 自定义log advisor
 * 实现可自定义日志级别，默认info级别日志的输出，单词只打印当前提示词和ai的回复
 */

import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {  //自定义拦截器必须实现这两个接口，一个是非流式输出的，一个是流式输出的
    private final int order;
    /**
     * 构造器设置日志级别
     */
    public MyLoggerAdvisor() {
        this.order = 0;
    }


    /**
     * 重写callAdvisor的环绕通知方法，实现自定义通知。
     * @param chatClientRequest 请求
     * @param callAdvisorChain advisor链
     * @return 响应
     */
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        //前置通知  请求
        this.logRequest(chatClientRequest);
        //⭐调用请求链中的下一个拦截器
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        //后置通知  响应
        this.logResponse(chatClientResponse);
        return chatClientResponse;
    }

    /**
     * 流式输出的advisor
     * @param chatClientRequest
     * @param streamAdvisorChain
     * @return
     */
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        //响应式代码
        this.logRequest(chatClientRequest); //记录前置日志
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest); //下一个advisor
        //MessageAggregator消息聚合器
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponses, this::logResponse);//后置通知
    }

    /**
     * 请求日志
     * @param request 请求
     */
    private void logRequest(ChatClientRequest request) {
        //只记录提示词
        log.info("prompt:{}",request.toString());
    }

    /**
     * 响应日志
     * @param chatClientResponse 响应
     */
    private void logResponse(ChatClientResponse chatClientResponse) {
        //断言
        assert chatClientResponse.chatResponse() != null;
        log.info("response:{}",chatClientResponse.chatResponse().getResult().getOutput().getText());
//        logger.debug("response: {}", this.responseToString.apply(chatClientResponse.chatResponse()));
    }

    /**
     * 提供唯一名称
     * @return 名称
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 设置advisor优先级
     * @return 优先级，越低越优先
     */
    public int getOrder() {
        //指定特定的日志级别
        return this.order;
    }

    public String toString() {
        return SimpleLoggerAdvisor.class.getSimpleName();
    }

    /**
     * 建造者模式 清晰易于拓展，适合大量代码
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Function<ChatClientRequest, String> requestToString;
        private Function<ChatResponse, String> responseToString;
        private int order = 0;

        private Builder() {
        }

        public Builder requestToString(Function<ChatClientRequest, String> requestToString) {
            this.requestToString = requestToString;
            return this;
        }

        public Builder responseToString(Function<ChatResponse, String> responseToString) {
            this.responseToString = responseToString;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public SimpleLoggerAdvisor build() {
            return new SimpleLoggerAdvisor(this.requestToString, this.responseToString, this.order);
        }
    }
}
