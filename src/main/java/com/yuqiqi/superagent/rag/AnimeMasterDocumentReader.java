package com.yuqiqi.superagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

//⭐RAG中的文档读取-> ETL 抽取 转换 加载

/**
 * 动漫大师文档加载器
 */
@Component
@Slf4j
public class AnimeMasterDocumentReader {

    //spring的资源解析类   用构造函数的方法注入
    private final ResourcePatternResolver resourcePatternResolver;

    public AnimeMasterDocumentReader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }
}
