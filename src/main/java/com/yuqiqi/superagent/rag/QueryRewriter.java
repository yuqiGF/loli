package com.yuqiqi.superagent.rag;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * ⭐查询重写器  在用户输入提示词后使用
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;
    private final DashScopeChatModel dashscopeChatModel;

    public QueryRewriter(DashScopeChatModel dashscopeChatModel){
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        //创建查询重写转化器
        this.queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
        this.dashscopeChatModel = dashscopeChatModel;
    }

    public String doQueryRewrite(String prompt){
        Query query = new Query(prompt);
        //执行查询重写
        Query transformedQuery = queryTransformer.transform(query);
        //输出重写后的提示词
        return transformedQuery.text();
    }
}
