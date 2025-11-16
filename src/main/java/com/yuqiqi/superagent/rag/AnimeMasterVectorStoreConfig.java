package com.yuqiqi.superagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ⭐向量数据库配置初始化   基于内存的向量数据库 Bean
 */
@Configuration
public class AnimeMasterVectorStoreConfig {
    @Resource
    private AnimeMasterDocumentReader animeMasterDocumentReader;

    //注入springAi的embeddingModel   自动装载
    @Bean
    VectorStore animeMasterVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        //使用简易向量模型
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        //获取需要添加的document对象
        List<Document> documentList = animeMasterDocumentReader.loadMarkdowns();
        //添加
        simpleVectorStore.add(documentList);
        //返回
        return simpleVectorStore;
    }

}
