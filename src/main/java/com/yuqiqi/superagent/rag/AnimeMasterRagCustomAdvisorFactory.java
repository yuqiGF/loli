package com.yuqiqi.superagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * ⭐创建自定义的rag检索增强的工厂
 */
public class AnimeMasterRagCustomAdvisorFactory {

    /**
     * 创建自定义的文本检索增强顾问 可以设置过滤   ⭐检索增强顾问  更加灵活   在不知道的时候会回答不知道
     *
     * @param vectorStore 向量存储
     * @param status 元数据中自定义的状态信息
     * @return 自定义的文本增强顾问
     */
    public static Advisor createAnimeMasterRagCustomAdvisor(VectorStore vectorStore , String status){
        //过滤特定状态的文档
        Filter.Expression expression = new FilterExpressionBuilder()    //过滤器
                .eq("status", status)
                .build();
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)  //指定一个向量存储
                .filterExpression(expression) //指定一个表达过滤
                .similarityThreshold(0.5) //相似度阈值
                .topK(3) //返回文档的数量
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)   //文档检索器
                .queryAugmenter(AnimeMasterContextualQueryAugmenterFactory.createInstance())   //查询增强器  检索到的文档为空的时候的措施
                .build();
    }
}
