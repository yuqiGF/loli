package com.yuqiqi.superagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//⭐RAG中的文档读取-> ETL 抽取 转换 加载

/**
 * 动漫大师文档加载器
 */
@Component
@Slf4j
public class AnimeMasterDocumentReader {

    //⭐spring的资源解析类   用构造函数的方法注入
    private final ResourcePatternResolver resourcePatternResolver;

    public AnimeMasterDocumentReader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    //⭐加载文件后转化为Spring Ai的document对象

    /**
     * 加载所有的Document对象
     * @return
     */
    public List<Document> loadMarkdowns(){
        //定义一个最终的document列表
        List<Document> allDocuments = new ArrayList<>();

        //加载多篇markdown文档
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                //获取文件名
                String filename = resource.getFilename();
                //⭐使用官方文档中的文档配置加载器  创建加载器配置
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)  //给每个文档加一些元信息
                        .build();
                //创建加载器
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                //获取到返回的结果 集合
                List<Document> documents = markdownDocumentReader.get();
                //添加到集合中
                allDocuments.addAll(documents);
            }
        } catch (IOException e) {
            log.error("Markdown文档加载失败",e);
        }
        return allDocuments;
    }
}
